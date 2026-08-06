package com.example.data.logic

import com.example.data.model.*
import java.util.UUID
import kotlin.math.pow
import kotlin.math.roundToInt

object MedicalCore {

    private fun normalizeDigits(input: String): String {
        return input
            .replace('۰', '0').replace('۱', '1').replace('۲', '2').replace('۳', '3').replace('۴', '4')
            .replace('۵', '5').replace('۶', '6').replace('۷', '7').replace('۸', '8').replace('۹', '9')
            .replace('٠', '0').replace('١', '1').replace('٢', '2').replace('٣', '3').replace('٤', '4')
            .replace('٥', '5').replace('٦', '6').replace('٧', '7').replace('٨', '8').replace('٩', '9')
            .replace(",", "").replace("٬", "").trim()
    }

    fun parseNumber(value: Any?): Double? {
        if (value == null) return null
        val str = normalizeDigits(value.toString())
        return str.toDoubleOrNull()
    }

    private fun getBmiCategory(bmi: Double): Pair<String, String> {
        return when {
            bmi < 18.5 -> "underweight" to "کم‌وزنی"
            bmi < 25.0 -> "healthy" to "محدوده عمومی وزن سالم"
            bmi < 30.0 -> "overweight" to "اضافه‌وزن"
            bmi < 35.0 -> "obesity_1" to "چاقی درجه ۱"
            bmi < 40.0 -> "obesity_2" to "چاقی درجه ۲"
            else -> "obesity_3" to "چاقی درجه ۳"
        }
    }

    fun calculateBmi(
        heightCmInput: Any?,
        weightKgInput: Any?,
        ageInput: Any?,
        conditions: List<String> = emptyList(),
        goal: String = "weight_health",
        pregnant: Boolean = false
    ): ToolOutput {
        val heightCm = parseNumber(heightCmInput)
        val weightKg = parseNumber(weightKgInput)
        val age = parseNumber(ageInput)

        val missing = mutableListOf<String>()
        if (heightCm == null || heightCm <= 0) missing.add("قد")
        if (weightKg == null || weightKg <= 0) missing.add("وزن")
        if (age == null || age <= 0) missing.add("سن")

        if (missing.isNotEmpty()) {
            return ToolOutput(
                toolId = ToolId.BMI_ASSESSMENT.rawValue,
                status = "insufficient_information",
                summary = "برای محاسبه، اطلاعات بیشتری لازم است.",
                missingInformation = missing,
                requiresHumanReview = true
            )
        }

        val h = heightCm!!
        val w = weightKg!!
        val a = age!!

        if (h < 120 || h > 230 || w < 30 || w > 350) {
            return ToolOutput(
                toolId = ToolId.BMI_ASSESSMENT.rawValue,
                status = "insufficient_information",
                summary = "یکی از مقادیر خارج از محدوده قابل بررسی است.",
                warnings = listOf("قد باید بین ۱۲۰ تا ۲۳۰ سانتی‌متر و وزن بین ۳۰ تا ۳۵۰ کیلوگرم باشد."),
                requiresHumanReview = true
            )
        }

        if (a < 18 || pregnant) {
            return ToolOutput(
                toolId = ToolId.BMI_ASSESSMENT.rawValue,
                status = "requires_human_review",
                summary = "این وضعیت به ارزیابی مستقیم پزشک نیاز دارد.",
                explanation = listOf(
                    if (a < 18) "BMI افراد زیر ۱۸ سال باید براساس سن و جنس و نمودارهای رشد تفسیر شود."
                    else "در بارداری، BMI فعلی برای پیشنهاد مسیر کاهش وزن یا جراحی کافی نیست."
                ),
                warnings = listOf("نتیجه عمومی درمانی نمایش داده نشد."),
                requiresHumanReview = true,
                recommendedActions = listOf(
                    RecommendedAction("درخواست مشاوره انسانی", "/tools/smart-consultation", 1)
                ),
                resultData = mapOf("age_group" to if (a < 18) "minor" else "adult", "pregnancy" to pregnant.toString())
            )
        }

        val bmiRaw = w / ((h / 100.0).pow(2.0))
        val bmi = (bmiRaw * 10).roundToInt() / 10.0
        val (catCode, catLabel) = getBmiCategory(bmi)

        val hasMetabolic = conditions.any { it in listOf("type2_diabetes", "hypertension", "sleep_apnea") }
        val conditionLabels = ToolCatalogData.CONDITIONS.filter { it.first in conditions }.map { it.second }

        var pathwayClass = "general_health_review"
        var pathwayLabel = "بررسی سبک زندگی و وضعیت سلامت"
        val explanation = mutableListOf(
            "BMI شما ${String.format("%.1f", bmi)} است و در دسته عمومی «$catLabel» قرار می‌گیرد.",
            "BMI یک ابزار غربالگری است و ترکیب بدن، توزیع چربی و شرایط پزشکی را به‌تنهایی نشان نمی‌دهد."
        )

        when {
            bmi >= 35.0 -> {
                pathwayClass = "bariatric_consultation_relevant"
                pathwayLabel = "بررسی تخصصی جراحی متابولیک و چاقی قابل طرح است"
                explanation.add("براساس راهنمای ASMBS/IFSO، در BMI برابر یا بالاتر از ۳۵، ارزیابی جراحی متابولیک می‌تواند مطرح باشد؛ انتخاب روش پس از ارزیابی کامل انجام می‌شود.")
            }
            bmi >= 30.0 && hasMetabolic -> {
                pathwayClass = "metabolic_surgery_may_be_considered"
                pathwayLabel = "ارزیابی درمان پزشکی و جراحی متابولیک قابل طرح است"
                explanation.add("وجود بیماری متابولیک همراه با BMI بین ۳۰ تا ۳۴٫۹ می‌تواند بررسی جراحی متابولیک را در کنار گزینه‌های غیرجراحی مطرح کند.")
            }
            bmi >= 30.0 -> {
                pathwayClass = "medical_weight_management_first"
                pathwayLabel = "درمان پزشکی چاقی و پاسخ به روش‌های غیرجراحی باید بررسی شود"
                explanation.add("در این محدوده، سابقه تلاش‌های کاهش وزن، بیماری‌های همراه و پایداری نتیجه روش‌های غیرجراحی در تصمیم‌گیری مهم‌اند.")
            }
            bmi >= 27.0 && hasMetabolic -> {
                pathwayClass = "medical_obesity_care_may_be_considered"
                pathwayLabel = "بررسی درمان پزشکی وزن قابل طرح است"
                explanation.add("برخی درمان‌های دارویی وزن در بزرگسالان دارای اضافه‌وزن و حداقل یک بیماری مرتبط ممکن است پس از ارزیابی پزشک مطرح شوند.")
            }
            bmi < 30.0 && goal == "localized_fat" -> {
                pathwayClass = "body_contouring_question"
                pathwayLabel = "ارزیابی پیکرتراشی برای چربی موضعی قابل طرح است"
                explanation.add("لیپوساکشن درمان چاقی یا روش کاهش وزن نیست؛ فقط برای فرم‌دهی چربی موضعی و پس از ارزیابی پوست و سلامت عمومی بررسی می‌شود.")
            }
        }

        return ToolOutput(
            toolId = ToolId.BMI_ASSESSMENT.rawValue,
            status = "completed",
            summary = pathwayLabel,
            explanation = explanation,
            warnings = listOf("این نتیجه تشخیص، تجویز دارو یا تأیید کاندیداتوری جراحی نیست."),
            requiresHumanReview = true,
            recommendedActions = listOf(
                RecommendedAction("تکمیل ارزیابی اولیه", "/tools/preliminary-assessment", 1),
                RecommendedAction("مقایسه روش‌ها", "/tools/bariatric-comparison", 2),
                RecommendedAction("ثبت درخواست مشاوره", "/tools/smart-consultation", 3)
            ),
            resultData = mapOf(
                "bmi" to bmi.toString(),
                "category_code" to catCode,
                "category_label" to catLabel,
                "pathway_class" to pathwayClass,
                "pathway_label" to pathwayLabel,
                "declared_conditions" to conditionLabels.joinToString("، "),
                "source_version" to ToolCatalogData.SOURCE_VERSION
            )
        )
    }

    fun compareProcedures(proceduresRequested: List<String>, priority: String): ToolOutput {
        val validKeys = ToolCatalogData.PROCEDURES.keys
        val selected = proceduresRequested.filter { it in validKeys }.ifEmpty { listOf("sleeve", "classic_bypass") }

        val details = selected.mapNotNull { key ->
            ToolCatalogData.PROCEDURES[key]?.let { key to it }
        }

        return ToolOutput(
            toolId = ToolId.BARIATRIC_COMPARISON.rawValue,
            status = "completed",
            summary = "تفاوت‌ها بر اساس اولویت شما مرتب شد.",
            explanation = listOf(
                "این مقایسه آموزشی است و نتیجه «برنده» تولید نمی‌کند.",
                "منبع و تاریخ بازبینی برای تمام ابعاد یکسان نگه داشته شده است."
            ),
            requiresHumanReview = true,
            recommendedActions = listOf(
                RecommendedAction("تکمیل ارزیابی اولیه", "/tools/preliminary-assessment", 1),
                RecommendedAction("ثبت مشاوره", "/tools/smart-consultation", 2)
            ),
            resultData = mapOf(
                "procedures" to details.joinToString(";") { "${it.first}:${it.second.name}" },
                "priority" to priority
            )
        )
    }

    fun preliminaryAssessment(
        heightCm: Any?,
        weightKg: Any?,
        age: Any?,
        conditions: List<String>,
        reflux: Boolean,
        priorSurgery: Boolean,
        pregnancyPlan: Boolean,
        longTermFollowup: Boolean,
        goal: String = "weight_health"
    ): ToolOutput {
        val bmiOut = calculateBmi(heightCm, weightKg, age, conditions, goal, false)
        if (bmiOut.status == "insufficient_information" || bmiOut.status == "requires_human_review") {
            return bmiOut.copy(toolId = ToolId.PRELIMINARY_ASSESSMENT.rawValue)
        }

        val bmiVal = bmiOut.resultData["bmi"]?.toDoubleOrNull() ?: 25.0
        val pathwayLabel = bmiOut.resultData["pathway_label"] ?: ""
        val declaredConditions = bmiOut.resultData["declared_conditions"] ?: ""

        val factors = mutableListOf("BMI محاسبه‌شده: $bmiVal")
        if (declaredConditions.isNotEmpty()) factors.add(declaredConditions)
        if (reflux) factors.add("رفلاکس گزارش‌شده")
        if (priorSurgery) factors.add("سابقه جراحی چاقی")
        if (pregnancyPlan) factors.add("برنامه بارداری")

        val potentialReviews = when {
            bmiVal >= 35.0 -> listOf("ارزیابی جراحی چاقی", if (reflux) "بررسی اختصاصی رفلاکس پیش از انتخاب روش" else "مقایسه اسلیو و بای‌پس")
            bmiVal >= 30.0 -> listOf("درمان پزشکی چاقی", "ارزیابی متابولیک و پاسخ به روش‌های قبلی")
            else -> listOf("ارزیابی تغذیه و درمان پزشکی", if (goal == "localized_fat") "ارزیابی جداگانه فرم‌دهی بدن" else "برنامه پایدار مدیریت وزن")
        }

        val warnings = if (pregnancyPlan) listOf("زمان‌بندی بارداری باید در انتخاب و زمان درمان بررسی شود.") else emptyList()

        return ToolOutput(
            toolId = ToolId.PRELIMINARY_ASSESSMENT.rawValue,
            status = "completed",
            summary = if (longTermFollowup) "اطلاعات برای یک جمع‌بندی اولیه کافی است." else "بررسی مستقیم پزشک برای تصمیم‌گیری ضروری است.",
            explanation = listOf(
                "مسیر عمومی BMI: $pathwayLabel",
                "روش نهایی فقط پس از معاینه، بررسی آزمایش‌ها و گفت‌وگو درباره ترجیحات انتخاب می‌شود."
            ),
            warnings = warnings,
            requiresHumanReview = true,
            recommendedActions = listOf(
                RecommendedAction("ارسال این خلاصه برای مشاوره", "/tools/smart-consultation", 1)
            ),
            resultData = mapOf(
                "sufficiency" to if (longTermFollowup) "sufficient_for_initial_summary" else "direct_review_needed",
                "factors" to factors.joinToString(" | "),
                "reviews" to potentialReviews.joinToString(" | ")
            )
        )
    }

    fun weightLossMetrics(
        heightCmInput: Any?,
        surgeryWeightInput: Any?,
        currentWeightInput: Any?,
        procedureKey: String = "sleeve"
    ): ToolOutput {
        val h = parseNumber(heightCmInput)
        val sWeight = parseNumber(surgeryWeightInput)
        val cWeight = parseNumber(currentWeightInput)

        if (h == null || sWeight == null || cWeight == null || h <= 0 || sWeight <= 0 || cWeight <= 0) {
            return ToolOutput(
                toolId = ToolId.WEIGHT_LOSS_TRACKER.rawValue,
                status = "insufficient_information",
                summary = "اطلاعات وزن و قد کامل نیست.",
                missingInformation = listOf("قد", "وزن روز عمل", "وزن فعلی")
            )
        }

        val totalLoss = (sWeight - cWeight * 10).roundToInt() / 10.0 // kg
        val totalLossPct = ((totalLoss / sWeight) * 1000).roundToInt() / 10.0
        val initialBmi = ((sWeight / (h / 100.0).pow(2.0)) * 10).roundToInt() / 10.0
        val currentBmi = ((cWeight / (h / 100.0).pow(2.0)) * 10).roundToInt() / 10.0
        val bmiChange = ((initialBmi - currentBmi) * 10).roundToInt() / 10.0

        val warnings = if (totalLossPct > 30.0) {
            listOf("برای تفسیر سرعت کاهش وزن، زمان گذشته از عمل و علائم همراه باید بررسی شود.")
        } else emptyList()

        val verb = if (totalLoss >= 0) "کاهش" else "افزایش"

        return ToolOutput(
            toolId = ToolId.WEIGHT_LOSS_TRACKER.rawValue,
            status = "completed",
            summary = "تا امروز ${kotlin.math.abs(totalLoss)} کیلوگرم $verb ثبت شده است.",
            explanation = listOf("این اعداد روند را توصیف می‌کنند و کیفیت نتیجه یا سلامت فرد را به‌تنهایی مشخص نمی‌کنند."),
            warnings = warnings,
            requiresHumanReview = false,
            recommendedActions = listOf(
                RecommendedAction("آماده‌سازی خلاصه پیگیری", "/tools/smart-consultation", 1)
            ),
            resultData = mapOf(
                "procedure" to procedureKey,
                "current_weight" to cWeight.toString(),
                "total_loss" to totalLoss.toString(),
                "total_loss_pct" to totalLossPct.toString(),
                "initial_bmi" to initialBmi.toString(),
                "current_bmi" to currentBmi.toString(),
                "bmi_change" to bmiChange.toString()
            )
        )
    }

    fun nutritionStage(
        daysAfterSurgeryInput: Any?,
        shortnessOfBreath: Boolean = false,
        severePain: Boolean = false,
        persistentVomiting: Boolean = false,
        cannotDrink: Boolean = false
    ): ToolOutput {
        val days = parseNumber(daysAfterSurgeryInput)?.toInt()
        if (days == null || days < 0) {
            return ToolOutput(
                toolId = ToolId.POST_OP_NUTRITION.rawValue,
                status = "insufficient_information",
                summary = "تعداد روز گذشته از عمل معتبر نیست.",
                missingInformation = listOf("روز گذشته از عمل")
            )
        }

        val (code, title, goal) = when {
            days <= 2 -> Triple("clear_liquids", "مایعات شفاف اولیه", "حفظ آب‌رسانی طبق دستور تیم درمان")
            days <= 14 -> Triple("full_liquids", "مایعات کامل و پروتئینی", "افزایش تدریجی پروتئین و مایعات")
            days <= 28 -> Triple("pureed_soft", "پوره و غذای نرم", "تحمل بافت نرم و وعده‌های کوچک")
            else -> Triple("long_term", "تثبیت الگوی غذایی", "پروتئین، آب‌رسانی و رفتار غذایی پایدار")
        }

        val urgent = shortnessOfBreath || severePain
        val rapidReview = persistentVomiting || cannotDrink

        val status = if (urgent) "urgent" else if (rapidReview) "requires_human_review" else "completed"

        val warnings = when {
            urgent -> listOf("با وجود علامت شدید، ادامه راهنمای غذایی کافی نیست؛ ارزیابی فوری لازم است.")
            rapidReview -> listOf("استفراغ مداوم یا ناتوانی در نوشیدن نیازمند تماس سریع با تیم درمان است.")
            else -> listOf("در صورت درد شدید، تنگی نفس، خونریزی یا ناتوانی در نوشیدن، فوراً ارزیابی پزشکی بگیرید.")
        }

        return ToolOutput(
            toolId = ToolId.POST_OP_NUTRITION.rawValue,
            status = status,
            summary = "در حالت عمومی، روز $days در مرحله «$title» قرار می‌گیرد.",
            explanation = listOf("پروتکل اختصاصی جراح و متخصص تغذیه همیشه بر این راهنمای عمومی اولویت دارد."),
            warnings = warnings,
            requiresHumanReview = urgent || rapidReview,
            recommendedActions = listOf(
                RecommendedAction("بررسی علائم هشدار", "/tools/warning-signs", 1)
            ),
            resultData = mapOf(
                "day" to days.toString(),
                "stage_code" to code,
                "stage_title" to title,
                "goal" to goal
            )
        )
    }

    fun insuranceGuidance(baseInsurance: String, supplementalInsurance: String): ToolOutput {
        val hasData = baseInsurance.isNotBlank() || supplementalInsurance.isNotBlank()
        return ToolOutput(
            toolId = ToolId.INSURANCE_GUIDE.rawValue,
            status = if (hasData) "completed" else "insufficient_information",
            summary = if (hasData) "امکان بررسی پوشش وجود دارد، اما نتیجه به استعلام مستقیم وابسته است."
            else "برای راهنمایی دقیق‌تر نام بیمه پایه یا تکمیلی را وارد کنید.",
            warnings = listOf("این راهنما تضمین پوشش یا مبلغ پرداختی بیمه نیست."),
            requiresHumanReview = true,
            recommendedActions = listOf(
                RecommendedAction("ارسال درخواست بررسی بیمه", "/tools/smart-consultation", 1)
            ),
            resultData = mapOf(
                "base_insurance" to baseInsurance,
                "supplemental_insurance" to supplementalInsurance
            )
        )
    }

    fun installmentPlan(
        totalAmountInput: Any?,
        downPaymentInput: Any?,
        installmentsInput: Any?,
        feeRateInput: Any? = 0
    ): ToolOutput {
        val total = parseNumber(totalAmountInput)
        val down = parseNumber(downPaymentInput) ?: 0.0
        val installments = parseNumber(installmentsInput)?.toInt() ?: 0
        val feeRate = parseNumber(feeRateInput) ?: 0.0

        if (total == null || total <= 0 || installments < 1 || down < 0 || down >= total || feeRate < 0) {
            return ToolOutput(
                toolId = ToolId.INSTALLMENT_CALCULATOR.rawValue,
                status = "insufficient_information",
                summary = "مقادیر برای محاسبه کامل نیست یا معتبر نیست.",
                warnings = listOf("مبلغ کل، پیش‌پرداخت و تعداد اقساط را بازبینی کنید.")
            )
        }

        val balance = total - down
        val fee = (balance * feeRate / 100.0).roundToInt()
        val payable = balance + fee
        val perInstallment = kotlin.math.ceil(payable / installments).toInt()

        val formatPerInst = String.format("%,d", perInstallment)

        return ToolOutput(
            toolId = ToolId.INSTALLMENT_CALCULATOR.rawValue,
            status = "completed",
            summary = "مبلغ تقریبی هر قسط $formatPerInst تومان است.",
            explanation = listOf("محاسبه انجام شده و تا تأیید اپراتور پیشنهاد رسمی محسوب نمی‌شود."),
            warnings = listOf("قیمت پایه و شرایط اقساط باید در زمان ثبت نهایی تأیید شوند."),
            requiresHumanReview = true,
            recommendedActions = listOf(
                RecommendedAction("درخواست تأیید شرایط", "/tools/smart-consultation", 1)
            ),
            resultData = mapOf(
                "total_amount" to total.toLong().toString(),
                "down_payment" to down.toLong().toString(),
                "balance" to balance.toLong().toString(),
                "installments" to installments.toString(),
                "per_installment" to perInstallment.toString(),
                "total_payable" to (down + payable).toLong().toString()
            )
        )
    }

    fun routeService(intent: String): ToolOutput {
        val routes = when (intent) {
            "weight_loss" -> listOf(
                RecommendedAction("ارزیابی BMI", "/tools/bmi-assessment", 1),
                RecommendedAction("مقایسه روش‌ها", "/tools/bariatric-comparison", 2),
                RecommendedAction("ارزیابی اولیه", "/tools/preliminary-assessment", 3)
            )
            "localized_fat" -> listOf(
                RecommendedAction("ارزیابی BMI با هدف چربی موضعی", "/tools/bmi-assessment", 1),
                RecommendedAction("ثبت مشاوره", "/tools/smart-consultation", 2)
            )
            "post_op" -> listOf(
                RecommendedAction("راهنمای تغذیه", "/tools/post-op-nutrition", 1),
                RecommendedAction("رهگیری وزن", "/tools/weight-loss-tracker", 2),
                RecommendedAction("علائم هشدار", "/tools/warning-signs", 3)
            )
            "cost_insurance" -> listOf(
                RecommendedAction("راهنمای بیمه", "/tools/insurance-guide", 1),
                RecommendedAction("محاسبه اقساط", "/tools/installment-calculator", 2),
                RecommendedAction("ثبت مشاوره", "/tools/smart-consultation", 3)
            )
            "abdominal_symptoms" -> listOf(
                RecommendedAction("بررسی علائم هشدار", "/tools/warning-signs", 1),
                RecommendedAction("ثبت مشاوره", "/tools/smart-consultation", 2)
            )
            else -> listOf(
                RecommendedAction("هاب ابزارها", "/tools", 1),
                RecommendedAction("ثبت مشاوره", "/tools/smart-consultation", 2)
            )
        }

        return ToolOutput(
            toolId = ToolId.SERVICE_ROUTER.rawValue,
            status = "completed",
            summary = "این مسیرها به موضوع اعلام‌شده نزدیک‌ترند.",
            explanation = listOf("پیشنهاد مسیر به‌معنای تشخیص بیماری یا انتخاب عمل نیست."),
            recommendedActions = routes
        )
    }

    fun screenWarningSigns(
        shortnessOfBreath: Boolean = false,
        reducedConsciousness: Boolean = false,
        heavyBleeding: Boolean = false,
        severePain: Boolean = false,
        persistentVomiting: Boolean = false,
        cannotDrink: Boolean = false,
        highFever: Boolean = false,
        worsening: Boolean = false,
        severityScore: Int = 0
    ): ToolOutput {
        val emergency = shortnessOfBreath || reducedConsciousness || heavyBleeding || severePain
        val rapid = persistentVomiting || cannotDrink || highFever
        val contact = worsening || severityScore >= 6

        val (level, label) = when {
            emergency -> "emergency" to "مراجعه فوری به اورژانس"
            rapid -> "rapid_medical_evaluation" to "ارزیابی پزشکی سریع"
            contact -> "contact_care_team" to "تماس با تیم درمان"
            else -> "general_guidance" to "راهنمایی عمومی و پایش"
        }

        val status = if (emergency) "urgent" else if (rapid || contact) "requires_human_review" else "completed"

        val explanation = if (emergency) {
            listOf("پیش از خواندن توضیحات بیشتر، برای دریافت کمک فوری اقدام کنید.")
        } else {
            listOf("اگر علائم بدتر شد یا علامت جدیدی اضافه شد، سطح اقدام باید دوباره بررسی شود.")
        }

        val actions = if (emergency) {
            listOf(RecommendedAction("تماس با اورژانس محل زندگی", "tel:112", 1))
        } else {
            listOf(RecommendedAction("ارسال خلاصه برای تیم درمان", "/tools/smart-consultation", 1))
        }

        return ToolOutput(
            toolId = ToolId.WARNING_SIGNS.rawValue,
            status = status,
            summary = label,
            explanation = explanation,
            warnings = listOf(
                "این ابزار علت علامت را تشخیص نمی‌دهد.",
                "اگر درباره شدت علامت مطمئن نیستید، مسیر ایمن‌تر را انتخاب کنید."
            ),
            requiresHumanReview = emergency || rapid || contact,
            recommendedActions = actions,
            resultData = mapOf("urgency_level" to level, "urgency_label" to label)
        )
    }
}
