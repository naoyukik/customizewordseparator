import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.VisualPosition;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;


class MultiByteWordSeparate {

  Editor editor;
  CaretModel caretModel;
  Caret primaryCaret;
  Document document;
  LogicalPosition logicalPos;
  VisualPosition visualPos;
  int currentCaretOffset;
  int startLineOffset;
  int endLineOffset;

  public MultiByteWordSeparate() {
  }

  void getCurrentCaretInfo(Caret primaryCaret) {
    /* 現在のキャレットポジション */
    logicalPos = primaryCaret.getLogicalPosition();
    visualPos = primaryCaret.getVisualPosition();

    /* 現在のキャレットオフセット */
    currentCaretOffset = primaryCaret.getOffset();
  }

  int getTextLength(int textRangeStartOffset, int textRangeEndOffset) {
    return textRangeEndOffset - textRangeStartOffset;
  }

  void initialize(@NotNull AnActionEvent e) {
    /* 初期化 */
    editor = e.getRequiredData(CommonDataKeys.EDITOR);
    caretModel = editor.getCaretModel();
    primaryCaret = caretModel.getPrimaryCaret();
    document = editor.getDocument();
  }

  boolean isLineBoundary(int currentCaretOffset, int boundaryOffset) {
    return currentCaretOffset == boundaryOffset;
  }


  void getCurrentLineInfo(Document document, VisualPosition visualPos) {
    /* 現在の行の情報 */
    startLineOffset = document.getLineStartOffset(visualPos.line);
    endLineOffset = document.getLineEndOffset(visualPos.line);
  }

  List<String> wordParse(String character) {
    return regularPattern(character);
  }

  private List<String> regularPattern(String word) {
    HashMap<String, String> mapPatterns = getPatterns();
    List<String> listPatterns = new ArrayList<>(mapPatterns.values());
    StringBuilder sb = new StringBuilder();
    for (String value : listPatterns) {
      sb.append(value);
      sb.append('|');
    }
    String pattern = sb.deleteCharAt(sb.length() - 1).toString();
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(word);

    return findAll(m);
  }

  private List<String> findAll(Matcher matcher) {
    List<String> matchArray = new ArrayList<>();
    while (matcher.find()) {
      int groupCount = matcher.groupCount();

      for (int i = 0; i <= groupCount; i++) {
        String g = matcher.group(i);
        matchArray.add(g);
      }
    }
    return matchArray;
  }

  private HashMap<String, String> getPatterns() {
    HashMap<String, String> patterns = new HashMap<>(15);
    patterns.put("cjk", "[\\u3400-\\u9FFF\\uF900-\\uFAFF]+");
    patterns.put("hiragana", "[\\u3040-\\u309F]+");
    patterns.put("katakana", "[\\u30A1-\\u30FA\\u30FC-\\u30FE]+");
    patterns.put("kanaSymbol", "[\\u30A0\\u30FB]+");
    patterns.put("cjkSymbol",
        "[\\u2190-\\u2193\\u25A0\\u25A1\\u25B2\\u25B3\\u25BC\\u25BD\\u25C6\\u25C7\\u25CB\\u25CE\\u25CF\\u2605\\u2606\\u3000-\\u3020]+");
    patterns.put("fullDigit", "[\\uFF10-\\uFF19]+");
    patterns.put("fullLatin", "[\\uFF21-\\uFF3A\\uFF41-\\uFF5A]+");
    patterns.put("halfCjkPunctuation", "[\\uFF61-\\uFF65]+");
    patterns.put("halfKatakana", "[\\uFF66-\\uFF9F]+");
    patterns.put("fullSymbol",
        "[\\uFF01-\\uFF0F\\uFF1A-\\uFF20\\uFF3B-\\uFF40\\uFF5B-\\uFF60\\uFFE0-\\uFFE6\\u005C\\u00A2\\u00A3\\u00A7\\u00A8\\u00AC\\u00B0\\u00B1\\u00B4\\u00B6\\u00D7\\u00F7\\u2010\\u2015\\u2016\\u2018\\u2019\\u201C\\u201D\\u2020\\u2021\\u2025\\u2026\\u2030\\u2032\\u2033\\u203B\\u2103]+");
    patterns.put("halfSymbol", "[\\uFFE8-\\uFFEE]+");
    patterns.put("latin", "[\\u0030-\\u0039\\u0041-\\u005A\\u0061-\\u007A\\u005F]+");
    patterns.put("latinSymbol",
        "[\\u0020-\\u002F\\u003A-\\u0040\\u005B-\\u005E\\u0060\\u00A5\\u007B-\\u007E\\u203E]+");
    patterns.put("czeroControls", "[\\u0000-\\u0009\\u000B\\u000C\\u000E-\\u001F]+");
    patterns.put("controlCharacters", "[\\u000A\\u000D]+");

    return patterns;
  }
}
