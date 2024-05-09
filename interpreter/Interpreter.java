package interpreter;
/*
 * program : compound_statement
 * compound_statement : statement_list
 * statement_list : statement | statement SEMI statement_list | statement statement_list
 * statement : compound_statement | assignment_statement | empty
 * assignment_statement : (LET | VAR) variable ASSIGN expr
 * empty :
 * expr: term ((PLUS | MINUS) term)*
 * term: factor ((MUL | DIV) factor)*
 * factor : PLUS factor
 *        | MINUS factor
 *        | INTEGER
 *        | LPAREN expr RPAREN
 *        | variable
 * variable: ID
 */