package com.wharvex.shank.interpreter;

public class RealDataType extends InterpreterDataType {

  private float storedVal;

  public RealDataType(float storedVal) {
    this.storedVal = storedVal;
  }

  public void setStoredVal(float storedVal) {
    this.storedVal = storedVal;
  }

  public float getStoredVal() {
    return this.storedVal;
  }

  public String toString() {
    return this.getStoredVal() + "";
  }

  public void fromString(String input) {
    this.setStoredVal(Float.parseFloat(input));
  }
}
