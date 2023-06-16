package com.example.batikcapstone.customview

import android.content.Context
import android.text.Layout
import android.util.AttributeSet
import android.view.ViewGroup.LayoutParams
import androidx.appcompat.widget.AppCompatTextView

class JustifiedTextView(context: Context, attrs: AttributeSet) : AppCompatTextView(context, attrs) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // Check if the text view width is set to match_parent or a specific width
        if (layoutParams.width == LayoutParams.MATCH_PARENT || layoutParams.width > 0) {
            val width = measuredWidth
            val textLayout = layout

            // Check if the text needs justification
            if (textLayout != null && textLayout.width > 0 && textLayout.width < width) {
                justifyTextLayout(textLayout, width)
            }
        }
    }

    private fun justifyTextLayout(textLayout: Layout, width: Int) {
        val lineCount = textLayout.lineCount
        val lines = mutableListOf<CharSequence>()

        // Retrieve the text lines from the layout
        for (i in 0 until lineCount) {
            val lineStart = textLayout.getLineStart(i)
            val lineEnd = textLayout.getLineEnd(i)
            val line = text.subSequence(lineStart, lineEnd)
            lines.add(line)
        }

        // Calculate the width of each line to distribute extra space evenly
        val lineWidths = IntArray(lineCount)
        var totalWidth = 0
        for (i in 0 until lineCount) {
            val lineWidth = textLayout.getLineWidth(i).toInt()
            lineWidths[i] = lineWidth
            totalWidth += lineWidth
        }

        // Calculate the extra space to distribute
        val extraSpace = width - totalWidth
        val spaceToAddPerLine = extraSpace / (lineCount - 1)
        val spaceToAddRemainder = extraSpace % (lineCount - 1)

        // Justify each line by adding extra space
        val justifiedText = StringBuilder()
        for (i in 0 until lineCount) {
            justifiedText.append(lines[i])

            // Add extra space to the line
            if (i < lineCount - 1) {
                val spaceCount = if (i < spaceToAddRemainder) {
                    spaceToAddPerLine + 1
                } else {
                    spaceToAddPerLine
                }
                for (j in 0 until spaceCount) {
                    justifiedText.append(' ')
                }
            }
        }

        // Set the justified text to the text view
        text = justifiedText.toString()
    }
}