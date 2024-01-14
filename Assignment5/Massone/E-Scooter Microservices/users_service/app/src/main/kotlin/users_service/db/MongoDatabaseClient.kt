package users_service.db


import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.core.json.JsonObject
import io.vertx.ext.mongo.MongoClient

class MongoDatabaseClient(private val mongoClient: MongoClient) : DatabaseClient {
    override fun findOne(
        collection: String,
        query: JsonObject,
        fields: JsonObject?,
        resultHandler: Handler<AsyncResult<JsonObject>>,
    ) {
        mongoClient.findOne(collection, query, fields, resultHandler)
    }

    override fun save(collection: String, document: JsonObject, resultHandler: Handler<AsyncResult<String>>) {
        mongoClient.save(collection, document, resultHandler)
    }
}