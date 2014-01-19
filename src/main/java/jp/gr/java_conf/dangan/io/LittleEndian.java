//start of LittleEndian.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LittleEndian.java
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
import java.io.OutputStream;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.ArrayIndexOutOfBoundsException;


/**
 * リトルエンディアンで
 * バイト配列や InputStream, OutputStream
 * にアクセスするメソッドを提供するユーティリティクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: LittleEndian.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     writeByte(), readByte() を撤去。
 * [maintenance]
 *     ソース整備
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class LittleEndian{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LittleEndian()
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。使用不可。
     */
    private LittleEndian(){ }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  read from byte array
    //------------------------------------------------------------------
    //  public static final int readShort( byte[] ByteArray, int index )
    //  public static final int readInt( byte[] ByteArray, int index )
    //  public static final long readLong( byte[] ByteArray, int index )
    //------------------------------------------------------------------
    /**
     * ByteArray の index の位置から リトルエンディアンで
     * 2バイト値を読み出す。読み出された 2バイト値は 
     * 0x0000～0xFFFFにマップされる。
     *
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータの開始位置
     * 
     * @return 読み出された2バイト値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final int readShort( byte[] ByteArray, int index ){
        return   ( ByteArray[index]       & 0xFF )
               | ( ( ByteArray[index + 1] & 0xFF ) << 8 );
    }

    /**
     * ByteArray の index の位置からリトルエンディアンで
     * 4バイト値を読み出す。
     *
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータの開始位置
     * 
     * @return 読み出された4バイト値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final int readInt( byte[] ByteArray, int index ){
        return   (   ByteArray[index]     & 0xFF )
               | ( ( ByteArray[index + 1] & 0xFF ) <<  8 )
               | ( ( ByteArray[index + 2] & 0xFF ) << 16 )
               | (   ByteArray[index + 3]          << 24 );
    }

    /**
     * ByteArray の index の位置からリトルエンディアンで
     * 8バイト値を読み出す。
     * 
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータの開始位置
     * 
     * @return 読み出された8バイト値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final long readLong( byte[] ByteArray, int index ){
        return   ( (long)LittleEndian.readInt( ByteArray, index ) & 0xFFFFFFFFL )
               | ( (long)LittleEndian.readInt( ByteArray, index + 4 ) << 32L );
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  read from InputStream
    //------------------------------------------------------------------
    //  public static final int readShort( InputStream in )
    //  public static final int readInt( InputStream in )
    //  public static final long readLong( InputStream in )
    //------------------------------------------------------------------
    /**
     * 入力ストリーム in から リトルエンディアンで
     * 2byte値を読み出す。
     * 
     * @param in 入力ストリーム
     * 
     * @return 読み出された2byte値
     * 
     * @exception EOFException
     *                  既に End Of Streamに達していたか、
     *                  読み込みの途中で End Of Streamに達した。
     *                  読み込み途中のデータは消失する。
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final int  readShort( InputStream in )
                                                throws IOException {
        int byte1 = in.read();
        int byte2 = in.read();

        if( 0 <= byte1 && 0 <= byte2 ){
            return     ( byte1 & 0xFF )
                   | ( ( byte2 & 0xFF ) << 8 );
        }else{
            throw new EOFException();
        }
    }

    /**
     * 入力ストリーム in から リトルエンディアンで
     * 4byte値を読み出す。
     * 
     * @param in 入力ストリーム
     * 
     * @return 読み出された4byte値
     * 
     * @exception EOFException
     *                  既に End Of Streamに達していたか、
     *                  読み込みの途中で End Of Streamに達した。
     *                  読み込み途中のデータは消失する。
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final int  readInt( InputStream in )
                                                throws IOException {
        int byte1 = in.read();
        int byte2 = in.read();
        int byte3 = in.read();
        int byte4 = in.read();

        if( 0 <= byte1 && 0 <= byte2 && 0 <= byte3 && 0 <= byte4 ){
            return     ( byte1 & 0xFF )
                   | ( ( byte2 & 0xFF ) <<  8 )
                   | ( ( byte3 & 0xFF ) << 16 )
                   | (   byte4          << 24 );
        }else{
            throw new EOFException();
        }
    }

    /**
     * 入力ストリーム in から リトルエンディアンで
     * 8byte値を読み出す。
     * 
     * @param in 入力ストリーム
     * 
     * @return 読み出された8byte値
     * 
     * @exception EOFException
     *                  既に End Of Streamに達していたか、
     *                  読み込みの途中で End Of Streamに達した。
     *                  読み込み途中のデータは消失する。
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final long readLong( InputStream in )
                                                throws IOException {

        return   ( (long)LittleEndian.readInt( in ) & 0xFFFFFFFFL )
               | ( (long)LittleEndian.readInt( in ) << 32 );

    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  write to byte array
    //------------------------------------------------------------------
    //  public static final void writeShort( byte[] ByteArray, int index, int value )
    //  public static final void writeInt( byte[] ByteArray, int index, int value )
    //  public static final void writeLong( byte[] ByteArray, int index, long value )
    //------------------------------------------------------------------
    /**
     * ByteArray の index の位置にリトルエンディアンで
     * 2byte値を書き出す。
     * 
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータを書きこむ位置
     * @param value     書きこむ 2byte値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final void writeShort( byte[] ByteArray, 
                                         int    index, 
                                         int    value ){

        if( 0 <= index && index + 1 < ByteArray.length ){
            ByteArray[index]     = (byte)value;
            ByteArray[index + 1] = (byte)( value >> 8 );
        }else{
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * ByteArray の index の位置にリトルエンディアンで
     * 4byte値を書き出す。
     * 
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータを書きこむ位置
     * @param value     書きこむ 4byte値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final void writeInt( byte[] ByteArray, 
                                       int    index, 
                                       int    value ){

        if( 0 <= index && index + 3 < ByteArray.length ){
            ByteArray[index]     = (byte)value;
            ByteArray[index + 1] = (byte)( value >>  8 );
            ByteArray[index + 2] = (byte)( value >> 16 );
            ByteArray[index + 3] = (byte)( value >> 24 );
        }else{
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * ByteArray の index の位置にリトルエンディアンで
     * 8byte値を書き出す。
     * 
     * @param ByteArray バイト配列
     * @param index     ByteArray内のデータを書きこむ位置
     * @param value     書きこむ 8byte値
     * 
     * @exception ArrayIndexOutOfBoundsException
     *                  indexから始まるデータが 
     *                  ByteArrayの範囲内に無い場合。
     */
    public static final void writeLong( byte[] ByteArray, 
                                        int    index, 
                                        long   value ){
        if( 0 <= index && index + 7 < ByteArray.length ){
            LittleEndian.writeInt( ByteArray, index, (int)value );
            LittleEndian.writeInt( ByteArray, index + 4, (int)(value >> 32) );
        }else{
            throw new ArrayIndexOutOfBoundsException();
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  write to OutputStream
    //------------------------------------------------------------------
    //  public static final void writeShort( OutputStream out, int value )
    //  public static final void writeInt( OutputStream out, int value )
    //  public static final void writeLong( OutputStream out, long value )
    //------------------------------------------------------------------
    /**
     * 出力ストリーム out に リトルエンディアンで
     * 2バイト書き出す。
     * 
     * @param out   出力ストリーム
     * @param value 書き出す2バイト値
     *
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final void writeShort( OutputStream out, int value )
                                                throws IOException {
        out.write( value & 0xFF );
        out.write( ( value >> 8 ) & 0xFF );
    }

    /**
     * 出力ストリーム out に リトルエンディアンで
     * 4バイト値を書き出す。
     * 
     * @param out   出力ストリーム
     * @param value 書き出す1バイト値
     *
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final void writeInt( OutputStream out, int value )
                                                throws IOException {
        out.write( value & 0xFF );
        out.write( ( value >>  8 ) & 0xFF );
        out.write( ( value >> 16 ) & 0xFF );
        out.write( value >>> 24 );
    }

    /**
     * 出力ストリーム out に リトルエンディアンで
     * 8バイト値を書き出す。
     * 
     * @param out   出力ストリーム
     * @param value 書き出す1バイト値
     *
     * @exception IOException
     *                  入出力エラーが発生した場合
     */
    public static final void writeLong( OutputStream out, long value )
                                                throws IOException {
        int low = (int)value;
        int hi  = (int)( value >> 32 );

        out.write( low & 0xFF );
        out.write( ( low >>  8 ) & 0xFF );
        out.write( ( low >> 16 ) & 0xFF );
        out.write( low >>> 24 );
        out.write( hi & 0xFF );
        out.write( ( hi >>  8 ) & 0xFF );
        out.write( ( hi >> 16 ) & 0xFF );
        out.write( hi >>> 24 );
    }

}
//end of LittleEndian.java
