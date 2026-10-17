package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RfqItem
import com.example.ui.components.MaterialChip
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@Composable
fun CustomRfqScreen(
    onAddItemToRequirement: (RfqItem) -> Unit,
    onDirectSubmit: (RfqItem) -> Unit,
    modifier: Modifier = Modifier
) {
    var productType by remember { mutableStateOf("Main Gate & Sliding Gate") }
    var title by remember { mutableStateOf("Custom Fabricated Gate") }
    var lengthStr by remember { mutableStateOf("14.0") }
    var heightStr by remember { mutableStateOf("7.0") }
    var depthStr by remember { mutableStateOf("0.0") }
    var selectedUnit by remember { mutableStateOf("feet") }
    var selectedMaterial by remember { mutableStateOf("Mild Steel (MS)") }
    var materialGrade by remember { mutableStateOf("IS 2062 Grade E250") }
    var selectedFinish by remember { mutableStateOf("Powder Coated (Matte Black)") }
    var quantityStr by remember { mutableStateOf("1") }
    var installationRequired by remember { mutableStateOf(true) }
    var specialInstructions by remember { mutableStateOf("") }

    val attachments = remember { mutableStateListOf("Architectural_Gate_Drawing_v2.pdf") }

    val productTypes = listOf(
        "Main Gate & Sliding Gate",
        "SS/MS Balcony Railing",
        "Spiral / Cantilever Staircase",
        "Roofing & PEB Shed",
        "Window Safety Grill",
        "Mezzanine Structural Steel",
        "Bespoke Laser Cut Facade / Screen"
    )

    val materials = listOf("Mild Steel (MS)", "Stainless Steel (SS 304)", "Stainless Steel (SS 316)", "Aluminium 6063", "Wrought Iron")
    val finishes = listOf("Powder Coated (Matte Black)", "PU Enamel Painted", "Hot-Dip Galvanized", "Satin Hairline Finish", "PVD Gold Coating")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("custom_rfq_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Custom Fabrication Request",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Submit technical drawings, dimensions & specifications to receive itemized quotations from verified fabricators.",
                    fontSize = 12.sp,
                    color = SteelSlateLight,
                    lineHeight = 17.sp
                )
            }
        }

        // Product Category Selector
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("1. Product / Work Type", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        productTypes.forEach { type ->
                            MaterialChip(
                                label = type,
                                isSelected = productType == type,
                                onClick = {
                                    productType = type
                                    title = "Custom $type"
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Item Title / Description Label", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        )
                    )
                }
            }
        }

        // Dimensions and Units
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("2. Measurements & Quantity", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Row {
                            listOf("feet", "mm", "meters").forEach { u ->
                                Surface(
                                    color = if (selectedUnit == u) ForgeAmberLight else Color.Transparent,
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier
                                        .clickable { selectedUnit = u }
                                        .padding(horizontal = 4.dp)
                                ) {
                                    Text(
                                        text = u,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedUnit == u) Color.Black else SteelSlateLight,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = lengthStr,
                            onValueChange = { lengthStr = it },
                            label = { Text("Length / Width ($selectedUnit)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text("Height ($selectedUnit)", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = { Text("Units", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.width(70.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )
                    }

                    val l = lengthStr.toDoubleOrNull() ?: 0.0
                    val h = heightStr.toDoubleOrNull() ?: 0.0
                    val q = quantityStr.toIntOrNull() ?: 1
                    val area = l * h * q

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Calculated Total Surface: ${String.format("%.2f", area)} sq. $selectedUnit",
                        color = ForgeAmberLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Material & Grade
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("3. Material & Grade Specifications", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                    ) {
                        materials.forEach { m ->
                            MaterialChip(
                                label = m,
                                isSelected = selectedMaterial == m,
                                onClick = {
                                    selectedMaterial = m
                                    materialGrade = when {
                                        m.contains("SS 304") -> "SS 304 Jindal Architectural Grade"
                                        m.contains("SS 316") -> "SS 316 Marine Grade (Anti-corrosive)"
                                        m.contains("Aluminium") -> "Alloy 6063-T6 Extrusion"
                                        else -> "IS 2062 Grade E250 Mild Steel"
                                    }
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = materialGrade,
                        onValueChange = { materialGrade = it },
                        label = { Text("Mill / Grade Certification Standard", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Finish & Surface Treatment:", fontSize = 12.sp, color = SteelSlateLight)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp)
                    ) {
                        finishes.forEach { f ->
                            MaterialChip(
                                label = f,
                                isSelected = selectedFinish == f,
                                onClick = { selectedFinish = f }
                            )
                        }
                    }
                }
            }
        }

        // Technical Drawings & Attachments
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("4. Technical Drawings & Photos", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        OutlinedButton(
                            onClick = {
                                attachments.add("Site_Photo_${System.currentTimeMillis() % 1000}.jpg")
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.UploadFile, contentDescription = null, modifier = Modifier.size(14.dp), tint = ForgeAmberLight)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach File", fontSize = 11.sp, color = ForgeAmberLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (attachments.isEmpty()) {
                        Text(
                            text = "No attachments added. Upload CAD / PDF / site photos for accurate vendor quoting.",
                            fontSize = 11.sp,
                            color = SteelSlateLight
                        )
                    } else {
                        attachments.forEach { file ->
                            Surface(
                                color = IndustrialSurfaceContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Description,
                                        contentDescription = null,
                                        tint = LaserCyanLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = file,
                                        fontSize = 12.sp,
                                        color = Color.White,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(
                                        onClick = { attachments.remove(file) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Remove",
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // On-Site Installation & Special Notes
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("On-Site Installation Required?", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                            Text("Fabricator sends skilled erection team with welding gear", fontSize = 11.sp, color = SteelSlateLight)
                        }
                        Switch(
                            checked = installationRequired,
                            onCheckedChange = { installationRequired = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = ForgeAmberLight,
                                checkedTrackColor = ForgeAmberLight.copy(alpha = 0.5f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = specialInstructions,
                        onValueChange = { specialInstructions = it },
                        label = { Text("Special Instructions (Motor provision, weld grinding flush, baseplate anchors)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                    )
                }
            }
        }

        // Action Buttons
        item {
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = {
                        val item = RfqItem(
                            productType = productType,
                            title = title,
                            lengthFt = lengthStr.toDoubleOrNull() ?: 10.0,
                            heightFt = heightStr.toDoubleOrNull() ?: 5.0,
                            depthFt = depthStr.toDoubleOrNull() ?: 0.0,
                            unit = selectedUnit,
                            material = selectedMaterial,
                            materialGrade = materialGrade,
                            finish = selectedFinish,
                            quantity = quantityStr.toIntOrNull() ?: 1,
                            installationRequired = installationRequired,
                            specialInstructions = specialInstructions,
                            referenceAttachmentNames = attachments.toList()
                        )
                        onAddItemToRequirement(item)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("custom_rfq_add_to_requirement_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForgeAmberLight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to My Requirement", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}
