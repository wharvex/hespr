package com.wharvex.hespr;

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
