//start of NotEnoughBitsException.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * NotEnoughBitsException.java
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

package jp.gr.java_conf.dangan.io;

//import classes and interfaces

//import exceptions
import java.io.IOException;

/**
 * 要求されたビット数のデータを得られなかった場合に
 * 投げられる例外。<br>
 * BitDataBrokenException と違い、こちらの例外を
 * 投げる場合には 実際には読み込み動作を行ってい
 * ないため、読み込み位置は例外を投げる前の時点と
 * 同じである点に注意すること。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: NotEnoughBitsException.java,v $
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
public class NotEnoughBitsException extends IOException{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int availableBits
    //------------------------------------------------------------------
    /**
     * 実際に読み込めるビット数
     */
    private int availableBits;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private NotEnoughBitsException()
    //  public NotEnoughBitsException( int availableBits )
    //  public NotEnoughBitsException( String message, int availableBits )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private NotEnoughBitsException(){ }

    /**
     * availableBits 使用可能であることを示す
     * NotEnoughBitsException を構築する。
     * 
     * @param availableBits 使用可能なビット数
     */
    public NotEnoughBitsException( int availableBits ){
        super();
        this.availableBits = availableBits;
    }

    /**
     * availableBits 使用可能であることを示し、
     * 詳細なメッセージを持つ
     * NotEnoughBitsException を構築する。
     * 
     * @param message       詳細なメッセージ
     * @param availableBits 使用可能なビット数
     */
    public NotEnoughBitsException( String message, int availableBits ){
        super( message );
        this.availableBits = availableBits;
    }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public int getAvailableBits()
    //------------------------------------------------------------------
    /**
     * 使用可能なビット数を得る。<br>
     * この例外を投げたメソッドにおいて、現在使用可能なビット数を返す。<br>
     * 
     * @return 使用可能なビット数
     */
    public int getAvailableBits(){
        return this.availableBits;
    }

}
//end of NotEnoughBitsException.java
