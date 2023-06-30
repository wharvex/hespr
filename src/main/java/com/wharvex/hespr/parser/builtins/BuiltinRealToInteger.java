package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.interpreter.RealDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
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
