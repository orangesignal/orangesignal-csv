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

package jp.gr.java_conf.dangan.util.lha;

/**
 * 動的ハフマンを扱うクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: DynamicHuffman.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     addLeaf() で葉の数が 1 から 2へと増加するときに
 *     最初からあった葉の重さが 1 だと決め付けていた。
 * [change]
 *     コンストラクタ DynamicHuffman( int, int ) で
 *     開始時のハフマン木のサイズでなく 開始時の葉の数を渡すように変更。
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の変更
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
class DynamicHuffman implements Cloneable {

	/**
	 * ハフマン木のルートを示す。
	 */
	public static final int ROOT = 0;

	/**
	 * ハフマン木を再構築する重さ
	 */
	private static final int MAX_WEIGHT = 0x8000;

	/**
	 * 添え字のノードの重さを示す。
	 */
	private int[] weight;

	/**
	 * 添え字のノードの子ノードのノード番号を保持する 兄弟特性を利用するため、 child が 小さいノードのノード番号 child - 1 が 大きいノードのノード番号となる。 葉の場合はデータをbit反転したものが入っている。
	 */
	private int[] child;

	/**
	 * 添え字のノードの親ノードのノード番号を保持する
	 */
	private int[] parent;

	/**
	 * 葉のノード番号を保持する。
	 */
	private int[] leafs;

	/**
	 * 現在のハフマン木の大きさ
	 */
	private int size;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * デフォルトコンストラクタ。 
	 */
	protected DynamicHuffman() {}

	/**
	 * コンストラクタ
	 * 
	 * @param count 葉の数
	 */
	public DynamicHuffman(final int count) {
		this(count, count);
	}

	/**
	 * コンストラクタ
	 * 
	 * @param max 葉の最大数
	 * @param start 開始時の葉の数
	 */
	public DynamicHuffman(final int max, final int first) {
		if (1 <= first && first <= max) {
			weight = new int[max * 2 - 1];
			child = new int[max * 2 - 1];
			parent = new int[max * 2 - 1];
			leafs = new int[max];
			size = Math.max(0, first * 2 - 1);

			// 葉を生成していく。
			int node = size - 1;
			for (int code = 0; code < first; code++, node--) {
				weight[node] = 1;
				child[node] = ~code;
				leafs[code] = node;
			}

			// 枝を生成していく。
			int child = size - 1;
			while (0 <= node && node != child) {
				weight[node] = weight[child] + weight[child - 1];

				this.child[node] = child;
				parent[child] = parent[child - 1] = node;

				child -= 2;
				node--;
			}
		} else if (max < first) {
			throw new IllegalArgumentException(
					"\"max\" must be larger than \"first\".");
		} else {
			throw new IllegalArgumentException("\"first\" must be one or more.");
		}
	}

	// ------------------------------------------------------------------
	// method of java.lang.Object

	/**
	 * このオブジェクトの現在の状態を持つコピーを作成して返す。
	 * 
	 * @return このオブジェクトの現在の状態を持つコピー
	 */
	@Override
	public Object clone() {
		final DynamicHuffman clone = new DynamicHuffman();
		clone.weight = weight.clone();
		clone.child = child.clone();
		clone.parent = parent.clone();
		clone.leafs = leafs.clone();
		clone.size = size;
		return clone;
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * データからノード番号を得る。
	 * 
	 * @param code データ
	 * @return codeのノード番号
	 */
	public int codeToNode(final int code) {
		return leafs[code];
	}

	/**
	 * ノードが葉でないノードなら子ノードのノード番号、 ノードが葉ならノードの持つデータを全ビット反転したものを得る。 子ノードのノード番号は兄弟特性と利用するため、<br>
	 * node の 0 の子ノードの場合 childNode( node )<br>
	 * node の 1 の子ノードの場合 childNode( node ) - 1<br>
	 * となる。
	 * 
	 * @param node ノード
	 * @return node の子ノードのノード番号
	 */
	public int childNode(final int node) {
		return child[node];
	}

	/**
	 * node の親ノードのノード番号を得る。
	 * 
	 * @param node ノード
	 * @return node の親ノードのノード番号。
	 */
	public int parentNode(final int node) {
		return parent[node];
	}

	// ------------------------------------------------------------------
	// original method

	/**
	 * code の重みが増すようにハフマン木を更新する。
	 * 
	 * @param code 重みを増やす葉
	 */
	public void update(final int code) {
		if (weight[DynamicHuffman.ROOT] == DynamicHuffman.MAX_WEIGHT) {
			rebuildTree();
		}

		int node = leafs[code];
		while (DynamicHuffman.ROOT != node) {
			int swapNode = node;
			while (weight[swapNode - 1] == weight[node]
					&& DynamicHuffman.ROOT < swapNode - 1) {
				swapNode--;
			}

			if (node != swapNode) {
				swap(node, swapNode);
			}

			weight[swapNode]++;
			node = parent[swapNode];
		}
		weight[DynamicHuffman.ROOT]++;
	}

	/**
	 * ハフマン木に code を示す葉を追加する。
	 * 
	 * @param code 葉の示す符号
	 * @exception IllegalStateException ハフマン木が十分に大きいため 葉が追加できない場合
	 */
	public void addLeaf(final int code) {
		if (size < weight.length - 1) {
			final int last = size - 1;
			final int large = size;
			final int small = size + 1;
			child[large] = child[last];
			child[small] = ~code;
			child[last] = small;
			weight[large] = weight[last];
			weight[small] = 0;
			leafs[~child[large]] = large;
			leafs[~child[small]] = small;
			parent[large] = parent[small] = last;
			size = small + 1;

			if (last == DynamicHuffman.ROOT) {
				weight[last] -= 1;
			}

			update(code);
		} else {
			throw new IllegalStateException();
		}
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * ハフマン木を再構築する。 重みが privateな定数 MAX_WEIGHT を超えた時に update(int)から呼び出される。 全てのノードの重みを およそ半分にする。
	 */
	private void rebuildTree() {
		int leafCount = 0;
		for (int i = 0; i < size; i++) {
			if (child[i] < 0) {
				weight[leafCount] = (weight[i] + 1) / 2;
				child[leafCount] = child[i];
				leafCount++;
			}
		}

		leafCount--;
		int position = size - 1;
		int leafPosition = size - 2;
		while (0 <= position) {
			while (leafPosition <= position) {
				leafs[~child[leafCount]] = position;
				weight[position] = weight[leafCount];
				child[position--] = child[leafCount--];
			}

			final int weight = this.weight[leafPosition]
					+ this.weight[leafPosition + 1];

			while (0 <= leafCount && this.weight[leafCount] <= weight) {
				leafs[~child[leafCount]] = position;
				this.weight[position] = this.weight[leafCount];
				child[position--] = child[leafCount--];
			}

			this.weight[position] = weight;
			child[position] = leafPosition + 1;
			parent[leafPosition] = parent[leafPosition + 1] = position;

			position--;
			leafPosition -= 2;
		}
	}

	/**
	 * ノード番号iのノードと ノード番号jのノードを入れ換える処理を行う。
	 * 
	 * @param i 入れ換え対象のノード
	 * @param j 入れ換え対象のノード
	 */
	private void swap(final int i, final int j) {
		if (child[i] < 0) {
			leafs[~child[i]] = j;
		} else {
			parent[child[i]] = parent[child[i] - 1] = j;
		}

		if (child[j] < 0) {
			leafs[~child[j]] = i;
		} else {
			parent[child[j]] = parent[child[j] - 1] = i;
		}

		int temp = child[i];
		child[i] = child[j];
		child[j] = temp;

		temp = weight[i];
		weight[i] = weight[j];
		weight[j] = temp;
	}

}