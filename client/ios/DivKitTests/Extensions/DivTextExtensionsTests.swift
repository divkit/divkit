@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import Serialization
import VGSL
import XCTest

final class DivTextExtensionsTests: XCTestCase {
  func test_AnimatedGradientWithoutDuration_UsesDefaultDuration() throws {
    let block = try animatedTextBlock()
    XCTAssertEqual(block.gradientModel?.animation, .init(duration: 1600))
  }

  func test_AnimatedGradientWithZeroDuration_PreservesZeroDuration() throws {
    let block = try animatedTextBlock(gradientProperties: ["duration": 0])
    XCTAssertEqual(block.gradientModel?.animation?.duration, 0)
  }

  func test_AnimatedGradientColorExpression_UpdatesGradient() throws {
    let storage = DivVariableStorage(outerStorage: nil)
    storage.put(["color": .color(.red)])
    let context = DivBlockModelingContext(variableStorage: storage)
    let gradient: [String: Any] = ["type": "gradient", "colors": ["@{color}", "#0000FF"]]
    let original = try animatedTextBlock(
      gradientProperties: ["gradient": gradient],
      context: context
    )
    storage.update(name: "color", value: .color(.green))
    let updated = try animatedTextBlock(
      gradientProperties: ["gradient": gradient],
      context: context
    )
    guard case let .linear(linear) = updated.gradientModel?.gradient else {
      return XCTFail("Expected a linear gradient")
    }
    XCTAssertEqual(linear.startColor, .green)
    XCTAssertFalse(original == updated)
  }

  func test_AnimatedGradientDisabledMask_KeepsGlyphs() throws {
    let block = try animatedTextBlock(textProperties: [
      "ranges": [[
        "start": 0,
        "end": 2,
        "mask": ["type": "solid", "color": "#777777", "is_enabled": false],
      ]],
    ])
    XCTAssertEqual(
      block.text.attribute(.animatedTextGradient, at: 0, effectiveRange: nil) as? Bool,
      true
    )
  }

  func test_AnimatedGradientExplicitTransparentRange_ExcludesGlyphs() throws {
    let block = try animatedTextBlock(textProperties: [
      "ranges": [["start": 1, "end": 3, "text_color": "#00000000"]],
    ])
    XCTAssertEqual(
      block.text.attribute(.animatedTextGradient, at: 0, effectiveRange: nil) as? Bool,
      true
    )
    XCTAssertNil(block.text.attribute(.animatedTextGradient, at: 1, effectiveRange: nil))
    XCTAssertEqual(
      block.text.attribute(.animatedTextGradient, at: 3, effectiveRange: nil) as? Bool,
      true
    )
  }

  func test_AnimatedGradientEnabledMask_ExcludesGlyphs() throws {
    let block = try animatedTextBlock(textProperties: [
      "ranges": [[
        "start": 0,
        "end": 2,
        "mask": ["type": "solid", "color": "#FF0000"],
      ]],
    ])
    XCTAssertNil(block.text.attribute(.animatedTextGradient, at: 0, effectiveRange: nil))
  }

  func test_AnimatedGradientCustomEllipsis_InheritsGradientExceptColoredRange() throws {
    let block = try animatedTextBlock(textProperties: [
      "ellipsis": ["text": "more", "ranges": [["start": 1, "end": 2, "text_color": "#00FF00"]]],
    ])
    let token = try XCTUnwrap(block.truncationToken)
    XCTAssertEqual(
      token.attribute(.animatedTextGradient, at: 0, effectiveRange: nil) as? Bool,
      true
    )
    XCTAssertNil(token.attribute(.animatedTextGradient, at: 1, effectiveRange: nil))
  }

  func test_AnimatedGradientDurationExpression_UpdatesBlock() throws {
    let storage = DivVariableStorage(outerStorage: nil)
    storage.put(["duration": .integer(1600)])
    let context = DivBlockModelingContext(variableStorage: storage)
    let original = try animatedTextBlock(
      gradientProperties: ["duration": "@{duration}"],
      context: context
    )
    storage.update(name: "duration", value: .integer(800))
    let updated = try animatedTextBlock(
      gradientProperties: ["duration": "@{duration}"],
      context: context
    )
    XCTAssertEqual(updated.gradientModel?.animation?.duration, 800)
    XCTAssertFalse(original == updated)
  }

  func test_AnimatedGradientZeroRuntimeDuration_PreservesZeroDuration() throws {
    let storage = DivVariableStorage(outerStorage: nil)
    storage.put(["duration": .integer(0)])
    let block = try animatedTextBlock(
      gradientProperties: ["duration": "@{duration}"],
      context: DivBlockModelingContext(variableStorage: storage)
    )
    XCTAssertEqual(block.gradientModel?.animation?.duration, 0)
  }

  func test_AnimatedGradientNegativeRuntimeDuration_UsesDefaultAndReportsValidationError() throws {
    let storage = DivVariableStorage(outerStorage: nil)
    storage.put(["duration": .integer(-1)])
    let context = DivBlockModelingContext(variableStorage: storage)
    let block = try animatedTextBlock(
      gradientProperties: ["duration": "@{duration}"],
      context: context,
      ignoreErrors: true
    )
    XCTAssertEqual(block.gradientModel?.animation?.duration, 1600)
    XCTAssertTrue(context.errorsStorage.errors.contains {
      $0.message.contains("Failed to validate value: -1")
    })
  }

  func test_AnimatedGradientMissingColors_FallsBackToTextColor() throws {
    let context = DivBlockModelingContext()
    let block = try animatedTextBlock(
      gradientProperties: ["gradient": ["type": "gradient"]],
      context: context, ignoreErrors: true
    )
    XCTAssertNil(block.gradientModel)
    XCTAssertTrue(context.errorsStorage.errors
      .contains { $0.message.contains("No colors specified") }
    )
  }

  func test_WithText() {
    let block = makeBlock(
      divText(text: "Hello!")
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextBlock(
          widthTrait: .resizable,
          text: "Hello!".withTypo(),
          verticalAlignment: .leading,
          accessibilityElement: nil,
          path: defaultPath
        ),
        accessibilityElement: accessibility(
          traits: .staticText,
          label: "Hello!"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibility() throws {
    let block = makeBlock(
      divText(
        accessibility: DivAccessibility(
          description: .value("Accessibility description"),
          type: .button
        ),
        id: "text_id",
        text: "Hello!"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextBlock(
          widthTrait: .resizable,
          text: "Hello!".withTypo(),
          verticalAlignment: .leading,
          accessibilityElement: nil,
          path: .root + 0 + "text_id"
        ),
        accessibilityElement: accessibility(
          traits: .button,
          label: "Accessibility description",
          identifier: "text_id"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAccessibilityWithoutDescription_AppliesTextAsDescription() throws {
    let block = makeBlock(
      divText(
        accessibility: DivAccessibility(type: .button),
        text: "Hello!"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextBlock(
          widthTrait: .resizable,
          text: "Hello!".withTypo(),
          verticalAlignment: .leading,
          accessibilityElement: nil,
          path: defaultPath
        ),
        accessibilityElement: accessibility(
          traits: .button,
          label: "Hello!"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_WithAction() {
    let block = makeBlock(
      divText(
        actions: [
          divAction(
            logId: "action_log_id",
            url: "https://some.url"
          ),
        ],
        text: "Hello!"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: textBlock(
          text: "Hello!",
          path: .root + 0 + "text"
        ),
        actions: NonEmptyArray(
          uiAction(
            logId: "action_log_id",
            path: .root + "0" + "text",
            url: "https://some.url"
          )
        ),
        actionAnimation: .default,
        accessibilityElement: accessibility(
          traits: .staticText,
          label: "Hello!"
        ),
        path: defaultPath
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }

  func test_Path_WithId() throws {
    let textId = "custom_text_id"

    let stateId = 3
    let cardId = DivCardID(rawValue: "custom_card_id")
    let context = DivBlockModelingContext(cardId: cardId)

    let block: TextBlock = try makeBlock(
      divText(
        id: textId
      ),
      context: context,
      stateId: stateId
    ).child.unwrap()
    let path = block.path

    let expectedPath = UIElementPath(cardId.rawValue) + stateId + textId

    assertEqual(path, expectedPath)
  }

  func test_Path_WithoutId() throws {
    let stateId = 10
    let cardId = DivCardID(rawValue: "card_id")
    let context = DivBlockModelingContext(cardId: cardId)

    let block: TextBlock = try makeBlock(
      divText(
        id: nil
      ),
      context: context,
      stateId: stateId
    ).child.unwrap()
    let path = block.path

    let expectedPath = UIElementPath(cardId.rawValue) + stateId + "text"

    assertEqual(path, expectedPath)
  }

  func test_TruncationPolicy_Word() throws {
    let textBlock = try makeTextBlock(
      truncatePolicy: .value(.word)
    )

    XCTAssertEqual(textBlock.truncationPolicy, TextTruncationPolicy.word)
  }

  func test_TruncationPolicy_WordSurvivesFocusUpdate() throws {
    let textBlock = try makeTextBlock(
      truncatePolicy: .value(.word)
    )

    let focusedBlock = try textBlock.updated(path: defaultPath, isFocused: true)

    XCTAssertTrue(focusedBlock.isFocused)
    XCTAssertEqual(focusedBlock.truncationPolicy, TextTruncationPolicy.word)
  }

  func test_TruncationPolicy_Default() throws {
    let textBlock = try makeTextBlock()

    XCTAssertEqual(textBlock.truncationPolicy, TextTruncationPolicy.grapheme)
  }

  func test_TruncationPolicy_WordIsIgnoredForMiddleTruncation() throws {
    let textBlock = try makeTextBlock(
      truncate: .value(.middle),
      truncatePolicy: .value(.word)
    )

    XCTAssertEqual(textBlock.truncationPolicy, TextTruncationPolicy.grapheme)
  }

  private func makeTextBlock(
    truncate: DivKit.Expression<DivText.Truncate>? = nil,
    truncatePolicy: DivKit.Expression<DivText.TruncatePolicy>? = nil
  ) throws -> TextBlock {
    try makeBlock(.divText(DivText(
      text: .value("Text"),
      truncate: truncate,
      truncatePolicy: truncatePolicy
    ))).child.unwrap()
  }
}

private func animatedTextBlock(
  gradientProperties: [String: Any] = [:],
  textProperties: [String: Any] = [:],
  context: DivBlockModelingContext = DivBlockModelingContext(),
  ignoreErrors: Bool = false
) throws -> TextBlock {
  var gradient: [String: Any] = [
    "type": "animated", "gradient": ["type": "gradient", "colors": ["#FF0000", "#0000FF"]],
  ]
  gradient.merge(gradientProperties) { _, new in new }
  var text: [String: Any] = ["type": "text", "text": "Hello!", "text_gradient": gradient]
  text.merge(textProperties) { _, new in new }
  let data = try JSONSerialization.data(withJSONObject: text)
  let json = try JSONSerialization.jsonObject(with: data) as! [String: Any]
  let div = try Div(dictionary: json, context: ParsingContext())
  return try makeBlock(div, context: context, ignoreErrors: ignoreErrors).child.unwrap()
}

private let defaultPath = UIElementPath.root + 0 + "text"
