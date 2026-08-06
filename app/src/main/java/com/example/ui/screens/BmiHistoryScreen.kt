package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AssessmentRepository
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BmiHistoryScreen(
    repository: AssessmentRepository,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val weightLogs by repository.weightLogs.collectAsState(initial = emptyList())
    // Sort logs by date ascending to show progress left-to-right
    val sortedLogs = weightLogs.sortedBy { it.dateLogged }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("تاریخچه روند BMI", color = DrkSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = DrkSurface)
                    }
                },
                actions = {
                    if (sortedLogs.isNotEmpty()) {
                        IconButton(onClick = {
                            val sb = java.lang.StringBuilder()
                            sb.append("گزارش روند کاهش وزن (BMI)\n\n")
                            sortedLogs.forEach { log ->
                                val dateStr = SimpleDateFormat("yyyy/MM/dd", Locale.US).format(Date(log.dateLogged))
                                sb.append("تاریخ: $dateStr - وزن: ${log.currentWeightKg} kg - BMI: ${String.format(Locale.US, "%.1f", log.bmi)}\n")
                            }
                            
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "گزارش روند BMI")
                                putExtra(Intent.EXTRA_TEXT, sb.toString())
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری گزارش"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "اشتراک‌گذاری", tint = DrkSurface)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DrkTurquoiseDark)
            )
        },
        containerColor = DrkBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (sortedLogs.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                        Text("هیچ تاریخچه‌ای ثبت نشده است.", color = DrkMuted, fontSize = 16.sp)
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        colors = CardDefaults.cardColors(containerColor = DrkSurface),
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "نمودار روند BMI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = DrkCharcoal
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            BmiLineChart(logs = sortedLogs, modifier = Modifier.fillMaxSize())
                        }
                    }
                }
                
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "رکوردهای ثبت‌شده",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DrkCharcoal
                    )
                }
                
                sortedLogs.reversed().forEach { log ->
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = DrkSurface),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DrkBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    val dateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.US)
                                    val dateStr = dateFormat.format(Date(log.dateLogged))
                                    Text(text = dateStr, fontSize = 12.sp, color = DrkMuted)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "وزن: ${log.currentWeightKg} kg",
                                        fontSize = 14.sp,
                                        color = DrkText
                                    )
                                }
                                Text(
                                    text = "BMI: ${String.format(Locale.US, "%.1f", log.bmi)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = DrkTurquoiseDark
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BmiLineChart(logs: List<com.example.data.local.WeightLogEntity>, modifier: Modifier = Modifier) {
    if (logs.size < 2) {
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Text("برای نمایش نمودار حداقل به دو رکورد نیاز است.", color = DrkMuted, fontSize = 13.sp)
        }
        return
    }

    val bmis = logs.map { it.bmi.toFloat() }
    val maxBmi = (bmis.maxOrNull() ?: 40f) + 2f
    val minBmi = ((bmis.minOrNull() ?: 20f) - 2f).coerceAtLeast(0f)

    Canvas(modifier = modifier.padding(vertical = 10.dp, horizontal = 5.dp)) {
        val width = size.width
        val height = size.height
        
        val pointSpacing = width / (logs.size - 1)
        val bmiRange = maxBmi - minBmi

        val path = Path()
        val points = mutableListOf<Offset>()

        logs.forEachIndexed { index, log ->
            val x = index * pointSpacing
            val normalizedY = 1f - ((log.bmi.toFloat() - minBmi) / bmiRange)
            val y = normalizedY * height
            
            val offset = Offset(x, y)
            points.add(offset)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = DrkTurquoise,
            style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )

        points.forEach { point ->
            drawCircle(
                color = DrkSurface,
                radius = 5.dp.toPx(),
                center = point
            )
            drawCircle(
                color = DrkTurquoiseDark,
                radius = 3.dp.toPx(),
                center = point
            )
        }
    }
}
