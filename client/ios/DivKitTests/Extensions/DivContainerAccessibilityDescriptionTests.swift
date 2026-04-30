@testable import DivKit
import DivKitTestsSupport
@testable import LayoutKit
import XCTest

final class DivContainerAccessibilityDescriptionTests: XCTestCase {
  func test_Nil_ForContainerWithoutAccessibility() throws {
    let label = try resolveLabel(
      divContainer()
    )

    XCTAssertNil(label)
  }

  func test_Hidden_ForExcludeMode() throws {
    let block = try makeBlock(
      divContainer(
        accessibility: DivAccessibility(
          description: .value("Container description"),
          mode: .value(.exclude)
        )
      )
    )

    XCTAssertEqual(block.accessibilityElement?.hideElementWithChildren, true)
  }

  func test_Description_ForContainerWithDescription() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(
          description: .value("Container description")
        )
      )
    )

    XCTAssertEqual(label, "Container description")
  }

  func test_MergedDescription_ForMergeMode() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(mode: .value(.merge)),
        items: [
          divText(text: "Hello!"),
          divContainer(
            accessibility: DivAccessibility(
              description: .value("Nested container description")
            )
          ),
        ]
      )
    )

    XCTAssertEqual(label, "Hello! Nested container description")
  }

  func test_MergedDescription_NotContainsExcludedItem() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(mode: .value(.merge)),
        items: [
          divText(text: "Hello!"),
          divText(
            accessibility: DivAccessibility(mode: .value(.exclude)),
            text: "Excluded"
          ),
        ]
      )
    )

    XCTAssertEqual(label, "Hello!")
  }

  func test_MergedDescription_ContainsTextFromNestedContainer() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(mode: .value(.merge)),
        items: [
          divText(text: "Hello!"),
          divContainer(
            items: [
              divText(text: "Nested item"),
            ]
          ),
        ]
      )
    )

    XCTAssertEqual(label, "Hello! Nested item")
  }

  func test_Description_HasHigherPriorityThanMergedDescription() throws {
    let label = try resolveLabel(
      divContainer(
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
      )
    )

    XCTAssertEqual(label, "Container description")
  }

  func test_MergedDescription_OnlyContainsVisibleContent() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(mode: .value(.merge)),
        items: [
          divText(text: "One"),
          divText(text: "Two"),
          divText(text: "Three", visibility: .value(.gone)),
          divText(text: "Four", visibility: .value(.invisible)),
        ]
      )
    )

    XCTAssertEqual(label, "One Two")
  }

  func test_MergedDescription_OnlyContainsActiveStateContent() throws {
    let label = try resolveLabel(
      divContainer(
        accessibility: DivAccessibility(mode: .value(.merge)),
        items: [
          divState(
            divId: "switcher",
            states: [
              divStateState(div: divText(text: "Active"), stateId: "a"),
              divStateState(div: divText(text: "Inactive"), stateId: "b"),
            ]
          ),
        ]
      )
    )

    XCTAssertEqual(label, "Active")
  }

  private func makeBlock(_ div: Div) throws -> Block {
    try div.value.makeBlock(context: DivBlockModelingContext())
  }

  private func resolveLabel(_ div: Div) throws -> String? {
    try makeBlock(div).accessibilityElement?.strings.label.flatMap {
      $0.isEmpty ? nil : $0
    }
  }
}
