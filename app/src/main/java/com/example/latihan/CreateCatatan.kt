package com.example.latihan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.latihan.databinding.ActivityCreateCatatanBinding
import com.example.latihan.entities.Catatan
import com.example.latihan.entities.User
import kotlinx.coroutines.launch

class CreateCatatan : AppCompatActivity() {

    private lateinit var binding: ActivityCreateCatatanBinding

    private var userList: List<User> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityCreateCatatanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupEvents()

        loadUsers()
    }

    private fun loadUsers() {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.catatanRepository.getUsers()
                if (response.isSuccessful) {

                    userList = response.body() ?: emptyList()

                    setupUserSpinner()
                } else {
                    displayMessage("Gagal memuat daftar user: ${response.code()}")
                }
            } catch (e: Exception) {
                displayMessage("Koneksi gagal: ${e.message}")
            }
        }
    }

    private fun setupUserSpinner() {
        val userNames = userList.map { it.name }

        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            userNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUserId.adapter = adapter
    }

    fun setupEvents(){
        binding.tombolsimpan.setOnClickListener {
            val judul = binding.inputjudul.text.toString()
            val isi = binding.inputisi.text.toString()

            val selectedPosition = binding.spinnerUserId.selectedItemPosition

            if (judul.isEmpty() || isi.isEmpty()){
                displayMessage("judul dan isi catatan harus diisi")
                return@setOnClickListener
            }

            if (userList.isEmpty() || selectedPosition == android.widget.AdapterView.INVALID_POSITION) {
                displayMessage("Pilih pemilik catatan (User) yang valid")
                return@setOnClickListener
            }

            val userId = userList[selectedPosition].id

            val payload = Catatan(
                judul = judul,
                isi = isi,
                id = null,
                user_id = userId
            )

            lifecycleScope.launch {
                val response = RetrofitClient.catatanRepository.createCatatan(payload)
                if (response.isSuccessful){
                    displayMessage("Catatan berhasil dibuat")

                    val intent = Intent(this@CreateCatatan, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    displayMessage("Gagal :: ${response.code()}")
                }
            }
        }
    }

    fun displayMessage(message: String){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    }
}