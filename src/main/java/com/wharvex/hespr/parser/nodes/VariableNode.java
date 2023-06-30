package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.VariableRange;
import com.wharvex.hespr.parser.VariableType;

public class VariableNode extends Node {

  private final String name;

  public String getVal() {
    return val;
  }

  public void setVal(String val) {
    this.val = val;
  }

  private String val;
  private final boolean isChangeable;
  private final boolean isArray;
  private final VariableType type;
  private final VariableRange range;

  public VariableNode(
      String name,
      VariableType type,
      boolean isChangeable,
      boolean isArray,
      int lineNum,
      VariableRange range) {
    this.name = name;
    this.type = type;
    this.isChangeable = isChangeable;
    this.isArray = isArray;
    this.lineNum = lineNum;
    this.range = range;
  }

  public VariableNode(
      String name,
      VariableType type,
      boolean isChangeable,
      boolean isArray,
      int lineNum) {
    this.name = name;
    this.type = type;
    this.isChangeable = isChangeable;
    this.isArray = isArray;
    this.lineNum = lineNum;
    this.range = new VariableRange();
  }

  /**
   * Copy constructor
   */
  public VariableNode(VariableNode v) {
    this.name = v.getName();
    this.isChangeable = v.getIsChangeable();
    this.isArray = v.getIsArray();
    this.type = v.getType();
    this.lineNum = v.getLineNum();
    this.range = v.getRange();
    this.val = v.getVal();
  }

  public VariableType getType() {
    return this.type;
  }

  private VariableRange getRange() {
    return this.range;
  }

  public boolean getIsChangeable() {
    return this.isChangeable;
  }

  public boolean getIsArray() {
    return this.isArray;
  }

  public String getName() {
    return this.name;
  }

  public int getIntFrom() {
    return this.getRange().getIntFrom();
  }

  public int getIntTo() {
    return this.getRange().getIntTo();
  }

  public float getRealFrom() {
    return this.getRange().getRealFrom();
  }

  public float getRealTo() {
    return this.getRange().getRealTo();
  }

  public String toString() {
    return this.getName()
        + " ("
        + (this.getIsChangeable() ? "variable" : "constant")
        + ", "
        + this.getType()
        + (this.getIsArray() ? " array " : " ") + "[" + this.getRange() + "]"
        + ")";
  }
}
