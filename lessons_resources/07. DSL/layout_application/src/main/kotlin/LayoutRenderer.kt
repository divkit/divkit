package com.yandex.divkit.example.dsl

import com.yandex.divkit.example.dsl.data.PostData
import com.yandex.divkit.example.dsl.data.Preview
import com.yandex.divkit.example.dsl.data.Variables.SELECTED_POST_DESCRIPTION
import com.yandex.divkit.example.dsl.data.exampleData
import divkit.dsl.Div
import divkit.dsl.Divan
import divkit.dsl.Image
import divkit.dsl.Url
import divkit.dsl.accessibility
import divkit.dsl.action
import divkit.dsl.center
import divkit.dsl.container
import divkit.dsl.core.bind
import divkit.dsl.core.expression
import divkit.dsl.data
import divkit.dsl.divan
import divkit.dsl.evaluate
import divkit.dsl.fixedSize
import divkit.dsl.gallery
import divkit.dsl.horizontal
import divkit.dsl.image
import divkit.dsl.render
import divkit.dsl.root
import divkit.dsl.scope.DivScope
import divkit.dsl.stringVariable
import divkit.dsl.text
import java.net.URL

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

    private fun DivScope.createSocialMediaButton(
        postImageUrl: URL,
        mediaName: String,
        preview: String,
    ): Image = image(
        imageUrl = Url.create("empty://"),
        preview = preview,
        width = fixedSize(25),
        height = fixedSize(25),
        accessibility = accessibility(
            description = mediaName,
        ),
        actions = listOf(
            action(
                logId = "shared to $mediaName",
                url = Url.create("my_client_action_handler://url=$postImageUrl&media=$mediaName"),
            )
        ),
    )

    private fun DivScope.createSocialMediaButtons(postImageUrl: URL): List<Div> = listOf(
        createSocialMediaButton(postImageUrl, "Div 1 Media", Preview.MEDIA_DIV_1),
        createSocialMediaButton(postImageUrl, "Div 2 Media", Preview.MEDIA_DIV_2),
        createSocialMediaButton(postImageUrl, "Div 3 Media", Preview.MEDIA_DIV_3),
        createSocialMediaButton(postImageUrl, "Div 4 Media", Preview.MEDIA_DIV_4),
    )

    private fun DivScope.createCard(post: PostData): Div {
        return with(CardTemplate) {
            render(
                template,
                descriptionRef bind post.description,
                imageRef bind Url.create(post.imageUrl.toString()),
                buttonsRef bind createSocialMediaButtons(post.imageUrl),
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
