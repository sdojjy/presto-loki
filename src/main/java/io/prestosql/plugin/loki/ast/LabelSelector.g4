// antlr4 LabelSelector.g4 -package io.prestosql.plugin.loki.ast
grammar LabelSelector;

basequery
   : LBRACE pair (COMMA pair)* RBRACE
   ;

pair
   : KEY OP STRING
   ;


OP
   : '='
   | '!='
   | '=~'
   | '!~'
   ;

LBRACE : '{' ;
RBRACE : '}' ;

COMMA
   : ','
   ;

KEY
   : LETTER LETTER*
   ;

NUMBER
   : NUM NUM*
   ;

STRING
   : '"' (ESC | SAFECODEPOINT)* '"'
   ;

fragment NUM
   : '1'..'9'
   ;

fragment LETTER
   : 'a'..'z' | 'A'..'Z' | '_'
   ;

fragment ESC
   : '\\' (["\\/bfnrt] | UNICODE)
   ;

fragment UNICODE
   : 'u' HEX HEX HEX HEX
   ;

fragment HEX
   : [0-9a-fA-F]
   ;

fragment SAFECODEPOINT
   : ~ ["\\\u0000-\u001F]
   ;

WS
   : [ \t\n\r] + -> skip
   ;