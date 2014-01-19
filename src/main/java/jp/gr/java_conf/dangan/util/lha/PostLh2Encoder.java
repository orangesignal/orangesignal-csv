//start of PostLh2Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh2Encoder.java
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

package jp.gr.java_conf.dangan.util.lha;

//import classes and interfaces
import java.io.OutputStream;
import java.lang.Math;
import jp.gr.java_conf.dangan.io.BitOutputStream;
import jp.gr.java_conf.dangan.util.lha.DynamicHuffman;
import jp.gr.java_conf.dangan.util.lha.PostLzssEncoder;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;

/**
 * -lh2- 圧縮用 PostLzssEncoder。 <br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLh2Encoder.java,v $
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
public class PostLh2Encoder implements PostLzssEncoder {

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
    private static final int DictionarySize = 8192;

    /** 最大一致長 */
    private static final int MaxMatch       = 256;

    /** 最小一致長 */
    private static final int Threshold      = 3;


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int CodeSize
    //------------------------------------------------------------------
    /**
     * code部のハフマン木のサイズ 
     * code部がこれ以上の値を扱う場合は余計なビットを出力して補う。
     */
    private static final int CodeSize = 286;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh2- 形式の圧縮データの出力先の ビット出力ストリーム
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  dynamic huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman codeHuffman
    //  private DynamicHuffman offHiHuffman
    //------------------------------------------------------------------
    /**
     * code部圧縮用適応的ハフマン木
     */
    private DynamicHuffman codeHuffman;

    /**
     * offHi部圧縮用適応的ハフマン木
     */
    private DynamicHuffman offHiHuffman;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int nextPosition
    //  private int matchLength
    //------------------------------------------------------------------
    /**
     * 出力したオリジナルのサイズをカウントする
     */
    private int position;

    /**
     * 次に葉を加える動作をする位置
     */
    private int nextPosition;

    /**
     * 現在処理中の一致長
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PostLh2Encoder()
    //  public PostLh2Encoder( OutputStream out )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PostLh2Encoder(){   }

    /**
     * -lh2- 圧縮用 PostLzssEncoder を構築する。
     * 
     * @param out 圧縮データを受け取る出力ストリーム
     */
    public PostLh2Encoder( OutputStream out ){
        if( out != null ){
            if( out instanceof BitOutputStream ){
                this.out = (BitOutputStream)out;
            }else{
                this.out = new BitOutputStream( out );
            }
            this.codeHuffman  = new DynamicHuffman( PostLh2Encoder.CodeSize );
            this.offHiHuffman = new DynamicHuffman(
                                      PostLh2Encoder.DictionarySize >> 6, 1 );
            this.position     = 0;
            this.nextPosition = 1 << 6;
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
        final int CodeMax = PostLh2Encoder.CodeSize - 1;

        int node  = this.codeHuffman.codeToNode( Math.min( code, CodeMax ) );
        int hcode = 0;
        int hlen  = 0;
        do{
            hcode >>>= 1;
            hlen++;
            if( ( node & 1 ) != 0 ) hcode |= 0x80000000;

            node = this.codeHuffman.parentNode( node );
        }while( node != DynamicHuffman.ROOT );

        this.out.writeBits( hlen, hcode >>> ( 32 - hlen ) );                    //throws IOException


        if( code < 0x100 ){
            this.position++;
        }else{
            this.matchLength = ( code & 0xFF ) + PostLh2Encoder.Threshold;

            if( CodeMax <= code ){
                this.out.writeBits( 8, code - CodeMax );                        //throws IOException
                code = CodeMax;   //updateするコードをCodeMaxにする。
            }
        }
        this.codeHuffman.update( code );
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ) throws IOException {
        if( this.nextPosition < PostLh2Encoder.DictionarySize ){
            while( this.nextPosition < this.position ){
                this.offHiHuffman.addLeaf( this.nextPosition >> 6 );
                this.nextPosition += 64;

                if( PostLh2Encoder.DictionarySize <= this.nextPosition )
                    break;
            }
        }

        int node  = this.offHiHuffman.codeToNode( offset >> 6 );
        int hcode = 0;
        int hlen  = 0;
        while( node != DynamicHuffman.ROOT ){
            hcode >>>= 1;
            hlen++;
            if( ( node & 1 ) != 0 ) hcode |= 0x80000000;

            node = this.offHiHuffman.parentNode( node );
        }

        this.out.writeBits( hlen, hcode >> ( 32 - hlen ) );                     //throws IOException
        this.out.writeBits( 6, offset );                                        //throws IOException
        this.offHiHuffman.update( offset >> 6 );

        this.position += this.matchLength;
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
     * 使用していたリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.out.close();                                                       //throws IOException

        this.out          = null;
        this.codeHuffman  = null;
        this.offHiHuffman = null;
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
     * -lh2-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lh2-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PostLh2Encoder.DictionarySize;
    }

    /**
     * -lh2-形式の LZSSの最大一致長を得る。
     * 
     * @return -lh2-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PostLh2Encoder.MaxMatch;
    }

    /**
     * -lh2-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh2-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PostLh2Encoder.Threshold;
    }

}
//end of PostLh2Encoder.java
