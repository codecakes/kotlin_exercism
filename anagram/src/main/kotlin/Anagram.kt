class Anagram(private val word: String) {
    private val wordset = word.toLowerCase()
        .groupingBy { it }.eachCount()

    fun match(anagrams: Collection<String>): Set<String> {
        return anagrams.filter {
            val lower = it.toLowerCase()
            lower != word.toLowerCase() && lower.groupingBy { g -> g }.eachCount() == wordset
        }.toSet()
    }
}
