package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class RecommendedAction(
    val label: String,
    val href: String,
    val priority: Int
)

@Serializable
data class ToolOutput(
    val toolId: String,
    val version: String = "1.0.0",
    val status: String = "completed", // "completed", "insufficient_information", "requires_human_review", "urgent", "failed"
    val sessionId: String = "ses_local",
    val summary: String,
    val explanation: List<String> = emptyList(),
    val missingInformation: List<String> = emptyList(),
    val warnings: List<String> = emptyList(),
    val recommendedActions: List<RecommendedAction> = emptyList(),
    val reviewedAt: String = "2026-07-26",
    val requiresHumanReview: Boolean = false,
    val notADiagnosis: Boolean = true,
    val resultData: Map<String, String> = emptyMap()
)
