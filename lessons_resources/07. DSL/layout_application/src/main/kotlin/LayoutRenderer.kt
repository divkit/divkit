package com.yandex.divkit.example.dsl

import com.yandex.divkit.example.dsl.data.PostData
import com.yandex.divkit.example.dsl.data.Variables.SELECTED_POST_DESCRIPTION
import com.yandex.divkit.example.dsl.data.exampleData
import divkit.dsl.Div
import divkit.dsl.Divan
import divkit.dsl.Url
import divkit.dsl.center
import divkit.dsl.container
import divkit.dsl.core.bind
import divkit.dsl.core.expression
import divkit.dsl.data
import divkit.dsl.divan
import divkit.dsl.evaluate
import divkit.dsl.gallery
import divkit.dsl.horizontal
import divkit.dsl.render
import divkit.dsl.root
import divkit.dsl.scope.DivScope
import divkit.dsl.stringVariable
import divkit.dsl.text

class LayoutRenderer {
    fun render(): Divan {
        val postDataList = exampleData

        return divan {
            data(
                logId = "generated_div",
                div = container(
                    items = listOf(
                        createSelectedPostIndicator(),
                        createPostsGallery(postDataList),
                    )
                ),
                variables = listOf(
                    stringVariable(
                        name = SELECTED_POST_DESCRIPTION,
                        value = "none",
                    )
                )
            )
        }
    }

    private fun DivScope.createSelectedPostIndicator(): Div {
        return text(
            textAlignmentHorizontal = center,
        ).evaluate(
            text = expression("Selected post: @{$SELECTED_POST_DESCRIPTION}"),
        )
    }

    private fun DivScope.createPostsGallery(posts: List<PostData>): Div {
        return gallery(
            orientation = horizontal,
            items = buildList {
                posts.forEach { post ->
                    add(createCard(post))
                }
            }
        )
    }

    private fun DivScope.createCard(person: PostData): Div {
        return with(CardTemplate) {
            render(
                template,
                descriptionRef bind person.description,
                imageRef bind Url.create(person.imageUrl.toString()),
            )
        }
    }

    @Suppress("unused")
    private fun getLayoutHardWay(): Divan {
        return divan {
            data(
                logId = "generated_div",
                states = listOf(
                    root(
                        stateId = 0,
                        div = container(
                            // Layout
                        )
                    )
                ),
            )
        }
    }
}
