package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.StringDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinRight extends BuiltinBase {

  /* someString, length, var resultString */
  /* resultString = last length characters of someString */
  public BuiltinRight() {
    super(
        "Right",
        Arrays.asList(
            new VariableNode("someString", VariableNode.VariableType.STRING, false, false, -1),
            new VariableNode("length", VariableNode.VariableType.INTEGER, false, false, -1),
            new VariableNode(
                "resultString", VariableNode.VariableType.STRING, true, false, -1)));
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
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
