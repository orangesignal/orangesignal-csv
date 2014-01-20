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

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;

/**
 * データを LZSS圧縮しながら 指定された PostLzssEncoder に出力する圧縮用出力ストリーム。<br>
 * 
 * @author $Author: dangan $
 * @version $Revision: 1.2 $
 */
public class LzssOutputStream extends OutputStream {

	/**
	 * lastsearchret に登録する値。 searchAndPutの処理が必要である事を示す。
	 */
	private static final int NEEDSEARCH = 0;

	/**
	 * searchret がこの値だった場合、 検索の結果、閾値以上の一致が見つからなかった事を示す。
	 */
	public static final int NOMATCH = -1;

	/**
	 * LZSS圧縮コードを排出する先の出力ストリーム
	 */
	private PostLzssEncoder encoder;

	/**
	 * LZSS辞書サイズ。
	 */
	private int dictionarySize;

	/**
	 * LZSS圧縮に使用される閾値。 一致長が この値以上であれば、圧縮コードを出力する。
	 */
	private int threshold;

	/**
	 * LZSS圧縮に使用される値。 最大一致長を示す。
	 */
	private int maxMatch;

	/**
	 * LZSS圧縮を施すためのバッファ。 前半は辞書領域、 後半は圧縮を施すためのデータの入ったバッファ。
	 */
	private byte[] textBuffer;

	/**
	 * 辞書の限界位置。 TextBuffer前半の辞書領域にデータが無い場合に 辞書領域にある不定のデータ(Javaでは0)を使用 して圧縮が行われるのを抑止する。
	 */
	private int dictionaryLimit;

	/**
	 * TextBuffer内書き込み完了位置 LzssOutputStream.write() によって書き込まれた位置
	 * 
	 * 以下の3者の関係は putPos <= searchedPos <= writtenPos となる。
	 */
	private int writtenPos;

	/**
	 * TextBuffer内 put() 完了位置 LzssSearchMethod の put() もしくは searchAndPut() で 検索機構への登録が完了した位置
	 */
	private int putPos;

	/**
	 * TextBuffer内 現在検索位置 次に LzssSearchMethod の search() もしくは searchAndPut() で 検索をすべき位置
	 */
	private int searchPos;

	/**
	 * 前回のencodeの最後のsearchretを保存しておく コンストラクタでは lastsearchret に無効な 数字である事を示す LzssOutputStream.NEEDSEARCHを 入力しておく。
	 */
	private int lastsearchret;

	/**
	 * 検索をつかさどるクラス
	 */
	private LzssSearchMethod method;

	/**
	 * write() によって書きこまれたデータを LZSSで圧縮し、圧縮したデータを encoderに出力する 出力ストリームを構築する。
	 * 
	 * @param encoder LZSS圧縮データ出力ストリーム
	 */
	public LzssOutputStream(final PostLzssEncoder encoder) {
		this(encoder, HashAndChainedListSearch.class.getName(), new Object[0]);
	}

	/**
	 * write() によって書きこまれたデータを LZSSで圧縮し、圧縮したデータを encoderに出力する 出力ストリームを構築する。
	 * 
	 * @param encoder LZSS圧縮データ出力ストリーム
	 * @param LzssSearchMethodClassName LzssSearchMethod の実装を示すパッケージ名も含めたクラス名
	 * @exception NoClassDefFoundError LzssSearchMethodClassName で与えられたクラスが 見つからない場合。
	 * @exception InstantiationError LzssSearchMethodClassName で与えられたクラスが abstract class であるためインスタンスを生成できない場合。
	 * @exception NoSuchMethodError LzssSearchMethodClassName で与えられたクラスが コンストラクタ LzssSearchMethod( int, int, int, byte[], int ) を持たない場合
	 */
	public LzssOutputStream(final PostLzssEncoder encoder, final String LzssSearchMethodClassName) {
		this(encoder, LzssSearchMethodClassName, new Object[0]);
	}

	/**
	 * write() によって書きこまれたデータを LZSSで圧縮し、圧縮したデータを encoderに出力する 出力ストリームを構築する。
	 * 
	 * @param encoder LZSS圧縮データ出力ストリーム
	 * @param LzssSearchMethodClassName LzssSearchMethod の実装を示すパッケージ名も含めたクラス名
	 * @exception NoClassDefFoundError LzssSearchMethodClassName で与えられたクラスが 見つからない場合。
	 * @exception InstantiationError LzssSearchMethodClassName で与えられたクラスが abstract class であるためインスタンスを生成できない場合。
	 * @exception NoSuchMethodError LzssSearchMethodClassName で与えられたクラスが コンストラクタ LzssSearchMethod( int, int, int, byte[] ) を持たない場合
	 */
	public LzssOutputStream(final PostLzssEncoder encoder, final String LzssSearchMethodClassName, final Object[] LzssSearchMethodExtraArguments) {
		dictionarySize = encoder.getDictionarySize();
		maxMatch = encoder.getMaxMatch();
		threshold = encoder.getThreshold();

		this.encoder = encoder;
		textBuffer = new byte[dictionarySize * 2 + maxMatch];
		writtenPos = dictionarySize;
		putPos = dictionarySize;
		searchPos = dictionarySize;
		dictionaryLimit = dictionarySize;
		lastsearchret = NEEDSEARCH;

		final Object[] arguments = new Object[LzssSearchMethodExtraArguments.length + 4];
		arguments[0] = new Integer(dictionarySize);
		arguments[1] = new Integer(maxMatch);
		arguments[2] = new Integer(threshold);
		arguments[3] = textBuffer;
		for (int i = 0; i < LzssSearchMethodExtraArguments.length; i++) {
			arguments[4 + i] = LzssSearchMethodExtraArguments[i];
		}

		try {
			method = (LzssSearchMethod) Factory.createInstance(LzssSearchMethodClassName, arguments);                                        // throw ClasNotfoundException, InvocationTargetException, NoSuchMethodException, InstantiationException
		} catch (final ClassNotFoundException exception) {
			throw new NoClassDefFoundError(exception.getMessage());
		} catch (final InvocationTargetException exception) {
			throw new Error(exception.getTargetException().getMessage());
		} catch (final NoSuchMethodException exception) {
			throw new NoSuchMethodError(exception.getMessage());
		} catch (final InstantiationException exception) {
			throw new InstantiationError(exception.getMessage());
		}
	}

	// ------------------------------------------------------------------
	// method of java.io.OutputStream method

	/**
	 * 圧縮機構に1バイトのデータを出力する。<br>
	 * 実際にPostLzssEncoder にデータが渡されるのは TextBuffer が満たされたときか、 flush で明示的に出力を指示した時のみ。<br>
	 * 
	 * @param data 1バイトのデータ
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final int data) throws IOException {
		textBuffer[writtenPos++] = (byte) data;

		if (textBuffer.length <= writtenPos) {
			encode(false);                                               // throws IOException
			slide();
		}
	}

	/**
	 * 圧縮機構に buffer 内のデータを全て出力する。<br>
	 * 実際にPostLzssEncoder にデータが渡されるのは TextBuffer が満たされたときか、 flush で明示的に出力を指示した時のみ。<br>
	 * 
	 * @param buffer データの格納されたバッファ
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer) throws IOException {
		this.write(buffer, 0, buffer.length);                                 // throws IOException
	}

	/**
	 * 圧縮機構に buffer 内の index から lengthバイトのデータを出力する。<br>
	 * 実際にPostLzssEncoder にデータが渡されるのは TextBuffer が満たされたときか、 flush で明示的に出力を指示した時のみ。<br>
	 * 
	 * @param buffer データの格納されたバッファ
	 * @param index buffer内データ開始位置
	 * @param length buffer内データの長さ
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void write(final byte[] buffer, final int index, final int length) throws IOException {
		int pos = index;
		final int end = index + length;

		while (pos < end) {
			final int space = textBuffer.length - writtenPos;
			if (end - pos < space) {
				System.arraycopy(buffer, pos, textBuffer, writtenPos, end - pos);
				writtenPos += end - pos;
				pos = end;
			} else {
				System.arraycopy(buffer, pos, textBuffer, writtenPos, space);
				writtenPos += space;
				pos += space;
				encode(false);                                           // throws IOException
				slide();
			}
		}
	}

	/**
	 * 圧縮機構に書き込まれた全てのデータを 接続された PostLzssEncoder に出力し、 接続された PostLzssEncoder を flush() する。<br>
	 * このとき、出力するデータの終端付近では 検索に search() を使用するため圧縮速度が低下する。 また flush() しない場合と比べて圧縮率が変化する。 これは flush() した位置付近ではデータパタンの検索に MaxMatch に満たないデータパタンを使用するため、 検索結果が不完全になるため。 この圧縮率の変化は、多くの場合圧縮率が少々低下するだけであるが、 例えば次のようなコードは LZ 圧縮を全く行わない。
	 * 
	 * <pre>
	 *  public void wrongCompress( InputStream in, LzssOutputSteam out ){
	 *      int r;
	 *      while( 0 <= r = in.read() ){
	 *          out.write( r );
	 *          out.flush();
	 *      }
	 *  }
	 * </pre>
	 * 
	 * また、このメソッドは PostLzssEncoder.flush() を呼び出すため flush() しない場合と比べて、出力データが変化する可能性がある。<br>
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 * @see PostLzssEncoder#flush()
	 */
	@Override
	public void flush() throws IOException {
		encode(false);                                                   // throw IOException
		if (dictionarySize * 2 <= putPos) {
			slide();
			if (searchPos < writtenPos) {
				encode(false);                                           // throw IOException
			}
		}
		encoder.flush();                                                   // throw IOException
	}

	/**
	 * このクラスに貯えられた全てのデータを接続された PostLzssEncoder に出力し この出力ストリームと、 接続されたストリームを閉じ、 使用していたリソースを開放する。
	 * 
	 * @exception IOException 入出力エラーが発生した場合
	 */
	@Override
	public void close() throws IOException {
		while (dictionarySize <= writtenPos) {
			encode(true);                                      // throw IOException
			if (writtenPos <= searchPos) {
				break;
			}
			slide();
		}

		encoder.close();                                                   // throw IOException
		encoder = null;

		textBuffer = null;
		method = null;
	}

	// ------------------------------------------------------------------
	// local method

	/**
	 * TextBuffer に貯えられたデータを圧縮しながら private変数 this.encoder に出力する。
	 * 
	 * @return TextBuffer 内の出力完了したデータの終端位置 + 1
	 * @exception IOException 入出力エラーが発生した場合
	 */
	private void encode(final boolean last) throws IOException {
		int end = Math.min(textBuffer.length - maxMatch, writtenPos - (last ? 0 : method.putRequires()));
		if (searchPos < end) {

			// ------------------------------------------------------------------
			// 前処理
			if (lastsearchret == NEEDSEARCH) {

				// ------------------------------------------------------------------
				// 検索機構に未登録のデータパタンを登録
				while (putPos < searchPos - 1) {
					method.put(++putPos);

					// 直前の flush() で put() できなかった
					// データパタンを put() しただけの場合は return
					if (dictionarySize * 2 <= putPos) {
						return;
					}
				}

				// lastsearchret が NEEDSEARCH なので searchAndPut で検索する。
				lastsearchret = method.searchAndPut(searchPos);
			}

			int searchret = lastsearchret;
			int matchlen = getMatchLen(searchret);
			int matchpos = getMatchPos(searchret);
			if (writtenPos - searchPos < matchlen) {
				matchlen = writtenPos - searchPos;
			}

			// ------------------------------------------------------------------
			// メインループ
			while (true) {
				int lastmatchlen = matchlen;
				final int lastmatchoff = searchPos - matchpos - 1;

				searchret = method.searchAndPut(++searchPos);
				matchlen = getMatchLen(searchret);
				matchpos = getMatchPos(searchret);
				if (writtenPos - searchPos < matchlen) {
					matchlen = writtenPos - searchPos;
				}

				if (lastmatchlen < matchlen || lastmatchlen < threshold) {
					encoder.writeCode(0xFF & textBuffer[searchPos - 1]); // throws IOException
					if (end <= searchPos) {
						putPos = searchPos;
						lastsearchret = searchret;
						break;
					}
				} else {
					encoder.writeCode(256 + lastmatchlen - threshold);// throws IOException
					encoder.writeOffset(lastmatchoff);                   // throws IOException

					lastmatchlen--;
					if (searchPos + lastmatchlen < end) {
						while (0 < --lastmatchlen) {
							method.put(++searchPos);
						}

						searchret = method.searchAndPut(++searchPos);
						matchlen = getMatchLen(searchret);
						matchpos = getMatchPos(searchret);
						if (writtenPos - searchPos < matchlen) {
							matchlen = writtenPos - searchPos;
						}
					} else if (end < searchPos + lastmatchlen) {
						putPos = searchPos;
						while (putPos < end) {
							method.put(++putPos);
						}
						searchPos += lastmatchlen;
						lastsearchret = NEEDSEARCH;
						break;
					} else {
						putPos = searchPos;
						while (putPos < end - 1) {
							method.put(++putPos);
						}
						putPos++;
						searchPos += lastmatchlen;
						lastsearchret = method.searchAndPut(searchPos);
						break;
					}
				}// if( lastmatchlen < matchlen || lastmatchlen < this.Threshold )
			}// while( true )
		}// if( this.searchPos < end )

		// ------------------------------------------------------------------
		// flush() 専用
		// putPos はそのままで searchPos のみ進める。
		end = Math.min(textBuffer.length - maxMatch, writtenPos);
		if (!last && searchPos < end) {
			if (lastsearchret == NEEDSEARCH) {
				lastsearchret = method.search(searchPos, putPos);
			}
			int searchret = lastsearchret;
			int matchlen = getMatchLen(searchret);
			int matchpos = getMatchPos(searchret);
			if (writtenPos - searchPos < matchlen) {
				matchlen = writtenPos - searchPos;
			}

			while (searchPos < end) {
				final int lastmatchlen = matchlen;
				final int lastmatchoff = searchPos - matchpos - 1;

				searchret = method.search(++searchPos, putPos);
				matchlen = getMatchLen(searchret);
				matchpos = getMatchPos(searchret);
				if (writtenPos - searchPos < matchlen) {
					matchlen = writtenPos - searchPos;
				}

				if (lastmatchlen < matchlen || lastmatchlen < threshold) {
					encoder.writeCode(0xFF & textBuffer[searchPos - 1]); // throws IOException
				} else {
					encoder.writeCode(256 + lastmatchlen - threshold);  // throws IOException
					encoder.writeOffset(lastmatchoff);                       // throws IOException

					searchPos += lastmatchlen - 1;
					searchret = method.search(searchPos, putPos);
					matchlen = getMatchLen(searchret);
					matchpos = getMatchPos(searchret);
					if (writtenPos - searchPos < matchlen) {
						matchlen = writtenPos - searchPos;
					}
				}
			}
			lastsearchret = NEEDSEARCH;
		}
	}

	/**
	 * TextBuffer内のpositionまでのデータを 前方へ移動する
	 * 
	 * @param position 次に TextBuffer内で DictionarySize の位置に来るべき 要素が現在あるindex
	 */
	private void slide() {
		dictionaryLimit = Math.max(0, dictionaryLimit - dictionarySize);

		method.slide();

		if (lastsearchret != NEEDSEARCH) {
			final int matchlen = getMatchLen(lastsearchret);
			final int matchpos = getMatchPos(lastsearchret);
			lastsearchret = createSearchReturn(matchlen, matchpos - dictionarySize);
		}

		writtenPos -= dictionarySize;
		searchPos -= dictionarySize;
		putPos -= dictionarySize;
		for (int i = dictionaryLimit; i < writtenPos; i++) {
			textBuffer[i] = textBuffer[i + dictionarySize];
		}
	}

	// ------------------------------------------------------------------
	// shared methods

	/**
	 * search の戻り値を生成する。 search は一致位置を返すが、一致長も同時に返したほうが 非常に便利であるため、一致位置も一致長も必要なビット数が 少ないことを利用して int型でやり取りする。 そのための統一した処理を約束する関数。 この関数で生成された値から 一致位置や一致長を取り出す際には getMatchLen、 getMatchPos を使用する。
	 * 
	 * @param matchlen 一致長
	 * @param matchpos 一致位置
	 * 
	 * @return 一致長と一致位置の情報を含むsearchの戻り値
	 */
	public static final int createSearchReturn(final int matchlen, final int matchpos) {
		return matchlen << 22 | matchpos;
	}

	/**
	 * createSearchReturn で生成された searchの戻り値から 一致長を取り出す。
	 * 
	 * @param searchret search の戻り値
	 * 
	 * @return 一致長
	 */
	public static final int getMatchLen(final int searchret) {
		return searchret >> 22;
	}

	/**
	 * createSearchReturn で生成された searchの戻り値から 一致位置を取り出す。
	 * 
	 * @param searchret search の戻り値
	 * 
	 * @return 一致位置
	 */
	public static final int getMatchPos(final int searchret) {
		if (0 <= searchret) {
			return searchret & 0x3FFFFF;
		}
		return -1;
	}

}