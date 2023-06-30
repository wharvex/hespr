package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.parser.nodes.FunctionNode;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.semantic.SemanticErrorException;
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
