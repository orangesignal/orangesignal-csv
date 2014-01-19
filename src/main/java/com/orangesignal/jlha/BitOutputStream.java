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

import java.io.IOException;
import java.io.OutputStream;

/**
 * 接続された出力ストリームにビットデータを出力するための 出力ストリームクラス。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BitOutputStream.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 * 
 * Revision 1.0  2002/09/11 00:00:00  dangan
 * add to version control
 * [change]
 *     close() 後の write系メソッドと flush() で
 *     例外を投げるように修正
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class BitOutputStream extends OutputStream {

	/**
	 * デフォルトおキャッシュサイズ
	 */
	private static final int DefaultCacheSize = 1024;

	/**
	 * 接続された出力ストリーム
	 */
	private OutputStream out;

	/**
	 * 速度低下抑止用バイト配列
	 */
	private byte[] cache;

	/**
	 * cacheBuffer 内の現在処理位置
	 */
	private int cachePosition;

	/**
	 * ビットバッファ
	 */
	private int bitBuffer;

	/**
	 * bitBuffer の 有効ビット数
	 */
	private int bitCount;

	// ------------------------------------------------------------------
	// Constructer

	/**
	 * 出力ストリーム out へ データをビット単位で 書きこめるようなストリームを構築する。<br>
	 * キャッシュサイズにはデフォルト値が使用される。
	 * 
	 * @param out 出力ストリーム
	 */
	public BitOutputStream(final OutputStream out) {
		this(out, BitOutputStream.DefaultCacheSize);

	}

	/**
	 * 出力ストリーム out へ データをビット単位で 書きこめるようなストリームを構築する。<br>
	 * 
	 * @param out 出力ストリーム
	 * @param CacheSize キャッシュサイズ
	 * @exception IllegalArgumentException CacheSize が 4未満の場合、または CacheSize が 4の倍数で無い場合。
	 */
	public BitOutputStream(final OutputStream out, final int CacheSize) {
		if (out != null && 4 <= CacheSize && 0 == (CacheSize & 0x03)) {
			this.out = out;
			cache = new byte[CacheSize];
			cachePosition = 0;
			bitBuffer = 0;
			bitCount = 0;
		} else if (out == null) {
			throw new NullPointerException("out");
		} else if (CacheSize < 4) {
			throw new IllegalArgumentException("CacheSize must be 4 or more.");
		} else {
			throw new IllegalArgumentException("CacheSize must be multiple of 4.");
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * 接続された出力ストリームに 8ビットのデータを出力する。<br>
	 * 
	 * @param data 8ビットのデータ。<br>上位24ビットは無視される。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final int data) throws IOException {
		writeBits(8, data);
	}

	/**
	 * 接続された出力ストリームにbufferの内容を連続した 8ビットのデータとして出力する。<br>
	 * 
	 * @param buffer 出力すべきデータを格納したバイト配列<br>
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer) throws IOException {
		this.write(buffer, 0, buffer.length);                                 // throws IOException
	}

	/**
	 * 接続された出力ストリームにbufferのindexから lengthバイトの内容を連続した 8ビットのデータ として出力する。<br>
	 * 
	 * @param buffer 出力すべきデータを格納したバイト配列
	 * @param index buffer内のデータ開始位置
	 * @param length 出力するデータ量(バイト数)
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer, int index, int length) throws IOException {
		if (bitCount % 8 == 0) {
			flush();                                                       // throws IOException
			out.write(buffer, index, length);                            // throws IOException
		} else {
			while (0 < length--) {
				writeBits(8, buffer[index++]);                           // throws IOException
			}
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * このビット出力ストリームにバッファリングされている 8ビット単位のデータを全て出力先に出力する。 8ビットに満たないデータは出力されないことに注意。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void flush() throws IOException {
		while (8 <= bitCount) {
			cache[cachePosition++] = (byte) (bitBuffer >> 24);
			bitBuffer <<= 8;
			bitCount -= 8;
		}

		out.write(cache, 0, cachePosition);                    // throws IOException
		cachePosition = 0;
		out.flush();                                                       // throws IOException
	}

	/**
	 * この出力ストリームと、接続された出力ストリームを閉じ、 使用していたリソースを開放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		while (0 < bitCount) {
			cache[cachePosition++] = (byte) (bitBuffer >> 24);
			bitBuffer <<= 8;
			bitCount -= 8;
		}

		out.write(cache, 0, cachePosition);                    // throws IOException
		cachePosition = 0;
		out.flush();                                                       // throws IOException
		out.close();                                                       // throws IOException

		out = null;
		cache = null;
		cachePosition = 0;
		bitCount = 128;
		bitBuffer = 0;
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * 接続された出力ストリームに1ビットのデータを出力する。<br>
	 * 
	 * @param data 1ビットのデータ。<br>上位31ビットは無視される。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void writeBit(final int data) throws IOException {
		bitBuffer |= (data & 0x00000001) << 31 - bitCount;
		bitCount++;
		if (32 <= bitCount) {
			writeOutBitBuffer();                        // throws IOException
		}
	}

	/**
	 * 真偽値を接続された出力ストリームに1ビットの データとして出力する。<br>
	 * true は 1、false は 0として出力する。<br>
	 * java.io.DataOutput の writeBoolean() とは 互換性が無いので注意すること。<br>
	 * 
	 * @param bool 真偽値
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void writeBoolean(final boolean bool) throws IOException {
		if (bool) {
			bitBuffer |= 1 << 31 - bitCount;
		}

		bitCount++;

		if (32 <= bitCount) {
			writeOutBitBuffer();                     // throws IOException
		}
	}

	/**
	 * 接続された出力ストリームにビットデータを出力する。<br>
	 * 
	 * @param count data の有効ビット数
	 * @param data ビットデータ
	 * @exception IOException 入出力エラーが発生した場合
	 */
	public void writeBits(int count, final int data) throws IOException {
		while (0 < count) {
			final int available = 32 - bitCount;
			if (count < available) {
				bitBuffer |= (data & 0xFFFFFFFF >>> 32 - count) << available
						- count;
				bitCount += count;
				count = 0;
			} else {
				count -= available;
				bitBuffer |= data >> count & 0xFFFFFFFF >>> 32 - available;
				writeOutBitBuffer();
			}
		}
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * ビットバッファに蓄えられたデータを全てキャッシュに 出力し、キャッシュが満ちた場合はキャッシュのデータを 接続された出力ストリームに出力する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOutBitBuffer() throws IOException {
		cache[cachePosition++] = (byte) (bitBuffer >> 24);
		cache[cachePosition++] = (byte) (bitBuffer >> 16);
		cache[cachePosition++] = (byte) (bitBuffer >> 8);
		cache[cachePosition++] = (byte) bitBuffer;

		bitBuffer = 0;
		bitCount = 0;

		if (cache.length <= cachePosition) {
			out.write(cache);
			cachePosition = 0;
		}
	}

}