import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) :
    SQLiteOpenHelper(context, "banco.db", null, 2) {

    companion object {
        const val TABELA_ALUNOS = "alunos"
        const val ID = "id"
        const val NOME = "nome"
        const val NOTAS = "notas"
        const val PRESENCA = "presenca"
        const val TURMA = "turma"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABELA_ALUNOS (
                $ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $NOME TEXT,
                $NOTAS TEXT,
                $PRESENCA INTEGER,
                $TURMA TEXT
            )
        """.trimIndent()

        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABELA_ALUNOS")
        onCreate(db)
    }

    fun inserirAluno(nome: String, notas: String, presenca: Boolean, turma: String): Boolean {
        val db = writableDatabase
        val valores = ContentValues()

        valores.put(NOME, nome)
        valores.put(NOTAS, notas)
        valores.put(PRESENCA, if (presenca) 1 else 0)
        valores.put(TURMA, turma)

        val resultado = db.insert(TABELA_ALUNOS, null, valores)
        db.close()

        return resultado != -1L
    }

    // 🔹 LISTAR
    fun listarAlunos(): List<Aluno> {
        val lista = mutableListOf<Aluno>()
        val db = readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABELA_ALUNOS", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(NOME))
                val notas = cursor.getString(cursor.getColumnIndexOrThrow(NOTAS))
                val presenca = cursor.getInt(cursor.getColumnIndexOrThrow(PRESENCA)) == 1
                val turma = cursor.getString(cursor.getColumnIndexOrThrow(TURMA))

                val aluno = Aluno(id, nome, notas, presenca, turma)
                lista.add(aluno)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }

    // 🔹 RELATÓRIO POR TURMA
    fun listarPorTurma(turma: String): List<Aluno> {
        val lista = mutableListOf<Aluno>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABELA_ALUNOS WHERE $TURMA = ?",
            arrayOf(turma)
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow(ID))
                val nome = cursor.getString(cursor.getColumnIndexOrThrow(NOME))
                val notas = cursor.getString(cursor.getColumnIndexOrThrow(NOTAS))
                val presenca = cursor.getInt(cursor.getColumnIndexOrThrow(PRESENCA)) == 1

                val aluno = Aluno(id, nome, notas, presenca, turma)
                lista.add(aluno)

            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return lista
    }
}