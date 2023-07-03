package com.wharvex.hespr.interpreter;

public class StringDataType extends InterpreterDataType {

  private String storedVal;

  private IntegerDataType from;
  private IntegerDataType to;

  public StringDataType(String storedVal, IntegerDataType from, IntegerDataType to) {
    this.storedVal = storedVal;
  }

  public String getStoredVal() {
    return this.storedVal;
  }

  public void setStoredVal(String storedVal) {
    this.storedVal = storedVal;
  }

  public IntegerDataType getFrom() {
    return from;
  }

  public IntegerDataType getTo() {
    return to;
  }

  public String toString() {
    return this.getStoredVal();
  }

  public void fromString(String input) {
    this.setStoredVal(input);
  }
}
