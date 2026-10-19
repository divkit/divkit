@file:JvmName("BuiltInParser")

package com.yandex.div.parser.json

import com.yandex.div.core.annotations.ExperimentalApi
import com.yandex.div.core.json.JsonObject
import com.yandex.div.json.ParsingEnvironment
import com.yandex.div2.DivAction
import com.yandex.div2.DivData
import com.yandex.div2.DivPatch
import com.yandex.div2.DivTemplate
import com.yandex.div2.DivVariable

@ExperimentalApi
public fun DivData.Companion.fromJson(env: ParsingEnvironment, json: JsonObject): DivData {
    return builtInParserComponent.divDataJsonEntityParser
        .value
        .deserialize(context = env, data = json)
}

@ExperimentalApi
public fun DivPatch.Companion.fromJson(env: ParsingEnvironment, json: JsonObject): DivPatch {
    return builtInParserComponent.divPatchJsonEntityParser
        .value
        .deserialize(context = env, data = json)
}

@ExperimentalApi
public fun DivAction.Companion.fromJson(env: ParsingEnvironment, json: JsonObject): DivAction {
    return builtInParserComponent.divActionJsonEntityParser
        .value
        .deserialize(context = env, data = json)
}

@ExperimentalApi
public fun DivVariable.Companion.fromJson(env: ParsingEnvironment, json: JsonObject): DivVariable {
    return builtInParserComponent.divVariableJsonEntityParser
        .value
        .deserialize(context = env, data = json)
}

@ExperimentalApi
public fun DivTemplate.Companion.fromJson(env: ParsingEnvironment, json: JsonObject): DivTemplate {
    return builtInParserComponent.divJsonTemplateParser
        .value
        .deserialize(context = env, data = json)
}

internal val builtInParserComponent = JsonParserComponent()
