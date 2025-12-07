package com.example.myapplication.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.PostEntity
import com.example.myapplication.data.PostViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Dashboard(
    onAddClick: (LocalDate) -> Unit,
    onEditClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: PostViewModel = viewModel(),
) {
    var currentDate by rememberSaveable { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("EEEE, d/L/uuuu")
    var showDatePicker by remember { mutableStateOf(false) }
    val posts by viewModel.allPosts.observeAsState(emptyList())

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // 🔍 Search icon (independent, takes only its own space)
                    IconButton(
                        onClick = { onSearchClick() }, modifier = Modifier.width(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search, contentDescription = "Search"
                        )
                    }

                    // 🧭 Main row (Back, Date, Next) fills the rest of the space
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // ⬅️ Previous Day
                        IconButton(onClick = { currentDate = currentDate.minusDays(1) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous Day"
                            )
                        }

                        // 📅 Date Text
                        Text(
                            text = currentDate.format(formatter),
                            modifier = Modifier
                                .clickable { showDatePicker = true }
                                .padding(horizontal = 8.dp),
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.titleLarge)

                        // ➡️ Next Day
                        IconButton(onClick = { currentDate = currentDate.plusDays(1) }) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next Day"
                            )
                        }
                    }
                }
            })
    }, floatingActionButton = {
        FloatingActionButton(onClick = { onAddClick(currentDate) }) {
            Text("+", fontSize = 18.sp)
        }
    }) { innerPadding ->
        val filteredPosts = posts.filter { post ->
            try {
                // Adjust this depending on your PostEntity.date format
                val postDate = LocalDate.parse(post.date.toString())
                postDate == currentDate
            } catch (e: Exception) {
                false // skip invalid date formats
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (filteredPosts.isEmpty()) {
                item {
                    Text(
                        text = "${currentDate.format(DateTimeFormatter.ofPattern("yyyy年M月d日"))}暂无帖子",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                item {
                    val totalProfitSum = (filteredPosts.sumOf {
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

                items(filteredPosts) { post ->
                    PostItem(
                        post = post, onEditClick = onEditClick,
                    )
                    HorizontalDivider()
                }
            }
        }

        // Date Picker Dialog
        if (showDatePicker) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = currentDate.toEpochDay() * 24 * 60 * 60 * 1000
            )

            DatePickerDialog(onDismissRequest = { showDatePicker = false }, confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            currentDate = LocalDate.ofEpochDay(millis / (24 * 60 * 60 * 1000))
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
    }
}

@Composable
fun PostItem(
    post: PostEntity,
    onEditClick: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick(post.postId) } // 👈 opens edit screen
            .padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = post.date.toString(), style = MaterialTheme.typography.titleMedium)
        Text(text = post.title, style = MaterialTheme.typography.titleMedium)
        Text(text = "%.2f".format(post.totalProfit), style = MaterialTheme.typography.titleMedium)
    }
}










