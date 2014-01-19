//start of DisconnectableInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * DisconnectableInputStream.java
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
import jp.gr.java_conf.dangan.io.Disconnectable;

//import exceptions
import java.io.IOException;
import java.lang.NullPointerException;

/**
 * データを供給する入力ストリームと データを処理する
 * 入力ストリームとの接続を解除するためのユーティリティクラス。<br>
 * java.io.BufferedInputStream 等のバッファリングするストリーム
 * との接続を解除する場合は
 * jp.gr.java_conf.dangan.io.LimitedInputStream 等を使用して
 * 接続解除位置を過ぎたバッファリングを抑止する必要がある。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: DisconnectableInputStream.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *     ソース整備
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class DisconnectableInputStream extends InputStream
                                       implements Disconnectable {

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
    //  constructer
    //------------------------------------------------------------------
    //  private DisconnectableInputStream()
    //  public DisconnectableInputStream( InputStream in )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private DisconnectableInputStream(){    }

    /**
     * in との接続を解除可能な入力ストリームを構築する。
     * 
     * @param in 入力ストリーム
     */
    public DisconnectableInputStream( InputStream in ){
        if( in != null ){
            this.in = in;
        }else{
            throw new NullPointerException( "in" );
        }
    }


    //------------------------------------------------------------------
    //  java.io.InputStream methods
    //------------------------------------------------------------------
    //  read method
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームから 次の1バイトのデータを得る。<br>
     * 
     * @return 読み込まれた1バイトのデータ。<br>
     *         EndOfStreamに達した場合は -1 を返す。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read() throws IOException {
        return this.in.read();                                                  //throws IOException
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
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer ) throws IOException {
        return this.in.read( buffer, 0, buffer.length );                        //throws IOException
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
     *         既に EndOfStream に達していた場合は -1 を返す。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer, int index, int length ) throws IOException {
        if( 0 < length ){
            return this.in.read( buffer, index, length );                       //throws IOException
        }else{
            return 0;
        }
    }

    /**
     * 接続された入力ストリームのデータを length バイト
     * 読み飛ばす。<br>
     * 
     * @param length 読み飛ばすバイト数。<br>
     * 
     * @return 実際に読み飛ばされたバイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {
        if( 0 < length ){
            return this.in.skip( length );                                      //throws IOException
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
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void reset() throws IOException {
        this.in.reset();                                                        //throws IOException
    }

    /**
     * 接続された入力ストリームが mark() と reset() を
     * サポートするかを得る。<br>
     * 
     * @return ストリームが mark() と reset() を
     *         サポートする場合は true。<br>
     *         サポートしない場合は false。<br>
     */
    public boolean  markSupprted(){
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
        return this.in.available();                                             //throws IOException
    }

    /**
     * 接続された入力ストリームとの接続を解除する。<br>
     * このメソッドは disconnect() を呼ぶだけである。<br>
     */
    public void close(){
        this.disconnect();
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.io.Disconnectable 
    //------------------------------------------------------------------
    //  public void disconnect
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームとの接続を解除する。<br>
     */
    public void disconnect(){
        this.in = null;
    }

}
//end of DisconnectableInputStream.java
