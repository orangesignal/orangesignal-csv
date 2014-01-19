//start of PreLh5Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh5Decoder.java
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
import java.io.InputStream;
import java.lang.Math;




//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;

import com.orangesignal.jlha.BadHuffmanTableException;
import com.orangesignal.jlha.CompressMethod;
import com.orangesignal.jlha.PreLzssDecoder;
import com.orangesignal.jlha.StaticHuffman;


/**
 * -lh4-, -lh5-, -lh6-, -lh7- 解凍用の PreLzssDecoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLh5Decoder.java,v $
 * Revision 1.3  2002/12/08 00:00:00  dangan
 * [bug fix]
 *     readCode でハフマン符号読み込み途中で
 *     EndOfStream に達した場合に EOFException を投げていなかった。
 *
 * Revision 1.2  2002/12/08 00:00:00  dangan
 * [change]
 *     クラス名 を PreLh5DecoderFast から PreLh5Decoder に変更。
 *
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     最新の BitInputStream と PreLh5Decoder からソースを取り込む。
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.3 $
 */
public class PreLh5Decoder implements PreLzssDecoder{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリーム
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int    cacheLimit
    //  private int    cachePosition
    //------------------------------------------------------------------
    /**
     * 速度低下抑止用バイト配列
     */
    private byte[] cache;

    /**
     * cache 内の有効バイト数
     */
    private int    cacheLimit;

    /**
     * cache 内の現在処理位置
     */
    private int    cachePosition;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  bit buffer
    //------------------------------------------------------------------
    //  private int    bitBuffer
    //  private int    bitCount
    //------------------------------------------------------------------
    /**
     * ビットバッファ
     */
    private int    bitBuffer;

    /**
     * bitBuffer の 有効ビット数
     */
    private int    bitCount;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  private int blockSize
    //  private int[] codeLen
    //  private short[] codeTable
    //  private int codeTableBits
    //  private short[][] codeTree
    //  private short[] offLenTable
    //  private int offLenTableBits
    //  private short[][] offLenTree
    //------------------------------------------------------------------
    /**
     * 現在処理中のブロックの残りサイズを示す。
     */
    private int blockSize;

    /**
     * code 部のハフマン符号長の表
     */
    private int[] codeLen;

    /**
     * code 部復号用のテーブル
     * 正の場合は codeTree のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[] codeTable;

    /**
     * codeTable を引くために必要なbit数。
     */
    private int codeTableBits;

    /**
     * codeTable に収まりきらないデータの復号用の木
     * 正の場合は codeTree のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[][] codeTree;

    /**
     * offLen 部のハフマン符号長の表
     */
    private int[] offLenLen;

    /**
     * offLen 部復号用のテーブル
     * 正の場合は offLenTree のindexを示す。
     * 負の場合は offLen を全ビット反転したもの。 
     */
    private short[] offLenTable;

    /**
     * offLenTable を引くために必要なbit数。
     */
    private int offLenTableBits;

    /**
     * offLenTable に収まりきらないデータの復号用の木
     * 正の場合は offLenTree のindexを示す。
     * 負の場合は offLen を全ビット反転したもの。 
     */
    private short[][] offLenTree;


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
     * LZSS 辞書サイズ
     */
    private int DictionarySize;

    /**
     * LZSS 最長一致長
     */
    private int MaxMatch;

    /**
     * LZSS 圧縮/非圧縮の閾値
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[]    markCache
    //  private int       markCacheLimit
    //  private int       markCachePosition
    //  private int       markBitBuffer
    //  private int       markBitCount
    //  private int       markBlockSize
    //  private int[]     markCodeLen
    //  private short[]   markCodeTable
    //  private short[][] markCodeTree
    //  private int[]     markOffLenLen
    //  private short[]   markOffLenTable
    //  private short[][] markOffLenTree
    //------------------------------------------------------------------
    /** 
     * mark位置がキャッシュの範囲内にあるかを示す。
     * markされたとき true に設定され、
     * 次に in から キャッシュへの読み込みが
     * 行われたときに false に設定される。
     */
    private boolean markPositionIsInCache;

    /** cache の バックアップ用 */
    private byte[]    markCache;
    /** cacheAvailable のバックアップ用 */
    private int       markCacheLimit;
    /** cachePosition のバックアップ用 */
    private int       markCachePosition;
    /** bitBuffer のバックアップ用 */
    private int       markBitBuffer;
    /** bitCount のバックアップ用 */
    private int       markBitCount;
    /** blockSizeのバックアップ用 */
    private int       markBlockSize;
    /** codeLen のバックアップ用 */
    private int[]     markCodeLen;
    /** codeTable のバックアップ用 */
    private short[]   markCodeTable;
    /** codeTree のバックアップ用 */
    private short[][] markCodeTree;
    /** offLenLen のバックアップ用 */
    private int[]     markOffLenLen;
    /** offLenTable のバックアップ用 */
    private short[]   markOffLenTable;
    /** offLenTree のバックアップ用 */
    private short[][] markOffLenTree;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PreLh5Decoder()
    //  public PreLh5Decoder( InputStream in )
    //  public PreLh5Decoder( InputStream in, String CompressMethod )
    //  public PreLh5Decoder( InputStream in, String CompressMethod, 
    //                            int CodeTableBits, int OffLenTableBits )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PreLh5Decoder(){    }

    /**
     * -lh5- 解凍用 PreLzssDecoder を構築する。<br>
     * テーブルサイズはデフォルト値を使用する。
     * 
     * @param in -lh5-形式の圧縮データを供給する入力ストリーム
     */
    public PreLh5Decoder( InputStream in ){
        this( in, CompressMethod.LH5, 12, 8 );
    }

    /**
     * -lh4-,-lh5-,-lh6-,-lh7- 解凍用 PreLzssDecoder を構築する。<br>
     * テーブルサイズには デフォルト値を使用する。
     * 
     * @param in      圧縮データを供給する入力ストリーム
     * @param method  圧縮法識別子<br>
     *  &emsp;&emsp; CompressMethod.LH4 <br>
     *  &emsp;&emsp; CompressMethod.LH5 <br>
     *  &emsp;&emsp; CompressMethod.LH6 <br>
     *  &emsp;&emsp; CompressMethod.LH7 <br>
     *  &emsp;&emsp; の何れかを指定する。
     * 
     * @exception IllegalArgumentException 
     *               method が上記以外の場合
     */
    public PreLh5Decoder( InputStream in,
                              String      method ){

        this( in, method, 12, 8 );
    }

    /**
     * -lh4-,-lh5-,-lh6-,-lh7- 解凍用 PreLzssDecoder を構築する。
     * 
     * @param in              圧縮データを供給する入力ストリーム
     * @param method          圧縮法識別子<br>
     *           &emsp;&emsp; CompressMethod.LH4 <br>
     *           &emsp;&emsp; CompressMethod.LH5 <br>
     *           &emsp;&emsp; CompressMethod.LH6 <br>
     *           &emsp;&emsp; CompressMethod.LH7 <br>
     *           &emsp;&emsp; の何れかを指定する。
     * @param CodeTableBits   code 部を復号するために使用する
     *                        テーブルのサイズをビット長で指定する。 
     *                        12 を指定すれば 4096 のルックアップテーブルを生成する。 
     * @param OffLenTableBits offLen 部を復号するために使用する
     *                        テーブルのサイズをビット長で指定する。
     *                        8 を指定すれば 256 のルックアップテーブルを生成する。 
     * 
     * @exception IllegalArgumentException <br>
     *           &emsp;&emsp; (1) method が上記以外の場合<br>
     *           &emsp;&emsp; (2) CodeTableBits もしくは 
     *                            OffLenTableBits が 0以下の場合<br>
     *           &emsp;&emsp; の何れか
     */
    public PreLh5Decoder( InputStream in,
                              String      method,
                              int         CodeTableBits,
                              int         OffLenTableBits ){
        if( CompressMethod.LH4.equals( method )
         || CompressMethod.LH5.equals( method )
         || CompressMethod.LH6.equals( method )
         || CompressMethod.LH7.equals( method ) ){

            this.DictionarySize = CompressMethod.toDictionarySize( method );
            this.MaxMatch       = CompressMethod.toMaxMatch( method );
            this.Threshold      = CompressMethod.toThreshold( method );

            if( in != null
             && 0 < CodeTableBits
             && 0 < OffLenTableBits ){
                this.in              = in;
                this.cache           = new byte[ 1024 ];
                this.cacheLimit      = 0;
                this.cachePosition   = 0;
                this.bitBuffer       = 0;
                this.bitCount        = 0;
                this.blockSize       = 0;
                this.codeTableBits   = CodeTableBits;
                this.offLenTableBits = OffLenTableBits;

                this.markPositionIsInCache = false;
                this.markCache             = null;
                this.markCacheLimit        = 0;
                this.markCachePosition     = 0;
                this.markBitBuffer         = 0;
                this.markBitCount          = 0;

            }else if( in == null ){
                throw new NullPointerException( "in" );
            }else if( CodeTableBits <= 0 ){
                throw new IllegalArgumentException( "CodeTableBits too small. CodeTableBits must be larger than 1." );
            }else{
                throw new IllegalArgumentException( "OffHiTableBits too small. OffHiTableBits must be larger than 1." );
            }
        }else if( null == method ){
            throw new NullPointerException( "method" );
        }else{
            throw new IllegalArgumentException( "Unknown compress method " + method );
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readCode()
    //  public int readOffset()
    //------------------------------------------------------------------
    /**
     * -lh5- 系の圧縮法で圧縮された 
     * 1byte のLZSS未圧縮のデータ、
     * もしくは圧縮コードのうち一致長を読み込む。<br>
     * 
     * @return 1byte の 未圧縮のデータ、
     *         もしくは圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BadHuffmanTableException
     *                         ハフマン木を構成するための
     *                         ハフマン符号長の表が不正である場合
     */
    public int readCode() throws IOException {
        if( this.blockSize <= 0 ){
            this.readBlockHead();
        }
        this.blockSize--;

        if( this.bitCount < 16 ){
            if( 2 <= this.cacheLimit - this.cachePosition ){
                this.bitBuffer |=   ( ( this.cache[ this.cachePosition++ ] & 0xFF ) << ( 24 - this.bitCount ) )
                                  | ( ( this.cache[ this.cachePosition++ ] & 0xFF ) << ( 16 - this.bitCount ) );
                this.bitCount  += 16;
            }else{
                this.fillBitBuffer();

                int node = this.codeTable[ this.bitBuffer >>> ( 32 - this.codeTableBits ) ];
                if( 0 <= node ){
                    int bits = this.bitBuffer << this.codeTableBits;
                    do{
                        node = this.codeTree[ bits >>> 31 ][ node ];
                        bits <<= 1;
                    }while( 0 <= node );
                }
                int len = this.codeLen[ ~node ];
                if( len <= this.bitCount ){
                    this.bitBuffer <<= len;
                    this.bitCount   -= len;

                    return ~node;
                }else{
                    this.bitCount  = 0;
                    this.bitBuffer = 0;
                    throw new EOFException();
                }
            }
        }

        int node = this.codeTable[ this.bitBuffer >>> ( 32 - this.codeTableBits ) ];
        if( 0 <= node ){
            int bits = this.bitBuffer << this.codeTableBits;
            do{
                node = this.codeTree[ bits >>> 31 ][ node ];
                bits <<= 1;
            }while( 0 <= node );
        }
        int len = this.codeLen[ ~node ];
        this.bitBuffer <<= len;
        this.bitCount   -= len;

        return ~node;
    }

    /**
     * -lh5- 系の圧縮法で圧縮された
     * LZSS圧縮コードのうち一致位置を読み込む。<br>
     * 
     * @return -lh5- 系で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int readOffset() throws IOException {
        if( this.bitCount < 16 ){
            if( 2 <= this.cacheLimit - this.cachePosition ){
                this.bitBuffer |=   ( ( this.cache[ this.cachePosition++ ] & 0xFF ) << ( 24 - this.bitCount ) )
                                  | ( ( this.cache[ this.cachePosition++ ] & 0xFF ) << ( 16 - this.bitCount ) );
                this.bitCount  += 16;
            }else{
                this.fillBitBuffer();
            }
        }

        int node = this.offLenTable[ this.bitBuffer >>> ( 32 - this.offLenTableBits ) ];
        if( 0 <= node ){
            int bits = this.bitBuffer << this.offLenTableBits;
            do{
                node = this.offLenTree[ bits >>> 31 ][ node ];
                bits <<= 1;
            }while( 0 <= node );
        }
        int offlen = ~node;
        int len = this.offLenLen[ offlen ];
        this.bitBuffer <<= len;
        this.bitCount   -= len;

        offlen--;
        if( 0 <= offlen ){
            return ( 1 << offlen ) | this.readBits( offlen );
        }else{
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームの現在位置にマークを設定し、
     * reset() メソッドでマークした時点の 読み込み位置に
     * 戻れるようにする。<br>
     * InputStream の mark() と違い、readLimit で設定した
     * 限界バイト数より前にマーク位置が無効になる可能性が
     * ある事に注意すること。<br>
     * 
     * @param readLimit マーク位置に戻れる限界のバイト数。
     *                  このバイト数を超えてデータを読み
     *                  込んだ場合 reset()できなくなる可
     *                  能性がある。<br>
     * 
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){

        //------------------------------------------------------------------
        //  ハフマン符号化で最悪の場合を考慮して readLimit を計算する
        if( this.blockSize < readLimit ){
            readLimit = readLimit * StaticHuffman.LIMIT_LEN / 8;
            readLimit += 272; //block head
        }else{
            readLimit = readLimit * StaticHuffman.LIMIT_LEN / 8;
        }

        //------------------------------------------------------------------
        //  BitInputStream 用キャッシュの readLimit を計算する。
        readLimit -= this.cacheLimit - this.cachePosition;
        readLimit -= this.bitCount / 8;
        readLimit += 4;
        readLimit  = ( readLimit + this.cache.length - 1 ) / this.cache.length 
                                                           * this.cache.length;

        //------------------------------------------------------------------
        //  mark 処理
        this.in.mark( readLimit );

        if( this.markCache == null ){
            this.markCache = (byte[])this.cache.clone();
        }else{
            System.arraycopy( this.cache, 0, 
                              this.markCache, 0, 
                              this.cacheLimit );
        }
        this.markCacheLimit        = this.cacheLimit;
        this.markCachePosition     = this.cachePosition;
        this.markBitBuffer         = this.bitBuffer;
        this.markBitCount          = this.bitCount;
        this.markPositionIsInCache = true;

        this.markBlockSize   = this.blockSize;
        this.markCodeLen     = this.codeLen;
        this.markCodeTable   = this.codeTable;
        this.markCodeTree    = this.codeTree;
        this.markOffLenLen   = this.offLenLen;
        this.markOffLenTable = this.offLenTable;
        this.markOffLenTree  = this.offLenTree;
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException <br>
     * &emsp;&emsp; (1) mark() せずに reset() しようとした場合。<br>
     * &emsp;&emsp; (2) 接続された入力ストリームが markSupported()で
     *                  false を返す場合。<br>
     * &emsp;&emsp; (3) 接続された入力ストリームで
     *                  入出力エラーが発生した場合。<br>
     * &emsp;&emsp; の何れか。
     */
    public void reset() throws IOException {
        if( this.markPositionIsInCache ){
            this.cachePosition = this.markCachePosition;
            this.bitBuffer     = this.markBitBuffer;
            this.bitCount      = this.markBitCount;

            this.blockSize     = this.markBlockSize;
            this.codeLen       = this.markCodeLen;
            this.codeTable     = this.markCodeTable;
            this.codeTree      = this.markCodeTree;
            this.offLenLen     = this.markOffLenLen;
            this.offLenTable   = this.markOffLenTable;
            this.offLenTree    = this.markOffLenTree;
        }else if( !this.in.markSupported() ){
            throw new IOException( "not support mark()/reset()." );
        }else if( this.markCache == null ){ //この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。 
            throw new IOException( "not marked." );
        }else{
            //in が reset() できない場合は
            //最初の行の this.in.reset() で
            //IOException を投げることを期待している。
            this.in.reset();                                                    //throws IOException
            System.arraycopy( this.markCache, 0, 
                              this.cache, 0, 
                              this.markCacheLimit );
            this.cacheLimit    = this.markCacheLimit;
            this.cachePosition = this.markCachePosition;
            this.bitBuffer     = this.markBitBuffer;
            this.bitCount      = this.markBitCount;

            this.blockSize     = this.markBlockSize;
            this.codeLen       = this.markCodeLen;
            this.codeTable     = this.markCodeTable;
            this.codeTree      = this.markCodeTree;
            this.offLenLen     = this.markOffLenLen;
            this.offLenTable   = this.markOffLenTable;
            this.offLenTree    = this.markOffLenTree;
        }
    }

    /**
     * 接続された入力ストリームが mark() と reset() を
     * サポートするかを得る。<br>
     * 
     * @return ストリームが mark() と reset() を
     *         サポートする場合は true。<br>
     *         サポートしない場合は false。<br>
     */
    public boolean markSupported(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
     * InputStream の available() と違い、
     * この最低バイト数は必ずしも保障されていない事に注意すること。<br>
     * 
     * @return ブロックしないで読み出せる最低バイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        int avail = ( ( this.cacheLimit - this.cachePosition )
                    + this.in.available() / this.cache.length * this.cache.length );//throws IOException
        avail += this.bitCount - 32;
        avail = avail / StaticHuffman.LIMIT_LEN;
        if( this.blockSize < avail ){
            avail -= 272;
        }
        return Math.max( avail, 0 );
    }

    /**
     * このストリームを閉じ、使用していた全ての資源を解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException
        this.in                    = null;

        this.cache                 = null;
        this.cacheLimit            = 0;
        this.cachePosition         = 0;
        this.bitBuffer             = 0;
        this.bitCount              = 0;

        this.markCache             = null;
        this.markCacheLimit        = 0;
        this.markCachePosition     = 0;
        this.markBitBuffer         = 0;
        this.markBitCount          = 0;
        this.markPositionIsInCache = false;

        this.blockSize       = 0;
        this.codeLen         = null;
        this.codeTable       = null;
        this.codeTree        = null;
        this.offLenLen       = null;
        this.offLenTable     = null;
        this.offLenTree      = null;

        this.markBlockSize   = 0;
        this.markCodeLen     = null;
        this.markCodeTable   = null;
        this.markCodeTree    = null;
        this.markOffLenLen   = null;
        this.markOffLenTable = null;
        this.markOffLenTree  = null;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * この PreLh5Decoder が扱うLZSS辞書のサイズを得る。
     * 
     * @return この PreLh5Decoder が扱うLZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return this.DictionarySize;
    }

    /**
     * この PreLh5Decoder が扱うLZSSの最大一致長を得る。
     * 
     * @return この PreLh5Decoder が扱うLZSSの最大一致長
     */
    public int getMaxMatch(){
        return this.MaxMatch;
    }

    /**
     * この PreLh5Decoder が扱う圧縮、非圧縮の閾値を得る。
     * 
     * @return この PreLh5Decoder が扱う圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return this.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  read block head
    //------------------------------------------------------------------
    //  private void readBlockHead()
    //  private int[] readCodeLenLenList()
    //  private int[] readCodeLenList( HuffmanDecoder decoder )
    //  private int[] readOffLenLenList()
    //------------------------------------------------------------------
    /**
     * ハフマンブロックの先頭にある
     * ブロックサイズやハフマン符号長のリストを読み込む。
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BadHuffmanTableException
     *                         ハフマン木を構成するための
     *                         ハフマン符号長の表が不正なため、
     *                         ハフマン復号器が生成できない場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     * @exception NotEnoughBitsException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private void readBlockHead() throws IOException {
        //ブロックサイズ読み込み
        //正常なデータの場合、この部分で EndOfStream に到達する。
        try{
            this.blockSize = this.readBits( 16 );                               //throws BitDataBrokenException, EOFException, IOException
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof EOFException ){
                throw (EOFException)exception.getCause();
            }else{
                throw exception;
            }
        }

        //codeLen 部の処理
        int[] codeLenLen = this.readCodeLenLen();                               //throws BitDataBrokenException, EOFException, IOException
        short[] codeLenTable;
        if( null != codeLenLen ){
            codeLenTable = StaticHuffman.createTable( codeLenLen );             //throws BadHuffmanTableException
        }else{
            codeLenTable = new short[]{ (short)this.readBits( 5 ) };            //throws BitDataBrokenException EOFException IOException
            codeLenLen   = new int[ codeLenTable[0] + 1 ];
        }

        //code 部の処理
        this.codeLen = this.readCodeLen( codeLenTable, codeLenLen );            //throws BitDataBrokenException NotEnoughBitsException EOFException IOException
        if( null != this.codeLen ){
            short[][] tableAndTree = 
                StaticHuffman.createTableAndTree( this.codeLen, this.codeTableBits );//throws BadHuffmanTableException
            this.codeTable = tableAndTree[0];
            this.codeTree  = new short[][]{ tableAndTree[1], tableAndTree[2] };
        }else{
            int code = this.readBits( 9 );                                      //throws BitDataBrokenException EOFException IOException
            this.codeLen   = new int[ 256 + this.MaxMatch - this.Threshold + 1 ];
            this.codeTable = new short[ 1 << this.codeTableBits ];
            for( int i = 0 ; i < this.codeTable.length ; i++ ){
                this.codeTable[i] = ((short)~code);
            }
            this.codeTree = new short[][]{ new short[0], new short[0] };
        }

        //offLen 部の処理
        this.offLenLen = this.readOffLenLen();                                  //throws BitDataBrokenException EOFException IOException
        if( null != this.offLenLen ){
            short[][] tableAndTree = 
                StaticHuffman.createTableAndTree( this.offLenLen, this.offLenTableBits );//throws BadHuffmanTableException
            this.offLenTable = tableAndTree[0];
            this.offLenTree  = new short[][]{ tableAndTree[1], tableAndTree[2] };
        }else{
            int offLen = this.readBits( Bits.len( Bits.len( this.DictionarySize ) ) );//throws BitDataBrokenException EOFException IOException
            this.offLenLen   = new int[ Bits.len( this.DictionarySize ) ];
            this.offLenTable = new short[ 1 << this.offLenTableBits ];
            for( int i = 0 ; i < this.offLenTable.length ; i++ ){
                this.offLenTable[i] = ((short)~offLen);
            }
            this.offLenTree = new short[][]{ new short[0], new short[0] };
        }
    }

    /**
     * Codeのハフマン符号長のリストの
     * ハフマン符号を復号するための
     * ハフマン符号長のリストを読みこむ。
     * 
     * @return ハフマン符号長のリスト。
     *         符号長のリストが無い場合は null
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private int[] readCodeLenLen() throws IOException {
        int listlen = this.readBits( 5 );                                       //throws BitDataBrokenException, EOFException, IOException
        if( 0 < listlen ){
            int[] codeLenLen = new int[listlen];
            int   index = 0;

            while( index < listlen ){
                int codelenlen = this.readBits( 3 );                            //throws BitDataBrokenException, EOFException, IOException
                if( codelenlen == 0x07 ){
                    while( this.readBoolean() ) codelenlen++;                   //throws EOFException, IOException
                }
                codeLenLen[index++] = codelenlen;

                if( index == 3 ){
                    index += this.readBits( 2 );                                //throws BitDataBrokenException, EOFException, IOException
                }
            }
            return codeLenLen;
        }else{
            return null;
        }
    }

    /**
     * Codeのハフマン符号長のリストを復号しながら読みこむ
     * 
     * @return ハフマン符号長のリスト。
     *         符号長のリストが無い場合は null
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     * @exception NotEnouthBitsException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private int[] readCodeLen( short[] codeLenTable, int[] codeLenLen ) 
                                                            throws IOException {

        final int codeLenTableBits = Bits.len( codeLenTable.length - 1 );

        int listlen = this.readBits( 9 );                                       //throws BitDataBrokenException, EOFException, IOException
        if( 0 < listlen ){
            int[] codeLen = new int[listlen];
            int   index = 0;

            while( index < listlen ){
                this.fillBitBuffer();
                int bits = ( 0 < codeLenTableBits
                           ? this.bitBuffer >>> ( 32 - codeLenTableBits )
                           : 0 );
                int codelen = codeLenTable[ bits ];
                int len = codeLenLen[ codelen ];
                this.bitBuffer <<= len;
                this.bitCount   -= len;

                if( codelen == 0 )      index++;
                else if( codelen == 1 ) index += this.readBits( 4 ) + 3;        //throws BitDataBrokenException, EOFException, IOException
                else if( codelen == 2 ) index += this.readBits( 9 ) + 20;       //throws BitDataBrokenException, EOFException, IOException
                else                    codeLen[index++] = codelen - 2;
            }
            return codeLen;
        }else{
            return null;
        }
    }

    /**
     * offLen のハフマン符号長のリストを読みこむ
     * 
     * @return ハフマン符号長のリスト。
     *         符号長のリストが無い場合は null
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private int[] readOffLenLen() throws IOException {
        int listlen = this.readBits( Bits.len( Bits.len( this.DictionarySize ) ) );//throws BitDataBrokenException, EOFException, IOException
        if( 0 < listlen ){
            int[] offLenLen = new int[listlen];
            int   index = 0;

            while( index < listlen ){
                int offlenlen = this.readBits( 3 );                             //throws BitDataBrokenException, EOFException, IOException
                if( offlenlen == 0x07 ){
                    while( this.readBoolean() ) offlenlen++;                    //throws EOFException, IOException
                }
                offLenLen[index++] = offlenlen;
            }
            return offLenLen;
        }else{
            return null;
        }
    }


    //------------------------------------------------------------------
    //  staff of BitInputStream
    //------------------------------------------------------------------
    //  bit read
    //------------------------------------------------------------------
    //  private boolean readBoolean()
    //  private int readBits( int count )
    //  private int cachedBits()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームから 1ビットのデータを
     * 真偽値として読み込む。<br>
     * 
     * @return 読み込まれた1ビットのデータが 
     *         1であれば true、0であれば false を返す。<br>
     * 
     * @exception EOFException 既にEndOfStreamに達していた場合
     * @exception IOException  接続された入力ストリームで
     *                         入出力エラーが発生した場合
     */
    private boolean readBoolean() throws IOException {
        if( 0 < this.bitCount ){
            boolean bool = ( this.bitBuffer < 0 );
            this.bitBuffer <<= 1;
            this.bitCount   -= 1;
            return bool;
        }else{
            this.fillBitBuffer();
            boolean bool = ( this.bitBuffer < 0 );
            this.bitBuffer <<= 1;
            this.bitCount   -= 1;
            return bool;
        }
    }

    /**
     * 接続された入力ストリームから count ビットのデータを
     * 読み込む。 戻り値が int値である事からも判るように
     * 読み込むことのできる 最大有効ビット数は 32ビットで
     * あるが、count は32以上の値を設定してもチェックを
     * 受けないため それ以上の値を設定した場合は ビット
     * データが読み捨てられる。<br>
     * たとえば readBits( 33 ) としたときは まず1ビットの
     * データを読み捨て、その後の 32ビットのデータを返す。<br>
     * また count に 0以下の数字を設定して呼び出した場合、
     * データを読み込む動作を伴わないため 戻り値は 常に0、
     * EndOfStream に達していても EOFException を
     * 投げない点に注意すること。<br>
     * 
     * @param count  読み込むデータのビット数
     * 
     * @return 読み込まれたビットデータ。<br>
     * 
     * @exception IOException 
     *               接続された入力ストリームで
     *               入出力エラーが発生した場合
     * @exception EOFException 
     *               既にEndOfStreamに達していた場合
     * @exception BitDataBrokenException 
     *               読み込み途中で EndOfStreamに達したため
     *               要求されたビット数のデータの読み込み
     *               に失敗した場合。<br>
     */
    private int readBits( int count ) throws IOException {
        if( 0 < count ){
            if( count <= this.bitCount ){
                int bits = this.bitBuffer >>> ( 32 - count );
                this.bitBuffer <<= count;
                this.bitCount   -= count;
                return bits;
            }else{
                final int requested = count;
                int bits = 0;
                try{
                    this.fillBitBuffer();                                       //throws LocalEOFException IOException
                    while( this.bitCount < count ){
                        count -= this.bitCount;
                        if( count < 32 ){
                            bits |= ( this.bitBuffer >>> ( 32 - this.bitCount ) ) << count;
                        }
                        this.bitBuffer = 0;
                        this.bitCount  = 0;
                        this.fillBitBuffer();                                   //throws LocalEOFException IOException
                    }
                    bits |= this.bitBuffer >>> ( 32 - count );
                    this.bitBuffer <<= count;
                    this.bitCount   -= count;
                    return bits;
                }catch( LocalEOFException exception ){
                    if( exception.thrownBy( this ) && count < requested ){
                        throw new BitDataBrokenException( exception, bits >>> count, requested - count );
                    }else{
                        throw exception;
                    }
                }
            }
        }else{
            return 0;
        }
    }

    /**
     * この BitInputStream 内に蓄えられているビット数を得る。<br>
     * 
     * @return この BitInputStream 内に蓄えられているビット数。<br>
     */
    private int cachedBits(){
        return this.bitCount + ( ( this.cacheLimit - this.cachePosition ) << 3 );
    }


    //------------------------------------------------------------------
    //  staff of BitInputSteram
    //------------------------------------------------------------------
    //  fill
    //------------------------------------------------------------------
    //  private void fillBitBuffer()
    //  private void fillCache()
    //------------------------------------------------------------------
    /**
     * bitBuffer にデータを満たす。
     * EndOfStream 付近を除いて bitBuffer には
     * 25bit のデータが確保されることを保障する。
     * 
     * @exception IOException       入出力エラーが発生した場合
     * @exception LocalEOFException bitBuffer が空の状態で EndOfStream に達した場合
     */
    private void fillBitBuffer() throws IOException {
        if( 32 <= this.cachedBits() ){
            if( this.bitCount <= 24 ){
                if( this.bitCount <= 16 ){
                    if( this.bitCount <= 8 ){
                        if( this.bitCount <= 0 ){
                            this.bitBuffer = this.cache[this.cachePosition++] << 24;
                            this.bitCount  = 8;
                        }
                        this.bitBuffer |= ( this.cache[this.cachePosition++] & 0xFF )
                                                            << ( 24 - this.bitCount );
                        this.bitCount  += 8;
                    }
                    this.bitBuffer |= ( this.cache[this.cachePosition++] & 0xFF )
                                                        << ( 24 - this.bitCount );
                    this.bitCount  += 8;
                }
                this.bitBuffer |= ( this.cache[this.cachePosition++] & 0xFF )
                                                    << ( 24 - this.bitCount );
                this.bitCount  += 8;
            }
        }else if( this.bitCount < 25 ){
            if( this.bitCount == 0 ){
                this.bitBuffer = 0;
            }

            int count = Math.min( ( 32 - this.bitCount ) >> 3, 
                                  this.cacheLimit - this.cachePosition );
            while( 0 < count-- ){
                this.bitBuffer |= ( this.cache[this.cachePosition++] & 0xFF )
                                                    << ( 24 - this.bitCount );
                this.bitCount  += 8;
            }
            this.fillCache();                                                   //throws IOException
            if( this.cachePosition < this.cacheLimit ){
                count = Math.min( ( 32 - this.bitCount ) >> 3, 
                                  this.cacheLimit - this.cachePosition );
                while( 0 < count-- ){
                    this.bitBuffer |= ( this.cache[this.cachePosition++] & 0xFF )
                                                        << ( 24 - this.bitCount );
                    this.bitCount  += 8;
                }
            }else if( this.bitCount <= 0 ){
                throw new LocalEOFException( this );
            }
        }
    }

    /**
     * cache が空になった時に cache にデータを読み込む。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void fillCache() throws IOException {
        this.markPositionIsInCache = false;
        this.cacheLimit            = 0;
        this.cachePosition         = 0;

        //cache にデータを読み込む
        int read = 0;
        while( 0 <= read && this.cacheLimit < this.cache.length ){
            read = this.in.read( this.cache,
                                 this.cacheLimit, 
                                 this.cache.length - this.cacheLimit );         //throws IOException

            if( 0 < read ) this.cacheLimit += read;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class LocalEOFException
    //------------------------------------------------------------------
    /**
     * BitInputStream 内で EndOfStream の検出に
     * EOFException を使用するのは少々問題があるので
     * ローカルな EOFException を定義する。
     */
    private static class LocalEOFException extends EOFException {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private Object owner
        //------------------------------------------------------------------
        /**
         * この例外を投げたオブジェクト
         */
        private Object owner;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public LocalEOFException()
        //  public LocalEOFException( String message )
        //------------------------------------------------------------------
        /**
         * コンストラクタ。
         * 
         * @param object この例外を投げたオブジェクト
         */
        public LocalEOFException( Object object ){
            super();
            this.owner = object;
        }

        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public boolean thrownBy( Object object )
        //------------------------------------------------------------------
        /**
         * この例外が object によって投げられたかどうかを得る。<br>
         * 
         * @param object オブジェクト
         * 
         * @return この例外が objectによって
         *         投げられた例外であれば true<br>
         *         違えば false<br>
         */
        public boolean thrownBy( Object object ){
            return this.owner == object;
        }
    }
}
//end of PreLh5Decoder.java
