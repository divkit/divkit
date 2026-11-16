package com.yandex.div.storage.templates

import com.yandex.div.core.annotations.InternalApi

@InternalApi
public class RawTemplateData(
        public val hash: String,
        public val data: ByteArray
)
