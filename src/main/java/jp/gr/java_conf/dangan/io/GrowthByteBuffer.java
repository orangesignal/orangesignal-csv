//start of GrowthByteBuffer.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * GrowthByteBuffer.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions
import java.lang.IllegalArgumentException;


/**
 * 自動的に伸張するバッファ。<br>
 * RandomAccessFile の メモリ版として使用する。
 * ただし、あまり巨大なデータを取り扱うのには向かない。
 * スレッドセーフではない。
 * jdk1.4 以降の ByteBufferとは互換性が無い。
 * 
 * <pre>
 * -- revision history --
 * $Log: GrowthByteBuffer.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     grow() でバッファの増加量の計算が間違っていたのを修正。
 * [change]
 *     読み込み限界に達した後の read( new byte[0] ) や 
 *     read( byte[] buf, int off, 0 ) の戻り値を
 *     InputStream と同じく 0 になるようにした
 * [maintenance]
 *     ソース整備
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class GrowthByteBuffer{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  default
    //------------------------------------------------------------------
    //  private static final int DefaultBufferSize
    //------------------------------------------------------------------
    /**
     * デフォルトの一つのバッファのサイズ
     */
    private static final int DefaultBufferSize = 16384;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  byte buffer
    //------------------------------------------------------------------
    //  private byte[][] buffer
    //  private int position
    //  private int limit
    //------------------------------------------------------------------
    /**
     * バッファ
     * 全て buffer[0].length と同じサイズのbyte配列の配列。
     */
    private byte[][] buffer;

    /**
     * 現在処理位置。
     * position は limit以降になる可能性もある。
     */
    private int position;

    /**
     * 現在読みこみ限界。
     * これ以降のデータは不定。
     * この位置のデータは読めることに注意すること。
     */
    private int limit;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  public GrowthByteBuffer()
    //  public GrouthByteBuffer( int BufferSize )
    //------------------------------------------------------------------
    /**
     * サイズを自動で伸張するバッファを構築する。<br>
     * バッファサイズにはデフォルト値が使用される。
     */
    public GrowthByteBuffer(){
        this( GrowthByteBuffer.DefaultBufferSize );
    }

    /**
     * サイズを自動で伸張するバッファを構築する。<br>
     *
     * @param BufferSize バッファのサイズ
     */
    public GrowthByteBuffer( int BufferSize ){
        if( 0 < BufferSize ){
            this.buffer    = new byte[16][];
            this.buffer[0] = new byte[ BufferSize ];
            this.position  = 0;
            this.limit     = -1;
        }else{
            throw new IllegalArgumentException( "BufferSize most be 1 or more." );
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * 現在位置に 1バイトのデータを書きこむ。
     * 
     * @param data 1バイトのデータ
     */
    public void write( int data ){
        this.grow( this.position );

        this.buffer[ this.position / this.buffer[0].length ]
                   [ this.position % this.buffer[0].length ]
            = (byte)data;

        this.position++;
    }

    /**
     * 現在位置に buffer の内容を書きこむ。
     * 
     * @param buffer 書きこむデータほ格納されたバッファ
     */
    public void write( byte[] buffer ){
        this.write( buffer, 0, buffer.length );
    }

    /**
     * 現在位置に buffer の indexからlengthバイトの内容を書きこむ。
     * 
     * @param buffer 書きこむデータほ格納されたバッファ
     * @param index  buffer内の書きこむデータの開始位置
     * @param length 書きこむデータ量
     */
    public void write( byte[] buffer, int index, int length ){
        this.grow( this.position + length - 1 );

        while( 0 < length ){
            int copylen = Math.min( ( this.position / this.buffer[0].length + 1 )
                                         * this.buffer[0].length,
                                    this.position + length ) - this.position;

            System.arraycopy( buffer, index, 
                              this.buffer[ this.position / this.buffer[0].length ], 
                                           this.position % this.buffer[0].length,
                              copylen );

            this.position += copylen;
            index         += copylen;
            length        -= copylen;
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  read
    //------------------------------------------------------------------
    //  public int read()
    //  public int read( byte[] buffer )
    //  public int read( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * 現在位置から 1byteのデータを読みこむ。
     * 
     * @return 読みこまれた1byteのデータ。<br>
     *         読みこみ限界を超えて読もうとした場合は -1
     */
    public int read(){
        if( this.position <= this.limit ){
            return this.buffer[ this.position / this.buffer[0].length ]
                              [ this.position++ % this.buffer[0].length ] & 0xFF;
        }else{
            return -1;
        }
    }

    /**
     * 現在位置から bufferを満たすようにデータを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * 
     * @return 実際に読みこまれたデータ量<br>
     *         読みこみ限界を超えて読もうとした場合は -1
     */
    public int read( byte[] buffer ){
        return this.read( buffer, 0, buffer.length );
    }

    /**
     * 現在位置から buffer のindexへ lengthのデータを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * @param index  buffer内データ読みこみ位置
     * @param length 読み込むデータの量
     * 
     * @return 実際に読みこまれたデータ量<br>
     *         読みこみ限界を超えて読もうとした場合は -1
     */
    public int read( byte[] buffer, int index, int length ){
        if( this.position <= this.limit ){
            int len = 0;
            while( 0 < length ){
                int copylen = Math.min( Math.min( ( this.position / this.buffer[0].length + 1 )
                                                  * this.buffer[0].length,
                                                  this.position + length ),
                                        this.limit + 1 ) - this.position;
                if( 0 < copylen ){
                    System.arraycopy( this.buffer[ this.position / this.buffer[0].length ], 
                                                   this.position % this.buffer[0].length,
                                      buffer, index, 
                                     copylen );

                    this.position += copylen;
                    index         += copylen;
                    len           += copylen;
                    length        -= copylen;
                }else{
                    break;
                }
            }
            return len;
        }else if( 0 < length ){
            return -1;
        }else{
            return 0;
        }
    }


    //------------------------------------------------------------------
    //  original methods
    //------------------------------------------------------------------
    //  access methods
    //------------------------------------------------------------------
    //  public int length()
    //  public void setLength( int length )
    //  public int position()
    //  public void setPosition( int position )
    //  public void seek( int position )
    //------------------------------------------------------------------
    /**
     * 現在の読みこみ限界を得る。
     * 
     * @return 現在の読みこみ限界
     */
    public int length(){
        return this.limit + 1;
    }

    /**
     * 読みこみ限界位置を設定する。
     * 
     * @param 新しい読みこみ限界位置
     */
    public void setLength( int length ){
        length--;
        if( this.limit < length ){
            this.grow( length );
        }else{
            this.limit = length;
        }
    }

    /**
     * 現在位置を得る。
     * 
     * @return 現在位置
     */
    public int position(){
        return this.position;
    }

    /**
     * 現在位置を設定する。
     * java.io.RandomAccessFileと同じく 
     * setPosition で読みこみ限界を超えた値を
     * 設定した直後にはバッファは増加していない。
     * その後 write によって書きこんだ時にはじ
     * めてバッファは増加する。
     * 
     * @param position 新しい現在位置
     */
    public void setPosition( int position ){
        this.position = position;
    }

    /**
     * 現在位置を設定する。
     * java.io.RandomAccessFileと同じく 
     * seek で読みこみ限界を超えた値を
     * 設定した直後にはバッファは増加していない。
     * その後 write によって書きこんだ時にはじ
     * めてバッファは増加する。
     * 
     * @param position 新しい現在位置
     */
    public void seek( int position ){
        this.setPosition( position );
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void grow( int limit )
    //------------------------------------------------------------------
    /**
     * 新しい読みこみ限界 limit を設定し、
     * limit まで バッファを増加させる。
     * 
     * @param 新しい読みこみ限界
     */
    private void grow( int limit ){
        if( this.limit < limit ){
            int last = 0;
            while( last < this.buffer.length 
                && this.buffer[last] != null )
                last++;

            limit++;
            if( last * this.buffer[0].length < limit ){
                int need = ( limit / this.buffer[0].length )
                         + ( limit % this.buffer[0].length == 0 ? 0 : 1 );

                if( this.buffer.length < need ){
                    byte[][] old = this.buffer;
                    this.buffer = new byte[ Math.max( old.length * 2, need ) ][];

                    for( int i = 0 ; i < last ; i++ )
                        this.buffer[i] = old[i];
                }
                for( int i = last ; i < need ; i++ )
                    this.buffer[ i ] = new byte[ this.buffer[0].length ];
            }

            this.limit = limit - 1;
        }
    }

}
//end of GrowthByteBuffer.java
