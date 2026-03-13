# FlexiTabs

`FlexiTabs` is an Android UI library for dynamic slider tabs that works in both the View system and Jetpack Compose.

It is designed for segmented controls such as:

- `Video | Image | Templates`
- `Home | Explore | Trending | Profile`

## Release

Current version: `v1.0.0`

Release description:

> Initial production-ready release of FlexiTabs with dynamic 3-10 tab support, animated indicator transitions, XML and Compose APIs, accessibility support, sample app coverage, and JitPack distribution.

## Features

- Dynamic tab count from 3 to 10
- Fixed and scrollable modes
- Animated sliding indicator
- Text-only, icon-only, and icon + text tabs
- Native XML/View API
- Native Compose API
- Adaptive text sizing
- Styling via `TabStyle`
- Accessibility support
- Sample app for Compose and XML

## Project Structure

- `:flexitabs` - reusable Android library
- `:sample` - demo app

## Requirements

- `minSdk 23`
- `compileSdk 36`
- Java 11 compatible Android build
- AndroidX enabled

## Installation

### JitPack

Add JitPack to your repositories:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("com.github.ruchit1999:flexitabs:v1.0.0")
}
```

### Local Development

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
    style = TabStyle(),
    onTabSelected = { selectedIndex = it }
)
```

Public Compose API:

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
    // handle selection
}
```

Public View API:

```kotlin
fun setTabs(items: List<TabItem>)
fun setSelectedIndex(index: Int)
fun getSelectedIndex(): Int
fun setOnTabSelectedListener(listener: OnTabSelectedListener)
fun setStyle(style: TabStyle)
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
    heightDp = 48f,
    textSizeSp = 14f,
    minTextSizeSp = 10f,
    iconSizeDp = 18f,
    horizontalPaddingDp = 16f,
    indicatorInsetDp = 4f,
    animationDurationMillis = 280,
    displayMode = TabDisplayMode.AUTO
)
```

## Architecture

- `model` - public models such as `TabItem`, `TabState`, and `TabStyle`
- `layout` - width calculation and positioning
- `render` - background, indicator, and content rendering
- `touch` - hit-testing
- `animation` - indicator motion controller
- `view` - `FlexiTabsView` and accessibility handling
- `compose` - Compose implementation
- `listener` - public callbacks

## Accessibility

- TalkBack-friendly virtual child tab nodes
- Selected tab announcement support
- Per-tab content descriptions
- Disabled tab support

## Sample App

The `:sample` module includes:

- 3-tab Compose example
- 4-tab Compose example
- scrollable Compose example
- dedicated XML showcase activity

## JitPack Notes

This project includes [`jitpack.yml`](/home/infizer/Documents/ruchit/FlexiTabs/jitpack.yml) so JitPack builds the library module and publishes it from tagged releases.

Coordinates:

```text
Repository: com.github.ruchit1999:flexitabs
Version tag: v1.0.0
```

JitPack resolves artifacts by Git tag. That means the consumer dependency must use the tag string exactly:

```kotlin
implementation("com.github.ruchit1999:flexitabs:v1.0.0")
```

Internally, the library build is configured to use JitPack's `VERSION` environment variable during JitPack builds, so the published artifact version matches the requested tag.

Release flow:

1. Push the repository to GitHub.
2. Create and push tag `v1.0.0`.
3. Wait for JitPack to build the tag.
4. Consume the library with `com.github.ruchit1999:flexitabs:v1.0.0`.

## Verification

Verified locally with:

```bash
./gradlew :flexitabs:testDebugUnitTest :sample:compileDebugKotlin
```
