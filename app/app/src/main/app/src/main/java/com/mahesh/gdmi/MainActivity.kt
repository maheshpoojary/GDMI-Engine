package com.mahesh.gdmi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mahesh.gdmi.engine.ReverseEdgeInput
import com.mahesh.gdmi.engine.ReverseEdgeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GDMIApp()
        }
    }
}

@androidx.compose.runtime.Composable
fun GDMIApp() {

    val viewModel = remember {
        ReverseEdgeViewModel()
    }

    var present by remember { mutableStateOf("") }
    var expected by remember { mutableStateOf("") }
    var target by remember { mutableStateOf("") }
    var movement by remember { mutableStateOf("") }
    var risk by remember { mutableStateOf("") }
    var timing by remember { mutableStateOf("") }

    var result by remember {
        mutableStateOf("Waiting for analysis...")
    }

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = "GENERAL DECISION & MATHEMATICAL INTELLIGENCE ENGINE",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                OutlinedTextField(
                    value = present,
                    onValueChange = { present = it },
                    label = { Text("Present") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = expected,
                    onValueChange = { expected = it },
                    label = { Text("Expected") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = target,
                    onValueChange = { target = it },
                    label = { Text("Target") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = movement,
                    onValueChange = { movement = it },
                    label = { Text("Movement") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = risk,
                    onValueChange = { risk = it },
                    label = { Text("Risk") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )

                OutlinedTextField(
                    value = timing,
                    onValueChange = { timing = it },
                    label = { Text("Timing") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = {

                        val input = ReverseEdgeInput(
                            present = present.toDoubleOrNull() ?: 0.0,
                            expected = expected.toDoubleOrNull() ?: 0.0,
                            target = target.toDoubleOrNull() ?: 0.0,
                            movement = movement.toDoubleOrNull() ?: 0.0,
                            risk = risk.toDoubleOrNull() ?: 0.0,
                            timing = timing.toDoubleOrNull() ?: 0.0
                        )

                        val output = viewModel.analyze(input)

                        result =
                            """
                            GAP: ${output.gap}
                            
                            EDGE SCORE: ${output.edgeScore}
                            
                            DECISION: ${output.decision}
                            
                            ${output.explanation}
                            """.trimIndent()
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ANALYZE")
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "GDMI RESULT",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = result,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
