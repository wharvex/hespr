package com.wharvex.shank.parser;

public class IntegerNode extends Node {

  private int val;

  public IntegerNode(int val) {
    this.val = val;
  }

  public int getVal() {
    return this.val;
  }

  public String toString() {
    return Integer.toString(this.val);
  }
}
