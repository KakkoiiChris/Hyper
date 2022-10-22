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

import kakkoiichris.hyper.util.HyperError
import kakkoiichris.hyper.util.Source
import kakkoiichris.hyper.util.Stack

/**
 * Hyper
 * Copyright (C) 2022, KakkoiiChris
 *
 * File:    Lexer.kt
 * Created: Saturday, October 22, 2022, 13:35:07
 *
 * @author Christian Bryce Alexander
 */
class Lexer(private val source: Source) : Iterator<Token> {
    companion object {
        const val NUL = '\u0000'
        
        val keywords = Token.Type.Keyword.values().associateBy { it.name.lowercase() }
        
        val literals = listOf(true, false).associateBy { it.toString() }
        
        val longRegex = """\d+[Ll]""".toRegex()
        val intRegex = """\d+""".toRegex()
        val floatRegex = """\d+(\.\d+)?([Ee][+-]?\d+)?[Ff]""".toRegex()
        val doubleRegex = """\d+(\.\d+)?([Ee][+-]?\d+)?""".toRegex()
        val longBinaryRegex = """0b[01]+[Ll]""".toRegex()
        val longHexRegex = """0x[\dA-Fa-f]+[Ll]""".toRegex()
        val intBinaryRegex = """0b[01]+""".toRegex()
        val intHexRegex = """0x[\dA-Fa-f]+""".toRegex()
    }
    
    private var pos = 0
    private var row = 1
    private var col = 1
    
    private val delimiters = Stack<String>()
    
    override fun hasNext() = pos <= source.text.length + 1
    
    override fun next(): Token {
        while (!atEndOfFile()) {
            if (match { it.isHorizontalWhitespace() }) {
                skipWhitespace()
                
                continue
            }
            
            if (match('$')) {
                skipLineComment()
                
                continue
            }
            
            if (match("/$")) {
                skipBlockComment()
                
                continue
            }
            
            return when {
                match { it.isDigit() }         -> number()
                
                match { it.isWordStartChar() } -> word()
                
                match('\'')                    -> char()
                
                match("\"\"\"")                -> verbatimString()
                
                match('"')                     -> string()
                
                match('`')                     -> when (delimiters.peek()?.length) {
                    3    -> templateVerbatimString()
                    
                    1    -> templateString()
                    
                    else -> HyperError.failure("Broken string delimiter!")
                }
                
                else                           -> operator()
            }
        }
        
        val type = if (pos == source.text.length)
            Token.Type.Symbol.END_OF_LINE
        else
            Token.Type.Symbol.END_OF_FILE
        
        return Token(here(), type)
    }
    
    private fun here() =
        Location(source.name, row, col)
    
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
    
    private fun match(string: String) =
        look(string.length).equals(string, ignoreCase = true)
    
    private fun skip(char: Char, offset: Int = 0): Boolean {
        if (match(char, offset)) {
            step()
            
            return true
        }
        
        return false
    }
    
    private fun skip(offset: Int = 0, predicate: (Char) -> Boolean): Boolean {
        if (match(offset, predicate)) {
            step()
            
            return true
        }
        
        return false
    }
    
    private fun skip(string: String): Boolean {
        if (match(string)) {
            step(string.length)
            
            return true
        }
        
        return false
    }
    
    private fun mustSkip(char: Char, offset: Int = 0) {
        if (!skip(char, offset)) {
            HyperError.forLexer("Character '${peek()}' is invalid; expected $char!", here())
        }
    }
    
    @Suppress("SameParameterValue")
    private fun mustSkip(string: String) {
        if (!skip(string)) {
            HyperError.forLexer("Sequence '${look(string.length)}' is invalid; expected $string!", here())
        }
    }
    
    private fun atEndOfFile() =
        pos >= source.text.length
    
    private fun Char.isWordStartChar() = isLetter() || this == '_'
    private fun Char.isWordChar() = isLetterOrDigit() || this == '_'
    private fun Char.isHorizontalWhitespace() = this in " \t"
    private fun Char.isEndOfLine() = this in "\r\n"
    private fun Char.isEndOfStatement() = this in ";\r\n"
    
    private fun skipWhitespace() {
        while (match { it.isHorizontalWhitespace() }) {
            step()
        }
    }
    
    private fun skipLineComment() {
        mustSkip('$')
        
        while (!skip('\n')) {
            step()
        }
    }
    
    private fun skipBlockComment() {
        mustSkip("/$")
        
        while (!skip("$/")) {
            step()
        }
    }
    
    private fun StringBuilder.take() {
        append(peek())
        step()
    }
    
    private fun number(): Token {
        val loc = here()
        
        val result = buildString {
            do {
                take()
            }
            while (match { it.isDigit() })
            
            when {
                match('b')                                 -> {
                    do {
                        take()
                    }
                    while (match { it in "01" })
                    
                    if (match { it in "Ll" }) {
                        take()
                    }
                }
                
                match('x')                                 -> {
                    do {
                        take()
                    }
                    while (match { it in "0123456789ABCDEFabcdef" })
                    
                    if (match { it in "Ll" }) {
                        take()
                    }
                }
                
                match('.') && match(1, Character::isDigit) -> {
                    do {
                        take()
                    }
                    while (match { it.isDigit() })
                    
                    if (match { this in "Ee" }) {
                        take()
                        
                        do {
                            take()
                        }
                        while (match { it.isDigit() })
                    }
                    
                    if (match { this in "Ff" }) {
                        take()
                    }
                }
                
                else                                       -> {
                    if (match { this in "FfLl" }) {
                        take()
                    }
                }
            }
        }
        
        val number: Any = when {
            result.matches(longRegex)       -> result.dropLast(1).toLong()
            
            result.matches(intRegex)        -> result.toInt()
            
            result.matches(floatRegex)      -> result.dropLast(1).toFloat()
            
            result.matches(doubleRegex)     -> result.toDouble()
            
            result.matches(longBinaryRegex) -> result.substring(2, result.length - 1).toLong(2)
            
            result.matches(longHexRegex)    -> result.substring(2, result.length - 1).toLong(16)
            
            result.matches(intBinaryRegex)  -> result.substring(2).toInt(2)
            
            result.matches(intHexRegex)     -> result.substring(2).toInt(16)
            
            else                            -> HyperError.forLexer("Unexpected number '$result'!", loc)
        }
        
        val type = Token.Type.Value(number)
        
        return Token(loc, type)
    }
    
    private fun word(): Token {
        val loc = here()
        
        val result = buildString {
            do {
                take()
            }
            while (match { it.isWordChar() })
        }
        
        val keyword = keywords[result]
        
        if (keyword != null) {
            return Token(loc, keyword)
        }
        
        val literal = literals[result]
        
        if (literal != null) {
            return Token(loc, Token.Type.Value(literal))
        }
        
        return Token(loc, Token.Type.Identifier(result))
    }
    
    private fun unicode(size: Int): Char {
        val loc = here()
        
        val result = buildString {
            repeat(size) {
                take()
            }
        }
        
        return result.toIntOrNull(16)?.toChar() ?: HyperError.forLexer("Unicode value '$result' is invalid!", loc)
    }
    
    private fun escape(): Char {
        mustSkip('\\')
        
        return when {
            skip('0')  -> '\u0000'
            
            skip('n')  -> '\n'
            
            skip('t')  -> '\t'
            
            skip('u')  -> unicode(4)
            
            skip('x')  -> unicode(2)
            
            skip('\\') -> '\\'
            
            skip('\'') -> '\''
            
            skip('"')  -> '"'
            
            else       -> HyperError.forLexer("Character escape '\\${peek()}' is invalid!", here())
        }
    }
    
    private fun char(): Token {
        val loc = here()
        
        mustSkip('\'')
        
        val result = if (match('\\')) {
            escape()
        }
        else {
            val char = peek()
            
            mustSkip(char)
            
            char
        }
        
        mustSkip('\'')
        
        return Token(loc, Token.Type.Value(result))
    }
    
    private fun verbatimString(): Token {
        val loc = here()
        
        val delimiter = look(3)
        
        mustSkip(delimiter)
        
        var movedIn = false
        
        val result = buildString {
            while (!match(delimiter)) {
                if (match(NUL)) {
                    HyperError.forLexer("Reached end of file inside of a string!", loc)
                }
                
                if (skip('\\')) {
                    append(
                        when {
                            skip('`') -> '`'
                            
                            else      -> '\\'
                        }
                    )
                    
                    continue
                }
                
                if (match('`')) {
                    delimiters.push(delimiter)
                    
                    movedIn = true
                    
                    return@buildString
                }
                
                take()
            }
        }
        
        mustSkip(if (movedIn) "`" else delimiter)
        
        val type = if (movedIn)
            Token.Type.LeftTemplate(result)
        else
            Token.Type.Value(result)
        
        return Token(loc, type)
    }
    
    private fun templateVerbatimString(): Token {
        val loc = here()
        
        mustSkip('`')
        
        val delimiter = delimiters.peek() ?: HyperError.forLexer("String template end quotes mismatched!", loc)
        
        var movedOut = true
        
        val result = buildString {
            while (!match(delimiter)) {
                if (match(NUL)) {
                    HyperError.forLexer("Reached end of file inside of a string!", loc)
                }
                
                if (skip('\\')) {
                    append(
                        when {
                            skip('`') -> '`'
                            
                            else      -> '\\'
                        }
                    )
                    
                    continue
                }
                
                if (match('`')) {
                    movedOut = false
                    
                    return@buildString
                }
                
                take()
            }
        }
        
        mustSkip(if (movedOut) delimiter else "`")
        
        if (movedOut) {
            delimiters.pop()
        }
        
        val type = if (movedOut)
            Token.Type.RightTemplate(result)
        else
            Token.Type.MiddleTemplate(result)
        
        return Token(loc, type)
    }
    
    private fun string(): Token {
        val loc = here()
        
        val delimiter = peek()
        
        mustSkip(delimiter)
        
        var movedIn = false
        
        val result = buildString {
            while (!match(delimiter)) {
                if (match(NUL)) {
                    HyperError.forLexer("Reached end of file inside of a string!", loc)
                }
                
                if (skip('\\')) {
                    append(when {
                        skip('\\')      -> '\\'
                        
                        skip(delimiter) -> delimiter
                        
                        skip('`')       -> '`'
                        
                        skip('0')       -> '\u0000'
                        
                        skip('b')       -> '\b'
                        
                        skip('f')       -> '\u000c'
                        
                        skip('n')       -> '\n'
                        
                        skip('r')       -> '\r'
                        
                        skip('t')       -> '\t'
                        
                        skip('x')       -> unicode(2)
                        
                        skip('u')       -> unicode(4)
                        
                        skip('U')       -> unicode(8)
                        
                        skip('(')       -> {
                            val name = buildString {
                                while (!match(")")) {
                                    take()
                                }
                                
                                mustSkip(')')
                            }
                            
                            Character.codePointOf(name).toChar()
                        }
                        
                        else            -> HyperError.forLexer("Character escape '\\${peek()}' is invalid!", here())
                    })
                }
                else {
                    if (match { it.isEndOfLine() }) {
                        HyperError.forLexer("String cannot be multiline; use verbatim string literal instead!", loc)
                    }
                    
                    if (match('`')) {
                        delimiters.push(delimiter.toString())
                        
                        movedIn = true
                        
                        return@buildString
                    }
                    
                    take()
                }
            }
        }
        
        mustSkip(if (movedIn) '`' else delimiter)
        
        val type = if (movedIn)
            Token.Type.LeftTemplate(result)
        else
            Token.Type.Value(result)
        
        return Token(loc, type)
    }
    
    private fun templateString(): Token {
        val loc = here()
        
        mustSkip('`')
        
        val delimiter = delimiters.peek()?.get(0) ?: HyperError.forLexer("String template end quotes mismatched!", loc)
        
        var movedOut = true
        
        val result = buildString {
            while (!match(delimiter)) {
                if (match(NUL)) {
                    HyperError.forLexer("Reached end of file inside of a string!", loc)
                }
                
                if (skip('\\')) {
                    append(when {
                        skip('\\')      -> '\\'
                        
                        skip(delimiter) -> delimiter
                        
                        skip('`')       -> '`'
                        
                        skip('0')       -> '\u0000'
                        
                        skip('b')       -> '\b'
                        
                        skip('f')       -> '\u000c'
                        
                        skip('n')       -> '\n'
                        
                        skip('r')       -> '\r'
                        
                        skip('t')       -> '\t'
                        
                        skip('x')       -> unicode(2)
                        
                        skip('u')       -> unicode(4)
                        
                        skip('U')       -> unicode(8)
                        
                        skip('(')       -> {
                            val name = buildString {
                                while (!match(")")) {
                                    take()
                                }
                                
                                mustSkip(')')
                            }
                            
                            Character.codePointOf(name).toChar()
                        }
                        
                        else            -> HyperError.forLexer("Character escape '\\${peek()}' is invalid!", here())
                    })
                }
                else {
                    if (match { it.isEndOfLine() }) {
                        HyperError.forLexer("String cannot be multiline; use verbatim string literal instead!", loc)
                    }
                    
                    if (match('`')) {
                        movedOut = false
                        return@buildString
                    }
                    
                    take()
                }
            }
        }
        
        mustSkip(if (movedOut) delimiter else '`')
        
        if (movedOut) {
            delimiters.pop()
        }
        
        val type = if (movedOut)
            Token.Type.RightTemplate(result)
        else
            Token.Type.MiddleTemplate(result)
        
        return Token(loc, type)
    }
    
    private fun operator(): Token {
        val loc = here()
        
        val type = when {
            skip('=')                      -> when {
                skip('=') -> Token.Type.Symbol.DOUBLE_EQUAL
                
                else      -> Token.Type.Symbol.EQUAL_SIGN
            }
            
            skip('+')                      -> when {
                skip('+') -> Token.Type.Symbol.DOUBLE_PLUS
                
                skip('=') -> Token.Type.Symbol.PLUS_EQUAL
                
                else      -> Token.Type.Symbol.PLUS
            }
            
            skip('-')                      -> when {
                skip('-') -> Token.Type.Symbol.DOUBLE_DASH
                
                skip('=') -> Token.Type.Symbol.DASH_EQUAL
                
                skip('>') -> Token.Type.Symbol.ARROW
                
                else      -> Token.Type.Symbol.DASH
            }
            
            skip('*')                      -> when {
                skip('=') -> Token.Type.Symbol.STAR_EQUAL
                
                else      -> Token.Type.Symbol.STAR
            }
            
            skip('/')                      -> when {
                skip('=') -> Token.Type.Symbol.SLASH_EQUAL
                
                else      -> Token.Type.Symbol.SLASH
            }
            
            skip('%')                      -> when {
                skip('=') -> Token.Type.Symbol.PERCENT_EQUAL
                
                else      -> Token.Type.Symbol.PERCENT
            }
            
            skip('<')                      -> when {
                skip('<') -> when {
                    skip('=') -> Token.Type.Symbol.DOUBLE_LESS_EQUAL
                    
                    else      -> Token.Type.Symbol.DOUBLE_LESS
                }
                
                skip('=') -> Token.Type.Symbol.LESS_EQUAL_SIGN
                
                else      -> Token.Type.Symbol.LESS_SIGN
            }
            
            skip('>')                      -> when {
                skip('>') -> when {
                    skip('>') -> when {
                        skip('=') -> Token.Type.Symbol.TRIPLE_GREATER_EQUAL
                        
                        else      -> Token.Type.Symbol.TRIPLE_GREATER
                    }
                    
                    skip('=') -> Token.Type.Symbol.DOUBLE_GREATER_EQUAL
                    
                    else      -> Token.Type.Symbol.DOUBLE_GREATER
                }
                
                skip('=') -> Token.Type.Symbol.GREATER_EQUAL_SIGN
                
                else      -> Token.Type.Symbol.GREATER_SIGN
            }
            
            skip('|')                      -> when {
                skip('|') -> Token.Type.Symbol.DOUBLE_PIPE
                
                skip('=') -> Token.Type.Symbol.PIPE_EQUAL
                
                else      -> Token.Type.Symbol.PIPE
            }
            
            skip('^')                      -> when {
                skip('=') -> Token.Type.Symbol.CARET_EQUAL
                
                else      -> Token.Type.Symbol.CARET
            }
            
            skip('&')                      -> when {
                skip('&') -> Token.Type.Symbol.DOUBLE_AMPERSAND
                
                skip('=') -> Token.Type.Symbol.AMPERSAND_EQUAL
                
                else      -> Token.Type.Symbol.AMPERSAND
            }
            
            skip('!')                      -> when {
                match("in") && match(2) { it.isWhitespace() } -> {
                    skip("in")
                    
                    Token.Type.Symbol.EXCLAMATION_IN
                }
                
                match("is") && match(2) { it.isWhitespace() } -> {
                    skip("is")
                    
                    Token.Type.Symbol.EXCLAMATION_IS
                }
                
                skip('=')                                     -> Token.Type.Symbol.EXCLAMATION_EQUAL
                
                else                                          -> Token.Type.Symbol.EXCLAMATION
            }
            
            skip('~')                      -> Token.Type.Symbol.TILDE
            
            skip('#')                      -> Token.Type.Symbol.POUND
            
            skip('.')                      -> when {
                skip('.') -> when {
                    skip('.') -> Token.Type.Symbol.TRIPLE_DOT
                    
                    else      -> Token.Type.Symbol.DOUBLE_DOT
                }
                
                else      -> Token.Type.Symbol.DOT
            }
            
            skip('(')                      -> Token.Type.Symbol.LEFT_PAREN
            
            skip(')')                      -> Token.Type.Symbol.RIGHT_PAREN
            
            skip('[')                      -> Token.Type.Symbol.LEFT_SQUARE
            
            skip(']')                      -> Token.Type.Symbol.RIGHT_SQUARE
            
            skip('{')                      -> Token.Type.Symbol.LEFT_BRACE
            
            skip('}')                      -> Token.Type.Symbol.RIGHT_BRACE
            
            skip(',')                      -> Token.Type.Symbol.COMMA
            
            skip('@')                      -> Token.Type.Symbol.AT
            
            skip(':')                      -> when {
                skip(':') -> Token.Type.Symbol.DOUBLE_COLON
                
                else      -> Token.Type.Symbol.COLON
            }
            
            skip { it.isEndOfStatement() } -> Token.Type.Symbol.END_OF_LINE
            
            else                           -> HyperError.forLexer("Character '${peek()}' is invalid!", here())
        }
        
        return Token(loc, type)
    }
}