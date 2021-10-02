package compiler.parser;

import java.util.ArrayList;

import compiler.parser.IASTNode;

public class ProgramNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.PROGRAM;
  private ArrayList<IASTNode> value;
  private IASTNode left;
  private IASTNode right;

  public ProgramNode(ArrayList<IASTNode> body) {
    this.value = body;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public ArrayList<IASTNode> getValue() {
    return this.value;
  }

  @Override
  public IASTNode getLeftNode() {
    return this.left;
  }

  @Override
  public IASTNode getRightNode() {
    return this.right;
  }

  public String toString() {
    return String.format(
      "{%nType: %s,%nValue: %s%n}%n",
      this.getType().name(), this.getValue()
    );
  }
}
