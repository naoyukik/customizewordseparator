package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.ex.util.EditorUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NextPrevWordHandler extends EditorActionHandler {
  private static String userPattern;
  private final boolean myNext;
  private final boolean myWithSelection;

  NextPrevWordHandler(boolean next, boolean withSelection, AnActionEvent e) {
    super(true);
    userPattern = CustomizeWordSeparatorsState.getInstance(e.getProject()).myState.customPattern1;
    myNext = next;
    myWithSelection = withSelection;
  }

  @Override
  protected void doExecute(@NotNull Editor editor, @Nullable Caret caret, DataContext dataContext) {
    assert caret != null;
    if (EditorUtil.isPasswordEditor(editor)) {
      int selectionStartOffset = caret.getLeadSelectionOffset();
      caret.moveToOffset(myNext ? editor.getDocument().getTextLength() : 0);
      if (myWithSelection) caret.setSelection(selectionStartOffset, caret.getOffset());
    }
    else {
      MoveCaretWordUtil.moveCaretWord(editor, caret, myNext, myWithSelection, dataContext, false);
    }
  }

  static String getUserPattern() {
    return userPattern;
  }
}
