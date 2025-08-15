@testable import LayoutKit
import XCTest

final class FixedLengthMaskFormatterTests: XCTestCase {
  func test_rawTextFormatting() {
    let formatter = makeFormatter(alwaysVisible: false)
    formatter.formatted(rawText: "").checkInputData(text: "+7 (", rawText: "")
    formatter.formatted(rawText: "503").checkInputData(text: "+7 (503) ", rawText: "503")
    formatter.formatted(rawText: "503111111")
      .checkInputData(text: "+7 (503) 111-11-1", rawText: "503111111")
    formatter.formatted(rawText: "q5a0b3c1d1e1f1g1h1j1")
      .checkInputData(text: "+7 (503) 111-11-11", rawText: "5031111111")
  }

  func test_rawTextFormattingWithAlwaysVisible() {
    let formatter = makeFormatter(alwaysVisible: true)
    formatter.formatted(rawText: "").checkInputData(text: "+7 (___) ___-__-__", rawText: "")
    formatter.formatted(rawText: "503")
      .checkInputData(text: "+7 (503) ___-__-__", rawText: "503")
    formatter.formatted(rawText: "503111111")
      .checkInputData(text: "+7 (503) 111-11-1_", rawText: "503111111")
    formatter.formatted(rawText: "q5a0b3c1d1e1f1g1h1j1")
      .checkInputData(text: "+7 (503) 111-11-11", rawText: "5031111111")
  }

  func test_inputData() {
    let formatter = makeFormatter(alwaysVisible: false)
    let rawText = "1234"
    let text = "+7 (123) 4"
    let data = formatter.formatted(rawText: rawText).rawData
    // "+7 (123) 4"
    // "0123456789"
    let rawData: [InputData.RawCharacter] = [
      .init(char: "1", index: text.index(text.startIndex, offsetBy: 4)),
      .init(char: "2", index: text.index(text.startIndex, offsetBy: 5)),
      .init(char: "3", index: text.index(text.startIndex, offsetBy: 6)),
      .init(char: "4", index: text.index(text.startIndex, offsetBy: 9)),
    ]
    XCTAssertEqual(data, rawData)
  }

  func test_cursorPosition() {
    let formatter = makeFormatter(alwaysVisible: false)
    let rawText = "1234"
    // "0123456789"
    // "+7 (123) 4"
    // "    012  3"
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(2, false)).cursorPosition,
      6
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(3, false)).cursorPosition,
      7
    )

    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(3, true)).cursorPosition,
      9
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(4, true)).cursorPosition,
      10
    )
  }
}

private func makeFormatter(alwaysVisible: Bool) -> FixedLengthMaskFormatter {
  FixedLengthMaskFormatter(
    pattern: "+7 (###) ###-##-##",
    alwaysVisible: alwaysVisible,
    patternElements: [PatternElement(
      key: "#",
      regex: try! NSRegularExpression(pattern: "\\d"),
      placeholder: "_"
    )]
  )
}

extension InputData {
  fileprivate func checkInputData(text: String, rawText: String) {
    XCTAssertEqual(self.text, text)
    XCTAssertEqual(self.rawText, rawText)
  }
}
