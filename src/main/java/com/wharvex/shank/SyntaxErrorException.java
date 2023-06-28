package com.wharvex.shank;

import com.wharvex.shank.lexer.StateType;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.TokenType;
import java.util.List;
import java.util.StringJoiner;

public class SyntaxErrorException extends Exception {

  private String expected;

  public SyntaxErrorException(
      ExcType exceptionType, StateType stateType, int LineNum, int colNum, String problem) {
    super(
        "\nERROR: "
            + exceptionType.baseMessage
            + problem
            + "\nfor StateType "
            + stateType
            + "\non line "
            + LineNum
            + " col "
            + colNum);
  }

  /**
   * Use this constructor for Parser errors (no StateType or column)
   */
  public SyntaxErrorException(ExcType exceptionType, int LineNum, String problem) {
    super("\nERROR: " + exceptionType.baseMessage + problem + "\non line " + LineNum);
  }

  public SyntaxErrorException(ExcType exceptionType, String expected, String found) {
    super("\nERROR: " + exceptionType.baseMessage + expected + " but found " + found);
    this.expected = expected;
  }

  public SyntaxErrorException(TokenType expected, Token found) {
    super("\nERROR: Expected " + expected + "; found " + found);
  }

  public SyntaxErrorException(List<TokenType> expected, Token found) {
    super("\nERROR: Expected " + joinExpectedTokenTypes(expected) + "; found " + found);
  }

  public SyntaxErrorException(Token blockType) {
    super("\nERROR: Block type <" + blockType + "> expected statements");
  }

  public SyntaxErrorException(ExcType excType, String problem) {
    super("\nERROR: " + excType.baseMessage + problem);
  }

  private static String joinExpectedTokenTypes(List<TokenType> tts) {
    StringJoiner ret = new StringJoiner(", ");
    tts.forEach(tt -> ret.add(tt.toString()));
    return ret.toString();
  }

  public String getExpected() {
    return this.expected;
  }
}
