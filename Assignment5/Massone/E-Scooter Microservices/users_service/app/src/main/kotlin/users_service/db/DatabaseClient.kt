package users_service.db

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject

interface DatabaseClient {
    fun findOne(
        collection: String,
        query: JsonObject,
        fields: JsonObject?,
        resultHandler: Handler<AsyncResult<JsonObject>>,
    )

    fun save(collection: String, document: JsonObject, resultHandler: Handler<AsyncResult<String>>)
}