package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.entity.SaleWithItems
import com.example.data.model.AppSettings
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.FormatUtils
import com.example.util.Localization
import com.example.util.ReceiptPdfGenerator
import org.json.JSONObject

@Composable
fun ReceiptPreviewDialog(
    saleWithItems: SaleWithItems,
    settings: AppSettings,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sale = saleWithItems.sale
    val lang = settings.language

    val customerInfoMap = remember(sale.customerInfoJson) {
        val map = mutableMapOf<String, String>()
        try {
            val json = JSONObject(sale.customerInfoJson)
            val keys = json.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val v = json.optString(key, "")
                if (v.isNotBlank()) {
                    map[key] = v
                }
            }
        } catch (e: Exception) {
            // ignore
        }
        map
    }

    Dialog(onDismissRequest = onDismiss) {
        GlowingGlassCard(
            glowColor = EmeraldNeon,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Modal Top Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Localization.get("receipt_title", lang),
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(28.dp).testTag("close_receipt_modal")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Realistic Thermal Paper Style Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFDFEFE))
                        .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Shop Brand Header
                        Text(
                            text = settings.shopName.uppercase(),
                            color = Color(0xFF0F172A),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            textAlign = TextAlign.Center
                        )

                        if (settings.shopAddress.isNotBlank()) {
                            Text(
                                text = settings.shopAddress,
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (settings.shopPhone.isNotBlank()) {
                            Text(
                                text = "Tel: ${settings.shopPhone}",
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Invoice & Timestamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "INV: ${sale.invoiceNumber}",
                                color = Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                            Text(
                                text = FormatUtils.formatDateTime(sale.timestamp),
                                color = Color(0xFF64748B),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        // Customer Information Fields
                        if (customerInfoMap.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            customerInfoMap.forEach { (k, v) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${k.replace("_", " ").capitalize()}:",
                                        color = Color(0xFF64748B),
                                        fontSize = 11.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    Text(
                                        text = v,
                                        color = Color(0xFF0F172A),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Line Items Table Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ITEM", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, modifier = Modifier.weight(1.5f))
                            Text("QTY", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, textAlign = TextAlign.Center, modifier = Modifier.width(36.dp))
                            Text("PRICE", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, modifier = Modifier.width(60.dp))
                            Text("TOTAL", color = Color(0xFF64748B), fontSize = 11.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace, textAlign = TextAlign.End, modifier = Modifier.width(70.dp))
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Line Items
                        saleWithItems.items.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.productName,
                                    color = Color(0xFF0F172A),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.weight(1.5f)
                                )
                                Text(
                                    text = "${item.quantity}",
                                    color = Color(0xFF0F172A),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(36.dp)
                                )
                                Text(
                                    text = FormatUtils.formatNumber(item.unitPrice),
                                    color = Color(0xFF0F172A),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(60.dp)
                                )
                                Text(
                                    text = FormatUtils.formatNumber(item.totalPrice),
                                    color = Color(0xFF0F172A),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.width(70.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(8.dp))

                        // Subtotal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("SUBTOTAL", color = Color(0xFF475569), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            Text(FormatUtils.formatMoney(sale.subtotal, settings.currencySymbol), color = Color(0xFF0F172A), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }

                        // Discount
                        if (sale.discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("DISCOUNT", color = Color(0xFFDC2626), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                                Text("-${FormatUtils.formatMoney(sale.discountAmount, settings.currencySymbol)}", color = Color(0xFFDC2626), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Grand Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("TOTAL", color = Color(0xFF0F172A), fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            Text(FormatUtils.formatMoney(sale.grandTotal, settings.currencySymbol), color = Color(0xFF0F172A), fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        DashedDivider()
                        Spacer(modifier = Modifier.height(10.dp))

                        // Footer Tagline
                        if (settings.receiptTagline.isNotBlank()) {
                            Text(
                                text = settings.receiptTagline,
                                color = Color(0xFF475569),
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Signature
                        if (settings.ownerSignature.isNotBlank()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .width(140.dp)
                                    .height(1.dp)
                                    .background(Color(0xFF94A3B8))
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = settings.ownerSignature,
                                color = Color(0xFF0F172A),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Authorized Signature",
                                color = Color(0xFF94A3B8),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Actions: Print & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassSecondaryButton(
                        text = Localization.get("share_receipt", lang),
                        icon = Icons.Default.Share,
                        modifier = Modifier.weight(1f).testTag("share_receipt_btn"),
                        onClick = {
                            ReceiptPdfGenerator.shareReceiptPdf(context, saleWithItems, settings)
                        }
                    )

                    GlassPrimaryButton(
                        text = Localization.get("print_receipt", lang),
                        icon = Icons.Default.Print,
                        modifier = Modifier.weight(1f).testTag("print_receipt_btn"),
                        onClick = {
                            ReceiptPdfGenerator.printReceipt(context, saleWithItems, settings)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DashedDivider() {
    Text(
        text = "------------------------------------------",
        color = Color(0xFFCBD5E1),
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        maxLines = 1
    )
}
