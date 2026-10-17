package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import com.example.data.auth.AuthState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppRole
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.LaserCyanLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CjTopBar(
    currentRole: AppRole,
    requirementCount: Int,
    onRoleSelected: (AppRole) -> Unit,
    onOpenRequirements: () -> Unit,
    authState: AuthState = AuthState.Unauthenticated,
    onSignInClick: () -> Unit = {},
    onSignOutClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var roleMenuExpanded by remember { mutableStateOf(false) }
    var userMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier.testTag("cj_top_app_bar"),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = IndustrialSurface,
            titleContentColor = Color.White
        ),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Industrial Anvil / Beam Logo badge
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ForgeAmberLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "CJ Fabrications Logo",
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Row {
                    Text(
                        text = "CJ ",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        color = ForgeAmberLight
                    )
                    Text(
                        text = "FABRICATIONS",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = Color.White,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        },
        actions = {
            // Role switcher pill button
            Box {
                Surface(
                    onClick = { roleMenuExpanded = true },
                    shape = RoundedCornerShape(16.dp),
                    color = when (currentRole) {
                        AppRole.CUSTOMER -> MaterialTheme.colorScheme.surfaceVariant
                        AppRole.VENDOR -> ForgeAmberLight.copy(alpha = 0.2f)
                        AppRole.ADMIN -> LaserCyanLight.copy(alpha = 0.2f)
                    },
                    modifier = Modifier.testTag("role_switcher_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = when (currentRole) {
                                AppRole.CUSTOMER -> Icons.Default.ListAlt
                                AppRole.VENDOR -> Icons.Default.Build
                                AppRole.ADMIN -> Icons.Default.Shield
                            },
                            contentDescription = "Role",
                            modifier = Modifier.size(14.dp),
                            tint = when (currentRole) {
                                AppRole.CUSTOMER -> Color.White
                                AppRole.VENDOR -> ForgeAmberLight
                                AppRole.ADMIN -> LaserCyanLight
                            }
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = currentRole.badge,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (currentRole) {
                                AppRole.CUSTOMER -> Color.White
                                AppRole.VENDOR -> ForgeAmberLight
                                AppRole.ADMIN -> LaserCyanLight
                            }
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Switch Role",
                            modifier = Modifier.size(14.dp),
                            tint = Color.LightGray
                        )
                    }
                }

                DropdownMenu(
                    expanded = roleMenuExpanded,
                    onDismissRequest = { roleMenuExpanded = false }
                ) {
                    AppRole.values().forEach { role ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = role.title,
                                    fontWeight = if (role == currentRole) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            onClick = {
                                onRoleSelected(role)
                                roleMenuExpanded = false
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = when (role) {
                                        AppRole.CUSTOMER -> Icons.Default.ListAlt
                                        AppRole.VENDOR -> Icons.Default.Build
                                        AppRole.ADMIN -> Icons.Default.Shield
                                    },
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Google One-Tap Sign In / Profile Status
            when (authState) {
                is AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp).padding(4.dp),
                        strokeWidth = 2.dp,
                        color = ForgeAmberLight
                    )
                }
                is AuthState.Authenticated -> {
                    Box {
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = Color(0xFF1E2836),
                            modifier = Modifier
                                .clickable { userMenuExpanded = true }
                                .padding(horizontal = 2.dp, vertical = 2.dp)
                                .testTag("user_profile_chip")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF22C55E))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = authState.user.displayName?.split(" ")?.firstOrNull() ?: "Signed In",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = userMenuExpanded,
                            onDismissRequest = { userMenuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = authState.user.email ?: authState.user.uid,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                },
                                onClick = {},
                                leadingIcon = {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null)
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sign Out", color = Color(0xFFEF4444)) },
                                onClick = {
                                    userMenuExpanded = false
                                    onSignOutClick()
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color(0xFFEF4444))
                                }
                            )
                        }
                    }
                }
                is AuthState.Unauthenticated, is AuthState.Error -> {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = ForgeAmberLight,
                        modifier = Modifier
                            .clickable(onClick = onSignInClick)
                            .testTag("google_signin_btn")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Sign In with Google",
                                tint = Color.Black,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Sign In",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // "My Requirement" Button with Badge counter
            IconButton(
                onClick = onOpenRequirements,
                modifier = Modifier.testTag("my_requirement_btn")
            ) {
                BadgedBox(
                    badge = {
                        if (requirementCount > 0) {
                            Badge(
                                containerColor = ForgeAmberLight,
                                contentColor = Color.Black
                            ) {
                                Text(
                                    text = requirementCount.toString(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Assignment,
                        contentDescription = "My Requirement RFQs",
                        tint = Color.White
                    )
                }
            }
        }
    )
}
