/**
 * PreLz5Decoder.java
 * 
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
 * -lz5- 解凍用 PreLzssDecoder。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLz5Decoder implements PreLzssDecoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 4096;

	/** 最大一致長 */
	private static final int MAX_MATCH = 18;

	/** 最小一致長 */
	private static final int THRESHOLD = 3;

	/**
	 * -lz5- 形式の圧縮データを供給するストリーム
	 */
	private InputStream in;

	/**
	 * 現在処理位置。 larc の一致位置から lha の一致位置への変換に必要
	 */
	private int position;

	/** Lzss圧縮情報のうち 一致位置(larcの一致位置) */
	private int matchPos;

	/** Lzss圧縮符号のうち 一致長 */
	private int matchLen;

	/** 8つのLzss圧縮、非圧縮を示すフラグをまとめたもの */
	private int flagByte;

	/** Lzss圧縮、非圧縮を示すフラグ */
	private int flagBit;

	/** positionのバックアップ用 */
	private int markPosition;

	/** matchOffsetのバックアップ用 */
	private int markMatchPos;

	/** matchLengthのバックアップ用 */
	private int markMatchLen;

	/** flagByteのバックアップ用。 */
	private int markFlagByte;

	/** flagCountのバックアップ用。 */
	private int markFlagBit;

	/**
	 * -lz5- 解凍用 PreLzssDecoder を構築する。
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 */
	public PreLz5Decoder(final InputStream in) {
		if (in != null) {
			if (in instanceof CachedInputStream) {
				this.in = in;
			} else {
				this.in = new CachedInputStream(in);
			}

			position = 0;
			matchPos = 0;
			matchLen = 0;
			flagByte = 0;
			flagBit = 0x100;

			markPosition = 0;
			markMatchPos = 0;
			markMatchLen = 0;
			markFlagByte = 0;
			markFlagBit = 0;
		} else {
			throw new NullPointerException("in");
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.PreLzssDecoder

	/**
	 * -lz5- で圧縮された 1byte の LZSS未圧縮のデータ、 もしくは圧縮コードのうち一致長を読み込む。<br>
	 * 
	 * @return 1byte の 未圧縮のデータもしくは、 圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 */
	@Override
	public int readCode() throws IOException {
		if (flagBit == 0x100) {
			flagByte = in.read();                                    // throws IOException

			if (0 <= flagByte) {
				flagBit = 0x01;
			} else {
				throw new EOFException();
			}
		}

		if (0 != (flagByte & flagBit)) {
			flagBit <<= 1;
			position++;
			final int ret = in.read();                                           // throws IOException
			if (0 <= ret) {
				return ret;
			}
			throw new EOFException();
		}
		flagBit <<= 1;
		final int c1 = in.read();                                          // throws IOException
		final int c2 = in.read();                                          // throws IOException

		if (0 <= c1) {
			matchPos = (c2 & 0xF0) << 4 | c1;
			matchLen = c2 & 0x0F;
			return matchLen | 0x100;
		}
		throw new EOFException();
	}

	/**
	 * -lz5- で圧縮された 圧縮コードのうち一致位置を読み込む。<br>
	 * 
	 * @return -lz5- で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int readOffset() throws IOException {
		final int offset = position - matchPos - 1 - MAX_MATCH & DICTIONARY_SIZE - 1;
		position += matchLen + THRESHOLD;
		return offset;
	}

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#mark(int)
	 */
	@Override
	public void mark(final int readLimit) {
		in.mark((readLimit * 9 + 7) / 8 + 2);
		markPosition = position;
		markMatchLen = matchLen;
		markMatchPos = matchPos;
		markFlagByte = flagByte;
		markFlagBit = flagBit;
	}

	/**
	 * 接続された入力ストリームの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。<br>
	 * 
	 * @exception IOException <br>
	 *                &emsp;&emsp; (1) mark() せずに reset() しようとした場合。<br>
	 *                &emsp;&emsp; (2) 接続された入力ストリームが markSupported()で false を返す場合。<br>
	 *                &emsp;&emsp; (3) 接続された入力ストリームで 入出力エラーが発生した場合。<br>
	 *                &emsp;&emsp; の何れか。
	 */
	@Override
	public void reset() throws IOException {
		// mark() していないのに reset() しようとした場合、
		// 接続されたストリームがmark/resetをサポートしない場合は
		// CachedInputStream が IOException を投げる。
		in.reset();                                                        // throws IOException

		position = markPosition;
		matchLen = markMatchLen;
		matchPos = markMatchPos;
		flagByte = markFlagByte;
		flagBit = markFlagBit;
	}

	/**
	 * 接続されたストリームが mark() と reset() をサポートするかを返す。
	 * 
	 * @return 接続されたストリームが mark,reset をサポートするならtrue, サポートしないなら false
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
	 * この値は保証される。
	 * 
	 * @return ブロックしないで読み出せる最低バイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PreLzssDecoder#available()
	 */
	@Override
	public int available() throws IOException {
		return Math.max(in.available() * 8 / 9 - 2, 0);                       // throws IOException
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
	}

	/**
	 * -lz5-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lz5-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return DICTIONARY_SIZE;
	}

	/**
	 * -lz5-形式の LZSSの最大一致長を得る。
	 * 
	 * @return -lz5-形式の LZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return MAX_MATCH;
	}

	/**
	 * -lz5-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lz5-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return THRESHOLD;
	}

}