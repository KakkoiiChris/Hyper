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
package kakkoiichris.hyper.parser

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Program.kt
 * Created: Sunday, November 27, 2022, 21:54:48
 *
 * @author Christian Bryce Alexander
 */
class Program(private val stmts: List<Stmt>) : Iterator<Stmt> by stmts.listIterator()