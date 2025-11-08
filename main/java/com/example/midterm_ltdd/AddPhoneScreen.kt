package com.example.midterm_ltdd

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.midterm_ltdd.until.base64ToBitmap
import com.example.midterm_ltdd.until.uriToBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPhoneScreen(
    vm: PhoneViewModel,
    onDone: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var imageBase64 by remember { mutableStateOf<String?>(null) }

    val picker = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) imageBase64 = uriToBase64(context, uri)
    }

    val gradient = Brush.horizontalGradient(
        listOf(Color(0xFF6A11CB), Color(0xFF2575FC))
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .background(gradient),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Thêm sản phẩm",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Các ô nhập
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Tên sản phẩm") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Loại sản phẩm") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Giá") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            // Nút chọn ảnh
            Button(
                onClick = { picker.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2575FC))
            ) {
                Text("Chọn hình ảnh", color = Color.White, fontWeight = FontWeight.Bold)
            }

            // Ảnh demo sau khi chọn
            imageBase64?.let { b64 ->
                base64ToBitmap(b64)?.let { bmp ->
                    Image(
                        bitmap = bmp.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 8.dp)
                            .background(Color(0xFFF2F2F2), RoundedCornerShape(12.dp))
                    )
                }
            }

            // Nút Lưu
            Button(
                onClick = {
                    vm.addPhone(name.trim(), category.trim(), price.trim(), imageBase64)
                    onDone()
                },
                enabled = name.isNotBlank() && category.isNotBlank() && price.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF6A11CB),
                    disabledContainerColor = Color.LightGray
                )
            ) {
                Text("Lưu", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
