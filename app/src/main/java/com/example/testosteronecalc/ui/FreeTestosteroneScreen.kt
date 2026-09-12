package com.example.testosteronecalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.AgeGroup
import com.example.testosteronecalc.HistoryItem
import com.example.testosteronecalc.HistoryStorage
import com.example.testosteronecalc.Status
import com.example.testosteronecalc.TUnit
import com.example.testosteronecalc.calculateFractions
import com.example.testosteronecalc.evaluateBioPercent
import com.example.testosteronecalc.evaluateFree
import com.example.testosteronecalc.freeTestosteroneRangeNmol
import com.example.testosteronecalc.toNmolL
import java.text.DecimalFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FreeTestosteroneScreen(storage: HistoryStorage) {
    var totalT by rememberSaveable { mutableStateOf("") }
    var totalUnit by rememberSaveable { mutableStateOf(TUnit.NMOL_L) }
    var shbg by rememberSaveable { mutableStateOf("") }
    var albumin by rememberSaveable { mutableStateOf("43") }
    var ageGroup by rememberSaveable { mutableStateOf(AgeGroup.M_18_29) }

    var freeNmol by remember { mutableStateOf<Double?>(null) }
    var bioNmol by remember { mutableStateOf<Double?>(null) }
    var freePercent by remember { mutableStateOf<Double?>(null) }
    var bioPercent by remember { mutableStateOf<Double?>(null) }
    var freeStatus by remember { mutableStateOf<Status?>(null) }
    var bioStatus by remember { mutableStateOf<Status?>(null) }

    val df = DecimalFormat("0.00")

    fun calculate() {
        val t = totalT.replace(',', '.').toDoubleOrNull() ?: return
        val s = shbg.replace(',', '.').toDoubleOrNull() ?: return
        val a = albumin.replace(',', '.').toDoubleOrNull() ?: 43.0

        val tNmol = toNmolL(t, totalUnit)
        if (tNmol <= 0.0) return

        val f = calculateFractions(tNmol, s, a)

        freeNmol = f.freeNmolL
        bioNmol = f.bioavailableNmolL
        freePercent = f.freeNmolL / tNmol * 100.0
        bioPercent = f.bioavailableNmolL / tNmol * 100.0

        freeStatus = evaluateFree(f.freeNmolL, ageGroup)
        bioStatus = evaluateBioPercent(bioPercent!!)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Фракции тестостерона", style = MaterialTheme.typography.headlineSmall)
        Text(
            "Свободный и биодоступный T по формуле Vermeulen (1999)",
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

        AgeGroupDropdown(ageGroup) { ageGroup = it }

        Button(onClick = { calculate() }, modifier = Modifier.fillMaxWidth()) {
            Text("Рассчитать")
        }

        freeNmol?.let { ft ->
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("СВОБОДНЫЙ ТЕСТОСТЕРОН",
                        style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${df.format(ft)} нмоль/л",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "≈ ${df.format(ft * 288.42)} пг/мл",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Доля: ${df.format(freePercent ?: 0.0)} % от общего",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    freeStatus?.let { s ->
                        Spacer(Modifier.height(8.dp))
                        StatusBadge(s)
                    }
                    val r = freeTestosteroneRangeNmol(ageGroup)
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Референс (${ageGroup.label}): ${r.low}–${r.high} нмоль/л",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("БИОДОСТУПНЫЙ ТЕСТОСТЕРОН",
                        style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${df.format(bioNmol ?: 0.0)} нмоль/л",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "≈ ${df.format((bioNmol ?: 0.0) * 288.42)} пг/мл",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Доля: ${df.format(bioPercent ?: 0.0)} % от общего",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    bioStatus?.let { s ->
                        Spacer(Modifier.height(8.dp))
                        StatusBadge(s)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Норма доли: 30–60 % от общего",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            OutlinedButton(
                onClick = {
                    storage.add(
                        HistoryItem(
                            inputValue = totalT.replace(',', '.').toDoubleOrNull() ?: 0.0,
                            fromUnit = totalUnit.name,
                            outputValue = ft,
                            toUnit = "NMOL_L",
                            timestamp = System.currentTimeMillis(),
                            type = "free",
                            shbg = shbg.replace(',', '.').toDoubleOrNull(),
                            albumin = albumin.replace(',', '.').toDoubleOrNull(),
                            bioavailable = bioNmol,
                            freePercent = freePercent,
                            bioPercent = bioPercent
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("💾 Сохранить в историю")
            }
        }

        Text(
            "ℹ️ Если свободный T ниже 0,225 нмоль/л — обратитесь к эндокринологу. " +
                    "Доли: свободный 1,5–3,5 %, биодоступный 30–60 % от общего.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun StatusBadge(status: Status) {
    Box(
        Modifier
            .background(Color(status.color))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(status.label, color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgeGroupDropdown(selected: AgeGroup, onSelect: (AgeGroup) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Возрастная группа") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            AgeGroup.values().forEach { g ->
                DropdownMenuItem(
                    text = { Text(g.label) },
                    onClick = { onSelect(g); expanded = false }
                )
            }
        }
    }
}
