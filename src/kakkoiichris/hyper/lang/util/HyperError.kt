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
package kakkoiichris.hyper.lang.util

import kakkoiichris.hyper.lang.lexer.Context
import kotlin.toString

/**
 * Hyper > HyperError.kt
 * Copyright (C) 2018, KakkoiiChris
 * Saturday, October 22, 2022, 14:07:33
 *
 * @author Christian Bryce Alexander
 *
 * A [RuntimeException] subclass to handle errors specific to the Jolt language.
 *
 * @param message The message of the error
 */
class HyperError(message: String) : RuntimeException(message)

/**
 * Throws a [HyperError] that displays an error message, as well as underlining the location within the source code where the error occurred, wrapped in a box.
 *
 * @param message The message of the error
 * @param source The source to get the code line from
 * @param context The location data of the error
 *
 * @throws HyperError
 */
fun hyperError(message: String, source: Source, context: Context): Nothing {
    val fullErrorMessage = buildString {
        appendLine("Hyper Error :: $message!")
        appendLine()
        appendLine("${context.row}| ${source.getLine(context.row)}")

        append(" ".repeat(context.column + (context.row.toString().length) + 1))
        append("$UNDERLINE".repeat(context.length))
    }

    throw HyperError(fullErrorMessage.wrapBox())
}

/**
 * Throws a [HyperError] that displays an error message, as well as underlining the location within the source code where the error occurred, wrapped in a box.
 *
 * @param message The message of the error
 * @param context The location data of the error
 *
 * @throws HyperError
 */
fun hyperError(message: String, context: Context): Nothing=
    hyperError(message, context.source, context)

/**
 * Throws a [HyperError] that displays an error message, without a source location, wrapped in a box.
 *
 * @param message The message of the error
 *
 * @throws HyperError
 */
fun hyperError(message: String): Nothing {
    val fullErrorMessage = "Hyper Error :: $message!"

    throw HyperError(fullErrorMessage.wrapBox())
}