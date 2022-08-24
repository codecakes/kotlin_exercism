package main.kotlin

import PigLatin.translate
import RuleCheck1Impl

fun main() {
    println(RuleCheck1Impl.check("i"))
    println(translate("ear"))
    println(translate("I want to go to London"))
    println(translate("xray"))
    println(translate("yttria"))
}