//start of PostLzsEncoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLzsEncoder.java
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

package com.orangesignal.jlha;

//import classes and interfaces
import java.io.OutputStream;




//import exceptions
import java.io.IOException;
import java.lang.NullPointerException;

import com.orangesignal.jlha.PostLzssEncoder;


/**
 * -lzs- 圧縮用 PostLzssEncoder。
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLzsEncoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lzs- の MaxMatch は 16 でなく 17 だったのを修正。
 * [maintenance]
 *     ソース整備
 *     タブの廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLzsEncoder implements PostLzssEncoder {


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
    private static final int DictionarySize = 2048;

    /** 最大一致長 */
    private static final int MaxMatch       = 17;

    /** 最小一致長 */
    private static final int Threshold      = 2;


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  length of LZSS code
    //------------------------------------------------------------------
    //  private static final int PositionBits
    //  private static final int LengthBits
    //------------------------------------------------------------------
    /** 一致位置のビット数 */
    private static final int PositionBits = Bits.len( PostLzsEncoder.DictionarySize - 1 );

    /** 一致長のビット数 */
    private static final int LengthBits = Bits.len( PostLzsEncoder.MaxMatch - PostLzsEncoder.Threshold );


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //  private int position
    //  private int matchLength
    //------------------------------------------------------------------
    /**
     * -lzs- 形式のデータを出力するビット出力ストリーム
     */
    private BitOutputStream out;

    /**
     * ストリーム内現在処理位置
     */
    private int position;

    /**
     * 現在処理中のLZSS圧縮コード
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PostLzsEncoder()
    //  public PostLzsEncoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PostLzsEncoder(){   }

    /**
     * -lzs- 圧縮用 PostLzssEncoder を構築する。
     * 
     * @param out -lzs- 形式の圧縮データを出力するストリーム
     */
    public PostLzsEncoder( OutputStream out ){
        if( out != null ){
            if( out instanceof BitOutputStream ){
                this.out = (BitOutputStream)out;
            }else{
                this.out = new BitOutputStream( out );
            }
            this.position    = 0;
            this.matchLength = 0;
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
        if( code < 0x100 ){
            this.out.writeBit( 1 );                                             //throws IOException
            this.out.writeBits( 8, code );                                      //throws IOException
            this.position++;
        }else{
            // close() 後の writeCode() で
            // NullPointerException を投げることを期待している。
            this.out.writeBit( 0 );                                             //throws IOException
            this.matchLength = code - 0x100;
        }
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ) throws IOException {
        int pos = ( this.position - offset - 1
                  - PostLzsEncoder.MaxMatch )
                & ( PostLzsEncoder.DictionarySize - 1 );

        this.position += this.matchLength + PostLzsEncoder.Threshold;

        this.out.writeBits( this.PositionBits, pos );                           //throws IOException
        this.out.writeBits( this.LengthBits,   this.matchLength );              //throws IOException
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * この PostLzssEncoder にバッファリングされている
     * 全ての 8ビット単位のデータを出力先の OutputStream に出力し、 
     * 出力先の OutputStream を flush() する。<br>
     * このメソッドは圧縮率を変化させない。 
     * 
     * @exception IOException 入出力エラーが発生した場合
     *
     * @see PostLzssEncoder#flush()
     * @see BitOutputStream#flush()
     */
    public void flush() throws IOException {
        this.out.flush();                                                       //throws IOException
    }

    /**
     * この出力ストリームと、接続された出力ストリームを閉じ、
     * 使用していたリソースを解放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.out.close();                                                       //throws IOException

        this.out = null;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  get LZSS patameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lzs-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lzs-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PostLzsEncoder.DictionarySize;
    }

    /**
     * -lzs-形式の LZSSの最大一致長を得る。
     * 
     * @return -lzs-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PostLzsEncoder.MaxMatch;
    }

    /**
     * -lzs-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lzs-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PostLzsEncoder.Threshold;
    }

}
//end of PostLzsEncoder.java
