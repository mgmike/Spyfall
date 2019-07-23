package mike.spyfall.ItemClasses

import android.widget.TextView

import java.util.ArrayList

/**
 * Created by Michael on 7/23/2017.
 */

class Location(val itemLocation: String) {
    var button: TextView? = null
        private set
    var alpha = false
        private set
    val roleList = ArrayList<String>()
    val repeatList = ArrayList<String>()

    var text: String
        get() = button!!.text.toString()
        set(text) {
            button!!.text = text
        }

    fun setTextView(button: TextView) {
        this.button = button
    }

    fun updateAlpha() {
        if (!alpha) {
            button!!.alpha = 0.6.toFloat()
            alpha = true
        } else {
            button!!.alpha = 1.toFloat()
            alpha = false
        }
    }

    fun addRole(r: String) {
        roleList.add(r)
    }

    fun addRepeat(r: String) {
        repeatList.add(r)
    }

    fun reset() {
        if (button != null) {
            alpha = false
            button!!.alpha = 1.toFloat()
        }
    }

    fun setAlpha() {
        if (!alpha) {
            button!!.alpha = 1.toFloat()
        } else {
            button!!.alpha = 0.6.toFloat()
        }
    }

    fun checkTextView(): Boolean {
        return if (button == null) {
            false
        } else {
            true
        }
    }

    fun updateText() {
        //if(button.getText() == null){
        button!!.text = itemLocation
        //}
    }

}
