package com.wharvex.shank.lexer;

import com.wharvex.shank.lexer.Token.TokenTypeType;

public enum TokenType {
  // KNOWNWORD
  DEFINE(TokenTypeType.KNOWNWORD),
  CONSTANTS(TokenTypeType.KNOWNWORD),
  VARIABLES(TokenTypeType.KNOWNWORD),
  VAR(TokenTypeType.KNOWNWORD),
  WRITE(TokenTypeType.KNOWNWORD),
  IF(TokenTypeType.KNOWNWORD),
  THEN(TokenTypeType.KNOWNWORD),
  ELSIF(TokenTypeType.KNOWNWORD),
  ELSE(TokenTypeType.KNOWNWORD),
  WHILE(TokenTypeType.KNOWNWORD),
  REPEAT(TokenTypeType.KNOWNWORD),
  UNTIL(TokenTypeType.KNOWNWORD),
  FOR(TokenTypeType.KNOWNWORD),
  FROM(TokenTypeType.KNOWNWORD),
  TO(TokenTypeType.KNOWNWORD),
  OF(TokenTypeType.KNOWNWORD),
  INTEGER(TokenTypeType.KNOWNWORD),
  REAL(TokenTypeType.KNOWNWORD),
  BOOLEAN(TokenTypeType.KNOWNWORD),
  CHARACTER(TokenTypeType.KNOWNWORD),
  STRING(TokenTypeType.KNOWNWORD),
  ARRAY(TokenTypeType.KNOWNWORD),
  MOD(TokenTypeType.KNOWNWORD),
  TRUE(TokenTypeType.KNOWNWORD),
  FALSE(TokenTypeType.KNOWNWORD),
  // OPERATOR
  PLUS(TokenTypeType.OPERATOR),
  MINUS(TokenTypeType.OPERATOR),
  TIMES(TokenTypeType.OPERATOR),
  DIVIDE(TokenTypeType.OPERATOR),
  LESSTHAN(TokenTypeType.OPERATOR),
  GREATERTHAN(TokenTypeType.OPERATOR),
  EQUALS(TokenTypeType.OPERATOR),
  LESSEQUAL(TokenTypeType.OPERATOR),
  GREATEREQUAL(TokenTypeType.OPERATOR),
  NOTEQUAL(TokenTypeType.OPERATOR),
  ASSIGN(TokenTypeType.OPERATOR),
  // LITERAL
  CHARACTERLITERAL(TokenTypeType.LITERAL),
  STRINGLITERAL(TokenTypeType.LITERAL),
  // INDENTATION
  INDENT(TokenTypeType.INDENTATION),
  DEDENT(TokenTypeType.INDENTATION),
  RAW_INDENT(TokenTypeType.INDENTATION),
  // PUNCTUATION
  COMMA(TokenTypeType.PUNCTUATION),
  COLON(TokenTypeType.PUNCTUATION),
  SEMICOLON(TokenTypeType.PUNCTUATION),
  LPAREN(TokenTypeType.PUNCTUATION),
  RPAREN(TokenTypeType.PUNCTUATION),
  LSQUARE(TokenTypeType.PUNCTUATION),
  RSQUARE(TokenTypeType.PUNCTUATION),
  // GENERAL
  IDENTIFIER(TokenTypeType.GENERAL),
  NUMBER(TokenTypeType.GENERAL),
  NUMBER_DECIMAL(TokenTypeType.GENERAL),
  ENDOFLINE(TokenTypeType.GENERAL),
  NONE(TokenTypeType.GENERAL),
  ENDOFFILE(TokenTypeType.GENERAL);

  final TokenTypeType typeType;

  TokenType(TokenTypeType typeType) {
    this.typeType = typeType;
  }
}
