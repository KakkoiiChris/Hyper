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

/**
 * Hyper > Source.kt
 * Copyright (C) 2018, KakkoiiChris
 * Wednesday, October 19, 2022, 00:50:17
 *
 * @author Christian Bryce Alexander
 */
data class Source(val name: String, val text: String) {
    companion object {
        fun readLocal(path: String): Source {
            val name = path.substring(path.lastIndexOf('/') + 1, path.indexOf('.'))

            val text = Source::class
                           .java
                           .getResourceAsStream(path)
                           ?.bufferedReader()
                           ?.readText()
                       ?: error("Could not read")

            return Source(name, text)
        }
    }
}