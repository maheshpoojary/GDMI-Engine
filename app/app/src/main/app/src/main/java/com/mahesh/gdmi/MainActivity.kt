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

    var value1 by remember {
        mutableStateOf("")
    }

    var value2 by remember {
        mutableStateOf("")
    }

    var value3 by remember {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf("Waiting for input...")
    }

    MaterialTheme {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),

                verticalArrangement = Arrangement.Top
            ) {

                Text(
                    text = "🧠 GDMIE",
                    style = MaterialTheme.typography.headlineLarge
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "General Decision & Mathematical Intelligence Engine",
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                OutlinedTextField(
                    value = value1,
                    onValueChange = {
                        value1 = it
                    },
                    label = {
                        Text("Input 1")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = value2,
                    onValueChange = {
                        value2 = it
                    },
                    label = {
                        Text("Input 2")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                OutlinedTextField(
                    value = value3,
                    onValueChange = {
                        value3 = it
                    },
                    label = {
                        Text("Input 3")
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                Button(
                    onClick = {

                        val input = mutableMapOf<String, Double>()

                        value1.toDoubleOrNull()?.let {
                            input["input1"] = it
                        }

                        value2.toDoubleOrNull()?.let {
                            input["input2"] = it
                        }

                        value3.toDoubleOrNull()?.let {
                            input["input3"] = it
                        }

                        if (input.isEmpty()) {

                            result = "NO DATA"

                        } else {

                            val gdmi = GDMIEngine()

                            val output = gdmi.process(input)

                            result =
                                """
                                SCORE: ${output.score}
                                
                                DECISION: ${output.decision}
                                
                                ${output.explanation}
                                """.trimIndent()
                        }
                    },

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        text = "ANALYZE"
                    )
                }

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "GDMI RESULT",
                    style = MaterialTheme.typography.titleMedium
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
