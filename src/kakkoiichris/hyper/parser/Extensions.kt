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
 * Copyright (C) 2024, KakkoiiChris
 *
 * File:    Extensions.kt
 * Created: Wednesday, February 28, 2024, 02:32:24
 *
 * @author Christian Bryce Alexander
 */

fun Expr.toArg() =
    Expr.Invoke.Argument(Context.none, false, Expr.Name.none, this)

fun Any.toExpr() =
    Expr.Value(Context.none, this)