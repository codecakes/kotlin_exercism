import java.util.*
import kotlin.reflect.KFunction0

/**
 * Rule 1:
 * If a word begins with a vowel sound, add an "ay" sound to the end of the word.
 * Please note that "xr" and "yt" at the beginning of a word make vowel sounds
 * (e.g. "xray" -> "xrayay", "yttria" -> "yttriaay").
 *
 * Rule 2:
 * If a word begins with a consonant sound, move it to the end of the word and then add an "ay" sound
 * to the end of the word. Consonant sounds can be made up of multiple consonants,
 * a.k.a. a consonant cluster (e.g. "chair" -> "airchay").
 *
 * Rule 3:
 * If a word starts with a consonant sound followed by "qu", move it to the end of the word,
 * and then add an "ay" sound to the end of the word (e.g. "square" -> "aresquay").
 *
 * Rule 4:
 * If a word contains a "y" after a consonant cluster or as the second letter in a two letter word it
 * makes a vowel sound (e.g. "rhythm" -> "ythmrhay", "my" -> "ymay").
 */

val VOWELS = listOf<Char>('a', 'e', 'i', 'o', 'u')
val vowelSoundingInitialInWord: Set<String> = setOf(
    "a", "e", "i", "o", "u", "x", "y"
)

const val SUFFIX = "ay"


sealed interface RuleChecker {

    fun check(word: String): Boolean
}

sealed interface RuleChecker1 : RuleChecker {
    fun checkRule1(word: String): Boolean
}

sealed interface RuleChecker2 : RuleChecker {
    fun checkRule2(word: String): Boolean
}

sealed interface RuleChecker3 : RuleChecker {
    fun checkRule3(word: String): Boolean
}

sealed interface RuleChecker4 : RuleChecker {
    fun checkRule4(word: String): Boolean
}


sealed interface Rule {

    fun invoke(word: String): String
}

sealed interface Rule1 : Rule {
    fun addVowelAy(word: String): String
}

sealed interface Rule2 : Rule {
    fun moveConsonantEnd(word: String): String
}

sealed interface Rule3 : Rule {
    fun moveConsonantQu(word: String): String
}

sealed interface Rule4 : Rule {
    fun makeVowelYAy(word: String): String
}


sealed interface ValidateRule<T : RuleChecker> {

    fun validateRule(word: String, ruleChecker: (String) -> Boolean): Boolean
}

sealed class ValidateRuleImpl {

    // Create a static method
    companion object : ValidateRule<RuleChecker> {
        override fun validateRule(word: String, ruleChecker: (String) -> Boolean): Boolean {
            return ruleChecker(word)
        }
    }
}

sealed class CommonConsonantHelper {

    companion object {
        /**
         * @param word - word to check
         * @return Sequence of characters in word if they're consonants, otherwise empty sequence
         */
        fun commonConsonantCheckSequence(word: String): Sequence<Char> {
            return word.asSequence().takeWhile {
                it.isLetter() && !VOWELS.contains(it)
            }
        }
    }
}

/**
 * Rule 1:
 * If a word begins with a vowel sound, add an "ay" sound to the end of the word.
 * Please note that "xr" and "yt" at the beginning of a word make vowel sounds
 * (e.g. "xray" -> "xrayay", "yttria" -> "yttriaay").
 */
object RuleCheck1Impl : RuleChecker1 {
    /**
     * @param word - word to check
     * @return true if word starts with vowel sound, false otherwise
     */
    override fun checkRule1(word: String): Boolean {
        if (word.length == 1 && VOWELS.contains(word.first())) {
            return true
        }
        // If its legit and second word is a consonant but the word starts with a vowel sounding consonant
        if (word.length > 1 &&
            vowelSoundingInitialInWord.contains(word.first().toString()) &&
            !VOWELS.contains(word[1])
        ) {
            return true
        }
        // If it starts with few vowels but ends with consonants.
        return word.takeWhile { VOWELS.contains(it) }.let {
            it.isNotEmpty() && it.length < word.length
        }
    }

    /**
     * @param word - word to check
     * @return true if word starts with consonant sound, false otherwise
     */
    override fun check(word: String): Boolean {
        return checkRule1(word)
    }
}

/**
 * Rule 2:
 * If a word begins with a consonant sound, move it to the end of the word and then add an "ay" sound
 * to the end of the word. Consonant sounds can be made up of multiple consonants,
 * a.k.a. a consonant cluster (e.g. "chair" -> "airchay").
 */
object RuleCheck2Impl : CommonConsonantHelper(), RuleChecker2 {

    /**
     * @param word - word to check
     * @return true if word starts with consonant cluster, false otherwise
     */
    override fun checkRule2(word: String): Boolean {
        // Count if there are initial consonants in the word
        return commonConsonantCheckSequence(word).any()
    }

    /**
     * @param word - word to check
     * @return true if word starts with consonant cluster, false otherwise
     */
    override fun check(word: String): Boolean {
        return checkRule2(word)
    }
}

/**
 * Rule 3:
 * If a word starts with a consonant sound followed by "qu", move it to the end of the word,
 * and then add an "ay" sound to the end of the word (e.g. "square" -> "aresquay").
 *
 */
object RuleCheck3Impl : CommonConsonantHelper(), RuleChecker3 {

    /**
     * @param word - word to check
     * @return true if word starts with consonant cluster followed by "qu", false otherwise
     */
    override fun checkRule3(word: String): Boolean {
        lateinit var initialConsonants: List<Char>
        val initialConsonantsSequence: Sequence<Char> = commonConsonantCheckSequence(word)
        if (initialConsonantsSequence.any()) {
            initialConsonants = initialConsonantsSequence.toList()
            return (
                    initialConsonants.last() == 'q' &&
                            word.subSequence(initialConsonants.size, word.length).first() == 'u'
                    )
        }
        return false
    }

    /**
     * @param word - word to check
     * @return true if word starts with consonant cluster followed by "qu", false otherwise
     */
    override fun check(word: String): Boolean {
        return checkRule3(word)
    }
}

/**
 * Rule 4:
 * If a word contains a "y" after a consonant cluster or as the second letter in a two letter word it
 * makes a vowel sound (e.g. "rhythm" -> "ythmrhay", "my" -> "ymay").
 */
object RuleCheck4Impl : CommonConsonantHelper(), RuleChecker4 {

    /**
     * @param word - word to check
     * @return true if word contains a "y" after a consonant cluster or
     *  as the second letter in a two-letter word it
     */
    override fun checkRule4(word: String): Boolean {
        if (word.first() == 'y' || !word.contains("y")) {
            return false
        }
        val initialConsonants: MutableList<Char> = mutableListOf()
        val seqConsonant = commonConsonantCheckSequence(word).takeWhile {
            it.isLetter() && it != 'y'
        }
        val isTwoLetterEndsWithY = word.length == 2 && word.last() == 'y'
        if (isTwoLetterEndsWithY) {
            return true
        }
        // Lazy load the char sequence of consonants if any.
        initialConsonants.addAll(0, seqConsonant.toList())
        return (
                // If the length is more than 1 and not just an article
                initialConsonants.size > 0 &&
                        word.subSequence(initialConsonants.size, word.length)
                            .contains('y', ignoreCase = true)
                )
    }

    /**
     * @param word - word to check
     * @return true if word contains a "y" after a consonant cluster or
     *  as the second letter in a two-letter word it
     */
    override fun check(word: String): Boolean {
        return checkRule4(word)
    }
}

/**
 * Rule 1:
 * If a word begins with a vowel sound, add an "ay" sound to the end of the word.
 * Please note that "xr" and "yt" at the beginning of a word make vowel sounds
 * (e.g. "xray" -> "xrayay", "yttria" -> "yttriaay").
 */
object Rule1Impl : CommonConsonantHelper(), Rule1 {
    override fun addVowelAy(word: String): String {
        return "${word}${SUFFIX}"
    }

    override fun invoke(word: String): String {
        return addVowelAy(word)
    }
}

/**
 * Rule 2:
 * If a word begins with a consonant sound, move it to the end of the word and then add an "ay" sound
 * to the end of the word. Consonant sounds can be made up of multiple consonants,
 * a.k.a. a consonant cluster (e.g. "chair" -> "airchay").
 */
object Rule2Impl : CommonConsonantHelper(), Rule2 {

    override fun moveConsonantEnd(word: String): String {
        val firstConsonantSequence: List<Char> = commonConsonantCheckSequence(word).toList()
        return word.subSequence(
            firstConsonantSequence.size, word.length
        ).toString() +
                firstConsonantSequence.joinToString("") +
                SUFFIX
    }

    override fun invoke(word: String): String {
        return moveConsonantEnd(word)
    }

}

/**
 * Rule 3:
 * If a word starts with a consonant sound followed by "qu", move it to the end of the word,
 * and then add an "ay" sound to the end of the word (e.g. "square" -> "aresquay").
 *
 */
object Rule3Impl : CommonConsonantHelper(), Rule3 {
    override fun moveConsonantQu(word: String): String {
        val firstConsonantSequence: List<Char> = commonConsonantCheckSequence(word).toList()
        val newWordSequence: CharSequence = word.subSequence(firstConsonantSequence.size, word.length)
        return "${
            word.subSequence(
                firstConsonantSequence.size + 1,
                word.length
            )
        }${firstConsonantSequence.joinToString("")}${newWordSequence.first()}${SUFFIX}"
    }

    override fun invoke(word: String): String {
        return moveConsonantQu(word)
    }
}

/**
 * Rule 4:
 * If a word contains a "y" after a consonant cluster or as the second letter in a two letter word it
 * makes a vowel sound (e.g. "rhythm" -> "ythmrhay", "my" -> "ymay").
 */
object Rule4Impl : CommonConsonantHelper(), Rule4 {
    override fun makeVowelYAy(word: String): String {
        val beforeYConsonantSequence: Sequence<Char> = commonConsonantCheckSequence(word).takeWhile {
            it.isLetter() && it != 'y'
        }
        val afterYWord: String = word.subSequence(beforeYConsonantSequence.count(), word.length).toString()
        return "${afterYWord}${beforeYConsonantSequence.joinToString("")}${SUFFIX}"
    }

    override fun invoke(word: String): String {
        return makeVowelYAy(word)
    }
}

val ruleMapper: Map<RuleChecker, Rule> = mapOf(
    // Test the vowel capability first
    RuleCheck1Impl to Rule1Impl,
    // Then test in order of complexity rules for consonants in descending order
    RuleCheck4Impl to Rule4Impl,
    RuleCheck3Impl to Rule3Impl,
    RuleCheck2Impl to Rule2Impl
)


/**
 * Memoizing cache function helper
 * Could have done the easier way but trying to act over-smart makes for good practice.
 */
internal fun <
        K : KFunction0<Map<RuleChecker, Rule>>,
        V : Map<RuleChecker, Rule>,
        X : MutableMap<RuleChecker, Rule>,
        > cacheFunction(cache: MutableMap<K, V>, theFunction: K): X {
    @Suppress("UNCHECKED_CAST") val hashMapping = cache.getOrPut(theFunction) { theFunction.invoke() as V }
    @Suppress("UNCHECKED_CAST")
    return hashMapping.toMutableMap() as X
}


object PigLatin {

    private val cache: MutableMap<KFunction0<Map<RuleChecker, Rule>>, Map<RuleChecker, Rule>> = mutableMapOf()

    private fun getRuleMap(): Map<RuleChecker, Rule> = ruleMapper
    private fun rulesMapper(): MutableMap<RuleChecker, Rule> = cacheFunction(cache, ::getRuleMap)
//    private fun rulesMapper(): MutableMap<RuleChecker, Rule>  = cacheFunction(cache, ::ruleMapper).toMutableMap()

//    private fun rulesMapper(): MutableMap<RuleChecker, Rule> = ruleMapper().toMutableMap()

    /**
     * Translates English to Pig Latin!
     *
     * @param phrase Phrase to translate to Pig Latin
     * @return Pig Latin sentence.
     * @see PigLatin.pigify
     */
    fun translate(phrase: String): String {
        return phrase.trim().lowercase(Locale.getDefault())
            .split(" ").joinToString(" ") { pigify(it) }
    }

    /**
     * @param word Word
     * @return Pig Latin word.
     * @see PigLatin.pigify
     */
    private fun pigify(word: String): String {
        return recRulesMatch(word, rulesMapper())
    }

    /**
     * @param word - word to translate
     * @param rulesMap - map of rule check to rule to apply on the word
     * @return pigified word
     */
    private tailrec fun recRulesMatch(word: String, rulesMap: MutableMap<RuleChecker, Rule>): String {
        if (rulesMap.isEmpty()) {
            return word
        }
        val ruleChecker: RuleChecker = rulesMap.keys.first()
        val ruleImpl: Rule = rulesMap.values.first()

        return when (ValidateRuleImpl.validateRule(word) { ruleChecker.check(it) }) {
            true -> {
                ruleImpl.invoke(word)
            }

            else -> {
                rulesMap.remove(ruleChecker)
                recRulesMatch(word, rulesMap)
            }
        }
    }
}
