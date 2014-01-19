//start of LhaInputStream.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaInputStream.java
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
import java.io.BufferedInputStream;
import java.util.Properties;
import jp.gr.java_conf.dangan.io.LimitedInputStream;
import jp.gr.java_conf.dangan.io.DisconnectableInputStream;
import jp.gr.java_conf.dangan.util.lha.LhaHeader;
import jp.gr.java_conf.dangan.util.lha.LhaProperty;
import jp.gr.java_conf.dangan.util.lha.CompressMethod;

//import exceptions
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.NullPointerException;

import java.lang.Error;


/**
 * 接続されたストリームからLHA書庫データを読みこみ、
 * エントリを解凍しつつ読み込むためのユーティリティクラス。<br>
 * java.util.zip.ZipInputStream と似たインターフェイスを持つように作った。<br>
 * 壊れた書庫の処理に関しては壊れたエントリ以降の
 * 壊れていないエントリも正常に読みこめない可能性がある。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaInputStream.java,v $
 * Revision 1.1.2.1  2003/07/20 13:22:31  dangan
 * [bug fix]
 *     getNextEntry() で CompressMethod.connectDecoder に 
 *     this.limit を渡すべきところで this.in を渡していた。
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
 *     書庫終端に達した場合はそれ以上読み込めないように修正。
 *     available() の振る舞いを java.util.zip.ZipInputStream と同じように
 *     エントリの終端に達していない場合は 1 エントリの終端に達した場合は 0 を返すように変更。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1.2.1 $
 */
public class LhaInputStream extends InputStream{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  source
    //------------------------------------------------------------------
    //  private InputStream source
    //  private boolean alreadyOpenedFirstEnrty
    //  private boolean reachedEndOfArchive
    //------------------------------------------------------------------
    /**
     * LHA書庫形式のデータを供給するInputStream。
     */
    private InputStream source;

    /**
     * 既に最初のエントリを読み込んでいるかを示す。
     */
    private boolean alreadyOpenedFirstEnrty;

    /**
     * 書庫終端に達したかを示す。
     */
    private boolean reachedEndOfArchive;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  for taking out a file from the archive
    //------------------------------------------------------------------
    //  private InputStream in
    //  private LimitedInputStream limit
    //  private boolean reachedEndOfEntry
    //------------------------------------------------------------------
    /**
     * LHA書庫内の１エントリの解凍されたデータ
     * を供給する InputStream。
     */
    private InputStream in;

    /**
     * LHA書庫内の１エントリの圧縮されたデータ
     * を供給するLimitedInputStream。
     * closeEntry 時にスキップするため。
     */
    private LimitedInputStream limit;

    /**
     * 現在処理中のエントリの終端に達した時に true にセットされる。
     */
    private boolean reachedEndOfEntry;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  backup for mark/reset
    //------------------------------------------------------------------
    //  private boolean markReachedEndOfEntry
    //------------------------------------------------------------------
    /** reachEndOfEntry のバックアップ用 */
    private boolean markReachedEndOfEntry;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  properties
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
    //  private LhaInputStream()
    //  public LhaInputStream( InputStream in )
    //  public LhaInputStream( InputStream in, Properties property )
    //  private void constructerHelper( InputStream in, Properties property )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private LhaInputStream(){   }

    /**
     * in から LHA書庫のデータを読み取る InputStream を構築する。<br>
     * 各圧縮形式に対応した復号器の生成式等を持つプロパティには
     * LhaProperty.getProperties() で得られたプロパティが使用される。<br>
     * 
     * @param in LHA書庫形式のデータを供給する入力ストリーム
     * 
     * @see LhaProperty#getProperties()
     */
    public LhaInputStream( InputStream in ){
        Properties property = LhaProperty.getProperties();

        try{
            this.constructerHelper( in, property );                             //After Java 1.1 throws UnsupportedEncodingException
        }catch( UnsupportedEncodingException exception ){
            throw new Error( "Unsupported encoding \"" + property.getProperty( "lha.encoding" ) + "\"." );
        }
    }

    /**
     * in から LHA書庫のデータを読み取る InputStreamを構築する。<br>
     * 
     * @param in       LHA書庫形式のデータを供給する入力ストリーム
     * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * 
     * @exception UnsupportedEncodingException
     *                 property.getProperty( "lha.encoding" ) で得られた
     *                 エンコーディング名がサポートされない場合
     */
    public LhaInputStream( InputStream in, Properties property )
                                         throws UnsupportedEncodingException {

        this.constructerHelper( in, property );                                 //After Java 1.1 throws UnsupportedEncodingException
    }

    /**
     * コンストラクタの初期化処理を担当するメソッド。
     * 
     * @param in       LHA書庫形式のデータを供給する入力ストリーム
     * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
     * 
     * @exception UnsupportedEncodingException
     *               encode がサポートされない場合
     */
    private void constructerHelper( InputStream in, Properties property )
                                        throws UnsupportedEncodingException {

        if( in != null && property != null ){
            String encoding = property.getProperty( "lha.encoding" ); 
            if( encoding == null ){
                encoding = LhaProperty.getProperty( "lha.encoding" );
            }

            //encoding名チェック
            encoding.getBytes( encoding );                                      //After Java 1.1 throws UnsupportedEncodingException

            if( in.markSupported() ){
                this.source = in;
            }else{
                this.source = new BufferedInputStream( in );
            }

            this.in                  = null;
            this.limit               = null;
            this.property            = (Properties)property.clone();
            this.reachedEndOfEntry   = false;
            this.reachedEndOfArchive = false;

        }else if( in == null ){
            throw new NullPointerException( "in" );
        }else{
            throw new NullPointerException( "property" );
        }
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
     * 現在のエントリから 1バイトのデータを読み込む。
     * 
     * @return 読みこまれた 1バイトのデータ。<br>
     *         既にエントリの終端に達した場合は -1
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     */
    public int read() throws IOException {
        if( this.in != null ){
            int ret = this.in.read();                                           //throws IOException
            if( ret < 0 ){
                this.reachedEndOfEntry = true;
            }
            return ret;
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 現在のエントリから buffer を満たすようにデータを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * 
     * @return 読みこまれたデータの量。<br>
     *         既にエントリの終端に達した場合は -1。
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     */
    public int read( byte[] buffer ) throws IOException {
        return this.read( buffer, 0, buffer.length );                           //throws IOException
    }

    /**
     * 現在のエントリから buffer のindexへ lengthバイトの
     * データをを読み込む。
     * 
     * @param buffer データを読み込むバッファ
     * @param index  buffer内のデータ読み込み開始位置
     * @param length 読み込むデータ量
     * 
     * @return 読みこまれたデータの量。<br>
     *         既にエントリの終端に達した場合は -1。
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     */
    public int read( byte[] buffer, int index, int length ) throws IOException {
        if( this.in != null ){
            int ret = this.in.read( buffer, index, length );                    //throws IOException
            if( ret < 0 ){
                this.reachedEndOfEntry = true;
            }
            return ret;
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 現在のエントリのデータを length バイト読みとばす。
     * 
     * @param length 読みとばすデータ量
     * 
     * @return 実際に読みとばしたデータ量
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     */
    public long skip( long length ) throws IOException {
        if( this.in != null ){
            if( 0 < length ){
                long len = this.in.skip( length - 1 );                          //throws IOException
                int ret  = this.in.read();                                      //throws IOException
                if( ret < 0 ){
                    this.reachedEndOfEntry = true;
                    return len;
                }else{
                    return len + 1;
                }
            }else{
                return 0;
            }
        }else{
            throw new IOException( "no entry" );
        }
    }


    //------------------------------------------------------------------
    //  method of java.io.InputStream
    //------------------------------------------------------------------
    //  mark/reset
    //------------------------------------------------------------------
    //  public void mark()
    //  public void reset()
    //  public boolean markSupported()
    //------------------------------------------------------------------
    /**
     * 現在読み取り中のエントリの現在位置にマークを設定し、
     * reset() でマークした読み込み位置に戻れるようにする。<br>
     *
     * @param readLimit マーク位置に戻れる限界読み込み量。
     *                  このバイト数を超えてデータを読み込んだ場合 
     *                  reset() できる保証はない。
     * 
     * @exception IllegalStateException
     *                  現在読み込み中のエントリが無い場合
     */
    public void mark( int readLimit ){
        if( this.in != null ){
            this.in.mark( readLimit );
            this.markReachedEndOfEntry = this.reachedEndOfEntry;
        }else{
            throw new IllegalStateException();
        }
    }

    /**
     * 現在読み取り中のエントリの読み込み位置を最後に
     * mark() メソッドが呼び出されたときの位置に設定する。
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     */
    public void reset() throws IOException {
        if( this.in != null ){
            this.in.reset();                                                    //throws IOException
            this.reachedEndOfEntry = this.markReachedEndOfEntry;
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * 接続された入力ストリームが mark()と
     * reset()をサポートするかを得る。<br>
     * ヘッダ読み込み時に mark/reset が必須のため
     * コンストラクタで渡された in が markSupported() で 
     * false を返す場合、このクラスは in を mark/reset をサポートする
     * BufferedInputStream でラップする。
     * そのため、このメソッドは常に true を返す。
     * 
     * @return 常に true
     */
    public boolean markSupported(){
        return this.source.markSupported();
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
     * 現在読み取り中のエントリの終端に達したかを得る。<br>
     * ブロックしないで読み込めるデータ量を返さない事に注意すること。
     * 
     * @return 現在読み取り中のエントリの終端に達した場合 0 達していない場合 1
     * 
     * @exception IOException 現在読み込み中のエントリが無いか
     *                        入出力エラーが発生した場合
     * 
     * @see java.util.zip.ZipInputStream#available()
     */
    public int available() throws IOException {
        if( this.in != null ){
            return ( this.reachedEndOfEntry ? 0 : 1 );
        }else{
            throw new IOException( "no entry" );
        }
    }

    /**
     * この入力ストリームを閉じ、使用していた
     * 全てのリソースを開放する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void close() throws IOException {
        if( this.in != null ){
            this.in.close();
            this.limit = null;
            this.in    = null;
        }

        this.source.close();
        this.source    = null;
    }


    //------------------------------------------------------------------
    //  original method  ( on the model of java.util.zip.ZipInputStream )
    //------------------------------------------------------------------
    //  manipulate entry
    //------------------------------------------------------------------
    //  public LhaHeader getNextEntry()
    //  public LhaHeader getNextEntryWithoutExtract()
    //  public void closeEntry()
    //------------------------------------------------------------------
    /**
     * 次のエントリを解凍しながら読みこむようにストリームを設定する。<br>
     * 
     * @return エントリの情報を持つ LhaHeader
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public LhaHeader getNextEntry() throws IOException {
        if( !this.reachedEndOfArchive ){
            if( this.in != null ){
                this.closeEntry();                                                  //throws IOException
            }

            byte[] HeaderData;
            if( this.alreadyOpenedFirstEnrty ){
                HeaderData = LhaHeader.getNextHeaderData( this.source );
            }else{
                HeaderData = LhaHeader.getFirstHeaderData( this.source );
                this.alreadyOpenedFirstEnrty = true;
            }
            if( null != HeaderData ){
                LhaHeader header = LhaHeader.createInstance( HeaderData, this.property );
                this.in    = new DisconnectableInputStream( this.source );
                this.limit = new LimitedInputStream( this.in, header.getCompressedSize() );
                this.in    = CompressMethod.connectDecoder( this.limit, 
                                                            header.getCompressMethod(), 
                                                            this.property,
                                                            header.getOriginalSize() );

                this.reachedEndOfEntry     = false;
                this.markReachedEndOfEntry = false;
                return header;
            }else{
                this.reachedEndOfArchive = true;
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 次のエントリを解凍しないで読みこむようにストリームを設定する。<br>
     * 
     * @return エントリの情報を持つ LhaHeader
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public LhaHeader getNextEntryWithoutExtract() throws IOException {

        if( !this.reachedEndOfArchive ){

            if( this.in != null ){
                this.closeEntry();                                                  //throws IOException
            }

            byte[] HeaderData;
            if( this.alreadyOpenedFirstEnrty ){
                HeaderData = LhaHeader.getNextHeaderData( this.source );
            }else{
                HeaderData = LhaHeader.getFirstHeaderData( this.source );
                this.alreadyOpenedFirstEnrty = true;
            }
            if( HeaderData != null ){

                LhaHeader header = LhaHeader.createInstance( HeaderData, this.property );
                this.in    = new DisconnectableInputStream( this.source );
                this.limit = new LimitedInputStream( this.in, header.getCompressedSize() );
                this.in    = this.limit;

                this.reachedEndOfEntry     = false;
                this.markReachedEndOfEntry = false;
                return header;
            }else{
                this.reachedEndOfArchive = true;
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * 現在読み取り中のエントリを閉じ、
     * 次のエントリを読みこめるようにストリームを設定する。
     * 
     * @exception IOException 入出力エラーが発生した場合
     */
    public void closeEntry() throws IOException {
        if( this.in != null ){
            while( 0 <= this.limit.read() ){
                this.limit.skip( Long.MAX_VALUE );
            }

            this.in.close();
            this.in    = null;
            this.limit = null;
        }
    }

}
//end of LhaInputStream.java
