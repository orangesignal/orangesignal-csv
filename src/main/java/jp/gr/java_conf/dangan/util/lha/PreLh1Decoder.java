//start of PreLh1Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh1Decoder.java
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
import java.io.InputStream;
import java.lang.Math;
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.io.BitInputStream;
import jp.gr.java_conf.dangan.util.lha.StaticHuffman;
import jp.gr.java_conf.dangan.util.lha.PreLzssDecoder;
import jp.gr.java_conf.dangan.util.lha.DynamicHuffman;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;
import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.util.lha.BadHuffmanTableException;

/**
 * -lh1- 解凍用の PreLzssDecoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLh1Decoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     available の計算が甘かったのを修正。
 * [maintenance]
 *     ソース整備
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh1Decoder implements PreLzssDecoder{


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
    //  source
    //------------------------------------------------------------------
    //  BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lh1- の圧縮データを供給する BitInputStream
     */
    BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  DynamicHuffman huffman
    //  int[] offHiLen
    //  short[] offHiTable
    //  int offHiTableBits
    //------------------------------------------------------------------
    /**
     * code部復号用の動的ハフマン木
     */
    DynamicHuffman huffman;

    /**
     * オフセット部の上位6bit復号用 
     * ハフマン符号長リスト。
     */
    int[] offHiLen;

    /**
     * オフセット部の上位6bit復号用テーブル。
     */
    short[] offHiTable;

    /**
     * オフセット部の上位6bit復号用テーブルを引くのに必要なbit数。
     */
    int offHiTableBits;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  DynamicHuffman markHuffman
    //------------------------------------------------------------------
    /** huffman のバックアップ用 */
    DynamicHuffman markHuffman;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  public PreLh1Decoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * -lh1- 解凍用 PreLzssDecoder を構築する。
     * 
     * @param in -lh1- で圧縮されたデータを供給する入力ストリーム
     */
    public PreLh1Decoder( InputStream in ){
        if( in != null ){
            if( in instanceof BitInputStream ){
                this.in         = (BitInputStream)in;
            }else{
                this.in         = new BitInputStream( in );
            }
            this.huffman        = new DynamicHuffman( 314 );
            this.markHuffman    = null;

            this.offHiLen       = PreLh1Decoder.createLenList();
            try{
                this.offHiTable = StaticHuffman.createTable( this.offHiLen );
            }catch( BadHuffmanTableException exception ){
            }
            this.offHiTableBits = Bits.len( this.offHiTable.length - 1 );
        }else{
            throw new NullPointerException( "in" );
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readCode()
    //  public int readOffset()
    //------------------------------------------------------------------
    /**
     * -lh1- で圧縮された 
     * 1byte のLZSS未圧縮のデータ、
     * もしくは圧縮コードのうち一致長を読み込む。<br>
     * 
     * @return 1byte の 未圧縮のデータもしくは、
     *         圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     */
    public int readCode() throws IOException {
        int node = this.huffman.childNode( DynamicHuffman.ROOT );
        while( 0 <= node ){
            node = this.huffman.childNode( node - ( in.readBoolean() ? 1 : 0 ) );//throws EOFException,IOException
        }
        int code = ~node;
        this.huffman.update( code );
        return code;
    }

    /**
     * -lh1- で圧縮された
     * LZSS圧縮コードのうち一致位置を読み込む。<br>
     * 
     * @return -lh1- で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException  入出力エラーが発生した場合。
     * @exception EOFException データが途中までしかないため
     *                         予期せぬ EndOfStream に到達した場合。
     * @exception BitDataBrokenException
     *                         データが途中までしかないため
     *                         予期せぬ EndOfStream に到達したか、
     *                         他の入出力エラーが発生した。  
     * @exception NotEnoughBitsException
     *                         データが途中までしかないため
     *                         予期せぬ EndOfStream に到達したか、
     *                         他の入出力エラーが発生した。  
     */
    public int readOffset() throws IOException {
        //offHiをあらわすのに最短の場合は 0 の 3bit で
        //offHiTableBits は 8bitで 両者の差は 5bit。 
        //そのため、下位6bitを読み込む事を加味すると
        //正常なデータでは peekBits が 
        //NotEnoughBitsException を投げることは無い。
        int offHi = this.offHiTable[ this.in.peekBits( this.offHiTableBits ) ]; //throws NotEnoughBitsException IOException
        this.in.skipBits( this.offHiLen[ offHi ] );                             //throws IOException

        return ( offHi << 6 ) | this.in.readBits( 6 );                          //throws BitDataBrokenException NotEnoughBitsException IOException
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  mark / reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームの現在位置にマークを設定し、
     * reset() メソッドでマークした時点の 読み込み位置に
     * 戻れるようにする。<br>
     * InputStream の mark() と違い、readLimit で設定した
     * 限界バイト数より前にマーク位置が無効になる可能性が
     * ある事に注意すること。<br>
     * 
     * @param readLimit マーク位置に戻れる限界のバイト数。
     *                  このバイト数を超えてデータを読み
     *                  込んだ場合 reset()できなくなる可
     *                  能性がある。<br>
     * 
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        this.in.mark( readLimit * 18 / 8 + 4 );
        this.markHuffman = (DynamicHuffman)this.huffman.clone();
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void reset() throws IOException {
        //mark()しないで reset() しようとした場合、
        //readLimit を超えて reset() しようとした場合、
        //接続された InputStream が markSupported() で false を返す場合は
        //BitInputStream が IOException を投げる。
        this.in.reset();                                                        //throws IOException

        this.huffman = (DynamicHuffman)this.markHuffman.clone();
    }

    /**
     * 接続された入力ストリームが mark() と reset() をサポートするかを得る。<br>
     * 
     * @return ストリームが mark() と reset() を
     *         サポートする場合は true。<br>
     *         サポートしない場合は false。<br>
     */
    public boolean markSupported(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
     * InputStream の available() と違い、
     * この最低バイト数は必ずしも保障されていない事に注意すること。<br>
     * 
     * @return ブロックしないで読み出せる最低バイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max( this.in.availableBits() / 18 - 4, 0 );                 //throws IOException
    }

    /**
     * このストリームを閉じ、使用していた全ての資源を解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException

        this.in             = null;
        this.huffman        = null;
        this.markHuffman    = null;

        this.offHiLen       = null;
        this.offHiTable     = null;
        this.offHiTableBits = 0;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  get LZSS parameter
    //------------------------------------------------------------------
    //  public int getDictionarySize()
    //  public int getMaxMatch()
    //  public int getThreshold()
    //------------------------------------------------------------------
    /**
     * -lh1-形式のLZSS辞書のサイズを得る。
     * 
     * @return -lh1-形式のLZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PreLh1Decoder.DictionarySize;
    }

    /**
     * -lh1-形式のLZSSの最大一致長を得る。
     * 
     * @return -lh1-形式のLZSSの最大一致長
     */
    public int getMaxMatch(){
        return PreLh1Decoder.MaxMatch;
    }

    /**
     * -lh1-形式のLZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh1-形式のLZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PreLh1Decoder.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  constant huffman tree
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
//end of PreLh1Decoder.java
