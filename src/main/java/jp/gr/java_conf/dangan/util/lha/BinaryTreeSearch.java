//start of BinaryTreeSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BinaryTreeSearch.java
 * 
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

//import classes and interfaces
import jp.gr.java_conf.dangan.util.lha.LzssOutputStream;
import jp.gr.java_conf.dangan.util.lha.LzssSearchMethod;

//import exceptions


/**
 * 二分木を使用した LzssSearchMethod の実装。<br>
 * <pre>
 * データ圧縮ハンドブック[改定第二版]
 *        M.ネルソン/J.-L.ゲィリー 著
 *                萩原剛志・山口英 訳
 *                  ISBN4-8101-8605-9
 *                             5728円(税抜き,当方の購入当時の価格)
 * </pre>
 * を参考にした。<br>
 * 二分木では、最長一致を見つけることはできるが、
 * 最も近い一致を見つけられるとは限らないため、
 * LZSSで 一致位置が近い場所に偏る事を
 * 利用するような -lh5- のような圧縮法では、
 * 圧縮率はいくらか低下する。
 * 
 * <pre>
 * -- revision history --
 * $Log: BinaryTreeSearch.java,v $
 * Revision 1.0  2002/08/06 00:00:00  dangan
 * add to version control
 * [change]
 *     LzssSearchMethod のインタフェイス変更にあわせてインタフェイス変更
 * [maintenance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class BinaryTreeSearch implements LzssSearchMethod{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int UNUSED
    //  private static final int ROOT_NODE
    //------------------------------------------------------------------
    /**
     * 使用されていない事を示す値。
     * parent[node] に UNUSED がある場合は node は未使用のnodeである。
     * small[node], large[node] に UNUSED がある場合は
     * 二分木のそちら側には節が無い事を示す。
     */
    private static final int UNUSED = -1;

    /**
     * 二分木の根を示す値。
     * parent[node] に ROOT_NODE がある場合は node は二分木の根である。
     */
    private static final int ROOT_NODE = -2;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  LZSS parameter
    //------------------------------------------------------------------
    //  private int DictionarySize
    //  private int MaxMatch
    //  private int Threshold
    //------------------------------------------------------------------
    /**
     * LZSS辞書サイズ。
     */
    private int DictionarySize;

    /**
     * LZSS圧縮に使用される値。
     * 最大一致長を示す。
     */
    private int MaxMatch;

    /**
     * LZSS圧縮に使用される閾値。
     * 一致長が この値以上であれば、圧縮コードを出力する。
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  text buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS圧縮を施すためのバッファ。
     * position を境に 前半は辞書領域、
     * 後半は圧縮を施すためのデータの入ったバッファ。
     * LzssSearchMethodの実装内では読み込みのみ許される。
     */
    private byte[] TextBuffer;

    /**
     * 辞書の限界位置。 
     * TextBuffer前半の辞書領域にデータが無い場合に
     * 辞書領域にある不定のデータ(Javaでは0)を使用
     * して圧縮が行われるのを抑止する。
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  binary tree
    //------------------------------------------------------------------
    //  private int root
    //  private int[] parent
    //  private int[] small
    //  private int[] large
    //  private int[] dummy
    //------------------------------------------------------------------
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


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private BinaryTreeSearch()
    //  public BinaryTreeSearch( int DictionarySize, int MaxMatch, 
    //                           int Threshold, byte[] TextBuffer )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     *使用不可
     */
    private BinaryTreeSearch(){ }

    /**
     * 二分木を使用した LzssSearchMethod を構築する。<br>
     * 
     * @param DictionarySize  辞書サイズ
     * @param MaxMatch        最長一致長
     * @param Threshold       圧縮、非圧縮の閾値
     * @param TextBuffer      LZSS圧縮を施すためのバッファ
     */
    public BinaryTreeSearch( int    DictionarySize,
                             int    MaxMatch,
                             int    Threshold,
                             byte[] TextBuffer ){

        this.DictionarySize  = DictionarySize;
        this.MaxMatch        = MaxMatch;
        this.Threshold       = Threshold;
        this.TextBuffer      = TextBuffer;
        this.DictionaryLimit = this.DictionarySize;

        this.root            = BinaryTreeSearch.UNUSED;
        this.parent          = new int[ this.DictionarySize ];
        this.large           = new int[ this.DictionarySize ];
        this.small           = new int[ this.DictionarySize ];

        for( int i = 0 ; i < this.parent.length ; i++ ){
            this.parent[i]   = BinaryTreeSearch.UNUSED;
        }
    }


    //------------------------------------------------------------------
    //  methods of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide()
    //  public int putRequires()
    //------------------------------------------------------------------
    /**
     * position から始まるデータパタンを二分木に登録する。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置
     */
    public void put( int position ){

        //------------------------------------------------------------------
        //  二分木から最も古いデータパタンを削除
        this.deleteNode( position - this.DictionarySize );

        //------------------------------------------------------------------
        //  二分木から position を挿入する位置を検索
        int parentpos  = this.root;
        int scanpos    = this.root;

        byte[] buf     = this.TextBuffer;
        int max        = position + this.MaxMatch;
        int p          = 0;
        int s          = 0;
        int len        = 0;
        while( scanpos != BinaryTreeSearch.UNUSED ){

            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ){
                    //完全一致を発見
                    this.replaceNode( scanpos, position );
                    return;
                }
            }

            parentpos = scanpos;
            scanpos = ( buf[ s ] < buf[ p ]
                      ? this.large[ scanpos & ( this.DictionarySize - 1 ) ]
                      : this.small[ scanpos & ( this.DictionarySize - 1 ) ] );
        }

        //------------------------------------------------------------------
        //  position から始まるデータパタンを 二分木に登録
        if( this.root != BinaryTreeSearch.UNUSED ){
            this.addNode( parentpos, position, p - position );
        }else{
            this.root = position;
            int node  = position & ( this.DictionarySize - 1 );
            this.parent[ node ] = BinaryTreeSearch.ROOT_NODE;
            this.small[ node ]  = BinaryTreeSearch.UNUSED;
            this.large[ node ]  = BinaryTreeSearch.UNUSED;
        }
    }

    /**
     * 二分木に登録されたデータパタンから
     * position から始まるデータパタンと
     * 最長の一致を持つものを検索し、
     * 同時に position から始まるデータパタンを 
     * 二分木に登録する。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置。
     * 
     * @return 一致が見つかった場合は
     *         LzssOutputStream.createSearchReturn 
     *         によって生成された一致位置と一致長の情報を持つ値、
     *         一致が見つからなかった場合は
     *         LzssOutputStream.NOMATCH。
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int searchAndPut( int position ){

        //------------------------------------------------------------------
        //  二分木から最も古いデータパタンを削除
        this.deleteNode( position - this.DictionarySize );

        //------------------------------------------------------------------
        //  二分木から最長一致を検索
        int matchlen   = -1;
        int matchpos   = this.root;
        int parentpos  = this.root;
        int scanpos    = this.root;

        byte[] buf     = this.TextBuffer;
        int max        = position + this.MaxMatch;
        int p          = 0;
        int s          = 0;
        int len        = 0;
        while( scanpos != BinaryTreeSearch.UNUSED ){

            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ){
                    //完全一致を発見
                    this.replaceNode( scanpos, position );
                    return LzssOutputStream.createSearchReturn( this.MaxMatch, scanpos );
                }
            }

            len = p - position;
            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
            }else if( matchlen == len && matchpos < scanpos ){
                matchpos = scanpos;
            }

            parentpos = scanpos;
            scanpos = ( buf[ s ] < buf[ p ]
                      ? this.large[ scanpos & ( this.DictionarySize - 1 ) ]
                      : this.small[ scanpos & ( this.DictionarySize - 1 ) ] );
        }

        //------------------------------------------------------------------
        //  position から始まるデータパタンを 二分木に登録
        if( this.root != BinaryTreeSearch.UNUSED ){
            this.addNode( parentpos, position, len );
        }else{
            this.root = position;
            int node  = position & ( this.DictionarySize - 1 );
            this.parent[ node ] = BinaryTreeSearch.ROOT_NODE;
            this.small[ node ]  = BinaryTreeSearch.UNUSED;
            this.large[ node ]  = BinaryTreeSearch.UNUSED;
        }

        //------------------------------------------------------------------
        //  メソッドの先頭で削除された
        //  最も遠いデータパタンと比較
        scanpos  = position - this.DictionarySize;
        if( this.DictionaryLimit <= scanpos ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            len = p - position;
            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
            }
        }

        //------------------------------------------------------------------
        //  最長一致を呼び出し元に返す。
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * 二分木に登録されたデータパタンを検索し
     * position から始まるデータパタンと
     * 最長の一致を持つものを得る。<br>
     * TextBuffer.length &lt position + MaxMatch 
     * となるような position では、
     * 二分木を完全に走査できないため
     * 最長一致を得られるとは限らない。<br>
     * 
     * @param position   TextBuffer内のデータパタンの開始位置。
     * @param lastPutPos 最後に登録したデータパタンの開始位置。
     * 
     * @return 一致が見つかった場合は
     *         LzssOutputStream.createSearchReturn 
     *         によって生成された一致位置と一致長の情報を持つ値、
     *         一致が見つからなかった場合は
     *         LzssOutputStream.NOMATCH。
     * 
     * @see LzssOutputStream#createSearchReturn(int,int)
     * @see LzssOutputStream#NOMATCH
     */
    public int search( int position, int lastPutPos ){

        //------------------------------------------------------------------
        //  二分木に登録されていないデータパタンを
        //  単純な逐次検索で検索する。
        int matchlen   = this.Threshold - 1;
        int matchpos   = position;
        int scanpos    = position - 1;
        int scanlimit  = Math.max( this.DictionaryLimit, lastPutPos );

        byte[] buf     = this.TextBuffer;
        int max        = Math.min( this.TextBuffer.length,
                                   position + this.MaxMatch );
        int s          = 0;
        int p          = 0;
        int len        = 0;
        while( scanlimit < scanpos ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            len = p - position;
            if( matchlen < len ){
                matchpos = scanpos;
                matchlen = len;
                if( max <= p ) break;
            }
            scanpos--;
        }

        //------------------------------------------------------------------
        //  二分木を探索
        scanpos   = this.root;
        scanlimit = Math.max( this.DictionaryLimit, 
                              position - this.DictionarySize );
        while( scanpos != BinaryTreeSearch.UNUSED ){
            s = scanpos;
            p = position;
            while( buf[ s ] == buf[ p ] ){
                s++;
                p++;
                if( max <= p ) break;
            }

            if( p < max ){
                len = p - position;
                if( scanlimit <= scanpos ){
                    if( matchlen < len ){
                        matchpos = scanpos;
                        matchlen = len;
                    }else if( matchlen == len && matchpos < scanpos ){
                        matchpos = scanpos;
                    }
                }
                scanpos = ( buf[ s ] < buf[ p ]
                          ? this.large[ scanpos & ( this.DictionarySize - 1 ) ]
                          : this.small[ scanpos & ( this.DictionarySize - 1 ) ] );
            }else{
                break;
            }
        }


        //------------------------------------------------------------------
        //  最長一致を呼び出し元に返す。
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }


    /**
     * TextBuffer内のpositionまでのデータを前方へ移動する際、
     * それに応じて 二分木を構成するデータも
     * TextBuffer内のデータと矛盾しないように前方へ移動する処理を行う。 
     * 
     * @param slideWidth ずらす幅
     * @param slideEnd   ずらすデータの終端 + 1(データ転送先)
     */
    public void slide(){
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );

        this.root -= this.DictionarySize;
        this.slideTree( this.parent );
        this.slideTree( this.small );
        this.slideTree( this.large );
    }

    /**
     * put() または searchAndPut() を使用して
     * データパタンを二分木に登録する際に
     * 必要とするデータ量を得る。<br>
     * 二分木は登録の際にデータパタンを構成する 
     * 全て(MaxMatchバイト)のデータを必要とする。
     * 
     * @return コンストラクタで与えた MaxMatch
     */
    public int putRequires(){
        return this.MaxMatch;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  manipulate node
    //------------------------------------------------------------------
    //  private void addNode( int addpos, int position, int len )
    //  private void deleteNode( int position )
    //  private void contractNode( int oldpos, int newpos )
    //  private void replaceNode( int oldpos, int newpos )
    //------------------------------------------------------------------
    /**
     * parentpos のデータパタンの子として 
     * position から始まるデータパタンを二分木に登録する。<br>
     * parentpos と position のデータパタンは len バイト一致する。
     * position の位置のノードはあらかじめ deleteNode 等で
     * UNUSED の状態にしておくこと。
     * 
     * @param parentpos 親のデータパタンのTextBuffer内の開始位置
     * @param position  新規追加するデータパタンのTextBuffer内の開始位置
     * @param len       親のデータパタンと新規追加するデータパタンの一致長
     */
    private void addNode( int parentpos, int position, int len ){
        int parentnode = parentpos & ( this.DictionarySize - 1 );
        int node       = position  & ( this.DictionarySize - 1 );

        if( this.TextBuffer[ parentpos + len ] < this.TextBuffer[ position + len ] ){
            this.large[ parentnode ] = position;
        }else{
            this.small[ parentnode ] = position;
        }
        this.parent[ node ] = parentpos;
        this.small[ node ]  = BinaryTreeSearch.UNUSED;
        this.large[ node ]  = BinaryTreeSearch.UNUSED;
    }

    /**
     * position から始まるデータパタンを二分木から削除する。<br>
     * 
     * @param position 削除するデータパタンの開始位置
     */
    private void deleteNode( int position ){
        int node = position & ( this.DictionarySize - 1 );

        if( this.parent[ node ] != BinaryTreeSearch.UNUSED ){
            if( this.small[ node ] == BinaryTreeSearch.UNUSED
             && this.large[ node ] == BinaryTreeSearch.UNUSED ){
                this.contractNode( position, BinaryTreeSearch.UNUSED );
            }else if( this.small[ node ] == BinaryTreeSearch.UNUSED ){
                this.contractNode( position, this.large[ node ] );
            }else if( this.large[ node ] == BinaryTreeSearch.UNUSED ){
                this.contractNode( position, this.small[ node ] );
            }else{
                int replace = this.findNext( position );
                this.deleteNode( replace );
                this.replaceNode( position, replace );
            }
        }
    }

    /**
     * 子に newpos しか持たない oldpos を, newpos で置き換える。
     * oldpos は二分木から削除される。
     * 
     * @param oldpos 削除するデータパタンの開始位置
     * @param newpos oldposに置き換わるデータパタンの開始位置
     */
    private void contractNode( int oldpos, int newpos ){
        int oldnode    = oldpos    & ( this.DictionarySize - 1 );
        int newnode    = newpos    & ( this.DictionarySize - 1 );
        int parentpos  = this.parent[ oldnode ];
        int parentnode = parentpos & ( this.DictionarySize - 1 );

        if( parentpos != BinaryTreeSearch.ROOT_NODE ){
            if( oldpos == this.small[ parentnode ] ){
                this.small[ parentnode ] = newpos;
            }else{
                this.large[ parentnode ] = newpos;
            }
        }else{
            this.root = newpos;
        }

        if( newpos != BinaryTreeSearch.UNUSED ){
            this.parent[ newnode ] = parentpos;
        }

        this.parent[ oldnode ] = BinaryTreeSearch.UNUSED;
    }

    /**
     * oldpos を二分木に含まれない新しいデータパタン newpos で置き換える。
     * newpos が二分木に含まれているような場合には、
     * いったんdeleteNode(newpos) するなどして、
     * 二分木から外す必要がある。
     * oldpos は二分木から削除される。
     * 
     * @param oldpos 削除するデータパタンの開始位置
     * @param newpos oldposに置き換わるデータパタンの開始位置
     */
    private void replaceNode( int oldpos, int newpos ){
        int oldnode    = oldpos    & ( this.DictionarySize - 1 );
        int newnode    = newpos    & ( this.DictionarySize - 1 );
        int parentpos  = this.parent[ oldnode ];
        int parentnode = parentpos & ( this.DictionarySize - 1 );

        if( parentpos != BinaryTreeSearch.ROOT_NODE ){
            if( oldpos == this.small[ parentnode ] ){
                this.small[ parentnode ] = newpos;
            }else{
                this.large[ parentnode ] = newpos;
            }
        }else{
            this.root = newpos;
        }

        this.parent[ newnode ] = parentpos;
        this.small[ newnode ]  = this.small[ oldnode ];
        this.large[ newnode ]  = this.large[ oldnode ];
        if( this.small[ newnode ] != BinaryTreeSearch.UNUSED ){
            this.parent[ this.small[ newnode ] & ( this.DictionarySize - 1 ) ] = newpos;
        }
        if( this.large[ newnode ] != BinaryTreeSearch.UNUSED ){
            this.parent[ this.large[ newnode ] & ( this.DictionarySize - 1 ) ] = newpos;
        }

        this.parent[ oldnode ] = BinaryTreeSearch.UNUSED;
        this.large[ oldnode ]  = BinaryTreeSearch.UNUSED;
        this.small[ oldnode ]  = BinaryTreeSearch.UNUSED;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private int findNext( int position )
    //  private void slideTree( int[] array )
    //------------------------------------------------------------------
    /**
     * deleteNode( position ) したときに、
     * small と large の両方の葉が見つかった場合、
     * position から始まるデータパタンと
     * 置き換えるべきデータパタンの開始位置を探し出す。
     * 
     * @param position 置き換えられるデータパタンの開始位置
     * 
     * @return position から始まるデータパタンと
     *         置き換えるべきデータパタンの開始位置
     */
    private int findNext( int position ){
        int node = position & ( this.DictionarySize - 1 );

        position = this.small[ node ];
        node     =  position & ( this.DictionarySize - 1 );
        while( BinaryTreeSearch.UNUSED != this.large[ node ] ){
            position = this.large[ node ];
            node     = position & ( this.DictionarySize - 1 );
        }

        return position;
    }


    /**
     * slide() 時に、二分木の各要素を移動する。
     * 
     * @param array 二分木を構成する配列
     */
    private void slideTree( int[] array ){
        for( int i = 0 ; i < array.length ; i++ ){
            array[ i ] = ( 0 <= array[i] 
                         ? array[i] - this.DictionarySize 
                         : array[i] );
        }
    }

}
//end of BinaryTreeSearch.java
