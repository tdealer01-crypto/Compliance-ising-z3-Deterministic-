package com.example.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.BuildConfig
import com.example.data.gemini.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean
)

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val conversationHistory = mutableListOf<Content>()

    fun sendMessage(text: String, isComplex: Boolean = false) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text = text, isUser = true)
        _messages.value = _messages.value + userMsg
        
        conversationHistory.add(Content(parts = listOf(Part(text = text)), role = "user"))
        
        val model = if (isComplex) "gemini-3.1-pro-preview" else "gemini-3.5-flash"
        val config = if (isComplex) GenerationConfig(thinkingConfig = ThinkingConfig("HIGH")) else null
        
        val req = GenerateContentRequest(
            contents = conversationHistory.toList(),
            generationConfig = config,
            systemInstruction = Content(parts = listOf(Part(text = "You are a helpful task manager AI assistant.")), role = "model")
        )
        
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.service.generateContent(
                    model = model,
                    apiKey = BuildConfig.GEMINI_API_KEY,
                    request = req
                )
                
                val replyText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "No response"
                val botMsg = ChatMessage(text = replyText, isUser = false)
                _messages.value = _messages.value + botMsg
                conversationHistory.add(Content(parts = listOf(Part(text = replyText)), role = "model"))
            } catch (e: Exception) {
                val errorMsg = ChatMessage(text = "Error: ${e.message}", isUser = false)
                _messages.value = _messages.value + errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(onNavigateBack: () -> Unit, viewModel: ChatViewModel = viewModel()) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var inputText by remember { mutableStateOf("") }
    var useComplex by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gemini Assistant") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Text("Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                reverseLayout = false
            ) {
                items(messages) { msg ->
                    MessageBubble(msg)
                }
                if (isLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = useComplex, onCheckedChange = { useComplex = it })
                    Text("Deep Thinking", style = MaterialTheme.typography.bodySmall)
                }
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    placeholder = { Text("Ask Gemini...") }
                )
                IconButton(onClick = {
                    viewModel.sendMessage(inputText, useComplex)
                    inputText = ""
                }) {
                    Icon(Icons.Default.Send, contentDescription = "Send")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(msg: ChatMessage) {
    val alignment = if (msg.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (msg.isUser) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        contentAlignment = alignment
    ) {
        Text(
            text = msg.text,
            modifier = Modifier
                .background(color, RoundedCornerShape(12.dp))
                .padding(12.dp),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
