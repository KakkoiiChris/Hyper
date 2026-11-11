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
package kakkoiichris.hyper

import kakkoiichris.hyper.lexer.Lexer
import kakkoiichris.hyper.parser.Parser
import kakkoiichris.hyper.util.Source
import java.io.File

/**
 * Hyper > Main.kt
 * Copyright (C) 2018, KakkoiiChris
 * Wednesday, October 19, 2022, 00:34:13
 *
 * @author Christian Bryce Alexander
 */
fun main(args: Array<String>) = when (args.size) {
    0    -> repl()

    1    -> file(args.first())

    else -> error("Usage: hyper [path]")
}

private const val HEADER = """
    ______  __
    ___  / / /_____  __________ _____ ________
    __  /_/ / __  / / /___  __ \_  _ \__  ___/
    _  __  /  _  /_/ / __  /_/ //  __/_  /
    /_/ /_/   _\__, /  _  .___/ \___/ /_/
              /____/   /_/
              
                SCRIPTING LANGUAGE
      Copyright (C) 2018, Christian Alexander"""

private fun repl() {
    println("${HEADER.trimIndent()}\n")

    while (true) {
        print("> ")

        val text = readln().takeIf { it.isNotEmpty() } ?: break

        println()

        exec("REPL", text)
    }
}

private fun file(path: String) {
    val file = File(path)

    val name = file.nameWithoutExtension
    val text = file.readText()

    exec(name, text)
}

private fun exec(name: String, text: String) {
    val source = Source(name, text)

    println("$source\n")

    val lexer = Lexer(source)

    val parser = Parser(lexer)

    val program = parser.parse()

    for (expr in program) {
        println(expr)
    }
}