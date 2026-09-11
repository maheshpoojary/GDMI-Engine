package com.mahesh.gdmi.engine

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ReverseEdgeScreen(
    viewModel: ReverseEdgeViewModel = viewModel()
) {

    var score by remember { mutableStateOf("100") }
    var overs by remember { mutableStateOf("10.0") }
    var wickets by remember { mutableStateOf("3") }
    var marketLine by remember { mutableStateOf("8.5") }
    var recentRuns by remember { mutableStateOf("10") }
    var oddsOver by remember { mutableStateOf("1.90") }
    var oddsUnder by remember { mutableStateOf("1.90") }

    val result by viewModel.result.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "GDMI — Reverse Edge Engine",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(20.dp))

        InputRow(
            firstLabel = "Score",
            firstValue = score,
            onFirstChange = { score = it },

            secondLabel = "Overs",
            secondValue = overs,
            onSecondChange = { overs = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        InputRow(
            firstLabel = "Wickets",
            firstValue = wickets,
            onFirstChange = { wickets = it },

            secondLabel = "Market Line",
            secondValue = marketLine,
            onSecondChange = { marketLine = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        InputRow(
            firstLabel = "Recent Over Runs",
            firstValue = recentRuns,
            onFirstChange = { recentRuns = it },

            secondLabel = "Over Odds",
            secondValue = oddsOver,
            onSecondChange = { oddsOver = it }
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = oddsUnder,
            onValueChange = { oddsUnder = it },
            label = { Text("Under Odds") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

                viewModel.analyze(
                    score = score.toIntOrNull() ?: 0,
                    overs = overs.toDoubleOrNull() ?: 0.0,
                    wickets = wickets.toIntOrNull() ?: 0,
                    marketLine = marketLine.toDoubleOrNull() ?: 0.0,
                    recentOverRuns = recentRuns.toDoubleOrNull() ?: 0.0,
                    oddsOver = oddsOver.toDoubleOrNull() ?: 0.0,
                    oddsUnder = oddsUnder.toDoubleOrNull() ?: 0.0
                )

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("RUN REVERSE ENGINE")
        }

        Spacer(modifier = Modifier.height(20.dp))

        result?.let { output ->

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = "ENGINE RESULT",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Expected Over Runs: %.2f"
                            .format(output.expectedOverRuns)
                    )

                    Text(
                        text = "Edge: %.2f"
                            .format(output.edge)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SIGNAL: ${output.signal}",
                        style = MaterialTheme.typography.headlineMedium
                    )

                    Text(
                        text = "Confidence: ${output.confidence}%"
                    )
                }
            }
        }
    }
}

@Composable
private fun InputRow(
    firstLabel: String,
    firstValue: String,
    onFirstChange: (String) -> Unit,
    secondLabel: String,
    secondValue: String,
    onSecondChange: (String) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        OutlinedTextField(
            value = firstValue,
            onValueChange = onFirstChange,
            label = { Text(firstLabel) },
            modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
            value = secondValue,
            onValueChange = onSecondChange,
            label = { Text(secondLabel) },
            modifier = Modifier.weight(1f)
        )
    }
}
