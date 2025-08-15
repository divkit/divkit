package com.yandex.divkit.example.dsl

import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

class Application {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val data = LayoutRenderer().render()

            val mapper = ObjectMapper().writerWithDefaultPrettyPrinter()
            val jsonText = mapper.writeValueAsString(data)

            File("./src/main/resources/output/div.json").writeText(jsonText)
        }
    }
}
