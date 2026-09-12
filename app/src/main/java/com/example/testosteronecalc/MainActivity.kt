package com.example.testosteronecalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.testosteronecalc.ui.CalculatorScreen
import com.example.testosteronecalc.ui.FreeTestosteroneScreen
import com.example.testosteronecalc.ui.HistoryScreen
import com.example.testosteronecalc.ui.ReferenceScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(applicationContext)
                }
            }
        }
    }
}

enum class Tab(val label: String, val icon: ImageVector) {
    CALC("Калькулятор", Icons.Filled.Calculate),
    FREE("Фракции T", Icons.Filled.Science),
    REF("Нормы", Icons.Filled.CheckCircle),
    HISTORY("История", Icons.Filled.History)
}

@Composable
fun AppRoot(context: android.content.Context) {
    var tab by remember { mutableStateOf(Tab.CALC) }
    val storage = remember { HistoryStorage(context) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                Tab.values().forEach { t ->
                    NavigationBarItem(
                        selected = tab == t,
                        onClick = { tab = t },
                        icon = { Icon(t.icon, contentDescription = t.label) },
                        label = { Text(t.label) }
                    )
                }
            }
        }
    ) { padding ->
        // Все экраны держатся в памяти одновременно.
        // Виден только активный, остальные скрыты.
        Box(Modifier.padding(padding)) {

            // Калькулятор
            HiddenTab(visible = tab == Tab.CALC) {
                CalculatorScreen(storage = storage)
            }

            // Фракции T
            HiddenTab(visible = tab == Tab.FREE) {
                FreeTestosteroneScreen(storage = storage)
            }

            // Нормы
            HiddenTab(visible = tab == Tab.REF) {
                ReferenceScreen()
            }

            // История
            HiddenTab(visible = tab == Tab.HISTORY) {
                HistoryScreen(storage = storage)
            }
        }
    }
}

/**
 * Показывает содержимое только когда visible == true.
 * Но НЕ удаляет композицию — состояние (remember) сохраняется.
 */
@Composable
private fun HiddenTab(visible: Boolean, content: @Composable () -> Unit) {
    if (visible) {
        content()
    }
}
