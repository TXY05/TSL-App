package com.example.myapplication.screen

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.ItemData
import com.example.myapplication.data.ItemEntity
import com.example.myapplication.data.LogisticsData
import com.example.myapplication.data.LogisticsEntity
import com.example.myapplication.data.PostEntity
import com.example.myapplication.data.PostViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Post(
    postId: Int? = null,
    defaultDate: LocalDate = LocalDate.now(),
    onBack: () -> Unit,
    viewModel: PostViewModel = viewModel(),
) {
    var selectedDate by rememberSaveable { mutableStateOf(defaultDate) }


    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    var showDatePicker by remember { mutableStateOf(false) }

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    var divisionText by remember { mutableStateOf("") }
    var r2mAgentDate by remember { mutableStateOf(LocalDate.now()) }
    var m2rAgentDate by remember { mutableStateOf(LocalDate.now()) }
    var r2mMultiplier by remember { mutableStateOf("1.310") }
    var m2rMultiplier by remember { mutableStateOf("1.327") }
    var r2mAgentNote by remember { mutableStateOf("") }
    var m2rAgentNote by remember { mutableStateOf("") }

    var items by remember { mutableStateOf(mutableListOf(ItemData())) }
    var logisticsItems = remember { mutableStateListOf(LogisticsData()) }

    val formatter = DateTimeFormatter.ofPattern("EEEE, d/L/uuuu")

    val SaveDateFormatter = DateTimeFormatter.ofPattern("yy/MM/dd")
    var customerFreight by remember { mutableStateOf("") }
    var factoryFreight by remember { mutableStateOf("") }

    var serviceCharge by remember { mutableDoubleStateOf(0.00) }
    var serviceChargeText by remember { mutableStateOf("") }


    var totalWithMultiplierText by remember { mutableStateOf("") }

    var totalCustomerAmountText by remember { mutableStateOf("") }

    var totalShipmentText by remember { mutableStateOf("") }

    var totalText by remember { mutableStateOf("") }

    var totalItemProfit by remember { mutableDoubleStateOf(0.0) }
    var firstProfit by remember { mutableDoubleStateOf(0.00) }
    var secondProfit by remember { mutableDoubleStateOf(0.00) }
    var totalProfit by remember { mutableDoubleStateOf(0.00) }


    LaunchedEffect(postId) {
        if (postId != null) {
            val postWithData = viewModel.getPostWithRelations(postId)
            postWithData?.let {
                selectedDate = it.post.date
                title = it.post.title
                description = it.post.description
                r2mAgentNote = it.post.r2mPerson
                r2mAgentDate = it.post.r2mDate
                r2mMultiplier = it.post.r2mRate

                m2rAgentNote = it.post.m2rPerson
                m2rAgentDate = it.post.m2rDate
                m2rMultiplier = it.post.m2rRate
                customerFreight = it.post.customerShipment
                factoryFreight = it.post.factoryShipment
                serviceCharge = it.post.serviceCharge
                serviceChargeText = serviceCharge.toString()

                items = it.items.map { item ->
                    ItemData(
                        title = item.title, amount = item.amount, inputs = listOf(
                            item.customerPrice,
                            item.customerAmount,
                            item.factoryPrice,
                            item.factoryAmount
                        )
                    )
                }.toMutableList()
                logisticsItems.clear()
                logisticsItems.addAll(
                    it.logistics.map { log ->
                        LogisticsData(
                            description = log.description,
                            person = log.person,
                            weight = log.weight,
                            amount = log.amount,
                            startDate = log.startDate?.format(SaveDateFormatter) ?: "",
                            endDate = log.endDate?.format(SaveDateFormatter) ?: ""
                        )
                    })

            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("编辑") }, navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back"
                    )
                }
            }, actions = {
                TextButton(onClick = {
                    val postEntity = PostEntity(
                        postId = postId ?: 0,
                        date = selectedDate,
                        title = title,
                        description = description,
                        r2mPerson = r2mAgentNote,
                        r2mDate = r2mAgentDate,
                        r2mRate = r2mMultiplier,
                        m2rDate = m2rAgentDate,
                        m2rPerson = m2rAgentNote,
                        m2rRate = m2rMultiplier,
                        customerShipment = customerFreight,
                        factoryShipment = factoryFreight,
                        serviceCharge = serviceCharge,
                        totalProfit = totalProfit
                    )

                    val itemEntities = items.map {
                        ItemEntity(
                            postOwnerId = postId ?: 0,
                            title = it.title,
                            amount = it.amount,
                            customerPrice = it.inputs[0],
                            customerAmount = it.inputs[1],
                            factoryPrice = it.inputs[2],
                            factoryAmount = it.inputs[3]
                        )
                    }

                    val logisticsEntities = logisticsItems.map {
                        LogisticsEntity(
                            postOwnerId = postId ?: 0,
                            description = it.description,
                            person = it.person,
                            weight = it.weight,
                            amount = it.amount,

                            startDate = it.startDate.takeIf { s -> s.isNotBlank() }?.let {
                                runCatching {
                                    LocalDate.parse(
                                        it,
                                        SaveDateFormatter
                                    )
                                }.getOrNull()
                            },
                            endDate = it.endDate.takeIf { s -> s.isNotBlank() }?.let {
                                runCatching {
                                    LocalDate.parse(
                                        it,
                                        SaveDateFormatter
                                    )
                                }.getOrNull()
                            }
                        )
                    }

                    viewModel.savePost(postEntity, itemEntities, logisticsEntities)
                    Toast.makeText(context, "已保存", Toast.LENGTH_SHORT).show()
                    onBack()
                }) {
                    Text("保存", style = MaterialTheme.typography.bodyLarge)
                }
            })
        }) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "日期: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 🗓 Date Selector (centered)
                Text(
                    text = selectedDate.format(formatter),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .clickable { showDatePicker = true }
                        .padding(top = 4.dp),
                    fontSize = 20.sp)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("客户名称") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("客户名称") },
                    singleLine = true
                )

                // 🗒 Description Input
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("厂家名称") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("厂家名称") },
                    singleLine = true
                )
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
                )

                DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                    TextButton(onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            selectedDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                        }
                        showDatePicker = false
                    }) {
                        Text("OK")
                    }
                }, dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }) {
                    DatePicker(
                        state = datePickerState, showModeToggle = false
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(items) { index, item ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        // Number, Title, Amount Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 16.sp
                            )
                            OutlinedTextField(
                                value = item.title,
                                onValueChange = { newValue ->
                                    items = items.toMutableList().also { list ->
                                        list[index] = list[index].copy(title = newValue)
                                    }
                                },
                                label = { Text("产品名称与规格", fontSize = 12.sp) },
                                placeholder = { Text("产品名称与规格", fontSize = 12.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = item.amount,
                                onValueChange = { newValue ->
                                    val updatedItem = item.copy(amount = newValue)
                                    val quantity = newValue.toDoubleOrNull() ?: 0.0

                                    val customerPrice = item.inputs[0].toDoubleOrNull() ?: 0.0
                                    val factoryPrice = item.inputs[2].toDoubleOrNull() ?: 0.0

                                    val updatedInputs = updatedItem.inputs.toMutableList()

                                    // 更新客户金额
                                    val customerTotal =
                                        (customerPrice * quantity * 100).roundToInt() / 100.0
                                    updatedInputs[1] =
                                        if (customerTotal > 0) customerTotal.toString() else ""

                                    // 更新厂家金额
                                    val factoryTotal =
                                        (factoryPrice * quantity * 100).roundToInt() / 100.0
                                    updatedInputs[3] =
                                        if (factoryTotal > 0) factoryTotal.toString() else ""

                                    items = items.toMutableList().also { list ->
                                        list[index] = updatedItem.copy(inputs = updatedInputs)
                                    }
                                },
                                label = { Text("数量") },
                                placeholder = { Text("数量") },
                                modifier = Modifier.width(100.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )
                            IconButton(onClick = {
                                items = items.toMutableList().apply { removeAt(index) }
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = Color.Red
                                )
                            }

                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Four input fields
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            OutlinedTextField(
                                value = item.inputs[0],
                                onValueChange = { newValue ->
                                    val updatedInputs = item.inputs.toMutableList()
                                    updatedInputs[0] = newValue

                                    // Try to calculate the amount
                                    val price = newValue.toDoubleOrNull() ?: 0.0
                                    val quantity = item.amount.toDoubleOrNull() ?: 0.0
                                    val total = ((price * quantity * 100).roundToInt() / 100.0)

                                    updatedInputs[1] = if (total > 0) total.toString() else ""

                                    items = items.toMutableList().also { list ->
                                        list[index] = list[index].copy(inputs = updatedInputs)
                                    }
                                },
                                label = {
                                    Text(
                                        text = "客户价", fontSize = 10.sp, color = Color.Red
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 14.sp, color = Color.Red
                                ),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Red,
                                    unfocusedIndicatorColor = Color.Red,
                                    disabledIndicatorColor = Color.Red,
                                    cursorColor = Color.Red,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )

                            OutlinedTextField(
                                value = item.inputs[1],
                                onValueChange = { /* read-only */ },
                                label = { Text("客户金额", color = Color.Red, fontSize = 8.sp) },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                singleLine = true,
                                textStyle = LocalTextStyle.current.copy(
                                    color = Color.Red, fontSize = 14.sp
                                ),
                                colors = TextFieldDefaults.colors(
                                    focusedIndicatorColor = Color.Red,
                                    unfocusedIndicatorColor = Color.Red,
                                    disabledIndicatorColor = Color.Red,
                                    cursorColor = Color.Red,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedContainerColor = Color.Transparent
                                )
                            )

                            OutlinedTextField(
                                value = item.inputs[2],
                                onValueChange = { newValue ->
                                    val updatedInputs = item.inputs.toMutableList()
                                    updatedInputs[2] = newValue

                                    val price = newValue.toDoubleOrNull() ?: 0.0
                                    val quantity = item.amount.toDoubleOrNull() ?: 0.0
                                    val total = ((price * quantity * 100).roundToInt() / 100.0)
                                    updatedInputs[3] = if (total > 0) total.toString() else ""

                                    items = items.toMutableList().also { list ->
                                        list[index] = list[index].copy(inputs = updatedInputs)
                                    }
                                },
                                label = {
                                    Text(
                                        "厂家价", fontSize = 10.sp
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            OutlinedTextField(
                                value = item.inputs[3],
                                onValueChange = { },
                                label = {
                                    Text(
                                        "厂家金额", fontSize = 8.sp
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                readOnly = true,
                                textStyle = LocalTextStyle.current.copy(fontSize = 14.sp),
                                singleLine = true
                            )

                        }

                    }

                }

                item {


                    val totalCustomerAmount = ((items.sumOf {
                        it.inputs.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                    } + (customerFreight.toDoubleOrNull() ?: 0.0)) * 100).roundToInt() / 100.0

                    val totalFactoryAmount = ((items.sumOf {
                        it.inputs.getOrNull(3)?.toDoubleOrNull() ?: 0.0
                    } + (factoryFreight.toDoubleOrNull() ?: 0.0)) * 100).roundToInt() / 100.0

                    totalItemProfit = totalCustomerAmount - totalFactoryAmount

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = customerFreight,
                            onValueChange = { customerFreight = it },
                            label = { Text("客户运费", color = Color.Red, fontSize = 14.sp) },
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Red, fontSize = 16.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = factoryFreight,
                            onValueChange = { factoryFreight = it },
                            label = { Text("厂家运费", fontSize = 14.sp) },
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = "%.2f".format(totalCustomerAmount),
                            onValueChange = { },
                            label = { Text("客户金额合计", color = Color.Red, fontSize = 14.sp) },
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(
                                color = Color.Red, fontSize = 16.sp
                            ),
                            modifier = Modifier.weight(1f),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Red,
                                unfocusedIndicatorColor = Color.Red,
                                disabledIndicatorColor = Color.Red,
                                cursorColor = Color.Red,
                                unfocusedContainerColor = Color.Transparent,
                                focusedContainerColor = Color.Transparent
                            ),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = "%.2f".format(totalFactoryAmount),
                            onValueChange = { },
                            label = { Text("厂家金额合计", fontSize = 14.sp) },
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        OutlinedTextField(
                            value = "%.2f".format(totalItemProfit),
                            onValueChange = { },
                            label = { Text("赚取货款", fontSize = 14.sp) },
                            readOnly = true,
                            textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                }


                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                items = (items + ItemData()).toMutableList()
                            }) {
                            Text("添加产品 (+)")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("国际物流处理与收费")
                }


                // --- Logistics Section ---
                itemsIndexed(logisticsItems) { index, logItem ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${index + 1}.",
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 18.sp
                            )

                            OutlinedTextField(
                                value = logItem.description,
                                onValueChange = { newValue ->
                                    logisticsItems[index] =
                                        logisticsItems[index].copy(description = newValue)

                                },
                                label = { Text("包裹内容") },
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 8.dp),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = logItem.person,
                                onValueChange = { newValue ->
                                    logisticsItems[index] =
                                        logisticsItems[index].copy(person = newValue)

                                },
                                label = { Text("收件人") },
                                modifier = Modifier.width(100.dp),
                                singleLine = true
                            )

                            IconButton(onClick = {
                                logisticsItems.removeAt(index)
                            }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "删除",
                                    tint = Color.Red
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // --- Dates ---
                        var showStartPicker by remember { mutableStateOf(false) }
                        var showEndPicker by remember { mutableStateOf(false) }


                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = logItem.weight,
                                onValueChange = { newValue ->
                                    logisticsItems[index] =
                                        logisticsItems[index].copy(weight = newValue)

                                },
                                label = { Text("重量kg") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = logItem.amount,
                                onValueChange = { newValue ->
                                    logisticsItems[index] =
                                        logisticsItems[index].copy(amount = newValue)

                                },
                                label = { Text("运费", fontSize = 14.sp) },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = logItem.startDate,
                                    onValueChange = { newValue ->
                                        logisticsItems[index] =
                                            logisticsItems[index].copy(startDate = newValue)

                                    },
                                    label = { Text("发件日期", fontSize = 14.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    singleLine = true,
                                )
                                // Transparent overlay to detect taps
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showStartPicker = true })
                            }


                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 4.dp)
                            ) {
                                OutlinedTextField(
                                    value = logItem.endDate,
                                    onValueChange = { newValue ->
                                        logisticsItems[index] =
                                            logisticsItems[index].copy(endDate = newValue)

                                    },
                                    label = { Text("收件日期", fontSize = 14.sp) },
                                    modifier = Modifier.fillMaxWidth(),
                                    readOnly = true,
                                    singleLine = true,
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clickable { showEndPicker = true })
                            }

                        }

                        // --- DatePickerDialogs (they now show properly) ---
                        if (showStartPicker) {
                            val startPickerState = rememberDatePickerState(
                                initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
                            )
                            DatePickerDialog(
                                onDismissRequest = { showStartPicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        startPickerState.selectedDateMillis?.let { millis ->
                                            val formatted = SimpleDateFormat(
                                                "dd/MM/yy", Locale.getDefault()
                                            ).format(Date(millis))
                                            logisticsItems[index] = logisticsItems[index].copy(
                                                startDate = formatted
                                            )
                                        }

                                        showStartPicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showStartPicker = false }) {
                                        Text("Cancel")
                                    }
                                }) {
                                DatePicker(
                                    state = startPickerState, showModeToggle = false
                                )
                            }
                        }


                        if (showEndPicker) {
                            val endPickerState = rememberDatePickerState(
                                initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
                            )
                            DatePickerDialog(
                                onDismissRequest = { showEndPicker = false },
                                confirmButton = {
                                    TextButton(onClick = {
                                        endPickerState.selectedDateMillis?.let { millis ->
                                            val formatted = SimpleDateFormat(
                                                "dd/MM/yy", Locale.getDefault()
                                            ).format(Date(millis))
                                            logisticsItems[index] = logisticsItems[index].copy(
                                                endDate = formatted
                                            )
                                        }
                                        showEndPicker = false
                                    }) {
                                        Text("OK")
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showEndPicker = false }) {
                                        Text("Cancel")
                                    }
                                }) {
                                DatePicker(
                                    state = endPickerState, showModeToggle = false
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Button(onClick = {
                            logisticsItems.add(LogisticsData())
                        }) {
                            Text("添加包裹 (+)")
                        }
                    }
                }

                item {
                    // 重新计算客户总金额（含运费）
                    val totalCustomerAmount = items.sumOf {
                        it.inputs.getOrNull(1)?.toDoubleOrNull() ?: 0.0
                    } + (customerFreight.toDoubleOrNull() ?: 0.0)

                    // 计算客户金额总计 × multiplier
                    val totalWithMultiplier =
                        totalCustomerAmount * (m2rMultiplier.toDoubleOrNull() ?: 1.0)

                    totalCustomerAmountText = "%.2f".format(totalCustomerAmount)
                    totalWithMultiplierText = "%.2f".format(totalWithMultiplier)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFFFE0))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "马币兑换人民币 （客户支付）",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = totalCustomerAmountText,
                                    onValueChange = {},
                                    label = { Text("马币货款", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = totalWithMultiplierText,
                                    onValueChange = {},
                                    label = { Text("折人民币", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = m2rMultiplier,
                                    onValueChange = { m2rMultiplier = it },
                                    label = { Text("汇率", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                }
                item {

                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

                    val result =
                        totalWithMultiplierText.toDouble() / (r2mMultiplier.toDoubleOrNull() ?: 1.0)

                    divisionText = "%.2f".format(result)


                    var showAgentDatePicker by remember { mutableStateOf(false) }

                    firstProfit = result - totalCustomerAmountText.toDouble()

                    if (showAgentDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
                        )

                        DatePickerDialog(
                            onDismissRequest = { showAgentDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        r2mAgentDate =
                                            LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                    }
                                    showAgentDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAgentDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }) {
                            DatePicker(
                                state = datePickerState, showModeToggle = false
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFFFFFE0))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "人民币兑换马币 （代理人支付）",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = divisionText,
                                    onValueChange = {},
                                    label = { Text("马币货款", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = totalWithMultiplierText,
                                    onValueChange = {},
                                    label = { Text("折人民币", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = r2mMultiplier,
                                    onValueChange = { r2mMultiplier = it },
                                    label = { Text("汇率", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = r2mAgentNote,
                                    onValueChange = { r2mAgentNote = it },
                                    label = { Text("代理人", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = r2mAgentDate.format(formatter),
                                        onValueChange = {},
                                        label = { Text("兑换日", fontSize = 14.sp) },
                                        readOnly = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { showAgentDatePicker = true })
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = "%.2f".format(firstProfit),
                                    onValueChange = { },
                                    label = { Text("赚取货款汇率差异", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    readOnly = true,
                                    singleLine = true
                                )
                            }

                        }
                    }

                }



                item {
                    val totalShipment = logisticsItems.sumOf {
                        it.amount.toDoubleOrNull() ?: 0.0
                    }

                    val total = totalShipment * (m2rMultiplier.toDoubleOrNull() ?: 1.0)

                    totalShipmentText = "%.2f".format(totalShipment)
                    totalText = "%.2f".format(total)

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(color = Color.Red, thickness = 3.dp)

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFB2F2A1)) // Light green background
                            .padding(12.dp)
                    ){
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "马币兑换人民币 （客户支付）",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = totalShipmentText,
                                    onValueChange = {},
                                    label = { Text("马币运费", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = totalText,
                                    onValueChange = {},
                                    label = { Text("折人民币", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = m2rMultiplier,
                                    onValueChange = { m2rMultiplier = it },
                                    label = { Text("汇率", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                        }
                    }

                }
                item {

                    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")

                    val result = totalText.toDouble() / (r2mMultiplier.toDoubleOrNull() ?: 1.0)

                    val divisionText = "%.2f".format(result)

                    var showAgentDatePicker by remember { mutableStateOf(false) }

                    if (showAgentDatePicker) {
                        val datePickerState = rememberDatePickerState(
                            initialSelectedDateMillis = selectedDate.toEpochDay() * 24 * 60 * 60 * 1000
                        )

                        DatePickerDialog(
                            onDismissRequest = { showAgentDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    datePickerState.selectedDateMillis?.let { millis ->
                                        m2rAgentDate =
                                            LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
                                    }
                                    showAgentDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showAgentDatePicker = false }) {
                                    Text("Cancel")
                                }
                            }) {
                            DatePicker(
                                state = datePickerState, showModeToggle = false
                            )
                        }
                    }

                    secondProfit = result - totalShipmentText.toDouble()
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .background(Color(0xFFB2F2A1))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "人民币兑换马币 （代理人支付）",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = divisionText,
                                    onValueChange = {},
                                    label = { Text("马币运费", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = totalText,
                                    onValueChange = {},
                                    label = { Text("折人民币", fontSize = 12.sp) },
                                    readOnly = true,
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = r2mMultiplier,
                                    onValueChange = { r2mMultiplier = it },
                                    label = { Text("汇率", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = m2rAgentNote,
                                    onValueChange = { m2rAgentNote = it },
                                    label = { Text("代理人", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )

                                Box(modifier = Modifier.weight(1f)) {
                                    OutlinedTextField(
                                        value = m2rAgentDate.format(formatter),
                                        onValueChange = {},
                                        label = { Text("兑换日", fontSize = 14.sp) },
                                        readOnly = true,
                                        textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )
                                    Box(
                                        modifier = Modifier
                                            .matchParentSize()
                                            .clickable { showAgentDatePicker = true })
                                }
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = "%.2f".format(secondProfit),
                                    onValueChange = { },
                                    label = { Text("赚取运费汇率差异", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    readOnly = true,
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedTextField(
                                    value = serviceChargeText,
                                    onValueChange = {
                                        serviceChargeText = it
                                        serviceCharge = serviceChargeText.toDoubleOrNull() ?: 0.0
                                    },
                                    label = { Text("服务费", fontSize = 14.sp) },
                                    textStyle = LocalTextStyle.current.copy(fontSize = 16.sp),
                                    modifier = Modifier.weight(1f),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Example calculated values — replace with your actual logic
                        val profitGoods = totalItemProfit
                        val profitRateDifference = firstProfit
                        val profitFreightRateDifference = secondProfit
                        totalProfit =
                            ((profitGoods + profitRateDifference + profitFreightRateDifference + serviceCharge) * 100).roundToInt() / 100.0

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("赚取货款", fontSize = 16.sp)
                            Text("%.2f".format(profitGoods), fontSize = 16.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("赚取货款汇率差异", fontSize = 16.sp)
                            Text("%.2f".format(profitRateDifference), fontSize = 16.sp)
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("赚取运费汇率差异", fontSize = 16.sp)
                            Text("%.2f".format(profitFreightRateDifference), fontSize = 16.sp)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("服务费", fontSize = 16.sp)
                            Text("%.2f".format(serviceCharge), fontSize = 16.sp)
                        }
                        HorizontalDivider(color = Color.Gray, thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "总利润", fontSize = 16.sp, fontWeight = FontWeight.Bold
                            )
                            Text(
                                "%.2f".format(totalProfit),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

// Delete Post Button
                    Text(
                        text = "删除",
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .background(Color.Red, shape = RoundedCornerShape(8.dp))
                            .clickable { showDeleteDialog = true }
                            .padding(vertical = 12.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge)

// Confirmation Dialog
                    if (showDeleteDialog) {
                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("删除") },
                            text = { Text("确认删除？") },
                            confirmButton = {
                                TextButton(onClick = {
                                    if (postId != null) {
                                        viewModel.deletePostById(postId)
                                        Toast.makeText(context, "已删除", Toast.LENGTH_SHORT).show()
                                        showDeleteDialog = false
                                        onBack()
                                    }
                                }) {
                                    Text("是的", color = Color.Red)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("取消")
                                }
                            })
                    }

                }
            }
        }
    }
}