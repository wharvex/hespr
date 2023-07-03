package com.wharvex.hespr.parser.builtins;

import static org.junit.Assert.*;

import com.wharvex.hespr.interpreter.ArrayDataType;
import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.parser.VariableType;
import java.util.Arrays;
import org.junit.Test;

public class BuiltinEndTest {

  @Test
  public void execute() {
    var end = new BuiltinEnd();
    var endRet = new IntegerDataType(0);
    end.execute(Arrays.asList(new ArrayDataType(VariableType.INTEGER, 0, 5), endRet));
    assertEquals(5, endRet.getStoredVal());
  }
}