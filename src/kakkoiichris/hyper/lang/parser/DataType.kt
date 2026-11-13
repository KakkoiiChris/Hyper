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
package kakkoiichris.hyper.lang.parser

/**
 * Hyper > DataType.kt
 * Copyright (C) 2018, KakkoiiChris
 * Sunday, November 27, 2022, 22:31:27
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
        F32,
        F64,
        CHAR
    }

    data class Range(val subType: DataType) : DataType

    data class Array(val subType: DataType, val initSize: Expr) : DataType

    data class Struct(val name: Expr.Name) : DataType

    data class Fun(val paramTypes: List<DataType>, val returnType: DataType) : DataType

    data class Vararg(val subType: DataType) : DataType
}