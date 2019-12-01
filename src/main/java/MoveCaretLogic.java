import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import java.util.List;
import org.jetbrains.annotations.NotNull;

class MoveCaretLogic {

  private MultiByteWordSeparate MBWS;

  String signMinus = "L";
  String signPlus = "R";

  MoveCaretLogic() {
    MBWS = new MultiByteWordSeparate();
  }

  boolean moveCaret(@NotNull AnActionEvent e, String sign) {
    /* 初期化 */
    MBWS.initialize(e);
    Caret primaryCaret = MBWS.editor.getCaretModel().getPrimaryCaret();
    Document document = MBWS.editor.getDocument();

    /* 現在のキャレット情報 */
    MBWS.getCurrentCaretInfo(primaryCaret);
    MBWS.getCurrentLineInfo(document, MBWS.visualPos);
    int currentCaretOffset = MBWS.currentCaretOffset;

    // テキスト取得範囲のオフセット
    int moveRange = 1;
    int textRangeStartOffset = MBWS.currentCaretOffset;
    int textRangeEndOffset = MBWS.endLineOffset;
    int boundaryOffset = textRangeEndOffset;
    if (sign.equals(signMinus)) {
      moveRange = -1;
      textRangeStartOffset = MBWS.startLineOffset;
      textRangeEndOffset = MBWS.currentCaretOffset;
      boundaryOffset = textRangeStartOffset;
    }

    int documentLength = document.getText().length();

    /* オフセットが移動できない位置なら終了 */
    if (currentCaretOffset < 0 || currentCaretOffset > documentLength) {
      return false;
    }

    /* 行頭なら強制で1文字分オフセットを移動する */
    if (MBWS.isLineBoundary(currentCaretOffset, boundaryOffset)) {
      primaryCaret.moveToOffset(currentCaretOffset + moveRange);
      return true;
    }

    // 現在のカーソル位置からのテキスト取得
    int textLength = MBWS.getTextLength(textRangeStartOffset, textRangeEndOffset);
    TextRange textRange = TextRange.from(textRangeStartOffset, textLength);
    String lineText = document.getText(textRange);

    // 単語種別に分割してList
    List<String> matchList = MBWS.wordParse(lineText);
    int matchSize = matchList.size();
    if (matchSize <= 0) {
      // 何もMatchしない場合、カーソルを一つ動かして処理を終了
      // TODO: 何もMatchしなかった場合、組み込みの移動コマンドに渡したい……
      primaryCaret.moveToOffset(currentCaretOffset + moveRange);
      return true;
    }

    // 取得する配列の位置
    int wordLength = getWordLength(sign, matchList);

    // 現在位置からカーソルの移動
    primaryCaret.moveToOffset(currentCaretOffset + wordLength);
    return true;
  }

  /**
   * Get character length
   *
   * @param sign      signMinus or signPlus
   * @param matchList MBWS.wordParse return value
   * @return Character length
   */
  private int getWordLength(String sign, List<String> matchList) {
    int matchSize = matchList.size();
    int position = 0;
    int orientation = 1;
    if (sign.equals(signMinus)) {
      position = matchSize - 1;
      orientation = -1;
    }
    String lastWord = matchList.get(position);
    return lastWord.length() * orientation;
  }
}
