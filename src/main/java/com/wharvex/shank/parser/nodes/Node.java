package com.wharvex.shank.parser.nodes;

public abstract class Node {

  public int getLineNum() {
    return this.lineNum;
  }

  public void setLineNum(int lineNum) {
    this.lineNum = lineNum;
  }

  protected int lineNum;

  public abstract String toString();
}
