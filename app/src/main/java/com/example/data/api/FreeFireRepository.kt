package com.example.data.api

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.local.SearchHistoryDao
import com.example.data.local.SearchHistoryEntity
import com.example.data.model.PlayerInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class FreeFireRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    val historyDao: SearchHistoryDao = db.searchHistoryDao()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(35, TimeUnit.SECONDS)
        .readTimeout(35, TimeUnit.SECONDS)
        .writeTimeout(35, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "FreeFirePlayerCheckerApp/1.0")
                .header("Accept", "application/json")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://freefireinfo-zy9l.onrender.com/")
        .client(okHttpClient)
        .build()

    private val api = retrofit.create(FreeFireApiService::class.java)

    val allHistory: Flow<List<SearchHistoryEntity>> = historyDao.getAllHistory()
    val favoriteHistory: Flow<List<SearchHistoryEntity>> = historyDao.getFavorites()

    suspend fun fetchPlayerInfo(uid: String, serverCode: String): Result<PlayerInfo> = withContext(Dispatchers.IO) {
        val cleanUid = uid.trim()
        val cleanServer = serverCode.trim().uppercase()

        if (cleanUid.isBlank()) {
            return@withContext Result.failure(IllegalArgumentException("UID cannot be empty"))
        }
        if (!cleanUid.all { it.isDigit() }) {
            return@withContext Result.failure(IllegalArgumentException("UID must contain only numbers"))
        }

        var lastError: Throwable? = null

        // 1. Try Primary Endpoint: /api/v1/player-profile?uid=...&server=...
        try {
            val response = api.getPlayerProfile(uid = cleanUid, server = cleanServer, region = cleanServer)
            val code = response.code()
            val rawBody = response.body()?.string() ?: response.errorBody()?.string() ?: ""

            if (response.isSuccessful && rawBody.isNotBlank()) {
                val parsed = parseJsonResponse(rawBody, cleanUid, cleanServer)
                if (parsed != null) {
                    saveToHistory(parsed)
                    return@withContext Result.success(parsed)
                }
            } else if (rawBody.isNotBlank()) {
                val apiErrMsg = extractErrorMessage(rawBody)
                if (apiErrMsg != null) {
                    lastError = Exception(apiErrMsg)
                }
            }
        } catch (e: Exception) {
            lastError = e
        }

        // 2. Try Fallback Endpoint: /info?uid=...&server=...
        try {
            val fallbackResponse = api.getPlayerInfoLegacy(uid = cleanUid, server = cleanServer)
            val rawBody = fallbackResponse.body()?.string() ?: fallbackResponse.errorBody()?.string() ?: ""
            if (fallbackResponse.isSuccessful && rawBody.isNotBlank()) {
                val parsed = parseJsonResponse(rawBody, cleanUid, cleanServer)
                if (parsed != null) {
                    saveToHistory(parsed)
                    return@withContext Result.success(parsed)
                }
            }
        } catch (e: Exception) {
            if (lastError == null) lastError = e
        }

        // 3. Try Search endpoint if needed: /api/v1/search-players?keyword=...&server=...
        try {
            val searchResponse = api.searchPlayers(keyword = cleanUid, server = cleanServer)
            val rawBody = searchResponse.body()?.string() ?: ""
            if (searchResponse.isSuccessful && rawBody.isNotBlank()) {
                val parsed = parseJsonResponse(rawBody, cleanUid, cleanServer)
                if (parsed != null) {
                    saveToHistory(parsed)
                    return@withContext Result.success(parsed)
                }
            }
        } catch (e: Exception) {
            // ignore fallback error
        }

        val finalMsg = when {
            lastError?.message?.contains("not found", ignoreCase = true) == true ->
                "Player with UID '$cleanUid' not found on server '$cleanServer'."
            lastError?.message?.isNotBlank() == true ->
                lastError.message ?: "Unable to fetch player info from server."
            else ->
                "No player profile found for UID '$cleanUid' on server '$cleanServer'. Please check the UID or try another server region."
        }

        return@withContext Result.failure(Exception(finalMsg))
    }

    private fun extractErrorMessage(jsonStr: String): String? {
        return try {
            val json = JSONObject(jsonStr)
            when {
                json.has("error") -> json.optString("error")
                json.has("message") -> json.optString("message")
                json.has("msg") -> json.optString("msg")
                json.has("detail") -> json.optString("detail")
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseJsonResponse(rawJson: String, inputUid: String, serverCode: String): PlayerInfo? {
        try {
            val trimmed = rawJson.trim()
            val rootObj: JSONObject = when {
                trimmed.startsWith("{") -> JSONObject(trimmed)
                trimmed.startsWith("[") -> {
                    val arr = JSONArray(trimmed)
                    if (arr.length() > 0) arr.getJSONObject(0) else return null
                }
                else -> return null
            }

            // Check if it's an error payload
            if (rootObj.optInt("status") == 404 ||
                rootObj.optString("error").isNotBlank() ||
                (rootObj.optString("message").contains("not found", ignoreCase = true))
            ) {
                return null
            }

            // Locate account / basic info objects
            val accountInfo = rootObj.optJSONObject("AccountInfo")
                ?: rootObj.optJSONObject("accountInfo")
                ?: rootObj.optJSONObject("basicInfo")
                ?: rootObj.optJSONObject("data")
                ?: rootObj.optJSONObject("player")
                ?: rootObj

            val profileInfo = rootObj.optJSONObject("AccountProfileInfo")
                ?: rootObj.optJSONObject("profileInfo")
                ?: rootObj.optJSONObject("profile")

            val guildInfo = rootObj.optJSONObject("GuildInfo")
                ?: rootObj.optJSONObject("guildInfo")
                ?: rootObj.optJSONObject("clan")
                ?: rootObj.optJSONObject("guild")

            val petInfo = rootObj.optJSONObject("PetInfo")
                ?: rootObj.optJSONObject("petInfo")
                ?: rootObj.optJSONObject("pet")

            val socialInfo = rootObj.optJSONObject("SocialInfo")
                ?: rootObj.optJSONObject("socialInfo")
                ?: rootObj.optJSONObject("social")

            val creditInfo = rootObj.optJSONObject("CreditScoreInfo")
                ?: rootObj.optJSONObject("creditScoreInfo")

            // Extract Name
            val name = optStringAny(
                accountInfo,
                "AccountName", "accountName", "nickname", "name", "playerName", "userName"
            ).ifBlank {
                optStringAny(rootObj, "name", "nickname", "AccountName")
            }.ifBlank { "Free Fire Player" }

            // Extract UID
            val uid = optStringAny(
                accountInfo,
                "AccountId", "accountId", "uid", "id", "player_id"
            ).ifBlank { inputUid }

            // Extract Level
            val level = optIntAny(accountInfo, "AccountLevel", "accountLevel", "level", "playerLevel", defaultValue = 1)
            val exp = optLongAny(accountInfo, "AccountEXP", "accountExp", "exp", "experience")
            val likes = optLongAny(accountInfo, "AccountLikes", "accountLikes", "likes", "likeCount")

            val createTime = optLongAny(accountInfo, "AccountCreateTime", "accountCreateTime", "createTime", "createdAt", "create_time")
            val lastLogin = optLongAny(accountInfo, "AccountLastLogin", "accountLastLogin", "lastLogin", "last_login", "lastOnline")

            val region = optStringAny(accountInfo, "AccountRegion", "accountRegion", "region", "server").ifBlank { serverCode }
            val releaseVersion = optStringAny(accountInfo, "ReleaseVersion", "releaseVersion", "version")
            val seasonId = optIntAny(accountInfo, "AccountSeasonId", "seasonId", "season")
            val badgeCount = optIntAny(accountInfo, "AccountBPBadges", "badges", "bpBadges")
            val avatarId = optStringAny(accountInfo, "AccountAvatarId", "avatarId", "avatar")
            val bannerId = optStringAny(accountInfo, "AccountBannerId", "bannerId", "banner")
            val title = optStringAny(accountInfo, "EquippedTitle", "title", "equippedTitle")

            // Ranks
            val brRankPoints = optIntAny(accountInfo, "BrRankPoint", "brRankPoint", "br_rank_points", "brPoints")
            val brMaxRank = optIntAny(accountInfo, "BrMaxRank", "brMaxRank", "br_max_rank")
            val csRankPoints = optIntAny(accountInfo, "CsRankPoint", "csRankPoint", "cs_rank_points", "csPoints")
            val csMaxRank = optIntAny(accountInfo, "CsMaxRank", "csMaxRank", "cs_max_rank")

            // Guild
            var hasGuild = false
            var gName = ""
            var gId = ""
            var gLevel = 0
            var gCap = 0
            var gMembers = 0
            var gOwner = ""
            var gOwnerName = ""

            if (guildInfo != null) {
                gName = optStringAny(guildInfo, "GuildName", "guildName", "name", "clanName")
                gId = optStringAny(guildInfo, "GuildID", "guildId", "id", "clanId")
                gLevel = optIntAny(guildInfo, "GuildLevel", "guildLevel", "level")
                gCap = optIntAny(guildInfo, "GuildCapacity", "guildCapacity", "capacity", "maxMembers")
                gMembers = optIntAny(guildInfo, "GuildMember", "guildMembers", "members", "memberCount")
                gOwner = optStringAny(guildInfo, "GuildOwner", "guildOwner", "leaderUid", "owner")
                gOwnerName = optStringAny(guildInfo, "GuildOwnerName", "guildOwnerName", "leaderName", "ownerName")
                hasGuild = gName.isNotBlank() || gId.isNotBlank()
            }

            // Pet
            var hasPet = false
            var pName = ""
            var pLevel = 0
            var pExp = 0L
            var pSkill = ""
            var pType = ""

            if (petInfo != null) {
                pName = optStringAny(petInfo, "PetName", "name", "petName")
                pLevel = optIntAny(petInfo, "PetLevel", "level", "petLevel")
                pExp = optLongAny(petInfo, "PetEXP", "exp", "petExp")
                pSkill = optStringAny(petInfo, "SelectedSkillId", "skill", "petSkill")
                pType = optStringAny(petInfo, "PetType", "type", "petType")
                hasPet = pName.isNotBlank() || pLevel > 0
            }

            // Social & Bio
            val signature = if (socialInfo != null) {
                optStringAny(socialInfo, "Signature", "signature", "bio", "AccountSignature")
            } else {
                optStringAny(accountInfo, "Signature", "signature", "bio")
            }
            val gender = socialInfo?.let { optStringAny(it, "Gender", "gender") } ?: ""
            val language = socialInfo?.let { optStringAny(it, "Language", "language") } ?: ""

            // Credit score
            val creditScore = creditInfo?.let {
                optIntAny(it, "CreditScore", "creditScore", "score", defaultValue = 100)
            } ?: 100

            // Formatted JSON preview
            val formattedJson = try {
                rootObj.toString(2)
            } catch (e: Exception) {
                rawJson
            }

            return PlayerInfo(
                uid = uid,
                name = name,
                server = serverCode,
                level = level,
                exp = exp,
                likes = likes,
                signature = signature,
                createTime = createTime,
                lastLogin = lastLogin,
                region = region,
                releaseVersion = releaseVersion,
                seasonId = seasonId,
                badgeCount = badgeCount,
                avatarId = avatarId,
                bannerId = bannerId,
                title = title,
                brRankPoints = brRankPoints,
                brMaxRank = brMaxRank,
                csRankPoints = csRankPoints,
                csMaxRank = csMaxRank,
                hasGuild = hasGuild,
                guildId = gId,
                guildName = gName,
                guildLevel = gLevel,
                guildCapacity = gCap,
                guildMembers = gMembers,
                guildOwner = gOwner,
                guildOwnerName = gOwnerName,
                hasPet = hasPet,
                petName = pName,
                petLevel = pLevel,
                petExp = pExp,
                petSkill = pSkill,
                petType = pType,
                gender = gender,
                language = language,
                creditScore = creditScore,
                rawJson = formattedJson
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private suspend fun saveToHistory(player: PlayerInfo) {
        try {
            val existing = historyDao.findEntry(player.uid, player.server)
            val entity = SearchHistoryEntity(
                id = existing?.id ?: 0L,
                uid = player.uid,
                server = player.server,
                playerName = player.name,
                playerLevel = player.level,
                playerLikes = player.likes,
                brRankPoints = player.brRankPoints,
                csRankPoints = player.csRankPoints,
                guildName = player.guildName,
                timestamp = System.currentTimeMillis(),
                isFavorite = existing?.isFavorite ?: false
            )
            historyDao.insert(entity)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun toggleFavorite(entity: SearchHistoryEntity) {
        withContext(Dispatchers.IO) {
            historyDao.update(entity.copy(isFavorite = !entity.isFavorite))
        }
    }

    suspend fun deleteHistory(id: Long) {
        withContext(Dispatchers.IO) {
            historyDao.deleteById(id)
        }
    }

    suspend fun clearHistory(onlyNonFavorites: Boolean = true) {
        withContext(Dispatchers.IO) {
            if (onlyNonFavorites) {
                historyDao.clearNonFavorites()
            } else {
                historyDao.clearAll()
            }
        }
    }

    private fun optStringAny(json: JSONObject, vararg keys: String): String {
        for (key in keys) {
            if (json.has(key) && !json.isNull(key)) {
                val str = json.optString(key, "")
                if (str.isNotBlank() && str != "null") return str
            }
        }
        return ""
    }

    private fun optIntAny(json: JSONObject, vararg keys: String, defaultValue: Int = 0): Int {
        for (key in keys) {
            if (json.has(key) && !json.isNull(key)) {
                val num = json.optInt(key, -1)
                if (num != -1) return num
                val str = json.optString(key, "")
                val parsed = str.toIntOrNull()
                if (parsed != null) return parsed
            }
        }
        return defaultValue
    }

    private fun optLongAny(json: JSONObject, vararg keys: String, defaultValue: Long = 0L): Long {
        for (key in keys) {
            if (json.has(key) && !json.isNull(key)) {
                val num = json.optLong(key, -1L)
                if (num != -1L) return num
                val str = json.optString(key, "")
                val parsed = str.toLongOrNull()
                if (parsed != null) return parsed
            }
        }
        return defaultValue
    }
}
