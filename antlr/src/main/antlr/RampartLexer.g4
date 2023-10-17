lexer grammar RampartLexer;

@header {
	package org.rampart.lang.grammar;
}
options {
    language=Java;
    TokenLabelType=CommonToken;
}
/* Keywords */
APP             : 'app';
CODE            : 'code' -> pushMode(SOURCE_BLOCK);
ENDAPP          : 'endapp';

/* Punctuation */
QUOTED_STRING : '"' (ESC|.)*? '"' ;
IDENTIFIER : INITIAL (INITIAL | DASH | SLASH | DOT | DIGIT)*;
INITIAL : LETTER | UNDERSCORE;
LEFT_BRACE : '{' ;
RIGHT_BRACE : '}' ;
LEFT_SQUARE : '[' ;
RIGHT_SQUARE : ']' ;
UNDERSCORE : '_';
COLON : ':';
COMMA : ',';
SINGLEQUOTE : '\'';
DOT : '.';
INTEGER : [0-9]+;
FLOAT : '-'* [0-9]+ DOT [0-9]+;
BOOLEAN : ('false' | 'true');

LEFT_PAREN : '('    -> pushMode(PARAMETER_LIST);
fragment SLASH : '/';
fragment DASH : '-';
fragment ESC :  '\\"' | '\\\\' ;
fragment LEFT_BLOCK_COMMENT : '/*';
fragment RIGHT_BLOCK_COMMENT : '*/';
fragment HEX : [0-9a-fA-F ];
fragment DIGIT : [0-9];
fragment LETTER : [a-zA-Z];

/* Whitespace & Comments*/
WS : [ \r\t\n]+ -> skip ;
LINE_COMMENT : ('#' | '//') ~[\r\n]* '\r'? '\n' -> skip ;
BLOCK_COMMENT : LEFT_BLOCK_COMMENT .*? RIGHT_BLOCK_COMMENT -> skip ;

/* Lexical Modes */
mode SOURCE_BLOCK;
    LEFT_PAREN3 : '('    -> type(LEFT_PAREN), pushMode(PARAMETER_LIST);
    COLON3 : ':' -> type(COLON), popMode, pushMode(SOURCE_CODE) ;
	WS3 : [ \r\t\n]+ -> skip ;
	LINE_COMMENT3 : ('#' | '//') ~[\r\n]* '\r'? '\n' -> skip ;
    BLOCK_COMMENT3 : LEFT_BLOCK_COMMENT .*? RIGHT_BLOCK_COMMENT -> skip ;

mode PARAMETER_LIST;
    LEFT_PAREN2 : '(' -> type(LEFT_PAREN) ;
    RIGHT_PAREN : ')' -> popMode ;
    QUOTED_STRING2 : '"' (ESC|.)*? '"' -> type(QUOTED_STRING) ;
    INTEGER2 : [0-9]+ -> type(INTEGER) ;
    FLOAT2 : '-'* [0-9]+ DOT [0-9]+ -> type(FLOAT) ;
    BOOLEAN2 : ('false' | 'true') -> type(BOOLEAN) ;
    IDENTIFIER2 : INITIAL (INITIAL | DASH | SLASH | DOT | DIGIT)* -> type(IDENTIFIER) ;
    LEFT_BRACE2 : '{' -> type(LEFT_BRACE) ;
    RIGHT_BRACE2 : '}' -> type(RIGHT_BRACE) ;
    LEFT_SQUARE2 : '[' -> type(LEFT_SQUARE) ;
    RIGHT_SQUARE2 : ']' -> type(RIGHT_SQUARE) ;
    UNDERSCORE2 : '_' -> type(UNDERSCORE);
    COLON2 : ':' -> type(COLON) ;
    COMMA2 : ',' -> type(COMMA) ;
    SINGLEQUOTE2 : '\'' -> type(SINGLEQUOTE) ;
    DOT2 : '.' -> type(DOT) ;
    WS2 : [ \r\t\n]+ -> skip ;
    LINE_COMMENT2 : ('#' | '//') ~[\r\n]* '\r'? '\n' -> skip ;
    BLOCK_COMMENT2 : LEFT_BLOCK_COMMENT .*? RIGHT_BLOCK_COMMENT -> skip ;

mode SOURCE_CODE;
    ENDCODE : 'endcode' -> popMode ;
    SOURCE : .+? ;
