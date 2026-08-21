package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.FlamePrimary
import com.example.ui.theme.FlamePrimaryLight
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.TierBronze
import com.example.ui.theme.TierDiamond
import com.example.ui.theme.TierGold
import com.example.ui.theme.TierGrandmaster
import com.example.ui.theme.TierHeroic
import com.example.ui.theme.TierMaster
import com.example.ui.theme.TierPlatinum
import com.example.ui.theme.TierSilver

@Composable
fun RanksTabContent(
    player: PlayerInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Battle Royale Rank Card
        RankModeCard(
            modeTitle = "BATTLE ROYALE (BR)",
            points = player.brRankPoints,
            tierName = player.brRankTier,
            maxRank = player.brMaxRank,
            accentColor = FlamePrimary,
            icon = Icons.Default.EmojiEvents
        )

        // Clash Squad Rank Card
        RankModeCard(
            modeTitle = "CLASH SQUAD (CS)",
            points = player.csRankPoints,
            tierName = player.csRankTier,
            maxRank = player.csMaxRank,
            accentColor = CyanAccent,
            icon = Icons.Default.MilitaryTech
        )

        // Rank Tier Guide Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "RANK SYSTEM REFERENCE",
                    color = GoldAccent,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                val tiers = listOf(
                    Triple("Grandmaster", "6000+ pts", TierGrandmaster),
                    Triple("Master", "4350+ pts", TierMaster),
                    Triple("Heroic", "3200+ pts", TierHeroic),
                    Triple("Diamond I-IV", "2200-3199", TierDiamond),
                    Triple("Platinum I-IV", "1600-2199", TierPlatinum),
                    Triple("Gold I-IV", "1200-1599", TierGold),
                    Triple("Silver I-III", "1000-1199", TierSilver),
                    Triple("Bronze", "0-999 pts", TierBronze)
                )

                tiers.chunked(2).forEach { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        pair.forEach { (name, pts, color) ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .border(0.5.dp, DarkOutline, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(name, color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(pts, color = TextMuted, fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun RankModeCard(
    modeTitle: String,
    points: Int,
    tierName: String,
    maxRank: Int,
    accentColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = accentColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = modeTitle,
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$points PTS",
                        color = accentColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tier info banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("CURRENT TIER", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = tierName,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (maxRank > 0) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text("MAX RANK", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Peak: $maxRank",
                            color = GoldAccent,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
