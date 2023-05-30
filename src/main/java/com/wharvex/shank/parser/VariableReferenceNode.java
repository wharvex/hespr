package com.wharvex.shank.parser;

public class VariableReferenceNode extends Node {

  private String name;
  private Node arrIdxExp;

  public VariableReferenceNode(String name, Node arrIdxExp) {
    this.name = name;
    this.arrIdxExp = arrIdxExp;
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
