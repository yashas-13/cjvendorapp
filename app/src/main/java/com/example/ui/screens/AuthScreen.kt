package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthState
import com.example.data.model.AppRole
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import kotlinx.coroutines.delay

/**
 * Screen 1: Customer Registration, Login & Auth
 * Features:
 * - Welcome screen with CJ Fabrications industrial logo & value prop
 * - Email/Password and Mobile Phone OTP login options
 * - Google One-Tap via Jetpack Credential Manager
 * - Registration with Full Name, +91 Phone, Email, Password, and Role selection
 * - Forgot Password modal and OTP 6-digit verification with countdown timer
 * - Terms of Service & Privacy Policy dialogs
 */
@Composable
fun AuthScreen(
    authState: AuthState,
    onSignInWithGoogle: () -> Unit,
    onSignOut: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Login, 1: Register
    var loginMethod by remember { mutableStateOf("EMAIL") } // "EMAIL" or "PHONE_OTP"

    // Form fields
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var phone by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(AppRole.CUSTOMER) }

    // OTP Flow states
    var isOtpSent by remember { mutableStateOf(false) }
    var otpCode by remember { mutableStateOf("") }
    var otpTimer by remember { mutableIntStateOf(60) }

    // Modals
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotEmail by remember { mutableStateOf("") }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var authSuccessMessage by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Countdown timer for OTP
    LaunchedEffect(isOtpSent, otpTimer) {
        if (isOtpSent && otpTimer > 0) {
            delay(1000L)
            otpTimer -= 1
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("auth_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Close / Back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("auth_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Auth",
                        tint = Color.LightGray
                    )
                }
            }

            // CJ Fabrications Industrial Logo & Header
            Surface(
                modifier = Modifier.size(72.dp),
                shape = RoundedCornerShape(16.dp),
                color = ForgeAmberLight,
                shadowElevation = 6.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "CJ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "CJ FABRICATIONS",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.sp
            )
            Text(
                text = "Precision Metalwork Direct From Certified Workshops • India",
                fontSize = 12.sp,
                color = ForgeAmberLight,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // If user is already authenticated
            if (authState is AuthState.Authenticated) {
                Surface(
                    color = IndustrialSurface,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForgeAmberLight.copy(alpha = 0.2f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    tint = ForgeAmberLight,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = authState.user.displayName ?: authState.user.email ?: "Authenticated User",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = authState.user.email ?: authState.user.phoneNumber ?: "Signed in via Google",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onSignOut,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("auth_sign_out_button")
                        ) {
                            Text("Sign Out", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                return@Column
            }

            // Google One-Tap Sign In Button
            OutlinedButton(
                onClick = onSignInWithGoogle,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = IndustrialSurface,
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B4354)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("google_one_tap_button")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "G",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color(0xFF4285F4)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Continue with Google One-Tap",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Divider "OR"
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E3440))
                Text(
                    text = "OR",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 14.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = Color(0xFF2E3440))
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Tab Selector: Login vs Register
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = IndustrialSurface,
                contentColor = ForgeAmberLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = ForgeAmberLight,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            text = "Sign In",
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_sign_in")
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            text = "Register",
                            fontSize = 14.sp,
                            fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("tab_register")
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedTab == 0) {
                // ==================== LOGIN TAB ====================
                // Sub-mode toggle: Email vs Phone OTP
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndustrialSurfaceContainer)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (loginMethod == "EMAIL") ForgeAmberLight else Color.Transparent)
                            .clickable { loginMethod = "EMAIL"; isOtpSent = false }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Email & Password",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (loginMethod == "EMAIL") Color.Black else Color.Gray
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (loginMethod == "PHONE_OTP") ForgeAmberLight else Color.Transparent)
                            .clickable { loginMethod = "PHONE_OTP" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Mobile Phone OTP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (loginMethod == "PHONE_OTP") Color.Black else Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (loginMethod == "EMAIL") {
                    // Email & Password Fields
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        placeholder = { Text("name@example.com") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ForgeAmberLight)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354),
                            focusedContainerColor = IndustrialSurface,
                            unfocusedContainerColor = IndustrialSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_email_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ForgeAmberLight)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle password visibility",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354),
                            focusedContainerColor = IndustrialSurface,
                            unfocusedContainerColor = IndustrialSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = {
                                forgotEmail = email
                                showForgotPasswordDialog = true
                            },
                            modifier = Modifier.testTag("forgot_password_button")
                        ) {
                            Text("Forgot Password?", fontSize = 12.sp, color = ForgeAmberLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            if (email.isBlank() || password.isBlank()) {
                                errorMessage = "Please enter both email and password."
                            } else {
                                errorMessage = null
                                authSuccessMessage = "Welcome back, ${email.substringBefore('@')}!"
                            }
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("auth_login_submit_button")
                    ) {
                        Text("Sign In With Email", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }

                } else {
                    // Mobile Phone OTP Flow
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number (+91)") },
                        placeholder = { Text("98450 12345") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ForgeAmberLight)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354),
                            focusedContainerColor = IndustrialSurface,
                            unfocusedContainerColor = IndustrialSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("auth_phone_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!isOtpSent) {
                        Button(
                            onClick = {
                                if (phone.length < 10) {
                                    errorMessage = "Please enter a valid 10-digit mobile number."
                                } else {
                                    errorMessage = null
                                    isOtpSent = true
                                    otpTimer = 60
                                }
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("send_otp_button")
                        ) {
                            Text("Send 6-Digit OTP", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // OTP Input Field
                        Surface(
                            color = IndustrialSurface,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "OTP sent to +91 $phone (Demo code: 739201)",
                                    fontSize = 12.sp,
                                    color = Color(0xFF10B981)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                OutlinedTextField(
                                    value = otpCode,
                                    onValueChange = { if (it.length <= 6) otpCode = it },
                                    label = { Text("Enter 6-Digit OTP") },
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = ForgeAmberLight,
                                        unfocusedBorderColor = Color(0xFF3B4354),
                                        focusedContainerColor = IndustrialSurfaceVariant,
                                        unfocusedContainerColor = IndustrialSurfaceVariant
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("otp_code_input")
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (otpTimer > 0) "Resend in ${otpTimer}s" else "OTP Expired",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                    TextButton(
                                        onClick = { otpTimer = 60 },
                                        enabled = otpTimer == 0
                                    ) {
                                        Text("Resend Code", fontSize = 12.sp, color = ForgeAmberLight)
                                    }
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = {
                                        if (otpCode.length == 6) {
                                            authSuccessMessage = "Phone verified successfully! Welcome to CJ Fabrications."
                                        } else {
                                            errorMessage = "Please enter all 6 digits of the OTP."
                                        }
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                                    modifier = Modifier.fillMaxWidth().testTag("verify_otp_button")
                                ) {
                                    Text("Verify & Sign In", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

            } else {
                // ==================== REGISTER TAB ====================
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Full Name") },
                    placeholder = { Text("Rajesh Kumar") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = ForgeAmberLight)
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeAmberLight,
                        unfocusedBorderColor = Color(0xFF3B4354),
                        focusedContainerColor = IndustrialSurface,
                        unfocusedContainerColor = IndustrialSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Mobile Number (+91)") },
                    placeholder = { Text("98450 12345") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = ForgeAmberLight)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeAmberLight,
                        unfocusedBorderColor = Color(0xFF3B4354),
                        focusedContainerColor = IndustrialSurface,
                        unfocusedContainerColor = IndustrialSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_phone_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address") },
                    placeholder = { Text("rajesh@builder.com") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Email, contentDescription = null, tint = ForgeAmberLight)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeAmberLight,
                        unfocusedBorderColor = Color(0xFF3B4354),
                        focusedContainerColor = IndustrialSurface,
                        unfocusedContainerColor = IndustrialSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_email_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Create Password (min 6 chars)") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = ForgeAmberLight)
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ForgeAmberLight,
                        unfocusedBorderColor = Color(0xFF3B4354),
                        focusedContainerColor = IndustrialSurface,
                        unfocusedContainerColor = IndustrialSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("reg_password_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Role Selector for Marketplace Registration
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "I am registering as:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.LightGray,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            AppRole.CUSTOMER to "Customer / Homeowner",
                            AppRole.VENDOR to "Fabrication Workshop",
                            AppRole.ADMIN to "Admin / Staff"
                        ).forEach { (role, label) ->
                            val isSelected = selectedRole == role
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) ForgeAmberLight.copy(alpha = 0.2f) else IndustrialSurface)
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) ForgeAmberLight else Color(0xFF343B4B),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedRole = role }
                                    .padding(vertical = 10.dp, horizontal = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ForgeAmberLight else Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Button(
                    onClick = {
                        if (fullName.isBlank() || phone.isBlank() || email.isBlank() || password.length < 6) {
                            errorMessage = "Please fill all registration fields (password min 6 chars)."
                        } else {
                            errorMessage = null
                            authSuccessMessage = "Account registered for $fullName! You can now submit RFQs and track orders."
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("reg_submit_button")
                ) {
                    Text("Create Account", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            // Error & Success Feedback
            errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = it,
                    color = Color(0xFFEF4444),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            authSuccessMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it,
                        color = Color(0xFF10B981),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Compliance Terms & Privacy Links
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showTermsDialog = true }) {
                    Text("Terms of Service", fontSize = 12.sp, color = Color.Gray)
                }
                Text("•", color = Color.DarkGray)
                TextButton(onClick = { showPrivacyDialog = true }) {
                    Text("Privacy Policy", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }

    // Forgot Password Dialog
    if (showForgotPasswordDialog) {
        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Your Password") },
            text = {
                Column {
                    Text(
                        text = "Enter your registered email address. We'll send instructions to reset your password.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = forgotEmail,
                        onValueChange = { forgotEmail = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showForgotPasswordDialog = false
                        authSuccessMessage = "Password reset link sent to $forgotEmail"
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Send Reset Link", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Terms of Service Dialog
    if (showTermsDialog) {
        AlertDialog(
            onDismissRequest = { showTermsDialog = false },
            title = { Text("Terms of Service") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "CJ Fabrications Marketplace Terms\n\n" +
                            "1. Quotations: All quotations are valid for 15 days from issue date. Material prices are subject to steel mill fluctuations.\n\n" +
                            "2. Fabrication: Work begins after 40% mobilization advance payment and site measurement verification.\n\n" +
                            "3. Warranty: Certified fabricators provide a 5-year workmanship guarantee against weld failure and rust penetration.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showTermsDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)) {
                    Text("Close", color = Color.Black)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Privacy Policy Dialog
    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy Policy") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(
                        text = "Data Protection & Privacy Policy\n\n" +
                            "• Contact information and site addresses are strictly shared only with assigned, verified workshop fabricators.\n\n" +
                            "• Customer photos and CAD drawings remain confidential and are not published without explicit permission.\n\n" +
                            "• We never sell customer records to third-party telemarketers.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showPrivacyDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)) {
                    Text("Understood", color = Color.Black)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}
