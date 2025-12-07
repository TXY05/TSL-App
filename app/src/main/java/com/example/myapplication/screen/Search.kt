package com.example.myapplication.screen

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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.myapplication.data.PostEntity
import com.example.myapplication.data.PostViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Search(
    navController: NavController,
    viewModel: PostViewModel = viewModel(),
) {
    var query by rememberSaveable { mutableStateOf("") }
    var fromDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var toDate by rememberSaveable { mutableStateOf<LocalDate?>(null) }
    var results by rememberSaveable { mutableStateOf<List<PostEntity>>(emptyList()) }


    var showFromPicker by remember { mutableStateOf(false) }
    var showToPicker by remember { mutableStateOf(false) }

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yy")
    val allPostsWithRelations by viewModel.allPostsWithRelations.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("搜索记录") }, navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                }
            })
        }) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // 🔍 搜索输入框 + 按钮
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("搜索标题或描述") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        results = allPostsWithRelations.filter { relation ->
                            val post = relation.post

                            // 🔹 Collect searchable fields from the post
                            val postFields = listOfNotNull(
                                post.title,
                                post.description,
                                post.m2rPerson,
                                post.r2mPerson,
                                post.customerShipment.toString(),
                                post.factoryShipment.toString(),
                                post.serviceCharge.toString(),
                                post.r2mRate.toString(),
                                post.r2mDate.toString(),
                                post.m2rRate.toString(),
                                post.m2rDate.toString(),
                                post.totalProfit.toString()
                            )

                            // 🔹 Flatten item-related fields
                            val itemFields = relation.items.flatMap { item ->
                                listOfNotNull(
                                    item.title,
                                    item.amount.toString(),
                                    item.customerPrice.toString(),
                                    item.customerAmount.toString(),
                                    item.factoryPrice.toString(),
                                    item.factoryAmount.toString()
                                )
                            }

                            // 🔹 Flatten logistics-related fields
                            val logisticsFields = relation.logistics.flatMap { log ->
                                listOfNotNull(
                                    log.description,
                                    log.person,
                                    log.weight.toString(),
                                    log.amount.toString()
                                )
                            }

                            // 🔹 Combine all searchable text
                            val allFields = postFields + itemFields + logisticsFields

                            // 🔹 Text search
                            val matchQuery = allFields.any { it.contains(query, ignoreCase = true) }

                            // 🔹 Date range filter
                            val matchDate =
                                (fromDate == null || !post.date.isBefore(fromDate)) && (toDate == null || !post.date.isAfter(
                                    toDate
                                ))

                            // ✅ Return combined filter condition
                            matchQuery && matchDate
                        }.map { it.post }
                            .sortedByDescending { it.date }

                    }) {
                    Icon(Icons.Default.Search, contentDescription = "搜索")
                }
            }

            Spacer(Modifier.height(12.dp))

            // 📅 日期筛选行
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showFromPicker = true }) {
                    Text(
                        fromDate?.format(formatter) ?: "选择开始日期",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showToPicker = true }) {
                    Text(
                        toDate?.format(formatter) ?: "选择结束日期",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                Spacer(Modifier.width(16.dp))

                // 🔄 重置按钮
                Button(onClick = {
                    fromDate = null
                    toDate = null
                    results = emptyList()
                }) {
                    Text("重置日期")
                }
            }

            Spacer(Modifier.height(16.dp))

            // 📋 搜索结果
            if (results.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = if (query.isEmpty()) "请输入关键词后点击搜索" else "暂无搜索结果",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                LazyColumn {
                    item {
                        val totalProfitSum = (results.sumOf {
                            it.totalProfit
                        })
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = "总利润: " + "%.2f".format(totalProfitSum),
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                        HorizontalDivider()

                    }
                    items(results.size) { index ->
                        val post = results[index]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    navController.navigate("editItem/${post.postId}")
                                }, colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = post.date.toString(), style = MaterialTheme.typography.titleMedium)
                                    Text(text = post.title, style = MaterialTheme.typography.titleMedium)
                                    Text(text = "%.2f".format(post.totalProfit), style = MaterialTheme.typography.titleMedium)
                                }
                            }
                        }
                    }
                }
            }

            // 🗓 开始日期选择器
            if (showFromPicker) {
                val state = rememberDatePickerState()
                DatePickerDialog(onDismissRequest = { showFromPicker = false }, confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let {
                            fromDate = LocalDate.ofEpochDay(it / 86_400_000)
                        }
                        showFromPicker = false
                    }) { Text("确定") }
                }, dismissButton = {
                    TextButton(onClick = { showFromPicker = false }) { Text("取消") }
                }) {
                    DatePicker(state = state)
                }
            }

            // 🗓 结束日期选择器
            if (showToPicker) {
                val state = rememberDatePickerState()
                DatePickerDialog(onDismissRequest = { showToPicker = false }, confirmButton = {
                    TextButton(onClick = {
                        state.selectedDateMillis?.let {
                            toDate = LocalDate.ofEpochDay(it / 86_400_000)
                        }
                        showToPicker = false
                    }) { Text("确定") }
                }, dismissButton = {
                    TextButton(onClick = { showToPicker = false }) { Text("取消") }
                }) {
                    DatePicker(state = state)
                }
            }
        }
    }
}


