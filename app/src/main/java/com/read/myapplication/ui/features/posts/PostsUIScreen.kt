package com.read.myapplication.ui.features.posts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.read.myapplication.domain.model.Post
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostsUIScreen(
    navController: NavController,
    vm: PostsListViewModel = hiltViewModel()
) {
    val ui by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // trigger initial load exactly once
    LaunchedEffect(Unit) { vm.submit(PostsIntent.Load) }

    // Collect one-off events to show snackbars (and navigate if you emit navigate)
    LaunchedEffect(vm.events) {
        vm.events.collect { event ->
            when (event) {
                is PostsUiEvent.Snackbar -> {
                    scope.launch { snackbarHostState.showSnackbar(event.text) }
                }

                is PostsUiEvent.Navigate -> {
                    navController.navigate(event.route)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Posts") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            if (ui.loading) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.align(alignment = Alignment.CenterHorizontally))
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Loading posts...",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
                        )
                    }
                }

                // Error state
                ui.error?.let { errorMsg ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Error loading posts", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        Text(errorMsg, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(12.dp))
                        Row {
                            Button(onClick = { vm.submit(PostsIntent.Load) }) { Text("Retry") }
                            Spacer(Modifier.width(8.dp))
                        }
                    }
                }

                // Empty state
                if (ui.posts.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("No posts available", style = MaterialTheme.typography.headlineSmall)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.submit(PostsIntent.Load) }) { Text("Load posts") }
                    }
                }

                // Content: list of posts
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ui.posts) { post ->
                        PostCard(
                            post = post,
                            onClick = { vm.submit(PostsIntent.PostClicked(post.id)) }
                        )
                    }
                }
            }
    }
}

@Composable
private fun PostCard(post: Post, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = post.title, style = MaterialTheme.typography.titleMedium, maxLines = 2)
            Spacer(Modifier.height(6.dp))
            Text(text = post.body, style = MaterialTheme.typography.bodyMedium, maxLines = 3)
            Spacer(Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text("ID: ${post.id}", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}