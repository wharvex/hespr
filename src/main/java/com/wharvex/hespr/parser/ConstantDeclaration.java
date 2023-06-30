package com.wharvex.hespr.parser;

public class ConstantDeclaration {

  private final VariableType type;
  private final String name;
  private final String val;

  public ConstantDeclaration(String name, VariableType type, String val) {
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

  public VariableType getType() {
    return this.type;
  }
}
