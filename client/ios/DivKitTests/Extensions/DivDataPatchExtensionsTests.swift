@testable import DivKit

import XCTest

import CommonCorePublic

final class DivDataPatchExtensionsTests: XCTestCase {
  func test_WhenNoSuitableChanges_DoesNothing() throws {
    let originalData = makeData(
      div: makeContainer(
        items: [
          makeText(id: "some_div", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", makeSeparator())
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingRootDiv_ReplacesDiv() throws {
    let originalData = makeData(
      div: makeText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = makeData(
      div: newDivText1
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingDivWithDifferentType_ReplacesDiv() throws {
    let oridinalData = makeData(
      div: makeText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = oridinalData.applyPatch(
      ("div_to_replace", makeSeparator())
    )
    let expectedData = makeData(
      div: makeSeparator()
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingRootDiv_DoesNothing() throws {
    let originalData = makeData(
      div: makeText(id: "div_to_delete", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingRootDivWithMultipleDivs_DoesNothing() throws {
    let originalData = makeData(
      div: makeText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingItemInContainer_ReplacesDiv() throws {
    let originalData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          newDivText1,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingItemsInContainer_ReplacesDivs() throws {
    let originalData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    let expectedData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          newDivText1,
          newDivText2,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingItemInContainer_RemovesDiv() throws {
    let originalData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          makeText(id: "div_to_delete", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenHaveMultipleChanges_AppliesAll() throws {
    let originalData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeText(id: "div_to_delete", text: "Text to delete"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makeContainer(
        items: [
          makeSeparator(),
          newDivText1,
          newDivText2,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingItemsInGallery_ReplacesDivs() throws {
    let originalData = makeData(
      div: makeGallery(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    let expectedData = makeData(
      div: makeGallery(
        items: [
          makeSeparator(),
          newDivText1,
          newDivText2,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingItemInGallery_RemovesDiv() throws {
    let originalData = makeData(
      div: makeGallery(
        items: [
          makeSeparator(),
          makeText(id: "div_to_delete", text: "Old text"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makeGallery(
        items: [
          makeSeparator(),
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingAndDeletingItemsInGrid_AppliesAll() throws {
    let originalData = makeData(
      div: makeGrid(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeSeparator(id: "div_to_delete"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makeGrid(
        items: [
          makeSeparator(),
          newDivText1,
          newDivText2,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingAndDeletingItemsInPager_AppliesAll() throws {
    let originalData = makeData(
      div: makePager(
        items: [
          makeSeparator(),
          makeText(id: "div_to_replace", text: "Old text"),
          makeSeparator(id: "div_to_delete"),
          makeSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makePager(
        items: [
          makeSeparator(),
          newDivText1,
          newDivText2,
          makeSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingStateDiv_ReplacesDiv() throws {
    let originalData = makeData(
      div: makeState(
        states: [
          makeText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = makeData(
      div: makeState(
        states: [
          newDivText1,
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingStateDiv_KeepsEmptyState() throws {
    let originalData = makeData(
      div: makeState(
        states: [
          makeText(id: "div_to_delete", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = makeData(
      div: makeState(
        states: [nil]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingStateDivWithMultipleDivs_DoesNothing() throws {
    let originalData = makeData(
      div: makeState(
        states: [
          makeText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingTabsDiv_ReplacesDiv() throws {
    let originalData = makeData(
      div: makeTabs(
        items: [
          makeText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = makeData(
      div: makeTabs(
        items: [
          newDivText1,
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingTabsDiv_DoesNothing() throws {
    let originalData = makeData(
      div: makeTabs(
        items: [
          makeText(id: "div_to_delete", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    XCTAssertEqual(patchedData, originalData)
  }
}

private let newDivText1 = makeText(text: "New text 1")
private let newDivText2 = makeText(text: "New text 2")

private func makeData(div: Div) -> DivData {
  makeDivData(
    logId: "div",
    states: [DivData.State(div: div, stateId: 0)]
  )
}

private func makeContainer(items: [Div]) -> Div {
  .divContainer(
    makeDivContainer(items: items)
  )
}

private func makeGallery(items: [Div]) -> Div {
  .divGallery(
    makeDivGallery(items: items)
  )
}

private func makeGrid(items: [Div]) -> Div {
  .divGrid(
    makeDivGrid(columnCount: 1, items: items)
  )
}

private func makePager(items: [Div]) -> Div {
  .divPager(
    makeDivPager(
      items: items,
      layoutMode: .divPageSize(DivPageSize(pageWidth: DivPercentageSize(value: .value(10))))
    )
  )
}

private func makeSeparator(id: String? = nil) -> Div {
  .divSeparator(DivSeparator(id: id))
}

private func makeState(states: [Div?]) -> Div {
  .divState(
    makeDivState(
      divId: "state",
      states: states.enumerated().map { index, div in
        makeDivStateState(div: div, stateId: "state_\(index)")
      }
    )
  )
}

private func makeTabs(items: [Div]) -> Div {
  .divTabs(
    makeDivTabs(
      items: items.enumerated().map { index, div in
        makeDivTabsItem(div: div, title: "Tab \(index)")
      }
    )
  )
}

private func makeText(id: String? = nil, text: String) -> Div {
  .divText(DivText(id: id, text: .value(NSString(string: text))))
}

extension DivData {
  fileprivate func applyPatch(_ changes: (String, [Div]?)...) -> DivData {
    let patch = DivPatch(
      changes: changes.map { id, items in
        DivPatch.Change(id: id, items: items)
      }
    )
    return applyPatch(patch)
  }

  fileprivate func applyPatch(_ change: (String, Div)) -> DivData {
    applyPatch((change.0, [change.1]))
  }
}
