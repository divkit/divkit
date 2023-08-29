import XCTest

@testable import LayoutKit

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
