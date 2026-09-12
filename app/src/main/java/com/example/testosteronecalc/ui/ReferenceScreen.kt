package com.example.testosteronecalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.*

@Composable
fun ReferenceScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Референсные диапазоны", style = MaterialTheme.typography.headlineSmall)
        Text("Общий тестостерон (нмоль/л)", style = MaterialTheme.typography.titleMedium)

        Category.values().forEach { c ->
            val r = referenceRange(c)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(c.label, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    Row {
                        Box(Modifier.background(Color(Status.LOW.color)).size(12.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("< ${r.low} — понижен", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(Modifier.padding(top = 4.dp)) {
                        Box(Modifier.background(Color(Status.NORMAL.color)).size(12.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("${r.low} – ${r.high} — норма", style = MaterialTheme.typography.bodySmall)
                    }
                    Row(Modifier.padding(top = 4.dp)) {
                        Box(Modifier.background(Color(Status.HIGH.color)).size(12.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("> ${r.high} — повышен", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))
        Text("Свободный тестостерон (пг/мл)", style = MaterialTheme.typography.titleMedium)
        Category.values().forEach { c ->
            val r = freeTestosteroneRange(c)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(c.label, style = MaterialTheme.typography.titleSmall)
                    Text(
                        "${r.low} – ${r.high} пг/мл",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        Text(
            "⚠️ Диапазоны ориентировочные. Точные нормы — только по референсам вашей лаборатории.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
