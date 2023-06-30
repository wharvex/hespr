package com.wharvex.hespr.parser;

import com.wharvex.hespr.lexer.Token;
import com.wharvex.hespr.lexer.TokenType;

public class CurrentToken {

  private Token prevToken;
  private Token curToken;
  private Token nxtToken;
  private Token nxtNxtToken;

  public CurrentToken(Token curToken, Token nxtToken, Token nxtNxtToken) {
    this.curToken = curToken;
    this.nxtToken = nxtToken;
    this.nxtNxtToken = nxtNxtToken;
    this.prevToken = new Token("", TokenType.NONE, 0);
  }

  public void setNxtNxtToken(Token nxtNxtToken) {
    this.prevToken = new Token(this.curToken);
    this.curToken = new Token(this.nxtToken);
    this.nxtToken = new Token(this.nxtNxtToken);
    this.nxtNxtToken = nxtNxtToken;
  }

  public Token getCurToken() {
    return this.curToken;
  }

  public Token getNxtToken() {
    return nxtToken;
  }

  public Token getNxtNxtToken() {
    return nxtNxtToken;
  }

  public TokenType getCurTokenType() {
    return this.getCurToken().getTokenType();
  }

  public Token getPrevToken() {
    return new Token(this.prevToken);
  }

  public TokenType getPrevTokenType() {
    return this.getPrevToken().getTokenType();
  }

  public int getLineNum() {
    return this.curToken.getTokenLineNum();
  }
}
