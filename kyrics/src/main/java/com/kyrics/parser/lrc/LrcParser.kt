package com.kyrics.parser.lrc

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable
import com.kyrics.parser.LyricsFormat
import com.kyrics.parser.LyricsMetadata
import com.kyrics.parser.LyricsParser
import com.kyrics.parser.ParseResult

/**
 * Parser for LRC (Lyric) format files.
 *
 * Supports both Simple LRC and Enhanced LRC formats:
 *
 * **Simple LRC** (line-level timing):
 * ```
 * [ti:Song Title]
 * [ar:Artist Name]
 * [al:Album Name]
 * [00:12.00]First line of lyrics
 * [00:17.20]Second line of lyrics
 * ```
 *
 * **Enhanced LRC** (word/syllable-level timing):
 * ```
 * [00:12.00]<00:12.00>First <00:13.50>line <00:14.20>of <00:15.00>lyrics
 * ```
 *
 * Time format: [mm:ss.xx] or [mm:ss:xx] where xx is hundredths of a second
 */
class LrcParser : LyricsParser {

    override val supportedFormat: LyricsFormat
        get() = if (isEnhanced) LyricsFormat.ENHANCED_LRC else LyricsFormat.LRC

    private var isEnhanced = false

    override fun canParse(content: String): Boolean {
        // LRC files have lines starting with timestamps in square brackets
        return LRC_LINE_PATTERN.containsMatchIn(content)
    }

    override fun parse(content: String): ParseResult {
        return try {
            val lines = content.lines()
            val metadata = parseMetadata(lines)
            val lyricsLines = mutableListOf<KyricsLine>()

            // Detect if this is enhanced LRC (has word-level timestamps)
            isEnhanced = lines.any { ENHANCED_WORD_PATTERN.containsMatchIn(it) }

            // Parse lyrics lines
            val offset = metadata.offset ?: 0
            for (line in lines) {
                parseLyricsLine(line, offset)?.let { lyricsLines.add(it) }
            }

            // Sort by start time and calculate end times
            val sortedLines = lyricsLines.sortedBy { it.start }
            val linesWithEnds = calculateEndTimes(sortedLines)

            // Add warning for simple LRC format
            val warnings = if (!isEnhanced) {
                listOf(SIMPLE_LRC_WARNING)
            } else {
                emptyList()
            }

            ParseResult.Success(
                lines = linesWithEnds,
                metadata = metadata,
                warnings = warnings
            )
        } catch (e: Exception) {
            ParseResult.Failure(
                error = "Failed to parse LRC: ${e.message}",
                lineNumber = null
            )
        }
    }

    /**
     * Parses metadata tags from LRC content.
     */
    private fun parseMetadata(lines: List<String>): LyricsMetadata {
        var title: String? = null
        var artist: String? = null
        var album: String? = null
        var offset: Int? = null
        var duration: Int? = null

        for (line in lines) {
            val match = METADATA_PATTERN.find(line) ?: continue
            val tag = match.groupValues[1].lowercase()
            val value = match.groupValues[2]

            when (tag) {
                "ti" -> title = value
                "ar" -> artist = value
                "al" -> album = value
                "offset" -> offset = value.toIntOrNull()
                "length" -> duration = parseTimeToMs(value)
            }
        }

        return LyricsMetadata(
            title = title,
            artist = artist,
            album = album,
            offset = offset,
            duration = duration
        )
    }

    /**
     * Parses a single lyrics line (with timestamp).
     */
    private fun parseLyricsLine(line: String, offset: Int): KyricsLine? {
        val match = LRC_LINE_PATTERN.find(line) ?: return null
        val timeStr = match.groupValues[1]
        val content = match.groupValues[2]

        if (content.isBlank()) return null

        val lineStart = parseTimeToMs(timeStr) + offset

        return if (isEnhanced && ENHANCED_WORD_PATTERN.containsMatchIn(content)) {
            parseEnhancedLine(lineStart, content, offset)
        } else {
            parseSimpleLine(lineStart, content)
        }
    }

    /**
     * Parses a simple LRC line (no word-level timing).
     * Splits content into word-based syllables for proper text wrapping.
     * All words share the same timing (line start to line end).
     */
    private fun parseSimpleLine(lineStart: Int, content: String): KyricsLine {
        // Split content into words, preserving spaces
        // This allows TextLayoutCalculator to wrap text properly
        val words = splitIntoWords(content)

        if (words.isEmpty()) {
            // Fallback for empty content
            val syllable = KyricsSyllable(
                content = content,
                start = lineStart,
                end = lineStart
            )
            return KyricsLine(
                syllables = listOf(syllable),
                start = lineStart,
                end = lineStart
            )
        }

        // Create syllables for each word
        // End times will be calculated later based on next line
        val syllables = words.map { word ->
            KyricsSyllable(
                content = word,
                start = lineStart,
                end = lineStart // Will be adjusted later
            )
        }

        return KyricsLine(
            syllables = syllables,
            start = lineStart,
            end = lineStart // Will be adjusted later
        )
    }

    /**
     * Splits content into words for proper text wrapping.
     * TextLayoutCalculator handles spacing between syllables, so we don't
     * include trailing spaces here.
     */
    private fun splitIntoWords(content: String): List<String> {
        if (content.isBlank()) return emptyList()

        // Split by whitespace and filter out empty strings
        return content.split(Regex("\\s+")).filter { it.isNotEmpty() }
    }

    /**
     * Parses an enhanced LRC line with word-level timestamps.
     * Format: <mm:ss.xx>word <mm:ss.xx>word ...
     */
    private fun parseEnhancedLine(lineStart: Int, content: String, offset: Int): KyricsLine {
        val syllables = mutableListOf<KyricsSyllable>()
        var lastEnd = lineStart

        // Find all word timestamps
        val matches = ENHANCED_WORD_PATTERN.findAll(content).toList()

        for (i in matches.indices) {
            val match = matches[i]
            val wordStart = parseTimeToMs(match.groupValues[1]) + offset
            val wordText = match.groupValues[2]

            // Determine end time
            val wordEnd = if (i < matches.size - 1) {
                parseTimeToMs(matches[i + 1].groupValues[1]) + offset
            } else {
                // Last word - use start + estimated duration
                wordStart + estimateWordDuration(wordText)
            }

            if (wordText.isNotEmpty()) {
                syllables.add(
                    KyricsSyllable(
                        content = wordText,
                        start = wordStart,
                        end = wordEnd
                    )
                )
                lastEnd = wordEnd
            }
        }

        // If no enhanced words found, fall back to simple parsing
        if (syllables.isEmpty()) {
            return parseSimpleLine(lineStart, content.replace(ENHANCED_WORD_PATTERN, "$2"))
        }

        return KyricsLine(
            syllables = syllables,
            start = syllables.first().start,
            end = lastEnd
        )
    }

    /**
     * Calculates end times for lines based on the start of the next line.
     * For simple LRC, distributes timing based on word length (weighted distribution).
     *
     * Key improvement: Estimates actual line duration based on content length,
     * rather than stretching to fill the entire gap to the next line.
     * This handles musical breaks and pauses naturally.
     */
    private fun calculateEndTimes(lines: List<KyricsLine>): List<KyricsLine> {
        if (lines.isEmpty()) return lines

        return lines.mapIndexed { index, line ->
            val nextLineStart = if (index < lines.size - 1) {
                lines[index + 1].start
            } else {
                // Last line - add a default duration
                line.start + DEFAULT_LINE_DURATION
            }

            val lineStart = line.start
            val gapToNextLine = nextLineStart - lineStart
            val syllableCount = line.syllables.size

            // Check if this is a simple LRC line (all syllables have same start time)
            val isSimpleLrc = line.syllables.all { it.start == lineStart && it.end <= it.start }

            val updatedSyllables = if (isSimpleLrc && syllableCount > 0) {
                // For simple LRC: estimate actual singing duration based on content
                val estimatedDuration = estimateLineDuration(line.syllables)

                // Use the shorter of: estimated duration or gap to next line
                // But ensure minimum duration for very short gaps
                val actualDuration = when {
                    // If gap is very short, use it (lines are close together)
                    gapToNextLine <= estimatedDuration -> gapToNextLine
                    // If there's a big gap (musical break), use estimated duration
                    else -> estimatedDuration
                }

                distributeTimingByWordLength(line.syllables, lineStart, actualDuration)
            } else {
                // For enhanced LRC: update end times if they weren't set
                line.syllables.mapIndexed { syllableIndex, syllable ->
                    if (syllable.end <= syllable.start) {
                        val syllableEnd = if (syllableIndex < syllableCount - 1) {
                            line.syllables[syllableIndex + 1].start
                        } else {
                            nextLineStart
                        }
                        syllable.copy(end = syllableEnd)
                    } else {
                        syllable
                    }
                }
            }

            val lineEnd = updatedSyllables.lastOrNull()?.end ?: lineStart
            line.copy(
                syllables = updatedSyllables,
                end = lineEnd
            )
        }
    }

    /**
     * Estimates the singing duration for a line based on its content.
     * Uses character count and word count to approximate how long it takes to sing.
     */
    private fun estimateLineDuration(syllables: List<KyricsSyllable>): Int {
        val totalChars = syllables.sumOf { it.content.length }
        val wordCount = syllables.size

        // Base duration: ~80ms per character (typical singing pace)
        // Plus ~100ms per word for natural pauses between words
        // Minimum 500ms, maximum based on content
        val baseDuration = (totalChars * MS_PER_CHARACTER) + (wordCount * MS_PER_WORD_GAP)

        return baseDuration.coerceIn(MIN_LINE_DURATION, MAX_LINE_DURATION)
    }

    /**
     * Distributes timing across syllables based on word length.
     * Longer words get more time, shorter words get less time.
     * Uses a weighted formula that considers:
     * - Character count (longer words take more time to sing)
     * - Minimum duration per word (even short words need some time)
     */
    private fun distributeTimingByWordLength(
        syllables: List<KyricsSyllable>,
        lineStart: Int,
        lineDuration: Int
    ): List<KyricsSyllable> {
        if (syllables.isEmpty()) return syllables

        // Calculate weight for each syllable based on character count
        // Use sqrt to prevent very long words from dominating
        // Add minimum weight (1.0) so even single-char words get reasonable time
        val weights = syllables.map { syllable ->
            val charCount = syllable.content.length.coerceAtLeast(1)
            MIN_WORD_WEIGHT + kotlin.math.sqrt(charCount.toFloat())
        }

        val totalWeight = weights.sum()

        // Distribute time based on weights
        var currentTime = lineStart
        return syllables.mapIndexed { index, syllable ->
            val weight = weights[index]
            val duration = ((weight / totalWeight) * lineDuration).toInt()
            val syllableStart = currentTime
            val syllableEnd = currentTime + duration
            currentTime = syllableEnd

            syllable.copy(start = syllableStart, end = syllableEnd)
        }
    }

    /**
     * Parses LRC time format to milliseconds.
     * Supports: mm:ss.xx, mm:ss:xx, mm:ss
     */
    private fun parseTimeToMs(timeStr: String): Int {
        val clean = timeStr.trim()

        // Handle mm:ss.xx or mm:ss:xx format
        val parts = clean.split(":", ".")
        return when (parts.size) {
            2 -> {
                // mm:ss
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                minutes * 60_000 + seconds * 1_000
            }
            3 -> {
                // mm:ss.xx or mm:ss:xx
                val minutes = parts[0].toIntOrNull() ?: 0
                val seconds = parts[1].toIntOrNull() ?: 0
                val centiseconds = parseFraction(parts[2])
                minutes * 60_000 + seconds * 1_000 + centiseconds
            }
            else -> 0
        }
    }

    private fun parseFraction(fraction: String): Int {
        return when (fraction.length) {
            1 -> (fraction.toIntOrNull() ?: 0) * 100
            2 -> (fraction.toIntOrNull() ?: 0) * 10
            else -> fraction.take(3).toIntOrNull() ?: 0
        }
    }

    private fun estimateWordDuration(word: String): Int {
        // Rough estimate: ~100ms per character, minimum 200ms
        return maxOf(200, word.length * 100)
    }

    companion object {
        // Matches LRC timestamp line: [mm:ss.xx]content
        private val LRC_LINE_PATTERN = Regex("""\[(\d{1,2}:\d{2}[.:]\d{2,3})](.*)""")

        // Matches metadata tags: [tag:value]
        private val METADATA_PATTERN = Regex("""\[(\w+):([^\]]*)]""")

        // Matches enhanced LRC word timestamps: <mm:ss.xx>word
        private val ENHANCED_WORD_PATTERN = Regex("""<(\d{1,2}:\d{2}[.:]\d{2,3})>([^<]*)""")

        private const val DEFAULT_LINE_DURATION = 3000 // 3 seconds default

        // Timing estimation constants for simple LRC
        private const val MS_PER_CHARACTER = 80 // ~80ms per character when singing
        private const val MS_PER_WORD_GAP = 100 // ~100ms pause between words
        private const val MIN_LINE_DURATION = 500 // Minimum 500ms per line
        private const val MAX_LINE_DURATION = 6000 // Maximum 6 seconds per line

        // Minimum weight for word timing distribution
        // Ensures short words (like "a", "I", "the") still get reasonable time
        private const val MIN_WORD_WEIGHT = 1.0f

        // Warning message for simple LRC format
        private const val SIMPLE_LRC_WARNING =
            "Simple LRC format detected (line-level timing only). " +
            "Word synchronization is estimated and may not be accurate. " +
            "For better karaoke experience, use Enhanced LRC or TTML format."

        /**
         * Checks if the content appears to be LRC format.
         */
        fun isLrc(content: String): Boolean {
            return LRC_LINE_PATTERN.containsMatchIn(content)
        }

        /**
         * Checks if the content is enhanced LRC (has word-level timestamps).
         */
        fun isEnhancedLrc(content: String): Boolean {
            return isLrc(content) && ENHANCED_WORD_PATTERN.containsMatchIn(content)
        }
    }
}
