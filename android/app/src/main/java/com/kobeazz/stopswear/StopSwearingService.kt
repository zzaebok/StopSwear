package com.kobeazz.stopswear

import android.accessibilityservice.AccessibilityService
import android.util.Log
import android.view.accessibility.AccessibilityEvent

class StopSwearingService : AccessibilityService(){
    lateinit private var swearClassifier: SwearClassifier
    lateinit private var jamo: Jamo

    private var isJamoInitialized = false
    private var isSwearClassifierInitialized = false

    companion object {
        private val TAG = "STOP_SWEARING_SERVICE"
    }

    override fun onInterrupt() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isJamoInitialized) {
            jamo = Jamo(this)
            isJamoInitialized = true
        }
        if (!isSwearClassifierInitialized) {
            swearClassifier = SwearClassifier(this)
            swearClassifier.initializeInterpreter()
            isSwearClassifierInitialized = true
        }

        Log.d(TAG, "accessibility event called")
        if (event?.eventType == AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED) {
            val rawText = event.text.toString()
            val text = rawText.substring(1, rawText.length-1)
            val inputString = jamo.stringToInput(text)
            swearClassifier.classify(inputString)
        }
    }

}