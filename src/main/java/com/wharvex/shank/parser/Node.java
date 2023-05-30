package com.wharvex.shank.parser;

public abstract class Node {

  public int getLineNum() {
    return lineNum;
  }

  public void setLineNum(int lineNum) {
    this.lineNum = lineNum;
  }

  private int lineNum;

  public abstract String toString();
}
