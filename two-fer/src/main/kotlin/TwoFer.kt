internal val twofer: () -> String = { "One for you, one for me." }

internal fun twofer(name: String): String {
    return "One for $name, one for me."
}
