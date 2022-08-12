package com.yandex.div.dsl

import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import com.yandex.div.dsl.context.TemplateContext
import com.yandex.div.dsl.context.card
import com.yandex.div.dsl.context.define
import com.yandex.div.dsl.context.override
import com.yandex.div.dsl.context.resolve
import com.yandex.div.dsl.context.templates
import com.yandex.div.dsl.model.Div
import com.yandex.div.dsl.model.DivAction
import com.yandex.div.dsl.model.DivAlignmentHorizontal
import com.yandex.div.dsl.model.DivAlignmentVertical
import com.yandex.div.dsl.model.DivContainer
import com.yandex.div.dsl.model.DivFontWeight
import com.yandex.div.dsl.model.DivTabs
import com.yandex.div.dsl.model.divAction
import com.yandex.div.dsl.model.divBorder
import com.yandex.div.dsl.model.divContainer
import com.yandex.div.dsl.model.divData
import com.yandex.div.dsl.model.divEdgeInsets
import com.yandex.div.dsl.model.divFixedSize
import com.yandex.div.dsl.model.divGallery
import com.yandex.div.dsl.model.divImage
import com.yandex.div.dsl.model.divImageBackground
import com.yandex.div.dsl.model.divMatchParentSize
import com.yandex.div.dsl.model.divSolidBackground
import com.yandex.div.dsl.model.divTabs
import com.yandex.div.dsl.model.divText
import com.yandex.div.dsl.model.divWrapContentSize
import com.yandex.div.dsl.model.item
import com.yandex.div.dsl.model.menuItem
import com.yandex.div.dsl.model.state
import com.yandex.div.dsl.model.tabTitleStyle
import com.yandex.div.dsl.model.template
import com.yandex.div.dsl.serializer.toJsonNode
import com.yandex.div.dsl.type.BoolInt
import com.yandex.div.dsl.type.color
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode
import java.net.URI

class DistrictCardTest {
    @Test
    fun renderDistrictCard() {

        val districtButtonActionLinkRef = reference<URI>("district_button_action_link")
        val titleItemsRef = reference<List<Div>>("title_items")
        val tabItemsLinkRef = reference<List<DivTabs.Item>>("tab_items_link")
        val districtGalleryItemActionLinkRef = reference<URI>("district_gallery_item_action_link")
        val addressRef = reference<String>("address")
        val messageRef = reference<String>("message")
        val avatarUrlRef = reference<URI>("avatar_url")
        val usernameRef = reference<String>("username")
        val imageCountRef = reference<String>("image_count")
        val commentCountRef = reference<String>("comment_count")
        val galleryTailActionLinkRef = reference<DivAction>("gallery_tail_action_link")
        val tailTextLinkRef = reference<String>("tail_text_link")

        val templates: TemplateContext<Div> = templates<Div> {
            define(
                "title_text",
                divText(
                    paddings = divEdgeInsets(
                        left = int(16),
                        top = int(12),
                        right = int(16),
                    ),
                    fontSize = int(18),
                    lineHeight = int(24),
                    textColor = value("#CC000000".color),
                    fontWeight = enum(DivFontWeight.MEDIUM)
                )
            )
            define(
                "title_menu",
                divImage(
                    width = divFixedSize(value = int(44)),
                    height = divFixedSize(value = int(44)),
                    imageUrl = value(URI.create("https://i.imgur.com/qNJQKU8.png")),
                )
            )
            define(
                "district_button",
                divText(
                    text = value("Написать новость"),
                    fontSize = int(14),
                    border = divBorder(cornerRadius = int(4)),
                    margins = divEdgeInsets(
                        left = int(16),
                        right = int(16),
                        top = int(12),
                    ),
                    paddings = divEdgeInsets(
                        bottom = int(8),
                        top = int(8),
                    ),
                    action = divAction(
                        url = districtButtonActionLinkRef,
                        logId = value("new_post"),
                    ),
                    textAlignmentHorizontal = enum(DivAlignmentHorizontal.CENTER),
                    background = listOf(divSolidBackground(color = value("#ffdc60".color))),
                )
            )
            define(
                "district_card",
                divContainer(
                    orientation = enum(DivContainer.Orientation.VERTICAL),
                    paddings = divEdgeInsets(bottom = int(16)),
                    height = divWrapContentSize(),
                    items = listOf(
                        divContainer(
                            orientation = enum(DivContainer.Orientation.HORIZONTAL),
                            items = titleItemsRef,
                        ),
                        divTabs(
                            switchTabsByContentSwipeEnabled = enum(BoolInt.FALSE),
                            titlePaddings = divEdgeInsets(
                                left = int(12),
                                right = int(12),
                                bottom = int(8),
                            ),
                            tabTitleStyle = tabTitleStyle(
                                fontWeight = enum(DivFontWeight.MEDIUM)
                            ),
                            items = tabItemsLinkRef
                        ),
                        template("district_button"),
                    ),
                    background = listOf(
                        divImageBackground(
                            imageUrl = value(URI.create("https://api.yastatic.net/morda-logo/i/yandex-app/district/district_day.3.png")),
                            preloadRequired = enum(BoolInt.TRUE),
                        )
                    )
                )
            )
            define(
                "district_gallery_item",
                divContainer(
                    orientation = enum(DivContainer.Orientation.VERTICAL),
                    width = divFixedSize(value = int(272)),
                    height = divFixedSize(value = int(204)),
                    border = divBorder(
                        cornerRadius = int(6)
                    ),
                    paddings = divEdgeInsets(
                        left = int(12),
                        right = int(12),
                        top = int(0),
                        bottom = int(0),
                    ),
                    background = listOf(
                        divSolidBackground(
                            color = value("#ffffff".color)
                        )
                    ),
                    action = divAction(
                        logId = value("district_item"),
                        url = districtGalleryItemActionLinkRef
                    ),
                    items = listOf(
                        divText(
                            text = addressRef,
                            fontSize = int(13),
                            textColor = value("#80000000".color),
                            lineHeight = int(16),
                            maxLines = int(1),
                            height = divWrapContentSize(),
                            paddings = divEdgeInsets(
                                left = int(0),
                                right = int(0),
                                top = int(12),
                                bottom = int(0),
                            ),
                        ),
                        divText(
                            text = messageRef,
                            alignmentVertical = enum(DivAlignmentVertical.TOP),
                            fontSize = int(14),
                            lineHeight = int(20),
                            maxLines = int(6),
                            height = divMatchParentSize(),
                            paddings = divEdgeInsets(
                                top = int(5),
                                bottom = int(5),
                            ),
                        ),
                        divContainer(
                            orientation = enum(DivContainer.Orientation.HORIZONTAL),
                            contentAlignmentVertical = enum(DivAlignmentVertical.CENTER),
                            paddings = divEdgeInsets(
                                bottom = int(12),
                            ),
                            items = listOf(
                                divImage(
                                    imageUrl = avatarUrlRef,
                                    width = divFixedSize(value = int(24)),
                                    height = divFixedSize(value = int(24)),
                                    border = divBorder(
                                        cornerRadius = int(12),
                                    ),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    preloadRequired = enum(BoolInt.TRUE),
                                ),
                                divText(
                                    text = usernameRef,
                                    fontSize = int(13),
                                    fontWeight = enum(DivFontWeight.MEDIUM),
                                    textColor = value("#80000000".color),
                                    lineHeight = int(16),
                                    maxLines = int(1),
                                    width = divFixedSize(value = int(126)),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    paddings = divEdgeInsets(
                                        left = int(8),
                                        right = int(8),
                                    ),
                                ),
                                divImage(
                                    imageUrl = value(URI.create("https://api.yastatic.net/morda-logo/i/yandex-app/district/images_day.1.png")),
                                    width = divFixedSize(value = int(24)),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    height = divFixedSize(value = int(24)),
                                    placeholderColor = value("#00ffffff".color),
                                    preloadRequired = enum(BoolInt.TRUE),
                                ),
                                divText(
                                    text = imageCountRef,
                                    fontSize = int(13),
                                    fontWeight = enum(DivFontWeight.MEDIUM),
                                    textColor = value("#80000000".color),
                                    lineHeight = int(16),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    maxLines = int(1),
                                    paddings = divEdgeInsets(
                                        right = int(10),
                                        left = int(4),
                                    ),
                                ),
                                divImage(
                                    imageUrl = value(URI.create("https://api.yastatic.net/morda-logo/i/yandex-app/district/images_day.1.png")),
                                    width = divFixedSize(value = int(24)),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    height = divFixedSize(value = int(24)),
                                    placeholderColor = value("#00ffffff".color),
                                    preloadRequired = enum(BoolInt.TRUE),
                                ),
                                divText(
                                    text = commentCountRef,
                                    fontSize = int(13),
                                    alignmentVertical = enum(DivAlignmentVertical.CENTER),
                                    fontWeight = enum(DivFontWeight.MEDIUM),
                                    textColor = value("#80000000".color),
                                    lineHeight = int(16),
                                    maxLines = int(1),
                                    paddings = divEdgeInsets(
                                        left = int(4)
                                    ),
                                )

                            )

                        )
                    )
                )
            )
            define(
                "gallery_tail",
                divContainer(
                    width = divFixedSize(value = int(104)),
                    height = divMatchParentSize(),
                    action = galleryTailActionLinkRef,
                    contentAlignmentVertical = enum(DivAlignmentVertical.CENTER),
                    contentAlignmentHorizontal = enum(DivAlignmentHorizontal.CENTER),
                    items = listOf(
                        divImage(
                            imageUrl = value(URI.create("https://i.imgur.com/CPmGi24.png")),
                            width = divFixedSize(value = int(40)),
                            height = divFixedSize(value = int(40)),
                            border = divBorder(
                                cornerRadius = int(20),
                            ),
                            background = listOf(
                                divSolidBackground(
                                    color = value("#ffffff".color)
                                )
                            ),
                            placeholderColor = value("#00ffffff".color),
                            preloadRequired = enum(BoolInt.TRUE),
                        ),
                        divText(
                            text = tailTextLinkRef,
                            fontSize = int(14),
                            textColor = value("#6b7a80".color),
                            lineHeight = int(16),
                            textAlignmentHorizontal = enum(DivAlignmentHorizontal.CENTER),
                            height = divWrapContentSize(),
                            paddings = divEdgeInsets(
                                left = int(0),
                                right = int(0),
                                top = int(10),
                                bottom = int(0),
                            )
                        )
                    )
                )
            )
        }
        val card = card {
            divData(
                logId = "district_card",
                states = listOf(
                    state(
                        stateId = 1,
                        div = template(
                            type = "district_card",
                            resolve(
                                titleItemsRef,
                                listOf(
                                    template(
                                        type = "title_text",
                                        override("text", "Новости района")
                                    ),
                                    template(
                                        type = "title_menu",
                                        override(
                                            "action",
                                            divAction(
                                                logId = "menu",
                                                menuItems = listOf(
                                                    menuItem(
                                                        text = "Настройки ленты",
                                                        action = divAction(
                                                            logId = "settings",
                                                            url = URI.create("http://ya.ru"),
                                                        )
                                                    ),
                                                    menuItem(
                                                        text = "Скрыть карточку",
                                                        action = divAction(
                                                            logId = "hide",
                                                            url = URI.create("http://ya.ru"),
                                                        )
                                                    )
                                                )
                                            )
                                        )
                                    )
                                )
                            ),
                            resolve(districtButtonActionLinkRef, URI.create("http://ya.ru")),
                            resolve(
                                tabItemsLinkRef, listOf(
                                    item(
                                        title = "ПОПУЛЯРНОЕ",
                                        div = divGallery(
                                            width = divMatchParentSize(40.0),
                                            height = divFixedSize(value = 204),
                                            paddings = divEdgeInsets(
                                                left = 16,
                                                right = 16,
                                            ),
                                            items = listOf(
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо если"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо…"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо…"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо…"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо…"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "gallery_tail",
                                                    resolve(tailTextLinkRef, "Все новости района")
                                                )
                                            )
                                        )
                                    ),
                                    item(
                                        title = "ВСЕ",
                                        div = divGallery(
                                            width = divMatchParentSize(40.0),
                                            height = divFixedSize(value = 204),
                                            paddings = divEdgeInsets(
                                                left = 16,
                                                right = 16,
                                            ),
                                            items = listOf(
                                                template(
                                                    type = "district_gallery_item",
                                                    resolve(addressRef, "Ул. Льва Толстого, 16"),
                                                    resolve(
                                                        messageRef,
                                                        "Каток в парке Горького подготовился серьезно к началу сезона.\nБудет три катка: основная площадка, детский каток и для любителей хоккея. А в выходные каток будет открыт до полуночи. И еще хорошо…"
                                                    ),
                                                    resolve(
                                                        avatarUrlRef,
                                                        URI.create("https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200")
                                                    ),
                                                    resolve(usernameRef, "igorbuschina"),
                                                    resolve(imageCountRef, "3"),
                                                    resolve(commentCountRef, "1"),
                                                    resolve(
                                                        districtGalleryItemActionLinkRef,
                                                        URI.create("http://ya.ru")
                                                    )
                                                ),
                                                template(
                                                    type = "gallery_tail",
                                                    resolve(tailTextLinkRef, "Все новости района")
                                                )
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            )
        }

        val actual = JsonNodeFactory.instance.objectNode().apply {
            set<ObjectNode>("templates", templates.toJsonNode())
            set<ObjectNode>("card", card.toJsonNode())
        }.toString()

        val expected = readResource("/json/district_card.json")
        JSONAssert.assertEquals(
            expected,
            actual,
            JSONCompareMode.STRICT
        )
    }
}

