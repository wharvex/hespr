package com.wharvex.shank.parser;

import com.wharvex.shank.SyntaxErrorException;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.Token.TokenType;

public class PeekCheck {

  private Token peekTokenRet;

  public PeekCheck(Token peekTokenRet, boolean expectsFileContinuation)
      throws SyntaxErrorException {
    this.handleExpectsFileContinuation(peekTokenRet, expectsFileContinuation);
    this.peekTokenRet = peekTokenRet;
  }

  private Token getToken() {
    return this.peekTokenRet;
  }

  public boolean isNull() {
    return this.getToken() == null;
  }

  public int getLineNum() {
    return this.peekTokenRet.getTokenLineNum();
  }

  public Token.TokenType getTokenType() {
    return this.peekTokenRet.getTokenType();
  }

  public void expectsDeclarationsOrIndent() throws SyntaxErrorException {
    if (!(this.getTokenType() == Token.TokenType.VARIABLES
        || this.getTokenType() == Token.TokenType.CONSTANTS
        || this.getTokenType() == Token.TokenType.INDENT)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          "variables, constants or indent",
          this.toString());
    }
  }

  private void handleExpectsFileContinuation(Token peekTokenRet, boolean expectsFileContinuation)
      throws SyntaxErrorException {
    if (expectsFileContinuation && peekTokenRet == null) {
      throw new SyntaxErrorException(SyntaxErrorException.ExcType.EOF_ERROR, -1, "");
    }
  }

  public void updatePeek(Token peekTokenRet, boolean expectsFileContinuation)
      throws SyntaxErrorException {
    this.handleExpectsFileContinuation(peekTokenRet, expectsFileContinuation);
    this.peekTokenRet = peekTokenRet;
  }

  public boolean isComma() {
    return this.getTokenType() == Token.TokenType.COMMA;
  }

  public boolean isEOL() {
    return this.getTokenType() == Token.TokenType.ENDOFLINE;
  }
  public boolean isIndent() {
    return this.getTokenType() == Token.TokenType.INDENT;
  }

  @Override
  public String toString() {
    return this.isNull() ? "EOF" : this.peekTokenRet.toString();
  }

}
