//start of DynamicHuffman.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * DynamicHuffman.java
 * 
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

//import classes and interfaces
import java.lang.Cloneable;

//import exceptions
import java.io.IOException;

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
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class DynamicHuffman implements Cloneable{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int ROOT
    //  private static final int MAX_WEIGHT
    //------------------------------------------------------------------
    /**
     * ハフマン木のルートを示す。
     */
    public static final int ROOT = 0;

    /**
     * ハフマン木を再構築する重さ
     */
    private static final int MAX_WEIGHT = 0x8000;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  huffman tree
    //------------------------------------------------------------------
    //  private int[] weight
    //  private int[] child
    //  private int[] parent
    //  private int[] leafs
    //  private int size
    //------------------------------------------------------------------
    /**
     * 添え字のノードの重さを示す。
     */
    private int[] weight;

    /**
     * 添え字のノードの子ノードのノード番号を保持する
     * 兄弟特性を利用するため、
     * child     が 小さいノードのノード番号
     * child - 1 が 大きいノードのノード番号となる。
     * 葉の場合はデータをbit反転したものが入っている。
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


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private DynamicHuffman()
    //  public DynamicHuffman( int count )
    //  public DynamicHuffman( int max, int first )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private DynamicHuffman(){   }

    /**
     * コンストラクタ
     * 
     * @param count 葉の数
     */
    public DynamicHuffman( int count ){
        this( count, count );
    }

    /**
     * コンストラクタ
     * 
     * @param max   葉の最大数
     * @param start 開始時の葉の数 
     */
    public DynamicHuffman( int max, int first ){
        if( 1 <= first && first <= max ){

            this.weight = new int[ max * 2 - 1 ];
            this.child  = new int[ max * 2 - 1 ];
            this.parent = new int[ max * 2 - 1 ];
            this.leafs  = new int[ max ];
            this.size   = Math.max( 0, first * 2 - 1 );

            //葉を生成していく。
            int node = this.size - 1;
            for( int code = 0 ; code < first ; code++, node-- ){
                this.weight[ node ] = 1;
                this.child[ node ]  = ~code;
                this.leafs[ code ]  = node;
            }

            //枝を生成していく。
            int child = this.size - 1;
            while( 0 <= node && node != child ){
                this.weight[node]  = this.weight[child] + this.weight[child-1];

                this.child[node]   = child;
                this.parent[child] = this.parent[child-1] = node;

                child -= 2;
                node--;
            }
        }else if( max < first ){
            throw new IllegalArgumentException( "\"max\" must be larger than \"first\"." );
        }else{
            throw new IllegalArgumentException( "\"first\" must be one or more." );
        }
    }


    //------------------------------------------------------------------
    //  method of java.lang.Object
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * このオブジェクトの現在の状態を持つコピーを作成して返す。
     * 
     * @return このオブジェクトの現在の状態を持つコピー
     */
    public Object clone(){
        DynamicHuffman clone = new DynamicHuffman();
        clone.weight = (int[])this.weight.clone();
        clone.child  = (int[])this.child.clone();
        clone.parent = (int[])this.parent.clone();
        clone.leafs  = (int[])this.leafs.clone();
        clone.size   = this.size;
        return clone;
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access to huffman tree
    //------------------------------------------------------------------
    //  public int codeToNode( int code )
    //  public int childNode( int node )
    //  public int parentNode( int node )
    //------------------------------------------------------------------
    /**
     * データからノード番号を得る。
     * 
     * @param code データ
     * 
     * @return codeのノード番号
     */
    public int codeToNode( int code ){
        return this.leafs[code];
    }

    /**
     * ノードが葉でないノードなら子ノードのノード番号、
     * ノードが葉ならノードの持つデータを全ビット反転したものを得る。
     * 子ノードのノード番号は兄弟特性と利用するため、<br>
     * node の 0 の子ノードの場合 childNode( node )<br>
     * node の 1 の子ノードの場合 childNode( node ) - 1<br>
     * となる。
     * 
     * @param node ノード
     * 
     * @return node の子ノードのノード番号
     */
    public int childNode( int node ){
        return this.child[node];
    }

    /**
     * node の親ノードのノード番号を得る。
     * 
     * @param node ノード
     * 
     * @return node の親ノードのノード番号。
     */
    public int parentNode( int node ){
        return this.parent[node];
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  update huffman tree
    //------------------------------------------------------------------
    //  public void update( int code )
    //  public void addLeaf( int code )
    //------------------------------------------------------------------
    /**
     * code の重みが増すようにハフマン木を更新する。
     * 
     * @param code 重みを増やす葉
     */
    public void update( int code ){
        if( this.weight[ DynamicHuffman.ROOT ] == DynamicHuffman.MAX_WEIGHT ){
            this.rebuildTree();
        }

        int node = this.leafs[code];
        while( DynamicHuffman.ROOT != node ){
            int swapNode = node;
            while( this.weight[swapNode - 1] == this.weight[node]
                && DynamicHuffman.ROOT < swapNode - 1 ){
                swapNode--;
            }

            if( node != swapNode ) this.swap( node, swapNode );

            this.weight[swapNode]++;
            node = this.parent[swapNode];
        }
        this.weight[ DynamicHuffman.ROOT ]++;
    }

    /**
     * ハフマン木に code を示す葉を追加する。
     * 
     * @param code 葉の示す符号
     * 
     * @exception IllegalStateException
     *              ハフマン木が十分に大きいため
     *              葉が追加できない場合
     */
    public void addLeaf( int code ){
        if( this.size < this.weight.length - 1 ){
            int last  = this.size - 1;
            int large = this.size;
            int small = this.size + 1;
            this.child[ large ] = this.child[ last ];
            this.child[ small ] = ~code;
            this.child[ last ]  = small;
            this.weight[ large ] = this.weight[ last ];
            this.weight[ small ] = 0;
            this.leafs[ ~this.child[ large ] ] = large;
            this.leafs[ ~this.child[ small ] ] = small;
            this.parent[ large ] = this.parent[ small ] = last;
            this.size = small + 1;

            if( last == DynamicHuffman.ROOT ){
                this.weight[ last  ] -= 1;
            }

            this.update( code );
        }else{
            throw new IllegalStateException();
        }
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void rebuildTree()
    //  private void swap( int i, int j )
    //------------------------------------------------------------------
    /**
     * ハフマン木を再構築する。
     * 重みが privateな定数 MAX_WEIGHT を超えた時に
     * update(int)から呼び出される。
     * 全てのノードの重みを およそ半分にする。
     */
    private void rebuildTree(){
        int leafCount = 0;
        for( int i = 0 ; i < this.size ; i++ )
            if( this.child[i] < 0 ){
                this.weight[leafCount] = ( this.weight[i] + 1 ) / 2;
                this.child[leafCount]  = this.child[i];
                leafCount++;
            }

        leafCount--;
        int position     = this.size - 1;
        int leafPosition = this.size - 2;
        while( 0 <= position ){
            while( leafPosition <= position ){
                this.leafs[~this.child[leafCount]] = position;
                this.weight[ position ]  = this.weight[ leafCount ];
                this.child[ position-- ] = this.child[ leafCount-- ];
            }

            int weight = this.weight[leafPosition]
                       + this.weight[leafPosition + 1];

            while( 0 <= leafCount && this.weight[leafCount] <= weight ){
                this.leafs[~this.child[leafCount]] = position;
                this.weight[ position ]  = this.weight[ leafCount ];
                this.child[ position-- ] = this.child[ leafCount-- ];
            }

            this.weight[position] = weight;
            this.child[position]  = leafPosition + 1;
            this.parent[leafPosition]
                = this.parent[leafPosition + 1]
                = position;

            position--;
            leafPosition -= 2;
        }
    }

    /**
     * ノード番号iのノードと
     * ノード番号jのノードを入れ換える処理を行う。
     * 
     * @param i 入れ換え対象のノード
     * @param j 入れ換え対象のノード
     */
    private void swap( int i, int j ){
        if( this.child[i] < 0 ){
            this.leafs[ ~this.child[i] ] = j;
        }else{
            this.parent[ this.child[i] ]
                = this.parent[ this.child[i] - 1 ]
                = j;
        }

        if( this.child[j] < 0 ){
            this.leafs[ ~this.child[j] ] = i;
        }else{
            this.parent[ this.child[j] ]
                = this.parent[ this.child[j] - 1 ]
                = i;
        }

        int temp      = this.child[i];
        this.child[i] = this.child[j];
        this.child[j] = temp;

        temp           = this.weight[i];
        this.weight[i] = this.weight[j];
        this.weight[j] = temp;
    }

}
//end of DynamicHuffman.java
