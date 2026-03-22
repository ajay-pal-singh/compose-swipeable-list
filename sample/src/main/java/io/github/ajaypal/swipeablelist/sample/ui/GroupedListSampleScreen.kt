package io.github.ajaypal.swipeablelist.sample.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.ajaypal.swipeablelist.GroupedSwipeableList
import io.github.ajaypal.swipeablelist.sample.ui.theme.SampleTheme

@Composable
fun GroupedListSampleScreen(modifier: Modifier = Modifier) {
    val contacts = remember {
        mutableStateListOf(
            Contact("aaron", "Aaron"),
            Contact("andrea", "Andrea"),
            Contact("ben", "Ben"),
            Contact("bianca", "Bianca"),
            Contact("charlie", "Charlie"),
        )
    }
    val removingIds = remember { mutableStateListOf<String>() }

    SampleScreen(
        modifier = modifier,
        title = "GroupedSwipeableList",
        subtitle = "Grouped rows with animated removal. Headers disappear when their last visible item is removed.",
    ) {
        GroupedSwipeableList(
            groupsMap = contacts.groupBy { it.name.first() },
            key = { it.id },
            groupHeader = { header ->
                Text(
                    modifier = Modifier.padding(vertical = 4.dp),
                    text = header.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                )
            },
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
                    subtitle = "Grouped under ${contact.name.first()}",
                )
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
private fun PreviewGroupedListSampleScreen() {
    SampleTheme {
        GroupedListSampleScreen()
    }
}
