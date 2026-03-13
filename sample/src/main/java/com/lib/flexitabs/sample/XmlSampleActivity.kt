package com.lib.flexitabs.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.flexitabs.listener.OnTabSelectedListener
import com.flexitabs.model.TabDisplayMode
import com.flexitabs.model.TabItem
import com.flexitabs.model.TabStyle
import com.lib.flexitabs.sample.databinding.ActivityXmlSampleBinding

class XmlSampleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityXmlSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityXmlSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.xml_sample_title)

        binding.fixedTabs.setTabs(
            listOf(
                TabItem("video", "Video", android.R.drawable.ic_media_play),
                TabItem("image", "Image", android.R.drawable.ic_menu_gallery),
                TabItem("template", "Templates", android.R.drawable.ic_menu_agenda)
            )
        )
        binding.fixedTabs.setOnTabSelectedListener(OnTabSelectedListener {
            binding.fixedSelection.text = getString(R.string.selected_label, binding.fixedTabs.getSelectedIndex())
        })
        binding.fixedTabs.setSelectedIndex(0)

        binding.fourTabs.setTabs(
            listOf(
                TabItem("home", "Home"),
                TabItem("explore", "Explore"),
                TabItem("trending", "Trending"),
                TabItem("profile", "Profile")
            )
        )
        binding.fourTabs.setStyle(
            TabStyle(
                backgroundColor = 0xFF0F172A.toInt(),
                indicatorColor = 0xFFE0F2FE.toInt(),
                selectedTextColor = 0xFF0F172A.toInt(),
                unselectedTextColor = 0xFFE2E8F0.toInt(),
                iconTintColor = 0xFFE2E8F0.toInt()
            )
        )
        binding.fourTabs.setSelectedIndex(1)

        binding.scrollableTabs.setTabs(
            listOf(
                TabItem("home", "Home"),
                TabItem("explore", "Explore"),
                TabItem("trending", "Trending"),
                TabItem("insights", "Insights"),
                TabItem("templates", "Templates"),
                TabItem("profile", "Profile"),
                TabItem("saved", "Saved"),
                TabItem("settings", "Settings")
            )
        )
        binding.scrollableTabs.setStyle(TabStyle(displayMode = TabDisplayMode.SCROLLABLE))
        binding.scrollableTabs.setSelectedIndex(4)
    }
}
