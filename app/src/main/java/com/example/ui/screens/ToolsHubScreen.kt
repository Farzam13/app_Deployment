package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ToolCatalogData
import com.example.ui.components.BrandHeader
import com.example.ui.components.ToolCardItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsHubScreen(
    onNavigateToTool: (String) -> Unit,
    onNavigateToAdmin: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredTools = remember(searchQuery) {
        if (searchQuery.isBlank()) {
            ToolCatalogData.TOOLS
        } else {
            ToolCatalogData.TOOLS.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.description.contains(searchQuery, ignoreCase = true) ||
                it.shortTitle.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Cream25),
        contentPadding = PaddingValues(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            BrandHeader(onAdminClick = onNavigateToAdmin)
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Burgundy100
                ) {
                    Text(
                        text = "۱۰ ابزار تعاملی پزشکی",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Burgundy800
                    )
                }

                Text(
                    text = "برای هر سؤال، یک مسیر ساختاریافته",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Ink
                )

                Text(
                    text = "ابزار مناسب را براساس هدفتان انتخاب کنید. هر نتیجه محدودیت پزشکی، منبع و اقدام بعدی مشخص دارد.",
                    fontSize = 13.sp,
                    color = InkSoft,
                    lineHeight = 20.sp
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("جستجو در ابزارها...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = InkSoft) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hub_search_input"),
                    shape = RoundedCornerShape(16.dp),
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

        items(filteredTools) { tool ->
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                ToolCardItem(
                    tool = tool,
                    onClick = { onNavigateToTool(tool.slug) }
                )
            }
        }
    }
}
