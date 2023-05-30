package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.ArrayDataType;
import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinStart extends BuiltinBase {

  /* start = the first index of this array */
  public BuiltinStart() {
    super(
        "Start",
        Arrays.asList(
            new VariableNode("someArray", VariableNode.VariableType.ANY, false, -1, -1, true),
            new VariableNode("start", VariableNode.VariableType.INTEGER, true, -1, -1, false)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal(((ArrayDataType) args.get(0)).getFrom());
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
