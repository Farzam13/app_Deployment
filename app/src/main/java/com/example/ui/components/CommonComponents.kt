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

            // BMI Orb display if available
            val bmiStr = output.resultData["bmi"]
            if (bmiStr != null) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(Burgundy700, Burgundy950)
                            )
                        )
                        .align(Alignment.CenterHorizontally),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "BMI", fontSize = 11.sp, color = Gold200)
                        Text(
                            text = bmiStr,
                            fontWeight = FontWeight.Black,
                            fontSize = 26.sp,
                            color = Color.White
                        )
                    }
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
