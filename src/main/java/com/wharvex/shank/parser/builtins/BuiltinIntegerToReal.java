package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.RealDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.parser.ParserHelper;
import com.wharvex.shank.parser.nodes.VariableNode;
import com.wharvex.shank.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinIntegerToReal extends BuiltinBase {

  /* someInteger, var someReal */
  public BuiltinIntegerToReal() {
    super(
        "IntegerToReal",
        Arrays.asList(
            new VariableNode(
                "someInteger", VariableType.INTEGER, false, false, -1),
            new VariableNode("someReal", VariableType.REAL, true, false, -1)));
  }

  public void execute(List<InterpreterDataType> args) {
    ((RealDataType) args.get(1)).setStoredVal(((IntegerDataType) args.get(0)).getStoredVal());
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
