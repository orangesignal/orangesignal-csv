//start of LimitedInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF


/**
 * LimitedInputStream.java
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
import java.lang.Math;

//import exceptions
import java.io.IOException;
import java.lang.IllegalArgumentException;

/**
 * 読み込み可能なデータ量が制限された入力ストリーム。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LimitedInputStream.java,v $
 * Revision 1.1.2.1  2003/07/20 17:03:37  dangan
 * [maintenance]
 *     最新の LimitedInputStream からソースを取り込む。
 *
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     EndOfStream に達した後の read( new byte[0] ) や 
 *     read( byte[] buf, int off, 0 ) の戻り値を
 *     InputStream と同じく 0 になるようにした
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1.2.1 $
 */
public class LimitedInputStream extends InputStream{


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
    //  current position
    //------------------------------------------------------------------
    //  private long position
    //  private final long limit
    //  private long markPosition
    //------------------------------------------------------------------
    /**
     * 現在読み込み位置
     */
    private long position;

    /**
     * 読み込み限界
     */
    private final long limit;

    /**
     * マーク位置
     */
    private long markPosition;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public LimitedInputStream( InputStream in, long limit )
    //------------------------------------------------------------------
    /**
     * in からの読み込み可能なデータ量を制限した
     * 入力ストリームを構築する。<br>
     * 
     * @param in    入力ストリーム
     * @param limit 読み込み可能バイト数
     *
     * @exception IllegalArgumentException
     *              limit が負数である場合
     */
    public LimitedInputStream( InputStream in, long limit ){
        if( in != null && 0 <= limit ){
            this.in           = in;
            this.position     = 0;
            this.limit        = limit;
            this.markPosition = -1;
        }else if( in == null ){
            throw new NullPointerException( "in" );
        }else{
            throw new IllegalArgumentException( "limit must be 0 or more." );
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
     * 接続された入力ストリームから 1バイトのデータを読み込む。
     * 
     * @return 読み込まれた 1バイトのデータ<br>
     *         既にEndOfStream に達していたか、
     *         制限に達した場合は -1 を返す。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read() throws IOException {
        if( this.position < this.limit ){
            int ret = this.in.read();                                           //throws IOException
            if( 0 <= ret ){
                this.position++;
            }
            return ret;
        }else{
            return -1;
        }
    }

    /**
     * 接続された入力ストリームから buffer を満たすように
     * データを読み込む。<br>
     * データは必ずしも buffer を満たすとは限らないことに注意。<br>
     * 
     * @param buffer 読み込んだデータを格納するためのバイト配列<br>
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既にEndOfStream に達していたか、
     *         制限に達した場合は -1 を返す。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer ) throws IOException {
        if( 0 < buffer.length ){
            int ret;
            if( buffer.length < this.limit - this.position ){
                ret = this.in.read( buffer );                                   //throws IOException
            }else if( this.position < this.limit ){
                ret = this.in.read( buffer, 0, (int)( this.limit - this.position ) );//throws IOException
            }else{
                return -1;
            }
            if( 0 < ret ){
                this.position += ret;
            }
            return ret;
        }else{
            return 0;
        }
    }

    /**
     * 接続された入力ストリームから バイト配列 buffer の
     * index で指定された位置から length バイトのデータを
     * 読み込む。<br>
     * データは必ずしも length バイト読み込まれるとは限ら
     * ないことに注意。<br>
     * 
     * @param buffer 読み込まれたデータを格納するためのバイト配列
     * @param index  buffer内のデータ読み込み開始位置
     * @param length bufferに読み込むデータ量
     * 
     * @return buffer に読み込んだデータ量をバイト数で返す。<br>
     *         既にEndOfStream に達していたか、
     *         制限に達した場合は -1 を返す。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer, int index, int length )
                                                           throws IOException {

        if( 0 < length ){
            if( this.limit <= this.position ){
                return -1;
            }else if( this.limit - this.position < length ){
                length = (int)( this.limit - this.position );
            }
            int ret = this.in.read( buffer, index, length );                    //throws IOException
            if( 0 < ret ){
                this.position += ret;
            }
            return ret;
        }else{
            return 0;
        }
    }

    /**
     * 接続された入力ストリームのデータを length バイト読み飛ばす。<br>
     * 
     * @param length 読み飛ばすバイト数。<br>
     * 
     * @return 実際に読み飛ばされたバイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {

        if( 0 < length ){
            if( this.limit <= this.position ){
                return 0;
            }else if( this.limit - this.position < length ){
                length = this.limit - this.position;
            } 
            length = this.in.skip( length );                                    //throws IOException
            if( 0 < length ){
                this.position += length;
            }
            return length;
        }else{
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupprted()
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
        this.in.mark( readLimit );
        this.markPosition = this.position;
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException <br>
     *              <ol>
     *                <li> LimitedInputStream に mark がなされていない場合。<br>
     *                <li> 接続された入力ストリームが markSupported()で
     *                     false を返す場合。<br>
     *                <li> 接続された入力ストリームで
     *                     入出力エラーが発生した場合。<br>
     *              </ol>
     *              の何れか。
     */
    public void reset() throws IOException {
        if( !this.in.markSupported() ){
            throw new IOException( "not support mark()/reset()." );
        }else if( this.markPosition < 0 ){ //コンストラクタで MarkPosition が -1 に設定されるのを利用する。
            throw new IOException( "not marked." );
        }else{
            this.in.reset();                                                    //throws IOException
            this.position = this.markPosition;
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
     * @exception IOException 入出力エラーが発生した場合
     */
    public int available() throws IOException {
        return (int)Math.min( (long)this.in.available(),                        //throws IOException
                               this.limit - this.position );
    }

    /**
     * この入力ストリームを閉じ、使用していた
     * 全てのリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException
        this.in = null;
    }

}
//end of LimitedInputStream.java
