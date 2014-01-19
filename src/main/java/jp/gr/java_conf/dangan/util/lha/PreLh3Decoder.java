//start of PreLh3Decoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PreLh3Decoder.java
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
import jp.gr.java_conf.dangan.io.BitInputStream;
import jp.gr.java_conf.dangan.util.lha.StaticHuffman;
import jp.gr.java_conf.dangan.util.lha.PreLzssDecoder;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import jp.gr.java_conf.dangan.io.BitDataBrokenException;
import jp.gr.java_conf.dangan.io.NotEnoughBitsException;
import jp.gr.java_conf.dangan.util.lha.BadHuffmanTableException;

/**
 * -lh3- 解凍用の PreLzssDecoder。
 * 
 * <pre>
 * -- revision history --
 * $Log: PreLh3Decoder.java,v $
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
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh3Decoder implements PreLzssDecoder {


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
    //  private BitInputStream in
    //------------------------------------------------------------------
    /**
     * -lh3- の圧縮データを供給する BitInputStream
     */
     private BitInputStream in;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman decoder
    //------------------------------------------------------------------
    //  private int blockSize
    //  private int[] codeLen
    //  private short[] codeTable
    //  private int codeTableBits
    //  private short[][] codeTree
    //  private int[] offHiLen
    //  private short[] offHiTable
    //  private int offHiTableBits
    //  private short[][] offHiTree
    //------------------------------------------------------------------
    /**
     * 現在処理中のブロックの残りサイズを示す。
     */
    private int blockSize;

    /**
     * code 部のハフマン符号長の表
     */
    private int[] codeLen;

    /**
     * code 部復号用のテーブル
     * 正の場合は codeTree のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[] codeTable;

    /**
     * codeTable を引くために必要なbit数。
     */
    private int codeTableBits;

    /**
     * codeTable に収まりきらないデータの復号用の木
     * 正の場合は codeTree のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[][] codeTree;

    /**
     * offHi 部のハフマン符号長の表
     */
    private int[] offHiLen;

    /**
     * offHi 部復号用のテーブル
     * 正の場合は offHi のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[] offHiTable;

    /**
     * offHiTable を引くために必要なbit数。
     */
    private int offHiTableBits;

    /**
     * offHiTable に収まりきらないデータの復号用の木
     * 正の場合は offHi のindexを示す。
     * 負の場合は code を全ビット反転したもの。 
     */
    private short[][] offHiTree;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private int markBlockSize
    //  private int[] markCodeLen
    //  private short[] markCodeTable
    //  private short[][] markCodeTree
    //  private int[] markOffHiLen
    //  private short[] markOffHiTable
    //  private short[][] markOffHiTree
    //------------------------------------------------------------------
    /** blockSizeのバックアップ用 */
    private int markBlockSize;
    /** codeLen のバックアップ用 */
    private int[] markCodeLen;
    /** codeTable のバックアップ用 */
    private short[] markCodeTable;
    /** codeTree のバックアップ用 */
    private short[][] markCodeTree;
    /** offHiLen のバックアップ用 */
    private int[] markOffHiLen;
    /** offHiTable のバックアップ用 */
    private short[] markOffHiTable;
    /** offHiTree のバックアップ用 */
    private short[][] markOffHiTree;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PreLh3Decoder()
    //  public PreLh3Decoder( InputStream in )
    //  public PreLh3Decoder( InputStream in, 
    //                        int CodeTableBits, int OffHiTableBits )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PreLh3Decoder(){    }

    /**
     * -lh3- 解凍用 PreLzssDecoder を構築する。<br>
     * テーブルサイズには デフォルト値を使用する。
     * 
     * @param in 圧縮データを供給する入力ストリーム
     */
    public PreLh3Decoder( InputStream in ){
        this( in, 12, 8 );
    }

    /**
     * -lh3- 解凍用 PreLzssDecoder を構築する。<br>
     * 
     * @param in             圧縮データを供給する入力ストリーム
     * @param CodeTableBits  code 部を復号するために使用する
     *                       テーブルのサイズをビット長で指定する。 
     *                       12 を指定すれば 4096 のルックアップテーブルを生成する。 
     * @param OffHiTableBits offHi 部を復号するために使用する
     *                       テーブルのサイズをビット長で指定する。
     *                       8 を指定すれば 256 のルックアップテーブルを生成する。 
     * 
     * @exception IllegalArgumentException
     *                       CodeTableBits, OffHiTableBits が 0以下の場合
     */
    public PreLh3Decoder( InputStream in, 
                          int         CodeTableBits,
                          int         OffHiTableBits ){
        if( in != null
         && 0 < CodeTableBits
         && 0 < OffHiTableBits ){
            if( in instanceof BitInputStream ){
                this.in = (BitInputStream)in;
            }else{
                this.in = new BitInputStream( in );
            }
            this.blockSize      = 0;
            this.codeTableBits  = CodeTableBits;
            this.offHiTableBits = OffHiTableBits;
        }else if( in == null ){
            throw new NullPointerException( "in" );
        }else if( CodeTableBits <= 0 ){
            throw new IllegalArgumentException( "CodeTableBits too small. CodeTableBits must be larger than 1." );
        }else{
            throw new IllegalArgumentException( "OffHiTableBits too small. OffHiTableBits must be larger than 1." );
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
     * -lh3- で圧縮された
     * 1byte のLZSS未圧縮のデータ、
     * もしくは圧縮コードのうち一致長を読み込む。<br>
     * 
     * @return 1byte の 未圧縮のデータもしくは、
     *         圧縮された圧縮コードのうち一致長
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BadHuffmanTableException
     *                         ハフマン木を構成するための
     *                         ハフマン符号長の表が不正なため、
     *                         ハフマン復号器が生成できない場合
     */
    public int readCode() throws IOException {
        if( this.blockSize <= 0 ){
            this.readBlockHead();
        }
        this.blockSize--;

        int code;
        try{
            int node = this.codeTable[ this.in.peekBits( this.codeTableBits ) ];
            if( node < 0 ){
                code = ~node;
                this.in.skipBits( this.codeLen[ code ] );
            }else{
                this.in.skipBits( this.codeTableBits );
                do{
                    node = this.codeTree[ this.in.readBit() ][ node ];
                }while( 0 <= node );
                code = ~node;
            }
        }catch( NotEnoughBitsException exception ){
            int avail = exception.getAvailableBits();
            int bits = this.in.peekBits( avail );
            bits = bits << ( this.codeTableBits - avail );
            int node = this.codeTable[ bits ];

            if( node < 0 ){
                code = ~node; 
                if( this.in.skipBits( this.codeLen[code] ) < this.codeLen[code] ){
                    throw new EOFException();
                }
            }else{
                this.in.skipBits( avail );
                throw new EOFException();
            }
        }catch( ArrayIndexOutOfBoundsException exception ){
            throw new EOFException();
        }

        final int CodeMax = PreLh3Decoder.CodeSize - 1;
        if( code == CodeMax ){
            code += this.in.readBits( 8 );
        }
        return code;
    }

    /**
     * -lh3- で圧縮された
     * LZSS圧縮コードのうち一致位置を読み込む。<br>
     * 
     * @return -lh3- で圧縮された圧縮コードのうち一致位置
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int readOffset() throws IOException {
        int offHi;
        try{
            int node = this.offHiTable[ this.in.peekBits( this.offHiTableBits ) ];
            if( node < 0 ){
                offHi = ~node;
                this.in.skipBits( this.offHiLen[ offHi ] );
            }else{
                this.in.skipBits( this.offHiTableBits );
                do{
                    node = this.offHiTree[ this.in.readBit() ][ node ];
                }while( 0 <= node );
                offHi = ~node;
            }
        }catch( NotEnoughBitsException exception ){
            int avail = exception.getAvailableBits();
            int bits = this.in.peekBits( avail );
            bits = bits << ( this.offHiTableBits - avail );
            int node = this.offHiTable[ bits ];

            if( node < 0 ){
                offHi = ~node; 
                if( this.offHiLen[offHi] <= avail ){
                    this.in.skipBits( this.offHiLen[offHi] );
                }else{
                    this.in.skipBits( avail );
                    throw new EOFException();
                }
            }else{
                this.in.skipBits( avail );
                throw new EOFException();
            }
        }catch( ArrayIndexOutOfBoundsException exception ){
            throw new EOFException();
        }
        
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
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        readLimit = readLimit * StaticHuffman.LimitLen / 8;
        if( this.blockSize < readLimit ){
            readLimit += 245;
        }
        this.in.mark( readLimit );

        this.markBlockSize  = this.blockSize;
        this.markCodeLen    = this.codeLen;
        this.markCodeTable  = this.codeTable;
        this.markCodeTree   = this.codeTree;
        this.markOffHiLen   = this.offHiLen;
        this.markOffHiTable = this.offHiTable;
        this.markOffHiTree  = this.offHiTree;
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

        this.blockSize  = this.markBlockSize;
        this.codeLen    = this.markCodeLen;
        this.codeTable  = this.markCodeTable;
        this.codeTree   = this.markCodeTree;
        this.offHiLen   = this.markOffHiLen;
        this.offHiTable = this.markOffHiTable;
        this.offHiTree  = this.markOffHiTree;
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
        int avail = this.in.available() * 8 / StaticHuffman.LimitLen;
        if( this.blockSize < avail ){
            avail -= 245;
        }
        return Math.max( avail, 0 );
    }

    /**
     * このストリームを閉じ、使用していた全ての資源を解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.in.close();
        this.in             = null;

        this.blockSize      = 0;
        this.codeLen        = null;
        this.codeTable      = null;
        this.codeTree       = null;
        this.offHiLen       = null;
        this.offHiTable     = null;
        this.offHiTree      = null;

        this.markBlockSize  = 0;
        this.markCodeLen    = null;
        this.markCodeTable  = null;
        this.markCodeTree   = null;
        this.markOffHiLen   = null;
        this.markOffHiTable = null;
        this.markOffHiTree  = null;
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
     * -lh3-形式の LZSS辞書のサイズを得る。
     * 
     * @return -lh3-形式の LZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return PreLh3Decoder.DictionarySize;
    }

    /**
     * -lh3-形式の LZSSの最大一致長を得る。
     * 
     * @return -lh3-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PreLh3Decoder.MaxMatch;
    }

    /**
     * -lh3-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh3-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PreLh3Decoder.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  read block head
    //------------------------------------------------------------------
    //  private void readBlockHead()
    //  private int[] readCodeLen()
    //  private int[] readOffHiLen()
    //------------------------------------------------------------------
    /**
     * ハフマンブロックの先頭にある
     * ブロックサイズやハフマン符号長のリストを読み込む。
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BadHuffmanTableException
     *                         ハフマン木を構成するための
     *                         ハフマン符号長の表が不正なため、
     *                         ハフマン復号器が生成できない場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private void readBlockHead() throws IOException {
        //ブロックサイズ読み込み
        //正常なデータの場合、この部分で EndOfStream に到達する。
        try{
            this.blockSize = this.in.readBits( 16 );                            //throws BitDataBrokenException, EOFException, IOException
        }catch( BitDataBrokenException exception ){
            if( exception.getCause() instanceof EOFException ){
                throw (EOFException)exception.getCause();
            }else{
                throw exception;
            }
        }

        //code 部の処理
        this.codeLen = this.readCodeLen();
        if( 1 < this.codeLen.length ){
            short[][] tableAndTree = 
                StaticHuffman.createTableAndTree( this.codeLen, this.codeTableBits );
            this.codeTable = tableAndTree[0];
            this.codeTree  = new short[][]{ tableAndTree[1], tableAndTree[2] };
        }else{
            int code = this.codeLen[0];
            this.codeLen   = new int[ PreLh3Decoder.CodeSize ];
            this.codeTable = new short[ 1 << this.codeTableBits ];
            for( int i = 0 ; i < this.codeTable.length ; i++ ){
                this.codeTable[i] = ((short)~code);
            }
            this.codeTree = new short[][]{ new short[0], new short[0] };
        }

        //offHi 部の処理
        this.offHiLen = this.readOffHiLen();
        if( 1 < this.offHiLen.length ){
            short[][] tableAndTree = 
                StaticHuffman.createTableAndTree( this.offHiLen, this.offHiTableBits );
            this.offHiTable = tableAndTree[0];
            this.offHiTree  = new short[][]{ tableAndTree[1], tableAndTree[2] };
        }else{
            int offHi = this.offHiLen[0];
            this.offHiLen   = new int[ PreLh3Decoder.DictionarySize >> 6 ];
            this.offHiTable = new short[ 1 << this.offHiTableBits ];
            for( int i = 0 ; i < this.offHiTable.length ; i++ ){
                this.offHiTable[i] = ((short)~offHi);
            }
            this.offHiTree = new short[][]{ new short[0], new short[0] };
        }
    }

    /**
     * code部 のハフマン符号長のリストを読みこむ。
     * 
     * @return ハフマン符号長のリスト。
     *         もしくは 長さ 1 の唯一のコード
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private int[] readCodeLen() throws IOException {
        int[] codeLen = new int[ PreLh3Decoder.CodeSize ];

        for( int i = 0 ; i < codeLen.length ; i++ ){
            if( this.in.readBoolean() )
                codeLen[i] = this.in.readBits( 4 ) + 1;

            if( i == 2 && codeLen[0] == 1 && codeLen[1] == 1 && codeLen[2] == 1 ){
                return new int[]{ this.in.readBits( 9 ) };
            }
        }
        return codeLen;
    }

    /**
     * offHi部のハフマン符号長のリストを読みこむ
     * 
     * @return ハフマン符号長のリスト。
     *         もしくは 長さ 1 の唯一のコード
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException EndOfStreamに達した場合
     * @exception BitDataBrokenException
     *                         予期せぬ原因でデータ読みこみが
     *                         中断されたため要求されたビット数
     *                         のデータが得られなかった場合
     */
    private int[] readOffHiLen() throws IOException {
        if( this.in.readBoolean() ){
            int[] offHiLen = new int[ PreLh3Decoder.DictionarySize >> 6 ];

            for( int i = 0 ; i < offHiLen.length ; i++ ){
                offHiLen[i] = this.in.readBits( 4 );

                if( i == 2 && offHiLen[0] == 1 && offHiLen[1] == 1 && offHiLen[2] == 1 ){
                    return new int[]{ this.in.readBits( 7 ) };
                }
            }
            return offHiLen;
        }else{
            return PreLh3Decoder.createConstOffHiLen();
        }
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  constant huffman tree
    //------------------------------------------------------------------
    //  private static int[] createConstOffHiLen()
    //------------------------------------------------------------------
    /**
     * -lh3- の offsetデコード用StaticHuffmanの
     * ハフマン符号長リストを生成する。
     * 
     * @return -lh3- の offsetデコード用StaticHuffmanの
     *         ハフマン符号長リスト
     */
    private static int[] createConstOffHiLen(){
        final int length = PreLh3Decoder.DictionarySize >> 6;
        final int[] list = { 2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0 };

        int[] LenList = new int[ length ];
        int index = 0;
        int len = list[ index++ ];

        for( int i = 0 ; i < length ; i++ ){
            while( list[index] == i ){
                len++;
                index++;
            }
            LenList[i] = len;
        }
        return LenList;
    }

}
//end of PreLh3Decoder.java
