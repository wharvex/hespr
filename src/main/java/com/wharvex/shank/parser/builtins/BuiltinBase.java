package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.parser.FunctionNode;
import com.wharvex.shank.parser.VariableNode;
import com.wharvex.shank.semantic.SemanticErrorException;
import java.util.List;

public abstract class BuiltinBase extends FunctionNode {

  // Don't use this
  public BuiltinBase(String name, List<VariableNode> params) {
    super(name, params);
  }

  public abstract void execute(List<InterpreterDataType> args) throws SemanticErrorException;

  public boolean variadicNeedsVar() {
    return false;
  }
}
