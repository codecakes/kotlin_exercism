val VOWELS = listOf<Char>('a', 'e', 'i', 'o', 'u')
const val QU = "qu"
const val SUFFIX = "ay"

object PigLatin {

    /**
     * Translates English to Pig Latin!
     *
     * Check Pig Latin from wikipedia.
     *
     * Works for cases like:
     *  arrayOf("quick fast run", "ickquay astfay unray")
     *
     * Y as second letter in two letter word
     *  arrayOf("my",     "ymay"),
     *  arrayOf("dry", "ydray"),
     *  arrayOf("fry", "yfray"),
     *  arrayOf("Clypeus", "ypeusclay"),
     *  arrayOf("Bryology", "yologybray"),
     *
     * @param phrase String a sentence
     * @return Pig Latin sentence.
     * @see PigLatin.pigify
     */
    fun translate(phrase: String): String {
        return phrase.trim().toLowerCase()
            .split(" ").joinToString(" ") { pigify(it) }
    }

    /**
     * cases:
     *  - if startswith consonant cluster followed by y or has y as second
     *  letter, remove everything before to end and suffix "ay"
     *  - if startswith consonant cluster, remove it to end and suffix "ay"
     *  - if startswith (optional: consonant followed by) "qu" remove until "qu" to end
     *  and suffix "ay"
     *  - if startswith "yt" or "xt" suffix "ay"
     *  - if word startswith a vowel suffix "ay"
     *  - if word startswith vowel followed by "qu" append "ay"
     *
     * @param word Word
     * @return Pig Latin word.
     * @see PigLatin.checkCluster
     */
    private fun pigify(word: String): String {
        // Both are mutually exclusive.
        val s = checkCluster(word)
        val vow: String = checkCluster(word, isConsonant = false)
        // So is this.
        val hasConsonant = s.isNotEmpty()
        val hasVowel = vow.isNotEmpty()
        // Explicit Rules
        return when {
            // startswith "xt" or "yt" sounds like a vowel
            word.startsWith("xr") || word.startsWith("xt") || word.startsWith("yt") -> {
                word + SUFFIX
            }
            // startswith y
            hasConsonant && word.startsWith('y') -> word.substringAfter('y') + "y" + SUFFIX
            // has 'y' after consonant cluster
            hasConsonant && s.contains('y') -> {
                "y" + word.substringAfter('y') + word.substringBefore('y') + SUFFIX
            }
            // has 'qu' after a optional consonant cluster in prefix.
            hasConsonant && word.startsWith(QU) -> word.substringAfter(QU) + QU + SUFFIX
            hasConsonant && word.contains(QU) -> word.substringAfter(QU).let { it + word.substringBefore(it) + SUFFIX }
            // has consonant cluster prefix.
            hasConsonant -> word.substringAfter(s) + s + SUFFIX
            // prefix vowel ends with qu
            hasVowel -> word + SUFFIX
            else -> word
        }
    }

    /**
     * Returns the initial consonant or vowel cluster.
     *
     * Breaks, starting from beginning, wherever letter in word is not a consonant or vowel.
     * @param word String Given word.
     * @param lastSuffix String Last remaining substring after current valid letter.
     * @param prefixStr String Valid prefix substring made of Vowels or Consonants.
     * @param isConsonant Boolean Whether to check for Vowels or Consonants.
     * @return Substring of vowels or consonants.
     * @see PigLatin.pigify for usage.
     */
    private tailrec fun checkCluster(
        word: String,
        lastSuffix: String = "",
        prefixStr: String = "",
        isConsonant: Boolean = true
    ): String {
        val (letter, suffixStr) = word.first().let { letter ->
            if ((isConsonant && !VOWELS.contains(letter)) || (!isConsonant && VOWELS.contains(letter))) {
                Pair(letter, word.substringAfter(letter))
            } else Pair(letter, "0")
        }
        return if (suffixStr == "0" || suffixStr == lastSuffix) prefixStr
        else if (suffixStr == "") prefixStr + letter
        else checkCluster(suffixStr, suffixStr, prefixStr + letter, isConsonant)
    }


}
