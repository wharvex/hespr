package com.wharvex.shank.semantic;

import com.wharvex.shank.parser.nodes.AssignmentNode;
import com.wharvex.shank.parser.nodes.BooleanNode;
import com.wharvex.shank.parser.nodes.ForNode;
import com.wharvex.shank.parser.nodes.FunctionNode;
import com.wharvex.shank.parser.nodes.IfNode;
import com.wharvex.shank.parser.nodes.IntegerNode;
import com.wharvex.shank.parser.nodes.MathOpNode;
import com.wharvex.shank.parser.nodes.Node;
import com.wharvex.shank.parser.nodes.ProgramNode;
import com.wharvex.shank.parser.nodes.RealNode;
import com.wharvex.shank.parser.nodes.RepeatNode;
import com.wharvex.shank.parser.nodes.StatementNode;
import com.wharvex.shank.parser.nodes.StringNode;
import com.wharvex.shank.parser.nodes.VariableNode;
import com.wharvex.shank.parser.nodes.VariableReferenceNode;
import com.wharvex.shank.parser.VariableType;
import com.wharvex.shank.parser.nodes.WhileNode;
import java.util.HashMap;
import java.util.List;

public class SemanticAnalysis {

  // todo: incorporate range checks on variables

  private ProgramNode program;

  public SemanticAnalysis(ProgramNode program) {
    this.program = program;
  }

  public ProgramNode getProgram() {
    return program;
  }

  public void checkAssignments() throws Exception {
    for (FunctionNode functionNode : this.getProgram().getFunctions().values()) {
      var varTypes = new HashMap<String, VariableType>();
      if (functionNode.getParams() != null) {
        for (VariableNode variableNode : functionNode.getParams()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      if (functionNode.getVariables() != null) {
        for (VariableNode variableNode : functionNode.getVariables()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      if (functionNode.getConstants() != null) {
        for (VariableNode variableNode : functionNode.getConstants()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      this.analyzeBlock(varTypes, functionNode.getStatements(), functionNode.getName());
    }
  }

  private void analyzeBlock(HashMap<String, VariableType> varTypes,
      List<StatementNode> statements, String funcName)
      throws Exception {
    if (statements == null) {
      return;
    }
    for (StatementNode statement : statements) {
      if (statement instanceof AssignmentNode) {
        String leftSideName = ((AssignmentNode) statement).getLeftSide().getName();
        VariableType leftSideType = varTypes.get(leftSideName);
        Node rightSide = ((AssignmentNode) statement).getRightSide();
        VariableType rightSideType = this.expression(rightSide, varTypes);
        if (rightSideType != leftSideType && rightSideType != VariableType.ANY
            && leftSideType != VariableType.ANY) {
          throw new Exception(
              "\nSEMANTIC ANALYSIS ERROR\nWhen assigning to variable <" + leftSideName
                  + "> in function <"
                  + funcName
                  + ">\nRight side of assignment must be of type " + leftSideType + " but it is "
                  + rightSideType);
        }
      } else if (statement instanceof ForNode) {
        this.analyzeBlock(varTypes, ((ForNode) statement).getStatements(), funcName);
      } else if (statement instanceof WhileNode) {
        this.analyzeBlock(varTypes, ((WhileNode) statement).getStatements(), funcName);
      } else if (statement instanceof RepeatNode) {
        this.analyzeBlock(varTypes, ((RepeatNode) statement).getStatements(), funcName);
      } else if (statement instanceof IfNode) {
        this.analyzeBlock(varTypes, ((IfNode) statement).getStatements(), funcName);
        IfNode nextIf = ((IfNode) statement).getNextIf();
        while (nextIf != null) {
          this.analyzeBlock(varTypes, nextIf.getStatements(), funcName);
          nextIf = nextIf.getNextIf();
        }
      }
    }
  }

  private VariableType expression(Node node,
      HashMap<String, VariableType> varTypes) throws Exception {
    if (!(node instanceof MathOpNode)) {
      if (node instanceof VariableReferenceNode) {
        return varTypes.get(((VariableReferenceNode) node).getName());
      } else if (node instanceof IntegerNode) {
        return VariableType.INTEGER;
      } else if (node instanceof RealNode) {
        return VariableType.REAL;
      } else if (node instanceof StringNode) {
        return VariableType.STRING;
      } else if (node instanceof BooleanNode) {
        return VariableType.BOOLEAN;
      }
    } else {
      VariableType mathOpLeftSideType = this.expression(
          ((MathOpNode) node).getLeftSide(), varTypes);
      VariableType mathOpRightSideType = this.expression(
          ((MathOpNode) node).getRightSide(), varTypes);
      if (mathOpLeftSideType == VariableType.STRING
          || mathOpRightSideType == VariableType.STRING) {
        return VariableType.STRING;
      } else {
        return mathOpRightSideType;
      }
    }
    return VariableType.ANY;
  }
}
