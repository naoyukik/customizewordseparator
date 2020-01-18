package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorActionManager;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MoveCaretWordUtil {

  /**
   * @param editor          openapi.Editor
   * @param caret           openapi.Caret
   * @param isNext          true when move to next
   * @param isWithSelection true when with selection
   * @param dataContext     openapi.DataContext
   * @param camel           true when camelHumps
   */
  public static void moveCaretWord(@NotNull Editor editor,
                                   @NotNull Caret caret,
                                   boolean isNext,
                                   boolean isWithSelection,
                                   DataContext dataContext,
                                   boolean camel) {
    final Document document = editor.getDocument();
    VisualPosition visualPos = caret.getVisualPosition();
    LogicalPosition logicalPos = caret.getLogicalPosition();
    int currentCaretOffset = caret.getOffset();
    int selectionStart = caret.getLeadSelectionOffset();

    if (isNext && currentCaretOffset == document.getTextLength()) return;

    int newOffset;

    FoldRegion currentFoldRegion = editor.getFoldingModel().getCollapsedRegionAtOffset(currentCaretOffset);
    if (currentFoldRegion != null) {
      newOffset = currentFoldRegion.getEndOffset();
    }
    else {
      // Current Line
      //int startLineOffset = document.getLineStartOffset(visualPos.line);
      int startLineOffset = document.getLineStartOffset(logicalPos.line);
      //int currentLineNumber = visualPos.line;
      int currentLineNumber = logicalPos.line;
      //int endLineOffset = document.getLineEndOffset(currentLineNumber);
      int endLineOffset = document.getLineEndOffset(currentLineNumber);

      if (currentLineNumber >= document.getLineCount()) return;

      // Preparation get words
      int moveRange = 1;
      int textRangeStartOffset = currentCaretOffset;
      int textRangeEndOffset = endLineOffset;
      int boundaryOffset = textRangeEndOffset;
      if (!isNext) {
        moveRange = -1;
        textRangeStartOffset = startLineOffset;
        textRangeEndOffset = currentCaretOffset;
        boundaryOffset = textRangeStartOffset;
      }

      int documentLength = document.getText().length();

      /* Return when position can't move. */
      if (currentCaretOffset < 0 || currentCaretOffset > documentLength) return;

      /* If at BOL/EOL, move one character */
      if (currentCaretOffset == boundaryOffset) {
        caret.moveToOffset(currentCaretOffset + moveRange);
        EditorModificationUtil.scrollToCaret(editor);
        setupSelection(caret, isWithSelection, selectionStart);
        return;
      }

      // Get text by current caret position
      int textLength = getTextLength(textRangeStartOffset, textRangeEndOffset);
      TextRange textRange = TextRange.from(textRangeStartOffset, textLength);
      String lineText = document.getText(textRange);

      List<String> matchList = wordParse(lineText);

      int matchSize = matchList.size();
      if (matchSize <= 0) {
        useBuiltinWordAction(editor, caret, isNext, isWithSelection, dataContext);
        return;
      }

      // Get number of characters to move
      int wordLength = getWordLength(isNext, matchList);

      newOffset = currentCaretOffset + wordLength;
    }

    caret.moveToOffset(newOffset);
    EditorModificationUtil.scrollToCaret(editor);
    setupSelection(caret, isWithSelection, selectionStart);
  }

  /**
   * @param editor          openapi.Editor
   * @param caret           openapi.Caret
   * @param isNext          true when move to next
   * @param isWithSelection true when with selection
   * @param dataContext     openapi.DataContext
   */
  private static void useBuiltinWordAction(@NotNull Editor editor,
                                           @NotNull Caret caret,
                                           boolean isNext, boolean isWithSelection, DataContext dataContext) {
    final EditorActionManager actionManager = EditorActionManager.getInstance();
    String ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD;
    if (isWithSelection && !isNext) {
      ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD_WITH_SELECTION;
    }
    else if (!isNext) {
      ideAction = IdeActions.ACTION_EDITOR_PREVIOUS_WORD;
    }
    else if (isWithSelection) {
      ideAction = IdeActions.ACTION_EDITOR_NEXT_WORD_WITH_SELECTION;
    }
    final EditorActionHandler actionHandler = actionManager.getActionHandler(ideAction);
    actionHandler.execute(editor, caret, dataContext);
  }

  /**
   * @param caret           openapi.Caret
   * @param isWithSelection true when with selection
   * @param startOffset     Start offset of the selection
   */
  private static void setupSelection(@NotNull Caret caret, boolean isWithSelection, int startOffset) {
    if (isWithSelection) {
      caret.setSelection(startOffset, caret.getOffset(), true);
    }
    else {
      caret.removeSelection();
    }
  }

  /**
   * @param isNext    Move next(Right) is true
   * @param matchList MBWS.wordParse return value
   * @return Character length
   */
  private static int getWordLength(boolean isNext, List<String> matchList) {
    int matchSize = matchList.size();
    int position = 0;
    int orientation = 1;
    if (!isNext) {
      position = matchSize - 1;
      orientation = -1;
    }
    String lastWord = matchList.get(position);
    return lastWord.length() * orientation;
  }

  private static int getTextLength(int textRangeStartOffset, int textRangeEndOffset) {
    return textRangeEndOffset - textRangeStartOffset;
  }

  private static List<String> wordParse(String character) {
    HashMap<String, String> userPatternsMap = getUserPatterns();
    List<String> userPatterns = new ArrayList<>(userPatternsMap.values());
    HashMap<String, String> defaultPatternsMap = getDefaultPatterns();
    List<String> defaultPatterns = new ArrayList<>(defaultPatternsMap.values());
    userPatterns.addAll(defaultPatterns);
    String pattern = String.join("|", userPatterns);
    Pattern p = Pattern.compile(pattern);
    Matcher m = p.matcher(character);

    return findAll(m);
  }

  private static List<String> findAll(Matcher matcher) {
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

  private static HashMap<String, String> getUserPatterns() {
    HashMap<String, String> patternMap = new HashMap<>();
    String userPattern = NextPrevWordHandler.getUserPattern();
    if (userPattern == null || userPattern.isEmpty()) {
      return patternMap;
    }
    String[] lines = userPattern.split("\n");
    for (String line : lines) {
      String[] items = line.split(",");
      patternMap.put(items[0], items[1]);
    }
    return patternMap;
    //return new ArrayList<>(Arrays.asList(userPattern.split("\n")));
  }

  private static HashMap<String, String> getDefaultPatterns() {
    HashMap<String, String> patterns = new HashMap<>(26);
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
    patterns.put("latin", "[\\u0030-\\u0039\\u0041-\\u005A\\u0061-\\u007A]+");
    patterns.put("latinSymbol",
                 "[\\u0020-\\u0021\\u0023\\u0025-\\u0026\\u002A-\\u002F\\u003A-\\u003B\\u003D\\u003F-\\u0040\\u005C\\u005E\\u0060\\u00A5\\u007C\\u007E\\u203E]+");
    patterns.put("czeroControls", "[\\u0000-\\u0009\\u000B\\u000C\\u000E-\\u001F]+");
    patterns.put("controlCharacters", "[\\u000A\\u000D]+");
    patterns.put("\"", "\\u0022");
    patterns.put("'", "\\u0027");
    patterns.put("(", "\\u0028");
    patterns.put(")", "\\u0029");
    patterns.put("<", "\\u003C");
    patterns.put(">", "\\u003E");
    patterns.put("[", "\\u005B");
    patterns.put("]", "\\u005D");
    patterns.put("{", "\\u007B");
    patterns.put("}", "\\u007D");
    patterns.put("ideaSigns", "[\\u0024\\u005F]+");

    return patterns;
  }
}
