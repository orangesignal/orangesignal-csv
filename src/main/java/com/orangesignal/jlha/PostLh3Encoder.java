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

import java.io.IOException;
import java.io.OutputStream;

/**
 * -lh3- 圧縮用 PostLzssEncoder。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class PostLh3Encoder implements PostLzssEncoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 8192;

	/** 最大一致長 */
	private static final int MAX_MATCH = 256;

	/** 最小一致長 */
	private static final int THRESHOLD = 3;

	/**
	 * OffHi部分の固定ハフマン符号長
	 */
	private static final int[] CONST_OFF_HI_LEN = createConstOffHiLen();

	/**
	 * code部のハフマン木のサイズ code部がこれ以上の値を扱う場合は余計なビットを出力して補う。
	 */
	private static final int CODE_SIZE = 286;

	/**
	 * -lh3- 形式の圧縮データの出力先の ビット出力ストリーム
	 */
	private BitOutputStream out;

	/**
	 * 静的ハフマン圧縮するためにデータを一時的に貯えるバッファ
	 */
	private byte[] buffer;

	/**
	 * バッファ内にある code データの数。
	 */
	private int blockSize;

	/**
	 * buffer内の現在処理位置
	 */
	private int position;

	/**
	 * flag バイト内の現在処理bit
	 */
	private int flagBit;

	/**
	 * buffer内の現在のflagバイトの位置
	 */
	private int flagPos;

	/**
	 * code部の頻度表
	 */
	private int[] codeFreq;

	/**
	 * offHi部の頻度表
	 */
	private int[] offHiFreq;

	/**
	 * -lh3- 圧縮用 PostLzssEncoderを構築する。<br>
	 * バッファサイズにはデフォルト値が使用される。
	 * 
	 * @param out 圧縮データを受け取る出力ストリーム
	 */
	public PostLh3Encoder(final OutputStream out) {
		this(out, 16384);
	}

	/**
	 * -lh3- 圧縮用 PostLzssEncoderを構築する。<br>
	 * 
	 * @param out 圧縮データを受け取る出力ストリーム
	 * @param BufferSize 静的ハフマン圧縮用のバッファサイズ
	 * @exception IllegalArgumentException BufferSize が小さすぎる場合
	 */
	public PostLh3Encoder(final OutputStream out, final int BufferSize) {
		final int DictionarySizeByteLen = 2;
		final int MinCapacity = (DictionarySizeByteLen + 1) * 8 + 1;

		if (out != null && MinCapacity <= BufferSize) {

			if (out instanceof BitOutputStream) {
				this.out = (BitOutputStream) out;
			} else {
				this.out = new BitOutputStream(out);
			}
			codeFreq = new int[CODE_SIZE];
			offHiFreq = new int[DICTIONARY_SIZE >> 6];
			buffer = new byte[BufferSize];
			blockSize = 0;
			position = 0;
			flagBit = 0;
			flagPos = 0;
		} else if (out == null) {
			throw new NullPointerException("out");
		} else {
			throw new IllegalArgumentException("BufferSize too small. BufferSize must be larger than " + MinCapacity);
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PostLzssEncoder

	/**
	 * 1byte の LZSS未圧縮のデータもしくは、 LZSS で圧縮された圧縮コードのうち一致長を書きこむ。<br>
	 * 
	 * @param code 1byte の LZSS未圧縮のデータもしくは、 LZSS で圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void writeCode(final int code) throws IOException {
		final int CodeMax = CODE_SIZE - 1;
		final int DictionarySizeByteLen = 2;
		final int Capacity = (DictionarySizeByteLen + 1) * 8 + 1;

		if (flagBit == 0) {
			if (buffer.length - position < Capacity || 65536 - 8 <= blockSize) {
				writeOut();                                                // throws IOException
			}
			flagBit = 0x80;
			flagPos = position++;
			buffer[flagPos] = 0;
		}

		// データ格納
		buffer[position++] = (byte) code;

		// 上位1ビットをフラグとして格納
		if (0x100 <= code) {
			buffer[flagPos] |= flagBit;
		}
		flagBit >>= 1;

		// 頻度表更新
		codeFreq[Math.min(code, CodeMax)]++;

		// ブロックサイズ更新
		blockSize++;
	}

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
	 * 
	 * @param offset LZSS で圧縮された圧縮コードのうち一致位置
	 */
	@Override
	public void writeOffset(final int offset) {
		// データ格納
		buffer[position++] = (byte) (offset >> 8);
		buffer[position++] = (byte) offset;

		// 頻度表更新
		offHiFreq[offset >> 6]++;
	}

	/**
	 * この PostLzssEncoder にバッファリングされている全ての 8ビット単位のデータを出力先の OutputStream に出力し、 出力先の OutputStream を flush() する。<br>
	 * このメソッドは圧縮率を変化させる。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 * @see BitOutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		writeOut();
		out.flush();
	}

	/**
	 * この出力ストリームと、接続された出力ストリームを閉じ、 使用していたリソースを開放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		writeOut();
		out.close();                                                       // throws IOException
		out = null;
		buffer = null;
		codeFreq = null;
		offHiFreq = null;
	}

	/**
	 * -lh3-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lh3-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return DICTIONARY_SIZE;
	}

	/**
	 * -lh3-形式の LZSSの最大一致長を得る。
	 * 
	 * @return -lh3-形式の LZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return MAX_MATCH;
	}

	/**
	 * -lh3-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lh3-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return THRESHOLD;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * バッファリングされた全てのデータを this.out に出力する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOut() throws IOException {
		final int CodeMax = CODE_SIZE - 1;

		if (0 < blockSize) {
			// ------------------------------------------------------------------
			// ブロックサイズ出力
			out.writeBits(16, blockSize);                           // throws IOException

			// ------------------------------------------------------------------
			// ハフマン符号表生成
			final int[] codeLen = StaticHuffman.FreqListToLenList(codeFreq);
			final int[] codeCode = StaticHuffman.LenListToCodeList(codeLen);
			final int[] offHiLen = getBetterOffHiLen(offHiFreq, StaticHuffman.FreqListToLenList(offHiFreq));
			final int[] offHiCode = StaticHuffman.LenListToCodeList(offHiLen);

			// ------------------------------------------------------------------
			// code部のハフマン符号表出力
			if (2 <= countNoZeroElement(codeFreq)) {
				writeCodeLenList(codeLen);                               // throws IOException
			} else {
				out.writeBits(15, 0x4210);                               // throws IOException
				out.writeBits(9, getNoZeroElementIndex(codeFreq)); // throws IOException
			}

			// ------------------------------------------------------------------
			// offHi部のハフマン符号表出力
			if (offHiLen != CONST_OFF_HI_LEN) {
				out.writeBit(1);                                         // throws IOException

				if (2 <= countNoZeroElement(offHiFreq)) {
					writeOffHiLenList(offHiLen);                         // throws IOException
				} else {
					out.writeBits(12, 0x0111);                           // throws IOException
					out.writeBits(7, getNoZeroElementIndex(offHiFreq));// throws IOException
				}
			} else {
				out.writeBit(0);                                         // throws IOException
			}

			// ------------------------------------------------------------------
			// ハフマン符号出力
			position = 0;
			flagBit = 0;
			for (int i = 0; i < blockSize; i++) {
				if (flagBit == 0) {
					flagBit = 0x80;
					flagPos = position++;
				}

				if (0 == (buffer[flagPos] & flagBit)) {
					final int code = buffer[position++] & 0xFF;
					out.writeBits(codeLen[code], codeCode[code]);    // throws IOException
				} else {
					final int code = buffer[position++] & 0xFF | 0x100;
					final int offset = (buffer[position++] & 0xFF) << 8 | buffer[position++] & 0xFF;
					final int offHi = offset >> 6;
					if (code < CodeMax) {
						out.writeBits(codeLen[code], codeCode[code]);// throws IOException
					} else {
						out.writeBits(codeLen[CodeMax], codeCode[CodeMax]);// throws IOException
						out.writeBits(8, code - CodeMax);                // throws IOException
					}
					out.writeBits(offHiLen[offHi], offHiCode[offHi]);// throws IOException
					out.writeBits(6, offset);                            // throws IOException
				}
				flagBit >>= 1;
			}

			// ------------------------------------------------------------------
			// 次のブロックのための処理
			for (int i = 0; i < codeFreq.length; i++) {
				codeFreq[i] = 0;
			}

			for (int i = 0; i < offHiFreq.length; i++) {
				offHiFreq[i] = 0;
			}

			blockSize = 0;
			position = 0;
			flagBit = 0;

		}// if( 0 < this.blockSize )
	}

	/**
	 * code部のハフマン符号長のリストを符号化しながら書き出す。
	 * 
	 * @param codeLen code部のハフマン符号長のリスト
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeCodeLenList(final int[] codeLen) throws IOException {
		for (final int element : codeLen) {
			if (0 < element) {
				out.writeBits(5, 0x10 | element - 1);             // throws IOException
			} else {
				out.writeBit(0);                                         // throws IOException
			}
		}
	}

	/**
	 * OffHi部のハフマン符号長のリストを符号化しながら書き出す。
	 * 
	 * @param OffHiLenList CodeFreq のハフマン符号長のリスト
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOffHiLenList(final int[] offHiLen) throws IOException {
		for (final int element : offHiLen) {
			out.writeBits(4, element);                               // throws IOException
		}
	}

	/**
	 * 配列内の 0でない要素数を得る。
	 * 
	 * @param array 配列
	 * @return 配列内の 0でない要素数
	 */
	private static int countNoZeroElement(final int[] array) {
		int count = 0;
		for (final int element : array) {
			if (0 != element) {
				count++;
			}
		}
		return count;
	}

	/**
	 * 配列内の 0でない最初の要素を得る。
	 * 
	 * @param array 配列
	 * @return 配列内の 0でない最初の要素 全ての要素が0の場合は 0を返す。
	 */
	private static int getNoZeroElementIndex(final int[] array) {
		for (int i = 0; i < array.length; i++) {
			if (0 != array[i]) {
				return i;
			}
		}
		return 0;
	}

	/**
	 * -lh3- の offHi部デコード用 ハフマン符号長リストを生成する。
	 * 
	 * @return -lh3- の offHi部デコード用 ハフマン符号長リスト
	 */
	private static int[] createConstOffHiLen() {
		final int length = DICTIONARY_SIZE >> 6;
		final int[] list = { 2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0 };

		final int[] offHiLen = new int[length];
		int index = 0;
		int len = list[index++];

		for (int i = 0; i < length; i++) {
			while (list[index] == i) {
				len++;
				index++;
			}
			offHiLen[i] = len;
		}
		return offHiLen;
	}

	/**
	 * OffHiFreqから生成された ハフマン符号長のリストと 固定ハフマン符号長のリストを比較して、出力ビット 数の少ないものを得る。
	 * 
	 * @param OffHiFreq offset部の上位6bitの出現頻度の表
	 * @param OffHiLen OffHiFreqから生成されたハフマン符 号長のリスト
	 * @return 出力ビット数の少ない方のハフマン符号長のリスト
	 */
	private static int[] getBetterOffHiLen(final int[] OffHiFreq, final int[] OffHiLen) {
		boolean detect = false;
		for (final int element : OffHiLen) {
			if (15 < element) { // 15 はwriteOffHiLenListで書きこめる最大のハフマン符号長を意味する。
				detect = true;
			}
		}

		if (!detect) {
			int origTotal = 1;
			int consTotal = 1;

			if (2 <= countNoZeroElement(OffHiFreq)) {
				origTotal += 4 * (DICTIONARY_SIZE >> 6);
			} else {
				origTotal += 4 * 3 + 7;
			}
			for (int i = 0; i < OffHiFreq.length; i++) {
				origTotal += OffHiFreq[i] * OffHiLen[i];
				consTotal += OffHiFreq[i] * CONST_OFF_HI_LEN[i];
			}

			if (origTotal < consTotal) {
				return OffHiLen;
			}
			return CONST_OFF_HI_LEN;
		}
		return CONST_OFF_HI_LEN;
	}

}