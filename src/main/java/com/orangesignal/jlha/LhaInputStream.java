/**
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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 接続されたストリームからLHA書庫データを読みこみ、 エントリを解凍しつつ読み込むためのユーティリティクラス。<br>
 * java.util.zip.ZipInputStream と似たインターフェイスを持つように作った。<br>
 * 壊れた書庫の処理に関しては壊れたエントリ以降の 壊れていないエントリも正常に読みこめない可能性がある。<br>
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
 * @author $Author: dangan $
 * @version $Revision: 1.1.2.1 $
 */
public class LhaInputStream extends InputStream {

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

	/**
	 * LHA書庫内の１エントリの解凍されたデータ を供給する InputStream。
	 */
	private InputStream in;

	/**
	 * LHA書庫内の１エントリの圧縮されたデータ を供給するLimitedInputStream。 closeEntry 時にスキップするため。
	 */
	private LimitedInputStream limit;

	/**
	 * 現在処理中のエントリの終端に達した時に true にセットされる。
	 */
	private boolean reachedEndOfEntry;

	/**
	 * reachEndOfEntry のバックアップ用
	 */
	private boolean markReachedEndOfEntry;

	/**
	 * 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 */
	private Properties property;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * in から LHA書庫のデータを読み取る InputStream を構築する。<br>
	 * 各圧縮形式に対応した復号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param in LHA書庫形式のデータを供給する入力ストリーム
	 * 
	 * @see LhaProperty#getProperties()
	 */
	public LhaInputStream(final InputStream in) {
		final Properties property = LhaProperty.getProperties();
		try {
			constructerHelper(in, property);                             // After Java 1.1 throws UnsupportedEncodingException
		} catch (final UnsupportedEncodingException exception) {
			throw new Error("Unsupported encoding \"" + property.getProperty("lha.encoding") + "\".");
		}
	}

	/**
	 * in から LHA書庫のデータを読み取る InputStreamを構築する。<br>
	 * 
	 * @param in LHA書庫形式のデータを供給する入力ストリーム
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @exception UnsupportedEncodingException property.getProperty( "lha.encoding" ) で得られた エンコーディング名がサポートされない場合
	 */
	public LhaInputStream(final InputStream in, final Properties property) throws UnsupportedEncodingException {
		constructerHelper(in, property);                                 // After Java 1.1 throws UnsupportedEncodingException
	}

	/**
	 * コンストラクタの初期化処理を担当するメソッド。
	 * 
	 * @param in LHA書庫形式のデータを供給する入力ストリーム
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @exception UnsupportedEncodingException encode がサポートされない場合
	 */
	private void constructerHelper(final InputStream in, final Properties property) throws UnsupportedEncodingException {
		if (in != null && property != null) {
			String encoding = property.getProperty("lha.encoding");
			if (encoding == null) {
				encoding = LhaProperty.getProperty("lha.encoding");
			}

			// encoding名チェック
			encoding.getBytes(encoding);                                      // After Java 1.1 throws UnsupportedEncodingException

			if (in.markSupported()) {
				source = in;
			} else {
				source = new BufferedInputStream(in);
			}

			this.in = null;
			limit = null;
			this.property = (Properties) property.clone();
			reachedEndOfEntry = false;
			reachedEndOfArchive = false;

		} else if (in == null) {
			throw new NullPointerException("in");
		} else {
			throw new NullPointerException("property");
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.InputStream

	/**
	 * 現在のエントリから 1バイトのデータを読み込む。
	 * 
	 * @return 読みこまれた 1バイトのデータ。<br>
	 * 既にエントリの終端に達した場合は -1
	 * 
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 */
	@Override
	public int read() throws IOException {
		if (in != null) {
			final int ret = in.read();                                           // throws IOException
			if (ret < 0) {
				reachedEndOfEntry = true;
			}
			return ret;
		}
		throw new IOException("no entry");
	}

	/**
	 * 現在のエントリから buffer を満たすようにデータを読み込む。
	 * 
	 * @param buffer データを読み込むバッファ
	 * @return 読みこまれたデータの量。<br>既にエントリの終端に達した場合は -1。
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 */
	@Override
	public int read(final byte[] buffer) throws IOException {
		return this.read(buffer, 0, buffer.length);                           // throws IOException
	}

	/**
	 * 現在のエントリから buffer のindexへ lengthバイトの データをを読み込む。
	 * 
	 * @param buffer データを読み込むバッファ
	 * @param index buffer内のデータ読み込み開始位置
	 * @param length 読み込むデータ量
	 * @return 読みこまれたデータの量。<br>既にエントリの終端に達した場合は -1。
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 */
	@Override
	public int read(final byte[] buffer, final int index, final int length) throws IOException {
		if (in != null) {
			final int ret = in.read(buffer, index, length);                    // throws IOException
			if (ret < 0) {
				reachedEndOfEntry = true;
			}
			return ret;
		}
		throw new IOException("no entry");
	}

	/**
	 * 現在のエントリのデータを length バイト読みとばす。
	 * 
	 * @param length 読みとばすデータ量
	 * @return 実際に読みとばしたデータ量
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 */
	@Override
	public long skip(final long length) throws IOException {
		if (in != null) {
			if (0 < length) {
				final long len = in.skip(length - 1);                          // throws IOException
				final int ret = in.read();                                      // throws IOException
				if (ret < 0) {
					reachedEndOfEntry = true;
					return len;
				}
				return len + 1;
			}
			return 0;
		}
		throw new IOException("no entry");
	}

	/**
	 * 現在読み取り中のエントリの現在位置にマークを設定し、 reset() でマークした読み込み位置に戻れるようにする。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界読み込み量。 このバイト数を超えてデータを読み込んだ場合 reset() できる保証はない。
	 * @exception IllegalStateException 現在読み込み中のエントリが無い場合
	 */
	@Override
	public void mark(final int readLimit) {
		if (in != null) {
			in.mark(readLimit);
			markReachedEndOfEntry = reachedEndOfEntry;
		} else {
			throw new IllegalStateException();
		}
	}

	/**
	 * 現在読み取り中のエントリの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。
	 * 
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 */
	@Override
	public void reset() throws IOException {
		if (in != null) {
			in.reset();                                                    // throws IOException
			reachedEndOfEntry = markReachedEndOfEntry;
		} else {
			throw new IOException("no entry");
		}
	}

	/**
	 * 接続された入力ストリームが mark()と reset()をサポートするかを得る。<br>
	 * ヘッダ読み込み時に mark/reset が必須のため コンストラクタで渡された in が markSupported() で false を返す場合、このクラスは in を mark/reset をサポートする BufferedInputStream でラップする。 そのため、このメソッドは常に true を返す。
	 * 
	 * @return 常に true
	 */
	@Override
	public boolean markSupported() {
		return source.markSupported();
	}

	/**
	 * 現在読み取り中のエントリの終端に達したかを得る。<br>
	 * ブロックしないで読み込めるデータ量を返さない事に注意すること。
	 * 
	 * @return 現在読み取り中のエントリの終端に達した場合 0 達していない場合 1
	 * @exception IOException 現在読み込み中のエントリが無いか 入出力エラーが発生した場合
	 * @see java.util.zip.ZipInputStream#available()
	 */
	@Override
	public int available() throws IOException {
		if (in != null) {
			return reachedEndOfEntry ? 0 : 1;
		}
		throw new IOException("no entry");
	}

	/**
	 * この入力ストリームを閉じ、使用していた 全てのリソースを開放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		if (in != null) {
			in.close();
			limit = null;
			in = null;
		}
		source.close();
		source = null;
	}

	// ------------------------------------------------------------------
	// original method ( on the model of java.util.zip.ZipInputStream )

	/**
	 * 次のエントリを解凍しながら読みこむようにストリームを設定する。<br>
	 * 
	 * @return エントリの情報を持つ LhaHeader
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public LhaHeader getNextEntry() throws IOException {
		if (!reachedEndOfArchive) {
			if (in != null) {
				closeEntry();                                                  // throws IOException
			}

			byte[] HeaderData;
			if (alreadyOpenedFirstEnrty) {
				HeaderData = LhaHeader.getNextHeaderData(source);
			} else {
				HeaderData = LhaHeader.getFirstHeaderData(source);
				alreadyOpenedFirstEnrty = true;
			}
			if (null != HeaderData) {
				final LhaHeader header = LhaHeader.createInstance(HeaderData,
						property);
				in = new DisconnectableInputStream(source);
				limit = new LimitedInputStream(in, header.getCompressedSize());
				in = CompressMethod.connectDecoder(limit,
						header.getCompressMethod(), property,
						header.getOriginalSize());

				reachedEndOfEntry = false;
				markReachedEndOfEntry = false;
				return header;
			}
			reachedEndOfArchive = true;
			return null;
		}
		return null;
	}

	/**
	 * 次のエントリを解凍しないで読みこむようにストリームを設定する。<br>
	 * 
	 * @return エントリの情報を持つ LhaHeader
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public LhaHeader getNextEntryWithoutExtract() throws IOException {
		if (!reachedEndOfArchive) {
			if (in != null) {
				closeEntry();                                                  // throws IOException
			}

			byte[] HeaderData;
			if (alreadyOpenedFirstEnrty) {
				HeaderData = LhaHeader.getNextHeaderData(source);
			} else {
				HeaderData = LhaHeader.getFirstHeaderData(source);
				alreadyOpenedFirstEnrty = true;
			}
			if (HeaderData != null) {

				final LhaHeader header = LhaHeader.createInstance(HeaderData,
						property);
				in = new DisconnectableInputStream(source);
				limit = new LimitedInputStream(in, header.getCompressedSize());
				in = limit;

				reachedEndOfEntry = false;
				markReachedEndOfEntry = false;
				return header;
			}
			reachedEndOfArchive = true;
			return null;
		}
		return null;
	}

	/**
	 * 現在読み取り中のエントリを閉じ、 次のエントリを読みこめるようにストリームを設定する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void closeEntry() throws IOException {
		if (in != null) {
			while (0 <= limit.read()) {
				limit.skip(Long.MAX_VALUE);
			}
			in.close();
			in = null;
			limit = null;
		}
	}

}