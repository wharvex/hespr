package com.wharvex.hespr.parser;

import com.wharvex.hespr.ExcType;
import com.wharvex.hespr.SyntaxErrorException;
import com.wharvex.hespr.lexer.TokenType;
import java.util.List;
import java.util.stream.Collectors;

public class ParserHelper {

  static final List<TokenType> dataTypes = List.of(
      TokenType.REAL,
      TokenType.INTEGER,
      TokenType.BOOLEAN,
      TokenType.STRING,
      TokenType.CHARACTER);
  static final List<TokenType> dataTypesIncArr = List.of(
      TokenType.ARRAY,
      TokenType.REAL,
      TokenType.INTEGER,
      TokenType.BOOLEAN,
      TokenType.STRING,
      TokenType.CHARACTER);
  static final List<TokenType> varConstIndent = List.of(
      TokenType.VARIABLES,
      TokenType.CONSTANTS,
      TokenType.INDENT);
  static final List<TokenType> literalTypesIncMinus = List.of(
      TokenType.NUMBER_DECIMAL,
      TokenType.NUMBER,
      TokenType.STRINGLITERAL,
      TokenType.CHARACTERLITERAL,
      TokenType.TRUE,
      TokenType.FALSE,
      TokenType.MINUS);
  static final List<TokenType> statementInitTypes = List.of(
      TokenType.FOR,
      TokenType.WHILE,
      TokenType.REPEAT,
      TokenType.IF,
      TokenType.IDENTIFIER,
      TokenType.DEDENT);
  static final List<TokenType> factorTypes = List.of(
      TokenType.MINUS,
      TokenType.NUMBER,
      TokenType.NUMBER_DECIMAL,
      TokenType.IDENTIFIER,
      TokenType.LPAREN,
      TokenType.TRUE,
      TokenType.FALSE,
      TokenType.STRINGLITERAL,
      TokenType.CHARACTERLITERAL);
  static final List<TokenType> termOpTypes = List.of(
      TokenType.TIMES,
      TokenType.MOD,
      TokenType.DIVIDE);
  static final List<TokenType> compareTypes = List.of(
      TokenType.LESSEQUAL, TokenType.LESSTHAN, TokenType.GREATEREQUAL, TokenType.GREATERTHAN,
      TokenType.NOTEQUAL, TokenType.EQUALS);
  static final List<TokenType> expOpTypes = List.of(TokenType.MINUS, TokenType.PLUS);
  static final List<TokenType> expOrTermEndTypes = List.of(TokenType.RPAREN,
      TokenType.ENDOFLINE, TokenType.THEN, TokenType.COMMA);
  static final List<TokenType> numberTypes = List.of(TokenType.NUMBER, TokenType.NUMBER_DECIMAL);

  public static String listToString(List<?> l) {
    return l == null ? "" : l.stream().map(Object::toString).collect(Collectors.joining("\n    "));
  }

  public static String listToStringInline(List<?> l) {
    return l == null ? "" : l.stream().map(Object::toString).collect(Collectors.joining(", "));
  }

  static VariableType getVarTypeFromTokenType(TokenType tt) {
    return switch (tt) {
      case STRING, STRINGLITERAL -> VariableType.STRING;
      case CHARACTER, CHARACTERLITERAL -> VariableType.CHARACTER;
      case INTEGER, NUMBER -> VariableType.INTEGER;
      case REAL, NUMBER_DECIMAL -> VariableType.REAL;
      case BOOLEAN, TRUE, FALSE -> VariableType.BOOLEAN;
      default -> VariableType.ANY;
    };
  }

  static CompareType getCompTypeFromTokenType(TokenType tt) throws SyntaxErrorException {
    return switch (tt) {
      case EQUALS -> CompareType.EQUALS;
      case GREATEREQUAL -> CompareType.GREATEREQUAL;
      case LESSEQUAL -> CompareType.LESSEQUAL;
      case GREATERTHAN -> CompareType.GREATERTHAN;
      case LESSTHAN -> CompareType.LESSTHAN;
      default -> throw new SyntaxErrorException(ExcType.INTERNAL_ERROR, "");
    };
  }
}
