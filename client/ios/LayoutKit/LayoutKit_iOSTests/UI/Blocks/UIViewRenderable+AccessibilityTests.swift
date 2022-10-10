@testable import LayoutKit

import XCTest

import BaseUI
import CommonCore

final class UIViewRenderable_AccessibilityTests: XCTestCase {
  func test_textBlockAccessibilityApplied() {
    let view = makeTextBlock(accessibility: accessibility).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_containerBlockAccessibilityApplied() {
    let view = makeContainerBlock(accessibility: accessibility).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_decoratingBlockAccessibilityApplied() {
    let view = makeDecoratingBlock(accessibility: accessibility).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_decoratingBlockWithActionsAccessibilityApplied() {
    let view = makeDecoratingBlockWithActions(
      accessibility: accessibility
    ).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_imageBlockAccessibilityApplied() {
    let view = makeImageBlock(accessibility: accessibility).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_animatableImageBlockAccessibilityApplied() {
    let view = makeAnimatableImageBlock(
      accessibility: accessibility
    ).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_switchBlockAccessibilityApplied() {
    let view = makeSwitchBlock(accessibility: accessibility).makeBlockView()
    view.assertHasAccessibilityEqualsTo(accessibility)
  }

  func test_textFieldBlockAccessibilityApplied() {
    let view = makeTextFieldBlock(accessibility: accessibility).makeBlockView()
    guard let textFieldView = view.subviews.filter({ $0 is UITextField }).first else {
      XCTFail()
      return
    }
    textFieldView.assertHasAccessibilityEqualsTo(accessibility)
  }
}

extension UIView {
  fileprivate func assertHasAccessibilityEqualsTo(
    _ accessibility: AccessibilityElement
  ) {
    XCTAssertTrue(isAccessibilityElement)
    XCTAssertEqual(
      accessibilityTraits,
      accessibility.traits.uiTraits
    )
    XCTAssertEqual(
      accessibilityIdentifier,
      accessibility.strings.identifier
    )
    XCTAssertEqual(
      accessibilityLabel,
      accessibility.strings.label
    )
    XCTAssertEqual(
      accessibilityHint,
      accessibility.strings.hint
    )
    XCTAssertEqual(
      accessibilityValue,
      accessibility.strings.value
    )
  }
}

private let accessibility = AccessibilityElement(
  traits: .none,
  strings: AccessibilityElement.Strings(
    label: "label",
    hint: "hint",
    value: "value",
    identifier: "identifier"
  )
)

private func makeTextBlock(
  accessibility: AccessibilityElement?
) -> TextBlock {
  TextBlock(
    widthTrait: .intrinsic,
    text: NSAttributedString(string: String()),
    accessibilityElement: accessibility
  )
}

private func makeContainerBlock(
  accessibility: AccessibilityElement?
) -> ContainerBlock {
  try! ContainerBlock(
    layoutDirection: .horizontal,
    children: [EmptyBlock.zeroSized],
    accessibilityElement: accessibility
  )
}

private func makeDecoratingBlock(
  accessibility: AccessibilityElement?
) -> DecoratingBlock {
  DecoratingBlock(
    child: EmptyBlock.zeroSized,
    accessibilityElement: accessibility
  )
}

private func makeDecoratingBlockWithActions(
  accessibility: AccessibilityElement
) -> DecoratingBlock {
  DecoratingBlock(
    child: EmptyBlock.zeroSized,
    actions: NonEmptyArray<UserInterfaceAction>([
      UserInterfaceAction(path: UIElementPath("0"), accessibilityElement: nil),
      UserInterfaceAction(path: UIElementPath("1"), accessibilityElement: accessibility),
    ])
  )
}

private func makeImageBlock(
  accessibility: AccessibilityElement?
) -> ImageBlock {
  ImageBlock(
    imageHolder: ImageHolderStub(),
    accessibilityElement: accessibility
  )
}

private func makeAnimatableImageBlock(
  accessibility: AccessibilityElement?
) -> AnimatableImageBlock {
  AnimatableImageBlock(
    imageHolder: ImageHolderStub(),
    widthTrait: .intrinsic,
    height: .ratio(1),
    accessibilityElement: accessibility
  )
}

private func makeSwitchBlock(
  accessibility: AccessibilityElement?
) -> SwitchBlock {
  SwitchBlock(
    on: true,
    enabled: true,
    action: nil,
    accessibilityElement: accessibility
  )
}

private func makeTextFieldBlock(
  accessibility: AccessibilityElement?
) -> TextFieldBlock {
  TextFieldBlock(
    text: NSAttributedString(),
    updateAction: UserInterfaceAction(path: UIElementPath("0")),
    accessibilityElement: accessibility
  )
}

private class ImageHolderStub: ImageHolder {
  let debugDescription = String()
  let image: Image? = nil
  let placeholder: ImagePlaceholder? = nil

  func requestImageWithCompletion(
    _: @escaping ((Image?) -> Void)
  ) -> Cancellable? { EmptyCancellable() }

  func reused(
    with _: ImagePlaceholder?, remoteImageURL _: URL?
  ) -> ImageHolder? { nil }

  func equals(_: ImageHolder) -> Bool { false }
}
