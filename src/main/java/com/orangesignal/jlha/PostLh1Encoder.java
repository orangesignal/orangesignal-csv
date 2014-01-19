//start of PostLh1Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh1Encoder.java
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

import com.orangesignal.jlha.BadHuffmanTableException;
import com.orangesignal.jlha.DynamicHuffman;
import com.orangesignal.jlha.PostLzssEncoder;
import com.orangesignal.jlha.StaticHuffman;

/**
 * -lh1- 圧縮用の PostLzssEncoder。 <br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLh1Encoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
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
public class PostLh1Encoder implements PostLzssEncoder{


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
    private static final int MaxMatch       = 60;

    /** 最小一致長 */
    private static final int Threshold      = 3;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh1- 形式の圧縮データの出力先の ビット出力ストリーム
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  dynamic huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman huffman
    //------------------------------------------------------------------
    /**
     * Code部圧縮用適応的ハフマン木
     */
    private DynamicHuffman huffman;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  static huffman list
    //------------------------------------------------------------------
    //  private int[] offHiCode
    //  private int[] offHiLen
    //------------------------------------------------------------------
    /**
     * offset部の上位6bit圧縮用ハフマン符号の表
     */
    private int[] offHiCode;

    /**
     * offset部の上位6bit圧縮用ハフマン符号長の表
     */
    private int[] offHiLen;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLh1Encoder()
    //  public PostLh1Encoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PostLh1Encoder(){   }

    /**
     * -lh1- 圧縮用 PostLzssEncoder を構築する。
     * 
     * @param out 圧縮データを受け取る出力ストリーム
     */
    public PostLh1Encoder( OutputStream out ){
        if( out != null ){
            if( out instanceof BitOutputStream ){
                this.out   = (BitOutputStream)out;
            }else{
                this.out   = new BitOutputStream( out );
            }
            this.huffman   = new DynamicHuffman( 314 );
            this.offHiLen  = PostLh1Encoder.createLenList();
            try{
                this.offHiCode = StaticHuffman.LenListToCodeList( this.offHiLen );
            }catch( BadHuffmanTableException exception ){
            }
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
        int node  = this.huffman.codeToNode( code );
        int hcode = 0;
        int hlen  = 0;
        do{
            hcode >>>= 1;
            hlen++;
            if( ( node & 1 ) != 0 ) hcode |= 0x80000000;

            node = this.huffman.parentNode( node );
        }while( node != DynamicHuffman.ROOT );

        this.out.writeBits( hlen, hcode >> ( 32 - hlen ) );                     //throws IOException
        this.huffman.update( code );
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ) throws IOException {
        int offHi = ( offset >> 6 );
        this.out.writeBits( this.offHiLen[offHi], this.offHiCode[offHi] );      //throws IOException
        this.out.writeBits( 6, offset );                                        //throws IOException
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

        this.out       = null;
        this.huffman   = null;
        this.offHiLen  = null;
        this.offHiCode = null;
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
     * -lh1-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lh1-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PostLh1Encoder.DictionarySize;
    }

    /**
     * -lh1-形式の LZSSの最大一致長を得る。
     * 
     * @return -lz5-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PostLh1Encoder.MaxMatch;
    }

    /**
     * -lh1-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh1-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PostLh1Encoder.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private static int[] createLenList()
    //------------------------------------------------------------------
    /**
     * -lh1- の offsetデコード用StaticHuffmanの
     * ハフマン符号長リストを生成する。
     * 
     * @return -lh1- の offsetデコード用StaticHuffmanの
     *         ハフマン符号長リスト
     */
    private static int[] createLenList(){
        final int length = 64;
        final int[] list = { 3, 0x01, 0x04, 0x0C, 0x18, 0x30, 0 };

        int[] LenList = new int[ length ];
        int index = 0;
        int len = list[ index++ ];

        for( int i = 0 ; i < length ; i++ ){
            if( list[index] == i ){
                len++;
                index++;
            }
            LenList[i] = len;
        }
        return LenList;
    }

}
//end of PostLh1Encoder.java
