package com.example.calculadorapropinas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculadorapropinas.ui.theme.CalculadoraPropinasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CalculadoraPropinasTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TipCalculatorApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TipCalculatorApp() {
    // --- ESTADO con remember y mutableStateOf ---
    var billAmount by remember { mutableStateOf("") }
    var selectedTipPercent by remember { mutableStateOf(15) }
    var numberOfPeople by remember { mutableStateOf(1) }
    var tipAmount by remember { mutableStateOf(0.0) }
    var totalAmount by remember { mutableStateOf(0.0) }
    var perPersonAmount by remember { mutableStateOf(0.0) }
    var showResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculadora de Propinas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ── TextField: Monto de la cuenta ──
            OutlinedTextField(
                value = billAmount,
                onValueChange = {
                    billAmount = it
                    showResult = false
                },
                label = { Text("Monto de la cuenta") },
                leadingIcon = { Text("$") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Botones de porcentaje de propina ──
            Text(
                text = "Porcentaje de propina",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(10, 15, 20).forEach { pct ->
                    FilterChip(
                        selected = selectedTipPercent == pct,
                        onClick = {
                            selectedTipPercent = pct
                            showResult = false
                        },
                        label = { Text("$pct%") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── TextField: Número de personas ──
            OutlinedTextField(
                value = numberOfPeople.toString(),
                onValueChange = { input ->
                    val parsed = input.toIntOrNull()
                    if (parsed != null && parsed > 0) {
                        numberOfPeople = parsed
                        showResult = false
                    }
                },
                label = { Text("Número de personas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Botón de calcular ──
            Button(
                onClick = {
                    val bill = billAmount.toDoubleOrNull() ?: 0.0
                    tipAmount = bill * (selectedTipPercent / 100.0)
                    totalAmount = bill + tipAmount
                    perPersonAmount = totalAmount / numberOfPeople
                    showResult = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = billAmount.isNotBlank()
            ) {
                Text("Calcular Propina", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Resultado dinámico con mutableStateOf ──
            if (showResult) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ResultRow("Monto base", "$${"%.2f".format(billAmount.toDoubleOrNull() ?: 0.0)}")
                        ResultRow("Propina ($selectedTipPercent%)", "$${"%.2f".format(tipAmount)}")
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ResultRow(
                            label = "Total",
                            value = "$${"%.2f".format(totalAmount)}",
                            isBold = true
                        )
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        ResultRow("Por persona", "$${"%.2f".format(perPersonAmount)}")
                    }
                }
            }
        }
    }
}

@Composable
fun ResultRow(label: String, value: String, isBold: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = value,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}