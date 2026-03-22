package io.github.ajaypal.swipeablelist

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Displays a flat list of swipeable rows.
 *
 * @param modifier modifier applied to the underlying `LazyColumn`.
 * @param items items rendered in display order.
 * @param key stable item key used for row state and programmatic scrolling.
 * @param itemSpacing vertical spacing applied below each row.
 * @param contentPadding padding applied to the `LazyColumn` content.
 * @param scrollToKey optional key to scroll into view. This must match the value returned by [key].
 * @param onScrollComplete called after a programmatic scroll completes.
 * @param swipeThreshold drag distance required for a row to settle open.
 * @param animationDurationMillis duration used for row open and close animations.
 * @param removingKeys item keys currently animating out of the list.
 * @param removeAnimationDurationMillis duration used for row removal animation.
 * @param onRemoveAnimationFinished called after a removing row finishes its exit animation. Remove the item
 * from your backing data here to preserve the exit animation.
 * @param mainContent primary row content.
 * @param leftContent content revealed when swiping right.
 * @param rightContent content revealed when swiping left.
 */
@Composable
fun <T : Any> SwipeableList(
    modifier: Modifier = Modifier,
    items: List<T>,
    key: (T) -> Any,
    itemSpacing: Dp = 10.dp,
    contentPadding: PaddingValues = PaddingValues(bottom = 10.dp),
    scrollToKey: Any? = null,
    onScrollComplete: () -> Unit = {},
    swipeThreshold: Dp = 100.dp,
    animationDurationMillis: Int = 200,
    removingKeys: Set<Any> = emptySet(),
    removeAnimationDurationMillis: Int = 250,
    onRemoveAnimationFinished: (T) -> Unit = {},
    mainContent: @Composable BoxScope.(item: T) -> Unit,
    leftContent: @Composable (item: T, closeRow: () -> Unit) -> Unit = { _, _ -> },
    rightContent: @Composable (item: T, closeRow: () -> Unit) -> Unit = { _, _ -> },
) {
    SwipeableListInternal<T, Any>(
        modifier = modifier,
        flatItems = items,
        flatKey = key,
        itemSpacing = itemSpacing,
        contentPadding = contentPadding,
        groupsMap = null,
        groupHeader = null,
        scrollToKey = scrollToKey,
        onScrollComplete = onScrollComplete,
        swipeThreshold = swipeThreshold,
        animationDurationMillis = animationDurationMillis,
        removingKeys = removingKeys,
        removeAnimationDurationMillis = removeAnimationDurationMillis,
        onRemoveAnimationFinished = onRemoveAnimationFinished,
        mainContent = mainContent,
        leftContent = leftContent,
        rightContent = rightContent,
    )
}

/**
 * Displays a grouped list of swipeable rows with sticky-style headers rendered inline in the list.
 *
 * Group headers count toward the internal lazy list indexes, but [scrollToKey] still targets items only.
 *
 * @param modifier modifier applied to the underlying `LazyColumn`.
 * @param groupsMap grouped items in display order. Use an ordered map when group order matters.
 * @param key stable item key used for row state and programmatic scrolling.
 * @param groupHeader composable used to render each group header.
 * @param itemSpacing vertical spacing applied below each row.
 * @param contentPadding padding applied to the `LazyColumn` content.
 * @param scrollToKey optional key to scroll into view. This must match the value returned by [key].
 * @param onScrollComplete called after a programmatic scroll completes.
 * @param swipeThreshold drag distance required for a row to settle open.
 * @param animationDurationMillis duration used for row open and close animations.
 * @param removingKeys item keys currently animating out of the list.
 * @param removeAnimationDurationMillis duration used for row removal animation.
 * @param onRemoveAnimationFinished called after a removing row finishes its exit animation. Remove the item
 * from your backing data here to preserve the exit animation.
 * @param mainContent primary row content.
 * @param leftContent content revealed when swiping right.
 * @param rightContent content revealed when swiping left.
 */
@Composable
fun <T : Any, K : Any> GroupedSwipeableList(
    modifier: Modifier = Modifier,
    groupsMap: Map<K, List<T>>,
    key: (T) -> Any,
    groupHeader: @Composable (K) -> Unit,
    itemSpacing: Dp = 10.dp,
    contentPadding: PaddingValues = PaddingValues(bottom = 10.dp),
    scrollToKey: Any? = null,
    onScrollComplete: () -> Unit = {},
    swipeThreshold: Dp = 100.dp,
    animationDurationMillis: Int = 200,
    removingKeys: Set<Any> = emptySet(),
    removeAnimationDurationMillis: Int = 250,
    onRemoveAnimationFinished: (T) -> Unit = {},
    mainContent: @Composable BoxScope.(item: T) -> Unit,
    leftContent: @Composable (item: T, closeRow: () -> Unit) -> Unit = { _, _ -> },
    rightContent: @Composable (item: T, closeRow: () -> Unit) -> Unit = { _, _ -> },
) {
    SwipeableListInternal(
        modifier = modifier,
        flatItems = null,
        flatKey = key,
        itemSpacing = itemSpacing,
        contentPadding = contentPadding,
        groupsMap = groupsMap,
        groupHeader = groupHeader,
        scrollToKey = scrollToKey,
        onScrollComplete = onScrollComplete,
        swipeThreshold = swipeThreshold,
        animationDurationMillis = animationDurationMillis,
        removingKeys = removingKeys,
        removeAnimationDurationMillis = removeAnimationDurationMillis,
        onRemoveAnimationFinished = onRemoveAnimationFinished,
        mainContent = mainContent,
        leftContent = leftContent,
        rightContent = rightContent,
    )
}

@Composable
private fun <T : Any, K : Any> SwipeableListInternal(
    modifier: Modifier,
    flatItems: List<T>?,
    flatKey: (T) -> Any,
    itemSpacing: Dp,
    contentPadding: PaddingValues,
    groupsMap: Map<K, List<T>>?,
    groupHeader: (@Composable (K) -> Unit)?,
    scrollToKey: Any?,
    onScrollComplete: () -> Unit,
    swipeThreshold: Dp,
    animationDurationMillis: Int,
    removingKeys: Set<Any>,
    removeAnimationDurationMillis: Int,
    onRemoveAnimationFinished: (T) -> Unit,
    mainContent: @Composable BoxScope.(item: T) -> Unit,
    leftContent: @Composable (item: T, closeRow: () -> Unit) -> Unit,
    rightContent: @Composable (item: T, closeRow: () -> Unit) -> Unit,
) {
    var openRowId by remember { mutableStateOf<Any?>(null) }
    val listState = rememberLazyListState()
    val visibleFlatItems = remember(flatItems, removingKeys, flatKey) {
        buildVisibleFlatItems(
            flatItems = flatItems,
            removingKeys = removingKeys,
            flatKey = flatKey,
        )
    }
    val visibleGroupsMap = remember(groupsMap, removingKeys, flatKey) {
        buildVisibleGroupsMap(
            groupsMap = groupsMap,
            removingKeys = removingKeys,
            flatKey = flatKey,
        )
    }

    val scrollIndexByKey = remember(visibleFlatItems, visibleGroupsMap, flatKey) {
        buildScrollIndexByItemKey(
            flatItems = visibleFlatItems,
            groupsMap = visibleGroupsMap,
            flatKey = flatKey,
        )
    }

    LaunchedEffect(scrollToKey, scrollIndexByKey) {
        scrollToKey
            ?.let(scrollIndexByKey::get)
            ?.also { index ->
                listState.animateScrollToItem(index)
                onScrollComplete()
            }
    }

    @Composable
    fun SwipeableListItem(item: T) {
        val id = flatKey(item)
        val isRemoving = id in removingKeys

        LaunchedEffect(id, isRemoving) {
            if (isRemoving) {
                delay(removeAnimationDurationMillis.toLong())
                onRemoveAnimationFinished(item)
            }
        }

        SwipeableRow(
            modifier = Modifier.padding(bottom = itemSpacing),
            isVisible = !isRemoving,
            isClosed = isRemoving || openRowId != null && openRowId != id,
            onSwiped = { if (!isRemoving) openRowId = id },
            swipeThreshold = swipeThreshold,
            animationDurationMillis = if (isRemoving) removeAnimationDurationMillis else animationDurationMillis,
            mainContent = { mainContent(item) },
            leftContent = { closeRow -> leftContent(item, closeRow) },
            rightContent = { closeRow -> rightContent(item, closeRow) },
        )
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = contentPadding,
    ) {
        if (visibleGroupsMap != null && groupHeader != null) {
            groupsMap?.forEach { (header, items) ->
                if (header in visibleGroupsMap) {
                    item(key = headerLazyKey(header)) { groupHeader(header) }
                }
                items(items, key = { itemLazyKey(flatKey(it)) }) { SwipeableListItem(it) }
            }
        } else if (visibleFlatItems != null) {
            items(flatItems.orEmpty(), key = { itemLazyKey(flatKey(it)) }) { SwipeableListItem(it) }
        }
    }
}

internal fun <T : Any> buildVisibleFlatItems(
    flatItems: List<T>?,
    removingKeys: Set<Any>,
    flatKey: (T) -> Any,
): List<T>? = flatItems?.filterNot { flatKey(it) in removingKeys }

internal fun <T : Any, K : Any> buildVisibleGroupsMap(
    groupsMap: Map<K, List<T>>?,
    removingKeys: Set<Any>,
    flatKey: (T) -> Any,
): Map<K, List<T>>? {
    if (groupsMap == null) return null

    val result = LinkedHashMap<K, List<T>>()
    groupsMap.forEach { (header, items) ->
        val visibleItems = items.filterNot { flatKey(it) in removingKeys }
        if (visibleItems.isNotEmpty()) {
            result[header] = visibleItems
        }
    }
    return result
}

internal fun <T : Any, K : Any> buildScrollIndexByItemKey(
    flatItems: List<T>?,
    groupsMap: Map<K, List<T>>?,
    flatKey: (T) -> Any,
): Map<Any, Int> {
    val result = LinkedHashMap<Any, Int>()
    var currentIndex = 0

    if (groupsMap != null) {
        groupsMap.values.forEach { groupItems ->
            currentIndex += 1
            groupItems.forEach { item ->
                result[flatKey(item)] = currentIndex
                currentIndex += 1
            }
        }
    } else {
        flatItems.orEmpty().forEachIndexed { index, item ->
            result[flatKey(item)] = index
        }
    }

    return result
}

internal fun headerLazyKey(header: Any): Pair<String, Any> = "header" to header

internal fun itemLazyKey(itemKey: Any): Pair<String, Any> = "item" to itemKey
