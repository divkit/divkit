@testable import DivKit
@testable import LayoutKit

import XCTest

import BaseUIPublic
import CommonCorePublic

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
          verticalAlignment: .leading
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
          accessibilityElement: accessibility(
            traits: .staticText,
            label: "Hello!"
          )
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

  func test_WithAction() {
    let block = makeBlock(
      divText(
        actions: [DivAction(
          logId: "action_log_id",
          url: .value(url("https://some.url"))
        )],
        text: "Hello!"
      )
    )

    let expectedBlock = StateBlock(
      child: DecoratingBlock(
        child: TextBlock(
          widthTrait: .resizable,
          text: "Hello!".withTypo(),
          verticalAlignment: .leading
        ),
        actions: NonEmptyArray(
          uiAction(logId: "action_log_id", url: "https://some.url")
        ),
        actionAnimation: .default,
        accessibilityElement: accessibility(
          traits: .staticText,
          label: "Hello!"
        )
      ),
      ids: []
    )

    assertEqual(block, expectedBlock)
  }
}
