package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.RfqItem
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import java.util.UUID

data class UploadedPhoto(
    val id: String = UUID.randomUUID().toString(),
    val label: String,
    val url: String
)

/**
 * Screen 4: Photo & Design Upload
 * Features:
 * - Camera and gallery options
 * - Support multiple images of gates, grills, railings, site locations, and reference designs
 * - Image previews with remove and replace actions
 * - Fields for project description, approximate dimensions, preferred material, finish, additional requirements
 * - "Submit Design" CTA adding the item to "My Requirement"
 */
@Composable
fun PhotoDesignUploadScreen(
    onDesignSubmitted: (RfqItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var projectTitle by remember { mutableStateOf("") }
    var categoryType by remember { mutableStateOf("Main Gates & Sliding Gates") }
    var description by remember { mutableStateOf("") }
    var lengthStr by remember { mutableStateOf("14.0") }
    var heightStr by remember { mutableStateOf("7.0") }
    var depthStr by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("feet") }
    var preferredMaterial by remember { mutableStateOf("Mild Steel (MS IS 2062)") }
    var preferredFinish by remember { mutableStateOf("Matte Black Powder Coated (80-micron)") }
    var specialInstructions by remember { mutableStateOf("") }
    var installationRequired by remember { mutableStateOf(true) }

    // Pre-loaded realistic uploaded photos with remove and replace actions
    val photos = remember {
        mutableStateListOf(
            UploadedPhoto("p1", "Reference Design 1", "https://images.unsplash.com/photo-1590381105924-c72589b9ef3f?w=600&auto=format&fit=crop&q=80"),
            UploadedPhoto("p2", "Existing Site Gate Pillar", "https://images.unsplash.com/photo-1513694203232-719a280e022f?w=600&auto=format&fit=crop&q=80"),
            UploadedPhoto("p3", "CAD Layout Drawing", "https://images.unsplash.com/photo-1503387762-592deb58ef4e?w=600&auto=format&fit=crop&q=80")
        )
    }

    var uploadSuccessMessage by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("photo_design_upload_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {

            // Header
            Text(
                text = "Photo & Design Upload",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Upload architectural photos, sketches, or CAD drawings. Our engineers will calculate material weights and assign certified workshops.",
                fontSize = 13.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Upload Triggers Card: Camera & Gallery
            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "ADD PHOTOS OR DRAWINGS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeAmberLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Camera Button
                        Button(
                            onClick = {
                                photos.add(
                                    UploadedPhoto(
                                        label = "Camera Capture #${photos.size + 1}",
                                        url = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=600&auto=format&fit=crop&q=80"
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialSurfaceContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("camera_upload_button")
                        ) {
                            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = ForgeAmberLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Camera", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }

                        // Gallery / Files Button
                        Button(
                            onClick = {
                                photos.add(
                                    UploadedPhoto(
                                        label = "Gallery Photo #${photos.size + 1}",
                                        url = "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=600&auto=format&fit=crop&q=80"
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndustrialSurfaceContainer),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).height(48.dp).testTag("gallery_upload_button")
                        ) {
                            Icon(imageVector = Icons.Default.PhotoLibrary, contentDescription = null, tint = LaserCyanLight)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Gallery / PDF", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Supported: JPEG, PNG, WEBP, PDF (Up to 15MB each). Maximum 8 attachments.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Previews Gallery with Remove and Replace actions
            if (photos.isNotEmpty()) {
                Text(
                    text = "ATTACHED PREVIEWS (${photos.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    itemsIndexed(photos) { index, photo ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                            modifier = Modifier.width(160.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(110.dp)
                                ) {
                                    AsyncImage(
                                        model = photo.url,
                                        contentDescription = photo.label,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    // Remove button
                                    IconButton(
                                        onClick = { photos.removeAt(index) },
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .padding(4.dp)
                                            .size(28.dp)
                                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                            .testTag("remove_photo_${photo.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Remove photo",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = photo.label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .clickable {
                                                // Replace with alternate photo
                                                photos[index] = photo.copy(
                                                    url = "https://images.unsplash.com/photo-1541888946425-d0fbb18615f8?w=600&auto=format&fit=crop&q=80",
                                                    label = "${photo.label} (Replaced)"
                                                )
                                            }
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Refresh,
                                            contentDescription = "Replace",
                                            tint = ForgeAmberLight,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Replace", fontSize = 11.sp, color = ForgeAmberLight)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Project Specifications Form
            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DESIGN SPECIFICATIONS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeAmberLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Title
                    OutlinedTextField(
                        value = projectTitle,
                        onValueChange = { projectTitle = it },
                        label = { Text("Project / Requirement Title") },
                        placeholder = { Text("e.g. Modern Laser-Cut Main Gate with Pedestrian Door") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upload_title_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Category Selector
                    Text("Fabrication Category", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(
                            listOf(
                                "Main Gates & Sliding Gates",
                                "Balcony & Staircase Railings",
                                "Window Grills & Security Grills",
                                "Roofing & Metal Sheds",
                                "Structural & Custom Metalwork"
                            ).size
                        ) { idx ->
                            val cat = listOf(
                                "Main Gates & Sliding Gates",
                                "Balcony & Staircase Railings",
                                "Window Grills & Security Grills",
                                "Roofing & Metal Sheds",
                                "Structural & Custom Metalwork"
                            )[idx]
                            val isSelected = categoryType == cat
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) ForgeAmberLight.copy(alpha = 0.2f) else IndustrialSurfaceContainer,
                                border = androidx.compose.foundation.BorderStroke(
                                    width = 1.dp,
                                    color = if (isSelected) ForgeAmberLight else Color(0xFF343B4B)
                                ),
                                modifier = Modifier.clickable { categoryType = cat }
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) ForgeAmberLight else Color.White,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dimensions Row
                    Text("Approximate Dimensions", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = lengthStr,
                            onValueChange = { lengthStr = it },
                            label = { Text("Length/Width") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("upload_length_input")
                        )
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("Height") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("upload_height_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Material & Finish Options
                    Text("Preferred Material", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Mild Steel (MS)", "Stainless Steel (SS 304)", "Aluminium").forEach { mat ->
                            val isSel = preferredMaterial.startsWith(mat.substringBefore(" "))
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeAmberLight.copy(alpha = 0.2f) else IndustrialSurfaceContainer)
                                    .border(1.dp, if (isSel) ForgeAmberLight else Color(0xFF343B4B), RoundedCornerShape(8.dp))
                                    .clickable { preferredMaterial = mat }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = mat,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) ForgeAmberLight else Color.LightGray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Description / Special Instructions
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Project Details & Special Instructions") },
                        placeholder = { Text("Specify thickness, motor automation needs, hinge positioning, or site access restrictions...") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("upload_description_input")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Installation required toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { installationRequired = !installationRequired }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = if (installationRequired) ForgeAmberLight else Color.Transparent,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (installationRequired) ForgeAmberLight else Color.Gray),
                            modifier = Modifier.size(20.dp)
                        ) {
                            if (installationRequired) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Site installation and civil anchoring required by fabricator team",
                            fontSize = 13.sp,
                            color = Color.White
                        )
                    }
                }
            }

            // Validation Error or Success banner
            validationError?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = Color(0xFFEF4444), fontSize = 13.sp)
            }

            uploadSuccessMessage?.let {
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
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Submit Design CTA
            Button(
                onClick = {
                    if (projectTitle.isBlank()) {
                        validationError = "Please enter a project title."
                        return@Button
                    }
                    val l = lengthStr.toDoubleOrNull() ?: 12.0
                    val h = heightStr.toDoubleOrNull() ?: 6.0
                    val randCode = (100..999).random()
                    val item = RfqItem(
                        id = UUID.randomUUID().toString(),
                        designCode = "CJ-UPL-$randCode",
                        productType = categoryType,
                        title = projectTitle.trim(),
                        lengthFt = l,
                        heightFt = h,
                        unit = unit,
                        material = preferredMaterial,
                        materialGrade = if (preferredMaterial.contains("SS")) "SS 304 Grade" else "IS 2062 Structural",
                        finish = preferredFinish,
                        quantity = 1,
                        installationRequired = installationRequired,
                        specialInstructions = description.ifBlank { "Custom design uploaded with ${photos.size} attachments." }
                    )
                    validationError = null
                    uploadSuccessMessage = "Design added to 'My Requirement'!"
                    onDesignSubmitted(item)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_uploaded_design_button")
            ) {
                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Design to My Requirement", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
