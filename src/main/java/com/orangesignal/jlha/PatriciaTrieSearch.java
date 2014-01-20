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

import java.math.BigInteger;

/**
 * PATRICIA Trie を使用した LzssSearchMethod の実装。
 * 
 * <pre>
 * -- revision history --
 * $Log: PatriciaTrieSearch.java,v $
 * Revision 1.2  2002/12/10 22:28:55  dangan
 * [bug fix]
 *     put( DictionarySize * 2 )
 *     searchAndPut( DictionarySize * 2 ) に対応していなかったのを修正。
 * 
 * Revision 1.1  2002/12/04 00:00:00  dangan
 * [change]
 *     LzssSearchMethod のインタフェイス変更に合わせてインタフェイス変更。
 * [maintenance]
 *     ソース整備
 * 
 * Revision 1.0  2002/08/15 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     contractNode で hashtable からの連結リストに繋ぐのを忘れていた修正。
 *     配列 に PatriciaTrieSearch.ROOT_NODE(-1) でアクセスしていたのを修正。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class PatriciaTrieSearch implements LzssSearchMethod {

	/**
	 * 使用されていない事を示す値。 parent[node] に UNUSED がある場合は node は未使用の node である。 prev[node], next[node] に UNUSED がある場合は、 そちら側に兄弟ノードが無いことを示す。
	 */
	private static final int UNUSED = 0;

	/**
	 * LZSS 辞書サイズ
	 */
	private final int dictionarySize;

	/**
	 * LZSS 圧縮に使用される値。 最大一致長を示す。
	 */
	private final int maxMatch;

	/**
	 * LZSS 圧縮/非圧縮の閾値。 一致長が この値以上であれば、圧縮コードを出力する。
	 */
	private final int threshold;

	/**
	 * LZSS 圧縮を施すためのバッファ。 LzssSearchMethod の実装内では読み込みのみ許される。
	 */
	private final byte[] textBuffer;

	/**
	 * 辞書の限界位置。 TextBuffer前半の辞書領域に未だにデータが無い場合に 辞書領域にある不定のデータ(Java では0)を使用して 圧縮が行われないようにする。
	 */
	private int dictionaryLimit;

	/**
	 * 親のノード番号を示す。 parent[node] は node の親ノードの番号を示す。
	 */
	private final int[] parent;

	/**
	 * 子のハッシュ値を示す。 hashTable[ hash( node, ch ) ] で node の文字 ch の子ノードのハッシュ値を示す。
	 */
	private final int[] hashTable;

	/**
	 * hashTable から連なる双方向連結リストの一部。 同じハッシュ値を持つ 一つ前のノードのノード番号を示す。 prev[ node ] は node と同じハッシュ値を持ち、 連結リスト内で node の一つ前に位置するノードの node 番号。 prev[ node ] が 負値の場合は全ビット反転したハッシュ値を示す。
	 */
	private final int[] prev;

	/**
	 * hashTable から連なる双方向連結リストの一部。 同じハッシュ値を持つ 一つ後のノードのノード番号を示す。 next[ node ] は node と同じハッシュ値を持ち、 連結リスト内で node の一つ後ろに位置するノードの node 番号。
	 * 
	 * また、葉でないノードに関しては next と avail で 未使用なノードの スタック(一方向連結リスト)を構成する。
	 * 
	 * さらに、完全一致があったため削除された葉ノードで、 PATRICIA Trie 内に存在している葉ノードへの一方向連結リストを構成する。
	 */
	private final int[] next;

	/**
	 * ノードの TextBuffer 内のデータパタンの開始位置を示す。 position[ node ] は node のデータパタンの開始位置を示す。
	 */
	private final int[] position;

	/**
	 * ノードの 分岐位置を示す。 level[ node ] は node の子ノードが分岐する位置を示す。
	 */
	private final int[] level;

	/**
	 * ノードの子ノードの数を示す。 childnum[ node ] は node の子ノードの数を示す。
	 */
	private final int[] childnum;

	/**
	 * next が構成する未使用ノードのスタックのスタックポインタ。
	 */
	private int avail;

	/**
	 * ハッシュ時に使用するシフト値
	 */
	private final int shift;

	/**
	 * 最後の searchAndPut() または put() で得られた 得られた PatriciaTrie内の最長一致位置
	 */
	private int lastMatchPos;

	/**
	 * 最後の searchAndPut() または put() で 得られた PatriciaTrie内の最長一致長
	 */
	private int lastMatchLen;

	/**
	 * コンストラクタ。 PATRICIA Trie を使用した検索機構を構築する。
	 * 
	 * @param DictionarySize 辞書サイズ
	 * @param MaxMatch 最長一致長
	 * @param Threshold 圧縮、非圧縮の閾値
	 * @param TextBuffer LZSS圧縮を施すためのバッファ
	 */
	public PatriciaTrieSearch(final int DictionarySize, final int MaxMatch, final int Threshold, final byte[] TextBuffer) {
		dictionarySize = DictionarySize;
		maxMatch = MaxMatch;
		threshold = Threshold;
		textBuffer = TextBuffer;
		dictionaryLimit = dictionarySize;

		parent = new int[dictionarySize * 2];
		prev = new int[dictionarySize * 2];
		next = new int[dictionarySize * 2];
		position = new int[dictionarySize];
		level = new int[dictionarySize];
		childnum = new int[dictionarySize];
		hashTable = new int[PatriciaTrieSearch.generateProbablePrime(dictionarySize + (dictionarySize >> 2))];

		for (int i = 2; i < dictionarySize; i++) {
			next[i] = i - 1;
		}
		avail = dictionarySize - 1;

		for (int i = 0; i < dictionarySize * 2; i++) {
			parent[i] = PatriciaTrieSearch.UNUSED;
		}

		for (int i = 0; i < hashTable.length; i++) {
			hashTable[i] = PatriciaTrieSearch.UNUSED;
		}

		shift = Bits.len(dictionarySize) - 8;

		lastMatchLen = 0;
		lastMatchPos = 0;
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod

	/**
	 * position から始まるデータパタンを PATRICIA Trie に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置
	 */
	@Override
	public void put(final int position) {
		// ------------------------------------------------------------------
		// PATRICIA Trie から最も古いデータパタンを削除
		final int posnode = (position & dictionarySize - 1) + dictionarySize;
		deleteNode(posnode);

		// ------------------------------------------------------------------
		// PATRICIA Trie から最長一致を検索
		int matchnode = -1;
		int matchpos = position;
		int scannode;
		int matchlen;
		if (3 < lastMatchLen) {

			// 前回の一致長が閾値より大きければ、
			// 葉から lastMatchLen - 1 の一致を検索する。
			scannode = lastMatchPos + 1 | dictionarySize;

			// 最長一致があったために scannode が
			// PATRICIA Trie から取り除かれている場合の処理
			while (parent[scannode] == PatriciaTrieSearch.UNUSED) {
				scannode = next[scannode];
			}

			// 葉から 順番に親へと辿って
			// lastMatchLen - 1 以下の level を持つノードを探す。
			int node = parent[scannode];
			lastMatchLen--;
			while (0 < node && lastMatchLen <= level[node]) {
				scannode = node;
				node = parent[node];
			}

			// さらに親へと辿って position を更新していく。
			while (0 < node) {
				this.position[node] = position;
				node = parent[node];
			}

			matchlen = lastMatchLen;
		} else {

			// PATRICIA Trie を 根から辿る。
			scannode = child(textBuffer[position] - 128, textBuffer[position + 1] & 0xFF);
			matchlen = 2;

			if (scannode == PatriciaTrieSearch.UNUSED) {
				// 根に position を追加する。
				attachNode(textBuffer[position] - 128, posnode, textBuffer[position + 1] & 0xFF);
				lastMatchLen = matchlen;
				return;
			}
		}

		while (true) {
			int max;
			if (scannode < dictionarySize) {
				max = level[scannode];
				matchnode = scannode;
				matchpos = this.position[scannode];
			} else {
				max = maxMatch;
				matchnode = scannode;
				matchpos = position <= scannode ? scannode - dictionarySize : scannode;
			}

			while (matchlen < max && textBuffer[matchpos + matchlen] == textBuffer[position + matchlen]) {
				matchlen++;
			}

			if (matchlen == max && matchlen < maxMatch) {
				this.position[scannode] = position;
				scannode = child(scannode, textBuffer[position + matchlen] & 0xFF);

				if (scannode == PatriciaTrieSearch.UNUSED) {
					attachNode(matchnode, posnode, textBuffer[position + matchlen] & 0xFF);
					break;
				}
				matchlen++;
			} else if (matchlen < max) {
				// matchnode と position を分岐させる。
				splitNode(matchnode, matchpos, posnode, position, matchlen);
				break;
			} else {
				// 完全一致を発見、ノードを置き換える。
				replaceNode(matchnode, posnode);
				next[matchnode] = position;
				break;
			}
		}

		// 検索結果を保存
		lastMatchLen = matchlen;
		lastMatchPos = matchpos;
	}

	/**
	 * PATRICIA Trie に登録されたデータパタンから position から始まるデータパタンと 最長の一致を持つものを検索し、 同時に position から始まるデータパタンを PATRICIA Trie に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置。
	 * @return 一致が見つかった場合は LzssOutputStream.createSearchReturn によって生成された一致位置と一致長の情報を持つ値、 一致が見つからなかった場合は LzssOutputStream.NOMATCH。
	 * @see LzssOutputStream#createSearchReturn(int,int)
	 * @see LzssOutputStream#NOMATCH
	 */
	@Override
	public int searchAndPut(final int position) {
		// ------------------------------------------------------------------
		// PATRICIA Trie から最も古いデータパタンを削除
		final int posnode = (position & dictionarySize - 1) + dictionarySize;
		deleteNode(posnode);

		// ------------------------------------------------------------------
		// PATRICIA Trie から最長一致を検索
		int matchnode = -1;
		int matchpos = position;
		int scannode = 0;
		int matchlen = 0;
		if (3 < lastMatchLen) {

			// 前回の一致長が閾値より大きければ、
			// 葉から lastMatchLen - 1 の一致を検索する。
			scannode = lastMatchPos + 1 | dictionarySize;

			// 最長一致があったために scannode が
			// PATRICIA Trie から取り除かれている場合の処理
			while (parent[scannode] == PatriciaTrieSearch.UNUSED) {
				scannode = next[scannode];
			}

			// 葉から 順番に親へと辿って
			// lastMatchLen - 1 以下の level を持つノードを探す。
			int node = parent[scannode];
			lastMatchLen--;
			while (0 < node && lastMatchLen <= level[node]) {
				scannode = node;
				node = parent[node];
			}

			// さらに親へと辿って position を更新していく。
			while (0 < node) {
				this.position[node] = position;
				node = parent[node];
			}

			matchlen = lastMatchLen;
		} else {
			// PATRICIA Trie を 根から辿る。
			scannode = child(textBuffer[position] - 128, textBuffer[position + 1] & 0xFF);
			matchlen = 2;
		}

		// scannode == UNUSED となるのは lastMatchLen が閾値より小さいときのみ。
		if (scannode != PatriciaTrieSearch.UNUSED) {
			while (true) {
				int max;
				if (scannode < dictionarySize) {
					max = level[scannode];
					matchnode = scannode;
					matchpos = this.position[scannode];
				} else {
					max = maxMatch;
					matchnode = scannode;
					matchpos = position <= scannode ? scannode - dictionarySize : scannode;
				}

				while (matchlen < max && textBuffer[matchpos + matchlen] == textBuffer[position + matchlen]) {
					matchlen++;
				}

				if (matchlen == max && matchlen < maxMatch) {
					this.position[scannode] = position;
					scannode = child(scannode, textBuffer[position + matchlen] & 0xFF);

					if (scannode == PatriciaTrieSearch.UNUSED) {
						// matchnode に position を追加する。
						attachNode(matchnode, posnode, textBuffer[position + matchlen] & 0xFF);
						break;
					}
					matchlen++;
				} else if (matchlen < max) {
					// matchnode と position を分岐させる。
					splitNode(matchnode, matchpos, posnode, position, matchlen);
					break;
				} else {
					// 完全一致を発見、ノードを置き換える。
					replaceNode(matchnode, posnode);
					next[matchnode] = position;
					break;
				}
			}
		} else { // if( scannode != PatriciaTrieSearch.UNUSED )
					// 根に position を追加する。
			attachNode(textBuffer[position] - 128, posnode, textBuffer[position + 1] & 0xFF);
			matchlen = 0;
		}

		// 検索結果を保存
		lastMatchLen = matchlen;
		lastMatchPos = matchpos;

		// ------------------------------------------------------------------
		// メソッド先頭で PATRICIA Trie から削除したデータパタンもチェックする。
		scannode = position - dictionarySize;
		if (dictionaryLimit <= scannode) {
			int len = 0;
			while (textBuffer[scannode + len] == textBuffer[position + len]) {
				if (maxMatch <= ++len) {
					break;
				}
			}

			if (matchlen < len) {
				matchpos = scannode;
				matchlen = len;
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
	 * PATRICIA Trie に登録されたデータパタンを検索し position から始まるデータパタンと 最長の一致を持つものを得る。<br>
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
		// PATRICIA Trie に登録されていないデータパタンを
		// 単純な逐次検索で検索する。
		int scanlimit = Math.max(dictionaryLimit, lastPutPos);
		int scanpos = position - 1;
		int matchlen = 0;
		int matchpos = 0;

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

			len = p - position;
			if (matchlen < len) {
				matchpos = scanpos;
				matchlen = len;
				if (max <= p) {
					break;
				}
			}
			scanpos--;
		}

		// ------------------------------------------------------------------
		// PATRICIA Trie を探索
		if (2 < textBuffer.length - position) {
			int matchnode = child(textBuffer[position] - 128, textBuffer[position + 1] & 0xFF);
			scanlimit = Math.max(dictionaryLimit, position - dictionarySize);
			len = 2;
			while (matchnode != PatriciaTrieSearch.UNUSED) {
				int maxlen;
				if (matchnode < dictionarySize) {
					maxlen = level[matchnode];
					scanpos = this.position[matchnode];
				} else {
					maxlen = maxMatch;
					scanpos = lastPutPos < matchnode ? matchnode - dictionarySize : matchnode;
				}

				if (scanlimit <= scanpos) {
					max = Math.min(textBuffer.length, position + maxlen);
					s = scanpos + len;
					p = position + len;
					if (p < max) {
						while (buf[s] == buf[p]) {
							s++;
							p++;
							if (max <= p) {
								break;
							}
						}
					}

					len = p - position;
					if (matchlen < len) {
						matchpos = scanpos;
						matchlen = len;
					}

					if (len == maxlen && matchlen < maxMatch) {
						if (position + len < textBuffer.length) {
							matchnode = child(matchnode, textBuffer[position + len] & 0xFF);

							if (matchnode != PatriciaTrieSearch.UNUSED) {
								len++;
							}
						} else {
							break;
						}
					} else {  // maxlen に満たない一致が見つかったか 完全一致が見つかった
						break;
					}
				} else { // if( scanlimit <= scanpos ) 一致したパタンは検索限界を超えていた。
					break;
				}
			}   // while( matchnode != PatriciaTrieSearch.UNUSED )
		}   // if( 2 <= this.TextBuffer.length - position )

		// ------------------------------------------------------------------
		// 最長一致を呼び出し元に返す。
		if (threshold <= matchlen) {
			return LzssOutputStream.createSearchReturn(matchlen, matchpos);
		}
		return LzssOutputStream.NOMATCH;
	}

	/**
	 * TextBuffer内のpositionまでのデータを 前方へ移動する際、それに応じて LzssSearchMethod 内のデータも TextBuffer内のデータと矛盾しないよ うに前方へ移動する処理を行う。
	 */
	@Override
	public void slide() {
		dictionaryLimit = Math.max(0, dictionaryLimit - dictionarySize);
		lastMatchPos -= dictionarySize;

		for (int i = 0; i < position.length; i++) {
			final int pos = position[i] - dictionarySize;
			if (0 < pos) {
				position[i] = pos;
			} else {
				position[i] = 0;
			}
		}
	}

	/**
	 * put() で LzssSearchMethodにデータを 登録するときに使用されるデータ量を得る。 PatriciaTrieSearch では、常に MaxMatch を返す。
	 * 
	 * @return 常に MaxMatch
	 */
	@Override
	public int putRequires() {
		return maxMatch;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * oldnode を splitLen で分岐させる。 oldnode のあった位置には新しいノードが新設され、 新しいノードは oldnode と position を子に持つ。
	 * 
	 * @param oldnode 分岐させるノード
	 * @param oldpos oldnode が指すデータパタンの開始位置
	 * @param posnode position 用ノード
	 * @param position TextBuffer 内のデータパタンの開始位置
	 * @param splitLen データパタン内の分岐位置
	 */
	private void splitNode(final int oldnode, final int oldpos, final int posnode, final int position, final int splitLen) {
		// スタックから 新しいノードを取得する。
		final int newnode = avail;
		avail = next[newnode];

		replaceNode(oldnode, newnode);
		level[newnode] = splitLen;
		this.position[newnode] = position;
		childnum[newnode] = 0;

		attachNode(newnode, oldnode, textBuffer[oldpos + splitLen] & 0xFF);
		attachNode(newnode, posnode, textBuffer[position + splitLen] & 0xFF);
	}

	/**
	 * PATRICIA Trie から葉である node を削除する。 必要であれば node の親ノードの繰上げ処理も行う。
	 * 
	 * @param node 削除する葉ノード
	 */
	private void deleteNode(final int node) {
		if (parent[node] != PatriciaTrieSearch.UNUSED) {
			final int parent = this.parent[node];
			final int prev = this.prev[node];
			final int next = this.next[node];

			this.parent[node] = PatriciaTrieSearch.UNUSED;
			this.prev[node] = PatriciaTrieSearch.UNUSED;
			this.next[node] = PatriciaTrieSearch.UNUSED;

			if (0 <= prev) {
				this.next[prev] = next;
			} else {
				hashTable[~prev] = next;
			}
			this.prev[next] = prev;

			if (0 < parent) { // parent が PATRICIA Trie の根で無い場合 true となる条件式
				childnum[parent]--;

				if (childnum[parent] <= 1) {
					contractNode(child(parent, textBuffer[position[parent] + level[parent]] & 0xFF));
				}
			}
		}
	}

	/**
	 * parentnode に childnode を追加する。
	 * 
	 * @param parentnode childnode を追加する対象の親ノード
	 * @param childnode parentnode に追加するノード
	 * @param pos TextBuffer内現在処理位置。 葉の position を確定するために使用される。
	 */
	private void attachNode(final int parentnode, final int childnode, final int ch) {
		final int hash = hash(parentnode, ch);
		final int node = hashTable[hash];
		hashTable[hash] = childnode;
		parent[childnode] = parentnode;
		prev[childnode] = ~hash;
		next[childnode] = node;
		prev[node] = childnode;

		if (0 < parentnode) {
			childnum[parentnode]++;
		}
	}

	/**
	 * oldnode と newnode を入れ替える。 newnode は子ノードとの関係を保持する。 oldnode は置き換えられて PATRICIA Trie から取り除かれる。
	 * 
	 * @param oldnode 入れ替えられて Trie から削除されるノード
	 * @param newnode oldnode のあった位置へ接続されるノード
	 */
	private void replaceNode(final int oldnode, final int newnode) {
		parent[newnode] = parent[oldnode];
		prev[newnode] = prev[oldnode];
		next[newnode] = next[oldnode];

		prev[next[newnode]] = newnode;

		if (prev[newnode] < 0) {
			hashTable[~prev[newnode]] = newnode;
		} else {
			next[prev[newnode]] = newnode;
		}

		parent[oldnode] = PatriciaTrieSearch.UNUSED;
		prev[oldnode] = PatriciaTrieSearch.UNUSED;
		next[oldnode] = PatriciaTrieSearch.UNUSED;
	}

	/**
	 * 兄弟の無くなった node を引き上げる。 node の親ノードは PATRICIA Trie から削除され、 代わりに node がその位置に接続される。 兄弟が無いかどうかの 判定は呼び出し側が行う。
	 * 
	 * @param node 引き上げるノード
	 */
	private void contractNode(final int node) {
		final int parentnode = parent[node];

		prev[next[node]] = prev[node];
		if (0 <= prev[node]) {
			next[prev[node]] = next[node];
		} else {
			hashTable[~prev[node]] = next[node];
		}
		replaceNode(parentnode, node);

		// 使用されなくなった parentnode をスタックに返還する。
		next[parentnode] = avail;
		avail = parentnode;
	}

	/**
	 * parent から ch で分岐した子を得る。 ノードが無い場合は UNUSED を返す。
	 * 
	 * @param parent 親ノード
	 * @param ch 分岐文字
	 * 
	 * @return 子ノード
	 */
	private int child(final int parent, final int ch) {
		int node = hashTable[hash(parent, ch)];

		// this.parent[ PatriciaTrieSearch.UNUSED ] = parent;
		while (node != PatriciaTrieSearch.UNUSED && this.parent[node] != parent) {
			node = next[node];
		}

		return node;
	}

	/**
	 * node と ch から ハッシュ値を得る
	 * 
	 * @param node ノード
	 * @param ch 分岐文字
	 * 
	 * @return ハッシュ値
	 */
	private int hash(final int node, final int ch) {
		return (node + (ch << shift) + 256) % hashTable.length;
	}

	/**
	 * num 以上の最も小さい 素数(もしくは擬似素数)を生成する。 戻り値が 素数でない確率は 1/256 以下である。
	 * 
	 * @param num この値以上の素数を生成する。
	 * 
	 * @return 生成された素数(もしくは擬似素数)
	 */
	private static int generateProbablePrime(int num) {
		num = num + ((num & 1) == 0 ? 1 : 0);

		while (!new BigInteger(Integer.toString(num)).isProbablePrime(8)) {
			num += 2;
			num = num + (num % 3 == 0 ? 2 : 0);
			num = num + (num % 5 == 0 ? 2 : 0);
			num = num + (num % 7 == 0 ? 2 : 0);
		}
		return num;
	}

}