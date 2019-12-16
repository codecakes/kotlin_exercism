object Isogram {

    fun isIsogram(input: String): Boolean {
        val diff = mutableMapOf<Char, Int>()
        input.trim().filter {
            it != ' ' && it != '.' && it != '-' && it != '_'
        }.toLowerCase().let {
            return when (it.length) {
                0 -> true
                1 -> false
                else -> {
                    it.forEachIndexed { index, c ->
                        if (diff.getOrDefault(c, -1) != -1) {
                            return false
                        } else diff[c] = index
                    }
                    true
                }
            }
        }
    }
}
