package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.InterpreterDataType;
import java.util.List;

public class BuiltinWrite extends BuiltinBase {

  public BuiltinWrite() {
    super("Write", null);
  }

  public void execute(List<InterpreterDataType> args) {
    System.out.println();
    for (InterpreterDataType arg : args) {
      System.out.print(arg + " ");
    }
  }

  @Override
  public boolean isVariadic() {
    return true;
  }

  @Override
  public String toString() {
    return this.getName() + " {}\n";
  }
}
