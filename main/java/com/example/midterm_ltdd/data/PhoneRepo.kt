package com.example.midterm_ltdd.data

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class PhoneRepo {
    private val col = Firebase.firestore.collection("phones")

    suspend fun addPhone(phone: Phone): String {
        val doc = col.add(phone).await()
        return doc.id
    }

    suspend fun getPhones(): List<PhoneItem> {
        val snap = col.orderBy("name").get().await()
        return snap.documents.map { d ->
            PhoneItem(
                id = d.id,
                name = d.getString("name") ?: "",
                category = d.getString("category") ?: "",
                price = d.getString("price") ?: "",
                image = d.getString("image")
            )
        }
    }

    suspend fun updatePhone(
        docId: String,
        name: String? = null,
        category: String? = null,
        price: String? = null,
        image: String? = null
    ) {
        val data = buildMap<String, Any?> {
            name?.let { put("name", it) }
            category?.let { put("category", it) }
            price?.let { put("price", it) }
            image?.let { put("image", it) }
        }
        if (data.isNotEmpty()) col.document(docId).update(data).await()
    }

    suspend fun deletePhone(docId: String) {
        col.document(docId).delete().await()
    }
}
