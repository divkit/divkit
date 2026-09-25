import DivKit
import DivKitTestsSupport
import Foundation
import Serialization
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

    let inputText = fullTemplates.templates["input_text"] as? [String: Any]
    XCTAssertEqual(inputText?["type"] as? String, "input")
    XCTAssertEqual(inputText?["font_size"] as? Int, 18)
    let containerUpper = fullTemplates.templates["container_upper"] as? [String: Any]
    XCTAssertEqual(containerUpper?["type"] as? String, "container")
    XCTAssertEqual(containerUpper?["orientation"] as? String, "vertical")
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

    let container = fullTemplates.templates["container_additional"] as? [String: Any]
    XCTAssertEqual(container?["type"] as? String, "container")
    XCTAssertEqual(container?["orientation"] as? String, "vertical")
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

    XCTAssertEqual(
      (fullTemplates.templates["title"] as? [String: Any])?["text"] as? String,
      "old title"
    )
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

    XCTAssertEqual(
      (fullTemplates.templates["title"] as? [String: Any])?["text"] as? String,
      "new title"
    )
  }

  func test_resolve_linkChainThroughNestedTemplate_dropsItem() {
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
    let card = makeCard(div: [
      "type": "list_item",
      "title": "Resolved through chain",
    ])
    let expectedValue = makeExpectedValue(div: [
      "type": "container",
      "orientation": "vertical",
      "items": [],
    ])

    let result = DivData.resolve(card: card, templates: templates)

    XCTAssertNotNil(expectedValue)
    assertEqual(result.value, expectedValue)
    XCTAssertEqual(result.errorsOrWarnings?.count, 1)
  }

  func test_resolveNewTemplates_templateInheritsFromEarlierBatch() {
    let firstBatch: [String: Any] = [
      "styled_text": [
        "type": "text",
        "font_size": 14,
      ],
    ]
    let secondBatch: [String: Any] = [
      "headline": [
        "type": "styled_text",
        "font_weight": "bold",
      ],
    ]
    let card = makeCard(div: [
      "type": "headline",
      "text": "Inherited across batches",
    ])

    let mergedTemplates = DivTemplates(dictionary: firstBatch).resolve(newTemplates: secondBatch)
    let mergedResult = DivData.resolve(
      card: card,
      templates: mergedTemplates
    )

    let unionDictionary = firstBatch.merging(secondBatch, uniquingKeysWith: { $1 })
    let legacyResult = DivData.resolve(
      card: card,
      templates: unionDictionary
    )

    XCTAssertNotNil(mergedResult.value)
    assertEqual(mergedResult.value, legacyResult.value)
    XCTAssertEqual(
      mergedResult.errorsOrWarnings?.count ?? 0,
      legacyResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_resolveNewTemplates_conflictHonorsShouldKeepExistingOnConflict() {
    let baseTemplates = DivTemplates(dictionary: [
      "styled_text": [
        "type": "text",
        "text": "old text",
      ],
      "headline": [
        "type": "styled_text",
        "font_size": 14,
      ],
    ])
    let card = makeCard(div: ["type": "headline"])
    let redefinition: [String: Any] = [
      "styled_text": [
        "type": "text",
        "text": "new text",
      ],
    ]

    let oldExpectedValue = makeExpectedValue(div: [
      "type": "text",
      "text": "old text",
      "font_size": 14,
    ])

    let baseResult = DivData.resolve(
      card: card,
      templates: baseTemplates
    )
    XCTAssertNotNil(baseResult.value)
    assertEqual(baseResult.value, oldExpectedValue)

    let keptTemplates = baseTemplates.resolve(newTemplates: redefinition)
    let keptResult = DivData.resolve(
      card: card,
      templates: keptTemplates
    )
    XCTAssertNotNil(keptResult.value)
    assertEqual(keptResult.value, oldExpectedValue)
    XCTAssertEqual(keptResult.errorsOrWarnings?.count ?? 0, 0)

    let overriddenTemplates = baseTemplates.resolve(
      newTemplates: redefinition,
      shouldKeepExistingOnConflict: false
    )

    let overriddenResult = DivData.resolve(
      card: card,
      templates: overriddenTemplates
    )
    XCTAssertNotNil(overriddenResult.value)
    assertEqual(overriddenResult.value, oldExpectedValue)
    XCTAssertEqual(overriddenResult.errorsOrWarnings?.count ?? 0, 0)

    let directCard = makeCard(div: ["type": "styled_text"])
    let directResult = DivData.resolve(
      card: directCard,
      templates: overriddenTemplates
    )
    XCTAssertNotNil(directResult.value)
    assertEqual(directResult.value, makeExpectedValue(div: [
      "type": "text",
      "text": "new text",
    ]))
    XCTAssertEqual(directResult.errorsOrWarnings?.count ?? 0, 0)

    let baseResultAfterMerging = DivData.resolve(
      card: card,
      templates: baseTemplates
    )
    XCTAssertNotNil(baseResultAfterMerging.value)
    assertEqual(baseResultAfterMerging.value, oldExpectedValue)
  }

  func test_initWithDictionary_dropsCyclicTemplates() {
    let cyclicTemplates: [String: Any] = [
      "ouroboros": ["type": "amphisbaena"],
      "amphisbaena": ["type": "ouroboros"],
    ]
    let templates = DivTemplates(dictionary: cyclicTemplates)

    XCTAssertFalse(templates.templates.keys.contains("ouroboros"))
    XCTAssertFalse(templates.templates.keys.contains("amphisbaena"))

    let card = makeCard(div: ["type": "ouroboros"])
    let result = DivData.resolve(
      card: card,
      templates: templates
    )
    let legacyResult = DivData.resolve(
      card: card,
      templates: cyclicTemplates
    )

    assertEqual(result.value, legacyResult.value)
    XCTAssertEqual(
      result.errorsOrWarnings?.count ?? 0,
      legacyResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_reuse_secondCardMatchesFreshInstance() {
    let sharedTemplates = DivTemplates(dictionary: templatesDictionary)
    let firstCard = makeCard(div: [
      "type": "headline",
      "text": "First card",
    ])
    let secondCard = makeCard(div: [
      "type": "styled_text",
      "text": "Second card",
    ])

    _ = DivData.resolve(
      card: firstCard,
      templates: sharedTemplates
    )
    let reusedResult = DivData.resolve(
      card: secondCard,
      templates: sharedTemplates
    )
    let freshResult = DivData.resolve(
      card: secondCard,
      templates: DivTemplates(dictionary: templatesDictionary)
    )

    XCTAssertNotNil(reusedResult.value)
    assertEqual(reusedResult.value, freshResult.value)
    XCTAssertEqual(
      reusedResult.errorsOrWarnings?.count ?? 0,
      freshResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_concurrentResolve_isConsistent() {
    let sharedTemplates = DivTemplates(dictionary: templatesDictionary)
    let card = makeCard(div: [
      "type": "headline",
      "text": "Resolved concurrently",
    ])

    let expectedResult = DivData.resolve(
      card: card,
      templates: DivTemplates(dictionary: templatesDictionary)
    )
    XCTAssertNotNil(expectedResult.value)
    XCTAssertNil(expectedResult.errorsOrWarnings)

    let iterations = 100
    let lock = NSLock()
    var results: [DeserializationResult<DivData>] = []
    DispatchQueue.concurrentPerform(iterations: iterations) { _ in
      let result = DivData.resolve(
        card: card,
        templates: sharedTemplates
      )
      lock.lock()
      results.append(result)
      lock.unlock()
    }

    XCTAssertEqual(results.count, iterations)
    for result in results {
      assertEqual(result.value, expectedResult.value)
      XCTAssertNil(result.errorsOrWarnings)
    }
  }

  func test_parseValue_nonCardValueUsesTemplates() {
    let result = DivTemplates(dictionary: templatesDictionary)
      .parseValue(type: DivText.self, from: ["type": "headline", "text": "x"])

    let text = result.value
    XCTAssertNotNil(text)
    XCTAssertEqual(text?.fontSize, .value(14))
    XCTAssertEqual(text?.fontWeight, .value(.bold))
    XCTAssertEqual(text?.textAlignmentHorizontal, .value(.center))
  }

  func test_parseValue_contextDeserializable_reportsContextWarnings() {
    let result = DivTemplates.empty.parseValue(
      type: DivText.self,
      from: ["type": "text", "text": "x", "font_size": "bad"]
    )

    guard case let .partialSuccess(_, warnings) = result else {
      return XCTFail("Expected partial success, got \(result)")
    }
    XCTAssertEqual(warnings.count, 1)
  }

  @available(*, deprecated)
  func test_parseValue_deprecatedTemplateValueShim_matchesModelType() {
    let action: [String: Any] = [
      "log_id": "action",
      "url": "div-action://set_variable?name=var&value=1",
    ]
    XCTAssertNotNil(DivTemplates.empty.parseValue(type: DivAction.self, from: action).value)
    XCTAssertEqual(
      DivTemplates.empty.parseValue(type: DivActionTemplate.self, from: action).value,
      DivTemplates.empty.parseValue(type: DivAction.self, from: action).value
    )

    let variable: [String: Any] = ["type": "integer", "name": "var", "value": 1]
    XCTAssertNotNil(DivTemplates.empty.parseValue(type: DivVariable.self, from: variable).value)
    XCTAssertEqual(
      DivTemplates.empty.parseValue(type: DivVariableTemplate.self, from: variable).value,
      DivTemplates.empty.parseValue(type: DivVariable.self, from: variable).value
    )

    let text: [String: Any] = ["type": "text", "text": "x"]
    XCTAssertNotNil(DivTemplates.empty.parseValue(type: DivText.self, from: text).value)
    XCTAssertEqual(
      DivTemplates.empty.parseValue(type: DivTextTemplate.self, from: text).value,
      DivTemplates.empty.parseValue(type: DivText.self, from: text).value
    )
  }

  func test_resolve_cardTemplatesOverload_ignoresFlagsInfo() {
    let card = makeCard(div: ["type": "headline", "text": "Flags are ignored"])
    let templates = DivTemplates(dictionary: templatesDictionary)
    let values = [DivFlagsInfo(), DivFlagsInfo(useUntypedTemplateResolver: true)].flatMap {
      [
        DivData.resolve(card: card, templates: templatesDictionary, flagsInfo: $0).value,
        DivData.resolve(card: card, templates: templates, flagsInfo: $0).value,
      ]
    }

    XCTAssertEqual(values.count, 4)
    XCTAssertNotNil(values[0])
    for value in values {
      assertEqual(value, values[0])
    }
  }

  func test_parseValue_failure_keepsIndependentErrors() {
    let result = DivData.resolve(
      card: ["log_id": "x", "functions": [["name": "f"]]],
      templates: [:]
    )

    guard case let .failure(errors) = result else {
      return XCTFail("Expected failure, got \(result)")
    }
    let descriptions = errors.map(\.description)
    XCTAssertGreaterThanOrEqual(errors.count, 2)
    XCTAssertTrue(descriptions.contains { $0.contains("arguments") })
    XCTAssertTrue(descriptions.contains { $0.contains("states") })
  }

  func test_parseValue_failure_keepsErrorWithSameDescriptionAsChainLeaf() {
    let result = DivTemplates.empty.parseValue(type: DivSelect.self, from: [
      "type": "select",
      "value_variable": "x",
      "disappear_actions": [
        ["log_id": "a", "typed": ["type": "set_variable", "variable_name": "v"]],
      ],
      "options": [["text": "o"]],
    ])

    guard case let .failure(errors) = result else {
      return XCTFail("Expected failure, got \(result)")
    }
    XCTAssertEqual(errors.count, 2)
  }

  func test_parseValue_failure_keepsIndependentErrorWithSameDescription() {
    let result = DivTemplates.empty.parseValue(
      type: DivText.self,
      from: ["type": "text", "ellipsis": [:]]
    )

    guard case let .failure(errors) = result else {
      return XCTFail("Expected failure, got \(result)")
    }
    XCTAssertEqual(errors.count, 2)
    XCTAssertTrue(errors.allSatisfy { $0.errorMessage == "Required field is missing: text" })
  }
}

private let templatesDictionary: [String: Any] = [
  "styled_text": [
    "type": "text",
    "font_size": 14,
    "text_alignment_horizontal": "center",
  ],
  "headline": [
    "type": "styled_text",
    "font_weight": "bold",
  ],
]

private func makeCard(div: [String: Any]) -> [String: Any] {
  [
    "log_id": "test_card",
    "states": [
      [
        "state_id": 0,
        "div": div,
      ],
    ],
  ]
}

private func makeExpectedValue(div: [String: Any]) -> DivData? {
  DivData.resolve(card: makeCard(div: div), templates: [:]).value
}
