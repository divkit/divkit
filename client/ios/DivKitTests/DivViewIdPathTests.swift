@testable import DivKit
import LayoutKit
import Testing

@Suite
struct DivViewIdPathTests {
  @Test
  func hostElementPath_isHostView() {
    let path = UIElementPath("card") + "0" + "container" + "button"

    #expect(path.viewId == DivViewId(cardId: "card"))
  }

  @Test
  func tooltipElementPath_roundTripsThroughFactoryPath() {
    let anchorPath = UIElementPath("card") + "0" + "anchor"
    let viewId = DivViewId(
      cardId: "card",
      tooltip: DivViewId.Tooltip(id: "hint", anchorPath: anchorPath)
    )

    #expect((viewId.path + "0" + "button").viewId == viewId)
    #expect(viewId.path.viewId == viewId)
  }

  @Test
  func tooltipSubstrateRoot_keepsSuffixInId() {
    let anchorPath = UIElementPath("card") + "0" + "anchor"
    let path = anchorPath + "\(DivViewId.tooltipMarker)hint_substrate"

    #expect(
      path.viewId == DivViewId(
        cardId: "card",
        tooltip: DivViewId.Tooltip(id: "hint_substrate", anchorPath: anchorPath)
      )
    )
  }

  @Test
  func numericTooltipId_isTooltipRatherThanHost() {
    let anchorPath = UIElementPath("card") + "anchor"
    let path = anchorPath + "\(DivViewId.tooltipMarker)0"

    #expect(
      path.viewId == DivViewId(
        cardId: "card",
        tooltip: DivViewId.Tooltip(id: "0", anchorPath: anchorPath)
      )
    )
  }

  @Test
  func cardRoot_isHostView() {
    #expect(UIElementPath("card").viewId == DivViewId(cardId: "card"))
  }
}
