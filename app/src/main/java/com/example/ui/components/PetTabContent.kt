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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CatchingPokemon
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun PetTabContent(
    player: PlayerInfo,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (player.hasPet && player.petName.isNotBlank()) {
            // Pet Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, CyanAccent.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyanAccent.copy(alpha = 0.15f))
                                .border(1.dp, CyanAccent, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Pets,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = player.petName,
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Pet Level ${player.petLevel}",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (player.petSkill.isNotBlank()) {
                        InfoRow(
                            icon = Icons.Default.AutoAwesome,
                            label = "Equipped Skill ID",
                            value = player.petSkill
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (player.petExp > 0) {
                        InfoRow(
                            icon = Icons.Default.Star,
                            label = "Pet EXP",
                            value = "${player.petExp} EXP"
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (player.petType.isNotBlank()) {
                        InfoRow(
                            icon = Icons.Default.CatchingPokemon,
                            label = "Pet Breed / Model",
                            value = player.petType
                        )
                    }
                }
            }
        } else {
            // No Pet card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Pets,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "NO PET EQUIPPED",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Player has not equipped any pet companion.",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }
        }

        // Social Info
        if (player.gender.isNotBlank() || player.language.isNotBlank()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = DarkSurfaceCard,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "SOCIAL PREFERENCES",
                        color = FlamePrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (player.gender.isNotBlank()) {
                        InfoRow(
                            icon = if (player.gender.contains("fem", ignoreCase = true)) Icons.Default.Female else Icons.Default.Male,
                            label = "Gender",
                            value = player.gender
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    if (player.language.isNotBlank()) {
                        InfoRow(
                            icon = Icons.Default.Psychology,
                            label = "In-game Language",
                            value = player.language
                        )
                    }
                }
            }
        }
    }
}
