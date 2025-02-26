package org.shoplify.frontend

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.logging.Logger
import javax.annotation.PostConstruct

@Service
class OpenAiKotlinFacade {
    private val logger: Logger = Logger.getLogger(OpenAiKotlinFacade::class.java.getName())

    @Value("\${openai_api_key}")
    private lateinit var openAiApiKey: String

    @Value("\${openai_model}")
    private lateinit var openAiModel: String

    private lateinit var openAI: OpenAI
    private final var maxTokenLimit = 10000;

    @PostConstruct
    fun init() {
        openAI = OpenAI(openAiApiKey)
    }

    fun getResponse(
        message: String,
        previousMessages: List<String>,
    ): String = runBlocking {
        // Constructing the prompt
        val prompt = buildString {
            append("User message: ").append(message)
            if (previousMessages.isNotEmpty()) {
                append(".Previous messages: ").append(previousMessages.joinToString { "," })
            }
        }
        val completionRequest = ChatCompletionRequest(
            model = ModelId(openAiModel),
            responseFormat = ChatResponseFormat.Text,
            messages = listOf(
                ChatMessage(
                    role = Role.System, content = """
                    You are a chat assistant to a fake demo ecommerce site. Assume its for a clothing store with shoes, slippers, bags etc.
                    If asked about non clothing stuff, say you dont have them.
                    When asked about any other topic, make sure to say its not in your focus areas of help.
                """.trimIndent()
                ),
                ChatMessage(role = Role.User, content = prompt)
            )
        )

        val response = openAI.chatCompletion(completionRequest, requestOptions = RequestOptions())
        response.choices.first().message.content + ""
    }
}