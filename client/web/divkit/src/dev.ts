import Root from './components/root.svelte';

/*const json = {
    templates: {
        "vertical_alignments_testcase": {
            "type": "text",
            "height": {
                "type": "fixed",
                "value": 40
            },
            "width": {
                "type": "fixed",
                "value": 60
            },
            "background": [
                {
                    "color": "#FFF",
                    "type": "solid"
                }
            ],
            "margins": {
                "top": 10,
                "bottom": 10
            }
        },
        "red_separator": {
            "type": "separator",
            "background": [
                {
                    "color": "#F00",
                    "type": "solid"
                }
            ],
            "height": {
                "type": "fixed",
                "value": 3
            }
        },
        "multiline_text_image_testcase": {
            "description": "image is in the first line of multiline text",
            "type": "text",
            "text": "line1\nline2\nline3",
            "width": { "type": "wrap_content" },
            "images": [
                {
                    "$start": "image_position",
                    "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                    "width": {
                        "type": "fixed",
                        "value": 24
                    },
                    "height": {
                        "type": "fixed",
                        "value": 24
                    }
                }
            ]
        },
        "multiline_text_image_testcase_large_line_height": {
            "type": "multiline_text_image_testcase",
            "line_height": 40
        },
        "multiline_text_image_testcase_small_line_height": {
            "type": "multiline_text_image_testcase",
            "line_height": 5
        },
        "image_between_two_zeros": {
            "type": "text",
            "text": "00",
            "width": { "type": "wrap_content" },
            "images": [
                {
                    "start": 1,
                    "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                    "width": {
                        "type": "fixed",
                        "value": 24
                    },
                    "height": {
                        "type": "fixed",
                        "value": 24
                    }
                }
            ]
        },
        "wrap_content_text": {
            "type": "text",
            "width": { "type": "wrap_content" }
        },
        "long_text": {
            "type": "text",
            "text": "It is a dark time for the Rebellion. Although the Death Star has been destroyed, Imperial troops have driven the Rebel forces from their hidden base and pursued them across the galaxy.",
            "font_size": 12
        }
    },
    card: {
        log_id: "abc",
        states: [{
            state_id: 0,
            div: {
                "type": "container",
                "width": { "type": "wrap_content" },
                "background": [
                    {
                        "type": "solid",
                        "color": "#FFF"
                    }
                ],
                "items": [
                    {
                        "type": "text",
                        "text": "0123456789",
                        "font_size": 20,
                        "width": { "type": "wrap_content" },
                        "ranges": [
                            {
                                "letter_spacing": 2,
                                "font_size": 10,
                                "start": 0,
                                "end": 1
                            },
                            {
                                "text_color": "#ff0000",
                                "font_weight": "bold",
                                "start": 2,
                                "end": 3
                            },
                            {
                                "text_color": "#00ff00",
                                "font_weight": "medium",
                                "start": 4,
                                "end": 5
                            },
                            {
                                "text_color": "#0000ff",
                                "font_weight": "light",
                                "start": 6,
                                "end": 7
                            }
                        ],
                        "images": [
                            {
                                "start": 3,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 24
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 24
                                }
                            },
                            {
                                "start": 9,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 40
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 24
                                }
                            },
                            {
                                "start": 7,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 24
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 10
                                }
                            },
                            {
                                "description": "Image that is out of text length",
                                "start": 100,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 24
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 10
                                }
                            }
                        ]
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is before fist symbol",
                        "type": "text",
                        "text": "0",
                        "width": { "type": "wrap_content" },
                        "images": [
                            {
                                "start": 0,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 24
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 24
                                }
                            }
                        ]
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is after last symbol",
                        "type": "text",
                        "text": "0",
                        "width": { "type": "wrap_content" },
                        "images": [
                            {
                                "start": 1,
                                "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                                "width": {
                                    "type": "fixed",
                                    "value": 24
                                },
                                "height": {
                                    "type": "fixed",
                                    "value": 24
                                }
                            }
                        ]
                    },
                    { "type": "red_separator" },
                    {
                        "description": "different size chars, first is smaller",
                        "type": "image_between_two_zeros",
                        "ranges": [
                            {
                                "font_size": 40,
                                "start": 1,
                                "end": 2
                            }
                        ]
                    },
                    { "type": "red_separator" },
                    {
                        "description": "different size chars, first is larger",
                        "type": "image_between_two_zeros",
                        "ranges": [
                            {
                                "font_size": 40,
                                "start": 0,
                                "end": 1
                            }
                        ]
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the first line of multiline text",
                        "type": "multiline_text_image_testcase",
                        "image_position": 2
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the mid line of multiline text",
                        "type": "multiline_text_image_testcase",
                        "image_position": 8
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the last line of multiline text",
                        "type": "multiline_text_image_testcase",
                        "image_position": 14
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the first line of multiline text with large line_height",
                        "type": "multiline_text_image_testcase_large_line_height",
                        "image_position": 2
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the mid line of multiline text with large line_height",
                        "type": "multiline_text_image_testcase_large_line_height",
                        "image_position": 8
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the last line of multiline text with large line_height",
                        "type": "multiline_text_image_testcase_large_line_height",
                        "image_position": 14
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the first line of multiline text with small line_height",
                        "type": "multiline_text_image_testcase_small_line_height",
                        "image_position": 2
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the mid line of multiline text with small line_height",
                        "type": "multiline_text_image_testcase_small_line_height",
                        "image_position": 8
                    },
                    { "type": "red_separator" },
                    {
                        "description": "image is in the last line of multiline text with small line_height",
                        "type": "multiline_text_image_testcase_small_line_height",
                        "image_position": 14
                    }
                ]
            }
        }]
    }
};*/

const json = {
    "templates": {
        "vertical_alignments_testcase": {
            "type": "text",
            "height": {
                "type": "fixed",
                "value": 40
            },
            "width": {
                "type": "fixed",
                "value": 60
            },
            "background": [
                {
                    "color": "#FFF",
                    "type": "solid"
                }
            ],
            "margins": {
                "top": 10,
                "bottom": 10
            }
        },
        "red_separator": {
            "type": "separator",
            "background": [
                {
                    "color": "#F00",
                    "type": "solid"
                }
            ],
            "height": {
                "type": "fixed",
                "value": 3
            }
        },
        "multiline_text_image_testcase": {
            "description": "image is in the first line of multiline text",
            "type": "text",
            "text": "line1\nline2\nline3",
            "width": { "type": "wrap_content" },
            "images": [
                {
                    "$start": "image_position",
                    "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                    "width": {
                        "type": "fixed",
                        "value": 24
                    },
                    "height": {
                        "type": "fixed",
                        "value": 24
                    }
                }
            ]
        },
        "multiline_text_image_testcase_large_line_height": {
            "type": "multiline_text_image_testcase",
            "line_height": 40
        },
        "multiline_text_image_testcase_small_line_height": {
            "type": "multiline_text_image_testcase",
            "line_height": 5
        },
        "image_between_two_zeros": {
            "type": "text",
            "text": "00",
            "width": { "type": "wrap_content" },
            "images": [
                {
                    "start": 1,
                    "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
                    "width": {
                        "type": "fixed",
                        "value": 24
                    },
                    "height": {
                        "type": "fixed",
                        "value": 24
                    }
                }
            ]
        },
        "wrap_content_text": {
            "type": "text",
            "width": { "type": "wrap_content" }
        },
        "long_text": {
            "type": "text",
            "text": "It is a dark time for the Rebellion. Although the Death Star has been destroyed, Imperial troops have driven the Rebel forces from their hidden base and pursued them across the galaxy.",
            "font_size": 12
        },
        "shy_text": {
            "type": "text",
            "paddings": {
                "left": 10,
                "top": 8,
                "right": 10,
                "bottom": 8
            },
            "text_color": "#21201f",
            "line_height": 15,
            "font_size": 12,
            "width": {
                "type": "fixed",
                "value": 80
            },
            "height": {
                "type": "wrap_content"
            }
        }
    },
    "card": {
            "type": "div2",
            "log_id": "snapshot_test_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "container",
                        "width": { "type": "wrap_content" },
                        "background": [
                            {
                                "type": "solid",
                                "color": "#FFF"
                            }
                        ],
                        "items": [
                            {
                                "description": "Different fonts",
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "Default; red 18 pt light with spacing 0.5; blue 8 pt bold with spacing 1.5.",
                                "font_size": 12,
                                "ranges": [
                                    {
                                        "start": 9,
                                        "end": 43,
                                        "font_size": 18,
                                        "font_weight": "light",
                                        "letter_spacing": 0.5,
                                        "text_color": "#F00"
                                    },
                                    {
                                        "start": 43,
                                        "end": 75,
                                        "font_size": 8,
                                        "font_weight": "bold",
                                        "letter_spacing": 1.5,
                                        "text_color": "#00F"
                                    }
                                ]
                            },
                            {
                                "description": "Overlapping ranges: latter has priority",
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "First style; overlapped (should be same as second); second style",
                                "font_size": 12,
                                "ranges": [
                                    {
                                        "start": 0,
                                        "end": 51,
                                        "font_size": 18,
                                        "font_weight": "light",
                                        "letter_spacing": 0.5,
                                        "text_color": "#F00"
                                    },
                                    {
                                        "start": 13,
                                        "end": 64,
                                        "font_size": 8,
                                        "font_weight": "bold",
                                        "letter_spacing": 1.5,
                                        "text_color": "#00F"
                                    }
                                ]
                            },
                            {
                                "description": "Partially overlapping ranges: non-conflicting values should stack",
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "First style; overlapped (font from first, spacing and color from second); second style",
                                "font_size": 12,
                                "ranges": [
                                    {
                                        "start": 0,
                                        "end": 73,
                                        "font_size": 18,
                                        "font_weight": "light"
                                    },
                                    {
                                        "start": 13,
                                        "end": 86,
                                        "letter_spacing": 1.5,
                                        "text_color": "#00F"
                                    }
                                ]
                            },
                            {
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "Range that ends beyond text length",
                                "ranges": [
                                    {
                                        "start": 0,
                                        "end": 100,
                                        "font_size": 18,
                                        "font_weight": "light",
                                        "letter_spacing": 0.5,
                                        "text_color": "#F00"
                                    }
                                ]
                            },
                            {
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "Range that starts and ends beyond text length",
                                "ranges": [
                                    {
                                        "start": 100,
                                        "end": 500,
                                        "font_size": 18,
                                        "font_weight": "light",
                                        "letter_spacing": 0.5,
                                        "text_color": "#F00"
                                    }
                                ]
                            },
                            {
                                "type": "text",
                                "width": { "type": "wrap_content" },
                                "text": "Range with start after end",
                                "ranges": [
                                    {
                                        "start": 10,
                                        "end": 5,
                                        "font_size": 18,
                                        "font_weight": "light",
                                        "letter_spacing": 0.5,
                                        "text_color": "#F00"
                                    }
                                ]
                            },
                            {
                                "type": "text",
                                "width": { "type": "fixed", "value": 120 },
                                "text": "Text with different sizes: small, big, small, big.",
                                "ranges": [
                                    {
                                        "start": 0,
                                        "end": 1,
                                        "font_size": 6
                                    },
                                    {
                                        "start": 1,
                                        "end": 2,
                                        "font_size": 7
                                    },
                                    {
                                        "start": 2,
                                        "end": 3,
                                        "font_size": 8
                                    },
                                    {
                                        "start": 3,
                                        "end": 4,
                                        "font_size": 9
                                    },
                                    {
                                        "start": 4,
                                        "end": 5,
                                        "font_size": 10
                                    },
                                    {
                                        "start": 5,
                                        "end": 6,
                                        "font_size": 11
                                    },
                                    {
                                        "start": 6,
                                        "end": 7,
                                        "font_size": 12
                                    },
                                    {
                                        "start": 7,
                                        "end": 8,
                                        "font_size": 13
                                    },
                                    {
                                        "start": 8,
                                        "end": 9,
                                        "font_size": 14
                                    },
                                    {
                                        "start": 9,
                                        "end": 10,
                                        "font_size": 15
                                    },
                                    {
                                        "start": 10,
                                        "end": 11,
                                        "font_size": 16
                                    },
                                    {
                                        "start": 11,
                                        "end": 12,
                                        "font_size": 17
                                    },
                                    {
                                        "start": 12,
                                        "end": 13,
                                        "font_size": 18
                                    },
                                    {
                                        "start": 13,
                                        "end": 14,
                                        "font_size": 19
                                    },
                                    {
                                        "start": 14,
                                        "end": 25,
                                        "font_size": 20
                                    },
                                    {
                                        "start": 27,
                                        "end": 33,
                                        "font_size": 9
                                    },
                                    {
                                        "start": 39,
                                        "end": 45,
                                        "font_size": 9
                                    },
                                    {
                                        "start": 34,
                                        "end": 38,
                                        "font_size": 25
                                    },
                                    {
                                        "start": 46,
                                        "end": 50,
                                        "font_size": 25
                                    }
                                ]
                            }
                        ]
                    }
                }
            ]
        }
};

// const json = {
//     "templates": {},
//     "card": {
//         "log_id": "snapshot_test_card",
//         "states": [
//             {
//                 "state_id": 0,
//                 "div": {
//                     type: "container",
//                     items: [{
//                         "type": "text",
//                         "width": {
//                             "type": "fixed",
//                             "value": 200
//                         },
//                         "text": "Main  attributes",
//                         "strike": "single",
//                         "underline": "single",
//                         "text_color": "#F00",
//                         "letter_spacing": 5,
//                         "font_size": 40,
//                         "font_weight": "bold",
//                         "images": [
//                             {
//                                 "start": 5,
//                                 "url": "https://alicekit.s3.yandex.net/images_for_divs/chess.png",
//                                 "width": {
//                                     "type": "fixed",
//                                     "value": 24
//                                 },
//                                 "height": {
//                                     "type": "fixed",
//                                     "value": 24
//                                 }
//                             }
//                         ],
//                         "ranges": [{
//                             "start": 1,
//                             "end": 4,
//                             "text_color": "#4eb0da",
//                             "actions": [{
//                                 "log_id": "abcde",
//                                 "url": "//ya.ru",
//                                 "target": "_blank"
//                             }]
//                         }],
//                         "border": {
//                             "corner_radius": 10,
//                             "corners_radius": {
//                                 "top-left": 10,
//                                 "top-right": 20,
//                                 "bottom-left": 30,
//                                 "bottom-right": 40,
//                             },
//                             "stroke": {
//                                 "width": 3,
//                                 "color": "#000"
//                             }
//                         },
//                         "accessibility": {
//                             "description": "bacde"
//                         },
//                         "background": [{
//                             "type": "solid",
//                             "color": "#ffcc00"
//                         }],
//                         "actions": [{
//                             "log_id": "abcde",
//                             "url": "//ya.ru"
//                         }],
//                         "visibility_actions": [{
//                             "log_id": "vis"
//                         }]
//                     }],
//                     "actions": [{
//                         "log_id": "abcde",
//                         "url": "//ya.ru"
//                     }]
//                 }
//             }
//         ]
//     }
// };

window.root = new Root({
    target: document.body,
    props: {
        id: 'abcde',
        json,
        /*onError({error}) {
            console.log(':) ' + error.message);
        }*/
        onStat(arg) {
            console.log(arg);
        }
    }
});
