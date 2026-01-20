package com.kyrics.parser.xml

/**
 * Lightweight XML parser optimized for TTML lyrics parsing.
 *
 * This is NOT a full XML parser. It's specifically designed for:
 * - Extracting `<p>` elements (lyrics lines)
 * - Extracting `<span>` elements (syllables) within paragraphs
 * - Parsing timing attributes (begin, end)
 * - Detecting background vocals (ttm:role="x-bg")
 *
 * Uses regex-based parsing for simplicity and zero dependencies.
 */
class SimpleXmlParser(private val content: String) {

    /**
     * Finds all elements with the given tag name.
     *
     * @param tagName The tag to search for (e.g., "p", "span")
     * @return List of matching [XmlElement]s
     */
    fun findElements(tagName: String): List<XmlElement> {
        val results = mutableListOf<XmlElement>()
        val pattern = buildElementPattern(tagName)
        val matcher = pattern.findAll(content)

        for (match in matcher) {
            val element = parseElement(tagName, match)
            if (element != null) {
                results.add(element)
            }
        }
        return results
    }

    /**
     * Finds all elements with the given tag that have a specific attribute value.
     *
     * @param tagName The tag to search for
     * @param attrName Attribute name to match
     * @param attrValue Attribute value to match
     * @return List of matching [XmlElement]s
     */
    fun findElementsByAttribute(
        tagName: String,
        attrName: String,
        attrValue: String
    ): List<XmlElement> {
        return findElements(tagName).filter { element ->
            element.attributes.any { (key, value) ->
                (key == attrName || key.endsWith(":$attrName")) && value == attrValue
            }
        }
    }

    /**
     * Extracts child elements of a specific type from within an element's inner XML.
     *
     * @param innerXml The inner XML to search within
     * @param tagName The child tag to find
     * @return List of child [XmlElement]s
     */
    fun findChildElements(innerXml: String, tagName: String): List<XmlElement> {
        val parser = SimpleXmlParser(innerXml)
        return parser.findElements(tagName)
    }

    /**
     * Extracts the text content from XML, stripping all tags.
     * Preserves trailing spaces (for lyrics like "Hello ") but trims leading whitespace.
     *
     * @param xml The XML string to extract text from
     * @return Plain text content with preserved trailing spaces
     */
    fun extractTextContent(xml: String): String {
        // Replace tags with empty string, then normalize
        return xml.replace(TAG_PATTERN, "")
            .replace(Regex("""\s*\n+\s*"""), "") // Remove newlines and surrounding whitespace
            .trimStart() // Trim leading whitespace (from XML indentation)
            // Note: trailing spaces are preserved (important for lyrics)
    }

    /**
     * Parses attribute string into a map.
     *
     * @param attributeString The attribute portion of a tag (e.g., `begin="0ms" end="500ms"`)
     * @return Map of attribute names to values
     */
    private fun parseAttributes(attributeString: String): Map<String, String> {
        val attrs = mutableMapOf<String, String>()
        val matcher = ATTR_PATTERN.findAll(attributeString)

        for (match in matcher) {
            val name = match.groupValues[1]
            val value = match.groupValues[2].ifEmpty { match.groupValues[3] }
            attrs[name] = value
        }
        return attrs
    }

    private fun buildElementPattern(tagName: String): Regex {
        // Matches both self-closing and regular elements
        // Group 1: attributes, Group 2: inner content (if not self-closing)
        return Regex(
            """<$tagName(\s+[^>]*)?>([^<]*(?:<(?!/$tagName>)[^<]*)*)</$tagName>|<$tagName(\s+[^>]*)?/>""",
            RegexOption.DOT_MATCHES_ALL
        )
    }

    private fun parseElement(tagName: String, match: MatchResult): XmlElement? {
        val fullMatch = match.value

        // Check if self-closing
        if (fullMatch.endsWith("/>")) {
            val attrString = match.groupValues.getOrNull(3) ?: match.groupValues.getOrNull(1) ?: ""
            return XmlElement(
                tag = tagName,
                attributes = parseAttributes(attrString),
                innerXml = "",
                textContent = ""
            )
        }

        val attrString = match.groupValues.getOrNull(1) ?: ""
        val innerXml = match.groupValues.getOrNull(2) ?: ""

        return XmlElement(
            tag = tagName,
            attributes = parseAttributes(attrString),
            innerXml = innerXml,
            textContent = extractTextContent(innerXml)
        )
    }

    companion object {
        // Pattern to match XML tags for stripping
        private val TAG_PATTERN = Regex("""<[^>]+>""")

        // Pattern to match attributes: name="value" or name='value'
        private val ATTR_PATTERN = Regex("""([\w:-]+)\s*=\s*["']([^"']*)["']|(\w+)\s*=\s*(\S+)""")

        /**
         * Checks if the content appears to be TTML/XML.
         */
        fun isTtml(content: String): Boolean {
            val trimmed = content.trimStart()
            return trimmed.startsWith("<?xml") ||
                    trimmed.startsWith("<tt") ||
                    trimmed.contains("<tt ")
        }
    }
}
