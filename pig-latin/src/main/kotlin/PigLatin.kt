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

const val QU = "qu"
const val SUFFIX = "ay"


sealed interface RuleChecker {

    fun check(word: String): Boolean
}

sealed interface RuleChecker1: RuleChecker {
    fun checkRule1(word: String): Boolean
}

sealed interface RuleChecker2: RuleChecker {
    fun checkRule2(word: String): Boolean
}

sealed interface RuleChecker3: RuleChecker {
    fun checkRule3(word: String): Boolean
}

sealed interface RuleChecker4: RuleChecker {
    fun checkRule4(word: String): Boolean
}


sealed interface Rule {

    fun invoke(word: String): String
}
sealed interface Rule1: Rule {
    fun addVowelAy(word: String): String
}

sealed interface Rule2: Rule {
    fun moveConsonantEnd(word: String): String
}

sealed interface Rule3: Rule {
    fun moveConsonantQu(word: String): String
}

sealed interface Rule4: Rule {
    fun makeVowelYAy(word: String): String
}


sealed interface ValidateRule<T: RuleChecker> {

    fun validateRule(word: String, ruleChecker: (String) -> Boolean): Boolean
}

sealed class ValidateRuleImpl<T: RuleChecker> {

    // Create a static method
    companion object: ValidateRule<RuleChecker>  {
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

object RuleCheck1Impl: RuleChecker1 {
    /**
     * @param word - word to check
     * @return true if word starts with vowel sound, false otherwise
     */
    override fun checkRule1(word: String): Boolean {
        return (word.length > 1 &&
                vowelSoundingInitialInWord.contains(word.first().toString()) &&
                !VOWELS.contains(word[1])
                )
    }

    /**
     * @param word - word to check
     * @return true if word starts with consonant sound, false otherwise
     */
    override fun check(word: String): Boolean {
        return checkRule1(word)
    }
}

object RuleCheck2Impl: CommonConsonantHelper(), RuleChecker2 {

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

object RuleCheck3Impl: CommonConsonantHelper(), RuleChecker3 {

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

object RuleCheck4Impl: CommonConsonantHelper(), RuleChecker4 {

    /**
     * @param word - word to check
     * @return true if word contains a "y" after a consonant cluster or
     *  as the second letter in a two-letter word it
     */
    override fun checkRule4(word: String): Boolean {
        val initialConsonants: MutableList<Char> = mutableListOf()
        val seqConsonant = commonConsonantCheckSequence(word)
        val isTwoLetterEndsWithY = word.length == 2 && word.last() == 'y'
        if (isTwoLetterEndsWithY) {
            return true
        }
        // Lazy load the char sequence of consonants if any.
        initialConsonants.addAll(0, seqConsonant.toList())
        if (initialConsonants.size > 0 && initialConsonants.contains('y')) {
            return true
        }
        return false
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


object Rule1Impl : Rule1 {
    override fun addVowelAy(word: String): String {
        return word + SUFFIX
    }

    override fun invoke(word: String): String {
        return addVowelAy(word)
    }
}

object Rule2Impl : Rule2 {
    override fun moveConsonantEnd(word: String): String {
        return word
    }

    override fun invoke(word: String): String {
        return moveConsonantEnd(word)
    }
}

object Rule3Impl : Rule3 {
    override fun moveConsonantQu(word: String): String {
        return word
    }

    override fun invoke(word: String): String {
        return moveConsonantQu(word)
    }
}

object Rule4Impl : Rule4 {
    override fun makeVowelYAy(word: String): String {
        return word
    }

    override fun invoke(word: String): String {
        return makeVowelYAy(word)
    }
}

internal fun ruleMapper(): Map<RuleChecker, Rule> = mapOf(
    RuleCheck4Impl to Rule4Impl,
    RuleCheck3Impl to Rule3Impl,
    RuleCheck2Impl to Rule2Impl,
    RuleCheck1Impl to Rule1Impl
)


/** Memoizing cache function helper
 * Could have done the easier way but makes for good practice.
 */
internal fun <
        K: KFunction0<Map<RuleChecker, Rule>>,
        V: Map<RuleChecker, Rule>> cacheFunction(
    cache: MutableMap<K, V>, theFunction: K): V {
    val safeMapping: Map<RuleChecker, Rule> = theFunction()
    return cache.getOrPut(theFunction) {
        @Suppress("UNCHECKED_CAST")
        safeMapping as V
    }
}


object PigLatin {

    private val cache: MutableMap<KFunction0<Map<RuleChecker, Rule>>, Map<RuleChecker, Rule>> = mutableMapOf()
    private fun rulesMapper(): MutableMap<RuleChecker, Rule>  = cacheFunction(cache, ::ruleMapper).toMutableMap()

//    private fun rulesMapper(): MutableMap<RuleChecker, Rule> = ruleMapper().toMutableMap()

    /**
     * Translates English to Pig Latin!
     *
     * @param word String a word
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
     * @see PigLatin.checkCluster
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
