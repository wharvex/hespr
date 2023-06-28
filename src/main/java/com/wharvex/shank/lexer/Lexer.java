package com.wharvex.shank.lexer;

import com.wharvex.shank.ExcType;
import com.wharvex.shank.SyntaxErrorException;

public class Lexer {

  public StateMachine stateMachine;
  private CharAction charAction;

  public Lexer(int numLinesInFile) {
    this.stateMachine = new StateMachine(numLinesInFile);
  }

  public void printTokens() {
    for (int i = 0; i < this.stateMachine.getTokensLength(); i++) {
      System.out.println(this.stateMachine.getTokenAt(i));
    }
  }

  public void lex(String line) throws Exception {
    int i = 0;
    this.stateMachine.setCurColNum(0);
    this.stateMachine.setCurLineLen(line.length());
    while (i < line.length() && !this.stateMachine.isCurStateError()) {
      this.stateMachine.setCurChar(line.charAt(i++));

      // DETERMINE VALUE OF charAction

      if (this.stateMachine.isCurStateOutside()) {
        if (!this.stateMachine.curCharCanStartState()) {
          this.charAction = CharAction.ERROR;
          this.stateMachine.setExceptionDetails(
              ExcType.INVALID_CHAR, "Invalid character to start a new state.");
        } else {
          this.charAction = this.stateMachine.getCharAction(CharAction.START_STATE);
        }
      } else if (this.stateMachine.curStateHasStopCharType()) {
        this.charAction =
            this.stateMachine.curCharStopsCurState()
                ? this.stateMachine.getCharAction(CharAction.STOP_STATE_AS_MEMBER)
                : this.stateMachine.getCharAction(CharAction.CONTINUE_STATE);
      } else {
        if (this.stateMachine.curCharContinuesCurState()) {
          this.charAction = this.stateMachine.getCharAction(CharAction.CONTINUE_STATE);
        } else if (this.stateMachine.curCharMorphsCurState()) {
          this.charAction = this.stateMachine.getCharAction(CharAction.MORPH_STATE);
        } else if (this.stateMachine.curCharErrorsCurState()) {
          this.charAction = CharAction.ERROR;
          this.stateMachine.setExceptionDetails(ExcType.INVALID_CHAR, "");
        } else {
          this.charAction = this.stateMachine.getCharAction(CharAction.STOP_STATE_AS_NON_MEMBER);
        }
      }

      // FOLLOW THE charAction

      switch (this.charAction) {

        // NORMAL ACTIONS

        case STOP_STATE_AS_NON_MEMBER:
          if (this.stateMachine.curStateValidAsIs()) {
            this.stateMachine.emitTokenIfNeeded();
            if (this.stateMachine.curCharCanStartState()) {
              this.stateMachine.setCurState(this.stateMachine.getStartState());
              this.stateMachine.accTokenIfNeeded();
            } else {
              this.stateMachine.setExceptionDetails(
                  ExcType.INVALID_CHAR,
                  "Invalid character to start a new state: ");
              this.stateMachine.switchToErrorState();
            }
          } else {
            this.stateMachine.switchToErrorState();
          }
          break;
        case STOP_STATE_AS_MEMBER:
        case STOP_STATE_AS_MEMBER_LINE_END:
          this.stateMachine.accTokenIfNeeded();
          this.stateMachine.emitTokenIfNeeded();
          this.stateMachine.switchToOutsideState();
          break;
        case START_STATE:
          this.stateMachine.setCurState(this.stateMachine.getStartState());
          this.stateMachine.accTokenIfNeeded();
          break;
        case CONTINUE_STATE:
          this.stateMachine.accTokenIfNeeded();
          break;
        case MORPH_STATE:
          this.stateMachine.setCurState(this.stateMachine.getMorphState());
          this.stateMachine.accTokenIfNeeded();
          break;

        // LINE END ACTIONS

        case STOP_STATE_AS_NON_MEMBER_LINE_END:
          if (this.stateMachine.curStateValidAsIs()) {
            this.stateMachine.emitTokenIfNeeded();
            if (this.stateMachine.curCharCanStartState()) {
              this.stateMachine.setCurState(this.stateMachine.getStartState());
              this.stateMachine.accTokenIfNeeded();
              if (this.stateMachine.curStateSpansLines()) {
                break;
              } else {
                if (this.stateMachine.curStateValidAsIs()) {
                  this.stateMachine.emitTokenIfNeeded();
                  this.stateMachine.switchToOutsideState();
                } else {
                  this.stateMachine.switchToErrorState();
                }
              }
            } else {
              this.stateMachine.setExceptionDetails(
                  ExcType.INVALID_CHAR,
                  "Invalid character to start a new state.");
              this.stateMachine.switchToErrorState();
            }
          } else {
            this.stateMachine.switchToErrorState();
          }
          break;
        case START_STATE_LINE_END:
          this.stateMachine.setCurState(this.stateMachine.getStartState());
          this.stateMachine.accTokenIfNeeded();
          if (this.stateMachine.curStateSpansLines()) {
            this.stateMachine.accTokenIfNeeded();
          } else {
            if (this.stateMachine.curStateValidAsIs()) {
              this.stateMachine.emitTokenIfNeeded();
              this.stateMachine.switchToOutsideState();
            } else {
              this.stateMachine.switchToErrorState();
            }
          }
          break;
        case CONTINUE_STATE_LINE_END:
          this.stateMachine.accTokenIfNeeded();
          if (this.stateMachine.curStateSpansLines()) {
            break;
          } else {
            if (this.stateMachine.curStateValidAsIs()) {
              this.stateMachine.emitTokenIfNeeded();
              this.stateMachine.switchToOutsideState();
            } else {
              this.stateMachine.switchToErrorState();
            }
          }
          break;
        case MORPH_STATE_LINE_END:
          this.stateMachine.setCurState(this.stateMachine.getMorphState());
          this.stateMachine.accTokenIfNeeded();
          if (this.stateMachine.curStateSpansLines()) {
            break;
          } else {
            if (this.stateMachine.curStateValidAsIs()) {
              this.stateMachine.emitTokenIfNeeded();
              this.stateMachine.switchToOutsideState();
            } else {
              this.stateMachine.switchToErrorState();
            }
          }
          break;
        case ERROR:
          this.stateMachine.switchToErrorState();
          break;
      }
    }

    // THROW ERROR IF NEEDED

    if (this.stateMachine.isCurStateError()) {
      throw new SyntaxErrorException(
          this.stateMachine.getExceptionType(),
          this.stateMachine.getPrevState(),
          this.stateMachine.getCurLineNum(),
          this.stateMachine.getCurColNum(),
          this.stateMachine.getExceptionProblem());
    }
    // Call assessIndent before emitEndLineToken to make the "ignore all-space lines" logic work
    this.stateMachine.assessIndent();
    this.stateMachine.emitEndLineToken();
    if (this.stateMachine.atFileEnd()) {
      // If indent level remained 0 through the whole file, then this won't emit the dedents
      // necessary for the parser to stop parsing without error. Make sure the Parser eventually
      // requires at least one indent
      for (int j = 0; j < this.stateMachine.getIndentLevel(); j++) {
        this.stateMachine.forceEmitToken(TokenType.DEDENT, "");
      }
    }
  }
}
