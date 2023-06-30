package com.wharvex.hespr.parser.nodes;

public class VariableReferenceNode extends Node {

  private final String name;
  private Node arrIdxExp;

  public VariableReferenceNode(String name, Node arrIdxExp, int lineNum) {
    this.name = name;
    this.arrIdxExp = arrIdxExp;
    this.lineNum = lineNum;
  }

  public VariableReferenceNode(String name, int lineNum) {
    this.name = name;
    this.lineNum = lineNum;
  }

  public String getName() {
    return this.name;
  }

  public Node getArrIdxExp() {
    return this.arrIdxExp;
  }

  public String toString() {
    return this.getName() + (this.getArrIdxExp() != null ? "[" + this.getArrIdxExp() + "]" : "");
  }
}
