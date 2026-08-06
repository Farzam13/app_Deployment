package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AssessmentRepository
import com.example.data.logic.MedicalCore
import com.example.data.model.ToolCatalogData
import com.example.data.model.ToolId
import com.example.data.model.ToolOutput
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolDetailScreen(
    slug: String,
    repository: AssessmentRepository,
    onNavigateBack: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val tool = remember(slug) { ToolCatalogData.findBySlug(slug) ?: ToolCatalogData.TOOLS.first() }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Form inputs state
    var heightCm by remember { mutableStateOf("170") }
    var weightKg by remember { mutableStateOf("95") }
    var age by remember { mutableStateOf("35") }
    var selectedConditions by remember { mutableStateOf(listOf<String>()) }
    var goal by remember { mutableStateOf("weight_health") }
    var pregnant by remember { mutableStateOf(false) }

    // Comparison tool state
    var selectedProcedures by remember { mutableStateOf(listOf("sleeve", "classic_bypass")) }
    var priorityFilter by remember { mutableStateOf("overview") }

    // Preliminary tool state
    var reflux by remember { mutableStateOf(false) }
    var priorSurgery by remember { mutableStateOf(false) }
    var pregnancyPlan by remember { mutableStateOf(false) }
    var longTermFollowup by remember { mutableStateOf(true) }

    // Weight loss tracker state
    var surgeryWeightKg by remember { mutableStateOf("115") }
    var currentWeightKg by remember { mutableStateOf("82") }
    var selectedProcedureKey by remember { mutableStateOf("sleeve") }

    // Nutrition stage state
    var daysAfterSurgery by remember { mutableStateOf("14") }
    var shortnessOfBreath by remember { mutableStateOf(false) }
    var severePain by remember { mutableStateOf(false) }
    var persistentVomiting by remember { mutableStateOf(false) }
    var cannotDrink by remember { mutableStateOf(false) }

    // Insurance state
    var baseInsurance by remember { mutableStateOf("تأمین اجتماعی") }
    var supplementalInsurance by remember { mutableStateOf("بیمه تکمیلی ایران") }

    // Installment calculator state
    var totalAmount by remember { mutableStateOf("80000000") }
    var downPayment by remember { mutableStateOf("30000000") }
    var installmentsCount by remember { mutableStateOf("10") }
    var feeRate by remember { mutableStateOf("0") }

    // Router state
    var routerIntent by remember { mutableStateOf("weight_loss") }

    // Warning signs state
    var warningReducedConsciousness by remember { mutableStateOf(false) }
    var warningHeavyBleeding by remember { mutableStateOf(false) }
    var warningHighFever by remember { mutableStateOf(false) }
    var warningWorsening by remember { mutableStateOf(false) }

    // Smart consultation form state
    var patientName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var consultationNotes by remember { mutableStateOf("") }

    // Output state
    var outputResult by remember { mutableStateOf<ToolOutput?>(null) }

    fun runCalculation() {
        val result = when (tool.id) {
            ToolId.BMI_ASSESSMENT -> MedicalCore.calculateBmi(
                heightCmInput = heightCm,
                weightKgInput = weightKg,
                ageInput = age,
                conditions = selectedConditions,
                goal = goal,
                pregnant = pregnant
            )
            ToolId.BARIATRIC_COMPARISON -> MedicalCore.compareProcedures(
                proceduresRequested = selectedProcedures,
                priority = priorityFilter
            )
            ToolId.PRELIMINARY_ASSESSMENT -> MedicalCore.preliminaryAssessment(
                heightCm = heightCm,
                weightKg = weightKg,
                age = age,
                conditions = selectedConditions,
                reflux = reflux,
                priorSurgery = priorSurgery,
                pregnancyPlan = pregnancyPlan,
                longTermFollowup = longTermFollowup,
                goal = goal
            )
            ToolId.WEIGHT_LOSS_TRACKER -> {
                val res = MedicalCore.weightLossMetrics(
                    heightCmInput = heightCm,
                    surgeryWeightInput = surgeryWeightKg,
                    currentWeightInput = currentWeightKg,
                    procedureKey = selectedProcedureKey
                )
                if (res.status == "completed") {
                    coroutineScope.launch {
                        repository.saveWeightLog(
                            heightCm = heightCm.toDoubleOrNull() ?: 170.0,
                            surgeryWeightKg = surgeryWeightKg.toDoubleOrNull() ?: 100.0,
                            currentWeightKg = currentWeightKg.toDoubleOrNull() ?: 80.0,
                            bmi = res.resultData["current_bmi"]?.toDoubleOrNull() ?: 0.0,
                            procedureName = selectedProcedureKey
                        )
                    }
                }
                res
            }
            ToolId.POST_OP_NUTRITION -> MedicalCore.nutritionStage(
                daysAfterSurgeryInput = daysAfterSurgery,
                shortnessOfBreath = shortnessOfBreath,
                severePain = severePain,
                persistentVomiting = persistentVomiting,
                cannotDrink = cannotDrink
            )
            ToolId.INSURANCE_GUIDE -> MedicalCore.insuranceGuidance(
                baseInsurance = baseInsurance,
                supplementalInsurance = supplementalInsurance
            )
            ToolId.INSTALLMENT_CALCULATOR -> MedicalCore.installmentPlan(
                totalAmountInput = totalAmount,
                downPaymentInput = downPayment,
                installmentsInput = installmentsCount,
                feeRateInput = feeRate
            )
            ToolId.SERVICE_ROUTER -> MedicalCore.routeService(intent = routerIntent)
            ToolId.WARNING_SIGNS -> MedicalCore.screenWarningSigns(
                shortnessOfBreath = shortnessOfBreath,
                reducedConsciousness = warningReducedConsciousness,
                heavyBleeding = warningHeavyBleeding,
                severePain = severePain,
                persistentVomiting = persistentVomiting,
                cannotDrink = cannotDrink,
                highFever = warningHighFever,
                worsening = warningWorsening
            )
            ToolId.SMART_CONSULTATION -> {
                ToolOutput(
                    toolId = ToolId.SMART_CONSULTATION.rawValue,
                    status = if (patientName.isNotBlank() && phoneNumber.isNotBlank()) "completed" else "insufficient_information",
                    summary = if (patientName.isNotBlank() && phoneNumber.isNotBlank()) "درخواست مشاوره شما آماده ثبت است." else "نام و شماره تماس الزامی است.",
                    missingInformation = if (patientName.isBlank() || phoneNumber.isBlank()) listOf("نام بیمار", "شماره تماس") else emptyList(),
                    requiresHumanReview = true
                )
            }
        }

        outputResult = result
        coroutineScope.launch {
            repository.saveHistory(tool.id.rawValue, result.summary, result.status)
        }
    }

    // Auto-run calculation initially for smooth UX
    LaunchedEffect(slug) {
        runCalculation()
    }

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

        // Tool Title Header
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("back_button")) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = Burgundy800)
                        }
                        Surface(shape = RoundedCornerShape(50), color = Burgundy100) {
                            Text(
                                text = tool.duration,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                fontSize = 11.sp,
                                color = Burgundy800,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(text = tool.title, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Ink)
                    Text(text = tool.description, fontSize = 13.sp, color = InkSoft, lineHeight = 19.sp)
                }
            }
        }

        // Form Section based on Tool ID
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(text = "ورود اطلاعات", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Ink)

                    when (tool.id) {
                        ToolId.BMI_ASSESSMENT, ToolId.PRELIMINARY_ASSESSMENT -> {
                            FormNumberField(label = "قد (سانتی‌متر)", value = heightCm, onValueChange = { heightCm = it }, unit = "cm")
                            FormNumberField(label = "وزن (کیلوگرم)", value = weightKg, onValueChange = { weightKg = it }, unit = "kg")
                            FormNumberField(label = "سن", value = age, onValueChange = { age = it }, unit = "سال")

                            Text(text = "بیماری‌های همراه (اختیاری):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                            ChoiceGridMultiSelect(
                                options = ToolCatalogData.CONDITIONS,
                                selectedValues = selectedConditions,
                                onToggle = { item ->
                                    selectedConditions = if (item in selectedConditions) selectedConditions - item else selectedConditions + item
                                }
                            )

                            YesNoRadioGroup(label = "آیا باردار هستید؟", selected = pregnant, onSelect = { pregnant = it })

                            if (tool.id == ToolId.PRELIMINARY_ASSESSMENT) {
                                YesNoRadioGroup(label = "سابقه رفلاکس شدید؟", selected = reflux, onSelect = { reflux = it })
                                YesNoRadioGroup(label = "سابقه جراحی قبلی چاقی؟", selected = priorSurgery, onSelect = { priorSurgery = it })
                                YesNoRadioGroup(label = "برنامه بارداری در ۲ سال آینده؟", selected = pregnancyPlan, onSelect = { pregnancyPlan = it })
                            }
                        }

                        ToolId.BARIATRIC_COMPARISON -> {
                            Text(text = "انتخاب روش‌های مورد نظر جهت مقایسه:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                            val procOptions = ToolCatalogData.PROCEDURES.map { it.key to it.value.name }
                            ChoiceGridMultiSelect(
                                options = procOptions,
                                selectedValues = selectedProcedures,
                                onToggle = { item ->
                                    selectedProcedures = if (item in selectedProcedures) selectedProcedures - item else selectedProcedures + item
                                }
                            )
                        }

                        ToolId.WEIGHT_LOSS_TRACKER -> {
                            FormNumberField(label = "قد (سانتی‌متر)", value = heightCm, onValueChange = { heightCm = it }, unit = "cm")
                            FormNumberField(label = "وزن روز عمل (کیلوگرم)", value = surgeryWeightKg, onValueChange = { surgeryWeightKg = it }, unit = "kg")
                            FormNumberField(label = "وزن فعلی (کیلوگرم)", value = currentWeightKg, onValueChange = { currentWeightKg = it }, unit = "kg")
                        }

                        ToolId.POST_OP_NUTRITION -> {
                            FormNumberField(label = "تعداد روز گذشته از عمل", value = daysAfterSurgery, onValueChange = { daysAfterSurgery = it }, unit = "روز")
                            YesNoRadioGroup(label = "تنگی نفس یا درد شدید؟", selected = shortnessOfBreath, onSelect = { shortnessOfBreath = it; severePain = it })
                            YesNoRadioGroup(label = "استفراغ مداوم یا عدم توانایی نوشیدن؟", selected = persistentVomiting, onSelect = { persistentVomiting = it; cannotDrink = it })
                        }

                        ToolId.INSURANCE_GUIDE -> {
                            FormNumberField(label = "نام بیمه پایه (مانند تأمین اجتماعی، سلامت، نیروهای مسلح)", value = baseInsurance, onValueChange = { baseInsurance = it })
                            FormNumberField(label = "نام بیمه تکمیلی (مانند ایران، آتیه‌سازان، دانا)", value = supplementalInsurance, onValueChange = { supplementalInsurance = it })
                        }

                        ToolId.INSTALLMENT_CALCULATOR -> {
                            FormNumberField(label = "مبلغ کل هزینه (تومان)", value = totalAmount, onValueChange = { totalAmount = it })
                            FormNumberField(label = "پیش‌پرداخت (تومان)", value = downPayment, onValueChange = { downPayment = it })
                            FormNumberField(label = "تعداد اقساط (ماه)", value = installmentsCount, onValueChange = { installmentsCount = it })
                        }

                        ToolId.SERVICE_ROUTER -> {
                            Text(text = "دغدغه اصلی شما چیست؟", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                            val routerOptions = listOf(
                                "weight_loss" to "کاهش وزن عمومی و درمان چاقی",
                                "localized_fat" to "چربی موضعی و پیکرتراشی",
                                "post_op" to "مراقبت‌های بعد از عمل (تغذیه/وزن)",
                                "cost_insurance" to "هزینه‌ها، بیمه و شرایط اقساط",
                                "abdominal_symptoms" to "علائم شکمی و هشدارهای پزشکی"
                            )
                            routerOptions.forEach { (key, label) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { routerIntent = key }
                                        .padding(vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    RadioButton(
                                        selected = routerIntent == key,
                                        onClick = { routerIntent = key },
                                        colors = RadioButtonDefaults.colors(selectedColor = Burgundy800)
                                    )
                                    Text(text = label, fontSize = 14.sp, color = Ink)
                                }
                            }
                        }

                        ToolId.WARNING_SIGNS -> {
                            Text(text = "لطفاً علائم فعلی خود را مشخص کنید:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Ink)
                            val warningList = listOf(
                                "shortness" to "تنگی نفس یا درد شدید سینه/شکم",
                                "vomiting" to "استفراغ مداوم یا عدم توانایی نوشیدن مایعات",
                                "fever" to "تب بالا همراه با لرز",
                                "bleeding" to "خونریزی یا تغییر سطح هوشیاری",
                                "worsening" to "بدترشدن تدریجی علائم قبلی"
                            )
                            warningList.forEach { (key, label) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val isChecked = when (key) {
                                        "shortness" -> shortnessOfBreath
                                        "vomiting" -> persistentVomiting
                                        "fever" -> warningHighFever
                                        "bleeding" -> warningHeavyBleeding
                                        "worsening" -> warningWorsening
                                        else -> false
                                    }
                                    Checkbox(
                                        checked = isChecked,
                                        onCheckedChange = { check ->
                                            when (key) {
                                                "shortness" -> { shortnessOfBreath = check; severePain = check }
                                                "vomiting" -> { persistentVomiting = check; cannotDrink = check }
                                                "fever" -> warningHighFever = check
                                                "bleeding" -> { warningHeavyBleeding = check; warningReducedConsciousness = check }
                                                "worsening" -> warningWorsening = check
                                            }
                                        },
                                        colors = CheckboxDefaults.colors(checkedColor = Burgundy800)
                                    )
                                    Text(text = label, fontSize = 13.sp, color = Ink)
                                }
                            }
                        }

                        ToolId.SMART_CONSULTATION -> {
                            FormNumberField(label = "نام و نام خانوادگی بیمار *", value = patientName, onValueChange = { patientName = it })
                            FormNumberField(label = "شماره همراه (جهت تماس) *", value = phoneNumber, onValueChange = { phoneNumber = it })
                            FormNumberField(label = "توضیحات و سؤالات شما (اختیاری)", value = consultationNotes, onValueChange = { consultationNotes = it })
                        }
                    }

                    Button(
                        onClick = {
                            runCalculation()
                            if (tool.id == ToolId.SMART_CONSULTATION && patientName.isNotBlank() && phoneNumber.isNotBlank()) {
                                coroutineScope.launch {
                                    repository.saveConsultation(
                                        name = patientName,
                                        phone = phoneNumber,
                                        contextStr = "درخواست مشاوره ثبت‌شده برای $patientName",
                                        notes = consultationNotes
                                    )
                                    Toast.makeText(context, "درخواست مشاوره با موفقیت ثبت شد", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_tool_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Burgundy800,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = if (tool.id == ToolId.SMART_CONSULTATION) "ثبت نهائی درخواست مشاوره" else "محاسبه و دریافت نتیجه",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        // Result Section
        if (outputResult != null) {
            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                    ResultViewCard(
                        output = outputResult!!,
                        onActionClick = { route ->
                            if (route.startsWith("/tools/")) {
                                val targetSlug = route.removePrefix("/tools/")
                                onNavigateToRoute(targetSlug)
                            } else {
                                onNavigateToRoute("tools")
                            }
                        }
                    )
                }
            }
        }
    }
}
