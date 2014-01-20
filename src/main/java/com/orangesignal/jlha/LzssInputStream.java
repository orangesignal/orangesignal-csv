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
 * LZSS 圧縮されたデータを解凍しながら供給する入力ストリーム。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class LzssInputStream extends InputStream {

	/**
	 * LZSS圧縮コードを返す入力ストリーム
	 */
	private PreLzssDecoder decoder;

	/**
	 * LZSS圧縮に使用される閾値。 一致長が この値以上であれば、圧縮コードを出力する。
	 */
	private int threshold;

	/**
	 * LZSS圧縮に使用される値。 最大一致長を示す。
	 */
	private int maxMatch;

	/**
	 * 解凍後のデータサイズ
	 */
	private long length;

	/**
	 * LZSS圧縮を展開するためのバッファ。
	 */
	private byte[] textBuffer;

	/**
	 * 現在読み込み位置。 read() によって外部に読み出された位置を示す。
	 */
	private long textPosition;

	/**
	 * 現在読み込み位置。 LZSS圧縮コードを展開して得られた位置を示す。
	 */
	private long textDecoded;

	/** TextBuffer のバックアップ用 */
	private byte[] markTextBuffer;

	/** TextPosition のバックアップ用 */
	private long markTextPosition;

	/** TextDecoded のバックアップ用 */
	private long markTextDecoded;

	/**
	 * in から LZSS圧縮データ の入力を受けて、 解凍されたデータを提供する入力ストリームを構築する。 このコンストラクタから生成された LzssInputStreamは -lh1-等の解凍データの最後のデータを読み込んだ後、 次のデータの読み取りで必ずEndOfStreamに達するとは 限らないデータを正常に復元できない(終端以降にゴミ データがつく可能性がある)。
	 * 
	 * @param decoder LZSS圧縮データ供給ストリーム
	 */
	public LzssInputStream(final PreLzssDecoder decoder) {
		this(decoder, Long.MAX_VALUE);
	}

	/**
	 * in から LZSS圧縮データ の入力を受けて、 解凍されたデータを提供する入力ストリームを構築する。
	 * 
	 * @param decoder LZSS圧縮データ供給ストリーム
	 * @param length 解凍後のサイズ
	 */
	public LzssInputStream(final PreLzssDecoder decoder, final long length) {
		maxMatch = decoder.getMaxMatch();
		threshold = decoder.getThreshold();
		this.length = length;

		this.decoder = decoder;
		textBuffer = new byte[decoder.getDictionarySize()];
		textPosition = 0;
		textDecoded = 0;

		if (this.decoder instanceof PreLz5Decoder) {
			initLz5TextBuffer();
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.InputStream

	/**
	 * コンストラクタで指定された PreLzssDecoder の 圧縮されたデータを解凍し、1バイトのデータを供給する。
	 * 
	 * @return 解凍された 1バイトのデータ
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int read() throws IOException {
		if (textDecoded <= textPosition) {
			try {
				decode();                                                  // throws EOFException IOException
			} catch (final EOFException exception) {
				if (textDecoded <= textPosition) {
					return -1;
				}
			}
		}
		return textBuffer[(int) textPosition++ & textBuffer.length - 1] & 0xFF;
	}

	/**
	 * コンストラクタで指定された PreLzssDecoder の 圧縮されたデータを解凍し、bufferを満たすように 解凍されたデータを読み込む。
	 * 
	 * @param buffer データを読み込むバッファ
	 * @return 読みこんだデータ量
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int read(final byte[] buffer) throws IOException {
		return this.read(buffer, 0, buffer.length);
	}

	/**
	 * コンストラクタで指定された PreLzssDecoder の 圧縮されたデータを解凍し、buffer の index から length バイトのデータを読み込む。
	 * 
	 * @param buffer データを読み込むバッファ
	 * @param index buffer 内のデータ読みこみ開始位置
	 * @param length 読み込むデータ量
	 * @return 読みこんだデータ量
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int read(final byte[] buffer, final int index, final int length) throws IOException {
		int position = index;
		final int end = index + length;
		try {
			while (position < end) {
				if (textDecoded <= textPosition) {
					decode();                                              // throws IOException
				}

				position = copyTextBufferToBuffer(buffer, position, end);
			}
		} catch (final EOFException exception) {
			position = copyTextBufferToBuffer(buffer, position, end);

			if (position == index) {
				return -1;
			}
		}

		return position - index;
	}

	/**
	 * 解凍されたデータを lengthバイト読み飛ばす。
	 * 
	 * @param length 読み飛ばすデータ量(単位はバイト)
	 * @return 実際に読み飛ばしたバイト数
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public long skip(final long length) throws IOException {
		final long end = textPosition + length;
		try {
			while (textPosition < end) {
				if (textDecoded <= textPosition) {
					decode();
				}

				textPosition = Math.min(end, textDecoded);
			}
		} catch (final EOFException exception) {
			textPosition = Math.min(end, textDecoded);
		}

		return length - (end - textPosition);
	}

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * InputStream の mark() と違い、 readLimit で設定した 限界バイト数より前にマーク位置が無効になる可能性がある。 ただし、readLimit を無視して無限に reset() 可能な InputStream と接続している場合は readLimit に どのような値を設定されても reset() で必ずマーク位置に復旧できる事を保証する。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#mark(int)
	 */
	@Override
	public void mark(int readLimit) {
		readLimit -= (int) (textDecoded - textPosition);
		final int Size = textBuffer.length - maxMatch;
		readLimit = (readLimit + Size - 1) / Size * Size;
		decoder.mark(Math.max(readLimit, 0));

		if (markTextBuffer == null) {
			markTextBuffer = textBuffer.clone();
		} else {
			System.arraycopy(textBuffer, 0, markTextBuffer, 0, textBuffer.length);
		}
		markTextPosition = textPosition;
		markTextDecoded = textDecoded;
	}

	/**
	 * 接続された入力ストリームの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void reset() throws IOException {
		if (markTextBuffer == null) {
			throw new IOException("not marked.");
		} else if (textDecoded - markTextPosition <= textBuffer.length) {
			textPosition = markTextPosition;
		} else if (decoder.markSupported()) {
			// reset
			decoder.reset();                                               // throws IOException
			System.arraycopy(markTextBuffer, 0, textBuffer, 0, textBuffer.length);
			textPosition = markTextPosition;
			textDecoded = markTextDecoded;
		} else {
			throw new IOException("mark/reset not supported.");
		}
	}

	/**
	 * 接続された入力ストリームが mark() と reset() を サポートするかを得る。<br>
	 * 
	 * @return ストリームが mark() と reset() を サポートする場合は true。<br>サポートしない場合は false。<br>
	 */
	@Override
	public boolean markSupported() {
		return decoder.markSupported();
	}

	/**
	 * 接続された入力ストリームからブロックしないで 読み込むことのできるバイト数を得る。<br>
	 * 
	 * @return ブロックしないで読み出せるバイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int available() throws IOException {
		return (int) (textDecoded - textPosition) + decoder.available();
	}

	/**
	 * この入力ストリームを閉じ、使用していた 全てのリソースを開放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		decoder.close();
		decoder = null;
		textBuffer = null;
		markTextBuffer = null;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * private変数 this.in から圧縮データを読み込み 解凍しながら TextBuffer にデータを書きこむ。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException ストリーム終端に達した場合
	 */
	private void decode() throws IOException {
		if (textDecoded < length) {
			final int TextMask = textBuffer.length - 1;
			final int TextStart = (int) textDecoded & TextMask;
			int TextPos = TextStart;
			final int TextLimit = (int) (Math.min(textPosition + textBuffer.length - maxMatch, length) - textDecoded) + TextStart;
			try {
				while (TextPos < TextLimit) {
					final int Code = decoder.readCode();                             // throws EOFException IOException

					if (Code < 0x100) {
						textBuffer[TextMask & TextPos++] = (byte) Code;
					} else {
						int MatchLength = (Code & 0xFF) + threshold;
						int MatchPosition = TextPos - decoder.readOffset() - 1;// throws IOException

						while (0 < MatchLength--) {
							textBuffer[TextMask & TextPos++] = textBuffer[TextMask & MatchPosition++];
						}
					}
				}
			} finally {
				textDecoded += TextPos - TextStart;
			}
		} else {
			throw new EOFException();
		}
	}

	/**
	 * private 変数 this.TextBuffer から bufferにデータを転送する。
	 * 
	 * @param buffer TextBufferの内容をコピーするバッファ
	 * @param position buffer内の書き込み現在位置
	 * @param end buffer内の書き込み終了位置
	 * 
	 * @return bufferの次に書き込みが行われるべき位置
	 */
	private int copyTextBufferToBuffer(final byte[] buffer, int position, final int end) {
		if ((textPosition & ~(textBuffer.length - 1)) < (textDecoded & ~(textBuffer.length - 1))) {
			final int length = Math.min(textBuffer.length - ((int) textPosition & textBuffer.length - 1), end - position);

			System.arraycopy(textBuffer, (int) textPosition & textBuffer.length - 1, buffer, position, length);

			textPosition += length;
			position += length;
		}

		if (textPosition < textDecoded) {
			final int length = Math.min((int) (textDecoded - textPosition), end - position);

			System.arraycopy(textBuffer, (int) textPosition & textBuffer.length - 1, buffer, position, length);

			textPosition += length;
			position += length;
		}

		return position;
	}

	/**
	 * -lz5- 用に TextBuffer を初期化する。
	 */
	private void initLz5TextBuffer() {
		int position = 18;
		for (int i = 0; i < 256; i++) {
			for (int j = 0; j < 13; j++) {
				textBuffer[position++] = (byte) i;
			}
		}

		for (int i = 0; i < 256; i++) {
			textBuffer[position++] = (byte) i;
		}

		for (int i = 0; i < 256; i++) {
			textBuffer[position++] = (byte) (255 - i);
		}

		for (int i = 0; i < 128; i++) {
			textBuffer[position++] = 0;
		}

		while (position < textBuffer.length) {
			textBuffer[position++] = (byte) ' ';
		}
	}

}