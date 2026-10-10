package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.DataBackupScreen
import com.example.ui.screens.DeveloperInfoDialog
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.PosSaleScreen
import com.example.ui.screens.ReceiptPreviewDialog
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.BackgroundMeshGradient
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.GlassPosTheme
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.Localization
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

enum class ScreenTab {
    HOME,
    POS,
    INVENTORY,
    HISTORY,
    REPORTS,
    SETTINGS,
    BACKUP
}

class MainActivity : ComponentActivity() {

    private val viewModel: PosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GlassPosTheme {
                GlassPosApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun GlassPosApp(viewModel: PosViewModel) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(ScreenTab.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val activeReceipt by viewModel.activeReceiptForPreview.collectAsStateWithLifecycle()
    val showDevModal by viewModel.showDeveloperModal.collectAsStateWithLifecycle()

    val lang = settings.language

    // Listen for toast/snackbar events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collectLatest { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(message)
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSpace)
    ) {
        // Ambient background glow effects matching Elegant Dark
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Top-left emerald glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(EmeraldNeon.copy(alpha = 0.08f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(0f, 0f),
                    radius = size.width * 0.85f
                )
            )
            // Bottom-right cyan/blue glow
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(CyanNeon.copy(alpha = 0.08f), Color.Transparent),
                    center = androidx.compose.ui.geometry.Offset(size.width, size.height),
                    radius = size.width * 0.85f
                )
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                GlassBottomNavigationBar(
                    currentTab = currentTab,
                    cartCount = cart.sumOf { it.quantity },
                    lang = lang,
                    onSelectTab = { currentTab = it }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "ScreenTransition"
                ) { tab ->
                    when (tab) {
                        ScreenTab.HOME -> {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateToPos = { currentTab = ScreenTab.POS },
                                onNavigateToInventory = { currentTab = ScreenTab.INVENTORY },
                                onNavigateToReports = { currentTab = ScreenTab.REPORTS },
                                onNavigateToBackup = { currentTab = ScreenTab.BACKUP },
                                onNavigateToSettings = { currentTab = ScreenTab.SETTINGS },
                                onSelectReceipt = { viewModel.activeReceiptForPreview.value = it }
                            )
                        }
                        ScreenTab.POS -> {
                            PosSaleScreen(
                                viewModel = viewModel,
                                onReceiptGenerated = { saleWithItems ->
                                    viewModel.activeReceiptForPreview.value = saleWithItems
                                }
                            )
                        }
                        ScreenTab.INVENTORY -> {
                            InventoryScreen(viewModel = viewModel)
                        }
                        ScreenTab.HISTORY -> {
                            HistoryScreen(
                                viewModel = viewModel,
                                onSelectReceipt = { viewModel.activeReceiptForPreview.value = it }
                            )
                        }
                        ScreenTab.REPORTS -> {
                            ReportsScreen(viewModel = viewModel)
                        }
                        ScreenTab.SETTINGS -> {
                            SettingsScreen(viewModel = viewModel)
                        }
                        ScreenTab.BACKUP -> {
                            DataBackupScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }

        // Global Receipt Preview Modal
        if (activeReceipt != null) {
            ReceiptPreviewDialog(
                saleWithItems = activeReceipt!!,
                settings = settings,
                onDismiss = { viewModel.activeReceiptForPreview.value = null }
            )
        }

        // Developer Info Modal
        if (showDevModal) {
            DeveloperInfoDialog(
                lang = lang,
                onDismiss = { viewModel.showDeveloperModal.value = false }
            )
        }
    }
}

@Composable
fun GlassBottomNavigationBar(
    currentTab: ScreenTab,
    cartCount: Int,
    lang: String,
    onSelectTab: (ScreenTab) -> Unit
) {
    val items = listOf(
        Triple(ScreenTab.HOME, Icons.Default.Home, Localization.get("nav_dashboard", lang)),
        Triple(ScreenTab.POS, Icons.Default.PointOfSale, Localization.get("nav_pos", lang)),
        Triple(ScreenTab.INVENTORY, Icons.Default.Inventory2, Localization.get("nav_inventory", lang)),
        Triple(ScreenTab.HISTORY, Icons.Default.History, Localization.get("nav_history", lang)),
        Triple(ScreenTab.REPORTS, Icons.Default.Analytics, Localization.get("nav_reports", lang)),
        Triple(ScreenTab.SETTINGS, Icons.Default.Settings, Localization.get("nav_settings", lang))
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 16.dp,
                    shape = RoundedCornerShape(26.dp),
                    ambientColor = Color.Black.copy(alpha = 0.5f),
                    spotColor = Color.Black.copy(alpha = 0.5f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(Color(0xFF0E1015).copy(alpha = 0.94f))
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color.White.copy(alpha = 0.12f),
                            Color.White.copy(alpha = 0.04f)
                        )
                    ),
                    shape = RoundedCornerShape(26.dp)
                )
                .padding(vertical = 6.dp, horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { (tab, icon, label) ->
                    val isSelected = (currentTab == tab)
                    val activeColor = EmeraldLight
                    val activeBackground = EmeraldNeon.copy(alpha = 0.12f)

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) activeBackground else Color.Transparent)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = ripple(bounded = true, color = activeColor.copy(alpha = 0.2f)),
                                onClick = { onSelectTab(tab) }
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                            .testTag("nav_tab_${tab.name.lowercase()}"),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (tab == ScreenTab.POS && cartCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = EmeraldNeon,
                                        contentColor = Color(0xFF0A0A0B)
                                    ) {
                                        Text("$cartCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) activeColor else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        } else {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) activeColor else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = label,
                            color = if (isSelected) activeColor else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}
