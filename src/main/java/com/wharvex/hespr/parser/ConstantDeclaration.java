package com.wharvex.hespr.parser;

import com.wharvex.hespr.parser.nodes.Node;

public class ConstantDeclaration {

  private final String name;
  private final Node val;

  public ConstantDeclaration(String name, Node val) {
    this.name = name;
    this.val = val;
  }

  public String getName() {
    return this.name;
  }

  public Node getVal() {
    return this.val;
  }
}
