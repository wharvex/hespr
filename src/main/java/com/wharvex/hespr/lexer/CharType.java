package com.wharvex.hespr.lexer;

import java.util.stream.*;

public enum CharType {
  // Every ASCII char code from 0 to 127 other than the ones enumerated below
  OTHER(true),
  // Letters are ASCII char codes 65-90 (uppercase) and 97-122 (lowercase)
  LETTER(IntStream.iterate(65, n -> n == 90 ? n + 7 : n + 1).limit(52).toArray()),
  // Numbers are ASCII char codes 48-57
  DIGIT(IntStream.rangeClosed(48, 57).toArray()),
  // A decimal point is ASCII char code 46
  // A space is ASCII char code 32
  SPACE(32),
  BANG(33),
  PIPE(124),
  TILDE(126),
  DECIMAL(46),
  STAR(42),
  PLUS(43),
  COMMA(44),
  MINUS(45),
  COLON(58),
  SLASH(47),
  SEMICOLON(59),
  LESSTHAN(60),
  EQUALS(61),
  GREATERTHAN(62),
  LPAREN(40),
  RPAREN(41),
  LSQUARE(91),
  RSQUARE(93),
  SINGLEQUOTE(39),
  DBLQUOTE(34),
  LCURLY(123),
  RCURLY(125),
  DOLLAR(36),
  UNDERSCORE(95),
  NONE(false),
  ANY_EXCEPT_RCURLY(true, 125);
  int[] range; // The range of ASCII character codes that fit the CharType
  Integer except;

  /**
   * Use this constructor for character ranges.
   */
  CharType(int[] range) {
    this.range = range;
  }

  /**
   * Use this constructor for single characters.
   */
  CharType(int code) {
    this.range = new int[]{code};
  }

  /**
   * Use this constructor as a catch-all or for CharacterType vals that signal special cases.
   * Everything in charTypes starts out as whatever CharType uses this constructor, and then all the
   * other CharTypes get added to charTypes in their ascii-code-correspondent index positions in the
   * StateMachine constructor
   */
  CharType(boolean catchAll) {
    if (catchAll) {
      this.range = IntStream.range(0, 127).toArray();
    }
  }

  CharType(boolean catchAll, int except) {
    if (catchAll) {
      this.except = except;
    }
  }
}
