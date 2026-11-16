package com.yandex.div.storage.templates

import com.yandex.div.core.annotations.InternalApi


@InternalApi
public class Template<T : Any>(
        public val id: String,
        public val hash: String,
        public val template: T,
)
