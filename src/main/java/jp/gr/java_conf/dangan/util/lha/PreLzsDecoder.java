//start of PreLzsDecoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLzsDecoder.java
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
import java.io.InputStream;
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.io.BitInputStream;
import jp.gr.java_conf.dangan.util.lha.PreLzssDecoder;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import jp.gr.java_conf.dangan.io.BitDataBrokenException;

/**
 * -lzs- 解凍用 PreLzssDecoder。
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLzsDecoder.java,v $
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     -lzs- の MaxMatch が 17 であるべきが 16 となっていたのを修正。
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
public class PreLzsDecoder implements PreLzssDecoder{


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
    //  bit length
    //------------------------------------------------------------------
    //  private static final int OffsetBits
    //  private static final int LengthBits
    //------------------------------------------------------------------
    /** 一致位置のビット数 */
    private static final int OffsetBits = Bits.len( PreLzsDecoder.DictionarySize - 1 );

    /** 一致長のビット数 */
    private static final int LengthBits = Bits.len( PreLzsDecoder.MaxMatch - PreLzsDecoder.Threshold );


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lzs- 形式の圧縮データを供給する BitInputStream
     */
    private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int matchOffset
    //  private int matchLength
    //------------------------------------------------------------------
    /** 
     * 現在処理位置。
     * LzssInputStreamの内部状態を取得できないために存在する。
     * LzssInputStreamの内部クラスとして書けば、positionは必要無い。
     */
    private int position;

    /** 最も新しいLzssコードの一致位置 */
    private int matchOffset;

    /**
     * 最も新しいLzssコードの一致長
     * LzssInputStreamの内部状態を取得できないために存在する。
     * LzssInputStreamの内部クラスとして書けば、matchLengthは必要無い。
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  member values
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markPosition
    //  private int markMatchOffset
    //  private int markMatchLength
    //------------------------------------------------------------------
    /** matchPositionのバックアップ用 */
    private int markPosition;

    /** matchPositionのバックアップ用 */
    private int markMatchOffset;

    /** matchLengthのバックアップ用 */
    private int markMatchLength;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  public PreLzsDecoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * -lzs- 解凍用 PreLzssDecoder を構築する。
     * 
     * @param in -lzs- 形式の圧縮データを供給する入力ストリーム
     */
    public PreLzsDecoder( InputStream in ){
        if( in != null ){
            if( in instanceof BitInputStream ){
                this.in = (BitInputStream)in;
            }else{
                this.in = new BitInputStream( in );
            }
            this.position    = 0;
            this.matchOffset = 0;
            this.matchLength = 0;
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
     * -lzs- で圧縮された
     * 1byte の LZSS未圧縮のデータ、
     * もしくは圧縮コードのうち一致長を読み込む。<br>
     * 
     * @return 1byte の 未圧縮のデータもしくは、
     *         圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int readCode() throws IOException {
        try{
            if( this.in.readBoolean() ){
                this.position++;
                return this.in.readBits( 8 );
            }else{
                this.matchOffset = this.in.readBits( this.OffsetBits );
                this.matchLength = this.in.readBits( this.LengthBits );
                return this.matchLength | 0x100;
            }
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof EOFException )
                throw (EOFException)exception.getCause();
            else
                throw exception;
        }
    }

    /**
     * -lzs- で圧縮された圧縮コードのうち
     * 一致位置を読み込む。<br>
     * 
     * @return -lzs- で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int readOffset() throws IOException {
        int offset = ( this.position - this.matchOffset - 1
                     - PreLzsDecoder.MaxMatch )
                   & ( PreLzsDecoder.DictionarySize - 1 );

        this.position += this.matchLength + PreLzsDecoder.Threshold;
        return offset;
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
        this.in.mark( ( readLimit * 9 + 7 ) / 8 + 1 );
        this.markPosition    = this.position;
        this.markMatchOffset = this.matchOffset;
        this.markMatchLength = this.matchLength;
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException <br>
     * &emsp;&emsp; (1) mark() せずに reset() しようとした場合。<br>
     * &emsp;&emsp; (2) 接続された入力ストリームが markSupported()で
     *                  false を返す場合。<br>
     * &emsp;&emsp; (3) 接続された入力ストリームで
     *                  入出力エラーが発生した場合。<br>
     * &emsp;&emsp; の何れか。
     */
    public void reset() throws IOException {
        //mark()しないで reset()しようとした場合、
        //接続された InputStream が mark/resetをサポートしない場合は
        //BitInputStream の reset()によって IOExceptionが投げられる。
        this.in.reset();                                                        //throws IOException

        this.position    = this.markPosition;
        this.matchOffset = this.markMatchOffset;
        this.matchLength = this.markMatchLength;
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
     * この最低バイト数は保証される。<br>
     * 
     * @return ブロックしないで読み出せる最低バイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max( this.in.availableBits() / 9 - 2, 0 );
    }

    /**
     * この出力とストリームと
     * 接続されていたストリームを閉じ、
     * 使用していたリソースを解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();

        this.in = null;
    }

    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
    //------------------------------------------------------------------
    //  get LZSS parameter
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
        return PreLzsDecoder.DictionarySize;
    }

    /**
     * -lzs-形式の LZSSの最長一致長を得る。
     * 
     * @return -lzs-形式の LZSSの最長一致長
     */
    public int getMaxMatch(){
        return PreLzsDecoder.MaxMatch;
    }

    /**
     * -lzs-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lzs-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PreLzsDecoder.Threshold;
    }

}
//end of PreLzsDecoder.java
