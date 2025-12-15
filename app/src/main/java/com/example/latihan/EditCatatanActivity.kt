package com.example.latihan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.latihan.databinding.ActivityEditCatatanBinding
import com.example.latihan.entities.Catatan
import com.example.latihan.entities.User
import kotlinx.coroutines.launch

class EditCatatanActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditCatatanBinding
    private var users: List<User> = emptyList()
    private var selectedUserId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityEditCatatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    override fun onStart() {
        super.onStart()
        loadUsers()
    }

    fun setupEvents(){
        binding.tombolEdit.setOnClickListener {
            val id = intent.getIntExtra("id_catatan", 0)
            val judul = binding.inputjudul.text.toString()
            val isi = binding.inputisi.text.toString()

            if(isi.isEmpty() || judul.isEmpty()){
                displayMessage("Judul dan isi catatan harus diisi")
                return@setOnClickListener
            }

            if (selectedUserId == 0) {
                displayMessage("Pilih User untuk catatan ini.")
                return@setOnClickListener
            }

            val updatedCatatan = Catatan(
                id = id,
                judul = judul,
                isi = isi,
                user_id = selectedUserId
            )

            lifecycleScope.launch {
                try {
                    val response = RetrofitClient.catatanRepository.editCatatan(id, updatedCatatan)

                    if (response.isSuccessful) {
                        displayMessage("Catatan berhasil diubah")
                        switchPage(MainActivity::class.java)
                    } else {
                        displayMessage("Gagal mengubah catatan: ${response.code()}")
                    }
                } catch (e: Exception) {
                    displayMessage("Koneksi gagal: ${e.message}")
                }
            }
        }
    }

    fun loadUsers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.getUsers()
                if (response.isSuccessful) {
                    users = response.body() ?: emptyList()
                    setupSpinner()
                    loadData()
                } else {
                    displayMessage("Gagal memuat daftar user: ${response.message()}")
                    switchPage(MainActivity::class.java)
                }
            } catch (e: Exception) {
                displayMessage("Koneksi gagal: ${e.message}")
            }
        }
    }

    fun setupSpinner() {
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            users.map { it.name }
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserId.adapter = adapter

        binding.spinnerUserId.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedUserId = users[position].id
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    fun loadData(){
        val id = intent.getIntExtra("id_catatan", 0)

        if (id == 0) {
            displayMessage("Error: id catatan tidak terkirim")
            switchPage(MainActivity::class.java)
            return
        }

        if (users.isEmpty()) {
            displayMessage("Daftar user belum dimuat. Coba lagi.")
            return
        }

        lifecycleScope.launch {
            val data = RetrofitClient.catatanRepository.getCatatan(id)
            if (data.isSuccessful) {
                val catatan = data.body()
                binding.inputjudul.setText(catatan?.judul)
                binding.inputisi.setText(catatan?.isi)

                val userIndex = users.indexOfFirst { it.id == catatan?.user_id }
                if (userIndex != -1) {
                    binding.spinnerUserId.setSelection(userIndex)
                    selectedUserId = users[userIndex].id
                } else if (users.isNotEmpty()) {
                    binding.spinnerUserId.setSelection(0)
                    selectedUserId = users.first().id
                }
                setupEvents()
            } else {
                displayMessage("Error: ${data.message()}")
                switchPage(MainActivity::class.java)
            }
        }
    }

    fun displayMessage(message:String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    fun switchPage(destination: Class<MainActivity>){
        val intent = Intent(this, destination)
        startActivity(intent)
        finish()
    }
}