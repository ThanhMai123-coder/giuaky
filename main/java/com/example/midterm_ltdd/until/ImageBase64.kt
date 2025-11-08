package com.example.midterm_ltdd.until


import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream
import kotlin.math.max
import kotlin.to

fun uriToBase64(
    context: Context,
    uri: Uri,
    maxEdge: Int = 512,
    quality: Int = 60
): String {
    val input = context.contentResolver.openInputStream(uri) ?: return ""
    val original = BitmapFactory.decodeStream(input) ?: return ""
    val (w, h) = original.width to original.height
    val scale = if (max(w, h) > maxEdge) maxEdge.toFloat() / max(w, h) else 1f
    val resized = if (scale < 1f)
        Bitmap.createScaledBitmap(original, (w * scale).toInt(), (h * scale).toInt(), true)
    else original

    val baos = ByteArrayOutputStream()
    resized.compress(Bitmap.CompressFormat.JPEG, quality, baos)
    val bytes = baos.toByteArray()
    return Base64.encodeToString(bytes, Base64.DEFAULT)
}

fun base64ToBitmap(b64: String): Bitmap? {
    return try {
        val bytes = Base64.decode(b64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    } catch (_: Exception) { null }
}
