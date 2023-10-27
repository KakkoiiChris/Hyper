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

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Stmt.kt
 * Created: Sunday, November 27, 2022, 21:54:33
 *
 * @author Christian Bryce Alexander
 */
sealed class Stmt(val context: Context) {
    open val disambiguation get() = ""

    abstract fun <X> accept(visitor: Visitor<X>): X

    override fun toString() =
        "${javaClass.simpleName}$disambiguation$context"

    class Empty(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitEmptyStmt(this)
    }

    class Declare(context: Context, val constant: Boolean, val mutable: Boolean, val name: Expr.Name, val type: Expr.Type, val expr: Expr) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitDeclareStmt(this)
    }

    class Block(context: Context, val stmts: List<Stmt>) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBlockStmt(this)
    }

    class If(context: Context, val condition: Expr, val `if`: Block, val `else`: Stmt) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitIfStmt(this)
    }

    class Match(context: Context, val subject: Expr, val branches: List<Branch>) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitMatchStmt(this)

        class Branch(val context: Context)
    }

    class While(context: Context, val condition: Expr, val block: Block) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitWhileStmt(this)
    }

    class Until(context: Context, val condition: Expr, val block: Block) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitUntilStmt(this)
    }

    class Loop(context: Context, val block: Block) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitLoopStmt(this)
    }

    class For(context: Context, val name: Expr.Name, val iterable: Expr, val block: Block) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitForStmt(this)
    }

    class Def(context: Context, val `for`: Boolean, val trait: Expr.Name, val target: Expr.Name, val funs: MutableList<Fun>) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitDefStmt(this)
    }

    class Fun(context: Context, val name: Expr.Name, val self: Boolean, val args: List<Arg>, val type: Expr.Type, val body: Stmt) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitFunStmt(this)
    }

    class Struct(context: Context, val name: Expr.Name, val args: List<Arg>) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitStructStmt(this)
    }

    class Enum(context: Context, val name: Expr.Name, val variants: List<Variant>) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitEnumStmt(this)

        sealed class Variant(val context: Context, val name: Expr.Name) {
            class Single(context: Context, name: Expr.Name) : Variant(context, name)

            class Typed(context: Context, name: Expr.Name, val types: List<Expr.Type>) : Variant(context, name)
        }
    }

    class Break(context: Context, val label:Expr.Name?, val expr: Expr?) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBreakStmt(this)
    }

    class Continue(context: Context, val label: Expr.Name?) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitContinueStmt(this)
    }

    class Return(context: Context, val expr: Expr?) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitReturnStmt(this)
    }

    class Expression(context: Context, val expr: Expr) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitExpressionStmt(this)
    }

    data class Arg(val context: Context, val name: Expr.Name, val type: Expr.Type)

    interface Visitor<X> {
        fun visit(stmt: Stmt) =
            stmt.accept(this)

        fun visitEmptyStmt(stmt: Empty): X

        fun visitDeclareStmt(stmt: Declare): X

        fun visitBlockStmt(stmt: Block): X

        fun visitIfStmt(stmt: If): X

        fun visitMatchStmt(stmt: Match): X

        fun visitWhileStmt(stmt: While): X

        fun visitUntilStmt(stmt: Until): X

        fun visitLoopStmt(stmt: Loop): X

        fun visitForStmt(stmt: For): X

        fun visitDefStmt(stmt: Def): X

        fun visitFunStmt(stmt: Fun): X

        fun visitStructStmt(stmt: Struct): X

        fun visitEnumStmt(stmt: Enum): X

        fun visitBreakStmt(stmt: Break): X

        fun visitContinueStmt(stmt: Continue): X

        fun visitReturnStmt(stmt: Return): X

        fun visitExpressionStmt(stmt: Expression): X
    }
}