package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ToolCatalogData
import com.example.ui.components.BrandHeader
import com.example.ui.components.ToolCardItem
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToTool: (String) -> Unit,
    onNavigateToHub: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryTools = ToolCatalogData.TOOLS.filter { it.phase == 1 }.take(4)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Cream25),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            BrandHeader(onAdminClick = onNavigateToAdmin)
        }

        // Hero Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .testTag("hero_section"),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Cream50),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = Gold200.copy(alpha = 0.5f)
                    ) {
                        Text(
                            text = "ارزیابی اولیه وضعیت وزن",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gold700
                        )
                    }

                    Text(
                        text = "BMI شما فقط یک عدد نیست؛ نقطه شروع یک تصمیم آگاهانه است.",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        color = Ink
                    )

                    Text(
                        text = "قد و وزن خود را وارد کنید تا شاخص توده بدنی، محدوده وزنی و معنای نتیجه را به‌صورت ساده و شخصی‌سازی‌شده ببینید.",
                        fontSize = 14.sp,
                        color = InkSoft,
                        lineHeight = 22.sp
                    )

                    Button(
                        onClick = { onNavigateToTool("bmi-assessment") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("hero_calculate_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burgundy800,
                            contentColor = Color.White
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "محاسبه BMI من",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Text("نتیجه فوری", fontSize = 11.sp, color = InkSoft)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = Burgundy700, modifier = Modifier.size(16.dp))
                            Text("بدون نیاز به ثبت‌نام", fontSize = 11.sp, color = InkSoft)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            Text("کاملاً رایگان", fontSize = 11.sp, color = InkSoft)
                        }
                    }

                    // Banner Image
                    Image(
                        painter = painterResource(id = R.drawable.bmi_hero_banner),
                        contentDescription = "تصویر ارزیابی پزشکی",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(20.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }

        // Trust Steps
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "مسیر ارزیابی هوشمند",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Ink
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val steps = listOf(
                        "۱" to "قد و وزن\nاطلاعات پایه",
                        "۲" to "محاسبه و تفسیر\nتوضیح ساده عدد",
                        "۳" to "اقدام بعدی\nروشن‌شدن مسیر"
                    )

                    steps.forEach { (num, text) ->
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(16.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(
                                modifier = Modifier.padding(12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Burgundy100),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = num, fontWeight = FontWeight.Bold, color = Burgundy800, fontSize = 13.sp)
                                }
                                Text(
                                    text = text,
                                    fontSize = 11.sp,
                                    color = Ink,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Featured Tools Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "ابزارهای کلیدی", fontSize = 12.sp, color = InkSoft, fontWeight = FontWeight.Bold)
                    Text(text = "برای ارزیابی دقیق‌تر", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Ink)
                }
                TextButton(
                    onClick = onNavigateToHub,
                    modifier = Modifier.testTag("view_all_tools_button")
                ) {
                    Text(text = "مشاهده همه (۱۰)", color = Burgundy800, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Primary tools list
        items(primaryTools) { tool ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ToolCardItem(
                    tool = tool,
                    onClick = { onNavigateToTool(tool.slug) }
                )
            }
        }

        // CTA Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Burgundy950
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "نیاز به مشاوره اختصاصی دارید؟",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = "اطلاعات ارزیابی‌های خود را بررسی و خلاصه‌ای شفاف برای تیم پزشکی ارسال کنید.",
                        fontSize = 13.sp,
                        color = Gold200,
                        lineHeight = 20.sp
                    )
                    Button(
                        onClick = { onNavigateToTool("smart-consultation") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("consultation_cta_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Gold500,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "ثبت مشاوره هوشمند", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
