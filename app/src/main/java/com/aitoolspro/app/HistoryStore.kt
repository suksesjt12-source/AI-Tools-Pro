package com.aitoolspro.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class HistoryItem(
    val prompt: String,
    val answerPreview: String,
    val createdAt: Long
)

object HistoryStore {
    private const val PREFS = "ai_tools_pro_history"
    private const val KEY_ITEMS = "items"

    fun load(context: Context): List<HistoryItem> {
        val raw = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_ITEMS, "[]") ?: "[]"
        return try {
            val array = JSONArray(raw)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                HistoryItem(
                    prompt = item.optString("prompt"),
                    answerPreview = item.optString("answer"),
                    createdAt = item.optLong("createdAt")
                )
            }.reversed()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun add(context: Context, prompt: String, answer: String) {
        val items = load(context).take(29).reversed().toMutableList()
        items.add(
            HistoryItem(
                prompt = prompt.take(300),
                answerPreview = answer.take(500),
                createdAt = System.currentTimeMillis()
            )
        )
        val array = JSONArray()
        items.forEach { item ->
            array.put(
                JSONObject()
                    .put("prompt", item.prompt)
                    .put("answer", item.answerPreview)
                    .put("createdAt", item.createdAt)
            )
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_ITEMS, array.toString())
            .apply()
    }
}
