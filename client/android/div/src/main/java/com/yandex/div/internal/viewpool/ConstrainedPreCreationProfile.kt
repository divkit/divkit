package com.yandex.div.internal.viewpool

import com.yandex.div.core.annotations.PublicApi

@PublicApi
data class ConstrainedPreCreationProfile(
    val id: String,
    val textCapacity: IntRange = 20..20,
    val imageCapacity: IntRange = 20..20,
    val gifImageCapacity: IntRange = 3..3,
    val overlapContainerCapacity: IntRange = 8..8,
    val linearContainerCapacity: IntRange = 12..12,
    val wrapContainerCapacity: IntRange = 4..4,
    val gridCapacity: IntRange = 4..4,
    val galleryCapacity: IntRange = 6..6,
    val pagerCapacity: IntRange = 2..2,
    val tabCapacity: IntRange = 2..2,
    val stateCapacity: IntRange = 4..4,
    val customCapacity: IntRange = 2..2,
    val indicatorCapacity: IntRange = 2..2,
    val sliderCapacity: IntRange = 2..2,
    val inputCapacity: IntRange = 2..2,
    val selectCapacity: IntRange = 2..2,
    val videoCapacity: IntRange = 2..2
) : ViewPreCreationProfile()
