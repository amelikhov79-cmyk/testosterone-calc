package com.example.testosteronecalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.*
import java.text.DecimalFormat

@Composable
fun HistoryScreen(storage: HistoryStorage) {
    var items by remember { mutableStateOf(storage.load()) }
    val df = DecimalFormat("#.####")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("История", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = {
                storage.clear()
                items = mutableListOf()
            }) {
                Icon(Icons.Filled.Delete, contentDescription = "Очистить")
            }
        }
        Spacer(Modifier.height(10.dp))

        if (items.isEmpty()) {
            Text("Пока пусто. Сохрани расчёт из вкладки «Калькулятор».")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            val tag = if (item.type == "free") "🧪 Свободный T" else "📊 Общий T"
                            Text(tag, style = MaterialTheme.typography.labelSmall)
                            Spacer(Modifier.height(4.dp))
                            if (item.type == "free") {
                                Text(
                                    "Общий T: ${df.format(item.inputValue)} ${unitLabel(item.fromUnit)}  →  " +
                                            "Свободный T: ${df.format(item.outputValue)} пг/мл",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                Text(
                                    "ГСПГ: ${item.shbg ?: "-"} нмоль/л · Альбумин: ${item.albumin ?: "-"} г/л",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            } else {
                                Text(
                                    "${df.format(item.inputValue)} ${unitLabel(item.fromUnit)} → " +
                                            "${df.format(item.outputValue)} ${unitLabel(item.toUnit)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            Text(
                                storage.formatDate(item.timestamp),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun unitLabel(name: String): String =
    runCatching { Unit.valueOf(name).label }.getOrDefault(name)
