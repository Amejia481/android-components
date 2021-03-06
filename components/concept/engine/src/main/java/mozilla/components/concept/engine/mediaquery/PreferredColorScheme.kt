package mozilla.components.concept.engine.mediaquery

/**
 * A simple data class used to suggest to page content that the user prefers a particular color
 * scheme.
 */
sealed class PreferredColorScheme {
    companion object

    object Light : PreferredColorScheme()
    object Dark : PreferredColorScheme()
    object System : PreferredColorScheme()
}
