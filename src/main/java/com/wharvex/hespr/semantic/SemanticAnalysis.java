package com.wharvex.hespr.semantic;

import com.wharvex.hespr.parser.nodes.AssignmentNode;
import com.wharvex.hespr.parser.nodes.BooleanNode;
import com.wharvex.hespr.parser.nodes.ForNode;
import com.wharvex.hespr.parser.nodes.FunctionNode;
import com.wharvex.hespr.parser.nodes.WhenNode;
import com.wharvex.hespr.parser.nodes.IntegerNode;
import com.wharvex.hespr.parser.nodes.MathOpNode;
import com.wharvex.hespr.parser.nodes.Node;
import com.wharvex.hespr.parser.nodes.ProgramNode;
import com.wharvex.hespr.parser.nodes.RealNode;
import com.wharvex.hespr.parser.nodes.RepeatNode;
import com.wharvex.hespr.parser.nodes.StatementNode;
import com.wharvex.hespr.parser.nodes.StringNode;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.nodes.VariableReferenceNode;
import com.wharvex.hespr.parser.VariableType;
import com.wharvex.hespr.parser.nodes.WhileNode;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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
      } else if (statement instanceof WhenNode) {
        this.analyzeBlock(varTypes, ((WhenNode) statement).getStatements(), funcName);
        Optional<WhenNode> possibleNextWhen = ((WhenNode) statement).getNextWhen();
        while (possibleNextWhen.isPresent()) {
          this.analyzeBlock(varTypes, possibleNextWhen.get().getStatements(), funcName);
          possibleNextWhen = possibleNextWhen.get().getNextWhen();
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
