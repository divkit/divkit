package com.yandex.divkit.multiplaform.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.yandex.divkit.multiplatform.DivKit
import com.yandex.divkit.multiplatform.dependencies.ActionHandler
import com.yandex.divkit.multiplatform.dependencies.DivKitDependencies
import com.yandex.divkit.multiplatform.dependencies.ErrorReporter

@Composable
internal fun MainScreen() {
    DivKit(dependencies) {
        val isDarkTheme = isSystemInDarkTheme()
        var globalVar by variable(name = "global_var", value = "<initial>")
        var isDarkThemeVar by variable(name = "is_dark_theme", value = isDarkTheme)
        LaunchedEffect(isDarkTheme) {
            isDarkThemeVar = isDarkTheme
        }
        Column(
            modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.systemBars),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DivView(
                cardId = "Sample",
                jsonData = jsonString,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = {
                    globalVar = "Set by Compose Button"
                },
                content = {
                    Text("Set Global Variable by Compose")
                }
            )
        }
    }
}

private class ActionHandlerImpl(
    private val handleAction: (DivKit, Map<String, Any>) -> Unit
): ActionHandler {
    override fun handle(divKit: DivKit, url: String) {
        if (url.startsWith("client-action") && url.endsWith("set_global_var")) {
            handleAction(divKit, mapOf("global_var" to "Set by DivKit action"))
        }
    }
}

private class ErrorReporterImpl: ErrorReporter {
    override fun report(cardId: String, message: String) {
        println("[Error] cardId: $cardId, message: $message")
    }
}

private val dependencies = DivKitDependencies(
    actionHandler = ActionHandlerImpl { divKit, variables ->
        divKit.setVariables(variables)
    },
    errorReporter = ErrorReporterImpl()
)

private val jsonString = """
{
  "templates": {
    "divgram_card": {
      "type": "container",
      "margins": {
        "left": 12,
        "right": 12,
        "top": 12,
        "bottom": 12
      },
      "background": [
        {
          "type": "solid",
          "color": "@{is_dark_theme ? '#000' : '#FFF'}"
        }
      ],
      "border": {
        "corner_radius": 14,
        "stroke": {
          "color": "#666"
        },
        "shadow": {
          "offset": {
            "x": {
              "value": 1
            },
            "y": {
              "value": 4
            }
          },
          "color": "#000"
        },
        "has_shadow": true
      },
      "items": [
        {
          "type": "image",
          "${'$'}image_url": "post_image",
          "aspect": {
            "ratio": 1
          },
          "content_alignment_vertical": "top"
        },
        {
          "type": "container",
          "orientation": "horizontal",
          "margins": {
            "left": 12,
            "right": 12,
            "top": 12,
            "bottom": 12
          },
          "content_alignment_vertical": "center",
          "items": [
            {
              "type": "text",
              "${'$'}text": "author",
              "text_color": "@{is_dark_theme ? '#FFF' : '#000'}",
              "font_size": 18,
              "ranges": [
                {
                  "start": 0,
                  "${'$'}end": "author_prefix_end",
                  "font_size": 14
                }
              ]
            }
          ]
        }
      ]
    }
  },
  "card": {
    "timers": [
      {
        "id": "like_timer",
        "tick_interval": 2000,
        "tick_actions": [
          {
            "log_id": "card0.add_timer_like",
            "url": "@{card0.likes > 7 ? '' : 'div-action://set_variable?name=card0.likes&value=@{card0.likes + 1}'}"
          }
        ]
      }
    ],
    "variables": [
      {
        "name": "color.background",
        "type": "color",
        "value": "#5e6e7d"
      },
      {
        "name": "card0.likes",
        "type": "integer",
        "value": 5
      },
      {
        "name": "card1.likes",
        "type": "integer",
        "value": 73
      },
      {
        "name": "card2.likes",
        "type": "integer",
        "value": 1001001
      },
      {
        "name": "cup.likes",
        "type": "integer",
        "value": 2
      },
      {
        "name": "toast_text",
        "type": "string",
        "value": ""
      }
    ],
    "log_id": "divgram",
    "states": [
      {
        "state_id": 0,
        "div": {
          "type": "gallery",
          "cross_content_alignment": "center",
          "orientation": "vertical",
          "height": {
            "type": "match_parent"
          },
          "width": {
            "type": "match_parent"
          },
          "items": [
            {
                "type": "text",
                "text": "Set Global Variable by DivKit",
                "paddings": {
                    "top": 10,
                    "left": 10,
                    "right": 10,
                    "bottom": 10
                },
                "width": {
                    "type": "wrap_content"
                },
                "height": {
                    "type": "wrap_content"
                },
                "margins": {
                    "top": 20
                },
                "font_size": 20,
                "text_color": "@{is_dark_theme ? '#000' : '#FFF'}",
                "border": {
                    "corner_radius": 12
                },
                "background": [
                    {
                        "type": "solid",
                        "color": "@{is_dark_theme ? '#FFF' : '#000'}"
                    }
                ],
                "actions": [
                    {
                        "log_id": "set_global_var",
                        "url": "client-action://set_global_var"
                    }
                ]
            },
            {
                "type": "text",
                "text": "Global var = @{global_var}",
                "width": {
                    "type": "wrap_content"
                },
                "height": {
                    "type": "wrap_content"
                },
                "paddings": {
                    "top": 10,
                    "left": 10,
                    "right": 10,
                    "bottom": 10
                },
                "margins": {
                    "top": 20
                },
                "font_size": 20,
                "text_color": "@{is_dark_theme ? '#000' : '#FFF'}",
                "border": {
                    "corner_radius": 12
                },
                "background": [
                    {
                        "type": "solid",
                        "color": "@{is_dark_theme ? '#FFF' : '#000'}"
                    }
                ]
            },
            {
              "type": "divgram_card",
              "likes_counter": "@{card0.likes}",
              "post_image": "https://yastatic.net/s3/home/divkit/wombat.jpg",
              "author": "by @yandex_art",
              "author_prefix_end": 3,
              "state_likes_id": "card0.likes",
              "visibility_actions": [
                {
                  "log_id": "card_visible",
                  "url": "div-action://timer?id=like_timer&action=start"
                }
              ],
              "add_like_log_id": "card0.add_like",
              "add_like_url": "div-action://set_variable?name=card0.likes&value=@{card0.likes + 1}",
              "set_liked_log_id": "card0.set_liked",
              "set_liked_url": "div-action://set_state?state_id=0/card0.likes/liked",
              "remove_like_log_id": "card0.remove_like",
              "remove_like_url": "div-action://set_variable?name=card0.likes&value=@{card0.likes - 1}",
              "set_disliked_log_id": "card0.set_disliked",
              "set_disliked_url": "div-action://set_state?state_id=0/card0.likes/disliked"
            },
            {
              "type": "divgram_card",
              "likes_counter": "@{card1.likes}",
              "post_image": "https://yastatic.net/s3/home/divkit/ducks.jpg",
              "author": "from @shedevrum",
              "author_prefix_end": 5,
              "state_likes_id": "card1.likes",
              "add_like_log_id": "card1.add_like",
              "add_like_url": "div-action://set_variable?name=card1.likes&value=@{card1.likes + 1}",
              "set_liked_log_id": "card1.set_liked",
              "set_liked_url": "div-action://set_state?state_id=0/card1.likes/liked",
              "remove_like_log_id": "card1.remove_like",
              "remove_like_url": "div-action://set_variable?name=card1.likes&value=@{card1.likes - 1}",
              "set_disliked_log_id": "card1.set_disliked",
              "set_disliked_url": "div-action://set_state?state_id=0/card1.likes/disliked"
            },
            {
              "type": "divgram_card",
              "likes_counter": "@{card2.likes}",
              "post_image": "https://yastatic.net/s3/home/divkit/pupduck.jpg",
              "author": "created by\n@me_and_you",
              "author_prefix_end": 11,
              "state_likes_id": "card2.likes",
              "add_like_log_id": "card2.add_like",
              "add_like_url": "div-action://set_variable?name=card2.likes&value=@{card2.likes + 1}",
              "set_liked_log_id": "card2.set_liked",
              "set_liked_url": "div-action://set_state?state_id=0/card2.likes/liked",
              "remove_like_log_id": "card2.remove_like",
              "remove_like_url": "div-action://set_variable?name=card2.likes&value=@{card2.likes - 1}",
              "set_disliked_log_id": "card2.set_disliked",
              "set_disliked_url": "div-action://set_state?state_id=0/card2.likes/disliked"
            },
            {
              "type": "container",
              "margins": {
                "left": 12,
                "right": 12,
                "top": 12,
                "bottom": 12
              },
              "height": {
                "type": "wrap_content"
              },
              "background": [
                {
                  "type": "solid",
                  "color": "@{is_dark_theme ? '#000' : '#FFF'}"
                }
              ],
              "border": {
                "corner_radius": 14,
                "stroke": {
                  "color": "#666"
                },
                "shadow": {
                  "offset": {
                    "x": {
                      "value": 1
                    },
                    "y": {
                      "value": 4
                    }
                  },
                  "color": "#000"
                },
                "has_shadow": true
              },
              "items": [
                {
                  "type": "container",
                  "orientation": "horizontal",
                  "margins": {
                    "left": 12,
                    "right": 12,
                    "top": 12,
                    "bottom": 12
                  },
                  "content_alignment_vertical": "center",
                  "items": [
                    {
                      "type": "text",
                      "text": "by @rive_app",
                      "text_color": "@{is_dark_theme ? '#FFF' : '#000'}",
                      "font_size": 18,
                      "ranges": [
                        {
                          "start": 0,
                          "end": 2,
                          "font_size": 14
                        }
                      ]
                    }
                  ]
                }
              ]
            }
          ]
        }
      }
    ]
  }
}
""".trimIndent()
