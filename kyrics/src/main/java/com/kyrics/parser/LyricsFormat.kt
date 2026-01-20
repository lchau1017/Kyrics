package com.kyrics.parser

/**
 * Supported lyrics file formats.
 */
enum class LyricsFormat {
    /** TTML (Timed Text Markup Language) - W3C standard, used by Apple Music */
    TTML,

    /** Simple LRC format - line-level timing only */
    LRC,

    /** Enhanced LRC format - word/syllable-level timing */
    ENHANCED_LRC,

    /** Unknown or unsupported format */
    UNKNOWN,
}
