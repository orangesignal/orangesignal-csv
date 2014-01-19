//start of WindowsDate.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * WindowsDate.java
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

package jp.gr.java_conf.dangan.util;

//import classes and interfaces
import java.util.Date;
import java.lang.Cloneable;

//import exceptions
import java.lang.IllegalArgumentException;

/**
 * WindowsのFILETIME形式の情報を扱うDateの派生クラス。<br>
 * FILETIME は 1601年 1月 1日 0時0分0秒からの経過時間を
 * 100ナノ秒単位で持つ64ビット値。<br>
 * このクラスでは FILETIME を long(64ビット値)として扱うときは
 * 基本的に符号無しとみなす。<br>
 * 1601年 1月 1日 0時0分0秒以前の時間を扱いたい場合は
 * WindowsDate( Date date ) か、WindowsDate.setTime( long time )を使用する。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: WindowsDate.java,v $
 * Revision 1.1  2002/12/05 00:00:00  dangan
 * [maintenance]
 *     javadoc コメントのスペルミスを修正。
 *     ソース整備
 *
 * Revision 1.0  2002/08/05 00:00:00  dangan
 * add to version control
 * [bug fix] 
 *     set系メソッドで 範囲外の時間をセットしようとして
 *     例外を投げるケースで時間の書き戻しが正しく行われていなかった。
 *     checkRange の時間の範囲が間違っていた。
 * [maintenance]
 *     タブの廃止
 *     ライセンス文の修正
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.1 $
 */
public class WindowsDate extends Date
                         implements Cloneable{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final long TIME_DIFFERENCE
    //------------------------------------------------------------------
    /**
     * FILETIME形式のデータと、java.util.Date.getTime() で
     * 得られる時間形式との時間差を 100ナノセカンド単位で示した数値。
     * なお、閏秒等は考慮に入れていない。
     */
    public static final long TIME_DIFFERENCE = 0x19DB1DED53E8000L;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int NanoSecounds
    //------------------------------------------------------------------
    /**
     * java.util.Date では保持できない 
     * ナノ秒単位の時間を保持するために用いる。
     */
    private int NanoSecounds;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public WindowsDate()
    //  public WindowsDate( Date date )
    //  public WindowsDate( long time )
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 現在の時間情報を持つ WindowsDateを構築する。
     * ナノ秒単位の時間は取得できないため、0に設定される。
     * 
     * @exception IllegalArgumentException
     *                  現在の時間が FILETIME 形式で表現できる
     *                  範囲外だった場合。
     */
    public WindowsDate(){
        super();
        this.NanoSecounds = 0;

        this.checkRange();
    }

    /**
     * dateで示される時間を表す WindowsDateを構築する。<br>
     * dateが WindowsDate のインスタンスならば
     * ナノ秒単位の情報もコピーされるが、それ以外の場合は
     * ナノ秒単位の情報には 0 が設定される。
     * 
     * @param date 新しく構築される WindowsDate の元となる時間情報を持つ 
     *             Date のオブジェクト
     * 
     * @exception IllegalArgumentException
     *                  現在の時間が FILETIME 形式で表現できる
     *                  範囲外だった場合。
     */
    public WindowsDate( Date date ){
        super( date.getTime() );
        if( date instanceof WindowsDate ){
            this.NanoSecounds = ((WindowsDate)date).NanoSecounds;
        }else{
            this.NanoSecounds = 0;
            this.checkRange();
        }
    }

    /**
     * 符号無し64ビットのFILETIME形式の時間情報から
     * 新しいWindowsDateを構築する。<br>
     * 
     * @param time FILETIME形式の時間情報
     */
    public WindowsDate( long time ){
        super( 0 <= time 
         ? ( time - WindowsDate.TIME_DIFFERENCE ) / 10000L
         : ( ( time >>> 1 ) - ( WindowsDate.TIME_DIFFERENCE >>> 1 ) ) / 5000L );

        this.NanoSecounds = 
                 (int)( ( time >>> 1 ) % 5000L * 2 + ( time & 1 ) ) * 100;
    }


    //------------------------------------------------------------------
    //  method of java.lang.Cloneable
    //------------------------------------------------------------------
    //  public Object clone()
    //------------------------------------------------------------------
    /**
     * このオブジェクトのコピーを返す。
     * 
     * @return このWindowsDateオブジェクトの複製
     */
    public Object clone(){
        return new WindowsDate( this );
    }


    //------------------------------------------------------------------
    //  method of java.util.Date
    //------------------------------------------------------------------
    //  set method with range check
    //------------------------------------------------------------------
    //  public void setYear( int year )
    //  public void setMonth( int month )
    //  public void setDate( int day )
    //  public void setHours( int hour )
    //  public void setMinutes( int minute )
    //  public void setSecounds( int secound )
    //  public void setTime( long time )
    //------------------------------------------------------------------
    /**
     * この WindowsDate の示す年を year で
     * 指定された値に1900を足したものに設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param year 1900を足すことで西暦を表すような 年の値
     * 
     * @exception IllegalArgumentException
     *             year に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setYear( int year ){
        long temp = this.getTime();

        try{
            super.setYear( year );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す月を month で指定された値に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param month 0が1月、1が2月を示すような月の値
     * 
     * @exception IllegalArgumentException
     *             month に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setMonth( int month ){
        long temp = this.getTime();

        try{
            super.setMonth( month );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す 一ヶ月の
     * 中での何日目かを date で指定された値に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param date 1が1日、2が2日を示すような日の値
     * 
     * @exception IllegalArgumentException
     *             date に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setDate( int date ){
        long temp = this.getTime();

        try{
            super.setDate( date );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す一日の中での時間を
     * hours で指定された値に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param hours 時間の値
     * 
     * @exception IllegalArgumentException
     *             hours に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setHours( int hours ){
        long temp = this.getTime();

        try{
            super.setHours( hours );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す一時間の中での分を
     * minutes で指定された値に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param minutes 分の値
     * 
     * @exception IllegalArgumentException
     *             minutes に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setMinutes( int minutes ){
        long temp = this.getTime();

        try{
            super.setMinutes( minutes );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す一分の中での秒数を
     * secounds で指定された値に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     *
     * @param secounds 秒数
     * 
     * @exception IllegalArgumentException
     *             secounds に変更したところ FILETIME形式で扱えない
     *             範囲の時間になった場合
     * @deprecated
     */
    public void setSeconds( int seconds ){
        long temp = this.getTime();

        try{
            super.setSeconds( seconds );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    /**
     * この WindowsDate の示す時間を 
     * 1970年1月1日 00:00:00 GMTから
     * time ミリ秒経過した時刻に設定する。<br>
     * このメソッドは範囲チェックを行うだけのために存在する。<br>
     * 
     * @param time 1970年1月1日 00:00:00GMT からの経過ミリ秒
     * 
     * @exception IllegalArgumentException
     *             time がFILETIME形式で扱えない
     *             範囲の時間を示していた場合
     */
    public void setTime( long time ){
        long temp = this.getTime();

        try{
            super.setTime( time );
            this.checkRange();
        }catch( IllegalArgumentException exception ){
            this.setTime( temp );
            throw exception;
        }
    }

    //------------------------------------------------------------------
    //  original method
    //------------------------------------------------------------------
    //  access method with FILETIME format
    //------------------------------------------------------------------
    //  public void setWindowsTime( long time )
    //  public long getWindowsTime()
    //------------------------------------------------------------------
    /**
     * この WindowsDate に FILETIME形式の時間情報を設定する。
     * 
     * @param time FILETIME形式の時間情報
     */
    public void setWindowsTime( long time ){
        super.setTime( 0 <= time 
         ? ( time - WindowsDate.TIME_DIFFERENCE ) / 10000L
         : ( ( time >>> 1 ) - ( WindowsDate.TIME_DIFFERENCE >>> 1 ) ) / 5000L );

        this.NanoSecounds = 
                 (int)( ( time >>> 1 ) % 5000L * 2 + ( time & 1 ) ) * 100;
    }

    /**
     * この WindowsDateが示す時間情報を FILETIME 形式で得る。
     * 
     * @return FILETIME形式の値
     */
    public long getWindowsTime() {
        return ( super.getTime() * 10000L + WindowsDate.TIME_DIFFERENCE
                 + (long)( this.NanoSecounds / 100 ) );
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  private void checkRange()
    //------------------------------------------------------------------
    /**
     * この WindowsDate が FILETIME形式で表せる時間の
     * 範囲内であるかを判定する。まだ不完全
     * 
     * @exception IllegalArgumentException
     *             この WindowsDate が FILETIME形式で扱えない
     *             範囲の時間を示していた場合
     */
    private void checkRange(){
        long time = super.getTime();
        if( !( 0xFFFCAE8C71E60F9BL <= time && time <= 0x000683218A10A8CBL ) )
            throw new IllegalArgumentException( "outside of range of Windows FILETIME format. " );
    }

}
