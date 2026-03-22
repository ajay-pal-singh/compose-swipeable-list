<img width="1080" height="2400" alt="SwipagleList_Android_JetpackCompose_Scrollto" src="https://github.com/user-attachments/assets/8d0add6f-38e5-4890-8c45-7e216d4f90a7" />
<img width="1080" height="2400" alt="SwipableList_Android_JetpackCompose_Group" src="https://github.com/user-attachments/assets/42b24964-bd5f-4fbc-b240-d890a7f06ef0" />
<img width="1080" height="2400" alt="SwipableList_Android_JetpackCompose" src="https://github.com/user-attachments/assets/4f9078be-ac92-481b-82c7-4c0c05ccfeb3" />

# Compose Swipeable List

Jetpack Compose components for swipeable rows and grouped or ungrouped swipeable lists.

## Modules

- `swipeablelist`: reusable Android library
- `sample`: demo app

## Installation

This project is configured for JitPack publishing.

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }
}
```

```kotlin
dependencies {
    implementation("com.github.ajaypal.compose-swipeable-list:swipeablelist:1.0.0")
}
```

## Samples

Flat list usage:

```kotlin
SwipeableList(
    items = contacts,
    key = { it.id },
    itemSpacing = 8.dp,
    mainContent = { contact ->
        ContactRow(contact)
    },
    leftContent = { _, closeRow ->
        ArchiveAction(onClick = closeRow)
    },
    rightContent = { contact, closeRow ->
        DeleteAction {
            closeRow()
            removeContact(contact.id)
        }
    },
)
```

Grouped list usage:

```kotlin
GroupedSwipeableList(
    groupsMap = contacts.groupBy { it.lastName.first() },
    key = { it.id },
    groupHeader = { initial ->
        Text(text = initial.toString())
    },
    itemSpacing = 8.dp,
    mainContent = { contact ->
        ContactRow(contact)
    },
    rightContent = { contact, closeRow ->
        DeleteAction {
            closeRow()
            removeContact(contact.id)
        }
    },
)
```

`groupHeader` is fully custom. The group key type is generic, so headers can render initials, section titles, dates, counts, badges, or any richer design your app needs.

Example of a richer custom header:

```kotlin
GroupedSwipeableList(
    groupsMap = contacts.groupBy { it.lastName.first().toString() },
    key = { it.id },
    groupHeader = { header ->
        Row {
            Text(text = header)
            Text(text = "${contactsByHeader.getValue(header).size} contacts")
        }
    },
    mainContent = { contact ->
        ContactRow(contact)
    },
)
```

Scroll-to-key usage:

```kotlin
var targetKey by remember { mutableStateOf<String?>(null) }
val groupedPlaces = remember(places) { places.sortedBy { it.label }.groupBy { it.label.first() }.toSortedMap() }

Button(onClick = { targetKey = "perth" }) {
    Text("Scroll To Perth")
}

GroupedSwipeableList(
    groupsMap = groupedPlaces,
    key = { it.id },
    scrollToKey = targetKey,
    onScrollComplete = { targetKey = null },
    groupHeader = { header ->
        Text(text = header.toString())
    },
    mainContent = { place ->
        PlaceRow(place)
    },
)
```

`scrollToKey` targets item keys only. In grouped mode, header rows are inserted into the lazy list and counted internally, but they do not interfere with item scrolling.

Animated removal usage:

```kotlin
val removingKeys = remember { mutableStateListOf<String>() }

SwipeableList(
    items = contacts,
    key = { it.id },
    removingKeys = removingKeys.toSet(),
    onRemoveAnimationFinished = { contact ->
        contacts.remove(contact)
        removingKeys.remove(contact.id)
    },
    mainContent = { contact ->
        ContactRow(contact)
    },
    rightContent = { contact, closeRow ->
        DeleteAction {
            closeRow()
            if (contact.id !in removingKeys) {
                removingKeys.add(contact.id)
            }
        }
    },
)
```

## API

### `SwipeableList`

- `items`: the row data source
- `key`: stable key for each item
- `scrollToKey`: optional item key to scroll into view
- `itemSpacing`: vertical spacing applied between rows
- `contentPadding`: `LazyColumn` content padding
- `onScrollComplete`: callback after programmatic scroll finishes
- `swipeThreshold`: distance required to settle open
- `animationDurationMillis`: open/close animation duration
- `removingKeys`: item keys currently animating out of the list
- `removeAnimationDurationMillis`: exit animation duration for removing rows
- `onRemoveAnimationFinished`: callback to remove an item from backing data after the exit animation
- `mainContent`: main row content
- `leftContent`: left-side action content
- `rightContent`: right-side action content

`scrollToKey` must match the value returned by `key`.

### `GroupedSwipeableList`

- `groupsMap`: grouped row data source
- `key`: stable key for each item
- `groupHeader`: composable content for each group header
- `scrollToKey`: optional item key to scroll into view
- `itemSpacing`: vertical spacing applied between rows
- `contentPadding`: `LazyColumn` content padding
- `onScrollComplete`: callback after programmatic scroll finishes
- `swipeThreshold`: distance required to settle open
- `animationDurationMillis`: open/close animation duration
- `removingKeys`: item keys currently animating out of the list
- `removeAnimationDurationMillis`: exit animation duration for removing rows
- `onRemoveAnimationFinished`: callback to remove an item from backing data after the exit animation
- `mainContent`: main row content
- `leftContent`: left-side action content
- `rightContent`: right-side action content

### `SwipeableRow`

Use `SwipeableRow` directly when you only need row behavior outside the list helper.

- `isVisible`: controls row visibility
- `isClosed`: forces the row shut when `true`
- `onSwiped`: called when the row settles open
- `swipeThreshold`: distance required to settle open
- `animationDurationMillis`: open/close animation duration
- `mainContent`: primary content
- `leftContent`: left-side action content
- `rightContent`: right-side action content

## Local Development

Build the library:

```bash
./gradlew :swipeablelist:assembleRelease
```

Run tests:

```bash
./gradlew :swipeablelist:testDebugUnitTest
```

Run the sample:

```bash
./gradlew :sample:assembleDebug
```

Publish to Maven local:

```bash
./gradlew :swipeablelist:publishReleasePublicationToMavenLocal
```

## Release Flow

```bash
git init
git add .
git commit -m "Initial release"
git remote add origin git@github.com:ajaypal/compose-swipeable-list.git
git push -u origin main
git tag 1.0.0
git push origin 1.0.0
```

Then verify the tag build on:

```text
https://jitpack.io/#ajaypal/compose-swipeable-list/1.0.0
```

## Current Public API

- `SwipeableList`
- `GroupedSwipeableList`
- `SwipeableRow`
