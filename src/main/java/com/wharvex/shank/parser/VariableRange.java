package com.wharvex.shank.parser;

public class VariableRange {

  private final int from, to;
  private final float realFrom, realTo;

  public VariableRange(int from, int to, float realFrom, float realTo) {
    this.from = from;
    this.to = to;
    this.realFrom = realFrom;
    this.realTo = realTo;
  }

  public int getFrom() {
    return this.from;
  }

  public int getTo() {
    return this.to;
  }

  public float getRealFrom() {
    return this.realFrom;
  }

  public float getRealTo() {
    return this.realTo;
  }
}
