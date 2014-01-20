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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

/**
 * LHA書庫ファイルからエントリデータを読み出す InputStreamを得るためのユーティリティクラス。<br>
 * java.util.zip.ZipFile と似た インターフェイスを持つように作った。 CRC16等によるチェックは行わない。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class LhaFile {

	/**
	 * LHA書庫形式のデータを持つ RandomAccessFileのインスタンス
	 */
	private RandomAccessFile archive;

	/**
	 * 最後に archive にアクセスしたオブジェクト
	 */
	private Object lastAccessObject;

	/**
	 * 各エントリのヘッダを持つ LhaHeader の Vector headers.elementAt( index ) のヘッダを持つエントリは entryPoint.elementAt( index ) の位置から始まる。
	 */
	private Vector<LhaHeader> headers;

	/**
	 * 各エントリの開始位置を持つ Long の Vector headers.elementAt( index ) のヘッダを持つエントリは entryPoint.elementAt( index ) の位置から始まる。
	 */
	private Vector<Long> entryPoint;

	/**
	 * エントリの名前(格納ファイル名)をキーに、 キーの名前のエントリの index を持つハッシュテーブル。 要素は Integer
	 */
	private Hashtable<String, Integer> hash;

	/**
	 * 同名ファイルの救出用。 重複した名前を持つエントリの index を持つ Vector 要素は Integer
	 */
	private Vector<Integer> duplicate;

	/**
	 * 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 */
	private Properties property;

	/**
	 * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
	 * 各圧縮形式に対応した復号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param filename LHA書庫ファイルの名前
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty#getProperties()
	 */
	public LhaFile(final String filename) throws IOException {
		final Properties property = LhaProperty.getProperties();
		final RandomAccessFile file = new RandomAccessFile(filename, "r");          // throws FileNotFoundException SecurityException
		constructerHelper(file, property, false);                        // After Java 1.1 throws UnsupportedEncodingException
	}

	/**
	 * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
	 * 
	 * @param filename LHA書庫ファイルの名前
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception UnsupportedEncodingException property.getProperty( "lha.encoding" ) で得られた エンコーディング名がサポートされない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty
	 */
	public LhaFile(final String filename, final Properties property) throws IOException {
		final RandomAccessFile file = new RandomAccessFile(filename, "r");          // throws FileNotFoundException SecurityException
		constructerHelper(file, property, false);                        // After Java 1.1 throws UnsupportedEncodingException
	}

	/**
	 * filename で指定されたファイルから書庫データを読みこむLhaFileを構築する。<br>
	 * 各圧縮形式に対応した復号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param filename LHA書庫ファイル
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty#getProperties()
	 */
	public LhaFile(final File filename) throws IOException {
		final Properties property = LhaProperty.getProperties();
		final RandomAccessFile file = new RandomAccessFile(filename, "r");          // throws FileNotFoundException SecurityException

		constructerHelper(file, property, false);                        // After Java 1.1 throws UnsupportedEncodingException
	}

	/**
	 * filename で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
	 * 
	 * @param filename LHA書庫ファイル
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception UnsupportedEncodingException property.getProperty( "lha.encoding" ) で得られた エンコーディング名がサポートされない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty
	 */
	public LhaFile(final File filename, final Properties property) throws IOException {
		final RandomAccessFile file = new RandomAccessFile(filename, "r");          // throws FileNotFoundException SecurityException
		constructerHelper(file, property, false);                        // After Java 1.1 throws UnsupportedEncodingException
	}

	/**
	 * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
	 * 各圧縮形式に対応した復号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param file LHA書庫ファイル
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty#getProperties()
	 */
	public LhaFile(final RandomAccessFile file) throws IOException {
		final Properties property = LhaProperty.getProperties();
		constructerHelper(file, property, false);
	}

	/**
	 * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
	 * 各圧縮形式に対応した復号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param file LHA書庫ファイル
	 * @param rescueMode true にすると壊れた書庫のデータを 復旧するための復旧モードでエントリを検索する。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty#getProperties()
	 */
	public LhaFile(final RandomAccessFile file, final boolean rescueMode) throws IOException {
		final Properties property = LhaProperty.getProperties();
		constructerHelper(file, property, rescueMode);
	}

	/**
	 * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
	 * 
	 * @param file LHA書庫ファイル
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty
	 */
	public LhaFile(final RandomAccessFile file, final Properties property) throws IOException {
		constructerHelper(file, property, false);
	}

	/**
	 * file で指定されたファイルから書庫データを読みこむ LhaFile を構築する。<br>
	 * 
	 * @param file LHA書庫ファイル
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @param rescueMode true にすると壊れた書庫のデータを 復旧するための復旧モードでエントリを検索する。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception FileNotFoundException ファイルが見つからない場合
	 * @exception SecurityException セキュリティマネージャがファイルの読み込みを許さない場合
	 * @see LhaProperty
	 */
	public LhaFile(final RandomAccessFile file, final Properties property, final boolean rescueMode) throws IOException {
		constructerHelper(file, property, rescueMode);
	}

	/**
	 * file を走査してエントリ情報を構築する。<br>
	 * 
	 * @param file LHA書庫ファイル
	 * @param propety 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @param rescueMode true にすると壊れた書庫のデータを 復旧するための復旧モードでエントリを検索する。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception UnsupportedEncodingException encodeがサポートされない場合
	 */
	private void constructerHelper(final RandomAccessFile file, final Properties property, final boolean rescueMode) throws IOException {
		headers = new Vector<LhaHeader>();
		entryPoint = new Vector<Long>();

		file.seek(0);
		final CachedRandomAccessFileInputStream archive = new CachedRandomAccessFileInputStream(file);

		byte[] HeaderData = LhaHeader.getFirstHeaderData(archive);
		while (null != HeaderData) {
			final LhaHeader header = LhaHeader.createInstance(HeaderData, property);
			headers.addElement(header);
			entryPoint.addElement(new Long(archive.position()));

			if (!rescueMode) {
				archive.skip(header.getCompressedSize());
				HeaderData = LhaHeader.getNextHeaderData(archive);
			} else {
				HeaderData = LhaHeader.getFirstHeaderData(archive);
			}
		}
		archive.close();

		hash = new Hashtable<String, Integer>();
		duplicate = new Vector<Integer>();
		for (int i = 0; i < headers.size(); i++) {
			final LhaHeader header = headers.elementAt(i);
			if (!hash.containsKey(header.getPath())) {
				hash.put(header.getPath(), new Integer(i));
			} else {
				duplicate.addElement(new Integer(i));
			}
		}

		this.archive = file;
		this.property = (Properties) property.clone();
	}

	// ------------------------------------------------------------------
	// original method ( on the model of java.util.zip.ZipFile )

	/**
	 * header で指定されたエントリの 内容を解凍しながら読みこむ入力ストリームを得る。<br>
	 * 
	 * @param header ヘッダ
	 * @return headerで指定されたヘッダを持つエントリの 内容を読みこむ入力ストリーム。<br>エントリが見つからない場合は null。
	 */
	public InputStream getInputStream(final LhaHeader header) {
		final int index = getIndex(header);
		if (0 <= index) {
			final long start = entryPoint.elementAt(index).longValue();
			final long len = header.getCompressedSize();
			final InputStream in = new RandomAccessFileInputStream(start, len);
			return CompressMethod.connectDecoder(in, header.getCompressMethod(), property, header.getOriginalSize());
		}
		return null;
	}

	/**
	 * nameで指定された名前を持つエントリの 内容を解凍しながら読みこむ入力ストリームを得る。<br>
	 * 
	 * @param name エントリの名前
	 * @return nameで指定された名前を持つエントリの 内容を解凍しながら読みこむ入力ストリーム。<br>
	 *         エントリが見つからない場合は null。
	 */
	public InputStream getInputStream(final String name) {
		if (hash.containsKey(name)) {
			final int index = hash.get(name).intValue();
			final LhaHeader header = headers.elementAt(index);
			final long start = entryPoint.elementAt(index).longValue();
			final long len = header.getCompressedSize();
			final InputStream in = new RandomAccessFileInputStream(start, len);
			return CompressMethod.connectDecoder(in, header.getCompressMethod(), property, header.getOriginalSize());
		}
		return null;
	}

	/**
	 * headerで指定されたエントリの内容を 解凍せずに読みこむ入力ストリームを返す。<br>
	 * 
	 * @param header ヘッダ
	 * @return headerで指定されたエントリの内容を 解凍せずに読みこむ入力ストリーム。<br>
	 *         エントリが見つからない場合は null。
	 */
	public InputStream getInputStreamWithoutExtract(final LhaHeader header) {
		final int index = getIndex(header);
		if (0 <= index) {
			final long start = entryPoint.elementAt(index).longValue();
			final long len = header.getCompressedSize();
			return new RandomAccessFileInputStream(start, len);
		}
		return null;
	}

	/**
	 * nameで指定された名前を持つエントリの 内容を解凍せずに読みこむ入力ストリームを返す。<br>
	 * 
	 * @param name エントリの名前
	 * @return nameで指定された名前を持つエントリの 内容を解凍せずに読みこむ入力ストリーム。<br>
	 *         エントリが見つからない場合は null。
	 */
	public InputStream getInputStreamWithoutExtract(final String name) {
		if (hash.containsKey(name)) {
			final int index = hash.get(name).intValue();
			final LhaHeader header = headers.elementAt(index);
			final long start = entryPoint.elementAt(index).longValue();
			final long len = header.getCompressedSize();
			return new RandomAccessFileInputStream(start, len);
		}
		return null;
	}

	// ------------------------------------------------------------------
	// original method ( on the model of java.util.zip.ZipFile )

	/**
	 * この LhaFile 内のエントリの数を得る。
	 * 
	 * @return ファイル内のエントリの数
	 */
	public int size() {
		return headers.size();
	}

	/**
	 * この LhaFile 内のエントリの LhaHeader の列挙子を得る。
	 * 
	 * @return LhaHeader の列挙子
	 * @exception IllegalStateException LhaFile が close() で閉じられている場合。
	 */
	public Enumeration<LhaHeader> entries() {
		if (archive != null) {
			return new HeaderEnumeration();
		}
		throw new IllegalStateException();
	}

	/**
	 * ファイル内のエントリを列挙した配列を得る。
	 * 
	 * @return ファイル内のエントリを列挙した配列
	 */
	public LhaHeader[] getEntries() {
		final LhaHeader[] headers = new LhaHeader[this.headers.size()];
		for (int i = 0; i < this.headers.size(); i++) {
			headers[i] = (LhaHeader) this.headers.elementAt(i).clone();
		}
		return headers;
	}

	/**
	 * この LHA書庫ファイルを閉じる。 その際、このLhaFileが発行した全ての InputStreamは強制的に閉じられる。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void close() throws IOException {
		archive.close();
		archive = null;
		lastAccessObject = null;
		headers = null;
		entryPoint = null;
		hash = null;
		property = null;
		duplicate = null;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * headers における target の index を得る。
	 * 
	 * @param target ヘッダ
	 * @return headers 内での target の index。 headers 内に target がない場合は -1
	 */
	private int getIndex(final LhaHeader target) {
		int index = hash.get(target.getPath()).intValue();

		LhaHeader header = headers.elementAt(index);
		if (!equal(header, target)) {
			boolean match = false;
			for (int i = 0; i < duplicate.size() && !match; i++) {
				index = duplicate.elementAt(i).intValue();
				header = headers.elementAt(index);

				if (equal(header, target)) {
					match = true;
				}
			}

			if (match) {
				return index;
			}
			return -1;
		}
		return index;
	}

	/**
	 * 2つの LhaHeader、header1 と header2 が同等か調べる。
	 * 
	 * @param header1 検査対象のヘッダ その1
	 * @param header2 検査対象のヘッダ その2
	 * @return header1 と header2 が同等であれば true 違えば false
	 */
	private static boolean equal(final LhaHeader header1, final LhaHeader header2) {
		return header1.getPath().equals(header2.getPath()) && header1.getCompressMethod().equals(header2.getCompressMethod()) && header1.getLastModified().equals(header2.getLastModified()) && header1.getCompressedSize() == header2.getCompressedSize() && header1.getOriginalSize() == header2.getOriginalSize() && header1.getCrc() == header2.getCrc() && header1.getOsid() == header2.getOsid() && header1.getHeaderLevel() == header2.getHeaderLevel();
	}

	// ------------------------------------------------------------------
	// inner classes

	/**
	 * LhaFileのarchiveの ある区間内のデータを得る InputStream。 複数エントリを同時に処理するための 同期処理を含む。
	 */
	private class RandomAccessFileInputStream extends InputStream {

		/**
		 * archive内の現在処理位置
		 */
		private long position;

		/**
		 * archive内のこのInputStreamの読み取り限界
		 */
		private final long end;

		/**
		 * archive内のマーク位置
		 */
		private long markPosition;

		// ------------------------------------------------------------------
		// Constructor

		/**
		 * コンストラクタ。
		 * 
		 * @param start 読みこみ開始位置
		 * @param size データのサイズ
		 */
		public RandomAccessFileInputStream(final long start, final long size) {
			position = start;
			end = start + size;
			markPosition = -1;
		}

		// ------------------------------------------------------------------
		// method of java.io.InputStream

		/**
		 * archiveの現在処理位置から 1byteのデータを読み込む。
		 * 
		 * @return 読みこまれた1byteのデータ<br>既に読みこみ限界に達した場合は -1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read() throws IOException {
			synchronized (archive) {
				if (position < end) {
					if (lastAccessObject != this) {
						archive.seek(position);
					}

					final int data = archive.read();
					if (0 <= data) {
						position++;
					}
					return data;
				}
				return -1;
			}
		}

		/**
		 * archiveの現在処理位置から bufferを満たすようにデータを読み込む。
		 * 
		 * @param buffer 読みこまれたデータを格納するバッファ
		 * @return 読みこまれたバイト数<br>既に読みこみ限界に達していた場合は-1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer) throws IOException {
			return this.read(buffer, 0, buffer.length);
		}

		/**
		 * archiveの現在処理位置から bufferのindexから始まる領域へ lengthバイトのデータを読み込む。
		 * 
		 * @param buffer 読みこまれたデータを格納するバッファ
		 * @param index buffer内の読みこみ開始位置
		 * @param length 読みこむバイト数。
		 * @return 読みこまれたバイト数<br>既に読みこみ限界に達していた場合は-1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer, final int index, int length) throws IOException {
			synchronized (archive) {
				if (position < end) {
					if (lastAccessObject != this) {
						archive.seek(position);
						lastAccessObject = this;
					}

					length = (int) Math.min(end - position, length);
					length = archive.read(buffer, index, length);
					if (0 <= length) {
						position += length;
					}
					return length;
				}
				return -1;
			}
		}

		/**
		 * lengthバイトのデータを読み飛ばす。
		 * 
		 * @param length 読み飛ばしたいバイト数
		 * @return 実際に読み飛ばされたバイト数
		 */
		@Override
		public long skip(final long length) {
			synchronized (archive) {
				final long skiplen = Math.min(end - position, length);
				position += skiplen;

				if (lastAccessObject == this) {
					lastAccessObject = null;
				}

				return skiplen;
			}
		}

		// ------------------------------------------------------------------
		// method of java.io.InputStream

		/**
		 * このオブジェクトがmark/resetをサポートするかを返す。
		 * 
		 * @return このオブジェクトはmark/resetをサポートする。<br>常にtrue。
		 */
		@Override
		public boolean markSupported() {
			return true;
		}

		/**
		 * 現在処理位置にマークを施し次のresetで 現在の処理位置に戻れるようにする。
		 * 
		 * @param readLimit マークの有効限界。 このオブジェクトでは意味を持たない。
		 */
		@Override
		public void mark(final int readLimit) {
			markPosition = position;
		}

		/**
		 * 最後にマークされた処理位置に戻す。
		 * 
		 * @exception IOException mark()されていない場合
		 */
		@Override
		public void reset() throws IOException {
			synchronized (archive) {
				if (0 <= markPosition) {
					position = markPosition;
				} else {
					throw new IOException("not marked");
				}

				if (lastAccessObject == this) {
					lastAccessObject = null;
				}
			}
		}

		/**
		 * 接続された入力ストリームからブロックしないで 読み込むことのできるバイト数を得る。<br>
		 * RandomAccessFileInputStream では 読み込みは常に RandomAccessFile に対する アクセスを伴うため、このメソッドは常に 0 を返す。
		 * 
		 * @return 常に 0<br>
		 */
		@Override
		public int available() {
			return 0;
		}

		/**
		 * この入力ストリームを閉じ、使用していた全てのリソースを開放する。<br>
		 * このメソッドは何も行わない。
		 */
		@Override
		public void close() {
		}

	}

	/**
	 * ヘッダ検索用 の RandomAccessFileInputStream。<br>
	 * バッファリングと同期処理を行わない事によって高速化してある。
	 */
	private static class CachedRandomAccessFileInputStream extends InputStream {

		/**
		 * データを供給する RandomAccessFile
		 */
		private RandomAccessFile archive;

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

		/**
		 * mark位置がキャッシュの範囲内にあるかを示す。 markされたとき true に設定され、 次に in から キャッシュへの読み込みが 行われたときに false に設定される。
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

		// ------------------------------------------------------------------
		// Constructer

		/**
		 * キャッシュを使用して 高速化した RandomAccessFileInputStream を構築する。
		 * 
		 * @param file データを供給する RandomAccessFile
		 */
		public CachedRandomAccessFileInputStream(final RandomAccessFile file) {
			archive = file;
			cache = new byte[1024];
			cachePosition = 0;
			cacheLimit = 0;
		}

		// ------------------------------------------------------------------
		// method of java.io.InputStream

		/**
		 * archiveの現在処理位置から 1byteのデータを読み込む。
		 * 
		 * @return 読みこまれた1byteのデータ<br>既に読みこみ限界に達した場合は -1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read() throws IOException {
			if (cachePosition < cacheLimit) {
				return cache[cachePosition++] & 0xFF;
			}
			fillCache();                                                     // throws IOException

			if (cachePosition < cacheLimit) {
				return cache[cachePosition++] & 0xFF;
			}
			return -1;
		}

		/**
		 * archiveの現在処理位置から bufferを満たすようにデータを読み込む。
		 * 
		 * @param buffer 読みこまれたデータを格納するバッファ
		 * @return 読みこまれたバイト数<br>既に読みこみ限界に達していた場合は-1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer) throws IOException {
			return this.read(buffer, 0, buffer.length);
		}

		/**
		 * archiveの現在処理位置から bufferのindexから始まる領域へ lengthバイトのデータを読み込む。
		 * 
		 * @param buffer 読みこまれたデータを格納するバッファ
		 * @param index buffer内の読みこみ開始位置
		 * @param length 読みこむバイト数。
		 * @return 読みこまれたバイト数<br>既に読みこみ限界に達していた場合は-1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer, int index, int length) throws IOException {
			final int requested = length;

			while (0 < length) {
				if (cacheLimit <= cachePosition) {
					fillCache();                                             // throws IOException
					if (cacheLimit <= cachePosition) {
						if (requested == length) {
							return -1;
						}
						break;
					}
				}

				final int copylen = Math.min(length, cacheLimit - cachePosition);
				System.arraycopy(cache, cachePosition, buffer, index, copylen);

				index += copylen;
				length -= copylen;
				cachePosition += copylen;
			}
			return requested - length;
		}

		/**
		 * lengthバイトのデータを読み飛ばす。
		 * 
		 * @param length 読み飛ばしたいバイト数
		 * @return 実際に読み飛ばされたバイト数
		 */
		@Override
		public long skip(long length) throws IOException {
			final long requested = length;

			if (cachePosition < cacheLimit) {
				final long avail = (long) cacheLimit - cachePosition;
				final long skiplen = Math.min(length, avail);

				length -= skiplen;
				cachePosition += (int) skiplen;
			}

			if (0 < length) {
				final long avail = archive.length() - archive.getFilePointer();
				final long skiplen = Math.min(avail, length);

				length -= skiplen;
				archive.seek(archive.getFilePointer() + skiplen);
			}

			return requested - length;
		}

		/**
		 * このオブジェクトがmark/resetをサポートするかを返す。
		 * 
		 * @return このオブジェクトはmark/resetをサポートする。<br>常にtrue。
		 */
		@Override
		public boolean markSupported() {
			return true;
		}

		/**
		 * 現在処理位置にマークを施し次のresetで 現在の処理位置に戻れるようにする。
		 * 
		 * @param readLimit マークの有効限界。 このオブジェクトでは意味を持たない。
		 */
		@Override
		public void mark(final int readLimit) {
			try {
				markPosition = archive.getFilePointer();
			} catch (final IOException exception) {
				throw new Error("caught IOException( " + exception.getMessage() + " ) in mark()");
			}

			if (markCache == null) {
				markCache = cache.clone();
			} else {
				System.arraycopy(cache, 0, markCache, 0, cacheLimit);
			}

			markCacheLimit = cacheLimit;
			markCachePosition = cachePosition;
			markPositionIsInCache = true;
		}

		/**
		 * 最後にマークされた処理位置に戻す。
		 * 
		 * @exception IOException mark()されていない場合
		 */
		@Override
		public void reset() throws IOException {
			if (markPositionIsInCache) {
				cachePosition = markCachePosition;
			} else if (markCache == null) { // この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。
				throw new IOException("not marked.");
			} else {
				// in が reset() できない場合は
				// 最初の行の this.in.reset() で
				// IOException を投げることを期待している。
				archive.seek(markPosition);                 // throws IOException

				System.arraycopy(markCache, 0, cache, 0, markCacheLimit);
				cacheLimit = markCacheLimit;
				cachePosition = markCachePosition;
			}
		}

		/**
		 * 接続された入力ストリームからブロックしないで 読み込むことのできるバイト数を得る。<br>
		 * 
		 * @return ブロックしないで読み出せるバイト数。<br>
		 */
		@Override
		public int available() {
			return cacheLimit - cachePosition;
		}

		/**
		 * この入力ストリームを閉じ、使用していた 全てのリソースを開放する。<br>
		 */
		@Override
		public void close() {
			archive = null;

			cache = null;
			cachePosition = 0;
			cacheLimit = 0;

			markPositionIsInCache = false;
			markCache = null;
			markCachePosition = 0;
			markCacheLimit = 0;
			markPosition = 0;
		}

		// ------------------------------------------------------------------
		// original method

		/**
		 * ファイル先頭を始点とする現在の読み込み位置を得る。
		 * 
		 * @return 現在の読み込み位置。
		 */
		public long position() throws IOException {
			long position = archive.getFilePointer();
			position -= cacheLimit - cachePosition;
			return position;
		}

		// ------------------------------------------------------------------
		// local method

		/**
		 * 必要がある場合に、キャッシュ用バッファにデータを 補填しキャッシュ用バッファに必ずデータが存在する ことを保証するために呼ばれる。<br>
		 * もし EndOfStream まで読み込まれている場合は データが 補填されないことによって それを示す。
		 * 
		 * @exception IOException 入出力エラーが発生した場合
		 */
		private void fillCache() throws IOException {
			markPositionIsInCache = false;
			cacheLimit = 0;
			cachePosition = 0;

			// キャッシュにデータを読み込み
			int read = 0;
			while (0 <= read && cacheLimit < cache.length) {
				read = archive.read(cache, cacheLimit, cache.length - cacheLimit);// throws IOException

				if (0 < read) {
					cacheLimit += read;
				}
			}
		}

	}

	/**
	 * LhaFile にある全ての LhaHeader を返す列挙子
	 */
	private class HeaderEnumeration implements Enumeration<LhaHeader> {

		/**
		 * 現在処理位置
		 */
		private int index;

		// ------------------------------------------------------------------
		// Constructor

		/**
		 * LhaFile にある全ての LhaHeader を返す列挙子を構築する。
		 */
		public HeaderEnumeration() {
			index = 0;
		}

		// ------------------------------------------------------------------
		// method of java.util.Enumeration

		/**
		 * 列挙子にまだ要素が残っているかを得る。
		 * 
		 * @return 列挙子にまだ要素が残っているなら true 残っていなければ false
		 * 
		 * @exception IllegalStateException 親の LhaFile が閉じられた場合
		 */
		@Override
		public boolean hasMoreElements() {
			if (archive != null) {
				return index < headers.size();
			}
			throw new IllegalStateException();
		}

		/**
		 * 列挙子の次の要素を得る。
		 * 
		 * @return 列挙子の次の要素
		 * @exception IllegalStateException 親の LhaFile が閉じられた場合。
		 * @exception NoSuchElementException 列挙子に要素が無い場合。
		 */
		@Override
		public LhaHeader nextElement() {
			if (archive != null) {
				if (index < headers.size()) {
					return (LhaHeader) headers.elementAt(index++).clone();
				}
				throw new NoSuchElementException();
			}
			throw new IllegalStateException();
		}
	}

}