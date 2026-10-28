package com.yandex.div.test.data

import com.yandex.div.json.expressions.Expression
import com.yandex.div2.Div
import com.yandex.div2.DivCollectionItemBuilder
import org.json.JSONArray

fun itemBuilder(
    data: Expression<JSONArray>,
    dataElementName: String? = null,
    prototypes: List<DivCollectionItemBuilder.Prototype>
): DivCollectionItemBuilder {
    if (dataElementName == null) {
        return DivCollectionItemBuilder(data = data, prototypes = prototypes)
    }
    return DivCollectionItemBuilder(
        data = data,
        dataElementName = dataElementName,
        prototypes = prototypes
    )
}

fun itemBuilder(
    data: JSONArray,
    dataElementName: String? = null,
    prototypes: List<DivCollectionItemBuilder.Prototype>
): DivCollectionItemBuilder {
    return itemBuilder(
        data = constant(data),
        dataElementName = dataElementName,
        prototypes = prototypes
    )
}

fun prototype(
    div: Div,
    id: Expression<String>? = null,
    selector: Expression<Boolean>? = null
): DivCollectionItemBuilder.Prototype {
    if (selector == null) {
        return DivCollectionItemBuilder.Prototype(div = div, id = id)
    }
    return DivCollectionItemBuilder.Prototype(div = div, id = id, selector = selector)
}
