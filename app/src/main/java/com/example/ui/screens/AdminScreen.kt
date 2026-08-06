package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AssessmentRepository
import com.example.data.model.ToolCatalogData
import com.example.ui.components.BrandHeader
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    repository: AssessmentRepository,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val consultations by repository.consultations.collectAsStateWithLifecycle(initialValue = emptyList())
    val weightLogs by repository.weightLogs.collectAsStateWithLifecycle(initialValue = emptyList())
    val coroutineScope = rememberCoroutineScope()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Cream25),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BrandHeader(onAdminClick = null)
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("admin_hero"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("admin_back_button")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Burgundy800)
                        }
                        Surface(shape = RoundedCornerShape(50), color = Gold200) {
                            Text(
                                text = "کنترل محصول و ایمنی",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Gold700
                            )
                        }
                    }

                    Text(text = "پنل مدیریت ابزارها", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink)
                    Text(
                        text = "نسخه قواعد، وضعیت داده‌های پویا، قیف استفاده و درخواست‌های مشاوره.",
                        fontSize = 13.sp,
                        color = InkSoft,
                        lineHeight = 19.sp
                    )
                }
            }
        }

        // System Rule Version Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Cream50),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "وضعیت سیستم و قواعد پزشکی", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "نسخه قواعد پزشکی:", fontSize = 13.sp, color = InkSoft)
                        Text(text = ToolCatalogData.SOURCE_VERSION, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Burgundy800)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "تعداد ابزارهای فعال:", fontSize = 13.sp, color = InkSoft)
                        Text(text = "۱۰ ابزار تعاملی", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = SuccessGreen)
                    }
                }
            }
        }

        // Consultations Section Header
        item {
            Text(
                text = "درخواست‌های مشاوره ثبت‌شده (${consultations.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Ink,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (consultations.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Text(
                        text = "هنوز هیچ درخواست مشاوره‌ای ثبت نشده است.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = InkSoft
                    )
                }
            }
        } else {
            items(consultations) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .testTag("consultation_item_${item.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(14.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = item.patientName, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Ink)
                            Text(text = "شماره تماس: ${item.phoneNumber}", fontSize = 13.sp, color = Burgundy800)
                            if (item.notes.isNotBlank()) {
                                Text(text = "یادداشت: ${item.notes}", fontSize = 12.sp, color = InkSoft)
                            }
                        }
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    repository.deleteConsultation(item.id)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف", tint = DangerRed)
                        }
                    }
                }
            }
        }

        // Weight Logs Header
        item {
            Text(
                text = "تاریخچه رهگیری وزن ثبت‌شده (${weightLogs.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Ink,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        if (weightLogs.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Text(
                        text = "تاریخچه وزنی ثبت نشده است.",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 13.sp,
                        color = InkSoft
                    )
                }
            }
        } else {
            items(weightLogs) { log ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = "عمل: ${log.procedureName}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Ink)
                        Text(text = "وزن روز عمل: ${log.surgeryWeightKg} kg | وزن فعلی: ${log.currentWeightKg} kg", fontSize = 13.sp, color = InkSoft)
                        Text(text = "BMI فعلی: ${log.bmi}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Burgundy800)
                    }
                }
            }
        }
    }
}
