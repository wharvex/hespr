package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.interpreter.StringDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinRight extends BuiltinBase {

  /* someString, length, var resultString */
  /* resultString = last length characters of someString */
  public BuiltinRight() {
    super(
        "Right",
        Arrays.asList(
            new VariableNode("someString", VariableType.STRING, false, false, -1),
            new VariableNode("length", VariableType.INTEGER, false, false, -1),
            new VariableNode(
                "resultString", VariableType.STRING, true, false, -1)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((StringDataType) args.get(2)).setStoredVal(
        ((StringDataType) args.get(0))
            .getStoredVal()
            .substring(
                ((StringDataType) args.get(0)).getStoredVal().length()
                    - ((IntegerDataType) args.get(1)).getStoredVal()
            ));
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
