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
import com.example.testosteronecalc.AgeGroup
import com.example.testosteronecalc.Category
import com.example.testosteronecalc.Status
import com.example.testosteronecalc.referenceRange

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

        // ===== Общий тестостерон =====
        Text("Общий тестостерон (нмоль/л)", style = MaterialTheme.typography.titleMedium)

        Category.values().forEach { c ->
            val r = referenceRange(c)
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(c.label, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(6.dp))
                    ColorRow(Status.LOW, "< ${r.low} — понижен")
                    ColorRow(Status.NORMAL, "${r.low} – ${r.high} — норма")
                    ColorRow(Status.HIGH, "> ${r.high} — повышен")
                }
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        // ===== Свободный тестостерон =====
        Text("Свободный тестостерон (нмоль/л)", style = MaterialTheme.typography.titleMedium)
        Text(
            "По возрастным группам (источник: unclinic.ru)",
            style = MaterialTheme.typography.bodySmall
        )

        AgeGroup.values().forEach { g ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Text(g.label, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "${g.low} – ${g.high} нмоль/л",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        // ===== Доли фракций =====
        Text("Доли фракций от общего T (%)", style = MaterialTheme.typography.titleMedium)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                ColorRow(Status.LOW, "Свободный < 1,5 %")
                ColorRow(Status.NORMAL, "Свободный 1,5 – 3,5 %")
                ColorRow(Status.HIGH, "Свободный > 3,5 %")
                Spacer(Modifier.height(6.dp))
                ColorRow(Status.LOW, "Биодоступный < 30 %")
                ColorRow(Status.NORMAL, "Биодоступный 30 – 60 %")
                ColorRow(Status.HIGH, "Биодоступный > 60 %")
            }
        }

        Text(
            "⚠️ Диапазоны ориентировочные. Точные нормы — только по референсам вашей лаборатории.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun ColorRow(status: Status, text: String) {
    Row(Modifier.padding(top = 4.dp)) {
        Box(
            Modifier
                .background(Color(status.color))
                .size(12.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(text, style = MaterialTheme.typography.bodySmall)
    }
}
