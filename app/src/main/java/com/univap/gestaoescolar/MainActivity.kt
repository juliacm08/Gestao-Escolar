package com.univap.gestaoescolar

import SQLiteHelper
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var banco: SQLiteHelper
    private lateinit var edtNome: EditText
    private lateinit var edtNotas: EditText
    private lateinit var checkPresenca: CheckBox
    private lateinit var checkFalta: CheckBox
    private lateinit var botao: Button
    private lateinit var txtRelatorio: TextView
    private lateinit var spinnerTurma: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        banco = SQLiteHelper(this)

        edtNome = findViewById(R.id.editCadastro)
        edtNotas = findViewById(R.id.editNotasMultiline)
        checkPresenca = findViewById(R.id.checkPresenca)
        checkFalta = findViewById(R.id.checkFalta)
        botao = findViewById(R.id.button)
        txtRelatorio = findViewById(R.id.textRelatorio)
        spinnerTurma = findViewById(R.id.spinnerTurma)

        val turmas = arrayOf("1A", "1B", "2A", "2B", "3A", "3B")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, turmas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTurma.adapter = adapter

        botao.setOnClickListener {

            val nome = edtNome.text.toString()
            val notas = edtNotas.text.toString()
            val turma = spinnerTurma.selectedItem.toString()

            var presenca = false
            if (checkPresenca.isChecked) {
                presenca = true
            }

            // 🔹 VALIDAÇÕES
            if (nome == "" || notas == "") {
                Toast.makeText(this, "Preencha os campos", Toast.LENGTH_SHORT).show()

            }
            else if (!nome.matches(Regex("^[\\p{L} ]+$"))) {
                Toast.makeText(this, "Nome inválido (use apenas letras)", Toast.LENGTH_SHORT).show()

            }
            else if (notas.any { it.isLetter() }) {
                Toast.makeText(this, "Notas devem ser números", Toast.LENGTH_SHORT).show()

            }
            else {

                val resultado = banco.inserirAluno(nome, notas, presenca, turma)

                if (resultado) {
                    Toast.makeText(this, "Aluno salvo!", Toast.LENGTH_SHORT).show()

                    edtNome.setText("")
                    edtNotas.setText("")
                    checkPresenca.isChecked = false
                    checkFalta.isChecked = false
                } else {
                    Toast.makeText(this, "Erro ao salvar", Toast.LENGTH_SHORT).show()
                }

                // 🔹 RELATÓRIO
                val lista = banco.listarPorTurma(turma)

                var texto = "Relatório da turma $turma:\n\n"

                for (aluno in lista) {
                    texto += aluno.nome + " - " +
                            aluno.notas + " - " +
                            if (aluno.presenca) "Presente" else "Falta"
                    texto += "\n"
                }

                txtRelatorio.text = texto
            }
        }
    }
}