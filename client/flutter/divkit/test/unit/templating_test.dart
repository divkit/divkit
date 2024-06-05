import 'package:flutter_test/flutter_test.dart';
import 'package:divkit/divkit.dart';

void main() {
  group('Flat layout cases', () {
    /// If there is no template — layout is used
    test('TemplatesResolver does not delete meta info', () {
      // Arrange
      const json = {
        "card": {
          "log_id": "sample",
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "margins": {"top": 24, "bottom": 24},
                "items": [
                  {"type": "title", "text": "DivKit"},
                ],
              },
            }
          ],
        },
      };

      // Act
      final card = TemplatesResolver(
        layout: json['card']!,
      ).merge();

      // Assert
      expect(card, json['card']);
    });

    /// Template add some properties to component
    test('TemplatesResolver resolve simple case', () {
      // Arrange
      const json = {
        "templates": {
          "title": {
            "type": "text",
            "font_size": 36,
          },
        },
        "card": {
          "log_id": "sample",
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "title", "text": "DivKit"},
                ],
              },
            }
          ],
        },
      };

      const result = {
        "log_id": "sample",
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {"type": "text", "font_size": 36, "text": "DivKit"},
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Template is correctly matching with it's type
    test('TemplatesResolver resolve two elements', () {
      // Arrange
      const json = {
        "templates": {
          "title": {
            "type": "text",
            "font_size": 36,
          },
          "subtitle": {
            "type": "text",
            "font_size": 16,
          },
        },
        "card": {
          "log_id": "sample",
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "title", "text": "DivKit"},
                  {
                    "type": "subtitle",
                    "text":
                        "DivKit is a new Yandex open source framework that helps speed up mobile development.",
                  }
                ],
              },
            }
          ],
        },
      };

      const result = {
        "log_id": "sample",
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {"type": "text", "font_size": 36, "text": "DivKit"},
                {
                  "type": "text",
                  "font_size": 16,
                  "text":
                      "DivKit is a new Yandex open source framework that helps speed up mobile development.",
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Layout property overrides template default
    test('TemplatesResolver props replacing', () {
      // Arrange
      const json = {
        "card": {
          "log_id": "sample",
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {
                    "type": "text",
                    "font_size": 36,
                    "paddings": {"top": 24, "bottom": 24},
                    "text": "DivKit"
                  },
                  {
                    "type": "text",
                    "font_size": 18,
                    "margins": {"left": 16, "right": 16},
                    "text": "DivKit is a new Yandex open source framework"
                  }
                ]
              }
            }
          ]
        }
      };

      const result = {
        "log_id": "sample",
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "text",
                  "font_size": 36,
                  "paddings": {"top": 24, "bottom": 24},
                  "text": "DivKit",
                },
                {
                  "type": "text",
                  "font_size": 18,
                  "margins": {"left": 16, "right": 16},
                  "text": "DivKit is a new Yandex open source framework",
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Named parameter of template is passed to target property
    test('TemplatesResolver merges named property', () {
      // Arrange
      const json = {
        "templates": {
          "button": {
            "type": "text",
            r"$text": "buttonText",
            "text_alignment_horizontal": "center",
            "background": [
              {"color": "#FF4FA771", "type": "solid"},
            ],
          },
        },
        "card": {
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "button", "buttonText": "Text on the button"},
                ],
              },
            }
          ],
        },
      };

      const result = {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "text",
                  "text": "Text on the button",
                  "text_alignment_horizontal": "center",
                  "background": [
                    {"color": "#FF4FA771", "type": "solid"},
                  ],
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });
  });

  group('Nested layout cases', () {
    /// Template is used on any level of layout
    test('TemplatesResolver resolve simple', () {
      // Arrange
      const json = {
        "templates": {
          "title": {
            "type": "text",
            "font_size": 36,
          },
        },
        "card": {
          "log_id": "sample",
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "title", "text": "DivKit"},
                  {
                    "type": "container",
                    "orientation": "vertical",
                    "items": [
                      {"type": "title", "text": "DivKit v2"},
                      {
                        "type": "container",
                        "orientation": "vertical",
                        "items": [
                          {"type": "title", "text": "DivKit v3"},
                        ],
                      }
                    ],
                  }
                ],
              },
            }
          ],
        },
      };

      const result = {
        "log_id": "sample",
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {"type": "text", "font_size": 36, "text": "DivKit"},
                {
                  "type": "container",
                  "orientation": "vertical",
                  "items": [
                    {"type": "text", "font_size": 36, "text": "DivKit v2"},
                    {
                      "type": "container",
                      "orientation": "vertical",
                      "items": [
                        {"type": "text", "font_size": 36, "text": "DivKit v3"},
                      ],
                    }
                  ],
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Template can inherit another template and override its properties
    test('TemplatesResolver overrides template by another template', () {
      // Arrange
      const json = {
        "templates": {
          "button": {
            "type": "text",
            r"$text": "buttonText",
            "buttonColor": "#FF4FA771",
            "text_alignment_horizontal": "center",
          },
          "actionButton": {
            "type": "button",
            r"$text": "buttonText",
            "buttonColor": "#FFFED42B",
          },
        },
        "card": {
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "button", "buttonText": "First button"},
                  {"type": "actionButton", "buttonText": "Second button"},
                ],
              },
            }
          ],
        },
      };

      final result = {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "text",
                  "text": "First button",
                  "text_alignment_horizontal": "center",
                  "buttonColor": "#FF4FA771",
                },
                {
                  "type": "text",
                  "text": "Second button",
                  "text_alignment_horizontal": "center",
                  "buttonColor": "#FFFED42B",
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Template can combine multiple templates (for example, as items of a container)
    test('TemplatesResolver uses template as item of another template', () {
      // Arrange
      const json = {
        "templates": {
          "circle": {
            "type": "container",
            "width": {"type": "fixed", "value": 64},
            "height": {"type": "fixed", "value": 64},
            "background": [
              {"color": "#C0C", "type": "solid"}
            ],
            "border": {"corner_radius": 85}
          },
          "box": {
            "type": "container",
            "width": {"type": "fixed", "value": 64},
            "height": {"type": "fixed", "value": 64},
            "background": [
              {"color": "#1C1", "type": "solid"}
            ]
          },
          "shapeRow": {
            "type": "container",
            "orientation": "horizontal",
            "items": [
              {"type": "circle"},
              {"type": "box"},
              {"type": "circle"},
              {"type": "box"}
            ]
          }
        },
        "card": {
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "shapeRow"}
                ]
              }
            }
          ]
        }
      };

      final result = {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "container",
                  "orientation": "horizontal",
                  "items": [
                    {
                      "type": "container",
                      "width": {"type": "fixed", "value": 64},
                      "height": {"type": "fixed", "value": 64},
                      "background": [
                        {"color": "#C0C", "type": "solid"}
                      ],
                      "border": {"corner_radius": 85}
                    },
                    {
                      "type": "container",
                      "width": {"type": "fixed", "value": 64},
                      "height": {"type": "fixed", "value": 64},
                      "background": [
                        {"color": "#1C1", "type": "solid"}
                      ]
                    },
                    {
                      "type": "container",
                      "width": {"type": "fixed", "value": 64},
                      "height": {"type": "fixed", "value": 64},
                      "background": [
                        {"color": "#C0C", "type": "solid"}
                      ],
                      "border": {"corner_radius": 85}
                    },
                    {
                      "type": "container",
                      "width": {"type": "fixed", "value": 64},
                      "height": {"type": "fixed", "value": 64},
                      "background": [
                        {"color": "#1C1", "type": "solid"}
                      ]
                    }
                  ]
                }
              ]
            }
          }
        ]
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Template can use named parameter deeper inside itself (for example, in container items)
    test('TemplatesResolver merges multiple named properties to items', () {
      // Arrange
      const json = {
        "templates": {
          "button": {
            "type": "text",
            r"$text": "buttonText",
            "font_size": 18,
            "text_alignment_horizontal": "center"
          },
          "actionButton": {"type": "button", r"$text": "buttonText"},
          "buttonFooter": {
            "type": "container",
            "orientation": "horizontal",
            "items": [
              {"type": "button", r"$text": "buttonText"},
              {"type": "actionButton", r"$text": "actionButtonText"}
            ]
          }
        },
        "card": {
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {"type": "button", "buttonText": "First button"},
                  {"type": "actionButton", "buttonText": "Second button"},
                  {
                    "type": "buttonFooter",
                    "buttonText": "Secondary",
                    "actionButtonText": "Primary"
                  }
                ]
              }
            }
          ]
        }
      };

      final result = {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "text",
                  "text": "First button",
                  "font_size": 18,
                  "text_alignment_horizontal": "center",
                },
                {
                  "type": "text",
                  "text": "Second button",
                  "font_size": 18,
                  "text_alignment_horizontal": "center",
                },
                {
                  "type": "container",
                  "orientation": "horizontal",
                  "items": [
                    {
                      "type": "text",
                      "text": "Secondary",
                      "font_size": 18,
                      "text_alignment_horizontal": "center",
                    },
                    {
                      "type": "text",
                      "text": "Primary",
                      "font_size": 18,
                      "text_alignment_horizontal": "center",
                    }
                  ],
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });

    /// Template can consist of multiple blocks with same params and different name parameters
    /// sometimes one named param is used twice, for example in "state"
    test('TemplatesResolver can merge named parameters for same param', () {
      // Arrange
      const json = {
        "templates": {
          "comment_text": {"type": "text"},
          "comment": {
            "type": "grid",
            "column_count": 3,
            "items": [
              {"type": "text", "text_color": "#3498DB", r"$text": "author"},
              {"type": "text", "text_color": "#1ABC9C", r"$text": "date"},
              {
                "type": "state",
                "states": [
                  {
                    "state_id": "initial",
                    "div": {"type": "comment_text", r"$text": "comment"},
                  },
                  {
                    "state_id": "colored",
                    "div": {
                      "type": "comment_text",
                      r"$text": "comment",
                      "text_color": "#5e5e5e",
                    },
                  }
                ],
              }
            ],
          },
        },
        "card": {
          "states": [
            {
              "state_id": 0,
              "div": {
                "type": "container",
                "orientation": "vertical",
                "items": [
                  {
                    "type": "comment",
                    "author": "Author #1",
                    "date": "today",
                    "comment": "comment #1",
                  },
                  {
                    "type": "comment",
                    "author": "Author #2",
                    "date": "yesterday",
                    "comment": "comment #2",
                  }
                ],
              },
            }
          ],
        },
      };

      final result = {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "grid",
                  "column_count": 3,
                  "items": [
                    {
                      "type": "text",
                      "text_color": "#3498DB",
                      "text": "Author #1",
                    },
                    {"type": "text", "text_color": "#1ABC9C", "text": "today"},
                    {
                      "type": "state",
                      "states": [
                        {
                          "state_id": "initial",
                          "div": {"type": "text", "text": "comment #1"},
                        },
                        {
                          "state_id": "colored",
                          "div": {
                            "type": "text",
                            "text": "comment #1",
                            "text_color": "#5e5e5e",
                          },
                        }
                      ],
                    }
                  ],
                },
                {
                  "type": "grid",
                  "column_count": 3,
                  "items": [
                    {
                      "type": "text",
                      "text_color": "#3498DB",
                      "text": "Author #2",
                    },
                    {
                      "type": "text",
                      "text_color": "#1ABC9C",
                      "text": "yesterday",
                    },
                    {
                      "type": "state",
                      "states": [
                        {
                          "state_id": "initial",
                          "div": {"type": "text", "text": "comment #2"},
                        },
                        {
                          "state_id": "colored",
                          "div": {
                            "type": "text",
                            "text": "comment #2",
                            "text_color": "#5e5e5e",
                          },
                        }
                      ],
                    }
                  ],
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver(
        layout: json['card']!,
        templates: json['templates'],
      ).merge();

      // Assert
      expect(layout, result);
    });
  });

  test('Renamed parameter throw templates', () {
    // Arrange
    const json = {
      "templates": {
        "some_text": {
          "type": "text",
          "text_color": "#ff0000",
        },
        "header": {
          "type": "some_text",
          r"$text": "header_text",
        },
        "some_card": {
          "type": "container",
          "orientation": "vertical",
          "items": [
            {
              "type": "header",
              "font_size": 20,
              "font_weight": "medium",
            },
            {
              "type": "some_text",
              "font_size": 14,
              "font_weight": "regular",
              r"$text": "title_text"
            }
          ]
        },
      },
      "card": {
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "some_card",
                  "header_text": "Header",
                  "title_text": "Title",
                }
              ],
            },
          }
        ],
      },
    };

    final result = {
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "container",
            "orientation": "vertical",
            "items": [
              {
                "type": "container",
                "header_text": "Header",
                "orientation": "vertical",
                "items": [
                  {
                    "type": "text",
                    "font_size": 20,
                    "font_weight": "medium",
                    "text_color": "#ff0000"
                  },
                  {
                    "type": "text",
                    "font_size": 14,
                    "font_weight": "regular",
                    "text": "Title",
                    "text_color": "#ff0000"
                  }
                ]
              }
            ]
          }
        }
      ]
    };

    // Act
    final layout = TemplatesResolver(
      layout: json['card']!,
      templates: json['templates'],
    ).merge();

    // Assert
    expect(layout, result);
  });

  test('Little Json with templates (1)', () {
    final json = {
      "templates": {
        "title": {
          "type": "text",
          "font_size": 20,
          "line_height": 24,
          "font_weight": "bold",
          "paddings": {"left": 24, "right": 24, "bottom": 16}
        },
        "subtitle": {
          "font_size": 15,
          "line_height": 20,
          "type": "text",
          "paddings": {"left": 24, "right": 24}
        },
        "image_block": {
          "type": "image",
          "image_url":
              "https://yastatic.net/s3/home/yandex-app/div_demo/containers.png",
          "width": {"type": "fixed", "value": 150},
          "height": {"type": "fixed", "value": 150},
          "margins": {"left": 16, "right": 16, "bottom": 16}
        },
        "button": {
          "type": "text",
          "width": {"type": "match_parent"},
          "height": {"type": "wrap_content"},
          "paddings": {"left": 16, "top": 16, "right": 16, "bottom": 16},
          "margins": {"left": 24, "right": 24},
          "border": {"corner_radius": 8},
          "background": [
            {"type": "solid", "color": "#0E000000"}
          ],
          "font_size": 14,
          "font_weight": "medium",
          "text_alignment_vertical": "center",
          "text_alignment_horizontal": "center",
          "text_color": "#000000"
        }
      },
      "card": {
        "log_id": "sample_card",
        "variables": [
          {"name": "change_state", "type": "boolean", "value": false},
          {"name": "state", "type": "boolean", "value": false}
        ],
        "variable_triggers": [
          {
            "condition": "@{change_state && state}",
            "mode": "on_variable",
            "actions": [
              {
                "log_id": "update change_state flag",
                "url": "div-action://set_variable?name=change_state&value=false"
              },
              {
                "log_id": "update change_state flag",
                "url": "div-action://set_variable?name=state&value=false"
              },
              {
                "log_id": "change state",
                "url":
                    "div-action://set_state?state_id=0/transition_change_demo_state/state1"
              }
            ]
          },
          {
            "condition": "@{change_state && !state}",
            "mode": "on_variable",
            "actions": [
              {
                "log_id": "update change_state flag",
                "url": "div-action://set_variable?name=change_state&value=false"
              },
              {
                "log_id": "update state variable",
                "url": "div-action://set_variable?name=state&value=true"
              },
              {
                "log_id": "change state",
                "url":
                    "div-action://set_state?state_id=0/transition_change_demo_state/state2"
              }
            ]
          }
        ],
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "margins": {"top": 24, "bottom": 24},
              "items": [
                {"type": "title", "text": "Move and resize animations"},
                {
                  "type": "subtitle",
                  "text":
                      "For each div, you can customize the transition animation to be played when the div changes its size or position.\n\nIn the example, the picture is animated to increase in width and moves to the upper-right corner when switching to state 2.",
                  "margins": {"bottom": 24}
                },
                {
                  "type": "state",
                  "width": {"type": "match_parent"},
                  "height": {"type": "fixed", "value": 250},
                  "id": "transition_change_demo_state",
                  "states": [
                    {
                      "state_id": "state1",
                      "div": {
                        "type": "image_block",
                        "id": "image",
                        "alignment_horizontal": "center",
                        "alignment_vertical": "top",
                        "width": {"type": "match_parent"},
                        "transition_change": {
                          "type": "change_bounds",
                          "duration": 1000
                        }
                      }
                    },
                    {
                      "state_id": "state2",
                      "div": {
                        "type": "image_block",
                        "id": "image",
                        "alignment_horizontal": "right",
                        "alignment_vertical": "bottom",
                        "transition_change": {
                          "type": "change_bounds",
                          "duration": 1000
                        }
                      }
                    }
                  ]
                },
                {
                  "type": "button",
                  "alignment_horizontal": "center",
                  "text": "Test Button",
                  "actions": [
                    {
                      "log_id": "set_state1",
                      "url":
                          "div-action://set_variable?name=change_state&value=true"
                    }
                  ]
                }
              ]
            }
          }
        ]
      }
    };

    final result = {
      "log_id": "sample_card",
      "variables": [
        {"name": "change_state", "type": "boolean", "value": false},
        {"name": "state", "type": "boolean", "value": false}
      ],
      "variable_triggers": [
        {
          "condition": "@{change_state && state}",
          "mode": "on_variable",
          "actions": [
            {
              "log_id": "update change_state flag",
              "url": "div-action://set_variable?name=change_state&value=false"
            },
            {
              "log_id": "update change_state flag",
              "url": "div-action://set_variable?name=state&value=false"
            },
            {
              "log_id": "change state",
              "url":
                  "div-action://set_state?state_id=0/transition_change_demo_state/state1"
            }
          ]
        },
        {
          "condition": "@{change_state && !state}",
          "mode": "on_variable",
          "actions": [
            {
              "log_id": "update change_state flag",
              "url": "div-action://set_variable?name=change_state&value=false"
            },
            {
              "log_id": "update state variable",
              "url": "div-action://set_variable?name=state&value=true"
            },
            {
              "log_id": "change state",
              "url":
                  "div-action://set_state?state_id=0/transition_change_demo_state/state2"
            }
          ]
        }
      ],
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "container",
            "orientation": "vertical",
            "margins": {"top": 24, "bottom": 24},
            "items": [
              {
                "type": "text",
                "text": "Move and resize animations",
                "font_size": 20,
                "line_height": 24,
                "font_weight": "bold",
                "paddings": {"left": 24, "right": 24, "bottom": 16}
              },
              {
                "type": "text",
                "text":
                    "For each div, you can customize the transition animation to be played when the div changes its size or position.\n\nIn the example, the picture is animated to increase in width and moves to the upper-right corner when switching to state 2.",
                "margins": {"bottom": 24},
                "font_size": 15,
                "line_height": 20,
                "paddings": {"left": 24, "right": 24}
              },
              {
                "type": "state",
                "width": {"type": "match_parent"},
                "height": {"type": "fixed", "value": 250},
                "id": "transition_change_demo_state",
                "states": [
                  {
                    "state_id": "state1",
                    "div": {
                      "type": "image",
                      "id": "image",
                      "alignment_horizontal": "center",
                      "alignment_vertical": "top",
                      "width": {"type": "match_parent"},
                      "transition_change": {
                        "type": "change_bounds",
                        "duration": 1000
                      },
                      "image_url":
                          "https://yastatic.net/s3/home/yandex-app/div_demo/containers.png",
                      "height": {"type": "fixed", "value": 150},
                      "margins": {"left": 16, "right": 16, "bottom": 16}
                    }
                  },
                  {
                    "state_id": "state2",
                    "div": {
                      "type": "image",
                      "id": "image",
                      "alignment_horizontal": "right",
                      "alignment_vertical": "bottom",
                      "transition_change": {
                        "type": "change_bounds",
                        "duration": 1000
                      },
                      "image_url":
                          "https://yastatic.net/s3/home/yandex-app/div_demo/containers.png",
                      "width": {"type": "fixed", "value": 150},
                      "height": {"type": "fixed", "value": 150},
                      "margins": {"left": 16, "right": 16, "bottom": 16}
                    }
                  }
                ]
              },
              {
                "type": "text",
                "alignment_horizontal": "center",
                "text": "Test Button",
                "actions": [
                  {
                    "log_id": "set_state1",
                    "url":
                        "div-action://set_variable?name=change_state&value=true"
                  }
                ],
                "width": {"type": "match_parent"},
                "height": {"type": "wrap_content"},
                "paddings": {"left": 16, "top": 16, "right": 16, "bottom": 16},
                "margins": {"left": 24, "right": 24},
                "border": {"corner_radius": 8},
                "background": [
                  {"type": "solid", "color": "#0E000000"}
                ],
                "font_size": 14,
                "font_weight": "medium",
                "text_alignment_vertical": "center",
                "text_alignment_horizontal": "center",
                "text_color": "#000000"
              }
            ]
          }
        }
      ]
    };

    // Act
    final layout = TemplatesResolver(
      layout: json['card']!,
      templates: json['templates'],
    ).merge();

    // Assert
    expect(layout, result);
  });

  test('Little Json with templates (2)', () {
    final json = {
      "card": {
        "log_id": "my-layout-id",
        "states": [
          {
            "state_id": 0,
            "div": {
              "type": "container",
              "orientation": "vertical",
              "items": [
                {
                  "type": "card",
                  "cardItems": [
                    {"type": "dayItem", "day": "28  мая"},
                    {"type": "lineDivider"},
                    {
                      "type": "orderItem",
                      "loyaltyChange": "+30",
                      "time": "17:40",
                      "address": "Льва Толстого, 16",
                      "class": "Эконом"
                    }
                  ]
                }
              ]
            }
          }
        ]
      },
      "templates": {
        "dayItem": {
          "type": "container",
          "orientation": "horizontal",
          "content_alignment_vertical": "center",
          "height": {
            "type": "wrap_content",
            "min_size": {"value": 56}
          },
          "items": [
            {"type": "body1", r"$mytext": "day"}
          ],
          "paddings": {"bottom": 12, "end": 16, "start": 16, "top": 12}
        },
        "body1": {
          "type": "text",
          r"$text": "mytext",
          "font_size": 20,
          "font_weight": "medium",
          "line_height": 20
        },
        "lineDivider": {
          "type": "container",
          "background": [
            {"type": "solid", "color": "#668A8784"}
          ],
          "height": {"type": "fixed", "value": 1},
          "margins": {"end": 16, "start": 16}
        },
        "orderItem": {
          "type": "container",
          "orientation": "overlap",
          "items": [
            {
              "type": "container",
              "action": {"url": ""},
              "action_animation": {
                "end_value": 0.1,
                "name": "fade",
                "start_value": 0.0
              },
              "background": [
                {"type": "solid", "color": "#FF000000"}
              ],
              "height": {"type": "match_parent"},
              "width": {"type": "match_parent"}
            },
            {
              "type": "container",
              "orientation": "horizontal",
              "items": [
                {
                  "type": "container",
                  "width": {"type": "fixed", "value": 16}
                },
                {
                  "type": "container",
                  "orientation": "vertical",
                  "items": [
                    {"type": "body2", r"$mytext": "time"},
                    {"type": "caption1", r"$mytext": "address"}
                  ],
                  "margins": {"bottom": 12, "top": 12}
                },
                {
                  "type": "container",
                  "orientation": "vertical",
                  "items": [
                    {
                      "width": {"type": "wrap_content"},
                      r"$mytext": "loyaltyChange",
                      "alignment_horizontal": "end",
                      "type": "body2"
                    },
                    {
                      "width": {"type": "wrap_content"},
                      r"$mytext": "class",
                      "alignment_horizontal": "end",
                      "type": "caption1"
                    }
                  ],
                  "margins": {"bottom": 12, "top": 12},
                  "width": {"type": "wrap_content"}
                },
                {"type": "navTrail"}
              ]
            }
          ]
        },
        "body2": {
          "type": "text",
          r"$text": "mytext",
          "font_size": 16,
          "font_weight": "regular",
          "line_height": 17
        },
        "caption1": {
          "type": "text",
          r"$text": "mytext",
          "font_size": 13,
          "font_weight": "regular",
          "line_height": 14
        },
        "navTrail": {
          "type": "image",
          "image_url": "ChevronRight.svg",
          "height": {"type": "fixed", "value": 24},
          "margins": {"bottom": 16, "end": 8, "top": 16},
          "width": {"type": "fixed", "value": 24}
        },
        "card": {"type": "container", r"$items": "cardItems"}
      }
    };

    final result = {
      "log_id": "my-layout-id",
      "states": [
        {
          "state_id": 0,
          "div": {
            "type": "container",
            "orientation": "vertical",
            "items": [
              {
                "type": "container",
                "items": [
                  {
                    "type": "container",
                    "orientation": "horizontal",
                    "content_alignment_vertical": "center",
                    "height": {
                      "type": "wrap_content",
                      "min_size": {"value": 56}
                    },
                    "items": [
                      {
                        "type": "text",
                        "text": "28  мая",
                        "font_size": 20,
                        "font_weight": "medium",
                        "line_height": 20
                      }
                    ],
                    "paddings": {
                      "bottom": 12,
                      "end": 16,
                      "start": 16,
                      "top": 12
                    }
                  },
                  {
                    "type": "container",
                    "background": [
                      {"type": "solid", "color": "#668A8784"}
                    ],
                    "height": {"type": "fixed", "value": 1},
                    "margins": {"end": 16, "start": 16}
                  },
                  {
                    "type": "container",
                    "orientation": "overlap",
                    "items": [
                      {
                        "type": "container",
                        "action": {"url": ""},
                        "action_animation": {
                          "end_value": 0.1,
                          "name": "fade",
                          "start_value": 0.0
                        },
                        "background": [
                          {"type": "solid", "color": "#FF000000"}
                        ],
                        "height": {"type": "match_parent"},
                        "width": {"type": "match_parent"}
                      },
                      {
                        "type": "container",
                        "orientation": "horizontal",
                        "items": [
                          {
                            "type": "container",
                            "width": {"type": "fixed", "value": 16}
                          },
                          {
                            "type": "container",
                            "orientation": "vertical",
                            "items": [
                              {
                                "type": "text",
                                "text": "17:40",
                                "font_size": 16,
                                "font_weight": "regular",
                                "line_height": 17
                              },
                              {
                                "type": "text",
                                "text": "Льва Толстого, 16",
                                "font_size": 13,
                                "font_weight": "regular",
                                "line_height": 14
                              }
                            ],
                            "margins": {"bottom": 12, "top": 12}
                          },
                          {
                            "type": "container",
                            "orientation": "vertical",
                            "items": [
                              {
                                "width": {"type": "wrap_content"},
                                "alignment_horizontal": "end",
                                "type": "text",
                                "text": "+30",
                                "font_size": 16,
                                "font_weight": "regular",
                                "line_height": 17
                              },
                              {
                                "width": {"type": "wrap_content"},
                                "alignment_horizontal": "end",
                                "type": "text",
                                "text": "Эконом",
                                "font_size": 13,
                                "font_weight": "regular",
                                "line_height": 14
                              }
                            ],
                            "margins": {"bottom": 12, "top": 12},
                            "width": {"type": "wrap_content"}
                          },
                          {
                            "type": "image",
                            "image_url": "ChevronRight.svg",
                            "height": {"type": "fixed", "value": 24},
                            "margins": {"bottom": 16, "end": 8, "top": 16},
                            "width": {"type": "fixed", "value": 24}
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
    };

    // Act
    final layout = TemplatesResolver(
      layout: json['card']!,
      templates: json['templates'],
    ).merge();

    // Assert
    expect(layout, result);
  });
}
