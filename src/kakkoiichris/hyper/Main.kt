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
package kakkoiichris.hyper

import kakkoiichris.hyper.util.Source
import java.io.File

/**
 * Hyper
 
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Main.kt
 * Created: Wednesday, October 19, 2022, 00:34:13
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
        
        val text = readLine()?.takeIf { it.isNotEmpty() } ?: break
        
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
}