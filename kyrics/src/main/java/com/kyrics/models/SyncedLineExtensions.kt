package com.kyrics.models

/**
 * Check if the given time falls within this line's time range.
 *
 * @param timeMs Time in milliseconds to check
 * @return True if the time is within [start, end] range
 */
fun ISyncedLine.containsTime(timeMs: Int): Boolean = timeMs in start..end

/**
 * Get the duration of this line in milliseconds.
 */
val ISyncedLine.duration: Int
    get() = end - start

/**
 * Calculate the progress through this line at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return Progress as a float from 0.0 to 1.0, or null if time is outside the line
 */
fun ISyncedLine.progressAt(timeMs: Int): Float? {
    if (timeMs < start || timeMs > end) return null
    if (duration == 0) return 1f
    return (timeMs - start).toFloat() / duration
}

/**
 * Check if this line has finished playing at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return True if the time is past this line's end time
 */
fun ISyncedLine.hasPlayedAt(timeMs: Int): Boolean = timeMs > end

/**
 * Check if this line is upcoming (hasn't started yet) at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return True if the time is before this line's start time
 */
fun ISyncedLine.isUpcomingAt(timeMs: Int): Boolean = timeMs < start

/**
 * Find the line that is playing at the given time.
 *
 * @param timeMs Time in milliseconds
 * @return The line playing at the given time, or null if no line matches
 */
fun List<ISyncedLine>.findLineAtTime(timeMs: Int): ISyncedLine? = find { it.containsTime(timeMs) }

/**
 * Find the index of the line that is playing at the given time.
 *
 * @param timeMs Time in milliseconds
 * @return The index of the line playing at the given time, or null if no line matches
 */
fun List<ISyncedLine>.findLineIndexAtTime(timeMs: Int): Int? = indexOfFirst { it.containsTime(timeMs) }.takeIf { it >= 0 }

/**
 * Get the total duration of all lines (from first start to last end).
 *
 * @return Total duration in milliseconds, or 0 if the list is empty
 */
fun List<ISyncedLine>.getTotalDuration(): Int {
    if (isEmpty()) return 0
    val firstStart = minOf { it.start }
    val lastEnd = maxOf { it.end }
    return lastEnd - firstStart
}

/**
 * Get the time range covered by all lines.
 *
 * @return A [Pair] of (startTime, endTime), or null if the list is empty
 */
fun List<ISyncedLine>.getTimeRange(): Pair<Int, Int>? {
    if (isEmpty()) return null
    return minOf { it.start } to maxOf { it.end }
}

/**
 * Find the next line that will play after the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return The next upcoming line, or null if no lines are upcoming
 */
fun List<ISyncedLine>.findNextLine(timeMs: Int): ISyncedLine? = filter { it.start > timeMs }.minByOrNull { it.start }

/**
 * Find the index of the next line that will play after the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return The index of the next upcoming line, or null if no lines are upcoming
 */
fun List<ISyncedLine>.findNextLineIndex(timeMs: Int): Int? {
    val nextLine = findNextLine(timeMs) ?: return null
    return indexOf(nextLine).takeIf { it >= 0 }
}

/**
 * Find the previous line that played before the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return The previous line, or null if no lines have played
 */
fun List<ISyncedLine>.findPreviousLine(timeMs: Int): ISyncedLine? = filter { it.end < timeMs }.maxByOrNull { it.end }

/**
 * Find the index of the previous line that played before the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return The index of the previous line, or null if no lines have played
 */
fun List<ISyncedLine>.findPreviousLineIndex(timeMs: Int): Int? {
    val prevLine = findPreviousLine(timeMs) ?: return null
    return indexOf(prevLine).takeIf { it >= 0 }
}

/**
 * Get all lines that have been played (completely) at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return List of lines that have finished playing
 */
fun List<ISyncedLine>.getPlayedLines(timeMs: Int): List<ISyncedLine> = filter { it.hasPlayedAt(timeMs) }

/**
 * Get all lines that are upcoming (haven't started) at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return List of lines that haven't started yet
 */
fun List<ISyncedLine>.getUpcomingLines(timeMs: Int): List<ISyncedLine> = filter { it.isUpcomingAt(timeMs) }

/**
 * Get lines within a distance range from the current line.
 *
 * @param currentIndex Index of the current line
 * @param range Number of lines before and after to include
 * @return Sublist of lines within the range
 */
fun List<ISyncedLine>.getLinesInRange(
    currentIndex: Int,
    range: Int,
): List<ISyncedLine> {
    if (isEmpty() || currentIndex !in indices) return emptyList()
    val startIndex = (currentIndex - range).coerceAtLeast(0)
    val endIndex = (currentIndex + range + 1).coerceAtMost(size)
    return subList(startIndex, endIndex)
}

/**
 * Calculate progress through the entire lyrics.
 *
 * @param timeMs Current time in milliseconds
 * @return Progress as a float from 0.0 to 1.0
 */
fun List<ISyncedLine>.calculateOverallProgress(timeMs: Int): Float {
    val range = getTimeRange() ?: return 0f
    val (startTime, endTime) = range
    val totalDuration = endTime - startTime
    if (totalDuration == 0) return 1f
    return ((timeMs - startTime).toFloat() / totalDuration).coerceIn(0f, 1f)
}

/**
 * Check if all lines have been played at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return True if all lines have finished playing
 */
fun List<ISyncedLine>.allPlayed(timeMs: Int): Boolean = isNotEmpty() && all { it.hasPlayedAt(timeMs) }

/**
 * Check if no lines have started at the given time.
 *
 * @param timeMs Current time in milliseconds
 * @return True if all lines are still upcoming
 */
fun List<ISyncedLine>.allUpcoming(timeMs: Int): Boolean = isNotEmpty() && all { it.isUpcomingAt(timeMs) }

/**
 * Filter to only accompaniment lines.
 */
fun List<KyricsLine>.filterAccompaniment(): List<KyricsLine> = filter { it.isAccompaniment }

/**
 * Filter to only main vocal lines (non-accompaniment).
 */
fun List<KyricsLine>.filterMainVocals(): List<KyricsLine> = filter { !it.isAccompaniment }

/**
 * Get the total character count across all lines.
 */
fun List<ISyncedLine>.getTotalCharacterCount(): Int = sumOf { it.getContent().length }

/**
 * Get the total syllable count for KyricsLine lists.
 */
fun List<KyricsLine>.getTotalSyllableCount(): Int = sumOf { it.syllables.size }
