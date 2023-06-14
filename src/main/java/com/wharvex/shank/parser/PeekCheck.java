package com.wharvex.shank.parser;

import com.wharvex.shank.SyntaxErrorException;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.TokenType;
import com.wharvex.shank.semantic.SemanticErrorException;

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

  public TokenType getTokenType() {
    return this.peekTokenRet.getTokenType();
  }

  public void expectsDeclarationsOrIndent() throws SyntaxErrorException {
    if (!(this.getTokenType() == TokenType.VARIABLES
        || this.getTokenType() == TokenType.CONSTANTS
        || this.isIndent())) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          "variables, constants or indent",
          this.toString());
    }
  }

  private boolean isTokenTypeDataType(TokenType t) {
    return t == TokenType.REAL
        || t == TokenType.INTEGER
        || t == TokenType.BOOLEAN
        || t == TokenType.STRING
        || t == TokenType.CHARACTER;
  }

  public void expectsDataType() throws SyntaxErrorException {
    if (!(this.getTokenType() == TokenType.ARRAY || this.isTokenTypeDataType(
        this.getTokenType()))) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR, "data type", this.toString());
    }
  }

  public void expectsRangeVarType() throws SemanticErrorException {
    if (this.getTokenType() != TokenType.STRING
        && this.getTokenType() != TokenType.REAL
        && this.getTokenType() != TokenType.INTEGER) {
      throw new SemanticErrorException(
          "Variable range not supported for type "
              + this.getTokenType()
              + " -- line "
              + this.getLineNum());
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

  public void updatePeek(Token peekTokenRet) {
    this.peekTokenRet = peekTokenRet;
  }

  public boolean isComma() {
    return this.getTokenType() == TokenType.COMMA;
  }

  public boolean isEOL() {
    return this.getTokenType() == TokenType.ENDOFLINE;
  }

  public boolean isIndent() {
    return this.getTokenType() == TokenType.INDENT;
  }

  @Override
  public String toString() {
    return this.isNull() ? "EOF" : this.peekTokenRet.toString();
  }

}
