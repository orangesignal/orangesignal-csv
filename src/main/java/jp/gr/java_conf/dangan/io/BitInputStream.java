//start of BitInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BitInputStream.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces
import java.io.InputStream;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;

/**
 * ビット入力のためのユーティリティクラス。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BitInputStream.java,v $
 * Revision 1.5  2002/12/07 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.4  2002/11/15 00:00:00  dangan
 * [improvement]
 *     prefetchBits() が  32bit の読み込みを保証するように修正
 * [change]
 *     メソッド名の変更
 *     prefetchBit     -> peekBit
 *     prefetchBoolean -> peekBoolean
 *     prefetchBits    -> peekBits
 *
 * Revision 1.3  2002/11/02 00:00:00  dangan
 * [bug fix]
 *     available() availableBits() で
 *     ブロックせずに読み込める量よりも大きい値を返していた。
 *
 * Revision 1.2  2002/09/05 00:00:00  dangan
 * [change]
 *     EndOfStream に達した後の read( new byte[0] ) や 
 *     read( byte[] buf, int off, 0 ) の戻り値を
 *     InputStream と同じく 0 になるようにした
 *
 * Revision 1.1  2002/09/04 00:00:00  dangan
 * [bug fix]
 *     skip( len ) と skipBits( len ) で len が 0 未満のとき
 *     正しく処理できていなかった。
 *
 * Revision 1.0  2002/09/03 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     mark() で 接続された in に渡す readLimit の計算が甘かったため、
 *     要求された readLimit に達する前にマーク位置が破棄される事があった。
 *     EndOfStream に達した後の skip() および skip( 0 ) が -1 を返していた。
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.5 $
 */
public class BitInputStream extends InputStream{

    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  default
    //------------------------------------------------------------------
    //  private static final int DefaultCacheSize
    //------------------------------------------------------------------
    /**
     * デフォルトのキャッシュサイズ
     */
    private static final int DefaultCacheSize = 1024;


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
    //  bit buffer
    //------------------------------------------------------------------
    //  private int    bitBuffer
    //  private int    bitCount
    //------------------------------------------------------------------
    /**
     * ビットバッファ。
     * ビットデータは最上位ビットから bitCount だけ格納されている。
     */
    private int    bitBuffer;

    /**
     * bitBuffer の 有効ビット数
     */
    private int    bitCount;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[] markCache
    //  private int    markCacheLimit
    //  private int    markCachePosition
    //  private int    markBitBuffer
    //  private int    markBitCount
    //------------------------------------------------------------------
    /**
     * mark位置がキャッシュの範囲内にあるかを示す。
     * markされたとき true に設定され、
     * 次に in から キャッシュへの読み込みが
     * 行われたときに false に設定される。
     */
    private boolean markPositionIsInCache;

    /** cache の バックアップ用 */
    private byte[] markCache;

    /** cacheLimit のバックアップ用 */
    private int    markCacheLimit;

    /** cachePosition のバックアップ用 */
    private int    markCachePosition;

    /** bitBuffer のバックアップ用 */
    private int    markBitBuffer;

    /** bitCount のバックアップ用 */
    private int    markBitCount;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private BitInputStream()
    //  public BitInputStream( InputStream in )
    //  public BitInputStream( InputStream in, int CacheSize )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private BitInputStream(){ }

    /**
     * 入力ストリーム in からのデータをビット単位で
     * 読み込めるようなストリームを構築する。<br>
     * 
     * @param in 入力ストリーム
     */
    public BitInputStream( InputStream in ){
        this( in, BitInputStream.DefaultCacheSize );
    }

    /**
     * 入力ストリーム in からのデータをビット単位で
     * 読み込めるようなストリームを構築する。<br>
     * 
     * @param in        入力ストリーム
     * @param CacheSize バッファサイズ
     */
    public BitInputStream( InputStream in, int CacheSize ){
        if( in != null && 4 <= CacheSize ){
            this.in                    = in;
            this.cache                 = new byte[ CacheSize ];
            this.cacheLimit            = 0;
            this.cachePosition         = 0;
            this.bitBuffer             = 0;
            this.bitCount              = 0;

            this.markPositionIsInCache = false;
            this.markCache             = null;
            this.markCacheLimit        = 0;
            this.markCachePosition     = 0;
            this.markBitBuffer         = 0;
            this.markBitCount          = 0;
        }else if( in == null ){
            throw new NullPointerException( "in" );
        }else{
            throw new IllegalArgumentException( "CacheSize must be 4 or more." );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------
    /**
     * 接続されたストリームから 8ビットのデータを読み込む。<br>
     * 
     * @return 読み出された 8ビットのデータ。<br>
     *         既に EndOfStream に達している場合は -1
     * 
     * @exception IOException
     *               接続された入力ストリームで
     *               入出力エラーが発生した場合
     * @exception BitDataBrokenException 
     *               EndOfStreamに達したため
     *               要求されたビット数のデータの
     *               読み込みに失敗した場合。<br>
     */
    public int read() throws IOException {
        try{
            return this.readBits( 8 );                                          //throws LocalEOFException BitDataBrokenException IOException
        }catch( LocalEOFException exception ){
            if( exception.thrownBy( this ) ) return -1;
            else                             throw exception;
        }
    }

    /**
     * 接続された入力ストリームから バイト配列 buffer を
     * 満たすようにデータを読み込む。<br>
     * データは必ずしも buffer を満たすとは限らないことに注意。<br>
     * 
     * @param buffer 読み込まれたデータを格納するためのバイト配列
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既に EndOfStream に達していた場合は -1 を返す。<br>
     * 
     * @exception IOException
     *               接続された入力ストリームで
     *               入出力エラーが発生した場合
     * @exception BitDataBrokenException 
     *               EndOfStreamに達したため
     *               要求されたビット数のデータの
     *               読み込みに失敗した場合。<br>
     */
    public int read( byte[] buffer ) throws IOException {
        return this.read( buffer, 0, buffer.length );                           //throws BitDataBrokenException IOException
    }

    /**
     * 接続された入力ストリームから バイト配列 buffer の
     * index で指定された位置から length バイトのデータを
     * 読み込む。<br>
     * このメソッドは lengthバイト読み込むか、
     * EndOfStream に到達するまでブロックする。<br>
     * データは必ずしも length バイト読み込まれるとは限ら
     * ないことに注意。<br>
     * 
     * @param buffer 読み込まれたデータを格納するためのバイト配列
     * @param index  buffer内のデータ読み込み開始位置
     * @param length bufferに読み込むデータ量
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既に EndOfStream に達していた場合は -1 を返す。<br>
     * 
     * @exception IOException
     *               接続された入力ストリームで
     *               入出力エラーが発生した場合
     * @exception BitDataBrokenException 
     *               EndOfStreamに達したため
     *               要求されたビット数のデータの
     *               読み込みに失敗した場合。<br>
     */
    public int read( byte[] buffer, int index, int length ) throws IOException {
        final int requested = length;
        try{
            while( 0 < length ){
                buffer[index++] = (byte)this.readBits( 8 );                     //throws LocalEOFException BitDataBrokenException IOException
                length--;
            }
            return requested;
        }catch( LocalEOFException exception ){
            if( exception.thrownBy( this ) ){
                if( requested != length ) return requested - length;
                else                      return -1;
            }else{
                throw exception;
            }
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof LocalEOFException 
             && ((LocalEOFException)exception.getCause()).thrownBy( this ) ){
                this.bitBuffer >>>= exception.getBitCount();
                this.bitCount  +=   exception.getBitCount();
                this.bitBuffer |= exception.getBitData() <<
                                    ( 32 - exception.getBitCount() );

                return requested - length;
            }else{
                throw exception;
            }
        }
    }

    /**
     * 接続された入力ストリームのデータを length バイト
     * 読み飛ばす。<br>
     * このメソッドは lengthバイト読み飛ばすか、
     * EndOfStream に到達するまでブロックする。<br>
     * データは必ずしも length バイト読み飛ばされるとは限ら
     * ないことに注意。<br>
     * 
     * @param length 読み飛ばすバイト数。<br>
     * 
     * @return 実際に読み飛ばされたバイト数。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {
        length = ( 0 < length ? length : 0 );
        final long requested = length;
        try{
            while( 0 < length ){
                this.readBits( 8 );
                length--;
            }
            return requested;
        }catch( LocalEOFException exception ){
            return requested - length;
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof LocalEOFException 
             && ((LocalEOFException)exception.getCause()).thrownBy( this ) ){
                this.bitBuffer >>>= exception.getBitCount();
                this.bitCount  +=   exception.getBitCount();
                this.bitBuffer |=   exception.getBitData() <<
                                      ( 32 - exception.getBitCount() );
                return requested - length;
            }else{
                throw exception;
            }
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
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
     * 
     * @param readLimit マーク位置に戻れる限界のバイト数。
     *                  このバイト数を超えてデータを読み
     *                  込んだ場合 reset()できなくなる可
     *                  能性がある。<br>
     */
    public void mark( int readLimit ){
        readLimit -= this.cacheLimit - this.cachePosition;
        readLimit -= this.bitCount / 8;
        readLimit += 4;
        readLimit  = ( ( readLimit / this.cache.length ) * this.cache.length
                     + ( readLimit % this.cache.length == 0 ? 0 : this.cache.length ) );

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
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException <br>
     *              (1) BitInputStream に mark がなされていない場合。<br>
     *              (2) 接続された入力ストリームが markSupported()で
     *                  false を返す場合。<br>
     *              (3) 接続された入力ストリームで
     *                  入出力エラーが発生した場合。<br>
     *              の何れか。
     */
    public void reset() throws IOException {
        if( this.markPositionIsInCache ){
            this.cachePosition = this.markCachePosition;
            this.bitBuffer     = this.markBitBuffer;
            this.bitCount      = this.markBitCount;
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
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームからブロックしないで
     * 読み込むことのできるバイト数を得る。<br>
     * 
     * @return ブロックしないで読み出せるバイト数。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int available() throws IOException {
        return this.availableBits() / 8;                                        //throws IOException
    }

    /**
     * この入力ストリームを閉じ、
     * 使用していたリソースを開放する。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
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
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readBit()
    //  public boolean readBoolean()
    //  public int readBits( int count )
    //  public int skipBits( int count )
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームから 1ビットのデータを
     * 読み込む。<br>
     * 
     * @return 読み込まれた1ビットのデータ。<br>
     *         既にEndOfStreamに達している場合は -1。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int readBit() throws IOException {
        if( 0 < this.bitCount ){
            int bit = this.bitBuffer >>> 31;
            this.bitBuffer <<= 1;
            this.bitCount   -= 1;
            return bit;
        }else{
            try{
                this.fillBitBuffer();
                int bit = this.bitBuffer >>> 31;
                this.bitBuffer <<= 1;
                this.bitCount   -= 1;
                return bit;
            }catch( LocalEOFException exception ){
                if( exception.thrownBy( this ) ){
                    return -1;
                }else{
                    throw exception;
                }
            }
        }
    }

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
    public boolean readBoolean() throws IOException {
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
    public int readBits( int count ) throws IOException {
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
     * 接続されたストリームから count ビットのデータを
     * 読み飛ばす。<br>
     * 
     * @param count 読み飛ばしてほしいビット数
     * 
     * @return 実際に読み飛びしたビット数
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int skipBits( int count ) throws IOException {
        count = Math.max( count, 0 );

        if( count < this.bitCount ){
            this.bitBuffer <<= count;
            this.bitCount  -=  count;
            return count;
        }else{
            final int requested = count;
            count -= this.bitCount;
            this.bitCount  = 0;
            this.bitBuffer = 0;
            try{
                while( ( this.cacheLimit - this.cachePosition ) * 8 <= count ){
                    count -= ( this.cacheLimit - this.cachePosition ) * 8;
                    this.cachePosition = this.cacheLimit;
                    this.fillCache();
                    if( this.cacheLimit == this.cachePosition ){
                        throw new LocalEOFException( this );
                    }
                }
                this.cachePosition += ( count >> 3 );
                count = count & 0x07;
                if( 0 < count ){
                    this.bitCount  = 8 - count;
                    this.bitBuffer = this.cache[ this.cachePosition++ ] << ( 24 + count );
                    count = 0;
                }
            }catch( LocalEOFException exception ){
            }
            return requested - count;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  prefetch
    //------------------------------------------------------------------
    //  public int peekBit()
    //  public boolean peekBoolean()
    //  public int peekBits( int count )
    //------------------------------------------------------------------
    /**
     * 読み込み位置を変えずに 1ビットのデータを先読みする。<br>
     * 
     * @return 読み込まれた1ビットのデータ。<br>
     *         既にEndOfStreamに達している場合は -1。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int peekBit() throws IOException {
        if( 0 < this.bitCount ){
            return this.bitBuffer >>> 31;
        }else{
            try{
                this.fillBitBuffer();                                           //throws LocalEOFException IOException
                return this.bitBuffer >>> 31;
            }catch( LocalEOFException exception ){
                if( exception.thrownBy( this ) ){
                    return -1;
                }else{
                    throw exception;
                }
            }

        }
    }

    /**
     * 読み込み位置を変えずに 1ビットのデータを
     * 真偽値として先読みする。<br>
     * 
     * @return 読み込まれた1ビットのデータが 
     *         1であれば true、0であれば false を返す。<br>
     * 
     * @exception EOFException 既にEndOfStreamに達していた場合
     * @exception IOException  接続された入力ストリームで
     *                         入出力エラーが発生した場合
     */
    public boolean peekBoolean() throws IOException {
        if( 0 < this.bitCount ){
            return ( this.bitBuffer < 0 );
        }else{
            this.fillBitBuffer();                                               //throws LocalEOFException IOException
            return ( this.bitBuffer < 0 );
        }
    }

    /**
     * 読み込み位置を変えずに count ビットのデータを先読みする。<br>
     * 戻り値が int型であることからもわかるように
     * 最大有効ビット数は 32ビットである。<br>
     * EndOfStream 付近を除いて、先読み出来ることが保障されるのは
     * 32ビットである。(ビットバッファの大きさが 32ビットであるため)<br>
     * もし 32ビット以上の先読み機能が必須となる場合は
     * その都度 mark()、readBits()、reset() を繰り返すか、
     * このクラスを使用することを諦めること。<br>
     * 
     * @param count 読み込むビット数
     * 
     * @return 先読みした count ビットのビットデータ
     * 
     * @exception EOFException
     *                    既にEndOfStreamに達していた場合
     * @exception IOException
     *                    接続された入力ストリームで
     *                    入出力エラーが発生した場合
     * @exception NotEnoughBitsException
     *                    count が先読み可能な範囲外の場合
     */
    public int peekBits( int count ) throws IOException {
        if( 0 < count ){
            if( count <= this.bitCount ){
                return this.bitBuffer >>> ( 32 - count );
            }else{
                this.fillBitBuffer();                                           //throws LocalEOFException, IOException
                if( count <= this.bitCount ){
                    return this.bitBuffer >>> ( 32 - count );
                }else if( count <= this.cachedBits() ){
                    if( count <= 32 ){
                        int bits = this.bitBuffer;
                        bits |= ( this.cache[ this.cachePosition ] & 0xFF ) 
                                                  >> ( this.bitCount - 24 );
                        return bits >>> ( 32 - count );
                    }else if( count - 32 < this.bitCount ){
                        int bits  = this.bitBuffer << ( count - 32 );;
                        int bcnt = this.bitCount - ( count - 32 );
                        int pos   = this.cachePosition;
                        while( bcnt < 25 ){
                            bits  |= ( this.cache[ pos++ ] & 0xFF ) << ( 24 - bcnt );
                            bcnt += 8; 
                        }
                        if( bcnt < 32 ){
                            bits  |= ( this.cache[ pos ] & 0xFF ) >> ( bcnt - 24 );
                        }
                        return bits;
                    }else{
                        count  -= this.bitCount;
                        count  -= 32;
                        int pos = this.cachePosition + ( count >> 3 );
                        count  &= 0x07;
                        if( 0 < count ){
                            return   (   this.cache[ pos ]              << ( 24 + count ) )
                                   | ( ( this.cache[ pos + 1 ] & 0xFF ) << ( 16 + count ) )
                                   | ( ( this.cache[ pos + 2 ] & 0xFF ) << (  8 + count ) )
                                   | ( ( this.cache[ pos + 3 ] & 0xFF ) << count )
                                   | ( ( this.cache[ pos + 4 ] & 0xFF ) >> (  8 - count ) );
                        }else{
                            return   (   this.cache[ pos ]              << 24 )
                                   | ( ( this.cache[ pos + 1 ] & 0xFF ) << 16 )
                                   | ( ( this.cache[ pos + 2 ] & 0xFF ) <<  8 )
                                   |   ( this.cache[ pos + 3 ] & 0xFF );
                        }
                    }
                }else{
                    throw new NotEnoughBitsException( this.cachedBits() );
                }
            }
        }else{
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int availableBits()
    //  private int cachedBits()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームからブロックしないで
     * 読み込むことのできるビット数を得る。<br>
     * 
     * @return ブロックしないで読み出せるビット数。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int availableBits() throws IOException {
        int avail = ( this.cacheLimit - this.cachePosition )
                  + this.in.available() / this.cache.length * this.cache.length;//throws IOException
        avail += this.bitCount - 32;

        return Math.max( avail, 0 );
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
    //  local method
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
        //  constructer
        //------------------------------------------------------------------
        //  public LocalEOFException( Object object )
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
        //  access method
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
//end of BitInputStream.java
