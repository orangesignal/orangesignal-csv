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

package com.orangesignal.jlha;

import java.util.zip.Checksum;

/**
 * LHAで使用される 単純な 1バイトのチェックサム値を 算出するためのクラス。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class LhaChecksum implements Checksum {

	/**
	 * チェックサム値
	 */
	private int checksum;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * 新しい チェックサムクラスを作成する。
	 */
	public LhaChecksum() {
		super();
		reset();
	}

	// ------------------------------------------------------------------
	// public method

	/**
	 * buffer で指定したバイト配列で チェックサム値を更新する。 このメソッドは update( buffer, 0, buffer.length ) と同等。
	 * 
	 * @param buffer チェックサムを更新するデータを持つバイト配列
	 */
	public void update(final byte[] buffer) {
		this.update(buffer, 0, buffer.length);
	}

	// ------------------------------------------------------------------
	// method of java.util.zip.Checksum method

	/**
	 * byte8 で指定した 1バイトのデータで チェックサム値を更新する。
	 * 
	 * @param byte8 チェックサムを更新する1バイトのデータ
	 */
	@Override
	public void update(final int byte8) {
		checksum += byte8;
	}

	/**
	 * buffer で指定したバイト配列で チェックサム値を更新する。
	 * 
	 * @param buffer チェックサムを更新するデータを持つバイト配列
	 * @param index データの開始位置
	 * @param length チェックサムの更新に使うバイト数
	 */
	@Override
	public void update(final byte[] buffer, int index, int length) {
		while (0 < length--) {
			checksum += buffer[index++];
		}
	}

	/**
	 * チェックサム値を初期値に設定しなおす。
	 */
	@Override
	public void reset() {
		checksum = 0;
	}

	/**
	 * チェックサム値を得る。 チェックサム値は 1バイトの値であり、 0x00～0xFFにマップされる。
	 * 
	 * @return チェックサム値
	 */
	@Override
	public long getValue() {
		return checksum & 0xFF;
	}

}