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

/**
 * 二分木を使用した LzssSearchMethod の実装。<br>
 * 
 * <pre>
 * データ圧縮ハンドブック[改定第二版]
 *        M.ネルソン/J.-L.ゲィリー 著
 *                萩原剛志・山口英 訳
 *                  ISBN4-8101-8605-9
 *                             5728円(税抜き,当方の購入当時の価格)
 * </pre>
 * 
 * を参考にした。<br>
 * 二分木では、最長一致を見つけることはできるが、 最も近い一致を見つけられるとは限らないため、 LZSSで 一致位置が近い場所に偏る事を 利用するような -lh5- のような圧縮法では、 圧縮率はいくらか低下する。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
class BinaryTreeSearch implements LzssSearchMethod {

	/**
	 * 使用されていない事を示す値。 parent[node] に UNUSED がある場合は node は未使用のnodeである。 small[node], large[node] に UNUSED がある場合は 二分木のそちら側には節が無い事を示す。
	 */
	private static final int UNUSED = -1;

	/**
	 * 二分木の根を示す値。 parent[node] に ROOT_NODE がある場合は node は二分木の根である。
	 */
	private static final int ROOT_NODE = -2;

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
	 * LZSS圧縮を施すためのバッファ。 position を境に 前半は辞書領域、 後半は圧縮を施すためのデータの入ったバッファ。 LzssSearchMethodの実装内では読み込みのみ許される。
	 */
	private byte[] textBuffer;

	/**
	 * 辞書の限界位置。 TextBuffer前半の辞書領域にデータが無い場合に 辞書領域にある不定のデータ(Javaでは0)を使用 して圧縮が行われるのを抑止する。
	 */
	private int dictionaryLimit;

	/**
	 * 二分木の根のデータパタンの開始位置を示す。
	 */
	private int root;

	/**
	 * 親のデータパタンの開始位置を示す。
	 */
	private int[] parent;

	/**
	 * 小さい子のデータパタンの開始位置を示す。
	 */
	private int[] small;

	/**
	 * 大きい子のデータパタンの開始位置を示す。
	 */
	private int[] large;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * 二分木を使用した LzssSearchMethod を構築する。<br>
	 * 
	 * @param DictionarySize 辞書サイズ
	 * @param MaxMatch 最長一致長
	 * @param Threshold 圧縮、非圧縮の閾値
	 * @param TextBuffer LZSS圧縮を施すためのバッファ
	 */
	public BinaryTreeSearch(final int DictionarySize, final int MaxMatch, final int Threshold, final byte[] TextBuffer) {
		this.dictionarySize = DictionarySize;
		this.maxMatch = MaxMatch;
		this.threshold = Threshold;
		this.textBuffer = TextBuffer;
		dictionaryLimit = this.dictionarySize;

		root = UNUSED;
		parent = new int[this.dictionarySize];
		large = new int[this.dictionarySize];
		small = new int[this.dictionarySize];

		for (int i = 0; i < parent.length; i++) {
			parent[i] = UNUSED;
		}
	}

	// ------------------------------------------------------------------
	// methods of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod

	/**
	 * position から始まるデータパタンを二分木に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置
	 */
	@Override
	public void put(final int position) {

		// ------------------------------------------------------------------
		// 二分木から最も古いデータパタンを削除
		deleteNode(position - dictionarySize);

		// ------------------------------------------------------------------
		// 二分木から position を挿入する位置を検索
		int parentpos = root;
		int scanpos = root;

		final byte[] buf = textBuffer;
		final int max = position + maxMatch;
		int p = 0;
		int s = 0;
		while (scanpos != UNUSED) {

			s = scanpos;
			p = position;
			while (buf[s] == buf[p]) {
				s++;
				p++;
				if (max <= p) {
					// 完全一致を発見
					replaceNode(scanpos, position);
					return;
				}
			}

			parentpos = scanpos;
			scanpos = buf[s] < buf[p] ? large[scanpos & dictionarySize - 1]
					: small[scanpos & dictionarySize - 1];
		}

		// ------------------------------------------------------------------
		// position から始まるデータパタンを 二分木に登録
		if (root != UNUSED) {
			addNode(parentpos, position, p - position);
		} else {
			root = position;
			final int node = position & dictionarySize - 1;
			parent[node] = ROOT_NODE;
			small[node] = UNUSED;
			large[node] = UNUSED;
		}
	}

	/**
	 * 二分木に登録されたデータパタンから position から始まるデータパタンと 最長の一致を持つものを検索し、 同時に position から始まるデータパタンを 二分木に登録する。<br>
	 * 
	 * @param position TextBuffer内のデータパタンの開始位置。
	 * @return 一致が見つかった場合は LzssOutputStream.createSearchReturn によって生成された一致位置と一致長の情報を持つ値、 一致が見つからなかった場合は LzssOutputStream.NOMATCH。
	 * @see LzssOutputStream#createSearchReturn(int,int)
	 * @see LzssOutputStream#NOMATCH
	 */
	@Override
	public int searchAndPut(final int position) {

		// ------------------------------------------------------------------
		// 二分木から最も古いデータパタンを削除
		deleteNode(position - dictionarySize);

		// ------------------------------------------------------------------
		// 二分木から最長一致を検索
		int matchlen = -1;
		int matchpos = root;
		int parentpos = root;
		int scanpos = root;

		final byte[] buf = textBuffer;
		final int max = position + maxMatch;
		int p = 0;
		int s = 0;
		int len = 0;
		while (scanpos != UNUSED) {

			s = scanpos;
			p = position;
			while (buf[s] == buf[p]) {
				s++;
				p++;
				if (max <= p) {
					// 完全一致を発見
					replaceNode(scanpos, position);
					return LzssOutputStream.createSearchReturn(maxMatch,
							scanpos);
				}
			}

			len = p - position;
			if (matchlen < len) {
				matchpos = scanpos;
				matchlen = len;
			} else if (matchlen == len && matchpos < scanpos) {
				matchpos = scanpos;
			}

			parentpos = scanpos;
			scanpos = buf[s] < buf[p] ? large[scanpos & dictionarySize - 1]
					: small[scanpos & dictionarySize - 1];
		}

		// ------------------------------------------------------------------
		// position から始まるデータパタンを 二分木に登録
		if (root != UNUSED) {
			addNode(parentpos, position, len);
		} else {
			root = position;
			final int node = position & dictionarySize - 1;
			parent[node] = ROOT_NODE;
			small[node] = UNUSED;
			large[node] = UNUSED;
		}

		// ------------------------------------------------------------------
		// メソッドの先頭で削除された
		// 最も遠いデータパタンと比較
		scanpos = position - dictionarySize;
		if (dictionaryLimit <= scanpos) {
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
	 * 二分木に登録されたデータパタンを検索し position から始まるデータパタンと 最長の一致を持つものを得る。<br>
	 * TextBuffer.length &lt position + MaxMatch となるような position では、 二分木を完全に走査できないため 最長一致を得られるとは限らない。<br>
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
		// 二分木に登録されていないデータパタンを
		// 単純な逐次検索で検索する。
		int matchlen = threshold - 1;
		int matchpos = position;
		int scanpos = position - 1;
		int scanlimit = Math.max(dictionaryLimit, lastPutPos);

		final byte[] buf = textBuffer;
		final int max = Math.min(textBuffer.length, position + maxMatch);
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
		// 二分木を探索
		scanpos = root;
		scanlimit = Math.max(dictionaryLimit, position - dictionarySize);
		while (scanpos != UNUSED) {
			s = scanpos;
			p = position;
			while (buf[s] == buf[p]) {
				s++;
				p++;
				if (max <= p) {
					break;
				}
			}

			if (p < max) {
				len = p - position;
				if (scanlimit <= scanpos) {
					if (matchlen < len) {
						matchpos = scanpos;
						matchlen = len;
					} else if (matchlen == len && matchpos < scanpos) {
						matchpos = scanpos;
					}
				}
				scanpos = buf[s] < buf[p] ? large[scanpos & dictionarySize - 1]
						: small[scanpos & dictionarySize - 1];
			} else {
				break;
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
	 * TextBuffer内のpositionまでのデータを前方へ移動する際、 それに応じて 二分木を構成するデータも TextBuffer内のデータと矛盾しないように前方へ移動する処理を行う。
	 * 
	 * @param slideWidth ずらす幅
	 * @param slideEnd ずらすデータの終端 + 1(データ転送先)
	 */
	@Override
	public void slide() {
		dictionaryLimit = Math.max(0, dictionaryLimit - dictionarySize);
		root -= dictionarySize;
		slideTree(parent);
		slideTree(small);
		slideTree(large);
	}

	/**
	 * put() または searchAndPut() を使用して データパタンを二分木に登録する際に 必要とするデータ量を得る。<br>
	 * 二分木は登録の際にデータパタンを構成する 全て(MaxMatchバイト)のデータを必要とする。
	 * 
	 * @return コンストラクタで与えた MaxMatch
	 */
	@Override
	public int putRequires() {
		return maxMatch;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * parentpos のデータパタンの子として position から始まるデータパタンを二分木に登録する。<br>
	 * parentpos と position のデータパタンは len バイト一致する。 position の位置のノードはあらかじめ deleteNode 等で UNUSED の状態にしておくこと。
	 * 
	 * @param parentpos 親のデータパタンのTextBuffer内の開始位置
	 * @param position 新規追加するデータパタンのTextBuffer内の開始位置
	 * @param len 親のデータパタンと新規追加するデータパタンの一致長
	 */
	private void addNode(final int parentpos, final int position, final int len) {
		final int parentnode = parentpos & dictionarySize - 1;
		final int node = position & dictionarySize - 1;

		if (textBuffer[parentpos + len] < textBuffer[position + len]) {
			large[parentnode] = position;
		} else {
			small[parentnode] = position;
		}
		parent[node] = parentpos;
		small[node] = UNUSED;
		large[node] = UNUSED;
	}

	/**
	 * position から始まるデータパタンを二分木から削除する。<br>
	 * 
	 * @param position 削除するデータパタンの開始位置
	 */
	private void deleteNode(final int position) {
		final int node = position & dictionarySize - 1;

		if (parent[node] != UNUSED) {
			if (small[node] == UNUSED
					&& large[node] == UNUSED) {
				contractNode(position, UNUSED);
			} else if (small[node] == UNUSED) {
				contractNode(position, large[node]);
			} else if (large[node] == UNUSED) {
				contractNode(position, small[node]);
			} else {
				final int replace = findNext(position);
				deleteNode(replace);
				replaceNode(position, replace);
			}
		}
	}

	/**
	 * 子に newpos しか持たない oldpos を, newpos で置き換える。 oldpos は二分木から削除される。
	 * 
	 * @param oldpos 削除するデータパタンの開始位置
	 * @param newpos oldposに置き換わるデータパタンの開始位置
	 */
	private void contractNode(final int oldpos, final int newpos) {
		final int oldnode = oldpos & dictionarySize - 1;
		final int newnode = newpos & dictionarySize - 1;
		final int parentpos = parent[oldnode];
		final int parentnode = parentpos & dictionarySize - 1;

		if (parentpos != ROOT_NODE) {
			if (oldpos == small[parentnode]) {
				small[parentnode] = newpos;
			} else {
				large[parentnode] = newpos;
			}
		} else {
			root = newpos;
		}

		if (newpos != UNUSED) {
			parent[newnode] = parentpos;
		}

		parent[oldnode] = UNUSED;
	}

	/**
	 * oldpos を二分木に含まれない新しいデータパタン newpos で置き換える。 newpos が二分木に含まれているような場合には、 いったんdeleteNode(newpos) するなどして、 二分木から外す必要がある。 oldpos は二分木から削除される。
	 * 
	 * @param oldpos 削除するデータパタンの開始位置
	 * @param newpos oldposに置き換わるデータパタンの開始位置
	 */
	private void replaceNode(final int oldpos, final int newpos) {
		final int oldnode = oldpos & dictionarySize - 1;
		final int newnode = newpos & dictionarySize - 1;
		final int parentpos = parent[oldnode];
		final int parentnode = parentpos & dictionarySize - 1;

		if (parentpos != ROOT_NODE) {
			if (oldpos == small[parentnode]) {
				small[parentnode] = newpos;
			} else {
				large[parentnode] = newpos;
			}
		} else {
			root = newpos;
		}

		parent[newnode] = parentpos;
		small[newnode] = small[oldnode];
		large[newnode] = large[oldnode];
		if (small[newnode] != UNUSED) {
			parent[small[newnode] & dictionarySize - 1] = newpos;
		}
		if (large[newnode] != UNUSED) {
			parent[large[newnode] & dictionarySize - 1] = newpos;
		}

		parent[oldnode] = UNUSED;
		large[oldnode] = UNUSED;
		small[oldnode] = UNUSED;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * deleteNode( position ) したときに、 small と large の両方の葉が見つかった場合、 position から始まるデータパタンと 置き換えるべきデータパタンの開始位置を探し出す。
	 * 
	 * @param position 置き換えられるデータパタンの開始位置
	 * @return position から始まるデータパタンと 置き換えるべきデータパタンの開始位置
	 */
	private int findNext(int position) {
		int node = position & dictionarySize - 1;

		position = small[node];
		node = position & dictionarySize - 1;
		while (UNUSED != large[node]) {
			position = large[node];
			node = position & dictionarySize - 1;
		}

		return position;
	}

	/**
	 * slide() 時に、二分木の各要素を移動する。
	 * 
	 * @param array 二分木を構成する配列
	 */
	private void slideTree(final int[] array) {
		for (int i = 0; i < array.length; i++) {
			array[i] = 0 <= array[i] ? array[i] - dictionarySize : array[i];
		}
	}

}