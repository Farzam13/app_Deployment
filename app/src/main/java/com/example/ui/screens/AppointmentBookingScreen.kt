package com.example.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.data.local.AssessmentRepository
import com.example.ui.components.FormNumberField
import com.example.ui.components.YesNoRadioGroup
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.pow
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentBookingScreen(
    repository: AssessmentRepository,
    onNavigateBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var currentStep by remember { mutableStateOf(1) }
    
    // Form State
    var selectedReason by remember { mutableStateOf("") }
    var selectedVisitType by remember { mutableStateOf("") }
    var selectedClinic by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    
    // Personal Info
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var isNewPatient by remember { mutableStateOf(true) }
    var referralSource by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var contactConsent by remember { mutableStateOf(false) }
    var saveConsent by remember { mutableStateOf(false) }

    // Conditional Fields - Bariatric
    var height by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var priorSurgery by remember { mutableStateOf(false) }
    var priorSurgeryType by remember { mutableStateOf("") }
    var hasReflux by remember { mutableStateOf(false) }
    var hasDiabetes by remember { mutableStateOf(false) }
    var insurance by remember { mutableStateOf("") }
    var mainGoal by remember { mutableStateOf("") }

    // Conditional Fields - Post-Op
    var opProcedureType by remember { mutableStateOf("") }
    var opDate by remember { mutableStateOf("") }
    var followUpReason by remember { mutableStateOf("") }
    var warningSigns by remember { mutableStateOf("") }
    var needsUrgentContact by remember { mutableStateOf(false) }
    
    // Result Tracking
    var trackingCode by remember { mutableStateOf("") }
    
    // Validation Errors
    var validationError by remember { mutableStateOf("") }

    val isBariatric = selectedReason in listOf("مشاوره جراحی چاقی", "بررسی اسلیو معده", "بررسی مینی‌بای‌پس معده", "بررسی بای‌پس کلاسیک")
    val isPostOp = selectedReason == "چکاپ و پیگیری پس از عمل"

    fun validateStep(): Boolean {
        validationError = ""
        when (currentStep) {
            1 -> {
                if (selectedReason.isBlank()) {
                    validationError = "لطفاً علت مراجعه را انتخاب کنید."
                    return false
                }
            }
            2 -> {
                if (selectedVisitType.isBlank() || selectedClinic.isBlank()) {
                    validationError = "لطفاً نوع ویزیت و مطب را انتخاب کنید."
                    return false
                }
            }
            3 -> {
                if (selectedDate.isBlank() || selectedTime.isBlank()) {
                    validationError = "لطفاً تاریخ و ساعت را انتخاب کنید."
                    return false
                }
            }
            4 -> {
                if (firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank() || age.isBlank()) {
                    validationError = "لطفاً تمامی فیلدهای ستاره‌دار را پر کنید."
                    return false
                }
                if (!phoneNumber.matches(Regex("^(09|\\\\+989|00989)\\\\d{9}\$"))) {
                    validationError = "شماره موبایل نامعتبر است."
                    return false
                }
                if (!contactConsent || !saveConsent) {
                    validationError = "تأیید رضایت‌نامه‌ها الزامی است."
                    return false
                }
            }
            5 -> {
                if (isBariatric) {
                    val h = height.toDoubleOrNull() ?: 0.0
                    val w = weight.toDoubleOrNull() ?: 0.0
                    if (h !in 120.0..230.0) {
                        validationError = "قد باید بین 120 تا 230 سانتی‌متر باشد."
                        return false
                    }
                    if (w !in 35.0..400.0) {
                        validationError = "وزن باید بین 35 تا 400 کیلوگرم باشد."
                        return false
                    }
                }
            }
        }
        return true
    }
    
    fun generateTrackingCode(): String {
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.US)
        val dateStr = dateFormat.format(Date())
        val randomNum = (100000..999999).random()
        return "DK-\$dateStr-\$randomNum"
    }

    val submitForm = {
        trackingCode = generateTrackingCode()
        coroutineScope.launch {
            repository.saveConsultation(
                name = "\$firstName \$lastName",
                phone = phoneNumber,
                contextStr = "درخواست نوبت: \$selectedReason - کد پیگیری: \$trackingCode",
                notes = notes
            )
            currentStep = 7
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ثبت درخواست نوبت و مشاوره", color = DrkSurface, fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (currentStep < 7) {
                        IconButton(onClick = { if (currentStep > 1) currentStep-- else onNavigateBack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "بازگشت", tint = DrkSurface)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = DrkTurquoiseDark)
            )
        },
        containerColor = DrkBackground,
        bottomBar = {
            if (currentStep < 7) {
                Surface(
                    color = DrkSurface,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (validationError.isNotBlank()) {
                            Text(
                                text = validationError,
                                color = DrkDanger,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        Button(
                            onClick = {
                                if (validateStep()) {
                                    if (currentStep == 6) submitForm()
                                    else currentStep++
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = DrkTurquoiseDark),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (currentStep == 6) "ثبت درخواست نوبت" else "ادامه ثبت نوبت",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            if (BuildConfig.DRK_APP_MODE == "preview") {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DrkGoldSoft),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "حالت پیش‌نمایش (Preview Mode). اطلاعات فقط در حافظه دستگاه ذخیره می‌شوند.",
                            modifier = Modifier.padding(12.dp),
                            fontSize = 12.sp,
                            color = DrkCharcoal
                        )
                    }
                }
            }

            // Progress Indicator
            if (currentStep < 7) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..6) {
                            val color = if (i <= currentStep) DrkTurquoise else DrkBorder
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .background(color, RoundedCornerShape(2.dp))
                            )
                            if (i < 6) Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            when (currentStep) {
                1 -> item { Step1Reason(selectedReason, onReasonSelect = { selectedReason = it }) }
                2 -> item { Step2VisitType(selectedVisitType, selectedClinic, onVisitSelect = { selectedVisitType = it }, onClinicSelect = { selectedClinic = it }) }
                3 -> item { Step3DateTime(selectedDate, selectedTime, onDateSelect = { selectedDate = it }, onTimeSelect = { selectedTime = it }) }
                4 -> item {
                    Step4PersonalInfo(
                        firstName = firstName, onFirstName = { firstName = it },
                        lastName = lastName, onLastName = { lastName = it },
                        phoneNumber = phoneNumber, onPhone = { phoneNumber = it },
                        age = age, onAge = { age = it },
                        city = city, onCity = { city = it },
                        isNewPatient = isNewPatient, onNewPatient = { isNewPatient = it },
                        referral = referralSource, onReferral = { referralSource = it },
                        notes = notes, onNotes = { notes = it },
                        contactConsent = contactConsent, onContactConsent = { contactConsent = it },
                        saveConsent = saveConsent, onSaveConsent = { saveConsent = it }
                    )
                }
                5 -> item {
                    if (isBariatric) {
                        Step5Bariatric(
                            height = height, onHeight = { height = it },
                            weight = weight, onWeight = { weight = it },
                            priorSurgery = priorSurgery, onPriorSurgery = { priorSurgery = it },
                            priorSurgeryType = priorSurgeryType, onPriorType = { priorSurgeryType = it },
                            hasReflux = hasReflux, onReflux = { hasReflux = it },
                            hasDiabetes = hasDiabetes, onDiabetes = { hasDiabetes = it },
                            insurance = insurance, onInsurance = { insurance = it },
                            mainGoal = mainGoal, onGoal = { mainGoal = it }
                        )
                    } else if (isPostOp) {
                        Step5PostOp(
                            opProcedureType = opProcedureType, onOpProcedure = { opProcedureType = it },
                            opDate = opDate, onOpDate = { opDate = it },
                            followUpReason = followUpReason, onReason = { followUpReason = it },
                            warningSigns = warningSigns, onWarnings = { warningSigns = it },
                            needsUrgent = needsUrgentContact, onUrgent = { needsUrgentContact = it }
                        )
                    } else {
                        Text("نیاز به اطلاعات تکمیلی برای این نوع ویزیت نیست.", color = DrkMuted)
                    }
                }
                6 -> item {
                    Step6Review(
                        reason = selectedReason, clinic = selectedClinic, visitType = selectedVisitType,
                        date = selectedDate, time = selectedTime,
                        name = "\$firstName \$lastName", phone = phoneNumber
                    )
                }
                7 -> item {
                    Step7Result(trackingCode = trackingCode, onHomeClick = onNavigateBack)
                }
            }

            // Zibatis Signature
            item {
                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Z", color = DrkTurquoise, fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.height(1.dp).width(12.dp).background(DrkGold))
                    Spacer(modifier = Modifier.width(4.dp))
                    Box(modifier = Modifier.size(4.dp).background(DrkTurquoiseDark, CircleShape))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Zibatis", color = DrkMuted, fontSize = 12.sp, letterSpacing = 1.sp)
                }
            }
        }
    }
}

@Composable
fun Step1Reason(selected: String, onReasonSelect: (String) -> Unit) {
    val options = listOf(
        "مشاوره جراحی چاقی", "بررسی اسلیو معده", "بررسی مینی‌بای‌پس معده", 
        "بررسی بای‌پس کلاسیک", "چکاپ و پیگیری پس از عمل", "جراحی فتق", 
        "جراحی کیسه صفرا", "جراحی زیبایی بدن", "مشاوره عمومی", "سایر موارد"
    )
    Column {
        Text("مرحله ۱: علت مراجعه", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        Spacer(modifier = Modifier.height(16.dp))
        options.forEach { option ->
            OptionCard(
                text = option,
                selected = selected == option,
                onClick = { onReasonSelect(option) }
            )
        }
    }
}

@Composable
fun Step2VisitType(
    visitType: String, clinic: String, 
    onVisitSelect: (String) -> Unit, onClinicSelect: (String) -> Unit
) {
    Column {
        Text("نوع ویزیت", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                OptionCard(text = "حضوری", selected = visitType == "حضوری", onClick = { onVisitSelect("حضوری") })
            }
            Box(modifier = Modifier.weight(1f)) {
                OptionCard(text = "آنلاین", selected = visitType == "آنلاین", onClick = { onVisitSelect("آنلاین") })
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("مطب", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        OptionCard(text = "مطب سعادت‌آباد", selected = clinic == "مطب سعادت‌آباد", onClick = { onClinicSelect("مطب سعادت‌آباد") })
        OptionCard(text = "مطب پیروزی", selected = clinic == "مطب پیروزی", onClick = { onClinicSelect("مطب پیروزی") })
    }
}

@Composable
fun Step3DateTime(date: String, time: String, onDateSelect: (String) -> Unit, onTimeSelect: (String) -> Unit) {
    val dates = listOf("۱۴۰۲/۰۷/۱۵", "۱۴۰۲/۰۷/۱۷", "۱۴۰۲/۰۷/۱۹")
    val times = listOf("۱۶:۰۰", "۱۷:۳۰", "۱۹:۰۰")
    
    Column {
        Text("زمان‌های قابل تنظیم (نمونه)", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        Spacer(modifier = Modifier.height(12.dp))
        Text("تاریخ پیشنهادی", color = DrkMuted, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        dates.forEach { d ->
            OptionCard(text = d, selected = date == d, onClick = { onDateSelect(d) })
        }
        
        if (date.isNotBlank()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("ساعت پیشنهادی", color = DrkMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                times.forEach { t ->
                    Box(modifier = Modifier.weight(1f)) {
                        OptionCard(text = t, selected = time == t, onClick = { onTimeSelect(t) })
                    }
                }
            }
        }
    }
}

@Composable
fun Step4PersonalInfo(
    firstName: String, onFirstName: (String) -> Unit,
    lastName: String, onLastName: (String) -> Unit,
    phoneNumber: String, onPhone: (String) -> Unit,
    age: String, onAge: (String) -> Unit,
    city: String, onCity: (String) -> Unit,
    isNewPatient: Boolean, onNewPatient: (Boolean) -> Unit,
    referral: String, onReferral: (String) -> Unit,
    notes: String, onNotes: (String) -> Unit,
    contactConsent: Boolean, onContactConsent: (Boolean) -> Unit,
    saveConsent: Boolean, onSaveConsent: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("اطلاعات مراجعه‌کننده", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        FormNumberField(label = "نام *", value = firstName, onValueChange = onFirstName)
        FormNumberField(label = "نام خانوادگی *", value = lastName, onValueChange = onLastName)
        FormNumberField(label = "شماره موبایل *", value = phoneNumber, onValueChange = onPhone)
        FormNumberField(label = "سن *", value = age, onValueChange = onAge)
        FormNumberField(label = "شهر", value = city, onValueChange = onCity)
        
        YesNoRadioGroup(label = "آیا مراجعه‌کننده جدید هستید؟", selected = isNewPatient, onSelect = onNewPatient)
        FormNumberField(label = "نحوه آشنایی", value = referral, onValueChange = onReferral)
        FormNumberField(label = "توضیحات اختیاری", value = notes, onValueChange = onNotes)
        
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 8.dp)) {
            Checkbox(checked = contactConsent, onCheckedChange = onContactConsent, colors = CheckboxDefaults.colors(checkedColor = DrkTurquoise))
            Text("رضایت برای تماس جهت هماهنگی نوبت", fontSize = 13.sp, color = DrkText)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = saveConsent, onCheckedChange = onSaveConsent, colors = CheckboxDefaults.colors(checkedColor = DrkTurquoise))
            Text("رضایت برای ذخیره اطلاعات ثبت‌شده", fontSize = 13.sp, color = DrkText)
        }
        
        Text(
            text = "اطلاعات ثبت‌شده فقط برای بررسی درخواست، هماهنگی نوبت و ادامه ارتباط با شما استفاده می‌شود.",
            fontSize = 11.sp, color = DrkMuted, modifier = Modifier.padding(start = 12.dp)
        )
    }
}

@Composable
fun Step5Bariatric(
    height: String, onHeight: (String) -> Unit,
    weight: String, onWeight: (String) -> Unit,
    priorSurgery: Boolean, onPriorSurgery: (Boolean) -> Unit,
    priorSurgeryType: String, onPriorType: (String) -> Unit,
    hasReflux: Boolean, onReflux: (Boolean) -> Unit,
    hasDiabetes: Boolean, onDiabetes: (Boolean) -> Unit,
    insurance: String, onInsurance: (String) -> Unit,
    mainGoal: String, onGoal: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("اطلاعات تکمیلی جراحی چاقی", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        FormNumberField(label = "قد (سانتی‌متر)", value = height, onValueChange = onHeight)
        FormNumberField(label = "وزن (کیلوگرم)", value = weight, onValueChange = onWeight)
        
        if (height.isNotBlank() && weight.isNotBlank()) {
            val h = height.toDoubleOrNull() ?: 0.0
            val w = weight.toDoubleOrNull() ?: 0.0
            if (h > 0) {
                val bmi = w / (h / 100).pow(2)
                Card(colors = CardDefaults.cardColors(containerColor = DrkTurquoise.copy(alpha = 0.1f))) {
                    Text(
                        text = "شاخص توده بدنی (BMI) تقریبی: " + String.format(Locale.US, "%.1f", bmi),
                        modifier = Modifier.padding(16.dp), color = DrkTurquoiseDark, fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        YesNoRadioGroup(label = "سابقه جراحی چاقی؟", selected = priorSurgery, onSelect = onPriorSurgery)
        if (priorSurgery) {
            FormNumberField(label = "نوع جراحی قبلی", value = priorSurgeryType, onValueChange = onPriorType)
        }
        YesNoRadioGroup(label = "وجود رفلاکس معده؟", selected = hasReflux, onSelect = onReflux)
        YesNoRadioGroup(label = "مبتلا به دیابت هستید؟", selected = hasDiabetes, onSelect = onDiabetes)
        FormNumberField(label = "بیمه درمانی", value = insurance, onValueChange = onInsurance)
        FormNumberField(label = "هدف اصلی مراجعه", value = mainGoal, onValueChange = onGoal)
    }
}

@Composable
fun Step5PostOp(
    opProcedureType: String, onOpProcedure: (String) -> Unit,
    opDate: String, onOpDate: (String) -> Unit,
    followUpReason: String, onReason: (String) -> Unit,
    warningSigns: String, onWarnings: (String) -> Unit,
    needsUrgent: Boolean, onUrgent: (Boolean) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("پیگیری پس از عمل", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        FormNumberField(label = "نوع عمل", value = opProcedureType, onValueChange = onOpProcedure)
        FormNumberField(label = "تاریخ تقریبی عمل", value = opDate, onValueChange = onOpDate)
        FormNumberField(label = "علت پیگیری", value = followUpReason, onValueChange = onReason)
        FormNumberField(label = "وجود علامت هشدار (درد، تهوع...)", value = warningSigns, onValueChange = onWarnings)
        YesNoRadioGroup(label = "نیاز به تماس سریع دارید؟", selected = needsUrgent, onSelect = onUrgent)
    }
}

@Composable
fun Step6Review(
    reason: String, clinic: String, visitType: String, date: String, time: String, name: String, phone: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("بازبینی اطلاعات", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = DrkCharcoal)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = DrkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DrkBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ReviewRow("علت مراجعه", reason)
                ReviewRow("مطب و نوع", "\$clinic - \$visitType")
                ReviewRow("زمان پیشنهادی", "\$date ساعت \$time")
                ReviewRow("نام مراجعه‌کننده", name)
                ReviewRow("شماره موبایل", phone)
            }
        }
    }
}

@Composable
fun ReviewRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = DrkMuted, fontSize = 14.sp)
        Text(value, color = DrkText, fontWeight = FontWeight.Medium, fontSize = 14.sp)
    }
}

@Composable
fun Step7Result(trackingCode: String, onHomeClick: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = "Success", tint = DrkSuccess, modifier = Modifier.size(80.dp))
        Text("درخواست نوبت شما ثبت شد", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = DrkCharcoal)
        Text(
            "کد پیگیری را نگه دارید. تیم پذیرش پس از بررسی زمان پیشنهادی، برای هماهنگی نهایی با شما تماس می‌گیرد.",
            textAlign = TextAlign.Center, color = DrkText, fontSize = 15.sp, lineHeight = 22.sp
        )
        
        Card(
            colors = CardDefaults.cardColors(containerColor = DrkBackground),
            border = androidx.compose.foundation.BorderStroke(2.dp, DrkTurquoise),
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("کد پیگیری", color = DrkMuted, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(trackingCode, fontWeight = FontWeight.Black, fontSize = 28.sp, color = DrkTurquoiseDark, letterSpacing = 2.sp)
            }
        }
        
        Button(
            onClick = onHomeClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = DrkCharcoal),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("بازگشت به صفحه اصلی", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun OptionCard(text: String, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) DrkTurquoise.copy(alpha = 0.1f) else DrkSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) DrkTurquoise else DrkBorder
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selected, onClick = null,
                colors = RadioButtonDefaults.colors(selectedColor = DrkTurquoise)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = text, fontSize = 15.sp, color = if (selected) DrkTurquoiseDark else DrkCharcoal, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}
