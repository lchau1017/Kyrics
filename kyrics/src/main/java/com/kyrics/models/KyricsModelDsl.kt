package com.kyrics.models

import com.kyrics.config.KyricsConfigDsl

/**
 * DSL builder for creating [KyricsLine] instances.
 *
 * Example usage:
 * ```kotlin
 * val line = kyricsLine(start = 1000, end = 3000) {
 *     syllable("Hel", duration = 200)
 *     syllable("lo ", duration = 300)
 *     syllable("World", duration = 500)
 * }
 * ```
 */
@KyricsConfigDsl
class KyricsLineBuilder(
    private val lineStart: Int,
    private val lineEnd: Int,
) {
    private val syllables = mutableListOf<KyricsSyllable>()
    private var currentTime: Int = lineStart
    private val metadataMap = mutableMapOf<String, String>()

    /**
     * Add a syllable with automatic timing based on duration.
     * Each syllable starts where the previous one ended.
     *
     * @param content The text content of the syllable
     * @param duration Duration in milliseconds
     */
    fun syllable(
        content: String,
        duration: Int,
    ) {
        syllables.add(
            KyricsSyllable(
                content = content,
                start = currentTime,
                end = currentTime + duration,
            ),
        )
        currentTime += duration
    }

    /**
     * Add a syllable with explicit start and end times.
     *
     * @param content The text content of the syllable
     * @param start Start time in milliseconds
     * @param end End time in milliseconds
     */
    fun syllable(
        content: String,
        start: Int,
        end: Int,
    ) {
        syllables.add(KyricsSyllable(content, start, end))
        currentTime = end
    }

    /**
     * Add multiple syllables from a list of pairs (content to duration).
     *
     * @param syllableData List of pairs where first is content and second is duration
     */
    fun syllables(vararg syllableData: Pair<String, Int>) {
        syllableData.forEach { (content, duration) ->
            syllable(content, duration)
        }
    }

    /**
     * Mark this line as accompaniment/background vocals.
     */
    fun accompaniment() {
        metadataMap["type"] = "accompaniment"
    }

    /**
     * Set the alignment for this line.
     *
     * @param alignment Alignment value: "left", "center", or "right"
     */
    fun alignment(alignment: String) {
        metadataMap["alignment"] = alignment
    }

    /**
     * Add custom metadata to this line.
     *
     * @param key Metadata key
     * @param value Metadata value
     */
    fun metadata(
        key: String,
        value: String,
    ) {
        metadataMap[key] = value
    }

    internal fun build(): KyricsLine =
        KyricsLine(
            syllables = syllables.toList(),
            start = lineStart,
            end = lineEnd,
            metadata = metadataMap.toMap(),
        )
}

/**
 * Creates a [KyricsLine] using a type-safe DSL builder.
 *
 * Example usage:
 * ```kotlin
 * val line = kyricsLine(start = 1000, end = 3000) {
 *     syllable("Hel", duration = 200)
 *     syllable("lo ", duration = 300)
 *     syllable("World", duration = 500)
 * }
 * ```
 *
 * @param start Line start time in milliseconds
 * @param end Line end time in milliseconds
 * @param block DSL builder block
 * @return A configured [KyricsLine] instance
 */
fun kyricsLine(
    start: Int,
    end: Int,
    block: KyricsLineBuilder.() -> Unit,
): KyricsLine = KyricsLineBuilder(start, end).apply(block).build()

/**
 * DSL builder for creating a list of [KyricsLine] instances.
 *
 * Example usage:
 * ```kotlin
 * val lyrics = kyricsLyrics {
 *     line(start = 0, end = 2000) {
 *         syllable("First ", duration = 500)
 *         syllable("line", duration = 500)
 *     }
 *     line(start = 2500, end = 4500) {
 *         syllable("Second ", duration = 600)
 *         syllable("line", duration = 400)
 *     }
 * }
 * ```
 */
@KyricsConfigDsl
class KyricsLyricsBuilder {
    private val lines = mutableListOf<KyricsLine>()

    /**
     * Add a line using the DSL builder.
     *
     * @param start Line start time in milliseconds
     * @param end Line end time in milliseconds
     * @param block DSL builder block for syllables
     */
    fun line(
        start: Int,
        end: Int,
        block: KyricsLineBuilder.() -> Unit,
    ) {
        lines.add(kyricsLine(start, end, block))
    }

    /**
     * Add a pre-built line.
     *
     * @param line The [KyricsLine] to add
     */
    fun line(line: KyricsLine) {
        lines.add(line)
    }

    /**
     * Add an accompaniment line using the DSL builder.
     *
     * @param start Line start time in milliseconds
     * @param end Line end time in milliseconds
     * @param block DSL builder block for syllables
     */
    fun accompaniment(
        start: Int,
        end: Int,
        block: KyricsLineBuilder.() -> Unit,
    ) {
        lines.add(
            kyricsLine(start, end) {
                accompaniment()
                block()
            },
        )
    }

    internal fun build(): List<KyricsLine> = lines.toList()
}

/**
 * Creates a list of [KyricsLine] using a type-safe DSL builder.
 *
 * Example usage:
 * ```kotlin
 * val lyrics = kyricsLyrics {
 *     line(start = 0, end = 2000) {
 *         syllable("First ", duration = 500)
 *         syllable("line", duration = 500)
 *     }
 *     line(start = 2500, end = 4500) {
 *         syllable("Second ", duration = 600)
 *         syllable("line", duration = 400)
 *     }
 * }
 * ```
 *
 * @param block DSL builder block
 * @return A list of configured [KyricsLine] instances
 */
fun kyricsLyrics(block: KyricsLyricsBuilder.() -> Unit): List<KyricsLine> = KyricsLyricsBuilder().apply(block).build()

// ============================================================================
// Convenience factory functions
// ============================================================================

/**
 * Factory object for creating [KyricsLine] instances.
 *
 * Example usage:
 * ```kotlin
 * val line = KyricsLineFactory.fromText("Hello World", 0, 1000)
 * val wordLine = KyricsLineFactory.fromWords("Hello World", 0, 1000)
 * val accompaniment = KyricsLineFactory.accompaniment("(Background)", 0, 1000)
 * ```
 */
object KyricsLineFactory {
    /**
     * Creates a simple [KyricsLine] from plain text.
     * The entire text is treated as a single syllable.
     *
     * @param content The text content
     * @param start Start time in milliseconds
     * @param end End time in milliseconds
     * @return A [KyricsLine] with a single syllable
     */
    fun fromText(
        content: String,
        start: Int,
        end: Int,
    ): KyricsLine =
        KyricsLine(
            syllables = listOf(KyricsSyllable(content, start, end)),
            start = start,
            end = end,
        )

    /**
     * Creates a [KyricsLine] by splitting text on whitespace.
     * Each word becomes a syllable with evenly distributed timing.
     *
     * @param content The text content (words separated by spaces)
     * @param start Start time in milliseconds
     * @param end End time in milliseconds
     * @return A [KyricsLine] with syllables for each word
     */
    fun fromWords(
        content: String,
        start: Int,
        end: Int,
    ): KyricsLine {
        val words = content.split(" ").filter { it.isNotEmpty() }
        if (words.isEmpty()) {
            return KyricsLine(emptyList(), start, end)
        }

        val totalDuration = end - start
        val wordDuration = totalDuration / words.size
        var currentStart = start

        val syllables =
            words.mapIndexed { index, word ->
                val syllableEnd = if (index == words.lastIndex) end else currentStart + wordDuration
                val syllable =
                    KyricsSyllable(
                        content = if (index < words.lastIndex) "$word " else word,
                        start = currentStart,
                        end = syllableEnd,
                    )
                currentStart = syllableEnd
                syllable
            }

        return KyricsLine(syllables, start, end)
    }

    /**
     * Creates an accompaniment [KyricsLine].
     *
     * @param content The text content
     * @param start Start time in milliseconds
     * @param end End time in milliseconds
     * @return An accompaniment [KyricsLine]
     */
    fun accompaniment(
        content: String,
        start: Int,
        end: Int,
    ): KyricsLine =
        KyricsLine(
            syllables = listOf(KyricsSyllable(content, start, end)),
            start = start,
            end = end,
            metadata = mapOf("type" to "accompaniment"),
        )
}

// ============================================================================
// Top-level convenience functions (alternative to factory object)
// ============================================================================

/**
 * Creates a simple [KyricsLine] from plain text.
 * The entire text is treated as a single syllable.
 *
 * @param content The text content
 * @param start Start time in milliseconds
 * @param end End time in milliseconds
 * @return A [KyricsLine] with a single syllable
 */
fun kyricsLineFromText(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.fromText(content, start, end)

/**
 * Creates a [KyricsLine] by splitting text on whitespace.
 * Each word becomes a syllable with evenly distributed timing.
 *
 * @param content The text content (words separated by spaces)
 * @param start Start time in milliseconds
 * @param end End time in milliseconds
 * @return A [KyricsLine] with syllables for each word
 */
fun kyricsLineFromWords(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.fromWords(content, start, end)

/**
 * Creates an accompaniment [KyricsLine].
 *
 * @param content The text content
 * @param start Start time in milliseconds
 * @param end End time in milliseconds
 * @return An accompaniment [KyricsLine]
 */
fun kyricsAccompaniment(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.accompaniment(content, start, end)
