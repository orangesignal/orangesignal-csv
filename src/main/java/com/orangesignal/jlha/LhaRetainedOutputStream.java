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

import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.Properties;
import java.util.Vector;

/**
 * 接続されたRandomAccessFileに 圧縮データを出力するためのユーティリティクラス。<br>
 * java.util.zip.ZipOutputStream と似たインターフェイスを持つように作った。<br>
 * 圧縮失敗時( 圧縮後サイズが圧縮前サイズを上回った場合 )の処理を自動的に行う。 進捗報告を実装する場合、このような処理をクラス内に隠蔽すると進捗報告は何秒間か 時によっては何十分も応答しなくなる。(例えばギガバイト級のデータを扱った場合) このような事態を避けたい場合は LhaImmediateOutputStreamを使用すること。<br>
 * また、JDK 1.1 以前では RandomAccessFile が setLength を持たないため、 書庫データの後ろに他のデータがある場合でもファイルサイズを切り詰めることが出来ない。 この問題点は常にサイズ0の新しいファイルを開く事によって回避する事ができる。<br>
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
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LhaRetainedOutputStream extends OutputStream {

	/**
	 * 書庫ファイル
	 */
	private RandomAccessFile archive;

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

	/**
	 * 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 */
	private Properties property;

	/**
	 * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param filename 圧縮データを書きこむファイルの名前
	 * @exception FileNotFoundException filename で与えられたファイルが見つからない場合。
	 * @exception SecurityException セキュリティマネージャがファイルへのアクセスを許さない場合。
	 * @see LhaProperty#getProperties()
	 */
	public LhaRetainedOutputStream(final String filename) throws FileNotFoundException {
		if (filename != null) {
			final RandomAccessFile file = new RandomAccessFile(filename, "rw");     // throws FileNotFoundException, SecurityException
			final Properties property = LhaProperty.getProperties();
			constructerHelper(file, property);
		} else {
			throw new NullPointerException("filename");
		}
	}

	/**
	 * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 
	 * @param filename 圧縮データを書きこむファイルの名前
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @exception FileNotFoundException filename で与えられたファイルが見つからない場合。
	 * @exception SecurityException セキュリティマネージャがファイルへのアクセスを許さない場合。
	 * @see LhaProperty
	 */
	public LhaRetainedOutputStream(final String filename, final Properties property) throws FileNotFoundException {
		if (filename != null) {
			final RandomAccessFile file = new RandomAccessFile(filename, "rw");     // throws FileNotFoundException, SecurityException
			constructerHelper(file, property);
		} else {
			throw new NullPointerException("filename");
		}
	}

	/**
	 * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param filename 圧縮データを書きこむファイルの名前
	 * @exception FileNotFoundException filename で与えられたファイルが見つからない場合。
	 * @exception SecurityException セキュリティマネージャがファイルへのアクセスを許さない場合。
	 * @exception IOException JDK1.2 でコンパイルするためだけに存在する。
	 * @see LhaProperty#getProperties()
	 */
	public LhaRetainedOutputStream(final File filename) throws IOException {
		if (filename != null) {
			final RandomAccessFile file = new RandomAccessFile(filename, "rw");     // throws FileNotFoundException, SecurityException
			final Properties property = LhaProperty.getProperties();
			constructerHelper(file, property);
		} else {
			throw new NullPointerException("filename");
		}
	}

	/**
	 * filename のファイルに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 
	 * @param filename 圧縮データを書きこむファイルの名前
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @exception FileNotFoundException filename で与えられたファイルが見つからない場合。
	 * @exception SecurityException セキュリティマネージャがファイルへのアクセスを許さない場合。
	 * @exception IOException JDK1.2 でコンパイルするためだけに存在する。
	 * @see LhaProperty
	 */
	public LhaRetainedOutputStream(final File filename, final Properties property) throws IOException {
		if (filename != null) {
			final RandomAccessFile file = new RandomAccessFile(filename, "rw");     // throws FileNotFoundException, SecurityException
			constructerHelper(file, property);
		} else {
			throw new NullPointerException("filename");
		}
	}

	/**
	 * fileに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param file RandomAccessFile のインスタンス。<br>
	 *            <ul>
	 *            <li>既に close() されていない事。
	 *            <li>コンストラクタの mode には "rw" オプションを使用して、 読みこみと書きこみが出来るように生成されたインスタンスであること。
	 *            </ul>
	 *            の条件を満たすもの。
	 * 
	 * @see LhaProperty#getProperties()
	 */
	public LhaRetainedOutputStream(final RandomAccessFile file) {
		if (file != null) {
			final Properties property = LhaProperty.getProperties();
			constructerHelper(file, property);
		} else {
			throw new NullPointerException("out");
		}
	}

	/**
	 * fileに 圧縮データを出力するOutputStreamを構築する。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param file RandomAccessFile のインスタンス。<br>
	 *            <ul>
	 *            <li>既に close() されていない事。
	 *            <li>コンストラクタの mode には "rw" オプションを使用して、 読みこみと書きこみが出来るように生成されたインスタンスであること。
	 *            </ul>
	 *            の条件を満たすもの。
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @see LhaProperty
	 */
	public LhaRetainedOutputStream(final RandomAccessFile file, final Properties property) {
		if (file != null && property != null) {
			constructerHelper(file, property);                           // throws UnsupportedEncodingException
		} else if (file == null) {
			throw new NullPointerException("null");
		} else {
			throw new NullPointerException("property");
		}

	}

	/**
	 * コンストラクタの初期化処理を担当するメソッド。
	 * 
	 * @param file RandomAccessFile のインスタンス。<br>
	 *            <ul>
	 *            <li>既に close() されていない事。
	 *            <li>コンストラクタの mode には "rw" オプションを使用して、 読みこみと書きこみが出来るように生成されたインスタンスであること。
	 *            </ul>
	 *            の条件を満たすもの。
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 */
	private void constructerHelper(final RandomAccessFile file, final Properties property) {
		archive = file;
		out = null;
		header = null;
		headerpos = -1;
		crc = new CRC16();
		this.property = property;
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * 現在のエントリに1バイトのデータを書きこむ。
	 * 
	 * @param data 書きこむデータ
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final int data) throws IOException {
		if (out != null) {
			if (header != null) {
				crc.update(data);
			}
			out.write(data);
		} else {
			throw new IOException("no entry");
		}
	}

	/**
	 * 現在のエントリに bufferの内容を全て書き出す。
	 * 
	 * @param buffer 書き出すデータの入ったバイト配列
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final byte[] buffer) throws IOException {
		this.write(buffer, 0, buffer.length);
	}

	/**
	 * 現在のエントリに bufferの indexから lengthバイトのデータを書き出す。
	 * 
	 * @param buffer 書き出すデータの入ったバイト配列
	 * @param index buffer内の書き出すべきデータの開始位置
	 * @param length データのバイト数
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final byte[] buffer, final int index, final int length) throws IOException {
		if (out != null) {
			if (header != null) {
				crc.update(buffer, index, length);
			}
			out.write(buffer, index, length);
		} else {
			throw new IOException("no entry");
		}
	}

	/**
	 * 現在書き込み中のエントリのデータを強制的に出力先に書き出す。 これは PostLzssEncoder, LzssOutputStream の規約どおり flush() しなかった場合とは別のデータを出力する。 (大抵の場合は 単に圧縮率が低下するだけである。)
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 * @see LzssOutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		if (out != null) {
			out.flush();                                                   // throws IOException
		} else {
			throw new IOException("no entry");
		}
	}

	/**
	 * 出力先に全てのデータを出力し、ストリームを閉じる。<br>
	 * また、使用していた全てのリソースを解放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		if (out != null) {
			closeEntry();                                                  // throws IOException
		}

		// ターミネータを出力
		archive.write(0);                                                // throws IOException
		try {
			archive.setLength(archive.getFilePointer());            // After Java1.2 throws IOException
		} catch (final NoSuchMethodError error) {
		}

		archive.close();                                                   // throws IOException
		archive = null;
		header = null;
		crc = null;
		property = null;
		rafo = null;
	}

	// ------------------------------------------------------------------
	// original method ( on the model of java.util.zip.ZipOutputStream )

	/**
	 * 新しいエントリを書き込むようにストリームを設定する。<br>
	 * このメソッドは 既に圧縮済みのエントリの場合は putNextEntryAlreadyCompressed(), 未だに圧縮されていない場合は putNextEntryNotYetCompressed() を呼び出す。<br>
	 * 圧縮されているかの判定は、
	 * <ul>
	 * <li>header.getCompressedSize()<br>
	 * <li>header.getCRC()<br>
	 * </ul>
	 * のどれか一つでも LhaHeader.UNKNOWN であれば未だに圧縮されていないとする。<br>
	 * header には正確な OriginalSize が指定されている必要がある。<br>
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception IllegalArgumentException header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
	 */
	public void putNextEntry(final LhaHeader header) throws IOException {
		if (header.getCompressedSize() == LhaHeader.UNKNOWN || header.getCrc() == LhaHeader.UNKNOWN) {
			putNextEntryNotYetCompressed(header);                        // throws IOException
		} else {
			putNextEntryAlreadyCompressed(header);                       // throws IOException
		}
	}

	/**
	 * 既に圧縮済みのエントリを書きこむようにストリームを設定する。<br>
	 * 圧縮済みデータが正しい事は、呼び出し側が保証する事。
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception IllegalArgumentException <ol>
	 *                <li>header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
	 *                <li>header.getComressedSize() が LhaHeader.UNKNOWN を返す場合
	 *                <li>header.getCRC() が LhaHeader.UNKNOWN を返す場合
	 *                </ol>
	 *                の何れか。
	 */
	public void putNextEntryAlreadyCompressed(final LhaHeader header) throws IOException {
		if (header.getOriginalSize() != LhaHeader.UNKNOWN && header.getCompressedSize() != LhaHeader.UNKNOWN && header.getCrc() != LhaHeader.UNKNOWN) {
			if (out != null) {
				closeEntry();
			}

			headerpos = archive.getFilePointer();

			encoding = property.getProperty("lha.encoding");
			if (encoding == null) {
				encoding = LhaProperty.getProperty("lha.encoding");
			}

			archive.write(header.getBytes(encoding));                  // throws IOException
			out = new RandomAccessFileOutputStream(archive, header.getCompressedSize());

		} else if (header.getOriginalSize() == LhaHeader.UNKNOWN) {
			throw new IllegalArgumentException("OriginalSize must not \"LhaHeader.UNKNOWN\".");
		} else if (header.getCompressedSize() == LhaHeader.UNKNOWN) {
			throw new IllegalArgumentException("CompressedSize must not \"LhaHeader.UNKNOWN\".");
		} else {
			throw new IllegalArgumentException("CRC must not \"LhaHeader.UNKNOWN\".");
		}
	}

	/**
	 * 未だに圧縮されていないエントリを書きこむようにストリームを設定する。<br>
	 * header には正確な OriginalSize が指定されている必要がある。<br>
	 * header に CompressedSize, CRCが指定されていても無視される。<br>
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception IllegalArgumentException header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
	 */
	public void putNextEntryNotYetCompressed(final LhaHeader header) throws IOException {
		if (header.getOriginalSize() != LhaHeader.UNKNOWN) {
			if (out != null) {
				closeEntry();
			}

			crc.reset();
			headerpos = archive.getFilePointer();
			this.header = (LhaHeader) header.clone();
			this.header.setCompressedSize(0);
			this.header.setCrc(0);

			encoding = property.getProperty("lha.encoding");
			if (encoding == null) {
				encoding = LhaProperty.getProperty("lha.encoding");
			}

			archive.write(this.header.getBytes(encoding));
			rafo = new RandomAccessFileOutputStream(archive, header.getOriginalSize());
			out = CompressMethod.connectEncoder(rafo, header.getCompressMethod(), property);

		} else {
			throw new IllegalArgumentException("OriginalSize must not \"LhaHeader.UNKNOWN\".");
		}
	}

	/**
	 * 現在出力中のエントリを閉じ、次のエントリが出力可能な状態にする。<br>
	 * 圧縮に失敗した(圧縮後サイズが圧縮前サイズを上回った)場合、 解凍し無圧縮で格納する。エントリのサイズが大きい場合、 この処理にはかなりの時間がかかる。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void closeEntry() throws IOException {
		if (header != null) {
			out.close();

			if (!rafo.cache.isEmpty()) {
				RandomAccessFileInputStream rafi;
				InputStream in;
				long pos = rafo.start;
				rafi = new RandomAccessFileInputStream(archive, rafo);
				in = CompressMethod.connectDecoder(rafi, header.getCompressMethod(), property, header.getOriginalSize());

				final byte[] buffer = new byte[8192];
				int length;
				while (0 <= (length = in.read(buffer))) {
					rafi.cache(pos + length);
					archive.seek(pos);
					archive.write(buffer, 0, length);
					pos += length;
				}
				in.close();

				header.setCompressMethod(CompressMethod.LH0);
			}

			final long pos = archive.getFilePointer();
			final long size = pos - headerpos - header.getBytes(encoding).length;
			header.setCompressedSize(size);
			if (header.getCrc() != LhaHeader.NO_CRC) {
				header.setCrc((int) crc.getValue());
			}

			archive.seek(headerpos);
			archive.write(header.getBytes(encoding));
			archive.seek(pos);
		}
		header = null;
		out = null;
	}

	// ------------------------------------------------------------------
	// inner classes

	/**
	 * RandomAccessFile を OutputStreamの インタフェイスに合わせるためのラッパクラス
	 */
	private static class RandomAccessFileOutputStream extends OutputStream {

		/**
		 * 出力先RandomAccessFile
		 */
		private RandomAccessFile archive;

		/**
		 * 格納限界を超えて書き込もうとした 場合のキャッシュ
		 */
		private final Cache cache;

		/**
		 * 格納開始位置
		 */
		private final long start;

		/**
		 * 現在処理位置
		 */
		private long pos;

		/**
		 * 格納限界
		 */
		private final long limit;

		// ------------------------------------------------------------------
		// Consutructor

		/**
		 * RandomAccessFile をラップした OutputStream を構築する。
		 * 
		 * @param archive 出力先のRandomAccessFile
		 * @param length 出力限界長
		 * @exception IOException 入出力エラーエラーが発生した場合
		 */
		public RandomAccessFileOutputStream(final RandomAccessFile archive, final long length) throws IOException {
			this.archive = archive;
			start = this.archive.getFilePointer();                       // throws IOException
			pos = start;
			limit = start + length;
			cache = new Cache();
		}

		// ------------------------------------------------------------------
		// method of java.io.OutputStream

		/**
		 * 接続された RandomAccessFile に1バイト書きこむ。
		 * 
		 * @param data 書きこむ1byteのデータ
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public void write(final int data) throws IOException {
			if (pos < limit && cache.isEmpty()) {
				pos++;
				archive.write(data);                                     // throws IOException
			} else {
				cache.add(new byte[] { (byte) data });
			}
		}

		/**
		 * 接続された RandomAccessFile に buffer の内容を全て書きこむ。
		 * 
		 * @param buffer 書きこむデータの入ったバイト配列
		 * @exception IOException 入出力エラーが発生した場合
		 * @exception EOFException コンストラクタに渡された長さを超えて 書きこもうとした場合
		 */
		@Override
		public void write(final byte[] buffer) throws IOException {
			this.write(buffer, 0, buffer.length);                             // throws IOException
		}

		/**
		 * 接続されたRandomAccessFileにbufferの内容をindexからlengthバイト書きこむ。
		 * 
		 * @param buffer 書きこむデータの入ったバイト配列
		 * @param index buffer内の書きこむデータの開始位置
		 * @param length 書きこむデータ量
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public void write(final byte[] buffer, final int index, final int length) throws IOException {
			if (pos + length < limit && cache.isEmpty()) {
				pos += length;
				archive.write(buffer, index, length);                    // throws IOException
			} else {
				cache.add(buffer, index, length);
			}
		}

		/**
		 * このストリームを閉じて使用していたリソースを開放する。
		 */
		@Override
		public void close() {
			archive = null;
		}

	}

	/**
	 * RandomAccessFile に InputStreamのインターフェイスをかぶせるラッパクラス。 圧縮後のサイズが圧縮前のサイズを上回ったときに解凍して 無圧縮で格納しなおす処理のために必要。
	 */
	private static class RandomAccessFileInputStream extends InputStream {

		/**
		 * 読み込み元RandomAccessFile
		 */
		private RandomAccessFile archive;

		/**
		 * 前部キャッシュ 書き込みが読み込みを追い越した時のキャッシュ
		 */
		private Cache front;

		/**
		 * 後部キャッシュ 書き込み限界を超えた分のデータのキャッシュ
		 */
		private Cache rear;

		/**
		 * 現在処理位置
		 */
		private long pos;

		/**
		 * 読み込み限界
		 */
		private final long limit;

		// ------------------------------------------------------------------
		// Consutructor

		/**
		 * RandomAccessFile をラップした InputStream を構築する。
		 * 
		 * @param archive データを供給する RandomAccessFile
		 * @param out 直前に圧縮データを受け取っていた RandomAccessFileOutputStream
		 */
		public RandomAccessFileInputStream(final RandomAccessFile archive, final RandomAccessFileOutputStream out) {
			this.archive = archive;
			pos = out.start;
			limit = out.pos;
			front = new Cache();
			rear = out.cache;
		}

		// ------------------------------------------------------------------
		// method of java.io.InputStream

		/**
		 * キャッシュかRandomAccessFileから 1バイトのデータを読み込む。
		 * 
		 * @return 読み込まれた1バイトのデータ<br>読み込むデータが無ければ -1
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read() throws IOException {
			int return_value = front.read();
			if (return_value < 0) {
				if (pos < limit) {
					archive.seek(pos++);
					return_value = archive.read();
				} else {
					return_value = rear.read();
				}
			}
			return return_value;
		}

		/**
		 * キャッシュか RandomAccessFileから bufferを満たすようにデータを読み込む。
		 * 
		 * @param buffer 読み込まれたデータを格納するバッファ
		 * @return 実際に読み込まれたデータ量
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer) throws IOException {
			return this.read(buffer, 0, buffer.length);
		}

		/**
		 * キャッシュか RandomAccessFileから bufferのindexへlengthバイト読み込む。
		 * 
		 * @param buffer 読み込まれたデータを格納するバッファ
		 * @param index buffer内の読み込み開始位置
		 * @param length 読み込むデータ量
		 * @return 実際に読み込まれたデータ量
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public int read(final byte[] buffer, final int index, final int length) throws IOException {
			int count = 0;
			int ret = front.read(buffer, index, length);
			if (0 <= ret) {
				count += ret;
			}

			archive.seek(pos);                                      // throws IOException
			ret = Math.min(length - count, Math.max((int) (limit - pos), 0));
			archive.readFully(buffer, index + count, ret);               // throws IOException
			if (0 <= ret) {
				pos += ret;
				count += ret;
			}

			ret = rear.read(buffer, index + count, length - count);
			if (0 <= ret) {
				count += ret;
			}

			if (0 < count) {
				return count;
			}
			return -1;
		}

		/**
		 * このストリームを閉じ 使用していたリソースを開放する。
		 */
		@Override
		public void close() {
			front = null;
			rear = null;
			archive = null;
		}

		// ------------------------------------------------------------------
		// original method

		/**
		 * posまで読み込んでいなければ、 現在読み込み位置からposまでのデータを 前部キャッシュにデータを追加する。
		 * 
		 * @param pos archive内の書き出し位置
		 */
		public void cache(final long pos) throws IOException {
			final int length = (int) Math.min(limit - this.pos, pos - this.pos);

			final byte[] buffer = new byte[length];
			if (0 < length) {
				archive.seek(this.pos);                                  // throws IOException
				archive.readFully(buffer);                               // throws IOException
				front.add(buffer);

				this.pos += length;
			}
		}

	}

	/**
	 * 書き込み限界を超えた書き込みや 読み込み位置を超えた書き込みをした場合に データをキャッシュするために使用する。
	 */
	private static class Cache {

		/**
		 * byte[] の Vector 各要素は 全て読み込まれたと 同時に捨てられる。
		 */
		private final Vector<byte[]> cache;

		/**
		 * 現在読み込み中の要素
		 */
		private byte[] current;

		/**
		 * currentの現在処理位置
		 */
		private int position;

		// ------------------------------------------------------------------
		// Constructor

		/**
		 * データの一時退避機構を構築する。
		 */
		public Cache() {
			current = null;
			position = 0;
			cache = new Vector<byte[]>();
		}

		/**
		 * キャッシュから 1バイトのデータを 0～255にマップして読み込む。
		 * 
		 * @return 読み込まれた1byteのデータ<br>キャッシュが空でデータが無い場合は -1
		 */
		public int read() {
			if (null != current) {
				final int ret = current[position++] & 0xFF;
				if (current.length <= position) {
					if (0 < cache.size()) {
						current = cache.firstElement();
						cache.removeElementAt(0);
					} else {
						current = null;
					}
					position = 0;
				}

				return ret;
			}
			return -1;
		}

		/**
		 * キャッシュから bufferのindexで始まる場所へlengthバイト読み込む。
		 * 
		 * @param buffer 読み込んだデータを保持するバッファ
		 * @param index buffer内の読み込み開始位置
		 * @param length 読み込むデータ量
		 * @return 実際に読み込まれたデータ量<br>キャッシュが空でデータが無い場合は -1
		 */
		public int read(final byte[] buffer, final int index, final int length) {
			int count = 0;

			while (null != current && count < length) {
				final int copylen = Math.min(current.length - position, length - count);
				System.arraycopy(current, position, buffer, index + count, copylen);

				position += copylen;
				count += copylen;

				if (current.length <= position) {
					if (0 < cache.size()) {
						current = cache.firstElement();
						cache.removeElementAt(0);
					} else {
						current = null;
					}
					position = 0;
				}
			}

			if (count == 0) {
				return -1;
			}
			return count;
		}

		/**
		 * キャッシュにデータを追加する。
		 * 
		 * @param buffer データの格納されたバッファ
		 */
		public void add(final byte[] buffer) {
			if (current == null) {
				current = buffer;
			} else {
				cache.addElement(buffer);
			}
		}

		/**
		 * キャッシュにデータを追加する。
		 * 
		 * @parma buffer データの格納されたバッファ
		 * @param index buffer内のデータ開始位置
		 * @param length 格納されているデータの量
		 */
		public void add(final byte[] buffer, final int index, final int length) {
			final byte[] buf = new byte[length];
			System.arraycopy(buffer, index, buf, 0, length);

			if (current == null) {
				current = buf;
			} else {
				cache.addElement(buf);
			}
		}

		/**
		 * このキャッシュが空かを得る。
		 * 
		 * @return このキャッシュが空なら true 空でなければ false
		 */
		public boolean isEmpty() {
			return current == null;
		}

	}

}