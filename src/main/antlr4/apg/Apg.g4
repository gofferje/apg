grammar Apg;

prog: (decl | fundef | expr)+ EOF   # Program
    ;

decl: ID ':' TYPE '=' (NUM | BOOL)  # Declaration
    ;

fundef: 'fun' ID '(' paramdefs? ')' ':' TYPE '{' expr '}'  # Functiondefinition
      ;
paramdefs: ID ':' TYPE (',' ID ':' TYPE)*;

expr: MINUS expr                  # UnaryMinus
    | expr MULT expr              # Multiplication
    | expr op=(PLUS | MINUS) expr # Addition
    | expr AND expr               # And
    | expr OR expr                # Or
    | expr EQ expr                # Equals
    | IF expr THEN expr ELSE expr # If
    | ID '(' params? ')'          # FunctionCall
    | ID                          # Variable
    | NUM                         # Number
    | BOOL                        # Boolean
    | '(' expr ')'                # ParExpression
    ;

params: expr (',' expr)*;

/* tokens */
BOOL: 'false' | 'true';
IF: 'if';
THEN: 'then';
ELSE: 'else';
ID: [a-z][a-zA-Z0-9_]*;
NUM: '0' | [1-9][0-9]*;
TYPE: INT_TYPE | BOOL_TYPE;
INT_TYPE: 'INT';
BOOL_TYPE: 'BOOL';
MULT: '*';
PLUS: '+';
MINUS: '-';
AND: '&&';
OR: '||';
EQ: '==';
COMMENT: '--' ~[\r\n]* -> skip;
WS: [ \t\r\n]+ -> skip;
