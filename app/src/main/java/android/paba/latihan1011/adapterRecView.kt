package android.paba.latihan1011

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson


class adapterRecView (
    private val listTask: ArrayList<tasklist>,
    private val sharedPreferences: SharedPreferences
    ) : RecyclerView.Adapter<adapterRecView.ListViewHolder>() {

        interface OnItemClickCallback {
            fun delData(pos: Int)
            fun editData(pos: Int)
            fun completeTask(pos: Int)
        }

        private lateinit var onItemClickCallback: OnItemClickCallback

        fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
            this.onItemClickCallback = onItemClickCallback
        }

        inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var _deskripsi = itemView.findViewById<TextView>(R.id.deskripsi)
            var _judul = itemView.findViewById<TextView>(R.id.namaTask)
            var _tanggal = itemView.findViewById<TextView>(R.id.tanggal)
            var _kategori = itemView.findViewById<TextView>(R.id.kategori)
            var _btnHapus = itemView.findViewById<Button>(R.id.btnHapus)
            var _btnUbah = itemView.findViewById<Button>(R.id.btnEdit)
            var _btnStart = itemView.findViewById<Button>(R.id.btnKerjakan)
            var _btnFav = itemView.findViewById<CheckBox>(R.id.fav)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
            val view: View =
                LayoutInflater.from(parent.context).inflate(R.layout.item_recycler, parent, false)
            return ListViewHolder(view)
        }

        override fun getItemCount(): Int = listTask.size

        override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
            val task = listTask[position]
            holder._judul.text = task.judul
            holder._tanggal.text = task.tanggal
            holder._deskripsi.text = task.deskripsi
            holder._kategori.text = task.kategori

            holder._btnHapus.setOnClickListener { onItemClickCallback.delData(position) }
            holder._btnUbah.setOnClickListener { onItemClickCallback.editData(position) }

            holder._btnStart.text = "START"
            holder._btnStart.setOnClickListener {
                if (holder._btnStart.text == "START") {
                    holder._btnStart.text = "SELESAI"
                    holder._btnUbah.isEnabled = false
                } else {
                    onItemClickCallback.completeTask(position)
                }
            }
            // Set initial favorite state
            holder._btnFav.isChecked = cekFavorit(task)

            holder._btnFav.setOnCheckedChangeListener { _, isChecked ->
                simpanFavorit(task, isChecked)
            }
        }

        private fun cekFavorit(task: tasklist): Boolean {
            val spFavorit = sharedPreferences.getStringSet("favoritTasks", emptySet()) ?: emptySet()
            val gson = Gson()
            return spFavorit.contains(gson.toJson(task))
        }


        private fun simpanFavorit(task: tasklist, isFavorit: Boolean) {
            val spEditor = sharedPreferences.edit()
            val gson = Gson()

            // Ambil data favorit saat ini
            val currentFavorit = sharedPreferences.getStringSet("favoritTasks", emptySet()) ?: emptySet()
            val updatedFavorit = currentFavorit.toMutableSet() // Salin ke MutableSet untuk modifikasi

            if (isFavorit) {
                updatedFavorit.add(gson.toJson(task))
            } else {
                updatedFavorit.remove(gson.toJson(task))
            }
            // Simpan kembali data favorit yang sudah diperbarui
            spEditor.putStringSet("favoritTasks", updatedFavorit)
            spEditor.apply()
        }

}