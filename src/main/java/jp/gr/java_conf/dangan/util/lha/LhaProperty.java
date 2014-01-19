//start of LhaProperty.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * LhaProperty.java
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
import java.util.Stack;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.StringTokenizer;
import jp.gr.java_conf.dangan.lang.reflect.Factory;

//import exceptions
import java.lang.RuntimeException;
import java.lang.SecurityException;
import java.lang.NoSuchMethodException;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;
import java.util.MissingResourceException;

import java.lang.Error;
import java.lang.NoSuchMethodError;
import java.lang.InstantiationError;
import java.lang.NoClassDefFoundError;


/**
 * LHA Library for Java の各種設定を扱う。<br>
 * LhaProperty.getProperty() や LhaProperty.getProperties() で得られる値は
 * システムプロパティ、設定ファイル、デフォルト値の何れかが用いられ、
 * その優先順位は以下のようになる。
 *  <ol>
 *    <li>システムプロパティ に設定されている値。
 *    <li>jp/gr/java_conf/dangan/util/lha/resources/lha.properties 
 *        に設定された値。
 *    <li>デフォルト値。
 *  </ol>
 * <br>
 * <br>
 * キーの一覧は以下のとおり。
 * <br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>キー</td>
 *      <td nowrap>対応する値の説明</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.encoding</td>
 *      <td nowrap>String とヘッダ内の文字列との相互変換に用いるエンコーディング</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.packages</td>
 *      <td nowrap>生成式内で使われるクラスのパッケージ名の列挙(カンマ区切り)</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lzs.encoder</td>
 *      <td nowrap>-lzs- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz4.encoder</td>
 *      <td nowrap>-lz4- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz5.encoder</td>
 *      <td nowrap>-lz5- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh0.encoder</td>
 *      <td nowrap>-lh0- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh1.encoder</td>
 *      <td nowrap>-lh1- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh2.encoder</td>
 *      <td nowrap>-lh2- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh3.encoder</td>
 *      <td nowrap>-lh3- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh4.encoder</td>
 *      <td nowrap>-lh4- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh5.encoder</td>
 *      <td nowrap>-lh5- 形式への符号化を行うオブジェクトの生成式</td>
 *   </tr>
 *    <tr>
 *      <td nowrap>lha.lh6.encoder</td>
 *      <td nowrap>-lh6- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh7.encoder</td>
 *      <td nowrap>-lh7- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lhd.encoder</td>
 *      <td nowrap>-lhd- 形式への符号化を行うオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lzs.decoder</td>
 *      <td nowrap>-lzs- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz4.decoder</td>
 *      <td nowrap>-lz4- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lz5.decoder</td>
 *      <td nowrap>-lz5- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh0.decoder</td>
 *      <td nowrap>-lh0- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh1.decoder</td>
 *      <td nowrap>-lh1- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh2.decoder</td>
 *      <td nowrap>-lh2- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh3.decoder</td>
 *      <td nowrap>-lh3- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh4.decoder</td>
 *      <td nowrap>-lh4- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh5.decoder</td>
 *      <td nowrap>-lh5- 形式のデータを復号化するオブジェクトの生成式</td>
 *   </tr>
 *    <tr>
 *      <td nowrap>lha.lh6.decoder</td>
 *      <td nowrap>-lh6- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lh7.decoder</td>
 *      <td nowrap>-lh7- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.lhd.decoder</td>
 *      <td nowrap>-lhd- 形式のデータを復号化するオブジェクトの生成式</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>lha.header</td>
 *      <td nowrap>LhaHeader のインスタンスの生成式</td>
 *    </tr>
 *  </table>
 * <br>
 * 生成式は以下のように定義される。<br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>&lt;生成式&gt;</td>
 *      <td nowrap>::= &lt;コンストラクタ&gt; | &lt;配列&gt; | &lt;置換文字列&gt; | &lt;クラス名&gt; | &lt;文字列&gt;</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;コンストラクタ&gt;</td>
 *      <td nowrap>::= &lt;クラス名&gt; '(' 引数 ')'</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;引数&gt;</td>
 *      <td nowrap>::= [ &lt;生成式&gt; [ ',' &lt;引数&gt; ] ]</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;配列&gt;</td>
 *      <td nowrap>::= '[' &lt;要素&gt; ']'</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>&lt;要素&gt;</td>
 *      <td nowrap>::= [ &lt;生成式&gt; [ ',' &lt;要素&gt; ] ]</td>
 *    </tr>
 *  </table>
 * <br>
 * クラス名は "lha.packages" に対応する値を使用して完全修飾名へと変換される。<br>
 * 置換文字列 はライブラリ内部でオブジェクトに置換される文字列で
 * 現在以下の4種類が定義されている。<br>
 *  <table border="0" cellspacing="4">
 *    <tr>
 *      <td nowrap>lha.???.encoder</td>
 *      <td nowrap>out</td>
 *      <td nowrap>圧縮後のデータを受け取る java.io.OutputStream</td>
 *    </tr>
 *    <tr>
 *      <td nowrap rowspan="2">lha.???.decoder</td>
 *      <td nowrap>in</td>
 *      <td nowrap>圧縮データを供給する java.io.InputStream</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>length</td>
 *      <td nowrap>復号化されたデータのバイト数</td>
 *    </tr>
 *    <tr>
 *      <td nowrap rowspan="2">lha.header</td>
 *      <td nowrap>data</td>
 *      <td nowrap>ヘッダデータを格納した byte配列</td>
 *    </tr>
 *    <tr>
 *      <td nowrap>encoding</td>
 *      <td nowrap>ヘッダ内の文字データを String に変換する際に使用するエンコーディング</td>
 *    </tr>
 *  </table>
 * <br>
 * <pre>
 * -- revision history --
 * $Log: LhaProperty.java,v $
 * Revision 1.0.2.2  2005/04/29 02:15:53  dangan
 * [bug fix]
 *     createDefaultProperty() で圧縮法識別子 -lhd- 用のエンコーダどデコーダが設定されていなかった。
 *
 * Revision 1.0.2.1  2004/06/27 12:09:49  dangan
 * [bugfix]
 *     生成式でカンマを使うべき部分でピリオドを使っていたのを修正。
 *
 * Revision 1.0  2002/12/05 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0.2.2 $
 */
public class LhaProperty {


    //------------------------------------------------------------------
    //  class field
    //------------------------------------------------------------------
    //  private static final Properties property
    //  public static final String encoding
    //------------------------------------------------------------------
    /**
     * LHA Library for Java の設定を保持するプロパティ
     */
    private static final Properties property = LhaProperty.createLhaProperty();

    /**
     * LHA Library for Java 内 で
     * デフォルトで使用されるエンコーディング
     */
    public static final String encoding = LhaProperty.property.getProperty( "lha.encoding" );


    //------------------------------------------------------------------
    //  constructpr
    //------------------------------------------------------------------
    //  private LhaProperty()
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ使用不可
     */
    private LhaProperty(){ }


    //------------------------------------------------------------------
    //  access method
    //------------------------------------------------------------------
    //  public static String getProperty( String key )
    //  public static Properties getProperties()
    //------------------------------------------------------------------
    /**
     * LHA Library for Java のプロパティから 
     * key に対応するものを取得する。<br>
     * 
     * @param key プロパティのキー
     * 
     * @return ブロパティの文字列
     */
    public static String getProperty( String key ){
        String def = LhaProperty.property.getProperty( key );
        try{
            if( key.equals( "lha.encoding" )
             && System.getProperty( key, def ).equals( "ShiftJISAuto" ) ){
                try{
                    String encoding = System.getProperty( "file.encoding" );
                    if( LhaProperty.isCategoryOfShiftJIS( encoding ) ){
                        return encoding;
                    }else{
                        return "SJIS";
                    }
                }catch( SecurityException exception ){
                    return "SJIS";
                }
            }else{
                return System.getProperty( key, def );
            }
        }catch( SecurityException exception ){
        }

        return def;
    }

    /**
     * LHA Library for Java のプロパティのコピーを得る。<br>
     * 
     * @return プロパティのコピー
     */
    public static Properties getProperties(){
        Properties property = (Properties)LhaProperty.property.clone();
        Enumeration enumkey = property.propertyNames();

        while( enumkey.hasMoreElements() ){
            String key = (String)enumkey.nextElement();
            try{
                String val = System.getProperty( key );
                if( null != val ){
                    property.put( key, val );
                }
            }catch( SecurityException exception ){
            }
        }

        if( property.getProperty( "lha.encoding" ).equals( "ShiftJISAuto" ) ){
            try{
                String encoding = System.getProperty( "file.encoding" );
                if( LhaProperty.isCategoryOfShiftJIS( encoding ) ){
                    property.put( "lha.encoding", encoding );
                }else{
                    property.put( "lha.encoding", "SJIS" );
                }
            }catch( SecurityException exception ){
                property.put( "lha.encoding", "SJIS" );
            }
        }

        return property;
    }


    //------------------------------------------------------------------
    //  parse
    //------------------------------------------------------------------
    //  public static Object parse( String source, 
    //                         Hashtable substitute, String packages )
    //  public static Object parse( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static Object parseConstructor( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static Object[] parseArray( String source, 
    //                         Hashtable substitute, String[] packages )
    //  private static String applyPackages( String str, String[] packages )
    //------------------------------------------------------------------
    /**
     * LHA Library for Java のプロパティ用の
     * 生成式 source を解析して 新しい Object を生成する。
     * 
     * @param souce      解析すべき生成式
     * @param substitute 置換対象文字列をkeyにもち、置換するObjectを値に持つ Hashtable
     * @param packages   カンマで区切られたパッケージ名の列挙
     * 
     * @return 生成された Object
     */
    public static Object parse( String    source, 
                                Hashtable substitute,
                                String    packages ){

        StringTokenizer tokenizer = new StringTokenizer( packages, "," );
        String[] packageArray = new String[ tokenizer.countTokens() ];
        int i = 0;
        while( tokenizer.hasMoreTokens() ){
            packageArray[i++] = tokenizer.nextToken().trim();
        }

        return LhaProperty.parse( source, substitute, packageArray );
    }

    /**
     * LHA Library for Java のプロパティ用の
     * 生成式 source を解析して 新しい Object を生成する。
     * 
     * @param souce      解析すべき文字列
     * @param substitute 置換対象文字列をkeyにもち、置換するObjectを値に持つ Hashtable
     * @param packages   パッケージ名の配列
     * 
     * @return 生成された Object
     */
    public static Object parse( String    source, 
                                Hashtable substitute,
                                String[]  packages ){

        source = source.trim();
        int casearcpos = source.indexOf( "(" );
        int bracepos   = source.indexOf( "[" );

        if( 0 <= casearcpos && ( bracepos < 0 || casearcpos < bracepos ) ){
            return LhaProperty.parseConstructor( source, substitute, packages );
        }else if( 0 <= bracepos && ( casearcpos < 0 || bracepos < casearcpos ) ){
            return LhaProperty.parseArray( source, substitute, packages );
        }else if( substitute.containsKey( source ) ){
            return substitute.get( source );
        }else{
            return LhaProperty.applyPackages( source, packages );
        }
    }

    /**
     * LHA Library for Java のプロパティ用の
     * コンストラクタを示す文字列 source を解析して、
     * 新しい インスタンスを生成する。
     * 
     * @param souce      解析すべきコンストラクタを示す文字列
     * @param substitute 置換対象文字列をkeyにもち、置換するObjectを値に持つ Hashtable
     * @param packages   パッケージ名の配列
     * 
     * @return 生成されたインスタンス
     */
    private static Object parseConstructor( String    source, 
                                            Hashtable substitute,
                                            String[]  packages ){

        String classname = source.substring( 0, source.indexOf( '(' ) ).trim();
        String arguments = source.substring( source.indexOf( '(' ) + 1, 
                                             source.lastIndexOf( ')' ) ).trim();

        classname = LhaProperty.applyPackages( classname, packages );
        Object[] args;
        if( !arguments.equals( "" ) ){
            StringTokenizer tokenizer = new StringTokenizer( arguments, ",()[]", true );
            Stack stack = new Stack();
            int pos = 0;
            while( tokenizer.hasMoreTokens() ){
                String token = tokenizer.nextToken();
                if( token.equals( "(" ) ){
                    stack.push( "(" );
                }else if( token.equals( ")" ) ){
                    if( !stack.empty() && stack.peek().equals( "(" ) ){
                        stack.pop();
                    }
                }else if( token.equals( "[" ) ){
                    stack.push( "[" );
                }else if( token.equals( "]" ) ){
                    if( !stack.empty() && stack.peek().equals( "[" ) ){
                        stack.pop();
                    }
                }else if( token.equals( "," ) ){
                    if( stack.empty() 
                     || ( !stack.peek().equals( "(" ) 
                       && !stack.peek().equals( "[" ) ) ){
                        stack.push( new Integer( pos ) );
                    }
                }
                pos += token.length();
            }

            pos = 0;
            args = new Object[ stack.size() + 1 ];
            for( int i = 0 ; i < stack.size() + 1 ; i++ ){
                String arg;
                if( i < stack.size() ){
                    arg = arguments.substring( pos, ((Integer)stack.elementAt(i)).intValue() );
                }else{
                    arg = arguments.substring( pos );
                }
                pos += arg.length() + 1;
                args[i] = LhaProperty.parse( arg, substitute, packages );
            }

        }else{
            args = new Object[0];            
        }

        try{
            return Factory.createInstance( classname, args );
        }catch( InstantiationException exception ){
            throw new InstantiationError( exception.getMessage() );
        }catch( InvocationTargetException exception ){
            if( exception.getTargetException() instanceof RuntimeException ){
                throw (RuntimeException)exception.getTargetException();
            }else if( exception.getTargetException() instanceof Error ){
                throw (Error)exception.getTargetException();
            }else{
                throw new Error( exception.getTargetException().getMessage() );
            }
        }catch( ClassNotFoundException exception ){
            throw new NoClassDefFoundError( exception.getMessage() );
        }catch( NoSuchMethodException exception ){
            throw new NoSuchMethodError( exception.getMessage() );
        }
    }

    /**
     * LHA Library for Java のプロパティ用の
     * 配列を示す文字列 source を解析して、
     * 新しい Object の配列を生成する。
     * 
     * @param souce      解析すべきコンストラクタを示す文字列
     * @param substitute 置換対象文字列をkeyにもち、置換するObjectを値に持つ Hashtable
     * @param packages   パッケージ名の配列
     * 
     * @return 生成された Object の配列
     */
    private static Object[] parseArray( String    source, 
                                        Hashtable substitute,
                                        String[]  packages ){

        String arguments = source.substring( source.indexOf( '[' ) + 1, 
                                             source.lastIndexOf( ']' ) ).trim();

        if( !arguments.equals( "" ) ){
            StringTokenizer tokenizer = new StringTokenizer( arguments, ",()[]", true );
            Stack stack = new Stack();
            int pos = 0;
            while( tokenizer.hasMoreTokens() ){
                String token = tokenizer.nextToken();
                if( token.equals( "(" ) ){
                    stack.push( "(" );
                }else if( token.equals( ")" ) ){
                    if( !stack.empty() && stack.peek().equals( "(" ) )
                        stack.pop();
                }else if( token.equals( "[" ) ){
                    stack.push( "[" );
                }else if( token.equals( "]" ) ){
                    if( !stack.empty() && stack.peek().equals( "[" ) )
                        stack.pop();
                }else if( token.equals( "," ) ){
                    if( stack.empty() 
                     || ( !stack.peek().equals( "(" ) 
                       && !stack.peek().equals( "[" ) ) )
                        stack.push( new Integer( pos ) );
                }
                pos += token.length();
            }

            pos = 0;
            Object[] array = new Object[ stack.size() + 1 ];
            for( int i = 0 ; i < stack.size() + 1 ; i++ ){
                String arg;
                if( i < stack.size() ){
                    arg = arguments.substring( pos, ((Integer)stack.elementAt(i)).intValue() );
                }else{
                    arg = arguments.substring( pos );
                }
                pos += arg.length() + 1;
                array[i] = LhaProperty.parse( arg, substitute, packages );
            }
            return array;
        }else{
            return new Object[0];
        }
    }


    /**
     * str をクラス名だと仮定して packages に含まれるパッケージ名と
     * 連結して完全修飾名を作成する事を試みる。
     * 
     * @param str      クラス名かもしれない文字列
     * @param packages パッケージ名の列挙
     * 
     * @return 完全修飾名、もしくは str
     */
    private static String applyPackages( String str, String[] packages ){
        for( int i = 0 ; i < packages.length ; i++ ){
            String classname;
            if( packages[i].equals( "" ) ){
                classname = str;
            }else{
                classname = packages[i] + "." + str;
            }
            try{
                Class.forName( classname );
                return classname;
            }catch( ClassNotFoundException exception ){
            }catch( LinkageError error ){
            }
        }
        return str;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  create property
    //------------------------------------------------------------------
    //  private static final Properties createLhaProperty()
    //  private static final Properties createDefaultProperty()
    //------------------------------------------------------------------
    /**
     * LHA Library for Java のプロパティを生成する。
     * 
     * @return 生成されたプロパティ
     */
    private static final Properties createLhaProperty(){
        String path         = "jp.gr.java_conf.dangan.util.lha.resources.lha";        
        Properties property = LhaProperty.createDefaultProperty();

        try{
            ResourceBundle bundle  = ResourceBundle.getBundle( path );
            Enumeration    enumkey = bundle.getKeys();
            while( enumkey.hasMoreElements() ){
                String key = (String)enumkey.nextElement();
                property.put( key, bundle.getString( key ) );
            }
        }catch( MissingResourceException exception ){
        }

        if( property.getProperty( "lha.encoding" ).equals( "ShiftJISAuto" ) ){
            try{
                String encoding = System.getProperty( "file.encoding" );
                if( LhaProperty.isCategoryOfShiftJIS( encoding ) ){
                    property.put( "lha.encoding", encoding );
                }else{
                    property.put( "lha.encoding", "SJIS" );
                }
            }catch( SecurityException exception ){
                property.put( "lha.encoding", "SJIS" );
            }
        }

        return property;
    }

    /**
     * LHA Library for Java のデフォルトのプロパティを生成する。
     * jp/gr/java_conf/dangan/util/lha/resources/ 以下に
     * 設定ファイルが無かった場合用。
     * 
     * @return デフォルトのプロパティ
     */
    private static final Properties createDefaultProperty(){
        Properties property = new Properties();

        //------------------------------------------------------------------
        //  encoding of String
        property.put( "lha.encoding", LhaProperty.getSystemEncoding() );

        //------------------------------------------------------------------
        //  package names
        property.put( "lha.packages", "jp.gr.java_conf.dangan.util.lha" );

        //------------------------------------------------------------------
        //  encoders
        property.put( "lha.lzs.encoder", "LzssOutputStream( PostLzsEncoder( out ), HashAndChainedListSearch, [ HashShort ] )" );
        property.put( "lha.lz4.encoder", "out" );
        property.put( "lha.lz5.encoder", "LzssOutputStream( PostLz5Encoder( out ), HashAndChainedListSearch )" );
        property.put( "lha.lhd.encoder", "out" );
        property.put( "lha.lh0.encoder", "out" );
        property.put( "lha.lh1.encoder", "LzssOutputStream( PostLh1Encoder( out ), HashAndChainedListSearch )" );
        property.put( "lha.lh2.encoder", "LzssOutputStream( PostLh2Encoder( out ), HashAndChainedListSearch )" );
        property.put( "lha.lh3.encoder", "LzssOutputStream( PostLh3Encoder( out ), HashAndChainedListSearch )" );
        property.put( "lha.lh4.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh4- ), HashAndChainedListSearch )" );
        property.put( "lha.lh5.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh5- ), HashAndChainedListSearch )" );
        property.put( "lha.lh6.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh6- ), HashAndChainedListSearch )" );
        property.put( "lha.lh7.encoder", "LzssOutputStream( PostLh5Encoder( out, -lh7- ), HashAndChainedListSearch )" );

        //------------------------------------------------------------------
        //  decoders
        property.put( "lha.lzs.decoder", "LzssInputStream( PreLzsDecoder( in ), length )" );
        property.put( "lha.lz4.decoder", "in" );
        property.put( "lha.lz5.decoder", "LzssInputStream( PreLz5Decoder( in ), length )" );
        property.put( "lha.lhd.decoder", "in" );
        property.put( "lha.lh0.decoder", "in" );
        property.put( "lha.lh1.decoder", "LzssInputStream( PreLh1Decoder( in ), length )" );
        property.put( "lha.lh2.decoder", "LzssInputStream( PreLh2Decoder( in ), length )" );
        property.put( "lha.lh3.decoder", "LzssInputStream( PreLh3Decoder( in ), length )" );
        property.put( "lha.lh4.decoder", "LzssInputStream( PreLh5Decoder( in, -lh4- ), length )" );
        property.put( "lha.lh5.decoder", "LzssInputStream( PreLh5Decoder( in, -lh5- ), length )" );
        property.put( "lha.lh6.decoder", "LzssInputStream( PreLh5Decoder( in, -lh6- ), length )" );
        property.put( "lha.lh7.decoder", "LzssInputStream( PreLh5Decoder( in, -lh7- ), length )" );

        //------------------------------------------------------------------
        //  header
        property.put( "lha.header", "LhaHeader( data, encoding )" );

        return property;
    }


    //------------------------------------------------------------------
    //  local method
    //------------------------------------------------------------------
    //  encoding
    //------------------------------------------------------------------
    //  private static final String getSystemEncoding()
    //  private static final boolean isJapanese( String encoding )
    //  private static final boolean isCategoryOfShiftJIS( String encoding )
    //------------------------------------------------------------------
    /**
     * System.getProperty( "file.encoding" ) で得られる エンコーディングを返す。
     * 得られたエンコーディングが 日本語のエンコーディングで、
     * なおかつShiftJIS系列で無い場合は強制的に "SJIS" を使用する。
     * セキュリティマネージャが システムプロパティへのアクセスを許さない場合は 
     * "ISO8859_1" を使用する。
     * 
     * @return System.getProperty( "file.encoding" ) で得られる エンコーディング
     */
    private static final String getSystemEncoding(){
        String encoding;        
        try{
            encoding = System.getProperty( "file.encoding" );
            if( LhaProperty.isJapanese( encoding )
             && !LhaProperty.isCategoryOfShiftJIS( encoding ) ){
                return "SJIS";
            }else{
                return encoding;
            }
        }catch( SecurityException exception ){
            encoding = "ISO8859_1";
        }
        return encoding;
    }

    /**
     * encoding が日本語のエンコーディングであるかを返す。
     * 
     * @param encoding エンコーディング
     * 
     * @return encoding が日本語のエンコーディングなら true 違えば false
     */
    private static final boolean isJapanese( String encoding ){

        String[] Coverters = { "Cp930",     //Japanese EBCDIC
                               "Cp939",     //Japanese EBCDIC
                               "Cp942",     //SJIS OS/2 日本語, Cp932 のスーパーセット, 0x5C -> '＼' (半角バックスラッシュ)
                               "Cp942C",    //SJIS OS/2 日本語, Cp932 のスーパーセット, 0x5C -> '￥' (半角円記号) 
                               "Cp943",     //SJIS OS/2 日本語, Cp942 のスーパーセット 新JIS対応, 0x5C -> '＼' (半角バックスラッシュ)
                               "Cp943C",    //SJIS OS/2 日本語, Cp942 のスーパーセット 新JIS対応, 0x5C -> '￥' (半角円記号)
                               "Cp33722",   //EUC IBM 日本語,
                               "MS932",     //Windows 日本語
                               "SJIS",      //Shift-JIS、日本語 
                               "EUC_JP",    //EUC, 日本語 JIS X 0201, 0208, 0212 
                               "ISO2022JP", //JIS X 0201, ISO 2022 形式の 0208、日本語 
                               "JIS0201",   //JIS X 0201, 日本語
                               "JIS0208",   //JIS X 0208, 日本語
                               "JIS0212",   //JIS X 0212, 日本語
                               "JISAutoDetect" }; //Shift-JIS EUC-JP ISO 2022 JP の検出および変換。読み込み専用。
        for( int i = 0 ; i < Coverters.length ; i++ ){
            if( encoding.equals( Coverters[i] ) ){
                return true;
            }
        }

        String[] Aliases   = { "eucjis", "euc-jp", "eucjp", "x-euc-jp", "x-eucjp", //Aliases of "EUC_JP"
                               "csEUCPkdFmtJapanese",                              //Alias of "EUCJIS"(?)
                               "extended_unix_code_packed_format_for_japanese ",   //Alias of "EUCJIS"(?)
                               "shift_jis", "ms_kanji",  "csShiftJIS",             //JDK1.1.1 - JDK1.1.7B Alias of "SJIS", JDK1.2 - JDK1.3 Alias of "MS932", JDK1.4 Alias of "SJIS"
                               "csWindows31J", "windows-31j",                      //Alias of "MS932"
                               "x-sjis",                                           //JDK1.2 Alias of "MS932", JDK1.3 Alias of "SJIS", JDK1.4 Alias of "MS932"
                               "jis",                                              //Alias of "ISO2022JP"
                               "iso-2022-jp",                                      //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                               "csISO2022JP",                                      //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                               "jis_encoding",                                     //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                               "csJISEncoding",                                    //JDK1.1.1-JDK1.1.5 Alias of "JIS", JDK1.1.6- Alias of "ISO2022JP"
                               "jis auto detect",                                  //Alias of "JISAutoDetect"
                               "cp930", "ibm-930", "ibm930", "930",                //Aliases of "Cp930"
                               "cp939", "ibm-939", "ibm939", "939",                //Aliases of "Cp939"
                               "cp942", "ibm-942", "ibm942", "942",                //Aliases of "Cp942"
                               "cp942c",                                           //Alias of "Cp942C"
                               "cp943", "ibm-943", "ibm943", "943",                //Aliases of "Cp943"
                               "cp943c",                                           //Alias of "Cp943C"
                               "cp33722", "ibm-33722", "ibm33722", "33722"  };     //Aliases of "Cp33722"
        for( int i = 0 ; i < Aliases.length ; i++ ){
            if( encoding.equalsIgnoreCase( Aliases[i] ) ){
                return true;
            }
        }

        return false;
    }


    /**
     * encoding が ShiftJIS 系列のエンコーディングであるかを返す。
     * 
     * @param encoding エンコーディング
     * 
     * @return encoding が日本語のエンコーディングなら true 違えば false
     */
    private static final boolean isCategoryOfShiftJIS( String encoding ){

        String[] Coverters = { "Cp942",     //SJIS OS/2 日本語, Cp932 のスーパーセット, 0x5C -> '＼' (半角バックスラッシュ)
                               "Cp942C",    //SJIS OS/2 日本語, Cp932 のスーパーセット, 0x5C -> '￥' (半角円記号) 
                               "Cp943",     //SJIS OS/2 日本語, Cp942 のスーパーセット 新JIS対応, 0x5C -> '＼' (半角バックスラッシュ)
                               "Cp943C",    //SJIS OS/2 日本語, Cp942 のスーパーセット 新JIS対応, 0x5C -> '￥' (半角円記号)
                               "MS932",     //Windows 日本語
                               "SJIS"  };   //Shift-JIS、日本語
        for( int i = 0 ; i < Coverters.length ; i++ ){
            if( encoding.equals( Coverters[i] ) ){
                return true;
            }
        }

        String[] Aliases   = { "shift_jis", "ms_kanji",  "csShiftJIS", //JDK1.1.1 - JDK1.1.7B Alias of "SJIS", JDK1.2 - JDK1.3 Alias of "MS932", JDK1.4 Alias of "SJIS"
                               "csWindows31J", "windows-31j",          //Alias of "MS932"
                               "x-sjis",                               //JDK1.2 Alias of "MS932", JDK1.3 Alias of "SJIS", JDK1.4 Alias of "MS932"
                               "cp942", "ibm-942", "ibm942", "942",    //Aliases of "Cp942"
                               "cp942c",                               //Alias of "Cp942C"
                               "cp943", "ibm-943", "ibm943", "943",    //Aliases of "Cp943"
                               "cp943c" };                             //Alias of "Cp943C"
        for( int i = 0 ; i < Aliases.length ; i++ ){
            if( encoding.equalsIgnoreCase( Aliases[i] ) ){
                return true;
            }
        }

        return false;
    }

}
//end of LhaProperty.java
