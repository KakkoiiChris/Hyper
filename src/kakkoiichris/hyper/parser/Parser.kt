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
package kakkoiichris.hyper.parser

import kakkoiichris.hyper.lexer.*
import kakkoiichris.hyper.util.HyperError

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Parser.kt
 * Created: Sunday, November 27, 2022, 21:53:27
 *
 * @author Christian Bryce Alexander
 */
class Parser(private val lexer: Lexer) {
    private var token = lexer.next()

    fun parse(): Program {
        val stmts = mutableListOf<Stmt>()

        while (!skip(Symbol.END_OF_FILE)) {
            stmts += stmt()
        }

        return Program(stmts)
    }

    private fun peek() =
        token

    private fun step() {
        token = lexer.next()
    }

    private fun match(type: Type) =
        peek().type == type

    private fun matchAny(vararg types: Type): Boolean {
        for (type in types) {
            if (peek().type == type) {
                return true
            }
        }

        return false
    }

    private fun skip(type: Type) =
        if (peek().type == type) {
            step()
            true
        }
        else {
            false
        }

    private fun skipAny(vararg types: Type): Boolean {
        for (type in types) {
            if (peek().type == type) {
                step()
                return true
            }
        }

        return false
    }

    private fun mustSkip(type: Type) {
        if (!skip(type)) {
            HyperError.forParser("Expected type $type; got ${peek().type} instead!", token.context)
        }
    }

    private fun here() =
        peek().context

    private fun stmt(): Stmt =
        Stmt.Empty(Context.none)
        /*when {
            matchAny(Keyword.LET, Keyword.VAR) -> declStmt()
            match(Keyword.IF)                  -> ifStmt()
            match(Keyword.MATCH)               -> matchStmt()
            match(Keyword.LOOP)                -> loopStmt()
            match(Keyword.WHILE)               -> whileStmt()
            match(Keyword.UNTIL)               -> untilStmt()
            match(Keyword.FOR)                 -> forStmt()
            match(Keyword.DEF)                 -> defStmt()
            match(Keyword.FUN)                 -> funStmt()
            match(Keyword.STRUCT)              -> structStmt()
            match(Keyword.ENUM)                -> enumStmt()
            match(Keyword.BREAK)               -> breakStmt()
            match(Keyword.CONTINUE)            -> continueStmt()
            match(Keyword.RETURN)              -> returnStmt()
            else                               -> TODO()
        }*/
}