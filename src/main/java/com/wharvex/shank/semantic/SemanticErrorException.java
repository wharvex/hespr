package com.wharvex.shank.semantic;

import com.wharvex.shank.SyntaxErrorException.ExcType;

public class SemanticErrorException extends Exception {

  public SemanticErrorException(String problem) {
    super(problem);
  }
}
