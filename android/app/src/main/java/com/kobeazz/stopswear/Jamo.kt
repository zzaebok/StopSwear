package com.kobeazz.stopswear

import android.content.Context
import android.util.Log
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.math.floor


class Jamo(private val context: Context) {
    val regex = Regex("(?<=HANGUL )(\\w+)")
    val parser: Parser = Parser.default()

    val jamoToNameString: String
    val hcjToNameString: String
    val jamoToName: JsonObject
    val hcjToName: JsonObject
    val jamoToNameMap: Map<Char, String>
    val hcjToNameMap: Map<Char, String>
    val nameToHcjMap: Map<String, Char>

    companion object {
        private const val TAG = "Jamo"
    }

    init {
        Log.d(TAG, "Jamo initialize start")
        val assetManager = context.assets
        jamoToNameString = BufferedReader(InputStreamReader(assetManager.open("U+11xx.json"), "UTF-8")).use {it.readText()}
        hcjToNameString = BufferedReader(InputStreamReader(assetManager.open("U+31xx.json"), "UTF-8")).use {it.readText()}
        jamoToName = parser.parse(StringBuilder(jamoToNameString)) as JsonObject
        hcjToName = parser.parse(StringBuilder(hcjToNameString)) as JsonObject
        val tempJamoToNameMap = jamoToName as Map<String, String>
        jamoToNameMap = tempJamoToNameMap.entries.associate { (k, v) -> k.toCharArray()[0] to v }
        val tempHcjToNameMap = hcjToName as Map<String, String>
        hcjToNameMap = tempHcjToNameMap.entries.associate { (k, v) -> k.toCharArray()[0] to v }
        nameToHcjMap = hcjToNameMap.entries.associate { (k,v) -> v to k }
    }



    fun stringToInput(string: String): String {
        val jamos: MutableList<Char> = mutableListOf<Char>()
        for (char in string) {
            for (jamo in hangleCharToJamo(char)) {
                jamos.add(jamo)
            }
        }
        val jamosString = jamos.joinToString("")
        return jamoToHcj(jamosString).joinToString("")
    }

    private fun hangleCharToJamo(syllable: Char): List<Char>{
        if (0xAC00 <= syllable.toInt() && syllable.toInt() <= 0xD7A3) {
            val rem = syllable.toInt() - 44032
            val tail = rem % 28
            val vowel = floor(1 + ((rem - tail) % 588) / 28.0)
            val lead = floor(1 + rem / 588.0)
            if (tail != 0) {
                return listOf((lead + 0x10ff).toChar(), (vowel + 0x1160).toChar(), (tail + 0x11a7).toChar())
            } else {
                return listOf((lead + 0x10ff).toChar(), (vowel + 0x1160).toChar())
            }
        } else {
            return listOf(syllable)
        }
    }

    private fun jamoToHcj(string: String) : List<Char>{
        val hcj = mutableListOf<Char>()
        for (char in string) {
            if (isJamo(char.toInt())) {
                val hcjName = this.regex.replace(getUnicodeName(char) ?: "", "LETTER")
                if (hcjName in nameToHcjMap.keys) {
                    hcj.add(nameToHcjMap[hcjName]!!)
                }
            } else {
                hcj.add(char)
            }
        }
        return hcj
    }

    private fun isJamo(ascii: Int) : Boolean =
            when (ascii) {
                in 0x1100 .. 0x11FF -> true
                in 0xA960 .. 0xA97C -> true
                in 0xD7B0 .. 0xD7C6 -> true
                in 0xD7CB .. 0xD7FB-> true
                in 0x3131 .. 0x3163 -> true
                in 0x3165 .. 0x318E -> true
                else -> false
            }

    private fun getUnicodeName(char: Char): String? {
        if (char !in jamoToNameMap.keys && char !in hcjToNameMap.keys) {
            throw IllegalArgumentException("Not jamo or nameless jamo character: ${char}")
        } else {
            when (char.toInt()) {
                in 0x3131 .. 0x3163 -> return hcjToNameMap[char]
                in 0x3165 .. 0x318E -> return hcjToNameMap[char]
                else -> return jamoToNameMap[char]
            }
        }
    }
}