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
 * -lzs- 圧縮用 PostLzssEncoder。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLzsEncoder implements PostLzssEncoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 2048;

	/** 最大一致長 */
	private static final int MAX_MATCH = 17;

	/** 最小一致長 */
	private static final int THRESHOLD = 2;

	/** 一致位置のビット数 */
	private static final int PositionBits = Bits.len(DICTIONARY_SIZE - 1);

	/** 一致長のビット数 */
	private static final int LengthBits = Bits.len(MAX_MATCH - THRESHOLD);

	/**
	 * -lzs- 形式のデータを出力するビット出力ストリーム
	 */
	private BitOutputStream out;

	/**
	 * ストリーム内現在処理位置
	 */
	private int position;

	/**
	 * 現在処理中のLZSS圧縮コード
	 */
	private int matchLength;

	/**
	 * -lzs- 圧縮用 PostLzssEncoder を構築する。
	 * 
	 * @param out -lzs- 形式の圧縮データを出力するストリーム
	 */
	public PostLzsEncoder(final OutputStream out) {
		if (out != null) {
			if (out instanceof BitOutputStream) {
				this.out = (BitOutputStream) out;
			} else {
				this.out = new BitOutputStream(out);
			}
			position = 0;
			matchLength = 0;
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
		if (code < 0x100) {
			out.writeBit(1);                                             // throws IOException
			out.writeBits(8, code);                                      // throws IOException
			position++;
		} else {
			// close() 後の writeCode() で
			// NullPointerException を投げることを期待している。
			out.writeBit(0);                                             // throws IOException
			matchLength = code - 0x100;
		}
	}

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
	 * 
	 * @param offset LZSS で圧縮された圧縮コードのうち一致位置
	 */
	@Override
	public void writeOffset(final int offset) throws IOException {
		final int pos = position - offset - 1 - MAX_MATCH & DICTIONARY_SIZE - 1;

		position += matchLength + THRESHOLD;

		out.writeBits(PositionBits, pos);                           // throws IOException
		out.writeBits(LengthBits, matchLength);              // throws IOException
	}

	/**
	 * この PostLzssEncoder にバッファリングされている 全ての 8ビット単位のデータを出力先の OutputStream に出力し、 出力先の OutputStream を flush() する。<br>
	 * このメソッドは圧縮率を変化させない。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 * @see BitOutputStream#flush()
	 */
	@Override
	public void flush() throws IOException {
		out.flush();                                                       // throws IOException
	}

	/**
	 * この出力ストリームと、接続された出力ストリームを閉じ、 使用していたリソースを解放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		out.close();                                                       // throws IOException
		out = null;
	}

	/**
	 * -lzs-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lzs-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return DICTIONARY_SIZE;
	}

	/**
	 * -lzs-形式の LZSSの最大一致長を得る。
	 * 
	 * @return -lzs-形式の LZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return MAX_MATCH;
	}

	/**
	 * -lzs-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lzs-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return THRESHOLD;
	}

}