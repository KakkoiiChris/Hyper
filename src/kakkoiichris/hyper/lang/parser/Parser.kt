/* 𝓚𝓪𝓴𝓴𝓸𝓲𝓲𝓒𝓱𝓻𝓲𝓼 𝓟𝓻𝓸𝓾𝓭𝓵𝔂 𝓟𝓻𝓮𝓼𝓮𝓷𝓽𝓼 𝓽𝓱𝓮
 *  ▄▄▄  ▄▄▄
 * █▀██  ██
 *   ██████   ██ ██  ████▄  ▄█▀█▄  ████▄
 *   ██  ██   ██▄██  ██ ██  ██▄█▀  ██
 * ▀██▀  ▀██▄▄▄▀██▀▄▄████▀▄▄▀█▄▄▄▄▄█▀
 *              ██   ██
 *            ▀▀▀    ▀
 *       𝓟𝓻𝓸𝓰𝓻𝓪𝓶𝓶𝓲𝓷𝓰 𝓛𝓪𝓷𝓰𝓾𝓪𝓰𝓮
 *             𝓔𝓼𝓽𝓭. 2018
 */
package kakkoiichris.hyper.lang.parser

import kakkoiichris.hyper.lang.lexer.*
import kakkoiichris.hyper.lang.lexer.Keyword.*
import kakkoiichris.hyper.lang.lexer.Symbol.*
import kakkoiichris.hyper.lang.util.hyperError
import java.util.Stack

/**
 * Hyper > Parser.kt
 * Copyright (C) 2018, KakkoiiChris
 * Sunday, November 27, 2022, 21:53:27
 *
 * @author Christian Bryce Alexander
 */
class Parser(lexer: Lexer) {
    private val lexers = Stack<Lexer>()

    private lateinit var token: Token<*>

    init {
        lexers.push(lexer)
    }

    fun parse() =
        program()

    private fun step() {
        if (lexers.isEmpty()) return

        if (lexers.peek().hasNext()) {
            token = lexers.peek().next()
        }
    }

    private fun match(type: TokenType) =
        token.type == type

    private inline fun <reified T : TokenType> match(): Boolean {
        val type = token.type

        return type is T
    }

    private fun matchAny(vararg types: TokenType): Boolean {
        for (type in types) {
            if (token.type == type) {
                return true
            }
        }

        return false
    }

    private fun skip(type: TokenType) =
        if (token.type == type) {
            step()
            true
        }
        else {
            false
        }

    private inline fun <reified T : TokenType> take(): T {
        val type = token.type

        type as? T ?: error("Broken token type '$type'!")

        step()

        return type
    }

    private fun mustSkip(type: TokenType) {
        if (!skip(type)) {
            hyperError("Expected type $type; got ${token.type} instead!", token.context)
        }
    }

    private fun here() =
        token.context

    private fun program(): Program {
        val mods = mutableListOf<Module>()

        var isRoot = true

        while (!skip(END_OF_FILE)) {
            mods += module(isRoot)

            isRoot = false
        }

        return Program(mods)
    }

    private fun module(isRoot: Boolean): Module {
        var name = ""

        if (!isRoot) {
            mustSkip(MOD)

            name = nameExpr().value

            mustSkip(Symbol.SEMICOLON)
        }

        val stmts = mutableListOf<Stmt>()

        while (!skip(END_OF_FILE)) {
            stmts += stmt()
        }

        return Module(Context.none(), name, emptyList(), emptyList())
    }

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
        val context = here()

        val stmts = mutableListOf<Stmt>()

        mustSkip(LEFT_BRACE)

        while (skip(SEMICOLON)) {
            stmts += stmt()
        }

        mustSkip(RIGHT_BRACE)

        return Stmt.Block(context, stmts)
    }

    private fun declareStmt(): Stmt.Declare {
        val context = here()

        val constant = skip(LET)

        if (!constant) {
            mustSkip(VAR)
        }

        val mutable = skip(MUT)

        val name = nameExpr()

        var type = Expr.Type(Context.none(), DataType.Inferred)

        val typed = skip(COLON)

        if (typed) {
            type = typeExpr()
        }

        var expr: Expr = Expr.Empty(Context.none())

        val assigned = skip(EQUAL_SIGN)

        if (!(typed || assigned)) {
            hyperError("Declaration must either have a data type or be assigned", here())
        }

        if (assigned) {
            expr = expr()
        }

        mustSkip(SEMICOLON)

        return Stmt.Declare(context, constant, mutable, name, type, expr)
    }

    private fun ifStmt(): Stmt.If {
        val context = here()

        mustSkip(IF)

        val condition = expr()

        val yes = blockStmt()

        var no: Stmt = Stmt.Empty(Context.none())

        if (skip(ELSE)) {
            no = if (match(IF)) {
                ifStmt()
            }
            else {
                blockStmt()
            }
        }

        return Stmt.If(context, condition, yes, no)
    }

    private fun matchStmt(): Stmt.Match {
        TODO("IMPLEMENT MATCH")
    }

    private fun loopStmt(): Stmt.Loop {
        val context = here()

        mustSkip(LOOP)

        val block = blockStmt()

        return Stmt.Loop(context, block)
    }

    private fun whileStmt(): Stmt.While {
        val context = here()

        mustSkip(WHILE)

        val condition = expr()

        val block = blockStmt()

        return Stmt.While(context, condition, block)
    }

    private fun untilStmt(): Stmt.Until {
        val context = here()

        mustSkip(UNTIL)

        val condition = expr()

        val block = blockStmt()

        return Stmt.Until(context, condition, block)
    }

    private fun forStmt(): Stmt.For {
        val context = here()

        mustSkip(FOR)

        val name = nameExpr()

        mustSkip(COLON)

        val iterable = expr()

        val block = blockStmt()

        return Stmt.For(context, name, iterable, block)
    }

    private fun defStmt(): Stmt.Def {
        val context = here()

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

        return Stmt.Def(context, `for`, trait, target, funs)
    }

    private fun funStmt(): Stmt.Fun {
        val context = here()

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

            args += Stmt.Arg(argName.context, argName, argType)
        }
        while (skip(COMMA))

        var type = Expr.Type(Context.none(), DataType.Primitive.VOID)

        if (skip(COLON)) {
            type = typeExpr()
        }

        val startBody = token

        val body = when (startBody.type) {
            SEMICOLON  -> {
                mustSkip(SEMICOLON)

                Stmt.Empty(startBody.context)
            }

            ARROW      -> {
                val expr = expr()

                mustSkip(SEMICOLON)

                Stmt.Return(expr.context, expr)
            }

            LEFT_BRACE -> blockStmt()

            else       -> hyperError("Function body is missing: use a ';', '->', or '{...}'", here())
        }

        return Stmt.Fun(context, name, self, args, type, body)
    }

    private fun structStmt(): Stmt.Struct {
        val context = here()

        mustSkip(STRUCT)

        val name = nameExpr()

        val args = mutableListOf<Stmt.Arg>()

        mustSkip(LEFT_BRACE)

        do {
            if (match(RIGHT_BRACE)) break

            val argName = nameExpr()

            mustSkip(COLON)

            val argType = typeExpr()

            args += Stmt.Arg(argName.context, argName, argType)
        }
        while (skip(COMMA))

        mustSkip(RIGHT_BRACE)

        return Stmt.Struct(context, name, args)
    }

    private fun enumStmt(): Stmt.Enum {
        val context = here()

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

                    Stmt.Enum.Variant.Typed(variantName.context, variantName, types)
                }

                else             -> Stmt.Enum.Variant.Single(name.context, name)
            }
        }
        while (skip(COMMA))

        mustSkip(RIGHT_BRACE)

        return Stmt.Enum(context, name, variants)
    }

    private fun breakStmt(): Stmt {
        val context = here()

        mustSkip(BREAK)

        val label = if (skip(COLON)) nameExpr() else null

        val expr = if (!match(SEMICOLON)) expr() else null

        mustSkip(SEMICOLON)

        return Stmt.Break(context, label, expr)
    }

    private fun continueStmt(): Stmt.Continue {
        val context = here()

        mustSkip(CONTINUE)

        val label = if (skip(COLON)) nameExpr() else null

        mustSkip(SEMICOLON)

        return Stmt.Continue(context, label)
    }

    private fun returnStmt(): Stmt.Return {
        val context = here()

        mustSkip(RETURN)

        val expr = if (!match(SEMICOLON)) expr() else null

        mustSkip(SEMICOLON)

        return Stmt.Return(context, expr)
    }

    private fun expressionStmt(): Stmt.Expression {
        val expr = expr()

        mustSkip(SEMICOLON)

        return Stmt.Expression(expr.context, expr)
    }

    private fun expr() =
        assignment()

    private fun assignment(): Expr {
        val expr = disjunction()

        if (!matchAny(*Symbol.compoundAssignOperators)) return expr

        val (context, op) = token

        mustSkip(op)

        fun desugar(newOp: Expr.Binary.Operator): Expr.Assign {
            val right = disjunction()

            return Expr.Assign(
                context,
                expr,
                Expr.Binary(context, newOp, expr, right)
            )
        }

        return when (op) {
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

                Expr.Assign(context, expr, right)
            }
        }
    }

    private fun subExpr() =
        disjunction()

    private fun disjunction(): Expr {
        var expr = conjunction()

        while (match(DOUBLE_PIPE)) {
            val (context, op) = token

            mustSkip(op)

            val right = conjunction()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun conjunction(): Expr {
        var expr = bitwiseDisjunction()

        while (match(DOUBLE_AMPERSAND)) {
            val (context, op) = token

            mustSkip(op)

            val right = bitwiseDisjunction()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun bitwiseDisjunction(): Expr {
        var expr = bitwiseExclusiveDisjunction()

        while (match(PIPE)) {
            val (context, op) = token

            mustSkip(op)

            val right = bitwiseExclusiveDisjunction()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun bitwiseExclusiveDisjunction(): Expr {
        var expr = bitwiseConjunction()

        while (match(CARET)) {
            val (context, op) = token

            mustSkip(op)

            val right = bitwiseConjunction()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun bitwiseConjunction(): Expr {
        var expr = equality()

        while (match(AMPERSAND)) {
            val (context, op) = token

            mustSkip(op)

            val right = equality()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun equality(): Expr {
        var expr = comparison()

        while (matchAny(DOUBLE_EQUAL, EXCLAMATION_EQUAL)) {
            val (context, op) = token

            mustSkip(op)

            val right = comparison()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun comparison(): Expr {
        var expr = bounding()

        while (matchAny(LESS_SIGN, LESS_EQUAL_SIGN, GREATER_SIGN, GREATER_EQUAL_SIGN)) {
            val (context, op) = token

            mustSkip(op)

            val right = bounding()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun bounding(): Expr {
        var expr = infix()

        while (matchAny(IN, EXCLAMATION_IN, IS, EXCLAMATION_IS)) {
            val (context, op) = token

            mustSkip(op)

            val right = infix()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun infix(): Expr {
        var expr = range()

        while (match<Identifier>()) {
            val name = nameExpr()

            val target = Expr.GetMember(name.context, expr, name)

            val arg = subExpr().toArg()

            expr = Expr.Invoke(name.context, target, listOf(arg))
        }

        return expr
    }

    private fun range(): Expr {
        var expr = shifting()

        while (matchAny(DOUBLE_DOT, TRIPLE_DOT)) {
            val (context, op) = token

            mustSkip(op)

            val right = shifting()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun shifting(): Expr {
        var expr = additive()

        while (matchAny(DOUBLE_LESS, DOUBLE_GREATER, TRIPLE_GREATER)) {
            val (context, op) = token

            mustSkip(op)

            val right = additive()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun additive(): Expr {
        var expr = multiplicative()

        while (matchAny(PLUS, DASH)) {
            val (context, op) = token

            mustSkip(op)

            val right = multiplicative()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun multiplicative(): Expr {
        var expr = casting()

        while (matchAny(STAR, SLASH, PERCENT)) {
            val (context, op) = token

            mustSkip(op)

            val right = casting()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
        }

        return expr
    }

    private fun casting(): Expr {
        var expr = prefix()

        while (match(AS)) {
            val (context, op) = token

            mustSkip(op)

            val right = typeExpr()

            expr = Expr.Binary(context, Expr.Binary.Operator[op], expr, right)
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

        val (context, op) = token

        mustSkip(op)

        return Expr.Prefix(context, Expr.Prefix.Operator[op], prefix())
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
            val (context, op) = token

            expr = when {
                skip(DOUBLE_PLUS) || skip(DOUBLE_DASH) -> Expr.Postfix(context, Expr.Postfix.Operator[op], expr)

                skip(DOT)                              -> Expr.GetMember(context, expr, nameExpr())

                skip(LEFT_SQUARE)                      -> {
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

                skip(LEFT_PAREN)                       -> {
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
                                    hyperError("", argExpr.context)
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

                    Expr.Invoke(context, expr, args)
                }

                else                                   -> error("Broken postfix error!")
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

            else                  -> hyperError("Invalid terminal expression", here())
        }
    }

    private fun valueExpr(): Expr.Value {
        val context = here()

        val value = take<Value>()

        return Expr.Value(context, value.value)
    }

    private fun nameExpr(): Expr.Name {
        val context = here()

        val identifier = take<Identifier>()

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
                val token = token

                val primitive = DataType.Primitive.entries
                    .firstOrNull {
                        token
                            .type
                            .toString()
                            .equals(it.name, true)
                    }
                    ?: hyperError("Token type '${token.type}' is not a valid base data type!", token.context)

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
                hyperError("Range subtype '$type' is invalid!", here())
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

        val left = take<LeftTemplate>()

        exprs += Expr.Value(context, left.value)

        while (!match<RightTemplate>()) {
            exprs += when {
                match<MiddleTemplate>() -> {
                    val middleContext = here()
                    val middle = take<MiddleTemplate>()

                    Expr.Value(middleContext, middle.value)
                }

                else                    -> expr()
            }
        }

        val rightContext = here()

        val right = take<RightTemplate>()

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