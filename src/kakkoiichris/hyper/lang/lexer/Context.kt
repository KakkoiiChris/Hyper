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
package kakkoiichris.hyper.lang.lexer

import kakkoiichris.hyper.lang.util.Source

/**
 * Hyper > Context.kt
 * Copyright (C) 2018, KakkoiiChris
 * Saturday, October 22, 2022, 12:27:04
 *
 * @author Christian Bryce Alexander
 */
data class Context(
    val source: Source,
    val row: Int,
    val column: Int,
    val start: Int,
    val length: Int
) {
    /**
     * @param lexeme The end [Context] to encompass
     *
     * @return A new [Context] instance spanning from this token to the other token
     */
    fun withLexeme(lexeme: String) =
        Context(source, row, column, start, lexeme.length)

    fun withEnd(end: Int) =
        Context(source, row, column, start, end - start)

    fun getCodeStamp() =
        "${source.name} (Row $row, Column $column)"

    override fun toString() =
        if (source.name.isNotEmpty()) " @ ${source.name}.hyp ($row, $column)" else ""

    companion object {
        fun none() = Context(Source("", ""), 0, 0, 0, 0)
    }
}