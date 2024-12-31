@testable import DivKit
import VGSL
import XCTest

final class DivAccessibilityExtensionsTests: XCTestCase {
  func test_resolve_Empty() {
    let element = DivAccessibility()
      .resolve()

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: nil)
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithId() {
    let element = DivAccessibility()
      .resolve(id: "id")

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(
        label: nil,
        identifier: "id"
      )
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeDescriptionAndHint() {
    let element = DivAccessibility(
      description: .value("Description"),
      hint: .value("hint"),
      type: .button
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(
        label: "Description",
        hint: "hint"
      )
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithType_MakesEmptyDescription() {
    let element = DivAccessibility(
      type: .image
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .image,
      strings: AccessibilityElement.Strings(label: "")
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeAndNilCustomDescription_MakesEmptyDescription() {
    let element = DivAccessibility(
      type: .button
    ).resolve(customDescriptionProvider: { nil })

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(label: "")
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithModeExclude_IgnoresParams() {
    let element = DivAccessibility(
      description: .value("Description"),
      hint: .value("hint"),
      mode: .value(.exclude),
      type: .button
    ).resolve(id: "id")

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: nil),
      hideElementWithChildren: true
    )

    assertEqual(element, expectedElement)
  }

  func test_resolve_WithTypeList() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .list
    ).resolve()

    let expectedElement = AccessibilityElement(
      traits: .none,
      strings: AccessibilityElement.Strings(label: "Description"),
      isContainer: true
    )

    assertEqual(element, expectedElement)
  }

  func test_defaultTraits_UsedForAutoType() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .auto
    ).resolve(defaultTraits: .header)

    let expectedElement = AccessibilityElement(
      traits: .header,
      strings: AccessibilityElement.Strings(label: "Description")
    )

    assertEqual(element, expectedElement)
  }

  func test_defaultTraits_HasLowerPriorityThanType() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .button
    ).resolve(defaultTraits: .header)

    let expectedElement = AccessibilityElement(
      traits: .button,
      strings: AccessibilityElement.Strings(label: "Description")
    )

    assertEqual(element, expectedElement)
  }

  func test_customDescriptionProvider_HasHigherPriorityThanDescription() {
    let element = DivAccessibility(
      description: .value("Description"),
      type: .text
    ).resolve(customDescriptionProvider: { "Custom description" })

    let expectedElement = AccessibilityElement(
      traits: .staticText,
      strings: AccessibilityElement.Strings(label: "Custom description")
    )

    assertEqual(element, expectedElement)
  }
}

extension DivAccessibility {
  fileprivate func resolve(
    id: String? = nil,
    defaultTraits: AccessibilityElement.Traits = .none,
    customDescriptionProvider: (() -> String?)? = nil
  ) -> AccessibilityElement {
    resolve(
      DivBlockModelingContext.default.expressionResolver,
      id: id,
      customParams: CustomAccessibilityParams(
        defaultTraits: defaultTraits,
        descriptionProvider: customDescriptionProvider
      )
    )
  }
}
