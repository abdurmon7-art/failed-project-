package com.example.data.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PlayerInfo(
    val uid: String,
    val name: String,
    val server: String,
    val level: Int = 1,
    val exp: Long = 0L,
    val likes: Long = 0L,
    val signature: String = "",
    val createTime: Long = 0L,
    val lastLogin: Long = 0L,
    val region: String = "",
    val releaseVersion: String = "",
    val seasonId: Int = 0,
    val badgeCount: Int = 0,
    val avatarId: String = "",
    val bannerId: String = "",
    val title: String = "",
    // Battle Royale Rank
    val brRankPoints: Int = 0,
    val brMaxRank: Int = 0,
    // Clash Squad Rank
    val csRankPoints: Int = 0,
    val csMaxRank: Int = 0,
    // Guild
    val hasGuild: Boolean = false,
    val guildId: String = "",
    val guildName: String = "",
    val guildLevel: Int = 0,
    val guildCapacity: Int = 0,
    val guildMembers: Int = 0,
    val guildOwner: String = "",
    val guildOwnerName: String = "",
    // Pet
    val hasPet: Boolean = false,
    val petName: String = "",
    val petLevel: Int = 0,
    val petExp: Long = 0L,
    val petSkill: String = "",
    val petType: String = "",
    // Social
    val gender: String = "",
    val language: String = "",
    val creditScore: Int = 100,
    // Raw JSON String for inspection
    val rawJson: String = ""
) {
    val formattedCreateDate: String
        get() {
            if (createTime <= 0) return "N/A"
            return try {
                val millis = if (createTime < 10000000000L) createTime * 1000L else createTime
                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(millis))
            } catch (e: Exception) {
                "N/A"
            }
        }

    val formattedLastLogin: String
        get() {
            if (lastLogin <= 0) return "Active recently"
            return try {
                val millis = if (lastLogin < 10000000000L) lastLogin * 1000L else lastLogin
                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(millis))
            } catch (e: Exception) {
                "Active recently"
            }
        }

    val brRankTier: String
        get() = calculateRankTier(brRankPoints)

    val csRankTier: String
        get() = calculateRankTier(csRankPoints)

    fun shareSummary(): String {
        return buildString {
            append("🔥 Free Fire Player Info 🔥\n")
            append("👤 Name: $name\n")
            append("🆔 UID: $uid\n")
            append("🌍 Server: $server\n")
            append("⭐ Level: $level (EXP: $exp)\n")
            append("❤️ Likes: $likes\n")
            append("🏆 BR Points: $brRankPoints ($brRankTier)\n")
            append("⚔️ CS Points: $csRankPoints ($csRankTier)\n")
            if (hasGuild && guildName.isNotBlank()) {
                append("🛡️ Guild: $guildName (Lv.$guildLevel, Members: $guildMembers/$guildCapacity)\n")
            }
            if (hasPet && petName.isNotBlank()) {
                append("🐾 Pet: $petName (Lv.$petLevel)\n")
            }
            if (signature.isNotBlank()) {
                append("📝 Bio: \"$signature\"\n")
            }
            append("\nChecked via Free Fire UID Checker")
        }
    }

    companion object {
        fun calculateRankTier(points: Int): String {
            return when {
                points >= 6000 -> "Grandmaster (6000+)"
                points >= 4350 -> "Master (${points})"
                points >= 3200 -> "Heroic (${points})"
                points >= 2600 -> "Diamond IV"
                points >= 2475 -> "Diamond III"
                points >= 2350 -> "Diamond II"
                points >= 2200 -> "Diamond I"
                points >= 1975 -> "Platinum IV"
                points >= 1850 -> "Platinum III"
                points >= 1725 -> "Platinum II"
                points >= 1600 -> "Platinum I"
                points >= 1500 -> "Gold IV"
                points >= 1400 -> "Gold III"
                points >= 1300 -> "Gold II"
                points >= 1200 -> "Gold I"
                points >= 1100 -> "Silver III"
                points >= 1050 -> "Silver II"
                points >= 1000 -> "Silver I"
                points > 0 -> "Bronze ($points)"
                else -> "Unranked"
            }
        }
    }
}
