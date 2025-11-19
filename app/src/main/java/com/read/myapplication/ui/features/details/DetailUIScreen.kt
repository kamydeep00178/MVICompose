package com.read.myapplication.ui.features.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlin.let

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailUIScreen(onBack: () -> Unit, vm: DetailsViewModel = hiltViewModel()) {
    val ui by vm.uiState.collectAsState()

    Scaffold(topBar = {
        TopAppBar(title = { Text("Post Details") }, navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        })
    }) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (ui.loading) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Loading...",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                    )
                }
                return@Box
            }

            ui.error?.let { err ->
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Error: $err")
                }
                return@Box
            }

            val detail = ui.detail
            if (detail != null) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(detail.title, style = MaterialTheme.typography.headlineSmall)
                    Text(detail.body, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(8.dp))
                    Text("ID: ${detail.id}", style = MaterialTheme.typography.labelMedium)
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No detail found")
                }
            }
        }
    }
}