package com.example.data.model

data class ServerRegion(
    val code: String,
    val name: String,
    val flag: String,
    val isPopular: Boolean = false
) {
    companion object {
        val ALL = listOf(
            ServerRegion("BD", "Bangladesh", "🇧🇩", isPopular = true),
            ServerRegion("IND", "India", "🇮🇳", isPopular = true),
            ServerRegion("SG", "Singapore", "🇸🇬", isPopular = true),
            ServerRegion("BR", "Brazil", "🇧🇷", isPopular = true),
            ServerRegion("ID", "Indonesia", "🇮🇩", isPopular = true),
            ServerRegion("PK", "Pakistan", "🇵🇰", isPopular = true),
            ServerRegion("ME", "Middle East", "🇦🇪", isPopular = true),
            ServerRegion("NA", "North America (US)", "🇺🇸", isPopular = true),
            ServerRegion("SAC", "South America (SAC)", "🇨🇱"),
            ServerRegion("VN", "Vietnam", "🇻🇳"),
            ServerRegion("TH", "Thailand", "🇹🇭"),
            ServerRegion("TW", "Taiwan", "🇹🇼"),
            ServerRegion("RU", "Russia / CIS", "🇷🇺"),
            ServerRegion("EU", "Europe", "🇪🇺")
        )

        val DEFAULT = ALL[0] // BD by default

        fun fromCode(code: String): ServerRegion {
            return ALL.find { it.code.equals(code, ignoreCase = true) }
                ?: ServerRegion(code.uppercase(), code.uppercase(), "🌐")
        }
    }
}
