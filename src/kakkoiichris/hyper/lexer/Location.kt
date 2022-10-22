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
 * File:    Location.kt
 * Created: Saturday, October 22, 2022, 12:27:04
 *
 * @author Christian Bryce Alexander
 */
data class Location(val name: String, val row: Int, val column: Int) {
    companion object {
        val none = Location("", 0, 0)
    }
    
    override fun toString() =
        if (name.isNotEmpty()) " @ $name.kb ($row, $column)" else ""
}