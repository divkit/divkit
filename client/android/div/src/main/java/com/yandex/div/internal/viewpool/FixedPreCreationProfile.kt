package com.yandex.div.internal.viewpool

import com.yandex.div.core.annotations.PublicApi

@PublicApi
data class FixedPreCreationProfile(
    val textCapacity: Int = 20,
    val imageCapacity: Int = 20,
    val gifImageCapacity: Int = 3,
    val overlapContainerCapacity: Int = 8,
    val linearContainerCapacity: Int = 12,
    val wrapContainerCapacity: Int = 4,
    val gridCapacity: Int = 4,
    val galleryCapacity: Int = 6,
    val pagerCapacity: Int = 2,
    val tabCapacity: Int = 2,
    val stateCapacity: Int = 4,
    val customCapacity: Int = 2,
    val indicatorCapacity: Int = 2,
    val sliderCapacity: Int = 2,
    val inputCapacity: Int = 2,
    val selectCapacity: Int = 2,
    val videoCapacity: Int = 2
) : ViewPreCreationProfile()
