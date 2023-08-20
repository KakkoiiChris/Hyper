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
    
    class Decl(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitDeclStmt(this)
    }
    
    class Block(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBlockStmt(this)
    }
    
    class Do(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitDoStmt(this)
    }
    
    class While(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitWhileStmt(this)
    }
    
    class Until(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitUntilStmt(this)
    }
    
    class Loop(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitLoopStmt(this)
    }
    
    class For(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitForStmt(this)
    }
    
    class Def(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitDefStmt(this)
    }
    
    class Struct(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitStructStmt(this)
    }
    
    class Enum(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitEnumStmt(this)
    }
    
    class Break(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBreakStmt(this)
    }
    
    class Continue(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitContinueStmt(this)
    }
    
    class Return(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitReturnStmt(this)
    }
    
    class Throw(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitThrowStmt(this)
    }
    
    class Expression(context: Context) : Stmt(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitExpressionStmt(this)
    }
    
    interface Visitor<X> {
        fun visit(stmt: Stmt) =
            stmt.accept(this)
        
        fun visitEmptyStmt(stmt: Empty): X
        
        fun visitDeclStmt(stmt: Decl): X
        
        fun visitBlockStmt(stmt: Block): X
        
        fun visitDoStmt(stmt: Do): X
        
        fun visitWhileStmt(stmt: While): X
        
        fun visitUntilStmt(stmt: Until): X
        
        fun visitLoopStmt(stmt: Loop): X
        
        fun visitForStmt(stmt: For): X
        
        fun visitDefStmt(stmt: Def): X
        
        fun visitStructStmt(stmt: Struct): X
        
        fun visitEnumStmt(stmt: Enum): X
        
        fun visitBreakStmt(stmt: Break): X
        
        fun visitContinueStmt(stmt: Continue): X
        
        fun visitReturnStmt(stmt: Return): X
        
        fun visitThrowStmt(stmt: Throw): X
        
        fun visitExpressionStmt(stmt: Expression): X
    }
}