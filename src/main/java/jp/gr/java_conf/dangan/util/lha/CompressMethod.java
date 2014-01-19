/**
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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Properties;

/**
 * LHAの各種定数を定義する。
 * 
 * <pre>
 * -- revision history --
 * $Log: CompressMethod.java,v $
 * Revision 1.1  2002/12/08 00:00:00  dangan
 * [change]
 *     クラス名を LhaConstants から CompressMethod へと変更。
 * 
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [change]
 *     LhaUtil の connectExtractInputStream を connectDecoder として
 *     connectCompressOutputStream を connectEncoder として引き継ぐ。
 *     LhaUtil の CompressMethodTo????????? を引き継ぐ。
 * [maintanance]
 *     ソース整備
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
final class CompressMethod {

	/**
	 * 圧縮形式を示す文字列。 LH0 は 無圧縮を示す "-lh0-" である。
	 */
	public static final String LH0 = "-lh0-";

	/**
	 * 圧縮形式を示す文字列。 LH1 は前段に 4キロバイトの辞書、最大一致長60バイトの LZSS法、後段に 適応的ハフマン法を使用することを意味する "-lh1-" である。
	 */
	public static final String LH1 = "-lh1-";

	/**
	 * 圧縮形式を示す文字列。 LH2 は前段に 8キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 適応的ハフマン法を使用することを意味する "-lh2-" である。 この圧縮法は LH1 から LH5 への改良途中で試験的に 使われたが、現在は使用されていない。
	 */
	public static final String LH2 = "-lh2-";

	/**
	 * 圧縮形式を示す文字列。 LH3 は前段に 8キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 静的ハフマン法を使用することを意味する "-lh3-" である。 この圧縮法は LH1 から LH5 への改良途中で試験的に 使われたが、現在は使用されていない。
	 */
	public static final String LH3 = "-lh3-";

	/**
	 * 圧縮形式を示す文字列。 LH4 は前段に 4キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 静的ハフマン法を使用することを意味する "-lh4-" である。 この圧縮法は 1990年代前半の非力なマシン上で圧縮を行う際、 LH5圧縮を行うだけのシステム資源を得られなかった時に使わ れたが、現在は殆ど使用されていない。
	 */
	public static final String LH4 = "-lh4-";

	/**
	 * 圧縮形式を示す文字列。 LH5 は前段に 8キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 静的ハフマン法を使用することを意味する "-lh5-" である。 現在、LHAで標準で使用される圧縮法である。
	 */
	public static final String LH5 = "-lh5-";

	/**
	 * 圧縮形式を示す文字列。 LH6 は前段に 32キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 静的ハフマン法を使用することを意味する "-lh6-" である。 "-lh6-" という文字列は LH7 の圧縮法の実験に使用されて いた。そのため、LHAの実験版が作成した書庫には "-lh6-" の文字列を使用しながら LH7 形式で圧縮されているものが 存在するらしい。 また この圧縮法は開発されてから 10年近く経つが未だに 公の場所に この圧縮法で圧縮された書庫は登録しないこと が望ましいとされている。
	 */
	public static final String LH6 = "-lh6-";

	/**
	 * 圧縮形式を示す文字列。 LH7 は前段に 64キロバイトの辞書、最大一致長256バイトの LZSS法、後段に 静的ハフマン法を使用することを意味する "-lh7-" である。 また この圧縮法は開発されてから 10年近く経つが未だに 公の場所に この圧縮法で圧縮された書庫は登録しないこと が望ましいとされている。
	 */
	public static final String LH7 = "-lh7-";

	/**
	 * 圧縮形式を示す文字列。 LHD は無圧縮で、ディレクトリを格納していることを示す "-lhd-" である。
	 */
	public static final String LHD = "-lhd-";

	/**
	 * 圧縮形式を示す文字列。 LZS は 2キロバイトの辞書、最大一致長17バイトの LZSS法を使用することを示す "-lzs-" である。 "-lzs-" は LHAが作成される前にメジャーであった Larc の形式であり、当時の互換性に配慮して定義さ れた。現在は殆ど使用されていない。
	 */
	public static final String LZS = "-lzs-";

	/**
	 * 圧縮形式を示す文字列。 LZ4 は 無圧縮を示す "-lz4-" である。 "-lz4-" は LHAが作成される前にメジャーであった Larc の形式であり、当時の互換性に配慮して定義さ れた。現在は殆ど使用されていない。
	 */
	public static final String LZ4 = "-lz4-";

	/**
	 * 圧縮形式を示す文字列。 LZ5 は 4キロバイトの辞書、最大一致長17バイトの LZSS法を使用することを示す "-lz5-" である。 "-lz5-" は LHAが作成される前にメジャーであった Larc の形式であり、当時の互換性に配慮して定義さ れた。現在は殆ど使用されていない。
	 */
	public static final String LZ5 = "-lz5-";

	/**
	 * デフォルトコンストラクタ使用不可
	 */
	private CompressMethod() {}

	// ------------------------------------------------------------------
	// convert to LZSS parameter

	/**
	 * 圧縮法識別子から 辞書サイズを得る。
	 * 
	 * @param method 圧縮法識別子
	 * @return 辞書サイズ
	 */
	public static int toDictionarySize(final String method) {
		if (CompressMethod.LZS.equalsIgnoreCase(method)) {
			return 2048;
		} else if (CompressMethod.LZ5.equalsIgnoreCase(method)) {
			return 4096;
		} else if (CompressMethod.LH1.equalsIgnoreCase(method)) {
			return 4096;
		} else if (CompressMethod.LH2.equalsIgnoreCase(method)) {
			return 8192;
		} else if (CompressMethod.LH3.equalsIgnoreCase(method)) {
			return 8192;
		} else if (CompressMethod.LH4.equalsIgnoreCase(method)) {
			return 4096;
		} else if (CompressMethod.LH5.equalsIgnoreCase(method)) {
			return 8192;
		} else if (CompressMethod.LH6.equalsIgnoreCase(method)) {
			return 32768;
		} else if (CompressMethod.LH7.equalsIgnoreCase(method)) {
			return 65536;
		} else if (CompressMethod.LZ4.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LH0.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LHD.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (method == null) {
			throw new NullPointerException("method");
		} else {
			throw new IllegalArgumentException("Unknown compress method. " + method);
		}
	}

	/**
	 * 圧縮法識別子から 圧縮/非圧縮の閾値を得る。
	 * 
	 * @param method 圧縮法識別子
	 * @return 圧縮/非圧縮
	 */
	public static int toThreshold(final String method) {
		if (CompressMethod.LZS.equalsIgnoreCase(method)) {
			return 2;
		} else if (CompressMethod.LZ5.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH1.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH2.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH3.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH4.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH5.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH6.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LH7.equalsIgnoreCase(method)) {
			return 3;
		} else if (CompressMethod.LZ4.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LH0.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LHD.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (method == null) {
			throw new NullPointerException("method");
		} else {
			throw new IllegalArgumentException("Unknown compress method. " + method);
		}
	}

	/**
	 * 圧縮法識別子から 最大一致長を得る。
	 * 
	 * @param method 圧縮法識別子
	 * @return 最大一致長
	 */
	public static int toMaxMatch(final String method) {
		if (CompressMethod.LZS.equalsIgnoreCase(method)) {
			return 17;
		} else if (CompressMethod.LZ5.equalsIgnoreCase(method)) {
			return 18;
		} else if (CompressMethod.LH1.equalsIgnoreCase(method)) {
			return 60;
		} else if (CompressMethod.LH2.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LH3.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LH4.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LH5.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LH6.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LH7.equalsIgnoreCase(method)) {
			return 256;
		} else if (CompressMethod.LZ4.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LH0.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (CompressMethod.LHD.equalsIgnoreCase(method)) {
			throw new IllegalArgumentException(method + " means no compress.");
		} else if (method == null) {
			throw new NullPointerException("method");
		} else {
			throw new IllegalArgumentException("Unknown compress method. " + method);
		}
	}

	// ------------------------------------------------------------------
	// shared method

	/**
	 * property に設定された生成式を利用して method の圧縮法でデータを圧縮し、outに出力するストリームを構築する。
	 * 
	 * @param out 圧縮データ出力先のストリーム
	 * @param method 圧縮法識別子
	 * @param property 各圧縮形式に対応した符号器の生成式等が含まれるプロパティ
	 * @return method の圧縮法でデータを圧縮し、outに出力するストリーム
	 */
	public static OutputStream connectEncoder(final OutputStream out, final String method, final Properties property) {
		final String key = "lha." + CompressMethod.getCore(method) + ".encoder";

		String generator = property.getProperty(key);
		if (generator == null) {
			generator = LhaProperty.getProperty(key);
		}

		String packages = property.getProperty("lha.packages");
		if (packages == null) {
			packages = LhaProperty.getProperty("lha.packages");
		}

		final Hashtable substitute = new Hashtable();
		substitute.put("out", out);

		return (OutputStream) LhaProperty.parse(generator, substitute, packages);
	}

	/**
	 * property に設定された生成式を利用して in から method の圧縮法で圧縮されたデータを解凍し 供給する入力ストリームを構築する。
	 * 
	 * @param in 圧縮データを供給するストリーム
	 * @param method 圧縮法識別子
	 * @param property 各圧縮形式に対応した復号器の生成式等が含まれるプロパティ
	 * @return in から method の圧縮法で圧縮されたデータを解凍し 供給する入力ストリームを構築する。
	 */
	public static InputStream connectDecoder(final InputStream in, final String method, final Properties property, final long length) {
		final String key = "lha." + CompressMethod.getCore(method) + ".decoder";

		String generator = property.getProperty(key);
		if (generator == null) {
			generator = LhaProperty.getProperty(key);
		}

		String packages = property.getProperty("lha.packages");
		if (packages == null) {
			packages = LhaProperty.getProperty("lha.packages");
		}

		final Hashtable substitute = new Hashtable();
		substitute.put("in", in);
		substitute.put("length", new Long(length));

		return (InputStream) LhaProperty.parse(generator, substitute, packages);
	}

	// ------------------------------------------------------------------
	// local method
	/**
	 * 圧縮法識別子 の前後の '-' を取り去って LhaProperty のキー lha.???.encoder / lha.???.decoder の ??? に入る文字列を生成する。
	 * 
	 * @param method 圧縮法識別子
	 * @return キーの中心に使える文字列
	 */
	private static String getCore(final String method) {
		if (method.startsWith("-") && method.endsWith("-")) {
			return method.substring(1, method.lastIndexOf('-')).toLowerCase();
		}
		throw new IllegalArgumentException("");
	}

}