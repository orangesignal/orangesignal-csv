//start of TwoLevelHashSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * TwoLevelHashSearch.java
 * 
 * Copyright (C) 2002  Michel Ishizuka  All rights reserved.
 * 
 * 以下の条件に同意するならばソースとバイナリ形式の再配布と使用を
 * 変更の有無にかかわらず許可する。
 * 
 * １．ソースコードの再配布において著作権表示と この条件のリスト
 *     および下記の声明文を保持しなくてはならない。
 * 
 * ２．バイナリ形式の再配布において著作権表示と この条件のリスト
 *     および下記の声明文を使用説明書もしくは その他の配布物内に
 *     含む資料に記述しなければならない。
 * 
 * このソフトウェアは石塚美珠瑠によって無保証で提供され、特定の目
 * 的を達成できるという保証、商品価値が有るという保証にとどまらず、
 * いかなる明示的および暗示的な保証もしない。
 * 石塚美珠瑠は このソフトウェアの使用による直接的、間接的、偶発
 * 的、特殊な、典型的な、あるいは必然的な損害(使用によるデータの
 * 損失、業務の中断や見込まれていた利益の遺失、代替製品もしくは
 * サービスの導入費等が考えられるが、決してそれだけに限定されない
 * 損害)に対して、いかなる事態の原因となったとしても、契約上の責
 * 任や無過失責任を含む いかなる責任があろうとも、たとえそれが不
 * 正行為のためであったとしても、またはそのような損害の可能性が報
 * 告されていたとしても一切の責任を負わないものとする。
 */

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.lang.reflect.Factory;
import jp.gr.java_conf.dangan.util.lha.HashShort;
import jp.gr.java_conf.dangan.util.lha.HashMethod;
import jp.gr.java_conf.dangan.util.lha.LzssOutputStream;
import jp.gr.java_conf.dangan.util.lha.LzssSearchMethod;

//import exceptions
import java.io.IOException;
import java.lang.NoSuchMethodException;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;

import java.lang.Error;
import java.lang.NoSuchMethodError;
import java.lang.InstantiationError;
import java.lang.NoClassDefFoundError;


/**
 * 二段階ハッシュと単方向連結リストを使って高速化された LzssSearchMethod。<br>
 * <a href="http://search.ieice.org/2000/pdf/e83-a_12_2689.pdf">定兼氏の論文</a>
 * を参考にした。
 * 
 * <pre>
 * -- revision history --
 * $Log: TwoLevelHashSearch.java,v $
 * Revision 1.1  2002/12/10 22:06:40  dangan
 * [bug fix]
 *     searchAndPut() で最近の最長一致を取れなかったバグを修正。
 *
 * Revision 1.0  2002/12/03 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class TwoLevelHashSearch implements LzssSearchMethod{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //------------------------------------------------------------------
    /**
     * LZSS辞書サイズ。
     */
    private int DictionarySize;

    /**
     * LZSS圧縮に使用される値。
     * 最大一致長を示す。
     */
    private int MaxMatch;

    /**
     * LZSS圧縮に使用される閾値。
     * 一致長が この値以上であれば、圧縮コードを出力する。
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS圧縮を施すためのバッファ。
     * 前半は辞書領域、
     * 後半は圧縮を施すためのデータの入ったバッファ。
     * LzssSearchMethodの実装内では読み込みのみ許される。
     */
    private byte[] TextBuffer;

    /**
     * 辞書の限界位置。 
     * TextBuffer前半の辞書領域にデータが無い場合に
     * 辞書領域にある不定のデータ(Javaでは0)を使用
     * して圧縮が行われるのを抑止する。
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  primary hash
    //------------------------------------------------------------------
    //  private HashMethod primaryHash
    //  private int[] primaryHashTable
    //  private int[] primaryCount
    //------------------------------------------------------------------
    /**
     * 一段目のハッシュ関数
     */
    private HashMethod primaryHash;

    /**
     * 一段目のハッシュテーブル
     * 添字は一段目のハッシュ値、内容は 二段目のハッシュテーブルの index
     */
    private int[] primaryHashTable;

    /**
     * 一段目のハッシュテーブルに幾つのデータパタンが
     * 登録されているかをカウントしておく。
     */
    private int[] primaryCount;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  secondary hash
    //------------------------------------------------------------------
    //  private int[] secondaryHashRequires
    //  private int[] secondaryHashTable
    //  private int[] dummy
    //------------------------------------------------------------------
    /**
     * 二段目のハッシュ値を算出するために必要なバイト数。
     */
    private int[] secondaryHashRequires;

    /**
     * 二段目のハッシュテーブル
     * 添字は 一段目のハッシュテーブルの値 + 二段目のハッシュ値、
     * 内容は TextBuffer 内のデータパタンの開始位置
     */
    private int[] secondaryHashTable;

    /**
     * slide() の毎に secondaryHashTable と入れ替えるダミー配列。
     * 使いまわし用。
     */
    private int[] dummy;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cahined list
    //------------------------------------------------------------------
    //  private int[] prev
    //------------------------------------------------------------------
    /**
     * 同じハッシュ値を持つデータパタン開始位置を持つ
     * 単方向連結リスト。
     */
    private int[] prev;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private TwoLevelHashSearch()
    //  public TwoLevelHashSearch( int DictionarySize, int MaxMatch, 
    //                             int Threshold, byte[] TextBuffer )
    //  public TwoLevelHashSearch( int DictionarySize, int MaxMatch, 
    //                             int Threshold, byte[] TextBuffer,
    //                             String HashMethodClassName )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private TwoLevelHashSearch(){ }

    /**
     * 二段階ハッシュを使用した LzssSearchMethod を構築する。<br>
     * 一段目のハッシュ関数には デフォルトのものが使用される。<br>
     * 
     * @param DictionarySize      辞書サイズ
     * @param MaxMatch            最大一致長
     * @param Threshold           圧縮、非圧縮の閾値
     * @param TextBuffer          LZSS圧縮を施すためのバッファ
     */
    public TwoLevelHashSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer ){
        this( DictionarySize,
              MaxMatch,
              Threshold,
              TextBuffer,
              HashShort.class.getName() );
    }


    /**
     * 二段階ハッシュを使用した LzssSearchMethod を構築する。
     * 
     * @param DictionarySize      辞書サイズ
     * @param MaxMatch            最大一致長
     * @param Threshold           圧縮、非圧縮の閾値
     * @param TextBuffer          LZSS圧縮を施すためのバッファ
     * @param HashMethodClassName Hash関数を提供するクラス名
     * 
     * @exception NoClassDefFoundError
     *              HashMethodClassName で与えられたクラスが見つからない場合。
     * @exception InstantiationError
     *              HashMethodClassName で与えられたクラスが
     *              abstract class であるためインスタンスを生成できない場合。
     * @exception NoSuchMethodError
     *              HashMethodClassName で与えられたクラスが
     *              コンストラクタ HashMethod( byte[] )を持たない場合。
     */
    public TwoLevelHashSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer,
                               String HashMethodClassName ){

        this.DictionarySize   = DictionarySize;
        this.MaxMatch         = MaxMatch;
        this.Threshold        = Threshold;
        this.TextBuffer       = TextBuffer;
        this.DictionaryLimit  = this.DictionarySize;

        try{
            this.primaryHash = (HashMethod)Factory.createInstance(
                                                HashMethodClassName, 
                                                new Object[]{ TextBuffer } );
        }catch( ClassNotFoundException exception ){
            throw new NoClassDefFoundError( exception.getMessage() );
        }catch( InvocationTargetException exception ){
            throw new Error( exception.getTargetException().getMessage() );
        }catch( NoSuchMethodException exception ){
            throw new NoSuchMethodError( exception.getMessage() );
        }catch( InstantiationException exception ){
            throw new InstantiationError( exception.getMessage() );
        }

        // ハッシュテーブル初期化
        this.primaryHashTable   = new int[ this.primaryHash.tableSize() ];
        this.secondaryHashTable = new int[ ( this.primaryHash.tableSize() 
                                           + this.DictionarySize / 4 ) ];
        for( int i = 0 ; i < this.primaryHashTable.length ; i++ ){
            this.primaryHashTable[i]   = i;
            this.secondaryHashTable[i] = -1;
        }

        // その他の配列生成 
        // primaryCount と secondaryHashRequires は配列生成時にゼロクリアされている事を利用する。
        this.primaryCount          = new int[ this.primaryHash.tableSize() ];
        this.secondaryHashRequires = new int[ this.primaryHash.tableSize() ];
        this.dummy                 = new int[ this.secondaryHashTable.length ];

        // 連結リスト初期化
        this.prev = new int[ this.DictionarySize ];
        for( int i = 0 ; i < this.prev.length ; i++ ){
            this.prev[i] = -1;
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide( int slideWidth, int slideEnd )
    //  public int putRequires()
    //------------------------------------------------------------------
    /**
     * position から始まるデータパタンを
     * 二段階ハッシュと連結リストから成る検索機構に登録する。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置
     */
    public void put( int position ){
        int phash = this.primaryHash.hash( position );
        int base  = this.primaryHashTable[ phash ];
        int shash = this.secondaryHash( position, this.secondaryHashRequires[ phash ] );

        this.primaryCount[ phash ]++;
        this.prev[ position & ( this.DictionarySize - 1 ) ] = 
                                        this.secondaryHashTable[ base + shash ];
        this.secondaryHashTable[ base + shash ] = position;
    }

    /**
     * 二段階ハッシュと連結リストから成る検索機構に登録された
     * データパタンから position から始まるデータパタンと
     * 最長の一致を持つものを検索し、
     * 同時に position から始まるデータパタンを 
     * 二段階ハッシュと連結リストから成る検索機構に登録する。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置。
     * 
     * @return 一致が見つかった場合は
     *         LzssOutputStream.createSearchReturn 
     *         によって生成された一致位置と一致長の情報を持つ値、
     *         一致が見つからなかった場合は
     *         LzssOutputStream.NOMATCH。
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int searchAndPut( int position ){
        int matchlen  = this.Threshold - 1;
        int matchpos  = position;
        int scanlimit = Math.max( this.DictionaryLimit,
                                  position - this.DictionarySize );


        int phash    = this.primaryHash.hash( position );
        int base     = this.primaryHashTable[ phash ];
        int requires = this.secondaryHashRequires[ phash ];
        int shash    = this.secondaryHash( position, requires );
        int scanpos  = this.secondaryHashTable[ base + shash ];

        byte[] buf   = this.TextBuffer;
        int max      = position + this.MaxMatch;
        int s        = 0;
        int p        = 0;
        int len      = 0;

        //------------------------------------------------------------------
        //  二段目のハッシュによって選ばれた連結リストを検索するループ
        while( scanlimit <= scanpos ){
            if( buf[ scanpos + matchlen ] == buf[ position + matchlen ] ){
                s = scanpos;
                p = position;
                while( buf[s] == buf[p] ){
                    s++;
                    p++;
                    if( max <= p ) break;
                }

                len = p - position;
                if( matchlen < len ){
                    matchpos = scanpos;
                    matchlen = len;
                    if( max <= p ) break;
                }
            }
            scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
        }

        //------------------------------------------------------------------
        //  二段目のハッシュによって厳選された連結リストに一致が無い場合、
        //  一段目のハッシュに登録されている全ての連結リストを検索する
        int revbits  = 1;
        int loopend  = requires - Math.max( 0, this.Threshold - this.primaryHash.hashRequires() );
        int maxmatch = this.primaryHash.hashRequires() + requires - 1;
        for( int i = 1, send = 4 ; i <= loopend && matchlen <= maxmatch ; i++, send <<= 2 ){
            max += position + maxmatch;
            while( revbits < send ){
                scanpos  = this.secondaryHashTable[ base + ( shash ^ revbits ) ];
                while( scanlimit <= scanpos ){
                    if( buf[ scanpos ] == buf[ position ] ){
                        s = scanpos + 1;
                        p = position + 1;
                        while( buf[s] == buf[p] ){
                            s++;
                            p++;
                            if( max <= p ) break;
                        }

                        len = p - position;
                        if( matchlen < len
                         || ( matchlen == len && matchpos < scanpos ) ){
                            matchpos = scanpos;
                            matchlen = len;
                            if( max <= p ){
                                scanlimit = scanpos;
                                break;
                            }
                        }
                    }
                    scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
                }
                revbits++;
            }
            maxmatch = this.primaryHash.hashRequires() + requires - i - 1;
        }
        
        //------------------------------------------------------------------
        //  二段階ハッシュと連結リストを使用した検索機構に
        //  position から始まるデータパタンを登録する。
        this.primaryCount[ phash ]++;
        this.prev[ position & ( this.DictionarySize - 1 ) ] = 
                                        this.secondaryHashTable[ base + shash ];
        this.secondaryHashTable[ base + shash ] = position;

        //------------------------------------------------------------------
        //  最長一致を呼び出し元に返す。
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * ハッシュと連結リストを使用した検索機構に登録された
     * データパタンを検索し position から始まるデータパタンと
     * 最長の一致を持つものを得る。<br>
     * 
     * @param position   TextBuffer内のデータパタンの開始位置。
     * @param lastPutPos 最後に登録したデータパタンの開始位置。
     * 
     * @return 一致が見つかった場合は
     *         LzssOutputStream.createSearchReturn 
     *         によって生成された一致位置と一致長の情報を持つ値、
     *         一致が見つからなかった場合は
     *         LzssOutputStream.NOMATCH。
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int search( int position, int lastPutPos ){

        //------------------------------------------------------------------
        //  ハッシュと連結リストによる検索機構に登録されていない
        //  データパタンを単純な逐次検索で検索する。
        int matchlen   = this.Threshold - 1;
        int matchpos   = position;
        int scanpos    = position - 1;
        int scanlimit  = Math.max( this.DictionaryLimit, lastPutPos );

        byte[] buf     = this.TextBuffer;
        int max        = Math.min( this.TextBuffer.length,
                                   position + this.MaxMatch );
        int s          = 0;
        int p          = 0;
        int len        = 0;
        while( scanlimit < scanpos ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
            }
            scanpos--;
        }

        //------------------------------------------------------------------
        //  二段階ハッシュと連結リストを使用した検索機構から検索する。
        int phashRequires = this.primaryHash.hashRequires();
        if( phashRequires < this.TextBuffer.length - position ){

            int phash    = this.primaryHash.hash( position );
            int base     = this.primaryHashTable[ phash ];
            int requires = this.secondaryHashRequires[ phash ];
            int shash;
            int start;
            if( phashRequires + requires < this.TextBuffer.length - position ){
                shash   = this.secondaryHash( position, requires );
                start   = 0;
            }else{
                int avail = this.TextBuffer.length - position - phashRequires;
                shash   = this.secondaryHash( position, avail ) << ( ( requires - avail ) * 2 );
                start   = requires - avail;
            }
            int revbits = 0;
            int loopend  = requires - Math.max( 0, this.Threshold - this.primaryHash.hashRequires() );
            int maxmatch = this.MaxMatch;

            //------------------------------------------------------------------
            //  一段目のに登録されている連結リストを優先度の順に検索するループ
            for( int i = start, send = ( 1 << ( i * 2 ) ) ; i <= requires ; i++, send <<= 2 ){
                max += position + maxmatch;
                while( revbits < send ){
                    scanpos  = this.secondaryHashTable[ base + ( shash ^ revbits ) ];
                    while( scanlimit <= scanpos ){
                        if( buf[ scanpos ] == buf[ position ] ){
                            s = scanpos + 1;
                            p = position + 1;
                            while( buf[s] == buf[p] ){
                                s++;
                                p++;
                                if( max <= p ) break;
                            }

                            len = p - position;
                            if( matchlen < len
                             || ( matchlen == len && matchpos < scanpos ) ){
                                matchpos = scanpos;
                                matchlen = len;
                                if( max <= p ){
                                    scanlimit = scanpos;
                                    break;
                                }
                            }
                        }
                        scanpos = this.prev[ scanpos & ( this.DictionarySize - 1 ) ];
                    }
                    revbits++;
                }
                maxmatch = this.primaryHash.hashRequires() + requires - i - 1;
            }
        }// if( phashRequires < this.TextBuffer.length - position )

        //------------------------------------------------------------------
        //  最長一致を呼び出し元に返す。
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }

    }

    /**
     * TextBuffer内のpositionまでのデータを
     * 前方へ移動する際、それに応じて SearchMethod内の
     * データも TextBuffer内のデータと矛盾しないように
     * 前方へ移動する処理を行う。
     */
    public void slide(){

        //------------------------------------------------------------------
        //  DictionaryLimit更新
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );

        //------------------------------------------------------------------
        //  primaryCount の値によって secondaryHashTable を再構成する
        int secondaryIndex = 0;
        int dummyIndex     = 0;
        for( int i = 0 ; i < this.primaryHashTable.length ; i++ ){
            this.primaryHashTable[i] = dummyIndex;
            int bits = this.secondaryHashRequires[i] * 2;

            if( 1 << ( 5 + bits ) <= this.primaryCount[i] ){
                for( int j = 0 ; j < ( 1 << bits ) ; j++ ){
                    this.divide( dummyIndex, secondaryIndex, this.primaryHash.hashRequires() + this.secondaryHashRequires[i] );
                    dummyIndex     += 4;
                    secondaryIndex += 1;
                }
                this.secondaryHashRequires[i]++;

            }else if( 0 < bits && this.primaryCount[i] < ( 1 << ( 2 + bits ) ) ){
                for( int j = 0 ; j < ( 1 << ( bits - 2 ) ) ; j++ ){
                    this.merge( dummyIndex, secondaryIndex );
                    dummyIndex     += 1;
                    secondaryIndex += 4;
                }
                this.secondaryHashRequires[i]--;

            }else{
                for( int j = 0 ; j < ( 1 << bits ) ; j++ ){
                    int pos = this.secondaryHashTable[ secondaryIndex++ ] - this.DictionarySize;
                    this.dummy[ dummyIndex++ ] = ( 0 <= pos ? pos : -1 );
                }
            }
            this.primaryCount[i] = 0;
        }
        int[] temp = this.secondaryHashTable;
        this.secondaryHashTable = this.dummy;
        this.dummy = temp;

        //------------------------------------------------------------------
        //  連結リストを更新
        for( int i = 0 ; i < this.prev.length ; i++  ){
            int pos =  this.prev[i] - this.DictionarySize;
            this.prev[i] = ( 0 <= pos ? pos : -1 );
        }
    }

    /**
     * put() で LzssSearchMethodにデータを
     * 登録するときに使用されるデータ量を得る。
     * TwoLevelHashSearch では、内部で使用している HashMethod の実装が 
     * hash() のために必要とするデータ量( HashMethod.hashRequires() の戻り値 ) 
     * と 二段目のハッシュに必要な最大のバイト数を足したものを返す。
     * 
     * @return 一段目と二段目のハッシュに必要なバイト数を足したもの。
     */
    public int putRequires(){
        return this.primaryHash.hashRequires() 
               + Math.max( Bits.len( this.DictionarySize ) - 5, 0 ) / 2;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  secondary hash method
    //------------------------------------------------------------------
    //  private int secondaryHash( int position, int hashRequires )
    //------------------------------------------------------------------
    /**
     * 二段目のハッシュ関数
     * 
     * @param position     TextBuffer内のデータパタンの開始位置
     * @param hashRequires 二段目のハッシュ値を算出するのに必要なバイト数
     */
    private int secondaryHash( int position, int hashRequires ){
        int hash = 0;
        int pos  = position + this.primaryHash.hashRequires();

        while( 0 < hashRequires-- ){
            hash <<= 2;
            hash  |= this.TextBuffer[ pos++ ] & 0x03;
        }

        return hash;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  divide and merge chained list
    //------------------------------------------------------------------
    //  private void divide( int dbase, int sbase, int divoff )
    //  private void merge( int dbase, int sbase )
    //------------------------------------------------------------------
    /**
     * 二段目のハッシュテーブルと連結リストを分岐させる。
     * 
     * @param dbase  分岐先 this.dummy の index
     * @param sbase  分岐元 this.secondaryHashTable の index
     * @param divoff 分岐位置 
     */
    private void divide( int dbase, int sbase, int divoff ){
        int limit     = this.DictionarySize;
        int position  = this.secondaryHashTable[ sbase ];
        int[] current = { -1, -1, -1, -1 };
        
        //------------------------------------------------------------------
        //  連結リストを分岐させていくループ
        while( limit < position ){
            int shash = this.TextBuffer[ position + divoff ] & 0x03;
            if( 0 < current[ shash ] ){
                this.prev[ current[ shash ] & ( this.DictionarySize - 1 ) ] = position;
            }else{
                this.dummy[ dbase + shash ] = position - this.DictionarySize; 
            }
            current[ shash ] = position;
            position = this.prev[ position & ( this.DictionarySize - 1 ) ];
        }

        //------------------------------------------------------------------
        //  連結リストをターミネートする。
        for( int i = 0 ; i < current.length ; i++ ){
            if( 0 < current[ i ] ){
                this.prev[ current[ i ] & ( this.DictionarySize - 1 ) ] = -1;
            }else{
                this.dummy[ dbase + i ] = -1; 
            }
        }
    }

    /**
     * 二段目のハッシュテーブルと連結リストを束ねる。
     * 
     * @param dbase  分岐先 this.dummy の index
     * @param sbase  分岐元 this.secondaryHashTable の index
     */
    private void merge( int dbase, int sbase ){
        int limit    = this.DictionarySize;
        int position = -1;

        //------------------------------------------------------------------
        //  連結リストを束ねていくループ
        while( true ){
            int shash = 0;
            int max   = this.secondaryHashTable[ sbase ];
            for( int i = 1 ; i < 4 ; i++ ){
                if( max < this.secondaryHashTable[ sbase + i ] ){
                    shash = i;
                    max   = this.secondaryHashTable[ sbase + i ];
                }
            }
            
            if( limit < max ){
                this.secondaryHashTable[ sbase + shash ] = 
                                 this.prev[ max & ( this.DictionarySize - 1 ) ];

                if( 0 < position ){
                    this.prev[ position & ( this.DictionarySize - 1 ) ] = max;
                }else{
                    this.dummy[ dbase ]  = max - this.DictionarySize;
                }
                position = max;
            }else{
                break;
            }
        }

        //------------------------------------------------------------------
        //  連結リストをターミネートする。
        if( 0 < position ){
            this.prev[ position & ( this.DictionarySize - 1 ) ] = -1;
        }else{
            this.dummy[ dbase ] = -1;
        }
    }

}
//end of TwoLevelHashSearch.java
