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
        out.add("+7" + matcher.group().replace("[^0-9]".toRegex(), "").substring(1))
    }
    return out
}

fun String.md5(): String {
    val sb = StringBuilder()
    val md = MessageDigest.getInstance("MD5")
    md.update(this.toByteArray())
    val digest = md.digest()
    for (b in digest) {
        sb.append(String.format("%02x", b and 0xff.toByte()))
    }

    return sb.toString()
}
