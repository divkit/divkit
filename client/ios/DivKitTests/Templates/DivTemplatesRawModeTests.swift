@testable import DivKit
import DivKitTestsSupport
import Foundation
import Serialization
import XCTest

final class DivTemplatesRawModeTests: XCTestCase {
  func test_initWithDictionaryInRawMode_makesCorrectTemplateToType() {
    let templates = makeRawTemplates([
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

    let mergedTemplates = makeRawTemplates(firstBatch).resolve(newTemplates: secondBatch)
    let mergedResult = DivData.resolve(
      card: card,
      templates: mergedTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )

    let unionDictionary = firstBatch.merging(secondBatch, uniquingKeysWith: { $1 })
    let legacyResult = DivData.resolve(
      card: card,
      templates: unionDictionary,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )

    XCTAssertNotNil(mergedResult.value)
    assertEqual(mergedResult.value, legacyResult.value)
    XCTAssertEqual(
      mergedResult.errorsOrWarnings?.count ?? 0,
      legacyResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_resolveNewTemplates_conflictHonorsShouldKeepExistingOnConflict() {
    let baseTemplates = makeRawTemplates([
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
      templates: baseTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    XCTAssertNotNil(baseResult.value)
    assertEqual(baseResult.value, oldExpectedValue)

    let keptTemplates = baseTemplates.resolve(newTemplates: redefinition)
    let keptResult = DivData.resolve(
      card: card,
      templates: keptTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
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
      templates: overriddenTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    XCTAssertNotNil(overriddenResult.value)
    assertEqual(overriddenResult.value, oldExpectedValue)
    XCTAssertEqual(overriddenResult.errorsOrWarnings?.count ?? 0, 0)

    let directCard = makeCard(div: ["type": "styled_text"])
    let directResult = DivData.resolve(
      card: directCard,
      templates: overriddenTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    XCTAssertNotNil(directResult.value)
    assertEqual(directResult.value, makeExpectedValue(div: [
      "type": "text",
      "text": "new text",
    ]))
    XCTAssertEqual(directResult.errorsOrWarnings?.count ?? 0, 0)

    let baseResultAfterMerging = DivData.resolve(
      card: card,
      templates: baseTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    XCTAssertNotNil(baseResultAfterMerging.value)
    assertEqual(baseResultAfterMerging.value, oldExpectedValue)
  }

  func test_initWithDictionaryInRawMode_dropsCyclicTemplates() {
    let cyclicTemplates: [String: Any] = [
      "ouroboros": ["type": "amphisbaena"],
      "amphisbaena": ["type": "ouroboros"],
    ]
    let templates = makeRawTemplates(cyclicTemplates)

    XCTAssertFalse(templates.templates.keys.contains("ouroboros"))
    XCTAssertFalse(templates.templates.keys.contains("amphisbaena"))

    let card = makeCard(div: ["type": "ouroboros"])
    let result = DivData.resolve(
      card: card,
      templates: templates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    let legacyResult = DivData.resolve(
      card: card,
      templates: cyclicTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )

    assertEqual(result.value, legacyResult.value)
    XCTAssertEqual(
      result.errorsOrWarnings?.count ?? 0,
      legacyResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_reuse_secondCardMatchesFreshInstance() {
    let sharedTemplates = makeRawTemplates(templatesDictionary)
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
      templates: sharedTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    let reusedResult = DivData.resolve(
      card: secondCard,
      templates: sharedTemplates,
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    let freshResult = DivData.resolve(
      card: secondCard,
      templates: makeRawTemplates(templatesDictionary),
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )

    XCTAssertNotNil(reusedResult.value)
    assertEqual(reusedResult.value, freshResult.value)
    XCTAssertEqual(
      reusedResult.errorsOrWarnings?.count ?? 0,
      freshResult.errorsOrWarnings?.count ?? 0
    )
  }

  func test_concurrentResolve_isConsistent() {
    let sharedTemplates = makeRawTemplates(templatesDictionary)
    let card = makeCard(div: [
      "type": "headline",
      "text": "Resolved concurrently",
    ])

    let expectedResult = DivData.resolve(
      card: card,
      templates: makeRawTemplates(templatesDictionary),
      flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
    )
    XCTAssertNotNil(expectedResult.value)
    XCTAssertNil(expectedResult.errorsOrWarnings)

    let iterations = 100
    let lock = NSLock()
    var results: [DeserializationResult<DivData>] = []
    DispatchQueue.concurrentPerform(iterations: iterations) { _ in
      let result = DivData.resolve(
        card: card,
        templates: sharedTemplates,
        flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
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

  func test_resolve_pipelineIsSelectedByContainerNotByFlag() {
    let card = makeCard(div: [
      "type": "styled_text",
      "text": "Same value in every mode",
    ])
    let expectedValue = makeExpectedValue(div: [
      "type": "text",
      "font_size": 14,
      "text_alignment_horizontal": "center",
      "text": "Same value in every mode",
    ])

    let containers = [
      DivTemplates(dictionary: templatesDictionary),
      makeRawTemplates(templatesDictionary),
    ]
    let flags = [
      DivFlagsInfo.default,
      DivFlagsInfo(useUntypedTemplateResolver: true),
    ]

    for templates in containers {
      for flagsInfo in flags {
        let result = DivData.resolve(card: card, templates: templates, flagsInfo: flagsInfo)
        XCTAssertNotNil(result.value)
        assertEqual(result.value, expectedValue)
        XCTAssertEqual(result.errorsOrWarnings?.count ?? 0, 0)
      }
    }
  }

  func test_parseValue_nonCardValueThroughRawContainerMatchesTypedParsing() {
    let textDict: [String: Any] = [
      "type": "text",
      "text": "Non-card value",
    ]

    let emptyRawResult = makeRawTemplates([:])
      .parseValue(type: DivTextTemplate.self, from: textDict)
    let emptyTypedResult = DivTemplates.empty
      .parseValue(type: DivTextTemplate.self, from: textDict)
    XCTAssertNotNil(emptyRawResult.value)
    assertEqual(emptyRawResult.value, emptyTypedResult.value)

    let rawResult = makeRawTemplates(templatesDictionary)
      .parseValue(type: DivTextTemplate.self, from: textDict)
    let typedResult = DivTemplates(dictionary: templatesDictionary)
      .parseValue(type: DivTextTemplate.self, from: textDict)
    XCTAssertNotNil(rawResult.value)
    assertEqual(rawResult.value, typedResult.value)
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

private func makeRawTemplates(_ dictionary: [String: Any]) -> DivTemplates {
  DivTemplates(
    dictionary: dictionary,
    flagsInfo: DivFlagsInfo(useUntypedTemplateResolver: true)
  )
}

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
