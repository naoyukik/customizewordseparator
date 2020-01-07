package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.project.Project;

import javax.swing.*;

public class CustomizeWordSeparatorsConfigurableGUI {
  private JPanel rootPanel;
  private JPanel CustomPatterns;
  private JLabel customPattern1;
  private JTextArea myTextArea1;
  private CustomizeWordSeparatorsState mState;

  public JPanel getRootPanel() {
    return rootPanel;
  }

  public void createUI(Project project) {
    mState = CustomizeWordSeparatorsState.getInstance(project);
    assert mState.getState() != null;
    myTextArea1.setText(mState.getState().customPattern1);
  }

  public void apply() {
    mState.setCustomPattern1(myTextArea1.getText());
  }

  public boolean isModified() {
    boolean modified = false;
    assert mState.getState() != null;
    modified |= !myTextArea1.getText().equals(mState.getState().customPattern1);
    return modified;
  }
}
