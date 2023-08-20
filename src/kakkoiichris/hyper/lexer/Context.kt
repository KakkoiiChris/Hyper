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
package kakkoiichris.hyper.lexer

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Context.kt
 * Created: Saturday, October 22, 2022, 12:27:04
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