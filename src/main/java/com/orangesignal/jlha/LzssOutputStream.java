//start of LzssOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssOutputStream.java
 * 
 * Copyright (C) 2001-2002  Michel Ishizuka  All rights reserved.
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

package com.orangesignal.jlha;

//import classes and interfaces
import java.io.OutputStream;




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

import com.orangesignal.jlha.HashAndChainedListSearch;
import com.orangesignal.jlha.LzssSearchMethod;
import com.orangesignal.jlha.PostLzssEncoder;


/**
 * データを LZSS圧縮しながら
 * 指定された PostLzssEncoder に出力する圧縮用出力ストリーム。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LzssOutputStream.java,v $
 * Revision 1.2  2002/12/06 00:00:00  dangan
 * [change]
 *     flush() で write() された全てのデータを 
 *     接続された PostLzssEncoder に出力するように修正。
 * [maintenance]
 *     slide幅が常に DictionarySize バイトになるように修正。
 *
 * Revision 1.1  2002/10/20 00:00:00  dangan
 * [bug fix]
 *     初期状態で flush() したり 連続で flush() すると
 *     ( lastsearchret が NEEDSEARCH の時に encode() が呼ばれると )
 *     直後の 1バイトが化けていた。
 *     flush() 時に putLength() を考慮していなかったため
 *     検索機構を破壊するような searchAndPut を行っていたのを修正。
 *     flush() 時に TextBuffer 最後尾のMaxMatchバイトのデータを出力していなかった。
 *
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     getMatchLen() で searchret >> 22 とすべきところが 
 *     searchret >>> 22 となっていたのを修正。
 * [maintenance]
 *     LhaUtil.createInstance() の使用をやめ
 *     代わりに Factory.createInstance() を使用する。
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LzssOutputStream extends OutputStream{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  special value
    //------------------------------------------------------------------
    //  private static final int NEEDSEARCH
    //  private static final int NOMATCH
    //------------------------------------------------------------------
    /**
     * lastsearchret に登録する値。
     * searchAndPutの処理が必要である事を示す。
     */
    private static final int NEEDSEARCH = 0;

    /**
     * searchret がこの値だった場合、
     * 検索の結果、閾値以上の一致が見つからなかった事を示す。
     */
    public static final int NOMATCH = -1;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private PostLzssEncoder encoder
    //------------------------------------------------------------------
    /**
     * LZSS圧縮コードを排出する先の出力ストリーム
     */
    private PostLzssEncoder encoder;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int Threshold
    //  private int MaxMatch
    //------------------------------------------------------------------
    /**
     * LZSS辞書サイズ。
     */
    private int DictionarySize;

    /**
     * LZSS圧縮に使用される閾値。
     * 一致長が この値以上であれば、圧縮コードを出力する。
     */
    private int Threshold;

    /**
     * LZSS圧縮に使用される値。
     * 最大一致長を示す。
     */
    private int MaxMatch;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //  private int writtenPos
    //  private int putPos
    //  private int searchedPos
    //------------------------------------------------------------------
    /**
     * LZSS圧縮を施すためのバッファ。
     * 前半は辞書領域、
     * 後半は圧縮を施すためのデータの入ったバッファ。
     */
    private byte[] TextBuffer;

    /**
     * 辞書の限界位置。 
     * TextBuffer前半の辞書領域にデータが無い場合に
     * 辞書領域にある不定のデータ(Javaでは0)を使用
     * して圧縮が行われるのを抑止する。
     */
    private int DictionaryLimit;

    /**
     * TextBuffer内書き込み完了位置
     * LzssOutputStream.write() によって書き込まれた位置
     * 
     * 以下の3者の関係は putPos <= searchedPos <= writtenPos となる。
     */
    private int writtenPos;

    /**
     * TextBuffer内 put() 完了位置
     * LzssSearchMethod の put() もしくは searchAndPut() で
     * 検索機構への登録が完了した位置
     */
    private int putPos;

    /**
     * TextBuffer内 現在検索位置
     * 次に LzssSearchMethod の search() もしくは searchAndPut() で
     * 検索をすべき位置
     */
    private int searchPos;

    /**
     * 前回のencodeの最後のsearchretを保存しておく
     * コンストラクタでは lastsearchret に無効な
     * 数字である事を示す LzssOutputStream.NEEDSEARCHを
     * 入力しておく。
     */
    private int lastsearchret;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  search method
    //------------------------------------------------------------------
    //  private LzssSearchMethod method
    //------------------------------------------------------------------
    /**
     * 検索をつかさどるクラス
     */
    private LzssSearchMethod method;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LzssOutputStream()
    //  public LzssOutputStream( PostLzssEncoder encoder )
    //  public LzssOutputStream( PostLzssEncoder encoder, String SearchMethod )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private LzssOutputStream(){ }

    /**
     * write() によって書きこまれたデータを
     * LZSSで圧縮し、圧縮したデータを encoderに出力する
     * 出力ストリームを構築する。
     * 
     * @param encoder LZSS圧縮データ出力ストリーム
     */
    public LzssOutputStream( PostLzssEncoder encoder ){
        this( encoder, 
              HashAndChainedListSearch.class.getName(),
              new Object[0] );
    }

    /**
     * write() によって書きこまれたデータを
     * LZSSで圧縮し、圧縮したデータを encoderに出力する
     * 出力ストリームを構築する。
     * 
     * @param encoder LZSS圧縮データ出力ストリーム
     * @param LzssSearchMethodClassName 
     *                LzssSearchMethod の実装を示すパッケージ名も含めたクラス名
     * 
     * @exception NoClassDefFoundError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              見つからない場合。
     * @exception InstantiationError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              abstract class であるためインスタンスを生成できない場合。
     * @exception NoSuchMethodError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              コンストラクタ LzssSearchMethod( int, int, int, byte[], int )
     *              を持たない場合
     */
    public LzssOutputStream( PostLzssEncoder encoder, 
                             String          LzssSearchMethodClassName ){
        this( encoder, 
              LzssSearchMethodClassName,
              new Object[0] );
    }

    /**
     * write() によって書きこまれたデータを
     * LZSSで圧縮し、圧縮したデータを encoderに出力する
     * 出力ストリームを構築する。
     * 
     * @param encoder LZSS圧縮データ出力ストリーム
     * @param LzssSearchMethodClassName 
     *                LzssSearchMethod の実装を示すパッケージ名も含めたクラス名
     * 
     * @exception NoClassDefFoundError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              見つからない場合。
     * @exception InstantiationError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              abstract class であるためインスタンスを生成できない場合。
     * @exception NoSuchMethodError
     *              LzssSearchMethodClassName で与えられたクラスが
     *              コンストラクタ LzssSearchMethod( int, int, int, byte[] )
     *              を持たない場合
     */
    public LzssOutputStream( PostLzssEncoder encoder, 
                             String   LzssSearchMethodClassName,
                             Object[] LzssSearchMethodExtraArguments ){

        this.DictionarySize  = encoder.getDictionarySize();
        this.MaxMatch        = encoder.getMaxMatch();
        this.Threshold       = encoder.getThreshold();

        this.encoder         = encoder;
        this.TextBuffer      = new byte[ this.DictionarySize * 2 
                                       + this.MaxMatch ];
        this.writtenPos      = this.DictionarySize;
        this.putPos          = this.DictionarySize;
        this.searchPos       = this.DictionarySize;
        this.DictionaryLimit = this.DictionarySize;
        this.lastsearchret   = LzssOutputStream.NEEDSEARCH;

        Object[] arguments   = new Object[ LzssSearchMethodExtraArguments.length + 4 ];
        arguments[0] = new Integer( this.DictionarySize );
        arguments[1] = new Integer( this.MaxMatch );
        arguments[2] = new Integer( this.Threshold );
        arguments[3] = this.TextBuffer;
        for( int i = 0 ; i < LzssSearchMethodExtraArguments.length ; i++ ){
            arguments[4+i] = LzssSearchMethodExtraArguments[i];
        }

        try{
            this.method = (LzssSearchMethod)Factory.createInstance( 
                            LzssSearchMethodClassName, 
                            arguments );                                        //throw ClasNotfoundException, InvocationTargetException, NoSuchMethodException, InstantiationException
        }catch( ClassNotFoundException exception ){
            throw new NoClassDefFoundError( exception.getMessage() );
        }catch( InvocationTargetException exception ){
            throw new Error( exception.getTargetException().getMessage() );
        }catch( NoSuchMethodException exception ){
            throw new NoSuchMethodError( exception.getMessage() );
        }catch( InstantiationException exception ){
            throw new InstantiationError( exception.getMessage() );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * 圧縮機構に1バイトのデータを出力する。<br>
     * 実際にPostLzssEncoder にデータが渡されるのは 
     * TextBuffer が満たされたときか、
     * flush で明示的に出力を指示した時のみ。<br>
     * 
     * @param data 1バイトのデータ
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void write( int data ) throws IOException {
        this.TextBuffer[ this.writtenPos++ ] = (byte)data;

        if( this.TextBuffer.length <= this.writtenPos ){
            this.encode( false );                                               //throws IOException
            this.slide();                                                       
        }
    }

    /**
     * 圧縮機構に buffer 内のデータを全て出力する。<br>
     * 実際にPostLzssEncoder にデータが渡されるのは 
     * TextBuffer が満たされたときか、
     * flush で明示的に出力を指示した時のみ。<br>
     * 
     * @param buffer データの格納されたバッファ
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );                                 //throws IOException
    }

    /**
     * 圧縮機構に buffer 内の index から lengthバイトのデータを出力する。<br>
     * 実際にPostLzssEncoder にデータが渡されるのは 
     * TextBuffer が満たされたときか、
     * flush で明示的に出力を指示した時のみ。<br>
     * 
     * @param buffer データの格納されたバッファ
     * @param index  buffer内データ開始位置
     * @param length buffer内データの長さ
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void write( byte[] buffer, int index, int length ) throws IOException {
        int pos = index;
        int end = index + length;

        while( pos < end ){
            int space = TextBuffer.length - writtenPos;
            if( end - pos < space ){
                System.arraycopy( buffer, pos, 
                                  this.TextBuffer, this.writtenPos, 
                                  end - pos );
                this.writtenPos += end - pos;
                pos = end;
            }else{
                System.arraycopy( buffer, pos, 
                                  this.TextBuffer, this.writtenPos,
                                  space );
                this.writtenPos += space;
                pos += space;
                this.encode( false );                                           //throws IOException
                this.slide();
            }
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * 圧縮機構に書き込まれた全てのデータを
     * 接続された PostLzssEncoder に出力し、
     * 接続された PostLzssEncoder を flush() する。<br>
     * このとき、出力するデータの終端付近では
     * 検索に search() を使用するため圧縮速度が低下する。
     * また flush() しない場合と比べて圧縮率が変化する。
     * これは flush() した位置付近ではデータパタンの検索に
     * MaxMatch に満たないデータパタンを使用するため、
     * 検索結果が不完全になるため。
     * この圧縮率の変化は、多くの場合圧縮率が少々低下するだけであるが、
     * 例えば次のようなコードは LZ 圧縮を全く行わない。
     * <pre>
     *  public void wrongCompress( InputStream in, LzssOutputSteam out ){
     *      int r;
     *      while( 0 <= r = in.read() ){
     *          out.write( r );
     *          out.flush();
     *      }
     *  }
     * </pre>
     * また、このメソッドは PostLzssEncoder.flush() を呼び出すため
     * flush() しない場合と比べて、出力データが変化する可能性がある。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PostLzssEncoder#flush()
     */
    public void flush() throws IOException {
        this.encode( false );                                                   //throw IOException
        if( this.DictionarySize * 2 <= this.putPos ){
            this.slide();
            if( this.searchPos < this.writtenPos ){
                this.encode( false );                                           //throw IOException
            }
        }
        this.encoder.flush();                                                   //throw IOException
    }

    /**
     * このクラスに貯えられた全てのデータを接続された 
     * PostLzssEncoder に出力し この出力ストリームと、
     * 接続されたストリームを閉じ、
     * 使用していたリソースを開放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        while( this.DictionarySize <= this.writtenPos ){
            this.encode( true );                                      //throw IOException
            if( this.writtenPos <= this.searchPos ){
                break;
            }else{
                this.slide();
            }
        }

        this.encoder.close();                                                   //throw IOException
        this.encoder = null;

        this.TextBuffer = null;
        this.method     = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private int encode()
    //  private void slide( int position )
    //------------------------------------------------------------------
    /**
     * TextBuffer に貯えられたデータを圧縮しながら
     * private変数 this.encoder に出力する。
     * 
     * @return TextBuffer 内の出力完了したデータの終端位置 + 1
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void encode( boolean last ) throws IOException {

        int end = Math.min( this.TextBuffer.length  - this.MaxMatch,
                            this.writtenPos - ( last ? 0 : this.method.putRequires() ) );
        if( this.searchPos < end ){

            //------------------------------------------------------------------
            //  前処理
            if( this.lastsearchret == LzssOutputStream.NEEDSEARCH ){

                //------------------------------------------------------------------
                //  検索機構に未登録のデータパタンを登録
                while( this.putPos < this.searchPos - 1 ){
                    this.method.put( ++this.putPos );

                    //直前の flush() で put() できなかった
                    //データパタンを put() しただけの場合は return
                    if( this.DictionarySize * 2 <= this.putPos ){
                        return;
                    }
                }

                //  lastsearchret が NEEDSEARCH なので searchAndPut で検索する。
                this.lastsearchret = this.method.searchAndPut( this.searchPos );
            }

            int searchret = this.lastsearchret;
            int matchlen  = LzssOutputStream.getMatchLen( searchret );
            int matchpos  = LzssOutputStream.getMatchPos( searchret );
            if( this.writtenPos - this.searchPos < matchlen ){
                matchlen = this.writtenPos - this.searchPos;
            }

            //------------------------------------------------------------------
            //  メインループ
            while( true ){
                int lastmatchlen = matchlen;
                int lastmatchoff = this.searchPos - matchpos - 1;

                searchret = this.method.searchAndPut( ++this.searchPos );
                matchlen  = LzssOutputStream.getMatchLen( searchret );
                matchpos  = LzssOutputStream.getMatchPos( searchret );
                if( this.writtenPos - this.searchPos < matchlen ){
                    matchlen = this.writtenPos - this.searchPos;
                }

                if( lastmatchlen < matchlen || lastmatchlen < this.Threshold ){
                    this.encoder.writeCode( 0xFF & this.TextBuffer[ this.searchPos - 1 ] ); //throws IOException
                    if( end <= this.searchPos ){
                        this.putPos        = this.searchPos;
                        this.lastsearchret = searchret;
                        break;
                    }
                }else{
                    this.encoder.writeCode( 256 + lastmatchlen - this.Threshold );//throws IOException
                    this.encoder.writeOffset( lastmatchoff );                   //throws IOException

                    lastmatchlen--;
                    if( this.searchPos + lastmatchlen < end ){
                        while( 0 < --lastmatchlen ){
                            this.method.put( ++this.searchPos );
                        }

                        searchret = this.method.searchAndPut( ++this.searchPos );
                        matchlen  = LzssOutputStream.getMatchLen( searchret );
                        matchpos  = LzssOutputStream.getMatchPos( searchret );
                        if( this.writtenPos - this.searchPos < matchlen ){
                            matchlen = this.writtenPos - this.searchPos;
                        }
                    }else if( end < this.searchPos + lastmatchlen ){
                        this.putPos = this.searchPos;
                        while( this.putPos < end ){
                            this.method.put( ++this.putPos );
                        }
                        this.searchPos    += lastmatchlen;
                        this.lastsearchret = LzssOutputStream.NEEDSEARCH;
                        break;
                    }else{
                        this.putPos = this.searchPos;
                        while( this.putPos < end - 1 ){
                            this.method.put( ++this.putPos );
                        }
                        this.putPos++;
                        this.searchPos    += lastmatchlen;
                        this.lastsearchret = this.method.searchAndPut( this.searchPos );
                        break;
                    }
                }// if( lastmatchlen < matchlen || lastmatchlen < this.Threshold )
            }// while( true )
        }// if( this.searchPos < end )

        //------------------------------------------------------------------
        //  flush() 専用
        //  putPos はそのままで searchPos のみ進める。
        end = Math.min( this.TextBuffer.length  - this.MaxMatch,
                        this.writtenPos );
        if( !last && this.searchPos < end ){
            if( this.lastsearchret == LzssOutputStream.NEEDSEARCH ){
                this.lastsearchret = this.method.search( this.searchPos, this.putPos );
            }
            int searchret = this.lastsearchret;
            int matchlen  = LzssOutputStream.getMatchLen( searchret );
            int matchpos  = LzssOutputStream.getMatchPos( searchret );
            if( this.writtenPos - this.searchPos < matchlen ){
                matchlen = this.writtenPos - this.searchPos;
            }

            while( this.searchPos < end ){
                int lastmatchlen = matchlen;
                int lastmatchoff = this.searchPos - matchpos - 1;

                searchret = this.method.search( ++this.searchPos, this.putPos );
                matchlen  = LzssOutputStream.getMatchLen( searchret );
                matchpos  = LzssOutputStream.getMatchPos( searchret );
                if( this.writtenPos - this.searchPos < matchlen ){
                    matchlen = this.writtenPos - this.searchPos;
                }

                if( lastmatchlen < matchlen || lastmatchlen < this.Threshold ){
                    this.encoder.writeCode( 0xFF & this.TextBuffer[this.searchPos - 1] ); //throws IOException
                }else{
                    this.encoder.writeCode( 256 + lastmatchlen - this.Threshold );  //throws IOException
                    this.encoder.writeOffset( lastmatchoff );                       //throws IOException

                    this.searchPos += lastmatchlen - 1;
                    searchret = this.method.search( this.searchPos, this.putPos );
                    matchlen  = LzssOutputStream.getMatchLen( searchret );
                    matchpos  = LzssOutputStream.getMatchPos( searchret );
                    if( this.writtenPos - this.searchPos < matchlen ){
                        matchlen = this.writtenPos - this.searchPos;
                    }
                }
            }
            this.lastsearchret = LzssOutputStream.NEEDSEARCH;
        }
    }


    /**
     * TextBuffer内のpositionまでのデータを
     * 前方へ移動する
     * 
     * @param position 次に TextBuffer内で
     *                 DictionarySize の位置に来るべき
     *                 要素が現在あるindex
     */
    private void slide(){
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );

        this.method.slide();

        if( this.lastsearchret != LzssOutputStream.NEEDSEARCH ){
            int matchlen = LzssOutputStream.getMatchLen( this.lastsearchret );
            int matchpos = LzssOutputStream.getMatchPos( this.lastsearchret );
            this.lastsearchret = LzssOutputStream.createSearchReturn( 
                                    matchlen, matchpos - this.DictionarySize );
        }

        this.writtenPos -= this.DictionarySize;
        this.searchPos  -= this.DictionarySize;
        this.putPos     -= this.DictionarySize;
        for( int i = this.DictionaryLimit ; i < this.writtenPos ; i++ )
            this.TextBuffer[ i ] = this.TextBuffer[ i + this.DictionarySize ];
    }


    //------------------------------------------------------------------
    //  shared methods
    //------------------------------------------------------------------
    //  private static final int createSearchReturn( int matchlen, int matchpos )
    //  private static final int getMatchLen( int searchret )
    //  private static final int getMatchPos( int searchret )
    //------------------------------------------------------------------
    /**
     * search の戻り値を生成する。
     * search は一致位置を返すが、一致長も同時に返したほうが
     * 非常に便利であるため、一致位置も一致長も必要なビット数が
     * 少ないことを利用して int型でやり取りする。
     * そのための統一した処理を約束する関数。
     * この関数で生成された値から 一致位置や一致長を取り出す際には
     * getMatchLen、 getMatchPos を使用する。
     * 
     * @param matchlen 一致長
     * @param matchpos 一致位置
     * 
     * @return 一致長と一致位置の情報を含むsearchの戻り値
     */
    public static final int createSearchReturn( int matchlen, int matchpos ){
        return matchlen << 22 | matchpos;
    }

    /**
     * createSearchReturn で生成された searchの戻り値から
     * 一致長を取り出す。
     * 
     * @param searchret search の戻り値
     * 
     * @return 一致長
     */
    public static final int getMatchLen( int searchret ){
        return searchret >> 22;
    }

    /**
     * createSearchReturn で生成された searchの戻り値から
     * 一致位置を取り出す。
     * 
     * @param searchret search の戻り値
     * 
     * @return 一致位置
     */
    public static final int getMatchPos( int searchret ){
        if( 0 <= searchret ) return searchret & 0x3FFFFF;
        else                 return -1;
    }

}
//end of LzssOutputStream.java
