package com.wharvex.shank.parser.builtins;

import static org.junit.jupiter.api.Assertions.*;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.StringDataType;
import com.wharvex.shank.semantic.SemanticErrorException;
import java.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BuiltinLeftTest {

  BuiltinLeft left;
  StringDataType leftResult;
  StringDataType someString;
  IntegerDataType len;

  @BeforeEach
  void setUp() {
    left = new BuiltinLeft();
    leftResult = new StringDataType("");
    someString = new StringDataType("cybernetics");
    len = new IntegerDataType(5);
  }

  @Test
  void execute() throws SemanticErrorException {
    left.execute(Arrays.asList(someString, len, leftResult));
    assertEquals("cyber", leftResult.getStoredVal());
  }

  @Test
  void executeLenTooBig() throws SemanticErrorException {
    len.setStoredVal(20);
    left.execute(Arrays.asList(someString, len, leftResult));
    assertEquals("cybernetics", leftResult.getStoredVal());
  }

  @Test
  void executeLenTooSmall() throws SemanticErrorException {
    len.setStoredVal(-1);
    assertThrows(SemanticErrorException.class,
        () -> left.execute(Arrays.asList(someString, len, leftResult)));
  }

  @AfterEach
  void tearDown() {
    left = null;
    leftResult = null;
    someString = null;
    len = null;
  }
}