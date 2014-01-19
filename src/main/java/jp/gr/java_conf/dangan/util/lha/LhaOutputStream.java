//start of LhaOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaOutputStream.java
 * 
 * Copyright (C) 2001-2002 Michel Ishizuka  All rights reserved.
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
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;
import jp.gr.java_conf.dangan.io.GrowthByteBuffer;
import jp.gr.java_conf.dangan.util.lha.CRC16;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaProperty;
import jp.gr.java_conf.dangan.util.lha.CompressMethod;


//import exceptions
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import java.lang.Error;


/**
 * 接続されたストリームに 圧縮データを出力するためのユーティリティクラス。<br>
 * java.util.zip.ZipOutputStream と似たインターフェイスを持つように作った。
 * Zipと違い、LHAの出力は本来 2パスであるため、1つのエントリを圧縮するまで、
 * エントリ全体のデータを持つ一時記憶領域が必要となる。
 * そのような記憶領域を使用したくない場合は LhaRetainedOutputStream か
 * LhaImmediateOutputStream を使用する事。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaOutputStream.java,v $
 * Revision 1.1.2.2  2005/05/03 07:48:40  dangan
 * [bug fix]
 *     圧縮法識別子 -lhd- を指定した時、圧縮後サイズがオリジナルサイズを下回らないため、
 *     必ず -lh0- に再設定されていた。そのためディレクトリ情報を格納できなかった。
 *
 * Revision 1.1.2.1  2005/04/29 02:14:28  dangan
 * [bug fix]
 *     圧縮法識別子 -lhd- を指定した時、圧縮後サイズがオリジナルサイズを下回らないため、
 *     必ず -lh0- に再設定されていた。そのためディレクトリ情報を格納できなかった。
 *
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [maintenance]
 *     LhaConstants から CompressMethod へのクラス名の変更に合わせて修正。
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     コンストラクタから 引数に String encode を取るものを廃止、
 *     Properties を引数に取るものを追加。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1.2.2 $
 */
public class LhaOutputStream extends OutputStream{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private OutputStream out
    //------------------------------------------------------------------
    /**
     * 圧縮データを出力するストリーム
     */
    private OutputStream out;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  to compress a file
    //------------------------------------------------------------------
    //  private CRC16 crc
    //  private Temporary temp
    //  private LhaHeader header
    //  private OutputStream tempOut
    //  private long length
    //------------------------------------------------------------------
    /**
     * CRC16値算出用クラス
     */
    private CRC16 crc;

    /**
     * 一時記憶用オブジェクト
     */
    private Temporary temp;

    /**
     * 現在圧縮中のエントリのヘッダ
     */
    private LhaHeader header;

    /**
     * 現在圧縮中のエントリの圧縮用出力ストリーム
     */
    private OutputStream tempOut;

    /**
     * 現在圧縮中エントリの圧縮前のデータの長さ
     */
    private long length;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  property
    //------------------------------------------------------------------
    //  private Properties property
    //------------------------------------------------------------------
    /**
     * 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     */
    private Properties property;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LhaOutputStream()
    //  public LhaOutputStream( OutputStream out )
    //  public LhaOutputStream( OutputStream out, Properties property )
    //  public LhaOutputStream( OutputStream out, RandomAccessFile file )
    //  public LhaOutputStream( OutputStream out, RandomAccessFile file,
    //                          Properties property )
    //  private void constructerHelper( OutputStream out, Temporary temp,
    //                          Properties property )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ
     * 使用不可
     */
    private LhaOutputStream(){  }

    /**
     * out に 圧縮データを出力するOutputStreamを構築する。<br>
     * 一時退避機構はメモリを使用する。このため、
     * 圧縮時データ量がメモリ量を超えるようなファイルは圧縮できない。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param out 圧縮データを出力するストリーム
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaOutputStream( OutputStream out ){

        if( out != null ){

            Properties property = LhaProperty.getProperties();
            this.constructerHelper( out, new TemporaryBuffer(), property );         //throws UnsupportedEncodingException

        }else{
            throw new NullPointerException( "out" );
        }
    }

    /**
     * out に 圧縮データを出力するOutputStreamを構築する。<br>
     * 一時退避機構はメモリを使用する。このため、
     * 圧縮時データ量がメモリ量を超えるようなファイルは圧縮できない。<br>
     * 
     * @param out      圧縮データを出力するストリーム
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     * 
     * @see LhaProperty
     */
    public LhaOutputStream( OutputStream out, Properties property ){

        if( out      != null
         && property != null ){

            this.constructerHelper( out, new TemporaryBuffer(), property );         //throws UnsupportedEncodingException

        }else if( out == null ){
            throw new NullPointerException( "out" );
        }else{
            throw new NullPointerException( "property" );
        }
    }

    /**
     * out に 圧縮データを出力するOutputStreamを構築する。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param out   圧縮データを出力するストリーム
     * @param file  RandomAccessFile のインスタンス。<br>
     *          <ul>
     *            <li>既に close() されていない事。
     *            <li>コンストラクタの mode には "rw" オプションを使用して、
     *                読みこみと書きこみが出来るように生成されたインスタンスであること。
     *          </ul>
     *          の条件を満たすもの。
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaOutputStream( OutputStream out, RandomAccessFile file ){

        if( out      != null
         && file     != null ){

            Properties property = LhaProperty.getProperties();
            this.constructerHelper( out, new TemporaryFile( file ), property ); //throws UnsupportedEncodingException

        }else if( out == null ){
            throw new NullPointerException( "out" );
        }else{
            throw new NullPointerException( "file" );
        }
    }

    /**
     * out に 圧縮データを出力するOutputStreamを構築する。<br>
     * 
     * @param out      圧縮データを出力するストリーム
     * @param file     RandomAccessFile のインスタンス。<br>
     *            <ul>
     *              <li>既に close() されていない事。
     *              <li>コンストラクタの mode には "rw" オプションを使用して、
     *                  読みこみと書きこみが出来るように生成されたインスタンスであること。
     *            </ul>
     *            の条件を満たすもの。
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     * 
     * @exception UnsupportedEncodingException
     *               encode がサポートされない場合
     * 
     * @see LhaProperty
     */
    public LhaOutputStream( OutputStream     out, 
                            RandomAccessFile file, 
                            Properties       property ){

        if( out      != null
         && file     != null 
         && property != null ){

            this.constructerHelper( out, new TemporaryFile( file ), property );     //throws UnsupportedEncodingException

        }else if( out == null ){
            throw new NullPointerException( "out" );
        }else if( file == null ){
            throw new NullPointerException( "file" );
        }else{
            throw new NullPointerException( "property" );
        }
    }

    /**
     * コンストラクタの初期化処理を担当するメソッド。
     * 
     * @param out    LHA書庫形式のデータを出力する出力ストリーム
     * @param temp   圧縮データの一時退避機構
     * @param encode ヘッダ内の文字列を変換するのに使用する
     *               エンコード日本では シフトJIS(SJIS,MS932,
     *               CP932等)を使用する事
     * 
     * @exception UnsupportedEncodingException
     *               encode がサポートされない場合
     */
    private void constructerHelper( OutputStream out,
                                    Temporary    temp,
                                    Properties   property ){
        this.out      = out;
        this.temp     = temp;
        this.property = property;

        this.crc     = new CRC16();
        this.header  = null;
        this.tempOut = null;
    }

    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  write
    //------------------------------------------------------------------
    //  public void write( int data )
    //  public void write( byte[] buffer )
    //  public void write( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * 現在のエントリに1バイトのデータを書きこむ。<br>
     * 
     * @param data 書きこむデータ
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( int data ) throws IOException {
        if( this.tempOut != null ){
            if( this.header != null ){
                crc.update( data );
            }

            this.tempOut.write( data );
            this.length++;
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 現在のエントリに bufferの内容を全て書き出す。<br>
     * 
     * @param buffer 書き出すデータの入ったバイト配列
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );
    }

    /**
     * 現在のエントリに bufferの indexから lengthバイトのデータを書き出す。<br>
     * 
     * @param buffer 書き出すデータの入ったバイト配列
     * @param index  buffer内の書き出すべきデータの開始位置
     * @param length データのバイト数
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( byte[] buffer, int index, int length ) throws IOException {
        if( this.tempOut != null ){
            if( this.header != null ){
                crc.update( buffer, index, length );
            }

            this.tempOut.write( buffer, index, length );
            this.length += length;
        }else{
            throw new IOException( "no entry" );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.OutputStream
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void flush()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * flush は二つの動作を行う。
     * 一つは現在書き込み中のエントリのデータを 
     * 一時退避機構に送りこむように指示する。
     * これは PostLzssDecoder、LzssOutputStream 
     * の規約どおり flush() しなかった場合と
     * 同じデータが出力される事を保証しない。
     * もう一つは 実際の出力先を flush() する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     *
     * @see PostLzssEncoder#flush()
     * @see LzssOutputStream#flush()
     */
    public void flush() throws IOException {
        if( this.tempOut != null ){
            this.tempOut.flush();                                               //throws IOException
        }

        if( this.tempOut != this.out ){
            this.out.flush();                                                   //throws IOException
        }
    }

    /**
     * 出力先に全てのデータを出力し、
     * ストリームを閉じる。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        if( this.tempOut != null ){
            this.closeEntry();                                                  //throws IOException
        }

        //ターミネータを出力
        this.out.write( 0 );                                                    //throws IOException
        this.out.close();                                                       //throws IOException
        this.out  = null;

        this.temp.close();
        this.temp = null;

        this.property = null;
        this.crc      = null;
        this.header   = null;
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipOutputStream  )
    //------------------------------------------------------------------
    //  manipulate entry
    //------------------------------------------------------------------
    //  public void putNextEntry( LhaHeader header )
    //  public void putNextEntryAlreadyCompressed( LhaHeader header )
    //  public void putNextEntryNotYetCompressed( LhaHeader header )
    //  public void closeEntry()
    //------------------------------------------------------------------
    /**
     * 新しいエントリを書き込むようにストリームを設定する。<br>
     * このメソッドは 既に圧縮済みのエントリの場合は
     * putNextEntryAlreadyCompressed(),
     * 未だに圧縮されていない場合は
     * putNextEntryNotYetCompressed() を呼び出す。<br>
     * 圧縮されているかの判定は、
     * <ul>
     *   <li>header.getCompressedSize()<br>
     *   <li>header.getOriginalSize()<br>
     *   <li>header.getCRC()<br>
     * </ul>
     * のどれか一つでも LhaHeader.UNKNOWN であれば未だに圧縮されていないとする。
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void putNextEntry( LhaHeader header ) throws IOException {
        if( header.getCompressedSize() == LhaHeader.UNKNOWN
         || header.getOriginalSize()   == LhaHeader.UNKNOWN
         || header.getCRC()            == LhaHeader.UNKNOWN ){
            this.putNextEntryNotYetCompressed( header );                        //throws IOException
        }else{
            this.putNextEntryAlreadyCompressed( header );                       //throws IOException
        }
    }

    /**
     * 既に圧縮済みのエントリを書きこむようにストリームを設定する。<br>
     * 圧縮済みなので、一時退避機構を経ずに直接出力先に出力される。
     * 圧縮済みデータが正しい事は、呼び出し側が保証する事。
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     * @exception IllegalArgumentException
     *               <ol>
     *                  <li>header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
     *                  <li>header.getComressedSize() が LhaHeader.UNKNOWN を返す場合
     *                  <li>header.getCRC() が LhaHeader.UNKNOWN を返す場合
     *               </ol>
     *               の何れか。
     */
    public void putNextEntryAlreadyCompressed( LhaHeader header ) throws IOException {
        if( header.getOriginalSize()   != LhaHeader.UNKNOWN
         && header.getCompressedSize() != LhaHeader.UNKNOWN
         && header.getCRC()            != LhaHeader.UNKNOWN ){

            if( this.tempOut != null ){
                this.closeEntry();                                              //throws IOException
            }

            String encoding = this.property.getProperty( "lha.encoding" );
            if( encoding == null ){
                encoding = LhaProperty.getProperty( "lha.encoding" );
            }
            this.out.write( header.getBytes( encoding ) );                      //throws IOException
            this.tempOut = out;


        }else if( header.getOriginalSize() == LhaHeader.UNKNOWN ){
            throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
        }else if( header.getCompressedSize() == LhaHeader.UNKNOWN ){
            throw new IllegalArgumentException( "CompressedSize must not \"LhaHeader.UNKNOWN\"." );
        }else{
            throw new IllegalArgumentException( "CRC must not \"LhaHeader.UNKNOWN\"." );
        }
    }

    /**
     * 未だに圧縮されていないエントリを書きこむようにストリームを設定する。<br>
     * header に OriginalSize, CompressedSize, CRCが指定されていても無視される。
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void putNextEntryNotYetCompressed( LhaHeader header ) throws IOException {
        if( this.tempOut != null ){
            this.closeEntry();                                                  //throws IOException
        }

        this.crc.reset();
        this.length  = 0;
        this.header  = (LhaHeader)header.clone();
        this.tempOut = CompressMethod.connectEncoder( this.temp.getOutputStream(), 
                                                      header.getCompressMethod(), 
                                                      this.property  );
    }

    /**
     * 現在出力中のエントリを閉じ、次のエントリが出力可能な状態にする。
     * 圧縮に失敗した(圧縮後サイズが圧縮前サイズを上回った)場合、
     * 解凍し無圧縮で格納する。エントリのサイズが大きい場合、
     * この処理にはかなりの時間がかかる。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void closeEntry() throws IOException {
        if( this.header != null ){
            this.tempOut.close();
            InputStream in;

            if( this.temp.length() < this.length ){
                this.header.setOriginalSize( this.length );
                this.header.setCompressedSize( this.temp.length() );
                this.header.setCRC( (int)crc.getValue() );

                in = this.temp.getInputStream();                                //throws IOException
            }else{
                String method = this.header.getCompressMethod();

                this.header.setOriginalSize( this.length );
                this.header.setCompressedSize( this.length );
                this.header.setCRC( (int)crc.getValue() );
                if( !this.header.getCompressMethod().equalsIgnoreCase( CompressMethod.LHD ) ){
                    this.header.setCompressMethod( CompressMethod.LH0 );
                }

                in = this.temp.getInputStream();                                //throws IOException
                in = CompressMethod.connectDecoder( in, 
                                                    method, 
                                                    this.property,
                                                    this.temp.length() );
            }

            String encoding = this.property.getProperty( "lha.encoding" );
            if( encoding == null ){
                encoding = LhaProperty.getProperty( "lha.encoding" );
            }
            this.out.write( this.header.getBytes( encoding ) );                 //throws UnsupportedEncodingException, IOException

            byte[] buffer = new byte[ 8192 ];
            int length;
            while( 0 <= ( length = in.read( buffer ) ) ){                       //throws IOException
                this.out.write( buffer, 0, length );                            //throws IOException
            }
        }
        this.header  = null;
        this.tempOut = null;
    }


    //------------------------------------------------------------------
    //  inner class
    //------------------------------------------------------------------
    //  private static interface Temporary
    //  private static class TemporaryFile
    //  private static class TemporaryBuffer
    //------------------------------------------------------------------
    /**
     * データの一時退避機構を提供する。
     */
    private static interface Temporary{

        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public abstract InputStream getInputStream()
        //  public abstract OutputStream getOutputStream()
        //  public abstract long length()
        //  public abstract void close()
        //------------------------------------------------------------------
        /**
         * 一時退避機構に貯えられたデータを取り出すInputStream を得る。<br>
         * このデータは直前の getOutputStream() で与えられる 
         * OutputStream に出力されたデータと同じである。<br>
         * getInputStream() で得られた InputStream が close() されるまで、
         * getOutputStream() を呼んではならない。<br>
         * また、getInputStream() で得られた InputStream が close() されるまで、
         * 再び getInputStream() を呼んではならない。<br>
         * 
         * @return 一時退避機構からデータを取り出す InputStream
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public abstract InputStream getInputStream() throws IOException;

        /**
         * データを一時退避機構に貯えるOutputStream を得る。<br>
         * 貯えたデータは直後の getInputStream() で得られる
         * InputStream から得る事が出来る。<br>
         * getOutputStream で得られた OutputStream が close() されるまで、
         * getInputStream() を呼んではならない。
         * また、getOutputStream() で得られた OutputStream が close() されるまで、
         * 再び getOutputStream() を呼んではならない。<br>
         * 
         * @return データを一時退避機構に貯える OutputStream
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public abstract OutputStream getOutputStream() throws IOException;

        /**
         * 一時退避機構に格納されているデータ量を得る。
         * これは 直前の getOutputStream() で与えられた
         * OutputStream に出力されたデータ量と同じである。
         *
         * @return 一時退避機構に格納されているデータ量
         */
        public abstract long length() throws IOException;

        /**
         * 一時退避機構で使用されていた、全てのシステムリソースを開放する。
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public abstract void close() throws IOException ;

    }

    /**
     * 一時退避機構に RandomAccessFile を使用するクラス。
     */
    private static class TemporaryFile implements Temporary{

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private RandomAccessFile tempfile
        //  private long length
        //------------------------------------------------------------------
        /**
         * 一時退避機構に使用する RandomAccessFile
         */
        private RandomAccessFile tempfile;

        /**
         * getOutputStream で与えた OutputStream に出力されたデータ量を保持する。
         */
        private long length;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public TemporaryFile( RandomAccessFile file )
        //------------------------------------------------------------------
        /**
         * コンストラクタ fileを使用して TemporaryFile を構築する。
         * 
         * @param file RandomAccessFile のインスタンス
         */
        public TemporaryFile( RandomAccessFile file ){
            if( file != null ){ 
                this.tempfile = file;
            }else{ 
                throw new NullPointerException( "file" );
            }
        }

        //------------------------------------------------------------------
        //  method of Temporary
        //------------------------------------------------------------------
        //  public InputStream getInputStream()
        //  public OutputStream getOutputStream()
        //  public long length()
        //  public void close()
        //------------------------------------------------------------------
        /**
         * 一時退避機構に貯えられたデータを取り出す InputStream を得る。<br>
         * このデータは直前の getOutputStream() で与えられる 
         * OutputStream に出力されたデータと同じ。<br>
         * 
         * @return 一時退避機構からデータを取り出す InputStream
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public InputStream getInputStream() throws IOException {
            return new TemporaryFileInputStream();
        }

        /**
         * データを一時退避機構に貯えるOutputStreamを得る。<br>
         * 貯えたデータは直後の getInputStream() で
         * 得られる InputStream から得る事が出来る。<br>
         * 
         * @return データを一時退避機構に貯える OutputStream
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public OutputStream getOutputStream() throws IOException {
            return new TemporaryFileOutputStream();
        }

        /**
         * 一時退避機構に格納されているデータ量を得る。<br>
         * これは 直前の getOutputStream() で与えられた
         * OutputStream に出力されたデータ量と同じである。<br>
         *
         * @return 一時退避機構に格納されているデータ量
         */
        public long length(){
            return this.length;
        }

        /**
         * 一時退避機構で使用されていた、全てのシステムリソースを開放する。
         * コンストラクタで与えられた RandomAccessFile は閉じられる。
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public void close() throws IOException {
            this.tempfile.close(); //throws IOException
            this.tempfile = null;
        }

        //------------------------------------------------------------------
        //  inner classes
        //------------------------------------------------------------------
        //  private class TemporaryFileInputStream
        //  private class TemporaryFileOutputStream
        //------------------------------------------------------------------
        /**
         * TemporaryFile の入力ストリーム
         */
        private class TemporaryFileInputStream extends InputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryFileInputStream()
            //------------------------------------------------------------------
            /**
             * TemporaryFile からデータを読み込む InputStream を構築する。<br>
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public TemporaryFileInputStream() throws IOException {
                TemporaryFile.this.tempfile.seek( 0 );                          //throws IOException
            }

            //------------------------------------------------------------------
            //  method of java.io.InputStream
            //------------------------------------------------------------------
            //  public int read()
            //  public int read( byte[] buffer )
            //  public int read( byte[] buffer, int index, int length )
            //------------------------------------------------------------------
            /**
             * TemporaryFileから 1バイトのデータを読み込む。
             * 
             * @return 読みこまれた1バイトのデータ
             *         既にEndOfStreamに達している場合は-1
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public int read() throws IOException {
                long pos   = TemporaryFile.this.tempfile.getFilePointer();      //throws IOException
                long limit = TemporaryFile.this.length;

                if( pos < limit ){
                    return TemporaryFile.this.tempfile.read();                  //throws IOException
                }else{
                    return -1;
                }
            }

            /**
             * TemporaryFileから bufferを満たすようにデータを読み込む。
             *
             * @param buffer データを読み込むバッファ
             * 
             * @return 読みこまれたデータ量
             *         既にEndOfStreamに達している場合は-1
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public int read( byte[] buffer ) throws IOException {
                return this.read( buffer, 0, buffer.length );                   //throws IOException
            }

            /**
             * TemporaryFileから bufferの indexへlengthバイトのデータを読み込む
             * 
             * @param buffer データを読み込むバッファ
             * @param index  buffer内のデータ読みこみ開始位置
             * @param length 読み込むデータ量
             * 
             * @return 読みこまれたデータ量
             *         既にEndOfStreamに達している場合は-1
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public int read( byte[] buffer, int index, int length ) 
                                                            throws IOException {
                long pos   = TemporaryFile.this.tempfile.getFilePointer();      //throws IOException
                long limit = TemporaryFile.this.length;
                length = (int)( Math.min( pos + length, limit ) - pos );

                if( pos < limit ){
                    return TemporaryFile.this.tempfile.read( buffer, index, length );//throws IOException
                }else{
                    return -1;
                }
            }

        }

        /**
         * TemporaryFile の出力ストリーム
         */
        private class TemporaryFileOutputStream extends OutputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryFileOutputStream()
            //------------------------------------------------------------------
            /**
             * TemporaryFile にデータを出力する OutputStream を構築する。<br>
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public TemporaryFileOutputStream() throws IOException {
                TemporaryFile.this.tempfile.seek( 0 );                          //throws IOException
                TemporaryFile.this.length = 0;
            }

            //------------------------------------------------------------------
            //  method of java.io.OutputStream
            //------------------------------------------------------------------
            //  public void write( int data )
            //  public void write( byte[] buffer )
            //  public void write( byte[] buffer, int index, int length )
            //------------------------------------------------------------------
            /**
             * TemporaryFile に 1byteのデータを書き出す。
             * 
             * @param data 書き出す1byteのデータ
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public void write( int data ) throws IOException {
                TemporaryFile.this.tempfile.write( data );                      //throws IOException
                TemporaryFile.this.length++;
            }

            /**
             * TemporaryFile に bufferの内容を全て書き出す。
             * 
             * @param buffer 書き出すデータの入ったバイト配列
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public void write( byte[] buffer ) throws IOException {
                TemporaryFile.this.tempfile.write( buffer );                    //throws IOException
                TemporaryFile.this.length += buffer.length;
            }

            /**
             * TemporaryFile に bufferのindex からlengthバイトの内容を書き出す。
             * 
             * @param buffer 書き出すデータの入ったバイト配列
             * @param index  buffer内の書き出すデータの開始位置
             * @param length 書き出すデータ量
             * 
             * @exception IOException 入出力エラーが発生した場合
             */
            public void write( byte[] buffer, int index, int length )
                                                        throws IOException {
                TemporaryFile.this.tempfile.write( buffer, index, length );     //throws IOException
                TemporaryFile.this.length += length;
            }

        }

    }

    /**
     * 一時退避機構に GrowthByteBufferを使用するクラス
     */
    private static class TemporaryBuffer implements Temporary {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private GrowthByteBuffer tempbuffer
        //------------------------------------------------------------------
        /**
         * 一時退避機構に使用されるバッファ
         */
        private GrowthByteBuffer tempbuffer;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public TemporaryBuffer()
        //------------------------------------------------------------------
        /**
         * GrowthByteBuffer を使用した検索機構を構築する。
         */
        public TemporaryBuffer(){
            this.tempbuffer = new GrowthByteBuffer();
        }

        //------------------------------------------------------------------
        //  method of Temporary
        //------------------------------------------------------------------
        //  public InputStream getInputStream()
        //  public OutputStream getOutputStream()
        //  public long length()
        //  public void close()
        //------------------------------------------------------------------
        /**
         * 一時退避機構に貯えられたデータを取り出す InputStream を得る。<br>
         * このデータは直前の getOutputStream() で与えられる 
         * OutputStream に出力されたデータと同じ。<br>
         * 
         * @return 一時退避機構からデータを取り出す InputStream
         */
        public InputStream getInputStream(){
            return new TemporaryBufferInputStream();
        }

        /**
         * データを一時退避機構に貯える OutputStream を得る。<br>
         * 貯えたデータは直後の getInputStream() で得られる 
         * InputStream から得る事が出来る。<br>
         * 
         * @return データを一時退避機構に貯える OutputStream
         */
        public OutputStream getOutputStream(){
            return new TemporaryBufferOutputStream();
        }

        /**
         * 一時退避機構に格納されているデータ量を得る。<br>
         * これは 直前の getOutputStream() で与えた
         * OutputStream に出力されたデータ量と同じである。
         *
         * @return 一時退避機構に格納されているデータ量
         */
        public long length(){
            return this.tempbuffer.length();
        }

        /**
         * 一時退避機構で使用されていた、全てのシステムリソースを開放する。
         */
        public void close(){
            this.tempbuffer = null;
        }

        //------------------------------------------------------------------
        //  inner classes
        //------------------------------------------------------------------
        //  private class TemporaryBufferInputStream
        //  private class TemporaryBufferOutputStream
        //------------------------------------------------------------------
        /**
         * TemporaryBuffer の入力ストリーム
         */
        private class TemporaryBufferInputStream extends InputStream{

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryBufferInputStream()
            //------------------------------------------------------------------
            /**
             * TemporaryBuffer からデータを読み込む InputStream を構築する。<br>
             */
            public TemporaryBufferInputStream(){
                TemporaryBuffer.this.tempbuffer.seek( 0 );
            }

            //------------------------------------------------------------------
            //  method of java.io.InputStream
            //------------------------------------------------------------------
            //  public int read()
            //  public int read( byte[] buffer )
            //  public int read( byte[] buffer, int index, int length )
            //------------------------------------------------------------------
            /**
             * TemporaryBuffer から 1バイトのデータを読み込む。
             * 
             * @return 読みこまれた1バイトのデータ
             *         既にEndOfStreamに達している場合は-1
             */
            public int read(){
                return TemporaryBuffer.this.tempbuffer.read();
            }

            /**
             * TemporaryBuffer から bufferを満たすようにデータを読み込む。
             *
             * @param buffer データを読み込むバッファ
             * 
             * @return 読みこまれたデータ量
             *         既にEndOfStreamに達している場合は-1
             */
            public int read( byte[] buffer ){
                return TemporaryBuffer.this.tempbuffer.read( buffer );
            }

            /**
             * TemporaryBuffer から bufferの indexへ lengthバイトのデータを読み込む
             * 
             * @param buffer データを読み込むバッファ
             * @param index  buffer内のデータ読みこみ開始位置
             * @param length 読み込むデータ量
             * 
             * @return 読みこまれたデータ量
             *         既にEndOfStreamに達している場合は-1
             */
            public int read( byte[] buffer, int index, int length ){
                return TemporaryBuffer.this.tempbuffer.read( buffer, index, length );
            }

        }

        /**
         * TemporaryBuffer の出力ストリーム
         */
        private class TemporaryBufferOutputStream extends OutputStream {

            //------------------------------------------------------------------
            //  constructor
            //------------------------------------------------------------------
            //  public TemporaryBufferOutputStream()
            //------------------------------------------------------------------
            /**
             * TemporaryBuffer にデータを出力する OutputStream を構築する。<br>
             */
            public TemporaryBufferOutputStream(){
                TemporaryBuffer.this.tempbuffer.seek( 0 );
                TemporaryBuffer.this.tempbuffer.setLength( 0 );
            }

            //------------------------------------------------------------------
            //  method of java.io.OutputStream
            //------------------------------------------------------------------
            //  public void write( int data )
            //  public void write( byte[] buffer )
            //  public void write( byte[] buffer, int index, int length )
            //------------------------------------------------------------------
            /**
             * TemporaryBuffer に 1byteのデータを書き出す。
             * 
             * @param data 書き出す1byteのデータ
             */
            public void write( int data ){
                TemporaryBuffer.this.tempbuffer.write( data );
            }

            /**
             * TemporaryBuffer に bufferの内容を全て書き出す。
             * 
             * @param buffer 書き出すデータの入ったバイト配列
             */
            public void write( byte[] buffer ){
                TemporaryBuffer.this.tempbuffer.write( buffer );
            }

            /**
             * TemporaryBuffer に bufferのindex から lengthバイトの内容を書き出す。
             * 
             * @param buffer 書き出すデータの入ったバイト配列
             * @param index  buffer内の書き出すデータの開始位置
             * @param length 書き出すデータ量
             */
            public void write( byte[] buffer, int index, int length ){
                TemporaryBuffer.this.tempbuffer.write( buffer, index, length );
            }

        }

    }

}
//end of LhaOutputStream.java
