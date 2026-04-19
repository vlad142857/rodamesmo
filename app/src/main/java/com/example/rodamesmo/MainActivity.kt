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
        val btnDistribuir = findViewById<Button>(R.id.btnDistribuir)
        val txtSugestaoEixos = findViewById<TextView>(R.id.txtSugestaoEixos)
        val txtCircunferenciaReal = findViewById<TextView>(R.id.txtCircunferenciaReal)

        // Com base na informação do usuário: 9m = A5, B4, C4, D4, E4, F4 (25 pinos)
        // Circ = 6*Base + 25*0.05 -> 9.05 = 6*1.30 + 1.25.
        val TAMANHO_BASE_EIXO = 1.30 

        btnDistribuir.setOnClickListener {
            val desejadaStr = editCircunferenciaDesejada.text.toString()
            
            if (desejadaStr.isNotEmpty()) {
                val target = desejadaStr.toDouble()
                
                val circunferenciaMinima = TAMANHO_BASE_EIXO * 6
                val sobraParaPinos = max(0.0, target - circunferenciaMinima)
                
                // Calcula total de pinos (passos de 5cm) - Regra: Nunca menor que o target
                // Sem margem extra: se der 9.00 certinho, ele mantém os pinos exatos.
                val totalPinos = ceil(sobraParaPinos / 0.05).toInt()
                val circunferenciaReal = circunferenciaMinima + (totalPinos * 0.05)
                
                // Distribuição Simétrica (Pares A-D, B-E, C-F)
                val pinosBasePorEixo = totalPinos / 6
                val pinosExtras = totalPinos % 6
                
                val ordemSimetrica = intArrayOf(0, 3, 1, 4, 2, 5) // Prioridade: A, D, B, E, C, F
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
                
                val passouCm = (circunferenciaReal - target) * 100
                txtCircunferenciaReal.text = String.format(Locale.getDefault(), 
                    "Circunferência Real: %.2f m\n(Excesso: %.1f cm)",
                    circunferenciaReal, passouCm)
            }
        }
    }
}