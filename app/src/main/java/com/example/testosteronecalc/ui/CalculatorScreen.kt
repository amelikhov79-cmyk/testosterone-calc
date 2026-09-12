package com.example.testosteronecalc.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.testosteronecalc.Category
import com.example.testosteronecalc.HistoryItem
import com.example.testosteronecalc.HistoryStorage
import com.example.testosteronecalc.Status
import com.example.testosteronecalc.TUnit
import com.example.testosteronecalc.convert
import com.example.testosteronecalc.evaluate
import com.example.testosteronecalc.toNmolL
import java.text.DecimalFormat

private val NullableDoubleSaver = Saver<Double?, Any>(
    save = { it ?: "null" },
    restore = { if (it == "null") null else (it as Number).toDouble() }
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(storage: HistoryStorage) {
    var input by rememberSaveable { mutableStateOf("") }
    var fromUnit by rememberSaveable { mutableStateOf(TUnit.NG_ML) }
    var toUnit by rememberSaveable { mutableStateOf(TUnit.NMOL_L) }
    var category by rememberSaveable { mutableStateOf(Category.MALE_ADULT) }

    var result by rememberSaveable(stateSaver = NullableDoubleSaver) {
        mutableStateOf<Double?>(null)
    }
    var statusName by rememberSaveable { mutableStateOf<String?>(null) }
    val status = statusName?.let { runCatching { Status.valueOf(it) }.getOrNull() }

    val df = DecimalFormat("0.00")

    fun doConvert() {
        val v = input.replace(',', '.').toDoubleOrNull() ?: return
        val out = convert(v, fromUnit, toUnit)
        val nmol = toNmolL(v, fromUnit)
        result = out
        statusName = evaluate(nmol, category).name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text("Калькулятор тестостерона", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = input,
            onValueChange = { input = it },
            label = { Text("Значение") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )

        UnitDropdown("Из единицы", fromUnit) { fromUnit = it }
        UnitDropdown("В единицу", toUnit) { toUnit = it }
        CategoryDropdown(category) { category = it }

        Button(onClick = { doConvert() }, modifier = Modifier.fillMaxWidth()) {
            Text("Пересчитать")
        }

        result?.let { r ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("Результат", style = MaterialTheme.typography.labelMedium)
                    Text(
                        "${df.format(r)} ${toUnit.label}",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    status?.let { s ->
                        Spacer(Modifier.height(8.dp))
                        Box(
                            Modifier
                                .background(Color(s.color))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(s.label, color = Color.White)
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(onClick = {
                        val v = input.replace(',', '.').toDoubleOrNull() ?: return@OutlinedButton
                        storage.add(
                            HistoryItem(
                                inputValue = v,
                                fromUnit = fromUnit.name,
                                outputValue = r,
                                toUnit = toUnit.name,
                                timestamp = System.currentTimeMillis()
                            )
                        )
                    }) {
                        Text("💾 Сохранить в историю")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(label: String, selected: TUnit, onSelect: (TUnit) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            TUnit.values().forEach { u ->
                DropdownMenuItem(
                    text = { Text(u.label) },
                    onClick = { onSelect(u); expanded = false }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryDropdown(selected: Category, onSelect: (Category) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Категория") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            Category.values().forEach { c ->
                DropdownMenuItem(
                    text = { Text(c.label) },
                    onClick = { onSelect(c); expanded = false }
                )
            }
        }
    }
}
