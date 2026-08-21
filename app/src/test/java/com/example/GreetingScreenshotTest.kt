package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.PlayerInfo
import com.example.ui.components.HeroBannerCard
import com.example.ui.components.PlayerProfileHeader
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun player_card_screenshot() {
        val samplePlayer = PlayerInfo(
            uid = "2720231804",
            name = "亗 Ᏼᴏꜱꜱ 亗",
            server = "BD",
            level = 76,
            likes = 18450,
            exp = 284000,
            brRankPoints = 3450,
            csRankPoints = 2800,
            guildName = "TEAM ESPORTS BD",
            guildLevel = 4
        )

        composeTestRule.setContent {
            MyApplicationTheme {
                PlayerProfileHeader(
                    player = samplePlayer,
                    isFavorite = false,
                    onToggleFavorite = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/player_card.png")
    }
}
