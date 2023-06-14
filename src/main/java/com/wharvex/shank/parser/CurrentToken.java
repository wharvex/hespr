package com.wharvex.shank.parser;

import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.TokenType;

public class CurrentToken {

  private Token curToken;
  private Token prevToken;

  public CurrentToken(Token curToken) {
    this.curToken = curToken;
    this.prevToken = new Token("", TokenType.NONE, 0);
  }

  public void setCurToken(Token curToken) {
    this.prevToken = new Token(this.curToken);
    this.curToken = curToken;
  }

  public Token getCurToken() {
    return this.curToken;
  }

  public TokenType getCurTokenType() {
    return this.getCurToken().getTokenType();
  }

  public Token getPrevToken() {
    return this.prevToken;
  }

  public TokenType getPrevTokenType() {
    return this.getPrevToken().getTokenType();
  }

  public int getLineNum() {
    return this.curToken.getTokenLineNum();
  }
  public int getPrevTokenLineNum() {
    return this.prevToken.getTokenLineNum();
  }
}
