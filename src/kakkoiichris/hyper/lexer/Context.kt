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
package kakkoiichris.hyper.lexer

/**
 * Hyper > Context.kt
 * Copyright (C) 2018, KakkoiiChris
 * Saturday, October 22, 2022, 12:27:04
 *
 * @author Christian Bryce Alexander
 */
data class Context(
    val name: String,
    val row: Int,
    val column: Int,
    val line: String,
    val region: IntRange
) {
    companion object {
        val none = Context(
            "",
            0,
            0,
            "",
            0..0
        )
    }

    operator fun rangeTo(end: Context) =
        Context(name, row, column, line, region.first..end.region.last)

    @OptIn(ExperimentalStdlibApi::class)
    operator fun rangeUntil(end: Context) =
        Context(name, row, column, line, region.first..<end.region.last)

    fun withRegion(region: IntRange) =
        Context(name, row, column, line, region)

    override fun toString() =
        if (name.isNotEmpty()) " @ $name.kb ($row, $column)" else ""
}