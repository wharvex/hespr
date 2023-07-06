package com.wharvex.hespr.lexer;

import com.wharvex.hespr.lexer.Token.TokenTypeType;

public enum TokenType {
  // KNOWNWORD
  BLOK(TokenTypeType.KNOWNWORD),
  PERM(TokenTypeType.KNOWNWORD),
  FLUX(TokenTypeType.KNOWNWORD),
  WHEN(TokenTypeType.KNOWNWORD),
  THEN(TokenTypeType.KNOWNWORD),
  ELIF(TokenTypeType.KNOWNWORD),
  ELSE(TokenTypeType.KNOWNWORD),
  WHIL(TokenTypeType.KNOWNWORD),
  TILL(TokenTypeType.KNOWNWORD),
  UNTIL(TokenTypeType.KNOWNWORD),
  WITH(TokenTypeType.KNOWNWORD),
  FROM(TokenTypeType.KNOWNWORD),
  TO(TokenTypeType.KNOWNWORD),
  OF(TokenTypeType.KNOWNWORD),
  INT(TokenTypeType.KNOWNWORD),
  REAL(TokenTypeType.KNOWNWORD),
  BOOL(TokenTypeType.KNOWNWORD),
  CHAR(TokenTypeType.KNOWNWORD),
  STR(TokenTypeType.KNOWNWORD),
  ARR(TokenTypeType.KNOWNWORD),
  MOD(TokenTypeType.KNOWNWORD),
  TRUE(TokenTypeType.KNOWNWORD),
  FALS(TokenTypeType.KNOWNWORD),
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
  ARROW(TokenTypeType.OPERATOR),
  UNDERSCORE(TokenTypeType.OPERATOR),
  BANG(TokenTypeType.OPERATOR),
  PIPE(TokenTypeType.OPERATOR),
  TILDE(TokenTypeType.OPERATOR),
  VAR(TokenTypeType.OPERATOR),
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
  LCURLY(TokenTypeType.PUNCTUATION),
  RCURLY(TokenTypeType.PUNCTUATION),
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
