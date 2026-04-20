package com.example.rodamesmo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("RodaMesmoPrefs", MODE_PRIVATE)

        val editCircDesejada = findViewById<EditText>(R.id.editCircunferenciaDesejada)
        val editGrade = findViewById<EditText>(R.id.editGrade)
        val editVoltas = findViewById<EditText>(R.id.editVoltas)
        val editPedido = findViewById<EditText>(R.id.editPedido)
        
        val btnDistribuir = findViewById<Button>(R.id.btnDistribuir)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val btnSalvarHist = findViewById<Button>(R.id.btnSalvarHistorico)
        val btnLimparHist = findViewById<Button>(R.id.btnLimparHistorico)
        
        val txtCircReal = findViewById<TextView>(R.id.txtCircReal)
        val txtPecasBobina = findViewById<TextView>(R.id.txtPecasBobina)
        val txtMetrosBobina = findViewById<TextView>(R.id.txtMetrosBobina)
        val txtQtdBobinas = findViewById<TextView>(R.id.txtQtdBobinas)
        val txtHistorico = findViewById<TextView>(R.id.txtHistorico)
        val rodaView = findViewById<RodaView>(R.id.rodaView)

        // Elementos do Lançamento Real
        val containerFolhasReais = findViewById<LinearLayout>(R.id.containerFolhasReais)
        val listaInputsFolhas = findViewById<LinearLayout>(R.id.listaInputsFolhas)
        val txtTotalFolhasReais = findViewById<TextView>(R.id.txtTotalFolhasReais)

        // CARREGAR DADOS SALVOS
        editCircDesejada.setText(prefs.getString("last_circ", ""))
        editGrade.setText(prefs.getString("last_grade", ""))
        editVoltas.setText(prefs.getString("last_voltas", ""))
        editPedido.setText(prefs.getString("last_pedido", ""))
        txtHistorico.text = prefs.getString("historico_txt", "Nenhum registro salvo.")

        fun atualizarSomaReal() {
            var soma = 0
            for (i in 0 until listaInputsFolhas.childCount) {
                val input = listaInputsFolhas.getChildAt(i) as? EditText
                soma += input?.text.toString().toIntOrNull() ?: 0
            }
            txtTotalFolhasReais.text = "TOTAL REAL DE FOLHAS: $soma"
        }

        btnDistribuir.setOnClickListener {
            val sCirc = editCircDesejada.text.toString()
            val sGrade = editGrade.text.toString()
            val sVoltas = editVoltas.text.toString()
            val sPedido = editPedido.text.toString()

            val targetDesejado = sCirc.toDoubleOrNull() ?: 0.0
            val voltas = sVoltas.toDoubleOrNull() ?: 0.0
            val grade = sGrade.toDoubleOrNull() ?: 1.0
            val pedido = sPedido.toDoubleOrNull() ?: 0.0
            
            if (targetDesejado > 0) {
                val targetArredondado = Math.ceil(targetDesejado / 0.05) * 0.05
                val totalPinos = Math.round((10.25 - targetArredondado) / 0.05).toInt()
                
                val pinosBase = totalPinos / 6
                val pinosExtras = totalPinos % 6
                val ordem = intArrayOf(0, 3, 1, 4, 2, 5)
                val extras = BooleanArray(6) { false }
                for (i in 0 until pinosExtras) extras[ordem[i]] = true
                val listaPinosParaGrafico = IntArray(6) { i -> if (extras[i]) pinosBase + 1 else pinosBase }
                
                txtCircReal.text = String.format(Locale.getDefault(), "Circunferência Real: %.3f m", targetArredondado)
                rodaView.visibility = View.VISIBLE
                rodaView.setPinos(listaPinosParaGrafico)

                val pecasPorBobina = grade * voltas
                val metrosPorBobina = targetArredondado * voltas
                val bobinasAEnfestar = if (pecasPorBobina > 0) pedido / pecasPorBobina else 0.0

                txtPecasBobina.text = String.format(Locale.getDefault(), "1. Peças por Bobina: %.0f", pecasPorBobina)
                txtMetrosBobina.text = String.format(Locale.getDefault(), "2. Metros por Bobina: %.2f m", metrosPorBobina)
                txtQtdBobinas.text = String.format(Locale.getDefault(), "3. Bobinas a Enfestar: %.2f", bobinasAEnfestar)

                // CRIAR FORMULÁRIO DE FOLHAS REAIS
                val numBobinasInt = Math.ceil(bobinasAEnfestar).toInt()
                listaInputsFolhas.removeAllViews()
                if (numBobinasInt > 0) {
                    containerFolhasReais.visibility = View.VISIBLE
                    for (i in 1..numBobinasInt) {
                        val editFolha = EditText(this).apply {
                            hint = "Folhas na Bobina $i"
                            inputType = android.text.InputType.TYPE_CLASS_NUMBER
                            setText(voltas.toInt().toString()) // Sugere o valor calculado
                            addTextChangedListener(object : TextWatcher {
                                override fun afterTextChanged(s: Editable?) { atualizarSomaReal() }
                                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                            })
                        }
                        listaInputsFolhas.addView(editFolha)
                    }
                    atualizarSomaReal()
                } else {
                    containerFolhasReais.visibility = View.GONE
                }

                btnSalvarHist.visibility = View.VISIBLE
                btnSalvarHist.setOnClickListener {
                    val sdf = SimpleDateFormat("dd/MM HH:mm", Locale.getDefault())
                    val dataStr = sdf.format(Date())
                    val novoRegistro = "[$dataStr] Circ: ${targetArredondado}m | Bobinas: ${String.format(Locale.getDefault(), "%.2f", bobinasAEnfestar)}\n"
                    val historicoNovo = novoRegistro + (prefs.getString("historico_txt", "") ?: "")
                    prefs.edit().putString("historico_txt", historicoNovo).apply()
                    txtHistorico.text = historicoNovo
                    btnSalvarHist.visibility = View.GONE
                }
            }
        }

        btnReset.setOnClickListener {
            editCircDesejada.setText(""); editVoltas.setText(""); editGrade.setText(""); editPedido.setText("")
            txtCircReal.text = ""; txtPecasBobina.text = ""; txtMetrosBobina.text = ""; txtQtdBobinas.text = ""
            rodaView.visibility = View.GONE
            btnSalvarHist.visibility = View.GONE
            containerFolhasReais.visibility = View.GONE
            listaInputsFolhas.removeAllViews()
        }

        btnLimparHist.setOnClickListener {
            prefs.edit().remove("historico_txt").apply()
            txtHistorico.text = "Nenhum registro salvo."
        }
    }
}