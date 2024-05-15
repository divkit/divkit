import 'package:divkit/src/core/template/templates_resolver.dart';
import 'package:flutter_test/flutter_test.dart';

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
      final card = TemplatesResolver.fromTemplates(
        layout: json['card']!,
      ).merge().toGenericMap();

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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

      // Assert
      expect(layout, result);
    });

    /// Layout property overrides template default
    test('TemplatesResolver props replacing', () {
      // Arrange
      const json = {
        "templates": {
          "title": {
            "type": "text",
            "font_size": 36,
            "paddings": {"left": 24, "right": 24},
          },
          "subtitle": {
            "type": "text",
            "font_size": 16,
            "margins": {"left": 24, "right": 24},
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
                  {
                    "type": "title",
                    "paddings": {"top": 24, "bottom": 24},
                    "text": "DivKit",
                  },
                  {
                    "type": "subtitle",
                    "font_size": 18,
                    "margins": {"left": 16, "right": 16},
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
                  "text":
                      "DivKit is a new Yandex open source framework that helps speed up mobile development.",
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
            "\$text": "buttonText",
            "buttonColor": "#FF4FA771",
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
                  "buttonColor": "#FF4FA771",
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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
            "\$text": "buttonText",
            "buttonColor": "#FF4FA771",
            "text_alignment_horizontal": "center",
          },
          "actionButton": {
            "type": "button",
            "\$text": "buttonText",
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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
            "background": [
              {"color": "#C0C", "type": "solid"},
            ],
            "border": {"corner_radius": 85},
          },
          "box": {
            "type": "container",
            "background": [
              {"color": "#1C1", "type": "solid"},
            ],
          },
          "shapeRow": {
            "type": "container",
            "orientation": "horizontal",
            "items": [
              {"type": "circle"},
              {"type": "box"},
              {"type": "circle"},
              {"type": "box"},
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
                  {"type": "shapeRow"},
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
                  "orientation": "horizontal",
                  "items": [
                    {
                      "type": "container",
                      "background": [
                        {"color": "#C0C", "type": "solid"},
                      ],
                      "border": {"corner_radius": 85},
                    },
                    {
                      "type": "container",
                      "background": [
                        {"color": "#1C1", "type": "solid"},
                      ],
                    },
                    {
                      "type": "container",
                      "background": [
                        {"color": "#C0C", "type": "solid"},
                      ],
                      "border": {"corner_radius": 85},
                    },
                    {
                      "type": "container",
                      "background": [
                        {"color": "#1C1", "type": "solid"},
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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
            "\$text": "buttonText",
            "buttonColor": "#FF4FA771",
            "font_size": 18,
            "text_alignment_horizontal": "center",
          },
          "actionButton": {
            "type": "button",
            "\$text": "buttonText",
            "buttonColor": "#FFFED42B",
          },
          "buttonFooter": {
            "type": "container",
            "orientation": "horizontal",
            "items": [
              {"type": "button", "\$text": "buttonText"},
              {"type": "actionButton", "\$text": "actionButtonText"},
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
                  {"type": "button", "buttonText": "First button"},
                  {"type": "actionButton", "buttonText": "Second button"},
                  {
                    "type": "buttonFooter",
                    "buttonText": "Secondary",
                    "actionButtonText": "Primary",
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
                  "type": "text",
                  "text": "First button",
                  "buttonColor": "#FF4FA771",
                  "font_size": 18,
                  "text_alignment_horizontal": "center",
                },
                {
                  "type": "text",
                  "text": "Second button",
                  "font_size": 18,
                  "text_alignment_horizontal": "center",
                  "buttonColor": "#FFFED42B",
                },
                {
                  "type": "container",
                  "orientation": "horizontal",
                  "items": [
                    {
                      "type": "text",
                      "text": "Secondary",
                      "buttonColor": "#FF4FA771",
                      "font_size": 18,
                      "text_alignment_horizontal": "center",
                    },
                    {
                      "type": "text",
                      "text": "Primary",
                      "font_size": 18,
                      "text_alignment_horizontal": "center",
                      "buttonColor": "#FFFED42B",
                    }
                  ],
                }
              ],
            },
          }
        ],
      };

      // Act
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

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
              {"type": "text", "text_color": "#3498DB", "\$text": "author"},
              {"type": "text", "text_color": "#1ABC9C", "\$text": "date"},
              {
                "type": "state",
                "states": [
                  {
                    "state_id": "initial",
                    "div": {"type": "comment_text", "\$text": "comment"},
                  },
                  {
                    "state_id": "colored",
                    "div": {
                      "type": "comment_text",
                      "\$text": "comment",
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
      final layout = TemplatesResolver.fromTemplates(
        layout: json['card']!,
        templates: json['templates'],
      ).merge().toGenericMap();

      // Assert
      expect(layout, result);
    });
  });
}
