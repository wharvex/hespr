package com.wharvex.shank.parser;

public class RealNode extends Node {

  private float val;

  public RealNode(float val) {
    this.val = val;
  }

  public float getVal() {
    return this.val;
  }

  public String toString() {
    return Float.toString(this.val);
  }
}
