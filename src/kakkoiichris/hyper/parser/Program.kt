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
package kakkoiichris.hyper.parser

/**
 * Hyper > Program.kt
 * Copyright (C) 2018, KakkoiiChris
 * Sunday, November 27, 2022, 21:54:48
 *
 * @author Christian Bryce Alexander
 */
class Program(private val stmts: List<Stmt>) : Iterator<Stmt> by stmts.listIterator()