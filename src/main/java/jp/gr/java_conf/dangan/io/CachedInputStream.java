//start of CachedInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CachedInputStream.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces
import java.io.InputStream;

//import exceptions
import java.io.IOException;
import java.lang.IllegalArgumentException;

/**
 * キャッシュを使用して高速化するための入力ストリーム。<br> 
 * BufferedInputStream とは read系メソッドが synchronized
 * されていないため、同期処理によるロスがない、mark/reset は
 * キャッシュ内の読み込み位置の移動で行えるときのみサポートであり、
 * それ以上は接続された入力ストリームの性能による、等の違いがある。
 * 
 * <pre>
 * -- revision history --
 * $Log: CachedInputStream.java,v $
 * Revision 1.3  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.2  2002/11/02 00:00:00  dangan
 * [bug fix]
 *     available() でブロックせずに読み込める量よりも大きい値を返していた。
 *
 * Revision 1.1  2002/09/05 00:00:00  dangan
 * [change]
 *     EndOfStream に達した後の read( new byte[0] ) や 
 *     read( byte[] buf, int off, 0 ) の戻り値を
 *     InputStream と同じく 0 になるようにした。
 *
 * Revision 1.0  2002/09/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     mark() で 接続された in に渡す readLimit の計算が甘かったため、
 *     要求された readLimit に達する前にマーク位置が破棄される事があった。
 *     read( buf, off, len ) 内の System.arraycopy の呼び出しで 
 *     dst と src を逆にしていた。
 * [change]
 *     EndOfStream に達した後の read( new byte[0] ) や
 *     read( buf, off，0 )  が -1 を返すように修正。
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.3 $
 */
public class CachedInputStream extends InputStream{

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
     * データを供給する入力ストリーム
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  cache
    //------------------------------------------------------------------
    //  private byte[] cache
    //  private int cachePosition
    //  private int cacheLimit
    //------------------------------------------------------------------
    /**
     * データを蓄えるためのキャッシュ
     */
    private byte[] cache;

    /**
     * cache内の現在処理位置
     */
    private int cachePosition;

    /**
     * cacheの読み込み限界位置
     */
    private int cacheLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markPositionIsInCache
    //  private byte[] markCache
    //  private int markCachePosition
    //  private int markCacheLimit
    //------------------------------------------------------------------
    /**
     * mark位置がキャッシュの範囲内にあるかを示す。
     * markされたとき true に設定され、
     * 次に in から キャッシュへの読み込みが
     * 行われたときに false に設定される。
     */
    private boolean markPositionIsInCache;

    /** cacheのバックアップ用 */
    private byte[] markCache;

    /** cachePositionのバックアップ用 */
    private int markCachePosition;

    /** cacheLimitのバックアップ用 */
    private int markCacheLimit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private CachedInputStream()
    //  public CachedInputStream( InputStream in )
    //  public CachedInputStream( InputStream in, int cacheSize )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private CachedInputStream(){ }

    /**
     * デフォルトのサイズのキャッシュを持つ
     * CachedInputStreamを構築する。
     * 
     * @param in キャッシュが必要な入力ストリーム
     * 
     * @exception IllegalArgumentException
     *                     in が null だった場合
     */
    public CachedInputStream( InputStream in ){
        this( in, CachedInputStream.DefaultCacheSize );
    }

    /**
     * 指定されたサイズのキャッシュを持つ
     * CachedInputStreamを構築する。
     * 
     * @param in        キャッシュが必要な入力ストリーム
     * @param cacheSize キャッシュのサイズ
     * 
     * @exception IllegalArgumentException
     *                     cacheSize が 0以下であるか、
     *                     in が null だった場合
     */
    public CachedInputStream( InputStream in, int cacheSize ){
        if( in != null && 0 < cacheSize ){
            this.in = in;

            this.cache                 = new byte[cacheSize];
            this.cachePosition         = 0;
            this.cacheLimit            = 0;

            this.markPositionIsInCache = false;
            this.markCache             = null;
            this.markCachePosition     = 0;
            this.markCacheLimit        = 0;

        }else if( in == null ){
            throw new IllegalArgumentException( "in must not be null." );
        }else{
            throw new IllegalArgumentException( "cacheSize must be one or more." );
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
     * 接続されたストリームから 1バイトのデータを
     * 0～255 にマップして読み込む。
     * 
     * @return 読み出された 1バイトのデータを返す。<br>
     *         既に EndOfStreamに達していた場合は -1を返す。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int read() throws IOException {
        if( this.cachePosition < this.cacheLimit ){
            return this.cache[ this.cachePosition++ ] & 0xFF;
        }else{
            this.fillCache();                                                     //throws IOException

            if( this.cachePosition < this.cacheLimit ){
                return this.cache[ this.cachePosition++ ] & 0xFF;
            }else{
                return -1;
            }
        }
    }

    /**
     * 接続されたストリームから bufferを満たすように
     * データを読み込む。<br>
     * このメソッドは buffer を満たすまでデータを読み込むか、
     * EndOfStreamに到達するまでブロックする。<br>
     * 
     * @param buffer 読み込んだデータを格納するためのバイト配列
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既に EndOfStreamに達していた場合は -1を返す。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int read( byte[] buffer ) throws IOException {
        return this.read( buffer, 0, buffer.length );
    }

    /**
     * 接続されたストリームから buffer に index で指定された
     * 位置へ length バイトデータを読み込む。<br>
     * このメソッドは length バイト読み込むか、
     * EndOfStreamに到達するまでブロックする。<br>
     * 
     * @param buffer 読み込んだデータを格納するためのバイト配列
     * @param index  buffer内のデータ読み込み開始位置
     * @param length bufferに読み込むデータ量
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既に EndOfStreamに達していた場合は -1を返す。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public int read( byte[] buffer, int index, int length ) 
                                                     throws IOException {
        final int requested = length;

        while( 0 < length ){
            if( this.cacheLimit <= this.cachePosition ){
                this.fillCache();                                             //throws IOException
                if( this.cacheLimit <= this.cachePosition ){
                    if( requested == length ){
                        return -1;
                    }else{
                        break;
                    }
                }
            }

            int copylen = Math.min( length,
                                    this.cacheLimit - this.cachePosition );
            System.arraycopy( this.cache, this.cachePosition,
                              buffer, index, copylen );

            index              += copylen;
            length             -= copylen;
            this.cachePosition += copylen;
        }

        return requested - length;
    }

    /**
     * 接続された入力ストリームのデータを length バイト読み飛ばす。<br>
     * このメソッドは length バイト読み飛ばすか
     * EndOfStream に到達するまでブロックする。<br>
     * 
     * @param length 読み飛ばすバイト数。<br>
     * 
     * @return 実際に読み飛ばされたバイト数。<br>
     * 
     * @exception IOException 接続された入力ストリームで
     *                        入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {
        final long requested = length;

        while( 0 < length ){
            if( this.cacheLimit <= this.cachePosition ){
                this.fillCache();                                             //throws IOException

                if( this.cacheLimit <= this.cachePosition ){
                    break;
                }
            }

            long skiplen = Math.min( length, (long)(this.cacheLimit - this.cachePosition) );

            length             -= skiplen;
            this.cachePosition += (int)skiplen;
        }

        return requested - length;
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
        readLimit = ( readLimit / this.cache.length ) * this.cache.length
                  + ( readLimit % this.cache.length == 0 ? 0 : this.cache.length );


        this.in.mark( readLimit );

        if( this.markCache == null ){
            this.markCache = (byte[])this.cache.clone();
        }else{
            System.arraycopy( this.cache, 0, this.markCache, 0, this.cacheLimit );
        }

        this.markCacheLimit        = this.cacheLimit;
        this.markCachePosition     = this.cachePosition;
        this.markPositionIsInCache = true;
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException <br>
     *              (1) CachedInputStream に mark がなされていない場合。<br>
     *              (2) 接続された入力ストリームが markSupported()で
     *                  false を返す場合。<br>
     *              (3) 接続された入力ストリームで
     *                  入出力エラーが発生した場合。<br>
     *              の何れか。
     */
    public void reset() throws IOException {
        if( this.markPositionIsInCache ){
            this.cachePosition  = this.markCachePosition;
        }else if( !this.in.markSupported() ){
            throw new IOException( "not support mark()/reset()." );
        }else if( this.markCache == null ){ //この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。 
            throw new IOException( "not marked." );
        }else{
            //in が reset() できない場合は
            //最初の行の this.in.reset() で
            //IOException を投げることを期待している。
            this.in.reset();                                                    //throws IOException
            System.arraycopy( this.markCache, 0, this.cache, 0, this.markCacheLimit );
            this.cacheLimit    = this.markCacheLimit;
            this.cachePosition = this.markCachePosition;
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
        return this.cacheLimit - this.cachePosition
              + ( this.in.available() / this.cache.length ) * this.cache.length;//throws IOException
    }

    /**
     * この入力ストリームを閉じ、使用していた
     * 全てのリソースを開放する。<br>
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

        this.markCache             = null;
        this.markCacheLimit        = 0;
        this.markCachePosition     = 0;
        this.markPositionIsInCache = false;
    }


    //------------------------------------------------------------------
    //  local methods
    //------------------------------------------------------------------
    //  private void fillCache()
    //------------------------------------------------------------------
    /**
     * 必要がある場合に、キャッシュ用バッファにデータを
     * 補填しキャッシュ用バッファに必ずデータが存在する
     * ことを保証するために呼ばれる。<br>
     * もし EndOfStream まで読み込まれている場合は データが
     * 補填されないことによって それを示す。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void fillCache() throws IOException {
        this.markPositionIsInCache = false;
        this.cacheLimit            = 0;
        this.cachePosition         = 0;

        //キャッシュにデータを読み込み
        int read = 0;
        while( 0 <= read && this.cacheLimit < this.cache.length ){
            read = this.in.read( this.cache,
                                 this.cacheLimit, 
                                 this.cache.length - this.cacheLimit );         //throws IOException

            if( 0 < read ) this.cacheLimit += read;
        }
    }


}
//end of CachedInputStream.java
