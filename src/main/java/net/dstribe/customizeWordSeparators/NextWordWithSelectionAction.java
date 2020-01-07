package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class NextWordWithSelectionAction extends AnAction {
  @Override
  public void update(@NotNull final AnActionEvent e) {
    final Project project = e.getProject();
    final Editor editor = e.getData(CommonDataKeys.EDITOR);
    boolean menuAllowed = false;
    if (editor != null && project != null) {
      menuAllowed = !editor.getCaretModel().getAllCarets().isEmpty();
    }
    e.getPresentation().setEnabledAndVisible(menuAllowed);
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    final Editor editor = e.getRequiredData(CommonDataKeys.EDITOR);
    final EditorActionHandler actionHandler = new NextPrevWordHandler(true, true, e);
    actionHandler.execute(editor, null, e.getDataContext());
  }
}
