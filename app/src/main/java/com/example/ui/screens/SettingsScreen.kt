package com.example.ui.screens

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Currency
import com.example.data.model.CustomerField
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowingGlassCard
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
import com.example.util.Localization

@Composable
fun SettingsScreen(
    viewModel: PosViewModel
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val lang = settings.language

    var shopName by remember(settings) { mutableStateOf(settings.shopName) }
    var shopPhone by remember(settings) { mutableStateOf(settings.shopPhone) }
    var shopAddress by remember(settings) { mutableStateOf(settings.shopAddress) }
    var footerNote by remember(settings) { mutableStateOf(settings.receiptTagline) }
    var signature by remember(settings) { mutableStateOf(settings.ownerSignature) }

    var customerFields by remember(settings) { mutableStateOf(settings.customerFields) }

    var showCurrencyDialog by remember { mutableStateOf(false) }
    var showAddFieldDialog by remember { mutableStateOf(false) }

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
                        text = Localization.get("settings_title", lang),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Customize Store & POS Behavior",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    tint = CyanLight,
                    modifier = Modifier.size(28.dp)
                )
            }
        }

        // Language Switcher
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Localization.get("language_select", lang),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = if (lang == "bn") "বাংলা (Bangladesh)" else "English (US)",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF0F172A))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (lang == "bn") EmeraldNeon else Color.Transparent)
                                .clickable { viewModel.setLanguage("bn") }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "বাংলা",
                                color = if (lang == "bn") Color.Black else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (lang == "en") EmeraldNeon else Color.Transparent)
                                .clickable { viewModel.setLanguage("en") }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "English",
                                color = if (lang == "en") Color.Black else TextSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // Shop Profile Information
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = null,
                            tint = CyanNeon,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = Localization.get("shop_name", lang),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column {
                        Text(Localization.get("shop_name", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = shopName,
                            onValueChange = { shopName = it },
                            placeholder = "My Store",
                            leadingIcon = Icons.Default.Store,
                            modifier = Modifier.fillMaxWidth().testTag("input_shop_name")
                        )
                    }

                    Column {
                        Text(Localization.get("shop_phone", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = shopPhone,
                            onValueChange = { shopPhone = it },
                            placeholder = "+880 1700-000000",
                            leadingIcon = Icons.Default.Phone,
                            modifier = Modifier.fillMaxWidth().testTag("input_shop_phone")
                        )
                    }

                    Column {
                        Text(Localization.get("shop_address", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = shopAddress,
                            onValueChange = { shopAddress = it },
                            placeholder = "Dhaka, Bangladesh",
                            leadingIcon = Icons.Default.LocationOn,
                            modifier = Modifier.fillMaxWidth().testTag("input_shop_address")
                        )
                    }

                    Column {
                        Text(Localization.get("receipt_footer", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = footerNote,
                            onValueChange = { footerNote = it },
                            placeholder = "Thank you for shopping with us!",
                            modifier = Modifier.fillMaxWidth().testTag("input_shop_footer")
                        )
                    }

                    Column {
                        Text(Localization.get("auth_signature", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = signature,
                            onValueChange = { signature = it },
                            placeholder = "Store Manager / Owner",
                            leadingIcon = Icons.Default.Person,
                            modifier = Modifier.fillMaxWidth().testTag("input_shop_signature")
                        )
                    }
                }
            }
        }

        // Currency Selector Card
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { showCurrencyDialog = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AttachMoney,
                            contentDescription = null,
                            tint = EmeraldNeon,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = Localization.get("currency_select", lang),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${settings.currencyCode} (${settings.currencySymbol})",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    GlassBadge(
                        text = "Change",
                        color = CyanLight,
                        fontSize = 11
                    )
                }
            }
        }

        // Required / Optional Customer Fields Management
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = IndigoNeon,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = Localization.get("customer_fields_settings", lang),
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        IconButton(
                            onClick = { showAddFieldDialog = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Field",
                                tint = CyanLight
                            )
                        }
                    }

                    Text(
                        text = "Enable fields to capture on POS and mark required fields (*)",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    // Customer fields list
                    customerFields.forEachIndexed { index, field ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A).copy(alpha = 0.6f))
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = field.getLabel(lang),
                                    color = if (field.isEnabled) TextPrimary else TextMuted,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = if (field.isRequired) "Mandatory Required (*)" else "Optional",
                                    color = if (field.isRequired) CyanLight else TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Required Checkbox
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable {
                                        val updated = customerFields.toMutableList()
                                        updated[index] = field.copy(isRequired = !field.isRequired)
                                        customerFields = updated
                                    }
                                ) {
                                    Checkbox(
                                        checked = field.isRequired,
                                        onCheckedChange = { checked ->
                                            val updated = customerFields.toMutableList()
                                            updated[index] = field.copy(isRequired = checked)
                                            customerFields = updated
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = CyanNeon,
                                            uncheckedColor = TextMuted
                                        )
                                    )
                                    Text(
                                        text = Localization.get("mandatory", lang),
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Enable Switch
                                Switch(
                                    checked = field.isEnabled,
                                    onCheckedChange = { checked ->
                                        val updated = customerFields.toMutableList()
                                        updated[index] = field.copy(isEnabled = checked)
                                        customerFields = updated
                                    },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = EmeraldNeon,
                                        uncheckedThumbColor = TextMuted,
                                        uncheckedTrackColor = Color(0xFF1E293B)
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Save Settings Action Button
        item {
            GlassPrimaryButton(
                text = Localization.get("btn_save", lang),
                icon = Icons.Default.Save,
                modifier = Modifier.fillMaxWidth().testTag("save_settings_btn"),
                onClick = {
                    val updated = settings.copy(
                        shopName = shopName.trim().ifBlank { "My Store" },
                        shopPhone = shopPhone.trim(),
                        shopAddress = shopAddress.trim(),
                        receiptTagline = footerNote.trim(),
                        ownerSignature = signature.trim(),
                        customerFields = customerFields
                    )
                    viewModel.updateSettings(updated)
                }
            )
        }

        // Developer Info Card
        item {
            GlowingGlassCard(
                glowColor = CyanNeon,
                onClick = { viewModel.showDeveloperModal.value = true }
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(CyanNeon.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = CyanLight,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Developer Information",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Created by Mehadi Hasan",
                                color = CyanLight,
                                fontSize = 12.sp
                            )
                        }
                    }

                    GlassBadge(
                        text = "View Bio",
                        color = CyanLight,
                        fontSize = 11
                    )
                }
            }
        }
    }

    // Currency Picker Dialog
    if (showCurrencyDialog) {
        CurrencyPickerDialog(
            currentCode = settings.currencyCode,
            onDismiss = { showCurrencyDialog = false },
            onSelect = { curr ->
                viewModel.setCurrency(curr.code, curr.symbol)
                showCurrencyDialog = false
            }
        )
    }

    // Add Custom Customer Field Dialog
    if (showAddFieldDialog) {
        AddCustomerFieldDialog(
            onDismiss = { showAddFieldDialog = false },
            onAdd = { newField ->
                val updated = customerFields + newField
                customerFields = updated
                showAddFieldDialog = false
            }
        )
    }
}

@Composable
fun CurrencyPickerDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onSelect: (Currency) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val filtered = remember(searchQuery) {
        if (searchQuery.isBlank()) Currency.allCurrencies
        else {
            Currency.allCurrencies.filter {
                it.nameEn.contains(searchQuery, ignoreCase = true) ||
                it.nameBn.contains(searchQuery, ignoreCase = true) ||
                it.code.contains(searchQuery, ignoreCase = true) ||
                it.symbol.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        GlowingGlassCard(
            glowColor = CyanNeon,
            modifier = Modifier.fillMaxWidth().height(480.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Select Currency",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                GlassTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = "Search currency (e.g. BDT, USD, EUR)...",
                    leadingIcon = Icons.Default.Search,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filtered) { curr ->
                        val isSelected = curr.code.equals(currentCode, ignoreCase = true)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) EmeraldNeon.copy(alpha = 0.2f) else Color(0xFF14161C).copy(alpha = 0.7f))
                                .clickable { onSelect(curr) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = curr.symbol,
                                    color = EmeraldLight,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(36.dp)
                                )
                                Column {
                                    Text(
                                        text = "${curr.nameEn} (${curr.nameBn})",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = curr.code,
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                GlassSecondaryButton(
                    text = "Close",
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun AddCustomerFieldDialog(
    onDismiss: () -> Unit,
    onAdd: (CustomerField) -> Unit
) {
    var labelEn by remember { mutableStateOf("") }
    var labelBn by remember { mutableStateOf("") }
    var isRequired by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        GlowingGlassCard(
            glowColor = IndigoNeon,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Add Customer Field",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text("Field Label (English)", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassTextField(
                        value = labelEn,
                        onValueChange = { labelEn = it },
                        placeholder = "e.g. National ID / Passport",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text("Field Label (বাংলা)", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassTextField(
                        value = labelBn,
                        onValueChange = { labelBn = it },
                        placeholder = "e.g. জাতীয় পরিচয়পত্র নং",
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { isRequired = !isRequired }
                ) {
                    Checkbox(
                        checked = isRequired,
                        onCheckedChange = { isRequired = it },
                        colors = CheckboxDefaults.colors(checkedColor = CyanNeon)
                    )
                    Text("Mark as Mandatory Required (*)", color = TextPrimary, fontSize = 13.sp)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassSecondaryButton(
                        text = "Cancel",
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    GlassPrimaryButton(
                        text = "Add Field",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (labelEn.isNotBlank()) {
                                val fieldId = labelEn.lowercase().replace(" ", "_").filter { it.isLetterOrDigit() || it == '_' }
                                onAdd(
                                    CustomerField(
                                        id = fieldId,
                                        labelEn = labelEn.trim(),
                                        labelBn = if (labelBn.isBlank()) labelEn.trim() else labelBn.trim(),
                                        isEnabled = true,
                                        isRequired = isRequired
                                    )
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
