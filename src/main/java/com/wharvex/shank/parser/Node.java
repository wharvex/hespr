package com.wharvex.shank.parser;

public abstract class Node {

  Node() {}

  public int getLineNum() {
    return this.lineNum;
  }

  public void setLineNum(int lineNum) {
    this.lineNum = lineNum;
  }

  protected int lineNum;

  public abstract String toString();
}
