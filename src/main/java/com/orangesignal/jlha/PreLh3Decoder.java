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
 * -lh3- 解凍用の PreLzssDecoder。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class PreLh3Decoder implements PreLzssDecoder {

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
	 * -lh3- の圧縮データを供給する BitInputStream
	 */
	private BitInputStream in;

	/**
	 * 現在処理中のブロックの残りサイズを示す。
	 */
	private int blockSize;

	/**
	 * code 部のハフマン符号長の表
	 */
	private int[] codeLen;

	/**
	 * code 部復号用のテーブル 正の場合は codeTree のindexを示す。 負の場合は code を全ビット反転したもの。
	 */
	private short[] codeTable;

	/**
	 * codeTable を引くために必要なbit数。
	 */
	private int codeTableBits;

	/**
	 * codeTable に収まりきらないデータの復号用の木 正の場合は codeTree のindexを示す。 負の場合は code を全ビット反転したもの。
	 */
	private short[][] codeTree;

	/**
	 * offHi 部のハフマン符号長の表
	 */
	private int[] offHiLen;

	/**
	 * offHi 部復号用のテーブル 正の場合は offHi のindexを示す。 負の場合は code を全ビット反転したもの。
	 */
	private short[] offHiTable;

	/**
	 * offHiTable を引くために必要なbit数。
	 */
	private int offHiTableBits;

	/**
	 * offHiTable に収まりきらないデータの復号用の木 正の場合は offHi のindexを示す。 負の場合は code を全ビット反転したもの。
	 */
	private short[][] offHiTree;

	/** blockSizeのバックアップ用 */
	private int markBlockSize;
	/** codeLen のバックアップ用 */
	private int[] markCodeLen;
	/** codeTable のバックアップ用 */
	private short[] markCodeTable;
	/** codeTree のバックアップ用 */
	private short[][] markCodeTree;
	/** offHiLen のバックアップ用 */
	private int[] markOffHiLen;
	/** offHiTable のバックアップ用 */
	private short[] markOffHiTable;
	/** offHiTree のバックアップ用 */
	private short[][] markOffHiTree;

	/**
	 * -lh3- 解凍用 PreLzssDecoder を構築する。<br>
	 * テーブルサイズには デフォルト値を使用する。
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 */
	public PreLh3Decoder(final InputStream in) {
		this(in, 12, 8);
	}

	/**
	 * -lh3- 解凍用 PreLzssDecoder を構築する。<br>
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 * @param CodeTableBits code 部を復号するために使用する テーブルのサイズをビット長で指定する。 12 を指定すれば 4096 のルックアップテーブルを生成する。
	 * @param OffHiTableBits offHi 部を復号するために使用する テーブルのサイズをビット長で指定する。 8 を指定すれば 256 のルックアップテーブルを生成する。
	 * @exception IllegalArgumentException CodeTableBits, OffHiTableBits が 0以下の場合
	 */
	public PreLh3Decoder(final InputStream in, final int CodeTableBits, final int OffHiTableBits) {
		if (in != null && 0 < CodeTableBits && 0 < OffHiTableBits) {
			if (in instanceof BitInputStream) {
				this.in = (BitInputStream) in;
			} else {
				this.in = new BitInputStream(in);
			}
			blockSize = 0;
			codeTableBits = CodeTableBits;
			offHiTableBits = OffHiTableBits;
		} else if (in == null) {
			throw new NullPointerException("in");
		} else if (CodeTableBits <= 0) {
			throw new IllegalArgumentException("CodeTableBits too small. CodeTableBits must be larger than 1.");
		} else {
			throw new IllegalArgumentException("OffHiTableBits too small. OffHiTableBits must be larger than 1.");
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder

	/**
	 * -lh3- で圧縮された 1byte のLZSS未圧縮のデータ、 もしくは圧縮コードのうち一致長を読み込む。<br>
	 * 
	 * @return 1byte の 未圧縮のデータもしくは、 圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BadHuffmanTableException ハフマン木を構成するための ハフマン符号長の表が不正なため、 ハフマン復号器が生成できない場合
	 */
	@Override
	public int readCode() throws IOException {
		if (blockSize <= 0) {
			readBlockHead();
		}
		blockSize--;

		int code;
		try {
			int node = codeTable[in.peekBits(codeTableBits)];
			if (node < 0) {
				code = ~node;
				in.skipBits(codeLen[code]);
			} else {
				in.skipBits(codeTableBits);
				do {
					node = codeTree[in.readBit()][node];
				} while (0 <= node);
				code = ~node;
			}
		} catch (final NotEnoughBitsException exception) {
			final int avail = exception.getAvailableBits();
			int bits = in.peekBits(avail);
			bits = bits << codeTableBits - avail;
			final int node = codeTable[bits];

			if (node < 0) {
				code = ~node;
				if (in.skipBits(codeLen[code]) < codeLen[code]) {
					throw new EOFException();
				}
			} else {
				in.skipBits(avail);
				throw new EOFException();
			}
		} catch (final ArrayIndexOutOfBoundsException exception) {
			throw new EOFException();
		}

		final int CodeMax = CODE_SIZE - 1;
		if (code == CodeMax) {
			code += in.readBits(8);
		}
		return code;
	}

	/**
	 * -lh3- で圧縮された LZSS圧縮コードのうち一致位置を読み込む。<br>
	 * 
	 * @return -lh3- で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int readOffset() throws IOException {
		int offHi;
		try {
			int node = offHiTable[in.peekBits(offHiTableBits)];
			if (node < 0) {
				offHi = ~node;
				in.skipBits(offHiLen[offHi]);
			} else {
				in.skipBits(offHiTableBits);
				do {
					node = offHiTree[in.readBit()][node];
				} while (0 <= node);
				offHi = ~node;
			}
		} catch (final NotEnoughBitsException exception) {
			final int avail = exception.getAvailableBits();
			int bits = in.peekBits(avail);
			bits = bits << offHiTableBits - avail;
			final int node = offHiTable[bits];

			if (node < 0) {
				offHi = ~node;
				if (offHiLen[offHi] <= avail) {
					in.skipBits(offHiLen[offHi]);
				} else {
					in.skipBits(avail);
					throw new EOFException();
				}
			} else {
				in.skipBits(avail);
				throw new EOFException();
			}
		} catch (final ArrayIndexOutOfBoundsException exception) {
			throw new EOFException();
		}

		return offHi << 6 | in.readBits(6);
	}

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * InputStream の mark() と違い、readLimit で設定した 限界バイト数より前にマーク位置が無効になる可能性が ある事に注意すること。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#mark(int)
	 */
	@Override
	public void mark(int readLimit) {
		readLimit = readLimit * StaticHuffman.LIMIT_LEN / 8;
		if (blockSize < readLimit) {
			readLimit += 245;
		}
		in.mark(readLimit);

		markBlockSize = blockSize;
		markCodeLen = codeLen;
		markCodeTable = codeTable;
		markCodeTree = codeTree;
		markOffHiLen = offHiLen;
		markOffHiTable = offHiTable;
		markOffHiTree = offHiTree;
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

		blockSize = markBlockSize;
		codeLen = markCodeLen;
		codeTable = markCodeTable;
		codeTree = markCodeTree;
		offHiLen = markOffHiLen;
		offHiTable = markOffHiTable;
		offHiTree = markOffHiTree;
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
		int avail = in.available() * 8 / StaticHuffman.LIMIT_LEN;
		if (blockSize < avail) {
			avail -= 245;
		}
		return Math.max(avail, 0);
	}

	/**
	 * このストリームを閉じ、使用していた全ての資源を解放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		in.close();
		in = null;

		blockSize = 0;
		codeLen = null;
		codeTable = null;
		codeTree = null;
		offHiLen = null;
		offHiTable = null;
		offHiTree = null;

		markBlockSize = 0;
		markCodeLen = null;
		markCodeTable = null;
		markCodeTree = null;
		markOffHiLen = null;
		markOffHiTable = null;
		markOffHiTree = null;
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
	 * ハフマンブロックの先頭にある ブロックサイズやハフマン符号長のリストを読み込む。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BadHuffmanTableException ハフマン木を構成するための ハフマン符号長の表が不正なため、 ハフマン復号器が生成できない場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private void readBlockHead() throws IOException {
		// ブロックサイズ読み込み
		// 正常なデータの場合、この部分で EndOfStream に到達する。
		try {
			blockSize = in.readBits(16);                            // throws BitDataBrokenException, EOFException, IOException
		} catch (final BitDataBrokenException exception) {
			if (exception.getCause() instanceof EOFException) {
				throw (EOFException) exception.getCause();
			}
			throw exception;
		}

		// code 部の処理
		codeLen = readCodeLen();
		if (1 < codeLen.length) {
			final short[][] tableAndTree = StaticHuffman.createTableAndTree(codeLen, codeTableBits);
			codeTable = tableAndTree[0];
			codeTree = new short[][] { tableAndTree[1], tableAndTree[2] };
		} else {
			final int code = codeLen[0];
			codeLen = new int[CODE_SIZE];
			codeTable = new short[1 << codeTableBits];
			for (int i = 0; i < codeTable.length; i++) {
				codeTable[i] = (short) ~code;
			}
			codeTree = new short[][] { new short[0], new short[0] };
		}

		// offHi 部の処理
		offHiLen = readOffHiLen();
		if (1 < offHiLen.length) {
			final short[][] tableAndTree = StaticHuffman.createTableAndTree(offHiLen, offHiTableBits);
			offHiTable = tableAndTree[0];
			offHiTree = new short[][] { tableAndTree[1], tableAndTree[2] };
		} else {
			final int offHi = offHiLen[0];
			offHiLen = new int[DICTIONARY_SIZE >> 6];
			offHiTable = new short[1 << offHiTableBits];
			for (int i = 0; i < offHiTable.length; i++) {
				offHiTable[i] = (short) ~offHi;
			}
			offHiTree = new short[][] { new short[0], new short[0] };
		}
	}

	/**
	 * code部 のハフマン符号長のリストを読みこむ。
	 * 
	 * @return ハフマン符号長のリスト。 もしくは 長さ 1 の唯一のコード
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private int[] readCodeLen() throws IOException {
		final int[] codeLen = new int[CODE_SIZE];

		for (int i = 0; i < codeLen.length; i++) {
			if (in.readBoolean()) {
				codeLen[i] = in.readBits(4) + 1;
			}

			if (i == 2 && codeLen[0] == 1 && codeLen[1] == 1 && codeLen[2] == 1) {
				return new int[] { in.readBits(9) };
			}
		}
		return codeLen;
	}

	/**
	 * offHi部のハフマン符号長のリストを読みこむ
	 * 
	 * @return ハフマン符号長のリスト。 もしくは 長さ 1 の唯一のコード
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private int[] readOffHiLen() throws IOException {
		if (in.readBoolean()) {
			final int[] offHiLen = new int[DICTIONARY_SIZE >> 6];

			for (int i = 0; i < offHiLen.length; i++) {
				offHiLen[i] = in.readBits(4);

				if (i == 2 && offHiLen[0] == 1 && offHiLen[1] == 1 && offHiLen[2] == 1) {
					return new int[] { in.readBits(7) };
				}
			}
			return offHiLen;
		}
		return createConstOffHiLen();
	}

	/**
	 * -lh3- の offsetデコード用StaticHuffmanの ハフマン符号長リストを生成する。
	 * 
	 * @return -lh3- の offsetデコード用StaticHuffmanの ハフマン符号長リスト
	 */
	private static int[] createConstOffHiLen() {
		final int length = DICTIONARY_SIZE >> 6;
		final int[] list = { 2, 0x01, 0x01, 0x03, 0x06, 0x0D, 0x1F, 0x4E, 0 };

		final int[] LenList = new int[length];
		int index = 0;
		int len = list[index++];

		for (int i = 0; i < length; i++) {
			while (list[index] == i) {
				len++;
				index++;
			}
			LenList[i] = len;
		}
		return LenList;
	}

}