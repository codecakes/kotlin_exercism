object Raindrops {

    fun convert(n: Int): String {
        var s = if (isFactor(n, 3)) "Pling" else ""
        s += if (isFactor(n, 5)) "Plang" else ""
        s += if (isFactor(n, 7)) "Plong" else ""
        return if (s.isEmpty()) n.toString() else s
    }

    private fun isFactor(n: Int, factorByNum: Int): Boolean {
        return n.rem(factorByNum) == 0
    }
}
