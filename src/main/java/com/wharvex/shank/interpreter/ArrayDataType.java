package com.wharvex.shank.interpreter;

import com.wharvex.shank.parser.ParserHelper;
import com.wharvex.shank.parser.VariableType;
import java.util.ArrayList;
import java.util.List;

public class ArrayDataType extends InterpreterDataType {

  private List<InterpreterDataType> storedVal;
  private VariableType arrType;
  private int from, to;

  public int getFrom() {
    return from;
  }

  public int getTo() {
    return to;
  }

  public VariableType getArrType() {
    return arrType;
  }

  public ArrayDataType(VariableType arrType, int from, int to) {
    this.storedVal = new ArrayList<InterpreterDataType>();
    this.arrType = arrType;
    while (this.storedVal.size() <= to) {
      switch (this.arrType) {
        case STRING -> {
          this.storedVal.add(new StringDataType(""));
        }
        case CHARACTER -> {
          this.storedVal.add(new CharacterDataType('0'));
        }
        case INTEGER -> {
          this.storedVal.add(new IntegerDataType(0));
        }
        case REAL -> {
          this.storedVal.add(new RealDataType(0));
        }
        case BOOLEAN -> {
          this.storedVal.add(new BooleanDataType(false));
        }
      }
    }
    this.from = from;
    this.to = to;
  }

  public List<InterpreterDataType> getStoredVal() {
    return this.storedVal;
  }

  public void setArrElm(int idx, InterpreterDataType elm) {
    this.storedVal.add(idx, elm);
  }

  public InterpreterDataType getArrElm(int idx) {
    return this.storedVal.get(idx);
  }

  public String toString() {
    return ParserHelper.listToString(this.getStoredVal());
  }

  public void fromString(String input) {
  }
}
