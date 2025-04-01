@testable import DivKit
@testable import LayoutKit
import VGSL
import XCTest

final class DivTextExtensionsTests: XCTestCase {
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
}

private let defaultPath = UIElementPath.root + 0 + "text"
