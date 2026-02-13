package com.yandex.div.internal.viewpool

import org.json.JSONObject

internal object PreCreationModelParser {

    private const val KEY_CAPACITY = "capacity"
    private const val KEY_MIN = "min"
    private const val KEY_MAX = "max"

    fun serialize(model: PreCreationModel): JSONObject {
        return JSONObject().apply {
            put(KEY_CAPACITY, model.capacity)
            put(KEY_MIN, model.min)
            put(KEY_MAX, model.max)
        }
    }

    fun deserialize(json: JSONObject): PreCreationModel {
        return PreCreationModel(
            capacity = json.getInt(KEY_CAPACITY),
            min = json.getInt(KEY_MIN),
            max = json.getInt(KEY_MAX),
        )
    }
}

internal object ViewPreCreationProfileParser {

    private const val KEY_ID = "id"
    private const val KEY_TEXT = "text"
    private const val KEY_IMAGE = "image"
    private const val KEY_GIF_IMAGE = "gifImage"
    private const val KEY_OVERLAP_CONTAINER = "overlapContainer"
    private const val KEY_LINEAR_CONTAINER = "linearContainer"
    private const val KEY_WRAP_CONTAINER = "wrapContainer"
    private const val KEY_GRID = "grid"
    private const val KEY_GALLERY = "gallery"
    private const val KEY_PAGER = "pager"
    private const val KEY_TAB = "tab"
    private const val KEY_STATE = "state"
    private const val KEY_CUSTOM = "custom"
    private const val KEY_INDICATOR = "indicator"
    private const val KEY_SLIDER = "slider"
    private const val KEY_INPUT = "input"
    private const val KEY_SELECT = "select"
    private const val KEY_VIDEO = "video"
    private const val KEY_SWITCH = "switch"

    fun serialize(profile: ViewPreCreationProfile): JSONObject {
        return JSONObject().apply {
            putOpt(KEY_ID, profile.id)
            put(KEY_TEXT, PreCreationModelParser.serialize(profile.text))
            put(KEY_IMAGE, PreCreationModelParser.serialize(profile.image))
            put(KEY_GIF_IMAGE, PreCreationModelParser.serialize(profile.gifImage))
            put(KEY_OVERLAP_CONTAINER, PreCreationModelParser.serialize(profile.overlapContainer))
            put(KEY_LINEAR_CONTAINER, PreCreationModelParser.serialize(profile.linearContainer))
            put(KEY_WRAP_CONTAINER, PreCreationModelParser.serialize(profile.wrapContainer))
            put(KEY_GRID, PreCreationModelParser.serialize(profile.grid))
            put(KEY_GALLERY, PreCreationModelParser.serialize(profile.gallery))
            put(KEY_PAGER, PreCreationModelParser.serialize(profile.pager))
            put(KEY_TAB, PreCreationModelParser.serialize(profile.tab))
            put(KEY_STATE, PreCreationModelParser.serialize(profile.state))
            put(KEY_CUSTOM, PreCreationModelParser.serialize(profile.custom))
            put(KEY_INDICATOR, PreCreationModelParser.serialize(profile.indicator))
            put(KEY_SLIDER, PreCreationModelParser.serialize(profile.slider))
            put(KEY_INPUT, PreCreationModelParser.serialize(profile.input))
            put(KEY_SELECT, PreCreationModelParser.serialize(profile.select))
            put(KEY_VIDEO, PreCreationModelParser.serialize(profile.video))
            put(KEY_SWITCH, PreCreationModelParser.serialize(profile.switch))
        }
    }

    fun deserialize(json: JSONObject): ViewPreCreationProfile {
        return ViewPreCreationProfile(
            id = json.optString(KEY_ID),
            text = PreCreationModelParser.deserialize(json.getJSONObject(KEY_TEXT)),
            image = PreCreationModelParser.deserialize(json.getJSONObject(KEY_IMAGE)),
            gifImage = PreCreationModelParser.deserialize(json.getJSONObject(KEY_GIF_IMAGE)),
            overlapContainer = PreCreationModelParser.deserialize(json.getJSONObject(KEY_OVERLAP_CONTAINER)),
            linearContainer = PreCreationModelParser.deserialize(json.getJSONObject(KEY_LINEAR_CONTAINER)),
            wrapContainer = PreCreationModelParser.deserialize(json.getJSONObject(KEY_WRAP_CONTAINER)),
            grid = PreCreationModelParser.deserialize(json.getJSONObject(KEY_GRID)),
            gallery = PreCreationModelParser.deserialize(json.getJSONObject(KEY_GALLERY)),
            pager = PreCreationModelParser.deserialize(json.getJSONObject(KEY_PAGER)),
            tab = PreCreationModelParser.deserialize(json.getJSONObject(KEY_TAB)),
            state = PreCreationModelParser.deserialize(json.getJSONObject(KEY_STATE)),
            custom = PreCreationModelParser.deserialize(json.getJSONObject(KEY_CUSTOM)),
            indicator = PreCreationModelParser.deserialize(json.getJSONObject(KEY_INDICATOR)),
            slider = PreCreationModelParser.deserialize(json.getJSONObject(KEY_SLIDER)),
            input = PreCreationModelParser.deserialize(json.getJSONObject(KEY_INPUT)),
            select = PreCreationModelParser.deserialize(json.getJSONObject(KEY_SELECT)),
            video = PreCreationModelParser.deserialize(json.getJSONObject(KEY_VIDEO)),
            switch = PreCreationModelParser.deserialize(json.getJSONObject(KEY_SWITCH)),
        )
    }
}
