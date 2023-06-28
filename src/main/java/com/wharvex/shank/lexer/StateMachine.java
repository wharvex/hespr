package com.wharvex.shank.lexer;

import com.wharvex.shank.ExcType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StateMachine {

  private String exceptionProblem, tokenValueStringTemp;
  private ExcType exceptionType;
  private CharType[] charTypes; // Array of CharType enum vals at indices equal to their ASCII code
  private StateType curState, prevState; // State in current vs. previous position
  private char curChar; // The current character lex is reading
  public int curLineNum, curColNum, curLineLen, numLinesInFile, indentLevel;
  public List<Token> tokens;
  private HashMap<String, TokenType> knownWords = new HashMap<String, TokenType>();

  public StateMachine(int numLinesInFile) {
    this.numLinesInFile = numLinesInFile;
    this.curState = StateType.OUTSIDE;
    this.tokenValueStringTemp = "";
    this.curColNum = this.curLineNum = 0;
    this.charTypes = new CharType[128];
    this.tokens = new ArrayList<Token>();
    for (CharType ct : CharType.values()) {
      if (ct.range != null) {
        for (int i : ct.range) {
          this.charTypes[i] = ct;
        }
      }
    }
    // This populates the HashMap with the lookup strings (lowercase) and token types
    for (TokenType tt : TokenType.values()) {
      if (tt.typeType == Token.TokenTypeType.KNOWNWORD) {
        this.knownWords.put(tt.toString().toLowerCase(), tt);
      }
    }
    this.indentLevel = 0;
  }

  // PUBLIC METHODS

  /**
   * This is called exactly once per new char encountered in a line, so increment curColNum here.
   */
  public void setCurChar(char givenChar) {
    this.curChar = givenChar;
    this.curColNum++;
  }

  /**
   * This is called exactly once per new line read from the file, so increment curLineNum here.
   */
  public void setCurLineLen(int len) {
    this.curLineLen = len;
    this.curLineNum++;
  }

  /**
   * @param
   * @return
   */
  public int getTokensLength() {
    return this.tokens.size();
  }

  /**
   * @param
   * @return
   */
  public Token getTokenAt(int idx) {
    return this.tokens.get(idx);
  }

  /**
   * @param
   * @return
   */
  public void setCurColNum(int colNum) {
    this.curColNum = colNum;
  }

  /**
   * @param
   * @return
   */
  public void switchToErrorState() {
    this.setPrevStateToCurState();
    this.curState = StateType.ERROR;
  }

  /**
   * @param
   * @return
   */
  public void switchToOutsideState() {
    this.setPrevStateToCurState();
    this.curState = StateType.OUTSIDE;
  }

  /**
   * @param
   * @return
   */
  public boolean isCurStateError() {
    return this.getCurState() == StateType.ERROR;
  }

  /**
   * @param
   * @return
   */
  public boolean curCharMorphsCurState() {
    return this.curStateHasMorphCharTypes()
        && this.charTypeInColl(this.getCurCharType(), this.getCurState().morphToCharTypes);
  }

  /**
   * @param
   * @return
   */
  public void setExceptionDetails(ExcType exceptionType, String problem) {
    this.exceptionType = exceptionType;
    this.exceptionProblem = problem;
  }

  /**
   * @param
   * @return
   */
  public void emitEndLineToken() {
    this.tokens.add(new Token("ENDOFLINE", TokenType.ENDOFLINE, this.getCurLineNum()));
  }

  /**
   * @param
   * @return
   */
  public int getCurColNum() {
    return this.curColNum;
  }

  /**
   * @param
   * @return
   */
  public int getCurLineNum() {
    return this.curLineNum;
  }

  /**
   * @param
   * @return
   */
  public StateType getPrevState() {
    return this.prevState;
  }

  /**
   * @param
   * @return
   */
  public void setCurState(StateType state) {
    if (this.getCurState() != StateType.OUTSIDE) {
      this.setPrevStateToCurState();
    }
    this.curState = state;
  }

  /**
   * @param
   * @return
   */
  public ExcType getExceptionType() {
    return this.exceptionType;
  }

  /**
   * @param
   * @return
   */
  public void assessIndent() {
    int rawIndentsQty = this.getTotalTokensOfGivenTypeThisLine(TokenType.RAW_INDENT);
    int totalTokensThisLine = this.getTotalTokensThisLine();
    // If there are no non-space/tab characters (i.e. if there are ONLY space/tab characters) on the
    // line, don’t output an INDENT or DEDENT and don’t change the stored indentation level.
    // (If you call assessIndent after adding the ENDOFLINE token, this ternary won't work properly)
    int indentLevel = totalTokensThisLine == rawIndentsQty ? this.getIndentLevel() : rawIndentsQty;
    int indentsDiff = indentLevel - this.getIndentLevel();
    this.setIndentLevel(indentLevel);
    TokenType IndentOrDedent =
        indentsDiff < 0 ? TokenType.DEDENT : TokenType.INDENT;
    indentsDiff = Math.abs(indentsDiff);
    // Want to inspect Tokens for the current line, which should be the last ones in this.tokens
    // Find the index position of the last token of the line before curLine, so we can know where
    // to add indent or dedent tokens
    int prevLineEndTokenIdx = this.getPrevLineEndIdxAndRemoveRawIndents();
    for (int i = 0; i < indentsDiff; i++) {
      this.tokens.add(prevLineEndTokenIdx + 1, new Token("", IndentOrDedent, this.getCurLineNum()));
    }
    rawIndentsQty = this.getTotalTokensOfGivenTypeThisLine(TokenType.RAW_INDENT);
    totalTokensThisLine = this.getTotalTokensThisLine();
    if (totalTokensThisLine == rawIndentsQty) {
      for (int i = 0; i < totalTokensThisLine; i++) {
        this.tokens.remove(this.tokens.size() - 1);
      }
    }
  }

  /**
   * @param
   * @return
   */
  public String getExceptionProblem() {
    return this.exceptionProblem;
  }

  /**
   * @param
   * @return
   */
  public boolean curStateValidAsIs() {
    // if (this.curStateAtMaxCharLim())
    //   return !this.curStateNeedsClosure(
    //       SyntaxErrorException.ExcType.TOO_MANY_CHARS);
    return !(this.curStateBelowMinCharLim() || this.curStateNeedsClosure());
  }

  /**
   * @param
   * @return
   */
  public boolean curStateHasStopCharType() {
    return this.getCurState().stopCharType != CharType.NONE;
  }

  /**
   * @param
   * @return
   */
  public boolean atFileEnd() {
    return this.getNumLinesInFile() == this.getCurLineNum();
  }

  /**
   * @param
   * @return
   */
  public boolean curStateSpansLines() {
    if (this.atFileEnd()) {
      return false;
    }
    return this.curState.spansLines;
  }

  /**
   * @param
   * @return
   */
  public void accTokenIfNeeded() {
    if (!this.curStateHasTokenType()) {
      return;
    }
    this.tokenValueStringTemp += this.getCurChar();
  }

  /**
   * @param
   * @return
   */
  public CharAction getCharAction(CharAction rawCharAction) {
    if (this.atLineEnd()) {
      for (CharAction ca : CharAction.values()) {
        if (ca.normalForm == rawCharAction) {
          return ca;
        }
      }
    }
    return rawCharAction;
  }

  /**
   * @param
   * @return
   */
  public void emitTokenIfNeeded() {
    if (!this.curStateHasTokenType()
        || (this.getCurState() == StateType.SPACE
        && (this.getTVSTLen() < 4 || this.anyNonIndentTokensThisLine()))) {
      this.clearTokenValueStringTemp();
      return;
    }
    if (this.getCurState() == StateType.SPACE) {
      for (int i = 0; i < this.getTVSTLen() / 4; i++) {
        this.tokens.add(new Token("", TokenType.RAW_INDENT, this.getCurLineNum()));
      }
      this.clearTokenValueStringTemp();
      return;
    }

    // Handle known words
    TokenType tokenTypeToUse;
    String tokenValStrToUse;
    if (this.knownWords.containsKey(this.getTokenValueStringTemp())) {
      tokenTypeToUse = this.knownWords.get(this.getTokenValueStringTemp());
      tokenValStrToUse = "";
    } else {
      tokenTypeToUse = this.getCurState().tokenType;
      tokenValStrToUse = this.getTokenValueStringTemp();
    }
    this.clearTokenValueStringTemp();
    this.tokens.add(new Token(tokenValStrToUse, tokenTypeToUse, this.getCurLineNum()));
  }

  /**
   * @param
   * @return
   */
  public boolean isCurStateOutside() {
    return this.getCurState() == StateType.OUTSIDE;
  }

  /**
   * @param
   * @return
   */
  public boolean isCurCharOther() {
    return this.getCurCharType() == CharType.OTHER;
  }

  /**
   * @param
   * @return
   */
  public boolean isCurCharRCurly() {
    return this.getCurCharType() == CharType.RCURLY;
  }

  /**
   * @param
   * @return
   */
  public boolean curCharCanStartState() {
    return !(this.isCurCharOther() || this.isCurCharRCurly());
  }

  /**
   * @param
   * @return
   */
  public boolean curCharContinuesCurState() {
    return this.curStateHasContinueCharTypes()
        && this.charTypeInColl(this.getCurCharType(), this.getCurState().continueCharTypes);
  }

  /**
   * @param
   * @return
   */
  public StateType getStartState() {
    for (StateType st : StateType.values()) {
      if (st.startCharType == this.getCurCharType()) {
        return st;
      }
    }
    this.setExceptionDetails(
        ExcType.INTERNAL_ERROR,
        "State that can be started by curChar not found.");
    return StateType.ERROR;
  }

  /**
   * @param
   * @return
   */
  public boolean curCharStopsCurState() {
    return this.getCurCharType() == this.getCurState().stopCharType;
  }

  /**
   * @param
   * @return
   */
  public boolean curCharErrorsCurState() {
    return this.charTypeInColl(this.getCurCharType(), this.getCurState().errorCharTypes);
  }

  /**
   * @param
   * @return
   */
  public StateType getMorphState() {
    for (StateType st : StateType.values()) {
      if (this.getLastCharTypeFromTVST() == st.morphedFromCharType) {
        for (CharType ct : this.getCurState().morphToCharTypes) {
          if (ct == this.getCurCharType() && ct == st.morphIdentifier) {
            return st;
          }
        }
      }
    }
    this.setExceptionDetails(
        ExcType.INTERNAL_ERROR,
        "State that curChar can morph curState to not found.");
    return StateType.ERROR;
  }

  /**
   * @param
   * @return
   */
  public int getIndentLevel() {
    return this.indentLevel;
  }

  /**
   * @param
   * @return
   */
  public void forceEmitToken(TokenType tokenType, String valStr) {
    this.tokens.add(new Token(valStr, tokenType, this.getCurLineNum()));
  }

  // PRIVATE METHODS

  /**
   * @param
   * @return
   */
  private int getNumLinesInFile() {
    return this.numLinesInFile;
  }

  /**
   * @param
   * @return
   */
  private boolean curStateHasTokenType() {
    return this.getCurState().tokenType != TokenType.NONE;
  }

  /**
   * @param
   * @return
   */
  private boolean curStateHasContinueCharTypes() {
    return this.getCurState().continueCharTypes.length > 0;
  }

  /**
   * @param
   * @return
   */
  private void setPrevStateToCurState() {
    this.prevState = this.curState;
  }

  /**
   * @param
   * @return
   */
  private boolean curStateHasMorphCharTypes() {
    return this.getCurState().morphToCharTypes.length > 0;
  }

  /**
   * @param
   * @return
   */
  private boolean curStateHasMinCharLen() {
    return this.getCurState().minCharLen > -1;
  }

  /**
   * @param
   * @return
   */
  private String getTokenValueStringTemp() {
    return this.tokenValueStringTemp;
  }

  /**
   * @param
   * @return
   */
  private StateType getCurState() {
    return this.curState;
  }

  /**
   * @param
   * @return
   */
  private char getCurChar() {
    return this.curChar;
  }

  /**
   * @param
   * @return
   */
  private CharType getCurCharType() {
    return this.charTypes[(int) this.getCurChar()];
  }

  /**
   * @param
   * @return
   */
  private CharType getGivenCharType(char givenChar) {
    return this.charTypes[(int) givenChar];
  }

  /**
   * @param
   * @return
   */
  private CharType getLastCharTypeFromTVST() {
    if (this.getTokenValueStringTemp().length() == 0) {
      return CharType.NONE;
    }
    return this.getGivenCharType(
        this.getTokenValueStringTemp().charAt(this.getTokenValueStringTemp().length() - 1));
  }

  /**
   * @param
   * @return
   */
  private int getCurLineLen() {
    return this.curLineLen;
  }

  /**
   * @param
   * @return
   */
  private boolean atLineEnd() {
    return this.getCurLineLen() == this.getCurColNum();
  }

  /**
   * @param
   * @return
   */
  private void clearTokenValueStringTemp() {
    this.tokenValueStringTemp = "";
  }

  /**
   * @param
   * @return
   */
  private void setIndentLevel(int indentLevel) {
    this.indentLevel = indentLevel;
  }

  /**
   * @param
   * @return
   */
  private int getTotalTokensThisLine() {
    int ret = 0;
    for (int i = this.tokens.size() - 1; i >= 0; i--) {
      if (this.tokens.get(i).getTokenLineNum() == this.getCurLineNum()) {
        ret++;
      } else {
        break;
      }
    }
    return ret;
  }

  /**
   * @param
   * @return
   */
  private int getTotalTokensOfGivenTypeThisLine(TokenType givenType) {
    int ret = 0;
    for (int i = this.tokens.size() - 1; i >= 0; i--) {
      if (this.tokens.get(i).getTokenLineNum() == this.getCurLineNum()) {
        if (this.tokens.get(i).getTokenType() == givenType) {
          ret++;
        }
      } else {
        break;
      }
    }
    return ret;
  }

  /**
   * @param
   * @return
   */
  private int getPrevLineEndIdxAndRemoveRawIndents() {
    int prevLineEndTokenIdx = -1;
    for (int i = this.tokens.size() - 1; i >= 0; i--) {
      if (this.tokens.get(i).getTokenType() == TokenType.RAW_INDENT) {
        this.tokens.remove(i);
      }
      // Do this to avoid index out of bounds error which happens when the line is all spaces
      int indexToCheck = i == this.tokens.size() ? i - 1 : i;
      // No need to traverse tokens from earlier lines
      if (this.tokens.get(indexToCheck).getTokenLineNum() < this.getCurLineNum()) {
        prevLineEndTokenIdx = indexToCheck;
        break;
      }
    }
    return prevLineEndTokenIdx;
  }

  /**
   * @param
   * @return
   */
  private boolean curStateNeedsClosure() {
    if (!this.curStateHasStopCharType()) {
      return false;
    }
    CharType checkType =
        this.curStateHasTokenType() ? this.getLastCharTypeFromTVST() : this.getCurCharType();
    if (this.getCurState().stopCharType != checkType) {
      this.setExceptionDetails(ExcType.NEEDS_CLOSURE, "");
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param
   * @return
   */
  private int getTVSTLen() {
    return this.getTokenValueStringTemp().length();
  }

  /**
   * @param
   * @return
   */
  private boolean curStateBelowMinCharLim() {
    if (this.curStateHasMinCharLen() && this.getTVSTLen() < this.getCurState().minCharLen) {
      this.setExceptionDetails(
          ExcType.NOT_ENOUGH_CHARS, this.getTokenValueStringTemp());
      return true;
    } else {
      return false;
    }
  }

  /**
   * @param
   * @return
   */
  private boolean charTypeInColl(CharType charType, CharType[] coll) {
    for (CharType ct : coll) {
      if (charType == ct) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param
   * @return
   */
  private boolean anyNonIndentTokensThisLine() {
    // todo: find a better way to do this
    for (int i = this.tokens.size() - 1; i >= 0; i--) {
      if (this.tokens.get(i).getTokenLineNum() == this.getCurLineNum()
          && this.tokens.get(i).getTokenType() != TokenType.RAW_INDENT) {
        return true;
      } else {
        break;
      }
    }
    return false;
  }
}
