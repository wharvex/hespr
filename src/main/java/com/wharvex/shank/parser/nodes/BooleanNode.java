package com.wharvex.shank.parser.nodes;

public class BooleanNode extends Node {

  private boolean val;

  public BooleanNode(boolean val, int lineNum) {
    this.val = val;
    this.lineNum = lineNum;
  }

  public boolean getVal() {
    return this.val;
  }

  public String toString() {
    return Boolean.toString(this.val);
  }
}
