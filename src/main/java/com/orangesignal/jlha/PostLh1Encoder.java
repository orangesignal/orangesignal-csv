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
 * -lh1- 圧縮用の PostLzssEncoder。 <br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PostLh1Encoder implements PostLzssEncoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 4096;

	/** 最大一致長 */
	private static final int MAX_MATCH = 60;

	/** 最小一致長 */
	private static final int THRESHOLD = 3;

	/**
	 * -lh1- 形式の圧縮データの出力先の ビット出力ストリーム
	 */
	private BitOutputStream out;

	/**
	 * Code部圧縮用適応的ハフマン木
	 */
	private DynamicHuffman huffman;

	/**
	 * offset部の上位6bit圧縮用ハフマン符号の表
	 */
	private int[] offHiCode;

	/**
	 * offset部の上位6bit圧縮用ハフマン符号長の表
	 */
	private int[] offHiLen;

	/**
	 * -lh1- 圧縮用 PostLzssEncoder を構築する。
	 * 
	 * @param out 圧縮データを受け取る出力ストリーム
	 */
	public PostLh1Encoder(final OutputStream out) {
		if (out != null) {
			if (out instanceof BitOutputStream) {
				this.out = (BitOutputStream) out;
			} else {
				this.out = new BitOutputStream(out);
			}
			huffman = new DynamicHuffman(314);
			offHiLen = createLenList();
			try {
				offHiCode = StaticHuffman.LenListToCodeList(offHiLen);
			} catch (final BadHuffmanTableException exception) {
			}
		} else {
			throw new NullPointerException("out");
		}
	}

	/**
	 * -lh1- の offsetデコード用StaticHuffmanの ハフマン符号長リストを生成する。
	 * 
	 * @return -lh1- の offsetデコード用StaticHuffmanの ハフマン符号長リスト
	 */
	private static int[] createLenList() {
		final int length = 64;
		final int[] list = { 3, 0x01, 0x04, 0x0C, 0x18, 0x30, 0 };

		final int[] LenList = new int[length];
		int index = 0;
		int len = list[index++];

		for (int i = 0; i < length; i++) {
			if (list[index] == i) {
				len++;
				index++;
			}
			LenList[i] = len;
		}
		return LenList;
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
		int node = huffman.codeToNode(code);
		int hcode = 0;
		int hlen = 0;
		do {
			hcode >>>= 1;
			hlen++;
			if ((node & 1) != 0) {
				hcode |= 0x80000000;
			}

			node = huffman.parentNode(node);
		} while (node != DynamicHuffman.ROOT);

		out.writeBits(hlen, hcode >> 32 - hlen);                     // throws IOException
		huffman.update(code);
	}

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
	 * 
	 * @param offset LZSS で圧縮された圧縮コードのうち一致位置
	 */
	@Override
	public void writeOffset(final int offset) throws IOException {
		final int offHi = offset >> 6;
		out.writeBits(offHiLen[offHi], offHiCode[offHi]);      // throws IOException
		out.writeBits(6, offset);                                        // throws IOException
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
		huffman = null;
		offHiLen = null;
		offHiCode = null;
	}

	/**
	 * -lh1-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lh1-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return DICTIONARY_SIZE;
	}

	/**
	 * -lh1-形式の LZSSの最大一致長を得る。
	 * 
	 * @return -lz5-形式の LZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return MAX_MATCH;
	}

	/**
	 * -lh1-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lh1-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return THRESHOLD;
	}

}