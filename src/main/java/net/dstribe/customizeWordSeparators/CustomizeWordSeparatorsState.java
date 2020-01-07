package net.dstribe.customizeWordSeparators;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * PersistentStateComponent keeps project config values
 */
@State(
  name = "CustomizeWordSeparatorsState",
  storages = {
    @Storage("CustomizeWordSeparatorsState.xml")}
)
public class CustomizeWordSeparatorsState implements PersistentStateComponent<CustomizeWordSeparatorsState.State> {

  State myState;

  public CustomizeWordSeparatorsState() {
    this.myState = new State();
  }

  @Nullable
  @Override
  public State getState() {
    return myState;
  }

  @Override
  public void loadState(@NotNull State state) {
    myState = state;
  }

  public void setCustomPattern1(String value) {
    myState.customPattern1 = value;
  }

  //String getCustomPattern1() {
  //  if (myState.customPattern1 == null) {
  //    myState.customPattern1 = "";
  //  }
  //  return myState.customPattern1;
  //}

  public static CustomizeWordSeparatorsState getInstance(Project project) {
    return ServiceManager.getService(project, CustomizeWordSeparatorsState.class);
  }

  static class State {
    public String customPattern1;
  }
}
