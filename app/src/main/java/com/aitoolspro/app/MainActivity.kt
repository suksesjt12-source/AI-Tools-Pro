package com.aitoolspro.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val AppBackground = Color(0xFF080D18)
private val PanelBackground = Color(0xFF111B2D)
private val PanelRaised = Color(0xFF17243A)
private val AccentBlue = Color(0xFF4C91FF)
private val AccentCyan = Color(0xFF45D5E8)
private val MainText = Color(0xFFF3F7FF)
private val MutedText = Color(0xFF9BAAC1)

private data class AiTool(
    val id: String,
    val nameId: String,
    val nameEn: String,
    val descId: String,
    val descEn: String,
    val icon: ImageVector,
    val systemPrompt: String,
    val hintId: String
)

private val tools = listOf(
    AiTool(
        "chat", "Chat AI", "AI Chat", "Asisten tanya jawab umum", "General question assistant",
        Icons.Default.ChatBubbleOutline,
        "You are AI Tools Pro, a clear and helpful general-purpose assistant. Answer accurately, state uncertainty, and use the user's language.",
        "Tulis pertanyaanmu..."
    ),
    AiTool(
        "writer", "AI Writer", "AI Writer", "Membuat dan menyunting tulisan", "Write and edit content",
        Icons.Default.Edit,
        "You are an expert writing assistant. Help draft, edit, outline, and improve writing. Match the requested tone and language.",
        "Tuliskan jenis teks yang ingin dibuat..."
    ),
    AiTool(
        "coding", "AI Coding", "AI Coding", "Bantuan kode dan debugging", "Code and debugging help",
        Icons.Default.Code,
        "You are a careful software engineering assistant. Provide usable code, explain assumptions, include error handling, and never invent test results.",
        "Jelaskan kode atau masalah pemrograman..."
    ),
    AiTool(
        "translator", "Penerjemah", "Translator", "Terjemahkan berbagai bahasa", "Translate between languages",
        Icons.Default.Translate,
        "You are a professional translator. Preserve meaning, tone, names, and formatting. Ask for a target language only if it is unclear.",
        "Masukkan teks dan bahasa tujuan..."
    ),
    AiTool(
        "math", "AI Math Solver", "AI Math Solver", "Penjelasan langkah demi langkah", "Step-by-step explanations",
        Icons.Default.Calculate,
        "You are a mathematics tutor. Solve carefully, show the important steps, and explain the reasoning in an age-appropriate way.",
        "Masukkan soal matematika..."
    ),
    AiTool(
        "pdf", "PDF Assistant", "PDF Assistant", "Ringkas teks dokumen", "Summarize document text",
        Icons.Default.Description,
        "You are a document assistant. Summarize supplied text, extract key points, explain sections, and clearly say when source text is missing. File upload parsing is not enabled in this MVP.",
        "Tempelkan teks dari dokumen PDF..."
    ),
    AiTool(
        "image", "AI Image Ideas", "AI Image Ideas", "Buat konsep dan prompt gambar", "Create image concepts and prompts",
        Icons.Default.Image,
        "You are a visual concept assistant. Write detailed image-generation prompts and suggest composition, lighting, and style. Actual image rendering requires a connected image-generation provider and is not included in this MVP.",
        "Jelaskan gambar yang ingin kamu konsep..."
    ),
    AiTool(
        "voice", "AI Voice", "AI Voice", "Bantuan naskah dan ide suara", "Voice script assistant",
        Icons.Default.Mic,
        "You are a voice assistant for preparing spoken scripts, summaries, and pronunciation notes. Live microphone transcription and speech synthesis are not enabled in this MVP.",
        "Tuliskan naskah atau kebutuhan suaramu..."
    ),
    AiTool(
        "research", "AI Research", "AI Research", "Susun analisis dan riset", "Structure research and analysis",
        Icons.Default.Science,
        "You are a research assistant. Structure the question, distinguish evidence from assumptions, cite sources only when supplied, and never fabricate references. Live web retrieval is not enabled in this MVP.",
        "Topik apa yang ingin kamu teliti..."
    ),
    AiTool(
        "web", "AI Web Search", "AI Web Search", "Bantu merancang pencarian web", "Help structure web searches",
        Icons.Default.Search,
        "You are a web research planner. Suggest focused search queries, explain what evidence to verify, and summarize provided web excerpts. Live web search integration is not enabled in this MVP.",
        "Apa informasi yang ingin kamu cari..."
    )
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AiToolsProTheme {
                AiToolsProApp()
            }
        }
    }
}

@Composable
private fun AiToolsProTheme(content: @Composable () -> Unit) {
    val colors = androidx.compose.material3.darkColorScheme(
        primary = AccentBlue,
        secondary = AccentCyan,
        background = AppBackground,
        surface = PanelBackground,
        onPrimary = Color.White,
        onBackground = MainText,
        onSurface = MainText
    )
    MaterialTheme(colorScheme = colors) {
        Surface(modifier = Modifier.fillMaxSize(), color = AppBackground) {
            content()
        }
    }
}

@Composable
private fun AiToolsProApp() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var language by remember { mutableStateOf(SecureSettings.getLanguage(context)) }
    var selectedTab by remember { mutableStateOf("home") }
    var selectedTool by remember { mutableStateOf<AiTool?>(null) }
    var messages by remember { mutableStateOf(emptyList<ChatMessage>()) }
    var isLoading by remember { mutableStateOf(false) }
    var config by remember { mutableStateOf(SecureSettings.loadProvider(context)) }
    var showProviderDialog by remember { mutableStateOf(false) }
    var history by remember { mutableStateOf(HistoryStore.load(context)) }

    BackHandler(enabled = selectedTool != null) {
        selectedTool = null
        messages = emptyList()
    }

    fun sendMessage(prompt: String) {
        val tool = selectedTool ?: return
        if (prompt.isBlank() || isLoading) return
        val updated = messages + ChatMessage("user", prompt.trim())
        messages = updated
        isLoading = true
        scope.launch {
            try {
                val answer = AiClient.complete(context, tool.systemPrompt, updated)
                messages = messages + ChatMessage("assistant", answer)
                HistoryStore.add(context, prompt.trim(), answer)
                history = HistoryStore.load(context)
            } catch (error: Exception) {
                messages = messages + ChatMessage(
                    "assistant",
                    "Tidak dapat menyelesaikan permintaan. ${error.message ?: "Periksa koneksi dan pengaturan provider."}"
                )
            } finally {
                isLoading = false
            }
        }
    }

    if (selectedTool != null) {
        ChatScreen(
            tool = selectedTool!!,
            language = language,
            messages = messages,
            isLoading = isLoading,
            onBack = {
                selectedTool = null
                messages = emptyList()
            },
            onSend = ::sendMessage,
            onOpenSettings = { showProviderDialog = true }
        )
    } else {
        Scaffold(
            containerColor = AppBackground,
            contentWindowInsets = WindowInsets.navigationBars,
            bottomBar = {
                NavigationBar(containerColor = PanelBackground, contentColor = MainText) {
                    NavigationBarItem(
                        selected = selectedTab == "home",
                        onClick = { selectedTab = "home" },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = null) },
                        label = { Text(if (language == "id") "Beranda" else "Home") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "history",
                        onClick = {
                            history = HistoryStore.load(context)
                            selectedTab = "history"
                        },
                        icon = { Icon(Icons.Default.History, contentDescription = null) },
                        label = { Text(if (language == "id") "Riwayat" else "History") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "settings",
                        onClick = { selectedTab = "settings" },
                        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                        label = { Text(if (language == "id") "Pengaturan" else "Settings") }
                    )
                }
            }
        ) { innerPadding ->
            when (selectedTab) {
                "history" -> HistoryScreen(
                    history = history,
                    language = language,
                    modifier = Modifier.padding(innerPadding)
                )
                "settings" -> SettingsScreen(
                    language = language,
                    config = config,
                    onLanguageChange = {
                        language = it
                        SecureSettings.setLanguage(context, it)
                    },
                    onProviderClick = { showProviderDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
                else -> HomeScreen(
                    language = language,
                    providerReady = config.apiKey.isNotBlank() && config.model.isNotBlank(),
                    onToolClick = { tool ->
                        selectedTool = tool
                        messages = emptyList()
                    },
                    onSettingsClick = { showProviderDialog = true },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    if (showProviderDialog) {
        ProviderDialog(
            initialConfig = config,
            language = language,
            onDismiss = { showProviderDialog = false },
            onSave = { baseUrl, model, apiKey ->
                SecureSettings.saveProvider(context, baseUrl, model, apiKey)
                config = SecureSettings.loadProvider(context)
                showProviderDialog = false
            }
        )
    }
}

@Composable
private fun HomeScreen(
    language: String,
    providerReady: Boolean,
    onToolClick: (AiTool) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 22.dp, end = 14.dp, top = 22.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("AI TOOLS PRO", color = AccentCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(
                    if (language == "id") "Satu ruang, banyak kemungkinan." else "One space, many possibilities.",
                    color = MainText,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            IconButton(onClick = onSettingsClick) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MainText)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .clickable(onClick = onSettingsClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = if (providerReady) Color(0xFF102C37) else PanelRaised)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 13.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (providerReady) Color(0xFF1B655D) else Color(0xFF263B5A)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = if (providerReady) AccentCyan else AccentBlue)
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        if (providerReady) {
                            if (language == "id") "Penyedia AI terhubung" else "AI provider configured"
                        } else {
                            if (language == "id") "Hubungkan penyedia AI" else "Connect an AI provider"
                        },
                        color = MainText,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        if (providerReady) configLabel(language, true) else if (language == "id") "Atur endpoint, model, dan API key milikmu" else "Set your endpoint, model, and API key",
                        color = MutedText,
                        fontSize = 12.sp
                    )
                }
                Text(if (language == "id") "Atur" else "Setup", color = AccentCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Text(
            if (language == "id") "Pilih alat AI" else "Choose an AI tool",
            color = MainText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 20.dp, top = 18.dp, bottom = 10.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 4.dp, bottom = 18.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tools) { tool ->
                ToolCard(tool = tool, language = language, onClick = { onToolClick(tool) })
            }
        }
    }
}

private fun configLabel(language: String, ready: Boolean): String =
    if (!ready) "" else if (language == "id") "Chat siap digunakan" else "Chat is ready"

@Composable
private fun ToolCard(tool: AiTool, language: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(144.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PanelBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF1B3152)),
                contentAlignment = Alignment.Center
            ) {
                Icon(tool.icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(23.dp))
            }
            Column {
                Text(if (language == "id") tool.nameId else tool.nameEn, color = MainText, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(3.dp))
                Text(if (language == "id") tool.descId else tool.descEn, color = MutedText, fontSize = 11.sp, lineHeight = 15.sp)
            }
        }
    }
}

@Composable
private fun ChatScreen(
    tool: AiTool,
    language: String,
    messages: List<ChatMessage>,
    isLoading: Boolean,
    onBack: () -> Unit,
    onSend: (String) -> Unit,
    onOpenSettings: () -> Unit
) {
    var input by remember(tool.id) { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 8.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MainText)
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(if (language == "id") tool.nameId else tool.nameEn, color = MainText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text("AI Tools Pro", color = MutedText, fontSize = 11.sp)
            }
            IconButton(onClick = onOpenSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Provider settings", tint = AccentCyan)
            }
        }
        Divider(color = Color(0xFF26344A))
        if (messages.isEmpty()) {
            Column(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(26.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier.size(74.dp).clip(RoundedCornerShape(24.dp)).background(Color(0xFF1B3152)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(tool.icon, contentDescription = null, tint = AccentCyan, modifier = Modifier.size(36.dp))
                }
                Spacer(Modifier.height(18.dp))
                Text(if (language == "id") "Mulai dengan ${tool.nameId}" else "Start with ${tool.nameEn}", color = MainText, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                Text(
                    if (language == "id") "Tulis permintaanmu. Jawaban AI akan muncul di sini." else "Write your request. The AI response will appear here.",
                    color = MutedText,
                    fontSize = 13.sp
                )
                if (tool.id in listOf("pdf", "image", "voice", "research", "web")) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        if (language == "id") "Catatan: fitur khusus file, media, dan pencarian langsung memerlukan integrasi provider tambahan." else "Note: dedicated file, media, and live search features require additional provider integrations.",
                        color = Color(0xFFFFC66D),
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message)
                }
                if (isLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = AccentCyan, strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text(if (language == "id") "AI sedang menyiapkan jawaban..." else "AI is preparing a response...", color = MutedText, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text(if (language == "id") tool.hintId else "Type your request...", color = MutedText) },
                shape = RoundedCornerShape(18.dp),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = Color(0xFF35445D),
                    focusedTextColor = MainText,
                    unfocusedTextColor = MainText,
                    cursorColor = AccentCyan
                )
            )
            Spacer(Modifier.width(8.dp))
            Button(
                onClick = {
                    val value = input.trim()
                    if (value.isNotEmpty() && !isLoading) {
                        onSend(value)
                        input = ""
                    }
                },
                enabled = input.isNotBlank() && !isLoading,
                modifier = Modifier.size(54.dp),
                contentPadding = PaddingValues(0.dp),
                shape = RoundedCornerShape(17.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentBlue)
            ) {
                Icon(Icons.Default.Send, contentDescription = if (language == "id") "Kirim" else "Send", tint = Color.White)
            }
        }
    }
}

@Composable
private fun MessageBubble(message: ChatMessage) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.88f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isUser) Color(0xFF1A4F91) else PanelBackground
            )
        ) {
            Text(
                text = message.content,
                color = MainText,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun HistoryScreen(history: List<HistoryItem>, language: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 22.dp)) {
        Text(if (language == "id") "Riwayat lokal" else "Local history", color = MainText, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Text(
            if (language == "id") "Percakapan tersimpan di perangkat ini." else "Conversations saved on this device.",
            color = MutedText,
            fontSize = 12.sp
        )
        Spacer(Modifier.height(16.dp))
        if (history.isEmpty()) {
            Text(if (language == "id") "Belum ada riwayat." else "No history yet.", color = MutedText)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(history) { item ->
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PanelBackground)) {
                        Column(Modifier.fillMaxWidth().padding(14.dp)) {
                            Text(item.prompt, color = MainText, fontWeight = FontWeight.SemiBold, maxLines = 2)
                            Spacer(Modifier.height(5.dp))
                            Text(item.answerPreview, color = MutedText, fontSize = 12.sp, maxLines = 3)
                            Spacer(Modifier.height(6.dp))
                            Text(
                                SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date(item.createdAt)),
                                color = AccentCyan,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsScreen(
    language: String,
    config: ProviderConfig,
    onLanguageChange: (String) -> Unit,
    onProviderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize().padding(20.dp)) {
        Text(if (language == "id") "Pengaturan" else "Settings", color = MainText, fontSize = 23.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = PanelBackground)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(if (language == "id") "Bahasa aplikasi" else "App language", color = MainText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { onLanguageChange("id") }, colors = ButtonDefaults.buttonColors(containerColor = if (language == "id") AccentBlue else PanelRaised)) {
                        Text("Indonesia")
                    }
                    Button(onClick = { onLanguageChange("en") }, colors = ButtonDefaults.buttonColors(containerColor = if (language == "en") AccentBlue else PanelRaised)) {
                        Text("English")
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onProviderClick),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = PanelBackground)
        ) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(if (language == "id") "Penyedia AI" else "AI provider", color = MainText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(config.baseUrl, color = MutedText, fontSize = 12.sp)
                Text(if (config.apiKey.isNotBlank() && config.model.isNotBlank()) if (language == "id") "Terkonfigurasi" else "Configured" else if (language == "id") "Belum dikonfigurasi" else "Not configured", color = AccentCyan, fontSize = 12.sp)
                Spacer(Modifier.height(10.dp))
                Text(if (language == "id") "Ketuk untuk mengubah endpoint, model, dan API key." else "Tap to change endpoint, model, and API key.", color = MutedText, fontSize = 12.sp)
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = PanelBackground)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(if (language == "id") "Privasi dan batas layanan" else "Privacy and service limits", color = MainText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Text(
                    if (language == "id") "Kunci API dienkripsi menggunakan Android Keystore dan disimpan pada perangkat. Aplikasi tidak menyediakan API gratis tanpa batas dan tidak melewati limit provider. Periksa harga serta kuota penyedia yang kamu pilih." else "The API key is encrypted with Android Keystore and stored on this device. The app does not provide unlimited free API access or bypass provider limits. Check the pricing and quotas of your chosen provider.",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = PanelBackground)) {
            Column(Modifier.fillMaxWidth().padding(16.dp)) {
                Text(if (language == "id") "Akun dan sinkronisasi" else "Account and sync", color = MainText, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(
                    if (language == "id") "Login dan sinkronisasi cloud belum tersedia pada versi awal. Riwayat saat ini hanya disimpan secara lokal." else "Sign-in and cloud sync are not available in this initial version. History is currently stored locally only.",
                    color = MutedText,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun ProviderDialog(
    initialConfig: ProviderConfig,
    language: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var baseUrl by remember { mutableStateOf(initialConfig.baseUrl) }
    var model by remember { mutableStateOf(initialConfig.model) }
    var apiKey by remember { mutableStateOf(initialConfig.apiKey) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelRaised,
        title = { Text(if (language == "id") "Hubungkan penyedia AI" else "Connect AI provider", color = MainText) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    if (language == "id") "Gunakan endpoint OpenAI-compatible HTTPS. Contoh endpoint dasar: https://openrouter.ai/api/v1" else "Use an OpenAI-compatible HTTPS endpoint. Example base endpoint: https://openrouter.ai/api/v1",
                    color = MutedText,
                    fontSize = 11.sp
                )
                OutlinedTextField(value = baseUrl, onValueChange = { baseUrl = it }, label = { Text("Base URL") }, singleLine = true)
                OutlinedTextField(value = model, onValueChange = { model = it }, label = { Text(if (language == "id") "ID model" else "Model ID") }, singleLine = true)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    label = { Text("API key") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                )
                if (validationError != null) {
                    Text(validationError!!, color = Color(0xFFFF8D8D), fontSize = 11.sp)
                }
                Text(
                    if (language == "id") "Kunci dienkripsi di perangkat. Jangan kirim atau bagikan kunci API kepada orang lain." else "The key is encrypted on this device. Do not share your API key.",
                    color = MutedText,
                    fontSize = 10.sp
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                try {
                    require(baseUrl.trim().startsWith("https://", ignoreCase = true)) { "Base URL harus menggunakan HTTPS." }
                    require(model.isNotBlank()) { if (language == "id") "ID model wajib diisi." else "Model ID is required." }
                    require(apiKey.isNotBlank()) { if (language == "id") "API key wajib diisi." else "API key is required." }
                    onSave(baseUrl, model, apiKey)
                } catch (error: Exception) {
                    validationError = error.message ?: "Input tidak valid."
                }
            }) {
                Text(if (language == "id") "Simpan" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == "id") "Batal" else "Cancel", color = MutedText)
            }
        }
    )
}
