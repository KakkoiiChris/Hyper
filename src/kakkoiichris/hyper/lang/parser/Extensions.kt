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

import kakkoiichris.hyper.lang.lexer.Context

/**
 * Hyper > Extensions.kt
 * Copyright (C) 2018, KakkoiiChris
 * Wednesday, February 28, 2024, 02:32:24
 *
 * @author Christian Bryce Alexander
 */

fun Expr.toArg() =
    Expr.Invoke.Argument(Context.none, false, Expr.Name.none, this)

fun Any.toExpr() =
    Expr.Value(Context.none, this)