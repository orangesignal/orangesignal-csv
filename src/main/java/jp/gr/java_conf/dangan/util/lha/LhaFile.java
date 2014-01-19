//start of LhaFile.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaFile.java
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
import java.io.RandomAccessFile;
import java.lang.Math;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaProperty;
import jp.gr.java_conf.dangan.util.lha.CompressMethod;

//import exceptions
import java.io.IOException;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.SecurityException;
import java.lang.IllegalArgumentException;
import java.util.NoSuchElementException;

import java.lang.Error;


/**
 * LHA書庫ファイルからエントリデータを読み出す
 * InputStreamを得るためのユーティリティクラス。<br>
 * java.util.zip.ZipFile と似た
 * インターフェイスを持つように作った。
 * CRC16等によるチェックは行わない。
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaFile.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [maintenance]
 *     LhaConstants から CompressMethod へのクラス名の変更に合わせて修正。
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [improvement]
 *     エントリの管理に Hashtable を使用する事によって
 *     大量のエントリを持つ書庫でエントリ開始位置を
 *     より速く見つけられるように改良。
 * [change]
 *     コンストラクタから 引数に String encode を取るものを廃止、
 *     Properties を引数に取るものを追加。
 * [maintanance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class LhaFile{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  archive file of LHA 
    //------------------------------------------------------------------
    //  private RandomAccessFile archive
    //  private Object LastAccessObject
    //  private Vector headers
    //  private Vector entryStart
    //  private Hashtable hash
    //  private Vector duplicate
    //------------------------------------------------------------------
    /**
     * LHA書庫形式のデータを持つ
     * RandomAccessFileのインスタンス
     */
    private RandomAccessFile archive;

    /**
     * 最後に archive にアクセスしたオブジェクト
     */
    private Object LastAccessObject;

    /**
     * 各エントリのヘッダを持つ LhaHeader の Vector
     * headers.elementAt( index ) のヘッダを持つエントリは 
     * entryPoint.elementAt( index ) の位置から始まる。
     */
    private Vector headers;

    /**
     * 各エントリの開始位置を持つ Long の Vector
     * headers.elementAt( index ) のヘッダを持つエントリは 
     * entryPoint.elementAt( index ) の位置から始まる。
     */
    private Vector entryPoint;

    /**
     * エントリの名前(格納ファイル名)をキーに、
     * キーの名前のエントリの index を持つハッシュテーブル。
     * 要素は Integer
     */
    private Hashtable hash;

    /**
     * 同名ファイルの救出用。
     * 重複した名前を持つエントリの index を持つ Vector
     * 要素は Integer
     */
    private Vector duplicate;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  property
    //------------------------------------------------------------------
    //  private Properties property
    //------------------------------------------------------------------
    /**
     * 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     */
    private Properties property;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private LhaFile()
    //  public LhaFile( String filename )
    //  public LhaFile( String filename, Properties property )
    //  public LhaFile( File file )
    //  public LhaFile( File file, Properties property )
    //  public LhaFile( RandomAccessFile archive )
    //  public LhaFile( RandomAccessFile archive, boolean rescueMode )
    //  public LhaFile( RandomAccessFile archive, Properties property )
    //  public LhaFile( RandomAccessFile archive, 
    //                  Properties property, boolean rescueMode )
    //  private void constructerHelper( RandomAccessFile archive, 
    //                                  Properties property,
    //                                  boolean rescueMode )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 仕様不可
     */
    private LhaFile(){  }

    /**
     * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
     * 各圧縮形式に対応した復号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param filename LHA書庫ファイルの名前
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaFile( String filename ) throws IOException {
        Properties property   = LhaProperty.getProperties();
        RandomAccessFile file = new RandomAccessFile( filename, "r" );          //throws FileNotFoundException SecurityException

        this.constructerHelper( file, property, false );                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
     * 
     * @param filename LHA書庫ファイルの名前
     * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) で得られた
     *                 エンコーディング名がサポートされない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty
     */
    public LhaFile( String filename, Properties property ) throws IOException {
        RandomAccessFile file = new RandomAccessFile( filename, "r" );          //throws FileNotFoundException SecurityException

        this.constructerHelper( file, property, false );                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
     * 各圧縮形式に対応した復号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param filename LHA書庫ファイル
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaFile( File filename ) throws IOException {
        Properties property   = LhaProperty.getProperties();
        RandomAccessFile file = new RandomAccessFile( filename, "r" );          //throws FileNotFoundException SecurityException

        this.constructerHelper( file, property, false );                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * filename で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
     * 
     * @param filename LHA書庫ファイル
     * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) で得られた
     *                 エンコーディング名がサポートされない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty
     */
    public LhaFile( File filename, Properties property ) throws IOException {
        RandomAccessFile file = new RandomAccessFile( filename, "r" );          //throws FileNotFoundException SecurityException

        this.constructerHelper( file, property, false );                        //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
     * 各圧縮形式に対応した復号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param file LHA書庫ファイル
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaFile( RandomAccessFile file ) throws IOException {
        Properties property   = LhaProperty.getProperties();

        this.constructerHelper( file, property, false );
    }

    /**
     * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
     * 各圧縮形式に対応した復号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param file       LHA書庫ファイル
     * @param rescueMode true にすると壊れた書庫のデータを
     *                   復旧するための復旧モードでエントリを検索する。
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaFile( RandomAccessFile file, boolean rescueMode ) 
                                                            throws IOException {
        Properties property   = LhaProperty.getProperties();

        this.constructerHelper( file, property, rescueMode );
    }

    /**
     * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
     * 
     * @param file     LHA書庫ファイル
     * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty
     */
    public LhaFile( RandomAccessFile file, Properties property ) 
                                                            throws IOException {

        this.constructerHelper( file, property, false );
    }

    /**
     * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
     * 
     * @param file       LHA書庫ファイル
     * @param property   各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * @param rescueMode true にすると壊れた書庫のデータを
     *                   復旧するための復旧モードでエントリを検索する。
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception FileNotFoundException
     *                 ファイルが見つからない場合
     * @exception SecurityException
     *                 セキュリティマネージャがファイルの読み込みを許さない場合
     * 
     * @see LhaProperty
     */
    public LhaFile( RandomAccessFile file, Properties property, boolean rescueMode ) 
                                                            throws IOException {

        this.constructerHelper( file, property, rescueMode );
    }


    /**
     * file を走査してエントリ情報を構築する。<br>
     * 
     * @param file       LHA書庫ファイル
     * @param propety    各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * @param rescueMode true にすると壊れた書庫のデータを
     *                   復旧するための復旧モードでエントリを検索する。
     * 
     * @exception IOException
     *                 入出力エラーが発生した場合
     * @exception UnsupportedEncodingException
     *                 encodeがサポートされない場合
     */
    private void constructerHelper( RandomAccessFile file,
                                    Properties       property,
                                    boolean          rescueMode )
                                                            throws IOException {

        this.headers    = new Vector();
        this.entryPoint = new Vector();

        file.seek( 0 );
        CachedRandomAccessFileInputStream archive =  new CachedRandomAccessFileInputStream( file );

        byte[] HeaderData = LhaHeader.getFirstHeaderData( archive );
        while( null != HeaderData ){
            LhaHeader header = LhaHeader.createInstance( HeaderData, property );
            headers.addElement( header );
            entryPoint.addElement( new Long( archive.position() ) );

            if( !rescueMode ){
                archive.skip( header.getCompressedSize() );
                HeaderData = LhaHeader.getNextHeaderData( archive );
            }else{
                HeaderData = LhaHeader.getFirstHeaderData( archive );
            }
        }
        archive.close();

        this.hash      = new Hashtable();
        this.duplicate = new Vector();
        for( int i = 0 ; i < this.headers.size() ; i++ ){
            LhaHeader header = (LhaHeader)headers.elementAt(i);

            if( !this.hash.containsKey( header.getPath() ) ){
                this.hash.put( header.getPath(), new Integer( i ) );
            }else{
                this.duplicate.addElement( new Integer( i ) );
            }
        }

        this.archive  = file;
        this.property = (Properties)property.clone();
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipFile )
    //------------------------------------------------------------------
    //  get InputStream
    //------------------------------------------------------------------
    //  public InputStream getInputStream( LhaHeader header )
    //  public InputStream getInputStream( String name )
    //  public InputStream getInputStreamWithoutExtract( LhaHeader header )
    //  public InputStream getInputStreamWithoutExtract( String name )
    //------------------------------------------------------------------
    /**
     * header で指定されたエントリの
     * 内容を解凍しながら読みこむ入力ストリームを得る。<br>
     * 
     * @param header ヘッダ
     * 
     * @return headerで指定されたヘッダを持つエントリの
     *         内容を読みこむ入力ストリーム。<br>
     *         エントリが見つからない場合は null。
     */
    public InputStream getInputStream( LhaHeader header ){
        int index = this.getIndex( header );
        if( 0 <= index ){
            long start = ((Long)this.entryPoint.elementAt( index )).longValue();
            long len   = header.getCompressedSize();
            InputStream in = new RandomAccessFileInputStream( start, len );

            return CompressMethod.connectDecoder( in, 
                                                  header.getCompressMethod(), 
                                                  this.property,
                                                  header.getOriginalSize() );
        }else{
            return null;
        }
    }

    /**
     * nameで指定された名前を持つエントリの
     * 内容を解凍しながら読みこむ入力ストリームを得る。<br>
     * 
     * @param name エントリの名前
     * 
     * @return nameで指定された名前を持つエントリの
     *         内容を解凍しながら読みこむ入力ストリーム。<br>
     *         エントリが見つからない場合は null。
     */
    public InputStream getInputStream( String name ){
        if( this.hash.containsKey( name ) ){
            int index  = ((Integer)this.hash.get( name )).intValue();
            LhaHeader header = (LhaHeader)this.headers.elementAt( index );
            long start = ((Long)this.entryPoint.elementAt( index )).longValue();
            long len   = header.getCompressedSize();
            InputStream in = new RandomAccessFileInputStream( start, len );

            return CompressMethod.connectDecoder( in, 
                                                  header.getCompressMethod(), 
                                                  this.property,
                                                  header.getOriginalSize() );
        }else{
            return null;
        }
    }

    /**
     * headerで指定されたエントリの内容を
     * 解凍せずに読みこむ入力ストリームを返す。<br>
     * 
     * @param header ヘッダ
     * 
     * @return headerで指定されたエントリの内容を
     *         解凍せずに読みこむ入力ストリーム。<br>
     *         エントリが見つからない場合は null。
     */
    public InputStream getInputStreamWithoutExtract( LhaHeader header ){
        int index = this.getIndex( header );
        if( 0 <= index ){
            long start = ((Long)this.entryPoint.elementAt( index )).longValue();
            long len   = header.getCompressedSize();

            return new RandomAccessFileInputStream( start, len );
        }else{
            return null;
        }
    }

    /**
     * nameで指定された名前を持つエントリの
     * 内容を解凍せずに読みこむ入力ストリームを返す。<br>
     * 
     * @param name エントリの名前
     * 
     * @return nameで指定された名前を持つエントリの
     *         内容を解凍せずに読みこむ入力ストリーム。<br>
     *         エントリが見つからない場合は null。
     */
    public InputStream getInputStreamWithoutExtract( String name ){
        if( this.hash.containsKey( name ) ){
            int index  = ((Integer)this.hash.get( name )).intValue();
            LhaHeader header = (LhaHeader)this.headers.elementAt( index );
            long start = ((Long)this.entryPoint.elementAt( index )).longValue();
            long len   = header.getCompressedSize();

            return new RandomAccessFileInputStream( start, len );
        }else{
            return null;
        }
    }


    //------------------------------------------------------------------
    //  original method ( on the model of java.util.zip.ZipFile  )
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public int size()
    //  public Enumeration entries()
    //  public LhaHeader[] getEntries()
    //  public void close()
    //------------------------------------------------------------------
    /**
     * この LhaFile 内のエントリの数を得る。
     * 
     * @return ファイル内のエントリの数
     */
    public int size(){
        return this.headers.size();
    }

    /**
     * この LhaFile 内のエントリの LhaHeader の列挙子を得る。
     * 
     * @return LhaHeader の列挙子
     * 
     * @exception IllegalStateException
     *                   LhaFile が close() で閉じられている場合。
     */
    public Enumeration entries(){
        if( this.archive != null ){
            return new HeaderEnumeration();
        }else{
            throw new IllegalStateException();
        }
    }

    /**
     * ファイル内のエントリを列挙した配列を得る。
     * 
     * @return ファイル内のエントリを列挙した配列
     */
    public LhaHeader[] getEntries(){
        LhaHeader[] headers = new LhaHeader[ this.headers.size() ];

        for( int i = 0 ; i < this.headers.size() ; i++ ){
            headers[i] = (LhaHeader)((LhaHeader)this.headers.elementAt( i )).clone();
        }

        return headers;
    }


    /**
     * この LHA書庫ファイルを閉じる。
     * その際、このLhaFileが発行した全ての
     * InputStreamは強制的に閉じられる。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        this.archive.close();
        this.archive          = null;
        this.LastAccessObject = null;
        this.headers          = null;
        this.entryPoint       = null;
        this.hash             = null;
        this.property         = null;
        this.duplicate        = null;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private int getIndex( LhaHeader target )
    //  private static boolean equal( LhaHeader header1, LhaHeader header2 )
    //------------------------------------------------------------------
    /**
     * headers における target の index を得る。
     * 
     * @param target ヘッダ
     * 
     * @return headers 内での target の index。 
     *         headers 内に target がない場合は -1
     */
    private int getIndex( LhaHeader target ){
        int index = ((Integer)this.hash.get( target.getPath() )).intValue();

        LhaHeader header = (LhaHeader)this.headers.elementAt( index );
        if( !LhaFile.equal( header, target ) ){
            boolean match = false;
            for( int i = 0 ; i < this.duplicate.size() && !match ; i++ ){
                index  = ((Integer)this.duplicate.elementAt( i )).intValue();
                header = (LhaHeader)this.headers.elementAt( index );

                if( LhaFile.equal( header, target ) ){
                    match = true;
                }
            }

            if( match ){
                return index;
            }else{
                return -1;
            }
        }else{
            return index;
        }
    }

    /**
     * 2つの LhaHeader、header1 と header2 が同等か調べる。
     * 
     * @param header1 検査対象のヘッダ その1
     * @param header2 検査対象のヘッダ その2
     * 
     * @return header1 と header2 が同等であれば true 違えば false
     */
    private static boolean equal( LhaHeader header1, LhaHeader header2 ){
        return    header1.getPath().equals( header2.getPath() )
               && header1.getCompressMethod().equals( header2.getCompressMethod() )
               && header1.getLastModified().equals( header2.getLastModified() )
               && header1.getCompressedSize() == header2.getCompressedSize()
               && header1.getOriginalSize()   == header2.getOriginalSize()
               && header1.getCrc()            == header2.getCrc()
               && header1.getOsid()           == header2.getOsid()
               && header1.getHeaderLevel()    == header2.getHeaderLevel();
    }


    //------------------------------------------------------------------
    //  inner classes
    //------------------------------------------------------------------
    //  private class RandomAccessFileInputStream
    //  private static class CachedRandomAccessFileInputStream
    //  private class EntryEnumeration
    //------------------------------------------------------------------
    /**
     * LhaFileのarchiveの ある区間内のデータを得る InputStream。
     * 複数エントリを同時に処理するための 同期処理を含む。
     */
    private class RandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  member values
        //------------------------------------------------------------------
        //  private long position
        //  private long end
        //  private long markPosition
        //------------------------------------------------------------------
        /**
         * archive内の現在処理位置
         */
        private long position;

        /**
         * archive内のこのInputStreamの読み取り限界
         */
        private long end;

        /**
         * archive内のマーク位置
         */
        private long markPosition;


        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public RandomAccessFileInputStream( long start, long size )
        //------------------------------------------------------------------
        /**
         * コンストラクタ。
         * 
         * @param start 読みこみ開始位置
         * @param size  データのサイズ
         */
        public RandomAccessFileInputStream( long start, long size ){
            this.position     = start;
            this.end          = start + size;
            this.markPosition = -1;
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
         * archiveの現在処理位置から 1byteのデータを読み込む。
         * 
         * @return 読みこまれた1byteのデータ<br>
         *         既に読みこみ限界に達した場合は -1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read() throws IOException {
            synchronized( LhaFile.this.archive ){
                if( this.position < this.end ){
                    if( LhaFile.this.LastAccessObject != this )
                        LhaFile.this.archive.seek( this.position );

                    int data = LhaFile.this.archive.read();
                    if( 0 <= data ) this.position++;
                    return data;
                }else{
                    return -1;
                }
            }
        }

        /**
         * archiveの現在処理位置から bufferを満たすようにデータを読み込む。
         * 
         * @param buffer 読みこまれたデータを格納するバッファ
         * 
         * @return 読みこまれたバイト数<br>
         *         既に読みこみ限界に達していた場合は-1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer ) throws IOException {
            return this.read( buffer, 0, buffer.length );
        }

        /**
         * archiveの現在処理位置から bufferのindexから始まる領域へ
         * lengthバイトのデータを読み込む。
         * 
         * @param buffer 読みこまれたデータを格納するバッファ
         * @param index  buffer内の読みこみ開始位置
         * @param length 読みこむバイト数。
         * 
         * @return 読みこまれたバイト数<br>
         *         既に読みこみ限界に達していた場合は-1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer, int index, int length )
                                                        throws IOException {
            synchronized( LhaFile.this.archive ){
                if( this.position < this.end ){
                    if( LhaFile.this.LastAccessObject != this ){
                        LhaFile.this.archive.seek( this.position );
                        LhaFile.this.LastAccessObject = this;
                    }

                    length = (int)Math.min( this.end - this.position, length );
                    length = LhaFile.this.archive.read( buffer, index, length );
                    if( 0 <= length ) this.position += length;
                    return length;
                }else{
                    return -1;
                }
            }
        }

        /**
         * lengthバイトのデータを読み飛ばす。
         * 
         * @param length 読み飛ばしたいバイト数
         * 
         * @return 実際に読み飛ばされたバイト数
         */
        public long skip( long length ){
            synchronized( LhaFile.this.archive ){
                long skiplen = Math.min( this.end - this.position, length );
                this.position += skiplen;

                if( LhaFile.this.LastAccessObject == this )
                    LhaFile.this.LastAccessObject = null;

                return skiplen;
            }
        }

        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  mark/reset
        //------------------------------------------------------------------
        //  public boolean markSupported()
        //  public void mark( int readLimit )
        //  public void reset()
        //------------------------------------------------------------------
        /**
         * このオブジェクトがmark/resetをサポートするかを返す。
         * 
         * @return このオブジェクトはmark/resetをサポートする。<br>
         *         常にtrue。
         */
        public boolean markSupported(){
            return true;
        }

        /**
         * 現在処理位置にマークを施し次のresetで
         * 現在の処理位置に戻れるようにする。
         * 
         * @param readLimit マークの有効限界。
         *                  このオブジェクトでは意味を持たない。
         */
        public void mark( int readLimit ){
            this.markPosition = this.position;
        }

        /**
         * 最後にマークされた処理位置に戻す。
         * 
         * @exception IOException mark()されていない場合
         */
        public void reset() throws IOException {
            synchronized( LhaFile.this.archive ){
                if( 0 <= this.markPosition ){
                    this.position = this.markPosition;
                }else{
                    throw new IOException( "not marked" );
                }

                if( LhaFile.this.LastAccessObject == this )
                    LhaFile.this.LastAccessObject = null;
            }
        }

        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public int available()
        //  public void close()
        //------------------------------------------------------------------
        /**
         * 接続された入力ストリームからブロックしないで
         * 読み込むことのできるバイト数を得る。<br>
         * RandomAccessFileInputStream では
         * 読み込みは常に RandomAccessFile に対する
         * アクセスを伴うため、このメソッドは常に 0 を返す。
         * 
         * @return 常に 0<br>
         */
        public int available(){
            return 0;
        }

        /**
         * この入力ストリームを閉じ、使用していた全てのリソースを開放する。<br>
         * このメソッドは何も行わない。
         */
        public void close(){
        }

    }

    /**
     * ヘッダ検索用 の RandomAccessFileInputStream。<br>
     * バッファリングと同期処理を行わない事によって高速化してある。
     */
    private static class CachedRandomAccessFileInputStream extends InputStream {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  source
        //------------------------------------------------------------------
        //  private RandomAccessFile archive
        //------------------------------------------------------------------
        /**
         * データを供給する RandomAccessFile
         */
        private RandomAccessFile archive;


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  cache
        //------------------------------------------------------------------
        //  private byte[] cache
        //  private int cachePosition
        //  private int cacheLimit
        //------------------------------------------------------------------
        /**
         * データを蓄えるためのキャッシュ
         */
        private byte[] cache;

        /**
         * cache内の現在処理位置
         */
        private int cachePosition;

        /**
         * cacheの読み込み限界位置
         */
        private int cacheLimit;


        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  backup for mark/reset
        //------------------------------------------------------------------
        //  private boolean markPositionIsInCache
        //  private byte[] markCache
        //  private int markCachePosition
        //  private int markCacheLimit
        //  private long markPosition
        //------------------------------------------------------------------
        /**
         * mark位置がキャッシュの範囲内にあるかを示す。
         * markされたとき true に設定され、
         * 次に in から キャッシュへの読み込みが
         * 行われたときに false に設定される。
         */
        private boolean markPositionIsInCache;

        /** cacheのバックアップ用 */
        private byte[] markCache;

        /** cachePositionのバックアップ用 */
        private int markCachePosition;

        /** cacheLimitのバックアップ用 */
        private int markCacheLimit;

        /** position のバックアップ用 */
        private long markPosition;


        //------------------------------------------------------------------
        //  constructer
        //------------------------------------------------------------------
        //  public CachedRandomAccessFileInputStream()
        //------------------------------------------------------------------
        /**
         * キャッシュを使用して 高速化した RandomAccessFileInputStream を構築する。
         * 
         * @param file データを供給する RandomAccessFile
         */
        public CachedRandomAccessFileInputStream( RandomAccessFile file ){
            this.archive       = file;

            this.cache         = new byte[ 1024 ];
            this.cachePosition = 0;
            this.cacheLimit    = 0;
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
         * archiveの現在処理位置から 1byteのデータを読み込む。
         * 
         * @return 読みこまれた1byteのデータ<br>
         *         既に読みこみ限界に達した場合は -1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read() throws IOException {
            if( this.cachePosition < this.cacheLimit ){
                return this.cache[ this.cachePosition++ ] & 0xFF;
            }else{
                this.fillCache();                                                     //throws IOException

                if( this.cachePosition < this.cacheLimit ){
                    return this.cache[ this.cachePosition++ ] & 0xFF;
                }else{
                    return -1;
                }
            }
        }

        /**
         * archiveの現在処理位置から bufferを満たすようにデータを読み込む。
         * 
         * @param buffer 読みこまれたデータを格納するバッファ
         * 
         * @return 読みこまれたバイト数<br>
         *         既に読みこみ限界に達していた場合は-1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer ) throws IOException {
            return this.read( buffer, 0, buffer.length );
        }

        /**
         * archiveの現在処理位置から bufferのindexから始まる領域へ
         * lengthバイトのデータを読み込む。
         * 
         * @param buffer 読みこまれたデータを格納するバッファ
         * @param index  buffer内の読みこみ開始位置
         * @param length 読みこむバイト数。
         * 
         * @return 読みこまれたバイト数<br>
         *         既に読みこみ限界に達していた場合は-1
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        public int read( byte[] buffer, int index, int length )
                                                        throws IOException {
            final int requested = length;

            while( 0 < length ){
                if( this.cacheLimit <= this.cachePosition ){
                    this.fillCache();                                             //throws IOException
                    if( this.cacheLimit <= this.cachePosition ){
                        if( requested == length ){
                            return -1;
                        }else{
                            break;
                        }
                    }
                }

                int copylen = Math.min( length,
                                        this.cacheLimit - this.cachePosition );
                System.arraycopy( this.cache, this.cachePosition,
                                  buffer, index, copylen );

                index              += copylen;
                length             -= copylen;
                this.cachePosition += copylen;
            }
            return requested - length;
        }

        /**
         * lengthバイトのデータを読み飛ばす。
         * 
         * @param length 読み飛ばしたいバイト数
         * 
         * @return 実際に読み飛ばされたバイト数
         */
        public long skip( long length ) throws IOException  {
            final long requested = length;

            if( this.cachePosition < this.cacheLimit ){
                long avail   = (long)this.cacheLimit - this.cachePosition;
                long skiplen = Math.min( length, avail );

                length -= skiplen;
                this.cachePosition += (int)skiplen;
            }

            if( 0 < length ){
                long avail    = this.archive.length() - this.archive.getFilePointer();
                long skiplen  = Math.min( avail, length );

                length -= skiplen;
                archive.seek( archive.getFilePointer() + skiplen );
            }

            return requested - length;
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  mark/reset
        //------------------------------------------------------------------
        //  public boolean markSupported()
        //  public void mark( int readLimit )
        //  public void reset()
        //------------------------------------------------------------------
        /**
         * このオブジェクトがmark/resetをサポートするかを返す。
         * 
         * @return このオブジェクトはmark/resetをサポートする。<br>
         *         常にtrue。
         */
        public boolean markSupported(){
            return true;
        }

        /**
         * 現在処理位置にマークを施し次のresetで
         * 現在の処理位置に戻れるようにする。
         * 
         * @param readLimit マークの有効限界。
         *                  このオブジェクトでは意味を持たない。
         */
        public void mark( int readLimit ){
            try{
                this.markPosition = this.archive.getFilePointer();
            }catch( IOException exception ){
                throw new Error( "caught IOException( " + exception.getMessage() + " ) in mark()" );
            }

            if( this.markCache == null ){
                this.markCache = (byte[])this.cache.clone();
            }else{
                System.arraycopy( this.cache, 0, this.markCache, 0, this.cacheLimit );
            }

            this.markCacheLimit        = this.cacheLimit;
            this.markCachePosition     = this.cachePosition;
            this.markPositionIsInCache = true;
        }

        /**
         * 最後にマークされた処理位置に戻す。
         * 
         * @exception IOException mark()されていない場合
         */
        public void reset() throws IOException {
            if( this.markPositionIsInCache ){
                this.cachePosition  = this.markCachePosition;
            }else if( this.markCache == null ){ //この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。 
                throw new IOException( "not marked." );
            }else{
                //in が reset() できない場合は
                //最初の行の this.in.reset() で
                //IOException を投げることを期待している。
                this.archive.seek( this.markPosition );                 //throws IOException

                System.arraycopy( this.markCache, 0, this.cache, 0, this.markCacheLimit );
                this.cacheLimit    = this.markCacheLimit;
                this.cachePosition = this.markCachePosition;
            }
        }


        //------------------------------------------------------------------
        //  method of java.io.InputStream
        //------------------------------------------------------------------
        //  other
        //------------------------------------------------------------------
        //  public int available()
        //  public void close()
        //------------------------------------------------------------------
        /**
         * 接続された入力ストリームからブロックしないで
         * 読み込むことのできるバイト数を得る。<br>
         * 
         * @return ブロックしないで読み出せるバイト数。<br>
         */
        public int available(){
            return this.cacheLimit - this.cachePosition;
        }

        /**
         * この入力ストリームを閉じ、使用していた
         * 全てのリソースを開放する。<br>
         */
        public void close(){
            this.archive       = null;

            this.cache         = null;
            this.cachePosition = 0;
            this.cacheLimit    = 0;

            this.markPositionIsInCache = false;
            this.markCache             = null;
            this.markCachePosition     = 0;
            this.markCacheLimit        = 0;
            this.markPosition          = 0;
        }


        //------------------------------------------------------------------
        //  original method
        //------------------------------------------------------------------
        //  public long position()
        //------------------------------------------------------------------
        /**
         * ファイル先頭を始点とする現在の読み込み位置を得る。
         * 
         * @return 現在の読み込み位置。
         */
        public long position() throws IOException {
            long position = this.archive.getFilePointer();

            position -= this.cacheLimit - this.cachePosition;

            return position;
        }

        //------------------------------------------------------------------
        //  local method
        //------------------------------------------------------------------
        //  private void fillCache()
        //------------------------------------------------------------------
        /**
         * 必要がある場合に、キャッシュ用バッファにデータを
         * 補填しキャッシュ用バッファに必ずデータが存在する
         * ことを保証するために呼ばれる。<br>
         * もし EndOfStream まで読み込まれている場合は データが
         * 補填されないことによって それを示す。
         * 
         * @exception IOException 入出力エラーが発生した場合
         */
        private void fillCache() throws IOException {
            this.markPositionIsInCache = false;
            this.cacheLimit            = 0;
            this.cachePosition         = 0;

            //キャッシュにデータを読み込み
            int read = 0;
            while( 0 <= read && this.cacheLimit < this.cache.length ){
                read = this.archive.read( this.cache,
                                          this.cacheLimit, 
                                          this.cache.length - this.cacheLimit );//throws IOException

                if( 0 < read ) this.cacheLimit += read;
            }
        }

    }

    /**
     * LhaFile にある全ての LhaHeader を返す列挙子
     */
    private class HeaderEnumeration implements Enumeration {

        //------------------------------------------------------------------
        //  instance field
        //------------------------------------------------------------------
        //  private int index
        //------------------------------------------------------------------
        /**
         * 現在処理位置
         */
        private int index;

        //------------------------------------------------------------------
        //  constructor
        //------------------------------------------------------------------
        //  public EntryEnumeration()
        //------------------------------------------------------------------
        /**
         * LhaFile にある全ての LhaHeader を返す列挙子を構築する。
         */
        public HeaderEnumeration(){
            this.index = 0;
        }

        //------------------------------------------------------------------
        //  method of java.util.Enumeration
        //------------------------------------------------------------------
        //  public boolean hasMoreElements()
        //  public Object nextElement()
        //------------------------------------------------------------------
        /**
         * 列挙子にまだ要素が残っているかを得る。
         * 
         * @return 列挙子にまだ要素が残っているなら true
         *         残っていなければ false
         * 
         * @exception IllegalStateException
         *                 親の LhaFile が閉じられた場合
         */
        public boolean hasMoreElements(){
            if( LhaFile.this.archive != null ){
                return this.index < LhaFile.this.headers.size();
            }else{
                throw new IllegalStateException();
            }
        }

        /**
         * 列挙子の次の要素を得る。
         * 
         * @return 列挙子の次の要素
         * 
         * @exception IllegalStateException
         *                 親の LhaFile が閉じられた場合。
         * @exception NoSuchElementException
         *                 列挙子に要素が無い場合。
         *                 
         */
        public Object nextElement(){
            if( LhaFile.this.archive != null ){
                if( this.index < LhaFile.this.headers.size() ){
                    return ((LhaHeader)LhaFile.this.headers.elementAt( this.index++ )).clone();
                }else{
                    throw new NoSuchElementException();
                }
            }else{
                throw new IllegalStateException();
            }
        }
    }

}
//end of LhaFile.java
