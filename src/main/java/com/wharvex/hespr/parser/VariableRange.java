package com.wharvex.hespr.parser;

import com.wharvex.hespr.lexer.Token;
import com.wharvex.hespr.lexer.TokenType;

public class VariableRange {

  private final Token from, to;

  public VariableRange(Token from, Token to) {
    this.from = from;
    this.to = to;
  }

  public VariableRange() {
    this.from = new Token("", TokenType.NONE, -1);
    this.to = new Token("", TokenType.NONE, -1);
  }

  public int getIntFrom() {
    return this.from.getTokenType() == TokenType.NUMBER ? Integer.parseInt(
        this.from.getValueString()) : -1;
  }

  public int getIntTo() {
    return this.to.getTokenType() == TokenType.NUMBER ? Integer.parseInt(this.to.getValueString())
        : -1;
  }

  public float getRealFrom() {
    return this.from.getTokenType() == TokenType.NUMBER_DECIMAL ? Float.parseFloat(
        this.from.getValueString())
        : -1;
  }

  public float getRealTo() {
    return this.to.getTokenType() == TokenType.NUMBER_DECIMAL ? Float.parseFloat(
        this.to.getValueString()) : -1;
  }

  @Override
  public String toString() {
    return this.getIntFrom() + " | " + this.getRealFrom() + " -> " + this.getIntTo() + " | "
        + this.getRealTo();
  }
}
