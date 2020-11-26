package com.kobeazz.stopswear

import android.content.Context
import android.content.res.AssetManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Toast
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class SwearClassifier(private val context: Context) {
    val vocabList: List<Char>
    val vocabDict: HashMap<Char, Int>
    private var interpreter: Interpreter? = null
    private var jamo: Jamo
    private val UNK = 0
    private val PAD: Int
    var isInitialized = false
        private set

    private var inputLength: Int = 0 // will be inferred from TF lite model

    companion object {
        private const val TAG = "SWEAR_CLASSIFIER"
        private const val MODEL_FILE = "model.tflite"
    }

    init {
        Log.d(TAG, "SwearClassifier initialized")
        jamo = Jamo(context)
        vocabList = listOf('ㄷ', 'ㅏ', 'ㄴ', 'ㄱ', 'ㅜ', 'ㅎ', 'ㄹ', 'ㅇ', 'ㅂ', 'ㅓ', 'ㅈ', 'ㅣ', ' ',
                'ㅡ', 'ㅢ', 'ㅁ', 'ㅗ', 'ㅅ', 'ㅔ', 'ㅕ', 'ㅑ', ';', 'B', 'J', '.', 'P', 'G',
                'ㄸ', 'ㅟ', 'ㅃ', 'ㅌ', '[', '1', ':', '8', '2', '3', '0', ']', 'V', 'L',
                'I', 'E', 'ㅋ', 'ㅖ', '(', 'ㅠ', ')', '5', 'ㅝ', 'ㅐ', 'ㅆ', '\'', 'ㅀ', 'ㅊ',
                't', 'x', 'ㅙ', 'ㅚ', 'ㅉ', 'ㅍ', 'ㅄ', '?', 'g', 'i', 'f', 'ㅛ', '6', '7', '☀',
                'ㄲ', 'v', 's', 'ㅘ', '!', 'ㄶ', 'p', 'c', 'ㄼ', '\u3000', 'k', '4', '9', ',',
                'ㅞ', 'ㅒ', '“', '”', 'N', '‘', '’', 'T', 'O', 'a', 'r', 'm', 'S', '+', 'o', 'd',
                'l', 'u', '·', '~', '/', 'ㄻ', '^', 'ㄺ', 'e', 'n', 'A', '-', 'D', '&', 'C',
                'F', 'j', 'M', 'K', '"', '_', 'Z', 'X', 'U', '…', 'ㄾ', 'w', '=', 'z',
                '>', '<', 'b', 'H', '@', '*', 'W', 'y', 'h', 'R', '%', 'ㄽ', '．',
                'ｊ', 'ｐ', 'ｇ', 'ㄵ', '{', '}', 'q', 'Y', 'Q',
                '$', 'ㄿ', '？', 'ㆍ', 'ㄳ', '⋅', '—')
        vocabDict = HashMap<Char, Int>()
        this.vocabList.forEachIndexed {
            i, element -> vocabDict.put(element, i+1)
        }
        PAD = vocabList.size + 1
        Log.d(TAG, "SwearClassifier initialized finished")
    }

    fun initializeInterpreter() {
        Log.d(TAG, "Initialized TFLite interpreter.")
        val assetManager = context.assets
        val model = loadModelFile(assetManager)

        val options = Interpreter.Options()
        options.setUseNNAPI(true)
        val interpreter = Interpreter(model, options)

        val inputShape = interpreter.getInputTensor(0).shape()
        inputLength = inputShape[1]
        Log.d(TAG, inputLength.toString())

        this.interpreter = interpreter
        isInitialized = true
    }

    private fun loadModelFile(assetManager: AssetManager): ByteBuffer {
        val fileDescriptor = assetManager.openFd("model.tflite")
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    fun classify(string: String) {
        Log.d(TAG, "SwearClassifier classify function is called")
        if (!isInitialized) {
            throw IllegalStateException("TF Lite interpreter is not initialized yet.")
        }
        val output = Array(1) { FloatArray(2) }
        val inputArray = preprocessing(string)
        Log.d(TAG, "inputArray: " + inputArray.toString())
        interpreter?.run(inputArray, output)
        getOutputString(output)
    }

    private fun getOutputString(output: Array<FloatArray>) {
        val maxIndex = output[0].indices.maxBy { output[0][it] }
        if (maxIndex == 1) {
            Toast.makeText(context, "나쁜말!", Toast.LENGTH_SHORT).show()
            val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, 20))
            } else {
                vibrator.vibrate(500)
            }
        }
    }

    private fun preprocessing(string: String): Array<LongArray> {
        val inputList: MutableList<Long> = mutableListOf<Long>()
        for (char in string) {
            if (char in vocabDict.keys) {
                inputList.add(vocabDict[char]!!.toLong())
            } else {
                inputList.add(UNK.toLong())
            }
        }
        for (i in 1..(inputLength - inputList.size)) {
            inputList.add(PAD.toLong())
        }
        Log.d(TAG, "inputList: " + inputList.toString())
        val inputArray: Array<LongArray> = Array(1) { LongArray(inputLength) }
        inputArray[0] = inputList.toLongArray()
        return inputArray
    }

}