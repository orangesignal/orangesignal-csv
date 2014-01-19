/**
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

package com.orangesignal.jlha;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * ビット入力のためのユーティリティクラス。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BitInputStream.java,v $
 * Revision 1.5  2002/12/07 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 * 
 * Revision 1.4  2002/11/15 00:00:00  dangan
 * [improvement]
 *     prefetchBits() が  32bit の読み込みを保証するように修正
 * [change]
 *     メソッド名の変更
 *     prefetchBit     -> peekBit
 *     prefetchBoolean -> peekBoolean
 *     prefetchBits    -> peekBits
 * 
 * Revision 1.3  2002/11/02 00:00:00  dangan
 * [bug fix]
 *     available() availableBits() で
 *     ブロックせずに読み込める量よりも大きい値を返していた。
 * 
 * Revision 1.2  2002/09/05 00:00:00  dangan
 * [change]
 *     EndOfStream に達した後の read( new byte[0] ) や 
 *     read( byte[] buf, int off, 0 ) の戻り値を
 *     InputStream と同じく 0 になるようにした
 * 
 * Revision 1.1  2002/09/04 00:00:00  dangan
 * [bug fix]
 *     skip( len ) と skipBits( len ) で len が 0 未満のとき
 *     正しく処理できていなかった。
 * 
 * Revision 1.0  2002/09/03 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     mark() で 接続された in に渡す readLimit の計算が甘かったため、
 *     要求された readLimit に達する前にマーク位置が破棄される事があった。
 *     EndOfStream に達した後の skip() および skip( 0 ) が -1 を返していた。
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.5 $
 */
public class BitInputStream extends InputStream {

	private static final int DefaultCacheSize = 1024;

	/**
	 * 接続された入力ストリーム
	 */
	private InputStream in;

	/**
	 * 速度低下抑止用バイト配列
	 */
	private byte[] cache;

	/**
	 * cache 内の有効バイト数
	 */
	private int cacheLimit;

	/**
	 * cache 内の現在処理位置
	 */
	private int cachePosition;

	/**
	 * ビットバッファ。 ビットデータは最上位ビットから bitCount だけ格納されている。
	 */
	private int bitBuffer;

	/**
	 * bitBuffer の 有効ビット数
	 */
	private int bitCount;

	/**
	 * mark位置がキャッシュの範囲内にあるかを示す。 markされたとき true に設定され、 次に in から キャッシュへの読み込みが 行われたときに false に設定される。
	 */
	private boolean markPositionIsInCache;

	/** cache の バックアップ用 */
	private byte[] markCache;

	/** cacheLimit のバックアップ用 */
	private int markCacheLimit;

	/** cachePosition のバックアップ用 */
	private int markCachePosition;

	/** bitBuffer のバックアップ用 */
	private int markBitBuffer;

	/** bitCount のバックアップ用 */
	private int markBitCount;

	// ------------------------------------------------------------------
	// Constructer

	/**
	 * 入力ストリーム in からのデータをビット単位で 読み込めるようなストリームを構築する。<br>
	 * 
	 * @param in 入力ストリーム
	 */
	public BitInputStream(final InputStream in) {
		this(in, BitInputStream.DefaultCacheSize);
	}

	/**
	 * 入力ストリーム in からのデータをビット単位で 読み込めるようなストリームを構築する。<br>
	 * 
	 * @param in 入力ストリーム
	 * @param CacheSize バッファサイズ
	 */
	public BitInputStream(final InputStream in, final int CacheSize) {
		if (in != null && 4 <= CacheSize) {
			this.in = in;
			cache = new byte[CacheSize];
			cacheLimit = 0;
			cachePosition = 0;
			bitBuffer = 0;
			bitCount = 0;

			markPositionIsInCache = false;
			markCache = null;
			markCacheLimit = 0;
			markCachePosition = 0;
			markBitBuffer = 0;
			markBitCount = 0;
		} else if (in == null) {
			throw new NullPointerException("in");
		} else {
			throw new IllegalArgumentException("CacheSize must be 4 or more.");
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.InputStream

	/**
	 * 接続されたストリームから 8ビットのデータを読み込む。<br>
	 * 
	 * @return 読み出された 8ビットのデータ。<br>
	 * 既に EndOfStream に達している場合は -1
	 * 
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception BitDataBrokenException EndOfStreamに達したため 要求されたビット数のデータの 読み込みに失敗した場合。<br>
	 */
	@Override
	public int read() throws IOException {
		try {
			return readBits(8);                                          // throws LocalEOFException BitDataBrokenException IOException
		} catch (final LocalEOFException exception) {
			if (exception.thrownBy(this)) {
				return -1;
			}
			throw exception;
		}
	}

	/**
	 * 接続された入力ストリームから バイト配列 buffer を 満たすようにデータを読み込む。<br>
	 * データは必ずしも buffer を満たすとは限らないことに注意。<br>
	 * 
	 * @param buffer 読み込まれたデータを格納するためのバイト配列
	 * @return buffer に読み込んだデータ量をバイト数で返す。<br>既に EndOfStream に達していた場合は -1 を返す。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception BitDataBrokenException EndOfStreamに達したため 要求されたビット数のデータの 読み込みに失敗した場合。<br>
	 */
	@Override
	public int read(final byte[] buffer) throws IOException {
		return this.read(buffer, 0, buffer.length);                           // throws BitDataBrokenException IOException
	}

	/**
	 * 接続された入力ストリームから バイト配列 buffer の index で指定された位置から length バイトのデータを 読み込む。<br>
	 * このメソッドは lengthバイト読み込むか、 EndOfStream に到達するまでブロックする。<br>
	 * データは必ずしも length バイト読み込まれるとは限ら ないことに注意。<br>
	 * 
	 * @param buffer 読み込まれたデータを格納するためのバイト配列
	 * @param index buffer内のデータ読み込み開始位置
	 * @param length bufferに読み込むデータ量
	 * @return buffer に読み込んだデータ量をバイト数で返す。<br>既に EndOfStream に達していた場合は -1 を返す。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception BitDataBrokenException EndOfStreamに達したため 要求されたビット数のデータの 読み込みに失敗した場合。<br>
	 */
	@Override
	public int read(final byte[] buffer, int index, int length) throws IOException {
		final int requested = length;
		try {
			while (0 < length) {
				buffer[index++] = (byte) readBits(8);                     // throws LocalEOFException BitDataBrokenException IOException
				length--;
			}
			return requested;
		} catch (final LocalEOFException exception) {
			if (exception.thrownBy(this)) {
				if (requested != length) {
					return requested - length;
				}
				return -1;
			}
			throw exception;
		} catch (final BitDataBrokenException exception) {
			if (exception.getCause() instanceof LocalEOFException
					&& ((LocalEOFException) exception.getCause())
							.thrownBy(this)) {
				bitBuffer >>>= exception.getBitCount();
				bitCount += exception.getBitCount();
				bitBuffer |= exception.getBitData() << 32 - exception
						.getBitCount();

				return requested - length;
			}
			throw exception;
		}
	}

	/**
	 * 接続された入力ストリームのデータを length バイト 読み飛ばす。<br>
	 * このメソッドは lengthバイト読み飛ばすか、 EndOfStream に到達するまでブロックする。<br>
	 * データは必ずしも length バイト読み飛ばされるとは限ら ないことに注意。<br>
	 * 
	 * @param length 読み飛ばすバイト数。<br>
	 * @return 実際に読み飛ばされたバイト数。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	@Override
	public long skip(long length) throws IOException {
		length = 0 < length ? length : 0;
		final long requested = length;
		try {
			while (0 < length) {
				readBits(8);
				length--;
			}
			return requested;
		} catch (final LocalEOFException exception) {
			return requested - length;
		} catch (final BitDataBrokenException exception) {
			if (exception.getCause() instanceof LocalEOFException
					&& ((LocalEOFException) exception.getCause())
							.thrownBy(this)) {
				bitBuffer >>>= exception.getBitCount();
				bitCount += exception.getBitCount();
				bitBuffer |= exception.getBitData() << 32 - exception
						.getBitCount();
				return requested - length;
			}
			throw exception;
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.InputStream

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 */
	@Override
	public void mark(int readLimit) {
		readLimit -= cacheLimit - cachePosition;
		readLimit -= bitCount / 8;
		readLimit += 4;
		readLimit = readLimit / cache.length * cache.length + (readLimit % cache.length == 0 ? 0 : cache.length);

		in.mark(readLimit);

		if (markCache == null) {
			markCache = cache.clone();
		} else {
			System.arraycopy(cache, 0, markCache, 0, cacheLimit);
		}
		markCacheLimit = cacheLimit;
		markCachePosition = cachePosition;
		markBitBuffer = bitBuffer;
		markBitCount = bitCount;
		markPositionIsInCache = true;
	}

	/**
	 * 接続された入力ストリームの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。<br>
	 * 
	 * @exception IOException <br>
	 * (1) BitInputStream に mark がなされていない場合。<br>
	 * (2) 接続された入力ストリームが markSupported()で false を返す場合。<br>
	 * (3) 接続された入力ストリームで 入出力エラーが発生した場合。<br>
	 * の何れか。
	 */
	@Override
	public void reset() throws IOException {
		if (markPositionIsInCache) {
			cachePosition = markCachePosition;
			bitBuffer = markBitBuffer;
			bitCount = markBitCount;
		} else if (!in.markSupported()) {
			throw new IOException("not support mark()/reset().");
		} else if (markCache == null) { // この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。
			throw new IOException("not marked.");
		} else {
			// in が reset() できない場合は
			// 最初の行の this.in.reset() で
			// IOException を投げることを期待している。
			in.reset();                                                    // throws IOException
			System.arraycopy(markCache, 0, cache, 0, markCacheLimit);
			cacheLimit = markCacheLimit;
			cachePosition = markCachePosition;
			bitBuffer = markBitBuffer;
			bitCount = markBitCount;
		}
	}

	/**
	 * 接続された入力ストリームが mark() と reset() を サポートするかを得る。<br>
	 * 
	 * @return ストリームが mark() と reset() を サポートする場合は true。<br>
	 * サポートしない場合は false。<br>
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	// ------------------------------------------------------------------
	// method of java.io.InputStream

	/**
	 * 接続された入力ストリームからブロックしないで 読み込むことのできるバイト数を得る。<br>
	 * 
	 * @return ブロックしないで読み出せるバイト数。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	@Override
	public int available() throws IOException {
		return availableBits() / 8;                                        // throws IOException
	}

	/**
	 * この入力ストリームを閉じ、 使用していたリソースを開放する。<br>
	 * 
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		in.close();                                                        // throws IOException
		in = null;

		cache = null;
		cacheLimit = 0;
		cachePosition = 0;
		bitBuffer = 0;
		bitCount = 0;

		markCache = null;
		markCacheLimit = 0;
		markCachePosition = 0;
		markBitBuffer = 0;
		markBitCount = 0;
		markPositionIsInCache = false;
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * 接続された入力ストリームから 1ビットのデータを 読み込む。<br>
	 * 
	 * @return 読み込まれた1ビットのデータ。<br>既にEndOfStreamに達している場合は -1。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public int readBit() throws IOException {
		if (0 < bitCount) {
			final int bit = bitBuffer >>> 31;
			bitBuffer <<= 1;
			bitCount -= 1;
			return bit;
		}
		try {
			fillBitBuffer();
			final int bit = bitBuffer >>> 31;
			bitBuffer <<= 1;
			bitCount -= 1;
			return bit;
		} catch (final LocalEOFException exception) {
			if (exception.thrownBy(this)) {
				return -1;
			}
			throw exception;
		}
	}

	/**
	 * 接続された入力ストリームから 1ビットのデータを 真偽値として読み込む。<br>
	 * 
	 * @return 読み込まれた1ビットのデータが 1であれば true、0であれば false を返す。<br>
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public boolean readBoolean() throws IOException {
		if (0 < bitCount) {
			final boolean bool = bitBuffer < 0;
			bitBuffer <<= 1;
			bitCount -= 1;
			return bool;
		}
		fillBitBuffer();
		final boolean bool = bitBuffer < 0;
		bitBuffer <<= 1;
		bitCount -= 1;
		return bool;
	}

	/**
	 * 接続された入力ストリームから count ビットのデータを 読み込む。 戻り値が int値である事からも判るように 読み込むことのできる 最大有効ビット数は 32ビットで あるが、count は32以上の値を設定してもチェックを 受けないため それ以上の値を設定した場合は ビット データが読み捨てられる。<br>
	 * たとえば readBits( 33 ) としたときは まず1ビットの データを読み捨て、その後の 32ビットのデータを返す。<br>
	 * また count に 0以下の数字を設定して呼び出した場合、 データを読み込む動作を伴わないため 戻り値は 常に0、 EndOfStream に達していても EOFException を 投げない点に注意すること。<br>
	 * 
	 * @param count 読み込むデータのビット数
	 * @return 読み込まれたビットデータ。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception BitDataBrokenException 読み込み途中で EndOfStreamに達したため 要求されたビット数のデータの読み込み に失敗した場合。<br>
	 */
	public int readBits(int count) throws IOException {
		if (0 < count) {
			if (count <= bitCount) {
				final int bits = bitBuffer >>> 32 - count;
				bitBuffer <<= count;
				bitCount -= count;
				return bits;
			}
			final int requested = count;
			int bits = 0;
			try {
				fillBitBuffer();                                       // throws LocalEOFException IOException
				while (bitCount < count) {
					count -= bitCount;
					if (count < 32) {
						bits |= bitBuffer >>> 32 - bitCount << count;
					}
					bitBuffer = 0;
					bitCount = 0;
					fillBitBuffer();                                   // throws LocalEOFException IOException
				}
				bits |= bitBuffer >>> 32 - count;
				bitBuffer <<= count;
				bitCount -= count;
				return bits;
			} catch (final LocalEOFException exception) {
				if (exception.thrownBy(this) && count < requested) {
					throw new BitDataBrokenException(exception,
							bits >>> count, requested - count);
				}
				throw exception;
			}
		}
		return 0;
	}

	/**
	 * 接続されたストリームから count ビットのデータを 読み飛ばす。<br>
	 * 
	 * @param count 読み飛ばしてほしいビット数
	 * @return 実際に読み飛びしたビット数
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public int skipBits(int count) throws IOException {
		count = Math.max(count, 0);

		if (count < bitCount) {
			bitBuffer <<= count;
			bitCount -= count;
			return count;
		}
		final int requested = count;
		count -= bitCount;
		bitCount = 0;
		bitBuffer = 0;
		try {
			while ((cacheLimit - cachePosition) * 8 <= count) {
				count -= (cacheLimit - cachePosition) * 8;
				cachePosition = cacheLimit;
				fillCache();
				if (cacheLimit == cachePosition) {
					throw new LocalEOFException(this);
				}
			}
			cachePosition += count >> 3;
			count = count & 0x07;
			if (0 < count) {
				bitCount = 8 - count;
				bitBuffer = cache[cachePosition++] << 24 + count;
				count = 0;
			}
		} catch (final LocalEOFException exception) {
		}
		return requested - count;
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * 読み込み位置を変えずに 1ビットのデータを先読みする。<br>
	 * 
	 * @return 読み込まれた1ビットのデータ。<br>既にEndOfStreamに達している場合は -1。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public int peekBit() throws IOException {
		if (0 < bitCount) {
			return bitBuffer >>> 31;
		}
		try {
			fillBitBuffer();                                           // throws LocalEOFException IOException
			return bitBuffer >>> 31;
		} catch (final LocalEOFException exception) {
			if (exception.thrownBy(this)) {
				return -1;
			}
			throw exception;
		}
	}

	/**
	 * 読み込み位置を変えずに 1ビットのデータを 真偽値として先読みする。<br>
	 * 
	 * @return 読み込まれた1ビットのデータが 1であれば true、0であれば false を返す。<br>
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public boolean peekBoolean() throws IOException {
		if (0 < bitCount) {
			return bitBuffer < 0;
		}
		fillBitBuffer();                                               // throws LocalEOFException IOException
		return bitBuffer < 0;
	}

	/**
	 * 読み込み位置を変えずに count ビットのデータを先読みする。<br>
	 * 戻り値が int型であることからもわかるように 最大有効ビット数は 32ビットである。<br>
	 * EndOfStream 付近を除いて、先読み出来ることが保障されるのは 32ビットである。(ビットバッファの大きさが 32ビットであるため)<br>
	 * もし 32ビット以上の先読み機能が必須となる場合は その都度 mark()、readBits()、reset() を繰り返すか、 このクラスを使用することを諦めること。<br>
	 * 
	 * @param count 読み込むビット数
	 * @return 先読みした count ビットのビットデータ
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception NotEnoughBitsException count が先読み可能な範囲外の場合
	 */
	public int peekBits(int count) throws IOException {
		if (0 < count) {
			if (count <= bitCount) {
				return bitBuffer >>> 32 - count;
			}
			fillBitBuffer();                                           // throws LocalEOFException, IOException
			if (count <= bitCount) {
				return bitBuffer >>> 32 - count;
			} else if (count <= cachedBits()) {
				if (count <= 32) {
					int bits = bitBuffer;
					bits |= (cache[cachePosition] & 0xFF) >> bitCount - 24;
					return bits >>> 32 - count;
				} else if (count - 32 < bitCount) {
					int bits = bitBuffer << count - 32;
					;
					int bcnt = bitCount - (count - 32);
					int pos = cachePosition;
					while (bcnt < 25) {
						bits |= (cache[pos++] & 0xFF) << 24 - bcnt;
						bcnt += 8;
					}
					if (bcnt < 32) {
						bits |= (cache[pos] & 0xFF) >> bcnt - 24;
					}
					return bits;
				} else {
					count -= bitCount;
					count -= 32;
					final int pos = cachePosition + (count >> 3);
					count &= 0x07;
					if (0 < count) {
						return cache[pos] << 24 + count
								| (cache[pos + 1] & 0xFF) << 16 + count
								| (cache[pos + 2] & 0xFF) << 8 + count
								| (cache[pos + 3] & 0xFF) << count
								| (cache[pos + 4] & 0xFF) >> 8 - count;
					}
					return cache[pos] << 24
							| (cache[pos + 1] & 0xFF) << 16
							| (cache[pos + 2] & 0xFF) << 8
							| cache[pos + 3] & 0xFF;
				}
			} else {
				throw new NotEnoughBitsException(cachedBits());
			}
		}
		return 0;
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * 接続された入力ストリームからブロックしないで 読み込むことのできるビット数を得る。<br>
	 * 
	 * @return ブロックしないで読み出せるビット数。<br>
	 * 
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	public int availableBits() throws IOException {
		int avail = cacheLimit - cachePosition + in.available() / cache.length * cache.length;// throws IOException
		avail += bitCount - 32;
		return Math.max(avail, 0);
	}

	/**
	 * この BitInputStream 内に蓄えられているビット数を得る。<br>
	 * 
	 * @return この BitInputStream 内に蓄えられているビット数。<br>
	 */
	private int cachedBits() {
		return bitCount + (cacheLimit - cachePosition << 3);
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * bitBuffer にデータを満たす。 EndOfStream 付近を除いて bitBuffer には 25bit のデータが確保されることを保障する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception LocalEOFException bitBuffer が空の状態で EndOfStream に達した場合
	 */
	private void fillBitBuffer() throws IOException {
		if (32 <= cachedBits()) {
			if (bitCount <= 24) {
				if (bitCount <= 16) {
					if (bitCount <= 8) {
						if (bitCount <= 0) {
							bitBuffer = cache[cachePosition++] << 24;
							bitCount = 8;
						}
						bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
						bitCount += 8;
					}
					bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
					bitCount += 8;
				}
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
				bitCount += 8;
			}
		} else if (bitCount < 25) {
			if (bitCount == 0) {
				bitBuffer = 0;
			}

			int count = Math
					.min(32 - bitCount >> 3, cacheLimit - cachePosition);
			while (0 < count--) {
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
				bitCount += 8;
			}
			fillCache();                                                   // throws IOException
			if (cachePosition < cacheLimit) {
				count = Math
						.min(32 - bitCount >> 3, cacheLimit - cachePosition);
				while (0 < count--) {
					bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
					bitCount += 8;
				}
			} else if (bitCount <= 0) {
				throw new LocalEOFException(this);
			}
		}
	}

	/**
	 * cache が空になった時に cache にデータを読み込む。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void fillCache() throws IOException {
		markPositionIsInCache = false;
		cacheLimit = 0;
		cachePosition = 0;

		// cache にデータを読み込む
		int read = 0;
		while (0 <= read && cacheLimit < cache.length) {
			read = in.read(cache, cacheLimit, cache.length - cacheLimit);         // throws IOException

			if (0 < read) {
				cacheLimit += read;
			}
		}
	}

	// ------------------------------------------------------------------
	// inner classes

	/**
	 * BitInputStream 内で EndOfStream の検出に EOFException を使用するのは少々問題があるので ローカルな EOFException を定義する。
	 */
	@SuppressWarnings("serial")
	private static class LocalEOFException extends EOFException {

		/**
		 * この例外を投げたオブジェクト
		 */
		private final Object owner;

		// ------------------------------------------------------------------
		// Constructer

		/**
		 * コンストラクタ。
		 * 
		 * @param object この例外を投げたオブジェクト
		 */
		public LocalEOFException(final Object object) {
			super();
			owner = object;
		}

		// ------------------------------------------------------------------
		// access method

		/**
		 * この例外が object によって投げられたかどうかを得る。<br>
		 * 
		 * @param object オブジェクト
		 * @return この例外が objectによって 投げられた例外であれば true<br>違えば false<br>
		 */
		public boolean thrownBy(final Object object) {
			return owner == object;
		}
	}

}