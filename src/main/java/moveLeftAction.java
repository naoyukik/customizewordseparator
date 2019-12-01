import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;


public class moveLeftAction extends AnAction {

  private MoveCaretLogic MCL;

  public moveLeftAction() {
    MCL = new MoveCaretLogic();
  }

  @Override
  public void actionPerformed(@NotNull AnActionEvent e) {
    MCL.moveCaret(e, MCL.signMinus);

    // get Word under carsor
    // primaryCaret.moveToOffset(1);
    //        primaryCaret.setSelection(currentCaretOffset-2, currentCaretOffset);
//        SelectionModel selectionModel = editor.getSelectionModel();
//        boolean hasSelection = selectionModel.hasSelection();
//        System.out.println( hasSelection );
//        int startOffset = visualPos.getColumn()-10;
//        primaryCaret.setSelection(visualPos.getLine(), startOffset);
//        System.out.println(primaryCaret.getVisualPosition());
  }
}
