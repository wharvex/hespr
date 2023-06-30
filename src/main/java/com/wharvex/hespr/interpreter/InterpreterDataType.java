package com.wharvex.hespr.interpreter;

public abstract class InterpreterDataType {

  private boolean isChangeable, isVar, isInitialized;

  public boolean isInitialized() {
    return isInitialized;
  }

  public void setInitialized(boolean initialized) {
    isInitialized = initialized;
  }

  public boolean getIsChangeable() {
    return this.isChangeable;
  }

  public boolean getIsVar() {
    return this.isVar;
  }

  public void setIsChangeable(boolean isChangeable) {
    this.isChangeable = isChangeable;
  }

  public void setIsVar(boolean isVar) {
    this.isVar = isVar;
  }

  public abstract String toString();

  public abstract void fromString(String input);

}
