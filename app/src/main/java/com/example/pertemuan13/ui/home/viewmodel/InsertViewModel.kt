package com.example.pertemuan13.ui.home.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pertemuan13.model.Mahasiswa
import com.example.pertemuan13.repository.RepositoryMhs
import kotlinx.coroutines.launch

class InsertViewModel (
    private val mhs: RepositoryMhs
) : ViewModel() {

    var  uiEvent: InsertUiState by mutableStateOf(InsertUiState())
        private set
    var uiState: FormState by mutableStateOf(FormState.Idle)
        private set
    fun updateState (mahasiswaEvent: MahasiswaEvent) {
        uiEvent = uiEvent.copy(
            insertUiEvent = mahasiswaEvent,
        )
    }


    //validasi data input pengguna
    fun validateFields(): Boolean {
        val event = uiEvent.insertUiEvent
        val errorState = FormErrorState(
            nim = if (event.nim.isNotEmpty()) null else "NIM tidak boleh kosong",
            nama = if (event.nama.isNotEmpty()) null else "Nama tidak boleh kosong",
            jenisKelamin = if (event.jenisKelamin.isNotEmpty()) null else "Jenis Kelamin tidak boleh kosong",
            alamat = if (event.alamat.isNotEmpty()) null else "Alamat tidak boleh kosong",
            kelas = if (event.kelas.isNotEmpty()) null else "Kelas tidak boleh kosong",
            angkatan = if (event.angkatan.isNotEmpty()) null else "Angkatan tidak boleh kosong",
            dosenpembimbing1 =  if (event.dosenpembimbing1.isNotEmpty()) null else "Dosen Pembimbing 1tidak boleh kosong",
            dosenpembimbing2 = if (event.dosenpembimbing2.isNotEmpty()) null else "Dosen Pembimbing 2 tidak boleh kosong",
            judulskripsi = if (event.judulskripsi.isNotEmpty()) null else "judul skripsi tidak boleh kosong"
        )

        uiEvent = uiEvent.copy(isEntryValid = errorState)
        return errorState.isValid()
    }

    fun insertMhs() {
        if (validateFields()) {
            viewModelScope.launch {
                uiState = FormState.Loading
                try {
                    mhs.insertMhs(uiEvent.insertUiEvent.toMhsModel())
                    uiState = FormState.Succes("Data berhasil disimpan")

                } catch (e: Exception) {
                    uiState = FormState.Error("Data gagal disimpan")
                }

            }
        } else {
            uiState = FormState.Error("Data tidak Valid")
        }
    }
    fun resetForm() {
       uiEvent = InsertUiState()
       uiState = FormState.Idle
    }
    fun resetSnackBarMessage() {
        uiState = FormState.Idle
    }

}

sealed class FormState {
    object Idle : FormState()
    object Loading : FormState()
    data class Succes(val message: String) : FormState()
    data class Error(val message: String) : FormState()
}

data class InsertUiState(
    val insertUiEvent : MahasiswaEvent = MahasiswaEvent(),
    val isEntryValid: FormErrorState = FormErrorState(),
)

data class FormErrorState(
    val nim: String? = null,
    val nama: String? = null,
    val jenisKelamin: String? = null,
    val alamat: String? = null,
    val kelas: String? = null,
    val angkatan: String? = null,
    val dosenpembimbing1: String? = null,
    val dosenpembimbing2: String? = null,
    val judulskripsi: String? = null

) {
    fun isValid() : Boolean {
        return nim == null && nama == null && jenisKelamin == null &&
                alamat == null && kelas == null && angkatan == null && dosenpembimbing1 == null &&  dosenpembimbing2 == null && judulskripsi == null
    }
}

//data class variabel yang menyimpan data input form
data class MahasiswaEvent(
    val nim: String = "",
    val nama: String = "",
    val jenisKelamin: String = "",
    val alamat: String = "",
    val kelas: String = "",
    val angkatan: String = "",
    val dosenpembimbing1: String = "",
    val dosenpembimbing2: String = "",
    val judulskripsi: String = "",

)

//Menyimpan input form kedalam entity
fun MahasiswaEvent.toMhsModel() : Mahasiswa = Mahasiswa (
    nim = nim,
    nama = nama,
    jenisKelamin = jenisKelamin,
    alamat = alamat,
    kelas = kelas,
    angkatan = angkatan,
    dosenpembimbing1 = dosenpembimbing1,
    dosenpembimbing2 = dosenpembimbing2,
    judulskripsi = judulskripsi
)