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
package kakkoiichris.hyper.util

import kakkoiichris.hyper.lexer.Context

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    HyperError.kt
 * Created: Saturday, October 22, 2022, 14:07:33
 *
 * @author Christian Bryce Alexander
 */
class HyperError(stage: String, msg: String, loc: Context) : Exception("Error @ $stage: $msg ($loc)") {
    companion object {
        fun forLexer(msg: String, loc: Context): Nothing =
            throw HyperError("Lexer", msg, loc)

        fun forParser(msg: String, loc: Context): Nothing =
            throw HyperError("Parser", msg, loc)

        fun forScript(msg: String, loc: Context): Nothing =
            throw HyperError("Script", msg, loc)

        fun failure(msg: String): Nothing =
            throw HyperError("All", msg, Context.none)
    }
}