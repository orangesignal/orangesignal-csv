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
 * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: PostLh5Encoder.java,v $
 * Revision 1.4  2002/12/08 00:00:00  dangan
 * [change]
 *     クラス名 を PostLh5EncoderCombo から PostLh5Encoder に変更。
 * 
 * Revision 1.3  2002/12/06 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 * 
 * Revision 1.2  2002/12/01 00:00:00  dangan
 * [change]
 *     flush() されないかぎり 
 *     接続された OutputStream をflush() しないように変更。
 * 
 * Revision 1.1  2002/12/01 00:00:00  dangan
 * [bug fix] 
 *     writeOutGroup でローカル変数 offLenFreq を使用しなければ
 *     ならない部分で this.offLenFreq を使用していた。
 * [maintenance]
 *     PostLh5Encoder から受け継いだインスタンスフィールド
 *     buffer, codeFreq, offLenFreq 廃止
 *     ソース整備
 * 
 * Revision 1.0  2002/07/31 00:00:00  dangan
 * add to version control
 * [improvement]
 *     DivideNum を導入する事によって処理するパターン数の減少を図る。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.4 $
 */
public class PostLh5Encoder implements PostLzssEncoder {

	/**
	 * -lh4-, -lh5-, -lh6-, -lh7- 形式の圧縮データの出力先の ビット出力ストリーム
	 */
	private BitOutputStream out;

	/**
	 * LZSSの辞書サイズ
	 */
	private int dictionarySize;

	/**
	 * LZSSの最大一致長
	 */
	private int maxMatch;

	/**
	 * LZSS 圧縮/非圧縮 の閾値
	 */
	private int threshold;

	/**
	 * 辞書サイズを示すのに必要なバイト数
	 */
	private int dictionarySizeByteLen;

	/**
	 * this.block[ this.currentBlock ] 内の現在処理位置
	 */
	private int position;

	/**
	 * flag バイト内の圧縮/非圧縮を示すフラグ
	 */
	private int flagBit;

	/**
	 * this.block[ this.currentBlock ] 内の flagバイトの位置
	 */
	private int flagPos;

	/**
	 * 現在処理中のハフマンブロックを示す。
	 */
	private int currentBlock;

	/**
	 * ハフマンコード格納用バッファ群
	 */
	private byte[][] block;

	/**
	 * 各ブロックの code データの数
	 */
	private int[] blockSize;

	/**
	 * 該当するブロックの code 部分の頻度表を持つ頻度表群
	 */
	private int[][] blockCodeFreq;

	/**
	 * 該当するブロックの offLen 部分の頻度表を持つ頻度表群
	 */
	private int[][] blockOffLenFreq;

	/**
	 * 全ブロックを幾つかのグループに分割するパターンの配列。
	 */
	private int[][] pattern;

	/**
	 * 複数ブロックを組み合わせたグループの配列。 this.group[0] 全ブロックを持つグループが this.group[1] this.group[2] には 全ブロックから各々最後と最初のブロックを欠いたグループが …というようにピラミッド状に構成される。
	 */
	private int[][] group;

	/**
	 * -lh5- 圧縮用 PostLzssEncoder を構築する。<br>
	 * バッファサイズにはデフォルト値が使用される。
	 * 
	 * @param out 圧縮データを受け取る OutputStream
	 */
	public PostLh5Encoder(final OutputStream out) {
		this(out, CompressMethod.LH5);
	}

	/**
	 * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
	 * バッファサイズにはデフォルト値が使用される。
	 * 
	 * @param out 圧縮データを受け取る OutputStream
	 * @param method 圧縮法を示す文字列<br>
	 *            &emsp;&emsp; CompressMethod.LH4 <br>
	 *            &emsp;&emsp; CompressMethod.LH5 <br>
	 *            &emsp;&emsp; CompressMethod.LH6 <br>
	 *            &emsp;&emsp; CompressMethod.LH7 <br>
	 *            &emsp;&emsp; の何れかを指定する。
	 * 
	 * @exception IllegalArgumentException method が上記以外の場合
	 */
	public PostLh5Encoder(final OutputStream out, final String method) {
		this(out, method, 16384);
	}

	/**
	 * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
	 * 
	 * @param out 圧縮データを受け取る OutputStream
	 * @param method 圧縮法を示す文字列<br>
	 *            &emsp;&emsp; CompressMethod.LH4 <br>
	 *            &emsp;&emsp; CompressMethod.LH5 <br>
	 *            &emsp;&emsp; CompressMethod.LH6 <br>
	 *            &emsp;&emsp; CompressMethod.LH7 <br>
	 *            &emsp;&emsp; の何れかを指定する。
	 * @param BufferSize LZSS圧縮データを退避しておく バッファのサイズ
	 * 
	 * @exception IllegalArgumentException <br>
	 *                &emsp;&emsp; (1) method が上記以外の場合<br>
	 *                &emsp;&emsp; (2) BufferSize が小さすぎる場合<br>
	 *                &emsp;&emsp; の何れか
	 */
	public PostLh5Encoder(final OutputStream out, final String method, final int BufferSize) {
		this(out, method, 1, BufferSize, 0);
	}

	/**
	 * -lh4-, -lh5-, -lh6-, -lh7- 圧縮用 PostLzssEncoder を構築する。<br>
	 * 1つが BlockSizeバイト の BlockNum 個のブロックを組み合わせて 最も出力ビット数の少ない構成で出力する。 組み合わせは 全ブロックを DivideNum + 1 個に分割して得られる 全パターンが試される。
	 * 
	 * @param out 圧縮データを受け取る OutputStream
	 * @param method 圧縮法を示す文字列<br>
	 *            &emsp;&emsp; CompressMethod.LH4 <br>
	 *            &emsp;&emsp; CompressMethod.LH5 <br>
	 *            &emsp;&emsp; CompressMethod.LH6 <br>
	 *            &emsp;&emsp; CompressMethod.LH7 <br>
	 *            &emsp;&emsp; の何れかを指定する。
	 * @param BlockNum ブロック数
	 * @param BlockSize 1ブロックのバイト数
	 * @param DivideNum 最大分割数
	 * 
	 * @exception IllegalArgumentException <br>
	 *                &emsp;&emsp; (1) CompressMethod が上記以外の場合<br>
	 *                &emsp;&emsp; (2) BlockNum が 0以下の場合<br>
	 *                &emsp;&emsp; (3) BlockSize が小さすぎる場合<br>
	 *                &emsp;&emsp; (4) DivideNum が 0未満であるか、BlockNum以上の場合<br>
	 *                &emsp;&emsp; のいずれか。
	 */
	public PostLh5Encoder(final OutputStream out, final String method, final int BlockNum, final int BlockSize, final int DivideNum) {
		if (CompressMethod.LH4.equals(method) || CompressMethod.LH5.equals(method) || CompressMethod.LH6.equals(method) || CompressMethod.LH7.equals(method)) {
			dictionarySize = CompressMethod.toDictionarySize(method);
			maxMatch = CompressMethod.toMaxMatch(method);
			threshold = CompressMethod.toThreshold(method);
			dictionarySizeByteLen = (Bits.len(dictionarySize - 1) + 7) / 8;

			final int minCapacity = (dictionarySizeByteLen + 1) * 8 + 1;

			if (out != null && 0 < BlockNum && 0 <= DivideNum && DivideNum < BlockNum && minCapacity <= BlockSize) {

				if (out instanceof BitOutputStream) {
					this.out = (BitOutputStream) out;
				} else {
					this.out = new BitOutputStream(out);
				}

				currentBlock = 0;
				block = new byte[BlockNum][];
				blockSize = new int[BlockNum];
				blockCodeFreq = new int[BlockNum][];
				blockOffLenFreq = new int[BlockNum][];

				final int codeFreqSize = 256 + maxMatch - threshold + 1;
				final int offLenFreqSize = Bits.len(dictionarySize);
				for (int i = 0; i < BlockNum; i++) {
					block[i] = new byte[BlockSize];
					blockCodeFreq[i] = new int[codeFreqSize];
					blockOffLenFreq[i] = new int[offLenFreqSize];
				}

				group = createGroup(BlockNum, DivideNum);
				pattern = createPattern(BlockNum, DivideNum);

				position = 0;
				flagBit = 0;
				flagPos = 0;
			} else if (out == null) {
				throw new NullPointerException("out");
			} else if (BlockNum <= 0) {
				throw new IllegalArgumentException("BlockNum too small. BlockNum must be 1 or more.");
			} else if (DivideNum < 0 || BlockNum <= DivideNum) {
				throw new IllegalArgumentException("DivideNum out of bounds( 0 to BlockNum - 1(" + (BlockNum - 1) + ") ).");
			} else {
				throw new IllegalArgumentException("BlockSize too small. BlockSize must be larger than " + minCapacity);
			}

		} else if (method == null) {
			throw new NullPointerException("method");
		} else {
			throw new IllegalArgumentException("Unknown compress method. " + method);
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
		final int need = (0x100 <= code ? dictionarySizeByteLen + 1 : 1) + (flagBit == 0 ? 1 : 0);
		if (block[currentBlock].length - position < need || 65535 <= blockSize[currentBlock]) {
			currentBlock++;
			if (block.length <= currentBlock) {
				writeOut();
			} else {
				position = 0;
			}

			flagBit = 0x80;
			flagPos = position++;
			block[currentBlock][flagPos] = 0;
		} else if (flagBit == 0) {
			flagBit = 0x80;
			flagPos = position++;
			block[currentBlock][flagPos] = 0;
		}

		// データ格納
		block[currentBlock][position++] = (byte) code;

		// 上位1ビットをフラグとして格納
		if (0x100 <= code) {
			block[currentBlock][flagPos] |= flagBit;
		}
		flagBit >>= 1;

		// 頻度表更新
		blockCodeFreq[currentBlock][code]++;

		// ブロックサイズ更新
		blockSize[currentBlock]++;
	}

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を書きこむ。<br>
	 * 
	 * @param offset LZSS で圧縮された圧縮コードのうち一致位置
	 */
	@Override
	public void writeOffset(final int offset) {
		// データ格納
		int shift = dictionarySizeByteLen - 1 << 3;
		while (0 <= shift) {
			block[currentBlock][position++] = (byte) (offset >> shift);
			shift -= 8;
		}

		// 頻度表更新
		blockOffLenFreq[currentBlock][Bits.len(offset)]++;
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
		writeOut();                                                        // throws IOException
		out.close();                                                       // throws IOException

		out = null;
		block = null;
		blockCodeFreq = null;
		blockOffLenFreq = null;
		group = null;
		pattern = null;
	}

	/**
	 * この PostLh5Encoder が扱うLZSS辞書のサイズを得る。
	 * 
	 * @return この PostLh5Encoder が扱うLZSS辞書のサイズ
	 */
	@Override
	public int getDictionarySize() {
		return dictionarySize;
	}

	/**
	 * この PostLh5Encoder が扱うLZSSの最長一致長を得る。
	 * 
	 * @return この PostLh5Encoder が扱うLZSSの最大一致長
	 */
	@Override
	public int getMaxMatch() {
		return maxMatch;
	}

	/**
	 * この PostLh5Encoder が扱うLZSSの圧縮、非圧縮の閾値を得る。
	 * 
	 * @return この PostLh5Encoder が扱うLZSSの圧縮、非圧縮の閾値
	 */
	@Override
	public int getThreshold() {
		return threshold;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * バッファリングされた全てのデータを this.out に出力する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOut() throws IOException {
		if (1 < block.length) {
			writeOutBestPattern();
		} else {
			writeOutGroup(new int[] { 0 });
			currentBlock = 0;
		}
		position = 0;
		flagBit = 0;
	}

	/**
	 * バッファリングされた全てのデータを最良の構成で this.out に出力する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOutBestPattern() throws IOException {
		int[] bestPattern = null;
		final int[] groupHuffLen = new int[group.length];

		// ------------------------------------------------------------------
		// group を出力したときの bit 数を求める。
		for (int i = 0; i < group.length; i++) {
			if (group != null) {
				int blockSize = 0;
				for (int j = 0; j < group[i].length; j++) {
					blockSize += this.blockSize[group[i][j]];
				}
				if (0 < blockSize && blockSize < 65536) {
					groupHuffLen[i] = calcHuffmanCodeLength(dictionarySize, margeArrays(group[i], blockCodeFreq), margeArrays(group[i], blockOffLenFreq));
				} else if (0 == blockSize) {
					groupHuffLen[i] = 0;
				} else {
					groupHuffLen[i] = -1;
				}
			} else {
				groupHuffLen[i] = -1;
			}
		}

		// ------------------------------------------------------------------
		// 出力 bit 数が最小となる pattern を総当りで求める。
		int smallest = Integer.MAX_VALUE;
		for (final int[] element : pattern) {
			int length = 0;

			for (int j = 0; j < element.length; j++) {
				if (0 <= groupHuffLen[element[j]]) {
					length += groupHuffLen[element[j]];
				} else {
					length = Integer.MAX_VALUE;
					break;
				}
			}
			if (length < smallest) {
				bestPattern = element;
				smallest = length;
			}
		}

		// ------------------------------------------------------------------
		// 最も出力 bit 数の少ないパターンで出力
		// どの パターン もブロックサイズが 65536 以上の
		// グループを持つ場合はブロック単位で出力。
		if (bestPattern != null) {
			for (final int element : bestPattern) {
				writeOutGroup(group[element]);             // throws IOException
			}
		} else {
			for (int i = 0; i < block.length; i++) {
				writeOutGroup(new int[] { i });
			}
		}

		currentBlock = 0;
	}

	/**
	 * group で指定された ブロック群をハフマン符号化して this.out に出力する。<br>
	 * 
	 * @param group 出力するブロック番号を持つ配列
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOutGroup(final int[] group) throws IOException {
		int[] codeFreq = margeArrays(group, blockCodeFreq);
		int[] offLenFreq = margeArrays(group, blockOffLenFreq);

		int blockSize = 0;
		for (final int element : group) {
			blockSize += this.blockSize[element];
		}

		if (0 < blockSize) {
			// ------------------------------------------------------------------
			// ブロックサイズ出力
			out.writeBits(16, blockSize);

			// ------------------------------------------------------------------
			// ハフマン符号表生成
			final int[] codeLen = StaticHuffman.FreqListToLenList(codeFreq);
			final int[] codeCode = StaticHuffman.LenListToCodeList(codeLen);
			final int[] offLenLen = StaticHuffman.FreqListToLenList(offLenFreq);
			final int[] offLenCode = StaticHuffman.LenListToCodeList(offLenLen);

			// ------------------------------------------------------------------
			// code 部のハフマン符号表出力
			if (2 <= countNoZeroElement(codeFreq)) {
				final int[] codeLenFreq = createCodeLenFreq(codeLen);
				final int[] codeLenLen = StaticHuffman.FreqListToLenList(codeLenFreq);
				final int[] codeLenCode = StaticHuffman.LenListToCodeList(codeLenLen);

				if (2 <= countNoZeroElement(codeLenFreq)) {
					writeCodeLenLen(codeLenLen);                         // throws IOException
				} else {
					out.writeBits(5, 0);                                 // throws IOException
					out.writeBits(5, getNoZeroElementIndex(codeLenFreq));// throws IOException
				}
				writeCodeLen(codeLen, codeLenLen, codeLenCode);          // throws IOException
			} else {
				out.writeBits(10, 0);                                    // throws IOException
				out.writeBits(18, getNoZeroElementIndex(codeFreq));// throws IOException
			}

			// ------------------------------------------------------------------
			// offLen 部のハフマン符号表出力
			if (2 <= countNoZeroElement(offLenFreq)) {
				writeOffLenLen(offLenLen);                               // throws IOException
			} else {
				final int len = Bits.len(Bits.len(dictionarySize));
				out.writeBits(len, 0);                                   // throws IOException
				out.writeBits(len, getNoZeroElementIndex(offLenFreq));// throws IOException
			}

			// ------------------------------------------------------------------
			// ハフマン符号出力
			for (final int element : group) {
				position = 0;
				flagBit = 0;
				final byte[] buffer = block[element];

				for (int j = 0; j < this.blockSize[element]; j++) {
					if (flagBit == 0) {
						flagBit = 0x80;
						flagPos = position++;
					}
					if (0 == (buffer[flagPos] & flagBit)) {
						final int code = buffer[position++] & 0xFF;
						out.writeBits(codeLen[code], codeCode[code]);    // throws IOException
					} else {
						final int code = buffer[position++] & 0xFF | 0x100;
						int offset = 0;
						for (int k = 0; k < dictionarySizeByteLen; k++) {
							offset = offset << 8 | buffer[position++] & 0xFF;
						}
						final int offlen = Bits.len(offset);
						out.writeBits(codeLen[code], codeCode[code]);   // throws IOException
						out.writeBits(offLenLen[offlen], offLenCode[offlen]); // throws IOException
						if (1 < offlen) {
							out.writeBits(offlen - 1, offset);  // throws IOException
						}
					}
					flagBit >>= 1;
				}
			}

			// ------------------------------------------------------------------
			// 次のブロックのための処理
			for (int i = 0; i < group.length; i++) {
				this.blockSize[group[i]] = 0;

				codeFreq = blockCodeFreq[group[i]];
				for (int j = 0; j < codeFreq.length; j++) {
					codeFreq[j] = 0;
				}

				offLenFreq = blockOffLenFreq[group[i]];
				for (int j = 0; j < offLenFreq.length; j++) {
					offLenFreq[j] = 0;
				}
			}
		}// if( 0 < blockSize )
	}

	/**
	 * codeLen の ハフマン符号長のリストを書き出す。
	 * 
	 * @param codeLenLen codeLenFreq のハフマン符号長のリスト
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeCodeLenLen(final int[] codeLenLen) throws IOException {
		int end = codeLenLen.length;
		while (0 < end && codeLenLen[end - 1] == 0) {
			end--;
		}

		out.writeBits(5, end);                                           // throws IOException
		int index = 0;
		while (index < end) {
			final int len = codeLenLen[index++];
			if (len <= 6) {
				out.writeBits(3, len);                        // throws IOException
			} else {
				out.writeBits(len - 3, (1 << len - 3) - 2);// throws IOException
			}

			if (index == 3) {
				while (codeLenLen[index] == 0 && index < 6) {
					index++;
				}
				out.writeBits(2, index - 3 & 0x03);                  // throws IOException
			}
		}
	}

	/**
	 * code 部のハフマン符号長のリストを ハフマンとランレングスで符号化しながら書き出す。
	 * 
	 * @param codeLen codeFreq のハフマン符号長のリスト
	 * @param codeLenLen codeLenFreq のハフマン符号長のリスト
	 * @param codeLenCode codeLenFreq のハフマン符号のリスト
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeCodeLen(final int[] codeLen, final int[] codeLenLen, final int[] codeLenCode) throws IOException {
		int end = codeLen.length;
		while (0 < end && codeLen[end - 1] == 0) {
			end--;
		}

		out.writeBits(9, end);                                           // throws IOException
		int index = 0;
		while (index < end) {
			final int len = codeLen[index++];

			if (0 < len) {
				out.writeBits(codeLenLen[len + 2], codeLenCode[len + 2]);// throws IOException
			} else {
				int count = 1;
				while (codeLen[index] == 0 && index < end) {
					count++;
					index++;
				}

				if (count <= 2) {
					for (int i = 0; i < count; i++) {
						out.writeBits(codeLenLen[0], codeLenCode[0]);      // throws IOException
					}
				} else if (count <= 18) {
					out.writeBits(codeLenLen[1], codeLenCode[1]);        // throws IOException
					out.writeBits(4, count - 3);                         // throws IOException
				} else if (count == 19) {
					out.writeBits(codeLenLen[0], codeLenCode[0]);        // throws IOException
					out.writeBits(codeLenLen[1], codeLenCode[1]);        // throws IOException
					out.writeBits(4, 0x0F);                              // throws IOException
				} else {
					out.writeBits(codeLenLen[2], codeLenCode[2]);        // throws IOException
					out.writeBits(9, count - 20);                        // throws IOException
				}
			}
		}
	}

	/**
	 * offLen のハフマン符号長のリストを書き出す
	 * 
	 * @param offLenLen offLenFreq のハフマン符号長のリスト
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void writeOffLenLen(final int[] offLenLen) throws IOException {
		int end = offLenLen.length;
		while (0 < end && offLenLen[end - 1] == 0) {
			end--;
		}

		int len = Bits.len(Bits.len(dictionarySize));
		out.writeBits(len, end);                                         // throws IOException
		int index = 0;
		while (index < end) {
			len = offLenLen[index++];
			if (len <= 6) {
				out.writeBits(3, len);                         // throws IOException
			} else {
				out.writeBits(len - 3, (1 << len - 3) - 2);// throws IOException
			}
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
	 * arrays の中から、indexes で指定された配列を連結する。
	 * 
	 * @param indexes arrays内の走査対象の配列を示す添え字の表
	 * @param arrays 走査対象の配列を含んだリスト
	 */
	private static int[] margeArrays(final int[] indexes, final int[][] arrays) {
		if (1 < indexes.length) {
			final int[] array = new int[arrays[0].length];

			for (final int indexe : indexes) {
				final int[] src = arrays[indexe];

				for (int j = 0; j < src.length; j++) {
					array[j] += src[j];
				}
			}
			return array;
		}
		return arrays[indexes[0]];
	}

	/**
	 * codeLen をランレングスとハフマンで符号化するための頻度表を作成する。 作成する頻度表は codeLenFreq[0]には要素数0の要素が1つあって読み飛ばす事を指示する codeLenFreq[1]には要素数0の要素が3～18あって、続く5bitのデータをみて その長さのデータを読み飛ばす事を指示する codeLenFreq[2]には要素数0の要素が20以上あって、続く9bitのデータをみて その長さのデータを読み飛ばす事を指示する という特殊な意味を持つ要素も含まれる。 従来の頻度は +2された位置にそれぞれ配置される。
	 * 
	 * @param codeLen codeFreq のハフマン符号長のリスト
	 * @return codeLen の頻度表
	 */
	private static int[] createCodeLenFreq(final int[] codeLen) {
		final int[] codeLenFreq = new int[StaticHuffman.LIMIT_LEN + 3];

		int end = codeLen.length;
		while (0 < end && codeLen[end - 1] == 0) {
			end--;
		}

		int index = 0;
		while (index < end) {
			final int len = codeLen[index++];

			if (0 < len) {
				codeLenFreq[len + 2]++;
			} else {
				int count = 1;
				while (codeLen[index] == 0 && index < end) {
					count++;
					index++;
				}

				if (count <= 2) {
					codeLenFreq[0] += count;
				} else if (count <= 18) {
					codeLenFreq[1]++;
				} else if (count == 19) {
					codeLenFreq[0]++;
					codeLenFreq[1]++;
				} else {
					codeLenFreq[2]++;
				}
			}
		}
		return codeLenFreq;
	}

	/**
	 * 指定された頻度情報でハフマン符号を 出力した場合のビット数を得る。
	 * 
	 * @param DictionarySize LZSS辞書サイズ
	 * @param codeFreq コード部の頻度情報
	 * @param offLenFreq オフセット部の長さの頻度情報
	 * @return この頻度情報でハフマン符号を出力した場合のビット数を得る。
	 */
	private static int calcHuffmanCodeLength(final int DictionarySize, final int[] codeFreq, final int[] offLenFreq) {
		// ------------------------------------------------------------------
		// 初期化
		int length = 0;
		int[] codeLen, offLenLen;
		try {
			codeLen = StaticHuffman.FreqListToLenList(codeFreq);
			StaticHuffman.LenListToCodeList(codeLen);
			offLenLen = StaticHuffman.FreqListToLenList(offLenFreq);
		} catch (final BadHuffmanTableException exception) { // 発生しない
			throw new Error("caught the BadHuffmanTableException which should be never thrown.");
		}

		// ------------------------------------------------------------------
		// code 部のハフマン頻度表の長さを算出する。
		length += 16;
		if (2 <= countNoZeroElement(codeFreq)) {
			final int[] codeLenFreq = createCodeLenFreq(codeLen);
			final int[] codeLenLen = StaticHuffman.FreqListToLenList(codeLenFreq);
			if (2 <= countNoZeroElement(codeLenFreq)) {
				length += calcCodeLenLen(codeLenLen);
			} else {
				length += 5;
				length += 5;
			}
			length += calcCodeLen(codeLen, codeLenLen);
		} else {
			length += 10;
			length += 18;
		}

		// ------------------------------------------------------------------
		// offLen 部のハフマン頻度表の長さを算出する。
		if (2 <= countNoZeroElement(offLenFreq)) {
			length += calcOffLenLen(DictionarySize, offLenLen);
		} else {
			final int len = Bits.len(Bits.len(DictionarySize));
			length += len;
			length += len;
		}

		// ------------------------------------------------------------------
		// LZSS圧縮後のデータをさらにハフマン符号化した長さを算出する。
		for (int i = 0; i < codeFreq.length; i++) {
			length += codeFreq[i] * codeLen[i];
		}
		for (int i = 0; i < offLenFreq.length; i++) {
			length += offLenFreq[i] * (offLenLen[i] + i - 1);
		}
		return length;
	}

	/**
	 * 指定したハフマン符号長の表を出力した場合のビット数を得る。
	 * 
	 * @param codeLenLen コード部のハフマン符号長を さらにハフマン符号化したものの表
	 * @return 指定したハフマン符号長の表を出力した場合のビット数
	 */
	private static int calcCodeLenLen(final int[] codeLenLen) {
		int length = 0;
		int end = codeLenLen.length;
		while (0 < end && codeLenLen[end - 1] == 0) {
			end--;
		}

		length += 5;

		int index = 0;
		while (index < end) {
			final int len = codeLenLen[index++];
			if (len <= 6) {
				length += len;
			} else {
				length += len - 3;
			}

			if (index == 3) {
				while (codeLenLen[index] == 0 && index < 6) {
					index++;
				}
				length += 2;
			}
		}
		return length;
	}

	/**
	 * 指定したハフマン符号長の表を出力した場合のビット数を得る。
	 * 
	 * @param codeLen コード部のハフマン符号長の表
	 * @param codeLenLen コード部のハフマン符号長を さらにハフマン符号化したものの表
	 * @return 指定したハフマン符号長の表を出力した場合のビット数
	 */
	private static int calcCodeLen(final int[] codeLen, final int[] codeLenLen) {
		int length = 0;
		int end = codeLen.length;
		while (0 < end && codeLen[end - 1] == 0) {
			end--;
		}

		length += 9;

		int index = 0;
		while (index < end) {
			final int len = codeLen[index++];

			if (0 < len) {
				length += codeLenLen[len + 2];
			} else {
				int count = 1;
				while (codeLen[index] == 0 && index < end) {
					count++;
					index++;
				}

				if (count <= 2) {
					for (int i = 0; i < count; i++) {
						length += codeLenLen[0];
					}
				} else if (count <= 18) {
					length += codeLenLen[1];
					length += 4;
				} else if (count == 19) {
					length += codeLenLen[0];
					length += codeLenLen[1];
					length += 4;
				} else {
					length += codeLenLen[2];
					length += 9;
				}
			}
		}
		return length;
	}

	/**
	 * 指定したハフマン符号長の表を出力した場合のビット数を得る。
	 * 
	 * @param DictionarySize LZSS辞書サイズ
	 * @param offLenLen オフセット部の長さのハフマン符号長の表
	 * @return 指定したハフマン符号長の表を出力した場合のビット数
	 */
	private static int calcOffLenLen(final int DictionarySize, final int[] offLenLen) {
		int length = 0;
		int end = offLenLen.length;
		while (0 < end && offLenLen[end - 1] == 0) {
			end--;
		}

		length += Bits.len(Bits.len(DictionarySize));

		int index = 0;
		while (index < end) {
			final int len = offLenLen[index++];
			if (len <= 6) {
				length += 3;
			} else {
				length += len - 3;
			}
		}
		return length;
	}

	/**
	 * BlockNumのブロックを連続したブロックに グループ化したもののリストを返す。
	 * 
	 * <pre>
	 * group = new int[] { 0, 1, 2 }
	 * </pre>
	 * 
	 * のような場合 block[0] と block[1] と block[2] から成るグループであることを示す。 またグループは group[0] は全ブロックから成るグループ、 group[1] と group[2] はそれぞれ全ブロックから 最後のブロックと最初のブロックを欠いたもの、 というように ピラミッド状に規則を持って生成され、 createPattern はこの規則性を利用するため このメソッドを改変する場合は注意すること。 また、使用しない group には null が入っているので注意すること。
	 * 
	 * @param BlockNum ブロックの個数
	 * @param DivideNum 最大分割数
	 * @reutrn 生成されたグループのリスト
	 */
	private static int[][] createGroup(final int BlockNum, final int DivideNum) {
		final int[][] group = new int[(BlockNum + 1) * BlockNum / 2][];

		if (DivideNum == 0) {
			// ------------------------------------------------------------------
			// 全ブロックを持つグループのみ生成
			group[0] = new int[BlockNum];
			for (int i = 0; i < BlockNum; i++) {
				group[0][i] = i;
			}
		} else if (2 < BlockNum && DivideNum == 1) {
			// ------------------------------------------------------------------
			// 同サイズのグループのうち最初のものと最後のものだけ生成。
			int index = 0;
			for (int size = BlockNum; 0 < size; size--) {
				group[index] = new int[size];
				for (int i = 0; i < size; i++) {
					group[index][i] = i;
				}
				if (size < BlockNum) {
					index += BlockNum - size;
					group[index] = new int[size];
					for (int i = 0; i < size; i++) {
						group[index][i] = i + BlockNum - size;
					}
				}
				index++;
			}
		} else {
			// ------------------------------------------------------------------
			// 全グループを生成。
			int index = 0;
			for (int size = BlockNum; 0 < size; size--) {
				for (int start = 0; size + start <= BlockNum; start++) {
					group[index] = new int[size];

					for (int i = 0; i < size; i++) {
						group[index][i] = start + i;
					}
					index++;
				}
			}
		}
		return group;
	}

	/**
	 * BlockNumのブロックを最大 DivideNum + 1個の領域に 分割したときの パターンの表を生成する。 1つのパターンは createGroup で生成される グループ配列への添字の列挙で示される。
	 * 
	 * <pre>
	 * pattern = new int[] { 1, 3 };
	 * </pre>
	 * 
	 * のような パターンは group[1] と group[3] の間で 分割されたことを示す。
	 * 
	 * @param BlockNum ブロックの個数
	 * @param DivideNum 最大分割数
	 * @return 生成されたパターンのリスト
	 */
	private static int[][] createPattern(final int BlockNum, final int DivideNum) {
		int index = 0;
		final int patternNum = calcPatternNum(BlockNum, DivideNum);
		final int[][] pattern = new int[patternNum][];

		for (int div = 0; div < Math.min(BlockNum, DivideNum + 1); div++) {
			// 分割位置を保持する配列。
			// 配列内の値は、例えば 0の場合は Block[0] と Block[1] の間で分割することを意味する。
			final int[] divPos = new int[div];
			for (int i = 0; i < divPos.length; i++) {
				divPos[i] = i;
			}

			// 同じ 分割数のパターンを生成するループ
			// more は この分割数で、まだパターンが生成できる事を示す。
			boolean more;
			do {
				pattern[index] = new int[div + 1];

				int start = 0;
				for (int i = 0; i < divPos.length; i++) {
					final int len = divPos[i] - start + 1;
					final int num = BlockNum - len;
					pattern[index][i] = (num + 1) * num / 2 + start;
					start += len;
				}
				final int num = BlockNum - (BlockNum - start);
				pattern[index][divPos.length] = (num + 1) * num / 2 + start;
				index++;

				// 分割位置を移動する。分割位置を移動できれば、
				// この分割数でまだ出力できるパターンがあると判断できる。
				more = false;
				int move = divPos.length - 1;
				int range = BlockNum - 2;
				while (0 <= move && !more) {
					if (divPos[move] < range) {
						divPos[move]++;
						if (move < divPos.length - 1) {
							for (int i = move; i < divPos.length - 1; i++) {
								divPos[i + 1] = divPos[i] + 1;
							}
						}
						more = true;
					}
					range = divPos[move] - 1;
					move--;
				}
			} while (more);
		}
		return pattern;
	}

	/**
	 * BlockNum 個のブロックを 最大 DivideNum + 1 個に連続した領域に分割した場合 何パターンできるかを得る。
	 * 
	 * @param BlockNum ブロックの個数
	 * @param DivideNum 分割数
	 * @return パターン数。
	 */
	private static int calcPatternNum(final int BlockNum, final int DivideNum) {
		int patternNum = 0;
		for (int div = 0; div <= DivideNum; div++) {
			final int count = div <= BlockNum / 2 ? div : BlockNum - 1 - div;

			int numerator = 1;
			for (int i = 1; i <= count; i++) {
				numerator *= BlockNum - i;
			}

			int denominator = 1;
			for (int i = 1; i <= count; i++) {
				denominator *= i;
			}

			patternNum += numerator / denominator;
		}
		return patternNum;
	}

}