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

import kakkoiichris.hyper.lexer.Context
import kakkoiichris.hyper.lexer.Lexer
import kakkoiichris.hyper.lexer.Token
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

        while (!skip(Token.Type.Symbol.END_OF_FILE)) {
            stmts += stmt()
        }

        return Program(stmts)
    }

    private fun peek() = token

    private fun step() {
        token = lexer.next()
    }

    private fun match(type: Token.Type) =
        peek().type == type

    private fun matchAny(vararg types: Token.Type): Boolean {
        for (type in types) {
            if (peek().type == type) {
                return true
            }
        }

        return false
    }

    private fun skip(type: Token.Type) =
        if (peek().type == type) {
            step()
            true
        }
        else {
            false
        }

    private fun skipAny(vararg types: Token.Type): Boolean {
        for (type in types) {
            if (peek().type == type) {
                step()
                return true
            }
        }

        return false
    }

    private fun mustSkip(type: Token.Type) {
        if (!skip(type)) {
            HyperError.forParser("Expected type $type; got ${peek().type} instead!", token.context)
        }
    }

    private fun stmt() =
        Stmt.Empty(Context.none)
}