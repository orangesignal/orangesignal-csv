//start of LzssSearchMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LzssSearchMethod.java
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

//import exceptions


/**
 * LzssOutputStream で使用される
 * 最長一致検索を提供するインターフェイス。<br> 
 * 
 * <br>
 * コンストラクタの形式は
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer )
 * 
 * <strong>パラメータ:</strong>
 *   DictionarySize - LZSSの辞書サイズ
 *   MaxMatch       - LZSSの最大一致長
 *   Threshold      - LZSSの圧縮/非圧縮の閾値
 *   TextBuffer     - LZSS圧縮を施すデータの入ったバッファ
 * </pre>
 * のような形式に則ること。<br>
 * また、追加の引数をとりたい場合は
 * <pre>
 * LzssSearchMethod( int    DictionarySize,
 *                   int    MaxMatch,
 *                   int    Threshold,
 *                   byte[] TextBuffer,
 *                   Object ExtraArgument1,
 *                   Object ExtraArgument2 )
 * </pre>
 * のような形式を用いる。<br>
 * なお、コンストラクタの引数チェックは追加の引数がある場合について行えばよい。
 * <br>
 * 
 * <pre>
 * -- revision history --
 * $Log: LzssSearchMethod.java,v $
 * Revision 1.1  2002/12/04 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     slide() で引数を取らずに 
 *     スライド幅を常に DictionarySize とするように変更。
 *     putLength を putRequires に変更
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public interface LzssSearchMethod{

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract void put( int position )
    //  public abstract int searchAndPut( int position )
    //  public abstract int search( int position, int lastPutPos )
    //  public abstract void slide()
    //  public abstract int putRequires()
    //------------------------------------------------------------------
    /**
     * position から始まるデータパタンを 
     * LzssSearchMethod の持つ検索機構に登録する。<br>
     * LzssOutputStream は 線形に、重複無く、
     * put または searchAndPut を呼び出す。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置
     */
    public abstract void put( int position );

    /**
     * 検索機構に登録されたデータパタンから
     * position から始まるデータパタンと
     * 最長の一致を持つものを検索し、
     * 同時に position から始まるデータパタンを 
     * LzssSearchMethod の持つ検索機構に登録する。<br>
     * LzssOutputStream は 線形に、重複無く、
     * put または searchAndPut を呼び出す。<br>
     * 
     * @param position TextBuffer内のデータパタンの開始位置
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
    public abstract int searchAndPut( int position );

    /**
     * 検索機構に登録されたデータパタンから
     * position から始まるデータパタンと
     * 最長の一致を持つものを検索する。<br>
     * このメソッドは LzssOutputStream の 
     * flush() を実装するためだけに提供される。<br>
     * TextBuffer.length &lt position + MaxMatch となるような 
     * position にも対応すること。
     * 
     * @param position   TextBuffer内のデータパタンの開始位置
     * @param lastPutPos 最後に登録したデータパタンの開始位置
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
    public abstract int search( int position, int lastPutPos );

    /**
     * LzssOutputStream が slide() でTextBuffer内のデータを
     * DictionarySize だけ移動させる際に検索機構内のデータを
     * それらと矛盾無く移動させる処理を行う。
     */
    public abstract void slide();

    /**
     * put() または searchAndPut() を使用して
     * データパタンを検索機構に登録する時に
     * 必要とするデータ量を得る。<br>
     * 
     * @return put() または searchAndPut() で
     *         検索機構に登録するデータ量
     */
    public abstract int putRequires();

}
//end of LzssSearchMethod.java
