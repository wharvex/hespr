package com.wharvex.hespr.parser.nodes;

public class CharacterNode extends Node {
  private char val;

  public CharacterNode(char val, int lineNum) {
    this.val = val;
    this.lineNum = lineNum;
  }

  public char getVal() {
    return this.val;
  }

  public String toString() {
    return String.valueOf(this.val);
  }

}
