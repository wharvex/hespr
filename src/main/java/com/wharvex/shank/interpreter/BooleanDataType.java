package com.wharvex.shank.interpreter;

public class BooleanDataType extends InterpreterDataType {

  private boolean storedVal;

  public BooleanDataType(boolean storedVal) {
    this.storedVal = storedVal;
  }

  public void setStoredVal(boolean storedVal) {
    this.storedVal = storedVal;
  }

  public boolean getStoredVal() {
    return this.storedVal;
  }

  public String toString() {
    return this.getStoredVal() + "";
  }

  public void fromString(String input) {
    this.setStoredVal(Boolean.parseBoolean(input));
  }
}
