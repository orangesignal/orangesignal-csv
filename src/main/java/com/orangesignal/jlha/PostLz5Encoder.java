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
 * -lz5- 圧縮用 PostLzssEncoder。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLz5Encoder implements PostLzssEncoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 4096;

	/** 最大一致長 */
	private static final int MAX_MATCH = 18;

	/** 最小一致長 */
	private static final int THRESHOLD = 3;

	/**
	 * -lz5- 圧縮データを出力するストリーム
	 */
	private OutputStream out;

	/** 圧縮データの一時格納用バッファ */
	private byte[] buf;

	/** buf内の現在処理位置 */
	private int index;

	/** buf内の Lzss圧縮、非圧縮を示すフラグの位置を示す */
	private int flagIndex;

	/** Lzss圧縮、非圧縮を示すフラグ */
	private int flagBit;

	/**
	 * ストリーム内現在処理位置 lha の offset から larc の offset への変換に必要
	 */
	private int position;

	/**
	 * -lz5- 圧縮用 PostLzssEncoder を構築する。<br>
	 * 
	 * @param out 圧縮データを出力する出力ストリーム
	 */
	public PostLz5Encoder(final OutputStream out) {
		if (out != null) {
			this.out = out;
			position = 0;
			buf = new byte[1024];
			index = 0;
			flagIndex = 0;
			flagBit = 0x100;
		} else {
			throw new NullPointerException("out");
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
		if (flagBit == 0x100) {
			if (buf.length - (2 * 8 + 1) < index) {
				out.write(buf, 0, index);                      // throws IOException
				index = 0;
			}
			flagBit = 0x01;
			flagIndex = index++;
			buf[flagIndex] = 0;
		}

		if (code < 0x100) {
			buf[flagIndex] |= flagBit;
			buf[index++] = (byte) code;
			position++;
		} else {
			buf[index++] = (byte) code;
		}
		flagBit <<= 1;
	}

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
	 * 
	 * @param offset LZSS で圧縮された圧縮コードのうち一致位置
	 */
	@Override
	public void writeOffset(final int offset) {
		final int pos = position - offset - 1 - MAX_MATCH & DICTIONARY_SIZE - 1;

		final int matchlen = buf[--index] & 0x0F;
		buf[index++] = (byte) pos;
		buf[index++] = (byte) (pos >> 4 & 0xF0 | matchlen);

		position += matchlen + THRESHOLD;

	}

	/**
	 * この PostLzssEncoder にバッファリングされている 出力可能なデータを出力先の OutputStream に出力し、 出力先の OutputStream を flush() する。<br>
	 * このメソッドは出力不可能な 最大15バイトのデータを バッファリングしたまま 出力しない。<br>
	 * このメソッドは圧縮率を変化させない。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 */
	@Override
	public void flush() throws IOException {
		if (flagBit == 0x100) {
			out.write(buf, 0, index);                          // throws IOException
			out.flush();                                                   // throws IOException

			index = 0;
			flagBit = 0x01;
			flagIndex = index++;
			buf[flagIndex] = 0;
		} else {
			out.write(buf, 0, flagIndex);                      // throws IOException
			out.flush();                                                   // throws IOException

			System.arraycopy(buf, flagIndex, buf, 0, index - flagIndex);
			index -= flagIndex;
			flagIndex = 0;
		}
	}

	/**
	 * この出力ストリームと、接続された出力ストリームを閉じ、 使用していたリソースを開放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		out.write(buf, 0, index);                              // throws IOException
		out.close();                                                       // throws IOException

		out = null;
		buf = null;
	}

	/**
	 * -lz5-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lz5-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return PostLz5Encoder.DICTIONARY_SIZE;
	}

	/**
	 * -lz5-形式の LZSSの最長一致長を得る。
	 * 
	 * @return -lz5-形式の LZSSの最長一致長
	 */
	@Override
	public int getMaxMatch() {
		return PostLz5Encoder.MAX_MATCH;
	}

	/**
	 * -lz5-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lz5-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return PostLz5Encoder.THRESHOLD;
	}

}