package io.github.ajaypal.swipeablelist.sample.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ajaypal.swipeablelist.SwipeableList
import io.github.ajaypal.swipeablelist.sample.ui.theme.SampleTheme

@Composable
fun FlatListSampleScreen(modifier: Modifier = Modifier) {
    val contacts = remember {
        mutableStateListOf(
            Contact("alice", "Alice"),
            Contact("amelia", "Amelia"),
            Contact("bella", "Bella"),
            Contact("carter", "Carter"),
        )
    }
    val removingIds = remember { mutableStateListOf<String>() }

    SampleScreen(
        modifier = modifier,
        title = "SwipeableList",
        subtitle = "Ungrouped list with independent swipe actions and animated item removal.",
    ) {
        SwipeableList(
            items = contacts,
            key = { it.id },
            itemSpacing = 12.dp,
            contentPadding = PaddingValues(bottom = 16.dp),
            removingKeys = removingIds.toSet(),
            onRemoveAnimationFinished = { contact ->
                contacts.remove(contact)
                removingIds.remove(contact.id)
            },
            mainContent = { contact ->
                ContactRow(
                    title = contact.name,
                    subtitle = "Swipe left to remove with animation",
                )
            },
            leftContent = { _, closeRow ->
                ArchiveAction(onClick = closeRow)
            },
            rightContent = { contact, closeRow ->
                DeleteAction {
                    closeRow()
                    if (contact.id !in removingIds) {
                        removingIds.add(contact.id)
                    }
                }
            },
        )
    }
}

@Preview
@Composable
private fun PreviewFlatListSampleScreen() {
    SampleTheme {
        FlatListSampleScreen()
    }
}
