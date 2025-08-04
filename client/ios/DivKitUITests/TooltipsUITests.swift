import XCTest

final class TooltipsUITests: XCTestCase {
  private enum TooltipTest {
    case tooltipPosition, closeOnSwitchOrientation
  }

  private enum Position: CaseIterable {
    case bottom, top, right
  }

  private let app = XCUIApplication()
  private lazy var baseDivView: XCUIElement = {
    let mainWindow = app.windows.element(boundBy: 0)
    return mainWindow.descendants(matching: .any).matching(identifier: "baseDivView").element
  }()

  private lazy var tooltip: XCUIElement = app.staticTexts["Title tooltip"]

  private lazy var toggleTooltipButton: XCUIElement = elementsQuery
    .staticTexts["tooltip with close button"]

  private lazy var closeTooltipButton: XCUIElement = app.buttons["close tooltip"]

  private var elementsQuery: XCUIElementTypeQueryProvider {
    app.scrollViews.otherElements
  }

  override func setUpWithError() throws {
    try super.setUpWithError()

    continueAfterFailure = false

    app.launchArguments.append("UITestsMode")
    app.launch()
  }

  func testTooltipPosition() throws {
    goTo(test: .tooltipPosition)

    for position in Position.allCases {
      checkTooltip(for: position)
    }
  }

  func testTooltipCloseOnSwitchOrientation() throws {
    let device = XCUIDevice.shared
    device.orientation = .portrait

    goTo(test: .closeOnSwitchOrientation)

    setPosition(.bottom)
    toggleTooltipButton.tap()
    XCTAssertTrue(tooltip.exists)

    device.orientation = .landscapeRight
    XCTAssertTrue(!tooltip.exists) // tooltip closed

    toggleTooltipButton.tap()
    device.orientation = .portrait
    XCTAssertTrue(!tooltip.exists) // tooltip closed again
  }

  private func checkTooltip(for position: Position) {
    setPosition(position)

    toggleTooltipButton.tap() // Open tooltip
    XCTAssertTrue(tooltip.exists)

    closeTooltipButton.tap() // Close tooltip
    XCTAssertTrue(!tooltip.exists)
  }

  private func setPosition(_ position: Position) {
    switch position {
    case .bottom:
      let setTooltipBottomPos = elementsQuery.staticTexts["Restore bottom position"]
      setTooltipBottomPos.tap()
    case .top:
      let setTooltipTopPos = elementsQuery.staticTexts["Set top position"]
      setTooltipTopPos.tap()
    case .right:
      let setTooltipRightPos = elementsQuery.staticTexts["Set right position"]
      setTooltipRightPos.tap()
    }
  }

  private func goTo(test: TooltipTest) {
    switch test {
    case .tooltipPosition, .closeOnSwitchOrientation:
      app.buttons["testing"].tap()
      app.scrollViews.otherElements.staticTexts["Tooltip on different positions"].tap()
    }
  }

}
