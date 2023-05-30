package com.wharvex.shank.parser;

public class ConstantDeclaration {

  private final VariableNode.VariableType type;
  private final String name;
  private final String val;

  public ConstantDeclaration(String name, VariableNode.VariableType type, String val) {
    this.name = name;
    this.type = type;
    this.val = val;
  }

  public String getName() {
    return this.name;
  }

  public String getVal() {
    return this.val;
  }

  public VariableNode.VariableType getType() {
    return this.type;
  }
}
