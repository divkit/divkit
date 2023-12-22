@testable import DivKit

import XCTest

final class DivContainerAccessibilityDescriptionTests: XCTestCase {
  func test_Nil_ForContainerWithoutAccessibility() {
    let description = divContainer()
      .resolveAccessibilityDescription()

    XCTAssertNil(description)
  }

  func test_Nil_ForExcludeMode() {
    let description = divContainer(
      accessibility: DivAccessibility(
        description: .value("Container description"),
        mode: .value(.exclude)
      )
    ).resolveAccessibilityDescription()

    XCTAssertNil(description)
  }

  func test_Description_ForContainerWithDescription() {
    let description = divContainer(
      accessibility: DivAccessibility(
        description: .value("Container description")
      )
    ).resolveAccessibilityDescription()

    XCTAssertEqual(description, "Container description")
  }

  func test_MergedDescription_ForMergeMode() {
    let description = divContainer(
      accessibility: DivAccessibility(mode: .value(.merge)),
      items: [
        divText(text: "Hello!"),
        divContainer(
          accessibility: DivAccessibility(
            description: .value("Nested container description")
          )
        ),
      ]
    ).resolveAccessibilityDescription()

    XCTAssertEqual(description, "Hello! Nested container description")
  }

  func test_MergedDescription_NotContainsExcludedItem() {
    let description = divContainer(
      accessibility: DivAccessibility(mode: .value(.merge)),
      items: [
        divText(text: "Hello!"),
        divText(
          accessibility: DivAccessibility(mode: .value(.exclude)),
          text: "Excluded"
        ),
      ]
    ).resolveAccessibilityDescription()

    XCTAssertEqual(description, "Hello!")
  }

  func test_MergedDescription_ContainsDescriptionFromNestedContainer() {
    let description = divContainer(
      accessibility: DivAccessibility(mode: .value(.merge)),
      items: [
        divText(text: "Hello!"),
        divContainer(
          items: [
            divText(text: "Nested item"),
          ]
        ),
      ]
    ).resolveAccessibilityDescription()

    XCTAssertEqual(description, "Hello! Nested item")
  }

  func test_Description_HasHigherPriorityThanMergedDescription() {
    let description = divContainer(
      accessibility: DivAccessibility(
        description: .value("Container description"),
        mode: .value(.merge)
      ),
      items: [
        divText(text: "Hello!"),
        divContainer(
          accessibility: DivAccessibility(
            description: .value("Nested container description")
          )
        ),
      ]
    ).resolveAccessibilityDescription()

    XCTAssertEqual(description, "Container description")
  }
}

extension Div {
  fileprivate func resolveAccessibilityDescription() -> String? {
    (value as! DivContainer).resolveAccessibilityDescription(.default)
  }
}
