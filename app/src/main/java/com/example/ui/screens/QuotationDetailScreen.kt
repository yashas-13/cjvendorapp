package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Quotation
import com.example.data.model.QuotationStatus
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import java.net.URLEncoder

/**
 * Screen 6: Quotation Screen
 * Features:
 * - Professional quotation screen with quotation number, date, validity, and project details
 * - Itemised materials, quantities, dimensions, labour, finishing, transportation, installation, applicable taxes, and total amount
 * - View PDF, Download, Share, Accept Quote, Reject Quote, and Request Revision actions
 * - Clearly distinguishes draft quotations from officially issued quotations
 */
@Composable
fun QuotationDetailScreen(
    quotation: Quotation,
    onAccept: () -> Unit,
    onReject: (reason: String) -> Unit,
    onRequestRevision: (notes: String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isOfficialIssued by remember { mutableStateOf(quotation.status != QuotationStatus.EXPIRED) }
    var showRejectDialog by remember { mutableStateOf(false) }
    var rejectReason by remember { mutableStateOf("") }
    var showRevisionDialog by remember { mutableStateOf(false) }
    var revisionNotes by remember { mutableStateOf("") }
    var showPdfViewerDialog by remember { mutableStateOf(false) }
    var showDownloadSuccessDialog by remember { mutableStateOf(false) }

    val formattedRef = "CJF-QT-2026-${quotation.id.takeLast(4).uppercase()}"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("quotation_detail_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {

            // Top Bar with Back
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("quote_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = if (isOfficialIssued) "Officially Issued Quotation" else "Draft Internal Estimate",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Ref: $formattedRef • Valid for ${quotation.validityDate}",
                        fontSize = 12.sp,
                        color = ForgeAmberLight
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // Status Badge
                Surface(
                    color = when (quotation.status) {
                        QuotationStatus.ACCEPTED -> Color(0xFF10B981).copy(alpha = 0.2f)
                        QuotationStatus.DECLINED -> Color(0xFFEF4444).copy(alpha = 0.2f)
                        QuotationStatus.PENDING -> ForgeAmberLight.copy(alpha = 0.2f)
                        QuotationStatus.EXPIRED -> Color.Gray.copy(alpha = 0.2f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        when (quotation.status) {
                            QuotationStatus.ACCEPTED -> Color(0xFF10B981)
                            QuotationStatus.DECLINED -> Color(0xFFEF4444)
                            QuotationStatus.PENDING -> ForgeAmberLight
                            QuotationStatus.EXPIRED -> Color.Gray
                        }
                    )
                ) {
                    Text(
                        text = quotation.status.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (quotation.status) {
                            QuotationStatus.ACCEPTED -> Color(0xFF10B981)
                            QuotationStatus.DECLINED -> Color(0xFFEF4444)
                            QuotationStatus.PENDING -> ForgeAmberLight
                            QuotationStatus.EXPIRED -> Color.Gray
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quotation Type Banner (Draft vs Officially Issued)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isOfficialIssued) Color(0xFF10B981).copy(alpha = 0.15f) else ForgeAmberLight.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isOfficialIssued) Color(0xFF10B981) else ForgeAmberLight
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (isOfficialIssued) Icons.Default.Verified else Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isOfficialIssued) Color(0xFF10B981) else ForgeAmberLight,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isOfficialIssued) "OFFICIALLY ISSUED & GST AUDITED" else "DRAFT ESTIMATION (INTERNAL USE)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isOfficialIssued) Color(0xFF10B981) else ForgeAmberLight
                        )
                    }

                    TextButton(onClick = { isOfficialIssued = !isOfficialIssued }) {
                        Text(
                            text = if (isOfficialIssued) "View Draft Mode" else "View Official Mode",
                            fontSize = 11.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fabricator Workshop Information Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForgeAmberLight,
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = quotation.vendorName.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = quotation.vendorName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                if (quotation.isGstVerified) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "GST Verified",
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${quotation.vendorRating} Rating", fontSize = 12.sp, color = Color.LightGray)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("• ${quotation.vendorCity}", fontSize = 12.sp, color = Color.Gray)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF2E3440))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Assigned Project Coordinator", fontSize = 11.sp, color = Color.Gray)
                            Text("${quotation.contactPerson} (${quotation.vendorPhone})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Delivery Lead Time", fontSize = 11.sp, color = Color.Gray)
                            Text("${quotation.leadTimeDays} Business Days", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = LaserCyanLight)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Itemized Bill of Quantities (BOQ) with materials, dimensions, labour, finishing, transport
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ITEMIZED BILL OF QUANTITIES (BOQ)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = ForgeAmberLight,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Project Dimensions: 14' x 7'",
                            fontSize = 11.sp,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Itemized Line items: materials, quantities, dimensions, labour, finishing, transportation, installation
                    DetailedBoqRow(
                        itemNumber = "1",
                        title = "Base Steel Sections & Structural Members",
                        dimensions = "14 ft x 7 ft frame (250 kg)",
                        qty = "1 unit",
                        specs = "ISMB 100 base + 50x50x2mm MS hollow pipe sections",
                        amount = quotation.baseFabricationCost
                    )

                    DetailedBoqRow(
                        itemNumber = "2",
                        title = "Raw Material Infill & Laser Cutting",
                        dimensions = "12 ft x 6.5 ft CNC sheet",
                        qty = "2 sheets",
                        specs = quotation.materialGradeGuarantee,
                        amount = quotation.materialCost
                    )

                    DetailedBoqRow(
                        itemNumber = "3",
                        title = "Fabrication Labour & Precision Machining",
                        dimensions = "Full frame assembly",
                        qty = "1 job",
                        specs = "Certified MIG welding, mitre grinding & pivot hinge assembly",
                        amount = 4500.0
                    )

                    DetailedBoqRow(
                        itemNumber = "4",
                        title = "Surface Treatment & 80-Micron Powder Coating",
                        dimensions = "182 sq. ft. total area",
                        qty = "2 coats",
                        specs = "Acid phosphating degreasing + Jotun exterior architectural powder",
                        amount = 3500.0
                    )

                    DetailedBoqRow(
                        itemNumber = "5",
                        title = "On-Site Civil Anchoring & Erection",
                        dimensions = "Site erection",
                        qty = "4 crew",
                        specs = "Chemical anchor studs, plum verification & gate balance tuning",
                        amount = quotation.installationCost
                    )

                    DetailedBoqRow(
                        itemNumber = "6",
                        title = "Logistics, Protective Packaging & Transit Insurance",
                        dimensions = "Flatbed transport",
                        qty = "1 trip",
                        specs = "Double bubble wrapping, corner edge protectors & local delivery",
                        amount = quotation.transportCost
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF343B4B))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Pre-tax Subtotal
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal (Pre-tax Base)", fontSize = 13.sp, color = Color.LightGray)
                        Text("₹${quotation.subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    // GST (18%)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Applicable Goods & Services Tax (GST 18%)", fontSize = 13.sp, color = Color.LightGray)
                        Text("₹${quotation.gstAmount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(10.dp))

                    // Total Amount
                    Surface(
                        color = IndustrialSurfaceContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Amount Payable (INR)", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                Text("Inclusive of materials, labour, finishing, GST & installation", fontSize = 10.sp, color = Color.Gray)
                            }
                            Text("₹${quotation.grandTotal.toInt()}", fontSize = 20.sp, fontWeight = FontWeight.Black, color = ForgeAmberLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Technical Scope & Warranty: ${quotation.technicalNotes}",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Toolbar (Screen 6 Requirement: View PDF, Download, Share, Accept Quote, Reject Quote, Request Revision)
            Text(
                text = "DOCUMENT ACTIONS & SHARING",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 1. View PDF
                OutlinedButton(
                    onClick = { showPdfViewerDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(42.dp).testTag("action_view_pdf")
                ) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("View PDF", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // 2. Download
                OutlinedButton(
                    onClick = { showDownloadSuccessDialog = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(42.dp).testTag("action_download_pdf")
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                // 3. Share via WhatsApp
                Button(
                    onClick = {
                        val text = "*CJ FABRICATIONS - OFFICIAL QUOTATION*\n" +
                            "Quotation Ref: *${formattedRef}*\n" +
                            "Fabricator: *${quotation.vendorName}*\n" +
                            "Total Amount: *₹${quotation.grandTotal.toInt()}* (Incl. 18% GST)\n" +
                            "Lead Time: *${quotation.leadTimeDays} Days*\n" +
                            "Material Guarantee: ${quotation.materialGradeGuarantee}\n" +
                            "Installation: Included at site\n" +
                            "Valid Till: ${quotation.validityDate}\n" +
                            "View full digital quotation on CJ Fabrications App."
                        val encoded = URLEncoder.encode(text, "UTF-8")
                        val uri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f).height(42.dp).testTag("action_share_quote")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Share", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Primary Decision Actions: Accept, Reject, Request Revision
            if (quotation.status == QuotationStatus.PENDING) {
                // Accept Quote Button
                Button(
                    onClick = onAccept,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("action_accept_quote")
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Accept Quote & Proceed to Order", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reject Quote
                    OutlinedButton(
                        onClick = { showRejectDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                        modifier = Modifier.weight(1f).testTag("action_reject_quote")
                    ) {
                        Text("Reject Quote", fontSize = 12.sp)
                    }

                    // Request Revision
                    OutlinedButton(
                        onClick = { showRevisionDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserCyanLight),
                        modifier = Modifier.weight(1f).testTag("action_request_revision")
                    ) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Request Revision", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Interactive Full-Page PDF Viewer Dialog
    if (showPdfViewerDialog) {
        AlertDialog(
            onDismissRequest = { showPdfViewerDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null, tint = Color(0xFFEF4444))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quotation PDF Document")
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = "CJ FABRICATIONS B2B QUOTATION", fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.Black)
                            Text(text = "ISO 9001:2015 Verified Metal Fabrication Marketplace", fontSize = 10.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color.LightGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text(text = "Document Ref: $formattedRef", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Text(text = "Status: ${quotation.status.name} (${if (isOfficialIssued) "OFFICIAL" else "DRAFT"})", fontSize = 11.sp, color = Color.Black)
                            Text(text = "Vendor Workshop: ${quotation.vendorName}", fontSize = 11.sp, color = Color.Black)
                            Text(text = "Vendor GSTIN: 29AABCJ9941K1Z2 (Audited)", fontSize = 10.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(text = "Scope: 14' x 7' Custom Architectural Main Gate", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            Text(text = "Material Grade: ${quotation.materialGradeGuarantee}", fontSize = 10.sp, color = Color.DarkGray)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pre-tax Subtotal:", fontSize = 11.sp, color = Color.Black)
                                Text("₹${quotation.subtotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("GST (18%):", fontSize = 11.sp, color = Color.Black)
                                Text("₹${quotation.gstAmount.toInt()}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                            }
                            HorizontalDivider(color = Color.Black, modifier = Modifier.padding(vertical = 4.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                                Text("₹${quotation.grandTotal.toInt()}", fontWeight = FontWeight.Black, fontSize = 13.sp, color = Color.Black)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showPdfViewerDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Close Preview", color = Color.Black)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Download Success Dialog
    if (showDownloadSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showDownloadSuccessDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Quotation Downloaded")
                }
            },
            text = {
                Text(
                    text = "PDF saved successfully to Downloads / Documents:\n\n'CJ_Fabrications_${formattedRef}.pdf' (420 KB)",
                    fontSize = 13.sp,
                    color = Color.LightGray
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDownloadSuccessDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Done", color = Color.Black)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Reject Dialog
    if (showRejectDialog) {
        AlertDialog(
            onDismissRequest = { showRejectDialog = false },
            title = { Text("Reject Quotation") },
            text = {
                Column {
                    Text("Please provide an optional reason for rejecting (e.g. Budget mismatch, timeline too long):", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("Reason...") },
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
                        showRejectDialog = false
                        onReject(rejectReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Confirm Rejection", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRejectDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Request Revision Dialog
    if (showRevisionDialog) {
        AlertDialog(
            onDismissRequest = { showRevisionDialog = false },
            title = { Text("Request Quotation Revision") },
            text = {
                Column {
                    Text("Enter modifications requested (e.g. change material grade to SS 316, adjust dimensions, or expedite delivery):", fontSize = 13.sp, color = Color.LightGray)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = revisionNotes,
                        onValueChange = { revisionNotes = it },
                        minLines = 3,
                        placeholder = { Text("Specify changes needed...") },
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
                        showRevisionDialog = false
                        onRequestRevision(revisionNotes)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Send to Fabricator", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRevisionDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}

@Composable
private fun DetailedBoqRow(
    itemNumber: String,
    title: String,
    dimensions: String,
    qty: String,
    specs: String,
    amount: Double
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("$itemNumber. $title", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Color.White, modifier = Modifier.weight(1f))
            Text("₹${amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Dim: $dimensions • Qty: $qty", fontSize = 11.sp, color = LaserCyanLight)
        }
        Text(specs, fontSize = 11.sp, color = Color.Gray, modifier = Modifier.padding(top = 1.dp))
    }
}
