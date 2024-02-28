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
import kakkoiichris.hyper.lexer.Symbol
import kakkoiichris.hyper.lexer.TokenType

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Expr.kt
 * Created: Sunday, November 27, 2022, 21:54:40
 *
 * @author Christian Bryce Alexander
 */
sealed class Expr(val context: Context) {
    open val disambiguation get() = ""

    abstract fun <X> accept(visitor: Visitor<X>): X

    override fun toString() =
        "${javaClass.simpleName}$disambiguation$context"

    class Empty(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitEmptyExpr(this)
    }

    class Value(context: Context, val value: Any) : Expr(context) {
        override val disambiguation get() = " $value"

        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitValueExpr(this)
    }

    class Name(context: Context, val value: String) : Expr(context) {
        companion object {
            val none = Name(Context.none, "")
        }

        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitNameExpr(this)
    }

    class Type(context: Context, val value: DataType) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitTypeExpr(this)
    }

    class Template(context: Context, val exprs: List<Expr>) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitTemplateExpr(this)
    }

    class Prefix(context: Context, val operator: Operator, val right: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitPrefixExpr(this)

        enum class Operator(val type: TokenType) {
            NEGATE(Symbol.DASH),
            NOT(Symbol.EXCLAMATION),
            INVERT(Symbol.TILDE),
            SIZE(Symbol.POUND),
            INCREMENT(Symbol.DOUBLE_PLUS),
            DECREMENT(Symbol.DOUBLE_DASH),
            REFERENCE(Symbol.AMPERSAND);

            companion object {
                operator fun get(type: TokenType) =
                    values().first { it.type == type }
            }
        }
    }

    class Postfix(context: Context, val operator: Operator, val left: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitPostfixExpr(this)

        enum class Operator(val type: TokenType) {
            INCREMENT(Symbol.DOUBLE_PLUS),
            DECREMENT(Symbol.DOUBLE_DASH);

            companion object {
                operator fun get(type: TokenType) =
                    values().first { it.type == type }
            }
        }
    }

    class Binary(context: Context, val operator: Operator, val left: Expr, val right: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBinaryExpr(this)

        enum class Operator(val type: TokenType) {
            ADD(Symbol.PLUS),
            SUBTRACT(Symbol.DASH),
            MULTIPLY(Symbol.STAR),
            DIVIDE(Symbol.SLASH),
            MODULUS(Symbol.PERCENT),
            BIT_AND(Symbol.AMPERSAND),
            BIT_XOR(Symbol.CARET),
            BIT_OR(Symbol.PIPE),
            BIT_SHIFT_LEFT(Symbol.DOUBLE_LESS),
            BIT_SHIFT_RIGHT(Symbol.DOUBLE_GREATER),
            BIT_SHIFT_UNSIGNED_RIGHT(Symbol.TRIPLE_GREATER);

            companion object {
                operator fun get(type: TokenType) =
                    values().first { it.type == type }
            }
        }
    }

    class Reference(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitReferenceExpr(this)
    }

    class Assign(context: Context, val target: Expr, val expr: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitAssignExpr(this)
    }

    class GetIndex(context: Context, val target: Expr, val index: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitGetIndexExpr(this)
    }

    class SetIndex(context: Context, val target: Expr, val index: Expr, val value: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSetIndexExpr(this)
    }

    class GetMember(context: Context, val target: Expr, val name: Name) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitGetMemberExpr(this)
    }

    class SetMember(context: Context, val target: Expr, val name: Name, val value: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSetMemberExpr(this)
    }

    class Invoke(context: Context, val target: Expr, val args: List<Argument>) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitInvokeExpr(this)

        data class Argument(val context: Context, val spread:Boolean, val name: Name?, val value: Expr)
    }

    class Vararg(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitVarargExpr(this)
    }

    class Spread(context: Context, val expr: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSpreadExpr(this)
    }

    class ListLiteral(context: Context, val elements: List<Expr>) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListLiteralExpr(this)
    }

    class ListLoop(context: Context, val element: Expr, val size: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListLoopExpr(this)
    }

    class ListFor(
        context: Context,
        val element: Expr,
        val isDestructured: Boolean,
        val pointers: List<Name>,
        val iterable: Expr,
        val condition: Expr
    ) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListForExpr(this)
    }

    class Lambda(context: Context, val `fun`: Stmt.Fun) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitLambdaExpr(this)
    }

    class If(context: Context, val condition: Expr, val body: Block, val `else`: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitIfExpr(this)
    }

    class Match(context: Context, val subject: Expr) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitMatchExpr(this)
    }

    class Block(context: Context, val exprs: List<Expr>) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBlockExpr(this)
    }

    class Statement(context: Context, val stmt: Stmt) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitStatementExpr(this)
    }

    interface Visitor<X> {
        fun visit(expr: Expr) =
            expr.accept(this)

        fun visitEmptyExpr(expr: Empty): X

        fun visitValueExpr(expr: Value): X

        fun visitNameExpr(expr: Name): X

        fun visitTypeExpr(expr: Type): X

        fun visitTemplateExpr(expr: Template): X

        fun visitPrefixExpr(expr: Prefix): X

        fun visitPostfixExpr(expr: Postfix): X

        fun visitBinaryExpr(expr: Binary): X

        fun visitReferenceExpr(expr: Reference): X

        fun visitAssignExpr(expr: Assign): X

        fun visitGetIndexExpr(expr: GetIndex): X

        fun visitSetIndexExpr(expr: SetIndex): X

        fun visitGetMemberExpr(expr: GetMember): X

        fun visitSetMemberExpr(expr: SetMember): X

        fun visitInvokeExpr(expr: Invoke): X

        fun visitVarargExpr(expr: Vararg): X

        fun visitSpreadExpr(expr: Spread): X

        fun visitListLiteralExpr(expr: ListLiteral): X

        fun visitListLoopExpr(expr: ListLoop): X

        fun visitListForExpr(expr: ListFor): X

        fun visitLambdaExpr(expr: Lambda): X

        fun visitIfExpr(expr: If): X

        fun visitMatchExpr(expr: Match): X

        fun visitBlockExpr(expr: Block): X

        fun visitStatementExpr(expr: Statement): X
    }
}