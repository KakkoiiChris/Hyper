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
package kakkoiichris.hyper.lang.lexer

import kakkoiichris.hyper.lang.util.Source
import kakkoiichris.hyper.lang.util.hyperError

/**
 * Hyper > Lexer.kt
 * Copyright (C) 2018, KakkoiiChris
 * Saturday, October 22, 2022, 13:35:07
 *
 * @author Christian Bryce Alexander
 */
class Lexer(private val source: Source) : Iterator<Token<*>> {
    companion object {
        const val NUL = '\u0000'

        val keywords = Keyword.entries.associateBy { it.name.lowercase() }

        val dataTypes = Primitive.entries.associateBy { it.name.lowercase() }

        val literals = listOf(true, false).associateBy { it.toString() }
    }

    private var pos = 0
    private var row = 1
    private var col = 1

    private val lexeme = StringBuilder()

    override fun hasNext() =
        pos <= source.text.length + 1

    override fun next(): Token<*> {
        while (!atEndOfFile()) {
            if (match { it.isWhitespace() }) {
                skipWhitespace()

                continue
            }

            if (match("//")) {
                skipLineComment()

                continue
            }

            if (match("/*")) {
                skipBlockComment()

                continue
            }

            lexeme.clear()

            return when {
                match { it.isDigit() }     -> number()

                match { it.isWordStart() } -> word()

                match('\'')                -> char()

                match('"')                 -> string()

                match('`')                 -> template()

                else                       -> symbol()
            }
        }

        return Token(here(), Symbol.END_OF_FILE)
    }

    private fun here() =
        Context(source, row, col, pos, 1)

    private fun peek(offset: Int = 0) =
        if (pos + offset in source.text.indices)
            source.text[pos + offset]
        else
            NUL

    private fun look(length: Int) =
        buildString {
            repeat(length) { offset ->
                append(peek(offset))
            }
        }

    private fun step(count: Int = 1) {
        repeat(count) {
            lexeme.append(peek())

            if (match('\n')) {
                row++
                col = 1
            }
            else {
                col++
            }

            pos++
        }
    }

    private fun match(char: Char, offset: Int = 0) =
        peek(offset) == char

    private fun match(offset: Int = 0, predicate: (Char) -> Boolean) =
        predicate(peek(offset))

    private fun match(string: String, ignoreCase: Boolean = false) =
        look(string.length).equals(string, ignoreCase)

    private fun skip(char: Char, offset: Int = 0): Boolean {
        if (match(char, offset)) {
            step()

            return true
        }

        return false
    }

    private fun skip(string: String, ignoreCase: Boolean = false): Boolean {
        if (match(string, ignoreCase)) {
            step(string.length)

            return true
        }

        return false
    }

    private fun mustSkip(char: Char, offset: Int = 0) {
        if (!skip(char, offset)) {
            hyperError("Character '${peek()}' is invalid; expected $char!", here())
        }
    }

    private fun mustSkip(string: String) {
        if (!skip(string)) {
            hyperError("Sequence '${look(string.length)}' is invalid; expected $string!", here())
        }
    }

    private fun atEndOfFile() =
        pos >= source.text.length

    private fun Char.isWordStart() =
        isLetter() || this == '_'

    private fun Char.isWord() =
        isLetterOrDigit() || this == '_'

    private fun skipWhitespace() {
        while (match { it.isWhitespace() }) {
            step()
        }
    }

    private fun skipLineComment() {
        mustSkip("//")

        while (!skip('\n')) {
            step()
        }
    }

    private fun skipBlockComment() {
        mustSkip("/*")

        while (!skip("*/")) {
            step()
        }
    }

    private fun StringBuilder.take() {
        append(peek())
        step()
    }

    private fun number() =
        when {
            match("0b", true) -> binaryNumber()
            match("0x", true) -> hexadecimalNumber()
            else              -> decimalNumber()
        }

    private fun Char.isBinaryStart() =
        this in "01"

    private fun Char.isBinary() =
        this in "01_"

    private fun binaryNumber(): Token<Value> {
        var context = here()

        step(2)

        if (!match { it.isBinaryStart() }) {
            hyperError("Binary number starting digit '${peek()}' is invalid", here())
        }

        val result = buildString {
            do {
                skip('_')

                take()
            }
            while (match { it.isBinary() })
        }

        val number: Any = when {
            skip("u8", true)  -> result.toUByteOrNull(2)
            skip("u16", true) -> result.toUShortOrNull(2)
            skip("u32", true) -> result.toUIntOrNull(2)
            skip("u64", true) -> result.toULongOrNull(2)
            skip("u", true)   -> result.toUIntOrNull(2)
            skip("i8", true)  -> result.toByteOrNull(2)
            skip("i16", true) -> result.toShortOrNull(2)
            skip("i32", true) -> result.toIntOrNull(2)
            skip("i64", true) -> result.toLongOrNull(2)
            else              -> result.toIntOrNull(2)
        }
            ?: hyperError("Invalid number", context.withLexeme(lexeme.toString()))

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val type = Value(number)

        return Token(context, type)
    }

    private fun Char.isHexadecimalStart() =
        isDigit() || lowercaseChar() in 'a'..'f'

    private fun Char.isHexadecimal() =
        isDigit() || lowercaseChar() in 'a'..'f' || this == '_'

    private fun hexadecimalNumber(): Token<Value> {
        var context = here()

        step(2)

        if (!match { it.isHexadecimalStart() }) {
            hyperError("Hexadecimal number starting digit '${peek()}' is invalid", here())
        }

        val result = buildString {
            do {
                skip('_')

                take()
            }
            while (match { it.isHexadecimal() })
        }

        val number: Any = when {
            skip("u8", true)  -> result.toUByteOrNull(16)
            skip("u16", true) -> result.toUShortOrNull(16)
            skip("u32", true) -> result.toUIntOrNull(16)
            skip("u64", true) -> result.toULongOrNull(16)
            skip("u", true)   -> result.toUIntOrNull(16)
            skip("i8", true)  -> result.toUByteOrNull(16)
            skip("i16", true) -> result.toUShortOrNull(16)
            skip("i32", true) -> result.toUIntOrNull(16)
            skip("i64", true) -> result.toULongOrNull(16)
            else              -> result.toIntOrNull(16)
        }
            ?: hyperError("Invalid number", context.withLexeme(lexeme.toString()))

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val type = Value(number)

        return Token(context, type)
    }

    private fun Char.isDecimalStart() =
        isDigit()

    private fun Char.isDecimal() =
        isDigit() || this == '_'

    private fun Char.isFloatStart() =
        lowercaseChar() in ".e"

    private fun decimalNumber(): Token<Value> {
        var context = here()

        if (!match { it.isDecimalStart() }) {
            hyperError("Decimal number starting digit '${peek()}' is invalid", here())
        }

        var result = buildString {
            do {
                skip('_')

                take()
            }
            while (match { it.isDecimal() })
        }

        if (!match { it.isFloatStart() }) {
            val number: Any = when {
                skip("u8", true)  -> result.toUByteOrNull()
                skip("u16", true) -> result.toUShortOrNull()
                skip("u32", true) -> result.toUIntOrNull()
                skip("u64", true) -> result.toULongOrNull()
                skip("u", true)   -> result.toUIntOrNull()
                skip("i8", true)  -> result.toByteOrNull()
                skip("i16", true) -> result.toShortOrNull()
                skip("i32", true) -> result.toIntOrNull()
                skip("i64", true) -> result.toLongOrNull()
                else              -> result.toIntOrNull()
            }
                ?: hyperError("Invalid number", context.withLexeme(lexeme.toString()))

            val lexeme = lexeme.toString()

            context = context.withLexeme(lexeme)

            val type = Value(number)

            return Token(context, type)
        }

        result = buildString {
            append(result)

            if (match('.') && match(1, Character::isDigit)) {
                do {
                    skip('_')

                    take()
                }
                while (match { it.isDecimal() })
            }

            if (match { it.lowercaseChar() == 'e' }) {
                take()

                do {
                    skip('_')

                    take()
                }
                while (match { it.isDecimal() })
            }
        }

        val number: Any = when {
            skip("f32", true) -> result.toFloatOrNull()
            skip("f", true)   -> result.toFloatOrNull()
            skip("f64", true) -> result.toDoubleOrNull()
            else              -> result.toDoubleOrNull()
        }
            ?: hyperError("Invalid number", context.withLexeme(lexeme.toString()))

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val type = Value(number)

        return Token(context, type)
    }

    private fun word(): Token<*> {
        var context = here()

        val result = buildString {
            do {
                take()
            }
            while (match { it.isWord() })
        }

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val keyword = keywords[result]

        if (keyword != null) {
            return Token(context, keyword)
        }

        val dataType = dataTypes[result]

        if (dataType != null) {
            return Token(context, dataType)
        }

        val literal = literals[result]

        if (literal != null) {
            return Token(context, Value(literal))
        }

        return Token(context, Identifier(result))
    }

    private fun unicode(size: Int): Char {
        var context = here()

        val result = buildString {
            repeat(size) {
                take()
            }
        }

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        return result.toIntOrNull(16)?.toChar() ?: hyperError("Unicode value '$result' is invalid!", context)
    }

    private fun charEscape(): Char {
        mustSkip('\\')

        return when {
            skip('\\') -> '\\'

            skip('\'') -> '\''

            skip('0')  -> '\u0000'

            skip('b')  -> '\b'

            skip('f')  -> '\u000c'

            skip('n')  -> '\n'

            skip('r')  -> '\r'

            skip('t')  -> '\t'

            skip('x')  -> unicode(2)

            skip('u')  -> unicode(4)

            skip('U')  -> unicode(8)

            skip('(')  -> {
                val name = buildString {
                    while (!skip(")")) {
                        take()
                    }
                }

                Character.codePointOf(name).toChar()
            }

            else       -> hyperError("Character escape '\\${peek()}' is invalid!", here())
        }
    }

    private fun char(): Token<Value> {
        var context = here()

        mustSkip('\'')

        val result = if (match('\\')) {
            charEscape()
        }
        else {
            val char = peek()

            mustSkip(char)

            char
        }

        mustSkip('\'')

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        return Token(context, Value(result))
    }

    private fun Char.isEndOfLine() =
        this in "\r\n"

    private fun stringEscape(): Char {
        mustSkip('\\')

        return when {
            skip('\\') -> '\\'

            skip('"')  -> '"'

            skip('`')  -> '`'

            skip('0')  -> '\u0000'

            skip('b')  -> '\b'

            skip('f')  -> '\u000c'

            skip('n')  -> '\n'

            skip('r')  -> '\r'

            skip('t')  -> '\t'

            skip('x')  -> unicode(2)

            skip('u')  -> unicode(4)

            skip('U')  -> unicode(8)

            skip('(')  -> {
                val name = buildString {
                    while (!skip(")")) {
                        take()
                    }
                }

                Character.codePointOf(name).toChar()
            }

            else       -> hyperError("Character escape '\\${peek()}' is invalid!", here())
        }
    }

    private fun string(): Token<*> {
        var context = here()

        mustSkip('"')

        var movedIn = false

        val result = buildString {
            while (!match('"')) {
                if (match(NUL)) {
                    hyperError("Reached end of file inside of a string!", context.withLexeme(lexeme.toString()))
                }

                if (match { it.isEndOfLine() }) {
                    hyperError("String cannot be multiline!", context.withLexeme(lexeme.toString()))
                }

                if (match('\\')) {
                    append(stringEscape())

                    continue
                }

                if (match('`')) {
                    movedIn = true

                    return@buildString
                }

                take()
            }
        }

        mustSkip(if (movedIn) '`' else '"')

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val type = if (movedIn)
            LeftTemplate(result)
        else
            Value(result)

        return Token(context, type)
    }

    private fun template(): Token<*> {
        var context = here()

        mustSkip('`')

        var movedOut = true

        val result = buildString {
            while (!match('"')) {
                if (match(NUL)) {
                    hyperError("Reached end of file inside of a string!", context.withLexeme(lexeme.toString()))
                }

                if (match { it.isEndOfLine() }) {
                    hyperError("String cannot be multiline!", context.withLexeme(lexeme.toString()))
                }

                if (match('\\')) {
                    append(stringEscape())

                    continue
                }

                if (match('`')) {
                    movedOut = false

                    return@buildString
                }

                take()
            }
        }

        mustSkip(if (movedOut) '"' else '`')

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        val type = if (movedOut)
            RightTemplate(result)
        else
            MiddleTemplate(result)

        return Token(context, type)
    }

    private fun symbol(): Token<Symbol> {
        var context = here()

        val type = when {
            skip('=') -> when {
                skip('=') -> Symbol.DOUBLE_EQUAL

                else      -> Symbol.EQUAL_SIGN
            }

            skip('+') -> when {
                skip('+') -> Symbol.DOUBLE_PLUS

                skip('=') -> Symbol.PLUS_EQUAL

                else      -> Symbol.PLUS
            }

            skip('-') -> when {
                skip('-') -> Symbol.DOUBLE_DASH

                skip('=') -> Symbol.DASH_EQUAL

                skip('>') -> Symbol.ARROW

                else      -> Symbol.DASH
            }

            skip('*') -> when {
                skip('=') -> Symbol.STAR_EQUAL

                else      -> Symbol.STAR
            }

            skip('/') -> when {
                skip('=') -> Symbol.SLASH_EQUAL

                else      -> Symbol.SLASH
            }

            skip('%') -> when {
                skip('=') -> Symbol.PERCENT_EQUAL

                else      -> Symbol.PERCENT
            }

            skip('<') -> when {
                skip('<') -> when {
                    skip('=') -> Symbol.DOUBLE_LESS_EQUAL

                    else      -> Symbol.DOUBLE_LESS
                }

                skip('=') -> Symbol.LESS_EQUAL_SIGN

                else      -> Symbol.LESS_SIGN
            }

            skip('>') -> when {
                skip('>') -> when {
                    skip('>') -> when {
                        skip('=') -> Symbol.TRIPLE_GREATER_EQUAL

                        else      -> Symbol.TRIPLE_GREATER
                    }

                    skip('=') -> Symbol.DOUBLE_GREATER_EQUAL

                    else      -> Symbol.DOUBLE_GREATER
                }

                skip('=') -> Symbol.GREATER_EQUAL_SIGN

                else      -> Symbol.GREATER_SIGN
            }

            skip('|') -> when {
                skip('|') -> Symbol.DOUBLE_PIPE

                skip('=') -> Symbol.PIPE_EQUAL

                else      -> Symbol.PIPE
            }

            skip('^') -> when {
                skip('=') -> Symbol.CARET_EQUAL

                else      -> Symbol.CARET
            }

            skip('&') -> when {
                skip('&') -> Symbol.DOUBLE_AMPERSAND

                skip('=') -> Symbol.AMPERSAND_EQUAL

                else      -> Symbol.AMPERSAND
            }

            skip('!') -> when {
                match("in") && match(2) { it.isWhitespace() } -> {
                    skip("in")

                    Symbol.EXCLAMATION_IN
                }

                match("is") && match(2) { it.isWhitespace() } -> {
                    skip("is")

                    Symbol.EXCLAMATION_IS
                }

                skip('=')                                     -> Symbol.EXCLAMATION_EQUAL

                else                                          -> Symbol.EXCLAMATION
            }

            skip('~') -> Symbol.TILDE

            skip('#') -> Symbol.POUND

            skip('.') -> when {
                skip('.') -> when {
                    skip('.') -> Symbol.TRIPLE_DOT

                    else      -> Symbol.DOUBLE_DOT
                }

                else      -> Symbol.DOT
            }

            skip('(') -> Symbol.LEFT_PAREN

            skip(')') -> Symbol.RIGHT_PAREN

            skip('[') -> Symbol.LEFT_SQUARE

            skip(']') -> Symbol.RIGHT_SQUARE

            skip('{') -> Symbol.LEFT_BRACE

            skip('}') -> Symbol.RIGHT_BRACE

            skip(',') -> Symbol.COMMA

            skip(':') -> when {
                skip(':') -> Symbol.DOUBLE_COLON

                else      -> Symbol.COLON
            }

            skip(';') -> Symbol.SEMICOLON

            else      -> hyperError("Character '${peek()}' is invalid!", here())
        }

        val lexeme = lexeme.toString()

        context = context.withLexeme(lexeme)

        return Token(context, type)
    }
}