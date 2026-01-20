package com.kyrics.parser.ttml

/**
 * Parses TTML time strings into milliseconds.
 *
 * Supported formats:
 * - Milliseconds: "100ms" -> 100
 * - Seconds: "1.5s" -> 1500
 * - Clock time MM:SS.mmm: "01:30.500" -> 90500
 * - Clock time HH:MM:SS.mmm: "00:01:30.500" -> 90500
 * - Plain integer: "1000" -> 1000
 *
 * @param timeString The time string to parse
 * @return Time in milliseconds, or 0 if parsing fails
 */
fun parseTime(timeString: String): Int = when {
    timeString.endsWith("ms") -> timeString.removeSuffix("ms").toIntOrNull() ?: 0
    timeString.endsWith("s") -> ((timeString.removeSuffix("s").toDoubleOrNull() ?: 0.0) * 1000).toInt()
    ":" in timeString -> parseColonTime(timeString)
    else -> timeString.toIntOrNull() ?: 0
}

private fun parseColonTime(timeString: String): Int {
    val parts = timeString.split(":")
    return when (parts.size) {
        2 -> parseMmSs(parts[0], parts[1])
        3 -> parseHhMmSs(parts[0], parts[1], parts[2])
        else -> 0
    }
}

private fun parseMmSs(minutes: String, seconds: String): Int {
    val (sec, ms) = parseSecMs(seconds)
    return (minutes.toIntOrNull() ?: 0) * 60_000 + sec * 1_000 + ms
}

private fun parseHhMmSs(hours: String, minutes: String, seconds: String): Int {
    val (sec, ms) = parseSecMs(seconds)
    return (hours.toIntOrNull() ?: 0) * 3_600_000 +
            (minutes.toIntOrNull() ?: 0) * 60_000 +
            sec * 1_000 + ms
}

private fun parseSecMs(seconds: String): Pair<Int, Int> {
    val parts = seconds.split(".")
    val sec = parts[0].toIntOrNull() ?: 0
    val ms = if (parts.size > 1) parseFraction(parts[1]) else 0
    return sec to ms
}

private fun parseFraction(fraction: String): Int = when (fraction.length) {
    1 -> fraction.toIntOrNull()?.times(100) ?: 0
    2 -> fraction.toIntOrNull()?.times(10) ?: 0
    else -> fraction.take(3).toIntOrNull() ?: 0
}
