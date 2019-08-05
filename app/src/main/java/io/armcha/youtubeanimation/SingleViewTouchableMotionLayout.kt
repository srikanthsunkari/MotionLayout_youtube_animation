package io.armcha.youtubeanimation

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.RecyclerView


/**
 *
 * Created by Arman Chatikyan on 05 Sep 2018
 *
 */

class SingleViewTouchableMotionLayout(context: Context, attributeSet: AttributeSet? = null) : MotionLayout(context, attributeSet) {
    val TAG = "SingleViewTouchable";
    private val viewToDetectTouch by lazy {
        findViewById<View>(R.id.videoViewContainer) //TODO move to Attributes
    }

    fun setWidth(value: Float) {
        layoutParams.width = value.toInt()
        Log.d(TAG,"width::" + value)
    }

    fun setHeight(value: Float) {
        layoutParams.height = value.toInt()
        Log.d(TAG,"width::" + value)
    }

    private val viewRect = Rect()
    private var touchStarted = false
    private val transitionListenerList = mutableListOf<TransitionListener?>()

    init {
        addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Log.d(TAG,"onTransitionChange p1:: $p1 p2 $p2 p3 $p3")
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.d(TAG,"onTransitionCompleted p1:: $p1")
                touchStarted = false
            }
        })

        super.setTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                Log.d(TAG,"onTransitionChange p1:: $p1 p2 $p2 p3 $p3")
                transitionListenerList.filterNotNull()
                        .forEach { it.onTransitionChange(p0, p1, p2, p3) }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                Log.d(TAG,"onTransitionCompleted p1:: $p1")
                transitionListenerList.filterNotNull()
                        .forEach { it.onTransitionCompleted(p0, p1) }
            }
        })
    }

    override fun setTransitionListener(listener: TransitionListener?) {
        addTransitionListener(listener)
    }

    fun addTransitionListener(listener: TransitionListener?) {
        transitionListenerList += listener
    }

    private val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
        override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
            transitionToEnd()
            return false
        }
    })

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event)
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                touchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!touchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            touchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return touchStarted && super.onTouchEvent(event)
    }
}