@testable import LayoutKit
import VGSL
import XCTest

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

  func test_decoratingBlockStaleTraitsHintValueClearedOnReconfigure() {
    // Verifies that after reconfiguring a DecoratingView, previously set
    // accessibilityTraits, hint, and value are cleared when the new element does
    // not carry them.  This is a regression test for DIVKIT-7963: the old
    // applyAccessibilityFromScratch only reset fields when the new element was
    // nil, so stale data from a previous non-nil element (e.g. a removed
    // .button trait or a removed hint/value from a past action overlay) could
    // persist.  The test fails on any implementation where the non-nil branch
    // of applyAccessibilityFromScratch does not explicitly clear accessibilityTraits,
    // accessibilityHint, and accessibilityValue before applying the new element.
    let richAccessibility = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(
        label: "Кнопка",
        hint: "Коснитесь дважды, чтобы активировать",
        value: "Сейчас в состоянии false",
        identifier: "button_1"
      )
    )
    // New element has .none trait, no hint, no value — all previously set fields
    // should be cleared.
    let plainAccessibility = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(
        label: "Элемент",
        hint: nil,
        value: nil,
        identifier: "element_1"
      )
    )

    let block1 = makeDecoratingBlock(accessibility: richAccessibility)
    let view = block1.makeBlockView()
    view.assertHasAccessibilityEqualsTo(richAccessibility)

    // Reconfigure: the new element lacks traits, hint, and value.
    let block2 = makeDecoratingBlock(accessibility: plainAccessibility)
    block2.configureBlockView(view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil)

    // All stale fields from the previous element must be gone.
    XCTAssertEqual(view.accessibilityTraits, plainAccessibility.traits.uiTraits)
    XCTAssertNil(view.accessibilityHint)
    XCTAssertNil(view.accessibilityValue)
    view.assertHasAccessibilityEqualsTo(plainAccessibility)
  }

  func test_decoratingBlockStaleTraitsHintValueClearedOnReconfigureWithActions() {
    // Verifies that stale traits/hint/value contributed by a previous action's
    // accessibilityElement are cleared when the action is removed and a new
    // plain base element is applied.  This tests the non-nil-to-non-nil
    // transition where action-overlay accessibility data from the old model
    // must not bleed into the new model.
    let actionElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(
        label: "Кнопка",
        hint: "Коснитесь дважды",
        value: nil,
        identifier: nil
      )
    )
    let action = UserInterfaceAction(
      path: UIElementPath("button_1"),
      accessibilityElement: actionElement
    )
    let baseAccessibility = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: "Контейнер")
    )
    // Old model: base element + action that overlays .button trait and hint.
    let blockWithAction = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      actions: NonEmptyArray<UserInterfaceAction>([action]),
      accessibilityElement: baseAccessibility
    )
    let view = blockWithAction.makeBlockView()

    // New model: same base element but NO actions; the .button trait and hint
    // from the action must be gone.
    let plainAccessibility = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: "Контейнер")
    )
    let blockWithoutAction = makeDecoratingBlock(accessibility: plainAccessibility)
    blockWithoutAction.configureBlockView(
      view, observer: nil, overscrollDelegate: nil, renderingDelegate: nil
    )

    XCTAssertEqual(view.accessibilityTraits, plainAccessibility.traits.uiTraits)
    XCTAssertNil(view.accessibilityHint)
    view.assertHasAccessibilityEqualsTo(plainAccessibility)
  }

  func test_wrapperDecoratingBlockForwardsAccessibilityActivateToChild() {
    // Verifies that a wrapper DecoratingView (no actions, used for margins) forwards
    // accessibilityActivate() to its child view so VoiceOver detects the subtree as
    // activatable and announces "Double tap to activate".
    // Regression test for DIVKIT-7963 (Bug 1: missing "Коснитесь дважды, чтобы активировать").
    let action = UserInterfaceAction(
      payload: .empty,
      path: UIElementPath("button_1")
    )
    let innerBlock = DecoratingBlock(
      child: EmptyBlock.zeroSized,
      actions: NonEmptyArray<UserInterfaceAction>([action]),
      accessibilityElement: AccessibilityElement(
        traits: .button,
        strings: AccessibilityElement.Strings(label: "Элемент 1")
      )
    )
    // Outer wrapper block (no accessibility, no actions) — created when margins are applied.
    let outerBlock = DecoratingBlock(child: innerBlock)

    let outerView = outerBlock.makeBlockView()
    outerBlock.configureBlockView(
      outerView,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )

    // The outer wrapper view has no actions; it should forward to child.
    XCTAssertTrue(
      outerView.accessibilityActivate(),
      "Outer wrapper DecoratingView should forward accessibilityActivate() to child"
    )
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
    _: @escaping @MainActor (Image?) -> Void
  ) -> Cancellable? {
    EmptyCancellable()
  }

  func reused(
    with _: ImagePlaceholder?,
    remoteImageURL _: URL?
  ) -> ImageHolder? {
    nil
  }

  func equals(_: ImageHolder) -> Bool {
    false
  }
}
