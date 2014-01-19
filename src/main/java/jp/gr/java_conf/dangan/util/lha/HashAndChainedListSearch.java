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

package jp.gr.java_conf.dangan.util.lha;

import java.lang.reflect.InvocationTargetException;

import jp.gr.java_conf.dangan.lang.reflect.Factory;

/**
 * ハッシュと単方向連結リストを使って高速化された LzssSearchMethod。<br>
 * 検索を打ち切ることによる高速化も行っているため、 必ず最長一致を見つけることが出来るとは限らない。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: HashAndChainedListSearch.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [change]
 *     LzssSearchMethod のインタフェイス変更にあわせてインタフェイス変更
 * [improvement]
 *     ar940528 の TEST5相当 の実装に変更。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class HashAndChainedListSearch implements LzssSearchMethod {

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
	 * ハッシュ関数
	 */
	private HashMethod hashMethod;

	/**
	 * ハッシュテーブル 添字はハッシュ値、内容はTextBuffer内の位置
	 */
	private int[] hashTable;

	/**
	 * 同じハッシュ値を持つデータパタンの連結リストの長さが 一定以上になった場合にセットするフラグ。
	 * 
	 * boolean[] にすると何故か遅くなるので char[] として 16個纏めて扱う。 扱う場合は ユーティリティメソッド isTooBig(), setTooBigFlag(), clearTooBigFlag() を介して扱う。
	 */
	private char[] tooBigFlag;

	/**
	 * 同じハッシュ値を持つデータパタン開始位置を持つ 単方向連結リスト。
	 */
	private int[] prev;

	/**
	 * 探索試行回数の上限値を持つ。 この回数以上の探索は行わない。
	 */
	private int searchLimitCount;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * ハッシュと連結リストを使用した LzssSearchMethod を構築する。<br>
	 * ハッシュ関数と探索試行回数の上限値にはデフォルトのものが使用される。<br>
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最長一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 */
	public HashAndChainedListSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer) {
		this(dictionarySize, maxMatch, threshold, textBuffer, HashDefault.class.getName(), 256);
	}

	/**
	 * ハッシュと連結リストを使用した LzssSearchMethod を構築する。<br>
	 * ハッシュ関数にはデフォルトのものが使用される。<br>
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最長一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 * @param searchLimitCount 探索試行回数の上限
	 * 
	 * @exception IllegalArgumentException SearchLimitCount が0以下の場合
	 */
	public HashAndChainedListSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer, final int searchLimitCount) {
		this(dictionarySize, maxMatch, threshold, textBuffer, HashDefault.class.getName(), searchLimitCount);
	}

	/**
	 * ハッシュと連結リストを使用した LzssSearchMethod を構築する。<br>
	 * 探索試行回数の上限値にはデフォルトのものが使用される。<br>
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最長一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 * @param hashMethodClassName Hash関数を提供するクラス名
	 * 
	 * @exception NoClassDefFoundError HashMethodClassName で与えられたクラスが 見つからない場合。
	 * @exception InstantiationError HashMethodClassName で与えられたクラスが abstract class であるためインスタンスを生成できない場合。
	 * @exception NoSuchMethodError HashMethodClassName で与えられたクラスが コンストラクタ HashMethod( byte[] )を持たない場合
	 */
	public HashAndChainedListSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer, final String hashMethodClassName) {
		this(dictionarySize, maxMatch, threshold, textBuffer,hashMethodClassName, 256);
	}

	/**
	 * ハッシュと連結リストを使用した LzssSearchMethod を構築する。<br>
	 * 
	 * @param dictionarySize 辞書サイズ
	 * @param maxMatch 最長一致長
	 * @param threshold 圧縮、非圧縮の閾値
	 * @param textBuffer LZSS圧縮を施すためのバッファ
	 * @param HashMethodClassName Hash関数を提供するクラス名
	 * @param SearchLimitCount 探索試行回数の上限
	 * 
	 * @exception IllegalArgumentException SearchLimitCount が0以下の場合
	 * @exception NoClassDefFoundError HashMethodClassName で与えられたクラスが 見つからない場合。
	 * @exception InstantiationError HashMethodClassName で与えられたクラスが abstract class であるためインスタンスを生成できない場合。
	 * @exception NoSuchMethodError HashMethodClassName で与えられたクラスが コンストラクタ HashMethod( byte[] )を持たない場合
	 */
	public HashAndChainedListSearch(final int dictionarySize, final int maxMatch, final int threshold, final byte[] textBuffer, final String HashMethodClassName, final int SearchLimitCount) {
		if (0 < SearchLimitCount) {

			this.dictionarySize = dictionarySize;
			this.maxMatch = maxMatch;
			this.threshold = threshold;
			this.textBuffer = textBuffer;
			dictionaryLimit = this.dictionarySize;
			this.searchLimitCount = SearchLimitCount;

			try {
				hashMethod = (HashMethod) Factory.createInstance(HashMethodClassName, new Object[] { textBuffer });
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
			hashTable = new int[hashMethod.tableSize()];
			for (int i = 0; i < hashTable.length; i++) {
				hashTable[i] = -1;
			}

			// 連結リスト初期化
			prev = new int[this.dictionarySize];
			for (int i = 0; i < prev.length; i++) {
				prev[i] = -1;
			}

			tooBigFlag = new char[hashMethod.tableSize() >> 4];
		} else {
			throw new IllegalArgumentException("SearchLimitCount must be 1 or more.");
		}
	}

	// ------------------------------------------------------------------
	// method jp.gr.java_conf.dangan.util.lha.LzssSearchMethod

	/**
	 * position から始まるデータパタンを ハッシュと連結リストから成る検索機構に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置
	 */
	@Override
	public void put(final int position) {
		final int hash = hashMethod.hash(position);
		prev[position & dictionarySize - 1] = hashTable[hash];
		hashTable[hash] = position;
	}

	/**
	 * ハッシュと連結リストから成る検索機構に登録された データパタンから position から始まるデータパタンと 最長の一致を持つものを検索し、 同時に position から始まるデータパタンを ハッシュと連結リストから成る検索機構に登録する。<br>
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
		int maxmatch = maxMatch;
		final int scanlimit = Math.max(dictionaryLimit, position - dictionarySize);

		// ------------------------------------------------------------------
		// 連結リストの長さが長すぎる場合 offset を使用して
		// 連結リストの短いハッシュ値を使う。
		final int poshash = hashMethod.hash(position);
		int offhash = poshash;
		int offset = 0;
		while (isTooBig(offhash) && offset < maxMatch - hashMethod.hashRequires()) {
			offset++;
			offhash = hashMethod.hash(position + offset);
		}

		// ------------------------------------------------------------------
		// メインループ
		// 最大 offhash と poshash から始まる 2つの連結リストを走査する。
		final byte[] buf = textBuffer;
		int max = position + maxMatch;
		int s = 0;
		int p = 0;
		int len = 0;
		while (true) {
			int scanpos = hashTable[offhash];
			int searchCount = searchLimitCount;

			while (scanlimit <= scanpos - offset && 0 < --searchCount) {
				if (buf[scanpos + matchlen - offset] == buf[position + matchlen]) {
					s = scanpos - offset;
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
						matchpos = scanpos - offset;
						matchlen = len;
						if (max <= p) {
							break;
						}
					}
				}
				scanpos = prev[scanpos & dictionarySize - 1];
			}

			if (searchCount <= 0) {
				setTooBigFlag(offhash);
			} else if (scanpos < scanlimit) {
				clearTooBigFlag(offhash);
			}

			if (0 < offset && matchlen < hashMethod.hashRequires() + offset) {
				offset = 0;
				maxmatch = hashMethod.hashRequires() + offset - 1;
				max = position + maxmatch;
				offhash = poshash;
			} else {
				break;
			}
		}

		// ------------------------------------------------------------------
		// ハッシュと連結リストを使用した検索機構に
		// position から始まるデータパタンを登録する。
		prev[position & dictionarySize - 1] = hashTable[poshash];
		hashTable[poshash] = position;

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
		// ハッシュと連結リストを使用した検索機構 から検索する。
		if (hashMethod.hashRequires() < textBuffer.length - position) {
			int maxmatch = maxMatch;
			scanlimit = Math.max(dictionaryLimit, position - dictionarySize);

			// 連結リストの長さが長すぎる場合 offset を使用して
			// 連結リストの短いハッシュ値を使う。
			final int poshash = hashMethod.hash(position);
			int offhash = poshash;
			int offset = 0;
			while (isTooBig(offhash)
					&& offset < maxMatch - hashMethod.hashRequires()) {
				offset++;
				offhash = hashMethod.hash(position + offset);
			}

			// メインループ
			// 最大 offhash と poshash から始まる 2つの連結リストを走査する。
			while (true) {
				int searchCount = searchLimitCount;
				scanpos = hashTable[offhash];

				while (scanlimit <= scanpos - offset && 0 < --searchCount) {
					if (buf[scanpos + matchlen - offset] == buf[position
							+ matchlen]) {
						s = scanpos - offset;
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
							matchpos = scanpos - offset;
							matchlen = len;
							if (max <= p) {
								break;
							}
						}
					}
					scanpos = prev[scanpos & dictionarySize - 1];
				}

				if (searchCount <= 0) {
					setTooBigFlag(offhash);
				} else if (scanpos < scanlimit) {
					clearTooBigFlag(offhash);
				}

				if (0 < offset && matchlen < hashMethod.hashRequires() + offset) {
					offset = 0;
					maxmatch = hashMethod.hashRequires() + offset - 1;
					max = Math.min(textBuffer.length, position + maxmatch);
					offhash = poshash;
				} else {
					break;
				}
			}
		}

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
		dictionaryLimit = Math.max(0, dictionaryLimit - dictionarySize);

		for (int i = 0; i < hashTable.length; i++) {
			final int pos = hashTable[i] - dictionarySize;
			hashTable[i] = 0 <= pos ? pos : -1;
		}

		for (int i = 0; i < prev.length; i++) {
			final int pos = prev[i] - dictionarySize;
			prev[i] = 0 <= pos ? pos : -1;
			;
		}
	}

	/**
	 * put() で LzssSearchMethodにデータを 登録するときに使用されるデータ量を得る。 HashAndChainedListSearch では、 内部で使用している HashMethod の実装が hash() のために必要とするデータ量 ( HashMethod.hashRequires() の戻り値 ) を返す。
	 * 
	 * @return 内部で使用している HashMethod の実装が hash() のために必要とするデータ量
	 */
	@Override
	public int putRequires() {
		return hashMethod.hashRequires();
	}

	// ------------------------------------------------------------------
	// method of ImprovedLzssSearchMethod

	/**
	 * より良い LZSS 圧縮のための選択肢を提供する searchAndPut()。 例えば一致長 3, 一致位置 4 と 一致長 4, 一致位置 1024 では 一致長 3, 一致位置 4 + 非圧縮1文字 の方が出力ビット数が 少なくなる事がある。そのような場合に対処するため一致長毎に positionに一番近い一致位置を列挙する。
	 * 
	 * @param position 検索対象のデータパタンの開始位置
	 * @param matchposs 一致位置の列挙を格納して返すための配列<br>
	 * matchpos[0] には 一致長が Threshold の一致位置が、<br>
	 * matchpos[1] には 一致長が Threshold + 1 の一致位置が格納される。<br>
	 * 一致が見つからなかった場合には LzssOutputStream.NOMATCH が格納される。
	 * 
	 * @return 一致が見つかった場合は LzssOutputStream.createSearchReturn で生成された SearchReturn が返される。<br>
	 * 一致が見つからない場合は LzssOutputStream.NOMATCH が返される。<br>
	 */
	public int searchAndPut(final int position, final int[] matchposs) {
		int matchlen = threshold - 1;
		int matchpos = position;
		final int maxmatch = maxMatch;
		final int scanlimit = Math.max(dictionaryLimit, position
				- dictionarySize);
		int searchCount = searchLimitCount;

		for (int i = 0; i < matchposs.length; i++) {
			matchposs[i] = LzssOutputStream.NOMATCH;
		}

		int scanpos = hashTable[hashMethod.hash(position)];

		while (scanlimit < scanpos && 0 < searchCount--) {
			if (textBuffer[scanpos + matchlen] == textBuffer[position
					+ matchlen]) {
				int len = 0;
				while (textBuffer[scanpos + len] == textBuffer[position + len]) {
					if (maxmatch <= ++len) {
						break;
					}
				}

				if (matchlen < len) {
					int i = matchlen + 1 - threshold;
					final int end = Math.min(len + 1 - threshold,
							matchposs.length);
					while (i < end) {
						matchposs[i++] = scanpos;
					}

					matchpos = scanpos;
					matchlen = len;
					if (maxmatch <= len) {
						break;
					}
				}
			}
			scanpos = prev[scanpos & dictionarySize - 1];
		}

		put(position);

		if (matchpos < position) {
			return LzssOutputStream.createSearchReturn(matchlen, matchpos);
		}
		return LzssOutputStream.NOMATCH;
	}

	// ------------------------------------------------------------------
	// local methods

	/**
	 * hash の連結リストが閾値を超えているかを得る。
	 * 
	 * @param hash ハッシュ値
	 * @return 連結リストの長さが閾値を超えているなら true 超えていなければ false
	 */
	private boolean isTooBig(final int hash) {
		return 0 != (tooBigFlag[hash >> 4] & 1 << (hash & 0x0F));
	}

	/**
	 * hash の連結リストが閾値を超えた事を示す フラグをセットする。
	 * 
	 * @param hash too big フラグをセットするハッシュ値
	 */
	private void setTooBigFlag(final int hash) {
		tooBigFlag[hash >> 4] |= 1 << (hash & 0x0F);
	}

	/**
	 * hash の連結リストが閾値を超えている事を示す フラグをクリアする。
	 * 
	 * @param hash too big フラグをクリアするハッシュ値
	 */
	private void clearTooBigFlag(final int hash) {
		tooBigFlag[hash >> 4] &= ~(1 << (hash & 0x0F));
	}

}