val LETTERS = ('a'..'z').map { it }

object Pangram {

    fun isPangram(input: String): Boolean {
        return if (input.isNotEmpty()) {
            input.toLowerCase().trim { it == ' ' }.split(Regex("[._ ]")).joinToString("")
                .let {
                    LETTERS.all { c -> it.contains(c) }
                }
        } else false
    }
}
