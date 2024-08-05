@testable import DivKit

import XCTest

import VGSL

final class DivDataPatchExtensionsTests: XCTestCase {
  private var callbacksCount = 0
  private lazy var divPatchCallbacks = Callbacks { _ in
    self.callbacksCount += 1
  }

  func test_WhenNoSuitableChanges_DoesNothing() throws {
    let originalData = divData(
      divContainer(
        items: [
          divText(id: "some_div", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", divSeparator())
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingRootDiv_ReplacesDiv() throws {
    let originalData = divData(
      divText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = divData(
      newDivText1
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingDivWithDifferentType_ReplacesDiv() throws {
    let oridinalData = divData(
      divText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = oridinalData.applyPatch(
      ("div_to_replace", divSeparator())
    )
    let expectedData = divData(
      divSeparator()
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingRootDiv_DoesNothing() throws {
    let originalData = divData(
      divText(id: "div_to_delete", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingRootDivWithMultipleDivs_DoesNothing() throws {
    let originalData = divData(
      divText(id: "div_to_replace", text: "Old text")
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingItemInContainer_ReplacesDiv() throws {
    let originalData = divData(
      divContainer(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = divData(
      divContainer(
        items: [
          divSeparator(),
          newDivText1,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingItemsInContainer_ReplacesDivs() throws {
    let originalData = divData(
      divContainer(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    let expectedData = divData(
      divContainer(
        items: [
          divSeparator(),
          newDivText1,
          newDivText2,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingItemInContainer_RemovesDiv() throws {
    let originalData = divData(
      divContainer(
        items: [
          divSeparator(),
          divText(id: "div_to_delete", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      divContainer(
        items: [
          divSeparator(),
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenHaveMultipleChanges_AppliesAll() throws {
    let originalData = divData(
      divContainer(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divText(id: "div_to_delete", text: "Text to delete"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      divContainer(
        items: [
          divSeparator(),
          newDivText1,
          newDivText2,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingItemsInGallery_ReplacesDivs() throws {
    let originalData = divData(
      divGallery(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    let expectedData = divData(
      divGallery(
        items: [
          divSeparator(),
          newDivText1,
          newDivText2,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingItemInGallery_RemovesDiv() throws {
    let originalData = divData(
      divGallery(
        items: [
          divSeparator(),
          divText(id: "div_to_delete", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      divGallery(
        items: [
          divSeparator(),
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingAndDeletingItemsInGrid_AppliesAll() throws {
    let originalData = divData(
      divGrid(
        columnCount: 1,
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divSeparator(id: "div_to_delete"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      divGrid(
        columnCount: 1,
        items: [
          divSeparator(),
          newDivText1,
          newDivText2,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingAndDeletingItemsInPager_AppliesAll() throws {
    let originalData = divData(
      makePager(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divSeparator(id: "div_to_delete"),
          divSeparator(),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2]),
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      makePager(
        items: [
          divSeparator(),
          newDivText1,
          newDivText2,
          divSeparator(),
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingStateDiv_ReplacesDiv() throws {
    let originalData = divData(
      makeState(
        states: [
          divText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = divData(
      makeState(
        states: [
          newDivText1,
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingStateDiv_KeepsEmptyState() throws {
    let originalData = divData(
      makeState(
        states: [
          divText(id: "div_to_delete", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    let expectedData = divData(
      makeState(
        states: [nil]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenReplacingStateDivWithMultipleDivs_DoesNothing() throws {
    let originalData = divData(
      makeState(
        states: [
          divText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", [newDivText1, newDivText2])
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacingTabsDiv_ReplacesDiv() throws {
    let originalData = divData(
      makeTabs(
        items: [
          divText(id: "div_to_replace", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_replace", newDivText1)
    )
    let expectedData = divData(
      makeTabs(
        items: [
          newDivText1,
        ]
      )
    )
    XCTAssertEqual(patchedData, expectedData)
  }

  func test_WhenDeletingTabsDiv_DoesNothing() throws {
    let originalData = divData(
      makeTabs(
        items: [
          divText(id: "div_to_delete", text: "Old text"),
        ]
      )
    )
    let patchedData = originalData.applyPatch(
      ("div_to_delete", nil)
    )
    XCTAssertEqual(patchedData, originalData)
  }

  func test_WhenReplacesSuitableDiv_CallElementChangedCallback() throws {
    let originalData = divData(
      divContainer(
        items: [
          divText(id: "some_div", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    _ = originalData.applyPatch(
      ("some_div", divSeparator()),
      callbacks: divPatchCallbacks
    )
    XCTAssertEqual(callbacksCount, 1)
  }

  func test_WhenNoSuitableChanges_DoesNotCallElementChangedCallback() throws {
    let originalData = divData(
      divContainer(
        items: [
          divText(id: "some_div", text: "Old text"),
          divSeparator(),
        ]
      )
    )
    _ = originalData.applyPatch(
      ("div_to_replace", divSeparator()),
      callbacks: divPatchCallbacks
    )
    XCTAssertEqual(callbacksCount, 0)
  }

  func test_WhenReplacingDivInMultipleItemsContainer_CallOnlyElementChangedCallbacks() throws {
    let originalData = divData(
      divContainer(
        items: [
          divSeparator(),
          divText(id: "div_to_replace", text: "Old text"),
          divText(id: "some_div", text: "Old text 2"),
          divSeparator(),
        ]
      )
    )
    _ = originalData.applyPatch(
      ("div_to_replace", [newDivText1]), ("div_to_replace_2", [newDivText1]),
      callbacks: divPatchCallbacks
    )
    XCTAssertEqual(callbacksCount, 1)
  }
}

private let newDivText1 = divText(text: "New text 1")
private let newDivText2 = divText(text: "New text 2")

private func makePager(items: [Div]) -> Div {
  divPager(
    items: items,
    layoutMode: .divPageSize(DivPageSize(pageWidth: DivPercentageSize(value: .value(10))))
  )
}

private func makeState(states: [Div?]) -> Div {
  divState(
    divId: "state",
    states: states.enumerated().map { index, div in
      divStateState(div: div, stateId: "state_\(index)")
    }
  )
}

private func makeTabs(items: [Div]) -> Div {
  divTabs(
    items: items.enumerated().map { index, div in
      divTabsItem(div: div, title: "Tab \(index)")
    }
  )
}

extension DivData {
  fileprivate func applyPatch(
    _ changes: (String, [Div]?)...,
    callbacks: DivPatchCallbacks = Callbacks.empty
  ) -> DivData {
    let patch = DivPatch(
      changes: changes.map { id, items in
        DivPatch.Change(id: id, items: items)
      }
    )
    return applyPatch(patch, callbacks: callbacks)
  }

  fileprivate func applyPatch(
    _ change: (String, Div),
    callbacks: DivPatchCallbacks = Callbacks.empty
  ) -> DivData {
    applyPatch((change.0, [change.1]), callbacks: callbacks)
  }
}
