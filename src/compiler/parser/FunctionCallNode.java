package compiler.parser;

import java.util.ArrayList;
import compiler.parser.IASTNode;

public class FunctionCallNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.FUNC_CALL;
  private String name;
  private ArrayList<IASTNode> arguments;
  private IASTNode left;
  private IASTNode right;

  public FunctionCallNode(String funcName, ArrayList<IASTNode> args) {
    this.name = funcName;
    this.arguments = args;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public ArrayList<IASTNode> getValue() {
    return this.arguments;
  }

  @Override
  public IASTNode getLeftNode() {
    return this.left;
  }

  @Override
  public IASTNode getRightNode() {
    return this.right;
  }

  public ArrayList<IASTNode> getArguments() {
    return this.getValue();
  }

  public String getFunctionName() {
    return this.name;
  }

  public String toString() {
    return String.format(
      "{%nType: %s,%nValue: %s%n}%n",
      this.getType().name(), this.getValue()
    );
  }
}
