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

    private fun match(type: TokenType) =
        peek().type == type

    private fun matchAny(vararg types: TokenType): Boolean {
        for (type in types) {
            if (peek().type == type) {
                return true
            }
        }

        return false
    }

    private fun skip(type: TokenType) =
        if (peek().type == type) {
            step()
            true
        }
        else {
            false
        }

    private inline fun <reified T : TokenType> take(): T? {
        val type = peek().type

        if (type is T) {
            step()

            return type
        }

        return null
    }

    private fun skipAny(vararg types: TokenType): Boolean {
        for (type in types) {
            if (peek().type == type) {
                step()
                return true
            }
        }

        return false
    }

    private fun mustSkip(type: TokenType) {
        if (!skip(type)) {
            HyperError.forParser("Expected type $type; got ${peek().type} instead!", token.context)
        }
    }

    private fun here() =
        peek().context

    private fun stmt(): Stmt =
        when {
            matchAny(Keyword.LET, Keyword.VAR) -> declareStmt()
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
        }

    private fun blockStmt(): Stmt.Block {
        val start = here()

        val stmts = mutableListOf<Stmt>()

        mustSkip(Symbol.LEFT_BRACE)

        while (skip(Symbol.SEMICOLON)) {
            stmts += stmt()
        }

        mustSkip(Symbol.RIGHT_BRACE)

        val context = start..<here()

        return Stmt.Block(context, stmts)
    }

    private fun declareStmt(): Stmt.Declare {
        val start = here()

        val constant = skip(Keyword.LET)

        if (!constant) {
            mustSkip(Keyword.VAR)
        }

        val mutable = skip(Keyword.MUT)

        val name = nameExpr()

        var type = Expr.Type(Context.none, Inferred)

        val typed = skip(Symbol.COLON)

        if (typed) {
            type = typeExpr()
        }

        var expr: Expr = Expr.Empty(Context.none)

        val assigned = skip(Symbol.EQUAL_SIGN)

        if (!(typed || assigned)) {
            TODO()
        }

        if (assigned) {
            expr = expr()
        }

        mustSkip(Symbol.SEMICOLON)

        val context = start..<here()

        return Stmt.Declare(context, constant, mutable, name, type, expr)
    }

    private fun ifStmt(): Stmt.If {
        val start = here()

        mustSkip(Keyword.IF)

        val condition = expr()

        val yes = blockStmt()

        var no: Stmt = Stmt.Empty(Context.none)

        if (skip(Keyword.ELSE)) {
            no = if (match(Keyword.IF)) {
                ifStmt()
            }
            else {
                blockStmt()
            }
        }

        val context = start..<here()

        return Stmt.If(context, condition, yes, no)
    }

    private fun matchStmt(): Stmt.Match {
        TODO("IMPLEMENT MATCH")
    }

    private fun loopStmt(): Stmt.Loop {
        val start = here()

        mustSkip(Keyword.LOOP)

        val block = blockStmt()

        val context = start..<here()

        return Stmt.Loop(context, block)
    }

    private fun whileStmt(): Stmt.While {
        val start = here()

        mustSkip(Keyword.WHILE)

        val condition = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.While(context, condition, block)
    }

    private fun untilStmt(): Stmt.Until {
        val start = here()

        mustSkip(Keyword.UNTIL)

        val condition = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.Until(context, condition, block)
    }

    private fun forStmt(): Stmt.For {
        val start = here()

        mustSkip(Keyword.FOR)

        val name = nameExpr()

        mustSkip(Symbol.COLON)

        val iterable = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.For(context, name, iterable, block)
    }

    private fun defStmt(): Stmt.Def {
        val start = here()

        mustSkip(Keyword.DEF)

        var trait = Expr.Name.none
        var target = nameExpr()

        val `for` = skip(Keyword.FOR)

        if (`for`) {
            trait = target

            target = nameExpr()
        }

        val funs = mutableListOf<Stmt.Fun>()

        mustSkip(Symbol.LEFT_BRACE)

        while (!skip(Symbol.RIGHT_BRACE)) {
            funs += funStmt()
        }

        val context = start..<here()

        return Stmt.Def(context, `for`, trait, target, funs)
    }

    private fun funStmt(): Stmt.Fun {
        val start = here()

        mustSkip(Keyword.FUN)

        val name = nameExpr()

        val args = mutableListOf<Stmt.Arg>()

        mustSkip(Symbol.LEFT_PAREN)

        var self = false

        if (skip(Keyword.SELF)) {
            self = true

            skip(Symbol.COMMA)
        }

        do {
            if (skip(Symbol.RIGHT_PAREN)) break

            val argName = nameExpr()

            mustSkip(Symbol.COLON)

            val argType = typeExpr()

            val argContext = argName.context..<argType.context

            args += Stmt.Arg(argContext, argName, argType)
        }
        while (skip(Symbol.COMMA))

        var type = Expr.Type(Context.none, Primitive.VOID)

        if (skip(Symbol.COLON)) {
            type = typeExpr()
        }

        val startBody = peek()

        val body = when (startBody.type) {
            Symbol.SEMICOLON  -> Stmt.Empty(startBody.context)
            Symbol.ARROW      -> Stmt.Empty(startBody.context)
            Symbol.LEFT_BRACE -> blockStmt()
            else              -> TODO()
        }

        val context = start..<here()

        return Stmt.Fun(context, name, self, args, type, body)
    }

    private fun structStmt(): Stmt.Struct {
        val start = here()

        mustSkip(Keyword.STRUCT)

        val name = nameExpr()

        val args = mutableListOf<Stmt.Arg>()

        mustSkip(Symbol.LEFT_BRACE)

        do {
            if (skip(Symbol.RIGHT_BRACE)) break

            val argName = nameExpr()

            mustSkip(Symbol.COLON)

            val argType = typeExpr()

            val argContext = argName.context..<argType.context

            args += Stmt.Arg(argContext, argName, argType)
        }
        while (skip(Symbol.COMMA))

        val context = start..<here()

        return Stmt.Struct(context, name, args)
    }

    private fun enumStmt(): Stmt.Enum {
        val start = here()

        mustSkip(Keyword.ENUM)

        val name = nameExpr()

        val variants = mutableListOf<Stmt.Enum.Variant>()

        mustSkip(Symbol.LEFT_BRACE)

        do {
            if (skip(Symbol.RIGHT_BRACE)) break

            val variantName = nameExpr()

            variants += when {
                skip(Symbol.LEFT_PAREN) -> {
                    val types = mutableListOf<Expr.Type>()

                    do {
                        types += typeExpr()
                    }
                    while (skip(Symbol.COMMA))

                    mustSkip(Symbol.RIGHT_PAREN)

                    Stmt.Enum.Variant.Typed(variantName.context..<types.last().context, variantName, types)
                }

                else                    -> Stmt.Enum.Variant.Single(name.context, name)
            }
        }
        while (skip(Symbol.COMMA))

        val context = start..<here()

        return Stmt.Enum(context, name, variants)
    }

    private fun breakStmt(): Stmt {
        val start = here()

        mustSkip(Keyword.BREAK)

        val label = if (skip(Symbol.COLON)) nameExpr() else null

        val expr = if (!match(Symbol.SEMICOLON)) expr() else null

        mustSkip(Symbol.SEMICOLON)

        val context = start..<here()

        return Stmt.Break(context, label, expr)
    }

    private fun continueStmt(): Stmt.Continue {
        val start = here()

        mustSkip(Keyword.CONTINUE)

        val label = if (skip(Symbol.COLON)) nameExpr() else null

        mustSkip(Symbol.SEMICOLON)

        val context = start..<here()

        return Stmt.Continue(context, label)
    }

    private fun returnStmt(): Stmt.Return {
        val start = here()

        mustSkip(Keyword.RETURN)

        val expr = if (!match(Symbol.SEMICOLON)) expr() else null

        mustSkip(Symbol.SEMICOLON)

        val context = start..<here()

        return Stmt.Return(context, expr)
    }

    private fun expr(): Expr =
        Expr.Empty(Context.none)

    private fun nameExpr(): Expr.Name {
        val context = here()

        val identifier = take<Identifier>() ?: TODO()

        return Expr.Name(context, identifier.value)
    }

    private fun typeExpr(): Expr.Type {
        return Expr.Type(Context.none, Inferred)
    }
}