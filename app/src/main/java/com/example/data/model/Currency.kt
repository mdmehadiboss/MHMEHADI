package com.example.data.model

data class Currency(
    val code: String,
    val symbol: String,
    val nameEn: String,
    val nameBn: String
) {
    fun displayName(lang: String): String {
        val name = if (lang == "bn") nameBn else nameEn
        return "$symbol $code ($name)"
    }

    companion object {
        val allCurrencies = listOf(
            Currency("BDT", "৳", "Bangladeshi Taka", "বাংলাদেশি টাকা"),
            Currency("USD", "$", "US Dollar", "মার্কিন ডলার"),
            Currency("EUR", "€", "Euro", "ইউরো"),
            Currency("GBP", "£", "British Pound", "ব্রিটিশ পাউন্ড"),
            Currency("INR", "₹", "Indian Rupee", "ভারতীয় রুপি"),
            Currency("SAR", "﷼", "Saudi Riyal", "সৌদি রিয়াল"),
            Currency("AED", "د.إ", "UAE Dirham", "সংযুক্ত আরব আমিরাত দিরহাম"),
            Currency("MYR", "RM", "Malaysian Ringgit", "মালয়েশিয়ান রিঙ্গিত"),
            Currency("SGD", "S$", "Singapore Dollar", "সিঙ্গাপুর ডলার"),
            Currency("CAD", "C$", "Canadian Dollar", "কানাডিয়ান ডলার"),
            Currency("AUD", "A$", "Australian Dollar", "অস্ট্রেলিয়ান ডলার"),
            Currency("JPY", "¥", "Japanese Yen", "জাপানি ইয়েন"),
            Currency("CNY", "¥", "Chinese Yuan", "চীনা ইউয়ান"),
            Currency("PKR", "₨", "Pakistani Rupee", "পাকিস্তানি রুপি"),
            Currency("QAR", "QR", "Qatari Riyal", "কাতারি রিয়াল"),
            Currency("KWD", "KD", "Kuwaiti Dinar", "কুয়েতি দিনার"),
            Currency("OMR", "OMR", "Omani Rial", "ওমানি রিয়াল"),
            Currency("TRY", "₺", "Turkish Lira", "তুর্কি লিরা"),
            Currency("KRW", "₩", "South Korean Won", "দক্ষিণ কোরিয়ান ওন"),
            Currency("THB", "฿", "Thai Baht", "থাই বাত"),
            Currency("IDR", "Rp", "Indonesian Rupiah", "ইন্দোনেশিয়ান রূপিয়া"),
            Currency("RUB", "₽", "Russian Ruble", "রাশিয়ান রুবেল"),
            Currency("BRL", "R$", "Brazilian Real", "ব্রাজিলিয়ান রিয়াল"),
            Currency("ZAR", "R", "South African Rand", "দক্ষিণ আফ্রিকান র্যান্ড"),
            Currency("EGP", "E£", "Egyptian Pound", "মিশরীয় পাউন্ড"),
            Currency("NPR", "₨", "Nepalese Rupee", "নেপালি রুপি"),
            Currency("LKR", "Rs", "Sri Lankan Rupee", "শ্রীলঙ্কান রুপি")
        )

        fun findByCode(code: String): Currency {
            return allCurrencies.firstOrNull { it.code.equals(code, ignoreCase = true) }
                ?: Currency("BDT", "৳", "Bangladeshi Taka", "বাংলাদেশি টাকা")
        }
    }
}
