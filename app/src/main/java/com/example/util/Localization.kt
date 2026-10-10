package com.example.util

object Localization {
    private val strings = mapOf(
        // App titles & nav
        "app_name" to Pair("GlassPOS", "গ্লাস পিওএস"),
        "nav_home" to Pair("Dashboard", "ড্যাশবোর্ড"),
        "nav_pos" to Pair("Quick Sale", "দ্রুত বিক্রি"),
        "nav_inventory" to Pair("Products", "পণ্য তালিকা"),
        "nav_history" to Pair("History", "বিক্রয় ইতিহাস"),
        "nav_reports" to Pair("Reports", "রিপোর্ট"),
        "nav_settings" to Pair("Settings", "সেটিংস"),
        "nav_backup" to Pair("Data & Backup", "ডেটা ও ব্যাকআপ"),

        // Dashboard
        "dashboard_title" to Pair("POS Dashboard", "পিওএস ড্যাশবোর্ড"),
        "dashboard_subtitle" to Pair("Offline Smart Retail Management", "স্মার্ট অফলাইন বিক্রয় ব্যবস্থাপনা"),
        "total_sales_today" to Pair("Today's Sales", "আজকের বিক্রি"),
        "total_sales_all_time" to Pair("All-Time Sales", "সর্বমোট বিক্রি"),
        "total_transactions" to Pair("Transactions", "মোট লেনদেন"),
        "total_products" to Pair("Products in Stock", "মোট পণ্য"),
        "low_stock_alert" to Pair("Low Stock Warning", "কম স্টকের সতর্কতা"),
        "items_low_stock" to Pair("products running low on stock", "টি পণ্যে স্টক শেষ হয়ে আসছে"),
        "quick_actions" to Pair("Quick Actions", "দ্রুত অ্যাকশন"),
        "action_new_sale" to Pair("Start Sale", "নতুন বিক্রি"),
        "action_add_product" to Pair("Add Product", "পণ্য যোগ করুন"),
        "action_view_reports" to Pair("Analytics", "রিপোর্ট ও অ্যানালিটিক্স"),
        "action_data_backup" to Pair("Backup & Sync", "ডেটা ব্যাকআপ"),
        "recent_sales" to Pair("Recent Invoices", "সাম্প্রতিক বিক্রয়সমূহ"),
        "no_sales_yet" to Pair("No sales recorded yet. Click 'Start Sale' to begin!", "এখনো কোনো বিক্রি হয়নি। শুরু করতে 'নতুন বিক্রি' চাপুন!"),

        // POS Sale Screen
        "pos_title" to Pair("Point of Sale (POS)", "পয়েন্ট অব সেল (পিওএস)"),
        "search_products" to Pair("Search items by name, barcode...", "নাম বা বারকোড দিয়ে খুঁজুন..."),
        "categories" to Pair("Categories", "ক্যাটাগরি"),
        "all" to Pair("All", "সব"),
        "stock" to Pair("Stock", "স্টক"),
        "out_of_stock" to Pair("Out of Stock", "স্টক নেই"),
        "cart_title" to Pair("Current Order Cart", "বর্তমান অর্ডার কার্ট"),
        "cart_empty" to Pair("Cart is empty. Tap products to add.", "কার্ট খালি। যোগ করতে পণ্যে চাপুন।"),
        "items_in_cart" to Pair("Items in Cart", "কার্টের আইটেম"),
        "subtotal" to Pair("Subtotal", "সাবটোটাল"),
        "discount" to Pair("Discount", "ছাড় / ডিসকাউন্ট"),
        "discount_type_percent" to Pair("Percentage (%)", "শতাংশ (%)"),
        "discount_type_fixed" to Pair("Fixed Amount", "নির্দিষ্ট টাকা"),
        "discount_value" to Pair("Discount Value", "ডিসকাউন্টের পরিমাণ"),
        "grand_total" to Pair("Grand Total", "সর্বমোট প্রদেয়"),
        "customer_info" to Pair("Customer Details", "ক্রেতার তথ্য"),
        "payment_method" to Pair("Payment Method", "পেমেন্ট মাধ্যম"),
        "payment_cash" to Pair("Cash", "নগদ (Cash)"),
        "payment_card" to Pair("Card / POS", "কার্ড (Card)"),
        "payment_mobile" to Pair("Mobile Banking (bKash/Nagad)", "মোবাইল ব্যাংকিং"),
        "btn_preview_receipt" to Pair("Preview & Print Receipt", "রিসিট দেখুন ও প্রিন্ট"),
        "btn_clear_cart" to Pair("Clear Cart", "কার্ট খালি করুন"),
        "validation_required_field" to Pair("is required! Please fill it before generating receipt.", "আবশ্যক! রিসিট জেনারেটের আগে পূরণ করুন।"),
        "validation_stock_limit" to Pair("Cannot add more than available stock!", "উপলব্ধ স্টকের চেয়ে বেশি যোগ করা যাবে না!"),
        "sale_success_msg" to Pair("Sale processed successfully! Stock updated.", "বিক্রি সফলভাবে সম্পন্ন হয়েছে ও স্টক আপডেট হয়েছে!"),

        // Receipt
        "receipt_title" to Pair("Sales Receipt", "বিক্রয় রিসিট"),
        "invoice_no" to Pair("Invoice #", "ইনভয়েস নং"),
        "date_time" to Pair("Date & Time", "তারিখ ও সময়"),
        "bill_to" to Pair("Bill To", "ক্রেতার বিবরণ"),
        "item_col" to Pair("Item", "পণ্য"),
        "qty_col" to Pair("Qty", "পরিমাণ"),
        "price_col" to Pair("Price", "মূল্য"),
        "total_col" to Pair("Total", "মোট"),
        "authorized_signature" to Pair("Authorized Signature", "অনুমোদিত স্বাক্ষর"),
        "btn_print" to Pair("Print Receipt", "প্রিন্ট করুন"),
        "btn_share_pdf" to Pair("Share PDF", "পিডিএফ শেয়ার"),
        "btn_complete_sale" to Pair("Confirm & Save Sale", "বিক্রি নিশ্চিত করুন"),
        "receipt_saved" to Pair("Sale Saved to Database", "বিক্রি ডাটাবেজে সংরক্ষিত"),

        // Inventory / Products
        "inventory_title" to Pair("Inventory Management", "পণ্য ও স্টক ব্যবস্থাপনা"),
        "add_new_product" to Pair("Add New Product", "নতুন পণ্য যোগ করুন"),
        "edit_product" to Pair("Edit Product", "পণ্য সম্পাদনা"),
        "delete_product" to Pair("Delete Product", "পণ্য মুছে ফেলুন"),
        "product_name" to Pair("Product Name", "পণ্যের নাম"),
        "selling_price" to Pair("Selling Price", "বিক্রয় মূল্য"),
        "stock_quantity" to Pair("Stock Quantity", "স্টকের পরিমাণ"),
        "category" to Pair("Category (e.g. Grocery, Snacks)", "ক্যাটাগরি"),
        "sku_barcode" to Pair("SKU / Barcode (Optional)", "বারকোড / এসকেইউ"),
        "btn_save" to Pair("Save Product", "পণ্য সংরক্ষণ করুন"),
        "btn_update" to Pair("Update Product", "আপডেট করুন"),
        "btn_cancel" to Pair("Cancel", "বাতিল"),
        "confirm_delete_product" to Pair("Are you sure you want to delete this product?", "আপনি কি নিশ্চিতভাবে এই পণ্যটি মুছে ফেলতে চান?"),
        "no_products_found" to Pair("No products found in inventory.", "ইনভেন্টরিতে কোনো পণ্য পাওয়া যায়নি।"),

        // History
        "history_title" to Pair("Sales History", "বিক্রয় ইতিহাস"),
        "search_history" to Pair("Search by invoice, customer, item...", "ইনভয়েস, ক্রেতার নাম বা ফোন দিয়ে খুঁজুন..."),
        "delete_history_item" to Pair("Delete Record", "রেকর্ড মুছুন"),
        "clear_all_history" to Pair("Clear All History", "সমগ্র ইতিহাস মুছুন"),
        "confirm_clear_all" to Pair("Are you sure you want to clear ALL sales history? This action cannot be undone.", "আপনি কি নিশ্চিতভাবে সকল বিক্রয় ইতিহাস মুছে ফেলতে চান? এটি আর ফিরিয়ে আনা যাবে না।"),
        "view_details" to Pair("View Receipt", "রিসিট দেখুন"),
        "no_history_found" to Pair("No sales records found.", "কোনো বিক্রয় ইতিহাস পাওয়া যায়নি।"),

        // Reports
        "reports_title" to Pair("Reports & Analytics", "রিপোর্ট ও অ্যানালিটিক্স"),
        "report_all_time" to Pair("All Time", "সম্পূর্ণ (সব সময়)"),
        "report_today" to Pair("Today", "আজকের"),
        "report_this_month" to Pair("This Month", "এই মাসের"),
        "report_custom" to Pair("Custom Range", "কাস্টম রেঞ্জ"),
        "total_revenue" to Pair("Total Revenue", "মোট বিক্রি"),
        "total_discount_given" to Pair("Total Discounts", "মোট ছাড়"),
        "avg_sale_value" to Pair("Average Sale", "গড় বিক্রি"),
        "top_selling_items" to Pair("Top Selling Products", "সর্বাধিক বিক্রিত পণ্যসমূহ"),
        "export_json_report" to Pair("Copy JSON Report", "রিপোর্ট কপি (JSON)"),
        "download_pdf_report" to Pair("Print / PDF Report", "রিপোর্ট প্রিন্ট ও পিডিএফ"),
        "report_copied" to Pair("Report JSON copied to clipboard!", "রিপোর্টের JSON ক্লিপবোর্ডে কপি হয়েছে!"),

        // Data & Backup
        "backup_title" to Pair("Data Backup & Restore", "ডেটা ব্যাকআপ ও রিস্টোর"),
        "backup_desc" to Pair("All your data is safely stored 100% offline on your device. Export JSON backups or restore from previously saved files.", "আপনার সকল তথ্য শতভাগ অফলাইনে ডিভাইসে সংরক্ষিত। ব্যাকআপ ফাইল এক্সপোর্ট বা রিস্টোর করুন।"),
        "btn_export_json" to Pair("Export Full Backup (JSON)", "সম্পূর্ণ ব্যাকআপ এক্সপোর্ট (JSON)"),
        "btn_copy_json" to Pair("Copy JSON to Clipboard", "ক্লিপবোর্ডে কপি করুন"),
        "btn_import_json" to Pair("Import & Restore Backup", "ব্যাকআপ ইম্পোর্ট ও রিস্টোর"),
        "paste_json_hint" to Pair("Paste exported backup JSON here to restore database...", "পূর্বে এক্সপোর্ট করা ব্যাকআপ JSON এখানে পেস্ট করুন..."),
        "btn_reset_sample" to Pair("Load Demo Store Data", "ডেমো পণ্য লোড করুন"),
        "export_copied_success" to Pair("Full database backup JSON copied!", "ডাটাবেজ ব্যাকআপ JSON সফলভাবে কপি হয়েছে!"),
        "restore_success" to Pair("Database restored successfully!", "ডাটাবেজ সফলভাবে রিস্টোর হয়েছে!"),
        "restore_error" to Pair("Invalid backup format or error restoring.", "ভুল ব্যাকআপ ফরম্যাট বা রিস্টোরে ত্রুটি হয়েছে।"),

        // Settings
        "settings_title" to Pair("POS Settings", "পিওএস সেটিংস"),
        "shop_info_header" to Pair("Shop Profile", "দোকানের প্রোফাইল"),
        "shop_name" to Pair("Shop / Business Name", "দোকান বা প্রতিষ্ঠানের নাম"),
        "shop_phone" to Pair("Contact Phone Number", "যোগাযোগের ফোন নম্বর"),
        "shop_address" to Pair("Shop Address", "দোকানের ঠিকানা"),
        "receipt_tagline" to Pair("Receipt Footer Note", "রিসিটের ফুটার বার্তা"),
        "currency_header" to Pair("Currency Configuration", "মুদ্রা নির্বাচন"),
        "currency_search" to Pair("Search Currency (e.g. BDT, USD, INR)...", "মুদ্রা খুঁজুন..."),
        "owner_signature" to Pair("Owner / Authorized Signer", "স্বাক্ষর (মালিকের নাম)"),
        "customer_fields_header" to Pair("Sales Form Required Fields", "প্রয়োজনীয় ক্ষেত্রসমূহ (Customer Fields)"),
        "customer_fields_desc" to Pair("Customize which customer fields appear in the sale form and toggle mandatory (required) status.", "সেলস ফর্মে কোন তথ্যগুলো থাকবে এবং আবশ্যক কিনা তা নির্ধারণ করুন।"),
        "field_name" to Pair("Field Name", "ক্ষেত্রের নাম"),
        "field_required" to Pair("Required (আবশ্যক)", "আবশ্যক (Required)"),
        "field_enabled" to Pair("Show in Form", "ফর্মে প্রদর্শন"),
        "add_custom_field" to Pair("Add New Field", "নতুন ফিল্ড যোগ করুন"),
        "language_header" to Pair("Application Language", "অ্যাপ্লিকেশনের ভাষা"),
        "btn_save_settings" to Pair("Save All Settings", "সেটিংস সংরক্ষণ করুন"),
        "settings_saved_msg" to Pair("Settings updated successfully!", "সেটিংস সফলভাবে সংরক্ষিত হয়েছে!"),

        // Developer Info
        "developer_info_btn" to Pair("Developer Information", "ডেভেলপার তথ্য"),
        "dev_name" to Pair("Mehadi Hasan", "মেহেদী হাসান"),
        "dev_title" to Pair("Full-Stack & Mobile Developer", "ফুল-স্ট্যাক ও মোবাইল ডেভেলপার"),
        "dev_bio" to Pair("Passionate software engineer specializing in offline-first POS architectures, sleek glassmorphism UI/UX, and robust Kotlin Android applications.", "পেশাদার সফটওয়্যার প্রকৌশলী, যিনি অফলাইন পিওএস আর্কিটেকচার, আধুনিক গ্লাসমরফিজম ডিজাইন ও উচ্চমানের অ্যান্ড্রয়েড অ্যাপ্লিকেশনে পারদর্শী।"),
        "dev_whatsapp" to Pair("WhatsApp Contact", "হোয়াটসঅ্যাপ যোগাযোগ"),
        "dev_whatsapp_handle" to Pair("wa.me/@devmehadihasan", "wa.me/@devmehadihasan"),
        "dev_close" to Pair("Close", "বন্ধ করুন")
    )

    fun get(key: String, lang: String): String {
        val pair = strings[key] ?: return key
        return if (lang == "bn") pair.second else pair.first
    }
}
