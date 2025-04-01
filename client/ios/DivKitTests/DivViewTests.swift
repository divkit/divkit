@testable @_spi(Internal) import DivKit
import LayoutKit
import Serialization
import VGSL
import XCTest

final class DivViewTests: XCTestCase {
  lazy var components = DivKitComponents()
  lazy var divView = DivView(divKitComponents: components)

  @MainActor
  func test_visibilityActions_afterZeroFrame() async {
    await divView.setData(appearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()

    XCTAssertEqual(components.visibilityCounter.visibilityCount(for: appearPath), 2)
  }

  @MainActor
  func test_disappearActions_afterZeroFrame() async {
    await divView.setData(disappearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()
    divView.disappear()

    XCTAssertEqual(components.visibilityCounter.visibilityCount(for: disappearPath), 2)
  }
}

extension DivView {
  fileprivate func setData(_ data: DivData) async {
    await setSource(.init(kind: .divData(data), cardId: "card"))
    frame = testFrame
  }

  fileprivate func appear() {
    onVisibleBoundsChanged(to: testFrame)
    forceLayout()
    delay()
  }

  fileprivate func disappear() {
    onVisibleBoundsChanged(to: .zero)
    forceLayout()
    delay()
  }
}

private func delay() {
  RunLoop.current.run(until: Date().addingTimeInterval(0.01))
}

private let testFrame = CGRect(x: 0, y: 0, width: 100, height: 100)
private let appearPath = UIElementPath("card") + "0" + "text" + "appear" + "appear_action"
private let disappearPath = UIElementPath("card") + "0" + "text" + "disappear" + "disappear_action"
private let appearTestData = divData(
  divText(
    text: "Sample",
    width: .divFixedSize(DivFixedSize(value: .value(100))),
    visibilityActions: [
      DivVisibilityAction(
        logId: .value("appear_action"),
        logLimit: .value(10),
        visibilityDuration: .value(0)
      ),
    ]
  )
)

private let disappearTestData = divData(
  divText(
    disappearActions: [
      DivDisappearAction(
        disappearDuration: .value(0),
        logId: .value("disappear_action"),
        logLimit: .value(10),
        visibilityPercentage: .value(0)
      ),
    ],
    text: "Sample",
    width: .divFixedSize(DivFixedSize(value: .value(100)))
  )
)
