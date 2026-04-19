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
        strokeWidth = 8f
        style = Paint.Style.STROKE
    }
    private val paintEixoVermelho = Paint().apply {
        color = Color.RED
        strokeWidth = 10f
        style = Paint.Style.STROKE
    }
    private val paintPino = Paint().apply {
        color = Color.parseColor("#333333")
        style = Paint.Style.FILL
    }
    private val paintText = Paint().apply {
        color = Color.BLACK
        textSize = 34f
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
        
        for (i in 0 until 6) {
            val ang = Math.toRadians((i * 60 - 90).toDouble())
            val len = 60f + (pinosList[i] * 6f)
            
            val x = (cx + cos(ang) * len).toFloat()
            val y = (cy + sin(ang) * len).toFloat()
            
            // Desenha o eixo (Vermelho para o primeiro, Cinza para os outros)
            if (i == 0) {
                canvas.drawLine(cx, cy, x, y, paintEixoVermelho)
            } else {
                canvas.drawLine(cx, cy, x, y, paintEixoComum)
            }
            
            // Desenha o pino
            canvas.drawCircle(x, y, 10f, paintPino)
            
            // Desenha o número de pinos livres
            val tx = (cx + cos(ang) * (len + 35f)).toFloat()
            val ty = (cy + sin(ang) * (len + 35f)).toFloat()
            
            // Se for o eixo vermelho, destaca o número em vermelho também
            if (i == 0) paintText.color = Color.RED else paintText.color = Color.BLACK
            canvas.drawText(pinosList[i].toString(), tx, ty + 12f, paintText)
        }
        
        // Centro da roda
        paintText.color = Color.BLACK
        canvas.drawCircle(cx, cy, 12f, paintText)
    }
}