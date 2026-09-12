package com.example.testosteronecalc

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val inputValue: Double,
    val fromUnit: String,
    val outputValue: Double,
    val toUnit: String,
    val timestamp: Long,
    val type: String = "total",
    val shbg: Double? = null,
    val albumin: Double? = null,
    val bioavailable: Double? = null,
    val freePercent: Double? = null,
    val bioPercent: Double? = null
)

class HistoryStorage(context: Context) {
    private val prefs = context.getSharedPreferences("history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun load(): MutableList<HistoryItem> {
        val json = prefs.getString("items", null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<HistoryItem>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun save(items: List<HistoryItem>) {
        prefs.edit().putString("items", gson.toJson(items)).apply()
    }

    fun add(item: HistoryItem) {
        val list = load()
        list.add(0, item)
        if (list.size > 100) list.removeAt(list.lastIndex)
        save(list)
    }

    /** Удалить одну запись по timestamp (он уникален с точностью до мс). */
    fun remove(item: HistoryItem) {
        val list = load()
        list.removeAll { it.timestamp == item.timestamp }
        save(list)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }

    fun formatDate(ts: Long): String =
        SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(ts))
}
