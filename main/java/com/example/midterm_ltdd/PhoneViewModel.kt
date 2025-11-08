package com.example.midterm_ltdd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm_ltdd.data.Phone
import com.example.midterm_ltdd.data.PhoneItem
import com.example.midterm_ltdd.data.PhoneRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PhoneViewModel(
    private val repo: PhoneRepo = PhoneRepo()
) : ViewModel() {
    private val fixedUser = "admin"
    private val fixedPass = "123456"

    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn

    fun login(username: String, password: String) {
        _loggedIn.value = (username == fixedUser && password == fixedPass)
        if (_loggedIn.value) {
            // khi login thành công thì tải danh sách
            loadPhones()
        } else {
            _error.value = "Sai tài khoản hoặc mật khẩu"
        }
    }

    fun logout() {
        _loggedIn.value = false
    }

    private val _phones = MutableStateFlow<List<PhoneItem>>(emptyList())
    val phones: StateFlow<List<PhoneItem>> = _phones

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadPhones() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                _phones.value = repo.getPhones()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun addPhone(name: String, category: String, price: String, image: String?) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.addPhone(Phone(name, category, price, image))
                loadPhones()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun updatePhone(
        docId: String,
        name: String?,
        category: String?,
        price: String?,
        image: String?
    ) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.updatePhone(docId, name, category, price, image)
                loadPhones()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }

    fun deletePhone(docId: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                repo.deletePhone(docId)
                loadPhones()
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }
}
