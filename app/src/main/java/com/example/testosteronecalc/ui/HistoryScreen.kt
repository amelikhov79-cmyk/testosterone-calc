package com.example.testosteronecalc.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.HistoryItem
import com.example.testosteronecalc.HistoryStorage
import com.example.testosteronecalc.TUnit
import java.text.DecimalFormat

@Composable
fun HistoryScreen(storage: HistoryStorage) {
    var items by remember { mutableStateOf(storage.load()) }
    var showClearDialog by remember { mutableStateOf(false) }
    val df = DecimalFormat("0.00")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("История", style = MaterialTheme.typography.headlineSmall)
            if (items.isNotEmpty()) {
                TextButton(onClick = { showClearDialog = true }) {
                    Text("Очистить всё")
                }
            }
        }
        Spacer(Modifier.height(10.dp))

        if (items.isEmpty()) {
            Text("Пока пусто. Сохрани расчёт из вкладки «Калькулятор».")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(items, key = { it.timestamp }) { item ->
                    Card(Modifier.fillMaxWidth()) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(Modifier.weight(1f)) {
                                val tag = if (item.type == "free") "🧪 Фракции T" else "📊 Общий T"
                                Text(tag, style = MaterialTheme.typography.labelSmall)
                                Spacer(Modifier.height(4.dp))
                                if (item.type == "free") {
                                    Text(
                                        "Общий T: ${df.format(item.inputValue)} ${unitLabel(item.fromUnit)}",
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Text(
                                        "Свободный T: ${df.format(item.outputValue)} нмоль/л" +
                                                (item.freePercent?.let { " (${df.format(it)} %)" } ?: ""),
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    item.bioavailable?.let { bio ->
                                        Text(
                                            "Биодоступный T: ${df.format(bio)} нмоль/л" +
                                                    (item.bioPercent?.let { " (${df.format(it)} %)" } ?: ""),
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
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
                            IconButton(onClick = {
                                storage.remove(item)
                                items = storage.load()
                            }) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = "Удалить запись"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Очистить всю историю?") },
            text = { Text("Все записи будут удалены. Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(onClick = {
                    storage.clear()
                    items = mutableListOf()
                    showClearDialog = false
                }) { Text("Удалить всё") }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) { Text("Отмена") }
            }
        )
    }
}

private fun unitLabel(name: String): String =
    runCatching { TUnit.valueOf(name).label }.getOrDefault(name)
