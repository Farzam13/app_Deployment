package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RecommendedAction
import com.example.data.model.RiskLevel
import com.example.data.model.ToolCatalogItem
import com.example.data.model.ToolOutput
import com.example.ui.theme.*

@Composable
fun BrandHeader(
    modifier: Modifier = Modifier,
    onAdminClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Cream25,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp, bottomStart = 14.dp, bottomEnd = 4.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(Burgundy700, Burgundy950)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "K",
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp
                    )
                }
                Column {
                    Text(
                        text = "دکتر یاسر کبیریزاده",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Ink
                    )
                    Text(
                        text = "فلوشیپ جراحی چاقی و مینی‌اینوازیو",
                        fontSize = 11.sp,
                        color = InkSoft
                    )
                }
            }

            if (onAdminClick != null) {
                IconButton(
                    onClick = onAdminClick,
                    modifier = Modifier.testTag("admin_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "پنل مدیریت",
                        tint = Burgundy700
                    )
                }
            }
        }
    }
}

@Composable
fun ToolCardItem(
    tool: ToolCatalogItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("tool_card_${tool.slug}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(18.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Burgundy100),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = tool.title,
                        tint = Burgundy800,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = when (tool.risk) {
                        RiskLevel.LOW -> SuccessBg
                        RiskLevel.MEDIUM -> AttentionBg
                        RiskLevel.HIGH -> Burgundy100
                    }
                ) {
                    Text(
                        text = tool.duration,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = when (tool.risk) {
                            RiskLevel.LOW -> SuccessGreen
                            RiskLevel.MEDIUM -> AttentionAmber
                            RiskLevel.HIGH -> Burgundy800
                        }
                    )
                }
            }

            Text(
                text = tool.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Ink
            )

            Text(
                text = tool.description,
                fontSize = 13.sp,
                color = InkSoft,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
fun ResultViewCard(
    output: ToolOutput,
    onActionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isUrgent = output.status == "urgent"
    val isWarning = output.status == "requires_human_review" || output.status == "insufficient_information"

    val containerColor = when {
        isUrgent -> DangerBg
        isWarning -> AttentionBg
        else -> SuccessBg
    }

    val headerColor = when {
        isUrgent -> DangerRed
        isWarning -> AttentionAmber
        else -> SuccessGreen
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("result_view_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isUrgent || isWarning) Icons.Default.Warning else Icons.Default.Check,
                    contentDescription = null,
                    tint = headerColor,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isUrgent) "اقدام فوری / مهم" else "نتیجه ارزیابی اولیه",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = headerColor
                )
            }

            Text(
                text = output.summary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Ink,
                lineHeight = 26.sp
            )

            // BMI Gauge Chart display if available
            val bmiStr = output.resultData["bmi"]
            if (bmiStr != null) {
                val bmiValue = bmiStr.toFloatOrNull()
                if (bmiValue != null) {
                    BmiGaugeChart(
                        bmi = bmiValue,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 12.dp)
                    )
                }
            }

            if (output.explanation.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "توضیحات و نکات کلیدی:",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Ink
                    )
                    output.explanation.forEach { item ->
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(start = 4.dp)
                        ) {
                            Text(text = "•", color = headerColor, fontWeight = FontWeight.Bold)
                            Text(text = item, fontSize = 13.sp, color = Ink, lineHeight = 20.sp)
                        }
                    }
                }
            }

            if (output.missingInformation.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.8f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "اطلاعات ناقص:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = DangerRed
                        )
                        Text(
                            text = output.missingInformation.joinToString("، "),
                            fontSize = 13.sp,
                            color = Ink
                        )
                    }
                }
            }

            if (output.warnings.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, headerColor.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        output.warnings.forEach { warn ->
                            Text(
                                text = warn,
                                fontSize = 12.sp,
                                color = headerColor,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            if (output.recommendedActions.isNotEmpty()) {
                Divider(color = headerColor.copy(alpha = 0.2f))
                Text(
                    text = "مسیرهای پیشنهادی بعدی:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Ink
                )
                output.recommendedActions.forEach { action ->
                    Button(
                        onClick = { onActionClick(action.href) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("action_button_${action.label}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burgundy800,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = action.label, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                text = "این نتیجه جایگزین معاینه، تشخیص یا تصمیم پزشک نیست.",
                fontSize = 11.sp,
                color = InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    unit: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_${label}"),
            shape = RoundedCornerShape(12.dp),
            trailingIcon = if (unit != null) {
                { Text(text = unit, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = InkSoft, modifier = Modifier.padding(end = 12.dp)) }
            } else null,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Burgundy700,
                unfocusedBorderColor = BorderColor
            ),
            singleLine = true
        )
    }
}

@Composable
fun ChoiceGridMultiSelect(
    options: List<Pair<String, String>>,
    selectedValues: List<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { (code, title) ->
            val isSelected = code in selectedValues
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onToggle(code) }
                    .testTag("choice_${code}"),
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) Burgundy100 else Color.White,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) Burgundy800 else BorderColor
                )
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) Burgundy950 else Ink
                    )
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggle(code) },
                        colors = CheckboxDefaults.colors(checkedColor = Burgundy800)
                    )
                }
            }
        }
    }
}

@Composable
fun YesNoRadioGroup(
    label: String,
    selected: Boolean?,
    onSelect: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(text = label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            FilterChip(
                selected = selected == true,
                onClick = { onSelect(true) },
                label = { Text("بله") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("yesno_yes_${label}"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Burgundy800,
                    selectedLabelColor = Color.White
                )
            )
            FilterChip(
                selected = selected == false,
                onClick = { onSelect(false) },
                label = { Text("خیر") },
                modifier = Modifier
                    .weight(1f)
                    .testTag("yesno_no_${label}"),
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Burgundy800,
                    selectedLabelColor = Color.White
                )
            )
        }
    }
}

@Composable
fun BmiGaugeChart(bmi: Float, modifier: Modifier = Modifier) {
    val gaugeColors = listOf(
        Color(0xFF3498DB), // Underweight (<18.5)
        Color(0xFF2E8B57), // Normal (18.5 - 24.9)
        Color(0xFFF1C40F), // Overweight (25 - 29.9)
        Color(0xFFE67E22), // Obese Class 1 (30 - 34.9)
        Color(0xFFE74C3C), // Obese Class 2 (35 - 39.9)
        Color(0xFF8E44AD)  // Obese Class 3 (>= 40)
    )
    
    // Normalizing between BMI 15 to 45
    val minBmi = 15f
    val maxBmi = 45f
    val normalized = ((bmi - minBmi) / (maxBmi - minBmi)).coerceIn(0f, 1f)
    val angleDegrees = 180f + (normalized * 180f)

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(200.dp)
                .height(110.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize().padding(10.dp)) {
                val strokeWidth = 16.dp.toPx()
                val radius = size.width / 2f
                val center = androidx.compose.ui.geometry.Offset(size.width / 2f, size.height)

                val segmentSweep = 180f / gaugeColors.size
                var currentAngle = 180f
                for (color in gaugeColors) {
                    drawArc(
                        color = color,
                        startAngle = currentAngle,
                        sweepAngle = segmentSweep,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = strokeWidth,
                            cap = androidx.compose.ui.graphics.StrokeCap.Butt
                        ),
                        size = androidx.compose.ui.geometry.Size(size.width, size.width),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, size.height - radius)
                    )
                    currentAngle += segmentSweep
                }

                val needleLength = radius - strokeWidth - 10.dp.toPx()
                val needleAngleRad = Math.toRadians(angleDegrees.toDouble())
                val endX = center.x + needleLength * kotlin.math.cos(needleAngleRad).toFloat()
                val endY = center.y + needleLength * kotlin.math.sin(needleAngleRad).toFloat()

                drawLine(
                    color = Ink,
                    start = center,
                    end = androidx.compose.ui.geometry.Offset(endX, endY),
                    strokeWidth = 4.dp.toPx(),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )

                drawCircle(
                    color = Ink,
                    radius = 8.dp.toPx(),
                    center = center
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "BMI: ${String.format(java.util.Locale.US, "%.1f", bmi)}",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = Ink
        )
        
        val category = when {
            bmi < 18.5f -> "کمبود وزن"
            bmi < 25f -> "وزن طبیعی"
            bmi < 30f -> "اضافه وزن"
            bmi < 35f -> "چاقی کلاس ۱"
            bmi < 40f -> "چاقی کلاس ۲"
            else -> "چاقی کلاس ۳ (مفرط)"
        }
        
        Text(
            text = category,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = InkSoft
        )
        
        Spacer(modifier = Modifier.height(12.dp))
        
        val tip = when {
            bmi < 18.5f -> "بهتر است با یک متخصص تغذیه برای دریافت برنامه غذایی مناسب و افزایش وزن سالم مشورت کنید."
            bmi < 25f -> "وضعیت وزنی شما در محدوده سالم قرار دارد. با حفظ رژیم غذایی متعادل و فعالیت بدنی منظم، این روند را ادامه دهید."
            bmi < 30f -> "توصیه می‌شود با افزایش فعالیت‌های فیزیکی و رعایت رژیم غذایی سالم، به سمت وزن ایده‌آل حرکت کنید."
            bmi < 35f -> "در این مرحله، مشورت با پزشک متخصص برای بررسی وضعیت سلامت عمومی و دریافت برنامه کاهش وزن ضروری است."
            bmi < 40f -> "خطر ابتلا به بیماری‌های متابولیک بالا است. بررسی گزینه‌های درمانی تخصصی و مشاوره پزشکی توصیه می‌شود."
            else -> "اقدام جدی برای مدیریت وزن تحت نظر تیم پزشکی و بررسی گزینه‌های درمانی مانند جراحی چاقی برای حفظ سلامت ضروری است."
        }
        
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = DrkTurquoise.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Info,
                    contentDescription = "نکته سلامتی",
                    tint = DrkTurquoiseDark,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = tip,
                    fontSize = 13.sp,
                    color = Ink,
                    style = androidx.compose.ui.text.TextStyle(lineHeight = 20.sp)
                )
            }
        }
    }
}
