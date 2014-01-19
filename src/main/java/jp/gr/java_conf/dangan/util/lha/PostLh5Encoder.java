//start of PostLh5Encoder.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PostLh5Encoder.java
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
import jp.gr.java_conf.dangan.io.Bits;
import jp.gr.java_conf.dangan.io.BitOutputStream;
import jp.gr.java_conf.dangan.util.lha.CompressMethod;
import jp.gr.java_conf.dangan.util.lha.StaticHuffman;
import jp.gr.java_conf.dangan.util.lha.PostLzssEncoder;

//import exceptions
import java.io.IOException;
import java.lang.NullPointerException;
import java.lang.IllegalArgumentException;
import jp.gr.java_conf.dangan.util.lha.BadHuffmanTableException;

import java.lang.Error;


/**
 * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLh5Encoder.java,v $
 * Revision 1.4  2002/12/08 00:00:00  dangan
 * [change]
 *     クラス名 を PostLh5EncoderCombo から PostLh5Encoder に変更。
 *
 * Revision 1.3  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.2  2002/12/01 00:00:00  dangan
 * [change]
 *     flush() されないかぎり 
 *     接続された OutputStream をflush() しないように変更。
 *
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [bug fix] 
 *     writeOutGroup でローカル変数 offLenFreq を使用しなければ
 *     ならない部分で this.offLenFreq を使用していた。
 * [maintenance]
 *     PostLh5Encoder から受け継いだインスタンスフィールド
 *     buffer, codeFreq, offLenFreq 廃止
 *     ソース整備
 *
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [improvement]
 *     DivideNum を導入する事によって処理するパターン数の減少を図る。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.4 $
 */
public class PostLh5Encoder implements PostLzssEncoder{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private BitOutputStream out
    //------------------------------------------------------------------
    /**
     * -lh4-, -lh5-, -lh6-, -lh7- 形式の圧縮データの出力先の ビット出力ストリーム
     */
    private BitOutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //  private int DictionarySizeByteLen
    //------------------------------------------------------------------
    /**
     * LZSSの辞書サイズ
     */
    private int DictionarySize;

    /**
     * LZSSの最大一致長
     */
    private int MaxMatch;

    /**
     * LZSS 圧縮/非圧縮 の閾値
     */
    private int Threshold;

    /**
     * 辞書サイズを示すのに必要なバイト数
     */
    private int DictionarySizeByteLen;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  current position
    //------------------------------------------------------------------
    //  private int position
    //  private int flagBit
    //  private int flagPos
    //------------------------------------------------------------------
    /**
     * this.block[ this.currentBlock ] 内の現在処理位置
     */
    private int position;

    /**
     * flag バイト内の圧縮/非圧縮を示すフラグ
     */
    private int flagBit;

    /**
     * this.block[ this.currentBlock ] 内の flagバイトの位置
     */
    private int flagPos;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman code blocks
    //------------------------------------------------------------------
    //  private int currentBlock
    //  private byte[][] block
    //  private int[] blockSize
    //  private int[][] blockCodeFreq
    //  private int[][] blockOffLenFreq
    //------------------------------------------------------------------
    /**
     * 現在処理中のハフマンブロックを示す。
     */
    private int currentBlock;

    /**
     * ハフマンコード格納用バッファ群
     */
    private byte[][] block;

    /**
     * 各ブロックの code データの数
     */
    private int[] blockSize;

    /**
     * 該当するブロックの code 部分の頻度表を持つ頻度表群
     */
    private int[][] blockCodeFreq;

    /**
     * 該当するブロックの offLen 部分の頻度表を持つ頻度表群
     */
    private int[][] blockOffLenFreq;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  groups of huffman code blocks and patterns of groups
    //------------------------------------------------------------------
    //  private int[][] pattern
    //  private int[][] group
    //------------------------------------------------------------------
    /**
     * 全ブロックを幾つかのグループに分割するパターンの配列。
     */
    private int[][] pattern;

    /**
     * 複数ブロックを組み合わせたグループの配列。
     * this.group[0] 全ブロックを持つグループが
     * this.group[1] this.group[2] には 全ブロックから各々最後と最初のブロックを欠いたグループが
     * …というようにピラミッド状に構成される。
     */
    private int[][] group;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private PostLh5Encoder()
    //  public PostLh5Encoder( OutputStream out )
    //  public PostLh5Encoder( OutputStream out, String method )
    //  public PostLh5Encoder( OutputStream out, String method, 
    //                              int BufferSize )
    //  public PostLh5Encoder( OutputStream out, String method,
    //                              int BlockNum, int BlockSize, int DivideNum )
    //------------------------------------------------------------------
    /**
     * 使用不可。
     */
    private PostLh5Encoder(){  }

    /**
     * -lh5- 圧縮用 PostLzssEncoder を構築する。<br>
     * バッファサイズにはデフォルト値が使用される。
     * 
     * @param out 圧縮データを受け取る OutputStream
     */
    public PostLh5Encoder( OutputStream out ){
        this( out, CompressMethod.LH5 );
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
     * バッファサイズにはデフォルト値が使用される。
     * 
     * @param out    圧縮データを受け取る OutputStream
     * @param method 圧縮法を示す文字列<br>
     *  &emsp;&emsp; CompressMethod.LH4 <br>
     *  &emsp;&emsp; CompressMethod.LH5 <br>
     *  &emsp;&emsp; CompressMethod.LH6 <br>
     *  &emsp;&emsp; CompressMethod.LH7 <br>
     *  &emsp;&emsp; の何れかを指定する。
     * 
     * @exception IllegalArgumentException 
     *               method が上記以外の場合
     */
    public PostLh5Encoder( OutputStream out, 
                                String       method ){
        this( out, method, 16384 );
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
     * 
     * @param out        圧縮データを受け取る OutputStream
     * @param method     圧縮法を示す文字列<br>
     *      &emsp;&emsp; CompressMethod.LH4 <br>
     *      &emsp;&emsp; CompressMethod.LH5 <br>
     *      &emsp;&emsp; CompressMethod.LH6 <br>
     *      &emsp;&emsp; CompressMethod.LH7 <br>
     *      &emsp;&emsp; の何れかを指定する。
     * @param BufferSize LZSS圧縮データを退避しておく
     *                   バッファのサイズ
     * 
     * @exception IllegalArgumentException <br>
     *      &emsp;&emsp; (1) method が上記以外の場合<br>
     *      &emsp;&emsp; (2) BufferSize が小さすぎる場合<br>
     *      &emsp;&emsp; の何れか
     */
    public PostLh5Encoder( OutputStream out,
                                String       method,
                                int          BufferSize ){
        this( out, method, 1, BufferSize, 0 );
    }

    /**
     * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
     * 1つが BlockSizeバイト の BlockNum 個のブロックを組み合わせて
     * 最も出力ビット数の少ない構成で出力する。
     * 組み合わせは 全ブロックを DivideNum + 1 個に分割して得られる
     * 全パターンが試される。
     * 
     * @param out       圧縮データを受け取る OutputStream
     * @param method    圧縮法を示す文字列<br>
     *     &emsp;&emsp; CompressMethod.LH4 <br>
     *     &emsp;&emsp; CompressMethod.LH5 <br>
     *     &emsp;&emsp; CompressMethod.LH6 <br>
     *     &emsp;&emsp; CompressMethod.LH7 <br>
     *     &emsp;&emsp; の何れかを指定する。
     * @param BlockNum  ブロック数
     * @param BlockSize 1ブロックのバイト数
     * @param DivideNum 最大分割数
     * 
     * @exception IllegalArgumentException <br>
     *     &emsp;&emsp; (1) CompressMethod が上記以外の場合<br>
     *     &emsp;&emsp; (2) BlockNum が 0以下の場合<br>
     *     &emsp;&emsp; (3) BlockSize が小さすぎる場合<br>
     *     &emsp;&emsp; (4) DivideNum が 0未満であるか、BlockNum以上の場合<br>
     *     &emsp;&emsp; のいずれか。
     */
    public PostLh5Encoder( OutputStream out,
                                String       method,
                                int          BlockNum,
                                int          BlockSize,
                                int          DivideNum ){

        if( CompressMethod.LH4.equals( method )
         || CompressMethod.LH5.equals( method )
         || CompressMethod.LH6.equals( method )
         || CompressMethod.LH7.equals( method ) ){

            this.DictionarySize        = CompressMethod.toDictionarySize( method );
            this.MaxMatch              = CompressMethod.toMaxMatch( method );
            this.Threshold             = CompressMethod.toThreshold( method );
            this.DictionarySizeByteLen = ( Bits.len( this.DictionarySize - 1 ) + 7 ) / 8;

            final int MinCapacity = ( DictionarySizeByteLen + 1 ) * 8 + 1;

            if( out != null
             && 0 < BlockNum
             && 0 <= DivideNum && DivideNum < BlockNum
             && MinCapacity <= BlockSize ){

                if( out instanceof BitOutputStream ){
                    this.out = (BitOutputStream)out;
                }else{
                    this.out = new BitOutputStream( out );
                }

                this.currentBlock    = 0;
                this.block           = new byte[ BlockNum ][];
                this.blockSize       = new int[ BlockNum ];
                this.blockCodeFreq   = new int[ BlockNum ][];
                this.blockOffLenFreq = new int[ BlockNum ][];

                int codeFreqSize   = 256 + this.MaxMatch - this.Threshold + 1;
                int offLenFreqSize = Bits.len( this.DictionarySize );
                for( int i = 0 ; i < BlockNum ; i++ ){
                    this.block[i]           = new byte[ BlockSize ];
                    this.blockCodeFreq[i]   = new int[ codeFreqSize ];
                    this.blockOffLenFreq[i] = new int[ offLenFreqSize ];
                }

                this.group      = PostLh5Encoder.createGroup( BlockNum, DivideNum );
                this.pattern    = PostLh5Encoder.createPattern( BlockNum, DivideNum );

                this.position   = 0;
                this.flagBit    = 0;
                this.flagPos    = 0;

            }else if( out == null ){
                throw new NullPointerException( "out" );
            }else if( BlockNum <= 0 ){
                throw new IllegalArgumentException( "BlockNum too small. BlockNum must be 1 or more." );
            }else if( DivideNum < 0 || BlockNum <= DivideNum ){
                throw new IllegalArgumentException( "DivideNum out of bounds( 0 to BlockNum - 1(" + ( BlockNum - 1 ) + ") )." );
            }else{
                throw new IllegalArgumentException( "BlockSize too small. BlockSize must be larger than " + MinCapacity );
            }


        }else if( method == null ){
            throw new NullPointerException( "method" );
        }else{
            throw new IllegalArgumentException( "Unknown compress method. " + method );
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
        int need = ( ( 0x100 <= code ? this.DictionarySizeByteLen + 1 : 1 )
                   + ( this.flagBit == 0 ? 1 : 0 ) );

        if( this.block[ this.currentBlock ].length - this.position < need
         || 65535 <= this.blockSize[ this.currentBlock ] ){

            this.currentBlock++;
            if( this.block.length <= this.currentBlock ){
                this.writeOut();
            }else{
                this.position   = 0;
            }

            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.block[ this.currentBlock ][ this.flagPos ] = 0;
        }else if( this.flagBit == 0 ){
            this.flagBit = 0x80;
            this.flagPos = this.position++;
            this.block[ this.currentBlock ][ this.flagPos ] = 0;
        }

        //データ格納
        this.block[ this.currentBlock ][ this.position++ ] = (byte)code;

        //上位1ビットをフラグとして格納
        if( 0x100 <= code ){
            this.block[ this.currentBlock ][ this.flagPos ] |= this.flagBit;
        }
        this.flagBit >>= 1;

        //頻度表更新
        this.blockCodeFreq[ this.currentBlock ][ code ]++;

        //ブロックサイズ更新
        this.blockSize[ this.currentBlock ]++;
    }

    /**
     * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
     * 
     * @param offset LZSS で圧縮された圧縮コードのうち一致位置
     */
    public void writeOffset( int offset ){
        //データ格納
        int shift = ( this.DictionarySizeByteLen - 1 ) << 3;
        while( 0 <= shift ){
            this.block[ this.currentBlock ][ this.position++ ] = (byte)( offset >> shift );
            shift -= 8;
        }

        //頻度表更新
        this.blockOffLenFreq[ this.currentBlock ][ Bits.len( offset ) ]++;
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
        this.writeOut();                                                        //throws IOException
        this.out.close();                                                       //throws IOException

        this.out             = null;
        this.block           = null;
        this.blockCodeFreq   = null;
        this.blockOffLenFreq = null;
        this.group           = null;
        this.pattern         = null;
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
     * この PostLh5Encoder が扱うLZSS辞書のサイズを得る。
     * 
     * @return この PostLh5Encoder が扱うLZSS辞書のサイズ
     */
    public int getDictionarySize(){
        return this.DictionarySize;
    }

    /**
     * この PostLh5Encoder が扱うLZSSの最長一致長を得る。
     * 
     * @return この PostLh5Encoder が扱うLZSSの最大一致長
     */
    public int getMaxMatch(){
        return this.MaxMatch;
    }

    /**
     * この PostLh5Encoder が扱うLZSSの圧縮、非圧縮の閾値を得る。
     * 
     * @return この PostLh5Encoder が扱うLZSSの圧縮、非圧縮の閾値
     */
    public int getThreshold(){
        return this.Threshold;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write huffman code
    //------------------------------------------------------------------
    //  private void writeOut()
    //  private void writeOutBestPattern()
    //  private void writeOutGroup( int[] group )
    //------------------------------------------------------------------
    /**
     * バッファリングされた全てのデータを this.out に出力する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOut() throws IOException {
        if( 1 < this.block.length ){
            this.writeOutBestPattern();
        }else{
            this.writeOutGroup( new int[]{ 0 } );
            this.currentBlock = 0;
        }

        this.position   = 0;
        this.flagBit    = 0;
    }

    /**
     * バッファリングされた全てのデータを最良の構成で this.out に出力する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOutBestPattern() throws IOException {
        int[] bestPattern  = null;
        int[] groupHuffLen = new int[ this.group.length  ];

        //------------------------------------------------------------------
        //  group を出力したときの bit 数を求める。
        for( int i = 0 ; i < this.group.length ; i++ ){
            if( this.group != null ){
                int blockSize = 0;
                for( int j = 0 ; j < this.group[i].length ; j++ ){
                    blockSize += this.blockSize[ this.group[i][j] ];
                }
                if( 0 < blockSize && blockSize < 65536 ){
                    groupHuffLen[i] = 
                        PostLh5Encoder.calcHuffmanCodeLength(
                            this.DictionarySize,
                            PostLh5Encoder.margeArrays( this.group[i], this.blockCodeFreq ),
                            PostLh5Encoder.margeArrays( this.group[i], this.blockOffLenFreq ) );
                }else if( 0 == blockSize ){
                    groupHuffLen[i] = 0;
                }else{
                    groupHuffLen[i] = -1;
                }
            }else{
                groupHuffLen[i] = -1;
            }
        }

        //------------------------------------------------------------------
        //  出力 bit 数が最小となる pattern を総当りで求める。
        int smallest = Integer.MAX_VALUE;
        for( int i = 0 ; i < this.pattern.length ; i++ ){
            int length = 0;

            for( int j = 0 ; j < this.pattern[i].length ; j++ ){
                if( 0 <= groupHuffLen[ this.pattern[i][j] ] ){
                    length += groupHuffLen[ this.pattern[i][j] ];
                }else{
                    length = Integer.MAX_VALUE;
                    break;
                }
            }
            if( length < smallest ){
                bestPattern = this.pattern[i];
                smallest    = length;
            }
        }

        //------------------------------------------------------------------
        //  最も出力 bit 数の少ないパターンで出力
        //  どの パターン もブロックサイズが 65536 以上の
        //  グループを持つ場合はブロック単位で出力。
        if( bestPattern != null ){
            for( int i = 0 ; i < bestPattern.length ; i++ ){
                this.writeOutGroup( this.group[ bestPattern[i] ] );             //throws IOException
            }
        }else{
            for( int i = 0 ; i < this.block.length ; i++ ){
                this.writeOutGroup( new int[]{ i } );
            }
        }

        this.currentBlock = 0;
    }

    /**
     * group で指定された ブロック群をハフマン符号化して this.out に出力する。<br>
     * 
     * @param group 出力するブロック番号を持つ配列
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOutGroup( int[] group ) throws IOException {
        int[] codeFreq   = PostLh5Encoder.margeArrays( group, this.blockCodeFreq );
        int[] offLenFreq = PostLh5Encoder.margeArrays( group, this.blockOffLenFreq );

        int blockSize = 0;
        for( int i = 0 ; i < group.length ; i++ ){
            blockSize += this.blockSize[ group[i] ];
        }

        if( 0 < blockSize ){
            //------------------------------------------------------------------
            //  ブロックサイズ出力
            this.out.writeBits( 16, blockSize );

            //------------------------------------------------------------------
            //  ハフマン符号表生成
            int[] codeLen    = StaticHuffman.FreqListToLenList( codeFreq );
            int[] codeCode   = StaticHuffman.LenListToCodeList( codeLen );
            int[] offLenLen  = StaticHuffman.FreqListToLenList( offLenFreq );
            int[] offLenCode = StaticHuffman.LenListToCodeList( offLenLen );


            //------------------------------------------------------------------
            //  code 部のハフマン符号表出力
            if( 2 <= PostLh5Encoder.countNoZeroElement( codeFreq ) ){
                int[] codeLenFreq = PostLh5Encoder.createCodeLenFreq( codeLen );
                int[] codeLenLen  = StaticHuffman.FreqListToLenList( codeLenFreq );
                int[] codeLenCode = StaticHuffman.LenListToCodeList( codeLenLen );

                if( 2 <= PostLh5Encoder.countNoZeroElement( codeLenFreq ) ){
                    this.writeCodeLenLen( codeLenLen );                         //throws IOException
                }else{
                    this.out.writeBits( 5, 0 );                                 //throws IOException
                    this.out.writeBits( 5, 
                        PostLh5Encoder.getNoZeroElementIndex( codeLenFreq ) );//throws IOException
                }
                this.writeCodeLen( codeLen, codeLenLen, codeLenCode );          //throws IOException
            }else{
                this.out.writeBits( 10, 0 );                                    //throws IOException
                this.out.writeBits( 18, 
                        PostLh5Encoder.getNoZeroElementIndex( codeFreq ) );//throws IOException
            }

            //------------------------------------------------------------------
            //  offLen 部のハフマン符号表出力
            if( 2 <= PostLh5Encoder.countNoZeroElement( offLenFreq ) ){
                this.writeOffLenLen( offLenLen );                               //throws IOException
            }else{
                int len = Bits.len( Bits.len( this.DictionarySize ) );
                this.out.writeBits( len, 0 );                                   //throws IOException
                this.out.writeBits( len, 
                        PostLh5Encoder.getNoZeroElementIndex( offLenFreq ) );//throws IOException
            }


            //------------------------------------------------------------------
            //  ハフマン符号出力
            for( int i = 0 ; i < group.length ; i++ ){
                this.position = 0;
                this.flagBit  = 0;
                byte[] buffer = this.block[ group[i] ];

                for( int j = 0 ; j < this.blockSize[ group[i] ] ; j++ ){
                    if( this.flagBit == 0 ){
                        this.flagBit = 0x80;
                        this.flagPos = this.position++;
                    }
                    if( 0 == ( buffer[ this.flagPos ] & this.flagBit ) ){
                        int code = buffer[ this.position++ ] & 0xFF;
                        this.out.writeBits( codeLen[ code ], codeCode[ code ] );    //throws IOException
                    }else{
                        int code = ( buffer[ this.position++ ] & 0xFF ) | 0x100;
                        int offset = 0;
                        for( int k = 0 ; k < this.DictionarySizeByteLen ; k++ ){
                           offset = ( offset << 8 ) | ( buffer[ this.position++ ] & 0xFF );
                        }
                        int offlen = Bits.len( offset );
                        this.out.writeBits( codeLen[ code ], codeCode[ code ]  );   //throws IOException
                        this.out.writeBits( offLenLen[ offlen ], offLenCode[ offlen ] ); //throws IOException
                        if( 1 < offlen ) this.out.writeBits( offlen - 1, offset );  //throws IOException
                    }
                    this.flagBit >>= 1;
                }
            }

            //------------------------------------------------------------------
            //  次のブロックのための処理
            for( int i = 0 ; i < group.length ; i++ ){
                this.blockSize[ group[i] ] = 0;

                codeFreq = this.blockCodeFreq[ group[i] ];
                for( int j = 0 ; j < codeFreq.length ; j++ ){
                    codeFreq[j] = 0;
                }

                offLenFreq = this.blockOffLenFreq[ group[i] ];
                for( int j = 0 ; j < offLenFreq.length ; j++ ){
                    offLenFreq[j] = 0;
                }
            }
        }//if( 0 < blockSize )
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  write out huffman list
    //------------------------------------------------------------------
    //  private void writeCodeLenLen( int[] codeLenLen )
    //  private void writeCodeLen( int[] codeLen,
    //           int[] codeLenLen, int[] codeLenCode )
    //  private void writeOffLenLen( int[] offLenLen )
    //------------------------------------------------------------------
    /**
     * codeLen の ハフマン符号長のリストを書き出す。
     * 
     * @param codeLenLen codeLenFreq のハフマン符号長のリスト
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeCodeLenLen( int[] codeLenLen ) throws IOException {
        int end = codeLenLen.length;
        while( 0 < end && codeLenLen[end - 1] == 0 ){
            end--;
        }

        this.out.writeBits( 5, end );                                           //throws IOException
        int index = 0;
        while( index < end ){
            int len = codeLenLen[ index++ ];
            if( len <= 6 ) this.out.writeBits( 3, len );                        //throws IOException
            else           this.out.writeBits( len - 3, ( 1 << ( len - 3 ) ) - 2 );//throws IOException

            if( index == 3 ){
                while( codeLenLen[index] == 0 && index < 6 ){
                    index++;
                }
                this.out.writeBits( 2, ( index - 3 ) & 0x03 );                  //throws IOException
            }
        }
    }

    /**
     * code 部のハフマン符号長のリストを
     * ハフマンとランレングスで符号化しながら書き出す。
     * 
     * @param codeLen     codeFreq のハフマン符号長のリスト
     * @param codeLenLen  codeLenFreq のハフマン符号長のリスト
     * @param codeLenCode codeLenFreq のハフマン符号のリスト
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeCodeLen( int[] codeLen,
                               int[] codeLenLen,
                               int[] codeLenCode ) throws IOException {
        int end = codeLen.length;
        while( 0 < end && codeLen[end - 1] == 0 ){
            end--;
        }

        this.out.writeBits( 9, end );                                           //throws IOException
        int index = 0;
        while( index < end ){
            int len = codeLen[ index++ ];

            if( 0 < len ){
                this.out.writeBits( codeLenLen[len + 2], codeLenCode[len + 2] );//throws IOException
            }else{
                int count = 1;
                while( codeLen[ index ] == 0 && index < end ){
                    count++;
                    index++;
                }

                if( count <= 2 ){
                    for( int i = 0 ; i < count ; i++ )
                        this.out.writeBits(codeLenLen[0], codeLenCode[0]);      //throws IOException
                }else if( count <= 18 ){
                    this.out.writeBits( codeLenLen[1], codeLenCode[1] );        //throws IOException
                    this.out.writeBits( 4, count - 3 );                         //throws IOException
                }else if( count == 19 ){
                    this.out.writeBits( codeLenLen[0], codeLenCode[0] );        //throws IOException
                    this.out.writeBits( codeLenLen[1], codeLenCode[1] );        //throws IOException
                    this.out.writeBits( 4, 0x0F );                              //throws IOException
                }else{
                    this.out.writeBits( codeLenLen[2], codeLenCode[2] );        //throws IOException
                    this.out.writeBits( 9, count - 20 );                        //throws IOException
                }
            }
        }
    }

    /**
     * offLen のハフマン符号長のリストを書き出す
     * 
     * @param offLenLen offLenFreq のハフマン符号長のリスト
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    private void writeOffLenLen( int[] offLenLen ) throws IOException {
        int end = offLenLen.length;
        while( 0 < end && offLenLen[end - 1] == 0 ){
            end--;
        }
        
        int len = Bits.len( Bits.len( this.DictionarySize ) );
        this.out.writeBits( len, end );                                         //throws IOException
        int index = 0;
        while( index < end ){
            len = offLenLen[ index++ ];
            if( len <= 6) this.out.writeBits( 3, len );                         //throws IOException
            else          this.out.writeBits( len - 3, ( 1 << ( len - 3 ) ) - 2 );//throws IOException
        }
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  staff of huffman encoder
    //------------------------------------------------------------------
    //  private static int countNoZeroElement( int[] array )
    //  private static int getNoZeroElementIndex( int[] array )
    //  private static int[] margeArrays( int[] indexes, int[][] arrays )
    //  private static int[] createCodeLenFreq( int[] CodeLenList )
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

    /**
     * arrays の中から、indexes で指定された配列を連結する。
     * 
     * @param indexes arrays内の走査対象の配列を示す添え字の表
     * @param arrays  走査対象の配列を含んだリスト
     */
    private static int[] margeArrays( int[] indexes, int[][] arrays ){
        if( 1 < indexes.length ){
            int[] array = new int[ arrays[0].length ];

            for( int i = 0 ; i < indexes.length ; i++ ){
                int[] src = arrays[ indexes[i] ];

                for( int j = 0 ; j < src.length ; j++ ){
                    array[j] += src[j];
                }
            }
            return array;
        }else{
            return arrays[ indexes[0] ];
        }
    }


    /**
     * codeLen をランレングスとハフマンで符号化するための頻度表を作成する。
     * 作成する頻度表は 
     * codeLenFreq[0]には要素数0の要素が1つあって読み飛ばす事を指示する
     * codeLenFreq[1]には要素数0の要素が3～18あって、続く5bitのデータをみて
     * その長さのデータを読み飛ばす事を指示する
     * codeLenFreq[2]には要素数0の要素が20以上あって、続く9bitのデータをみて
     * その長さのデータを読み飛ばす事を指示する
     * という特殊な意味を持つ要素も含まれる。
     * 従来の頻度は +2された位置にそれぞれ配置される。
     * 
     * @param codeLen codeFreq のハフマン符号長のリスト
     * 
     * @return codeLen の頻度表
     */
    private static int[] createCodeLenFreq( int[] codeLen ){
        int[] codeLenFreq = new int[ StaticHuffman.LIMIT_LEN + 3 ];

        int end = codeLen.length;
        while( 0 < end && codeLen[end - 1] == 0 ){
            end--;
        }

        int index = 0;
        while( index < end ){
            int len = codeLen[ index++ ];

            if( 0 < len ){
                codeLenFreq[ len + 2 ]++;
            }else{
                int count = 1;
                while( codeLen[ index ] == 0 && index < end ){
                    count++;
                    index++;
                }

                if( count <= 2 ){
                    codeLenFreq[0] += count;
                }else if( count <= 18 ){
                    codeLenFreq[1]++;
                }else if( count == 19 ){
                    codeLenFreq[0]++;
                    codeLenFreq[1]++;
                }else{
                    codeLenFreq[2]++;
                }
            }
        }
        return codeLenFreq;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  calc the langth of encoded data
    //------------------------------------------------------------------
    //  private static int calcHuffmanCodeLength( int DictionarySize, 
    //                            int[] CodeFreq, int[] OffLenFreq )
    //  private static int calcCodeLenLen( int[] codeLenLen )
    //  private static int calcCodeLen( int[] codeLen, int[] codeLenLen )
    //  private static int calcOffLenLen( int DictionarySize, int[] offLenLen )
    //------------------------------------------------------------------
    /**
     * 指定された頻度情報でハフマン符号を
     * 出力した場合のビット数を得る。
     * 
     * @param DictionarySize LZSS辞書サイズ
     * @param codeFreq       コード部の頻度情報
     * @param offLenFreq     オフセット部の長さの頻度情報
     * 
     * @return この頻度情報でハフマン符号を出力した場合のビット数を得る。
     */
    private static int calcHuffmanCodeLength( int   DictionarySize,
                                              int[] codeFreq,
                                              int[] offLenFreq ){

        //------------------------------------------------------------------
        //  初期化
        int length = 0;
        int[] codeLen, codeCode, offLenLen;
        try{
            codeLen   = StaticHuffman.FreqListToLenList( codeFreq );
            codeCode  = StaticHuffman.LenListToCodeList( codeLen );
            offLenLen = StaticHuffman.FreqListToLenList( offLenFreq );
        }catch( BadHuffmanTableException exception ){ //発生しない
            throw new Error( "caught the BadHuffmanTableException which should be never thrown." );
        }

        //------------------------------------------------------------------
        //  code 部のハフマン頻度表の長さを算出する。
        length += 16;
        if( 2 <= PostLh5Encoder.countNoZeroElement( codeFreq ) ){
            int[] codeLenFreq = PostLh5Encoder.createCodeLenFreq( codeLen );
            int[] codeLenLen  = StaticHuffman.FreqListToLenList( codeLenFreq );
            if( 2 <= PostLh5Encoder.countNoZeroElement( codeLenFreq ) ){
                length += PostLh5Encoder.calcCodeLenLen( codeLenLen );
            }else{
                length += 5;
                length += 5;
            }
            length += PostLh5Encoder.calcCodeLen( codeLen, codeLenLen );
        }else{
            length += 10;
            length += 18;
        }

        //------------------------------------------------------------------
        //  offLen 部のハフマン頻度表の長さを算出する。
        if( 2 <= PostLh5Encoder.countNoZeroElement( offLenFreq ) ){
            length += PostLh5Encoder.calcOffLenLen( DictionarySize, offLenLen );
        }else{
            int len = Bits.len( Bits.len( DictionarySize ) );
            length += len;
            length += len;
        }

        //------------------------------------------------------------------
        //  LZSS圧縮後のデータをさらにハフマン符号化した長さを算出する。
        for( int i = 0 ; i < codeFreq.length ; i++ ){
            length += codeFreq[i] * codeLen[i];
        }
        for( int i = 0 ; i < offLenFreq.length ; i++ ){
            length += offLenFreq[i] * ( offLenLen[i] + i - 1 );
        }
        return length;
    }

    /**
     * 指定したハフマン符号長の表を出力した場合のビット数を得る。
     * 
     * @param codeLenLen コード部のハフマン符号長を
     *                   さらにハフマン符号化したものの表
     * 
     * @return 指定したハフマン符号長の表を出力した場合のビット数
     */
    private static int calcCodeLenLen( int[] codeLenLen ){
        int length = 0;
        int end    = codeLenLen.length;
        while( 0 < end && codeLenLen[end - 1] == 0 ){
            end--;
        }

        length += 5;

        int index = 0;
        while( index < end ){
            int len = codeLenLen[ index++ ];
            if( len <= 6 ) length += len;
            else           length += len - 3;

            if( index == 3 ){
                while( codeLenLen[index] == 0 && index < 6 ){
                    index++;
                }
                length += 2;
            }
        }
        return length;
    }

    /**
     * 指定したハフマン符号長の表を出力した場合のビット数を得る。
     * 
     * @param codeLen    コード部のハフマン符号長の表
     * @param codeLenLen コード部のハフマン符号長を
     *                   さらにハフマン符号化したものの表
     * 
     * @return 指定したハフマン符号長の表を出力した場合のビット数
     */
    private static int calcCodeLen( int[] codeLen,
                                    int[] codeLenLen ){
        int length = 0;
        int end    = codeLen.length;
        while( 0 < end && codeLen[end - 1] == 0 ){
            end--;
        }

        length += 9;

        int index = 0;
        while( index < end ){
            int len = codeLen[ index++ ];

            if( 0 < len ){
                length += codeLenLen[ len + 2 ];
            }else{
                int count = 1;
                while( codeLen[ index ] == 0 && index < end ){
                    count++;
                    index++;
                }

                if( count <= 2 ){
                    for( int i = 0 ; i < count ; i++ )
                        length += codeLenLen[0];
                }else if( count <= 18 ){
                    length += codeLenLen[1];
                    length += 4;
                }else if( count == 19 ){
                    length += codeLenLen[0];
                    length += codeLenLen[1];
                    length += 4;
                }else{
                    length += codeLenLen[2];
                    length += 9;
                }
            }
        }
        return length;
    }

    /**
     * 指定したハフマン符号長の表を出力した場合のビット数を得る。
     * 
     * @param DictionarySize LZSS辞書サイズ
     * @param offLenLen      オフセット部の長さのハフマン符号長の表
     * 
     * @return 指定したハフマン符号長の表を出力した場合のビット数
     */
    private static int calcOffLenLen( int   DictionarySize,
                                      int[] offLenLen ){
        int length = 0;
        int end    = offLenLen.length;
        while( 0 < end && offLenLen[end - 1] == 0 ){
            end--;
        }

        length += Bits.len( Bits.len( DictionarySize ) );

        int index = 0;
        while( index < end ){
            int len = offLenLen[ index++ ];
            if( len <= 6) length += 3;
            else          length += len - 3;
        }
        return length;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  create group and pattern
    //------------------------------------------------------------------
    //  private static int[][] createGroup( int BlockNum, int DivideNum )
    //  private static int[][] createPattern( int BlockNum, int DivideNum )
    //  private static int calcPatternNum( int BlockNum, int DivideNum )
    //------------------------------------------------------------------
    /**
     * BlockNumのブロックを連続したブロックに
     * グループ化したもののリストを返す。
     * <pre>
     * group = new int[]{ 0,1,2 }
     * </pre>
     * のような場合 
     * block[0] と block[1] と block[2] 
     * から成るグループであることを示す。
     * またグループは 
     * group[0] は全ブロックから成るグループ、
     * group[1] と group[2] はそれぞれ全ブロックから
     * 最後のブロックと最初のブロックを欠いたもの、
     * というように ピラミッド状に規則を持って生成され、
     * createPattern はこの規則性を利用するため 
     * このメソッドを改変する場合は注意すること。
     * また、使用しない group には null が入っているので注意すること。
     * 
     * @param BlockNum  ブロックの個数
     * @param DivideNum 最大分割数
     * 
     * @reutrn 生成されたグループのリスト
     */
    private static int[][] createGroup( int BlockNum, int DivideNum ){
        int[][] group = new int[ ( BlockNum + 1 ) * BlockNum / 2 ][];

        if( DivideNum == 0 ){
            //------------------------------------------------------------------
            //  全ブロックを持つグループのみ生成
            group[0] = new int[ BlockNum ];
            for( int i = 0 ; i < BlockNum ; i++ ){
                group[0][i] = i;
            }
        }else if( 2 < BlockNum && DivideNum == 1 ){
            //------------------------------------------------------------------
            //  同サイズのグループのうち最初のものと最後のものだけ生成。
            int index = 0;
            for( int size = BlockNum ; 0 < size ; size-- ){
                group[ index ] = new int[ size ];
                for( int i = 0 ; i < size ; i++  ){
                    group[index][i] = i;
                }
                if( size < BlockNum ){
                    index +=  BlockNum - size;
                    group[ index ] = new int[ size ];
                    for( int i = 0 ; i < size ; i++ ){
                        group[index][i] = i + BlockNum - size;
                    }
                }
                index++;
            }
        }else{
            //------------------------------------------------------------------
            //  全グループを生成。
            int index = 0;
            for( int size = BlockNum ; 0 < size ; size-- ){
                for( int start = 0 ; size + start <= BlockNum ; start++ ){
                    group[index] = new int[ size ];

                    for( int i = 0 ; i < size ; i++  ){
                        group[index][i] = start + i;
                    }
                    index++;
                }
            }
        }
        return group;
    }

    /**
     * BlockNumのブロックを最大 DivideNum + 1個の領域に
     * 分割したときの パターンの表を生成する。
     * 1つのパターンは createGroup で生成される
     * グループ配列への添字の列挙で示される。
     * <pre>
     * pattern = new int[]{ 1,3 }; 
     * </pre>
     * のような パターンは group[1] と group[3] の間で
     * 分割されたことを示す。
     * 
     * @param BlockNum  ブロックの個数
     * @param DivideNum 最大分割数
     * 
     * @return 生成されたパターンのリスト
     */
    private static int[][] createPattern( int BlockNum, int DivideNum ){
        int index = 0;
        int patternNum = PostLh5Encoder.calcPatternNum( BlockNum, DivideNum );
        int[][] pattern = new int[ patternNum ][];

        for( int div = 0 ; div < Math.min( BlockNum, DivideNum + 1 ) ; div++ ){
            //分割位置を保持する配列。 
            //配列内の値は、例えば 0の場合は Block[0] と Block[1] の間で分割することを意味する。
            int[] divPos = new int[ div ]; 
            for( int i = 0 ; i < divPos.length ; i++ ){
                divPos[i] = i;
            }

            //同じ 分割数のパターンを生成するループ
            //more は この分割数で、まだパターンが生成できる事を示す。
            boolean more;
            do{
                pattern[index] = new int[ div + 1 ];

                int start = 0;
                for( int i = 0 ; i < divPos.length ; i++ ){
                    int len = ( divPos[i] - start ) + 1;
                    int num = BlockNum - len;
                    pattern[index][i] = ( num + 1 ) * num / 2 + start;
                    start += len;
                }
                int num = BlockNum - ( BlockNum - start );
                pattern[index][divPos.length] = ( num + 1 ) * num / 2 + start;
                index++;

                //分割位置を移動する。分割位置を移動できれば、
                //この分割数でまだ出力できるパターンがあると判断できる。
                more = false;
                int move  = divPos.length - 1;
                int range = BlockNum - 2;
                while( 0 <= move && !more ){
                    if( divPos[move] < range ){
                        divPos[move]++;
                        if( move < divPos.length - 1 ){
                            for( int i = move ; i < divPos.length - 1 ; i++ )
                                divPos[i+1] = divPos[i] + 1;
                        }
                        more = true;
                    }
                    range = divPos[move] - 1;
                    move--;
                }
            }while( more );
        }
        return pattern;
    }

    /**
     * BlockNum 個のブロックを 
     * 最大 DivideNum + 1 個に連続した領域に分割した場合
     * 何パターンできるかを得る。
     * 
     * @param BlockNum  ブロックの個数
     * @param DivideNum 分割数
     * 
     * @return パターン数。
     */
    private static int calcPatternNum( int BlockNum, int DivideNum ){
        int patternNum = 0;
        for( int div = 0 ; div <= DivideNum ; div++ ){
            int count = ( div <= ( BlockNum / 2 ) ? div : BlockNum - 1 - div );

            int numerator = 1;
            for( int i = 1 ; i <= count ; i++ ){
                numerator *= ( BlockNum - i );
            }

            int denominator = 1;
            for( int i = 1 ; i <= count ; i++ ){
                denominator *= i;
            }
            
            patternNum += numerator / denominator;
        }
        return patternNum;
    }

}
//end of PostLh5Encoder.java
