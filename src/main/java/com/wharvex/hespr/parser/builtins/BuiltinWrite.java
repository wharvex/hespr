package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.InterpreterDataType;
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
