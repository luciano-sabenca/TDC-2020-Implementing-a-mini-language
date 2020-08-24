grammar TDCLang;

expression
    : FALSE
    | TRUE
    | '(' TRUE ')'
    | '(' FALSE ')'
    | '(' logicalExpression ')'
    ;

compareExpression
    : ID EQ String # compareEqualsString
    | ID EQ INT # compareEqualsNumber
    | ID NumberComparators INT # compareNumber
    | expression # toExpression
    ;

logicalAndComparison
    : compareExpression
    | logicalAndComparison 'AND' compareExpression
    ;

logicalORComparison
    : logicalAndComparison
    | logicalORComparison 'OR' logicalAndComparison
    ;

logicalExpression
    : logicalORComparison
   ;

whenLogicalCondition
    : logicalExpression '->' String
    ;

whenExpression
    : 'WHEN' '{' ( whenLogicalCondition ';')+ 'else' '->' String '}' # whenLogical
    ;

generalExpression
    : whenExpression EOF
    | logicalExpression EOF
    ;


FALSE : 'false';
TRUE : 'true' ;
ELSE : 'else' ;
COMMA : ';' ;
ARROW : '->' ;
WHEN : 'WHEN';
AND : 'AND';
OR : 'OR';
INT    : [0-9]+;
NL     : '\n';
WS     : [ \t\r]+ -> skip;
ID     : [a-zA-Z_][a-zA-Z_0-9]*;
QUOTE : '"' ;
String : '"' [a-zA-Z_0-9\-.|+#$@!&()\\[\] ]* '"';

NumberComparators
    : GT
    | LT
    | LTE
    | GTE
    ;

GT       : '>';
GTE      : '>=';
LT       : '<';
LTE      : '<=';
MINUS    : '-';
LPAR     : '(';
RPAR     : ')';
LBRACE   : '{' ;
RBRACE   : '}' ;
EQ       : '==';