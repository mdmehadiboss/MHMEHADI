package com.example.data.model

import org.json.JSONArray
import org.json.JSONObject

data class CustomerField(
    val id: String,
    val labelEn: String,
    val labelBn: String,
    val isRequired: Boolean = false,
    val isEnabled: Boolean = true
) {
    fun getLabel(lang: String): String = if (lang == "bn") labelBn else labelEn

    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("id", id)
        obj.put("labelEn", labelEn)
        obj.put("labelBn", labelBn)
        obj.put("isRequired", isRequired)
        obj.put("isEnabled", isEnabled)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): CustomerField {
            return CustomerField(
                id = obj.optString("id", ""),
                labelEn = obj.optString("labelEn", ""),
                labelBn = obj.optString("labelBn", ""),
                isRequired = obj.optBoolean("isRequired", false),
                isEnabled = obj.optBoolean("isEnabled", true)
            )
        }

        fun defaultFields(): List<CustomerField> {
            return listOf(
                CustomerField("name", "Customer Name", "ক্রেতার নাম", isRequired = true, isEnabled = true),
                CustomerField("phone", "Phone Number", "ফোন নম্বর", isRequired = false, isEnabled = true),
                CustomerField("address", "Address", "ঠিকানা", isRequired = false, isEnabled = true),
                CustomerField("district", "District / Area", "জেলা / এলাকা", isRequired = false, isEnabled = true),
                CustomerField("email", "Email Address", "ইমেইল", isRequired = false, isEnabled = false),
                CustomerField("note", "Special Note", "বিশেষ নোট", isRequired = false, isEnabled = true)
            )
        }

        fun listToJson(list: List<CustomerField>): String {
            val arr = JSONArray()
            list.forEach { arr.put(it.toJson()) }
            return arr.toString()
        }

        fun listFromJson(jsonStr: String): List<CustomerField> {
            if (jsonStr.isBlank()) return defaultFields()
            return try {
                val arr = JSONArray(jsonStr)
                val list = mutableListOf<CustomerField>()
                for (i in 0 until arr.length()) {
                    list.add(fromJson(arr.getJSONObject(i)))
                }
                list
            } catch (e: Exception) {
                defaultFields()
            }
        }
    }
}
