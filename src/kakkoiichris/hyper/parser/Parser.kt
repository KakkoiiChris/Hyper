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
import kakkoiichris.hyper.lexer.Keyword.*
import kakkoiichris.hyper.lexer.Symbol.*
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

        while (!skip(END_OF_FILE)) {
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

    private inline fun <reified T : TokenType> match(): Boolean {
        val type = peek().type

        if (type is T) {
            step()

            return true
        }

        return false
    }

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

    private inline fun <reified T : TokenType> skip(): Boolean {
        val type = peek().type

        if (type is T) {
            step()

            return true
        }

        return false
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

    private inline fun <reified T : TokenType> mustSkip() {
        if (!skip<T>()) {
            HyperError.forParser("Expected type ${T::class.simpleName}; got ${peek().type} instead!", token.context)
        }
    }

    private fun here() =
        peek().context

    private fun stmt(): Stmt =
        when {
            matchAny(LET, VAR) -> declareStmt()
            match(IF)          -> ifStmt()
            match(MATCH)       -> matchStmt()
            match(LOOP)        -> loopStmt()
            match(WHILE)       -> whileStmt()
            match(UNTIL)       -> untilStmt()
            match(FOR)         -> forStmt()
            match(DEF)         -> defStmt()
            match(FUN)         -> funStmt()
            match(STRUCT)      -> structStmt()
            match(ENUM)        -> enumStmt()
            match(BREAK)       -> breakStmt()
            match(CONTINUE)    -> continueStmt()
            match(RETURN)      -> returnStmt()
            else               -> expressionStmt()
        }

    private fun blockStmt(): Stmt.Block {
        val start = here()

        val stmts = mutableListOf<Stmt>()

        mustSkip(LEFT_BRACE)

        while (skip(SEMICOLON)) {
            stmts += stmt()
        }

        mustSkip(RIGHT_BRACE)

        val context = start..<here()

        return Stmt.Block(context, stmts)
    }

    private fun declareStmt(): Stmt.Declare {
        val start = here()

        val constant = skip(LET)

        if (!constant) {
            mustSkip(VAR)
        }

        val mutable = skip(MUT)

        val name = nameExpr()

        var type = Expr.Type(Context.none, DataType.Inferred)

        val typed = skip(COLON)

        if (typed) {
            type = typeExpr()
        }

        var expr: Expr = Expr.Empty(Context.none)

        val assigned = skip(EQUAL_SIGN)

        if (!(typed || assigned)) {
            TODO("MUST TYPE OR ASSIGN")
        }

        if (assigned) {
            expr = expr()
        }

        mustSkip(SEMICOLON)

        val context = start..<here()

        return Stmt.Declare(context, constant, mutable, name, type, expr)
    }

    private fun ifStmt(): Stmt.If {
        val start = here()

        mustSkip(IF)

        val condition = expr()

        val yes = blockStmt()

        var no: Stmt = Stmt.Empty(Context.none)

        if (skip(ELSE)) {
            no = if (match(IF)) {
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

        mustSkip(LOOP)

        val block = blockStmt()

        val context = start..<here()

        return Stmt.Loop(context, block)
    }

    private fun whileStmt(): Stmt.While {
        val start = here()

        mustSkip(WHILE)

        val condition = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.While(context, condition, block)
    }

    private fun untilStmt(): Stmt.Until {
        val start = here()

        mustSkip(UNTIL)

        val condition = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.Until(context, condition, block)
    }

    private fun forStmt(): Stmt.For {
        val start = here()

        mustSkip(FOR)

        val name = nameExpr()

        mustSkip(COLON)

        val iterable = expr()

        val block = blockStmt()

        val context = start..<here()

        return Stmt.For(context, name, iterable, block)
    }

    private fun defStmt(): Stmt.Def {
        val start = here()

        mustSkip(DEF)

        var trait = Expr.Name.none
        var target = nameExpr()

        val `for` = skip(FOR)

        if (`for`) {
            trait = target

            target = nameExpr()
        }

        val funs = mutableListOf<Stmt.Fun>()

        mustSkip(LEFT_BRACE)

        while (!skip(RIGHT_BRACE)) {
            funs += funStmt()
        }

        val context = start..<here()

        return Stmt.Def(context, `for`, trait, target, funs)
    }

    private fun funStmt(): Stmt.Fun {
        val start = here()

        mustSkip(FUN)

        val name = nameExpr()

        val args = mutableListOf<Stmt.Arg>()

        mustSkip(LEFT_PAREN)

        var self = false

        if (skip(SELF)) {
            self = true

            skip(COMMA)
        }

        do {
            if (skip(RIGHT_PAREN)) break

            val argName = nameExpr()

            mustSkip(COLON)

            val argType = typeExpr()

            val argContext = argName.context..<argType.context

            args += Stmt.Arg(argContext, argName, argType)
        }
        while (skip(COMMA))

        var type = Expr.Type(Context.none, DataType.Primitive.VOID)

        if (skip(COLON)) {
            type = typeExpr()
        }

        val startBody = peek()

        val body = when (startBody.type) {
            SEMICOLON  -> {
                mustSkip(SEMICOLON)

                Stmt.Empty(startBody.context)
            }

            ARROW      -> {
                val expr = expr()

                val context = expr.context..<here()

                mustSkip(SEMICOLON)

                Stmt.Return(context, expr)
            }

            LEFT_BRACE -> blockStmt()

            else       -> TODO("FUN BODY")
        }

        val context = start..<here()

        return Stmt.Fun(context, name, self, args, type, body)
    }

    private fun structStmt(): Stmt.Struct {
        val start = here()

        mustSkip(STRUCT)

        val name = nameExpr()

        val args = mutableListOf<Stmt.Arg>()

        mustSkip(LEFT_BRACE)

        do {
            if (match(RIGHT_BRACE)) break

            val argName = nameExpr()

            mustSkip(COLON)

            val argType = typeExpr()

            val argContext = argName.context..<argType.context

            args += Stmt.Arg(argContext, argName, argType)
        }
        while (skip(COMMA))

        val context = start..<here()

        mustSkip(RIGHT_BRACE)

        return Stmt.Struct(context, name, args)
    }

    private fun enumStmt(): Stmt.Enum {
        val start = here()

        mustSkip(ENUM)

        val name = nameExpr()

        val variants = mutableListOf<Stmt.Enum.Variant>()

        mustSkip(LEFT_BRACE)

        do {
            if (match(RIGHT_BRACE)) break

            val variantName = nameExpr()

            variants += when {
                skip(LEFT_PAREN) -> {
                    val types = mutableListOf<Expr.Type>()

                    do {
                        types += typeExpr()
                    }
                    while (skip(COMMA))

                    mustSkip(RIGHT_PAREN)

                    Stmt.Enum.Variant.Typed(variantName.context..<types.last().context, variantName, types)
                }

                else             -> Stmt.Enum.Variant.Single(name.context, name)
            }
        }
        while (skip(COMMA))

        val context = start..<here()

        mustSkip(RIGHT_BRACE)

        return Stmt.Enum(context, name, variants)
    }

    private fun breakStmt(): Stmt {
        val start = here()

        mustSkip(BREAK)

        val label = if (skip(COLON)) nameExpr() else null

        val expr = if (!match(SEMICOLON)) expr() else null

        val context = start..<here()

        mustSkip(SEMICOLON)

        return Stmt.Break(context, label, expr)
    }

    private fun continueStmt(): Stmt.Continue {
        val start = here()

        mustSkip(CONTINUE)

        val label = if (skip(COLON)) nameExpr() else null

        val context = start..<here()

        mustSkip(SEMICOLON)

        return Stmt.Continue(context, label)
    }

    private fun returnStmt(): Stmt.Return {
        val start = here()

        mustSkip(RETURN)

        val expr = if (!match(SEMICOLON)) expr() else null

        val context = start..<here()

        mustSkip(SEMICOLON)

        return Stmt.Return(context, expr)
    }

    private fun expressionStmt(): Stmt.Expression {
        val expr = expr()

        val context = expr.context..<here()

        mustSkip(SEMICOLON)

        return Stmt.Expression(context, expr)
    }

    private fun expr() =
        assignment()

    private fun assignment(): Expr {
        val expr = disjunction()

        if (!matchAny(*Symbol.compoundAssignOperators)) return expr

        val op = peek()

        mustSkip(op.type)

        fun desugar(newOp: Expr.Binary.Operator): Expr.Assign {
            val right = disjunction()

            val context = expr.context..<right.context

            return Expr.Assign(
                context,
                expr,
                Expr.Binary(context, newOp, expr, right)
            )
        }

        return when (op.type) {
            PLUS_EQUAL           -> desugar(Expr.Binary.Operator.ADD)

            DASH_EQUAL           -> desugar(Expr.Binary.Operator.SUBTRACT)

            STAR_EQUAL           -> desugar(Expr.Binary.Operator.MULTIPLY)

            SLASH_EQUAL          -> desugar(Expr.Binary.Operator.DIVIDE)

            PERCENT_EQUAL        -> desugar(Expr.Binary.Operator.MODULUS)

            AMPERSAND_EQUAL      -> desugar(Expr.Binary.Operator.BIT_AND)

            CARET_EQUAL          -> desugar(Expr.Binary.Operator.BIT_XOR)

            PIPE_EQUAL           -> desugar(Expr.Binary.Operator.BIT_OR)

            DOUBLE_LESS_EQUAL    -> desugar(Expr.Binary.Operator.BIT_SHIFT_LEFT)

            DOUBLE_GREATER_EQUAL -> desugar(Expr.Binary.Operator.BIT_SHIFT_RIGHT)

            TRIPLE_GREATER_EQUAL -> desugar(Expr.Binary.Operator.BIT_SHIFT_UNSIGNED_RIGHT)

            else                 -> {
                val right = disjunction()

                val context = expr.context..<right.context

                Expr.Assign(context, expr, right)
            }
        }
    }

    private fun subExpr() =
        disjunction()

    private fun disjunction(): Expr {
        var expr = conjunction()

        while (match(DOUBLE_PIPE)) {
            val op = peek()

            mustSkip(op.type)

            val right = conjunction()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun conjunction(): Expr {
        var expr = bitwiseDisjunction()

        while (match(DOUBLE_AMPERSAND)) {
            val op = peek()

            mustSkip(op.type)

            val right = bitwiseDisjunction()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun bitwiseDisjunction(): Expr {
        var expr = bitwiseExclusiveDisjunction()

        while (match(PIPE)) {
            val op = peek()

            mustSkip(op.type)

            val right = bitwiseExclusiveDisjunction()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun bitwiseExclusiveDisjunction(): Expr {
        var expr = bitwiseConjunction()

        while (match(CARET)) {
            val op = peek()

            mustSkip(op.type)

            val right = bitwiseConjunction()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun bitwiseConjunction(): Expr {
        var expr = equality()

        while (match(AMPERSAND)) {
            val op = peek()

            mustSkip(op.type)

            val right = equality()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (matchAny(DOUBLE_EQUAL, EXCLAMATION_EQUAL)) {
            val op = peek()

            mustSkip(op.type)

            val right = comparison()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = bounding()

        while (matchAny(LESS_SIGN, LESS_EQUAL_SIGN, GREATER_SIGN, GREATER_EQUAL_SIGN)) {
            val op = peek()

            mustSkip(op.type)

            val right = bounding()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun bounding(): Expr {
        var expr = infix()

        while (matchAny(IN, EXCLAMATION_IN, IS, EXCLAMATION_IS)) {
            val op = peek()

            mustSkip(op.type)

            val right = infix()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun infix(): Expr {
        var expr = range()

        while (match<Identifier>()) {
            val name = nameExpr()

            val target = Expr.GetMember(name.context, expr, name)

            var arg = subExpr().toArg()

            expr = Expr.Invoke(name.context..<arg.context, target, listOf(arg))
        }

        return expr
    }

    private fun range(): Expr {
        var expr = shifting()

        while (matchAny(DOUBLE_DOT, TRIPLE_DOT)) {
            val op = peek()

            mustSkip(op.type)

            val right = shifting()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun shifting(): Expr {
        var expr = additive()

        while (matchAny(DOUBLE_LESS, DOUBLE_GREATER, TRIPLE_GREATER)) {
            val op = peek()

            mustSkip(op.type)

            val right = additive()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun additive(): Expr {
        var expr = multiplicative()

        while (matchAny(PLUS, DASH)) {
            val op = peek()

            mustSkip(op.type)

            val right = multiplicative()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun multiplicative(): Expr {
        var expr = casting()

        while (matchAny(STAR, SLASH, PERCENT)) {
            val op = peek()

            mustSkip(op.type)

            val right = casting()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun casting(): Expr {
        var expr = prefix()

        while (match(AS)) {
            val op = peek()

            mustSkip(op.type)

            val right = typeExpr()

            val context = expr.context..<right.context

            expr = Expr.Binary(context, Expr.Binary.Operator[op.type], expr, right)
        }

        return expr
    }

    private fun prefix(): Expr {
        if (!matchAny(
                DASH,
                EXCLAMATION,
                TILDE,
                POUND,
                DOUBLE_PLUS,
                DOUBLE_DASH,
                AMPERSAND
            )
        )
            return postfix()

        val op = peek()

        mustSkip(op.type)

        val right = typeExpr()

        val context = op.context..<right.context

        return Expr.Prefix(context, Expr.Prefix.Operator[op.type], prefix())
    }

    private fun postfix(): Expr {
        var expr = terminal()

        while (matchAny(
                DOUBLE_PLUS,
                DOUBLE_DASH,
                DOT,
                DOUBLE_COLON,
                LEFT_SQUARE,
                LEFT_PAREN,
                LEFT_BRACE
            )
        ) {
            val op = peek()

            expr = when {
                skipAny(
                    DOUBLE_PLUS,
                    DOUBLE_DASH
                )                 -> Expr.Postfix(expr.context..<op.context, Expr.Postfix.Operator[op.type], expr)

                skip(DOT)         -> Expr.GetMember(op.context, expr, nameExpr())

                skip(LEFT_SQUARE) -> {
                    val indices = mutableListOf<Expr>()

                    do {
                        indices += expr()
                    }
                    while (skip(COMMA))

                    mustSkip(RIGHT_SQUARE)

                    var subExpr = expr

                    for (index in indices) {
                        subExpr = Expr.GetIndex(index.context, subExpr, index)
                    }

                    subExpr
                }

                skip(LEFT_PAREN)  -> {
                    mustSkip(LEFT_PAREN)

                    val args = mutableListOf<Expr.Invoke.Argument>()

                    if (!skip(RIGHT_PAREN)) {
                        do {
                            val spread = skip(STAR)

                            var argExpr = subExpr()

                            val name = if (skip(EQUAL_SIGN)) {
                                if (argExpr is Expr.Name) {
                                    val n = argExpr

                                    argExpr = subExpr()

                                    n
                                }
                                else {
                                    HyperError.forParser("", argExpr.context)
                                }
                            }
                            else {
                                Expr.Name.none
                            }

                            args += Expr.Invoke.Argument(argExpr.context, spread, name, argExpr)
                        }
                        while (skip(COMMA))

                        mustSkip(RIGHT_PAREN)
                    }

                    Expr.Invoke(op.context, expr, args)
                }

                else              -> error("Broken postfix error!")
            }
        }

        return expr
    }

    private fun terminal(): Expr {
        return when {
            match<Value>()        -> valueExpr()

            match<Identifier>()   -> nameExpr()

            match<LeftTemplate>() -> templateExpr()

            match(LEFT_PAREN)     -> nestedExpr()

            match(LEFT_SQUARE)    -> listExpr()

            match(PIPE)           -> lambdaExpr()

            match(IF)             -> ifExpr()

            match(MATCH)          -> matchExpr()

            match(BREAK)          -> Expr.Statement(here(), breakStmt())

            match(CONTINUE)       -> Expr.Statement(here(), continueStmt())

            else                  -> TODO("TERMINAL")
        }
    }

    private fun valueExpr(): Expr.Value {
        val context = here()

        val value = take<Value>() ?: TODO()

        return Expr.Value(context, value.value)
    }

    private fun nameExpr(): Expr.Name {
        val context = here()

        val identifier = take<Identifier>() ?: TODO()

        return Expr.Name(context, identifier.value)
    }

    private fun typeExpr(): Expr.Type {
        val loc = here()

        var type: DataType = when {
            match<Identifier>() -> DataType.Struct(nameExpr())

            skip(LEFT_PAREN)    -> {
                val paramTypes = mutableListOf<DataType>()

                var returnType: DataType = DataType.Primitive.VOID

                if (!skip(RIGHT_PAREN)) {
                    do {
                        paramTypes += typeExpr().value
                    }
                    while (skip(COMMA))

                    if (skip(ARROW)) {
                        returnType = typeExpr().value
                    }

                    mustSkip(RIGHT_PAREN)
                }

                DataType.Fun(paramTypes, returnType)
            }

            else                -> {
                val token = peek()

                val primitive = DataType.Primitive
                    .values()
                    .firstOrNull {
                        token
                            .type
                            .toString()
                            .equals(it.name, true)
                    }
                    ?: HyperError.forParser("Token type '${token.type}' is not a valid base data type!", token.context)

                mustSkip(token.type)

                primitive
            }
        }

        while (skip(LEFT_SQUARE)) {
            when {
                skip(RIGHT_SQUARE) -> type = DataType.Array(type, (-1).toExpr())

                else               -> {
                    do {
                        val initSize = if (!matchAny(COMMA, RIGHT_SQUARE)) expr() else (-1).toExpr()

                        type = DataType.Array(type, initSize)
                    }
                    while (skip(COMMA))

                    mustSkip(RIGHT_SQUARE)
                }
            }
        }

        if (type is DataType.Array) {
            val initSizes = mutableListOf<Expr>()

            do {
                if (type is DataType.Array) {
                    initSizes += type.initSize

                    type = type.subType
                }
                else break
            }
            while (true)

            while (initSizes.isNotEmpty()) {
                type = DataType.Array(type, initSizes.removeAt(0))
            }
        }
        else if (skip(DOUBLE_DOT)) {
            if (type in listOf(DataType.Primitive.I32, DataType.Primitive.I64, DataType.Primitive.CHAR)) {
                type = DataType.Range(type)
            }
            else {
                HyperError.forParser("Range subtype '$type' is invalid!", here())
            }
        }

        if (skip(STAR)) {
            type = DataType.Vararg(type)
        }

        return Expr.Type(loc, type)
    }

    private fun templateExpr(): Expr.Template {
        val context = here()

        val exprs = mutableListOf<Expr>()

        val left = take<LeftTemplate>() ?: TODO()

        exprs += Expr.Value(context, left.value)

        while (!match<RightTemplate>()) {
            exprs += when {
                match<MiddleTemplate>() -> {
                    val middleContext = here()
                    val middle = take<MiddleTemplate>() ?: TODO()

                    Expr.Value(middleContext, middle.value)
                }

                else                    -> expr()
            }
        }

        val rightContext = here()

        val right = take<RightTemplate>() ?: TODO()

        exprs += Expr.Value(rightContext, right.value)

        return Expr.Template(context, exprs)
    }

    private fun nestedExpr(): Expr {
        mustSkip(LEFT_PAREN)

        val expr = expr()

        mustSkip(RIGHT_PAREN)

        return expr
    }

    private fun listExpr(): Expr {
        val loc = here()

        mustSkip(LEFT_SQUARE)

        val elements = mutableListOf<Expr>()

        var generator = false

        if (!match(RIGHT_SQUARE)) {
            do {
                val spread = skip(STAR)

                var element = expr()

                if (spread) {
                    element = Expr.Spread(element.context, element)
                }

                elements += element
            }
            while (skip(COMMA))



            if (matchAny(FOR, LOOP)) {
                generator = true
            }
        }

        return if (generator) {
            val element = elements[0]

            when {
                skip(LOOP) -> {
                    val size = expr()

                    mustSkip(RIGHT_SQUARE)

                    Expr.ListLoop(loc, element, size)
                }

                skip(FOR)  -> {
                    val isDestructured = skip(LEFT_PAREN)

                    val pointers = mutableListOf<Expr.Name>()

                    if (isDestructured) {
                        do {
                            pointers += nameExpr()
                        }
                        while (skip(COMMA))
                    }
                    else {
                        pointers += nameExpr()
                    }

                    mustSkip(IN)

                    val iterable = expr()


                    val condition = if (skip(IF)) {
                        expr()
                    }
                    else {
                        Expr.Empty(here())
                    }



                    mustSkip(RIGHT_SQUARE)

                    Expr.ListFor(loc, element, isDestructured, pointers, iterable, condition)
                }

                else       -> error("Broken list generator!")
            }
        }
        else {
            mustSkip(RIGHT_SQUARE)

            Expr.ListLiteral(loc, elements)
        }
    }

    private fun lambdaExpr(): Expr.Lambda {
        val `fun` = funStmt()

        return Expr.Lambda(`fun`.context, `fun`)
    }

    private fun exprBodyStmt(): Expr {
        val loc = here()

        return when {
            matchAny(LET, VAR, WHILE, LOOP, FOR, BREAK, CONTINUE, RETURN) -> Expr.Statement(loc, stmt())

            else                                                          -> expr()
        }
    }

    private fun exprBody(): Expr {
        val loc = here()

        return when {
            skip(COLON)      -> exprBodyStmt()

            skip(LEFT_BRACE) -> {
                val exprs = mutableListOf<Expr>()

                while (!skip(RIGHT_BRACE)) {
                    exprs += exprBodyStmt()
                }

                Expr.Block(loc, exprs)
            }

            else             -> HyperError.forParser("Missing body!", here())
        }
    }

    private fun exprBlock(): Expr.Block {
        val context = here()

        val exprs = mutableListOf<Expr>()

        mustSkip(LEFT_BRACE)

        if (!skip(RIGHT_BRACE)) {
            do {
                exprs += expr()
            }
            while (!skip(RIGHT_BRACE))
        }

        return Expr.Block(context, exprs)
    }

    private fun ifExpr(): Expr.If {
        val loc = here()

        mustSkip(IF)

        val condition = expr()

        val body = exprBlock()

        var `else`: Expr = Expr.Empty(here())

        if (skip(ELSE)) {
            `else` = if (match(IF)) {
                ifExpr()
            }
            else {
                exprBlock()
            }
        }

        return Expr.If(loc, condition, body, `else`)
    }

    private fun matchExpr(): Expr.Match {
        val context = here()

        mustSkip(MATCH)

        val subject = expr()

        mustSkip(LEFT_BRACE)
        mustSkip(RIGHT_BRACE)

        return Expr.Match(context, subject)
    }
}