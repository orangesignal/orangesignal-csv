//start of PostLz5Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLz5Encoder.java
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces
import java.io.OutputStream;
import jp.gr.java_conf.dangan.util.lha.PostLzssEncoder;

//import exceptions
import java.io.IOException;
import java.lang.NullPointerException;

/**
 * -lz5- 圧縮用 PostLzssEncoder。
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLz5Encoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lz5- の MaxMatch は 16 でなく 18 だった。
 *     flush() で出力できるデータを出力していなかったのを修正。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLz5Encoder implements PostLzssEncoder{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private static final int DictionarySize
    //  private static final int MaxMatch
    //  private static final int Threshold
    //------------------------------------------------------------------
    /** 辞書サイズ */
    private static final int DictionarySize = 4096;

    /** 最大一致長 */
    private static final int MaxMatch       = 18;

    /** 最小一致長 */
    private static final int Threshold      = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * -lz5- 圧縮データを出力するストリーム
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  buffer
    //------------------------------------------------------------------
    //  private byte[] buf
    //  private int index
    //  private int flagIndex
    //  private int flagBit
    //------------------------------------------------------------------
    /** 圧縮データの一時格納用バッファ */
    private byte[] buf;

    /** buf内の現在処理位置 */
    private int index;

    /** buf内の Lzss圧縮、非圧縮を示すフラグの位置を示す */
    private int flagIndex;

    /** Lzss圧縮、非圧縮を示すフラグ */
    private int flagBit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //------------------------------------------------------------------
    /**
     * ストリーム内現在処理位置 
     * lha の offset から larc の offset への変換に必要
     */
    private int position;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLz5Encoder()
    //  public PostLz5Encoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可
     */
    private PostLz5Encoder(){ }

    /**
     * -lz5- 圧縮用 PostLzssEncoder を構築する。<br>
     * 
     * @param out 圧縮データを出力する出力ストリーム
     */
    public PostLz5Encoder( OutputStream out ){
        if( out != null ){
            this.out       = out;
            this.position  = 0;
            this.buf       = new byte[1024];
            this.index     = 0;
            this.flagIndex = 0;
            this.flagBit   = 0x100;
        }else{
            throw new NullPointerException( "out" );
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void writeCode( int code )
    //  public void writeOffset( int offset )
    //------------------------------------------------------------------
    /**
     * 1byte の LZSS未圧縮のデータもしくは、
     * LZSS で圧縮された圧縮コードのうち一致長を書きこむ。<br>
     * 
     * @param code 1byte の LZSS未圧縮のデータもしくは、
     *             LZSS で圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void writeCode( int code ) throws IOException {
        if( this.flagBit == 0x100 ){
            if( this.buf.length - ( 2 * 8 + 1 ) < this.index ){
                this.out.write( this.buf, 0, this.index );                      //throws IOException
                this.index = 0;
            }
            this.flagBit = 0x01;
            this.flagIndex = this.index++;
            this.buf[ this.flagIndex ] = 0;
        }

        if( code < 0x100 ){
            this.buf[ this.flagIndex ] |= this.flagBit;
            this.buf[ this.index++ ]    = (byte)code;
            this.position++;
        }else{
            this.buf[ this.index++ ]    = (byte)code;
        }
        this.flagBit <<= 1;
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ){
        int pos = ( this.position - offset - 1 
                  - PostLz5Encoder.MaxMatch )
                & ( PostLz5Encoder.DictionarySize - 1 );

        int matchlen  = this.buf[ --this.index ] & 0x0F;
        this.buf[ this.index++ ] = (byte)pos;
        this.buf[ this.index++ ] = (byte)( ( ( pos >> 4 ) & 0xF0 ) | matchlen );

        this.position += matchlen + this.Threshold;

    }


    //------------------------------------------------------------------
    //  method jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * この PostLzssEncoder にバッファリングされている
     * 出力可能なデータを出力先の OutputStream に出力し、
     * 出力先の OutputStream を flush() する。<br>
     * このメソッドは出力不可能な 最大15バイトのデータを
     * バッファリングしたまま 出力しない。<br>
     * このメソッドは圧縮率を変化させない。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PostLzssEncoder#flush()
     */
    public void flush() throws IOException {
        if( this.flagBit == 0x100 ){
            this.out.write( this.buf, 0, this.index );                          //throws IOException
            this.out.flush();                                                   //throws IOException

            this.index = 0;
            this.flagBit               = 0x01;
            this.flagIndex             = this.index++;
            this.buf[ this.flagIndex ] = 0;
        }else{
            this.out.write( this.buf, 0, this.flagIndex );                      //throws IOException
            this.out.flush();                                                   //throws IOException

            System.arraycopy( this.buf, this.flagIndex,
                              this.buf, 0,
                              this.index - this.flagIndex );
            this.index     -= this.flagIndex;
            this.flagIndex  = 0;
        }
    }

    /**
     * この出力ストリームと、接続された出力ストリームを閉じ、
     * 使用していたリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.out.write( this.buf, 0, this.index );                              //throws IOException
        this.out.close();                                                       //throws IOException

        this.out = null;
        this.buf = null;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lz5-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lz5-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PostLz5Encoder.DictionarySize;
    }

    /**
     * -lz5-形式の LZSSの最長一致長を得る。
     * 
     * @return -lz5-形式の LZSSの最長一致長
     */
    public int getMaxMatch(){
        return PostLz5Encoder.MaxMatch;
    }

    /**
     * -lz5-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lz5-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PostLz5Encoder.Threshold;
    }

}
//end of PostLz5Encoder.java
