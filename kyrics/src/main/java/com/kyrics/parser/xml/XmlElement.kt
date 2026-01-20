package com.kyrics.parser.xml

/**
 * Represents a parsed XML element.
 *
 * @property tag The element tag name (e.g., "p", "span")
 * @property attributes Map of attribute names to values
 * @property innerXml The raw inner XML content (includes child tags)
 * @property textContent The text content only (excludes child tags)
 */
data class XmlElement(
    val tag: String,
    val attributes: Map<String, String>,
    val innerXml: String,
    val textContent: String
) {
    /**
     * Gets an attribute value by name.
     *
     * @param name Attribute name (case-sensitive)
     * @return The attribute value or null if not found
     */
    fun getAttribute(name: String): String? = attributes[name]

    /**
     * Gets an attribute value with namespace prefix.
     * Looks for both "prefix:name" and just "name".
     *
     * @param namespace Namespace prefix (e.g., "ttm")
     * @param name Attribute name
     * @return The attribute value or null if not found
     */
    fun getAttributeWithNamespace(namespace: String, name: String): String? =
        attributes["$namespace:$name"] ?: attributes[name]

    /**
     * Checks if this element has a specific attribute value.
     */
    fun hasAttribute(name: String, value: String): Boolean =
        attributes[name] == value
}
