package com.wharvex.hespr.lexer;

public enum CharAction {
  STOP_STATE_AS_NON_MEMBER,
  STOP_STATE_AS_MEMBER,
  START_STATE,
  CONTINUE_STATE,
  MORPH_STATE,
  STOP_STATE_AS_NON_MEMBER_LINE_END(CharAction.STOP_STATE_AS_NON_MEMBER),
  STOP_STATE_AS_MEMBER_LINE_END(CharAction.STOP_STATE_AS_MEMBER),
  START_STATE_LINE_END(CharAction.START_STATE),
  CONTINUE_STATE_LINE_END(CharAction.CONTINUE_STATE),
  MORPH_STATE_LINE_END(CharAction.MORPH_STATE),
  ERROR;
  CharAction normalForm;

  CharAction(CharAction normalForm) {
    this.normalForm = normalForm;
  }

  CharAction() {
  }
}
