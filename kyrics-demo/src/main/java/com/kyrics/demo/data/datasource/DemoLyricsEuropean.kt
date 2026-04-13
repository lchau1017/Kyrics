package com.kyrics.demo.data.datasource

import com.kyrics.models.KyricsLine
import com.kyrics.models.KyricsSyllable

internal object DemoLyricsEuropean {
    fun frenchTrack(): List<KyricsLine> = frenchFirst() + frenchSecond()

    fun spanishTrack(): List<KyricsLine> = spanishFirst() + spanishSecond()

    fun germanTrack(): List<KyricsLine> = germanFirst() + germanSecond()

    fun portugueseTrack(): List<KyricsLine> = portugueseFirst() + portugueseSecond()

    fun italianTrack(): List<KyricsLine> = italianFirst() + italianSecond()

    // ==================== French ====================

    private fun frenchFirst(): List<KyricsLine> =
        listOf(
            words("Le soleil se couche doucement", 0, 5000),
            words("Les couleurs peignent le ciel", 5500, 10_000),
            words("Lumi\u00E8re dor\u00E9e sur les eaux", 10_500, 16_000),
        )

    private fun frenchSecond(): List<KyricsLine> =
        listOf(
            words("Les oiseaux rentrent ce soir", 16_500, 21_000),
            words("Les \u00E9toiles brilleront fort", 21_500, 26_000),
            words("Jusqu'\u00E0 la lumi\u00E8re du matin", 26_500, 30_000),
        )

    // ==================== Spanish ====================

    private fun spanishFirst(): List<KyricsLine> =
        listOf(
            words("El sol se pone lentamente", 0, 5000),
            words("Colores pintan el cielo", 5500, 10_000),
            words("Luz dorada sobre el agua", 10_500, 16_000),
        )

    private fun spanishSecond(): List<KyricsLine> =
        listOf(
            words("Los p\u00E1jaros vuelan a casa", 16_500, 21_000),
            words("Las estrellas brillar\u00E1n fuerte", 21_500, 26_000),
            words("Hasta la luz de la ma\u00F1ana", 26_500, 30_000),
        )

    // ==================== German ====================

    private fun germanFirst(): List<KyricsLine> =
        listOf(
            words("Die Sonne geht langsam unter", 0, 5000),
            words("Farben malen den Himmel", 5500, 10_000),
            words("Goldenes Licht \u00FCber dem Wasser", 10_500, 16_000),
        )

    private fun germanSecond(): List<KyricsLine> =
        listOf(
            words("V\u00F6gel fliegen heute Nacht heim", 16_500, 21_000),
            words("Sterne werden hell leuchten", 21_500, 26_000),
            words("Bis das Morgenlicht erscheint", 26_500, 30_000),
        )

    // ==================== Portuguese ====================

    private fun portugueseFirst(): List<KyricsLine> =
        listOf(
            words("O sol se p\u00F5e devagar", 0, 5000),
            words("Cores pintam o c\u00E9u", 5500, 10_000),
            words("Luz dourada sobre a \u00E1gua", 10_500, 16_000),
        )

    private fun portugueseSecond(): List<KyricsLine> =
        listOf(
            words("P\u00E1ssaros voam para casa", 16_500, 21_000),
            words("Estrelas v\u00E3o brilhar forte", 21_500, 26_000),
            words("At\u00E9 a luz da manh\u00E3", 26_500, 30_000),
        )

    // ==================== Italian ====================

    private fun italianFirst(): List<KyricsLine> =
        listOf(
            words("Il sole tramonta piano", 0, 5000),
            words("I colori dipingono il cielo", 5500, 10_000),
            words("Luce dorata sopra le acque", 10_500, 16_000),
        )

    private fun italianSecond(): List<KyricsLine> =
        listOf(
            words("Gli uccelli volano a casa", 16_500, 21_000),
            words("Le stelle brilleranno forte", 21_500, 26_000),
            words("Fino alla luce del mattino", 26_500, 30_000),
        )

    /**
     * Creates a line with word-level timing by splitting on spaces and
     * distributing time evenly across words.
     */
    private fun words(
        text: String,
        start: Int,
        end: Int,
    ): KyricsLine {
        val parts = text.split(" ")
        val duration = end - start
        val wordDuration = duration / parts.size
        val syllables =
            parts.mapIndexed { i, word ->
                val wordStart = start + i * wordDuration
                val wordEnd = if (i == parts.lastIndex) end else wordStart + wordDuration
                val content = if (i < parts.lastIndex) "$word " else word
                KyricsSyllable(content, wordStart, wordEnd)
            }
        return KyricsLine(syllables = syllables, start = start, end = end)
    }
}
