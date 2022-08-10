package com.yandex.div.dsl.context

import com.fasterxml.jackson.annotation.JsonInclude
import com.yandex.div.dsl.model.Div

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CardWithTemplates(val card: CardContext, val templates: TemplateContext<Div>? = null)
