package com.kyrics.parser

/**
 * Supported lyrics file formats.
 */
enum class LyricsFormat {
    /** TTML (Timed Text Markup Language) - W3C standard, used by Apple Music */
    TTML,

    /** Unknown or unsupported format */
    UNKNOWN,
}
