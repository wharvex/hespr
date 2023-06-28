package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.RealDataType;
import com.wharvex.shank.parser.ParserHelper;
import com.wharvex.shank.parser.nodes.VariableNode;
import com.wharvex.shank.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinRealToInteger extends BuiltinBase {

  /* someReal, var someInt */
  public BuiltinRealToInteger() {
    super(
        "RealToInteger",
        Arrays.asList(
            new VariableNode("someReal", VariableType.REAL, false, false, -1),
            new VariableNode("someInt", VariableType.INTEGER, true, false, -1)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal((int) ((RealDataType) args.get(0)).getStoredVal());
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
