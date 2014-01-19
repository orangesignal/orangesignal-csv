//start of BadHuffmanTableException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * BadHuffmanTableException.java
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

//import exceptions
import java.io.IOException;

/**
 * BlockHuffman.LenListToCodeList() 内で、
 * 渡された LenList ( ハフマン符号長の表 )が不正なため、
 * ハフマン符号を生成できない事を示す。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BadHuffmanTableException.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class BadHuffmanTableException extends IOException{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public BadHuffmanTableException()
    //  public BadHuffmanTableException( String message )
    //------------------------------------------------------------------
    /**
     * 新しい BadHuffmanTableException を構築する。
     */
    public BadHuffmanTableException(){
        super();
    }

    /**
     * 新しい BadHuffmanTableException を構築する。
     *
     * @param message 詳細なメッセージ
     */
    public BadHuffmanTableException( String message ){
        super( message );
    }
}
//end of BadHuffmanTableException.java
