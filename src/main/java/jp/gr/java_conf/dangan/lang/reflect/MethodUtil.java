//start of MethodUtil.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * MethodUtil.java
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
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

//import exceptions
import java.lang.ClassNotFoundException;
import java.lang.NoSuchMethodException;
import java.lang.reflect.InvocationTargetException;

/**
 * メソッドに関するユーティリティクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: MethodUtil.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class MethodUtil{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private MethodUtil()
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private MethodUtil(){  }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  invoke static method
    //------------------------------------------------------------------
    //  public static Object invoke( Object obj, String name, Object[] args )
    //------------------------------------------------------------------
    /**
     * インスタンスobjの nameという名前の
     * メソッドをargsを引数として実行する。
     * 
     * @param obj  インスタンス
     * @param name メソッド名
     * @param args 引数の配列
     * 
     * @return 戻り値
     * 
     * @exception InvocationTargetException
     *                 コンストラクタで例外が発生した場合
     * 
     * @exception NoSuchMethodException
     *                 args を引数に取れる name という名前の
     *                 インスタンスメソッドが見つからなかった場合。
     */
    public static Object invoke( Object obj, String name, Object[] args ) 
                                              throws InvocationTargetException,
                                                     NoSuchMethodException {
        Class  type   = obj.getClass();
        Method method = MethodUtil.getMatchFullInstanceMethod( type, name, args );

        if( method == null ){
            method    = MethodUtil.getInstanceMethod( type, name, args );

            if( method != null )
                args      = Type.parseAll( method.getParameterTypes(), args );
        }

        if( method != null ){
            try{
                return method.invoke( obj, args );
            }catch( IllegalAccessException exception ){
                throw new IllegalAccessError( exception.toString() );
            }
        }else{
            throw new NoSuchMethodException();
        }
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  invoke static method
    //------------------------------------------------------------------
    //  public static Object invokeStatic( String classname, String name, Object[] args )
    //  public static Object invokeStatic( Class type, String name, Object[] args )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの nameという名前の
     * static メソッドをargsを引数として実行する。
     * 
     * @param classname クラス名
     * @param name      メソッド名
     * @param args      引数の配列
     * 
     * @return 戻り値
     * 
     * @exception ClassNotFoundException
     *                 classname のクラスが見つからなかった場合
     * 
     * @exception InvocationTargetException
     *                 コンストラクタで例外が発生した場合
     * 
     * @exception NoSuchMethodException
     *                 args を引数に取れる name という名前の
     *                 インスタンスメソッドが見つからなかった場合。
     */
    public static Object invokeStatic( String   classname, 
                                       String   name, 
                                       Object[] args ) 
                                              throws ClassNotFoundException,
                                                     InvocationTargetException,
                                                     NoSuchMethodException {
        return MethodUtil.invokeStatic( Class.forName( classname ),             //throw ClassNotFoundException
                                        name, 
                                        args );                                 //throw InvocationTargetException, NoSuchMethodException
    }

    /**
     * type で示されるクラスの nameという名前の
     * static メソッドをargsを引数として実行する。
     * 
     * @param type 型情報
     * @param name メソッド名
     * @param args 引数の配列
     * 
     * @return 戻り値
     * 
     * @exception InvocationTargetException
     *                 コンストラクタで例外が発生した場合
     * 
     * @exception NoSuchMethodException
     *                 args を引数に取れる name という名前の
     *                 インスタンスメソッドが見つからなかった場合。
     */
    public static Object invokeStatic( Class type, String name, Object[] args ) 
                                              throws InvocationTargetException,
                                                     NoSuchMethodException {
        Method method = MethodUtil.getMatchFullStaticMethod( type, name, args );

        if( method == null ){
            method    = MethodUtil.getStaticMethod( type, name, args );

            if( method != null )
                args      = Type.parseAll( method.getParameterTypes(), args );
        }

        if( method != null ){
            try{
                return method.invoke( null, args );
            }catch( IllegalAccessException exception ){
                throw new IllegalAccessError( exception.toString() );
            }
        }else{
            throw new NoSuchMethodException();
        }
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get instance method
    //------------------------------------------------------------------
    //  public static Method getInstanceMethod( String classname, 
    //                                          String name, Object[] args )
    //  public static Method getInstanceMethod( Class  type,
    //                                          String name, Object[] args )
    //  public static Method getInstanceMethod( String classname, String  name, 
    //                                          Object[] args,    boolean all )
    //  public static Method getInstanceMethod( Class  type,      String name, 
    //                                          Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public なインスタンスメソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getInstanceMethod( String   classname,
                                            String   name,
                                            Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getInstanceMethod( Class.forName( classname ),
                                             name,
                                             args,
                                             false );
    }

    /**
     * type の public なインスタンスメソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param type 型情報。
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getInstanceMethod( Class    type,
                                            String   name,
                                            Object[] args ){
        return MethodUtil.getInstanceMethod( type, name, args, false );
    }

    /**
     * classname で示されるクラスの インスタンスメソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * @param all       public のメソッドのみを検索するなら false。
     *                  public, protected, private, パッケージプライベートの
     *                  全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getInstanceMethod( String   classname,
                                            String   name,
                                            Object[] args,
                                            boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getInstanceMethod( Class.forName( classname ),
                                             name,
                                             args,
                                             all );
    }

    /**
     * type の インスタンスメソッドのうち、name という名前で args を
     * Type.parse した後 受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * @param all  public のメソッドのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getInstanceMethod( Class    type,
                                            String   name,
                                            Object[] args,
                                            boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchRestrictAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full instance method
    //------------------------------------------------------------------
    //  public static Method getMatchFullInstanceMethod( String classname, 
    //                                    String name, Object[] args )
    //  public static Method getMatchFullInstanceMethod( Class  type,
    //                                    String name, Object[] args )
    //  public static Method getMatchFullInstanceMethod( String classname, 
    //                String  name,  Object[] args,    boolean all )
    //  public static Method getMatchFullInstanceMethod( Class  type,
    //                String name,   Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public なインスタンスメソッドのうち、
     * name という名前で args を 直接受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getMatchFullInstanceMethod( String   classname,
                                                     String   name,
                                                     Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullInstanceMethod( 
                                  Class.forName( classname ),
                                  name,
                                  args,
                                  false );
    }

    /**
     * type の public なインスタンスメソッドのうち、
     * name という名前で args を直接受け入れることができるものを得る。
     * 
     * @param type 型情報。
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getMatchFullInstanceMethod( Class    type,
                                                     String   name,
                                                     Object[] args ){
        return MethodUtil.getMatchFullInstanceMethod( type, name, args, false );
    }

    /**
     * classname で示されるクラスの インスタンスメソッドのうち、
     * name という名前で args を直接受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * @param all       public のメソッドのみを検索するなら false。
     *                  public, protected, private, パッケージプライベートの
     *                  全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getMatchFullInstanceMethod( String   classname,
                                                     String   name,
                                                     Object[] args,
                                                     boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullInstanceMethod( 
                                  Class.forName( classname ),
                                  name,
                                  args,
                                  all );
    }


    /**
     * type の インスタンスメソッドのうち、name という名前で 
     * args を直接受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * @param all  public のメソッドのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getMatchFullInstanceMethod( Class    type,
                                                     String   name,
                                                     Object[] args,
                                                     boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && !Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;        
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get static method
    //------------------------------------------------------------------
    //  public static Method getStaticMethod( String classname, 
    //                                        String name, Object[] args )
    //  public static Method getStaticMethod( Class  type,
    //                                        String name, Object[] args )
    //  public static Method getStaticMethod( String classname, String  name, 
    //                                        Object[] args,    boolean all )
    //  public static Method getStaticMethod( Class  type,      String name, 
    //                                        Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public static メソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getStaticMethod( String   classname,
                                          String   name,
                                          Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getStaticMethod( Class.forName( classname ),
                                           name,
                                           args,
                                           false );
    }

    /**
     * type の public staticなメソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param type 型情報。
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getStaticMethod( Class    type,
                                          String   name,
                                          Object[] args ){
        return MethodUtil.getStaticMethod( type, name, args, false );
    }

    /**
     * classname で示されるクラスの static メソッドのうち、
     * name という名前で args を Type.parse した後
     * 受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * @param all       public のメソッドのみを検索するなら false。
     *                  public, protected, private, パッケージプライベートの
     *                  全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getStaticMethod( String   classname,
                                          String   name,
                                          Object[] args,
                                          boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getStaticMethod( Class.forName( classname ),
                                           name,
                                           args,
                                           all );
    }

    /**
     * type の static メソッドのうち、name という名前で args を
     * Type.parse した後 受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * @param all  public のメソッドのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getStaticMethod( Class    type,
                                          String   name,
                                          Object[] args,
                                          boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchRestrictAll( methods[i].getParameterTypes(), args ) )
                return methods[i];

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full static method
    //------------------------------------------------------------------
    //  public static Method getMatchFullStaticMethod( String classname, 
    //                                    String name, Object[] args )
    //  public static Method getMatchFullStaticMethod( Class  type,
    //                                    String name, Object[] args )
    //  public static Method getMatchFullStaticMethod( String classname, 
    //                String  name,  Object[] args,    boolean all )
    //  public static Method getMatchFullStaticMethod( Class  type,
    //                String name,   Object[] args,    boolean all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public static メソッドのうち、
     * name という名前で args を 直接受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getMatchFullStaticMethod( String   classname,
                                                   String   name,
                                                   Object[] args ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullStaticMethod( Class.forName( classname ),
                                                    name,
                                                    args,
                                                    false );
    }

    /**
     * type の public staticなメソッドのうち、
     * name という名前で args を直接受け入れることができるものを得る。
     * 
     * @param type 型情報。
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getMatchFullStaticMethod( Class    type,
                                                   String   name,
                                                   Object[] args ){
        return MethodUtil.getMatchFullStaticMethod( type, name, args, false );
    }

    /**
     * classname で示されるクラスの static メソッドのうち、
     * name という名前で args を直接受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     * @param name      検索するメソッド名。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のメソッド以外のものが見つかる可能性がある。
     * @param all       public のメソッドのみを検索するなら false。
     *                  public, protected, private, パッケージプライベートの
     *                  全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Method getMatchFullStaticMethod( String   classname,
                                                   String   name,
                                                   Object[] args,
                                                   boolean  all ) 
                                                throws  ClassNotFoundException {
        return MethodUtil.getMatchFullStaticMethod( Class.forName( classname ),
                                                    name,
                                                    args,
                                                    all );
    }


    /**
     * type の static メソッドのうち、name という名前で 
     * args を直接受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     * @param name 検索するメソッド名。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のメソッド以外のものが見つかる可能性がある。
     * @param all  public のメソッドのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのメソッドを検索するなら true。
     * 
     * @return args を引数に取ることができる nameという名前の メソッド。
     *         見つからなければ null。
     */
    public static Method getMatchFullStaticMethod( Class    type,
                                                   String   name,
                                                   Object[] args,
                                                   boolean  all ){
        Method[] methods = all 
                         ? type.getDeclaredMethods()
                         : type.getMethods();

        for( int i = 0 ; i < methods.length ; i++ )
            if( methods[i].getName().equals( name )
             && Modifier.isStatic( methods[i].getModifiers() )
             && Type.matchFullAll( methods[i].getParameterTypes(), args ) )
                return methods[i];
        
        return null;        
    }

}
//end of Method.java
