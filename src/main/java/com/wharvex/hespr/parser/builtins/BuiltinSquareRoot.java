package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.interpreter.RealDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
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
