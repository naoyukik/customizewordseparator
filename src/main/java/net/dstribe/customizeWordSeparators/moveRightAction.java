package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class moveRightAction extends AnAction {

  private MoveCaretLogic MCL;

  public moveRightAction() {
    MCL = new MoveCaretLogic();
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    // キーの移動距離
    MCL.moveCaret(e, MCL.signPlus, false);

//    /* 初期化 */
//    MBWS.initialize(e);
//    Caret primaryCaret = MBWS.editor.getCaretModel().getPrimaryCaret();
//    Document document = MBWS.editor.getDocument();
//
//    // キーの移動距離
//    int moveRange = 1;
//
//    /* 現在のキャレット情報 */
//    MBWS.getCurrentCaretInfo(primaryCaret);
//    MBWS.getCurrentLineInfo(document, MBWS.visualPos);
//    int currentCaretOffset = MBWS.currentCaretOffset;
//
//    // テキスト取得範囲のオフセット
//    int textRangeStartOffset = MBWS.currentCaretOffset;
//    int textRangeEndOffset = document.getLineEndOffset(MBWS.visualPos.line);
//
//    int documentLength = document.getText().length();
//
//    /* オフセットが移動できない位置なら終了 */
//    if (currentCaretOffset < 0 || currentCaretOffset > documentLength) {
//      return;
//    }
//
//    /* 行末なら強制で1文字分オフセットを移動する */
//    if (MBWS.isLineBoundary(currentCaretOffset, textRangeEndOffset)) {
//      primaryCaret.moveToOffset(currentCaretOffset + moveRange);
//      return;
//    }
//
//    // 現在のカーソル位置からのテキスト取得
//    int textLength = MBWS.getTextLength(textRangeStartOffset, textRangeEndOffset);
//    TextRange textRange = TextRange.from(textRangeStartOffset, textLength);
//    String lineText = document.getText(textRange);
//
//    // 単語種別に分割してList
//    List<String> matchList = MBWS.wordParse(lineText);
//    int matchSize = matchList.size();
//    if (matchSize <= 0) {
//      // 何もMatchしない場合、処理を終了
//      // TODO: 何もMatchしなかった場合、組み込みの移動コマンドに渡したい……
//      return;
//    }
//
//    // 行頭から取得
//    int positionGet = 0;
//    String selectedWord = matchList.get(positionGet);
//    int wordLength = selectedWord.length();
//
//    // 現在位置からカーソルの移動
//    primaryCaret.moveToOffset(currentCaretOffset + wordLength);

    //      System.out.println(primaryCaret.getSelectedText());
    ////    moveCaret(matchList, true);
//    // get Word under carsor
//    // primaryCaret.moveToOffset(1);
////        SelectionModel selectionModel = editor.getSelectionModel();
////        boolean hasSelection = selectionModel.hasSelection();
////        System.out.println( hasSelection );
////        int startOffset = visualPos.getColumn()-10;
////        primaryCaret.setSelection(visualPos.getLine(), startOffset);
////        System.out.println(primaryCaret.getVisualPosition());
  }
}
