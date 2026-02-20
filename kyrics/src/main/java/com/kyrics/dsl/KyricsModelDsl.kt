package com.kyrics.dsl

import com.kyrics.config.KyricsConfigDsl
import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

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
    private var isAccompaniment = false
    private var alignment = "center"

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
        isAccompaniment = true
    }

    /**
     * Set the alignment for this line.
     *
     * @param alignment Alignment value: "left", "center", or "right"
     */
    fun alignment(alignment: String) {
        this.alignment = alignment
    }

    internal fun build(): KyricsLine =
        KyricsLine(
            syllables = syllables.toList(),
            start = lineStart,
            end = lineEnd,
            isAccompaniment = isAccompaniment,
            alignment = alignment,
        )
}

/**
 * Creates a [KyricsLine] using a type-safe DSL builder.
 */
fun kyricsLine(
    start: Int,
    end: Int,
    block: KyricsLineBuilder.() -> Unit,
): KyricsLine = KyricsLineBuilder(start, end).apply(block).build()

/**
 * DSL builder for creating a list of [KyricsLine] instances.
 */
@KyricsConfigDsl
class KyricsLyricsBuilder {
    private val lines = mutableListOf<KyricsLine>()

    fun line(
        start: Int,
        end: Int,
        block: KyricsLineBuilder.() -> Unit,
    ) {
        lines.add(kyricsLine(start, end, block))
    }

    fun line(line: KyricsLine) {
        lines.add(line)
    }

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
 */
fun kyricsLyrics(block: KyricsLyricsBuilder.() -> Unit): List<KyricsLine> = KyricsLyricsBuilder().apply(block).build()

// ============================================================================
// Factory functions
// ============================================================================

/**
 * Factory object for creating [KyricsLine] instances.
 */
object KyricsLineFactory {
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
                val syllableEnd =
                    if (index == words.lastIndex) end else currentStart + wordDuration
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

    fun accompaniment(
        content: String,
        start: Int,
        end: Int,
    ): KyricsLine =
        KyricsLine(
            syllables = listOf(KyricsSyllable(content, start, end)),
            start = start,
            end = end,
            isAccompaniment = true,
        )
}

fun kyricsLineFromText(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.fromText(content, start, end)

fun kyricsLineFromWords(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.fromWords(content, start, end)

fun kyricsAccompaniment(
    content: String,
    start: Int,
    end: Int,
): KyricsLine = KyricsLineFactory.accompaniment(content, start, end)
