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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ProductEntity
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils
import com.example.util.Localization

@Composable
fun InventoryScreen(
    viewModel: PosViewModel
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val searchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()

    val lang = settings.language

    var productToEdit by remember { mutableStateOf<ProductEntity?>(null) }
    var showAddEditDialog by remember { mutableStateOf(false) }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = Localization.get("inventory_title", lang),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} ${Localization.get("total_products", lang)}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                GlassSecondaryButton(
                    text = Localization.get("btn_reset_sample", lang),
                    icon = Icons.Default.Refresh,
                    onClick = { viewModel.loadSampleData() },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            GlassTextField(
                value = searchQuery,
                onValueChange = { viewModel.productSearchQuery.value = it },
                placeholder = Localization.get("search_products", lang),
                leadingIcon = Icons.Default.Search,
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    {
                        IconButton(onClick = { viewModel.productSearchQuery.value = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                } else null,
                modifier = Modifier.fillMaxWidth().testTag("inventory_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Product List
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Inventory2,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = Localization.get("no_products_found", lang),
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GlassPrimaryButton(
                            text = Localization.get("add_new_product", lang),
                            icon = Icons.Default.Add,
                            onClick = {
                                productToEdit = null
                                showAddEditDialog = true
                            }
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        ProductInventoryItemCard(
                            product = product,
                            currencySymbol = settings.currencySymbol,
                            lang = lang,
                            onEdit = {
                                productToEdit = product
                                showAddEditDialog = true
                            },
                            onDelete = {
                                productToDelete = product
                            }
                        )
                    }
                }
            }
        }

        // Floating Action Button (FAB) for Add Product
        FloatingActionButton(
            onClick = {
                productToEdit = null
                showAddEditDialog = true
            },
            containerColor = EmeraldNeon,
            contentColor = Color.Black,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 90.dp, end = 20.dp)
                .size(56.dp)
                .testTag("fab_add_product")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = Localization.get("add_new_product", lang),
                modifier = Modifier.size(28.dp)
            )
        }
    }

    // Add / Edit Product Dialog
    if (showAddEditDialog) {
        AddEditProductDialog(
            product = productToEdit,
            currencySymbol = settings.currencySymbol,
            lang = lang,
            onDismiss = { showAddEditDialog = false },
            onSave = { savedProduct ->
                viewModel.saveProduct(savedProduct) {
                    showAddEditDialog = false
                }
            }
        )
    }

    // Delete Confirmation Dialog
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            containerColor = Color(0xFF0F172A),
            title = {
                Text(
                    text = Localization.get("delete_product", lang),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "${Localization.get("confirm_delete_product", lang)}\n\n\"${prod.name}\"",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteProduct(prod)
                        productToDelete = null
                    }
                ) {
                    Text(Localization.get("delete_product", lang), color = RoseNeon, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text(Localization.get("btn_cancel", lang), color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun ProductInventoryItemCard(
    product: ProductEntity,
    currencySymbol: String,
    lang: String,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val isOutOfStock = product.stock <= 0
    val isLowStock = product.stock in 1..5

    GlassCard(modifier = Modifier.fillMaxWidth().testTag("inv_item_${product.id}")) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.name,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (product.category.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        GlassBadge(
                            text = product.category,
                            color = IndigoNeon,
                            fontSize = 10,
                            paddingHorizontal = 6.dp,
                            paddingVertical = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = FormatUtils.formatMoney(product.price, currencySymbol),
                        color = EmeraldLight,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    when {
                        isOutOfStock -> {
                            GlassBadge(
                                text = Localization.get("out_of_stock", lang),
                                color = RoseNeon,
                                fontSize = 10
                            )
                        }
                        isLowStock -> {
                            GlassBadge(
                                text = "${Localization.get("stock", lang)}: ${product.stock} (Low)",
                                color = AmberNeon,
                                fontSize = 10
                            )
                        }
                        else -> {
                            GlassBadge(
                                text = "${Localization.get("stock", lang)}: ${product.stock}",
                                color = CyanLight,
                                fontSize = 10
                            )
                        }
                    }

                    if (product.sku.isNotBlank()) {
                        Text(
                            text = "SKU: ${product.sku}",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onEdit) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = CyanLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = RoseNeon.copy(alpha = 0.8f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddEditProductDialog(
    product: ProductEntity?,
    currencySymbol: String,
    lang: String,
    onDismiss: () -> Unit,
    onSave: (ProductEntity) -> Unit
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var priceText by remember { mutableStateOf(product?.price?.toString() ?: "") }
    var stockText by remember { mutableStateOf(product?.stock?.toString() ?: "10") }
    var category by remember { mutableStateOf(product?.category ?: "Grocery") }
    var sku by remember { mutableStateOf(product?.sku ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        GlowingGlassCard(
            glowColor = EmeraldNeon,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = if (product == null) Localization.get("add_new_product", lang) else Localization.get("edit_product", lang),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text(Localization.get("product_name", lang), color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassTextField(
                        value = name,
                        onValueChange = { name = it },
                        placeholder = "e.g. Basmati Rice (1kg)",
                        modifier = Modifier.fillMaxWidth().testTag("input_product_name")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${Localization.get("selling_price", lang)} ($currencySymbol)", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = priceText,
                            onValueChange = { priceText = it },
                            placeholder = "120.00",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_product_price")
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(Localization.get("stock_quantity", lang), color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        GlassTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            placeholder = "50",
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("input_product_stock")
                        )
                    }
                }

                Column {
                    Text(Localization.get("category", lang), color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassTextField(
                        value = category,
                        onValueChange = { category = it },
                        placeholder = "e.g. Grocery, Snacks, Beverage, Pharmacy",
                        modifier = Modifier.fillMaxWidth().testTag("input_product_category")
                    )
                }

                Column {
                    Text(Localization.get("sku_barcode", lang), color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    GlassTextField(
                        value = sku,
                        onValueChange = { sku = it },
                        placeholder = "e.g. RIC-01, 890123456",
                        modifier = Modifier.fillMaxWidth().testTag("input_product_sku")
                    )
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = RoseNeon,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    GlassSecondaryButton(
                        text = Localization.get("btn_cancel", lang),
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    )

                    GlassPrimaryButton(
                        text = if (product == null) Localization.get("btn_save", lang) else Localization.get("btn_update", lang),
                        modifier = Modifier.weight(1f).testTag("btn_save_product"),
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Product name cannot be empty"
                                return@GlassPrimaryButton
                            }
                            val price = priceText.toDoubleOrNull()
                            if (price == null || price < 0) {
                                errorMessage = "Please enter a valid price"
                                return@GlassPrimaryButton
                            }
                            val stock = stockText.toIntOrNull()
                            if (stock == null || stock < 0) {
                                errorMessage = "Please enter a valid stock quantity"
                                return@GlassPrimaryButton
                            }

                            val result = ProductEntity(
                                id = product?.id ?: 0,
                                name = name.trim(),
                                price = price,
                                stock = stock,
                                category = if (category.isBlank()) "General" else category.trim(),
                                sku = sku.trim(),
                                createdAt = product?.createdAt ?: System.currentTimeMillis()
                            )
                            onSave(result)
                        }
                    )
                }
            }
        }
    }
}
