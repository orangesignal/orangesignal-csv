//start of CRC16.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * CRC16.java
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
 * CRC16値を算出するためのクラス。
 * 
 * クラス内の定数、処理、説明は
 * <pre>
 * Ｃ言語によるアルゴリズム辞典
 *   奥村晴彦著 技術評論社 
 *   ISBN4-87408-414-1 C3055 2400円(購入当時)
 * </pre>
 * によった。
 * 
 * <pre>
 * -- revision history --
 * $Log: CRC16.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintanance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の変更
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class CRC16 implements Checksum{


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  public static final int CRC_ANSY_POLY
    //  public static final int CRC_ANSY_INIT
    //  public static final int CCITT_POLY
    //  public static final int CCITT_INIT
    //  public static final int DefaultPOLY
    //  public static final int DefaultINIT
    //------------------------------------------------------------------
    /**
     * CRC-ANSY または CRC-16 として有名な
     * 多項式 x^16 + x^15 + x^2 + 1 をビット表現にしたもの。
     */
    public static final int CRC_ANSY_POLY = 0xA001;

    /**
     * LHAで使用される crc の初期値。
     * 作者が勝手に設定した値であり、
     * CRC-ANSY でこの値が初期値として
     * 定められているかは知らない。
     */
    public static final int CRC_ANSY_INIT = 0x0000;

    /**
     * CCITT の X.25という規格の
     * 多項式 x^16 + x^12 + x^5 + 1 をビット表現にしたもの。
     */
    public static final int CCITT_POLY = 0x8408;

    /**
     * CCITT の X.25という規格の crc の初期値。
     */
    public static final int CCITT_INIT = 0xFFFF;

    /**
     * LHAで通常使用される、という意味でデフォルトのCRC多項式。
     * CRC16.CRC_ANSY_POLY と同等である。
     */
    public static final int DefaultPOLY = CRC16.CRC_ANSY_POLY;

    /**
     * LHAで通常使用される、という意味でデフォルトのcrcの初期値。
     * CRC16.CRC_ANSY_INIT と同等である。
     */
    public static final int DefaultINIT = CRC16.CRC_ANSY_INIT;


    //------------------------------------------------------------------
    //  instance field
    //------------------------------------------------------------------
    //  private int crc
    //  private int init
    //  private int[] crcTable
    //------------------------------------------------------------------
    /** 
     * CRC16値 
     */
    private int crc;

    /** 
     * crc の初期値 
     */
    private int init;

    /** 
     * CRC16値の更新用テーブル 
     */
    private int[] crcTable;


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  public CRC16()
    //  public CRC16( int poly )
    //  public CRC16( int poly, int init )
    //  public CRC16( int[] crcTable, int init )
    //------------------------------------------------------------------
    /**
     * LHAで使用される 多項式と初期値を持つ CRC16を生成する。
     */
    public CRC16(){
        this( DefaultPOLY, DefaultINIT );
    }

    /**
     * poly で指定される 多項式を持つ CRC16を生成する。
     * 初期値は poly が CRC16.CCITT_POLY であれば
     * CRC16.CCITT_INIT を そうでなければ 
     * CRC16.DefaultINIT を使用する。
     * 
     * @param poly CRC16算出に使用する多項式のビット表現
     */
    public CRC16( int poly ){
        this( poly, 
              ( poly == CRC16.CCITT_POLY ? 
                        CRC16.CCITT_INIT : 
                        CRC16.DefaultINIT ) );
    }

    /**
     * poly で指定される 多項式と initで指定される初期値を持つ
     * CRC16を生成する。
     * 
     * @param poly CRC16算出に使用する多項式のビット表現
     * @param init crc の初期値
     */
    public  CRC16( int poly, int init ){
        this( CRC16.makeCrcTable( poly ), init );
    }

    /**
     * crcTable で指定される CRC算出用表と 
     * initで指定される初期値を持つ CRC16を作成する。
     *
     * @param crcTable CRC16算出に使用する表
     * @param init     crc の初期値
     */
    public  CRC16( int[] crcTable, int init ){
        final int BYTE_PATTERNS= 256;

        if( crcTable.length == BYTE_PATTERNS ){
            this.crcTable = crcTable;
            this.init     = init;

            this.reset();
        }else{
            throw new IllegalArgumentException( "crcTable.length must equals 256" );
        }
    }


    //------------------------------------------------------------------
    //  method of java.util.zip.Checksum
    //------------------------------------------------------------------
    //  update
    //------------------------------------------------------------------
    //  public void update( int byte8 )
    //  public void update( byte[] buffer )
    //  public void update( byte[] buffer, int index, int length )
    //------------------------------------------------------------------
    /**
     * byte8 で指定される 1バイトのデータで crcの値を更新する。
     * 
     * @param byte8 crcを更新する 1バイトのデータ
     */
    public void update( int byte8 ){
        final int BYTE_BITS = 8;
        this.crc = ( this.crc >> BYTE_BITS )
                    ^ this.crcTable[ ( this.crc ^ byte8 ) & 0xFF ];
    }

    /**
     * buffer で指定したバイト配列で crc の値を更新する。
     * 
     * @param buffer crcを更新する データを持つバイト配列
     */
    public void update( byte[] buffer ){
        this.update( buffer, 0, buffer.length );
    }

    /**
     * buffer で指定したバイト配列で crc の値を更新する。
     * 
     * @param buffer crcを更新する データを持つバイト配列
     * @param index  データの開始位置
     * @param length チェックサムの更新に使うバイト数
     */
    public void update( byte[] buffer, int index, int length ){
        final int BYTE_BITS = 8;

        while( 0 < ( index & 0x03 ) && 0 < length-- ){
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ buffer[index++] ) & 0xFF ];
        }

        while( 4 <= length ){
            int data =  (   buffer[index++] & 0xFF )
                      | ( ( buffer[index++] & 0xFF ) <<  8 )
                      | ( ( buffer[index++] & 0xFF ) << 16 )
                      | (   buffer[index++]          << 24 );

            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            data >>>= BYTE_BITS;
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ data ) & 0xFF ];
            length -= 4;
        }

        while( 0 < length-- ){
            this.crc = ( this.crc >> BYTE_BITS )
                       ^ this.crcTable[ ( this.crc ^ buffer[index++] ) & 0xFF ];
        }
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
     * crc 値を初期値に設定しなおす。
     */
    public void reset(){
        this.crc = this.init;
    }

    /**
     * crc 値を得る。
     * crc 値は 2バイトの値であり、 
     * 0x0000～0xFFFFにマップされる。
     * 
     * @return crc 値
     */
    public long getValue(){
        return this.crc & 0xFFFF;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  public static int[] makeCrcTable( int init )
    //------------------------------------------------------------------
    /**
     * CRC値算出用の 表を作成する。
     * 
     * @param poly CRC算出用の多項式
     */
    public static int[] makeCrcTable( int poly ){
        final int BYTE_PATTERNS = 256;
        final int BYTE_BITS     = 8;
        int[] crcTable = new int[BYTE_PATTERNS];

        for( int i = 0 ; i < BYTE_PATTERNS ; i++ ){
            crcTable[i] = i;

            for( int j = 0 ; j < BYTE_BITS ; j++ ){
                if( ( crcTable[i] & 1 ) != 0 ){
                    crcTable[i] = ( crcTable[i] >> 1 ) ^ poly;
                }else{
                    crcTable[i] >>= 1;
                }
            }
        }

        return crcTable;
    }

}
//end of CRC16.java
