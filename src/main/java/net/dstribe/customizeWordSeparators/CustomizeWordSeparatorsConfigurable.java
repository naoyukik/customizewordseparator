package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nls.Capitalization;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * Preference
 */
public class CustomizeWordSeparatorsConfigurable implements SearchableConfigurable {

  @SuppressWarnings("FieldCanBeLocal")
  private final Project mProject;
  private CustomizeWordSeparatorsConfigurableGUI mGUI;

  CustomizeWordSeparatorsConfigurable(@NotNull Project project) {
    mProject = project;
  }

  @NotNull
  @Override
  public String getId() {
    return "preference.CustomizeWordSeparatorsConfigurable";
  }

  @Nls(capitalization = Capitalization.Title)
  @Override
  public String getDisplayName() {
    return "Customize Word Separators";
  }

  /**
   * Initialize Edit GUI
   *
   * @return CustomizeWordSeparatorsConfigurableGUI
   */
  @Nullable
  @Override
  public JComponent createComponent() {
    mGUI = new CustomizeWordSeparatorsConfigurableGUI();
    mGUI.createUI(mProject);
    return mGUI.getRootPanel();
  }

  @Override
  public boolean isModified() {
    return mGUI.isModified();
  }

  @Override
  public void apply() throws ConfigurationException {
    mGUI.apply();
  }

  @Override
  public void disposeUIResources() {
    mGUI = null;
  }
}
