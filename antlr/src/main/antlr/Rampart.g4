grammar Rampart;

@header {
	package org.rampart.lang.grammar;
}

options { tokenVocab=RampartLexer; }

apps : app+ ;

app
    :   APP parameterListDeclaration COLON
            appDeclaration+
            ruleDeclaration+
        ENDAPP ;

parameterListDeclaration
    :   LEFT_PAREN parameterList? RIGHT_PAREN;

parameterList
    :   parameter (COMMA parameter)*;

parameter
    :   dictionary
    |   list
    |   dictionaryNVP
    |   value;

appDeclaration
    :   IDENTIFIER parameterListDeclaration;

ruleDeclaration
    : startName=IDENTIFIER parameterListDeclaration COLON
        nestedRuleDeclaration* // function("some/func/decl/")
        (sourceCodeDeclaration | ) // optional source code declaration
        nestedRuleDeclaration*
      endName=IDENTIFIER
      {
        if(!$endName.text.startsWith("end")) {
            notifyErrorListeners("closing rule tag must begin with 'end'");
        }
      }

      {
        if(!$startName.text.equalsIgnoreCase($endName.text.substring(3))) {
            notifyErrorListeners("closing rule tag does not match opening rule tag '" + $startName.text + "'");
        }
      };

nestedRuleDeclaration
    :
        IDENTIFIER parameterListDeclaration;

expression
    :   dictionary
    |   list
    |   value;

list
    :   LEFT_SQUARE expressionList? RIGHT_SQUARE;

expressionList
    :   expression (COMMA expression)*;

dictionaryNVP
    :   nvpName COLON nvpValue;

nvpName
    : IDENTIFIER;

nvpValue
    : expression;

dictionary
    :   LEFT_BRACE dictionaryNVPList RIGHT_BRACE;

dictionaryNVPList
    :   dictionaryNVP (COMMA dictionaryNVP)*;

sourceCodeDeclaration
   :    CODE parameterListDeclaration COLON
            sourceCode
        ENDCODE;

value
    : QUOTED_STRING
    | INTEGER
    | FLOAT
    | BOOLEAN
    | constant;

sourceCode
    :   ~(ENDCODE)*;

constant
    : IDENTIFIER;
