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
 * -lh4-, -lh5-, -lh6-, -lh7- 解凍用の PreLzssDecoder。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.3 $
 */
public class PreLh5Decoder implements PreLzssDecoder {

	/**
	 * 接続された入力ストリーム
	 */
	private InputStream in;

	/**
	 * 速度低下抑止用バイト配列
	 */
	private byte[] cache;

	/**
	 * cache 内の有効バイト数
	 */
	private int cacheLimit;

	/**
	 * cache 内の現在処理位置
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
	 * offLen 部のハフマン符号長の表
	 */
	private int[] offLenLen;

	/**
	 * offLen 部復号用のテーブル 正の場合は offLenTree のindexを示す。 負の場合は offLen を全ビット反転したもの。
	 */
	private short[] offLenTable;

	/**
	 * offLenTable を引くために必要なbit数。
	 */
	private int offLenTableBits;

	/**
	 * offLenTable に収まりきらないデータの復号用の木 正の場合は offLenTree のindexを示す。 負の場合は offLen を全ビット反転したもの。
	 */
	private short[][] offLenTree;

	/**
	 * LZSS 辞書サイズ
	 */
	private int dictionarySize;

	/**
	 * LZSS 最長一致長
	 */
	private int maxMatch;

	/**
	 * LZSS 圧縮/非圧縮の閾値
	 */
	private int threshold;

	/**
	 * mark位置がキャッシュの範囲内にあるかを示す。 markされたとき true に設定され、 次に in から キャッシュへの読み込みが 行われたときに false に設定される。
	 */
	private boolean markPositionIsInCache;

	/** cache の バックアップ用 */
	private byte[] markCache;
	/** cacheAvailable のバックアップ用 */
	private int markCacheLimit;
	/** cachePosition のバックアップ用 */
	private int markCachePosition;
	/** bitBuffer のバックアップ用 */
	private int markBitBuffer;
	/** bitCount のバックアップ用 */
	private int markBitCount;
	/** blockSizeのバックアップ用 */
	private int markBlockSize;
	/** codeLen のバックアップ用 */
	private int[] markCodeLen;
	/** codeTable のバックアップ用 */
	private short[] markCodeTable;
	/** codeTree のバックアップ用 */
	private short[][] markCodeTree;
	/** offLenLen のバックアップ用 */
	private int[] markOffLenLen;
	/** offLenTable のバックアップ用 */
	private short[] markOffLenTable;
	/** offLenTree のバックアップ用 */
	private short[][] markOffLenTree;

	/**
	 * -lh5- 解凍用 PreLzssDecoder を構築する。<br>
	 * テーブルサイズはデフォルト値を使用する。
	 * 
	 * @param in -lh5-形式の圧縮データを供給する入力ストリーム
	 */
	public PreLh5Decoder(final InputStream in) {
		this(in, CompressMethod.LH5, 12, 8);
	}

	/**
	 * -lh4-,-lh5-,-lh6-,-lh7- 解凍用 PreLzssDecoder を構築する。<br>
	 * テーブルサイズには デフォルト値を使用する。
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 * @param method 圧縮法識別子<br>
	 *            &emsp;&emsp; CompressMethod.LH4 <br>
	 *            &emsp;&emsp; CompressMethod.LH5 <br>
	 *            &emsp;&emsp; CompressMethod.LH6 <br>
	 *            &emsp;&emsp; CompressMethod.LH7 <br>
	 *            &emsp;&emsp; の何れかを指定する。
	 * 
	 * @exception IllegalArgumentException method が上記以外の場合
	 */
	public PreLh5Decoder(final InputStream in, final String method) {

		this(in, method, 12, 8);
	}

	/**
	 * -lh4-,-lh5-,-lh6-,-lh7- 解凍用 PreLzssDecoder を構築する。
	 * 
	 * @param in 圧縮データを供給する入力ストリーム
	 * @param method 圧縮法識別子<br>
	 *            &emsp;&emsp; CompressMethod.LH4 <br>
	 *            &emsp;&emsp; CompressMethod.LH5 <br>
	 *            &emsp;&emsp; CompressMethod.LH6 <br>
	 *            &emsp;&emsp; CompressMethod.LH7 <br>
	 *            &emsp;&emsp; の何れかを指定する。
	 * @param CodeTableBits code 部を復号するために使用する テーブルのサイズをビット長で指定する。 12 を指定すれば 4096 のルックアップテーブルを生成する。
	 * @param OffLenTableBits offLen 部を復号するために使用する テーブルのサイズをビット長で指定する。 8 を指定すれば 256 のルックアップテーブルを生成する。
	 * 
	 * @exception IllegalArgumentException <br>
	 *                &emsp;&emsp; (1) method が上記以外の場合<br>
	 *                &emsp;&emsp; (2) CodeTableBits もしくは OffLenTableBits が 0以下の場合<br>
	 *                &emsp;&emsp; の何れか
	 */
	public PreLh5Decoder(final InputStream in, final String method, final int CodeTableBits, final int OffLenTableBits) {
		if (CompressMethod.LH4.equals(method) || CompressMethod.LH5.equals(method) || CompressMethod.LH6.equals(method) || CompressMethod.LH7.equals(method)) {

			dictionarySize = CompressMethod.toDictionarySize(method);
			maxMatch = CompressMethod.toMaxMatch(method);
			threshold = CompressMethod.toThreshold(method);

			if (in != null && 0 < CodeTableBits && 0 < OffLenTableBits) {
				this.in = in;
				cache = new byte[1024];
				cacheLimit = 0;
				cachePosition = 0;
				bitBuffer = 0;
				bitCount = 0;
				blockSize = 0;
				codeTableBits = CodeTableBits;
				offLenTableBits = OffLenTableBits;

				markPositionIsInCache = false;
				markCache = null;
				markCacheLimit = 0;
				markCachePosition = 0;
				markBitBuffer = 0;
				markBitCount = 0;

			} else if (in == null) {
				throw new NullPointerException("in");
			} else if (CodeTableBits <= 0) {
				throw new IllegalArgumentException("CodeTableBits too small. CodeTableBits must be larger than 1.");
			} else {
				throw new IllegalArgumentException("OffHiTableBits too small. OffHiTableBits must be larger than 1.");
			}
		} else if (null == method) {
			throw new NullPointerException("method");
		} else {
			throw new IllegalArgumentException("Unknown compress method " + method);
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder

	/**
	 * -lh5- 系の圧縮法で圧縮された 1byte のLZSS未圧縮のデータ、 もしくは圧縮コードのうち一致長を読み込む。<br>
	 * 
	 * @return 1byte の 未圧縮のデータ、 もしくは圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BadHuffmanTableException ハフマン木を構成するための ハフマン符号長の表が不正である場合
	 */
	@Override
	public int readCode() throws IOException {
		if (blockSize <= 0) {
			readBlockHead();
		}
		blockSize--;

		if (bitCount < 16) {
			if (2 <= cacheLimit - cachePosition) {
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount | (cache[cachePosition++] & 0xFF) << 16 - bitCount;
				bitCount += 16;
			} else {
				fillBitBuffer();

				int node = codeTable[bitBuffer >>> 32 - codeTableBits];
				if (0 <= node) {
					int bits = bitBuffer << codeTableBits;
					do {
						node = codeTree[bits >>> 31][node];
						bits <<= 1;
					} while (0 <= node);
				}
				final int len = codeLen[~node];
				if (len <= bitCount) {
					bitBuffer <<= len;
					bitCount -= len;
					return ~node;
				}
				bitCount = 0;
				bitBuffer = 0;
				throw new EOFException();
			}
		}

		int node = codeTable[bitBuffer >>> 32 - codeTableBits];
		if (0 <= node) {
			int bits = bitBuffer << codeTableBits;
			do {
				node = codeTree[bits >>> 31][node];
				bits <<= 1;
			} while (0 <= node);
		}
		final int len = codeLen[~node];
		bitBuffer <<= len;
		bitCount -= len;

		return ~node;
	}

	/**
	 * -lh5- 系の圧縮法で圧縮された LZSS圧縮コードのうち一致位置を読み込む。<br>
	 * 
	 * @return -lh5- 系で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public int readOffset() throws IOException {
		if (bitCount < 16) {
			if (2 <= cacheLimit - cachePosition) {
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount | (cache[cachePosition++] & 0xFF) << 16 - bitCount;
				bitCount += 16;
			} else {
				fillBitBuffer();
			}
		}

		int node = offLenTable[bitBuffer >>> 32 - offLenTableBits];
		if (0 <= node) {
			int bits = bitBuffer << offLenTableBits;
			do {
				node = offLenTree[bits >>> 31][node];
				bits <<= 1;
			} while (0 <= node);
		}
		int offlen = ~node;
		final int len = offLenLen[offlen];
		bitBuffer <<= len;
		bitCount -= len;

		offlen--;
		if (0 <= offlen) {
			return 1 << offlen | readBits(offlen);
		}
		return 0;
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.PreLzssDecoder

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の 読み込み位置に 戻れるようにする。<br>
	 * InputStream の mark() と違い、readLimit で設定した 限界バイト数より前にマーク位置が無効になる可能性が ある事に注意すること。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み 込んだ場合 reset()できなくなる可 能性がある。<br>
	 * @see PreLzssDecoder#mark(int)
	 */
	@Override
	public void mark(int readLimit) {
		// ------------------------------------------------------------------
		// ハフマン符号化で最悪の場合を考慮して readLimit を計算する
		if (blockSize < readLimit) {
			readLimit = readLimit * StaticHuffman.LIMIT_LEN / 8;
			readLimit += 272; // block head
		} else {
			readLimit = readLimit * StaticHuffman.LIMIT_LEN / 8;
		}

		// ------------------------------------------------------------------
		// BitInputStream 用キャッシュの readLimit を計算する。
		readLimit -= cacheLimit - cachePosition;
		readLimit -= bitCount / 8;
		readLimit += 4;
		readLimit = (readLimit + cache.length - 1) / cache.length * cache.length;

		// ------------------------------------------------------------------
		// mark 処理
		in.mark(readLimit);

		if (markCache == null) {
			markCache = cache.clone();
		} else {
			System.arraycopy(cache, 0, markCache, 0, cacheLimit);
		}
		markCacheLimit = cacheLimit;
		markCachePosition = cachePosition;
		markBitBuffer = bitBuffer;
		markBitCount = bitCount;
		markPositionIsInCache = true;

		markBlockSize = blockSize;
		markCodeLen = codeLen;
		markCodeTable = codeTable;
		markCodeTree = codeTree;
		markOffLenLen = offLenLen;
		markOffLenTable = offLenTable;
		markOffLenTree = offLenTree;
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
		if (markPositionIsInCache) {
			cachePosition = markCachePosition;
			bitBuffer = markBitBuffer;
			bitCount = markBitCount;

			blockSize = markBlockSize;
			codeLen = markCodeLen;
			codeTable = markCodeTable;
			codeTree = markCodeTree;
			offLenLen = markOffLenLen;
			offLenTable = markOffLenTable;
			offLenTree = markOffLenTree;
		} else if (!in.markSupported()) {
			throw new IOException("not support mark()/reset().");
		} else if (markCache == null) { // この条件式は未だにマークされていないことを示す。コンストラクタで markCache が null に設定されるのを利用する。
			throw new IOException("not marked.");
		} else {
			// in が reset() できない場合は
			// 最初の行の this.in.reset() で
			// IOException を投げることを期待している。
			in.reset();                                                    // throws IOException
			System.arraycopy(markCache, 0, cache, 0, markCacheLimit);
			cacheLimit = markCacheLimit;
			cachePosition = markCachePosition;
			bitBuffer = markBitBuffer;
			bitCount = markBitCount;

			blockSize = markBlockSize;
			codeLen = markCodeLen;
			codeTable = markCodeTable;
			codeTree = markCodeTree;
			offLenLen = markOffLenLen;
			offLenTable = markOffLenTable;
			offLenTree = markOffLenTree;
		}
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
	 * InputStream の available() と違い、 この最低バイト数は必ずしも保障されていない事に注意すること。<br>
	 * 
	 * @return ブロックしないで読み出せる最低バイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PreLzssDecoder#available()
	 */
	@Override
	public int available() throws IOException {
		int avail = cacheLimit - cachePosition + in.available() / cache.length * cache.length;// throws IOException
		avail += bitCount - 32;
		avail = avail / StaticHuffman.LIMIT_LEN;
		if (blockSize < avail) {
			avail -= 272;
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
		in.close();                                                        // throws IOException
		in = null;

		cache = null;
		cacheLimit = 0;
		cachePosition = 0;
		bitBuffer = 0;
		bitCount = 0;

		markCache = null;
		markCacheLimit = 0;
		markCachePosition = 0;
		markBitBuffer = 0;
		markBitCount = 0;
		markPositionIsInCache = false;

		blockSize = 0;
		codeLen = null;
		codeTable = null;
		codeTree = null;
		offLenLen = null;
		offLenTable = null;
		offLenTree = null;

		markBlockSize = 0;
		markCodeLen = null;
		markCodeTable = null;
		markCodeTree = null;
		markOffLenLen = null;
		markOffLenTable = null;
		markOffLenTree = null;
	}

	/**
	 * この PreLh5Decoder が扱うLZSS辞書のサイズを得る。
	 * 
	 * @return この PreLh5Decoder が扱うLZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return dictionarySize;
	}

	/**
	 * この PreLh5Decoder が扱うLZSSの最大一致長を得る。
	 * 
	 * @return この PreLh5Decoder が扱うLZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return maxMatch;
	}

	/**
	 * この PreLh5Decoder が扱う圧縮、非圧縮の閾値を得る。
	 * 
	 * @return この PreLh5Decoder が扱う圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return threshold;
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
	 * @exception NotEnoughBitsException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private void readBlockHead() throws IOException {
		// ブロックサイズ読み込み
		// 正常なデータの場合、この部分で EndOfStream に到達する。
		try {
			blockSize = readBits(16);                               // throws BitDataBrokenException, EOFException, IOException
		} catch (final BitDataBrokenException exception) {
			if (exception.getCause() instanceof EOFException) {
				throw (EOFException) exception.getCause();
			}
			throw exception;
		}

		// codeLen 部の処理
		int[] codeLenLen = readCodeLenLen();                               // throws BitDataBrokenException, EOFException, IOException
		short[] codeLenTable;
		if (null != codeLenLen) {
			codeLenTable = StaticHuffman.createTable(codeLenLen);             // throws BadHuffmanTableException
		} else {
			codeLenTable = new short[] { (short) readBits(5) };            // throws BitDataBrokenException EOFException IOException
			codeLenLen = new int[codeLenTable[0] + 1];
		}

		// code 部の処理
		codeLen = readCodeLen(codeLenTable, codeLenLen);            // throws BitDataBrokenException NotEnoughBitsException EOFException IOException
		if (null != codeLen) {
			final short[][] tableAndTree = StaticHuffman.createTableAndTree(codeLen, codeTableBits);// throws BadHuffmanTableException
			codeTable = tableAndTree[0];
			codeTree = new short[][] { tableAndTree[1], tableAndTree[2] };
		} else {
			final int code = readBits(9);                                      // throws BitDataBrokenException EOFException IOException
			codeLen = new int[256 + maxMatch - threshold + 1];
			codeTable = new short[1 << codeTableBits];
			for (int i = 0; i < codeTable.length; i++) {
				codeTable[i] = (short) ~code;
			}
			codeTree = new short[][] { new short[0], new short[0] };
		}

		// offLen 部の処理
		offLenLen = readOffLenLen();                                  // throws BitDataBrokenException EOFException IOException
		if (null != offLenLen) {
			final short[][] tableAndTree = StaticHuffman.createTableAndTree(offLenLen, offLenTableBits);// throws BadHuffmanTableException
			offLenTable = tableAndTree[0];
			offLenTree = new short[][] { tableAndTree[1], tableAndTree[2] };
		} else {
			final int offLen = readBits(Bits.len(Bits.len(dictionarySize)));// throws BitDataBrokenException EOFException IOException
			offLenLen = new int[Bits.len(dictionarySize)];
			offLenTable = new short[1 << offLenTableBits];
			for (int i = 0; i < offLenTable.length; i++) {
				offLenTable[i] = (short) ~offLen;
			}
			offLenTree = new short[][] { new short[0], new short[0] };
		}
	}

	/**
	 * Codeのハフマン符号長のリストの ハフマン符号を復号するための ハフマン符号長のリストを読みこむ。
	 * 
	 * @return ハフマン符号長のリスト。 符号長のリストが無い場合は null
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private int[] readCodeLenLen() throws IOException {
		final int listlen = readBits(5);                                       // throws BitDataBrokenException, EOFException, IOException
		if (0 < listlen) {
			final int[] codeLenLen = new int[listlen];
			int index = 0;

			while (index < listlen) {
				int codelenlen = readBits(3);                            // throws BitDataBrokenException, EOFException, IOException
				if (codelenlen == 0x07) {
					while (readBoolean()) {
						codelenlen++;                   // throws EOFException, IOException
					}
				}
				codeLenLen[index++] = codelenlen;

				if (index == 3) {
					index += readBits(2);                                // throws BitDataBrokenException, EOFException, IOException
				}
			}
			return codeLenLen;
		}
		return null;
	}

	/**
	 * Codeのハフマン符号長のリストを復号しながら読みこむ
	 * 
	 * @return ハフマン符号長のリスト。 符号長のリストが無い場合は null
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 * @exception NotEnouthBitsException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private int[] readCodeLen(final short[] codeLenTable, final int[] codeLenLen) throws IOException {
		final int codeLenTableBits = Bits.len(codeLenTable.length - 1);
		final int listlen = readBits(9);                                       // throws BitDataBrokenException, EOFException, IOException
		if (0 < listlen) {
			final int[] codeLen = new int[listlen];
			int index = 0;

			while (index < listlen) {
				fillBitBuffer();
				final int bits = 0 < codeLenTableBits ? bitBuffer >>> 32 - codeLenTableBits : 0;
				final int codelen = codeLenTable[bits];
				final int len = codeLenLen[codelen];
				bitBuffer <<= len;
				bitCount -= len;

				if (codelen == 0) {
					index++;
				} else if (codelen == 1) {
					index += readBits(4) + 3;        // throws BitDataBrokenException, EOFException, IOException
				} else if (codelen == 2) {
					index += readBits(9) + 20;       // throws BitDataBrokenException, EOFException, IOException
				} else {
					codeLen[index++] = codelen - 2;
				}
			}
			return codeLen;
		}
		return null;
	}

	/**
	 * offLen のハフマン符号長のリストを読みこむ
	 * 
	 * @return ハフマン符号長のリスト。 符号長のリストが無い場合は null
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合
	 * @exception BitDataBrokenException 予期せぬ原因でデータ読みこみが 中断されたため要求されたビット数 のデータが得られなかった場合
	 */
	private int[] readOffLenLen() throws IOException {
		final int listlen = readBits(Bits.len(Bits.len(dictionarySize)));// throws BitDataBrokenException, EOFException, IOException
		if (0 < listlen) {
			final int[] offLenLen = new int[listlen];
			int index = 0;

			while (index < listlen) {
				int offlenlen = readBits(3);                             // throws BitDataBrokenException, EOFException, IOException
				if (offlenlen == 0x07) {
					while (readBoolean()) {
						offlenlen++;                    // throws EOFException, IOException
					}
				}
				offLenLen[index++] = offlenlen;
			}
			return offLenLen;
		}
		return null;
	}

	// ------------------------------------------------------------------
	// staff of BitInputStream

	/**
	 * 接続された入力ストリームから 1ビットのデータを 真偽値として読み込む。<br>
	 * 
	 * @return 読み込まれた1ビットのデータが 1であれば true、0であれば false を返す。<br>
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 */
	private boolean readBoolean() throws IOException {
		if (0 < bitCount) {
			final boolean bool = bitBuffer < 0;
			bitBuffer <<= 1;
			bitCount -= 1;
			return bool;
		}
		fillBitBuffer();
		final boolean bool = bitBuffer < 0;
		bitBuffer <<= 1;
		bitCount -= 1;
		return bool;
	}

	/**
	 * 接続された入力ストリームから count ビットのデータを 読み込む。 戻り値が int値である事からも判るように 読み込むことのできる 最大有効ビット数は 32ビットで あるが、count は32以上の値を設定してもチェックを 受けないため それ以上の値を設定した場合は ビット データが読み捨てられる。<br>
	 * たとえば readBits( 33 ) としたときは まず1ビットの データを読み捨て、その後の 32ビットのデータを返す。<br>
	 * また count に 0以下の数字を設定して呼び出した場合、 データを読み込む動作を伴わないため 戻り値は 常に0、 EndOfStream に達していても EOFException を 投げない点に注意すること。<br>
	 * 
	 * @param count 読み込むデータのビット数
	 * @return 読み込まれたビットデータ。<br>
	 * @exception IOException 接続された入力ストリームで 入出力エラーが発生した場合
	 * @exception EOFException 既にEndOfStreamに達していた場合
	 * @exception BitDataBrokenException 読み込み途中で EndOfStreamに達したため 要求されたビット数のデータの読み込み に失敗した場合。<br>
	 */
	private int readBits(int count) throws IOException {
		if (0 < count) {
			if (count <= bitCount) {
				final int bits = bitBuffer >>> 32 - count;
				bitBuffer <<= count;
				bitCount -= count;
				return bits;
			}
			final int requested = count;
			int bits = 0;
			try {
				fillBitBuffer();                                       // throws LocalEOFException IOException
				while (bitCount < count) {
					count -= bitCount;
					if (count < 32) {
						bits |= bitBuffer >>> 32 - bitCount << count;
					}
					bitBuffer = 0;
					bitCount = 0;
					fillBitBuffer();                                   // throws LocalEOFException IOException
				}
				bits |= bitBuffer >>> 32 - count;
				bitBuffer <<= count;
				bitCount -= count;
				return bits;
			} catch (final LocalEOFException exception) {
				if (exception.thrownBy(this) && count < requested) {
					throw new BitDataBrokenException(exception, bits >>> count, requested - count);
				}
				throw exception;
			}
		}
		return 0;
	}

	/**
	 * この BitInputStream 内に蓄えられているビット数を得る。<br>
	 * 
	 * @return この BitInputStream 内に蓄えられているビット数。<br>
	 */
	private int cachedBits() {
		return bitCount + (cacheLimit - cachePosition << 3);
	}

	/**
	 * bitBuffer にデータを満たす。 EndOfStream 付近を除いて bitBuffer には 25bit のデータが確保されることを保障する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception LocalEOFException bitBuffer が空の状態で EndOfStream に達した場合
	 */
	private void fillBitBuffer() throws IOException {
		if (32 <= cachedBits()) {
			if (bitCount <= 24) {
				if (bitCount <= 16) {
					if (bitCount <= 8) {
						if (bitCount <= 0) {
							bitBuffer = cache[cachePosition++] << 24;
							bitCount = 8;
						}
						bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
						bitCount += 8;
					}
					bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
					bitCount += 8;
				}
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
				bitCount += 8;
			}
		} else if (bitCount < 25) {
			if (bitCount == 0) {
				bitBuffer = 0;
			}

			int count = Math.min(32 - bitCount >> 3, cacheLimit - cachePosition);
			while (0 < count--) {
				bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
				bitCount += 8;
			}
			fillCache();                                                   // throws IOException
			if (cachePosition < cacheLimit) {
				count = Math.min(32 - bitCount >> 3, cacheLimit - cachePosition);
				while (0 < count--) {
					bitBuffer |= (cache[cachePosition++] & 0xFF) << 24 - bitCount;
					bitCount += 8;
				}
			} else if (bitCount <= 0) {
				throw new LocalEOFException(this);
			}
		}
	}

	/**
	 * cache が空になった時に cache にデータを読み込む。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void fillCache() throws IOException {
		markPositionIsInCache = false;
		cacheLimit = 0;
		cachePosition = 0;

		// cache にデータを読み込む
		int read = 0;
		while (0 <= read && cacheLimit < cache.length) {
			read = in.read(cache, cacheLimit, cache.length - cacheLimit);         // throws IOException

			if (0 < read) {
				cacheLimit += read;
			}
		}
	}

	// ------------------------------------------------------------------
	// inner classes

	/**
	 * BitInputStream 内で EndOfStream の検出に EOFException を使用するのは少々問題があるので ローカルな EOFException を定義する。
	 */
	private static class LocalEOFException extends EOFException {

		private static final long serialVersionUID = 1L;

		/**
		 * この例外を投げたオブジェクト
		 */
		private final Object owner;

		// ------------------------------------------------------------------
		// Constructor

		/**
		 * コンストラクタ。
		 * 
		 * @param object この例外を投げたオブジェクト
		 */
		public LocalEOFException(final Object object) {
			super();
			owner = object;
		}

		/**
		 * この例外が object によって投げられたかどうかを得る。<br>
		 * 
		 * @param object オブジェクト
		 * @return この例外が objectによって 投げられた例外であれば true<br>違えば false<br>
		 */
		public boolean thrownBy(final Object object) {
			return owner == object;
		}

	}

}