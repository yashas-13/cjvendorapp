package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.AddAPhoto
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.DesignServices
import androidx.compose.material.icons.outlined.Engineering
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
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
import com.example.data.model.FabricationOrder
import com.example.data.model.NotificationItem
import com.example.data.model.Quotation
import com.example.data.model.RfqRequest
import com.example.ui.components.CjTopBar
import com.example.ui.screens.AdminPortalScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CustomerProfileScreen
import com.example.ui.screens.CustomerTrackingPortalScreen
import com.example.ui.screens.DesignDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MyRequirementScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrderTrackingScreen
import com.example.ui.screens.PhotoDesignUploadScreen
import com.example.ui.screens.QuotationDetailScreen
import com.example.ui.screens.RfqDetailScreen
import com.example.ui.screens.ServiceBookingScreen
import com.example.ui.screens.VendorPortalScreen
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.viewmodel.FabricationViewModel

enum class NavigationTab(val label: String) {
    HOME("Home"),
    CATALOG("Designs"),
    UPLOAD("Upload"),
    BOOK_SERVICE("Book Visit"),
    REQUIREMENT("Requirement"),
    PROFILE("Profile"),
    PORTAL("Staff Portal")
}

/**
 * MainScreen — Orchestrates the complete 10-screen architecture for CJ Fabrications:
 * Screen 1: AuthScreen (Login, Registration, OTP, Google One-Tap)
 * Screen 2: HomeScreen (Home Dashboard with search, category cards, and 4 quick actions)
 * Screen 3: CatalogScreen & DesignDetailScreen (Product Catalogue & Customisation options)
 * Screen 4: PhotoDesignUploadScreen (Photo & CAD Design Upload with gallery previews)
 * Screen 5: ServiceBookingScreen (Service Booking with date/time, address, and reference generation)
 * Screen 6: QuotationDetailScreen (Itemized BOQ Quotation view, WhatsApp export, Accept/Reject)
 * Screen 7: OrderTrackingScreen (9-stage visual milestone timeline with factory photos)
 * Screen 8: NotificationsScreen (Date-grouped notifications centre)
 * Screen 9: CustomerProfileScreen (Customer identity, saved site addresses, and activity hub)
 * Screen 10: AdminPortalScreen (Role-protected staff dashboard) & VendorPortalScreen (Fabricator workspace)
 */
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
    val serviceBookings by viewModel.serviceBookings.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadNotificationsCount by viewModel.unreadNotificationCount.collectAsState()
    val customerProfile by viewModel.customerProfile.collectAsState()

    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val materialFilter by viewModel.materialFilter.collectAsState()
    val filteredDesigns by viewModel.filteredDesigns.collectAsState()
    val trackedRfq by viewModel.currentTrackedRfq.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()
    val authState by viewModel.authState.collectAsState()

    // Navigation state
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }

    // Modal / Detail Sub-screens
    var showAuthModal by remember { mutableStateOf(false) }
    var showNotificationsScreen by remember { mutableStateOf(false) }
    var selectedDesignDetail by remember { mutableStateOf<DesignItem?>(null) }
    var selectedQuotationDetail by remember { mutableStateOf<Quotation?>(null) }
    var selectedOrderForTracking by remember { mutableStateOf<FabricationOrder?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearUserMessage()
        }
    }

    // Predictive back handler across modal and detail screens
    BackHandler(
        enabled = showAuthModal ||
            showNotificationsScreen ||
            selectedQuotationDetail != null ||
            selectedOrderForTracking != null ||
            selectedDesignDetail != null ||
            trackedRfq != null ||
            currentTab != NavigationTab.HOME
    ) {
        when {
            showAuthModal -> showAuthModal = false
            showNotificationsScreen -> showNotificationsScreen = false
            selectedQuotationDetail != null -> selectedQuotationDetail = null
            selectedOrderForTracking != null -> selectedOrderForTracking = null
            selectedDesignDetail != null -> selectedDesignDetail = null
            trackedRfq != null -> viewModel.setTrackedRfq(null)
            else -> currentTab = NavigationTab.HOME
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
                    currentTab = NavigationTab.REQUIREMENT
                    selectedDesignDetail = null
                    selectedQuotationDetail = null
                    selectedOrderForTracking = null
                    showNotificationsScreen = false
                    showAuthModal = false
                    viewModel.setTrackedRfq(null)
                },
                authState = authState,
                onSignInClick = { showAuthModal = true },
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
                // 1. Home (Screen 2)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.HOME &&
                        selectedDesignDetail == null &&
                        trackedRfq == null &&
                        selectedQuotationDetail == null &&
                        selectedOrderForTracking == null &&
                        !showNotificationsScreen &&
                        !showAuthModal,
                    onClick = {
                        currentTab = NavigationTab.HOME
                        selectedDesignDetail = null
                        selectedQuotationDetail = null
                        selectedOrderForTracking = null
                        showNotificationsScreen = false
                        showAuthModal = false
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                            contentDescription = "Home"
                        )
                    },
                    label = { Text("Home", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // 2. Designs (Screen 3)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.CATALOG && selectedDesignDetail == null,
                    onClick = {
                        currentTab = NavigationTab.CATALOG
                        selectedDesignDetail = null
                        selectedQuotationDetail = null
                        selectedOrderForTracking = null
                        showNotificationsScreen = false
                        showAuthModal = false
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.CATALOG) Icons.Filled.DesignServices else Icons.Outlined.DesignServices,
                            contentDescription = "Designs"
                        )
                    },
                    label = { Text("Designs", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // 3. Upload (Screen 4)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.UPLOAD,
                    onClick = {
                        currentTab = NavigationTab.UPLOAD
                        selectedDesignDetail = null
                        selectedQuotationDetail = null
                        selectedOrderForTracking = null
                        showNotificationsScreen = false
                        showAuthModal = false
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.UPLOAD) Icons.Filled.AddAPhoto else Icons.Outlined.AddAPhoto,
                            contentDescription = "Upload"
                        )
                    },
                    label = { Text("Upload", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // 4. Book Visit (Screen 5)
                NavigationBarItem(
                    selected = currentTab == NavigationTab.BOOK_SERVICE,
                    onClick = {
                        currentTab = NavigationTab.BOOK_SERVICE
                        selectedDesignDetail = null
                        selectedQuotationDetail = null
                        selectedOrderForTracking = null
                        showNotificationsScreen = false
                        showAuthModal = false
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        Icon(
                            imageVector = if (currentTab == NavigationTab.BOOK_SERVICE) Icons.Filled.Engineering else Icons.Outlined.Engineering,
                            contentDescription = "Book Visit"
                        )
                    },
                    label = { Text("Book Visit", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // 5. Requirement
                NavigationBarItem(
                    selected = currentTab == NavigationTab.REQUIREMENT,
                    onClick = {
                        currentTab = NavigationTab.REQUIREMENT
                        selectedDesignDetail = null
                        selectedQuotationDetail = null
                        selectedOrderForTracking = null
                        showNotificationsScreen = false
                        showAuthModal = false
                        viewModel.setTrackedRfq(null)
                    },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (requirementDraft.isNotEmpty()) {
                                    Badge(containerColor = ForgeAmberLight, contentColor = Color.Black) {
                                        Text("${requirementDraft.size}", fontSize = 9.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (currentTab == NavigationTab.REQUIREMENT) Icons.Filled.Assignment else Icons.Outlined.Assignment,
                                contentDescription = "Requirement"
                            )
                        }
                    },
                    label = { Text("RFQ", fontSize = 10.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.Black,
                        selectedTextColor = ForgeAmberLight,
                        indicatorColor = ForgeAmberLight,
                        unselectedIconColor = Color.LightGray,
                        unselectedTextColor = Color.LightGray
                    )
                )

                // 6. Profile / Staff Portal (Screen 9 / Screen 10)
                if (currentRole == AppRole.CUSTOMER) {
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.PROFILE,
                        onClick = {
                            currentTab = NavigationTab.PROFILE
                            selectedDesignDetail = null
                            selectedQuotationDetail = null
                            selectedOrderForTracking = null
                            showNotificationsScreen = false
                            showAuthModal = false
                            viewModel.setTrackedRfq(null)
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentTab == NavigationTab.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ForgeAmberLight,
                            indicatorColor = ForgeAmberLight,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        )
                    )
                } else {
                    val portalLabel = if (currentRole == AppRole.ADMIN) "Admin" else "Vendor"
                    NavigationBarItem(
                        selected = currentTab == NavigationTab.PORTAL,
                        onClick = {
                            currentTab = NavigationTab.PORTAL
                            selectedDesignDetail = null
                            selectedQuotationDetail = null
                            selectedOrderForTracking = null
                            showNotificationsScreen = false
                            showAuthModal = false
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRole == AppRole.ADMIN) Icons.Filled.Shield else Icons.Filled.Build,
                                contentDescription = portalLabel
                            )
                        },
                        label = { Text(portalLabel, fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.Black,
                            selectedTextColor = ForgeAmberLight,
                            indicatorColor = ForgeAmberLight,
                            unselectedIconColor = Color.LightGray,
                            unselectedTextColor = Color.LightGray
                        )
                    )
                }
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
            // Priority Modal Sub-screens:
            if (showAuthModal) {
                // Screen 1: Customer Registration, Login & Auth
                AuthScreen(
                    authState = authState,
                    onSignInWithGoogle = { viewModel.signInWithGoogle() },
                    onSignOut = { viewModel.signOut() },
                    onClose = { showAuthModal = false }
                )
            } else if (showNotificationsScreen) {
                // Screen 8: Notifications Centre
                NotificationsScreen(
                    notifications = notifications,
                    onNotificationClick = { item ->
                        viewModel.markNotificationAsRead(item.id)
                        showNotificationsScreen = false
                        if (item.referenceId != null) {
                            val matchedOrder = orders.find { it.id == item.referenceId }
                            val matchedQuote = quotationsMap.values.flatten().find { it.id == item.referenceId }
                            if (matchedOrder != null) {
                                selectedOrderForTracking = matchedOrder
                            } else if (matchedQuote != null) {
                                selectedQuotationDetail = matchedQuote
                            } else {
                                currentTab = NavigationTab.PROFILE
                            }
                        }
                    },
                    onMarkAllRead = { viewModel.markAllNotificationsAsRead() },
                    onBack = { showNotificationsScreen = false }
                )
            } else if (selectedQuotationDetail != null) {
                // Screen 6: Quotation Detail Screen
                QuotationDetailScreen(
                    quotation = selectedQuotationDetail!!,
                    onAccept = {
                        viewModel.acceptQuote(selectedQuotationDetail!!.rfqId, selectedQuotationDetail!!.id) { newOrder ->
                            selectedQuotationDetail = null
                            selectedOrderForTracking = newOrder
                        }
                    },
                    onReject = { reason ->
                        viewModel.rejectQuotation(selectedQuotationDetail!!.rfqId, selectedQuotationDetail!!.id, reason)
                        selectedQuotationDetail = null
                    },
                    onRequestRevision = { notes ->
                        viewModel.requestQuotationRevision(selectedQuotationDetail!!.rfqId, selectedQuotationDetail!!.id, notes)
                        selectedQuotationDetail = null
                    },
                    onBack = { selectedQuotationDetail = null }
                )
            } else if (selectedOrderForTracking != null) {
                // Screen 7: Order Tracking & Milestones Screen
                OrderTrackingScreen(
                    order = selectedOrderForTracking!!,
                    onContactCoordinator = {
                        // Handled within OrderTrackingScreen
                    }
                )
            } else if (selectedDesignDetail != null) {
                // Screen 3 Detail: Design Detail Modal
                DesignDetailScreen(
                    design = selectedDesignDetail!!,
                    onBack = { selectedDesignDetail = null },
                    onAddToRequirement = { item ->
                        viewModel.addToRequirement(item)
                        selectedDesignDetail = null
                        currentTab = NavigationTab.REQUIREMENT
                    },
                    onOpenCustomForm = {
                        selectedDesignDetail = null
                        currentTab = NavigationTab.UPLOAD
                    }
                )
            } else if (trackedRfq != null) {
                // RFQ Tracking & Quotation Comparison
                val quotesForTracked = quotationsMap[trackedRfq!!.id] ?: emptyList()
                val orderForTracked = orders.find { it.rfqId == trackedRfq!!.id }
                RfqDetailScreen(
                    rfq = trackedRfq!!,
                    quotations = quotesForTracked,
                    associatedOrder = orderForTracked,
                    onAcceptQuote = { quoteId ->
                        viewModel.acceptQuote(trackedRfq!!.id, quoteId) { order ->
                            selectedOrderForTracking = order
                            viewModel.setTrackedRfq(null)
                        }
                    },
                    onBack = { viewModel.setTrackedRfq(null) },
                    whatsAppUrlGenerator = { viewModel.getWhatsAppUrl(it) },
                    whatsAppMessageFormatter = { (viewModel.getWhatsAppUrl(it)) }
                )
            } else {
                // Primary Tabs:
                when (currentTab) {
                    NavigationTab.HOME -> {
                        // Screen 2: Home Dashboard
                        HomeScreen(
                            categories = viewModel.categories,
                            onCategoryClick = { catId ->
                                viewModel.selectCategory(catId)
                                currentTab = NavigationTab.CATALOG
                            },
                            onNavigateToCatalog = { currentTab = NavigationTab.CATALOG },
                            onNavigateToCustomRfq = { currentTab = NavigationTab.UPLOAD },
                            onNavigateToBookService = { currentTab = NavigationTab.BOOK_SERVICE },
                            onNavigateToUploadPhotos = { currentTab = NavigationTab.UPLOAD },
                            onNavigateToTrackOrders = {
                                val firstOrd = orders.firstOrNull()
                                if (firstOrd != null) {
                                    selectedOrderForTracking = firstOrd
                                } else {
                                    currentTab = NavigationTab.PROFILE
                                }
                            },
                            onNavigateToNotifications = { showNotificationsScreen = true },
                            unreadNotificationsCount = unreadNotificationsCount,
                            onTrackRfq = { ref ->
                                val found = viewModel.trackRfqByReference(ref)
                                if (!found) {
                                    val matchedOrder = orders.find { it.orderNumber.equals(ref, ignoreCase = true) }
                                    if (matchedOrder != null) {
                                        selectedOrderForTracking = matchedOrder
                                    } else {
                                        val firstRfq = rfqList.firstOrNull()
                                        if (firstRfq != null) {
                                            viewModel.setTrackedRfq(firstRfq)
                                        }
                                    }
                                }
                            }
                        )
                    }

                    NavigationTab.CATALOG -> {
                        // Screen 3: Product Catalogue
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

                    NavigationTab.UPLOAD -> {
                        // Screen 4: Photo & Design Upload
                        PhotoDesignUploadScreen(
                            onDesignSubmitted = { item ->
                                viewModel.addToRequirement(item)
                                currentTab = NavigationTab.REQUIREMENT
                            }
                        )
                    }

                    NavigationTab.BOOK_SERVICE -> {
                        // Screen 5: Service Booking
                        ServiceBookingScreen(
                            customerProfile = customerProfile,
                            onBookingSubmitted = { st, name, ph, em, addr, lm, ct, pin, dt, slot, details, photos ->
                                viewModel.createServiceBooking(
                                    serviceType = st,
                                    customerName = name,
                                    customerPhone = ph,
                                    customerEmail = em,
                                    siteAddress = addr,
                                    landmark = lm,
                                    city = ct,
                                    pincode = pin,
                                    preferredDate = dt,
                                    preferredTimeSlot = slot,
                                    projectDetails = details,
                                    attachedPhotoCount = photos
                                ) {
                                    // Booking handled within screen confirmation modal
                                }
                            }
                        )
                    }

                    NavigationTab.REQUIREMENT -> {
                        // My Requirement (RFQ Builder & Submission)
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
                                        viewModel.setTrackedRfq(createdRfq)
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

                    NavigationTab.PROFILE -> {
                        // Screen 9: Customer Profile & Settings
                        val allQuotes = quotationsMap.values.flatten()
                        CustomerProfileScreen(
                            profile = customerProfile,
                            authState = authState,
                            bookings = serviceBookings,
                            quotations = allQuotes,
                            orders = orders,
                            onUpdateProfile = { viewModel.updateCustomerProfile(it) },
                            onAddAddress = { viewModel.addSavedAddress(it) },
                            onDeleteAddress = { viewModel.deleteSavedAddress(it) },
                            onSelectBooking = { bk ->
                                currentTab = NavigationTab.BOOK_SERVICE
                            },
                            onSelectQuotation = { qt ->
                                selectedQuotationDetail = qt
                            },
                            onSelectOrder = { ord ->
                                selectedOrderForTracking = ord
                            },
                            onOpenAuthModal = { showAuthModal = true },
                            onSignOut = { viewModel.signOut() }
                        )
                    }

                    NavigationTab.PORTAL -> {
                        // Screen 10: Admin Dashboard & Portal or Vendor Portal
                        when (currentRole) {
                            AppRole.CUSTOMER -> {
                                CustomerTrackingPortalScreen(
                                    rfqs = rfqList,
                                    quotationsMap = quotationsMap,
                                    orders = orders,
                                    onSelectRfq = { rfq -> viewModel.setTrackedRfq(rfq) },
                                    onTrackByRef = { ref ->
                                        val found = viewModel.trackRfqByReference(ref)
                                        if (!found) {
                                            val matched = rfqList.find { it.referenceNumber.equals(ref, ignoreCase = true) }
                                            if (matched != null) viewModel.setTrackedRfq(matched)
                                        }
                                    },
                                    onNavigateToCatalog = { currentTab = NavigationTab.CATALOG },
                                    onNavigateToCustomRfq = { currentTab = NavigationTab.UPLOAD },
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
                }
            }
        }
    }
}
