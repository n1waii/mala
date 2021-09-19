package parser;

interface IASTNode {
  String getType();
  IASTNode getRightNode();
  IASTNode getLeftNode();
}

interface IASTNodeOp extends IASTNode {
  String getOperator();
}

interface IASTNodeValue extends IASTNode {
  Object getValue();
}
