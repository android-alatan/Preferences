package io.androidalatan.datastore.preference.invocator.get.mock

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter
import com.squareup.moshi.ToJson

class MockPersonAdapter {

    internal var toJsonCount = 0
    internal var completeToJsonCount = 0
    internal var fromJsonCount = 0
    internal var completeFromJsonCount = 0

    @ToJson
    fun toJson(jsonWriter: JsonWriter, value: TestPersonObj?) {
        toJsonCount++
        jsonWriter.beginObject()
        if (value != null) {
            jsonWriter.name("name")
            jsonWriter.value(value.name)
        }

        jsonWriter.endObject()
        completeToJsonCount++
    }

    @FromJson
    fun fromJson(reader: JsonReader): TestPersonObj {
        fromJsonCount++
        reader.beginObject()
        var name: String? = null
        if (reader.selectName(JsonReader.Options.of("name")) == 0) {
            name = reader.readJsonValue() as? String
        }

        val testPersonClass =
            TestPersonObj(name ?: throw NullPointerException("name is null"))

        reader.endObject()
        completeFromJsonCount++
        return testPersonClass
    }
}