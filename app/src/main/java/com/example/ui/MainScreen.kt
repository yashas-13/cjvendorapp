package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AppRole
import com.example.data.model.DesignItem
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.ui.components.CjTopBar
import com.example.ui.screens.AdminPortalScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CustomerTrackingPortalScreen
import com.example.ui.screens.CustomRfqScreen
import com.example.ui.screens.DesignDetailScreen
import com.example.ui.screens.HelpPolicyScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyRequirementScreen
import com.example.ui.screens.RfqDetailScreen
import com.example.ui.screens.VendorPortalScreen
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.viewmodel.FabricationViewModel

enum class NavigationTab(val label: String) {
    HOME("Home"),
    CATALOG("Designs"),
    CUSTOM_RFQ("Custom RFQ"),
    MY_REQUIREMENT("Requirement"),
    PORTAL("Portal"),
    HELP("Help & Terms")
}

@Composable
fun MainScreen(
    viewModel: FabricationViewModel = viewModel(),
    modifier: Modifier = Modifier
) {
    val currentRole by viewModel.currentRole.collectAsState()
    val requirementDraft by viewModel.requirementDraft.collectAsState()
    val rfqList by viewModel.rfqList.collectAsState()
    val quotationsMap by viewModel.quotationsMap.collectAsState()
    val orders by viewModel.orders.collectAsState()
    val vendors by viewModel.vendors.collectAsState()

    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val materialFilter by viewModel.materialFilter.collectAsState()
    val filteredDesigns by viewModel.filteredDesigns.collectAsState()
    val trackedRfq by viewModel.currentTrackedRfq.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val authState by viewModel.authState.collectAsState()

    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    var selectedDesignDetail by remember { mutableStateOf<DesignItem?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Back button handling
    BackHandler(enabled = selectedDesignDetail != null || trackedRfq != null || currentTab != NavigationTab.HOME) {
        if (selectedDesignDetail != null) {
            selectedDesignDetail = null
        } else if (trackedRfq != null) {
            viewModel.setTrackedRfq(null)
        } else {
            currentTab = NavigationTab.HOME
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CjTopBar(
                currentRole = currentRole,
                requirementCount = requirementDraft.size,
                onRoleSelected = { role ->
                    viewModel.setRole(role)
                    if (role != AppRole.CUSTOMER) {
                        currentTab = NavigationTab.PORTAL
                    }
                },
                onOpenRequirements = {
                    currentTab = NavigationTab.MY_REQUIREMENT
                    selectedDesignDetail = null
                    viewModel.setTrackedRfq(null)
                },
                authState = authState,
                onSignInClick = { viewModel.signInWithGoogle() },
                onSignOutClick = { viewModel.signOut() }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = IndustrialSurface,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .testTag("bottom_nav_bar")
            ) {
                // Home
                NavigationBarItem(
                    selected = currentTab == NavigationTab.HOME && selectedDesignDetail == null && trackedRfq == null,
                    onClick = {
                        currentTab = NavigationTab.HOME
                        selectedDesignDetail = null
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // Designs Catalog
                NavigationBarItem(
                    selected = currentTab == NavigationTab.CATALOG,
                    onClick = {
                        currentTab = NavigationTab.CATALOG
                        selectedDesignDetail = null
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.CATALOG) Icons.Filled.DesignServices else Icons.Outlined.DesignServices,
                            contentDescription = "Designs"
                        )
                    },
                    label = { Text("Designs", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // Custom RFQ Form
                NavigationBarItem(
                    selected = currentTab == NavigationTab.CUSTOM_RFQ,
                    onClick = {
                        currentTab = NavigationTab.CUSTOM_RFQ
                        selectedDesignDetail = null
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.CUSTOM_RFQ) Icons.Filled.Build else Icons.Outlined.Build,
                            contentDescription = "Custom RFQ"
                        )
                    },
                    label = { Text("Custom RFQ", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // My Requirement
                NavigationBarItem(
                    selected = currentTab == NavigationTab.MY_REQUIREMENT,
                    onClick = {
                        currentTab = NavigationTab.MY_REQUIREMENT
                        selectedDesignDetail = null
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (requirementDraft.isNotEmpty()) {
                                    Badge(containerColor = ForgeAmberLight, contentColor = Color.Black) {
                                        Text("${requirementDraft.size}", fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == NavigationTab.MY_REQUIREMENT) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                contentDescription = "Requirement"
                            )
                        }
                    },
                    label = { Text("Requirement", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // Portal (Vendor / Admin / Tracking)
                val portalLabel = when (currentRole) {
                    AppRole.CUSTOMER -> "Tracking"
                    AppRole.VENDOR -> "Vendor"
                    AppRole.ADMIN -> "Admin"
                }
                NavigationBarItem(
                    selected = currentTab == NavigationTab.PORTAL || trackedRfq != null,
                    onClick = {
                        currentTab = NavigationTab.PORTAL
                        selectedDesignDetail = null
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentRole == AppRole.ADMIN) Icons.Filled.Shield else Icons.Filled.Build,
                            contentDescription = portalLabel
                        )
                    },
                    label = { Text(portalLabel, fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Priority Sub-screens:
            if (selectedDesignDetail != null) {
                DesignDetailScreen(
                    design = selectedDesignDetail!!,
                    onBack = { selectedDesignDetail = null },
                    onAddToRequirement = { item ->
                        viewModel.addToRequirement(item)
                        selectedDesignDetail = null
                        currentTab = NavigationTab.MY_REQUIREMENT
                    },
                    onOpenCustomForm = {
                        selectedDesignDetail = null
                        currentTab = NavigationTab.CUSTOM_RFQ
                    }
                )
            } else if (trackedRfq != null) {
                val quotesForTracked = quotationsMap[trackedRfq!!.id] ?: emptyList()
                val orderForTracked = orders.find { it.rfqId == trackedRfq!!.id }
                RfqDetailScreen(
                    rfq = trackedRfq!!,
                    quotations = quotesForTracked,
                    associatedOrder = orderForTracked,
                    onAcceptQuote = { quoteId ->
                        viewModel.acceptQuote(trackedRfq!!.id, quoteId) { order ->
                            // updated
                        }
                    },
                    onBack = { viewModel.setTrackedRfq(null) },
                    whatsAppUrlGenerator = { viewModel.getWhatsAppUrl(it) },
                    whatsAppMessageFormatter = { (viewModel.getWhatsAppUrl(it)) }
                )
            } else {
                when (currentTab) {
                    NavigationTab.HOME -> {
                        HomeScreen(
                            categories = viewModel.categories,
                            onCategoryClick = { catId ->
                                viewModel.selectCategory(catId)
                                currentTab = NavigationTab.CATALOG
                            },
                            onNavigateToCatalog = { currentTab = NavigationTab.CATALOG },
                            onNavigateToCustomRfq = { currentTab = NavigationTab.CUSTOM_RFQ },
                            onTrackRfq = { ref ->
                                val found = viewModel.trackRfqByReference(ref)
                                if (!found) {
                                    // if not found, show first demo RFQ
                                    val firstRfq = rfqList.firstOrNull()
                                    if (firstRfq != null) {
                                        viewModel.setTrackedRfq(firstRfq)
                                    }
                                }
                            }
                        )
                    }

                    NavigationTab.CATALOG -> {
                        val syncStatus by viewModel.projectSyncStatus.collectAsState()
                        CatalogScreen(
                            categories = viewModel.categories,
                            designs = filteredDesigns,
                            selectedCategoryId = selectedCategoryId,
                            searchQuery = searchQuery,
                            selectedMaterial = materialFilter,
                            syncStatus = syncStatus,
                            onRefreshSync = { viewModel.triggerProjectSync() },
                            onCategorySelect = { viewModel.selectCategory(it) },
                            onSearchChange = { viewModel.setSearchQuery(it) },
                            onMaterialSelect = { viewModel.setMaterialFilter(it) },
                            onDesignClick = { id ->
                                selectedDesignDetail = viewModel.getDesignById(id)
                            },
                            onAddToRequirement = { item ->
                                viewModel.addToRequirement(item)
                            }
                        )
                    }

                    NavigationTab.CUSTOM_RFQ -> {
                        CustomRfqScreen(
                            onAddItemToRequirement = { item ->
                                viewModel.addToRequirement(item)
                                currentTab = NavigationTab.MY_REQUIREMENT
                            },
                            onDirectSubmit = { item ->
                                viewModel.addToRequirement(item)
                                currentTab = NavigationTab.MY_REQUIREMENT
                            }
                        )
                    }

                    NavigationTab.MY_REQUIREMENT -> {
                        MyRequirementScreen(
                            items = requirementDraft,
                            onRemoveItem = { viewModel.removeFromRequirement(it) },
                            onSubmitRfq = { name, phone, email, pType, addr, city, pin, target, budget ->
                                viewModel.submitRfq(
                                    customerName = name,
                                    customerPhone = phone,
                                    customerEmail = email,
                                    projectType = pType,
                                    siteAddress = addr,
                                    city = city,
                                    pincode = pin,
                                    targetCompletionDate = target,
                                    budgetRange = budget,
                                    onSuccess = { createdRfq ->
                                        // tracked
                                    }
                                )
                            },
                            onNavigateToCatalog = { currentTab = NavigationTab.CATALOG },
                            onNavigateToTrackRfq = { rfq -> viewModel.setTrackedRfq(rfq) },
                            whatsAppUrlGenerator = { viewModel.getWhatsAppUrl(it) },
                            whatsAppMessageFormatter = { rfq ->
                                "RFQ ${rfq.referenceNumber} for ${rfq.customerName} (${rfq.city}): ${rfq.items.size} custom fabrication items."
                            }
                        )
                    }

                    NavigationTab.PORTAL -> {
                        when (currentRole) {
                            AppRole.CUSTOMER -> {
                                CustomerTrackingPortalScreen(
                                    rfqs = rfqList,
                                    quotationsMap = quotationsMap,
                                    orders = orders,
                                    onSelectRfq = { rfq ->
                                        viewModel.setTrackedRfq(rfq)
                                    },
                                    onTrackByRef = { ref ->
                                        val found = viewModel.trackRfqByReference(ref)
                                        if (!found) {
                                            val matched = rfqList.find { it.referenceNumber.equals(ref, ignoreCase = true) }
                                            if (matched != null) {
                                                viewModel.setTrackedRfq(matched)
                                            }
                                        }
                                    },
                                    onNavigateToCatalog = { currentTab = NavigationTab.CATALOG },
                                    onNavigateToCustomRfq = { currentTab = NavigationTab.CUSTOM_RFQ },
                                    whatsAppUrlGenerator = { viewModel.getWhatsAppUrl(it) }
                                )
                            }

                            AppRole.VENDOR -> {
                                val selectedVendorId by viewModel.selectedVendorId.collectAsState()
                                val currentVendor = vendors.find { it.id == selectedVendorId } ?: vendors.first()
                                val assigned = rfqList.filter { it.assignedVendorIds.contains(currentVendor.id) }
                                val vendorOrders = orders.filter { it.vendorId == currentVendor.id }
                                VendorPortalScreen(
                                    currentVendor = currentVendor,
                                    allVendors = vendors,
                                    onSelectVendor = { viewModel.selectVendor(it) },
                                    assignedRfqs = assigned,
                                    vendorOrders = vendorOrders,
                                    onSubmitQuote = { rfqId, bf, mc, ic, tc, ld, valDate, guar, sv, nts ->
                                        viewModel.submitVendorQuotation(rfqId, currentVendor.id, bf, mc, ic, tc, ld, valDate, guar, sv, nts)
                                    },
                                    onAdvanceOrderMilestone = { orderId, step, rem ->
                                        viewModel.advanceOrderMilestone(orderId, step, rem)
                                    }
                                )
                            }

                            AppRole.ADMIN -> {
                                val auditLogs by viewModel.auditLogs.collectAsState()
                                AdminPortalScreen(
                                    rfqs = rfqList,
                                    vendors = vendors,
                                    orders = orders,
                                    auditLogs = auditLogs,
                                    onAssignVendor = { rfqId, vendorId ->
                                        viewModel.assignVendor(rfqId, vendorId)
                                    },
                                    onUpdateVendorVerificationStatus = { vid, st ->
                                        viewModel.updateVendorVerificationStatus(vid, st)
                                    }
                                )
                            }
                        }
                    }

                    NavigationTab.HELP -> {
                        HelpPolicyScreen()
                    }
                }
            }
        }
    }
}
