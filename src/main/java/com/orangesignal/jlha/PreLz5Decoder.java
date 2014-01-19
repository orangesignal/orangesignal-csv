//start of PreLz5Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLz5Decoder.java
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

package com.orangesignal.jlha;

//import classes and interfaces
import java.io.InputStream;
import java.lang.Math;




//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;

import com.orangesignal.jlha.PreLzssDecoder;

/**
 * -lz5- 解凍用 PreLzssDecoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLz5Decoder.java,v $
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
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
public class PreLz5Decoder implements PreLzssDecoder{


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
    //  source
    //------------------------------------------------------------------
    //  private InputStream in
    //------------------------------------------------------------------
    /**
     * -lz5- 形式の圧縮データを供給するストリーム
     */
    private InputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int matchPos
    //  private int matchLen
    //------------------------------------------------------------------
    /** 
     * 現在処理位置。
     * larc の一致位置から lha の一致位置への変換に必要
     */
    private int position;

    /** Lzss圧縮情報のうち 一致位置(larcの一致位置) */
    private int matchPos;

    /** Lzss圧縮符号のうち 一致長 */
    private int matchLen;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  flag
    //------------------------------------------------------------------
    //  private int flagByte
    //  private int flagBit
    //------------------------------------------------------------------
    /** 8つのLzss圧縮、非圧縮を示すフラグをまとめたもの */
    private int flagByte;

    /** Lzss圧縮、非圧縮を示すフラグ */
    private int flagBit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markPosition
    //  private int markMatchPos
    //  private int markMatchLen
    //  private int markFlagByte
    //  private int markFlagBit
    //------------------------------------------------------------------
    /** positionのバックアップ用 */
    private int markPosition;

    /** matchOffsetのバックアップ用 */
    private int markMatchPos;

    /** matchLengthのバックアップ用 */
    private int markMatchLen;

    /** flagByteのバックアップ用。*/
    private int markFlagByte;

    /** flagCountのバックアップ用。 */
    private int markFlagBit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLz5Decoder()
    //  public PreLz5Decoder( InputStream in )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可
     */
    private PreLz5Decoder(){ }

    /**
     * -lz5- 解凍用 PreLzssDecoder を構築する。
     * 
     * @param in 圧縮データを供給する入力ストリーム
     */
    public PreLz5Decoder( InputStream in ){
        if( in != null ){
            if( in instanceof CachedInputStream ){
                this.in = (CachedInputStream)in;
            }else{
                this.in = new CachedInputStream( in );
            }

            this.position     = 0;
            this.matchPos     = 0;
            this.matchLen     = 0;
            this.flagByte     = 0;
            this.flagBit      = 0x100;

            this.markPosition = 0;
            this.markMatchPos = 0;
            this.markMatchLen = 0;
            this.markFlagByte = 0;
            this.markFlagBit  = 0;
        }else{
            throw new NullPointerException( "in" );
        }
    }

    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int readCode()
    //  public int readOffset()
    //------------------------------------------------------------------
    /**
     * -lz5- で圧縮された 
     * 1byte の LZSS未圧縮のデータ、
     * もしくは圧縮コードのうち一致長を読み込む。<br>
     * 
     * @return 1byte の 未圧縮のデータもしくは、
     *         圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     */
    public int readCode() throws IOException {
        if( this.flagBit == 0x100 ){
            this.flagByte  = this.in.read();                                    //throws IOException

            if( 0 <= this.flagByte ){
                this.flagBit = 0x01;
            }else{
                throw new EOFException();
            }
        }

        if( 0 != ( this.flagByte & this.flagBit ) ){
            this.flagBit <<= 1;
            this.position++;
            int ret = this.in.read();                                           //throws IOException
            if( 0 <= ret ) return ret;
            else           throw new EOFException();
        }else{
            this.flagBit <<= 1;
            int c1   = this.in.read();                                          //throws IOException
            int c2   = this.in.read();                                          //throws IOException

            if( 0 <= c1 ){
                this.matchPos = ( ( c2 & 0xF0 ) << 4 ) | c1;
                this.matchLen = c2 & 0x0F;
                return this.matchLen | 0x100;
            }else{
                throw new EOFException();
            }
        }
    }

    /**
     * -lz5- で圧縮された
     * 圧縮コードのうち一致位置を読み込む。<br>
     * 
     * @return -lz5- で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int readOffset() throws IOException {
        int offset  = ( this.position - this.matchPos - 1
                      - PreLz5Decoder.MaxMatch )
                    & ( PreLz5Decoder.DictionarySize - 1 );

        this.position += this.matchLen + PreLz5Decoder.Threshold;

        return offset;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark( int readLimit )
    //  public void reset()
    //  public boolean markSupported()
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
     *
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        this.in.mark( ( readLimit * 9 + 7 ) / 8 + 2 );
        this.markPosition = this.position;
        this.markMatchLen = this.matchLen;
        this.markMatchPos = this.matchPos;
        this.markFlagByte = this.flagByte;
        this.markFlagBit  = this.flagBit;
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
        //mark() していないのに reset() しようとした場合、
        //接続されたストリームがmark/resetをサポートしない場合は
        //CachedInputStream が IOException を投げる。
        this.in.reset();                                                        //throws IOException

        this.position = this.markPosition;
        this.matchLen = this.markMatchLen;
        this.matchPos = this.markMatchPos;
        this.flagByte = this.markFlagByte;
        this.flagBit  = this.markFlagBit;
    }

    /**
     * 接続されたストリームが mark() と reset()
     * をサポートするかを返す。
     * 
     * @return 接続されたストリームが mark,reset をサポートするならtrue,
     *         サポートしないなら false
     */
    public boolean markSupported(){
        return this.in.markSupported();
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.PreLzssDecoder
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
     * この値は保証される。
     * 
     * @return ブロックしないで読み出せる最低バイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PreLzssDecoder#available()
     */
    public int available() throws IOException {
        return Math.max( in.available() * 8 / 9 - 2, 0 );                       //throws IOException
    }

    /**
     * このストリームを閉じ、使用していた全ての資源を解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();                                                        //throws IOException

        this.in = null;
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
     * -lz5-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lz5-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PreLz5Decoder.DictionarySize;
    }

    /**
     * -lz5-形式の LZSSの最大一致長を得る。
     * 
     * @return -lz5-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PreLz5Decoder.MaxMatch;
    }

    /**
     * -lz5-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lz5-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PreLz5Decoder.Threshold;
    }

}
//end of PreLz5Decoder.java
