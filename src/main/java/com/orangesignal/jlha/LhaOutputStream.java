/**
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

package com.orangesignal.jlha;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

/**
 * 接続されたストリームに 圧縮データを出力するためのユーティリティクラス。<br>
 * java.util.zip.ZipOutputStream と似たインターフェイスを持つように作った。 Zipと違い、LHAの出力は本来 2パスであるため、1つのエントリを圧縮するまで、 エントリ全体のデータを持つ一時記憶領域が必要となる。 そのような記憶領域を使用したくない場合は LhaRetainedOutputStream か LhaImmediateOutputStream を使用する事。<br>
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
 * @author $Author: dangan $
 * @version $Revision: 1.1.2.2 $
 */
public class LhaOutputStream extends OutputStream {

	/**
	 * 圧縮データを出力するストリーム
	 */
	private OutputStream out;

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

	/**
	 * 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 */
	private Properties property;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * out に 圧縮データを出力するOutputStreamを構築する。<br>
	 * 一時退避機構はメモリを使用する。このため、 圧縮時データ量がメモリ量を超えるようなファイルは圧縮できない。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param out 圧縮データを出力するストリーム
	 * @see LhaProperty#getProperties()
	 */
	public LhaOutputStream(final OutputStream out) {
		if (out != null) {
			final Properties property = LhaProperty.getProperties();
			constructerHelper(out, new TemporaryBuffer(), property);         // throws UnsupportedEncodingException
		} else {
			throw new NullPointerException("out");
		}
	}

	/**
	 * out に 圧縮データを出力するOutputStreamを構築する。<br>
	 * 一時退避機構はメモリを使用する。このため、 圧縮時データ量がメモリ量を超えるようなファイルは圧縮できない。<br>
	 * 
	 * @param out 圧縮データを出力するストリーム
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @see LhaProperty
	 */
	public LhaOutputStream(final OutputStream out, final Properties property) {
		if (out != null && property != null) {
			constructerHelper(out, new TemporaryBuffer(), property);         // throws UnsupportedEncodingException
		} else if (out == null) {
			throw new NullPointerException("out");
		} else {
			throw new NullPointerException("property");
		}
	}

	/**
	 * out に 圧縮データを出力するOutputStreamを構築する。<br>
	 * 各圧縮形式に対応した符号器の生成式等を持つプロパティには LhaProperty.getProperties() で得られたプロパティが使用される。<br>
	 * 
	 * @param out 圧縮データを出力するストリーム
	 * @param file RandomAccessFile のインスタンス。<br>
	 * <ul>
	 * <li>既に close() されていない事。
	 * <li>コンストラクタの mode には "rw" オプションを使用して、 読みこみと書きこみが出来るように生成されたインスタンスであること。
	 * </ul>
	 * の条件を満たすもの。
	 * 
	 * @see LhaProperty#getProperties()
	 */
	public LhaOutputStream(final OutputStream out, final RandomAccessFile file) {
		if (out != null && file != null) {
			final Properties property = LhaProperty.getProperties();
			constructerHelper(out, new TemporaryFile(file), property); // throws UnsupportedEncodingException
		} else if (out == null) {
			throw new NullPointerException("out");
		} else {
			throw new NullPointerException("file");
		}
	}

	/**
	 * out に 圧縮データを出力するOutputStreamを構築する。<br>
	 * 
	 * @param out 圧縮データを出力するストリーム
	 * @param file RandomAccessFile のインスタンス。<br>
	 * <ul>
	 * <li>既に close() されていない事。
	 * <li>コンストラクタの mode には "rw" オプションを使用して、 読みこみと書きこみが出来るように生成されたインスタンスであること。
	 * </ul>
	 * の条件を満たすもの。
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @exception UnsupportedEncodingException encode がサポートされない場合
	 * @see LhaProperty
	 */
	public LhaOutputStream(final OutputStream out, final RandomAccessFile file, final Properties property) {
		if (out != null && file != null && property != null) {
			constructerHelper(out, new TemporaryFile(file), property);     // throws UnsupportedEncodingException
		} else if (out == null) {
			throw new NullPointerException("out");
		} else if (file == null) {
			throw new NullPointerException("file");
		} else {
			throw new NullPointerException("property");
		}
	}

	/**
	 * コンストラクタの初期化処理を担当するメソッド。
	 * 
	 * @param out LHA書庫形式のデータを出力する出力ストリーム
	 * @param temp 圧縮データの一時退避機構
	 * @param encode ヘッダ内の文字列を変換するのに使用する エンコード日本では シフトJIS(SJIS,MS932, CP932等)を使用する事
	 * @exception UnsupportedEncodingException encode がサポートされない場合
	 */
	private void constructerHelper(final OutputStream out, final Temporary temp, final Properties property) {
		this.out = out;
		this.temp = temp;
		this.property = property;

		crc = new CRC16();
		header = null;
		tempOut = null;
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * 現在のエントリに1バイトのデータを書きこむ。<br>
	 * 
	 * @param data 書きこむデータ
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final int data) throws IOException {
		if (tempOut != null) {
			if (header != null) {
				crc.update(data);
			}
			tempOut.write(data);
			length++;
		} else {
			throw new IOException("no entry");
		}
	}

	/**
	 * 現在のエントリに bufferの内容を全て書き出す。<br>
	 * 
	 * @param buffer 書き出すデータの入ったバイト配列
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final byte[] buffer) throws IOException {
		this.write(buffer, 0, buffer.length);
	}

	/**
	 * 現在のエントリに bufferの indexから lengthバイトのデータを書き出す。<br>
	 * 
	 * @param buffer 書き出すデータの入ったバイト配列
	 * @param index buffer内の書き出すべきデータの開始位置
	 * @param length データのバイト数
	 * @exception IOException 入出力エラーが発生した場合。
	 */
	@Override
	public void write(final byte[] buffer, final int index, final int length) throws IOException {
		if (tempOut != null) {
			if (header != null) {
				crc.update(buffer, index, length);
			}
			tempOut.write(buffer, index, length);
			this.length += length;
		} else {
			throw new IOException("no entry");
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * flush は二つの動作を行う。 一つは現在書き込み中のエントリのデータを 一時退避機構に送りこむように指示する。 これは PostLzssDecoder、LzssOutputStream の規約どおり flush() しなかった場合と 同じデータが出力される事を保証しない。 もう一つは 実際の出力先を flush() する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 * @see LzssOutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		if (tempOut != null) {
			tempOut.flush();                                               // throws IOException
		}
		if (tempOut != out) {
			out.flush();                                                   // throws IOException
		}
	}

	/**
	 * 出力先に全てのデータを出力し、 ストリームを閉じる。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		if (tempOut != null) {
			closeEntry();                                                  // throws IOException
		}

		// ターミネータを出力
		out.write(0);                                                    // throws IOException
		out.close();                                                       // throws IOException
		out = null;

		temp.close();
		temp = null;

		property = null;
		crc = null;
		header = null;
	}

	// ------------------------------------------------------------------
	// original method ( on the model of java.util.zip.ZipOutputStream )

	/**
	 * 新しいエントリを書き込むようにストリームを設定する。<br>
	 * このメソッドは 既に圧縮済みのエントリの場合は putNextEntryAlreadyCompressed(), 未だに圧縮されていない場合は putNextEntryNotYetCompressed() を呼び出す。<br>
	 * 圧縮されているかの判定は、
	 * <ul>
	 * <li>header.getCompressedSize()<br>
	 * <li>header.getOriginalSize()<br>
	 * <li>header.getCRC()<br>
	 * </ul>
	 * のどれか一つでも LhaHeader.UNKNOWN であれば未だに圧縮されていないとする。
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void putNextEntry(final LhaHeader header) throws IOException {
		if (header.getCompressedSize() == LhaHeader.UNKNOWN
				|| header.getOriginalSize() == LhaHeader.UNKNOWN
				|| header.getCrc() == LhaHeader.UNKNOWN) {
			putNextEntryNotYetCompressed(header);                        // throws IOException
		} else {
			putNextEntryAlreadyCompressed(header);                       // throws IOException
		}
	}

	/**
	 * 既に圧縮済みのエントリを書きこむようにストリームを設定する。<br>
	 * 圧縮済みなので、一時退避機構を経ずに直接出力先に出力される。 圧縮済みデータが正しい事は、呼び出し側が保証する事。
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception IllegalArgumentException <ol>
	 * <li>header.getOriginalSize() が LhaHeader.UNKNOWN を返す場合
	 * <li>header.getComressedSize() が LhaHeader.UNKNOWN を返す場合
	 * <li>header.getCRC() が LhaHeader.UNKNOWN を返す場合
	 * </ol>
	 * の何れか。
	 */
	public void putNextEntryAlreadyCompressed(final LhaHeader header) throws IOException {
		if (header.getOriginalSize() != LhaHeader.UNKNOWN
				&& header.getCompressedSize() != LhaHeader.UNKNOWN
				&& header.getCrc() != LhaHeader.UNKNOWN) {

			if (tempOut != null) {
				closeEntry();                                              // throws IOException
			}

			String encoding = property.getProperty("lha.encoding");
			if (encoding == null) {
				encoding = LhaProperty.getProperty("lha.encoding");
			}
			out.write(header.getBytes(encoding));                      // throws IOException
			tempOut = out;

		} else if (header.getOriginalSize() == LhaHeader.UNKNOWN) {
			throw new IllegalArgumentException(
					"OriginalSize must not \"LhaHeader.UNKNOWN\".");
		} else if (header.getCompressedSize() == LhaHeader.UNKNOWN) {
			throw new IllegalArgumentException(
					"CompressedSize must not \"LhaHeader.UNKNOWN\".");
		} else {
			throw new IllegalArgumentException(
					"CRC must not \"LhaHeader.UNKNOWN\".");
		}
	}

	/**
	 * 未だに圧縮されていないエントリを書きこむようにストリームを設定する。<br>
	 * header に OriginalSize, CompressedSize, CRCが指定されていても無視される。
	 * 
	 * @param header 書きこむエントリについての情報を持つ LhaHeaderのインスタンス。
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void putNextEntryNotYetCompressed(final LhaHeader header) throws IOException {
		if (tempOut != null) {
			closeEntry();                                                  // throws IOException
		}

		crc.reset();
		length = 0;
		this.header = (LhaHeader) header.clone();
		tempOut = CompressMethod.connectEncoder(temp.getOutputStream(), header.getCompressMethod(), property);
	}

	/**
	 * 現在出力中のエントリを閉じ、次のエントリが出力可能な状態にする。 圧縮に失敗した(圧縮後サイズが圧縮前サイズを上回った)場合、 解凍し無圧縮で格納する。エントリのサイズが大きい場合、 この処理にはかなりの時間がかかる。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void closeEntry() throws IOException {
		if (header != null) {
			tempOut.close();
			InputStream in;

			if (temp.length() < length) {
				header.setOriginalSize(length);
				header.setCompressedSize(temp.length());
				header.setCrc((int) crc.getValue());

				in = temp.getInputStream();                                // throws IOException
			} else {
				final String method = header.getCompressMethod();

				header.setOriginalSize(length);
				header.setCompressedSize(length);
				header.setCrc((int) crc.getValue());
				if (!header.getCompressMethod().equalsIgnoreCase(
						CompressMethod.LHD)) {
					header.setCompressMethod(CompressMethod.LH0);
				}

				in = temp.getInputStream();                                // throws IOException
				in = CompressMethod.connectDecoder(in, method, property,
						temp.length());
			}

			String encoding = property.getProperty("lha.encoding");
			if (encoding == null) {
				encoding = LhaProperty.getProperty("lha.encoding");
			}
			out.write(header.getBytes(encoding));                 // throws UnsupportedEncodingException, IOException

			final byte[] buffer = new byte[8192];
			int length;
			while (0 <= (length = in.read(buffer))) {                       // throws IOException
				out.write(buffer, 0, length);                            // throws IOException
			}
		}
		header = null;
		tempOut = null;
	}

	// ------------------------------------------------------------------
	// inner class

	/**
	 * データの一時退避機構を提供する。
	 */
	private static interface Temporary {

		/**
		 * 一時退避機構に貯えられたデータを取り出すInputStream を得る。<br>
		 * このデータは直前の getOutputStream() で与えられる OutputStream に出力されたデータと同じである。<br>
		 * getInputStream() で得られた InputStream が close() されるまで、 getOutputStream() を呼んではならない。<br>
		 * また、getInputStream() で得られた InputStream が close() されるまで、 再び getInputStream() を呼んではならない。<br>
		 * 
		 * @return 一時退避機構からデータを取り出す InputStream
		 * @exception IOException 入出力エラーが発生した場合
		 */
		InputStream getInputStream() throws IOException;

		/**
		 * データを一時退避機構に貯えるOutputStream を得る。<br>
		 * 貯えたデータは直後の getInputStream() で得られる InputStream から得る事が出来る。<br>
		 * getOutputStream で得られた OutputStream が close() されるまで、 getInputStream() を呼んではならない。 また、getOutputStream() で得られた OutputStream が close() されるまで、 再び getOutputStream() を呼んではならない。<br>
		 * 
		 * @return データを一時退避機構に貯える OutputStream
		 * @exception IOException 入出力エラーが発生した場合
		 */
		OutputStream getOutputStream() throws IOException;

		/**
		 * 一時退避機構に格納されているデータ量を得る。 これは 直前の getOutputStream() で与えられた OutputStream に出力されたデータ量と同じである。
		 * 
		 * @return 一時退避機構に格納されているデータ量
		 */
		long length() throws IOException;

		/**
		 * 一時退避機構で使用されていた、全てのシステムリソースを開放する。
		 * 
		 * @exception IOException 入出力エラーが発生した場合
		 */
		void close() throws IOException;

	}

	/**
	 * 一時退避機構に RandomAccessFile を使用するクラス。
	 */
	private static class TemporaryFile implements Temporary {

		/**
		 * 一時退避機構に使用する RandomAccessFile
		 */
		private RandomAccessFile tempfile;

		/**
		 * getOutputStream で与えた OutputStream に出力されたデータ量を保持する。
		 */
		private long length;

		// ------------------------------------------------------------------
		// Constructor

		/**
		 * コンストラクタ fileを使用して TemporaryFile を構築する。
		 * 
		 * @param file RandomAccessFile のインスタンス
		 */
		public TemporaryFile(final RandomAccessFile file) {
			if (file != null) {
				tempfile = file;
			} else {
				throw new NullPointerException("file");
			}
		}

		/**
		 * 一時退避機構に貯えられたデータを取り出す InputStream を得る。<br>
		 * このデータは直前の getOutputStream() で与えられる OutputStream に出力されたデータと同じ。<br>
		 * 
		 * @return 一時退避機構からデータを取り出す InputStream
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public InputStream getInputStream() throws IOException {
			return new TemporaryFileInputStream();
		}

		/**
		 * データを一時退避機構に貯えるOutputStreamを得る。<br>
		 * 貯えたデータは直後の getInputStream() で 得られる InputStream から得る事が出来る。<br>
		 * 
		 * @return データを一時退避機構に貯える OutputStream
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public OutputStream getOutputStream() throws IOException {
			return new TemporaryFileOutputStream();
		}

		/**
		 * 一時退避機構に格納されているデータ量を得る。<br>
		 * これは 直前の getOutputStream() で与えられた OutputStream に出力されたデータ量と同じである。<br>
		 * 
		 * @return 一時退避機構に格納されているデータ量
		 */
		@Override
		public long length() {
			return length;
		}

		/**
		 * 一時退避機構で使用されていた、全てのシステムリソースを開放する。 コンストラクタで与えられた RandomAccessFile は閉じられる。
		 * 
		 * @exception IOException 入出力エラーが発生した場合
		 */
		@Override
		public void close() throws IOException {
			tempfile.close(); // throws IOException
			tempfile = null;
		}

		// ------------------------------------------------------------------
		// inner classes

		/**
		 * TemporaryFile の入力ストリーム
		 */
		private class TemporaryFileInputStream extends InputStream {

			// ------------------------------------------------------------------
			// Constructor

			/**
			 * TemporaryFile からデータを読み込む InputStream を構築する。<br>
			 * 
			 * @exception IOException 入出力エラーが発生した場合
			 */
			public TemporaryFileInputStream() throws IOException {
				tempfile.seek(0);                          // throws IOException
			}

			// ------------------------------------------------------------------
			// method of java.io.InputStream

			/**
			 * TemporaryFileから 1バイトのデータを読み込む。
			 * 
			 * @return 読みこまれた1バイトのデータ 既にEndOfStreamに達している場合は-1
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public int read() throws IOException {
				final long pos = tempfile.getFilePointer();      // throws IOException
				final long limit = length;

				if (pos < limit) {
					return tempfile.read();                  // throws IOException
				}
				return -1;
			}

			/**
			 * TemporaryFileから bufferを満たすようにデータを読み込む。
			 * 
			 * @param buffer データを読み込むバッファ
			 * @return 読みこまれたデータ量 既にEndOfStreamに達している場合は-1
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public int read(final byte[] buffer) throws IOException {
				return this.read(buffer, 0, buffer.length);                   // throws IOException
			}

			/**
			 * TemporaryFileから bufferの indexへlengthバイトのデータを読み込む
			 * 
			 * @param buffer データを読み込むバッファ
			 * @param index buffer内のデータ読みこみ開始位置
			 * @param length 読み込むデータ量
			 * @return 読みこまれたデータ量 既にEndOfStreamに達している場合は-1
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public int read(final byte[] buffer, final int index, int length) throws IOException {
				final long pos = tempfile.getFilePointer();      // throws IOException
				final long limit = TemporaryFile.this.length;
				length = (int) (Math.min(pos + length, limit) - pos);

				if (pos < limit) {
					return tempfile.read(buffer, index, length);// throws IOException
				}
				return -1;
			}

		}

		/**
		 * TemporaryFile の出力ストリーム
		 */
		private class TemporaryFileOutputStream extends OutputStream {

			// ------------------------------------------------------------------
			// Constructor

			/**
			 * TemporaryFile にデータを出力する OutputStream を構築する。<br>
			 * 
			 * @exception IOException 入出力エラーが発生した場合
			 */
			public TemporaryFileOutputStream() throws IOException {
				tempfile.seek(0);                          // throws IOException
				length = 0;
			}

			// ------------------------------------------------------------------
			// method of java.io.OutputStream

			/**
			 * TemporaryFile に 1byteのデータを書き出す。
			 * 
			 * @param data 書き出す1byteのデータ
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public void write(final int data) throws IOException {
				tempfile.write(data);                      // throws IOException
				length++;
			}

			/**
			 * TemporaryFile に bufferの内容を全て書き出す。
			 * 
			 * @param buffer 書き出すデータの入ったバイト配列
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public void write(final byte[] buffer) throws IOException {
				tempfile.write(buffer);                    // throws IOException
				length += buffer.length;
			}

			/**
			 * TemporaryFile に bufferのindex からlengthバイトの内容を書き出す。
			 * 
			 * @param buffer 書き出すデータの入ったバイト配列
			 * @param index buffer内の書き出すデータの開始位置
			 * @param length 書き出すデータ量
			 * @exception IOException 入出力エラーが発生した場合
			 */
			@Override
			public void write(final byte[] buffer, final int index, final int length) throws IOException {
				tempfile.write(buffer, index, length);     // throws IOException
				TemporaryFile.this.length += length;
			}

		}

	}

	/**
	 * 一時退避機構に GrowthByteBufferを使用するクラス
	 */
	private static class TemporaryBuffer implements Temporary {

		/**
		 * 一時退避機構に使用されるバッファ
		 */
		private GrowthByteBuffer tempbuffer;

		/**
		 * GrowthByteBuffer を使用した検索機構を構築する。
		 */
		public TemporaryBuffer() {
			tempbuffer = new GrowthByteBuffer();
		}

		// ------------------------------------------------------------------
		// method of Temporary

		/**
		 * 一時退避機構に貯えられたデータを取り出す InputStream を得る。<br>
		 * このデータは直前の getOutputStream() で与えられる OutputStream に出力されたデータと同じ。<br>
		 * 
		 * @return 一時退避機構からデータを取り出す InputStream
		 */
		@Override
		public InputStream getInputStream() {
			return new TemporaryBufferInputStream();
		}

		/**
		 * データを一時退避機構に貯える OutputStream を得る。<br>
		 * 貯えたデータは直後の getInputStream() で得られる InputStream から得る事が出来る。<br>
		 * 
		 * @return データを一時退避機構に貯える OutputStream
		 */
		@Override
		public OutputStream getOutputStream() {
			return new TemporaryBufferOutputStream();
		}

		/**
		 * 一時退避機構に格納されているデータ量を得る。<br>
		 * これは 直前の getOutputStream() で与えた OutputStream に出力されたデータ量と同じである。
		 * 
		 * @return 一時退避機構に格納されているデータ量
		 */
		@Override
		public long length() {
			return tempbuffer.length();
		}

		/**
		 * 一時退避機構で使用されていた、全てのシステムリソースを開放する。
		 */
		@Override
		public void close() {
			tempbuffer = null;
		}

		// ------------------------------------------------------------------
		// inner classes

		/**
		 * TemporaryBuffer の入力ストリーム
		 */
		private class TemporaryBufferInputStream extends InputStream {

			// ------------------------------------------------------------------
			// Constructor

			/**
			 * TemporaryBuffer からデータを読み込む InputStream を構築する。<br>
			 */
			public TemporaryBufferInputStream() {
				tempbuffer.seek(0);
			}

			// ------------------------------------------------------------------
			// method of java.io.InputStream

			/**
			 * TemporaryBuffer から 1バイトのデータを読み込む。
			 * 
			 * @return 読みこまれた1バイトのデータ 既にEndOfStreamに達している場合は-1
			 */
			@Override
			public int read() {
				return tempbuffer.read();
			}

			/**
			 * TemporaryBuffer から bufferを満たすようにデータを読み込む。
			 * 
			 * @param buffer データを読み込むバッファ
			 * @return 読みこまれたデータ量 既にEndOfStreamに達している場合は-1
			 */
			@Override
			public int read(final byte[] buffer) {
				return tempbuffer.read(buffer);
			}

			/**
			 * TemporaryBuffer から bufferの indexへ lengthバイトのデータを読み込む
			 * 
			 * @param buffer データを読み込むバッファ
			 * @param index buffer内のデータ読みこみ開始位置
			 * @param length 読み込むデータ量
			 * @return 読みこまれたデータ量 既にEndOfStreamに達している場合は-1
			 */
			@Override
			public int read(final byte[] buffer, final int index, final int length) {
				return tempbuffer.read(buffer, index, length);
			}

		}

		/**
		 * TemporaryBuffer の出力ストリーム
		 */
		private class TemporaryBufferOutputStream extends OutputStream {

			// ------------------------------------------------------------------
			// constructor

			/**
			 * TemporaryBuffer にデータを出力する OutputStream を構築する。<br>
			 */
			public TemporaryBufferOutputStream() {
				tempbuffer.seek(0);
				tempbuffer.setLength(0);
			}

			// ------------------------------------------------------------------
			// method of java.io.OutputStream

			/**
			 * TemporaryBuffer に 1byteのデータを書き出す。
			 * 
			 * @param data 書き出す1byteのデータ
			 */
			@Override
			public void write(final int data) {
				tempbuffer.write(data);
			}

			/**
			 * TemporaryBuffer に bufferの内容を全て書き出す。
			 * 
			 * @param buffer 書き出すデータの入ったバイト配列
			 */
			@Override
			public void write(final byte[] buffer) {
				tempbuffer.write(buffer);
			}

			/**
			 * TemporaryBuffer に bufferのindex から lengthバイトの内容を書き出す。
			 * 
			 * @param buffer 書き出すデータの入ったバイト配列
			 * @param index buffer内の書き出すデータの開始位置
			 * @param length 書き出すデータ量
			 */
			@Override
			public void write(final byte[] buffer, final int index, final int length) {
				tempbuffer.write(buffer, index, length);
			}

		}

	}

}