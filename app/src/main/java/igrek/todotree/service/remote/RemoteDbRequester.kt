package igrek.todotree.service.remote

import android.app.Activity
import android.provider.Settings
import igrek.todotree.info.logger.LoggerFactory.logger
import igrek.todotree.service.preferences.Preferences
import igrek.todotree.service.preferences.PropertyDefinition
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.util.*

class RemoteDbRequester(
        preferences: Preferences,
        private val activity: Activity,
) {

    companion object {
        private const val todoApiBase = "https://todo.igrek.dev/api/v1"

        private const val getAllTodosUrl = "$todoApiBase/todo"
        private const val postNewTodoUrl = "$todoApiBase/todo"
        private val deleteTodoUrl = { id: Long -> "$todoApiBase/todo/$id" }

        private const val authTokenHeader = "X-Auth-Token"
    }

    private val httpRequester = HttpRequester()
    private val jsonType = MediaType.parse("application/json; charset=utf-8")
    private val jsonSerializer = Json(JsonConfiguration.Stable)

    private var authToken = ""

    init {
        authToken = preferences.getValue(PropertyDefinition.userAuthToken, String::class.java) ?: ""
    }

    fun fetchAllRemoteTodos(): Deferred<Result<List<TodoDto>>> {
        val request: Request = Request.Builder()
                .url(getAllTodosUrl)
                .addHeader(authTokenHeader, authToken)
                .build()
        return httpRequester.httpRequestAsync(request) { response: Response ->
            val json = response.body()?.string() ?: ""
            val allDtos: AllTodoDto = jsonSerializer.parse(AllTodoDto.serializer(), json)
            allDtos.entities
        }
    }

    fun createRemoteTodo(content: String): Deferred<Result<String>> {
        logger.info("Creating remote todo")
        val deviceId = Settings.Secure.getString(activity.contentResolver, Settings.Secure.ANDROID_ID)
        val timestampS = Date().time / 1000
        val todoDto = TodoDto(content = content, create_timestamp = timestampS, device_id = deviceId)
        val json = jsonSerializer.stringify(TodoDto.serializer(), todoDto)
        val request: Request = Request.Builder()
                .url(postNewTodoUrl)
                .post(RequestBody.create(jsonType, json))
                .addHeader(authTokenHeader, authToken)
                .build()
        return httpRequester.httpRequestAsync(request) { content }
    }

    fun deleteRemoteTodo(id: Long): Deferred<Result<Unit>> {
        logger.info("Deleting remote todo")
        val request: Request = Request.Builder()
                .url(deleteTodoUrl(id))
                .delete()
                .addHeader(authTokenHeader, authToken)
                .build()
        return GlobalScope.async {
            val dr = httpRequester.httpRequestAsync(request) { true }
            dr.await().map { }
        }
    }

}