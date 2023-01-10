const json1 = {
  "templates": {
    "title": {
      "type": "text",
      "font_size": 20,
      "line_height": 24,
      "font_weight": "bold",
      "paddings": {
        "left": 24,
        "right": 24,
        "bottom": 16
      }
    },
    "subtitle": {
      "font_size": 15,
      "line_height": 20,
      "type": "text",
      "paddings": {
        "left": 24,
        "right": 24
      }
    },
    "button": {
      "type": "text",
      "height": {
        "type": "fixed",
        "value": 48
      },
      "margins": {
        "left": 16,
        "right": 16,
        "bottom": 16
      },
      "border": {
        "corner_radius": 16
      },
      "background": [
        {
          "type": "solid",
          "$color": "background_color"
        }
      ],
      "font_size": 14,
      "font_weight": "medium",
      "text_alignment_vertical": "center",
      "text_alignment_horizontal": "center"
    }
  },
  "card": {
    "log_id": "sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "container",
          "orientation": "vertical",
          "margins": {
            "top": 24,
            "bottom": 24
          },
          "items": [
            {
              "type": "title",
              "text": "Tap animations"
            },
            {
              "type": "subtitle",
              "text": "If a div has at least one tap action specified, you can set a tap animation.\n\nFade, scale, and a combination of fade and scale animations are supported.",
              "margins": {
                "bottom": 24
              }
            },
            {
              "type": "button",
              "background_color": "#00B341",
              "text": "Fade",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "fade_button_press",
                  "url": "div-action://animation/fade"
                }
              ],
              "action_animation": {
                "name": "fade",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#0077FF",
              "text_color": "#ffffff",
              "text": "Scale",
              "actions": [
                {
                  "log_id": "scale_button_press",
                  "url": "div-action://animation/scale"
                }
              ],
              "action_animation": {
                "name": "scale",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#FFCC00",
              "text": "Set",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "set_button_press",
                  "url": "div-action://animation/set"
                }
              ],
              "action_animation": {
                "name": "set",
                "items": [
                  {
                    "name": "fade",
                    "start_value": 1,
                    "end_value": 0.2,
                    "duration": 300,
                    "interpolator": "ease_in_out"
                  },
                  {
                    "name": "scale",
                    "start_value": 1,
                    "end_value": 0.5,
                    "duration": 500,
                    "interpolator": "ease_in_out"
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
const json2 = {
  "templates": {
    "title": {
      "type": "text",
      "font_size": 20,
      "line_height": 24,
      "font_weight": "bold",
      "paddings": {
        "left": 24,
        "right": 24,
        "bottom": 16
      }
    },
    "subtitle": {
      "font_size": 15,
      "line_height": 20,
      "type": "text",
      "paddings": {
        "left": 24,
        "right": 24
      }
    },
    "button": {
      "type": "text",
      "height": {
        "type": "fixed",
        "value": 48
      },
      "margins": {
        "left": 16,
        "right": 16,
        "bottom": 16
      },
      "border": {
        "corner_radius": 16
      },
      "background": [
        {
          "type": "solid",
          "$color": "background_color"
        }
      ],
      "font_size": 14,
      "font_weight": "medium",
      "text_alignment_vertical": "center",
      "text_alignment_horizontal": "center"
    }
  },
  "card": {
    "log_id": "sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "container",
          "orientation": "vertical",
          "margins": {
            "top": 24,
            "bottom": 24
          },
          "items": [
            {
              "type": "title",
              "text": "Tap animations"
            },
            {
              "type": "subtitle",
              "text": "If a div has at least one tap action specified, you can set a tap animation.\n\nFade, scale, and a combination of fade and scale animations are supported.",
              "margins": {
                "bottom": 24
              }
            },
            {
              "type": "button",
              "background_color": "#00B341",
              "text": "Fade",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "fade_button_press",
                  "url": "div-action://animation/fade"
                }
              ],
              "action_animation": {
                "name": "fade",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#0077FF",
              "text_color": "#ffffff",
              "text": "Scale",
              "actions": [
                {
                  "log_id": "scale_button_press",
                  "url": "div-action://animation/scale"
                }
              ],
              "action_animation": {
                "name": "scale",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#FFCC00",
              "text": "Set",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "set_button_press",
                  "url": "div-action://animation/set"
                }
              ],
              "action_animation": {
                "name": "set",
                "items": [
                  {
                    "name": "fade",
                    "start_value": 1,
                    "end_value": 0.2,
                    "duration": 300,
                    "interpolator": "ease_in_out"
                  },
                  {
                    "name": "scale",
                    "start_value": 1,
                    "end_value": 0.5,
                    "duration": 500,
                    "interpolator": "ease_in_out"
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
const json3 = {
  "templates": {
    "title": {
      "type": "text",
      "font_size": 20,
      "line_height": 24,
      "font_weight": "bold",
      "paddings": {
        "left": 24,
        "right": 24,
        "bottom": 16
      }
    },
    "subtitle": {
      "font_size": 15,
      "line_height": 20,
      "type": "text",
      "paddings": {
        "left": 24,
        "right": 24
      }
    },
    "button": {
      "type": "text",
      "height": {
        "type": "fixed",
        "value": 48
      },
      "margins": {
        "left": 16,
        "right": 16,
        "bottom": 16
      },
      "border": {
        "corner_radius": 16
      },
      "background": [
        {
          "type": "solid",
          "$color": "background_color"
        }
      ],
      "font_size": 14,
      "font_weight": "medium",
      "text_alignment_vertical": "center",
      "text_alignment_horizontal": "center"
    }
  },
  "card": {
    "log_id": "sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "container",
          "orientation": "vertical",
          "margins": {
            "top": 24,
            "bottom": 24
          },
          "items": [
            {
              "type": "title",
              "text": "Tap animations"
            },
            {
              "type": "subtitle",
              "text": "If a div has at least one tap action specified, you can set a tap animation.\n\nFade, scale, and a combination of fade and scale animations are supported.",
              "margins": {
                "bottom": 24
              }
            },
            {
              "type": "button",
              "background_color": "#00B341",
              "text": "Fade",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "fade_button_press",
                  "url": "div-action://animation/fade"
                }
              ],
              "action_animation": {
                "name": "fade",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#0077FF",
              "text_color": "#ffffff",
              "text": "Scale",
              "actions": [
                {
                  "log_id": "scale_button_press",
                  "url": "div-action://animation/scale"
                }
              ],
              "action_animation": {
                "name": "scale",
                "start_value": 1,
                "end_value": 0.4,
                "duration": 500,
                "interpolator": "ease_in_out"
              }
            },
            {
              "type": "button",
              "background_color": "#FFCC00",
              "text": "Set",
              "text_color": "#000000",
              "actions": [
                {
                  "log_id": "set_button_press",
                  "url": "div-action://animation/set"
                }
              ],
              "action_animation": {
                "name": "set",
                "items": [
                  {
                    "name": "fade",
                    "start_value": 1,
                    "end_value": 0.2,
                    "duration": 300,
                    "interpolator": "ease_in_out"
                  },
                  {
                    "name": "scale",
                    "start_value": 1,
                    "end_value": 0.5,
                    "duration": 500,
                    "interpolator": "ease_in_out"
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

export class MockData {
  templates = [
    {
      id: '111',
      title: 'Title 1',
      description: 'Some description',
      tags: [
        {
          id: '7ba25537-79ea-458e-9a80-69ffd917dcac', tag: 'butterfly'
        },
        {
          id: '8f420c37-bb58-4e85-9b44-daa7de70d42b', tag: 'cards'
        }
      ],
      template: json1
    },
    {
      id: '222',
      title: 'Title 2',
      description: 'Some description',
      tags: [
        {
          id: '7ba25537-79ea-458e-9a80-69ffd917dcac', tag: 'butterfly'
        },
        {
          id: '8f420c37-bb58-4e85-9b44-daa7de70d42b', tag: 'cards'
        }
      ],
      template: json2
    },
    {
      id: '333',
      title: 'Title 3',
      description: 'Some description',
      tags: [],
      template: json3
    },
  ]
  tags = [
    {
      id: '111',
      tag: 'abc'
    },
    {
      id: '222',
      tag: 'edc'
    },
    {
      id: '333',
      tag: 'ads'
    },
  ]
}
