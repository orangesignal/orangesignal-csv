//start of LhaRetainedOutputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaRetainedOutputStream.java
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
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Vector;
import java.util.Properties;




//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import com.orangesignal.jlha.CRC16;
import com.orangesignal.jlha.CompressMethod;
import com.orangesignal.jlha.LhaHeader;
import com.orangesignal.jlha.LhaProperty;

/**
 * 接続されたRandomAccessFileに 圧縮データを出力するためのユーティリティクラス。<br>
 * java.util.zip.ZipOutputStream と似たインターフェイスを持つように作った。<br>
 * 圧縮失敗時( 圧縮後サイズが圧縮前サイズを上回った場合 )の処理を自動的に行う。
 * 進捗報告を実装する場合、このような処理をクラス内に隠蔽すると進捗報告は何秒間か
 * 時によっては何十分も応答しなくなる。(例えばギガバイト級のデータを扱った場合)
 * このような事態を避けたい場合は LhaImmediateOutputStreamを使用すること。<br>
 * また、JDK 1.1 以前では RandomAccessFile が setLength を持たないため、
 * 書庫データの後ろに他のデータがある場合でもファイルサイズを切り詰めることが出来ない。
 * この問題点は常にサイズ0の新しいファイルを開く事によって回避する事ができる。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaRetainedOutputStream.java,v $
 * Revision 1.2  2002/12/11 02:25:14  dangan
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
public class LhaRetainedOutputStream extends OutputStream{


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
    //  private RandomAccessFileOutputStream rafo
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
     * 圧縮用出力ストリーム
     */
    private RandomAccessFileOutputStream rafo;

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
    //  private LhaRetainedOutputStream()
    //  public LhaRetainedOutputStream( String filename )
    //  public LhaRetainedOutputStream( String filename, Properties property )
    //  public LhaRetainedOutputStream( File file )
    //  public LhaRetainedOutputStream( File file, Properties property )
    //  public LhaRetainedOutputStream( RandomAccessFile archive )
    //  public LhaRetainedOutputStream( RandomAccessFile archive, Properties property )
    //  private void constructerHelper( RandomAccesFile archive, Properties property )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ
     * 使用不可
     */
    private LhaRetainedOutputStream(){  }

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
    public LhaRetainedOutputStream( String filename ) 
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
    public LhaRetainedOutputStream( String filename, Properties property )
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
    public LhaRetainedOutputStream( File filename ) throws IOException {

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
     * @exception IOException
     *               JDK1.2 でコンパイルするためだけに存在する。
     * 
     * @see LhaProperty
     */
    public LhaRetainedOutputStream( File filename, Properties property )
                                                            throws IOException {

        if( filename != null ){
            RandomAccessFile file = new RandomAccessFile( filename, "rw" );     //throws FileNotFoundException, SecurityException
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
    public LhaRetainedOutputStream( RandomAccessFile file ){

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
    public LhaRetainedOutputStream( RandomAccessFile file, 
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
        this.archive  = null;
        this.header   = null;
        this.crc      = null;
        this.property = null;
        this.rafo     = null;
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
         || header.getCrc()            == LhaHeader.UNKNOWN ){
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
     */
    public void putNextEntryAlreadyCompressed( LhaHeader header )
                                                throws IOException {
        if( header.getOriginalSize()   != LhaHeader.UNKNOWN
         && header.getCompressedSize() != LhaHeader.UNKNOWN
         && header.getCrc()            != LhaHeader.UNKNOWN ){

            if( this.out != null ){
                this.closeEntry();
            }

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
    }

    /**
     * 未だに圧縮されていないエントリを書きこむようにストリームを設定する。<br>
     * header には正確な OriginalSize が指定されている必要がある。<br>
     * header に CompressedSize, CRCが指定されていても無視される。<br>
     * 
     * @param header 書きこむエントリについての情報を持つ
     *               LhaHeaderのインスタンス。
     * 
     * @exception IOException 入出力エラーが発生した場合
     * @exception IllegalArgumentException
     *                        header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
     */
    public void putNextEntryNotYetCompressed( LhaHeader header ) 
                                                        throws IOException {
        if( header.getOriginalSize() != LhaHeader.UNKNOWN ){
            if( this.out != null ){
                this.closeEntry();
            }

            this.crc.reset();
            this.headerpos = this.archive.getFilePointer();
            this.header    = (LhaHeader)header.clone();
            this.header.setCompressedSize( 0 );
            this.header.setCrc( 0 );

            this.encoding = this.property.getProperty( "lha.encoding" );
            if( this.encoding == null ){
                this.encoding = LhaProperty.getProperty( "lha.encoding" );
            }

            this.archive.write( this.header.getBytes( encoding ) );
            this.rafo = new RandomAccessFileOutputStream( this.archive, header.getOriginalSize() );
            this.out = CompressMethod.connectEncoder( this.rafo, 
                                                      header.getCompressMethod(), 
                                                      this.property  );

        }else{
            throw new IllegalArgumentException( "OriginalSize must not \"LhaHeader.UNKNOWN\"." );
        }
    }

    /**
     * 現在出力中のエントリを閉じ、次のエントリが出力可能な状態にする。<br>
     * 圧縮に失敗した(圧縮後サイズが圧縮前サイズを上回った)場合、
     * 解凍し無圧縮で格納する。エントリのサイズが大きい場合、
     * この処理にはかなりの時間がかかる。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void closeEntry() throws IOException {
        if( this.header != null ){
            this.out.close();

            if( !this.rafo.cache.isEmpty() ){
                RandomAccessFileInputStream rafi;
                InputStream in;
                long pos = this.rafo.start;
                rafi = new RandomAccessFileInputStream( this.archive, this.rafo  );
                in = CompressMethod.connectDecoder( rafi, 
                                                    header.getCompressMethod(), 
                                                    this.property,
                                                    this.header.getOriginalSize() );

                byte[] buffer = new byte[8192];
                int length;
                while( 0 <= ( length = in.read( buffer ) ) ){
                    rafi.cache( pos + length );
                    this.archive.seek( pos );
                    this.archive.write( buffer, 0, length );
                    pos += length;
                }
                in.close();

                this.header.setCompressMethod( CompressMethod.LH0 );
            }

            long pos  = this.archive.getFilePointer();
            long size = ( pos - this.headerpos
                              - this.header.getBytes( this.encoding ).length );
            this.header.setCompressedSize( size );
            if( this.header.getCrc() != LhaHeader.NO_CRC ){
                this.header.setCrc( (int)this.crc.getValue() );
            }

            this.archive.seek( this.headerpos );
            this.archive.write( this.header.getBytes( this.encoding ) );
            this.archive.seek( pos );
        }
        this.header = null;
        this.out    = null;
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private static class RandomAccessFileOutputStream
    //  private static class RandomAccessFileInputStream
    //  private static class Cache
    //------------------------------------------------------------------
    /**
     * RandomAccessFile を OutputStreamの インタフェイスに合わせるためのラッパクラス
     */
    private static class RandomAccessFileOutputStream extends OutputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  sink
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //  private GrowthByteBuffer cache
        //------------------------------------------------------------------
        /**
         * 出力先RandomAccessFile
         */
        private RandomAccessFile archive;

        /**
         * 格納限界を超えて書き込もうとした
         * 場合のキャッシュ
         */
        private Cache cache;


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  position
        //------------------------------------------------------------------
        //  private long start
        //  private long pos
        //  private long limit
        //------------------------------------------------------------------
        /**
         * 格納開始位置
         */
        private long start;

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
            this.start   = this.archive.getFilePointer();                       //throws IOException
            this.pos     = this.start;
            this.limit   = this.start + length;
            this.cache   = new Cache();
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
         * 接続された RandomAccessFile に1バイト書きこむ。
         * 
         * @param data 書きこむ1byteのデータ
         * 
         * @exception IOException  入出力エラーが発生した場合
         */
        public void write( int data ) throws IOException {
            if( this.pos < this.limit && this.cache.isEmpty() ){
                this.pos++;
                this.archive.write( data );                                     //throws IOException
            }else{
                this.cache.add( new byte[]{ (byte)data } );
            }
        }

        /**
         * 接続された RandomAccessFile に buffer の内容を全て書きこむ。
         * 
         * @param buffer 書きこむデータの入ったバイト配列
         * 
         * @exception IOException  入出力エラーが発生した場合
         * @exception EOFException コンストラクタに渡された長さを超えて
         *                         書きこもうとした場合
         */
        public void write( byte[] buffer ) throws IOException {
            this.write( buffer, 0, buffer.length );                             //throws IOException
        }

        /**
         * 接続されたRandomAccessFileにbufferの内容をindexからlengthバイト書きこむ。
         * 
         * @param buffer 書きこむデータの入ったバイト配列
         * @param index  buffer内の書きこむデータの開始位置
         * @param length 書きこむデータ量
         * 
         * @exception IOException  入出力エラーが発生した場合
         */
        public void write( byte[] buffer, int index, int length )
                                                        throws IOException {

            if( this.pos + length < this.limit && this.cache.isEmpty() ){
                this.pos += length;
                this.archive.write( buffer, index, length );                    //throws IOException
            }else{
                this.cache.add( buffer, index, length );
            }
        }


        //------------------------------------------------------------------
        //  method of java.io.OutputStream
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------
        /**
         * このストリームを閉じて使用していたリソースを開放する。
         */
        public void close(){
            this.archive = null;
        }

    }

    /**
     * RandomAccessFile に InputStreamのインターフェイスをかぶせるラッパクラス。
     * 圧縮後のサイズが圧縮前のサイズを上回ったときに解凍して 
     * 無圧縮で格納しなおす処理のために必要。
     */
    private static class RandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  source
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //  private Cache front
        //  private Cache rear
        //------------------------------------------------------------------
        /**
         * 読み込み元RandomAccessFile
         */
        private RandomAccessFile archive;

        /**
         * 前部キャッシュ
         * 書き込みが読み込みを追い越した時のキャッシュ
         */
        private Cache front;

        /**
         * 後部キャッシュ
         * 書き込み限界を超えた分のデータのキャッシュ
         */
        private Cache rear;


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
         * 読み込み限界
         */
        private long limit;


        //------------------------------------------------------------------
        //  consutructor
        //------------------------------------------------------------------
        //  public RandomAccessFileInputStream( RandomAccessFile archive,
        //                                      RandomAccessFileOutputStream out )
        //------------------------------------------------------------------
        /**
         * RandomAccessFile をラップした InputStream を構築する。
         * 
         * @param archive データを供給する RandomAccessFile
         * @param out     直前に圧縮データを受け取っていた RandomAccessFileOutputStream
         */
        public RandomAccessFileInputStream( RandomAccessFile archive,
                                            RandomAccessFileOutputStream out ){
            this.archive = archive;
            this.pos     = out.start;
            this.limit   = out.pos;
            this.front   = new Cache();
            this.rear    = out.cache;
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  read
        //------------------------------------------------------------------
        //  public int read()
        //  public int read( byte[] buffer )
        //  public int read( byte[] buffer, int index, int length )
        //------------------------------------------------------------------
        /**
         * キャッシュかRandomAccessFileから 1バイトのデータを読み込む。
         * 
         * @return 読み込まれた1バイトのデータ<br>
         *         読み込むデータが無ければ -1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read() throws IOException {
            int return_value = this.front.read();
            if( return_value < 0 ){
                if( this.pos < this.limit ){
                    this.archive.seek( this.pos++ );
                    return_value = this.archive.read();
                }else{
                    return_value = this.rear.read();
                }
            }

            return return_value;
        }

        /**
         * キャッシュか RandomAccessFileから bufferを満たすようにデータを読み込む。
         * 
         * @param buffer 読み込まれたデータを格納するバッファ
         * 
         * @return 実際に読み込まれたデータ量
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer ) throws IOException {
            return this.read( buffer, 0, buffer.length );
        }

        /**
         * キャッシュか RandomAccessFileから bufferのindexへlengthバイト読み込む。
         * 
         * @param buffer 読み込まれたデータを格納するバッファ
         * @param index  buffer内の読み込み開始位置
         * @param length 読み込むデータ量
         * 
         * @return 実際に読み込まれたデータ量
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer, int index, int length ) throws IOException {

            int count = 0;
            int ret   = this.front.read( buffer, index, length );
            if( 0 <= ret ){
                count += ret;
            }

            this.archive.seek( this.pos );                                      //throws IOException
            ret = Math.min( length - count, 
                            Math.max( (int)( this.limit - this.pos ), 0 ) );
            this.archive.readFully( buffer, index + count, ret );               //throws IOException
            if( 0 <= ret ){
                this.pos += ret;
                count    += ret;
            }

            ret = this.rear.read( buffer, index + count, length - count );
            if( 0 <= ret ){
                count += ret;
            }

            if( 0 < count ){
                return count;
            }else{
                return -1;
            }
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream 
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public void close()
        //------------------------------------------------------------------
        /**
         * このストリームを閉じ
         * 使用していたリソースを開放する。
         */
        public void close(){
            this.front   = null;
            this.rear    = null;
            this.archive = null;
        }


        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public void cache( long pos )
        //------------------------------------------------------------------
        /**
         * posまで読み込んでいなければ、
         * 現在読み込み位置からposまでのデータを
         * 前部キャッシュにデータを追加する。
         * 
         * @param pos archive内の書き出し位置
         */
        public void cache( long pos ) throws IOException {
            int length = (int)Math.min( this.limit - this.pos,
                                        pos - this.pos );

            byte[] buffer = new byte[ length ];
            if( 0 < length ){
                this.archive.seek( this.pos );                                  //throws IOException
                this.archive.readFully( buffer );                               //throws IOException
                this.front.add( buffer );
 
                this.pos += length;
            }
        }
    }

    /**
     * 書き込み限界を超えた書き込みや
     * 読み込み位置を超えた書き込みをした場合に
     * データをキャッシュするために使用する。
     */
    private static class Cache{

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private Vector cache
        //  private byte[] current
        //  private int position
        //------------------------------------------------------------------
        /**
         * byte[] の Vector
         * 各要素は 全て読み込まれたと
         * 同時に捨てられる。
         */
        private Vector cache;

        /**
         * 現在読み込み中の要素
         */
        private byte[] current;

        /**
         * currentの現在処理位置
         */
        private int position;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public Cache()
        //------------------------------------------------------------------
        /**
         * データの一時退避機構を構築する。
         */
        public Cache(){
            this.current  = null;
            this.position = 0;
            this.cache    = new Vector();
        }


        //------------------------------------------------------------------
        //  read
        //------------------------------------------------------------------
        //  public int read()
        //  public int read( byte[] buffer, int index, int length )
        //------------------------------------------------------------------
        /**
         * キャッシュから 1バイトのデータを
         * 0～255にマップして読み込む。
         * 
         * @return 読み込まれた1byteのデータ<br>
         *         キャッシュが空でデータが無い場合は -1
         */
        public int read(){
            if( null != this.current ){
                int ret = this.current[ this.position++ ] & 0xFF;

                if( this.current.length <= this.position ){
                    if( 0 < this.cache.size() ){
                        this.current = (byte[])this.cache.firstElement();
                        this.cache.removeElementAt( 0 );
                    }else{
                        this.current = null;
                    }
                    this.position = 0;
                }
                
                return ret;
            }else{
                return -1;
            }
        }

        /**
         * キャッシュから bufferのindexで始まる場所へlengthバイト読み込む。
         * 
         * @param buffer 読み込んだデータを保持するバッファ
         * @param index  buffer内の読み込み開始位置
         * @param length 読み込むデータ量
         * 
         * @return 実際に読み込まれたデータ量<br>
         *         キャッシュが空でデータが無い場合は -1
         */
        public int read( byte[] buffer, int index, int length ){
            int count = 0;

            while( null != this.current && count < length ){
                int copylen = Math.min( this.current.length - this.position,
                                        length - count );
                System.arraycopy( this.current, this.position,
                                  buffer,       index + count,  copylen );

                this.position += copylen;
                count         += copylen;

                if( this.current.length <= this.position ){
                    if( 0 < this.cache.size() ){
                        this.current = (byte[])this.cache.firstElement();
                        this.cache.removeElementAt( 0 );
                    }else{
                        this.current = null;
                    }
                    this.position = 0;
                }
            }

            if( count == 0 ){
                return -1;
            }else{
                return count;
            }
        }


        //------------------------------------------------------------------
        //  write
        //------------------------------------------------------------------
        //  public void add( byte[] buffer )
        //  public void add( byte[] buffer, int index, int length )
        //------------------------------------------------------------------
        /**
         * キャッシュにデータを追加する。
         * 
         * @param buffer データの格納されたバッファ
         */
        public void add( byte[] buffer ){
            if( this.current == null ){
                this.current = buffer;
            }else{
                this.cache.addElement( buffer );
            }
        }

        /**
         * キャッシュにデータを追加する。
         * 
         * @parma buffer データの格納されたバッファ
         * @param index  buffer内のデータ開始位置
         * @param length 格納されているデータの量
         */
        public void add( byte[] buffer, int index, int length ){
            byte[] buf = new byte[ length ];
            System.arraycopy( buffer, index, buf, 0, length );

            if( this.current == null ){
                this.current = buf;
            }else{
                this.cache.addElement( buf );
            }
        }


        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public boolean isEmpty()
        //------------------------------------------------------------------
        /**
         * このキャッシュが空かを得る。
         * 
         * @return このキャッシュが空なら true
         *         空でなければ false
         */
        public boolean isEmpty(){
            return this.current == null;
        }

    }

}
//end of LhaRetainedOutputStream.java
