package com.wharvex.hespr.interpreter;

public class StringDataType extends InterpreterDataType {

  private String storedVal;

  public StringDataType(String storedVal) {
    this.storedVal = storedVal;
  }

  public String getStoredVal() {
    return this.storedVal;
  }

  public void setStoredVal(String storedVal) {
    this.storedVal = storedVal;
  }

  public String toString() {
    return this.getStoredVal();
  }

  public void fromString(String input) {
    this.setStoredVal(input);
  }
}
