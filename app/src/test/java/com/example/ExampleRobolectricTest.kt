package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.PlayerInfo
import com.example.data.model.ServerRegion
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Free Fire Checker", appName)
    }

    @Test
    fun `test server regions list contains expected servers`() {
        val popular = ServerRegion.ALL.filter { it.isPopular }
        val codes = popular.map { it.code }
        assertEquals(true, codes.contains("BD"))
        assertEquals(true, codes.contains("IND"))
        assertEquals(true, codes.contains("SG"))
        assertEquals(true, codes.contains("BR"))
    }

    @Test
    fun `test player rank tier calculation`() {
        val grandmaster = PlayerInfo(uid = "123", brRankPoints = 6500, csRankPoints = 4400)
        assertEquals("Grandmaster", grandmaster.brRankTier)
        assertEquals("Master", grandmaster.csRankTier)

        val heroic = PlayerInfo(uid = "456", brRankPoints = 3300, csRankPoints = 2500)
        assertEquals("Heroic", heroic.brRankTier)
        assertEquals("Diamond", heroic.csRankTier)
    }
}
