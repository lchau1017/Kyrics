package com.kyrics.demo.data.datasource

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

@Suppress("MaxLineLength")
internal object DemoLyricsCjk {
    fun chineseTrack(): List<KyricsLine> =
        listOf(
            line("\u592A\u967D\u6B63\u5728\u6162\u6162\u843D\u4E0B", 0, 5000),
            line("\u8272\u5F69\u6E32\u67D3\u4E86\u5929\u7A7A", 5500, 10_000),
            line("\u91D1\u8272\u7684\u5149\u7051\u904E\u6C34\u9762", 10_500, 16_000),
            line("\u9CE5\u5152\u4ECA\u665A\u98DB\u56DE\u5BB6", 16_500, 21_000),
            line("\u661F\u661F\u5C07\u6703\u9583\u8000\u5149\u8292", 21_500, 26_000),
            line("\u76F4\u5230\u6668\u5149\u964D\u81E8", 26_500, 30_000),
        )

    fun japaneseTrack(): List<KyricsLine> =
        listOf(
            line("\u592A\u967D\u304C\u3086\u3063\u304F\u308A\u6C88\u3080", 0, 5000),
            line("\u7A7A\u3092\u5F69\u308B\u8272\u305F\u3061", 5500, 10_000),
            line("\u91D1\u8272\u306E\u5149\u304C\u6C34\u9762\u3092\u6E21\u308B", 10_500, 16_000),
            line("\u9CE5\u305F\u3061\u304C\u5BB6\u306B\u5E30\u308B\u591C", 16_500, 21_000),
            line("\u661F\u304C\u660E\u308B\u304F\u8F1D\u304F", 21_500, 26_000),
            line("\u671D\u306E\u5149\u304C\u6765\u308B\u307E\u3067", 26_500, 30_000),
        )

    fun koreanTrack(): List<KyricsLine> =
        listOf(
            line("\uD574\uAC00 \uCC9C\uCC9C\uD788 \uC9C0\uACE0 \uC788\uC5B4", 0, 5000),
            line("\uC0C9\uCC44\uAC00 \uD558\uB298\uC744 \uBB3C\uB4E4\uC5EC", 5500, 10_000),
            line("\uAE08\uBE5B\uC774 \uBB3C \uC704\uB97C \uAC74\uB108", 10_500, 16_000),
            line("\uC0C8\uB4E4\uC774 \uC624\uB298 \uBC24 \uC9D1\uC73C\uB85C \uB3CC\uC544\uAC00", 16_500, 21_000),
            line("\uBCC4\uB4E4\uC774 \uBC1D\uAC8C \uBE5B\uB098\uB9AC", 21_500, 26_000),
            line("\uC544\uCE68 \uBE5B\uC774 \uC62C \uB54C\uAE4C\uC9C0", 26_500, 30_000),
        )

    private fun line(
        text: String,
        start: Int,
        end: Int,
    ): KyricsLine =
        KyricsLine(
            syllables = listOf(KyricsSyllable(text, start, end)),
            start = start,
            end = end,
        )
}
