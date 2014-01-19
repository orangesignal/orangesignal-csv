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

import java.lang.reflect.InvocationTargetException;

/**
 * 二段階ハッシュと単方向連結リストを使って高速化された LzssSearchMethod。<br>
 * <a href="http://search.ieice.org/2000/pdf/e83-a_12_2689.pdf">定兼氏の論文</a> を参考にした。
 * 
 * <pre>
 * -- revision history --
 * $Log: TwoLevelHashSearch.java,v $
 * Revision 1.1  2002/12/10 22:06:40  dangan
 * [bug fix]
 *     searchAndPut() で最近の最長一致を取れなかったバグを修正。
 * 
 * Revision 1.0  2002/12/03 00:00:00  dangan
 * first edition
 * add to version control
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class TwoLevelHashSearch implements LzssSearchMethod {

	/**
	 * LZSS辞書サイズ。
	 */
	private int dictionarySize;

	/**
	 * LZSS圧縮に使用される値。 最大一致長を示す。
	 */
	private int maxMatch;

	/**
	 * LZSS圧縮に使用される閾値。 一致長が この値以上であれば、圧縮コードを出力する。
	 */
	private int threshold;

	/**
	 * LZSS圧縮を施すためのバッファ。 前半は辞書領域、 後半は圧縮を施すためのデータの入ったバッファ。 LzssSearchMethodの実装内では読み込みのみ許される。
	 */
	private byte[] textBuffer;

	/**
	 * 辞書の限界位置。 TextBuffer前半の辞書領域にデータが無い場合に 辞書領域にある不定のデータ(Javaでは0)を使用 して圧縮が行われるのを抑止する。
	 */
	private int dictionaryLimit;

	/**
	 * 一段目のハッシュ関数
	 */
	private HashMethod primaryHash;

	/**
	 * 一段目のハッシュテーブル 添字は一段目のハッシュ値、内容は 二段目のハッシュテーブルの index
	 */
	private int[] primaryHashTable;

	/**
	 * 一段目のハッシュテーブルに幾つのデータパタンが 登録されているかをカウントしておく。
	 */
	private int[] primaryCount;

	/**
	 * 二段目のハッシュ値を算出するために必要なバイト数。
	 */
	private int[] secondaryHashRequires;

	/**
	 * 二段目のハッシュテーブル 添字は 一段目のハッシュテーブルの値 + 二段目のハッシュ値、 内容は TextBuffer 内のデータパタンの開始位置
	 */
	private int[] secondaryHashTable;

	/**
	 * slide() の毎に secondaryHashTable と入れ替えるダミー配列。 使いまわし用。
	 */
	private int[] dummy;

	/**
	 * 同じハッシュ値を持つデータパタン開始位置を持つ 単方向連結リスト。
	 */
	private int[] prev;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * 二段階ハッシュを使用した LzssSearchMethod を構築する。<br>
	 * 一段目のハッシュ関数には デフォルトのものが使用される。<br>
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最大一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 */
	public TwoLevelHashSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer) {
		this(dictionarySize, maxMatch, threshold, textBuffer, HashShort.class.getName());
	}

	/**
	 * 二段階ハッシュを使用した LzssSearchMethod を構築する。
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最大一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 * @param hashMethodClassName Hash関数を提供するクラス名
	 * @exception NoClassDefFoundError HashMethodClassName で与えられたクラスが見つからない場合。
	 * @exception InstantiationError HashMethodClassName で与えられたクラスが abstract class であるためインスタンスを生成できない場合。
	 * @exception NoSuchMethodError HashMethodClassName で与えられたクラスが コンストラクタ HashMethod( byte[] )を持たない場合。
	 */
	public TwoLevelHashSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer, final String hashMethodClassName) {
		this.dictionarySize = dictionarySize;
		this.maxMatch = maxMatch;
		this.threshold = threshold;
		this.textBuffer = textBuffer;
		dictionaryLimit = this.dictionarySize;

		try {
			primaryHash = (HashMethod) Factory.createInstance(hashMethodClassName, new Object[]{ textBuffer });
		} catch (final ClassNotFoundException exception) {
			throw new NoClassDefFoundError(exception.getMessage());
		} catch (final InvocationTargetException exception) {
			throw new Error(exception.getTargetException().getMessage());
		} catch (final NoSuchMethodException exception) {
			throw new NoSuchMethodError(exception.getMessage());
		} catch (final InstantiationException exception) {
			throw new InstantiationError(exception.getMessage());
		}

		// ハッシュテーブル初期化
		primaryHashTable = new int[primaryHash.tableSize()];
		secondaryHashTable = new int[primaryHash.tableSize() + this.dictionarySize / 4];
		for (int i = 0; i < primaryHashTable.length; i++) {
			primaryHashTable[i] = i;
			secondaryHashTable[i] = -1;
		}

		// その他の配列生成
		// primaryCount と secondaryHashRequires は配列生成時にゼロクリアされている事を利用する。
		primaryCount = new int[primaryHash.tableSize()];
		secondaryHashRequires = new int[primaryHash.tableSize()];
		dummy = new int[secondaryHashTable.length];

		// 連結リスト初期化
		prev = new int[this.dictionarySize];
		for (int i = 0; i < prev.length; i++) {
			prev[i] = -1;
		}
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod

	/**
	 * position から始まるデータパタンを 二段階ハッシュと連結リストから成る検索機構に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置
	 */
	@Override
	public void put(final int position) {
		final int phash = primaryHash.hash(position);
		final int base = primaryHashTable[phash];
		final int shash = secondaryHash(position, secondaryHashRequires[phash]);
		primaryCount[phash]++;
		prev[position & dictionarySize - 1] = secondaryHashTable[base + shash];
		secondaryHashTable[base + shash] = position;
	}

	/**
	 * 二段階ハッシュと連結リストから成る検索機構に登録された データパタンから position から始まるデータパタンと 最長の一致を持つものを検索し、 同時に position から始まるデータパタンを 二段階ハッシュと連結リストから成る検索機構に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置。
	 * @return 一致が見つかった場合は LzssOutputStream.createSearchReturn によって生成された一致位置と一致長の情報を持つ値、 一致が見つからなかった場合は LzssOutputStream.NOMATCH。
	 * @see LzssOutputStream#createSearchReturn(int,int)
	 * @see LzssOutputStream#NOMATCH
	 */
	@Override
	public int searchAndPut(final int position) {
		int matchlen = threshold - 1;
		int matchpos = position;
		int scanlimit = Math.max(dictionaryLimit, position - dictionarySize);

		final int phash = primaryHash.hash(position);
		final int base = primaryHashTable[phash];
		final int requires = secondaryHashRequires[phash];
		final int shash = secondaryHash(position, requires);
		int scanpos = secondaryHashTable[base + shash];

		final byte[] buf = textBuffer;
		int max = position + maxMatch;
		int s = 0;
		int p = 0;
		int len = 0;

		// ------------------------------------------------------------------
		// 二段目のハッシュによって選ばれた連結リストを検索するループ
		while (scanlimit <= scanpos) {
			if (buf[scanpos + matchlen] == buf[position + matchlen]) {
				s = scanpos;
				p = position;
				while (buf[s] == buf[p]) {
					s++;
					p++;
					if (max <= p) {
						break;
					}
				}

				len = p - position;
				if (matchlen < len) {
					matchpos = scanpos;
					matchlen = len;
					if (max <= p) {
						break;
					}
				}
			}
			scanpos = prev[scanpos & dictionarySize - 1];
		}

		// ------------------------------------------------------------------
		// 二段目のハッシュによって厳選された連結リストに一致が無い場合、
		// 一段目のハッシュに登録されている全ての連結リストを検索する
		int revbits = 1;
		final int loopend = requires
				- Math.max(0, threshold - primaryHash.hashRequires());
		int maxmatch = primaryHash.hashRequires() + requires - 1;
		for (int i = 1, send = 4; i <= loopend && matchlen <= maxmatch; i++, send <<= 2) {
			max += position + maxmatch;
			while (revbits < send) {
				scanpos = secondaryHashTable[base + (shash ^ revbits)];
				while (scanlimit <= scanpos) {
					if (buf[scanpos] == buf[position]) {
						s = scanpos + 1;
						p = position + 1;
						while (buf[s] == buf[p]) {
							s++;
							p++;
							if (max <= p) {
								break;
							}
						}

						len = p - position;
						if (matchlen < len || matchlen == len
								&& matchpos < scanpos) {
							matchpos = scanpos;
							matchlen = len;
							if (max <= p) {
								scanlimit = scanpos;
								break;
							}
						}
					}
					scanpos = prev[scanpos & dictionarySize - 1];
				}
				revbits++;
			}
			maxmatch = primaryHash.hashRequires() + requires - i - 1;
		}

		// ------------------------------------------------------------------
		// 二段階ハッシュと連結リストを使用した検索機構に
		// position から始まるデータパタンを登録する。
		primaryCount[phash]++;
		prev[position & dictionarySize - 1] = secondaryHashTable[base + shash];
		secondaryHashTable[base + shash] = position;

		// ------------------------------------------------------------------
		// 最長一致を呼び出し元に返す。
		if (threshold <= matchlen) {
			return LzssOutputStream.createSearchReturn(matchlen, matchpos);
		}
		return LzssOutputStream.NOMATCH;
	}

	/**
	 * ハッシュと連結リストを使用した検索機構に登録された データパタンを検索し position から始まるデータパタンと 最長の一致を持つものを得る。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置。
	 * @param lastPutPos 最後に登録したデータパタンの開始位置。
	 * @return 一致が見つかった場合は LzssOutputStream.createSearchReturn によって生成された一致位置と一致長の情報を持つ値、 一致が見つからなかった場合は LzssOutputStream.NOMATCH。
	 * @see LzssOutputStream#createSearchReturn(int,int)
	 * @see LzssOutputStream#NOMATCH
	 */
	@Override
	public int search(final int position, final int lastPutPos) {
		// ------------------------------------------------------------------
		// ハッシュと連結リストによる検索機構に登録されていない
		// データパタンを単純な逐次検索で検索する。
		int matchlen = threshold - 1;
		int matchpos = position;
		int scanpos = position - 1;
		int scanlimit = Math.max(dictionaryLimit, lastPutPos);

		final byte[] buf = textBuffer;
		int max = Math.min(textBuffer.length, position + maxMatch);
		int s = 0;
		int p = 0;
		int len = 0;
		while (scanlimit < scanpos) {
			s = scanpos;
			p = position;
			while (buf[s] == buf[p]) {
				s++;
				p++;
				if (max <= p) {
					break;
				}
			}

			if (matchlen < len) {
				matchpos = scanpos;
				matchlen = len;
			}
			scanpos--;
		}

		// ------------------------------------------------------------------
		// 二段階ハッシュと連結リストを使用した検索機構から検索する。
		final int phashRequires = primaryHash.hashRequires();
		if (phashRequires < textBuffer.length - position) {

			final int phash = primaryHash.hash(position);
			final int base = primaryHashTable[phash];
			final int requires = secondaryHashRequires[phash];
			int shash;
			int start;
			if (phashRequires + requires < textBuffer.length - position) {
				shash = secondaryHash(position, requires);
				start = 0;
			} else {
				final int avail = textBuffer.length - position - phashRequires;
				shash = secondaryHash(position, avail) << (requires - avail) * 2;
				start = requires - avail;
			}
			int revbits = 0;
			int maxmatch = maxMatch;

			// ------------------------------------------------------------------
			// 一段目のに登録されている連結リストを優先度の順に検索するループ
			for (int i = start, send = 1 << i * 2; i <= requires; i++, send <<= 2) {
				max += position + maxmatch;
				while (revbits < send) {
					scanpos = secondaryHashTable[base + (shash ^ revbits)];
					while (scanlimit <= scanpos) {
						if (buf[scanpos] == buf[position]) {
							s = scanpos + 1;
							p = position + 1;
							while (buf[s] == buf[p]) {
								s++;
								p++;
								if (max <= p) {
									break;
								}
							}

							len = p - position;
							if (matchlen < len || matchlen == len
									&& matchpos < scanpos) {
								matchpos = scanpos;
								matchlen = len;
								if (max <= p) {
									scanlimit = scanpos;
									break;
								}
							}
						}
						scanpos = prev[scanpos & dictionarySize - 1];
					}
					revbits++;
				}
				maxmatch = primaryHash.hashRequires() + requires - i - 1;
			}
		}// if( phashRequires < this.TextBuffer.length - position )

		// ------------------------------------------------------------------
		// 最長一致を呼び出し元に返す。
		if (threshold <= matchlen) {
			return LzssOutputStream.createSearchReturn(matchlen, matchpos);
		}
		return LzssOutputStream.NOMATCH;
	}

	/**
	 * TextBuffer内のpositionまでのデータを 前方へ移動する際、それに応じて SearchMethod内の データも TextBuffer内のデータと矛盾しないように 前方へ移動する処理を行う。
	 */
	@Override
	public void slide() {
		// ------------------------------------------------------------------
		// DictionaryLimit更新
		dictionaryLimit = Math.max(0, dictionaryLimit - dictionarySize);

		// ------------------------------------------------------------------
		// primaryCount の値によって secondaryHashTable を再構成する
		int secondaryIndex = 0;
		int dummyIndex = 0;
		for (int i = 0; i < primaryHashTable.length; i++) {
			primaryHashTable[i] = dummyIndex;
			final int bits = secondaryHashRequires[i] * 2;

			if (1 << 5 + bits <= primaryCount[i]) {
				for (int j = 0; j < 1 << bits; j++) {
					divide(dummyIndex, secondaryIndex,
							primaryHash.hashRequires()
									+ secondaryHashRequires[i]);
					dummyIndex += 4;
					secondaryIndex += 1;
				}
				secondaryHashRequires[i]++;

			} else if (0 < bits && primaryCount[i] < 1 << 2 + bits) {
				for (int j = 0; j < 1 << bits - 2; j++) {
					merge(dummyIndex, secondaryIndex);
					dummyIndex += 1;
					secondaryIndex += 4;
				}
				secondaryHashRequires[i]--;

			} else {
				for (int j = 0; j < 1 << bits; j++) {
					final int pos = secondaryHashTable[secondaryIndex++]
							- dictionarySize;
					dummy[dummyIndex++] = 0 <= pos ? pos : -1;
				}
			}
			primaryCount[i] = 0;
		}
		final int[] temp = secondaryHashTable;
		secondaryHashTable = dummy;
		dummy = temp;

		// ------------------------------------------------------------------
		// 連結リストを更新
		for (int i = 0; i < prev.length; i++) {
			final int pos = prev[i] - dictionarySize;
			prev[i] = 0 <= pos ? pos : -1;
		}
	}

	/**
	 * put() で LzssSearchMethodにデータを 登録するときに使用されるデータ量を得る。 TwoLevelHashSearch では、内部で使用している HashMethod の実装が hash() のために必要とするデータ量( HashMethod.hashRequires() の戻り値 ) と 二段目のハッシュに必要な最大のバイト数を足したものを返す。
	 * 
	 * @return 一段目と二段目のハッシュに必要なバイト数を足したもの。
	 */
	@Override
	public int putRequires() {
		return primaryHash.hashRequires() + Math.max(Bits.len(dictionarySize) - 5, 0) / 2;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * 二段目のハッシュ関数
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置
	 * @param hashRequires 二段目のハッシュ値を算出するのに必要なバイト数
	 */
	private int secondaryHash(final int position, int hashRequires) {
		int hash = 0;
		int pos = position + primaryHash.hashRequires();

		while (0 < hashRequires--) {
			hash <<= 2;
			hash |= textBuffer[pos++] & 0x03;
		}

		return hash;
	}

	/**
	 * 二段目のハッシュテーブルと連結リストを分岐させる。
	 * 
	 * @param dbase 分岐先 this.dummy の index
	 * @param sbase 分岐元 this.secondaryHashTable の index
	 * @param divoff 分岐位置
	 */
	private void divide(final int dbase, final int sbase, final int divoff) {
		final int limit = dictionarySize;
		int position = secondaryHashTable[sbase];
		final int[] current = { -1, -1, -1, -1 };

		// ------------------------------------------------------------------
		// 連結リストを分岐させていくループ
		while (limit < position) {
			final int shash = textBuffer[position + divoff] & 0x03;
			if (0 < current[shash]) {
				prev[current[shash] & dictionarySize - 1] = position;
			} else {
				dummy[dbase + shash] = position - dictionarySize;
			}
			current[shash] = position;
			position = prev[position & dictionarySize - 1];
		}

		// ------------------------------------------------------------------
		// 連結リストをターミネートする。
		for (int i = 0; i < current.length; i++) {
			if (0 < current[i]) {
				prev[current[i] & dictionarySize - 1] = -1;
			} else {
				dummy[dbase + i] = -1;
			}
		}
	}

	/**
	 * 二段目のハッシュテーブルと連結リストを束ねる。
	 * 
	 * @param dbase 分岐先 this.dummy の index
	 * @param sbase 分岐元 this.secondaryHashTable の index
	 */
	private void merge(final int dbase, final int sbase) {
		final int limit = dictionarySize;
		int position = -1;

		// ------------------------------------------------------------------
		// 連結リストを束ねていくループ
		while (true) {
			int shash = 0;
			int max = secondaryHashTable[sbase];
			for (int i = 1; i < 4; i++) {
				if (max < secondaryHashTable[sbase + i]) {
					shash = i;
					max = secondaryHashTable[sbase + i];
				}
			}

			if (limit < max) {
				secondaryHashTable[sbase + shash] = prev[max & dictionarySize
						- 1];

				if (0 < position) {
					prev[position & dictionarySize - 1] = max;
				} else {
					dummy[dbase] = max - dictionarySize;
				}
				position = max;
			} else {
				break;
			}
		}

		// ------------------------------------------------------------------
		// 連結リストをターミネートする。
		if (0 < position) {
			prev[position & dictionarySize - 1] = -1;
		} else {
			dummy[dbase] = -1;
		}
	}

}