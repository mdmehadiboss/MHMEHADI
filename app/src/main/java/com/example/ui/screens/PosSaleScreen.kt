package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.entity.ProductEntity
import com.example.data.entity.SaleWithItems
import com.example.data.model.CartItem
import com.example.ui.components.FilterChipGlass
import com.example.ui.components.GlassBadge
import com.example.ui.components.GlassCard
import com.example.ui.components.GlassPrimaryButton
import com.example.ui.components.GlassSecondaryButton
import com.example.ui.components.GlassTextField
import com.example.ui.components.GlowingGlassCard
import com.example.ui.theme.AmberNeon
import com.example.ui.theme.CyanLight
import com.example.ui.theme.CyanNeon
import com.example.ui.theme.DeepSpace
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.IndigoNeon
import com.example.ui.theme.PrimaryGradient
import com.example.ui.theme.RoseNeon
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.PosViewModel
import com.example.util.FormatUtils
import com.example.util.Localization
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosSaleScreen(
    viewModel: PosViewModel,
    onReceiptGenerated: (SaleWithItems) -> Unit
) {
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val cart by viewModel.cart.collectAsStateWithLifecycle()
    val searchQuery by viewModel.productSearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val discountVal by viewModel.discountValue.collectAsStateWithLifecycle()
    val isPercentDisc by viewModel.isPercentageDiscount.collectAsStateWithLifecycle()
    val customerValues by viewModel.customerFieldValues.collectAsStateWithLifecycle()

    val lang = settings.language

    var showCartBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    // Extract categories
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct().filter { it.isNotBlank() && it != "General" }
    }

    // Calculations
    val subtotal = cart.sumOf { it.totalPrice }
    val discountAmount = if (isPercentDisc) {
        subtotal * (discountVal.coerceIn(0.0, 100.0) / 100.0)
    } else {
        discountVal.coerceAtLeast(0.0).coerceAtMost(subtotal)
    }
    val grandTotal = (subtotal - discountAmount).coerceAtLeast(0.0)
    val totalItemCount = cart.sumOf { it.quantity }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header & Cart summary trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = Localization.get("pos_title", lang),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${products.size} ${Localization.get("total_products", lang)} available",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }

                // Cart Trigger button
                GlowingGlassCard(
                    glowColor = if (cart.isNotEmpty()) EmeraldNeon else CyanNeon,
                    onClick = { showCartBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                            .testTag("open_cart_btn"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        BadgedBox(
                            badge = {
                                if (totalItemCount > 0) {
                                    Badge(
                                        containerColor = EmeraldNeon,
                                        contentColor = Color.Black
                                    ) {
                                        Text("$totalItemCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = if (cart.isNotEmpty()) EmeraldLight else TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = FormatUtils.formatMoney(grandTotal, settings.currencySymbol),
                            color = if (cart.isNotEmpty()) EmeraldLight else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
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
                modifier = Modifier.fillMaxWidth().testTag("pos_search_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Chips Row
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 6.dp)
            ) {
                items(categories) { cat ->
                    val isSelected = (selectedCategory == cat)
                    val label = if (cat == "All") Localization.get("all", lang) else cat
                    FilterChipGlass(
                        text = label,
                        isSelected = isSelected,
                        onClick = { viewModel.selectedCategory.value = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = Localization.get("no_products_found", lang),
                            color = TextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 140.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 120.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(products, key = { it.id }) { product ->
                        val inCartQty = cart.firstOrNull { it.productId == product.id }?.quantity ?: 0
                        ProductGridCard(
                            product = product,
                            currencySymbol = settings.currencySymbol,
                            inCartQty = inCartQty,
                            lang = lang,
                            onAddToCart = { viewModel.addToCart(product) }
                        )
                    }
                }
            }
        }

        // Floating Bottom Sticky Cart Bar when items are in cart
        if (cart.isNotEmpty() && !showCartBottomSheet) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 90.dp)
                    .fillMaxWidth()
            ) {
                GlowingGlassCard(
                    glowColor = EmeraldNeon,
                    onClick = { showCartBottomSheet = true }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$totalItemCount",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = Localization.get("items_in_cart", lang),
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = FormatUtils.formatMoney(grandTotal, settings.currencySymbol),
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = Localization.get("btn_preview_receipt", lang),
                                color = EmeraldLight,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Receipt,
                                contentDescription = null,
                                tint = EmeraldLight,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Bottom Sheet for Checkout, Cart Editing, Customer info, & Receipt preview
        if (showCartBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showCartBottomSheet = false },
                sheetState = sheetState,
                containerColor = Color(0xFF0F172A),
                dragHandle = {
                    Box(
                        modifier = Modifier
                            .padding(vertical = 10.dp)
                            .width(40.dp)
                            .height(4.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                    )
                }
            ) {
                CartCheckoutBottomSheetContent(
                    viewModel = viewModel,
                    cart = cart,
                    subtotal = subtotal,
                    discountAmount = discountAmount,
                    grandTotal = grandTotal,
                    discountVal = discountVal,
                    isPercentDisc = isPercentDisc,
                    customerValues = customerValues,
                    settings = settings,
                    lang = lang,
                    onClose = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showCartBottomSheet = false
                        }
                    },
                    onCompleteSale = { saleWithItems ->
                        coroutineScope.launch {
                            sheetState.hide()
                            showCartBottomSheet = false
                            onReceiptGenerated(saleWithItems)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ProductGridCard(
    product: ProductEntity,
    currencySymbol: String,
    inCartQty: Int,
    lang: String,
    onAddToCart: () -> Unit
) {
    val isOutOfStock = product.stock <= 0
    val isLowStock = product.stock in 1..5

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = !isOutOfStock, onClick = onAddToCart)
            .testTag("product_card_${product.id}"),
        borderColor = if (inCartQty > 0) EmeraldNeon.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Category & Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.category,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )

                when {
                    isOutOfStock -> {
                        GlassBadge(
                            text = Localization.get("out_of_stock", lang),
                            color = RoseNeon,
                            fontSize = 9
                        )
                    }
                    isLowStock -> {
                        GlassBadge(
                            text = "${Localization.get("stock", lang)}: ${product.stock}",
                            color = AmberNeon,
                            fontSize = 9
                        )
                    }
                    else -> {
                        GlassBadge(
                            text = "${Localization.get("stock", lang)}: ${product.stock}",
                            color = CyanLight,
                            fontSize = 9
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Product Name
            Text(
                text = product.name,
                color = if (isOutOfStock) TextMuted else TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                minLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Price & Add to Cart button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = FormatUtils.formatMoney(product.price, currencySymbol),
                    color = EmeraldLight,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                // Plus button or in-cart counter
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isOutOfStock) Color(0xFF334155)
                            else if (inCartQty > 0) EmeraldNeon
                            else Color(0xFF1E293B)
                        )
                        .clickable(enabled = !isOutOfStock, onClick = onAddToCart),
                    contentAlignment = Alignment.Center
                ) {
                    if (inCartQty > 0) {
                        Text(
                            text = "$inCartQty",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = if (isOutOfStock) TextMuted else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CartCheckoutBottomSheetContent(
    viewModel: PosViewModel,
    cart: List<CartItem>,
    subtotal: Double,
    discountAmount: Double,
    grandTotal: Double,
    discountVal: Double,
    isPercentDisc: Boolean,
    customerValues: Map<String, String>,
    settings: com.example.data.model.AppSettings,
    lang: String,
    onClose: () -> Unit,
    onCompleteSale: (SaleWithItems) -> Unit
) {
    val enabledCustomerFields = settings.customerFields.filter { it.isEnabled }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Sheet Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = EmeraldNeon,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = Localization.get("cart_title", lang),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (cart.isNotEmpty()) {
                    Text(
                        text = Localization.get("btn_clear_cart", lang),
                        color = RoseNeon,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { viewModel.clearCart() }
                    )
                }
            }
        }

        // Cart items list
        if (cart.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = Localization.get("cart_empty", lang),
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(cart) { item ->
                CartItemRow(
                    item = item,
                    currencySymbol = settings.currencySymbol,
                    onIncrease = {
                        val product = ProductEntity(
                            id = item.productId,
                            name = item.productName,
                            price = item.unitPrice,
                            stock = item.maxStock
                        )
                        viewModel.addToCart(product)
                    },
                    onDecrease = { viewModel.decreaseCartItem(item.productId) },
                    onDelete = { viewModel.removeFromCart(item.productId) }
                )
            }
        }

        // Discount Selector Section
        if (cart.isNotEmpty()) {
            item {
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Localization.get("discount", lang),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            // Discount Type Switcher
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(2.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isPercentDisc) EmeraldNeon else Color.Transparent)
                                        .clickable { viewModel.isPercentageDiscount.value = true }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "%",
                                        color = if (isPercentDisc) Color.Black else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!isPercentDisc) EmeraldNeon else Color.Transparent)
                                        .clickable { viewModel.isPercentageDiscount.value = false }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = settings.currencySymbol,
                                        color = if (!isPercentDisc) Color.Black else TextSecondary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        GlassTextField(
                            value = if (discountVal == 0.0) "" else discountVal.toString(),
                            onValueChange = {
                                val parsed = it.toDoubleOrNull() ?: 0.0
                                viewModel.discountValue.value = parsed
                            },
                            placeholder = "0.0",
                            leadingIcon = Icons.Default.Discount,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth().testTag("discount_input")
                        )
                    }
                }
            }

            // Customer Info Fields (Configured in Settings)
            if (enabledCustomerFields.isNotEmpty()) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = Localization.get("customer_info", lang),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            for (field in enabledCustomerFields) {
                                val label = field.getLabel(lang) + if (field.isRequired) " *" else ""
                                val value = customerValues[field.id].orEmpty()

                                Column {
                                    Text(
                                        text = label,
                                        color = if (field.isRequired) CyanLight else TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = if (field.isRequired) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    GlassTextField(
                                        value = value,
                                        onValueChange = { viewModel.setCustomerFieldValue(field.id, it) },
                                        placeholder = field.getLabel(lang),
                                        keyboardOptions = if (field.id == "phone") KeyboardOptions(keyboardType = KeyboardType.Phone) else KeyboardOptions.Default,
                                        modifier = Modifier.fillMaxWidth().testTag("cust_field_${field.id}")
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Order Summary Totals Card
            item {
                GlowingGlassCard(glowColor = EmeraldNeon) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(Localization.get("subtotal", lang), color = TextSecondary, fontSize = 13.sp)
                            Text(FormatUtils.formatMoney(subtotal, settings.currencySymbol), color = TextPrimary, fontSize = 13.sp)
                        }

                        if (discountAmount > 0) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = Localization.get("discount", lang) + if (isPercentDisc) " ($discountVal%)" else "",
                                    color = RoseNeon,
                                    fontSize = 13.sp
                                )
                                Text("-${FormatUtils.formatMoney(discountAmount, settings.currencySymbol)}", color = RoseNeon, fontSize = 13.sp)
                            }
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color.White.copy(alpha = 0.1f))
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = Localization.get("grand_total", lang),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = FormatUtils.formatMoney(grandTotal, settings.currencySymbol),
                                color = EmeraldLight,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Action Button: Complete Sale & Generate Receipt
            item {
                GlassPrimaryButton(
                    text = Localization.get("btn_preview_receipt", lang),
                    icon = Icons.Default.Receipt,
                    modifier = Modifier.fillMaxWidth().testTag("generate_receipt_btn"),
                    onClick = {
                        viewModel.generateReceiptAndProcessSale { saleWithItems ->
                            onCompleteSale(saleWithItems)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CartItemRow(
    item: CartItem,
    currencySymbol: String,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.productName,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${FormatUtils.formatMoney(item.unitPrice, currencySymbol)} × ${item.quantity} = ${FormatUtils.formatMoney(item.totalPrice, currencySymbol)}",
                    color = EmeraldLight,
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Stepper
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A))
                        .clickable(onClick = onDecrease),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Minus",
                        tint = TextPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = "${item.quantity}",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldNeon.copy(alpha = 0.3f))
                        .clickable(onClick = onIncrease),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Plus",
                        tint = EmeraldLight,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
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
