//start of LhaImmediateOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaImmediateOutputStream.java
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
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.Math;
import java.util.Properties;
import jp.gr.java_conf.dangan.util.lha.CRC16;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaProperty;
import jp.gr.java_conf.dangan.util.lha.CompressMethod;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.SecurityException;
import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;

import java.lang.NoSuchMethodError;


/**
 * 接続されたRandomAccessFileに 圧縮データを出力するためのユーティリティクラス。<br>
 * java.util.zip.ZipOutputStream と似たインターフェイスを持つように作った。<br>
 * 圧縮失敗時( 圧縮後サイズが圧縮前サイズを上回った場合 )の処理を
 * 手動で行わなければならない。
 * 以下に そのようなコードを示す。
 * <pre>
 * LhaCompressFiles( String arcfile, File[] files ){
 *   LhaImmediateOutputStream lio = new LhaImmediateOutputStream( arcfile );
 * 
 *   for( int i = 0 ; i &lt files.length ; i++ ){
 *     RandomAccessFile raf = new RandomAccessFile( files[i] );
 *     LhaHeader header = new LhaHeader( files[i].getName() );
 *     header.setLastModified( new Date( files.lastModified() ) );
 *     header.setOriginalSize( files.length() );
 *     byte[] buffer  = new byte[8192];
 *     int    length;
 * 
 *     while( 0 &lt= ( length = raf.read( buffer ) ) ){
 *         lio.write( buffer, 0, length );
 *     }
 * <strong>
 *     if( !lio.closeEntry() ){
 *       header.setCompressMethod( CompressMethod.LH0 );
 *       lio.putNextEntry( lhaheader );
 *       raf.seek( 0 );
 *       while( 0 &lt= ( length = raf.read( buffer ) ) ){
 *           lio.write( buffer, 0, length );
 *       }
 *       lio.closeEntry();
 *     }
 * </strong>
 *   lio.close();
 * }
 * </pre>
 * 進捗報告を実装する場合、このような処理をクラス内に隠蔽すると進捗報告は何秒間か
 * 時によっては何十分も応答しなくなる。(例えばギガバイト級のデータを扱った場合)
 * LhaRetainedOutputStream で発生する、このような事態を避けるために設計されている。<br>
 * また、JDK 1.1 以前では RandomAccessFile が setLength を持たないため、
 * 書庫データの後ろに他のデータがある場合でもファイルサイズを切り詰めることが出来ない。<br>
 * この問題点は常にサイズ0の新しいファイルを開く事によって回避する事ができる。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaImmediateOutputStream.java,v $
 * Revision 1.2  2002/12/11 02:25:06  dangan
 * [bug fix]
 *     jdk1.2 でコンパイルできなかった箇所を修正。
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
 * @version $Revision: 1.2 $
 */
public class LhaImmediateOutputStream extends OutputStream{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  sink
    //------------------------------------------------------------------
    //  private RandomAccessFile archive
    //------------------------------------------------------------------
    /**
     * 書庫ファイル
     */
    private RandomAccessFile archive;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  to compress a file
    //------------------------------------------------------------------
    //  private OutputStream out
    //  private LhaHeader header
    //  private String encoding
    //  private long headerpos
    //  private CRC16 crc
    //------------------------------------------------------------------
    /**
     * 圧縮用出力ストリーム
     */
    private OutputStream out;

    /**
     * 現在圧縮中のヘッダ
     */
    private LhaHeader header;

    /**
     * ヘッダの出力に使用したエンコーディング
     */
    private String encoding;

    /**
     * ヘッダ位置
     */
    private long headerpos;

    /**
     * CRC値算出用
     */
    private CRC16 crc;


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
    //  private LhaImmediateOutputStream()
    //  public LhaImmediateOutputStream( String filename )
    //  public LhaImmediateOutputStream( String filename, Properties property )
    //  public LhaImmediateOutputStream( File file )
    //  public LhaImmediateOutputStream( File file, Properties property )
    //  public LhaImmediateOutputStream( RandomAccessFile archive )
    //  public LhaImmediateOutputStream( RandomAccessFile archive, Properties property )
    //  private void constructerHelper( RandomAccesFile archive, Properties property )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ
     * 使用不可
     */
    private LhaImmediateOutputStream(){ }

    /**
     * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param filename 圧縮データを書きこむファイルの名前
     * 
     * @exception FileNotFoundException
     *               filename で与えられたファイルが見つからない場合。
     * @exception SecurityException
     *               セキュリティマネージャがファイルへのアクセスを許さない場合。
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaImmediateOutputStream( String filename ) 
                                                throws FileNotFoundException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException
            Properties property   = LhaProperty.getProperties();
        
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
        }
    }

    /**
     * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
     * 
     * @param filename 圧縮データを書きこむファイルの名前
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     * 
     * @exception FileNotFoundException
     *               filename で与えられたファイルが見つからない場合。
     * @exception SecurityException
     *               セキュリティマネージャがファイルへのアクセスを許さない場合。
     * 
     * @see LhaProperty
     */
    public LhaImmediateOutputStream( String filename, Properties property )
                                                  throws FileNotFoundException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
        }
    }

    /**
     * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param filename 圧縮データを書きこむファイルの名前
     * 
     * @exception FileNotFoundException
     *               filename で与えられたファイルが見つからない場合。
     * @exception SecurityException
     *               セキュリティマネージャがファイルへのアクセスを許さない場合。
     * @exception IOException
     *               JDK1.2 でコンパイルするためだけに存在する。
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaImmediateOutputStream( File filename ) throws IOException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException, IOException(jdk1.2)
            Properties property   = LhaProperty.getProperties();
        
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
        }
    }

    /**
     * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
     * 
     * @param filename 圧縮データを書きこむファイルの名前
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     * 
     * @exception FileNotFoundException
     *               filename で与えられたファイルが見つからない場合。
     * @exception SecurityException
     *               セキュリティマネージャがファイルへのアクセスを許さない場合。
     * @exception IOException
     *               JDK1.2 でコンパイルするためだけに存在する。
     * 
     * @see LhaProperty
     */
    public LhaImmediateOutputStream( File filename, Properties property )
                                                  throws IOException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException, IOException(jdk1.2)
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "filename" );
        }
    }

    /**
     * fileに 圧縮データを出力するOutputStreamを構築する。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param file RandomAccessFile のインスタンス。<br>
     *             <ul>
     *                <li>既に close() されていない事。
     *                <li>コンストラクタの mode には "rw" オプションを使用して、
     *                    読みこみと書きこみが出来るように生成されたインスタンスであること。
     *              </ul>
     *              の条件を満たすもの。
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaImmediateOutputStream( RandomAccessFile file ){

        if( file != null ){
            Properties property   = LhaProperty.getProperties();
            this.constructerHelper( file, property );
        }else{
            throw new NullPointerException( "out" );
        }
    }

    /**
     * fileに 圧縮データを出力するOutputStreamを構築する。<br>
     * 各圧縮形式に対応した符号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param file     RandomAccessFile のインスタンス。<br>
     *                 <ul>
     *                   <li>既に close() されていない事。
     *                   <li>コンストラクタの mode には "rw" オプションを使用して、
     *                       読みこみと書きこみが出来るように生成されたインスタンスであること。
     *                 </ul>
     *                 の条件を満たすもの。
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     * 
     * @see LhaProperty
     */
    public LhaImmediateOutputStream( RandomAccessFile file, 
                                     Properties       property ){

        if( file != null
         && property != null ){

            this.constructerHelper( file, property );                           //throws UnsupportedEncodingException

        }else if( file == null ){
            throw new NullPointerException( "null" );
        }else{
            throw new NullPointerException( "property" );
        }

    }

    /**
     * コンストラクタの初期化処理を担当するメソッド。
     * 
     * @param file     RandomAccessFile のインスタンス。<br>
     *                 <ul>
     *                   <li>既に close() されていない事。
     *                   <li>コンストラクタの mode には "rw" オプションを使用して、
     *                       読みこみと書きこみが出来るように生成されたインスタンスであること。
     *                 </ul>
     *                 の条件を満たすもの。
     * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
     */
    private void constructerHelper( RandomAccessFile file, 
                                    Properties       property ){

        this.archive   = file;

        this.out       = null;
        this.header    = null;
        this.headerpos = -1;
        this.crc       = new CRC16();
        this.property  = property;
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
     * 現在のエントリに1バイトのデータを書きこむ。
     * 
     * @param data 書きこむデータ
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( int data ) throws IOException {
        if( this.out != null ){
            if( this.header != null ){
                crc.update( data );
            }

            this.out.write( data );
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 現在のエントリに bufferの内容を全て書き出す。
     * 
     * @param buffer 書き出すデータの入ったバイト配列
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( byte[] buffer ) throws IOException {
        this.write( buffer, 0, buffer.length );
    }

    /**
     * 現在のエントリに bufferの indexから
     * lengthバイトのデータを書き出す。
     * 
     * @param buffer 書き出すデータの入ったバイト配列
     * @param index  buffer内の書き出すべきデータの開始位置
     * @param length データのバイト数
     * 
     * @exception IOException 入出力エラーが発生した場合。
     */
    public void write( byte[] buffer, int index, int length ) throws IOException {
        if( this.out != null ){
            if( this.header != null ){
                crc.update( buffer, index, length );
            }

            this.out.write( buffer, index, length );
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
     * 現在書き込み中のエントリのデータを強制的に出力先に書き出す。
     * これは PostLzssEncoder, LzssOutputStream の規約どおり
     * flush() しなかった場合とは別のデータを出力する。
     * (大抵の場合は 単に圧縮率が低下するだけである。)
     * 
     * @exception IOException 入出力エラーが発生した場合
     * 
     * @see PostLzssEncoder#flush()
     * @see LzssOutputStream#flush()
     */
    public void flush() throws IOException {
        if( this.out != null ){
            this.out.flush();                                                   //throws IOException
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 出力先に全てのデータを出力し、ストリームを閉じる。<br>
     * また、使用していた全てのリソースを解放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        if( this.out != null ){
            this.closeEntry();                                                  //throws IOException
        }

        //ターミネータを出力
        this.archive.write( 0 );                                                //throws IOException
        try{
            this.archive.setLength( this.archive.getFilePointer() );            //After Java1.2 throws IOException
        }catch( NoSuchMethodError error ){
        }
        this.archive.close();                                                   //throws IOException
        this.archive = null;

        this.crc      = null;
        this.property = null;
        this.encoding = null;
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
     *   <li>header.getCRC()<br>
     * </ul>
     * のどれか一つでも LhaHeader.UNKNOWN であれば未だに圧縮されていないとする。<br>
     * header には正確な OriginalSize が指定されている必要がある。<br>
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
     */
    public void putNextEntry( LhaHeader header ) throws IOException {
        if( header.getCompressedSize() == LhaHeader.UNKNOWN
         || header.getCRC()            == LhaHeader.UNKNOWN ){
            this.putNextEntryNotYetCompressed( header );                        //throws IOException
        }else{
            this.putNextEntryAlreadyCompressed( header );                       //throws IOException
        }
    }

    /**
     * 既に圧縮済みのエントリを書きこむようにストリームを設定する。<br>
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
     * @exception IllegalStateException
     *               以前のエントリが未だに closeEntry() されていない場合
     */
    public void putNextEntryAlreadyCompressed( LhaHeader header )
                                                            throws IOException {

        if( this.out == null ){

            if( header.getOriginalSize()   != LhaHeader.UNKNOWN
             && header.getCompressedSize() != LhaHeader.UNKNOWN
             && header.getCRC()            != LhaHeader.UNKNOWN ){

                this.headerpos = this.archive.getFilePointer();

                this.encoding = this.property.getProperty( "lha.encoding" );
                if( this.encoding == null ){
                    this.encoding = LhaProperty.getProperty( "lha.encoding" );
                }

                this.archive.write( header.getBytes( encoding ) );                  //throws IOException
                this.out = new RandomAccessFileOutputStream( this.archive, header.getCompressedSize() );

            }else if( header.getOriginalSize() == LhaHeader.UNKNOWN ){
                throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
            }else if( header.getCompressedSize() == LhaHeader.UNKNOWN ){
                throw new IllegalArgumentException( "CompressedSize must not \"LhaHeader.UNKNOWN\"." );
            }else{
                throw new IllegalArgumentException( "CRC must not \"LhaHeader.UNKNOWN\"." );
            }

        }else{
            throw new IllegalStateException( "entry is not closed." );
        }
    }

    /**
     * 未だに圧縮されていないエントリを書きこむようにストリームを
     * 設定する。header に CompressedSize,CRCが指定されていても無
     * 視される。このメソッドに渡される header には
     * LhaHeader.setOriginalSize() を用いて 正確なオリジナルサイズ
     * が指定されている必要がある。
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() が
     *                        LhaHeader.UNKNOWN を返した場合
     * @exception IllegalStateException
     *                        以前のエントリが未だに
     *                        closeEntry() されていない場合
     */
    public void putNextEntryNotYetCompressed( LhaHeader header ) 
                                                        throws IOException {

        if( out == null ){

            if( header.getOriginalSize() != LhaHeader.UNKNOWN ){

                this.crc.reset();
                this.headerpos = this.archive.getFilePointer();
                this.header    = (LhaHeader)header.clone();
                this.header.setCompressedSize( 0 );
                this.header.setCRC( 0 );

                this.encoding = this.property.getProperty( "lha.encoding" );
                if( this.encoding == null ){
                    this.encoding = LhaProperty.getProperty( "lha.encoding" );
                }

                this.archive.write( this.header.getBytes( encoding ) );
                this.out = new RandomAccessFileOutputStream( this.archive, header.getOriginalSize() );
                this.out = CompressMethod.connectEncoder( this.out, 
                                                          header.getCompressMethod(), 
                                                          this.property  );

            }else{
                throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
            }

        }else{
            throw new IllegalStateException( "entry is not closed." );
        }
    }

    /**
     * 現在出力中のエントリを閉じ、次のエントリが出力可能な状態にする。<br>
     * putNextEntryNotYetCompressed() で開いたエントリを閉じる場合
     * このメソッドは圧縮に失敗した(圧縮後サイズが圧縮前サイズを上回った)場合、
     * エントリ全体を書き込み先 の RandomAccessFile から削除する。<br>
     * この削除処理は単に ファイルポインタを エントリ開始位置まで巻き戻すだけなので
     * RandomAccessFile に setLength() が無い jdk1.1 以前では 
     * エントリを無圧縮(もしくは他の圧縮法)で再出力しない場合、
     * 書庫データの終端以降に圧縮に失敗した不完全なデータが残ったままになる。<br>
     * 
     * @return エントリが出力された場合は true、
     *         圧縮前よりも圧縮後の方がサイズが大きくなったため、
     *         エントリが削除された場合は false。
     *         また、現在処理中のエントリが無かった場合も true を返す。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public boolean closeEntry() throws IOException {
        if( this.out != null ){

            this.out.close();
            if( this.header != null ){

                long pos = this.archive.getFilePointer();
                long size = ( pos - this.headerpos
                                  - this.header.getBytes( this.encoding ).length );

                this.header.setCompressedSize( size );
                if( this.header.getCRC() != LhaHeader.NO_CRC ){
                    this.header.setCRC( (int)this.crc.getValue() );
                }

                this.archive.seek( this.headerpos );
                if( this.header.getCompressMethod().equals( CompressMethod.LH0 )
                 || this.header.getCompressMethod().equals( CompressMethod.LHD )
                 || this.header.getCompressMethod().equals( CompressMethod.LZ4 )
                 || this.header.getCompressedSize() < this.header.getOriginalSize() ){

                    this.archive.write( this.header.getBytes( this.encoding ) );
                    this.archive.seek( pos );
                    this.header = null;
                    this.out    = null;
                    return true;
                }else{
                    this.header = null;
                    this.out    = null;
                    return false;
                }
            }else{
                this.out    = null;
                return true;
            }
        }else{
            return true;
        }
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class RandomAccessFileOutputStream
    //------------------------------------------------------------------
    /**
     * RandomAccessFileをOutputStreamのインタフェイスに合わせるためのラッパクラス
     */
    private static class RandomAccessFileOutputStream extends OutputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  sink
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //------------------------------------------------------------------
        /**
         * 出力先RandomAccessFile
         */
        private RandomAccessFile archive;

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  position
        //------------------------------------------------------------------
        //  private long pos
        //  private long limit
        //------------------------------------------------------------------
        /**
         * 現在処理位置
         */
        private long pos;

        /**
         * 格納限界
         */
        private long limit;

        //------------------------------------------------------------------
        //  consutructor
        //------------------------------------------------------------------
        //  public RandomAccessFileOutputStream( RandomAccessFile archive,
        //                                       long length )
        //------------------------------------------------------------------
        /**
         * RandomAccessFile をラップした OutputStream を構築する。
         * 
         * @param archive 出力先のRandomAccessFile
         * @param length  出力限界長
         * 
         * @exception IOException 入出力エラーエラーが発生した場合
         */
        public RandomAccessFileOutputStream( RandomAccessFile archive,
                                             long length ) throws IOException {
            this.archive = archive;
            this.pos     = this.archive.getFilePointer();                       //throws IOException
            this.limit   = this.pos + length;
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
         * 接続されたRandomAccessFileに1バイト書きこむ。<br>
         * コンストラクタに渡された限界を超えて書き込もうとした場合は
         * 何も行わない。
         * 
         * @param data 書きこむ1byteのデータ
         * 
         * @exception IOException  入出力エラーが発生した場合
         */
        public void write( int data ) throws IOException {
            if( this.pos < this.limit ){
                this.pos++;
                this.archive.write( data );                                     //throws IOException
            }
        }

        /**
         * 接続されたRandomAccessFileにbufferの内容を全て書きこむ。
         * コンストラクタに渡された限界を超えて書き込もうとした場合は
         * 何も行わない。
         * 
         * @param buffer 書きこむデータの入ったバイト配列
         * 
         * @exception IOException  入出力エラーが発生した場合
         */
        public void write( byte[] buffer ) throws IOException {
            this.write( buffer, 0, buffer.length );                             //throws IOException
        }

        /**
         * 接続されたRandomAccessFileにbufferの内容をindexから lengthバイト書きこむ。
         * コンストラクタに渡された限界を超えて書き込もうとした場合は
         * 何も行わない。
         * 
         * @param buffer 書きこむデータの入ったバイト配列
         * @param index  buffer内の書きこむデータの開始位置
         * @param length 書きこむデータ量
         * 
         * @exception IOException  入出力エラーが発生した場合
         */
        public void write( byte[] buffer, int index, int length )
                                                        throws IOException {

            if( this.limit < this.pos + length ){
                length = (int)Math.max( this.limit - this.pos, 0 );
            }
            this.archive.write( buffer, index, length );                        //throws IOException
            this.pos += length;
        }

        //------------------------------------------------------------------
        //  method of java.io.OutputStream
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------
        /**
         * このストリームを閉じて 使用していたリソースを開放する。<br>
         */
        public void close(){
            this.archive = null;
        }

    }

}
//end of LhaImmediateOutputStream.java
