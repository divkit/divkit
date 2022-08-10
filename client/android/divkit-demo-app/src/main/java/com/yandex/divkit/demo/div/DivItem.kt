package com.yandex.divkit.demo.div

import com.yandex.div2.DivData
import java.util.UUID

data class DivItem(val data: DivData, val id: String = UUID.randomUUID().toString())