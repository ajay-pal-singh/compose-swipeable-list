package io.github.ajaypal.swipeablelist.sample.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.github.ajaypal.swipeablelist.GroupedSwipeableList
import io.github.ajaypal.swipeablelist.sample.ui.theme.SampleTheme

@Composable
fun ScrollToSampleScreen(modifier: Modifier = Modifier) {
    val places = remember {
        listOf(
            Place("adelaide", "Adelaide"),
            Place("alice-springs", "Alice Springs"),
            Place("ballarat", "Ballarat"),
            Place("brisbane", "Brisbane"),
            Place("broome", "Broome"),
            Place("cairns", "Cairns"),
            Place("darwin", "Darwin"),
            Place("geelong", "Geelong"),
            Place("gold-coast", "Gold Coast"),
            Place("hobart", "Hobart"),
            Place("launceston", "Launceston"),
            Place("melbourne", "Melbourne"),
            Place("newcastle", "Newcastle"),
            Place("perth", "Perth"),
            Place("sydney", "Sydney"),
            Place("townsville", "Townsville"),
            Place("wollongong", "Wollongong"),
        )
            .sortedBy { it.label }
    }
    val groupedPlaces = remember(places) { places.groupBy { it.label.first() }.toSortedMap() }
    var scrollTargetKey by remember { mutableStateOf<String?>(null) }

    SampleScreen(
        modifier = modifier,
        title = "Scroll To",
        subtitle = "Grouped and alphabetized scroll-to-key. Header rows are counted internally and do not break item targeting.",
        controls = {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { scrollTargetKey = "ballarat" }) {
                    Text("Scroll To Ballarat")
                }
                Button(onClick = { scrollTargetKey = "wollongong" }) {
                    Text("Scroll To Wollongong")
                }
            }
        },
    ) {
        GroupedSwipeableList(
            groupsMap = groupedPlaces,
            key = { it.id },
            scrollToKey = scrollTargetKey,
            onScrollComplete = { scrollTargetKey = null },
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(bottom = 16.dp),
            groupHeader = { header ->
                Text(
                    modifier = Modifier,
                    text = header.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            },
            mainContent = { place ->
                ContactRow(
                    title = place.label,
                    subtitle = "Tap the buttons above to jump here across grouped sections",
                    leadingIcon = Icons.Default.Place,
                )
            },
            leftContent = { _, closeRow ->
                ArchiveAction(onClick = closeRow)
            },
        )
    }
}

@Preview
@Composable
private fun PreviewScrollToSampleScreen() {
    SampleTheme {
        ScrollToSampleScreen()
    }
}
