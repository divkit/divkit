import DivKit
import XCTest

final class DivTemplatesTests: XCTestCase {
  func test_initWithDictionary_makesCorrectTemplateToType() {
    let templates = DivTemplates(dictionary: [
      "input_text": [
        "font_size": 18,
        "type": "input_text_borderless",
      ],
      "container_upper": [
        "orientation": "vertical",
        "type": "container_lower",
      ],
      "input_text_borderless": [
        "type": "input",
      ],
      "container_lower": [
        "orientation": "overlap",
        "type": "container",
      ],
    ])

    XCTAssertEqual(
      templates.templateToType,
      [
        "input_text_borderless": "input",
        "container_lower": "container",
        "input_text": "input",
        "container_upper": "container",
      ]
    )
  }

  func test_resolveAdditionalTemplates_templatesWithInheritance() {
    let baseTemplates = DivTemplates(dictionary: [
      "input_text_borderless": [
        "type": "input",
      ],
      "container_lower": [
        "orientation": "overlap",
        "type": "container",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(newTemplates: [
      "input_text": [
        "font_size": 18,
        "type": "input_text_borderless",
      ],
      "container_upper": [
        "orientation": "vertical",
        "type": "container_lower",
      ],
    ])

    XCTAssertTrue(fullTemplates.templates["input_text"] is DivInputTemplate)
    XCTAssertTrue(fullTemplates.templates["container_upper"] is DivContainerTemplate)
  }

  func test_makesCorrectTemplateToType_templatesWithInheritance() {
    let baseTemplates = DivTemplates(dictionary: [
      "input_text_borderless": [
        "type": "input",
      ],
      "container_lower": [
        "orientation": "overlap",
        "type": "container",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(newTemplates: [
      "input_text": [
        "font_size": 18,
        "type": "input_text_borderless",
      ],
      "container_upper": [
        "orientation": "vertical",
        "type": "container_lower",
      ],
    ])

    XCTAssertEqual(
      fullTemplates.templateToType,
      [
        "input_text_borderless": "input",
        "container_lower": "container",
        "input_text": "input",
        "container_upper": "container",
      ]
    )
  }

  func test_resolveAdditionalTemplates_templatesWithContainer() {
    let baseTemplates = DivTemplates(dictionary: [
      "title": [
        "type": "text",
      ],
      "input_text": [
        "type": "input",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(newTemplates: [
      "container_additional": [
        "items": [
          [
            "text": "Text inputs",
            "type": "title",
          ],
          [
            "type": "input_text",
          ],
        ],
        "orientation": "vertical",
        "type": "container",
      ],
    ])

    XCTAssertTrue(fullTemplates.templates["container_additional"] is DivContainerTemplate)
  }

  func test_makesCorrectTemplateToType_templatesWithContainer() {
    let baseTemplates = DivTemplates(dictionary: [
      "title": [
        "type": "text",
      ],
      "input_text": [
        "type": "input",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(newTemplates: [
      "container_additional": [
        "items": [
          [
            "text": "Text inputs",
            "type": "title",
          ],
          [
            "type": "input_text",
          ],
        ],
        "orientation": "vertical",
        "type": "container",
      ],
    ])

    XCTAssertEqual(
      fullTemplates.templateToType,
      [
        "title": "text",
        "input_text": "input",
        "container_additional": "container",
      ]
    )
  }

  func test_resolveAdditionalTemplatesWithConflict_keepExisting() {
    let baseTemplates = DivTemplates(dictionary: [
      "title": [
        "text": "old title",
        "type": "text",
      ],
      "input_text": [
        "type": "input",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(newTemplates: [
      "title": [
        "text": "new title",
        "type": "text",
      ],
      "container_additional": [
        "items": [
          [
            "type": "title",
          ],
          [
            "type": "input_text",
          ],
        ],
        "orientation": "vertical",
        "type": "container",
      ],
    ])

    if let textTemplate = fullTemplates.templates["title"] as? DivTextTemplate,
       case let .value(expression) = textTemplate.text,
       case let .value(val) = expression {
      XCTAssertEqual(val, "old title")
    } else {
      XCTFail("Can't get template `text` property")
    }
  }

  func test_resolveAdditionalTemplatesWithConflict_overrideExisting() {
    let baseTemplates = DivTemplates(dictionary: [
      "title": [
        "text": "old title",
        "type": "text",
      ],
      "input_text": [
        "type": "input",
      ],
    ])
    let fullTemplates = baseTemplates.resolve(
      newTemplates: [
        "title": [
          "text": "new title",
          "type": "text",
        ],
        "container_additional": [
          "items": [
            [
              "type": "title",
            ],
            [
              "type": "input_text",
            ],
          ],
          "orientation": "vertical",
          "type": "container",
        ],
      ],
      shouldKeepExistingOnConflict: false
    )

    if let textTemplate = fullTemplates.templates["title"] as? DivTextTemplate,
       case let .value(expression) = textTemplate.text,
       case let .value(val) = expression {
      XCTAssertEqual(val, "new title")
    } else {
      XCTFail("Can't get template `text` property")
    }
  }

  func test_untypedResolver_matchesTypedResolver() {
    let templates: [String: Any] = [
      "base_text": [
        "type": "text",
        "font_size": 14,
        "$text": "text_value",
      ],
      "list_item": [
        "type": "container",
        "orientation": "vertical",
        "items": [
          [
            "type": "base_text",
            "$text_value": "title",
          ],
        ],
      ],
    ]
    let card: [String: Any] = [
      "log_id": "test",
      "states": [
        [
          "state_id": 0,
          "div": [
            "type": "list_item",
            "title": "Resolved through chain",
          ],
        ],
      ],
    ]

    let typedResult = DivData.resolve(
      card: card,
      templates: templates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: false)
    )
    let untypedResult = DivData.resolve(
      card: card,
      templates: templates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )

    XCTAssertEqual(typedResult.value, untypedResult.value)
    XCTAssertEqual(
      typedResult.errorsOrWarnings?.count ?? 0,
      untypedResult.errorsOrWarnings?.count ?? 0
    )
  }
}
