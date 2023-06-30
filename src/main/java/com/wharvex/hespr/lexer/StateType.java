package com.wharvex.hespr.lexer;

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
  TAB(
      CharType.TAB, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.NONE, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.RAW_INDENT), // tokenType
  COMMENT(
      CharType.LCURLY, // startCharType
      new CharType[]{}, // continueCharTypes
      CharType.RCURLY, // stopCharType
      CharType.NONE, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.NONE, // morphIdentifier
      true, // spansLines
      -1, // minCharLen
      new CharType[]{}, // errorCharTypes
      TokenType.NONE),
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
      new CharType[]{}, // morphToCharTypes
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
      new CharType[]{}, // morphToCharTypes
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
      new CharType[]{CharType.EQUALS}, // morphToCharTypes
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
      CharType.COLON, // morphedFromCharType
      new CharType[]{}, // morphToCharTypes
      CharType.EQUALS, // morphIdentifier
      false, // spansLines
      -1, // minCharLen
      new CharType[]{CharType.RCURLY, CharType.OTHER}, // errorCharTypes
      TokenType.ASSIGN), // tokenType
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
      TokenType.NONE); // tokenType
  CharType startCharType, morphedFromCharType, morphIdentifier, stopCharType;
  CharType[] continueCharTypes, morphToCharTypes, errorCharTypes;
  TokenType tokenType; // The TokenType associated with the state
  boolean spansLines;
  int minCharLen;

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
}
