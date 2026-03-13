# FlexiTabs

`FlexiTabs` is a dynamic slider tab Android UI SDK for apps that need a segmented control style component in both the classic View system and Jetpack Compose.

It supports:

- 3 to 10 dynamic tabs
- fixed and scrollable layouts
- animated sliding indicator
- text, icon, or icon + text tabs
- XML and Compose APIs
- shared architecture across rendering, layout, touch, and animation

## Modules

- `:flexitabs` - reusable SDK/library module
- `:sample` - demo app with Compose and XML showcases

## Installation

```kotlin
dependencies {
    implementation("io.github.<username>:flexitabs:1.0.0")
}
```

For local development:

```kotlin
dependencies {
    implementation(project(":flexitabs"))
}
```

## Compose Usage

```kotlin
var selectedIndex by remember { mutableIntStateOf(0) }

FlexiTabs(
    tabs = listOf(
        TabItem("video", "Video", android.R.drawable.ic_media_play),
        TabItem("image", "Image", android.R.drawable.ic_menu_gallery),
        TabItem("templates", "Templates", android.R.drawable.ic_menu_agenda)
    ),
    selectedIndex = selectedIndex,
    onTabSelected = { selectedIndex = it }
)
```

## XML Usage

```xml
<com.flexitabs.view.FlexiTabsView
    android:id="@+id/flexiTabs"
    android:layout_width="match_parent"
    android:layout_height="48dp"
    app:ft_displayMode="fixed" />
```

```kotlin
binding.flexiTabs.setTabs(
    listOf(
        TabItem("home", "Home"),
        TabItem("explore", "Explore"),
        TabItem("profile", "Profile")
    )
)
binding.flexiTabs.setSelectedIndex(0)
binding.flexiTabs.setOnTabSelectedListener { index ->
    // react to tab change
}
```

## Styling

```kotlin
val style = TabStyle(
    backgroundColor = 0xFF0F172A.toInt(),
    indicatorColor = 0xFFE0F2FE.toInt(),
    selectedTextColor = 0xFF0F172A.toInt(),
    unselectedTextColor = 0xFFE2E8F0.toInt(),
    iconTintColor = 0xFFE2E8F0.toInt(),
    cornerRadiusDp = 24f,
    textSizeSp = 14f,
    minTextSizeSp = 10f,
    animationDurationMillis = 320,
    displayMode = TabDisplayMode.AUTO
)
```

## API

### View API

```kotlin
fun setTabs(items: List<TabItem>)
fun setSelectedIndex(index: Int)
fun getSelectedIndex(): Int
fun setOnTabSelectedListener(listener: OnTabSelectedListener)
fun setStyle(style: TabStyle)
```

### Compose API

```kotlin
@Composable
fun FlexiTabs(
    tabs: List<TabItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
    style: TabStyle = FlexiTabsDefaults.style(),
    onTabSelected: (Int) -> Unit
)
```

## Architecture

The library is split into focused packages:

- `model` - immutable config and state models
- `layout` - tab sizing and positioning strategy
- `render` - background, indicator, and tab content drawing
- `touch` - hit-testing abstraction
- `animation` - indicator motion controller
- `view` - XML/View implementation and accessibility
- `compose` - native Compose API reusing the same domain/style model
- `listener` - public callbacks

## Accessibility

- TalkBack-friendly virtual tab nodes via `ExploreByTouchHelper`
- selected state announcements
- per-tab content descriptions
- disabled tab handling

## Publishing

The library module already includes `maven-publish` setup.

Artifacts:

```text
Group: io.github.<username>
Artifact: flexitabs
Version: 1.0.0
```

For JitPack, publishing works from the GitHub repository directly once pushed.

For Maven Central, update the coordinates, developer metadata, and signing configuration in [`flexitabs/build.gradle.kts`](/home/infizer/Documents/ruchit/FlexiTabs/flexitabs/build.gradle.kts).

## Demo App

The sample app includes:

- 3-tab Compose example
- 4-tab Compose example
- scrollable Compose example
- dedicated XML showcase activity

## Verification

Verified locally with:

```bash
./gradlew :flexitabs:testDebugUnitTest :sample:compileDebugKotlin
```
