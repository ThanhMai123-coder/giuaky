package com.example.midterm_ltdd

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.midterm_ltdd.data.PhoneItem
import com.example.midterm_ltdd.until.uriToBase64

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoneListScreen(
    vm: PhoneViewModel,
    onAdd: () -> Unit
) {
    val phones by vm.phones.collectAsState()
    val loading by vm.loading.collectAsState()
    val error by vm.error.collectAsState()

    LaunchedEffect(Unit) { vm.loadPhones() }

    val topGradient = Brush.horizontalGradient(listOf(Color(0xFF1976D2), Color(0xFF42A5F5)))

    Scaffold(
        topBar = {
            // Thanh tiêu đề có chữ “Thêm” và “Đăng xuất”
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(topGradient),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📱 Danh sách sản phẩm",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        TextButton(onClick = onAdd) {
                            Text("Thêm", color = Color.White, fontSize = 16.sp)
                        }
                        TextButton(onClick = { vm.logout() }) {
                            Text("Đăng xuất", color = Color.White, fontSize = 16.sp)
                        }
                    }
                }
            }
        },

        containerColor = Color(0xFFF7FAFF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            if (loading) LinearProgressIndicator(Modifier.fillMaxWidth())

            error?.let {
                Text(
                    text = "Lỗi: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            if (phones.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Hiện chưa có sản phẩm nào",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(phones, key = { it.id }) { phone ->
                        ProductCard(phone = phone, vm = vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductCard(phone: PhoneItem, vm: PhoneViewModel) {
    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = phone.name,
                color = Color(0xFF0D47A1),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Text(text = "Loại: ${phone.category}", color = Color(0xFF455A64))
            Text(
                text = "Giá: ${phone.price} đ",
                color = Color(0xFF1976D2),
                fontWeight = FontWeight.Medium,
                fontSize = 15.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showEdit = true }) {
                    Text("Sửa", color = Color(0xFF1976D2))
                }
                TextButton(onClick = { showDelete = true }) {
                    Text("Xóa", color = Color(0xFFD32F2F))
                }
            }
        }
    }

    if (showEdit) EditPhoneDialog(phone, vm, onDismiss = { showEdit = false })
    if (showDelete) ConfirmDeleteDialog(
        onConfirm = {
            vm.deletePhone(phone.id)
            showDelete = false
        },
        onDismiss = { showDelete = false }
    )
}

@Composable
private fun EditPhoneDialog(phone: PhoneItem, vm: PhoneViewModel, onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(phone.name) }
    var category by remember { mutableStateOf(phone.category) }
    var price by remember { mutableStateOf(phone.price) }
    var newImageB64 by remember { mutableStateOf<String?>(null) }
    val context = androidx.compose.ui.platform.LocalContext.current

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) newImageB64 = uriToBase64(context, uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Chỉnh sửa sản phẩm", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Tên sản phẩm") })
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Loại") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Giá") })
                TextButton(onClick = { picker.launch("image/*") }) { Text("Chọn ảnh mới") }
                newImageB64?.let { Text("✅ Ảnh mới đã chọn", color = Color(0xFF2E7D32)) }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                vm.updatePhone(phone.id, name, category, price, newImageB64)
                onDismiss()
            }) {
                Text("Lưu", color = Color(0xFF1565C0))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}

@Composable
private fun ConfirmDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Xóa sản phẩm", fontWeight = FontWeight.Bold) },
        text = { Text("Bạn có chắc chắn muốn xóa sản phẩm này?") },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) { Text("Xóa", color = Color(0xFFD32F2F)) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Hủy") } }
    )
}
