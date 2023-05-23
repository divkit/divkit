export const DEFAULT_JSON_VALUE = `{
    "templates": {
        "tutorialCard": {
            "type": "container",
            "items": [
                {
                    "type": "text",
                    "font_size": 21,
                    "font_weight": "bold",
                    "margins": {
                        "bottom": 16
                    },
                    "$text": "title"
                },
                {
                    "type": "text",
                    "font_size": 16,
                    "margins": {
                        "bottom": 16
                    },
                    "$text": "body"
                },
                {
                    "type": "container",
                    "$items": "links"
                }
            ],
            "margins": {
                "bottom": 6
            },
            "orientation": "vertical",
            "paddings": {
                "top": 10,
                "bottom": 0,
                "left": 30,
                "right": 30
            }
        },
        "link": {
            "type": "text",
            "action": {
                "$url": "link",
                "$log_id": "log"
            },
            "font_size": 14,
            "margins": {
                "bottom": 2
            },
            "text_color": "#0000ff",
            "underline": "single",
            "$text": "link_text"
        }
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
                                "left": 60
                            }
                        },
                        {
                            "type": "tutorialCard",
                            "title": "DivKit",
                            "body": "What is DivKit and why did I get here?\\n\\nDivKit is a new Yandex open source framework that helps speed up mobile development.\\n\\niOS, Android, Web — update the interface of any applications directly from the server, without publishing updates.\\n\\nFor 5 years we have been using DivKit in the Yandex search app, Alice, Edadeal, Market, and now we are sharing it with you.\\n\\nThe source code is published on GitHub under the Apache 2.0 license.",
                            "links": [
                                {
                                    "type": "link",
                                    "link_text": "More about DivKit",
                                    "link": "https://divkit.tech/",
                                    "log": "landing"
                                },
                                {
                                    "type": "link",
                                    "link_text": "Documentation",
                                    "link": "https://divkit.tech/doc/",
                                    "log": "docs"
                                },
                                {
                                    "type": "link",
                                    "link_text": "News channel",
                                    "link": "https://t.me/divkit_news",
                                    "log": "tg_news"
                                },
                                {
                                    "type": "link",
                                    "link_text": "EN Community chat",
                                    "link": "https://t.me/divkit_community_en",
                                    "log": "tg_en_chat"
                                },
                                {
                                    "type": "link",
                                    "link_text": "RU Community chat",
                                    "link": "https://t.me/divkit_community_ru",
                                    "log": "tg_ru_chat"
                                }
                            ]
                        }
                    ]
                }
            }
        ]
    }
}
`;

export const DEFAULT_TS_VALUE  = `import {
    DivContainer,
    DivText,
    DivImage,
    reference,
    divCard,
    templateHelper,
    rewriteRefs
} from '@divkitframework/jsonbuilder';

const templates = {
    tutorialCard: new DivContainer({
        paddings: {
            top: 30,
            bottom: 0,
            left: 30,
            right: 30
        },
        margins: {
            bottom: 6
        },
        items: [
            new DivText({
                text: reference('title'),
                font_weight: 'bold',
                font_size: 21,
                margins: {
                    bottom: 16
                }
            }),
            new DivText({
                text: reference('body'),
                margins: {
                    bottom: 16
                },
                font_size: 16
            }),
            new DivContainer({
                items: reference('links')
            })
        ]
    }),
    link: new DivText({
        text: reference('text'),
        text_color: '#0000ff',
        underline: 'single',
        font_size: 14,
        margins: {
            bottom: 2
        }
    })
};

const thelper = templateHelper(templates);

export function getJson() {
    return divCard(rewriteRefs(templates), {
        log_id: 'div2_sample_card',
        states: [
            {
                state_id: 0,
                div: new DivContainer({
                    items: [
                        new DivImage({
                            image_url: 'https://yastatic.net/s3/home/divkit/logo.png',
                            margins: {
                                top: 10,
                                right: 60,
                                bottom: 10,
                                left: 60
                            }
                        }),
                        thelper.tutorialCard({
                            title: 'DivKit',
                            body: 'What is DivKit and why did I get here?\\n\\nDivKit is a new Yandex open source framework that helps speed up mobile development.\\n\\niOS, Android, Web — update the interface of any applications directly from the server, without publishing updates.\\n\\nFor 5 years we have been using DivKit in the Yandex search app, Alice, Edadeal, Market, and now we are sharing it with you.\\n\\nThe source code is published on GitHub under the Apache 2.0 license.',
                            links: [
                                thelper.link({
                                    text: 'More about DivKit',
                                    action: {
                                        url: 'https://divkit.tech/',
                                        log_id: 'landing'
                                    }
                                }),
                                thelper.link({
                                    text: 'Documentation',
                                    action: {
                                        url: 'https://divkit.tech/doc/',
                                        log_id: 'docs'
                                    }
                                }),
                                thelper.link({
                                    text: 'News channel',
                                    action: {
                                        url: 'https://t.me/divkit_news',
                                        log_id: 'tg_news'
                                    }
                                }),
                                thelper.link({
                                    text: 'EN Community chat',
                                    action: {
                                        url: 'https://t.me/divkit_community_en',
                                        log_id: 'tg_en_chat'
                                    }
                                }),
                                thelper.link({
                                    text: 'RU Community chat',
                                    action: {
                                        url: 'https://t.me/divkit_community_ru',
                                        log_id: 'tg_ru_chat'
                                    }
                                })
                            ]
                        })
                    ]
                })
            }
        ]
    });
}
`;
