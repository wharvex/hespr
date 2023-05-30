package com.wharvex.shank.parser;

public class ArrayDeclaration {

  private final int from, to;
  private final VariableNode.VariableType type;

  public ArrayDeclaration(int from, int to, VariableNode.VariableType type) {
    this.from = from;
    this.to = to;
    this.type = type;
  }

  public int getFrom() {
    return this.from;
  }

  public int getTo() {
    return this.to;
  }

  public VariableNode.VariableType getType() {
    return this.type;
  }
}
