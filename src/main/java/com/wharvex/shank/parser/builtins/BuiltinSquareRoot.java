package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.RealDataType;
import com.wharvex.shank.parser.ParserHelper;
import com.wharvex.shank.parser.nodes.VariableNode;
import com.wharvex.shank.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinSquareRoot extends BuiltinBase {

  // someFloat, var result
  public BuiltinSquareRoot() {
    super(
        "SquareRoot",
        Arrays.asList(
            new VariableNode("someFloat", VariableType.REAL, false, false, -1),
            new VariableNode("result", VariableType.REAL, true, false, -1)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((RealDataType) args.get(1)).setStoredVal(
        (float) Math.sqrt(((RealDataType) args.get(0)).getStoredVal()));
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
