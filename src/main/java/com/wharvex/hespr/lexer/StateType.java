package com.wharvex.hespr.lexer;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Each StateType has charTypes that can start, continue, morph, or stop the state.
 *
 * <p>A state "morphs" when it changes but does not emit a token and keeps accumulating
 * tokenValueStringTemp.
 */
public enum StateType {

  // BEGIN SPACE STATES

  SPACE(
      CharType.SPACE, // startCharType
      new CharType[]{CharType.SPACE}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.RAW_INDENT), // tokenType
  COMMENT(
      CharType.NONE, // startCharType
      new CharType[]{CharType.ANY_EXCEPT_RCURLY}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.LCURLY, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.LCURLY, // morphIdentifier
      true, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.NONE),
  LCURLY(
      CharType.LCURLY, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.LCURLY}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.LCURLY),
  RCURLY(
      CharType.RCURLY, // startCharType
      new CharType[]{CharType.RCURLY}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      2, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.LCURLY,
      2),
  STRINGLITERAL(
      CharType.DBLQUOTE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.DBLQUOTE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      2, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.STRINGLITERAL),
  CHARACTERLITERAL(
      CharType.SINGLEQUOTE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.SINGLEQUOTE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      2, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.CHARACTERLITERAL),
  STAR(
      CharType.STAR, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.TIMES),
  PLUS(
      CharType.PLUS, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.PLUS),
  COMMA(
      CharType.COMMA, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.COMMA),
  MINUS(
      CharType.MINUS, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.GREATERTHAN}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.MINUS), // tokenType
  SLASH(
      CharType.SLASH, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.DIVIDE), // tokenType
  SEMICOLON(
      CharType.SEMICOLON, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.SEMICOLON), // tokenType
  DOLLAR(
      CharType.DOLLAR, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.VAR), // tokenType
  UNDERSCORE(
      CharType.UNDERSCORE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.UNDERSCORE), // tokenType
  LPAREN(
      CharType.LPAREN, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.LPAREN), // tokenType
  RPAREN(
      CharType.RPAREN, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.RPAREN), // tokenType
  LSQUARE(
      CharType.LSQUARE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.LSQUARE), // tokenType
  BANG(
      CharType.BANG, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.BANG), // tokenType
  PIPE(
      CharType.PIPE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.PIPE), // tokenType
  TILDE(
      CharType.TILDE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.TILDE), // tokenType
  RSQUARE(
      CharType.RSQUARE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.RSQUARE), // tokenType
  EQUALS(
      CharType.EQUALS, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.UNDERSCORE}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.EQUALS), // tokenType
  COLON(
      CharType.COLON, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.COLON), // tokenType
  LESSTHAN(
      CharType.LESSTHAN, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.EQUALS, CharType.GREATERTHAN}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.LESSTHAN), // tokenType
  GREATERTHAN(
      CharType.GREATERTHAN, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.EQUALS}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.GREATERTHAN), // tokenType
  GREATEREQUAL(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.GREATERTHAN, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.EQUALS, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.GREATEREQUAL), // tokenType
  LESSEQUAL(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.LESSTHAN, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.EQUALS, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.LESSEQUAL), // tokenType
  NOTEQUAL(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.LESSTHAN, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.GREATERTHAN, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.NOTEQUAL), // tokenType
  ASSIGN(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.EQUALS, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.UNDERSCORE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.ASSIGN), // tokenType
  ARROW(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.MINUS, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.GREATERTHAN, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.ARROW), // tokenType
  IDENTIFIER(
      CharType.LETTER, // startCharType
      new CharType[]{CharType.LETTER, CharType.DIGIT}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.DECIMAL, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.IDENTIFIER), // tokenType
  NUMBER(
      CharType.DIGIT, // startCharType
      new CharType[]{CharType.DIGIT}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{CharType.DECIMAL}, // morphToCharTypes
      CharType.DECIMAL, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.LETTER, CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.NUMBER), // tokenType
  NUMBER_DECIMAL(
      CharType.DECIMAL, // startCharType
      new CharType[]{CharType.DIGIT}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.DIGIT, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.DECIMAL, // morphIdentifier
      false, // spansLines
      2, // minCharLen
      new CharType[]{CharType.LETTER, CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.NUMBER_DECIMAL), // tokenType
  OUTSIDE(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      true, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.NONE), // tokenType
  ERROR(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.NONE), // tokenType
  NONE(
      CharType.NONE, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.NONE); // tokenType
  CharType startCharType, morphedFromCharType, morphIdentifier, stopCharType;
  CharType[] continueCharTypes, morphToCharTypes, errorCharTypes;
  TokenType tokenType; // The TokenType associated with the state
  boolean spansLines;
  int minCharLen;

  private static final class Helper {

    static Map<StateType, Integer> MAX_CHAR_LEN = new HashMap<>();
  }

  static {
    for (StateType st : values()) {
      Helper.MAX_CHAR_LEN.put(st, -1);
    }
  }

  public static Optional<Integer> getMaxCharLen(StateType st) {
    return Optional.ofNullable(Helper.MAX_CHAR_LEN.get(st));
  }

  StateType(
      CharType startCharType,
      CharType[] continueCharTypes,
      CharType stopCharType,
      CharType morphedFromCharType,
      CharType[] morphToCharTypes,
      CharType morphIdentifier,
      boolean spansLines,
      int minCharLen,
      CharType[] errorCharTypes,
      TokenType tokenType) {
    this.startCharType = startCharType;
    this.continueCharTypes = continueCharTypes;
    this.stopCharType = stopCharType;
    this.morphedFromCharType = morphedFromCharType;
    this.morphToCharTypes = morphToCharTypes;
    this.morphIdentifier = morphIdentifier;
    this.spansLines = spansLines;
    this.minCharLen = minCharLen;
    this.errorCharTypes = errorCharTypes;
    this.tokenType = tokenType;
  }

  StateType(
      CharType startCharType,
      CharType[] continueCharTypes,
      CharType stopCharType,
      CharType morphedFromCharType,
      CharType[] morphToCharTypes,
      CharType morphIdentifier,
      boolean spansLines,
      int minCharLen,
      CharType[] errorCharTypes,
      TokenType tokenType,
      int maxCharLen) {
    this.startCharType = startCharType;
    this.continueCharTypes = continueCharTypes;
    this.stopCharType = stopCharType;
    this.morphedFromCharType = morphedFromCharType;
    this.morphToCharTypes = morphToCharTypes;
    this.morphIdentifier = morphIdentifier;
    this.spansLines = spansLines;
    this.minCharLen = minCharLen;
    this.errorCharTypes = errorCharTypes;
    this.tokenType = tokenType;
    Helper.MAX_CHAR_LEN.put(this, maxCharLen);
  }
}
