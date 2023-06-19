package com.wharvex.shank;

import com.wharvex.shank.lexer.StateType;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.TokenType;
import java.util.List;
import java.util.StringJoiner;

public class SyntaxErrorException extends Exception {

  public enum ExcType {
    INVALID_CHAR("Current state encountered invalid character. "),
    NEEDS_CLOSURE("Closure needed before stopping current state. "),
    TOO_MANY_CHARS("Too many characters in Token.valueString "),
    NOT_ENOUGH_CHARS("Not enough characters in Token.valueString "),
    INTERNAL_ERROR("Something went wrong internally. "),
    FUNCTION_ERROR("function() expected "),
    PARAMETERS_ERROR("While parsing function parameter declarations, expected "),
    VARIABLES_ERROR("While parsing variable declarations, expected "),
    CONSTANTS_ERROR("While parsing constant declarations, expected "),
    IDX_EXP_ERROR(""),
    ASSIGNMENT_ERROR("assignment() "),
    STATEMENTS_ERROR("statements() expected "),
    EOL_ERROR("Expected "),
    EXPRESSION_ERROR("expression() expected "),
    TERM_ERROR("term() expected "),
    FACTOR_ERROR("factor() expected "),
    FOR_ERROR("parseFor() expected "),
    REPEAT_ERROR("parseRepeat() expected "),
    IF_ERROR("parseIf() expected "),
    FUNC_CALL_ERROR("functionCall() expected "),
    EOF_ERROR("Unexpected end of file");
    String baseMessage;

    ExcType(String baseMessage) {
      this.baseMessage = baseMessage;
    }
  }

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

  private static String joinExpectedTokenTypes(List<TokenType> tts) {
    StringJoiner ret = new StringJoiner(", ");
    tts.forEach(tt -> ret.add(tt.toString()));
    return ret.toString();
  }

  public String getExpected() {
    return this.expected;
  }
}
