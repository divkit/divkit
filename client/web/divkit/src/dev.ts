import Root from './components/Root.svelte';
import { SizeProvider } from './extensions/sizeProvider';
import { Gesture } from './extensions/gesture';
import { lottieExtensionBuilder } from './extensions/lottie';
import { markdownExtensionBuilder } from './extensions/markdown';
import type { DivExtensionClass } from '../typings/common';
import Lottie from 'lottie-web/build/player/lottie';
import markdownit from 'markdown-it';
import { initComponents } from './devCustomComponents';

import markdownCss from './devMarkdown.module.css';

const md = markdownit();

const json = {
    "templates": {
      "base_text": {
        "type": "text",
        "text_color": "@{argb(getColorAlpha('#FFECF0F1'), getColorRed('#FFECF0F1'), getColorGreen('#FFECF0F1'), getColorBlue('#FFECF0F1'))}"
      },
      "base_separator": {
        "type": "separator",
        "delimiter_style": {
          "color": "#00000000"
        }
      },
      "title_text": {
        "type": "base_text",
        "paddings": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 10,
          "bottom": 10
        },
        "background": [
          {
            "type": "solid",
            "color": "@{sky_blue}"
          }
        ],
        "font_size": 16,
        "font_weight": "medium",
        "text_alignment_horizontal": "center"
      },
      "invisible_text": {
        "type": "base_text",
        "paddings": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 10,
          "bottom": 10
        },
        "visibility": "invisible"
      },
      "gone_text": {
        "type": "base_text",
        "paddings": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 10,
          "bottom": 10
        },
        "visibility": "gone"
      },
      "base_input": {
        "type": "input",
        "margins": {
          "left": 8,
          "top": 8,
          "right": 8,
          "bottom": 8
        },
        "paddings": {
          "left": 8,
          "top": 8,
          "right": 8,
          "bottom": 8
        },
        "text_color": "@{argb(getColorAlpha('#FFECF0F1'), getColorRed('#FFECF0F1'), getColorGreen('#FFECF0F1'), getColorBlue('#FFECF0F1'))}",
        "hint_text": "Hint text",
        "hint_color": "#@{golden_dragon}"
      },
      "card_header": {
        "type": "container",
        "orientation": "horizontal",
        "items": [
          {
            "type": "base_text",
            "paddings": {
              "left": "@{padding.left}",
              "right": "@{padding.right}",
              "top": 20,
              "bottom": 10
            },
            "font_size": 20,
            "font_weight": "regular",
            "$text": "header_text"
          },
          {
            "type": "image",
            "width": {
              "type": "fixed",
              "value": 40
            },
            "height": {
              "type": "fixed",
              "value": 40
            },
            "paddings": {
              "left": 20,
              "right": 20,
              "top": 20,
              "bottom": 20
            },
            "placeholder_color": "#00000000",
            "image_url": "file:///android_asset/images/benchmark/menu.png",
            "action": {
              "log_id": "benchmark/menu",
              "menu_items": [
                {
                  "text": "Menu item 1",
                  "action": {
                    "log_id": "benchmark/menu/item_1",
                    "url": "@{empty_action}"
                  }
                },
                {
                  "text": "Menu item 2",
                  "action": {
                    "log_id": "benchmark/menu/item_2",
                    "url": "@{empty_action}"
                  }
                },
                {
                  "text": "Menu item 3",
                  "action": {
                    "log_id": "benchmark/menu/item_3",
                    "url": "@{empty_action}"
                  }
                },
                {
                  "text": "Menu item 4",
                  "action": {
                    "log_id": "benchmark/menu/item_4",
                    "url": "@{empty_action}"
                  }
                }
              ]
            }
          }
        ]
      },
      "tab_pager": {
        "type": "tabs",
        "has_separator": true,
        "separator_color": "@{dark_sky}",
        "separator_paddings": {
          "left": 0,
          "top": 0,
          "right": 0,
          "bottom": 0
        },
        "tab_title_style": {
          "paddings": {
            "left": 10,
            "right": 10,
            "top": 5,
            "bottom": 5
          },
          "active_text_color": "@{dark_sky}",
          "inactive_text_color": "#ECF0F1",
          "active_background_color": "@{mint_green}",
          "inactive_background_color": "#00000000"
        },
        "title_paddings": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 10,
          "bottom": 10
        }
      },
      "page": {
        "type": "gallery",
        "paddings": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 10,
          "bottom": 10
        },
        "item_spacing": 20
      },
      "page_element": {
        "type": "container",
        "width": {
          "type": "fixed",
          "value": 224
        },
        "background": [
          {
            "type": "solid",
            "color": "@{dark_sky}"
          }
        ],
        "border": {
          "corner_radius": 8
        }
      },
      "block_with_grids": {
        "type": "page_element"
      },
      "list_item": {
        "type": "grid",
        "height": {
          "type": "fixed",
          "value": 64
        },
        "column_count": 4,
        "items": [
          {
            "type": "base_separator",
            "width": {
              "type": "fixed",
              "value": 4
            },
            "height": {
              "type": "match_parent"
            },
            "row_span": 3,
            "background": [
              {
                "type": "solid",
                "$color": "indicator_color"
              }
            ]
          },
          {
            "type": "image",
            "width": {
              "type": "fixed",
              "value": 32
            },
            "height": {
              "type": "fixed",
              "value": 32
            },
            "margins": {
              "left": "@{padding.left}",
              "right": "@{padding.right}"
            },
            "row_span": 2,
            "alignment_horizontal": "center",
            "alignment_vertical": "center",
            "placeholder_color": "#00000000",
            "$image_url": "icon_url"
          },
          {
            "type": "base_text",
            "width": {
              "type": "match_parent",
              "weight": 1
            },
            "height": {
              "type": "match_parent",
              "weight": 1
            },
            "font_size": 14,
            "text_alignment_vertical": "bottom",
            "$text": "title",
            "visibility_actions": [
              {
                "log_id": "benchmark/list_item/title",
                "url": "@{empty_action}"
              }
            ]
          },
          {
            "type": "base_text",
            "width": {
              "type": "wrap_content"
            },
            "height": {
              "type": "fixed",
              "value": 24
            },
            "margins": {
              "left": "@{padding.left}",
              "right": "@{padding.right}"
            },
            "paddings": {
              "left": 8,
              "right": 8
            },
            "alignment_horizontal": "center",
            "alignment_vertical": "center",
            "row_span": 2,
            "background": [
              {
                "type": "solid",
                "color": "@{cinnabar}"
              }
            ],
            "border": {
              "corner_radius": 12
            },
            "font_weight": "medium",
            "text_alignment_vertical": "center",
            "$text": "counter"
          },
          {
            "type": "base_text",
            "width": {
              "type": "match_parent",
              "weight": 1
            },
            "height": {
              "type": "match_parent",
              "weight": 1
            },
            "font_weight": "medium",
            "text_alignment_vertical": "top",
            "text_color": "@{setColorBlue(setColorGreen(setColorRed(setColorAlpha(rgb(0.0, 0.0, 0.0), 1.0), 0.7412), 0.7647), 0.7804)}",
            "$text": "subtitle"
          },
          {
            "type": "base_separator",
            "width": {
              "type": "match_parent"
            },
            "height": {
              "type": "fixed",
              "value": 1
            },
            "column_span": 3,
            "background": [
              {
                "type": "solid",
                "color": "@{rgb(div(mul(3.0, 16.0) + 4.0, 255.0), div(mul(4.0, 16.0) + 9.0, 255.0), div(mul(5.0, 16.0) + 14.0, 255.0))}"
              }
            ]
          }
        ]
      },
      "form": {
        "type": "page_element",
        "items": [
          {
            "type": "title_text",
            "$text": "form_title"
          },
          {
            "type": "container",
            "orientation": "horizontal",
            "layout_mode": "wrap",
            "separator": {
              "show_at_start": true,
              "style": {
                "type": "shape_drawable",
                "shape": {
                  "type": "rounded_rectangle",
                  "item_width": {
                    "type": "fixed",
                    "value": 8
                  },
                  "item_height": {
                    "type": "fixed",
                    "value": 8
                  },
                  "corner_radius": {
                    "type": "fixed",
                    "value": 4
                  }
                },
                "color": "@{sky_blue}"
              }
            },
            "items": [
              {
                "type": "base_text",
                "width": {
                  "type": "wrap_content"
                },
                "margins": {
                  "left": "@{padding.left}",
                  "right": "@{padding.right}"
                },
                "line_height": 16,
                "$text": "top_input_title_1"
              },
              {
                "type": "base_text",
                "width": {
                  "type": "wrap_content"
                },
                "margins": {
                  "left": "@{padding.left}",
                  "right": "@{padding.right}"
                },
                "line_height": 16,
                "$text": "top_input_title_2"
              },
              {
                "type": "base_input",
                "border": {
                  "corner_radius": 8,
                  "stroke": {
                    "color": "#@{golden_dragon}"
                  }
                },
                "$text_variable": "top_input_variable"
              },
              {
                "type": "base_text",
                "width": {
                  "type": "wrap_content"
                },
                "margins": {
                  "left": "@{padding.left}"
                },
                "line_height": 16,
                "$text": "middle_input_title"
              },
              {
                "type": "base_input",
                "native_interface": {
                  "color": "#@{golden_dragon}"
                },
                "$text_variable": "middle_input_variable"
              },
              {
                "type": "base_text",
                "width": {
                  "type": "wrap_content"
                },
                "margins": {
                  "left": "@{padding.left}"
                },
                "line_height": 16,
                "$text": "bottom_input_title_1"
              },
              {
                "type": "base_text",
                "width": {
                  "type": "wrap_content"
                },
                "line_height": 16,
                "$text": "bottom_input_title_2"
              },
              {
                "type": "base_input",
                "native_interface": {
                  "color": "@{vivid_orange}"
                },
                "$text_variable": "bottom_input_variable"
              }
            ]
          }
        ]
      },
      "block_with_variables": {
        "type": "page_element",
        "items": [
          {
            "type": "title_text",
            "text": "VARIABLES"
          },
          {
            "type": "base_text",
            "margins": {
              "left": "@{padding.left}",
              "right": "@{padding.right}",
              "top": 10
            },
            "line_height": 16,
            "text": "counter: @{counter}\nempty_action: '@{empty_action}'\npadding.left: '@{padding.left}'\npadding.right: '@{padding.right}'\nsky_blue: '@{sky_blue}'\nmint_green: '@{mint_green}'\ndark_sky: '@{dark_sky}'\ngolden_dragon: '@{golden_dragon}'\nmedium_sea_green: '@{medium_sea_green}'\nvivid_orange: '@{vivid_orange}'\ncinnabar: '@{cinnabar}'"
          },
          {
            "type": "base_text",
            "margins": {
              "left": "@{padding.left}",
              "right": "@{padding.right}",
              "top": 10,
              "bottom": 10
            },
            "text": "#lorem #ipsum #dolor #sit #amet",
            "ranges": [
              {
                "start": 0,
                "end": 6,
                "text_color": "@{mint_green}"
              },
              {
                "start": 7,
                "end": 13,
                "text_color": "@{medium_sea_green}"
              },
              {
                "start": 14,
                "end": 20,
                "text_color": "#@{golden_dragon}"
              },
              {
                "start": 21,
                "end": 25,
                "text_color": "@{vivid_orange}"
              },
              {
                "start": 26,
                "end": 31,
                "text_color": "@{cinnabar}"
              }
            ]
          }
        ]
      },
      "block_with_invisible_items": {
        "type": "page_element",
        "items": [
          {
            "type": "title_text",
            "text": "INVISIBLE ITEMS"
          },
          {
            "type": "invisible_text",
            "text": "Item 1"
          },
          {
            "type": "gone_text",
            "text": "Item 2"
          },
          {
            "type": "invisible_text",
            "text": "Item 3"
          },
          {
            "type": "gone_text",
            "text": "Item 4"
          },
          {
            "type": "invisible_text",
            "text": "Item 5"
          },
          {
            "type": "gone_text",
            "text": "Item 6"
          },
          {
            "type": "invisible_text",
            "text": "Item 7"
          },
          {
            "type": "gone_text",
            "text": "Item 8"
          },
          {
            "type": "invisible_text",
            "text": "Item 9"
          },
          {
            "type": "gone_text",
            "text": "Item 10"
          }
        ]
      },
      "button": {
        "type": "text",
        "height": {
          "type": "fixed",
          "value": 48
        },
        "margins": {
          "left": "@{padding.left}",
          "right": "@{padding.right}",
          "top": 20,
          "bottom": 20
        },
        "background": [
          {
            "type": "solid",
            "color": "@{mint_green}"
          }
        ],
        "border": {
          "corner_radius": 4
        },
        "font_size": 16,
        "font_weight": "medium",
        "text_alignment_horizontal": "center",
        "text_alignment_vertical": "center",
        "text_color": "@{dark_sky}"
      },
      "benchmark_card": {
        "type": "container",
        "margins": {
          "left": 8,
          "top": 8,
          "right": 8,
          "bottom": 8
        },
        "background": [
          {
            "type": "solid",
            "color": "@{rgb(div(mul(3.0, 16.0) + 4.0, 255.0), div(mul(4.0, 16.0) + 9.0, 255.0), div(mul(5.0, 16.0) + 14.0, 255.0))}"
          }
        ],
        "border": {
          "corner_radius": 8,
          "has_shadow": 1
        },
        "items": [
          {
            "type": "card_header"
          },
          {
            "type": "tab_pager",
            "$items": "pages"
          },
          {
            "type": "separator",
            "delimiter_style": {
              "color": "@{dark_sky}"
            }
          },
          {
            "type": "button",
            "$text": "button_text",
            "$action": "button_action"
          }
        ]
      }
    },
    "card": {
      "log_id": "benchmark",
      "variables": [
        {
          "name": "counter",
          "type": "integer",
          "value": 1
        },
        {
          "name": "empty_action",
          "type": "url",
          "value": "action://none"
        },
        {
          "name": "padding.left",
          "type": "integer",
          "value": 20
        },
        {
          "name": "padding.right",
          "type": "integer",
          "value": 20
        },
        {
          "name": "sky_blue",
          "type": "color",
          "value": "#3498DB"
        },
        {
          "name": "mint_green",
          "type": "color",
          "value": "#1ABC9C"
        },
        {
          "name": "dark_sky",
          "type": "color",
          "value": "#2C3E50"
        },
        {
          "name": "golden_dragon",
          "type": "string",
          "value": "F1C40F"
        },
        {
          "name": "medium_sea_green",
          "type": "string",
          "value": "#2ECC71"
        },
        {
          "name": "vivid_orange",
          "type": "color",
          "value": "#E67E22"
        },
        {
          "name": "cinnabar",
          "type": "string",
          "value": "#E74C3C"
        },
        {
          "name": "top_input_var",
          "type": "string",
          "value": "top input variable value"
        },
        {
          "name": "middle_input_var",
          "type": "string",
          "value": "middle input variable value"
        },
        {
          "name": "bottom_input_var",
          "type": "string",
          "value": ""
        }
      ],
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "benchmark_card",
            "header_text": "Benchmark card",
            "button_text": "Counter: @{counter}",
            "button_action": {
              "log_id": "benchmark/button",
              "url": "@{empty_action}"
            },
            "pages": [
              {
                "title": "Page 1",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 2",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 3",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 4",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 5",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 6",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 7",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              },
              {
                "title": "Page 8",
                "div": {
                  "type": "page",
                  "items": [
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    },
                    {
                      "type": "block_with_grids",
                      "items": [
                        {
                          "type": "list_item",
                          "indicator_color": "@{sky_blue}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_1",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{mint_green}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_2",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "#@{golden_dragon}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_3",
                            "url": "@{empty_action}"
                          }
                        },
                        {
                          "type": "list_item",
                          "indicator_color": "@{vivid_orange}",
                          "icon_url": "file:///android_asset/images/benchmark/list_item_icon.png",
                          "title": "Item title",
                          "subtitle": "Item subtitle",
                          "counter": "1K",
                          "action": {
                            "log_id": "benchmark/list_item_4",
                            "url": "@{empty_action}"
                          }
                        }
                      ]
                    },
                    {
                      "type": "block_with_variables"
                    },
                    {
                      "type": "block_with_invisible_items"
                    },
                    {
                      "type": "form",
                      "form_title": "FORM TITLE",
                      "top_input_title_1": "Long top input title 1",
                      "top_input_title_2": "Long top input title 2",
                      "top_input_variable": "top_input_var",
                      "middle_input_title": "Middle input title",
                      "middle_input_variable": "middle_input_var",
                      "bottom_input_title_1": "Bottom",
                      "bottom_input_title_2": "input_title",
                      "bottom_input_variable": "bottom_input_var"
                    }
                  ]
                }
              }
            ]
          }
        }
      ]
    }
  }
  ;

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
            ['gesture', Gesture],
            ['lottie', lottieExtensionBuilder(Lottie.loadAnimation)],
            ['markdown', markdownExtensionBuilder(str => md.render(str), {
                cssClass: markdownCss['markdown-extension']
            })],
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
                    localStorage.setItem('divkit:' + name, JSON.stringify({ value, type, lifetime: Date.now() + lifetime * 1000 }));
                } catch (err) {
                    //
                }
            },
        }
    }
});

initComponents();
