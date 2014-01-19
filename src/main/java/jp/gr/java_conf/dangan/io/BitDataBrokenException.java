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

package jp.gr.java_conf.dangan.io;

import java.io.IOException;

/**
 * EndOfStream に達してしまったため要求されたビット数の データを得られなかった場合に投げられる例外。<br>
 * jp.gr.java_conf.dangan.io.BitInputStream 用であるため、 保持しておける データは 32ビットまでとなっている点に 注意すること。<br>
 * NotEnoughBitsException と違い、こちらの例外を投げる 場合には 実際に読み込み動作を行ってしまっているため 読み込み位置は例外を投げる前の時点から変化してしまっ ている点に注意すること。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: BitDataBrokenException.java,v $
 * Revision 1.1  2002/12/07 00:00:00  dangan
 * [maintenance]
 *     ソース整備
 * 
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.1 $
 */
@SuppressWarnings("serial")
public class BitDataBrokenException extends IOException {

	/**
	 * ビットデータが途中までしか 取得できない原因となった例外
	 */
	private Throwable cause;

	/**
	 * 途中までのビットデータ
	 */
	private int bitData;

	/**
	 * bitData の有効ビット数
	 */
	private int bitCount;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * 新しい BitDataBrokenException を構築する。<br>
	 * 
	 * @param cause ビットデータが途中までしか取得できない 原因となった例外
	 * @param bitData 要求されたビット数に満たないビットデータ
	 * @param bitCount bitData のビット数
	 */
	public BitDataBrokenException(final Throwable cause, final int bitData, final int bitCount) {
		this.cause = cause;
		this.bitData = bitData;
		this.bitCount = bitCount;
	}

	// ------------------------------------------------------------------
	// access method

	/**
	 * ビットデータが途中までしか 取得できない原因となった例外を得る。<br>
	 * 
	 * @return 原因となった例外
	 */
	@Override
	public Throwable getCause() {
		return cause;
	}

	/**
	 * 要求されたビット数に満たない "壊れた" ビットデータを得る。<br>
	 * 
	 * @return ビットデータ
	 */
	public int getBitData() {
		return bitData;
	}

	/**
	 * getBitData() で得られる ビットデータの有効ビット数を得る。
	 * 
	 * @return ビットデータの有効ビット数
	 */
	public int getBitCount() {
		return bitCount;
	}

}