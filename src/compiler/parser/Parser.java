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

  private static HashMap<LexemeToken, Boolean> expression_operators = new HashMap<LexemeToken, Boolean>();
  private static HashMap<LexemeToken, Integer> op_precedences = new HashMap<LexemeToken, Integer>();
  private static HashMap<LexemeToken, OperatorAssociativity> op_associativity = new HashMap<LexemeToken, OperatorAssociativity>();

  public Parser(Lexer lexer) {
      this.lexer = lexer;
      setup_expression_operators();
      setup_op_precedences();
      setup_op_associativities();
  }

  private static void throwError(Lexeme lexeme, String errorMessage) {
    throw new Error("FileName:" + lexeme.getLine() + ":" + lexeme.getColumn() + "error: " + errorMessage);
  }

  private Lexeme expectNextToken(LexemeToken token) {
    Lexeme lexeme = this.lexer.getNextLexeme();
    if (lexeme.getToken() != token) {
      throwError(lexeme, String.format("Expected token of type %s, instead got %s", token.toString(), lexeme.getToken().toString()));
    }
    return lexeme;
  }

  private Lexeme expectCurrentToken(LexemeToken token) {
    Lexeme lexeme = this.lexer.getCurrentLexeme();
    if (lexeme.getToken() != token) {
      throwError(lexeme, String.format("Expected token of type %s, instead got %s", token.toString(), lexeme.getToken().toString()));
    }
    return lexeme;
  }

  private Lexeme expectLastToken(LexemeToken token) {
    Lexeme lexeme = this.lexer.getLastLexeme();
    if (lexeme.getToken() != token) {
      throwError(lexeme, String.format("Expected token of type %s, instead got %s", token.toString(), lexeme.getToken().toString()));
    }
    return lexeme;
  }


  private IASTNode parse_expression() {
    // this is going to follow the shunting yard algorithm

    Stack<Lexeme> operators = new Stack<Lexeme>();
    Stack<IASTNode> operands = new Stack<IASTNode>();

    // while there is a lexeme to read and the lexeme is a valid expression operator
    // used to differentiate between expressions assigned to variables and function arguments
    while (this.lexer.getNextLexeme() != null && expression_operators.containsKey(this.lexer.getCurrentLexeme().getToken())) {
      Lexeme lexeme = this.lexer.getCurrentLexeme();
      switch (lexeme.getToken()) {
        case NUMBER:
          operands.push(new NumLiteralNode((Double) lexeme.getValue()));
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
      // note: check for mismatched parenthesis(if its a function call, there should be one extra parenthesis)
      /*
      Lexeme operator = operators.pop();
      if (operators.empty()) {
        // if its empty, 'operator' is the last operator and if its a function call, we expect a right parenthesis
        if (operator.getToken() == LexemeToken.RIGHT_PAREN) {

        }
      }
      */
      operands.push(new BinExpNode((String) operators.pop().getValue(), operands.pop(), operands.pop()));
    }

    // note: check for remaining operands
    return operands.pop();
  }

  private IASTNode parse_variable_definition() {
    //Lexeme keyword = this.lexer.getCurrentLexeme();
    Lexeme varName = this.expectNextToken(LexemeToken.IDENTIFIER);
    this.expectNextToken(LexemeToken.ASSIGNMENT_OP);
    IASTNode varValue = this.parse_expression();
    return new LocalVarDefNode((String) varName.getValue(), varValue);
  }

  private IASTNode parse_function_call() {
    // f(1+2, 3, 5, 2+5)
    // let x = 1 + 2 * 3

    Lexeme functionName = this.lexer.getLastLexeme();
    Lexeme nextLexeme = this.lexer.getNextLexeme();
    if (nextLexeme.getToken() == LexemeToken.RIGHT_PAREN) {
      // function call with no argument
      return new FunctionCallNode((String) functionName.getValue(), null);
    }

    // function calls with arguments
    ArrayList<IASTNode> arguments = new ArrayList<IASTNode>();
    while (this.lexer.getCurrentLexeme() != null && !expression_operators.containsKey(this.lexer.getCurrentLexeme().getToken())) {
      arguments.add(this.parse_expression());
      Lexeme currentLexeme = this.lexer.getCurrentLexeme();
      if (currentLexeme != null) {
        if (!expression_operators.containsKey(currentLexeme.getToken())) { // end of function call
          return new FunctionCallNode((String) functionName.getValue(), arguments);
        }
      }
      this.expectCurrentToken(LexemeToken.COMMA);
    }
  }

  public IASTNode parse() {
      ArrayList<IASTNode> body = new ArrayList<IASTNode>();

      this.lexer.getNextLexeme();
      while (!this.lexer.isExhausted()) {
        Lexeme lexeme = this.lexer.getCurrentLexeme();
        switch (lexeme.getToken()) {
          case KEYWORD_LET:
            body.add(this.parse_variable_definition());
            this.lexer.getNextLexeme();
            break;
          case IDENTIFIER:
            Lexeme next = this.lexer.getNextLexeme();
            if (next != null) {
              if (next.getToken() == LexemeToken.LEFT_PAREN) {
                body.add(this.parse_function_call());
                this.lexer.getNextLexeme();
              } else if (next.getToken() == LexemeToken.ASSIGNMENT_OP) {
                //body.add(this.parse_variable_assignment())
              } else {
                throwError(next, "Unexpected token");
              }
            } else {
              throwError(lexeme, "Unexpected token");
            }
            break;
          default: break;
        }
      }

      return new ProgramNode(body);
  }

  private static void setup_expression_operators() {
    expression_operators.put(LexemeToken.BINARY_MUL, true);
    expression_operators.put(LexemeToken.BINARY_DIV, true);
    expression_operators.put(LexemeToken.BINARY_ADD, true);
    expression_operators.put(LexemeToken.BINARY_SUB, true);
    expression_operators.put(LexemeToken.LEFT_PAREN, true);
    expression_operators.put(LexemeToken.RIGHT_PAREN, true);
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
