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
    }
  }
}`;
