//start of PatriciaTrieSearch.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * PatriciaTrieSearch.java
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

package com.orangesignal.jlha;

//import classes and interfaces
import java.math.BigInteger;

import com.orangesignal.jlha.LzssOutputStream;
import com.orangesignal.jlha.LzssSearchMethod;

//import exceptions

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
 * @author  $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class PatriciaTrieSearch implements LzssSearchMethod{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final int UNUSED
    //------------------------------------------------------------------
    /**
     * 使用されていない事を示す値。
     * parent[node] に UNUSED がある場合は node は未使用の node である。
     * prev[node], next[node] に UNUSED がある場合は、
     * そちら側に兄弟ノードが無いことを示す。
     */
    private static final int UNUSED = 0;


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
     * LZSS 辞書サイズ
     */
    private int DictionarySize;

    /**
     * LZSS 圧縮に使用される値。
     * 最大一致長を示す。
     */
    private int MaxMatch;

    /**
     * LZSS 圧縮/非圧縮の閾値。
     * 一致長が この値以上であれば、圧縮コードを出力する。
     */
    private int Threshold;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  Text Buffer
    //------------------------------------------------------------------
    //  private byte[] TextBuffer
    //  private int DictionaryLimit
    //------------------------------------------------------------------
    /**
     * LZSS 圧縮を施すためのバッファ。
     * LzssSearchMethod の実装内では読み込みのみ許される。
     */
    private byte[] TextBuffer;

    /**
     * 辞書の限界位置。
     * TextBuffer前半の辞書領域に未だにデータが無い場合に
     * 辞書領域にある不定のデータ(Java では0)を使用して
     * 圧縮が行われないようにする。
     */
    private int DictionaryLimit;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  PATRICIA TRIE
    //------------------------------------------------------------------
    //  private int[] parent
    //  private int[] hashTable
    //  private int[] prev
    //  private int[] next
    //  private int[] position
    //  private int[] level
    //  private int[] childnum
    //  private int avail
    //  private int shift
    //------------------------------------------------------------------
    /**
     * 親のノード番号を示す。
     * parent[node] は node の親ノードの番号を示す。
     */
    private int[] parent;

    /**
     * 子のハッシュ値を示す。
     * hashTable[ hash( node, ch ) ] で
     * node の文字 ch の子ノードのハッシュ値を示す。
     */
    private int[] hashTable;

    /**
     * hashTable から連なる双方向連結リストの一部。
     * 同じハッシュ値を持つ 一つ前のノードのノード番号を示す。
     * prev[ node ] は node と同じハッシュ値を持ち、
     * 連結リスト内で node の一つ前に位置するノードの node 番号。
     * prev[ node ] が 負値の場合は全ビット反転したハッシュ値を示す。
     */
    private int[] prev;

    /**
     * hashTable から連なる双方向連結リストの一部。
     * 同じハッシュ値を持つ 一つ後のノードのノード番号を示す。
     * next[ node ] は node と同じハッシュ値を持ち、
     * 連結リスト内で node の一つ後ろに位置するノードの node 番号。
     * 
     * また、葉でないノードに関しては next と avail で 未使用なノードの
     * スタック(一方向連結リスト)を構成する。
     * 
     * さらに、完全一致があったため削除された葉ノードで、
     * PATRICIA Trie 内に存在している葉ノードへの一方向連結リストを構成する。
     */
    private int[] next;

    /**
     * ノードの TextBuffer 内のデータパタンの開始位置を示す。
     * position[ node ] は node のデータパタンの開始位置を示す。
     */
    private int[] position;

    /**
     * ノードの 分岐位置を示す。
     * level[ node ] は node の子ノードが分岐する位置を示す。
     */
    private int[] level;

    /**
     * ノードの子ノードの数を示す。
     * childnum[ node ] は node の子ノードの数を示す。
     */
    private int[] childnum;

    /**
     * next が構成する未使用ノードのスタックのスタックポインタ。
     */
    private int avail;

    /**
     * ハッシュ時に使用するシフト値
     */
    private int shift;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  private int lastMatchPos
    //  private int lastMatchLen
    //------------------------------------------------------------------
    /**
     * 最後の searchAndPut() または put() で得られた
     * 得られた PatriciaTrie内の最長一致位置
     */
    private int lastMatchPos;

    /**
     * 最後の searchAndPut() または put() で
     * 得られた PatriciaTrie内の最長一致長
     */
    private int lastMatchLen;


    //------------------------------------------------------------------
    //  constructer
    //------------------------------------------------------------------
    //  private PatriciaTreeSearch()
    //  public PatriciaTreeSearch( int DictionarySize, int MaxMatch,
    //                             int Threshold, byte[] TextBuffer )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private PatriciaTrieSearch(){   }

    /**
     * コンストラクタ。
     * PATRICIA Trie を使用した検索機構を構築する。
     * 
     * @param DictionarySize 辞書サイズ
     * @param MaxMatch       最長一致長
     * @param Threshold      圧縮、非圧縮の閾値
     * @param TextBuffer     LZSS圧縮を施すためのバッファ
     */
    public PatriciaTrieSearch( int    DictionarySize,
                               int    MaxMatch,
                               int    Threshold,
                               byte[] TextBuffer ){

        this.DictionarySize  = DictionarySize;
        this.MaxMatch        = MaxMatch;
        this.Threshold       = Threshold;
        this.TextBuffer      = TextBuffer;
        this.DictionaryLimit = this.DictionarySize;

        this.parent          = new int[ this.DictionarySize * 2 ];
        this.prev            = new int[ this.DictionarySize * 2 ];
        this.next            = new int[ this.DictionarySize * 2 ];
        this.position        = new int[ this.DictionarySize ];
        this.level           = new int[ this.DictionarySize ];
        this.childnum        = new int[ this.DictionarySize ];
        this.hashTable       = new int[ 
                PatriciaTrieSearch.generateProbablePrime( 
                        this.DictionarySize + ( this.DictionarySize >> 2 ) ) ];

        for( int i = 2 ; i < this.DictionarySize ; i++ ){
            this.next[i] = i - 1;
        }
        this.avail = this.DictionarySize - 1;

        for( int i = 0 ; i < this.DictionarySize * 2 ; i++ ){
            this.parent[ i ]    = PatriciaTrieSearch.UNUSED;
        }

        for( int i = 0 ; i < this.hashTable.length ; i++ ){
            this.hashTable[ i ] = PatriciaTrieSearch.UNUSED;
        }

        this.shift = Bits.len( this.DictionarySize ) - 8;

        this.lastMatchLen = 0;
        this.lastMatchPos = 0;
    }


    //------------------------------------------------------------------
    //  method of jp.gr.java_conf.dangan.util.lha.LzssSearchMethod
    //------------------------------------------------------------------
    //  public void put( int position )
    //  public int searchAndPut( int position )
    //  public int search( int position, int lastPutPos )
    //  public void slide( int slideWidth, int slideEnd )
    //  public int putRequires()
    //------------------------------------------------------------------
    /**
     * position から始まるデータパタンを
     * PATRICIA Trie に登録する。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置
     */
    public void put( int position ){

        //------------------------------------------------------------------
        //  PATRICIA Trie から最も古いデータパタンを削除
        int posnode = ( position & ( this.DictionarySize - 1 ) ) + this.DictionarySize;
        this.deleteNode( posnode );

        //------------------------------------------------------------------
        //  PATRICIA Trie から最長一致を検索
        int matchnode = -1;
        int matchpos  = position;
        int scannode;
        int matchlen;
        if( 3 < this.lastMatchLen ){

            //前回の一致長が閾値より大きければ、
            //葉から lastMatchLen - 1 の一致を検索する。
            scannode  = ( this.lastMatchPos + 1 ) | this.DictionarySize;

            //最長一致があったために scannode が 
            //PATRICIA Trie から取り除かれている場合の処理
            while( this.parent[ scannode ] == PatriciaTrieSearch.UNUSED ){
                scannode = this.next[ scannode ];
            }

            //葉から 順番に親へと辿って
            //lastMatchLen - 1 以下の level を持つノードを探す。
            int node  = this.parent[ scannode ];
            this.lastMatchLen--;
            while( 0 < node 
                && this.lastMatchLen <= this.level[ node ] ){
                scannode = node;
                node = this.parent[ node ];
            }

            //さらに親へと辿って position を更新していく。
            while( 0 < node  ){
                this.position[ node ] = position;
                node = this.parent[ node ];
            }

            matchlen  = this.lastMatchLen;
        }else{

            //PATRICIA Trie を 根から辿る。
            scannode  = this.child( this.TextBuffer[ position ] - 128, 
                                    this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen  = 2;

            if( scannode == PatriciaTrieSearch.UNUSED ){
                //根に position を追加する。
                this.attachNode( this.TextBuffer[ position ] - 128, posnode, 
                                 this.TextBuffer[ position + 1 ] & 0xFF );
                this.lastMatchLen = matchlen;
                return;
            }
        }

        while( true ){
            int max;
            if( scannode < this.DictionarySize ){
                max       = this.level[ scannode ];
                matchnode = scannode;
                matchpos  = this.position[ scannode ];
            }else{
                max       = this.MaxMatch;
                matchnode = scannode;
                matchpos  = ( position <= scannode
                            ? scannode - this.DictionarySize
                            : scannode );
            }

            while( matchlen < max
                && ( this.TextBuffer[ matchpos + matchlen ] 
                  == this.TextBuffer[ position + matchlen ] ) ){
                matchlen++;
            }

            if( matchlen == max && matchlen < this.MaxMatch ){
                this.position[ scannode ] = position;
                scannode = this.child( scannode, 
                                       this.TextBuffer[ position + matchlen ] & 0xFF );

                if( scannode == PatriciaTrieSearch.UNUSED ){
                    this.attachNode( matchnode, posnode, 
                                     this.TextBuffer[ position + matchlen ] & 0xFF );
                    break;
                }else{
                    matchlen++;
                }
            }else if( matchlen < max ){
                //matchnode と position を分岐させる。
                this.splitNode( matchnode, matchpos, posnode, position, matchlen );
                break;
            }else{
                //完全一致を発見、ノードを置き換える。
                this.replaceNode( matchnode, posnode );
                this.next[ matchnode ] = position;
                break;
            }
        }

        //検索結果を保存
        this.lastMatchLen = matchlen;
        this.lastMatchPos = matchpos;
    }

    /**
     * PATRICIA Trie に登録されたデータパタンから 
     * position から始まるデータパタンと
     * 最長の一致を持つものを検索し、
     * 同時に position から始まるデータパタンを 
     * PATRICIA Trie に登録する。<br>
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
        //  PATRICIA Trie から最も古いデータパタンを削除
        int posnode = ( position & ( this.DictionarySize - 1 ) ) + this.DictionarySize;
        this.deleteNode( posnode );

        //------------------------------------------------------------------
        //  PATRICIA Trie から最長一致を検索
        int matchnode = -1;
        int matchpos  = position;
        int scannode  = 0;
        int matchlen  = 0;
        if( 3 < this.lastMatchLen ){

            //前回の一致長が閾値より大きければ、
            //葉から lastMatchLen - 1 の一致を検索する。
            scannode  = ( this.lastMatchPos + 1 ) | this.DictionarySize;

            //最長一致があったために scannode が 
            //PATRICIA Trie から取り除かれている場合の処理
            while( this.parent[ scannode ] == PatriciaTrieSearch.UNUSED ){
                scannode = this.next[ scannode ];
            }

            //葉から 順番に親へと辿って
            //lastMatchLen - 1 以下の level を持つノードを探す。
            int node  = this.parent[ scannode ];
            this.lastMatchLen--;
            while( 0 < node 
                && this.lastMatchLen <= this.level[ node ] ){
                scannode = node;
                node = this.parent[ node ];
            }

            //さらに親へと辿って position を更新していく。
            while( 0 < node  ){
                this.position[ node ] = position;
                node = this.parent[ node ];
            }

            matchlen  = this.lastMatchLen;
        }else{
            //PATRICIA Trie を 根から辿る。
            scannode  = this.child( this.TextBuffer[ position ] - 128, 
                                    this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen  = 2;
        }

        // scannode == UNUSED となるのは lastMatchLen が閾値より小さいときのみ。
        if( scannode != PatriciaTrieSearch.UNUSED ){
            while( true ){
                int max;
                if( scannode < this.DictionarySize ){
                    max       = this.level[ scannode ];
                    matchnode = scannode;
                    matchpos  = this.position[ scannode ];
                }else{
                    max       = this.MaxMatch;
                    matchnode = scannode;
                    matchpos  = ( position <= scannode
                                ? scannode - this.DictionarySize
                                : scannode );
                }

                while( matchlen < max
                    && ( this.TextBuffer[ matchpos + matchlen ] 
                      == this.TextBuffer[ position + matchlen ] ) ){
                    matchlen++;
                }

                if( matchlen == max && matchlen < this.MaxMatch ){
                    this.position[ scannode ] = position;
                    scannode = this.child( scannode, 
                                           this.TextBuffer[ position + matchlen ] & 0xFF );

                    if( scannode == PatriciaTrieSearch.UNUSED ){
                        //matchnode に position を追加する。
                        this.attachNode( matchnode, posnode, 
                                         this.TextBuffer[ position + matchlen ] & 0xFF );
                        break;
                    }else{
                        matchlen++;
                    }
                }else if( matchlen < max ){
                    //matchnode と position を分岐させる。
                    this.splitNode( matchnode, matchpos, posnode, position, matchlen );
                    break;
                }else{
                    //完全一致を発見、ノードを置き換える。
                    this.replaceNode( matchnode, posnode );
                    this.next[ matchnode ] = position;
                    break;
                }
            }
        }else{ //if( scannode != PatriciaTrieSearch.UNUSED )
            //根に position を追加する。
            this.attachNode( this.TextBuffer[ position ] - 128, posnode, 
                             this.TextBuffer[ position + 1 ] & 0xFF );
            matchlen = 0;
        }

        //検索結果を保存
        this.lastMatchLen = matchlen;
        this.lastMatchPos = matchpos;


        //------------------------------------------------------------------
        //  メソッド先頭で PATRICIA Trie から削除したデータパタンもチェックする。
        scannode = position - this.DictionarySize;
        if( this.DictionaryLimit <= scannode ){
            int len = 0;
            while( this.TextBuffer[ scannode + len ]
                == this.TextBuffer[ position + len ] )
                if( this.MaxMatch <= ++len ) break;

            if( matchlen < len ){
                matchpos = scannode;
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
     * PATRICIA Trie に登録されたデータパタンを検索し
     * position から始まるデータパタンと
     * 最長の一致を持つものを得る。<br>
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
        //  PATRICIA Trie に登録されていないデータパタンを
        //  単純な逐次検索で検索する。
        int scanlimit = Math.max( this.DictionaryLimit, lastPutPos );
        int scanpos   = position - 1;
        int matchlen  = 0;
        int matchpos  = 0;

        byte[] buf    = this.TextBuffer;
        int max       = Math.min( this.TextBuffer.length,
                                  position + this.MaxMatch );
        int s         = 0;
        int p         = 0;
        int len       = 0;
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
        //  PATRICIA Trie を探索
        if( 2 < this.TextBuffer.length - position  ){
            int matchnode = this.child( this.TextBuffer[ position ] - 128, 
                                        this.TextBuffer[ position + 1 ] & 0xFF );
            scanlimit = Math.max( this.DictionaryLimit, 
                                  position - this.DictionarySize );
            len       = 2;
            while( matchnode != PatriciaTrieSearch.UNUSED ){
                int maxlen;
                if( matchnode < this.DictionarySize ){
                    maxlen  = this.level[ matchnode ];
                    scanpos = this.position[ matchnode ];
                }else{
                    maxlen  = this.MaxMatch;
                    scanpos = ( lastPutPos < matchnode
                              ? matchnode - this.DictionarySize
                              : matchnode );
                }

                if( scanlimit <= scanpos ){
                    max = Math.min( this.TextBuffer.length,
                                    position + maxlen );
                    s   = scanpos  + len;
                    p   = position + len;
                    if( p < max ){
                        while( buf[ s ] == buf[ p ] ){
                            s++;
                            p++;
                            if( max <= p ) break;
                        }
                    }

                    len = p - position;
                    if( matchlen < len ){
                        matchpos = scanpos;
                        matchlen = len;
                    }

                    if( len == maxlen && matchlen < this.MaxMatch ){
                        if( position + len < this.TextBuffer.length ){
                            matchnode = this.child( matchnode, 
                                                    this.TextBuffer[ position + len ] & 0xFF );

                            if( matchnode != PatriciaTrieSearch.UNUSED ){
                                len++;
                            }
                        }else{
                            break;
                        }
                    }else{  //maxlen に満たない一致が見つかったか 完全一致が見つかった
                        break;
                    }
                }else{ //if( scanlimit <= scanpos ) 一致したパタンは検索限界を超えていた。
                    break;
                }
            }   //while( matchnode != PatriciaTrieSearch.UNUSED )
        }   //if( 2 <= this.TextBuffer.length - position  )


        //------------------------------------------------------------------
        //  最長一致を呼び出し元に返す。
        if( this.Threshold <= matchlen ){
            return LzssOutputStream.createSearchReturn( matchlen, matchpos );
        }else{
            return LzssOutputStream.NOMATCH;
        }
    }

    /**
     * TextBuffer内のpositionまでのデータを
     * 前方へ移動する際、それに応じて LzssSearchMethod
     * 内のデータも TextBuffer内のデータと矛盾しないよ
     * うに前方へ移動する処理を行う。 
     */
    public void slide(){
        this.DictionaryLimit = Math.max( 0, this.DictionaryLimit - this.DictionarySize );
        this.lastMatchPos   -= this.DictionarySize;

        for( int i = 0 ; i < this.position.length ; i++ ){
            int pos = this.position[i] - this.DictionarySize;
            if( 0 < pos ){
                this.position[i] = pos;
            }else{
                this.position[i] = 0;
            }
        }
    }

    /**
     * put() で LzssSearchMethodにデータを
     * 登録するときに使用されるデータ量を得る。
     * PatriciaTrieSearch では、常に MaxMatch を返す。
     * 
     * @return 常に MaxMatch
     */
    public int putRequires(){
        return this.MaxMatch;
    } 

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  manipulate node
    //------------------------------------------------------------------
    //  private void splitNode( int oldnode, int oldpos, int position, int splitLen )
    //  private void deleteNode( int node )
    //  private void attatchNode( int parentnode, int childnode, int pos )
    //  private void replaceNode( int oldnode, int newnode )
    //  private void contractNode( int node )
    //------------------------------------------------------------------
    /**
     * oldnode を splitLen で分岐させる。
     * oldnode のあった位置には新しいノードが新設され、
     * 新しいノードは oldnode と position を子に持つ。
     * 
     * @param oldnode  分岐させるノード 
     * @param oldpos   oldnode が指すデータパタンの開始位置
     * @param posnode  position 用ノード
     * @param position TextBuffer 内のデータパタンの開始位置
     * @param splitLen データパタン内の分岐位置
     */
    private void splitNode( int oldnode, int oldpos, int posnode, int position, int splitLen ){
        //スタックから 新しいノードを取得する。
        int newnode = this.avail;
        this.avail  = this.next[ newnode ];

        this.replaceNode( oldnode, newnode );
        this.level[ newnode ]     = splitLen;
        this.position[ newnode ]  = position;
        this.childnum[ newnode ]  = 0;

        this.attachNode( newnode, oldnode,  
                         this.TextBuffer[ oldpos   + splitLen ] & 0xFF );
        this.attachNode( newnode, posnode, 
                         this.TextBuffer[ position + splitLen ] & 0xFF );
    }


    /**
     * PATRICIA Trie から葉である node を削除する。
     * 必要であれば node の親ノードの繰上げ処理も行う。
     * 
     * @param node 削除する葉ノード
     */
    private void deleteNode( int node ){
        if( this.parent[ node ] != PatriciaTrieSearch.UNUSED ){
            int parent = this.parent[ node ];
            int prev   = this.prev[ node ];
            int next   = this.next[ node ];

            this.parent[ node ] = PatriciaTrieSearch.UNUSED;
            this.prev[ node ]   = PatriciaTrieSearch.UNUSED;
            this.next[ node ]   = PatriciaTrieSearch.UNUSED;

            if( 0 <= prev ){
                this.next[ prev ]       = next;
            }else{
                this.hashTable[ ~prev ] = next;
            }
            this.prev[ next ] = prev;

            if( 0 < parent ){ //parent が PATRICIA Trie の根で無い場合 true となる条件式
                this.childnum[ parent ]--;

                if( this.childnum[ parent ] <= 1 ){
                    this.contractNode( this.child( parent,
                                        this.TextBuffer[ this.position[ parent ]
                                                        + this.level[ parent ] ]
                                        & 0xFF ) );
                }
            }
        }
    }

    /**
     * parentnode に childnode を追加する。
     * 
     * @param parentnode childnode を追加する対象の親ノード
     * @param childnode  parentnode に追加するノード
     * @param pos        TextBuffer内現在処理位置。
     *                   葉の position を確定するために使用される。
     */
    private void attachNode( int parentnode, int childnode, int ch ){
        int hash                 = this.hash( parentnode, ch );
        int node                 = this.hashTable[ hash ];
        this.hashTable[ hash ]   = childnode;
        this.parent[ childnode ] = parentnode;
        this.prev[ childnode ]   = ~hash;
        this.next[ childnode ]   = node;
        this.prev[ node ]        = childnode;

        if( 0 < parentnode ){
            this.childnum[ parentnode ]++;
        }
    }

    /**
     * oldnode と newnode を入れ替える。
     * newnode は子ノードとの関係を保持する。
     * oldnode は置き換えられて PATRICIA Trie から取り除かれる。
     * 
     * @param oldnode 入れ替えられて Trie から削除されるノード
     * @param newnode oldnode のあった位置へ接続されるノード
     */
    private void replaceNode( int oldnode, int newnode ){
        this.parent[ newnode ]   = this.parent[ oldnode ];
        this.prev[ newnode ]     = this.prev[ oldnode ];
        this.next[ newnode ]     = this.next[ oldnode ];

        this.prev[ this.next[ newnode ] ] = newnode;

        if( this.prev[ newnode ] < 0 ){
            this.hashTable[ ~this.prev[ newnode ] ] = newnode;
        }else{
            this.next[ this.prev[ newnode ] ]       = newnode;
        }

        this.parent[ oldnode ] = PatriciaTrieSearch.UNUSED;
        this.prev[ oldnode ]   = PatriciaTrieSearch.UNUSED;
        this.next[ oldnode ]   = PatriciaTrieSearch.UNUSED;
    }

    /**
     * 兄弟の無くなった node を引き上げる。
     * node の親ノードは PATRICIA Trie から削除され、
     * 代わりに node がその位置に接続される。
     * 兄弟が無いかどうかの 判定は呼び出し側が行う。
     * 
     * @param node 引き上げるノード
     */
    private void contractNode( int node ){
        int parentnode    = this.parent[ node ];

        this.prev[ this.next[ node ] ] = this.prev[ node ];
        if( 0 <= this.prev[ node ] ){
            this.next[ this.prev[ node ] ]        = this.next[ node ];
        }else{
            this.hashTable[ ~ this.prev[ node ] ] = this.next[ node ];
        }
        this.replaceNode( parentnode, node );

        //使用されなくなった parentnode をスタックに返還する。
        this.next[ parentnode ] = this.avail;
        this.avail              = parentnode;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void slideTree( int[] src, int[] dst, int width )
    //  private int child( int parent, int ch )
    //  private int hash( int node, int ch )
    //------------------------------------------------------------------
    /**
     * slide 時に Trie の各要素を移動させる処理を行う。
     * 
     * @param src   移動元
     * @param dst   移動先
     * @param width 移動幅
     */
    private void slideTree( int[] src, int[] dst, int width ){
        for( int i = 0 ; i < this.DictionarySize ; i++ )
            dst[i] = ( src[ i ] < this.DictionarySize
                     ? src[ i ]
                     : ( ( src[ i ] - width ) & ( this.DictionarySize - 1 ) ) 
                         + this.DictionarySize );

        for( int i = this.DictionarySize ; i < src.length ; i++  )
            dst[ ( ( i - width ) & ( this.DictionarySize - 1 ) ) 
                 + this.DictionarySize ] = ( src[ i ] < this.DictionarySize
                                           ? src[ i ]
                                           : ( ( src[ i ] - width ) 
                                               & ( this.DictionarySize - 1 ) )
                                             + this.DictionarySize );
    }

    /**
     * parent から ch で分岐した子を得る。
     * ノードが無い場合は UNUSED を返す。
     * 
     * @param parent 親ノード
     * @param ch     分岐文字
     * 
     * @return 子ノード
     */
    private int child( int parent, int ch ){
        int node = this.hashTable[ this.hash( parent, ch ) ];

        //this.parent[ PatriciaTrieSearch.UNUSED ] = parent;
        while( node != PatriciaTrieSearch.UNUSED
            && this.parent[ node ] != parent ){
            node = this.next[ node ];
        }

        return node;
    }

    /**
     * node と ch から ハッシュ値を得る
     * 
     * @param node ノード
     * @param ch   分岐文字
     * 
     * @return ハッシュ値
     */
    private int hash( int node, int ch ){
        return ( node + ( ch << this.shift ) + 256 ) % this.hashTable.length;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  generate prime
    //------------------------------------------------------------------
    //  private static int generateProbablePrime( int num )
    //------------------------------------------------------------------
    /**
     * num 以上の最も小さい 素数(もしくは擬似素数)を生成する。 
     * 戻り値が 素数でない確率は 1/256 以下である。
     * 
     * @param num この値以上の素数を生成する。
     *
     * @return 生成された素数(もしくは擬似素数)
     */
    private static int generateProbablePrime( int num ){
        num = num + ( ( num & 1 ) == 0 ? 1 : 0 );

        while( !(new BigInteger(Integer.toString(num))).isProbablePrime( 8 ) ){
            num += 2;
            num = num + ( ( num % 3 ) == 0 ? 2 : 0 );
            num = num + ( ( num % 5 ) == 0 ? 2 : 0 );
            num = num + ( ( num % 7 ) == 0 ? 2 : 0 );
        }
        return num;
    }

}

// 
//  
//  //------------------------------------------------------------------
//  //  local method
//  //------------------------------------------------------------------
//  //  check
//  //------------------------------------------------------------------
//  //  private void checkTrie( int pos )
//  //  private void checkNode( int node, int pos )
//  //  private void writeNode( int node )
//  //------------------------------------------------------------------
//  /**
//   * Trie全体のチェックを行う。
//   * 
//   * @param pos 現在処理位置。
//   * 
//   * @exception RuntimeException Trie が崩れていた場合。
//   */
//  private void checkTrie( int pos ){
//      for( int i = -256 ; i < 0 ; i++ ){
//          this.checkNode( i, pos );
//      }
//
//      for( int i = 1 ; i < this.DictionarySize ; i++ ){
//          if( this.parent[ i ] != PatriciaTrieSearch.UNUSED ){
//              this.checkNode( i, pos );
//          }
//      }
//  }
//
//  /**
//   * 葉でない Node のチェックを行う。
//   * 
//   * チェック項目は
//   * (1) 親子関係
//   * (2) position に矛盾が無い事。
//   * (3) level に矛盾が無い事。
//   * (4) node が this.childnum[node] 個の子供を持っている事。
//   * の4項目。
//   * 
//   * @param node チェックするノード
//   * @param pos  現在処理位置
//   * 
//   * @exception RuntimeException 上記のチェックの何れかが失敗した場合。
//   */
//  private void checkNode( int node, int pos ){
//
//      int nlevel;
//      int npos;
//      if( node < 0 ){
//          nlevel = 0;
//          npos   = this.TextBuffer.length;
//      }else{
//          nlevel = this.level[ node ];
//          npos   = this.position[ node ];
//      }
//
//      int childcount = 0;
//      for( int i = 0 ; i < 256 ; i++ ){
//          int child = this.child( node, i );
//
//          if( child != PatriciaTrieSearch.UNUSED ){
//              childcount++;
//
//              if( this.parent[ child ] != node ){
//                  System.out.println( "unlink::parent<->child" );
//                  this.writeNode( node );
//                  this.writeNode( child );
//                  throw new RuntimeException( "Trie Broken" );
//              }
//
//              if( child < this.DictionarySize ){
//                  if( this.level[ child ] <= nlevel ){
//                      System.out.println( "broken hierarchy::level" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//
//                  if( npos < this.position[ child ] ){
//                      System.out.println( "broken hierarchy::position" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//                  //this.checkTrie( child, pos );
//              }else{
//                  int childpos = ( pos <= child ? child - this.DictionarySize : child );
//                  if( npos < childpos ){
//                      System.out.println( "broken hierarchy::position" );
//                      this.writeNode( node );
//                      this.writeNode( child );
//                      throw new RuntimeException( "Trie Broken" );
//                  }
//              }
//          }
//      }
//
//      if( 0 < node && node < this.DictionarySize ){
//          if( this.childnum[ node ] != childcount ){
//              System.out.println( "broken hierarchy::childnum" );
//              this.writeNode( node );
//              throw new RuntimeException( "Trie Broken" );
//          }
//      }
//  }
//
//  /**
//   * ノードの情報を出力する。
//   * 
//   * @param node 情報を出力するノード
//   */
//  private void writeNode( int node ){
//      if( 0 < node ){
//        System.out.println( "this.parent[" + node + "]  ::" + this.parent[ node ] );
//        System.out.println( "this.prev[" + node + "]    ::" + this.prev[ node ] );
//        System.out.println( "this.next[" + node + "]    ::" + this.next[ node ] );
//        if( node < this.DictionarySize ){
//            System.out.println( "this.childnum[" + node + "]::" + this.childnum[ node ] );
//            System.out.println( "this.position[" + node + "]::" + this.position[ node ] );
//            System.out.println( "this.level[" + node + "]   ::" + this.level[ node ] );
//        }
//    }else if( node < 0 ){
//        System.out.println( "ROOT_NODE                  ::" + node );
//    }else{
//        System.out.println( "UNUSED                     ::" + node );
//    }
//      
//  }

//end of PatriciaTrieSearch.java
