package com.example.flowstate.features

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.flowstate.data.FlowstateDatabaseHelper

@Composable
fun DashboardScreen(
    navController: NavController,
    dbHelper: FlowstateDatabaseHelper,
    modifier: Modifier = Modifier
) {
    Column {
        Text("Hello World")

        Button(onClick = {
            navController.navigate("details/test-assignment-1")
        }) {
            Text("Open Test Assignment")
        }

    }
}
