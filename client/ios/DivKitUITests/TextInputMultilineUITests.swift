import XCTest

final class TextInputMultilineUITests: XCTestCase {
  private let app = XCUIApplication()

  private lazy var input: XCUIElement = app.otherElements["input_without_actions"]
  private lazy var mirror: XCUIElement = app.staticTexts["mirror_without_actions"]
  private lazy var keyboard: XCUIElement = app.keyboards.firstMatch

  override func setUpWithError() throws {
    try super.setUpWithError()

    continueAfterFailure = false

    app.launchArguments.append("UITestsMode")
    app.launch()
    app.open(URL(string: "playground://test?title=Text%20input%20(multiline)")!)
  }

  func testCaretStaysWhereUserPlacedIt() throws {
    XCTAssertTrue(input.waitForExistence(timeout: 5))

    tap(at: CGVector(dx: input.frame.midX, dy: input.frame.midY))
    XCTAssertTrue(keyboard.waitForExistence(timeout: 3), "keyboard did not appear")

    type("aaa")
    tapReturn()
    type("bbb")
    XCTAssertEqual(mirror.label, "Text: Aaa\nBbb", "seed")

    tap(at: CGVector(
      dx: input.frame.maxX - 20,
      dy: input.frame.minY + input.frame.height / 4
    ))
    tapReturn()
    type("mid")

    XCTAssertEqual(mirror.label, "Text: Aaa\nMid\nBbb")
  }

  private func tap(at offset: CGVector) {
    app.coordinate(withNormalizedOffset: .zero).withOffset(offset).tap()
  }

  private func type(_ text: String) {
    for character in text {
      let key = String(character)
      for candidate in [key, key.uppercased()] {
        if app.keyboards.keys[candidate].exists {
          app.keyboards.keys[candidate].tap()
          break
        }
      }
    }
  }

  private func tapReturn() {
    for candidate in ["return", "Return", "Search", "Go"] {
      if app.keyboards.buttons[candidate].exists {
        app.keyboards.buttons[candidate].tap()
        return
      }
    }
    XCTFail("no return-like key on the visible keyboard")
  }
}
