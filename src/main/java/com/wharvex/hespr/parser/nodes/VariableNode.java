package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.VariableRange;
import com.wharvex.hespr.parser.VariableType;

public class VariableNode extends Node {

  private final String name;

  public Node getVal() {
    return val;
  }

  public void setVal(Node val) {
    this.val = val;
  }

  private Node val;
  private final boolean isChangeable;

  private boolean isArray;
  private VariableType type;
  private VariableRange range;

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

  public VariableNode(
      String name,
      boolean isChangeable,
      int lineNum) {
    this.name = name;
    this.isChangeable = isChangeable;
    this.lineNum = lineNum;
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

  public VariableRange getRange() {
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

  public void setIsArray(boolean isArray) {
    this.isArray = isArray;
  }

  public void setType(VariableType type) {
    this.type = type;
  }

  public void setRange(VariableRange range) {
    this.range = range;
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
