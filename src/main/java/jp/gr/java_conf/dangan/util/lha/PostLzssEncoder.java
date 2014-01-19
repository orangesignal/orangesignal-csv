//start of PostLzssEncoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLzssEncoder.java
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

//import exceptions
import java.io.IOException;

/**
 * LZSS圧縮コードを処理する インターフェイス。
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLzssEncoder.java,v $
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface PostLzssEncoder{


    //------------------------------------------------------------------
    //  original method ( on the model of java.io.OutputStream )
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public abstract void flush()
    //  public abstract void close()
    //------------------------------------------------------------------
    /**
     * この PostLzssEncoder にバッファリングされている
     * 出力可能なデータを出力先の OutputStream に出力し、
     * 出力先の OutputStream を flush() する。<br>
     * java.io.OutputStream の メソッド flush() と似ているが、
     * flush() しなかった場合と flush() した場合の出力については
     * 同じであることを保証しなくて良い。<br>
     * つまりOutputStream の flush() では同じデータを出力する事を
     * 期待されるような以下の二つのコードは、
     * PostLzssEncoder においては 別のデータを出力をしても良い。
     * <pre>
     * (1)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.writeCode( 0 );
     *   out.close();
     * 
     * (2)
     *   PostLzssEncoder out = new ImplementedPostLzssEncoder();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.flush();
     *   out.writeCode( 0 );
     *   out.close();
     * </pre>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public abstract void flush() throws IOException;

    /**
     * この出力ストリームと、接続された出力ストリームを閉じ、
     * 使用していたリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public abstract void close() throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public abstract void writeCode( int code )
    //  public abstract void writeOffset( int offset )
    //------------------------------------------------------------------
    /**
     * 1byte の LZSS未圧縮のデータもしくは、
     * LZSS で圧縮された圧縮コードのうち一致長を書きこむ。<br>
     * 未圧縮データは 0～255、
     * LZSS圧縮コード(一致長)は 256～510 を使用すること。
     * 
     * @param code 1byte の LZSS未圧縮のデータもしくは、
     *             LZSS で圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public abstract void writeCode( int code ) throws IOException;

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public abstract void writeOffset( int offset ) throws IOException;


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public abstract int getDictionarySize()
    //  public abstract int getMaxMatch()
    //  public abstract int getThreshold()
    //------------------------------------------------------------------
    /**
     * このPostLzssEncoderが処理するLZSS辞書のサイズを得る。
     * 
     * @param LZSS辞書のサイズ
     */
    public abstract int getDictionarySize();

    /**
     * このPostLzssEncoderが処理する最大一致長を得る。
     * 
     * @param 最長一致長
     */
    public abstract int getMaxMatch();

    /**
     * このPostLzssEncoderが処理する圧縮、非圧縮の閾値を得る。
     * 
     * @param 圧縮、非圧縮の閾値
     */
    public abstract int getThreshold();

}
//end of PostLzssEncoder.java
