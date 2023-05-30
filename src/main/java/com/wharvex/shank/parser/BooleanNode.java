package com.wharvex.shank.parser;

public class BooleanNode extends Node {

  private boolean val;

  public BooleanNode(boolean val) {
    this.val = val;
  }

  public boolean getVal() {
    return this.val;
  }

  public String toString() {
    return Boolean.toString(this.val);
  }
}
