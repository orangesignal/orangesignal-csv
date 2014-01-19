//start of LzssInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssInputStream.java
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
import jp.gr.java_conf.dangan.util.lha.PreLzssDecoder;

//import exceptions
import java.io.IOException;
import java.io.EOFException;

/**
 * LZSS 圧縮されたデータを解凍しながら供給する入力ストリーム。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LzssInputStream.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [bug fix]
 *     mark() 内で接続された PreLzssDecoder の 
 *     mark に与える readLimit の計算が甘かったのを修正。
 *
 * Revision 1.0  2002/07/25 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     available() のスペルミスを修正。
 *     skip() において decode() を呼ぶ判定条件が間違っていたのを修正。
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
public class LzssInputStream extends InputStream{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private PreLzssDecoder decoder
    //------------------------------------------------------------------
    /**
     * LZSS圧縮コードを返す入力ストリーム
     */
    private PreLzssDecoder decoder;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int Threshold
    //  private int MaxMatch
    //  private long Length
    //------------------------------------------------------------------
    /**
     * LZSS圧縮に使用される閾値。
     * 一致長が この値以上であれば、圧縮コードを出力する。
     */
    private int Threshold;

    /**
     * LZSS圧縮に使用される値。
     * 最大一致長を示す。
     */
    private int MaxMatch;

    /**
     * 解凍後のデータサイズ
     */
    private long Length;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private long TextPosition
    //  private long TextDecoded
    //------------------------------------------------------------------
    /**
     * LZSS圧縮を展開するためのバッファ。
     */
    private byte[] TextBuffer;

    /**
     * 現在読み込み位置。
     * read() によって外部に読み出された位置を示す。
     */
    private long TextPosition;

    /**
     * 現在読み込み位置。
     * LZSS圧縮コードを展開して得られた位置を示す。
     */
    private long TextDecoded;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private byte[] MarkTextBuffer
    //  private long MarkTextPosition
    //  private long MarkTextDecoded
    //------------------------------------------------------------------
    /** TextBuffer のバックアップ用 */
    private byte[] MarkTextBuffer;

    /** TextPosition のバックアップ用 */
    private long MarkTextPosition;

    /** TextDecoded のバックアップ用 */
    private long MarkTextDecoded;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LzssInputStream()
    //  public LzssInputStream( PreLzssDecoder decoder )
    //  public LzssInputStream( PreLzssDecoder decoder, long length )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private LzssInputStream(){ }

    /**
     * in から LZSS圧縮データ の入力を受けて、
     * 解凍されたデータを提供する入力ストリームを構築する。
     * このコンストラクタから生成された LzssInputStreamは
     * -lh1-等の解凍データの最後のデータを読み込んだ後、
     * 次のデータの読み取りで必ずEndOfStreamに達するとは
     * 限らないデータを正常に復元できない(終端以降にゴミ
     * データがつく可能性がある)。
     * 
     * @param decoder LZSS圧縮データ供給ストリーム
     */
    public LzssInputStream( PreLzssDecoder decoder ){
        this( decoder, Long.MAX_VALUE );
    }

    /**
     * in から LZSS圧縮データ の入力を受けて、
     * 解凍されたデータを提供する入力ストリームを構築する。
     * 
     * 
     * @param decoder LZSS圧縮データ供給ストリーム
     * @param length  解凍後のサイズ
     */
    public LzssInputStream( PreLzssDecoder decoder,
                            long           length ){
        this.MaxMatch      = decoder.getMaxMatch();
        this.Threshold     = decoder.getThreshold();
        this.Length        = length;

        this.decoder        = decoder;
        this.TextBuffer     = new byte[ decoder.getDictionarySize() ];
        this.TextPosition   = 0;
        this.TextDecoded    = 0;

        if( this.decoder instanceof PreLz5Decoder )
            this.initLz5TextBuffer();
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //  public long skip( long length )
    //------------------------------------------------------------------
    /**
     * コンストラクタで指定された PreLzssDecoder の
     * 圧縮されたデータを解凍し、1バイトのデータを供給する。
     * 
     * @return 解凍された 1バイトのデータ
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read() throws IOException {
        if( this.TextDecoded <= this.TextPosition ){
            try{
                this.decode();                                                  //throws EOFException IOException
            }catch( EOFException exception ){
                if( this.TextDecoded <= this.TextPosition )
                    return -1;
            }
        }

        return this.TextBuffer[ (int)this.TextPosition++
                                 & ( this.TextBuffer.length - 1 ) ] & 0xFF;
    }

    /**
     * コンストラクタで指定された PreLzssDecoder の
     * 圧縮されたデータを解凍し、bufferを満たすように
     * 解凍されたデータを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * 
     * @return 読みこんだデータ量
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer ) throws IOException {
        return this.read( buffer, 0, buffer.length );
    }

    /**
     * コンストラクタで指定された PreLzssDecoder の
     * 圧縮されたデータを解凍し、buffer の index から
     * length バイトのデータを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * @param index  buffer 内のデータ読みこみ開始位置
     * @param length 読み込むデータ量
     * 
     * @return 読みこんだデータ量
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int read( byte[] buffer, int index, int length ) throws IOException {
        int position = index;
        int end      = index + length;
        try{
            while( position < end ){
                if( this.TextDecoded <= this.TextPosition )
                    this.decode();                                              //throws IOException

                position = this.copyTextBufferToBuffer( buffer, position, end );
            }
        }catch( EOFException exception ){
            position = this.copyTextBufferToBuffer( buffer, position, end );

            if( position == index ) return -1;
        }

        return position - index;
    }

    /**
     * 解凍されたデータを lengthバイト読み飛ばす。
     * 
     * @param length 読み飛ばすデータ量(単位はバイト)
     * 
     * @return 実際に読み飛ばしたバイト数
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {
        long end = this.TextPosition + length;
        try{
            while( this.TextPosition < end ){
                if( this.TextDecoded <= this.TextPosition )
                    this.decode();

                this.TextPosition = Math.min( end, this.TextDecoded );
            }
        }catch( EOFException exception ){
            this.TextPosition = Math.min( end, this.TextDecoded );
        }

        return length - ( end - this.TextPosition );
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
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
     * InputStream の mark() と違い、 readLimit で設定した
     * 限界バイト数より前にマーク位置が無効になる可能性がある。
     * ただし、readLimit を無視して無限に reset() 可能な 
     * InputStream と接続している場合は readLimit に
     * どのような値を設定されても
     * reset() で必ずマーク位置に復旧できる事を保証する。<br>
     * 
     * @param readLimit マーク位置に戻れる限界のバイト数。
     *                  このバイト数を超えてデータを読み
     *                  込んだ場合 reset()できなくなる可
     *                  能性がある。<br>
     * 
     * @see PreLzssDecoder#mark(int)
     */
    public void mark( int readLimit ){
        readLimit -= (int)( this.TextDecoded - this.TextPosition );
        int Size = this.TextBuffer.length - this.MaxMatch;
        readLimit = ( readLimit + Size - 1 ) / Size * Size;
        this.decoder.mark( Math.max( readLimit, 0 ) );

        if( this.MarkTextBuffer == null ){
            this.MarkTextBuffer = (byte[])this.TextBuffer.clone();
        }else{
            System.arraycopy( this.TextBuffer, 0, 
                              this.MarkTextBuffer, 0, 
                              this.TextBuffer.length );
        }
        this.MarkTextPosition = this.TextPosition;
        this.MarkTextDecoded  = this.TextDecoded;
    }

    /**
     * 接続された入力ストリームの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void reset() throws IOException {
        if( this.MarkTextBuffer == null ){
            throw new IOException( "not marked." );
        }else if( this.TextDecoded - this.MarkTextPosition 
               <= this.TextBuffer.length ){
            this.TextPosition = this.MarkTextPosition;
        }else if( this.decoder.markSupported() ){
            //reset
            this.decoder.reset();                                               //throws IOException
            System.arraycopy( this.MarkTextBuffer, 0, 
                              this.TextBuffer, 0, 
                              this.TextBuffer.length );
            this.TextPosition = this.MarkTextPosition;
            this.TextDecoded  = this.MarkTextDecoded;
        }else{
            throw new IOException( "mark/reset not supported." );
        }
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
        return  this.decoder.markSupported();
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  other methods
    //------------------------------------------------------------------
    //  public int available()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * 接続された入力ストリームからブロックしないで
     * 読み込むことのできるバイト数を得る。<br>
     * 
     * @return ブロックしないで読み出せるバイト数。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public int available() throws IOException {
        return (int)( this.TextDecoded - this.TextPosition )
               + this.decoder.available();
    }

    /**
     * この入力ストリームを閉じ、使用していた
     * 全てのリソースを開放する。<br>
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.decoder.close();
        this.decoder        = null;
        this.TextBuffer     = null;
        this.MarkTextBuffer = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void decode()
    //  private int copyTextBufferToBuffer( byte[] buffer, int position, int end )
    //  private void initLz5TextBuffer()
    //------------------------------------------------------------------
    /**
     * private変数 this.in から圧縮データを読み込み
     * 解凍しながら TextBuffer にデータを書きこむ。
     * 
     * @exception IOException  入出力エラーが発生した場合
     * @exception EOFException ストリーム終端に達した場合
     */
    private void decode() throws IOException {
        if( this.TextDecoded < this.Length ){
            final int  TextMask  = this.TextBuffer.length - 1;
            final int  TextStart = (int)this.TextDecoded & TextMask;
            int        TextPos   = TextStart;
            int        TextLimit = (int)( Math.min( this.TextPosition 
                                                         + this.TextBuffer.length 
                                                         - this.MaxMatch,
                                                     this.Length ) 
                                           - this.TextDecoded ) + TextStart;
            try{
                while( TextPos < TextLimit ){
                    int Code = this.decoder.readCode();                             //throws EOFException IOException

                    if( Code < 0x100 ){
                        this.TextBuffer[ TextMask & TextPos++ ] = (byte)Code;
                    }else{
                        int MatchLength   = ( Code & 0xFF ) + this.Threshold;
                        int MatchPosition = TextPos - this.decoder.readOffset() - 1;//throws IOException

                        while( 0 < MatchLength-- )
                            this.TextBuffer[ TextMask & TextPos++ ]
                                = this.TextBuffer[ TextMask & MatchPosition++ ];
                    }
                }
            }finally{
                this.TextDecoded += TextPos - TextStart;
            }
        }else{
            throw new EOFException();
        }
    }

    /**
     * private 変数 this.TextBuffer から bufferにデータを転送する。
     * 
     * @param buffer   TextBufferの内容をコピーするバッファ
     * @param position buffer内の書き込み現在位置
     * @param end      buffer内の書き込み終了位置
     * 
     * @return bufferの次に書き込みが行われるべき位置
     */
    private int copyTextBufferToBuffer( byte[] buffer, int position, int end ){
        if( ( this.TextPosition & ~( this.TextBuffer.length - 1 ) )
              < ( this.TextDecoded & ~( this.TextBuffer.length - 1 ) ) ){
            int length = Math.min( this.TextBuffer.length - 
                                     ( (int)this.TextPosition 
                                          & this.TextBuffer.length - 1 ),
                                     end - position );

            System.arraycopy( this.TextBuffer, 
                              (int)this.TextPosition
                                 & this.TextBuffer.length - 1,
                              buffer, position, length  );

            this.TextPosition += length;
            position          += length;
        }

        if( this.TextPosition < this.TextDecoded ){
            int length = Math.min( (int)( this.TextDecoded 
                                          - this.TextPosition ),
                                     end - position );

            System.arraycopy( this.TextBuffer, 
                              (int)this.TextPosition
                                 & this.TextBuffer.length - 1,
                              buffer, position, length  );

            this.TextPosition += length;
            position          += length;
        }

        return position;
    }

    /**
     * -lz5- 用に TextBuffer を初期化する。
     */
    private void initLz5TextBuffer(){
        int position = 18;
        for( int i = 0 ; i < 256 ; i++ )
            for( int j = 0 ; j < 13 ; j++ )
                this.TextBuffer[ position++ ] = (byte)i;

        for( int i = 0 ; i < 256 ; i++ )
            this.TextBuffer[ position++ ] = (byte)i;

        for( int i = 0 ; i < 256 ; i++ )
            this.TextBuffer[ position++ ] = (byte)(255 - i);

        for( int i = 0 ; i < 128 ; i++ )
            this.TextBuffer[ position++ ] = 0;

        while( position < this.TextBuffer.length )
            this.TextBuffer[ position++ ] = (byte)' ';
    }

}
//end of LzssInputStream.java
