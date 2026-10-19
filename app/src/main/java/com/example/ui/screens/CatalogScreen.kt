package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.DesignItem
import com.example.data.model.FabricationCategory
import com.example.data.model.RfqItem
import com.example.ui.components.MaterialChip
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.data.repository.DataSyncStatus
import com.example.ui.theme.SteelSlateLight
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    categories: List<FabricationCategory>,
    designs: List<DesignItem>,
    selectedCategoryId: String?,
    searchQuery: String,
    selectedMaterial: String?,
    onCategorySelect: (String?) -> Unit,
    onSearchChange: (String) -> Unit,
    onMaterialSelect: (String?) -> Unit,
    onDesignClick: (String) -> Unit,
    onAddToRequirement: (RfqItem) -> Unit,
    syncStatus: DataSyncStatus? = null,
    onRefreshSync: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var quickAddDesign by remember { mutableStateOf<DesignItem?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("catalog_screen")
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Search gates, railings, stairs, laser cut...", fontSize = 13.sp, color = Color.Gray) },
            leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = ForgeAmberLight)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear", tint = Color.LightGray)
                    }
                }
            },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ForgeAmberLight,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("catalog_search_input")
        )

        // Real-time Sync Status Row
        if (syncStatus != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (syncStatus.error == null) Color(0xFF10B981) else Color(0xFFF59E0B))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (syncStatus.isSyncing) "Syncing with PostgreSQL..." else "PostgreSQL + Firestore live (${syncStatus.itemsCount} listings)",
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )
                }
                if (onRefreshSync != null) {
                    Text(
                        text = "Sync Now",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeAmberLight,
                        modifier = Modifier
                            .clickable { onRefreshSync() }
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }

        // Category Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            MaterialChip(
                label = "All Designs",
                isSelected = selectedCategoryId == null,
                onClick = { onCategorySelect(null) }
            )
            categories.forEach { cat ->
                MaterialChip(
                    label = cat.shortName,
                    isSelected = selectedCategoryId == cat.id,
                    onClick = { onCategorySelect(cat.id) }
                )
            }
        }

        // Material Filter Horizontal Scroll
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            val materials = listOf(
                "Mild Steel",
                "Stainless Steel",
                "Galvanized",
                "Aluminium",
                "High-Tensile Steel",
                "CRCA Sheet",
                "Wrought Iron"
            )
            MaterialChip(
                label = "All Materials",
                isSelected = selectedMaterial == null,
                onClick = { onMaterialSelect(null) }
            )
            materials.forEach { mat ->
                MaterialChip(
                    label = mat,
                    isSelected = selectedMaterial == mat,
                    onClick = { onMaterialSelect(if (selectedMaterial == mat) null else mat) }
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Catalog List
        if (designs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = SteelSlateLight,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No designs found matching filters",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Try clearing the search query or material filters.",
                        color = SteelSlateLight,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(designs, key = { it.id }) { design ->
                    DesignCard(
                        design = design,
                        onClick = { onDesignClick(design.id) },
                        onQuickAdd = { quickAddDesign = design }
                    )
                }
            }
        }
    }

    // Quick Customization BottomSheet when clicking "Add to My Requirement"
    quickAddDesign?.let { design ->
        QuickAddRequirementSheet(
            design = design,
            onDismiss = { quickAddDesign = null },
            onAdd = { item ->
                onAddToRequirement(item)
                quickAddDesign = null
            }
        )
    }
}

@Composable
private fun DesignCard(
    design: DesignItem,
    onClick: () -> Unit,
    onQuickAdd: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("design_card_${design.code}")
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                AsyncImage(
                    model = design.imageUrl,
                    contentDescription = design.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Code Badge
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(bottomEnd = 8.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = design.code,
                        color = ForgeAmberLight,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                if (design.isPopular) {
                    Surface(
                        color = ForgeAmberLight,
                        shape = RoundedCornerShape(bottomStart = 8.dp),
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = "POPULAR CHOICE",
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = design.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = design.description,
                    fontSize = 12.sp,
                    color = SteelSlateLight,
                    maxLines = 2,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Materials & Specs row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Indicative Rate:",
                            fontSize = 10.sp,
                            color = SteelSlateLight
                        )
                        Text(
                            text = "₹${design.indicativeRateMin} - ₹${design.indicativeRateMax} / ${design.rateUnit}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeAmberLight
                        )
                    }

                    Button(
                        onClick = onQuickAdd,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ForgeAmberLight,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("add_to_requirement_btn_${design.code}")
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Add to My Requirement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QuickAddRequirementSheet(
    design: DesignItem,
    onDismiss: () -> Unit,
    onAdd: (RfqItem) -> Unit
) {
    var lengthStr by remember { mutableStateOf("12.0") }
    var heightStr by remember { mutableStateOf("6.5") }
    var selectedMaterial by remember { mutableStateOf(design.materialsAvailable.firstOrNull() ?: "MS IS 2062") }
    var selectedFinish by remember { mutableStateOf(design.finishOptions.firstOrNull() ?: "Powder Coated") }
    var quantityStr by remember { mutableStateOf("1") }
    var installRequired by remember { mutableStateOf(true) }
    var notes by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = IndustrialSurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Add to My Requirement",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "${design.code} • ${design.title}",
                fontSize = 12.sp,
                color = ForgeAmberLight
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Dimensions Input
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = lengthStr,
                    onValueChange = { lengthStr = it },
                    label = { Text("Length / Width (ft)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = heightStr,
                    onValueChange = { heightStr = it },
                    label = { Text("Height (ft)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedTextField(
                    value = quantityStr,
                    onValueChange = { quantityStr = it },
                    label = { Text("Qty", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.width(70.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Area calculation preview
            val l = lengthStr.toDoubleOrNull() ?: 0.0
            val h = heightStr.toDoubleOrNull() ?: 0.0
            val q = quantityStr.toIntOrNull() ?: 1
            val totalSqFt = l * h * q

            Surface(
                color = Color.Black.copy(alpha = 0.3f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Total Calculated Area: ${String.format("%.1f", totalSqFt)} sq. ft.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "Est. ₹${(totalSqFt * design.indicativeRateMin).toInt()} - ₹${(totalSqFt * design.indicativeRateMax).toInt()}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeAmberLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Material selection
            Text("Preferred Material:", fontSize = 12.sp, color = SteelSlateLight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                design.materialsAvailable.forEach { mat ->
                    MaterialChip(
                        label = mat,
                        isSelected = selectedMaterial == mat,
                        onClick = { selectedMaterial = mat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Finish selection
            Text("Finish & Coating:", fontSize = 12.sp, color = SteelSlateLight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp)
            ) {
                design.finishOptions.forEach { fin ->
                    MaterialChip(
                        label = fin,
                        isSelected = selectedFinish == fin,
                        onClick = { selectedFinish = fin }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val item = RfqItem(
                        designCode = design.code,
                        productType = design.categoryId,
                        title = design.title,
                        lengthFt = l,
                        heightFt = h,
                        material = selectedMaterial,
                        materialGrade = "Architectural / Industrial Standard",
                        finish = selectedFinish,
                        quantity = q,
                        installationRequired = installRequired,
                        specialInstructions = notes
                    )
                    onAdd(item)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_add_requirement_btn")
            ) {
                Text(
                    text = "Confirm & Add to Requirement",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
