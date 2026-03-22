package io.github.ajaypal.swipeablelist.sample.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack

@Composable
fun SwipeableContentScreen() {
    var currentScreen by rememberSaveable { mutableStateOf(SampleDestination.Home) }

    SampleScaffold(
        title = currentScreen.title,
        showBack = currentScreen != SampleDestination.Home,
        onBack = { currentScreen = SampleDestination.Home },
    ) { contentPadding ->
        when (currentScreen) {
            SampleDestination.Home -> SampleMenuScreen(
                modifier = Modifier.padding(contentPadding),
                onNavigate = { currentScreen = it },
            )
            SampleDestination.FlatList -> FlatListSampleScreen(modifier = Modifier.padding(contentPadding))
            SampleDestination.GroupedList -> GroupedListSampleScreen(modifier = Modifier.padding(contentPadding))
            SampleDestination.ScrollTo -> ScrollToSampleScreen(modifier = Modifier.padding(contentPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleScaffold(
    title: String,
    showBack: Boolean,
    onBack: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                    }
                },
            )
        },
    ) { contentPadding ->
        Surface(modifier = Modifier.fillMaxSize()) {
            content(contentPadding)
        }
    }
}

enum class SampleDestination(val title: String) {
    Home("Samples"),
    FlatList("SwipeableList"),
    GroupedList("GroupedSwipeableList"),
    ScrollTo("Scroll To"),
}
