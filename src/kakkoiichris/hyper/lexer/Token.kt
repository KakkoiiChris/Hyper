/**********************************************
 * ______  __                                 *
 * ___  / / /_____  __________ _____ ________ *
 * __  /_/ / __  / / /___  __ \_  _ \__  ___/ *
 * _  __  /  _  /_/ / __  /_/ //  __/_  /     *
 * /_/ /_/   _\__, /  _  .___/ \___/ /_/      *
 *           /____/   /_/                     *
 *                                            *
 *            SCRIPTING LANGUAGE              *
 *  Copyright (C) 2018, Christian Alexander   *
 **********************************************/
package kakkoiichris.hyper.lexer

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Token.kt
 * Created: Saturday, October 22, 2022, 12:25:38
 *
 * @author Christian Bryce Alexander
 */
data class Token(val location: Location, val type: Type) {
    sealed interface Type {
        enum class Keyword : Type {
            LET,
            VAR,
            DO,
            IF,
            ELSE,
            MATCH,
            LOOP,
            WHILE,
            UNTIL,
            FOR,
            DEF,
            STRUCT,
            ENUM,
            BREAK,
            CONTINUE,
            RETURN,
            NONE,
            BOOL,
            BYTE,
            SHORT,
            INT,
            LONG,
            FLOAT,
            DOUBLE,
            CHAR,
            STRING,
            ANY,
            IS,
            AS
        }
        
        enum class Symbol(val value: String) : Type {
            EQUAL_SIGN("="),
            PLUS_EQUAL("+="),
            DASH_EQUAL("-="),
            STAR_EQUAL("*="),
            SLASH_EQUAL("/="),
            PERCENT_EQUAL("%="),
            AMPERSAND_EQUAL("&="),
            CARET_EQUAL("^="),
            PIPE_EQUAL("|="),
            DOUBLE_LESS_EQUAL("<<="),
            DOUBLE_GREATER_EQUAL(">>="),
            TRIPLE_GREATER_EQUAL(">>>="),
            DOUBLE_PIPE("||"),
            DOUBLE_AMPERSAND("&&"),
            PIPE("|"),
            CARET("^"),
            AMPERSAND("&"),
            DOUBLE_EQUAL("=="),
            EXCLAMATION_EQUAL("!="),
            LESS_SIGN("<"),
            LESS_EQUAL_SIGN("<="),
            GREATER_SIGN(">"),
            GREATER_EQUAL_SIGN(">="),
            EXCLAMATION_IN("!in"),
            EXCLAMATION_IS("!is"),
            DOUBLE_DOT(".."),
            TRIPLE_DOT("..."),
            DOUBLE_LESS("<<"),
            DOUBLE_GREATER(">>"),
            TRIPLE_GREATER(">>>"),
            PLUS("+"),
            DASH("-"),
            STAR("*"),
            SLASH("/"),
            PERCENT("%"),
            EXCLAMATION("!"),
            TILDE("~"),
            POUND("#"),
            DOUBLE_PLUS("++"),
            DOUBLE_DASH("--"),
            DOT("."),
            COLON(":"),
            DOUBLE_COLON("::"),
            AT("@"),
            ARROW("->"),
            LEFT_PAREN("("),
            RIGHT_PAREN(")"),
            LEFT_SQUARE("["),
            RIGHT_SQUARE("]"),
            LEFT_BRACE("{"),
            RIGHT_BRACE("}"),
            COMMA(","),
            END_OF_LINE(";"),
            END_OF_FILE("0");
        }
        
        data class Value(val value: Any) : Type
        
        data class Identifier(val value: String) : Type
        
        data class LeftTemplate(val value: String) : Type
        
        data class MiddleTemplate(val value: String) : Type
        
        data class RightTemplate(val value: String) : Type
    }
}