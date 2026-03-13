# FlexiTabs

`FlexiTabs` is a dynamic slider tab Android UI SDK for apps that need a segmented control style component in both the classic View system and Jetpack Compose.

It supports:

- 3 to 10 dynamic tabs
- fixed and scrollable layouts
- animated sliding indicator
- text, icon, or icon + text tabs
- XML and Compose APIs
- shared architecture across rendering, layout, touch, and animation

## Release

Current release line: `v1.0.0`

Release description:

> FlexiTabs v1.0.0 is the first production-ready release of the library, shipping dynamic slider tabs for Android Views and Jetpack Compose with animation, adaptive sizing, accessibility support, and GitHub Packages publishing support.

## Modules

- `:flexitabs` - reusable SDK/library module
- `:sample` - demo app with Compose and XML showcases

## Installation

### GitHub Packages

Add the GitHub Packages repository:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.pkg.github.com/<github-username>/flexitabs")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

Then add the dependency:

```kotlin
dependencies {
    implementation("io.github.<github-username>:flexitabs:1.0.0")
}
```

### Local Module

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

The library module includes a GitHub Packages publishing configuration and a GitHub Actions workflow.

Artifacts:

```text
Group: io.github.<github-username>
Artifact: flexitabs
Version: 1.0.0
Git tag: v1.0.0
```

### Release flow

1. Update the placeholders in `gradle.properties` with your real GitHub username, repo, and developer info.
2. Commit the project to GitHub.
3. Create and push a tag such as `v1.0.0`.
4. GitHub Actions publishes the `release` publication to GitHub Packages.

### Manual publish

```bash
export GITHUB_ACTOR=<github-username>
export GITHUB_TOKEN=<github-token>
./gradlew :flexitabs:publishReleasePublicationToGitHubPackagesRepository
```

The publishing metadata is centralized in [`gradle.properties`](/home/infizer/Documents/ruchit/FlexiTabs/gradle.properties) and the repository/publication setup lives in [`flexitabs/build.gradle.kts`](/home/infizer/Documents/ruchit/FlexiTabs/flexitabs/build.gradle.kts).

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
