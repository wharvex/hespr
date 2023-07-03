package com.wharvex.hespr.parser;

import com.wharvex.hespr.lexer.Token;
import com.wharvex.hespr.lexer.TokenType;
import com.wharvex.hespr.parser.nodes.Node;

public class VariableRange {

  private Node from, to;

  public VariableRange(Node from, Node to) {
    this.from = from;
    this.to = to;
  }

  public VariableRange() {
  }

  public Node getFrom() {
    return this.from;
  }

  public Node getTo() {
    return this.to;
  }

  @Override
  public String toString() {
    return this.getFrom() + " to " + this.getTo();
  }
}
