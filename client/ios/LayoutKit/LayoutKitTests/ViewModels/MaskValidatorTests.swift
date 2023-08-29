import XCTest

@testable import LayoutKit

final class MaskValidatorTests: XCTestCase {
  func test_removingSymbol() {
    let validator = makeMaskValidator(alwaysVisible: false)
    let inputData = validator.formatted(rawText: "")

    // "   ab cde f g hi j "

    XCTAssertEqual(validator.removeSymbols(at: 0, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 5, data: inputData).0, "acdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 10, data: inputData).0, "abcdeghij")
    XCTAssertEqual(validator.removeSymbols(at: 1, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 18, data: inputData).0, "abcdefghi")
  }

  func test_removingSymbolRange() {
    let validator = makeMaskValidator(alwaysVisible: false)
    let inputData = validator.formatted(rawText: "")

    // "   ab cde f g hi j "

    XCTAssertEqual(validator.removeSymbols(at: 0..<3, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 0..<5, data: inputData).0, "cdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 2..<7, data: inputData).0, "defghij")
    XCTAssertEqual(validator.removeSymbols(at: 0..<18, data: inputData).0, "")
    XCTAssertEqual(validator.removeSymbols(at: 15..<16, data: inputData).0, "abcdefghj")
  }

  func test_addSymbolsAtPosition() {
    let validator = makeMaskValidator(alwaysVisible: false)
    let inputData = validator.formatted(rawText: "")

    // "   ab cde f g hi j "

    XCTAssertEqual(
      validator.addSymbols(at: 0..<1, data: inputData, string: "1").0,
      "1abcdefghij"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 5..<5, data: inputData, string: "1").0,
      "ab1cdefghij"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 0..<3, data: inputData, string: "123").0,
      "123bcdefghij"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 3..<5, data: inputData, string: "12").0,
      "a12cdefghij"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 18..<19, data: inputData, string: "1").0,
      "abcdefghij1"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 19..<20, data: inputData, string: "1").0,
      "abcdefghij1"
    )
  }
}

private func makeMaskValidator(alwaysVisible: Bool) -> MaskValidator {
  MaskValidator(formatter: FakeMaskFormatter())
}

extension InputData {
  fileprivate func checkInputData(text: String, rawText: String) {
    XCTAssertEqual(self.text, text)
    XCTAssertEqual(self.rawText, rawText)
  }
}

private final class FakeMaskFormatter: MaskFormatter {
  private static let length = 19
  private static let data: [(ch: Character, indexInFormatted: Int)] = [
    ("a", 3),
    ("b", 4),
    ("c", 6),
    ("d", 7),
    ("e", 8),
    ("f", 10),
    ("g", 12),
    ("h", 14),
    ("i", 15),
    ("j", 17),
  ]

  func formatted(rawText: String, rawCursorPosition: CursorData?) -> InputData {
    var text = String(repeating: " ", count: Self.length)
    var rawData = [InputData.RawCharacter]()
    Self.data.forEach {
      let indexInNewString = text.index(text.startIndex, offsetBy: $0.indexInFormatted)
      text.replaceSubrange(indexInNewString...indexInNewString, with: [$0.ch])
      rawData.append(.init(char: $0.ch, index: indexInNewString))
    }
    return InputData(
      text: text,
      cursorPosition: rawCursorPosition?.cursorPosition,
      rawData: rawData
    )
  }

  func equals(_ other: MaskFormatter) -> Bool {
    guard let _ = other as? FakeMaskFormatter else {
      return false
    }
    return true
  }
}
