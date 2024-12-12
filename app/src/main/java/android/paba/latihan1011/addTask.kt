package android.paba.latihan1011

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class addTask : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_task)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val _editDeskripsi = findViewById<EditText>(R.id.editDeskripsi)
        val _editTanggal = findViewById<EditText>(R.id.editTanggal)
        val _editKategori = findViewById<EditText>(R.id.editKategori)
        val _editNama = findViewById<EditText>(R.id.editNama)
        val _btnSimpan = findViewById<Button>(R.id.btnSimpan)

        val isEditMode = intent.getBooleanExtra("editMode", false)
        val task = intent.getParcelableExtra<tasklist>("task")
        val taskIndex = intent.getIntExtra("taskIndex", -1)

        if (isEditMode){
            title = "Edit Task"
            task?.let {
                _editNama.setText(it.judul)
                _editTanggal.setText(it.tanggal)
                _editKategori.setText(it.kategori)
                _editDeskripsi.setText(it.deskripsi)
            }
        } else {
            title = "Add Task"
        }

        _btnSimpan.setOnClickListener {
            val nama = _editNama.text.toString().trim()
            val tanggal = _editTanggal.text.toString().trim()
            val kategori = _editKategori.text.toString().trim()
            val deskripsi = _editDeskripsi.text.toString().trim()

            // Validasi input
            var isValid = true
            if (nama.isEmpty()) {
                _editNama.error = "Nama tugas harus diisi"
                isValid = false
            }
            if (tanggal.isEmpty()) {
                _editTanggal.error = "Tanggal harus diisi"
                isValid = false
            }
            if (kategori.isEmpty()) {
                _editKategori.error = "Kategori harus diisi"
                isValid = false
            }
            if (deskripsi.isEmpty()) {
                _editDeskripsi.error = "Deskripsi harus diisi"
                isValid = false
            }

            if (isValid) {
                val updatedTask = tasklist(
                    judul = nama,
                    tanggal = tanggal,
                    kategori = kategori,
                    deskripsi = deskripsi
                )

                val resultIntent = Intent()
                resultIntent.putExtra("updatedTask", updatedTask)
                if (isEditMode) resultIntent.putExtra("taskIndex", taskIndex)

                setResult(RESULT_OK, resultIntent)
                finish()
            }
        }

    }
}