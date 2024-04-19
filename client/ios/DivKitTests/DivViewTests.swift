import XCTest

import CommonCorePublic
@testable import DivKit
import LayoutKit
import NetworkingPublic
import Serialization
import VGSL_Fundamentals_Tiny

final class DivViewTests: XCTestCase {
  func test_visibilityActions_afterZeroFrame() {
    let components = DivKitComponents()
    let divView = DivView(divKitComponents: components)
    divView.setSource(.init(kind: .divData(testData), cardId: "card"))
    divView.frame = testFrame

    divView.onVisibleBoundsChanged(to: testFrame)
    divView.forceLayout()
    delay()

    divView.onVisibleBoundsChanged(to: .zero)
    divView.forceLayout()
    delay()

    divView.onVisibleBoundsChanged(to: testFrame)
    divView.forceLayout()
    delay()

    XCTAssertEqual(components.visibilityCounter.visibilityCount(for: path), 2)
  }
}

private func delay() {
  RunLoop.current.run(until: Date().addingTimeInterval(0.01))
}

private let testFrame = CGRect(x: 0, y: 0, width: 100, height: 100)
private let path = UIElementPath("card") + "0" + "action"
private let testData = divData(
  divText(
    text: "Sample",
    width: .divFixedSize(DivFixedSize(value: .value(100))),
    visibilityActions: [
      DivVisibilityAction(
        logId: .value("action"),
        logLimit: .value(10),
        visibilityDuration: .value(0)
      ),
    ]
  )
)
