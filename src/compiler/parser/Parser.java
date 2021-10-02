// implements shunting yard algorithm

package compiler.parser;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;

import compiler.Lexer;
import compiler.Lexeme;
import compiler.LexemeToken;

public class Parser {
  enum OperatorAssociativity {
    RIGHT, LEFT
  }

  private Lexer lexer;
  private static HashMap<LexemeToken, Integer> op_precedences = new HashMap<LexemeToken, Integer>();
  private static HashMap<LexemeToken, OperatorAssociativity> op_associativity = new HashMap<LexemeToken, OperatorAssociativity>();

  public Parser(Lexer lexer) {
      this.lexer = lexer;
      setup_op_precedences();
      setup_op_associativities();
  }

  private static void throwError(Lexeme lexeme, String errorMessage) {
    throw new Error("FileName:" + lexeme.getLine() + ":" + lexeme.getColumn() + "error: " + errorMessage);
  }

  private Lexeme expectToken(LexemeToken token) {
    Lexeme lexeme = this.lexer.getNextLexeme();
    if (lexeme.getToken() != token) {
      throwError(lexeme, String.format("Expected token of type %s, instead got %s", token.toString(), lexeme.getToken().toString()));
    }
    return lexeme;
  }

  IASTNode parseExpression() {
    // this is going to follow the shunting yard algorithm
    // however, it will return one AST (PROGRAM) node that can be traversed

    Stack<Lexeme> operators = new Stack<Lexeme>();
    Stack<IASTNode> operands = new Stack<IASTNode>();

    while (!this.lexer.isExhausted()) {
      Lexeme lexeme = this.lexer.getNextLexeme();
      switch (lexeme.getToken()) {
        case NUMBER:
          operands.push(new NumLiteralNode((Integer) lexeme.getValue()));
          break;
        default:
          Lexeme op1 = lexeme;
          Integer op1Precedence = op_precedences.get(op1.getToken());
          if (op1Precedence != null) {
            while (!operators.empty() && (op_precedences.containsKey(operators.peek().getToken()))) {
              Lexeme op2 = operators.peek();
              Integer op2Precedence = op_precedences.get(op2.getToken());
              if ((op_associativity.get(op1.getToken()) == OperatorAssociativity.LEFT && op1Precedence == op2Precedence) || op2Precedence > op1Precedence) {
                operators.pop();
                operands.push(new BinExpNode((String) op2.getValue(), operands.pop(), operands.pop()));
              } else {
                break;
              }
            }
            operators.push(op1);
          }
          break;
      }
    }

    while (!operators.empty()) {
      // note: check for mismatched parenthesis
      operands.push(new BinExpNode((String) operators.pop().getValue(), operands.pop(), operands.pop()));
    }

    // note: check for remaining operands
    return operands.pop();
  }

  public IASTNode Parse() {
      ArrayList<IASTNode> body = new ArrayList<IASTNode>();

      while (!this.lexer.isExhausted()) {
        Lexeme lexeme = this.lexer.getNextLexeme();
        switch (lexeme.getToken()) {
          case KEYWORD_LET:
            Lexeme varName = this.expectToken(LexemeToken.IDENTIFIER);
            this.expectToken(LexemeToken.ASSIGNMENT_OP);
            IASTNode varValue = this.parseExpression();
            body.add(new LocalVarDefNode((String) varName.getValue(), varValue));
            break;
        }
      }

      return new ProgramNode(body);
  }

  private static void setup_op_precedences() {
    op_precedences.put(LexemeToken.BINARY_MUL, 3);
    op_precedences.put(LexemeToken.BINARY_DIV, 3);
    op_precedences.put(LexemeToken.BINARY_ADD, 2);
    op_precedences.put(LexemeToken.BINARY_SUB, 2);
  }

  private static void setup_op_associativities() {
    op_associativity.put(LexemeToken.BINARY_MUL, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_DIV, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_ADD, OperatorAssociativity.LEFT);
    op_associativity.put(LexemeToken.BINARY_SUB, OperatorAssociativity.LEFT);
  }
}
