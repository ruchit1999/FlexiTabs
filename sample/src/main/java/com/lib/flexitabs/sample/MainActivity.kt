package com.lib.flexitabs.sample

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.flexitabs.compose.FlexiTabs
import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    primary = Color(0xFF0F172A),
                    secondary = Color(0xFF1D4ED8)
                )
            ) {
                SampleScreen()
            }
        }
    }
}

@Composable
private fun SampleScreen() {
    val context = LocalContext.current
    Scaffold { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = stringResource(R.string.sample_title), style = MaterialTheme.typography.headlineMedium)
                    Text(text = stringResource(R.string.sample_subtitle), style = MaterialTheme.typography.bodyLarge)
                    Button(onClick = {
                        context.startActivity(Intent(context, XmlSampleActivity::class.java))
                    }) {
                        Text(text = stringResource(R.string.open_xml_sample))
                    }
                }
            }
            items(sampleSections()) { section ->
                ShowcaseCard(section = section)
            }
        }
    }
}

@Composable
private fun ShowcaseCard(section: SampleSection) {
    var selectedIndex by remember(section.title) { mutableIntStateOf(section.initialIndex) }
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = section.title, style = MaterialTheme.typography.titleLarge)
            Text(text = section.description, style = MaterialTheme.typography.bodyMedium)
            FlexiTabs(
                tabs = section.tabs,
                selectedIndex = selectedIndex,
                style = section.style,
                modifier = Modifier.fillMaxWidth(),
                onTabSelected = { selectedIndex = it }
            )
            Text(
                text = "Selected: ${section.tabs[selectedIndex].title}",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

private data class SampleSection(
    val title: String,
    val description: String,
    val tabs: List<TabItem>,
    val initialIndex: Int,
    val style: TabStyle
)

private fun sampleSections(): List<SampleSection> {
    return listOf(
        SampleSection(
            title = "3 Tabs",
            description = "Fixed segmented control with icon + text.",
            tabs = listOf(
                TabItem("video", "Video", android.R.drawable.ic_media_play),
                TabItem("image", "Image", android.R.drawable.ic_menu_gallery),
                TabItem("template", "Templates", android.R.drawable.ic_menu_agenda)
            ),
            initialIndex = 0,
            style = TabStyle()
        ),
        SampleSection(
            title = "4 Tabs",
            description = "Balanced fixed layout for common navigation sets.",
            tabs = listOf(
                TabItem("home", "Home"),
                TabItem("explore", "Explore"),
                TabItem("trend", "Trending"),
                TabItem("profile", "Profile")
            ),
            initialIndex = 1,
            style = TabStyle(
                indicatorColor = 0xFFE0F2FE.toInt(),
                backgroundColor = 0xFF0F172A.toInt(),
                selectedTextColor = 0xFF0F172A.toInt(),
                unselectedTextColor = 0xFFE2E8F0.toInt(),
                iconTintColor = 0xFFE2E8F0.toInt()
            )
        ),
        SampleSection(
            title = "Scrollable",
            description = "Auto switches to scrollable layout when the tab count grows.",
            tabs = listOf(
                TabItem("home", "Home"),
                TabItem("explore", "Explore"),
                TabItem("trend", "Trending"),
                TabItem("insights", "Insights"),
                TabItem("templates", "Templates"),
                TabItem("profile", "Profile"),
                TabItem("saved", "Saved"),
                TabItem("settings", "Settings")
            ),
            initialIndex = 3,
            style = TabStyle(displayMode = TabDisplayMode.AUTO)
        )
    )
}
