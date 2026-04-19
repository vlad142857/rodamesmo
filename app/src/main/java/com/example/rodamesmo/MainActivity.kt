package com.example.rodamesmo

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editCircDesejada = findViewById<EditText>(R.id.editCircunferenciaDesejada)
        val editGrade = findViewById<EditText>(R.id.editGrade)
        val editVoltas = findViewById<EditText>(R.id.editVoltas)
        val editPedido = findViewById<EditText>(R.id.editPedido)
        
        val btnDistribuir = findViewById<Button>(R.id.btnDistribuir)
        val btnReset = findViewById<Button>(R.id.btnReset)
        
        val txtCircReal = findViewById<TextView>(R.id.txtCircReal)
        
        val txtPecasBobina = findViewById<TextView>(R.id.txtPecasBobina)
        val txtMetrosBobina = findViewById<TextView>(R.id.txtMetrosBobina)
        val txtQtdBobinas = findViewById<TextView>(R.id.txtQtdBobinas)
        val rodaView = findViewById<RodaView>(R.id.rodaView)

        btnDistribuir.setOnClickListener {
            val targetDesejado = editCircDesejada.text.toString().toDoubleOrNull() ?: 0.0
            val voltas = editVoltas.text.toString().toDoubleOrNull() ?: 0.0
            val grade = editGrade.text.toString().toDoubleOrNull() ?: 1.0
            val pedido = editPedido.text.toString().toDoubleOrNull() ?: 0.0
            
            if (targetDesejado > 0) {
                // ARREDONDAMENTO PARA CIMA (Múltiplo de 5cm)
                val targetArredondado = Math.ceil(targetDesejado / 0.05) * 0.05
                
                // LÓGICA DE COMPENSAÇÃO (Invertida)
                val totalPinos = Math.round((10.25 - targetArredondado) / 0.05).toInt()
                val circPecaReal = targetArredondado
                
                // Distribuição (Pino extra prioridade Eixo Vermelho)
                val pinosBase = totalPinos / 6
                val pinosExtras = totalPinos % 6
                val ordem = intArrayOf(0, 3, 1, 4, 2, 5)
                val extras = BooleanArray(6) { false }
                for (i in 0 until pinosExtras) extras[ordem[i]] = true
                
                val listaPinosParaGrafico = IntArray(6)
                for (i in 0 until 6) {
                    listaPinosParaGrafico[i] = if (extras[i]) pinosBase + 1 else pinosBase
                }
                
                // Mostrar resultados
                txtCircReal.text = String.format(Locale.getDefault(), "Circunferência Real da Peça: %.3f m", circPecaReal)
                rodaView.visibility = View.VISIBLE
                rodaView.setPinos(listaPinosParaGrafico)

                // OS 3 CÁLCULOS
                val pecasPorBobina = grade * voltas
                txtPecasBobina.text = String.format(Locale.getDefault(), "1. Peças por Bobina: %.0f", pecasPorBobina)
                txtMetrosBobina.text = String.format(Locale.getDefault(), "2. Metros por Bobina: %.2f m", circPecaReal * voltas)

                if (pecasPorBobina > 0) {
                    txtQtdBobinas.text = String.format(Locale.getDefault(), "3. Bobinas a Enfestar: %.2f", pedido / pecasPorBobina)
                }
            }
        }

        btnReset.setOnClickListener {
            editCircDesejada.setText("")
            editVoltas.setText("")
            editGrade.setText("")
            editPedido.setText("")
            txtCircReal.text = ""
            txtPecasBobina.text = ""
            txtMetrosBobina.text = ""
            txtQtdBobinas.text = ""
            rodaView.visibility = View.GONE
            // Limpa o desenho interno da roda também
            rodaView.setPinos(intArrayOf(0, 0, 0, 0, 0, 0))
        }
    }
}