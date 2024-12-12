package android.paba.latihan1011

import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {
    private var arTask = arrayListOf<tasklist>()
    private lateinit var _rwTask: RecyclerView
    private lateinit var adapterTask: adapterRecView

    lateinit var sp : SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("dataSP", MODE_PRIVATE)
        _rwTask = findViewById<RecyclerView>(R.id.rvJadwal)

        loadTasks()
        var _tambah = findViewById<FloatingActionButton>(R.id.fab)
        _tambah.setOnClickListener {
            val intent = Intent(this@MainActivity, addTask::class.java)
            startActivityForResult(intent, 1) // Gunakan startActivityForResult
        }

        TampilkanData()

    }
    private fun loadTasks() {
        val gson = Gson()
        val isiSP = sp.getString("spTask", null)
        val type = object : TypeToken<ArrayList<tasklist>>() {}.type
        if (isiSP != null) {
            arTask = gson.fromJson(isiSP, type)
        }

        // Load favorites
        arTask.addAll(loadFavoriteTasks())
    }

    private fun loadFavoriteTasks(): ArrayList<tasklist> {
        val gson = Gson()
        val spFavorit = sp.getStringSet("favoritTasks", mutableSetOf()) ?: mutableSetOf()
        val favoriteTasks = ArrayList<tasklist>()

        for (taskJson in spFavorit) {
            val task = gson.fromJson(taskJson, tasklist::class.java)
            if (!arTask.contains(task)) {
                favoriteTasks.add(task)
            }
        }
        return favoriteTasks
    }

    fun TampilkanData() {
        _rwTask.layoutManager = LinearLayoutManager(this)
        adapterTask = adapterRecView(arTask,sp)
        _rwTask.adapter = adapterTask

        adapterTask.setOnItemClickCallback(object : adapterRecView.OnItemClickCallback {

            override fun delData(pos: Int) {
                AlertDialog.Builder(this@MainActivity)
                    .setTitle("HAPUS DATA")
                    .setMessage("Apakah Benar Data " + arTask[pos].judul + " akan dihapus ?")
                    .setPositiveButton(
                        "HAPUS", DialogInterface.OnClickListener { dialog, which ->
                            arTask.removeAt(pos)
                            TampilkanData() // Beri tahu adapter bahwa item telah dihapus
                        }
                    )
                    .setNegativeButton(
                        "BATAL", DialogInterface.OnClickListener { dialog, which ->
                            Toast.makeText(
                                this@MainActivity,
                                "Data Batal Dihapus",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    ).show()
            }

            override fun editData(pos: Int) {
                val intent = Intent(this@MainActivity, addTask::class.java)
                intent.putExtra("task", arTask[pos])
                intent.putExtra("editMode", true)
                intent.putExtra("taskIndex", pos)
                startActivityForResult(intent, 2)

            }

            override fun completeTask(pos: Int) {
                arTask.removeAt(pos)
                TampilkanData()
                Toast.makeText(this@MainActivity, "Task selesai!", Toast.LENGTH_SHORT).show()
            }


        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            val updatedTask = data.getParcelableExtra<tasklist>("updatedTask")
            val taskIndex = data.getIntExtra("taskIndex", -1)

            when (requestCode) {
                1 -> { // Tambah task
                    if (updatedTask != null) {
                        arTask.add(updatedTask)
                        adapterTask.notifyItemInserted(arTask.size - 1)
                    }
                }

                2 -> { // Edit task
                    if (updatedTask != null && taskIndex != -1) {
                        arTask[taskIndex] = updatedTask
                        adapterTask.notifyItemChanged(taskIndex)
                    }
                }
            }
        }
    }
}