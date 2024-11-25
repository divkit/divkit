@testable import DivKit
@testable import LayoutKit

import VGSL
import XCTest

private final class DivBaseExtensionsAndAccessibilityElementsStorageTests: XCTestCase {
  func test_OrderedIds_WhenForwardIdExist() {
    let testData = divData(
      divGallery(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    assertEqual(
      getOrderedIds(divData: testData),
      ["firstId"]
    )
  }

  func test_OrderedIds_WhenForwardIdNotExist() {
    let testData = divData(
      divGallery(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
        ]
      )
    )

    assertEqual(
      getOrderedIds(divData: testData),
      ["firstId", "secondId"]
    )
  }

  func test_OrderedIds_CycleOfTwoElements() {
    let testData = divData(
      divGallery(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
          createDivTextWithNextFocusId(
            id: "secondId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    assertEqual(
      getOrderedIds(divData: testData),
      ["firstId", "secondId"]
    )
  }

  func test_AccessibilityElemenents_WhenForwardIdExist() async {
    let testData = divData(
      divGallery(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId"]
    )
  }

  func test_AccessibilityElemenents_WhenForwardIdNotExist() async {
    let testData = divData(
      divGallery(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId"]
    )
  }

  func test_AccessibilityElemenents_CycleOfTwoElements() async {
    let testData = divData(
      divContainer(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
          createDivTextWithNextFocusId(
            id: "secondId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId", "secondId"]
    )
  }

  func test_AccessibilityElemenents_SecondElementDivVisibilityVisible() async {
    let testData = divData(
      divContainer(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
          divText(
            focus: DivFocus(
              nextFocusIds: DivFocus.NextFocusIds(
                forward: .value("thirdId")
              )
            ),
            id: "secondId",
            width: fixedSize(10),
            visibility: .value(.visible)
          ),
          createDivTextWithNextFocusId(
            id: "thirdId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId", "secondId", "thirdId"]
    )
  }

  func test_AccessibilityElemenents_SecondElementDivVisibilityGone() async {
    let testData = divData(
      divContainer(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
          divText(
            focus: DivFocus(
              nextFocusIds: DivFocus.NextFocusIds(
                forward: .value("thirdId")
              )
            ),
            id: "secondId",
            width: fixedSize(10),
            visibility: .value(.gone)
          ),
          createDivTextWithNextFocusId(
            id: "thirdId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId", "thirdId"]
    )
  }

  func test_AccessibilityElemenents_DivVisibilityInvisible() async {
    let testData = divData(
      divContainer(
        items: [
          createDivTextWithNextFocusId(
            id: "firstId",
            forwardId: "secondId"
          ),
          divText(
            focus: DivFocus(
              nextFocusIds: DivFocus.NextFocusIds(
                forward: .value("thirdId")
              )
            ),
            id: "secondId",
            width: fixedSize(10),
            visibility: .value(.invisible)
          ),
          createDivTextWithNextFocusId(
            id: "thirdId",
            forwardId: "firstId"
          ),
        ]
      )
    )

    await assertEqual(
      getAccessibilityIds(divData: testData),
      ["firstId", "thirdId"]
    )
  }

  private func createDivTextWithNextFocusId(id: String, forwardId: String) -> Div {
    divText(
      focus: DivFocus(
        nextFocusIds: DivFocus.NextFocusIds(
          forward: .value(forwardId)
        )
      ),
      id: id,
      width: fixedSize(10)
    )
  }

  private func getOrderedIds(divData: DivData) -> [String]? {
    let context = DivBlockModelingContext()
    _ = try! divData.makeBlock(context: context)

    return context.accessibilityElementsStorage.getOrderedIds()
  }

  @MainActor
  private func getAccessibilityIds(divData: DivData) async -> [String]? {
    let divView = DivView(divKitComponents: DivKitComponents())
    await divView.setSource(.init(kind: .divData(divData), cardId: "card"))
    divView.frame = CGRect(x: 0, y: 0, width: 100, height: 100)
    divView.forceLayout()

    guard let elements = divView.accessibilityElements else {
      XCTFail("accessibilityElements are not available or have the wrong type")
      return nil
    }

    let ids = elements.compactMap { element -> String? in
      if let element = element as? UIView {
        return element.accessibilityIdentifier
      }
      return nil
    }

    return ids
  }
}
