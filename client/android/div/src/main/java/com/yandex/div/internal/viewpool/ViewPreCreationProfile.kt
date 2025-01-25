package com.yandex.div.internal.viewpool

import kotlinx.serialization.Serializable

@Serializable
data class PreCreationModel(
    val capacity: Int,
    val min: Int = 0,
    val max: Int = Int.MAX_VALUE
)

@Serializable
data class ViewPreCreationProfile(
    val id: String? = null,
    val text: PreCreationModel = PreCreationModel(20),
    val image: PreCreationModel = PreCreationModel(20),
    val gifImage: PreCreationModel = PreCreationModel(3),
    val overlapContainer: PreCreationModel = PreCreationModel(8),
    val linearContainer: PreCreationModel = PreCreationModel(12),
    val wrapContainer: PreCreationModel = PreCreationModel(4),
    val grid: PreCreationModel = PreCreationModel(4),
    val gallery: PreCreationModel = PreCreationModel(6),
    val pager: PreCreationModel = PreCreationModel(2),
    val tab: PreCreationModel = PreCreationModel(2),
    val state: PreCreationModel = PreCreationModel(4),
    val custom: PreCreationModel = PreCreationModel(2),
    val indicator: PreCreationModel = PreCreationModel(2),
    val slider: PreCreationModel = PreCreationModel(2),
    val input: PreCreationModel = PreCreationModel(2),
    val select: PreCreationModel = PreCreationModel(2),
    val video: PreCreationModel = PreCreationModel(2),
    val switch: PreCreationModel = PreCreationModel(2),
) {
    companion object {
        fun unconstrained(
            id: String? = null,
            textCapacity: Int = 20,
            imageCapacity: Int = 20,
            gifImageCapacity: Int = 3,
            overlapContainerCapacity: Int = 8,
            linearContainerCapacity: Int = 12,
            wrapContainerCapacity: Int = 4,
            gridCapacity: Int = 4,
            galleryCapacity: Int = 6,
            pagerCapacity: Int = 2,
            tabCapacity: Int = 2,
            stateCapacity: Int = 4,
            customCapacity: Int = 2,
            indicatorCapacity: Int = 2,
            sliderCapacity: Int = 2,
            inputCapacity: Int = 2,
            selectCapacity: Int = 2,
            videoCapacity: Int = 2,
            switchCapacity: Int = 2
        ): ViewPreCreationProfile = ViewPreCreationProfile(
            id = id,
            text = PreCreationModel(textCapacity),
            image = PreCreationModel(imageCapacity),
            gifImage = PreCreationModel(gifImageCapacity),
            overlapContainer = PreCreationModel(overlapContainerCapacity),
            linearContainer = PreCreationModel(linearContainerCapacity),
            wrapContainer = PreCreationModel(wrapContainerCapacity),
            grid = PreCreationModel(gridCapacity),
            gallery = PreCreationModel(galleryCapacity),
            pager = PreCreationModel(pagerCapacity),
            tab = PreCreationModel(tabCapacity),
            state = PreCreationModel(stateCapacity),
            custom = PreCreationModel(customCapacity),
            indicator = PreCreationModel(indicatorCapacity),
            slider = PreCreationModel(sliderCapacity),
            input = PreCreationModel(inputCapacity),
            select = PreCreationModel(selectCapacity),
            video = PreCreationModel(videoCapacity),
            switch = PreCreationModel(switchCapacity)
        )
    }
}
