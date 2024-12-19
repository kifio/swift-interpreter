/**
    program : compound_statement
    compound_statement : statement_list
    statement_list : statement | statement (SEMI | NEW_LINE) statement_list
    statement : compound_statement | assignment_statement | empty
    assignment_statement : ((LET | VAR) | empty) variable (COLON (INT | DOUBLE) | empty) ASSIGN expr
    empty :
    expr: term ((PLUS | MINUS) term)*
    term: factor ((MUL | DIV) factor)*
    factor : PLUS factor
           | MINUS factor
           | INTEGER
           | LPAREN expr RPAREN
           | variable
    variable: ID
**/

// Пример кода
let number: Int = -4
var a = -6
var b: Int
var c: Int = a + number
var x: Int
let y: Double

// number = 2 + a
b = 10 * a + 10 * number / 4
c = a - -b

func someFunc() {
    let asdf = 100
}

// Полиморфизма нет
func someFunc2(with: Int, args: Double) {
    let and = 0
    var body = 1 / args
    let y = and + body + with
    someFunc2(with: and, args: body)
}

someFunc()
someFunc2(with: 10, args: 0.5)
someFunc2(with: 10, args: 2.5)


x = 11
y = 20 / 7 + 3.14

// Token[type=ID, value=number] = -4.0
// Token[type=ID, value=a] = -4.0
// Token[type=ID, value=c] = -8.0
// Token[type=ID, value=b] = -50.0
// Token[type=ID, value=c] = -54.0
// Token[type=ID, value=x] = 11.0
// Token[type=ID, value=y] = 5.997142857142857
// 0.0
