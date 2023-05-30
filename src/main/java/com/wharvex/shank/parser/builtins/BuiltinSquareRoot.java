package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.RealDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinSquareRoot extends BuiltinBase {

  // someFloat, var result
  public BuiltinSquareRoot() {
    super(
        "SquareRoot",
        Arrays.asList(
            new VariableNode("someFloat", VariableNode.VariableType.REAL, false, -1, -1, false),
            new VariableNode("result", VariableNode.VariableType.REAL, true, -1, -1, false)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((RealDataType) args.get(1)).setStoredVal(
        (float) Math.sqrt(((RealDataType) args.get(0)).getStoredVal()));
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
