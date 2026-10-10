package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ReportSummary
import com.example.ui.components.FilterChipGlass
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils
import com.example.util.Localization
import com.example.util.ReceiptPdfGenerator
import org.json.JSONArray
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: PosViewModel
) {
    val context = LocalContext.current
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val reportSummary by viewModel.currentReport.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedReportType.collectAsStateWithLifecycle()

    val lang = settings.language

    var showCustomDateDialog by remember { mutableStateOf(false) }
    val dateRangePickerState = rememberDateRangePickerState()

    val reportTypes = listOf(
        Pair("today", Localization.get("report_today", lang)),
        Pair("month", Localization.get("report_this_month", lang)),
        Pair("all", Localization.get("report_all_time", lang)),
        Pair("custom", Localization.get("report_custom", lang))
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("reports_title", lang),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Sales Performance & Analytics",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Analytics,
                    contentDescription = null,
                    tint = CyanLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // 4 Filter Type Tabs
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(reportTypes) { pair ->
                    val isSelected = selectedType == pair.first
                    FilterChipGlass(
                        text = pair.second,
                        isSelected = isSelected,
                        onClick = {
                            if (pair.first == "custom") {
                                showCustomDateDialog = true
                            } else {
                                viewModel.loadReport(pair.first)
                            }
                        }
                    )
                }
            }
        }

        val report = reportSummary
        if (report != null) {
            // 4 Core Metrics 2x2 Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Revenue
                        GlowingGlassCard(
                            glowColor = EmeraldNeon,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = Localization.get("total_revenue", lang),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = FormatUtils.formatMoney(report.totalSales, settings.currencySymbol),
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Total Transactions
                        GlowingGlassCard(
                            glowColor = CyanNeon,
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = Localization.get("total_transactions", lang),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${report.totalTransactions}",
                                    color = TextPrimary,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Total Discounts
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = Localization.get("total_discount_given", lang),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = FormatUtils.formatMoney(report.totalDiscount, settings.currencySymbol),
                                    color = RoseNeon,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Average Sale Value
                        GlassCard(modifier = Modifier.weight(1f)) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = Localization.get("avg_sale_value", lang),
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = FormatUtils.formatMoney(report.averageSale, settings.currencySymbol),
                                    color = CyanLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Top Selling Products Breakdown
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Localization.get("top_selling_items", lang),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = AmberNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (report.topProducts.isEmpty()) {
                            Text(
                                text = "No items sold in this timeframe.",
                                color = TextMuted,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 12.dp)
                            )
                        } else {
                            report.topProducts.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (index == 0) AmberNeon.copy(alpha = 0.3f)
                                                    else if (index == 1) CyanNeon.copy(alpha = 0.3f)
                                                    else Color(0xFF1E293B)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${index + 1}",
                                                color = if (index == 0) AmberNeon else if (index == 1) CyanLight else TextSecondary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = item.productName,
                                                color = TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                            Text(
                                                text = "${item.quantitySold} units sold",
                                                color = TextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    Text(
                                        text = FormatUtils.formatMoney(item.totalRevenue, settings.currencySymbol),
                                        color = EmeraldLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                if (index < report.topProducts.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color.White.copy(alpha = 0.05f))
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Export Actions: Copy JSON Report & Print / PDF Report
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassSecondaryButton(
                        text = Localization.get("export_json_report", lang),
                        icon = Icons.Default.ContentCopy,
                        modifier = Modifier.weight(1f).testTag("copy_report_json_btn"),
                        onClick = {
                            val json = JSONObject().apply {
                                put("reportType", report.reportType)
                                put("startDate", report.startDate)
                                put("endDate", report.endDate)
                                put("totalTransactions", report.totalTransactions)
                                put("totalSales", report.totalSales)
                                put("totalDiscount", report.totalDiscount)
                                put("averageSale", report.averageSale)
                                val topArr = JSONArray()
                                report.topProducts.forEach { p ->
                                    topArr.put(JSONObject().apply {
                                        put("name", p.productName)
                                        put("quantity", p.quantitySold)
                                        put("revenue", p.totalRevenue)
                                    })
                                }
                                put("topProducts", topArr)
                            }.toString(2)

                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Sales Report", json)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, Localization.get("report_copied", lang), Toast.LENGTH_SHORT).show()
                        }
                    )

                    GlassPrimaryButton(
                        text = Localization.get("download_pdf_report", lang),
                        icon = Icons.Default.Print,
                        modifier = Modifier.weight(1f).testTag("print_report_btn"),
                        onClick = {
                            ReceiptPdfGenerator.printReport(context, report, settings)
                        }
                    )
                }
            }
        }
    }

    // Custom Date Range Picker Dialog
    if (showCustomDateDialog) {
        DatePickerDialog(
            onDismissRequest = { showCustomDateDialog = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = dateRangePickerState.selectedStartDateMillis ?: 0L
                        val end = dateRangePickerState.selectedEndDateMillis ?: System.currentTimeMillis()
                        viewModel.loadReport("custom", start, end)
                        showCustomDateDialog = false
                    }
                ) {
                    Text("Apply Range", color = EmeraldNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomDateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        ) {
            DateRangePicker(
                state = dateRangePickerState,
                title = {
                    Text(
                        text = "Select Date Range",
                        color = TextPrimary,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    }
}
