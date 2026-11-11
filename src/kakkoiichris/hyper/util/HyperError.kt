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
package kakkoiichris.hyper.util

import kakkoiichris.hyper.lexer.Context

/**
 * Hyper > HyperError.kt
 * Copyright (C) 2018, KakkoiiChris
 * Saturday, October 22, 2022, 14:07:33
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