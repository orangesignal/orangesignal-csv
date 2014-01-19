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
 * 静的ハフマン用ユーティリティ関数群を保持する。<br>
 * ハフマン符号は最大16ビットに制限される。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
final class StaticHuffman {

	/**
	 * LHAがDOSの16bitモードを使用して作られたことによる ハフマン符号長の制限。
	 */
	public static final int LIMIT_LEN = 16;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * デフォルトコンストラクタ。 使用不可。
	 */
	private StaticHuffman() {}

	// ------------------------------------------------------------------
	// shared method

	/**
	 * 頻度表から ハフマン符号のビット長の表を作成する。
	 * 
	 * @param FreqList 頻度表
	 * @return ハフマン符号のビット長の表
	 */
	public static int[] FreqListToLenList(final int[] FreqList) {
		/**
		 * ハフマン木を構成する配列群 ハフマン木は 0～FreqList.length までは全てが葉であり、 そのノード番号は符号である。木がいったん完成した後は TreeCount-1がルートノードとなる。 NodeWeight:: そのノードの重みを持つ SmallNode:: 小さな子ノードのノード番号を持つ LargeNode:: 大きな子ノードのノード番号を持つ TreeCount:: 有効なノードの個数を持つ
		 */
		final int[] NodeWeight = new int[FreqList.length * 2 - 1];
		final int[] SmallNode = new int[FreqList.length * 2 - 1];
		final int[] LargeNode = new int[FreqList.length * 2 - 1];
		int TreeCount = FreqList.length;

		/**
		 * ハフマン木の葉のノード番号を小さな順に格納したリスト。 Leafs:: リスト本体 LeafCount:: 葉の個数
		 */
		final int[] Leafs = new int[FreqList.length];
		int LeafCount = 0;

		/**
		 * ハフマン木の葉でないノードのノード番号を 小さな順に格納したリストを格納する。 Nodes:: リスト本体 NodeCount:: 葉でないノードの個数
		 */
		final int[] Nodes = new int[FreqList.length - 1];
		int NodeCount = 0;

		// 木に葉をセットし、
		// Leafsに頻度1以上の葉のみセットする。
		for (int i = 0; i < FreqList.length; i++) {
			NodeWeight[i] = FreqList[i];

			if (0 < FreqList[i]) {
				Leafs[LeafCount++] = i;
			}
		}

		if (2 <= LeafCount) {
			// =================================
			// ハフマン木を作成する
			// =================================

			// ハフマン木の葉となるべき要素を整列させる。
			mergeSort(Leafs, 0, LeafCount - 1, FreqList, new int[LeafCount / 2 + 1]);

			// 葉か、ノードの最小のもの2つを新しいノードに
			// 結びつける事を繰り返し、ルートノードまで作成する。
			// この処理によってハフマン木が完成する。
			int LeafIndex = 0;
			int NodeIndex = 0;
			do {
				int small;
				if (NodeCount <= NodeIndex) {
					small = Leafs[LeafIndex++];
				} else if (LeafCount <= LeafIndex) {
					small = Nodes[NodeIndex++];
				} else if (NodeWeight[Leafs[LeafIndex]] <= NodeWeight[Nodes[NodeIndex]]) {
					small = Leafs[LeafIndex++];
				} else {
					small = Nodes[NodeIndex++];
				}

				int large;
				if (NodeCount <= NodeIndex) {
					large = Leafs[LeafIndex++];
				} else if (LeafCount <= LeafIndex) {
					large = Nodes[NodeIndex++];
				} else if (NodeWeight[Leafs[LeafIndex]] <= NodeWeight[Nodes[NodeIndex]]) {
					large = Leafs[LeafIndex++];
				} else {
					large = Nodes[NodeIndex++];
				}

				final int newNode = TreeCount++;
				NodeWeight[newNode] = NodeWeight[small] + NodeWeight[large];
				SmallNode[newNode] = small;
				LargeNode[newNode] = large;
				Nodes[NodeCount++] = newNode;
			} while (NodeIndex + LeafIndex < NodeCount + LeafCount - 1);

			// ============================================
			// ハフマン木からハフマン符号長の表を作成する。
			// ============================================
			// ハフマン木からハフマン符号長の頻度表を作成する。
			final int[] LenFreq = huffmanTreeToLenFreq(SmallNode, LargeNode, TreeCount - 1);

			// ハフマン符号長の頻度長から符号長の表を作成する。
			final int[] LenList = new int[FreqList.length];
			LeafIndex = 0;
			for (int len = LIMIT_LEN; 0 < len; len--) {
				while (0 < LenFreq[len]--) {
					LenList[Leafs[LeafIndex++]] = len;
				}
			}

			return LenList;
		}
		return new int[FreqList.length];
	}

	/**
	 * 頻度表から ハフマン符号のビット長の表を作成する。 オリジナルのLHAと同じコードを出力する。
	 * 
	 * @param FreqList 頻度表
	 * @return ハフマン符号のビット長の表
	 */
	public static int[] FreqListToLenListOriginal(final int[] FreqList) {
		/**
		 * ハフマン木を構成する配列群 ハフマン木は 0～FreqList.length までは全てが葉であり、 そのノード番号は符号である。木がいったん完成した後は TreeCount-1がルートノードとなる。 NodeWeight:: そのノードの重みを持つ SmallNode:: 小さな子ノードのノード番号を持つ LargeNode:: 大きな子ノードのノード番号を持つ TreeCount:: 有効なノードの個数を持つ
		 */
		final int[] NodeWeight = new int[FreqList.length * 2 - 1];
		final int[] SmallNode = new int[FreqList.length * 2 - 1];
		final int[] LargeNode = new int[FreqList.length * 2 - 1];
		int TreeCount = FreqList.length;

		/**
		 * ハフマン木の葉のノード番号を小さな順に格納したリスト。 Leafs:: リスト本体 LeafCount:: 葉の個数
		 */
		final int[] Leafs = new int[FreqList.length];
		int LeafCount = 0;

		/**
		 * ハフマン木の全てのノードのノード番号を 小さな順に格納したリストを格納する。 ヒープソートを使用するため、Heap[0]は使用しない Heap:: リスト本体 HeapLast:: Heapの最後の要素
		 */
		final int[] Heap = new int[FreqList.length * 2];
		int HeapLast = 0;

		// 木に葉をセットし、
		// Heapに頻度1以上の葉のみセットする。
		for (int i = 0; i < FreqList.length; i++) {
			NodeWeight[i] = FreqList[i];

			if (0 < FreqList[i]) {
				Heap[++HeapLast] = i;
			}
		}

		if (2 <= HeapLast) {
			// =================================
			// ハフマン木を作成する
			// =================================

			// ハフマン木の葉となるべき要素を整列させる。
			for (int i = HeapLast / 2; 1 <= i; i--) {
				downHeap(Heap, HeapLast, NodeWeight, i);
			}

			// 葉か、ノードの最小のもの2つを新しいノードに
			// 結びつける事を繰り返し、ルートノードまで作成する。
			// この処理によってハフマン木が完成する。
			do {
				final int small = Heap[1];
				if (small < FreqList.length) {
					Leafs[LeafCount++] = small;
				}

				Heap[1] = Heap[HeapLast--];
				downHeap(Heap, HeapLast, NodeWeight, 1);
				final int large = Heap[1];
				if (large < FreqList.length) {
					Leafs[LeafCount++] = large;
				}

				final int newNode = TreeCount++;
				NodeWeight[newNode] = NodeWeight[small] + NodeWeight[large];
				SmallNode[newNode] = small;
				LargeNode[newNode] = large;

				Heap[1] = newNode;
				downHeap(Heap, HeapLast, NodeWeight, 1);
			} while (1 < HeapLast);

			// ============================================
			// ハフマン木からハフマン符号長の表を作成する。
			// ============================================

			// ハフマン木からハフマン符号長の頻度表を作成する。
			final int[] LenFreq = huffmanTreeToLenFreq(SmallNode, LargeNode, TreeCount - 1);
			// ハフマン符号長の頻度長から符号長の表を作成する。
			final int[] LenList = new int[FreqList.length];
			int LeafIndex = 0;
			for (int len = LIMIT_LEN; 0 < len; len--) {
				while (0 < LenFreq[len]--) {
					LenList[Leafs[LeafIndex++]] = len;
				}
			}

			return LenList;
		}
		return new int[FreqList.length];
	}

	/**
	 * ハフマン符号長のリストから ハフマン符号表を作成する。
	 * 
	 * @param LenList ハフマン符号長のリスト
	 * @return ハフマン符号表
	 * @exception BadHuffmanTableException LenListが不正なため、 ハフマン符号表が生成出来ない場合
	 */
	public static int[] LenListToCodeList(final int[] LenList) throws BadHuffmanTableException {
		// ハフマン符号長の頻度表
		final int[] LenFreq = new int[LIMIT_LEN + 1];
		// ハフマン符号長に対応した符号
		final int[] CodeStart = new int[LIMIT_LEN + 2];

		// ハフマン符号長の頻度表作成
		for (int i = 0; i < LenList.length; i++) {
			LenFreq[LenList[i]]++;
		}

		if (LenFreq[0] < LenList.length) {

			// CodeStart[1] = 0; //Javaでは必要無いのでコメントアウトしている。
			for (int i = 1; i <= LIMIT_LEN; i++) {
				CodeStart[i + 1] = CodeStart[i] + LenFreq[i] << 1;
			}

			if (CodeStart[LIMIT_LEN + 1] != 0x20000) {
				throw new BadHuffmanTableException();
			}

			final int[] CodeList = new int[LenList.length];
			for (int i = 0; i < CodeList.length; i++) {
				if (0 < LenList[i]) {
					CodeList[i] = CodeStart[LenList[i]]++;
				}
			}

			return CodeList;
		}
		return new int[LenList.length];
	}

	// ------------------------------------------------------------------
	// shared method

	/**
	 * LenList から、ハフマン復号用のテーブルを生成する。<br>
	 * 
	 * @param LenList ハフマン符号長の表
	 * @return ハフマン復号用テーブル。
	 * @exception BadHuffmanTableException LenListが不正なため、 ハフマン符号表が生成出来ない場合
	 */
	public static short[] createTable(final int[] LenList) throws BadHuffmanTableException {
		final int[] CodeList = LenListToCodeList(LenList);            // throws BadHuffmanTableException
		int TableBits = 0;
		int LastCode = 0;

		for (int i = 0; i < LenList.length; i++) {
			if (TableBits <= LenList[i]) {
				TableBits = LenList[i];
				LastCode = i;
			}
		}

		final short[] Table = new short[1 << TableBits];
		for (int i = 0; i < LenList.length; i++) {
			if (0 < LenList[i]) {
				final int start = CodeList[i] << TableBits - LenList[i];
				final int end = i != LastCode ? start
						+ (1 << TableBits - LenList[i]) : Table.length;

				for (int j = start; j < end; j++) {
					Table[j] = (short) i;
				}
			}
		}
		return Table;
	}

	/**
	 * LenList から、ハフマン復号用のテーブルと木を生成する。 テーブルは TableBits の大きさを持ち、それ以上の部分は木に格納される。<br>
	 * 戻り値は new short[][]{ Table, Tree[0], Tree[1] } となる。<br>
	 * テーブルを引いた結果もしくは木を走査した際、負の値を得た場合、 それは復号化されたコードを全ビット反転したものである。 正の値であればそれは 木を走査するための index であり、 Tree[bit][index] のように使用する。
	 * 
	 * @param LenList ハフマン符号長の表
	 * @param TableBits ハフマン復号用テーブルの大きさ。
	 * @return ハフマン復号用テーブルと木。
	 * @exception BadHuffmanTableException LenListが不正なため、 ハフマン符号表が生成出来ない場合
	 */
	public static short[][] createTableAndTree(final int[] LenList, final int TableBits) throws BadHuffmanTableException {
		// ------------------------------------------------------------------
		// ハフマン符号長リストから ハフマン符号のリストを得る。
		final int[] CodeList = LenListToCodeList(LenList);            // throws BadHuffmanTableException

		// ------------------------------------------------------------------
		// ハフマン符号長のリストを走査し、
		// LastCode を得る。
		// また 木を構成するのに必要な配列サイズを得るための準備を行う。
		final short[] Table = new short[1 << TableBits];
		int LastCode = 0;
		for (int i = 0; i < LenList.length; i++) {
			if (LenList[LastCode] <= LenList[i]) {
				LastCode = i;
			}

			if (TableBits < LenList[i]) {
				Table[CodeList[i] >> LenList[i] - TableBits]++;
			}
		}

		// ------------------------------------------------------------------
		// 木を構成するのに必要な配列サイズを得、テーブルを初期化する。
		final short INIT = -1;
		int count = 0;
		for (int i = 0; i < Table.length; i++) {
			if (0 < Table[i]) {
				count += Table[i] - 1;
			}
			Table[i] = INIT;
		}
		final short[] Small = new short[count];
		final short[] Large = new short[count];

		// ------------------------------------------------------------------
		// テーブルと木を構成する。
		int avail = 0;
		for (int i = 0; i < LenList.length; i++) {
			if (0 < LenList[i]) {
				final int TreeBits = LenList[i] - TableBits;
				if (TreeBits <= 0) {
					final int start = CodeList[i] << TableBits - LenList[i];
					final int end = i != LastCode ? start
							+ (1 << TableBits - LenList[i]) : Table.length;
					for (int j = start; j < end; j++) {
						Table[j] = (short) ~i;
					}
				} else {
					final int TableCode = CodeList[i] >> TreeBits;
					int node;
					if (Table[TableCode] == INIT) {
						node = Table[TableCode] = (short) avail++;
					} else {
						node = Table[TableCode];
					}
					for (int j = TableBits + 1; j < LenList[i]; j++) {
						if (0 == (CodeList[i] & 1 << LenList[i] - j)) {
							if (Small[node] == 0) {
								node = Small[node] = (short) avail++;
							} else {
								node = Small[node];
							}
						} else {
							if (Large[node] == 0) {
								node = Large[node] = (short) avail++;
							} else {
								node = Large[node];
							}
						}
					}
					if (0 == (CodeList[i] & 0x01)) {
						Small[node] = (short) ~i;
					} else {
						Large[node] = (short) ~i;
					}
				}
			}
		}
		return new short[][] { Table, Small, Large };
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * マージソート、再帰関数<br>
	 * arrayは weightの添字、arrayのfirstからlastの区間内で weightが小さい順に並ぶようにソートする。 workはそのための作業領域。
	 * 
	 * @param array ソート対象の配列
	 * @param first ソート区間の最初
	 * @param last ソート区間の最後
	 * @param weight ソートの際に参照される重みのリスト
	 * @param work マージソート用作業領域
	 */
	private static void mergeSort(final int[] array, final int first, final int last, final int[] weight, final int[] work) {
		if (first < last) {
			final int middle = (first + last) / 2 + (first + last) % 2;
			// 前半をソート
			mergeSort(array, first, middle - 1, weight, work);
			// 後半をソート
			mergeSort(array, middle, last, weight, work);

			// 前半を workへ
			System.arraycopy(array, first, work, 0, middle - first);

			// ソートされた前半と ソートされた後半を
			// 整列しつつマージする。
			int srcIndex = middle;
			int workIndex = 0;
			int dstIndex = first;
			while (srcIndex <= last && workIndex < middle - first) {
				array[dstIndex++] = weight[work[workIndex]] < weight[array[srcIndex]] ? work[workIndex++] : array[srcIndex++];
			}

			// workに残った要素を arrayに戻す
			if (workIndex < middle - first) {
				System.arraycopy(work, workIndex, array, dstIndex, middle - first - workIndex);
			}
		}
	}

	/**
	 * heapはweightの添え字 num*2, num*2+1の地点でヒープが出来ていることを 前提として heap に numを頂点とするヒープを作る。<br>
	 * ヒープソートの一部分。
	 * 
	 * @param heap ヒープを生成する配列
	 * @param size ヒープのサイズ
	 * @param weight 整列の基準となる重みのリスト
	 * @param num 今回作成するヒープの頂点
	 */
	private static void downHeap(final int[] heap, final int size, final int[] weight, int num) {
		final int top = heap[num];
		int i;
		while ((i = 2 * num) <= size) {
			if (i < size && weight[heap[i]] > weight[heap[i + 1]]) {
				i++;
			}
			if (weight[top] <= weight[heap[i]]) {
				break;
			}

			heap[num] = heap[i];
			num = i;
		}
		heap[num] = top;
	}

	/**
	 * ハフマン木から ハフマン符号長の頻度表を作成する。<br>
	 * ハフマン木を辿って ハフマン符号長の頻度表を作成する。 また、符号長を 16ビットに制限するための処理もここで行う。
	 * 
	 * @param SmallNode 小さい子ノードのノード番号の表
	 * @param LargeNode 大きい子ノードのノード番号の表
	 * @param root ハフマン木のルートノード
	 * @return ハフマン符号長を最大16ビットに制限した ハフマン符号長表
	 */
	private static int[] huffmanTreeToLenFreq(final int[] SmallNode, final int[] LargeNode, final int root) {
		final int[] LenFreq = new int[LIMIT_LEN + 1];

		// ハフマン木から頻度表作成
		internalHuffmanTreeToLenFreq(SmallNode, LargeNode, root, 0, LenFreq);

		// System.out.println( "到達::StaticHuffman.HuffmanTreeToLenFreq--ハフマン木からハフマン符号長のリスト取得--" );

		// 最大16ビットの制限により、修正を受けている場合は
		// 符号長の表から、上位のノードを下位へと引きずりおろす
		// ことによって符号長の表を修正する。
		int weight = 0;
		for (int i = LIMIT_LEN; 0 < i; i--) {
			weight += LenFreq[i] << LIMIT_LEN - i;
		}

		// System.out.println( "weight::" + weight );

		while (1 << LIMIT_LEN < weight) {
			LenFreq[LIMIT_LEN]--;
			for (int i = LIMIT_LEN - 1; 0 < i; i--) {
				if (0 < LenFreq[i]) {
					LenFreq[i]--;
					LenFreq[i + 1] += 2;
					break;
				}
			}

			weight--;
		}

		return LenFreq;
	}

	/**
	 * ハフマン木探索メソッド、再帰関数。<br>
	 * ハフマン木を探索していき、nodeが葉であれば 渡された符号長の頻度表を更新し、 ノードであれば、小さい方と大きい方の両方の 子ノードを再帰的に探索する。<br>
	 * 
	 * @param SmallNode 小さい子ノードのノード番号の表
	 * @param LargeNode 大きい子ノードのノード番号の表
	 * @param node 処理するノード番号
	 * @param len ハフマン木のrootからの長さ
	 * @param LenFreq 符号長の頻度表
	 */
	private static void internalHuffmanTreeToLenFreq(final int[] SmallNode, final int[] LargeNode, final int node, final int len, final int[] LenFreq) {
		if (node < (SmallNode.length + 1) / 2) {
			// nodeが葉なら頻度表更新
			LenFreq[len < LIMIT_LEN ? len : LIMIT_LEN]++;
		} else {
			// nodeがノードなら両方のノードを再帰的に探索
			internalHuffmanTreeToLenFreq(SmallNode, LargeNode, SmallNode[node], len + 1, LenFreq);
			internalHuffmanTreeToLenFreq(SmallNode, LargeNode, LargeNode[node], len + 1, LenFreq);
		}
	}

}