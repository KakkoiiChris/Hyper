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
    
    class Prefix(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitPrefixExpr(this)
    }
    
    class Postfix(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitPostfixExpr(this)
    }
    
    class Binary(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBinaryExpr(this)
    }
    
    class Reference(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitReferenceExpr(this)
    }
    
    class Assign(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitAssignExpr(this)
    }
    
    class GetIndex(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitGetIndexExpr(this)
    }
    
    class SetIndex(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSetIndexExpr(this)
    }
    
    class GetMember(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitGetMemberExpr(this)
    }
    
    class SetMember(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSetMemberExpr(this)
    }
    
    class Invoke(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitInvokeExpr(this)
    }
    
    class Vararg(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitVarargExpr(this)
    }
    
    class Spread(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitSpreadExpr(this)
    }
    
    class ListLiteral(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListLiteralExpr(this)
    }
    
    class ListLoop(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListLoopExpr(this)
    }
    
    class ListFor(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitListForExpr(this)
    }
    
    class MapLiteral(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitMapLiteralExpr(this)
    }
    
    class MapFor(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitMapForExpr(this)
    }
    
    class Lambda(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitLambdaExpr(this)
    }
    
    class If(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitIfExpr(this)
    }
    
    class Match(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitMatchExpr(this)
    }
    
    class Try(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitTryExpr(this)
    }
    
    class Block(context: Context) : Expr(context) {
        override fun <X> accept(visitor: Visitor<X>) =
            visitor.visitBlockExpr(this)
    }
    
    class Statement(context: Context) : Expr(context) {
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
        
        fun visitMapLiteralExpr(expr: MapLiteral): X
        
        fun visitMapForExpr(expr: MapFor): X
        
        fun visitLambdaExpr(expr: Lambda): X
        
        fun visitIfExpr(expr: If): X
        
        fun visitMatchExpr(expr: Match): X
        
        fun visitTryExpr(expr: Try): X
        
        fun visitBlockExpr(expr: Block): X
        
        fun visitStatementExpr(expr: Statement): X
    }
}