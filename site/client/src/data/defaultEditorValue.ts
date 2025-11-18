export const DEFAULT_EDITOR_VALUE = `{
  "card": {
    "log_id": "div2_sample_card",
    "states": [
      {
        "state_id": 0,
        "div": {
          "items": [
            {
              "type": "_template_close",
              "alignment_horizontal": "right",
              "height": {
                "type": "fixed",
                "value": 28
              },
              "margins": {
                "top": 20,
                "right": 24
              },
              "width": {
                "type": "fixed",
                "value": 28
              }
            }
          ],
          "background": [
            {
              "color": "#F1EBDC",
              "type": "solid"
            }
          ],
          "height": {
            "type": "match_parent"
          },
          "orientation": "overlap",
          "type": "container"
        }
      }
    ]
  },
  "templates": {
    "_template_lottie": {
      "type": "gif",
      "scale": "fit",
      "extensions": [
        {
          "id": "lottie",
          "params": {
            "$lottie_url": "lottie_url"
          }
        }
      ],
      "gif_url": "https://empty"
    },
    "_template_button": {
      "type": "text",
      "content_alignment_horizontal": "center",
      "border": {
        "$corner_radius": "corners"
      },
      "paddings": {
        "bottom": 24,
        "left": 28,
        "right": 28,
        "top": 22
      },
      "width": {
        "type": "wrap_content"
      }
    },
    "_template_close": {
      "accessibility": {
        "description": "Закрыть",
        "mode": "merge",
        "type": "button"
      },
      "actions": [
        {
          "log_id": "close_popup",
          "url": "div-screen://close"
        }
      ],
      "image_url": "https://yastatic.net/s3/home/div/div_fullscreens/cross2.3.png",
      "tint_color": "#73000000",
      "type": "image"
    },
    "_template_list_item": {
      "type": "container",
      "orientation": "horizontal",
      "items": [
        {
          "type": "image",
          "image_url": "https://yastatic.net/s3/home/div/div_fullscreens/hyphen.4.png",
          "$tint_color": "list_color",
          "width": {
            "type": "fixed",
            "value": 28,
            "unit": "sp"
          },
          "height": {
            "type": "fixed",
            "value": 28,
            "unit": "sp"
          },
          "margins": {
            "top": 2,
            "right": 12,
            "bottom": 2
          }
        },
        {
          "type": "text",
          "$text": "list_text",
          "$text_color": "list_color",
          "font_size": 24,
          "line_height": 32,
          "font_weight": "medium",
          "width": {
            "type": "wrap_content",
            "constrained": true
          }
        }
      ]
    }
  }
}`;
