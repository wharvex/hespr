package com.wharvex.hespr.parser.nodes;

public class StringNode extends Node {

  private String val;

  public StringNode(String val, int lineNum) {
    if (val.length() == 2) {
      this.val = "";
    } else {
      this.val = val.substring(1, val.length() - 1);
    }
    this.lineNum = lineNum;
  }

  public String getVal() {
    return this.val;
  }

  public String toString() {
    return this.val;
  }
}
