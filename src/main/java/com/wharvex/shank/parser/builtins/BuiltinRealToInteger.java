package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.RealDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinRealToInteger extends BuiltinBase {

  /* someReal, var someInt */
  public BuiltinRealToInteger() {
    super(
        "RealToInteger",
        Arrays.asList(
            new VariableNode("someReal", VariableNode.VariableType.REAL, false, -1, -1, false),
            new VariableNode("someInt", VariableNode.VariableType.INTEGER, true, -1, -1, false)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal((int) ((RealDataType) args.get(0)).getStoredVal());
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
