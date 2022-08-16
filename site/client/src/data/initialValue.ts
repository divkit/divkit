export const DEFAULT_JSON_VALUE = `{
    "templates": {
        "tutorialCard": {
            "type": "container",
            "border": {
                "corner_radius": 16,
                "stroke": {
                    "color": "#aaaaaa",
                    "width": 1
                }
            },
            "items": [
                {
                    "type": "text",
                    "font_size": 21,
                    "font_weight": "bold",
                    "margins": {
                        "bottom": 8
                    },
                    "$text": "title"
                },
                {
                    "type": "text",
                    "font_size": 16,
                    "margins": {
                        "bottom": 4
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
                "top": 12,
                "bottom": 12,
                "left": 12,
                "right": 12
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
                            "type": "tutorialCard",
                            "title": "DivKit",
                            "body": "Server side driven ui Яндекса\\nПолезные ссылки:",
                            "links": [
                                {
                                    "type": "link",
                                    "link_text": "Официальная документация",
                                    "link": "https://doc.yandex-team.ru/divkit/overview/",
                                    "log": "docs"
                                },
                                {
                                    "type": "link",
                                    "link_text": "Примеры карточек",
                                    "link": "https://bitbucket.browser.yandex-team.ru/projects/ml/repos/mobile-alice-library-android/browse/div/divkit-demo-app/src/main/assets/div2",
                                    "log": "samples"
                                },
                                {
                                    "type": "link",
                                    "link_text": "Клуб в Этушке",
                                    "link": "https://clubs.at.yandex-team.ru/divkit/",
                                    "log": "atushka"
                                },
                                {
                                    "type": "link",
                                    "link_text": "Канал в слаке",
                                    "link": "https://yndx-all.slack.com/archives/C01FSDSGC7P",
                                    "log": "slack"
                                }
                            ]
                        },
                        {
                            "type": "tutorialCard",
                            "title": "Горячие клавиши редактора",
                            "body": "ctrl+space - вызвать автодополнение\\nctrl+f2 - заменить всё\\nctrl+shift+i - отформатировать json\\nctrl+shift+a закомментировать блок\\nСхолпнуть json-блоки можно с помощью стрелочек, что появляются при наведении на блок с номерами строк слева\\nf1 - вызвать меню со всеми доступными командами и горячими клавишами",
                            "links": [
                                {
                                    "type": "separator",
                                    "delimiter_style": {
                                        "color": "#00000000"
                                    }
                                }
                            ]
                        },
                        {
                            "type": "tutorialCard",
                            "title": "Интерактивная верстка на ваших устройствах",
                            "body": "Для того, чтобы посмотреть вашу верстку на android:\\n1) Скачайте demo apk по ссылке ниже\\n2) Установите apk на устройство и перейдите во вкладку \\"Div2\\"\\n3) Нажмите на share на этой страничке и отсканируйте QR-код, нажав соответствующую кнопку в приложении, либо вставьте урл из поля \\"Link to json\\"\\n4) Для каждого подключенного устройства в редакторе появится вкладка с тем, что отображается сейчас на экране",
                            "links": [
                                {
                                    "type": "link",
                                    "link_text": "apk демо-приложения",
                                    "link": "https://beta.m.soft.yandex.ru/description?app=divkit&platform_shortcut=android&branch=R-100",
                                    "log": "apk"
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
    DivSeparator,
    reference,
    divCard,
    templateHelper,
    rewriteRefs
} from '@divkit/jsonbuilder';

const templates = {
    tutorialCard: new DivContainer({
        paddings: {
            top: 12,
            bottom: 12,
            left: 12,
            right: 12
        },
        margins: {
            bottom: 6
        },
        border: {
            corner_radius: 16,
            stroke: {
                color: '#aaaaaa',
                width: 1
            }
        },
        items: [
            new DivText({
                text: reference('title'),
                font_weight: 'bold',
                font_size: 21,
                margins: {
                    bottom: 8
                }
            }),
            new DivText({
                text: reference('body'),
                margins: {
                    bottom: 4
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
                        thelper.tutorialCard({
                            title: 'DivKit',
                            body: 'Server side driven ui Яндекса\\nПолезные ссылки:',
                            links: [
                                thelper.link({
                                    text: 'Официальная документация',
                                    action: {
                                        url: 'https://doc.yandex-team.ru/divkit/overview/',
                                        log_id: 'docs'
                                    }
                                }),
                                thelper.link({
                                    text: 'Примеры карточек',
                                    action: {
                                        url: 'https://bitbucket.browser.yandex-team.ru/projects/ml/repos/mobile-alice-library-android/browse/div/divkit-demo-app/src/main/assets/div2',
                                        log_id: 'samples'
                                    }
                                }),
                                thelper.link({
                                    text: 'Клуб в Этушке',
                                    action: {
                                        url: 'https://clubs.at.yandex-team.ru/divkit/',
                                        log_id: 'atushka'
                                    }
                                }),
                                thelper.link({
                                    text: 'Канал в слаке',
                                    action: {
                                        url: 'https://yndx-all.slack.com/archives/C01FSDSGC7P',
                                        log_id: 'slack'
                                    }
                                })
                            ]
                        }),
                        thelper.tutorialCard({
                            title: 'Горячие клавиши редактора',
                            body: 'ctrl+space - вызвать автодополнение\\nctrl+f2 - заменить всё\\nctrl+shift+i - отформатировать json\\nctrl+shift+a закомментировать блок\\nСхолпнуть json-блоки можно с помощью стрелочек, что появляются при наведении на блок с номерами строк слева\\nf1 - вызвать меню со всеми доступными командами и горячими клавишами',
                            links: [
                                new DivSeparator({
                                    delimiter_style: {
                                        color: '#00000000'
                                    }
                                })
                            ]
                        }),
                        thelper.tutorialCard({
                            title: 'Интерактивная верстка на ваших устройствах',
                            body: 'Для того, чтобы посмотреть вашу верстку на android:\\n1) Скачайте demo apk по ссылке ниже\\n2) Установите apk на устройство и перейдите во вкладку \\"Div2\\"\\n3) Нажмите на share на этой страничке и отсканируйте QR-код, нажав соответствующую кнопку в приложении, либо вставьте урл из поля \\"Link to json\\"\\n4) Для каждого подключенного устройства в редакторе появится вкладка с тем, что отображается сейчас на экране',
                            links: [
                                thelper.link({
                                    text: 'apk демо-приложения',
                                    action: {
                                        url: 'https://beta.m.soft.yandex.ru/description?app=divkit&platform_shortcut=android&branch=R-100',
                                        log_id: 'apk'
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
