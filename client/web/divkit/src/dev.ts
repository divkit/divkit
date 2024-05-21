import Root from './components/Root.svelte';
import { SizeProvider } from './extensions/sizeProvider';
import { lottieExtensionBuilder } from './extensions/lottie';
import type { DivExtensionClass } from '../typings/common';
import Lottie from 'lottie-web/build/player/lottie';
import { initComponents } from './devCustomComponents';

const json = {
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
                            "title": "@{getHours(parseUnixTimeAsLocal(1715855877))}",
                            "body": "Server side driven ui Яндекса\nПолезные ссылки:",
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
                                    "link": "https://a.yandex-team.ru/arcadia/divkit/public/test_data/samples",
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
                                    "link_text": "Канал в Мессенджере",
                                    "link": "https://q.yandex-team.ru/#/join/b7263a0a-c2ef-4c02-9669-e8a1f4cf3762",
                                    "log": "slack"
                                }
                            ]
                        },
                        {
                            "type": "tutorialCard",
                            "title": "Горячие клавиши редактора",
                            "body": "ctrl+space - вызвать автодополнение\nctrl+f2 - заменить всё\nctrl+shift+i - отформатировать json\nctrl+shift+a закомментировать блок\nСхолпнуть json-блоки можно с помощью стрелочек, что появляются при наведении на блок с номерами строк слева\nf1 - вызвать меню со всеми доступными командами и горячими клавишами",
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
                            "body": "Для того, чтобы посмотреть вашу верстку на android:\n1) Скачайте demo apk по ссылке ниже\n2) Установите apk на устройство и перейдите во вкладку \"Div2\"\n3) Нажмите на share на этой страничке и отсканируйте QR-код, нажав соответствующую кнопку в приложении, либо вставьте урл из поля \"Link to json\"\n4) Для каждого подключенного устройства в редакторе появится вкладка с тем, что отображается сейчас на экране",
                            "links": [
                                {
                                    "type": "link",
                                    "link_text": "Android демо-приложение",
                                    "link": "https://beta.m.soft.yandex.ru/description?app=divkit&platform_shortcut=android&branch=unknown",
                                    "log": "android"
                                },
                                {
                                    "type": "link",
                                    "link_text": "iOS демо-приложение",
                                    "link": "https://teamcity.yandex-team.ru/buildConfiguration/MobileNew_Monorepo_DivKit_iOS_trunk?branch=&mode=builds",
                                    "log": "ios"
                                }
                            ]
                        }
                    ],
                    "margins": {
                        "top": 8,
                        "bottom": 8,
                        "left": 8,
                        "right": 8
                    }
                }
            }
        ]
    }
};

window.root = new Root({
    target: document.body,
    props: {
        id: 'abcde',
        json,
        onStat(arg) {
            console.log(arg);
        },
        extensions: new Map<string, DivExtensionClass>([
            ['size_provider', SizeProvider],
            ['lottie', lottieExtensionBuilder(Lottie.loadAnimation)],
        ]),
        customComponents: new Map([
            ['old_custom_card_1', {
                element: 'old-custom-card1'
            }],
            ['old_custom_card_2', {
                element: 'old-custom-card2'
            }],
            ['new_custom_card_1', {
                element: 'new-custom-card'
            }],
            ['new_custom_card_2', {
                element: 'new-custom-card'
            }],
            ['new_custom_container_1', {
                element: 'new-custom-container'
            }]
        ]),
        store: {
            getValue(name, type) {
                try {
                    const json = localStorage.getItem('divkit:' + name);
                    if (json) {
                        const parsed = JSON.parse(json);
                        if (type === parsed.type && Date.now() < parsed.lifetime && parsed.value) {
                            return parsed.value;
                        }
                    }
                } catch (err) {
                    //
                }
            },
            setValue(name, type, value, lifetime) {
                try {
                    localStorage.setItem('divkit:' + name, JSON.stringify({value, type, lifetime: Date.now() + lifetime * 1000}));
                } catch (err) {
                    //
                }
            },
        }
    }
});

initComponents();
