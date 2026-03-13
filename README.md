# FlexiTabs

`FlexiTabs` is an Android UI library for dynamic slider tabs that works in both the classic View system and Jetpack Compose.

It is built for segmented controls such as:

- `Video | Image | Templates`
- `Home | Explore | Trending | Profile`

## Release

Current version: `v1.0.0`

Release description:

> Initial production-ready release of FlexiTabs with dynamic 3-10 tab support, animated indicator transitions, XML + Compose APIs, accessibility support, sample app coverage, and GitHub Packages publishing.

## Features

- Dynamic tab count from 3 to 10
- Fixed and scrollable display modes
- Animated sliding indicator
- Text-only, icon-only, and icon + text tabs
- Native custom View API for XML-based projects
- Native Compose API for Compose-based projects
- Adaptive text sizing with minimum text size support
- Styling via `TabStyle`
- Accessibility support with TalkBack-friendly tab focus
- Sample app with Compose and XML showcases

## Project Structure

- `:flexitabs` - reusable Android library
- `:sample` - demo app for Compose and XML integration

## Requirements

- `minSdk 23`
- `compileSdk 36`
- Java 11 compatible Android build
- AndroidX enabled

## Installation

### GitHub Packages

Add the GitHub Packages repository:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/ruchit1999/flexitabs")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

Add the dependency:

```kotlin
dependencies {
    implementation("io.github.ruchit1999:flexitabs:1.0.0")
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

The component is configured through `TabStyle`.

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

The library is intentionally split into focused packages:

- `model` - immutable public models such as `TabItem`, `TabState`, and `TabStyle`
- `layout` - tab measurement and width calculation
- `render` - background, indicator, and tab content rendering
- `touch` - hit-testing and tab selection resolution
- `animation` - indicator animation controller
- `view` - `FlexiTabsView` and accessibility handling
- `compose` - Compose implementation
- `listener` - public callback contract

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

## Publishing

Publishing metadata is centralized in [`gradle.properties`](/home/infizer/Documents/ruchit/FlexiTabs/gradle.properties).

Configured artifact:

```text
Group: io.github.ruchit1999
Artifact: flexitabs
Version: 1.0.0
Tag: v1.0.0
Repository: GitHub Packages
```

The library publication is defined in [`flexitabs/build.gradle.kts`](/home/infizer/Documents/ruchit/FlexiTabs/flexitabs/build.gradle.kts).

### GitHub Release Flow

1. Push the repository to `github.com/ruchit1999/flexitabs`.
2. Ensure `GITHUB_TOKEN` or package publish permissions are available in GitHub Actions.
3. Create and push tag `v1.0.0`.
4. The workflow in [`.github/workflows/publish.yml`](/home/infizer/Documents/ruchit/FlexiTabs/.github/workflows/publish.yml) runs verification and publishes the `release` artifact to GitHub Packages.

### Manual Publish

```bash
export GITHUB_ACTOR=ruchit1999
export GITHUB_TOKEN=<github-token>
./gradlew :flexitabs:publishReleasePublicationToGitHubPackagesRepository
```

## Verification

Verified locally with:

```bash
./gradlew :flexitabs:testDebugUnitTest :sample:compileDebugKotlin
```
