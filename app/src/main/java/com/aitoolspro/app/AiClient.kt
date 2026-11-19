package com.aitoolspro.app

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class ChatMessage(
    val role: String,
    val content: String
)

object AiClient {
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun complete(
        context: Context,
        systemPrompt: String,
        messages: List<ChatMessage>
    ): String = withContext(Dispatchers.IO) {
        val config = SecureSettings.loadProvider(context)
        require(config.apiKey.isNotBlank()) {
            "API key belum diatur. Buka Pengaturan lalu hubungkan penyedia AI."
        }
        require(config.model.isNotBlank()) {
            "Nama model belum diisi di Pengaturan."
        }
        require(config.baseUrl.startsWith("https://", ignoreCase = true)) {
            "Endpoint harus menggunakan HTTPS."
        }

        val endpoint = if (config.baseUrl.endsWith("/chat/completions")) {
            config.baseUrl
        } else {
            config.baseUrl.trimEnd('/') + "/chat/completions"
        }

        val requestMessages = JSONArray()
            .put(JSONObject().put("role", "system").put("content", systemPrompt))
        messages.forEach { message ->
            requestMessages.put(
                JSONObject()
                    .put("role", message.role)
                    .put("content", message.content)
            )
        }

        val payload = JSONObject()
            .put("model", config.model)
            .put("messages", requestMessages)
            .put("temperature", 0.7)

        val request = Request.Builder()
            .url(endpoint)
            .header("Authorization", "Bearer ${config.apiKey}")
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")
            .post(payload.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        httpClient.newCall(request).execute().use { response ->
            val bodyText = response.body?.string().orEmpty()
            val trimmedBody = bodyText.trimStart()
            val looksLikeHtml = trimmedBody.startsWith("<!DOCTYPE", ignoreCase = true) ||
                trimmedBody.startsWith("<html", ignoreCase = true)

            if (looksLikeHtml) {
                throw IllegalStateException(
                    "Server mengirim halaman HTML, bukan JSON API. Periksa Base URL. " +
                        "Untuk OpenRouter gunakan https://openrouter.ai/api/v1 (tanpa /chat/completions)."
                )
            }

            val json = try {
                JSONObject(bodyText)
            } catch (_: JSONException) {
                throw IllegalStateException(
                    "Respons server bukan JSON yang valid (HTTP ${response.code}). " +
                        "Periksa Base URL dan pastikan penyedia mendukung endpoint OpenAI-compatible."
                )
            }

            if (!response.isSuccessful) {
                val errorObject = json.opt("error")
                val detail = when (errorObject) {
                    is JSONObject -> errorObject.optString("message")
                    is String -> errorObject
                    else -> json.optString("message")
                }
                throw IllegalStateException(
                    detail.takeIf { it.isNotBlank() && it != "null" }
                        ?: "Permintaan AI gagal (HTTP ${response.code}). Periksa model, API key, dan kuota provider."
                )
            }

            val choices = json.optJSONArray("choices")
                ?: throw IllegalStateException(
                    "Respons provider tidak memiliki data 'choices'. Periksa apakah model dan endpoint mendukung Chat Completions."
                )
            val first = choices.optJSONObject(0)
                ?: throw IllegalStateException("Provider mengembalikan respons kosong.")
            val message = first.optJSONObject("message")
            val content = when (val value = message?.opt("content")) {
                is String -> value
                is JSONArray -> value.toString()
                else -> ""
            }
            content.ifBlank { "Provider tidak mengembalikan teks. Coba model lain." }
        }
    }
}
