@file:JvmName("StringUtils")

package motocitizen.utils

import java.security.MessageDigest
import java.util.*
import java.util.regex.Pattern
import kotlin.experimental.and

fun String.getPhonesFromText(): List<String> {
    val out = ArrayList<String>()
    val matcher = Pattern.compile("[7|8][ (-]?[\\d]{3}[ )-]?[\\d]{3}[ -]?[\\d]{2}[ -]?[\\d]{2}[\\D]").matcher(this + ".")
    while (matcher.find()) {
        out.add("+7" + matcher.group().replace("[^\\d]".toRegex(), "").substring(1))
    }
    return out
}

fun String.md5(): String {
    val sb = StringBuilder()
    val md = MessageDigest.getInstance("MD5")
    md.update(toByteArray())
    md.digest().forEach { sb.append(String.format("%02x", it and 0xff.toByte())) }

    return sb.toString()
}

fun String.carryOver(symbols: Array<Char> = arrayOf(' ', ',')): Pair<String, String> {
    val half = length / 2
    val max = symbols.map { this.lastIndexOf(it, half) }.max() ?: -1
    return when (max) {
        -1   -> Pair(this, "")
        else -> Pair(substring(0, max), substring(max + 1))
    }
}