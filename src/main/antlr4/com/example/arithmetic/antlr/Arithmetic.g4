grammar Arithmetic;

// Parser Rules

prog:   expr EOF ;

 

expr:   expr op=('multiply'|'divide') expr      # MulDiv

    |   expr op=('plus'|'subtract') expr        # AddSub

    |   NUMBER                                   # Number

    |   '(' expr ')'                             # Parens

    ;

 

// Lexer Rules

NUMBER  : [0-9]+ ('.' [0-9]+)? ;    // Integer or decimal numbers

 

WS      : [ \t\r\n]+ -> skip ;       // Skip whitespace