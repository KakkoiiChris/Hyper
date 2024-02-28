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
 * File:    DataType.kt
 * Created: Sunday, November 27, 2022, 22:31:27
 *
 * @author Christian Bryce Alexander
 */
interface DataType {
    object Inferred : DataType

    enum class Primitive : DataType {
        VOID,
        BOOL,
        U8,
        U16,
        U32,
        U64,
        I8,
        I16,
        I32,
        I64,
        CHAR
    }

    data class Range(val subType: DataType) : DataType

    data class Array(val subType: DataType, val initSize: Expr) : DataType

    data class Struct(val name: Expr.Name) : DataType

    data class Fun(val paramTypes: List<DataType>, val returnType: DataType) : DataType

    data class Vararg(val subType: DataType) : DataType
}