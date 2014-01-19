//start of LhaChecksum.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaChecksum.java
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
import java.util.zip.Checksum;

//import exceptions

/**
 * LHAで使用される 単純な 1バイトのチェックサム値を
 * 算出するためのクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: LhaChecksum.java,v $
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
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
public class LhaChecksum implements Checksum{


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int checksum
    //------------------------------------------------------------------
    /** 
     * チェックサム値
     */
    private int checksum;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public LhaChecksum()
    //------------------------------------------------------------------
    /**
     * 新しい チェックサムクラスを作成する。
     */
    public LhaChecksum(){
        super();
        this.reset();
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum method
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * byte8 で指定した 1バイトのデータで チェックサム値を更新する。
     *
     * @param byte8 チェックサムを更新する1バイトのデータ
     */
    public void update( int byte8 ){
        this.checksum += byte8;
    }

    /**
     * buffer で指定したバイト配列で チェックサム値を更新する。
     * このメソッドは
     *   update( buffer, 0, buffer.length ) 
     * と同等。
     * 
     * @param buffer チェックサムを更新するデータを持つバイト配列
     */
    public void update( byte[] buffer ){
        this.update( buffer, 0, buffer.length );
    }

    /**
     * buffer で指定したバイト配列で チェックサム値を更新する。
     * 
     * @param buffer チェックサムを更新するデータを持つバイト配列
     * @param index  データの開始位置
     * @param length チェックサムの更新に使うバイト数
     */
    public void update( byte[] buffer, int index, int length ){
        while( 0 < length-- )
            this.checksum += buffer[index++];
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  other
    //------------------------------------------------------------------
    //  public void reset()
    //  public long getValue()
    //------------------------------------------------------------------
    /**
     * チェックサム値を初期値に設定しなおす。
     */
    public void reset(){
        this.checksum = 0;
    }

    /**
     * チェックサム値を得る。
     * チェックサム値は 1バイトの値であり、 
     * 0x00～0xFFにマップされる。
     * 
     * @return チェックサム値
     */
    public long getValue(){
        return this.checksum & 0xFF;
    }

}
//end of LhaChecksum.java
