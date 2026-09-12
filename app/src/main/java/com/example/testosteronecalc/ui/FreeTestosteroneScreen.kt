package com.example.testosteronecalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.*
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeTestosteroneScreen(storage: HistoryStorage) {
    var totalT by remember { mutableStateOf("") }
    var totalUnit by remember { mutableStateOf(Unit.NMOL_L) }
    var shbg by remember { mutableStateOf("") }
    var albumin by remember { mutableStateOf("43") }
    var category by remember { mutableStateOf(Category.MALE_ADULT) }

    var freeResult by remember { mutableStateOf<Double?>(null) }
    var freeStatus by remember { mutableStateOf<Status?>(null) }

    val df = DecimalFormat("#.##")

    fun calculate() {
        val t = totalT.replace(',', '.').toDoubleOrNull() ?: return
        val s = shbg.replace(',', '.').toDoubleOrNull() ?: return
        val a = albumin.replace(',', '.').toDoubleOrNull() ?: 43.0

        val tNmol = toNmolL(t, totalUnit)
        val ft = calculateFreeTestosterone(tNmol, s, a)
        freeResult = ft

        val range = freeTestosteroneRange(category)
        freeStatus = when {
            ft < range.low -> Status.LOW
            ft > range.high -> Status.HIGH
            else -> Status.NORMAL
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Свободный тестостерон", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Расчёт по формуле Vermeulen (1999)",
            style = MaterialTheme.typography.bodySmall
        )

        OutlinedTextField(
            value = totalT,
            onValueChange = { totalT = it },
            label = { Text("Общий тестостерон") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        UnitDropdown("Единица общего T", totalUnit) { totalUnit = it }

        OutlinedTextField(
            value = shbg,
            onValueChange = { shbg = it },
            label = { Text("ГСПГ / SHBG (нмоль/л)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = albumin,
            onValueChange = { albumin = it },
            label = { Text("Альбумин (г/л)") },
            supportingText = { Text("По умолчанию 43 г/л (4.3 г/дл)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        CategoryDropdown(category) { category = it }

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Рассчитать свободный T")
        }

        freeResult?.let { ft ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Свободный тестостерон", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${df.format(ft)} пг/мл",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "≈ ${df.format(ft / 288.42)} пмоль/л",
                        style = MaterialTheme.typography.bodySmall
                    )
                    freeStatus?.let { s ->
                        Spacer(Modifier.height(10.dp))
                        Box(
                            Modifier
                                .background(Color(s.color))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(s.label, color = Color.White)
                        }
                    }
                    val r = freeTestosteroneRange(category)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Референс (${category.label}): ${r.low}–${r.high} пг/мл",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick = {
                        storage.add(
                            HistoryItem(
                                inputValue = totalT.replace(',', '.').toDoubleOrNull() ?: 0.0,
                                fromUnit = totalUnit.name,
                                outputValue = ft,
                                toUnit = "PG_ML",
                                timestamp = System.currentTimeMillis(),
                                type = "free",
                                shbg = shbg.replace(',', '.').toDoubleOrNull(),
                                albumin = albumin.replace(',', '.').toDoubleOrNull()
                            )
                        )
                    }) {
                        Text("💾 Сохранить в историю")
                    }
                }
            }
        }

        Text(
            "ℹ️ Альбумин по умолчанию 43 г/л — среднее значение. Если у тебя есть анализ, измени вручную.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
