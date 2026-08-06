package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class ToolId(val rawValue: String) {
    BMI_ASSESSMENT("bmi_assessment"),
    BARIATRIC_COMPARISON("bariatric_comparison"),
    PRELIMINARY_ASSESSMENT("preliminary_assessment"),
    WEIGHT_LOSS_TRACKER("weight_loss_tracker"),
    POST_OP_NUTRITION("post_op_nutrition"),
    INSURANCE_GUIDE("insurance_guide"),
    INSTALLMENT_CALCULATOR("installment_calculator"),
    SERVICE_ROUTER("service_router"),
    WARNING_SIGNS("warning_signs"),
    SMART_CONSULTATION("smart_consultation");

    companion object {
        fun fromString(value: String): ToolId {
            return values().firstOrNull { it.rawValue == value } ?: BMI_ASSESSMENT
        }
    }
}

enum class RiskLevel { LOW, MEDIUM, HIGH }

data class ToolCatalogItem(
    val id: ToolId,
    val slug: String,
    val title: String,
    val shortTitle: String,
    val description: String,
    val duration: String,
    val iconSymbol: String,
    val icon: ImageVector,
    val risk: RiskLevel,
    val phase: Int,
    val eventPrefix: String
)

data class SourceItem(
    val id: String,
    val title: String,
    val url: String,
    val reviewedAt: String
)

data class ProcedureDetail(
    val name: String,
    val mechanism: String,
    val restriction: String,
    val malabsorption: String,
    val metabolic: String,
    val diabetes: String,
    val reflux: String,
    val supplements: String,
    val complexity: String,
    val followup: String,
    val advantages: List<String>,
    val limitations: List<String>
)

object ToolCatalogData {
    val SOURCES = listOf(
        SourceItem(
            id = "asmbs-ifso-2022",
            title = "راهنمای انتخاب بیمار ASMBS/IFSO",
            url = "https://asmbs.org/resources/2022-american-society-for-metabolic-and-bariatric-surgery-ifso-indications-for-metabolic-and-bariatric-surgery",
            reviewedAt = "2026-07-26"
        ),
        SourceItem(
            id = "cdc-bmi-adults",
            title = "دسته‌بندی BMI بزرگسالان — CDC",
            url = "https://www.cdc.gov/bmi/adult-calculator/bmi-categories.html",
            reviewedAt = "2026-07-26"
        ),
        SourceItem(
            id = "asps-liposuction",
            title = "محدودیت‌های لیپوساکشن — ASPS",
            url = "https://www.plasticsurgery.org/cosmetic-procedures/liposuction",
            reviewedAt = "2026-07-26"
        )
    )

    const val SOURCE_VERSION = "medical-core-2026.07.26"

    val CONDITIONS = listOf(
        "type2_diabetes" to "دیابت نوع ۲",
        "hypertension" to "فشارخون",
        "sleep_apnea" to "آپنه خواب",
        "fatty_liver" to "کبد چرب",
        "joint_problem" to "مشکلات مفصلی"
    )

    val PROCEDURES = mapOf(
        "sleeve" to ProcedureDetail(
            name = "اسلیو معده",
            mechanism = "کاهش حجم معده و اثرات هورمونی مرتبط با اشتها",
            restriction = "زیاد",
            malabsorption = "معمولاً ندارد",
            metabolic = "قابل‌توجه",
            diabetes = "ممکن است بهبود ایجاد کند؛ انتخاب فردی است",
            reflux = "در برخی افراد می‌تواند ایجاد یا تشدید شود",
            supplements = "پیگیری و مکمل طبق آزمایش‌ها",
            complexity = "یک مسیر جراحی بدون اتصال روده",
            followup = "پیگیری تغذیه و آزمایش‌های منظم",
            advantages = listOf("ساختار گوارشی ساده‌تر از بای‌پس", "عدم ایجاد اتصال روده"),
            limitations = listOf("برای رفلاکس شدید ممکن است انتخاب مناسبی نباشد", "غیرقابل بازگشت بودن برداشت معده")
        ),
        "mini_bypass" to ProcedureDetail(
            name = "مینی‌بای‌پس",
            mechanism = "محدودیت حجم همراه با تغییر مسیر بخشی از روده",
            restriction = "زیاد",
            malabsorption = "متوسط تا بیشتر",
            metabolic = "زیاد",
            diabetes = "اثر متابولیک قابل‌توجه؛ نیازمند ارزیابی",
            reflux = "نوع و شدت رفلاکس در انتخاب مهم است",
            supplements = "نیازمند تعهد جدی به مکمل و آزمایش",
            complexity = "یک اتصال گوارشی",
            followup = "پیگیری مادام‌العمر ضروری",
            advantages = listOf("اثر متابولیک", "یک اتصال جراحی"),
            limitations = listOf("ریسک سوءجذب و کمبود", "بررسی رفلاکس صفراوی ضروری است")
        ),
        "classic_bypass" to ProcedureDetail(
            name = "بای‌پس کلاسیک",
            mechanism = "معده کوچک‌تر و تغییر مسیر روده با دو اتصال",
            restriction = "زیاد",
            malabsorption = "متوسط",
            metabolic = "زیاد",
            diabetes = "اثر متابولیک قابل‌توجه؛ نیازمند ارزیابی",
            reflux = "در برخی بیماران دارای رفلاکس، بیشتر بررسی می‌شود",
            supplements = "نیازمند مکمل و پایش بلندمدت",
            complexity = "دو اتصال گوارشی",
            followup = "پیگیری مادام‌العمر ضروری",
            advantages = listOf("سابقه بالینی طولانی", "قابل بررسی در برخی بیماران رفلاکسی"),
            limitations = listOf("پیچیدگی بیشتر", "ریسک کمبود و فتق داخلی")
        ),
        "sasi" to ProcedureDetail(
            name = "ساسی‌بای‌پس",
            mechanism = "ترکیب اسلیو با یک مسیر کمکی روده‌ای",
            restriction = "زیاد",
            malabsorption = "متغیر",
            metabolic = "زیاد",
            diabetes = "ممکن است اثر متابولیک قابل‌توجه داشته باشد",
            reflux = "نیازمند بررسی دقیق علائم و آناتومی",
            supplements = "پایش و مکمل بلندمدت",
            complexity = "اسلیو همراه با یک اتصال",
            followup = "پیگیری دقیق و طولانی‌مدت",
            advantages = listOf("ترکیب محدودیت و اثر متابولیک", "حفظ مسیر طبیعی بخشی از غذا"),
            limitations = listOf("مناسب همه افراد نیست", "داده و تجربه مرکز در انتخاب مهم است")
        )
    )

    val TOOLS: List<ToolCatalogItem> = listOf(
        ToolCatalogItem(
            id = ToolId.BMI_ASSESSMENT,
            slug = "bmi-assessment",
            title = "محاسبه BMI و ارزیابی عمومی",
            shortTitle = "ارزیابی BMI",
            description = "BMI خود را محاسبه کنید و مسیرهای درمانی قابل بررسی را بدون تشخیص قطعی بشناسید.",
            duration = "حدود ۲ دقیقه",
            iconSymbol = "◌",
            icon = Icons.Outlined.Speed,
            risk = RiskLevel.MEDIUM,
            phase = 1,
            eventPrefix = "bmi"
        ),
        ToolCatalogItem(
            id = ToolId.BARIATRIC_COMPARISON,
            slug = "bariatric-comparison",
            title = "مقایسه روش‌های جراحی چاقی",
            shortTitle = "مقایسه عمل‌ها",
            description = "اسلیو، مینی‌بای‌پس، بای‌پس کلاسیک و ساسی را براساس دغدغه خود مقایسه کنید.",
            duration = "حدود ۳ دقیقه",
            iconSymbol = "⇄",
            icon = Icons.Outlined.CompareArrows,
            risk = RiskLevel.MEDIUM,
            phase = 1,
            eventPrefix = "comparison"
        ),
        ToolCatalogItem(
            id = ToolId.PRELIMINARY_ASSESSMENT,
            slug = "preliminary-assessment",
            title = "ارزیابی مقدماتی روش قابل بررسی",
            shortTitle = "ارزیابی اولیه",
            description = "اطلاعات مهم برای مشاوره را ساختاریافته جمع کنید و خلاصه‌ای قابل ارائه بگیرید.",
            duration = "کمتر از ۳ دقیقه",
            iconSymbol = "✓",
            icon = Icons.Outlined.AssignmentTurnedIn,
            risk = RiskLevel.HIGH,
            phase = 1,
            eventPrefix = "assessment"
        ),
        ToolCatalogItem(
            id = ToolId.WEIGHT_LOSS_TRACKER,
            slug = "weight-loss-tracker",
            title = "رهگیری کاهش وزن بعد از عمل",
            shortTitle = "رهگیری وزن",
            description = "روند وزن، BMI و تغییرات خود را بدون قضاوت و بدون وعده نتیجه مشاهده کنید.",
            duration = "حدود ۱ دقیقه",
            iconSymbol = "↘",
            icon = Icons.Outlined.ShowChart,
            risk = RiskLevel.MEDIUM,
            phase = 1,
            eventPrefix = "tracker"
        ),
        ToolCatalogItem(
            id = ToolId.POST_OP_NUTRITION,
            slug = "post-op-nutrition",
            title = "راهنمای تغذیه بعد از عمل",
            shortTitle = "راهنمای تغذیه",
            description = "براساس نوع عمل و روز گذشته از جراحی، مرحله عمومی پروتکل را مشاهده کنید.",
            duration = "حدود ۱ دقیقه",
            iconSymbol = "◇",
            icon = Icons.Outlined.Restaurant,
            risk = RiskLevel.HIGH,
            phase = 1,
            eventPrefix = "nutrition"
        ),
        ToolCatalogItem(
            id = ToolId.INSURANCE_GUIDE,
            slug = "insurance-guide",
            title = "راهنمای بیمه و مدارک",
            shortTitle = "راهنمای بیمه",
            description = "مسیر استعلام و مدارک معمول را بدون تضمین پوشش بیمه مرور کنید.",
            duration = "حدود ۲ دقیقه",
            iconSymbol = "▣",
            icon = Icons.Outlined.Shield,
            risk = RiskLevel.MEDIUM,
            phase = 2,
            eventPrefix = "insurance"
        ),
        ToolCatalogItem(
            id = ToolId.INSTALLMENT_CALCULATOR,
            slug = "installment-calculator",
            title = "محاسبه‌گر اقساط",
            shortTitle = "محاسبه اقساط",
            description = "پیش‌پرداخت، مانده، مبلغ هر قسط و جمع پرداخت را شفاف محاسبه کنید.",
            duration = "کمتر از ۱ دقیقه",
            iconSymbol = "₮",
            icon = Icons.Outlined.Calculate,
            risk = RiskLevel.LOW,
            phase = 2,
            eventPrefix = "installment"
        ),
        ToolCatalogItem(
            id = ToolId.SERVICE_ROUTER,
            slug = "service-router",
            title = "از دغدغه تا خدمت مرتبط",
            shortTitle = "پیداکردن مسیر",
            description = "اگر نام عمل را نمی‌دانید، از هدف یا دغدغه خود به مسیرهای مرتبط برسید.",
            duration = "حدود ۱ دقیقه",
            iconSymbol = "⌁",
            icon = Icons.Outlined.AltRoute,
            risk = RiskLevel.MEDIUM,
            phase = 2,
            eventPrefix = "router"
        ),
        ToolCatalogItem(
            id = ToolId.WARNING_SIGNS,
            slug = "warning-signs",
            title = "بررسی سطح فوریت علائم هشدار",
            shortTitle = "علائم هشدار",
            description = "سطح فوریت مراجعه را با قواعد ازپیش‌تأییدشده بررسی کنید؛ بدون تشخیص علت.",
            duration = "کمتر از ۱ دقیقه",
            iconSymbol = "!",
            icon = Icons.Filled.Warning,
            risk = RiskLevel.HIGH,
            phase = 2,
            eventPrefix = "warning"
        ),
        ToolCatalogItem(
            id = ToolId.SMART_CONSULTATION,
            slug = "smart-consultation",
            title = "فرم هوشمند درخواست مشاوره",
            shortTitle = "ثبت مشاوره",
            description = "خلاصه اطلاعات قبلی را بازبینی و پس از رضایت صریح برای تیم درمان ارسال کنید.",
            duration = "حدود ۲ دقیقه",
            iconSymbol = "✦",
            icon = Icons.Outlined.ContactPhone,
            risk = RiskLevel.MEDIUM,
            phase = 1,
            eventPrefix = "consultation"
        )
    )

    fun findBySlug(slug: String): ToolCatalogItem? = TOOLS.firstOrNull { it.slug == slug }
    fun findById(id: ToolId): ToolCatalogItem? = TOOLS.firstOrNull { it.id == id }
}
