/**
 * Copyright (C) 2001  Michel Ishizuka  All rights reserved.
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

import java.io.EOFException;
import java.io.IOException;

/**
 * LZSS圧縮コードを供給するインターフェイス。
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.0 $
 */
public interface PreLzssDecoder {

	/**
	 * 接続された入力ストリームの現在位置にマークを設定し、 reset() メソッドでマークした時点の読み込み位置に戻れるようにする。<br>
	 * InputStream の mark() と違い、 readLimit で設定した 限界バイト数より前にマーク位置が無効になってもかまわない。 ただし、readLimit を無視して無限に reset() 可能な InputStream と接続している場合は readLimit にどのような値を設定されても reset() で必ずマーク位置に復旧できなければならない。<br>
	 * 
	 * @param readLimit マーク位置に戻れる限界のバイト数。 このバイト数を超えてデータを読み込んだ場合 reset()できなくなる可能性がある。<br>
	 */
	void mark(int readLimit);

	/**
	 * 接続された入力ストリームの読み込み位置を最後に mark() メソッドが呼び出されたときの位置に設定する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	void reset() throws IOException;

	/**
	 * 接続された入力ストリームが mark() と reset() を サポートするかを得る。<br>
	 * 
	 * @return ストリームが mark() と reset() を サポートする場合は true。<br>サポートしない場合は false。<br>
	 */
	boolean markSupported();

	/**
	 * 接続された入力ストリームからブロックしないで 読み込むことのできる最低バイト数を得る。<br>
	 * この数値は完全である事を保障しなくてよい。 これは故意に作成されたデータ等ではブロックせずに 読み込む事の出来る最低バイト数を得るには 実際に読み込んでみる以外に方法がないためである。
	 * 
	 * @return ブロックしないで読み出せる最低バイト数。<br>
	 * @exception IOException 入出力エラーが発生した場合
	 */
	int available() throws IOException;

	/**
	 * この入力ストリームを閉じ、使用していた 全てのリソースを開放する。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	void close() throws IOException;

	/**
	 * 1byte の LZSS未圧縮のデータもしくは、 LZSS で圧縮された圧縮コードのうち一致長を読み込む。<br>
	 * 未圧縮データは 0～255、 LZSS圧縮コード(一致長)は 256～511 の値を取らなければならない。<br>
	 * 
	 * @return 1byte の LZSS未圧縮のデータもしくは、 LZSS で圧縮された圧縮コードのうち一致長
	 * @exception IOException 入出力エラーが発生した場合
	 * @exception EOFException EndOfStreamに達した場合<br>
	 */
	int readCode() throws IOException;

	/**
	 * LZSS で圧縮された圧縮コードのうち一致位置を読み込む。<br>
	 * 
	 * @return LZSS で圧縮された圧縮コードのうち一致位置
	 * @exception IOException 入出力エラーが発生した場合
	 */
	int readOffset() throws IOException;

	/**
	 * このPreLzssDecoderが処理するLZSS辞書のサイズを得る。
	 * 
	 * @return LZSS辞書のサイズ
	 */
	int getDictionarySize();

	/**
	 * このPreLzssDecoderが処理する最長一致長を得る。
	 * 
	 * @return 最長一致長
	 */
	int getMaxMatch();

	/**
	 * このPreLzssDecoderが処理する圧縮、非圧縮の閾値を得る。
	 * 
	 * @return LZSSの閾値
	 */
	int getThreshold();

}