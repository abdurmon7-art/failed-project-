package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.FlamePrimary
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class ProfileTab(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun PlayerProfileView(
    player: PlayerInfo,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onSearchAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        ProfileTab("Overview", Icons.Default.Info, "tab_overview"),
        ProfileTab("Ranks", Icons.Default.EmojiEvents, "tab_ranks"),
        ProfileTab("Guild", Icons.Default.Shield, "tab_guild"),
        ProfileTab("Pet", Icons.Default.Pets, "tab_pet"),
        ProfileTab("Raw JSON", Icons.Default.Code, "tab_raw")
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_profile_view")
    ) {
        // Player Header Card
        PlayerProfileHeader(
            player = player,
            isFavorite = isFavorite,
            onToggleFavorite = onToggleFavorite
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Row
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = DarkSurfaceCard,
            contentColor = FlamePrimary,
            edgePadding = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, DarkOutline, RoundedCornerShape(14.dp)),
            indicator = { tabPositions ->
                if (selectedTab < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = FlamePrimary,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = selectedTab == index
                Tab(
                    selected = isSelected,
                    onClick = { onTabSelected(index) },
                    modifier = Modifier.testTag(tab.testTag),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = null,
                                tint = if (isSelected) FlamePrimary else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.title,
                                color = if (isSelected) FlamePrimary else TextMuted,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tab Content with Animated Transition
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "tab_content"
        ) { tabIndex ->
            when (tabIndex) {
                0 -> OverviewTabContent(player = player)
                1 -> RanksTabContent(player = player)
                2 -> GuildTabContent(player = player)
                3 -> PetTabContent(player = player)
                4 -> RawDataTabContent(player = player)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Check another UID action button
        Button(
            onClick = onSearchAgain,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("search_another_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = DarkSurfaceCard,
                contentColor = TextWhite
            ),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = FlamePrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "SEARCH ANOTHER PLAYER UID",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite
            )
        }
    }
}
