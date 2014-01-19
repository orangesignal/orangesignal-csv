//start of MsdosDate.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * MsdosDate.java
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

package jp.gr.java_conf.dangan.util;

//import classes and interfaces
import java.util.Date;
import java.lang.Cloneable;

//import exceptions
import java.lang.IllegalArgumentException;

/**
 * MS-DOS形式の時間情報を扱うDateの派生クラス。<br>
 * データは 4byte値であり、MS-DOSが 主にIntel の x86系CPU上で
 * 動作したことから LittleEndianで格納される。<br>
 * フォーマットは以下のとおり。<br>
 * <pre>
 * +---------------+---------------++---------------+---------------+
 * | 日付-上位byte | 日付-下位byte || 時刻-上位byte | 時刻-下位byte |
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0||7|6|5|4|3|2|1|0|7|6|5|4|3|2|1|0|
 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-++-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * |   年-7bit   |月-4bit| 日-5bit ||時間-5bit|  分-6bit  | 秒-5bit |
 * +-------------+-------+---------++---------+-----------+---------+
 * </pre>
 * ・年は 1980～2107年 を 0～127 で表す。WindowsのシステムAPIの
 *   一部は 2099年までしかサポートしないという情報がある。<br>
 * ・月は 1～12月 を 1～12で表す。0～11でないことに注意。<br>
 * ・日は 1～31日 を 1～31で表す。0～30でないことに注意。<br>
 * ・時間は 0～23時 を 0～23で表す。<br>
 * ・分は 0～59分 を 0～59で表す。<br>
 * ・秒は 0～58秒 を 0～29で表す。秒の情報はビット数が足りない
 *   ため 最小単位は 1秒でなく 2秒である。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: MsdosDate.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     javadoc コメントのスペルミスを修正。
 *     ソース整備
 *
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [bug fix]
 *     setTime() で ftimeの限界精度である2秒以上の精度で記録していた。
 * [maintenance]
 *     タブの廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class MsdosDate extends Date
                       implements Cloneable {


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public MsdosDate( Date date )
    //  public MsdosDate( int time )
    //------------------------------------------------------------------
    /**
     * date で示される時間を表す MsdosDate を構築する。 <br>
     * MS-DOS 形式の時間情報で表せない細かい精度の情報は
     * 無視され、最小時間単位は java.util.Date の 1ミリ秒でなく
     * MS-DOS 形式の時間情報 の最小単位である 2秒となる。
     * 
     * @param date 新しく構築される MsdosDate の基になる時間情報
     *             を持つ Dateオブジェクト
     * 
     * @exception IllegalArgumentException
     *             date が MS-DOS時間形式で扱えない範囲の時間を
     *             示していた場合
     */
    public MsdosDate( Date date ){
        super( ( date.getTime() / 2000L ) * 2000L  );
        this.checkRange();
    }

    /**
     * MS-DOS 形式の時間情報から 新しい MsdosDate を構築
     * する。
     * 
     * @param time MS-DOS 形式の時間情報
     */
    public MsdosDate( int time ){
        super( ( ( time >> 25 ) & 0x7F ) + 80,
               ( ( time >> 21 ) & 0x0F ) - 1,
               ( time >> 16 ) & 0x1F,
               ( time >> 11 ) & 0x1F,
               ( time >> 5 )  & 0x3F,
               ( time << 1 )  & 0x3F );                                         //deprecated

        this.checkRange();
    }


    //------------------------------------------------------------------
    //  method of java.lang.Cloneable
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * このオブジェクトのコピーを返す。
     * 
     * @return このMsdosDateオブジェクトの複製
     */
    public Object clone(){
        return new MsdosDate( this );
    }


    //------------------------------------------------------------------
    //  method of java.util.Date
    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  set method with range check
    //------------------------------------------------------------------
    //  public void setYear( int year )
    //  public void setTime( long time )
    //------------------------------------------------------------------
    /**
     * この MsdosDate の示す年を year で指定された値に1900を足し
     * たものに設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @deprecated
     * @param year 1900を足すことで西暦を表すような 年の値
     * 
     * @exception IllegalArgumentException
     *             year が MS-DOS時間形式で扱えない範囲の時間を
     *             示していた場合
     */
    public void setYear( int year ){
        if( year < 80 || 207 < year ){
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
        }else{
            super.setYear( year );                                              //deprecated
        }
    }

    /**
     * この MsdosDate の示す時間を 1970年1月1日 00:00:00 GMTから
     * time ミリ秒経過した時刻に設定する。<br>
     * MS-DOS 形式の時間情報で表せない細かい精度の情報は
     * 無視され、最小時間単位は java.util.Date の 1ミリ秒でなく
     * MS-DOS 形式の時間情報 の最小単位である 2秒となる。
     * 
     * @param time 1970年1月1日 00:00:00GMT からの経過ミリ秒
     * 
     * @exception IllegalArgumentException
     *             time が MS-DOS時間形式で扱えない範囲の時間を
     *             示していた場合
     */
    public void setTime( long time ){
        int year = ( new Date( time ) ).getYear();
        if( year < 80 || 207 < year ){
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
        }else{
            super.setTime( ( time / 2000L ) * 2000L );
        }
    }


    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access method of MS-DOS time format
    //------------------------------------------------------------------
    //  public void setMsdosTime( int time )
    //  public int getMsdosTime()
    //------------------------------------------------------------------
    /**
     * この MsdosDate に MS-DOS 時間形式の時間情報を設定する。
     * 
     * @param time MS-DOS 時間形式の時間情報
     */
    public void setMsdosTime( int time ){
        Date date = new Date( ( ( time >> 25 ) & 0x7F ) + 80,
                              ( ( time >> 21 ) & 0x0F ) - 1,
                              ( time >> 16 ) & 0x1F,
                              ( time >> 11 ) & 0x1F,
                              ( time >> 5 )  & 0x3F,
                              ( time << 1 )  & 0x3F );                          //deprecated

        this.setTime( date.getTime() );
    }

    /**
     * この MsdosDateが示す時間情報を MS-DOS 時間形式で得る。
     * 
     * @return MS-DOS時間形式の値
     */
    public int getMsdosTime(){
        return ( ( super.getYear() - 80 ) << 25 )                               //deprecated
               | ( ( super.getMonth() + 1 ) << 21 )                             //deprecated
               | ( super.getDate()    << 16 )                                   //deprecated
               | ( super.getHours()   << 11 )                                   //deprecated
               | ( super.getMinutes() <<  5 )                                   //deprecated
               | ( super.getSeconds() >>  1 );                                  //deprecated
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void checkRange()
    //------------------------------------------------------------------
    /**
     * この MsdosDate が MS-DOS時間形式で表せる時間の範囲内で
     * あるかを判定する。
     * 
     * @exception IllegalArgumentException
     *             この MsdosDate が MS-DOS時間形式で扱えない
     *             範囲の時間を示していた場合
     */
    private void checkRange(){
        int year = this.getYear();
        if( year < 80 || 207 < year )
            throw new IllegalArgumentException( "out of MS-DOS time format range." );
    }

}
//end of MsdosDate.java
