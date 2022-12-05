from datetime import datetime

from app.schemas import (
    CreateTagRequest,
    CreateTagResponse,
    CreateTemplateRequest,
    CreateTemplateResponse,
    TagForListSchema,
    TagSchema,
    TemplateSchema,
    UpdateTemplateRequest,
)

template_1 = {"margins": {"bottom": 16}, "$text": "title"}
template_2 = {
    "templates": {
        "tutorialCard": {
            "type": "container",
            "items": [
                {
                    "type": "text",
                    "font_size": 21,
                    "font_weight": "bold",
                    "margins": {"bottom": 16},
                    "$text": "title",
                },
                {
                    "type": "text",
                    "font_size": 16,
                    "margins": {"bottom": 16},
                    "$text": "body",
                },
                {"type": "container", "$items": "links"},
            ],
            "margins": {"bottom": 6},
            "orientation": "vertical",
            "paddings": {"top": 10, "bottom": 0, "left": 30, "right": 30},
        },
        "link": {
            "type": "text",
            "action": {"$url": "link", "$log_id": "log"},
            "font_size": 14,
            "margins": {"bottom": 2},
            "text_color": "#0000ff",
            "underline": "single",
            "$text": "link_text",
        },
    },
    "card": {
        "log_id": "div2_sample_card",
        "states": [
            {
                "state_id": 0,
                "div": {
                    "type": "container",
                    "items": [
                        {
                            "type": "image",
                            "image_url": "https://yastatic.net/s3/home/divkit/logo.png",
                            "margins": {
                                "top": 10,
                                "right": 60,
                                "bottom": 10,
                                "left": 60,
                            },
                        },
                        {
                            "type": "tutorialCard",
                            "title": "DivKit",
                            "body": "What is DivKit and why did I get here?\n"
                            "\nDivKit is a new Yandex open source framework"
                            " that helps speed up mobile development.\n"
                            "\niOS, Android, Web — update the interface of any "
                            "applications directly from the server, without "
                            "publishing updates.\n\nFor 5 years we have been "
                            "using Devkit in the Yandex search app, Alice, "
                            "Edadeal, Market, and now we are sharing it with "
                            "you.\n\nThe source code is published on GitHub "
                            "under the Apache 2.0 license.",
                            "links": [
                                {
                                    "type": "link",
                                    "link_text": "More about DivKit",
                                    "link": "https://divkit.tech/",
                                    "log": "landing",
                                },
                                {
                                    "type": "link",
                                    "link_text": "Documentation",
                                    "link": "https://divkit.tech/doc/",
                                    "log": "docs",
                                },
                                {
                                    "type": "link",
                                    "link_text": "News channel",
                                    "link": "https://t.me/divkit_news",
                                    "log": "tg_news",
                                },
                                {
                                    "type": "link",
                                    "link_text": "EN Community chat",
                                    "link": "https://t.me/divkit_community_en",
                                    "log": "tg_en_chat",
                                },
                                {
                                    "type": "link",
                                    "link_text": "RU Community chat",
                                    "link": "https://t.me/divkit_community_ru",
                                    "log": "tg_ru_chat",
                                },
                            ],
                        },
                    ],
                },
            }
        ],
    },
}
tag_uuid = "3422b448-2460-4fd2-9183-8000de6f8343"
tag_uuid_2 = "d591ba23-cce9-437e-95df-31d7fe6ac023"
tag_insert_data = CreateTagRequest(tag="mytag")
tag_result_data = CreateTagResponse(id=tag_uuid)
tag_invalid_len = {"id": "3422b448-2460-4fd2-9183-8000de6f8343", "tag": "mytag" * 30}
tag_data = TagForListSchema(id=tag_uuid, tag="mytag", max_use=10)
tag_data_2 = TagForListSchema(id=tag_uuid_2, tag="mytag2", max_use=5)

tag_for_template_data = TagSchema(id=tag_uuid, tag="mytag")
tag_for_template_data_2 = TagSchema(id=tag_uuid_2, tag="mytag2")


template_uuid = "3422b448-2460-4fd2-9183-8000de6f8343"
template_insert_data = CreateTemplateRequest(
    template=template_1,
    description="desription",
    title="title",
)

template_insert_data_invalid_len_description = {
    "template": {},
    "description": 300 * "desription",
    "title": "title",
}

template_insert_data_invalid_len_title = {
    "template": {},
    "description": "desription",
    "title": "title" * 50,
}

template_insert_data_w_tag_ids = CreateTemplateRequest(
    template=template_1,
    description="desription",
    title="title",
    tag_ids=[tag_uuid, tag_uuid_2],
)

template_insert_data_not_valid_tag_ids = {
    "template": template_1,
    "description": "desription",
    "title": "title",
    "tag_ids": ["not_valid"],
}

template_result_data = CreateTemplateResponse(id=template_uuid)
template_data_wo_url = {
    "id": template_uuid,
    "template": template_1,
    "description": "description",
    "title": "template_1",
    "dt_updated": datetime(2011, 11, 4, 0, 5, 23, 283000),
}
template_data_wo_url_w_tags = TemplateSchema(
    id=template_uuid,
    template=template_1,
    description="description",
    title="template_1",
    dt_updated=datetime(2011, 11, 4, 0, 5, 23, 283000),
    tags=[tag_for_template_data, tag_for_template_data_2],
)
template_data_w_url = {
    "id": template_uuid,
    "template": template_2,
    "description": "description",
    "title": "template_1",
    "dt_updated": datetime(2011, 11, 4, 0, 5, 23, 283000),
    "url": "http:/some_url",
}
template_data_w_url_wo_tags = TemplateSchema(
    id=template_uuid,
    template=template_1,
    description="description",
    title="template_1",
    dt_updated=datetime(2011, 11, 4, 0, 5, 23, 283000),
    tags=[],
    url="http:/some_url",
)

template_update_data = UpdateTemplateRequest(
    id=template_uuid,
    template={"margins": {"bottom": 16}, "$text": "title"},
    description="new_description",
    title="new_title",
    tag_ids=[tag_uuid],
)

template_update_invalid_len_description = {
    "id": "d591ba23-cce9-437e-95df-31d7fe6ac023",
    "template": {"margins": {"bottom": 16}, "$text": "title"},
    "description": "new_description" * 300,
    "title": "new_title",
    "tag_ids": [tag_uuid],
}

template_update_invalid_len_title = {
    "id": "d591ba23-cce9-437e-95df-31d7fe6ac023",
    "template": {"margins": {"bottom": 16}, "$text": "title"},
    "description": "new_description",
    "title": "new_title" * 50,
    "tag_ids": [tag_uuid],
}

template_update_data_not_valid_template_id = {
    "id": "not_valid",
    "template": {"margins": {"bottom": 16}, "$text": "title"},
    "description": "new_description",
    "title": "new_title",
    "tag_ids": [tag_uuid],
}

template_update_data_not_valid_tag_id = {
    "id": template_uuid,
    "template": {"margins": {"bottom": 16}, "$text": "title"},
    "description": "new_description",
    "title": "new_title",
    "tag_ids": ["not_valid"],
}

template_return_data_wo_tags = TemplateSchema(
    id=template_uuid,
    template={"margins": {"bottom": 16}, "$text": "title"},
    description="description",
    title="template_1",
    dt_updated=datetime(2011, 11, 4, 0, 5, 23, 283000),
    tags=[],
)

template_return_data_w_tags = TemplateSchema(
    id=template_uuid,
    template={"margins": {"bottom": 16}, "$text": "title"},
    description="description",
    title="template_1",
    dt_updated=datetime(2011, 11, 4, 0, 5, 23, 283000),
    tags=[tag_for_template_data, tag_for_template_data_2],
)
