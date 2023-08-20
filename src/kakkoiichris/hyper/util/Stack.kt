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
 * File:    Stack.kt
 * Created: Saturday, October 22, 2022, 14:09:14
 *
 * @author Christian Bryce Alexander
 */
class Stack<X> {
    private val elements = ArrayDeque<X>()

    fun peek() =
        elements.lastOrNull()

    fun push(x: X) =
        elements.addLast(x)

    fun pop() =
        elements.removeLastOrNull()
}