package com.example.rodamesmo

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale
import kotlin.math.ceil
import kotlin.math.max

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editCircunferenciaDesejada = findViewById<EditText>(R.id.editCircunferenciaDesejada)
        val editVoltas = findViewById<EditText>(R.id.editVoltas)
        val editGrade = findViewById<EditText>(R.id.editGrade)
        val editPedido = findViewById<EditText>(R.id.editPedido)
        
        val btnDistribuir = findViewById<Button>(R.id.btnDistribuir)
        
        val txtSugestaoEixos = findViewById<TextView>(R.id.txtSugestaoEixos)
        val txtCircunferenciaReal = findViewById<TextView>(R.id.txtCircunferenciaReal)
        val txtPecasBobina = findViewById<TextView>(R.id.txtPecasBobina)
        val txtQtdBobinas = findViewById<TextView>(R.id.txtQtdBobinas)

        // Calibragem: 9.00m = 25 pinos (A:5, B-F:4)
        val TAMANHO_BASE_TOTAL = 7.75
        val TAMANHO_BASE_EIXO = TAMANHO_BASE_TOTAL / 6.0 

        btnDistribuir.setOnClickListener {
            val desejadaStr = editCircunferenciaDesejada.text.toString()
            val voltasStr = editVoltas.text.toString()
            val gradeStr = editGrade.text.toString()
            val pedidoStr = editPedido.text.toString()
            
            if (desejadaStr.isNotEmpty()) {
                val target = desejadaStr.toDouble()
                val voltas = if (voltasStr.isNotEmpty()) voltasStr.toDouble() else 0.0
                val grade = if (gradeStr.isNotEmpty()) gradeStr.toDouble() else 0.0
                val pedido = if (pedidoStr.isNotEmpty()) pedidoStr.toDouble() else 0.0
                
                val sobraParaPinos = max(0.0, target - TAMANHO_BASE_TOTAL)
                
                // 1. Calcula total de pinos
                val totalPinos = ceil((sobraParaPinos - 0.0001) / 0.05).toInt()
                val circunferenciaReal = TAMANHO_BASE_TOTAL + (totalPinos * 0.05)
                
                // 2. Distribuição Simétrica
                val pinosBasePorEixo = totalPinos / 6
                val pinosExtras = totalPinos % 6
                val ordemSimetrica = intArrayOf(0, 3, 1, 4, 2, 5)
                val temExtra = BooleanArray(6) { false }
                for (i in 0 until pinosExtras) {
                    temExtra[ordemSimetrica[i]] = true
                }
                
                val nomes = arrayOf("A", "B", "C", "D", "E", "F")
                val sb = StringBuilder()
                for (i in 0 until 6) {
                    val p = if (temExtra[i]) pinosBasePorEixo + 1 else pinosBasePorEixo
                    val metros = TAMANHO_BASE_EIXO + (p * 0.05)
                    sb.append("Eixo %s: %d pinos (%.2f m)\n".format(Locale.getDefault(), nomes[i], p, metros))
                }
                
                txtSugestaoEixos.text = sb.toString().trim()
                
                val excessoCm = (circunferenciaReal - target) * 100
                txtCircunferenciaReal.text = String.format(Locale.getDefault(), 
                    "Circunferência Real: %.2f m\n(Excesso: %.1f cm)",
                    circunferenciaReal, if (excessoCm < 0.01) 0.0 else excessoCm)
                
                // 3. Cálculos de Produção
                val pecasPorBobina = grade * voltas
                txtPecasBobina.text = String.format(Locale.getDefault(),
                    "Qtde de Peças por Bobina: %.0f", pecasPorBobina)

                if (pecasPorBobina > 0) {
                    val qtdBobinas = pedido / pecasPorBobina
                    txtQtdBobinas.text = String.format(Locale.getDefault(),
                        "Qtde de Bobinas a Enfestar: %.2f", qtdBobinas)
                } else {
                    txtQtdBobinas.text = "Qtde de Bobinas a Enfestar: 0"
                }
            }
        }
    }
}