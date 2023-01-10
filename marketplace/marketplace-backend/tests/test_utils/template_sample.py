# pylint: disable-all

template_samples = [
    {
        "templates": {
            "text_block": {
                "type": "text",
                "width": {"type": "match_parent"},
                "height": {"type": "wrap_content"},
                "paddings": {"left": 24, "top": 16, "right": 16, "bottom": 16},
                "margins": {"left": 24, "right": 24},
                "border": {"corner_radius": 8},
                "background": [{"type": "solid", "color": "#0E000000"}],
                "font_size": 14,
                "font_weight": "medium",
                "text_alignment_vertical": "center",
                "text_alignment_horizontal": "center",
                "text_color": "#000000",
            },
            "title": {
                "type": "text",
                "font_size": 20,
                "line_height": 24,
                "font_weight": "bold",
                "paddings": {"left": 24, "right": 24, "bottom": 16},
            },
            "subtitle": {
                "font_size": 15,
                "line_height": 20,
                "type": "text",
                "paddings": {"left": 24, "right": 24},
            },
        },
        "card": {
            "log_id": "sample_card",
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "container",
                        "orientation": "vertical",
                        "margins": {"top": 24, "bottom": 24},
                        "items": [
                            {"type": "title", "text": "A set of states"},
                            {
                                "type": "subtitle",
                                "text": "States can be used to create interactive elements, such as buttons or expandable content.\n\nThe example shows a button that changes its own text when clicked.",
                                "margins": {"bottom": 12},
                            },
                            {
                                "type": "state",
                                "div_id": "sample",
                                "states": [
                                    {
                                        "state_id": "first",
                                        "div": {
                                            "type": "text_block",
                                            "text": "This is first state",
                                            "actions": [
                                                {
                                                    "log_id": "switch_state1",
                                                    "url": "div-action://set_state?state_id=0/sample/second",
                                                }
                                            ],
                                        },
                                    },
                                    {
                                        "state_id": "second",
                                        "div": {
                                            "type": "text_block",
                                            "text": "This is second state",
                                            "actions": [
                                                {
                                                    "log_id": "switch_state1",
                                                    "url": "div-action://set_state?state_id=0/sample/first",
                                                }
                                            ],
                                        },
                                    },
                                ],
                            },
                        ],
                    },
                }
            ],
        },
    },
    {
        "templates": {
            "title": {
                "type": "text",
                "font_size": 20,
                "line_height": 24,
                "font_weight": "bold",
                "paddings": {"left": 24, "right": 24, "bottom": 16},
            },
            "subtitle": {
                "font_size": 15,
                "line_height": 20,
                "type": "text",
                "paddings": {"left": 24, "right": 24},
            },
            "input_text_borderless": {
                "type": "input",
                "width": {"type": "match_parent"},
                "height": {"type": "wrap_content"},
                "margins": {"left": 16, "top": 20, "right": 16, "bottom": 16},
                "paddings": {"left": 16, "top": 10, "right": 16, "bottom": 10},
                "alignment_horizontal": "center",
                "alignment_vertical": "center",
                "font_size": 16,
                "font_weight": "medium",
                "text_color": "#000000",
                "hint_color": "#888888",
                "highlight_color": "#e0bae3",
                "line_height": 22,
            },
            "input_text": {
                "type": "input_text_borderless",
                "background": [{"type": "solid", "color": "#0e000000"}],
                "border": {"corner_radius": 8},
            },
            "output_text": {
                "type": "text",
                "width": {"type": "match_parent"},
                "height": {"type": "wrap_content"},
                "paddings": {"left": 24, "right": 24, "bottom": 16},
                "alpha": 1,
                "alignment_horizontal": "center",
                "alignment_vertical": "center",
                "font_size": 16,
                "font_weight": "medium",
                "text_alignment_horizontal": "left",
                "text_alignment_vertical": "center",
                "text_color": "#000000",
            },
        },
        "card": {
            "log_id": "sample_card",
            "variables": [
                {
                    "name": "my_single_text",
                    "type": "string",
                    "value": "This is single-line input",
                },
                {
                    "name": "my_multi_text",
                    "type": "string",
                    "value": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmodtempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                },
                {"name": "my_borderless_text", "type": "string", "value": ""},
                {"name": "my_email_text", "type": "string", "value": ""},
                {"name": "my_number_text", "type": "string", "value": ""},
                {
                    "name": "my_focus_text",
                    "type": "string",
                    "value": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmodtempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.",
                },
            ],
            "states": [
                {
                    "state_id": 0,
                    "div": {
                        "type": "container",
                        "orientation": "vertical",
                        "margins": {"top": 24, "bottom": 24},
                        "items": [
                            {"type": "title", "text": "Text inputs"},
                            {
                                "type": "subtitle",
                                "text": "You can input your own data and interact with it.",
                            },
                            {
                                "type": "input_text",
                                "text_variable": "my_single_text",
                                "keyboard_type": "single_line_text",
                            },
                            {
                                "type": "output_text",
                                "text": "Text: @{my_single_text}",
                                "ranges": [
                                    {"start": 0, "end": 5, "text_color": "#777777"}
                                ],
                            },
                            {
                                "type": "subtitle",
                                "text": "The input can consist of either one or multiple lines.",
                            },
                            {"type": "input_text", "text_variable": "my_multi_text"},
                            {
                                "type": "subtitle",
                                "text": "The input text can also be presented in a format of an underlined text.",
                            },
                            {
                                "type": "input_text_borderless",
                                "hint_text": "This is borderless text",
                                "native_interface": {"color": "#000000"},
                                "text_variable": "my_borderless_text",
                            },
                            {
                                "type": "subtitle",
                                "text": "There are multiple types of keyboard inputs, for example, for numbers, phones, e-mails etc.",
                            },
                            {
                                "type": "input_text",
                                "keyboard_type": "email",
                                "text_variable": "my_email_text",
                                "hint_text": "This is input for e-mails",
                            },
                            {
                                "type": "input_text",
                                "keyboard_type": "number",
                                "text_variable": "my_number_text",
                                "hint_text": "This is input for numbers",
                            },
                            {
                                "type": "subtitle",
                                "text": "There are also different features, such as changing color of text, changing spacing between lines and symbols or making the whole text selected on tapping.",
                            },
                            {
                                "type": "subtitle",
                                "text": "The following text will be selected fully when focused.",
                            },
                            {
                                "type": "input_text",
                                "text_variable": "my_focus_text",
                                "select_all_on_focus": 1,
                                "line_height": 32,
                                "letter_spacing": 2,
                                "text_color": "#FF0000",
                            },
                        ],
                    },
                }
            ],
        },
    },
]
