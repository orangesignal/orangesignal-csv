//start of Type.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Type.java
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

package jp.gr.java_conf.dangan.lang.reflect;

//import classes and interfaces
import java.math.BigInteger;

//import exceptions
import java.lang.IllegalArgumentException;
import java.lang.NumberFormatException;

/**
 * Reflection の機能を扱いやすくするように
 * 型情報を扱うユーティリティクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: Type.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class Type{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private Type()
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private Type(){  }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  utility methods for type matching
    //------------------------------------------------------------------
    //  public static boolean matchFullAll( Class[] types, Object[] args )
    //  public static boolean matchRestrictAll( Class[] types, Object[] args )
    //  public static boolean matchAll( Class[] types, Object[] args )
    //------------------------------------------------------------------
    /**
     * args が変換無しで types と一致するかを得る。
     * 
     * @param types 型情報配列
     * @param args  判定対象のオブジェクト配列
     * 
     * @return args が types に一致すれば true。<br>
     *         違えば flase。
     */
    public static boolean matchFullAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.matchFull( types[i], args[i] );

        return match;
    }

    /**
     * args が Type.parse による変換を伴えば
     * types と一致するかを得る。
     * matchAll() より厳密に判定する。
     * 
     * @param types 型情報配列
     * @param args  判定対象のオブジェクト配列
     * 
     * @return args が types に一致すれば true。<br>
     *         違えば flase。
     */
    public static boolean matchRestrictAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.matchRestrict( types[i], args[i] );

        return match;
    }

    /**
     * args が Type.parse による変換を伴えば
     * types と一致するかを得る。
     * 
     * @param types 型情報配列
     * @param args  判定対象のオブジェクト配列
     * 
     * @return args が types に一致すれば true。<br>
     *         違えば flase。
     */
    public static boolean matchAll( Class[] types, Object[] args ){
        boolean match = ( types.length == args.length );

        for( int i = 0 ; i < types.length ; i++ )
            match = match && Type.match( types[i], args[i] );

        return match;
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  type matching
    //------------------------------------------------------------------
    //  public static boolean matchFull( Class type, Object obj )
    //  public static boolean matchRestrict( Class type, Object obj )
    //  public static boolean match( Class type, Object obj )
    //------------------------------------------------------------------
    /**
     * obj が変換無しで type と一致するかを得る。
     * 
     * @param type 型情報
     * @param obj  判定対象のオブジェクト
     * 
     * @return obj が type の実体であれば true。<br>
     *         違えば false。
     */
    public static boolean matchFull( Class type, Object obj ){
        if( type.isInstance( obj ) ){
            return true;
        }else if( !type.isPrimitive() && obj == null ){
            return true;
        }else if( type.equals( Boolean.TYPE ) && obj instanceof Boolean ){
            return true;
        }else if( type.equals( Byte.TYPE ) && obj instanceof Byte ){
            return true;
        }else if( type.equals( Short.TYPE ) && obj instanceof Short ){
            return true;
        }else if( type.equals( Character.TYPE ) && obj instanceof Character ){
            return true;
        }else if( type.equals( Integer.TYPE ) && obj instanceof Integer ){
            return true;
        }else if( type.equals( Long.TYPE ) && obj instanceof Long ){
            return true;
        }else if( type.equals( Float.TYPE ) && obj instanceof Float ){
            return true;
        }else if( type.equals( Double.TYPE ) && obj instanceof Double ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * obj が type の実体であるかを得る。
     * type が数値を示すプリミティブ型
     * ( byte, short, int, long, float, double のいずれか )を
     * であり、かつ obj がそれらのプリミティブのラッパ型、
     * ( Byte, Short, Integer, Long, Float, Double のいずれか )
     * のインスタンスである場合 変換可能と判断して true を返す。
     * 
     * @param type 型情報
     * @param obj  判定対象のオブジェクト
     * 
     * @return obj が type の実体であれば true。<br>
     *         違えば false。
     */
    public static boolean matchRestrict( Class type, Object obj ){

        if( Type.matchFull( type, obj ) ){
            return true;
        }else if( ( type.equals( Byte.TYPE ) || type.equals( Short.TYPE ) 
                 || type.equals( Integer.TYPE ) || type.equals( Long.TYPE ) 
                 || type.equals( Float.TYPE )  || type.equals( Double.TYPE ) )
               && ( obj instanceof Byte || obj instanceof Short
                 || obj instanceof Integer || obj instanceof Long
                 || obj instanceof Float || obj instanceof Double ) ){
            return true;
        }else{
            return false;
        }
    }

    /**
     * obj が type の実体であるかを得る。
     * obj が Type.parse( type, obj ) で変換可能な場合
     * trueを返す。
     * 
     * @param type 型情報
     * @param obj  判定対象のオブジェクト
     * 
     * @return obj が type の実体であれば true。<br>
     *         違えば false。
     */
    public static boolean match( Class type, Object obj ){
        final String str = ( obj == null ? null : obj.toString() );

        if( Type.matchRestrict( type, obj ) ){
            return true;
        }else if( type.equals( String.class ) ){
            return true;
        }else if( !type.isPrimitive() && "NULL".equalsIgnoreCase( str ) ){
            return true;
        }else if( ( type.equals( Byte.class ) || type.equals( Byte.TYPE )
                 || type.equals( Short.class ) || type.equals( Short.TYPE )
                 || type.equals( Integer.class ) || type.equals( Integer.TYPE )
                 || type.equals( Long.class ) || type.equals( Long.TYPE )
                 || type.equals( Float.class ) || type.equals( Float.TYPE )
                 || type.equals( Double.class ) || type.equals( Double.TYPE ) )
               && ( obj instanceof Number
                 || ( obj != null && Type.isLongString( obj.toString() ) )
                 || ( obj != null && Type.isDoubleString( obj.toString() ) ) ) ){
            return true;
        }else if( ( type.equals( Boolean.TYPE ) || type.equals( Boolean.class ) )
               && ( "TRUE".equalsIgnoreCase( str ) || "FALSE".equalsIgnoreCase( str ) ) ){
            return true;
        }else if( ( type.equals( Character.class ) || type.equals( Character.TYPE ) )
               && obj instanceof String 
               && ( str.length() == 1 || Type.isUnicodeEscape( str ) ) ){
            return true;
        }else{
            return false;
        }
    }


    //------------------------------------------------------------------
    //  shared method
    //------------------------------------------------------------------
    //  parse
    //------------------------------------------------------------------
    //  public static Object[] parseAll( Class[] types, Object[] args )
    //  public static Object parse( Class type, Object obj )
    //------------------------------------------------------------------
    /**
     * Factory.matchAll( types, args ) でマッチした args を
     * 一括して types で示される型に変換する。
     * 
     * @param types 変換する型情報配列
     * @param args  変換対象のオブジェクト配列
     * 
     * @return 変換後のオブジェクト配列
     * 
     * @exception IllegalAccessError
     *             args を types に変換不可能な場合。
     */
    public static Object[] parseAll( Class[] types, Object[] args ){
        if( types.length == args.length ){
            Object[] objs = new Object[ args.length ];

            for( int i = 0 ; i < args.length ; i++ )
                objs[i] = Type.parse( types[i], args[i] );

            return objs;
        }else{
            throw new IllegalArgumentException();
        }
    }

    /**
     * Factory.match( type, obj ) でマッチした obj を
     * type で示される型に変換する。
     * 
     * @param type 変換する型情報
     * @param obj  変換対象のオブジェクト
     * 
     * @return 変換後のオブジェクト
     * 
     * @exception IllegalArgumentException
     *             Factory.match( type, obj ) でマッチしていない obj を
     *             変換しようとした場合。
     */
    public static Object parse( Class type, Object obj ){
        final String str = ( obj == null ? null : obj.toString() );

        if( type.isInstance( obj ) ){
            return obj;
        }else if( !type.isPrimitive() 
               && !type.equals( String.class ) 
               && ( obj == null || "NULL".equalsIgnoreCase( str ) ) ){
            return null;
        }else if( type.equals( String.class ) ){
            return str;
        }else if( ( type.equals( Byte.class ) || type.equals( Byte.TYPE )
                 || type.equals( Short.class ) || type.equals( Short.TYPE )
                 || type.equals( Integer.class ) || type.equals( Integer.TYPE )
                 || type.equals( Long.class ) || type.equals( Long.TYPE )
                 || type.equals( Float.class ) || type.equals( Float.TYPE )
                 || type.equals( Double.class ) || type.equals( Double.TYPE ) )
               && ( obj instanceof Number
                 || ( obj != null && Type.isLongString( str ) )
                 || ( obj != null && Type.isDoubleString( str ) ) ) ){
            Number num = null;
            if( obj instanceof Number ){
                num = (Number)obj;
            }else{
                try{
                    if( Type.isLongString( str ) )
                        num = new Long( Long.parseLong( str ) );
                    else
                        num = new Double( str );
                }catch( NumberFormatException exception ){
                    num = Type.parseHexadecimal( str.substring( 2 ) );
                }
            }
            
            if( type.equals( Byte.class ) || type.equals( Byte.TYPE ) ){
                return new Byte( num.byteValue() );
            }else if( type.equals( Short.class ) || type.equals( Short.TYPE ) ){
                return new Short( num.shortValue() );
            }else if( type.equals( Integer.class ) || type.equals( Integer.TYPE ) ){
                return new Integer( num.intValue() );
            }else if( type.equals( Long.class ) || type.equals( Long.TYPE ) ){
                return new Long( num.longValue() );
            }else if( type.equals( Float.class ) || type.equals( Float.TYPE ) ){
                return new Float( num.floatValue() );
            }else{
                return new Double( num.doubleValue() );
            }    
        }else if( type.equals( Boolean.class )
               || type.equals( Boolean.TYPE ) ){
            if( "TRUE".equalsIgnoreCase( str ) ){
                return new Boolean( true );
            }else if( "FALSE".equalsIgnoreCase( str ) ){
                return new Boolean( false );
            }
        }else if( ( type.equals( Character.class )
                 || type.equals( Character.TYPE ) )
               && obj != null ){
            if( str.length() == 1 ){
                return new Character( str.charAt( 0 ) );
            }else if( Type.isUnicodeEscape( str ) ){
                return new Character( (char)Type.parseHexadecimal( str.substring( 2 ) ).intValue() );
            }
        }
        throw new IllegalArgumentException();
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  chack that string match the pattern of type.
    //------------------------------------------------------------------
    //  private static boolean isUnicodeEscape( String str )
    //  private static boolean isLongString( String str )
    //  private static boolean isDoubleString( String str )
    //------------------------------------------------------------------
    /**
     * str が ユニコードエスケープされた1文字であるかを得る。
     * 
     * @param str 文字列
     * 
     * @return str がユニコードエスケープされた1文字である場合
     */
    private static boolean isUnicodeEscape( String str ){
        if( str.length() == 6
         && str.startsWith( "\\u" )
         && Type.isHexadecimal( str.substring( 2 ) ) ){
            return true;
         }else{
            return false;
         }
    }

    /**
     * str が確実に Integer を示す文字列であるかを得る。
     * 
     * @param str 文字列
     * 
     * @return str が確実に Integer を示す文字列なら true。
     *         違えば false。
     */
    private static boolean isLongString( String str ){
        try{
            Long.parseLong( str );
            return true;
        }catch( NumberFormatException exception ){
        }

        if( str.startsWith( "0x" ) && Type.isHexadecimal( str.substring( 2 ) ) ){
            BigInteger val = Type.parseHexadecimal( str.substring( 2 ) );
            final BigInteger zero  = new BigInteger( "0" );
            final BigInteger limit = new BigInteger( "FFFFFFFFFFFFFFFF", 16 );

            if( zero.compareTo( val ) <= 0 && val.compareTo( limit ) <= 0  ) 
                return true;
            else
                return false;
        }else{
            return false;
        }
    }

    /**
     * str が確実に Double を示す文字列であるかを得る。
     * 
     * @param str 文字列
     * 
     * @return str が確実に Integer を示す文字列なら true。
     *         違えば false。
     */
    private static boolean isDoubleString( String str ){
        try{
            Double  num = Double.valueOf( str );

            if( !num.isInfinite()
             || str.equals( "Infinity" )
             || str.equals( "-Infinity" ) )
                return true;
            else
                return false;
        }catch( NumberFormatException exception ){
        }

        return false;
    }

    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  processing hexadecimal
    //------------------------------------------------------------------
    //  private static boolean isHexadecimal( String str )
    //  private static long perseHexadecimal( String str )
    //------------------------------------------------------------------
    /**
     * 文字列が 16進の文字列かを判定する。
     * 
     * @param str 判定対象の文字列
     * 
     * @return str が16進の文字列であれば true。
     *         違えば false。
     */
    private static boolean isHexadecimal( String str ){
        final String hexadecimal  = "0123456789ABCDEF";
        str = str.toUpperCase();

        if( 0 < str.length() ){
            for( int i = 0 ; i < str.length() ; i++ )
                if( hexadecimal.indexOf( str.charAt( i ) ) < 0 )
                    return false;

            return true;
        }else{
            return false;
        }
    }

    /**
     * 文字列を 16進の文字列として解釈し、値を得る。
     * 
     * @param str 文字列
     * 
     * @return str を16進数として解釈した値。
     *         str が16進数でない場合の結果は不定。
     */
    private static BigInteger parseHexadecimal( String str ){
        return new BigInteger( str, 16 );
    }

}
//end of Type.java
