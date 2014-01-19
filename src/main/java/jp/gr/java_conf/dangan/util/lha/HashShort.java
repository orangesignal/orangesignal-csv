/**
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

package jp.gr.java_conf.dangan.util.lha;

/**
 * データパタンの先頭2バイトから 0 ～ 4095 のハッシュ値を生成するハッシュ関数。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class HashShort implements HashMethod {

	/**
	 * LZSS圧縮を施すためのバッファ。 前半は辞書領域、 後半は圧縮を施すためのデータの入ったバッファ。 HashMethodの実装内では Hash値の生成のための読み込みにのみ使用する。
	 */
	private byte[] textBuffer;

	// ------------------------------------------------------------------
	// Constructor

	/**
	 * データパタンの先頭2バイトから 0x000 ～ 0xFFF までの値を生成する ハッシュ関数を構築する。
	 * 
	 * @param textBuffer LZSS圧縮用のバッファ。 Hash値生成のため読み込み用に使用する。
	 */
	public HashShort(final byte[] textBuffer) {
		this.textBuffer = textBuffer;
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.util.lha.HashMethod

	/**
	 * ハッシュ関数。 コンストラクタで渡された TextBuffer の position からの データパタンの hash値を生成する。
	 * 
	 * @param position データパタンの開始位置
	 * @return ハッシュ値
	 */
	@Override
	public int hash(final int position) {
		return ((textBuffer[position + 1] & 0x0F) << 8 | (textBuffer[position + 1] & 0xFF) >> 4) ^ (textBuffer[position] & 0xFF) << 2;
	}

	/**
	 * ハッシュ関数がハッシュ値を生成するために使用するバイト数を得る。<br>
	 * このハッシュ関数はデータパタンの先頭 2 バイトのデータから ハッシュ値を生成するため、このメソッドは常に 2 を返す。
	 * 
	 * @return 常に 2
	 */
	@Override
	public int hashRequires() {
		return 2;
	}

	/**
	 * ハッシュテーブルのサイズを得る。<br>
	 * このハッシュ関数は 0x000 ～ 0xFFF までのハッシュ値を生成するため このメソッドは常に 0x1000(4096) を返す。
	 * 
	 * @return 常に 0x1000(4096)
	 */
	@Override
	public int tableSize() {
		return 0x1000;
	}

}