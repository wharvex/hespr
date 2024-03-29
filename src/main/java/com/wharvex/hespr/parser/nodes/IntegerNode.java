package com.wharvex.hespr.parser.nodes;

public class IntegerNode extends Node {

  private int val;

  public IntegerNode(int val, int lineNum) {
    this.val = val;
    this.lineNum = lineNum;
  }

  public int getVal() {
    return this.val;
  }

  public String toString() {
    return Integer.toString(this.val);
  }
}
