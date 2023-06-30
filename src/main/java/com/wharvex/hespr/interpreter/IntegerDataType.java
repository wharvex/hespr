package com.wharvex.hespr.interpreter;

public class IntegerDataType extends InterpreterDataType {

  private int storedVal;

  public IntegerDataType(int storedVal) {
    this.storedVal = storedVal;
  }

  public void setStoredVal(int storedVal) {
    this.storedVal = storedVal;
  }

  public int getStoredVal() {
    return this.storedVal;
  }

  public String toString() {
    return String.valueOf(this.getStoredVal());
  }

  public void fromString(String input) {
    this.setStoredVal(Integer.parseInt(input));
  }
}
