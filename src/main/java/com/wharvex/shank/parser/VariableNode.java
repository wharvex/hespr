package com.wharvex.shank.parser;

public class VariableNode extends Node {

  public enum VariableType {
    STRING,
    CHARACTER,
    INTEGER,
    REAL,
    BOOLEAN,
    ANY
  }

  private String name;
  private String val;
  private boolean changeable, isArray;
  private int from, to;
  private float realFrom, realTo;
  private VariableType type;

  /**
   * Normal constructor
   */
  public VariableNode(
      String name, VariableType type, boolean changeable, int from, int to, boolean isArray) {
    this.name = name;
    this.type = type;
    this.changeable = changeable;
    this.from = from;
    this.to = to;
    this.isArray = isArray;
    this.realFrom = -1;
    this.realTo = -1;
  }

  public VariableNode(
      String name,
      VariableType type,
      boolean changeable,
      int from,
      int to,
      boolean isArray,
      float realFrom,
      float realTo) {
    this.name = name;
    this.type = type;
    this.changeable = changeable;
    this.from = from;
    this.to = to;
    this.isArray = isArray;
    this.realFrom = realFrom;
    this.realTo = realTo;
  }

  /**
   * Copy constructor
   */
  public VariableNode(VariableNode v) {
    this.name = v.getName();
    this.val = v.getVal();
    this.changeable = v.isChangeable();
    this.from = v.getFrom();
    this.to = v.getTo();
    this.type = v.getType();
  }

  public String getVal() {
    return this.val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  public VariableType getType() {
    return this.type;
  }

  public boolean isChangeable() {
    return this.changeable;
  }

  public boolean getIsArray() {
    return this.isArray;
  }

  public String getName() {
    return this.name;
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

  public String toString() {
    String range = "";
    boolean hasFromTo = this.getFrom() != -1 && this.getTo() != -1;
    boolean hasRealFromTo = Float.compare(this.getRealFrom(), -1) != 0;
    if (!this.getIsArray()) {
      if (hasFromTo) {
        range = ", range: " + this.getFrom() + "->" + this.getTo();
      } else if (hasRealFromTo) {
        range = ", range: " + this.getRealFrom() + "->" + this.getRealTo();
      }
    }
    return this.getName()
        + " ("
        + (this.isChangeable() ? "variable" : "constant")
        + ", "
        + this.getType()
        + range
        + (this.getIsArray() ? ", array[" + this.getFrom() + "->" + this.getTo() + "]" : "")
        + ", val = "
        + this.getVal()
        + ")";
  }
}
