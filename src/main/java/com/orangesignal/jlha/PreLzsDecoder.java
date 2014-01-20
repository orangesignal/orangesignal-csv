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
 * -lzs- 解凍用 PreLzssDecoder。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLzsDecoder implements PreLzssDecoder {

	/** 辞書サイズ */
	private static final int DICTIONARY_SIZE = 2048;

	/** 最大一致長 */
	private static final int MAX_MATCH = 17;

	/** 最小一致長 */
	private static final int THRESHOLD = 2;

	/** 一致位置のビット数 */
	private static final int OffsetBits = Bits.len(PreLzsDecoder.DICTIONARY_SIZE - 1);

	/** 一致長のビット数 */
	private static final int LengthBits = Bits.len(PreLzsDecoder.MAX_MATCH - PreLzsDecoder.THRESHOLD);

	/**
	 * -lzs- 形式の圧縮データを供給する BitInputStream
	 */
	private BitInputStream in;

	/**
	 * 現在処理位置。 LzssInputStreamの内部状態を取得できないために存在する。 LzssInputStreamの内部クラスとして書けば、positionは必要無い。
	 */
	private int position;

	/** 最も新しいLzssコードの一致位置 */
	private int matchOffset;

	/**
	 * 最も新しいLzssコードの一致長 LzssInputStreamの内部状態を取得できないために存在する。 LzssInputStreamの内部クラスとして書けば、matchLengthは必要無い。
	 */
	private int matchLength;

	/** matchPositionのバックアップ用 */
	private int markPosition;

	/** matchPositionのバックアップ用 */
	private int markMatchOffset;

	/** matchLengthのバックアップ用 */
	private int markMatchLength;

	// ------------------------------------------------------------------
	// Constructers

	/**
	 * -lzs- 解凍用 PreLzssDecoder を構築する。
	 * 
	 * @param in -lzs- 形式の圧縮データを供給する入力ストリーム
	 */
	public PreLzsDecoder(final InputStream in) {
		if (in != null) {
			if (in instanceof BitInputStream) {
				this.in = (BitInputStream) in;
			} else {
				this.in = new BitInputStream(in);
			}
			position = 0;
			matchOffset = 0;
			matchLength = 0;
		} else {
			throw new NullPointerException("in");
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder

	/**
	 * -lzs- で圧縮された 1byte の LZSS未圧縮のデータ、 もしくは圧縮コードのうち一致長を読み込む。<br>
	 * 
	 * @return 1byte の 未圧縮のデータもしくは、 圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int readCode() throws IOException {
		try {
			if (in.readBoolean()) {
				position++;
				return in.readBits(8);
			}
			matchOffset = in.readBits(OffsetBits);
			matchLength = in.readBits(LengthBits);
			return matchLength | 0x100;
		} catch (final BitDataBrokenException exception) {
			if (exception.getCause() instanceof EOFException) {
				throw (EOFException) exception.getCause();
			}
			throw exception;
		}
	}

	/**
	 * -lzs- で圧縮された圧縮コードのうち 一致位置を読み込む。<br>
	 * 
	 * @return -lzs- で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int readOffset() throws IOException {
		final int offset = position - matchOffset - 1 - PreLzsDecoder.MAX_MATCH & PreLzsDecoder.DICTIONARY_SIZE - 1;
		position += matchLength + PreLzsDecoder.THRESHOLD;
		return offset;
	}

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * InputStream の mark() と違い、readLimit で設定した 限界バイト数より前にマーク位置が無効になる可能性が ある事に注意すること。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#mark(int)
	 */
	@Override
	public void mark(final int readLimit) {
		in.mark((readLimit * 9 + 7) / 8 + 1);
		markPosition = position;
		markMatchOffset = matchOffset;
		markMatchLength = matchLength;
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
		// mark()しないで reset()しようとした場合、
		// 接続された InputStream が mark/resetをサポートしない場合は
		// BitInputStream の reset()によって IOExceptionが投げられる。
		in.reset();                                                        // throws IOException

		position = markPosition;
		matchOffset = markMatchOffset;
		matchLength = markMatchLength;
	}

	/**
	 * 接続された入力ストリームが mark() と reset() を サポートするかを得る。<br>
	 * 
	 * @return ストリームが mark() と reset() を サポートする場合は true。<br>サポートしない場合は false。<br>
	 */
	@Override
	public boolean markSupported() {
		return in.markSupported();
	}

	/**
	 * ブロックせずに読み出すことの出来る最低バイト数を得る。<br>
	 * InputStream の available() と違い、 この最低バイト数は保証される。<br>
	 * 
	 * @return ブロックしないで読み出せる最低バイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PreLzssDecoder#available()
	 */
	@Override
	public int available() throws IOException {
		return Math.max(in.availableBits() / 9 - 2, 0);
	}

	/**
	 * この出力とストリームと 接続されていたストリームを閉じ、 使用していたリソースを解放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		in.close();
		in = null;
	}

	/**
	 * -lzs-形式の LZSS辞書のサイズを得る。
	 * 
	 * @return -lzs-形式の LZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return PreLzsDecoder.DICTIONARY_SIZE;
	}

	/**
	 * -lzs-形式の LZSSの最長一致長を得る。
	 * 
	 * @return -lzs-形式の LZSSの最長一致長
	 */
	@Override
	public int getMaxMatch() {
		return PreLzsDecoder.MAX_MATCH;
	}

	/**
	 * -lzs-形式の LZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return -lzs-形式の LZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return PreLzsDecoder.THRESHOLD;
	}

}