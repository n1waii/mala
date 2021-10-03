package compiler.parser;

import compiler.parser.IASTNode;

public class NumLiteralNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.NUM_LITERAL;
  private Double value;
  private IASTNode left;
  private IASTNode right;

  public NumLiteralNode(Double value) {
    this.value = value;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public Double getValue() {
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
