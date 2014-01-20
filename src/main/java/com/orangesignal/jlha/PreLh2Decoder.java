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
import java.io.IOException;
import java.io.InputStream;

/**
 * -lh2- 解凍用 PreLzssDecoder。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh2Decoder implements PreLzssDecoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 8192;

	/** 最大一致長 */
	private static final int MAX_MATCH = 256;

	/** 最小一致長 */
	private static final int THRESHOLD = 3;

	/**
	 * code部のハフマン木のサイズ code部がこれ以上の値を扱う場合は余計なビットを出力して補う。
	 */
	private static final int CODE_SIZE = 286;

	/**
	 * -lh2- の圧縮データを供給する BitInputStream
	 */
	private BitInputStream in;

	/**
	 * Lzss非圧縮データ 1byte か Lzss圧縮コードのうち一致長を 得るための 動的ハフマン木
	 */
	private DynamicHuffman codeHuffman;

	/**
	 * Lzss圧縮コードの上位7bitの値を得るための動的ハフマン木
	 */
	private DynamicHuffman offHiHuffman;

	/**
	 * (解凍後のデータの)現在処理位置
	 */
	private int position;

	/**
	 * 次に addLeaf() すべき position
	 */
	private int nextPosition;

	/**
	 * 一致長
	 */
	private int matchLength;

	/** codeHuffman のバックアップ用 */
	private DynamicHuffman markCodeHuffman;

	/** offHiHuffman のバックアップ用 */
	private DynamicHuffman markOffHiHuffman;

	/** position のバックアップ用 */
	private int markPosition;

	/** nextPosition のバックアップ用 */
	private int markNextPosition;

	/** matchLength のバックアップ用 */
	private int markMatchLength;

	/**
	 * -lh2- 解凍用 PreLzssDecoder を構築する。<br>
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 */
	public PreLh2Decoder(final InputStream in) {
		if (in != null) {
			if (in instanceof BitInputStream) {
				this.in = (BitInputStream) in;
			} else {
				this.in = new BitInputStream(in);
			}
			codeHuffman = new DynamicHuffman(CODE_SIZE);
			offHiHuffman = new DynamicHuffman(DICTIONARY_SIZE >> 6, 1);

			position = 0;
			nextPosition = 1 << 6;
			matchLength = 0;
		} else {
			throw new NullPointerException("in");
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder
	/**
	 * -lh2- で圧縮された 1byte のLZSS未圧縮のデータ、 もしくは圧縮コードのうち一致長を読み込む。<br>
	 * 
	 * @return 1byte の 未圧縮のデータもしくは、 圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 */
	@Override
	public int readCode() throws IOException {
		final int CodeMax = CODE_SIZE - 1;

		int node = codeHuffman.childNode(DynamicHuffman.ROOT);
		while (0 <= node) {
			node = codeHuffman.childNode(node - (in.readBoolean() ? 1 : 0));// throws EOFException,IOException
		}
		int code = ~node;
		codeHuffman.update(code);

		if (code < 0x100) {
			position++;
		} else {
			if (code == CodeMax) {
				try {
					code += in.readBits(8);
				} catch (final BitDataBrokenException exception) {
					if (exception.getCause() instanceof EOFException) {
						throw (EOFException) exception.getCause();
					}
				}
			}
			matchLength = code - 0x100 + THRESHOLD;
		}
		return code;
	}

	/**
	 * -lh2- で圧縮された LZSS圧縮コードのうち一致位置を読み込む。<br>
	 * 
	 * @return -lh2- で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 */
	@Override
	public int readOffset() throws IOException {
		if (nextPosition < DICTIONARY_SIZE) {
			while (nextPosition < position) {
				offHiHuffman.addLeaf(nextPosition >> 6);
				nextPosition += 64;

				if (DICTIONARY_SIZE <= nextPosition) {
					break;
				}
			}
		}
		position += matchLength;

		int node = offHiHuffman.childNode(DynamicHuffman.ROOT);
		while (0 <= node) {
			node = offHiHuffman.childNode(node - (in.readBoolean() ? 1 : 0));// throws EOFException,IOException
		}
		final int offHi = ~node;
		offHiHuffman.update(offHi);

		return offHi << 6 | in.readBits(6);
	}

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * InputStream の mark() と違い、readLimit で設定した 限界バイト数より前にマーク位置が無効になる可能性が ある事に注意すること。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#available()
	 */
	@Override
	public void mark(final int readLimit) {
		in.mark(readLimit * 18 / 8 + 4);
		markCodeHuffman = (DynamicHuffman) codeHuffman.clone();
		markOffHiHuffman = (DynamicHuffman) offHiHuffman.clone();
		markPosition = position;
		markNextPosition = nextPosition;
		markMatchLength = matchLength;
	}

	/**
	 * 接続された入力ストリームの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void reset() throws IOException {
		// mark()しないで reset() しようとした場合、
		// readLimit を超えて reset() しようとした場合、
		// 接続された InputStream が markSupported() で false を返す場合は
		// BitInputStream が IOException を投げる。
		in.reset();                                                        // throws IOException

		codeHuffman = (DynamicHuffman) markCodeHuffman.clone();
		offHiHuffman = (DynamicHuffman) markOffHiHuffman.clone();
		position = markPosition;
		nextPosition = markNextPosition;
		matchLength = markMatchLength;
	}

	/**
	 * 接続された入力ストリームが mark() と reset() を サポートするかを得る。<br>
	 * 
	 * @return ストリームが mark() と reset() を サポートする場合は true。<br>
	 *         サポートしない場合は false。<br>
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
	 * InputStream の available() と違い、 この最低バイト数は必ずしも保障されていない事に注意すること。<br>
	 * 
	 * @return ブロックしないで読み出せる最低バイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PreLzssDecoder#available()
	 */
	@Override
	public int available() throws IOException {
		return Math.max(in.availableBits() / 18 - 4, 0);                 // throws IOException
	}

	/**
	 * このストリームを閉じ、使用していた全ての資源を解放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		in.close();                                                        // throws IOException

		in = null;
		codeHuffman = null;
		offHiHuffman = null;
		markCodeHuffman = null;
		markOffHiHuffman = null;
	}

	/**
	 * -lh2-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lh2-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return DICTIONARY_SIZE;
	}

	/**
	 * -lh2-形式の LZSSの最大一致長を得る。
	 * 
	 * @return -lh2-形式の LZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return MAX_MATCH;
	}

	/**
	 * -lh2-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lh2-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return THRESHOLD;
	}

}