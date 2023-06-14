package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.ArrayDataType;
import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinEnd extends BuiltinBase {

  /* end = the last index of this array */
  public BuiltinEnd() {
    super(
        "End",
        Arrays.asList(
            new VariableNode("someArray", VariableNode.VariableType.ANY, false, true, -1),
            new VariableNode("end", VariableNode.VariableType.INTEGER, true, false, -1)));
  }

  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal(((ArrayDataType) args.get(0)).getTo());
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
