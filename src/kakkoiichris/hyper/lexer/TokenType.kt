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
 * Copyright (C) 2023, KakkoiiChris
 *
 * File:    TokenType.kt
 * Created: Sunday, August 20, 2023, 15:25:00
 *
 * @author Christian Bryce Alexander
 */
sealed interface Type

enum class Keyword : Type {
    LET,
    VAR,
    MUT,
    IF,
    ELSE,
    MATCH,
    LOOP,
    WHILE,
    UNTIL,
    FOR,
    DEF,
    FUN,
    STRUCT,
    ENUM,
    BREAK,
    CONTINUE,
    RETURN,
    IS,
    AS
}

enum class DataType : Type {
    NONE,
    BOOL,
    U8,
    U16,
    U32,
    U64,
    I8,
    I16,
    I32,
    I64,
    F32,
    F64,
    CHAR,
    STR,
    ANY
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
    SEMICOLON(";"),
    END_OF_FILE("0");
}

data class Value(val value: Any) : Type

data class Identifier(val value: String) : Type

data class LeftTemplate(val value: String) : Type

data class MiddleTemplate(val value: String) : Type

data class RightTemplate(val value: String) : Type