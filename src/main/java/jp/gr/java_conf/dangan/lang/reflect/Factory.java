//start of Factory.java
//TEXT_STYLE:CODE=Shift_JIS(Japanese):RET_CODE=CRLF

/**
 * Factory.java
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
import java.lang.reflect.Constructor;
import java.text.NumberFormat;

//import exceptions
import java.lang.NoSuchMethodException;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;

/**
 * クラス名と 引数になるObject の配列から、
 * createInstance() によって新しいインスタンスを作り出す
 * ユーティリティクラス。
 * 
 * <pre>
 * -- revision history --
 * $Log: Factory.java,v $
 * Revision 1.0  2002/10/01 00:00:00  dangan
 * first edition
 * add to version control
 *
 * </pre>
 * 
 * @author  $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class Factory{


    //------------------------------------------------------------------
    //  constructor
    //------------------------------------------------------------------
    //  private Factory()
    //------------------------------------------------------------------
    /**
     * デフォルトコンストラクタ。
     * 使用不可。
     */
    private Factory(){  }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
	//	create instance
    //------------------------------------------------------------------
    //  public static Object createInstance( String classname, Object[] args )
    //  public static Object createInstance( Class clas, Object[] args )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスのインスタンスを生成する。
     * コンストラクタには args の型と一致するものを使用する。
     * 
     * @param classname クラス名
     * @param args      引数の配列
     * 
     * @return 生成されたインスタンス
     *         args と型情報がマッチする
     *         コンストラクタが存在しなかった場合は null
     * 
     * @exception InvocationTargetException
     *                 コンストラクタで例外が発生した場合
     * 
     * @exception InstantiationException
     *                 abstractクラスのインスタンスを得ようとした場合
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Object createInstance( String classname, Object[] args )
                                              throws InvocationTargetException,
                                                     InstantiationException,
                                                     ClassNotFoundException,
                                                     NoSuchMethodException {
        return Factory.createInstance( Class.forName( classname ), args );
    }

    /**
     * type で示されるクラスのインスタンスを生成する。
     * コンストラクタには args の型と一致するものを使用する。
     * 
     * @param type クラス名
     * @param args 引数の配列
     * 
     * @return 生成されたインスタンス
     *         args と型情報がマッチする
     *         コンストラクタが存在しなかった場合は null
     * 
     * @exception InvocationTargetException
     *                 コンストラクタで例外が発生した場合
     * 
     * @exception InstantiationException
     *                 abstractクラスのインスタンスを得ようとした場合
     */
    public static Object createInstance( Class type, Object[] args ) 
                                              throws InvocationTargetException,
                                                     InstantiationException,
                                                     NoSuchMethodException {
        Constructor constructor = Factory.getMatchFullConstructor( type, args );

        if( constructor == null ){
            constructor = Factory.getConstructor( type, args );

            if( constructor != null )
                args        = Type.parseAll( constructor.getParameterTypes(), args );
        }

        if( constructor != null ){
            try{
                return constructor.newInstance( args );
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
    //  get constructor
    //------------------------------------------------------------------
    //  public static Constructor getConstructor( String   classname,
    //                                            Object[] args )
    //  public static Constructor getConstructor( Class type, Object[] args )
    //  public static Constructor getConstructor( String   classname,
    //                                            Object[] args, boolean all )
    //  public static Constructor getConstructor( Class type,
    //                                            Object[] args, boolean  all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public なコンストラクタのうち、
     * args を Type.parse した場合、引数として受け入れることが
     * できるものを得る。
     * 
     * @param classname クラス名。
     *                  この型のインスタンスを生成するための
     *                  コンストラクタを得る。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のコンストラクタ以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Constructor getConstructor( String   classname,
                                              Object[] args ) 
                                                throws  ClassNotFoundException {
        return Factory.getConstructor( Class.forName( classname ),
                                       args );
    }
    
    /**
     * type のpublic なコンストラクタのうち、args を 
     * Type.parse した場合 引数として受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     *             この型のインスタンスを生成するためのコンストラクタを得る。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のコンストラクタ以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     */
    public static Constructor getConstructor( Class    type,
                                              Object[] args ){
        return Factory.getConstructor( type, args, false );
    }

    /**
     * classnameで示されるクラスの コンストラクタのうち、args を 
     * Type.parse して 引数として受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     *                  この型のインスタンスを生成するためのコンストラクタを得る。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のコンストラクタ以外のものが見つかる可能性がある。
     * @param all  public のコンストラクタのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのコンストラクタをけんさくするなら true。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Constructor getConstructor( String   classname,
                                              Object[] args,
                                              boolean  all ) 
                                                throws  ClassNotFoundException {
        return Factory.getConstructor( Class.forName( classname ),
                                       args,
                                       all );
    }

    /**
     * type のコンストラクタのうち、args を Type.parse した場合
     * 引数として受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     *             この型のインスタンスを生成するためのコンストラクタを得る。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のコンストラクタ以外のものが見つかる可能性がある。
     * @param all  public のコンストラクタのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのコンストラクタをけんさくするなら true。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     */
    public static Constructor getConstructor( Class    type,
                                              Object[] args,
                                              boolean  all ){
        Constructor[] constructors = all 
                                   ? type.getDeclaredConstructors()
                                   : type.getConstructors();

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchFullAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchRestrictAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];

        
        return null;        
    }


    //------------------------------------------------------------------
	//	shared method
    //------------------------------------------------------------------
    //  get match full constructor
    //------------------------------------------------------------------
    //  public static Constructor getMatchFullConstructor( String   classname,
    //                                                     Object[] args )
    //  public static Constructor getMatchFullConstructor( Class type, 
    //                                                     Object[] args )
    //  public static Constructor getMatchFullConstructor( String   classname,
    //                                            Object[] args, boolean all )
    //  public static Constructor getMatchFullConstructor( Class type, 
    //                                            Object[] args, boolean all )
    //------------------------------------------------------------------
    /**
     * classname で示されるクラスの public なコンストラクタのうち、
     * args を そのまま引数として受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     *                  この型のインスタンスを生成するための
     *                  コンストラクタを得る。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のコンストラクタ以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Constructor getMatchFullConstructor( String   classname,
                                                       Object[] args ) 
                                                throws  ClassNotFoundException {
        return Factory.getMatchFullConstructor( Class.forName( classname ),
                                                args );
    }

    /**
     * type の public なコンストラクタのうち、args を
     * そのまま引数として受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     *             この型のインスタンスを生成するためのコンストラクタを得る。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のコンストラクタ以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     */
    public static Constructor getMatchFullConstructor( Class    type, 
                                                       Object[] args ){
        return Factory.getMatchFullConstructor( type, args, false );
    }

    /**
     * classname で示されるクラスの コンストラクタのうち、
     * args を そのまま引数として受け入れることができるものを得る。
     * 
     * @param classname クラス名。
     *                  この型のインスタンスを生成するための
     *                  コンストラクタを得る。
     * @param args      引数配列。
     *                  null を含めても良いが、null を使用した場合は
     *                  Object のサブクラスであれば全てマッチしてしまうため、
     *                  目的のコンストラクタ以外のものが見つかる可能性がある。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     * 
     * @exception ClassNotFoundException
     *                 classname で示されるクラスが存在しない場合
     */
    public static Constructor getMatchFullConstructor( String   classname,
                                                       Object[] args,
                                                       boolean  all ) 
                                                throws  ClassNotFoundException {
        return Factory.getMatchFullConstructor( Class.forName( classname ),
                                                args, 
                                                all );
    }

    /**
     * type のコンストラクタのうち、args を
     * そのまま引数として受け入れることができるものを得る。
     * 
     * @param type 型情報。 
     *             この型のインスタンスを生成するためのコンストラクタを得る。
     * @param args 引数配列。
     *             null を含めても良いが、null を使用した場合は
     *             Object のサブクラスであれば全てマッチしてしまうため、
     *             目的のコンストラクタ以外のものが見つかる可能性がある。
     * @param all  public のコンストラクタのみを検索するなら false。
     *             public, protected, private, パッケージプライベートの
     *             全てのコンストラクタをけんさくするなら true。
     * 
     * @return args を引数に取ることができるコンストラクタ。
     *         見つからなければ null。
     */
    public static Constructor getMatchFullConstructor( Class    type, 
                                                       Object[] args,
                                                       boolean  all ){
        Constructor[] constructors = all 
                                   ? type.getDeclaredConstructors()
                                   : type.getConstructors();

        for( int i = 0 ; i < constructors.length ; i++ )
            if( Type.matchFullAll( constructors[i].getParameterTypes(), args ) )
                return constructors[i];
        
        return null;
    }

}
//end of Factory.java
