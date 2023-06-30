package com.wharvex.hespr.parser;

public class ArrayDeclaration {

  private final int from, to;
  private final VariableType type;

  public ArrayDeclaration(int from, int to, VariableType type) {
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

  public VariableType getType() {
    return this.type;
  }
}
