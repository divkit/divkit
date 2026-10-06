@testable @_spi(Internal) import DivKit
import DivKitTestsSupport
import LayoutKit
import Testing
import UIKit

@MainActor
@Suite
struct DivViewTests {
  private let components = DivKitComponents()
  private let divView: DivView

  init() {
    divView = DivView(divKitComponents: components)
  }

  @Test
  func tooltipContentSize_matchesBlockSizing() async throws {
    let constraint = CGSize(width: 240, height: 320)
    let divs = [
      divContainer(
        height: .divFixedSize(DivFixedSize(value: .value(40))),
        width: .divFixedSize(DivFixedSize(value: .value(80)))
      ),
      divContainer(
        height: .divFixedSize(DivFixedSize(value: .value(40))),
        width: .divMatchParentSize(DivMatchParentSize())
      ),
      divText(
        text: "Tooltip",
        width: .divWrapContentSize(DivWrapContentSize()),
        height: .divWrapContentSize(DivWrapContentSize())
      ),
    ]

    for (index, div) in divs.enumerated() {
      let cardId = DivCardID(rawValue: "tooltip_size_\(index)")
      let data = divData(div)
      let block = try data.makeBlock(
        context: components.makeContext(cardId: cardId, cachedImageHolders: [])
      )
      let view = DivView(divKitComponents: components)
      await view.setSource(.init(kind: .divData(data), cardId: cardId))

      #expect(
        view.tooltipContentSize(constrainedBy: constraint, useLegacyWidth: true)
          == block.intrinsicSize
      )
      #expect(
        view.tooltipContentSize(constrainedBy: constraint, useLegacyWidth: false)
          == block.size(forResizableBlockSize: constraint)
      )
    }
  }

  @Test
  func visibilityActions_afterZeroFrame() async {
    await divView.setData(appearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()

    let delays = divView.visibilityHierarchyDepth()
    await skipMainRunLoopCycles(delays)

    #expect(components.visibilityCounter.visibilityCount(for: appearPath) == 2)
  }

  @Test
  func disappearActions_afterZeroFrame() async {
    await divView.setData(disappearTestData)

    divView.appear()
    divView.disappear()
    divView.appear()
    divView.disappear()

    let delays = divView.visibilityHierarchyDepth()
    await skipMainRunLoopCycles(delays)

    #expect(components.visibilityCounter.visibilityCount(for: disappearPath) == 2)
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
