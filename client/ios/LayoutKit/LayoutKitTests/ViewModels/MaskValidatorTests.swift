import XCTest

import LayoutKit

final class MaskValidatorTests: XCTestCase {
  func test_rawTextFormatting() {
    let phoneValidator = makeMaskValidator(alwaysVisible: false)
    phoneValidator.formatted(rawText: "").checkInputData(text: "+7 (", rawText: "")
    phoneValidator.formatted(rawText: "503").checkInputData(text: "+7 (503) ", rawText: "503")
    phoneValidator.formatted(rawText: "503111111")
      .checkInputData(text: "+7 (503) 111-11-1", rawText: "503111111")
    phoneValidator.formatted(rawText: "q5a0b3c1d1e1f1g1h1j1")
      .checkInputData(text: "+7 (503) 111-11-11", rawText: "5031111111")
  }

  func test_rawTextFormattingWithAlwaysVisible() {
    let phoneValidator = makeMaskValidator(alwaysVisible: true)
    phoneValidator.formatted(rawText: "").checkInputData(text: "+7 (___) ___-__-__", rawText: "")
    phoneValidator.formatted(rawText: "503")
      .checkInputData(text: "+7 (503) ___-__-__", rawText: "503")
    phoneValidator.formatted(rawText: "503111111")
      .checkInputData(text: "+7 (503) 111-11-1_", rawText: "503111111")
    phoneValidator.formatted(rawText: "q5a0b3c1d1e1f1g1h1j1")
      .checkInputData(text: "+7 (503) 111-11-11", rawText: "5031111111")
  }

  func test_removingSymbol() {
    let phoneValidator = makeMaskValidator(alwaysVisible: false)
    let inputData = phoneValidator.formatted(rawText: "9051234567")

    // "+7 (905) 123-45-67"

    XCTAssertEqual(phoneValidator.removeSymbols(at: 0, data: inputData).0, "9051234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 5, data: inputData).0, "051234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 10, data: inputData).0, "905234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 1, data: inputData).0, "9051234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 18, data: inputData).0, "905123456")
  }

  func test_removingSymbolRange() {
    let phoneValidator = makeMaskValidator(alwaysVisible: false)
    let inputData = phoneValidator.formatted(rawText: "9051234567")

    // "+7 (905) 123-45-67"

    XCTAssertEqual(phoneValidator.removeSymbols(at: 0..<3, data: inputData).0, "9051234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 0..<5, data: inputData).0, "051234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 2..<7, data: inputData).0, "1234567")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 0..<18, data: inputData).0, "")
    XCTAssertEqual(phoneValidator.removeSymbols(at: 15..<16, data: inputData).0, "9051234567")
  }

  func test_addSymbolsAtPosition() {
    let phoneValidator = makeMaskValidator(alwaysVisible: false)
    var inputData = phoneValidator.formatted(rawText: "9051234567")

    // "+7 (905) 123-45-67"

    XCTAssertEqual(phoneValidator.addSymbols(at: 0, data: inputData, string: "1").0, "1905123456")
    XCTAssertEqual(
      phoneValidator.addSymbols(at: 0, data: inputData, string: "1234567890").0,
      "1234567890"
    )
    XCTAssertEqual(phoneValidator.addSymbols(at: 5, data: inputData, string: "1").0, "9105123456")

    inputData = phoneValidator.formatted(rawText: "905")
    // "+7 (905) "
    XCTAssertEqual(phoneValidator.addSymbols(at: 7, data: inputData, string: "1").0, "9051")
    XCTAssertEqual(phoneValidator.addSymbols(at: 9, data: inputData, string: "1").0, "9051")
  }
}

private func makeMaskValidator(alwaysVisible: Bool) -> MaskValidator {
  MaskValidator(
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
