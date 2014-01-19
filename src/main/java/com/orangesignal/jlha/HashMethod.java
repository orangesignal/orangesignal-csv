//start of HashMethod.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * HashMethod.java
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

package com.orangesignal.jlha;

//import classes and interfaces

//import exceptions


/**
 * ハッシュ関数を提供するインターフェイス。<br>
 * <br>
 * コンストラクタの形式は
 * <pre>
 * HashMethod( byte[] TextBuffer )
 * 
 * <strong>パラメータ:</strong>
 *   TextBuffer     - LZSS圧縮を施すデータの入ったバッファ
 * </pre>
 * のような形式に則ること。<br>
 * また、追加の引数をとりたい場合は
 * <pre>
 * HashMethod( byte[] TextBuffer,
 *             Object ExtraData1,
 *             Object ExtraData2 )
 * </pre>
 * のような形式を用いる。<br>
 * なお、コンストラクタの引数チェックは追加の引数がある場合について行えばよい。
 * 
 * <pre>
 * -- revision history --
 * $Log: HashMethod.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version cotrol
 * [change]
 *     requiredSize() を hashRequires() に名前変更。
 *     size() を tableSize() 名前変更。
 * [maintanance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface HashMethod{


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  public abstract int hash( int position )
    //  public abstract int hashRequires()
    //  public abstract int tableSize()
    //------------------------------------------------------------------
    /**
     * ハッシュ関数。
     * コンストラクタで渡された TextBuffer 内の
     * position からのデータパタンの hash値を生成する。
     *
     * @param position データパタンの開始位置
     * 
     * @return ハッシュ値
     */
    public abstract int hash( int position );

    /**
     * ハッシュ関数が
     * ハッシュ値を生成するために使用するバイト数を得る。
     * 
     * @return ハッシュ関数がハッシュ値を
     *         生成するために使用するバイト数
     */
    public abstract int hashRequires();

    /**
     * この HashMethod を使った場合の 
     * HashTable のサイズを得る。
     * 
     * @return この HashMethod を使った場合の HashTable のサイズ
     */
    public abstract int tableSize();

}
//end of HashMethod.java
