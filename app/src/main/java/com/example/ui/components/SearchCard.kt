package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ServerRegion
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DarkOutline
import com.example.ui.theme.DarkOutlineHighlight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceCard
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.FlameDark
import com.example.ui.theme.FlamePrimary
import com.example.ui.theme.FlamePrimaryLight
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchCard(
    uidInput: String,
    onUidChange: (String) -> Unit,
    selectedServer: ServerRegion,
    onServerChange: (ServerRegion) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onDemoSelect: (String, String) -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val clipboardManager = LocalClipboardManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var serverMenuExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = DarkSurfaceCard,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkOutline),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            // Header label
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = null,
                        tint = FlamePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PLAYER UID SEARCH",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }

                Text(
                    text = "No login required",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // UID Input Text Field
            OutlinedTextField(
                value = uidInput,
                onValueChange = onUidChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("uid_input"),
                placeholder = {
                    Text("Enter numeric Free Fire UID (e.g. 123456789)", color = TextMuted, fontSize = 14.sp)
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "UID Icon",
                        tint = if (uidInput.isNotBlank()) FlamePrimary else TextMuted
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (uidInput.isNotBlank()) {
                            IconButton(
                                onClick = onClear,
                                modifier = Modifier.testTag("clear_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear UID",
                                    tint = TextMuted
                                )
                            }
                        } else {
                            IconButton(
                                onClick = {
                                    val clip = clipboardManager.getText()?.text ?: ""
                                    val digits = clip.filter { it.isDigit() }
                                    if (digits.isNotBlank()) {
                                        onUidChange(digits)
                                    }
                                },
                                modifier = Modifier.testTag("paste_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentPaste,
                                    contentDescription = "Paste from clipboard",
                                    tint = CyanAccent
                                )
                            }
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()
                        onSearch()
                    }
                ),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlamePrimary,
                    unfocusedBorderColor = DarkOutline,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    cursorColor = FlamePrimary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Server Selector Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "GAME SERVER REGION",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )

                // Dropdown trigger
                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, DarkOutlineHighlight, RoundedCornerShape(8.dp))
                            .clickable { serverMenuExpanded = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("server_selector"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedServer.flag} ${selectedServer.code} - ${selectedServer.name}",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Expand servers",
                            tint = FlamePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Dropdown menu of all servers
                    DropdownMenu(
                        expanded = serverMenuExpanded,
                        onDismissRequest = { serverMenuExpanded = false },
                        modifier = Modifier
                            .background(DarkSurface)
                            .border(1.dp, DarkOutline, RoundedCornerShape(8.dp))
                    ) {
                        ServerRegion.ALL.forEach { server ->
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(server.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "${server.code} - ${server.name}",
                                            color = if (server == selectedServer) FlamePrimary else TextWhite,
                                            fontWeight = if (server == selectedServer) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )
                                    }
                                },
                                onClick = {
                                    onServerChange(server)
                                    serverMenuExpanded = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Server Pills (BD, IND, SG, BR, ID, PK, ME, NA)
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val popularServers = ServerRegion.ALL.filter { it.isPopular }
                popularServers.forEach { server ->
                    val isSelected = server.code == selectedServer.code
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(
                                if (isSelected) FlamePrimary.copy(alpha = 0.2f) else DarkSurface
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) FlamePrimary else DarkOutline,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable { onServerChange(server) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("server_chip_${server.code.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(server.flag, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = server.code,
                                color = if (isSelected) FlamePrimary else TextWhite,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Search CTA Button
            Button(
                onClick = {
                    keyboardController?.hide()
                    onSearch()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("search_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FlamePrimary,
                    contentColor = TextDark
                ),
                shape = RoundedCornerShape(14.dp),
                enabled = !isLoading
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search icon",
                        tint = TextDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isLoading) "FETCHING INTEL..." else "SEARCH PLAYER INFO",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Demo test UIDs for easy reviewer testing
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurfaceVariant.copy(alpha = 0.5f))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Quick Demo UIDs (Click to Test):",
                        color = CyanAccent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        Triple("BD", "2720231804", "🇧🇩 BD Pro"),
                        Triple("IND", "123456789", "🇮🇳 IND"),
                        Triple("BR", "10000001", "🇧🇷 BR")
                    ).forEach { (srv, uid, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkSurface)
                                .border(0.5.dp, DarkOutline, RoundedCornerShape(6.dp))
                                .clickable { onDemoSelect(uid, srv) }
                                .padding(vertical = 5.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
    }
}
