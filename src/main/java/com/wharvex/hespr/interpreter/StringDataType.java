package com.wharvex.hespr.interpreter;

public class StringDataType extends InterpreterDataType {

  private String storedVal;

  private int from;
  private int to;

  public StringDataType(String storedVal, int from, int to) {
    this.storedVal = storedVal;
    this.from = from;
    this.to = to;
  }

  public StringDataType(String storedVal) {
    this.storedVal = storedVal;
    this.from = -1;
    this.to = -1;
  }

  public String getStoredVal() {
    return this.storedVal;
  }

  public void setStoredVal(String storedVal) {
    this.storedVal = storedVal;
  }

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public String toString() {
    return this.getStoredVal();
  }

  public void fromString(String input) {
    this.setStoredVal(input);
  }
}
