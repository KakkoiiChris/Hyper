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

/**
 * Hyper
 
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Source.kt
 * Created: Wednesday, October 19, 2022, 00:50:17
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