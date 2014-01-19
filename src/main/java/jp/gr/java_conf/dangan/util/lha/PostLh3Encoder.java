//start of PostLh3Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh3Encoder.java
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
import jp.gr.java_conf.dangan.util.lha.StaticHuffman;
import jp.gr.java_conf.dangan.util.lha.PostLzssEncoder;

//import exceptions
import java.io.IOException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;


/**
 * -lh3- 圧縮用 PostLzssEncoder。<br>
 * 
 * <pre>
 * $Log: PostLh3Encoder.java,v $
 * Revision 1.2  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [change]
 *     flush() されないかぎり 
 *     接続された OutputStream をflush() しないように変更。
 * [maintenance]
 *     ソース整備。
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
 * @version $Revision: 1.2 $
 */
public class PostLh3Encoder implements PostLzssEncoder{

    //------------------------------------------------------------------
    //  class fields
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private static final int DictionarySize
    //  private static final int MaxMatch
    //  private static final int Threshold
    //------------------------------------------------------------------
    /** 辞書サイズ */
    private static final int DictionarySize  = 8192;

    /** 最大一致長 */
    private static final int MaxMatch        = 256;

    /** 最小一致長 */
    private static final int Threshold       = 3;


    //------------------------------------------------------------------
    //  class fields
    //------------------------------------------------------------------
    //  private static final int[] ConstOffHiLen
    //  private static final int CodeSize
    //------------------------------------------------------------------
    /**
     * OffHi部分の固定ハフマン符号長
     */
    private static final int[] ConstOffHiLen = PostLh3Encoder.createConstOffHiLen();

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
     * -lh3- 形式の圧縮データの出力先の ビット出力ストリーム
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  buffer of LZSS codes
    //------------------------------------------------------------------
    //  private byte[] buffer
    //  private int blockSize
    //  private int position
    //  private int flagBit
    //  private int flagPos
    //------------------------------------------------------------------
    /**
     * 静的ハフマン圧縮するためにデータを一時的に貯えるバッファ
     */
    private byte[] buffer;

    /**
     * バッファ内にある code データの数。
     */
    private int blockSize;

    /**
     * buffer内の現在処理位置
     */
    private int position;

    /**
     * flag バイト内の現在処理bit
     */
    private int flagBit;

    /**
     * buffer内の現在のflagバイトの位置
     */
    private int flagPos;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  frequancy counter for huffman
    //------------------------------------------------------------------
    //  private int[] codeFreq
    //  private int[] offHiFreq
    //------------------------------------------------------------------
    /**
     * code部の頻度表
     */
    private int[] codeFreq;

    /**
     * offHi部の頻度表
     */
    private int[] offHiFreq;


    //------------------------------------------------------------------
    //  constructers
    //------------------------------------------------------------------
    //  private PostLh3Encoder()
    //  public PostLh3Encoder( OutputStream out )
    //  public PostLh3Encoder( OutputStream out, int BufferSize )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PostLh3Encoder(){   }

    /**
     * -lh3- 圧縮用 PostLzssEncoderを構築する。<br>
     * バッファサイズにはデフォルト値が使用される。
     * 
     * @param out 圧縮データを受け取る出力ストリーム
     */
    public PostLh3Encoder( OutputStream out ){
        this( out, 16384 );
    }

    /**
     * -lh3- 圧縮用 PostLzssEncoderを構築する。<br>
     * 
     * @param out        圧縮データを受け取る出力ストリーム
     * @param BufferSize 静的ハフマン圧縮用のバッファサイズ
     * 
     * @exception IllegalArgumentException 
     *                   BufferSize が小さすぎる場合
     */
    public PostLh3Encoder( OutputStream out, int BufferSize ){
        final int DictionarySizeByteLen = 2;
        final int MinCapacity = ( DictionarySizeByteLen + 1 ) * 8 + 1;

        if( out != null
         && MinCapacity <= BufferSize ){

            if( out instanceof BitOutputStream ){
                this.out   = (BitOutputStream)out;
            }else{
                this.out   = new BitOutputStream( out );
            }
            this.codeFreq  = new int[ PostLh3Encoder.CodeSize ];
            this.offHiFreq = new int[ PostLh3Encoder.DictionarySize >> 6 ];
            this.buffer    = new byte[ BufferSize ];
            this.blockSize = 0;
            this.position  = 0;
            this.flagBit   = 0;
            this.flagPos   = 0;
        }else if( out == null ){
            throw new NullPointerException( "out" );
        }else{
            throw new IllegalArgumentException( "BufferSize too small. BufferSize must be larger than " + MinCapacity );
        }
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder
    //------------------------------------------------------------------
    //  write methods
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
        final int CodeMax  = PostLh3Encoder.CodeSize - 1;
        final int DictionarySizeByteLen = 2;
        final int Capacity = ( DictionarySizeByteLen + 1 ) * 8 + 1;

        if( this.flagBit == 0 ){
            if( this.buffer.length - this.position < Capacity 
             || ( 65536 - 8 ) <= this.blockSize ){
                this.writeOut();                                                //throws IOException
            }
            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.buffer[ this.flagPos ] = 0;
        }

        //データ格納
        this.buffer[this.position++] = (byte)code;

        //上位1ビットをフラグとして格納
        if( 0x100 <= code ) this.buffer[ this.flagPos ] |= this.flagBit;
        this.flagBit >>= 1;

        //頻度表更新
        this.codeFreq[ Math.min( code, CodeMax ) ]++;

        //ブロックサイズ更新
        this.blockSize++;
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ){
        //データ格納
        this.buffer[ this.position++ ] = (byte)( offset >> 8 );
        this.buffer[ this.position++ ] = (byte)offset;

        //頻度表更新
        this.offHiFreq[ ( offset >> 6 ) ]++;
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
     * この PostLzssEncoder にバッファリングされている全ての
     * 8ビット単位のデータを出力先の OutputStream に出力し、
     * 出力先の OutputStream を flush() する。<br>
     * このメソッドは圧縮率を変化させる。
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PostLzssEncoder#flush()
     * @see BitOutputStream#flush()
     */
    public void flush() throws IOException {
        this.writeOut();
        this.out.flush();
    }

    /**
     * この出力ストリームと、接続された出力ストリームを閉じ、
     * 使用していたリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.writeOut();
        this.out.close();                                                       //throws IOException

        this.out       = null;
        this.buffer    = null;
        this.codeFreq  = null;
        this.offHiFreq = null;
    }


    //------------------------------------------------------------------
    //  jp.gr.java_conf.dangan.util.lha.PostLzssEncoder methods
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
        return PostLh3Encoder.DictionarySize;
    }

    /**
     * -lh3-形式の LZSSの最大一致長を得る。
     * 
     * @return -lh3-形式の LZSSの最大一致長
     */
    public int getMaxMatch(){
        return PostLh3Encoder.MaxMatch;
    }

    /**
     * -lh3-形式の LZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return -lh3-形式の LZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return PostLh3Encoder.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman code
    //------------------------------------------------------------------
    //  private void writeOut()
    //------------------------------------------------------------------
    /**
     * バッファリングされた全てのデータを this.out に出力する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOut() throws IOException {
        final int CodeMax = PostLh3Encoder.CodeSize - 1;

        if( 0 < this.blockSize ){
            //------------------------------------------------------------------
            //  ブロックサイズ出力
            this.out.writeBits( 16, this.blockSize );                           //throws IOException

            //------------------------------------------------------------------
            //  ハフマン符号表生成
            int[] codeLen   = StaticHuffman.FreqListToLenList( this.codeFreq );
            int[] codeCode  = StaticHuffman.LenListToCodeList( codeLen );
            int[] offHiLen  = PostLh3Encoder.getBetterOffHiLen( this.offHiFreq,
                               StaticHuffman.FreqListToLenList( this.offHiFreq ) );
            int[] offHiCode = StaticHuffman.LenListToCodeList( offHiLen );

            //------------------------------------------------------------------
            //  code部のハフマン符号表出力
            if( 2 <= PostLh3Encoder.countNoZeroElement( this.codeFreq ) ){
                this.writeCodeLenList( codeLen );                               //throws IOException
            }else{
                this.out.writeBits( 15, 0x4210 );                               //throws IOException
                this.out.writeBits( 9,  
                       PostLh3Encoder.getNoZeroElementIndex( this.codeFreq ) ); //throws IOException
            }

            //------------------------------------------------------------------
            //  offHi部のハフマン符号表出力
            if( offHiLen != PostLh3Encoder.ConstOffHiLen ){
                this.out.writeBit( 1 );                                         //throws IOException

                if( 2 <= PostLh3Encoder.countNoZeroElement( this.offHiFreq ) ){
                    this.writeOffHiLenList( offHiLen );                         //throws IOException
                }else{
                    this.out.writeBits( 12, 0x0111 );                           //throws IOException
                    this.out.writeBits( 7,  
                       PostLh3Encoder.getNoZeroElementIndex( this.offHiFreq ) );//throws IOException
                }
            }else{
                this.out.writeBit( 0 );                                         //throws IOException
            }

            //------------------------------------------------------------------
            //  ハフマン符号出力
            this.position = 0;
            this.flagBit  = 0;
            for( int i = 0 ; i < blockSize ; i++ ){
                if( this.flagBit == 0 ){
                    this.flagBit = 0x80;
                    this.flagPos = this.position++;
                }

                if( 0 == ( this.buffer[ this.flagPos ] & this.flagBit ) ){
                    int code = this.buffer[ this.position++ ] & 0xFF;
                    this.out.writeBits( codeLen[ code ], codeCode[ code ] );    //throws IOException
                }else{
                    int code = ( this.buffer[ this.position++ ] & 0xFF ) | 0x100;
                    int offset = ( ( this.buffer[ this.position++ ] & 0xFF ) << 8 )
                               | ( this.buffer[ this.position++ ] & 0xFF );
                    int offHi  = offset >> 6;
                    if( code < CodeMax ){
                        this.out.writeBits( codeLen[ code ], codeCode[ code ] );//throws IOException
                    }else{
                        this.out.writeBits( codeLen[ CodeMax ], codeCode[ CodeMax ] );//throws IOException
                        this.out.writeBits( 8, code - CodeMax );                //throws IOException
                    }
                    this.out.writeBits( offHiLen[ offHi ], offHiCode[ offHi ] );//throws IOException
                    this.out.writeBits( 6, offset );                            //throws IOException
                }
                this.flagBit >>= 1;
            }

            //------------------------------------------------------------------
            //  次のブロックのための処理
            for( int i = 0 ; i < this.codeFreq.length ; i++ ){
                this.codeFreq[i] = 0;
            }

            for( int i = 0 ; i < this.offHiFreq.length ; i++ ){
                this.offHiFreq[i] = 0;
            }

            this.blockSize = 0;
            this.position  = 0;
            this.flagBit   = 0;

        }// if( 0 < this.blockSize )
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman list
    //------------------------------------------------------------------
    //  private void writeCodeLenList( int[] codeLen )
    //  private void writeOffHiLenList( int[] offHiLen )
    //------------------------------------------------------------------
    /**
     * code部のハフマン符号長のリストを符号化しながら書き出す。
     * 
     * @param codeLen code部のハフマン符号長のリスト
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeCodeLenList( int[] codeLen ) throws IOException {
        for( int i = 0 ; i < codeLen.length ; i++ ){
            if( 0 < codeLen[i] ){
                this.out.writeBits( 5, 0x10 | ( codeLen[i] - 1 ) );             //throws IOException
            }else{
                this.out.writeBit( 0 );                                         //throws IOException
            }
        }
    }

    /**
     * OffHi部のハフマン符号長のリストを符号化しながら書き出す。
     * 
     * @param OffHiLenList CodeFreq のハフマン符号長のリスト
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOffHiLenList( int[] offHiLen ) throws IOException {
        for( int i = 0 ; i < offHiLen.length ; i++ ){
            this.out.writeBits( 4, offHiLen[i] );                               //throws IOException
        }
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  staff of huffman encoder
    //------------------------------------------------------------------
    //  private static int countNoZeroElement( int[] array )
    //  private static int getNoZeroElementIndex( int[] array )
    //------------------------------------------------------------------
    /**
     * 配列内の 0でない要素数を得る。
     * 
     * @param array 配列
     * 
     * @return 配列内の 0でない要素数
     */
    private static int countNoZeroElement( int[] array ){
        int count = 0;
        for( int i = 0 ; i < array.length ; i++ ){
            if( 0 != array[i] ){
                count++;
            }
        }
        return count;
    }

    /**
     * 配列内の 0でない最初の要素を得る。
     * 
     * @param array 配列
     * 
     * @return 配列内の 0でない最初の要素
     *         全ての要素が0の場合は 0を返す。
     */
    private static int getNoZeroElementIndex( int[] array ){
        for( int i = 0 ; i < array.length ; i++ ){
            if( 0 != array[i] ){
                return i;
            }
        }
        return 0;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  calc the length of encoded data
    //------------------------------------------------------------------
    //  private static int[] createLenList()
    //  private static int[] getBetterOffHiLenList( int[] OffHiFreq, 
    //                                              int[] OffHiLen )
    //------------------------------------------------------------------
    /**
     * -lh3- の offHi部デコード用 ハフマン符号長リストを生成する。
     * 
     * @return -lh3- の offHi部デコード用 ハフマン符号長リスト
     */
    private static int[] createConstOffHiLen(){
        final int length = PostLh3Encoder.DictionarySize >> 6;
        final int[] list = { 2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0 };

        int[] offHiLen = new int[ length ];
        int index = 0;
        int len = list[ index++ ];

        for( int i = 0 ; i < length ; i++ ){
            while( list[index] == i ){
                len++;
                index++;
            }
            offHiLen[i] = len;
        }
        return offHiLen;
    }

    /**
     * OffHiFreqから生成された ハフマン符号長のリストと
     * 固定ハフマン符号長のリストを比較して、出力ビット
     * 数の少ないものを得る。
     * 
     * @param OffHiFreq offset部の上位6bitの出現頻度の表
     * @param OffHiLen  OffHiFreqから生成されたハフマン符
     *                  号長のリスト
     * 
     * @return 出力ビット数の少ない方のハフマン符号長のリスト
     */
    private static int[] getBetterOffHiLen( int[] OffHiFreq, 
                                            int[] OffHiLen ){
        boolean detect = false;
        for( int i = 0 ; i < OffHiLen.length ; i++ ){
            if( 15 < OffHiLen[i] ){ //15 はwriteOffHiLenListで書きこめる最大のハフマン符号長を意味する。
                detect = true;
            }
        }

        if( !detect ){
            int origTotal = 1;
            int consTotal = 1;

            if( 2 <= PostLh3Encoder.countNoZeroElement( OffHiFreq ) ){
                origTotal += 4 * ( PostLh3Encoder.DictionarySize >> 6 );
            }else{
                origTotal += 4 * 3 + 7;
            }
            for( int i = 0 ; i < OffHiFreq.length ; i++ ){
                origTotal += OffHiFreq[i] * OffHiLen[i];
                consTotal += OffHiFreq[i] * PostLh3Encoder.ConstOffHiLen[i];
            }

            if( origTotal < consTotal ) return OffHiLen;
            else                        return PostLh3Encoder.ConstOffHiLen;
        }else{
            return PostLh3Encoder.ConstOffHiLen;
        }
    }

}
//end of PostLh3Encoder.java
