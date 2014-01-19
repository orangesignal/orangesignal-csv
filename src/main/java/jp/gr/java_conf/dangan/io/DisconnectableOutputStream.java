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
import java.io.OutputStream;

/**
 * データを処理して出力する出力ストリームと データをデバイスに出力するストリームとの 接続を解除するためのユーティリティクラス。<br>
 * 
 * <pre>
 * -- revision history --
 * $Log: DisconnectableOutputStream.java,v $
 * Revision 1.0  2002/07/24 00:00:00  dangan
 * add to version control
 * [maintenance]
 *     タブ廃止
 *     ライセンス文の修正
 *     ソース整備
 * 
 * </pre>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public class DisconnectableOutputStream extends OutputStream implements Disconnectable {

	/**
	 * 接続された出力ストリーム
	 */
	private OutputStream out;

	// ------------------------------------------------------------------
	// Constructer

	/**
	 * out との接続を解除可能な出力ストリームを構築する。
	 * 
	 * @param out 出力ストリーム
	 */
	public DisconnectableOutputStream(final OutputStream out) {
		if (out != null) {
			this.out = out;
		} else {
			throw new NullPointerException("out");
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream method

	/**
	 * 接続された出力ストリームに 1バイトのデータを出力する。<br>
	 * 
	 * @param data 書きこまれるべき 1バイトのデータ。<br>
	 * 一般的に上位3バイトは無視される。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final int data) throws IOException {
		out.write(data);                                                 // throws IOException
	}

	/**
	 * 接続された出力ストリームに buffer内のデータを 全て出力する。<br>
	 * 
	 * @param buffer 書きこまれるべきデータを格納した バイト配列。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer) throws IOException {
		out.write(buffer, 0, buffer.length);                             // throws IOException
	}

	/**
	 * 接続された出力ストリームに buffer内のデータを indexで指定された位置から lengthバイト出力する。<br>
	 * 
	 * @param buffer 書きこまれるべきデータを格納した バイト配列。<br>
	 * @param index buffer内の書きこむべきデータの開始位置。<br>
	 * @param length 書きこむべきデータ量。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer, final int index, final int length) throws IOException {
		out.write(buffer, index, length);                                // throws IOException
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream

	/**
	 * 接続された出力ストリームに蓄えられたデータを全て出力する ように指示する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void flush() throws IOException {
		out.flush();
	}

	/**
	 * 接続された出力ストリームとの接続を解除する。<br>
	 * このメソッドは disconnect() を呼び出すだけである。<br>
	 */
	@Override
	public void close() {
		disconnect();
	}

	// ------------------------------------------------------------------
	// method of jp.gr.java_conf.dangan.io.Disconnectable

	/**
	 * 接続された出力ストリームとの接続を解除する。<br>
	 */
	@Override
	public void disconnect() {
		out = null;
	}

}