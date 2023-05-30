package com.wharvex.shank.interpreter;

public class CharacterDataType extends InterpreterDataType {

  private char storedVal;
  private boolean isChangeable;
  private boolean isVar;
  private boolean isSet;

  public CharacterDataType(char storedVal) {
    this.storedVal = storedVal;
  }

  public CharacterDataType() {
  }

  public char getStoredVal() {
    return this.storedVal;
  }

  public void setStoredVal(char storedVal) {
    this.storedVal = storedVal;
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

  public String toString() {
    return this.getStoredVal() + "";
  }

  public void fromString(String input) {
    this.setStoredVal(input.charAt(0));
  }
}
