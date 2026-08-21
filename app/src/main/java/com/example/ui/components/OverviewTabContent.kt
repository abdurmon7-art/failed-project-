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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PlayerInfo
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.FlamePrimary
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun OverviewTabContent(
    player: PlayerInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Player Bio / Signature Card
        if (player.signature.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = null,
                            tint = FlamePrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "PLAYER SIGNATURE / BIO",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "\"${player.signature}\"",
                        color = TextWhite,
                        fontSize = 14.sp,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // Account Activity & Timestamps Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = DarkSurfaceCard,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "ACCOUNT INTEL",
                    color = FlamePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow(
                    icon = Icons.Default.CalendarMonth,
                    label = "Account Created",
                    value = player.formattedCreateDate
                )
                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.AccessTime,
                    label = "Last Active",
                    value = player.formattedLastLogin
                )
                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.VerifiedUser,
                    label = "Fair Play Credit Score",
                    value = "${player.creditScore} / 100"
                )
                Spacer(modifier = Modifier.height(10.dp))

                InfoRow(
                    icon = Icons.Default.MilitaryTech,
                    label = "Season ID",
                    value = if (player.seasonId > 0) "Season ${player.seasonId}" else "Active Season"
                )
            }
        }

        // Additional Profile details if present
        if (player.title.isNotBlank() || player.releaseVersion.isNotBlank() || player.language.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EQUIPMENT & EXTRAS",
                        color = CyanAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (player.title.isNotBlank()) {
                        InfoRow(icon = Icons.Default.MilitaryTech, label = "Equipped Title", value = player.title)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (player.language.isNotBlank()) {
                        InfoRow(icon = Icons.Default.Info, label = "Language", value = player.language)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    if (player.releaseVersion.isNotBlank()) {
                        InfoRow(icon = Icons.Default.Shield, label = "Game Client Version", value = player.releaseVersion)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = TextMuted,
                fontSize = 13.sp
            )
        }
        Text(
            text = value,
            color = TextWhite,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
