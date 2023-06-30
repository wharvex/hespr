package com.wharvex.hespr.parser.nodes;

public class RealNode extends Node {

  private float val;

  public RealNode(float val, int lineNum) {
    this.val = val;
    this.lineNum = lineNum;
  }

  public float getVal() {
    return this.val;
  }

  public String toString() {
    return Float.toString(this.val);
  }
}
