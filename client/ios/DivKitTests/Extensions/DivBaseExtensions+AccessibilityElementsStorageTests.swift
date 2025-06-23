@testable import DivKit
@testable import LayoutKit
import DivKitTestsSupport
import Testing
import UIKit
import VGSL

@Suite()
struct DivBaseExtensionsAndAccessibilityElementsStorageTests {
  @Test
  func getOrderedIds_WhenForwardIdExist() {
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

    #expect(getOrderedIds(divData: testData) == ["firstId"])
  }

  @Test
  func getOeredIds_WhenForwardIdNotExist() {
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

    #expect(getOrderedIds(divData: testData) == ["firstId", "secondId"])
  }

  @Test
  func getOrderedIds_CycleOfTwoElements() {
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

    #expect(getOrderedIds(divData: testData) == ["firstId", "secondId"])
  }

  @Test
  func getAccessibilityElemenents_WhenForwardIdExist() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId"])
  }

  @Test
  func getAccessibilityElemenents_WhenForwardIdNotExist() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId"])
  }

  @Test
  func getAccessibilityElemenents_CycleOfTwoElements() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId", "secondId"])
  }

  @Test
  func getAccessibilityElemenents_SecondElementDivVisibilityVisible() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId", "secondId", "thirdId"])
  }

  @Test
  func getAccessibilityElemenents_SecondElementDivVisibilityGone() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId", "thirdId"])
  }

  @Test
  func getAccessibilityElemenents_DivVisibilityInvisible() async {
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

    #expect(await getAccessibilityIds(divData: testData) == ["firstId", "thirdId"])
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
      Issue.record("accessibilityElements are not available or have the wrong type")
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
