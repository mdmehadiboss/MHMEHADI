package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.SaleWithItems
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils
import com.example.util.Localization
import org.json.JSONObject

@Composable
fun HistoryScreen(
    viewModel: PosViewModel,
    onSelectReceipt: (SaleWithItems) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val salesHistory by viewModel.salesHistory.collectAsStateWithLifecycle()
    val searchQuery by viewModel.historySearchQuery.collectAsStateWithLifecycle()

    val lang = settings.language

    var saleToDelete by remember { mutableStateOf<SaleWithItems?>(null) }
    var showClearAllConfirmation by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = Localization.get("history_title", lang),
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${salesHistory.size} ${Localization.get("total_transactions", lang)}",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }

            if (salesHistory.isNotEmpty()) {
                GlassSecondaryButton(
                    text = Localization.get("clear_all_history", lang),
                    icon = Icons.Default.DeleteSweep,
                    textColor = RoseNeon,
                    borderColor = RoseNeon.copy(alpha = 0.3f),
                    onClick = { showClearAllConfirmation = true },
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar
        GlassTextField(
            value = searchQuery,
            onValueChange = { viewModel.historySearchQuery.value = it },
            placeholder = Localization.get("search_history", lang),
            leadingIcon = Icons.Default.Search,
            trailingIcon = if (searchQuery.isNotEmpty()) {
                {
                    IconButton(onClick = { viewModel.historySearchQuery.value = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            } else null,
            modifier = Modifier.fillMaxWidth().testTag("history_search_input")
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sales List
        if (salesHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = Localization.get("no_history_found", lang),
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(salesHistory, key = { it.sale.id }) { saleWithItems ->
                    HistoryItemCard(
                        saleWithItems = saleWithItems,
                        currencySymbol = settings.currencySymbol,
                        lang = lang,
                        onViewReceipt = { onSelectReceipt(saleWithItems) },
                        onDelete = { saleToDelete = saleWithItems }
                    )
                }
            }
        }
    }

    // Delete Single Sale Dialog
    if (saleToDelete != null) {
        val s = saleToDelete!!
        AlertDialog(
            onDismissRequest = { saleToDelete = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(
                    text = Localization.get("delete_history_item", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete invoice ${s.sale.invoiceNumber}?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteSale(s.sale.id)
                        saleToDelete = null
                    }
                ) {
                    Text(Localization.get("delete_product", lang), color = RoseNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { saleToDelete = null }) {
                    Text(Localization.get("btn_cancel", lang), color = TextSecondary)
                }
            }
        )
    }

    // Clear All Confirmation Dialog
    if (showClearAllConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirmation = false },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(
                    text = Localization.get("clear_all_history", lang),
                    color = RoseNeon,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = Localization.get("confirm_clear_all", lang),
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.clearAllHistory()
                        showClearAllConfirmation = false
                    }
                ) {
                    Text(Localization.get("clear_all_history", lang), color = RoseNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirmation = false }) {
                    Text(Localization.get("btn_cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun HistoryItemCard(
    saleWithItems: SaleWithItems,
    currencySymbol: String,
    lang: String,
    onViewReceipt: () -> Unit,
    onDelete: () -> Unit
) {
    val sale = saleWithItems.sale
    var isExpanded by remember { mutableStateOf(false) }

    val customerName = remember(sale.customerInfoJson) {
        try {
            val obj = JSONObject(sale.customerInfoJson)
            obj.optString("name", "")
        } catch (e: Exception) {
            ""
        }
    }

    GlassCard(
        modifier = Modifier.fillMaxWidth().testTag("history_item_${sale.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(EmeraldNeon.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = sale.invoiceNumber,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = FormatUtils.formatDateTime(sale.timestamp),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = FormatUtils.formatMoney(sale.grandTotal, currencySymbol),
                        color = EmeraldLight,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (sale.discountAmount > 0) {
                        Text(
                            text = "-${FormatUtils.formatMoney(sale.discountAmount, currencySymbol)} off",
                            color = RoseNeon,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            if (customerName.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassBadge(
                        text = "Customer: $customerName",
                        color = CyanLight,
                        fontSize = 10,
                        paddingHorizontal = 6.dp,
                        paddingVertical = 2.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Expandable Items breakdown
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )

                    saleWithItems.items.forEach { itm ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${itm.quantity}× ${itm.productName}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = FormatUtils.formatMoney(itm.totalPrice, currencySymbol),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color.White.copy(alpha = 0.08f))
                    )
                }
            }

            // Actions row: Toggle items, View receipt, Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { isExpanded = !isExpanded },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${saleWithItems.items.size} items",
                        color = CyanLight,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = CyanLight,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    GlassSecondaryButton(
                        text = Localization.get("view_details", lang),
                        icon = Icons.Default.Receipt,
                        onClick = onViewReceipt,
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RoseNeon.copy(alpha = 0.8f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
