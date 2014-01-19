//start of PreLh2Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh2Decoder.java
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
import java.io.InputStream;
import java.lang.Math;




//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;

import com.orangesignal.jlha.DynamicHuffman;
import com.orangesignal.jlha.PreLzssDecoder;


/**
 * -lh2- 解凍用 PreLzssDecoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLh2Decoder.java,v $
 * Revision 1.1  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * [bug fix]
 *     available() の計算が甘かったのを修正。
 * [maintenance]
 *     ソース整備
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh2Decoder implements PreLzssDecoder{


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
    //  source
    //------------------------------------------------------------------
    //  private BitInputStream
    //------------------------------------------------------------------
    /**
     * -lh2- の圧縮データを供給する BitInputStream
     */
     private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman tree
    //------------------------------------------------------------------
    //  private DynamicHuffman codeHuffman
    //  private DynamicHuffman offHiHuffman
    //------------------------------------------------------------------
    /**
     * Lzss非圧縮データ 1byte か Lzss圧縮コードのうち一致長を
     * 得るための 動的ハフマン木
     */
    private DynamicHuffman codeHuffman;

    /**
     * Lzss圧縮コードの上位7bitの値を得るための動的ハフマン木
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
     * (解凍後のデータの)現在処理位置
     */
    private int position;

    /**
     * 次に addLeaf() すべき position
     */
    private int nextPosition;

    /**
     * 一致長
     */
    private int matchLength;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark method
    //------------------------------------------------------------------
    //  private DynamicHuffman markCodeHuffman
    //  private DynamicHuffman markOffHiHuffman
    //  private int markPosition
    //  private int markNextPosition
    //  private int markMatchLength
    //------------------------------------------------------------------
    /** codeHuffman のバックアップ用 */
    private DynamicHuffman markCodeHuffman;

    /** offHiHuffman のバックアップ用 */
    private DynamicHuffman markOffHiHuffman;

    /** position のバックアップ用 */
    private int markPosition;

    /** nextPosition のバックアップ用 */
    private int markNextPosition;

    /** matchLength のバックアップ用 */
    private int markMatchLength;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLh2Decoder()
    //  public PreLh2Decoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PreLh2Decoder(){    }


    /**
     * -lh2- 解凍用 PreLzssDecoder を構築する。<br>
     * 
     * @param in 圧縮データを供給する入力ストリーム
     */
    public PreLh2Decoder( InputStream in ){
        if( in != null ){
            if( in instanceof BitInputStream ){
                this.in       = (BitInputStream)in;
            }else{
                this.in       = new BitInputStream( in );
            }
            this.codeHuffman  = new DynamicHuffman( PreLh2Decoder.CodeSize );
            this.offHiHuffman = new DynamicHuffman( 
                                       PreLh2Decoder.DictionarySize >> 6, 1 );

            this.position     = 0;
            this.nextPosition = 1 << 6;
            this.matchLength  = 0;
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
     * -lh2- で圧縮された 
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
        final int CodeMax = PreLh2Decoder.CodeSize - 1;

        int node = this.codeHuffman.childNode( DynamicHuffman.ROOT );
        while( 0 <= node ){
            node = this.codeHuffman.childNode( node - ( in.readBoolean() ? 1 : 0 ) );//throws EOFException,IOException
        }
        int code = ~node;
        this.codeHuffman.update( code );

        if( code < 0x100 ){
            this.position++;
        }else{
            if( code == CodeMax ){
                try{
                    code += this.in.readBits( 8 );
                }catch( BitDataBrokenException exception ){
                    if( exception.getCause() instanceof EOFException )
                        throw (EOFException)exception.getCause();
                }
            }
            this.matchLength = code - 0x100 + PreLh2Decoder.Threshold;
        }
        return code;
    }

    /**
     * -lh2- で圧縮された
     * LZSS圧縮コードのうち一致位置を読み込む。<br>
     * 
     * @return -lh2- で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     */
    public int readOffset() throws IOException {
        if( this.nextPosition < PreLh2Decoder.DictionarySize ){
            while( this.nextPosition < this.position ){
                this.offHiHuffman.addLeaf( this.nextPosition >> 6 );
                this.nextPosition += 64;

                if( PreLh2Decoder.DictionarySize <= this.nextPosition )
                    break;
            }
        }
        this.position += this.matchLength;

        int node = this.offHiHuffman.childNode( DynamicHuffman.ROOT );
        while( 0 <= node ){
            node = this.offHiHuffman.childNode( node - ( in.readBoolean() ? 1 : 0 ) );//throws EOFException,IOException
        }
        int offHi = ~node;
        this.offHiHuffman.update( offHi );

        return ( offHi << 6 ) | this.in.readBits( 6 );
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
     * @see PreLzssDecoder#available()
     */
    public void mark( int readLimit ){
        this.in.mark( readLimit * 18 / 8 + 4 );
        this.markCodeHuffman  = (DynamicHuffman)this.codeHuffman.clone();
        this.markOffHiHuffman = (DynamicHuffman)this.offHiHuffman.clone();
        this.markPosition     = this.position;
        this.markNextPosition = this.nextPosition;
        this.markMatchLength  = this.matchLength;
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

        this.codeHuffman  = (DynamicHuffman)this.markCodeHuffman.clone();
        this.offHiHuffman = (DynamicHuffman)this.markOffHiHuffman.clone();
        this.position     = this.markPosition;
        this.nextPosition = this.markNextPosition;
        this.matchLength  = this.markMatchLength;
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

        this.in               = null;
        this.codeHuffman      = null;
        this.offHiHuffman     = null;
        this.markCodeHuffman  = null;
        this.markOffHiHuffman = null;
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
        return PreLh2Decoder.DictionarySize;
    }

    /**
     * -lh2-形式の LZSSの最大一致長を得る。
     * 
     * @return -lh2-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PreLh2Decoder.MaxMatch;
    }

    /**
     * -lh2-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh2-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PreLh2Decoder.Threshold;
    }

}
//end of PreLh2Decoder.java
