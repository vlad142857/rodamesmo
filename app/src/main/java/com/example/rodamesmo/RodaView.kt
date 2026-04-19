package com.example.rodamesmo

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.sin

class RodaView(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private val paintEixoComum = Paint().apply {
        color = Color.LTGRAY
        strokeWidth = 12f
        style = Paint.Style.STROKE
    }
    private val paintEixoVermelho = Paint().apply {
        color = Color.RED
        strokeWidth = 15f
        style = Paint.Style.STROKE
    }
    private val paintPino = Paint().apply {
        color = Color.parseColor("#333333")
        style = Paint.Style.FILL
    }
    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 50f // Tamanho do texto
        typeface = android.graphics.Typeface.DEFAULT_BOLD
        textAlign = Paint.Align.CENTER
    }
    
    private var pinosList = IntArray(6) { 0 }
    
    fun setPinos(pinos: IntArray) {
        pinosList = pinos
        visibility = View.VISIBLE
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val cx = width / 2f
        val cy = height / 2f
        
        // Ajustado para os números não fugirem da tela
        val fixedLen = width * 0.28f 
        
        for (i in 0 until 6) {
            val ang = Math.toRadians((i * 60 - 90).toDouble())
            
            val x = (cx + cos(ang) * fixedLen).toFloat()
            val y = (cy + sin(ang) * fixedLen).toFloat()
            
            if (i == 0) {
                canvas.drawLine(cx, cy, x, y, paintEixoVermelho)
            } else {
                canvas.drawLine(cx, cy, x, y, paintEixoComum)
            }
            
            canvas.drawCircle(x, y, 15f, paintPino)
            
            // Posição do texto mais próxima do pino
            val tx = (cx + cos(ang) * (fixedLen + 55f)).toFloat()
            val ty = (cy + sin(ang) * (fixedLen + 55f)).toFloat()
            
            if (i == 0) paintText.color = Color.RED else paintText.color = Color.BLACK
            canvas.drawText(pinosList[i].toString(), tx, ty + 18f, paintText)
        }
        
        paintText.color = Color.BLACK
        canvas.drawCircle(cx, cy, 18f, paintText)
    }
}