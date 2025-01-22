@testable import LayoutKit
import XCTest

final class MaskValidatorTests: XCTestCase {
  func test_removingSymbol() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")
    // "0123456789012345678"
    // "   ab cde f g hi j "

    XCTAssertEqual(validator.removeSymbols(at: 0, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 5, data: inputData).0, "acdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 6, data: inputData).0, "abdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 8, data: inputData).0, "abcdfghij")
    XCTAssertEqual(validator.removeSymbols(at: 10, data: inputData).0, "abcdeghij")
    XCTAssertEqual(validator.removeSymbols(at: 1, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 18, data: inputData).0, "abcdefghi")
  }

  func test_removingSymbolRange() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")
    // "0123456789012345678"
    // "   ab cde f g hi j "

    XCTAssertEqual(validator.removeSymbols(at: 7..<11, data: inputData).0, "abcghij")
    XCTAssertEqual(validator.removeSymbols(at: 7..<10, data: inputData).0, "abcfghij")
    XCTAssertEqual(validator.removeSymbols(at: 8..<10, data: inputData).0, "abcdfghij")
    XCTAssertEqual(validator.removeSymbols(at: 0..<3, data: inputData).0, "abcdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 0..<5, data: inputData).0, "cdefghij")
    XCTAssertEqual(validator.removeSymbols(at: 2..<7, data: inputData).0, "defghij")
    XCTAssertEqual(validator.removeSymbols(at: 0..<18, data: inputData).0, "")
    XCTAssertEqual(validator.removeSymbols(at: 15..<16, data: inputData).0, "abcdefghj")
  }

  func test_cursorAfterRemovingSymbol() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")
    // "0123456789012345678"
    // "   ab cde f g hi j "
    // "   01 234 5 6 78 9 "

    XCTAssertEqual(validator.removeSymbols(at: 2, data: inputData).1?.cursorPosition.rawValue, 0)
    XCTAssertEqual(validator.removeSymbols(at: 3, data: inputData).1?.cursorPosition.rawValue, 0)
    XCTAssertEqual(validator.removeSymbols(at: 4, data: inputData).1?.cursorPosition.rawValue, 1)
    XCTAssertEqual(validator.removeSymbols(at: 5, data: inputData).1?.cursorPosition.rawValue, 1)
    XCTAssertEqual(validator.removeSymbols(at: 7, data: inputData).1?.cursorPosition.rawValue, 3)
    XCTAssertEqual(validator.removeSymbols(at: 14, data: inputData).1?.cursorPosition.rawValue, 7)
    XCTAssertEqual(validator.removeSymbols(at: 18, data: inputData).1?.cursorPosition.rawValue, 9)
  }

  func test_cursorAfterRemovingSymbolRange() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")
    // "0123456789012345678"
    // "   ab cde f g hi j "
    // "   01 234 5 6 78 9 "

    XCTAssertEqual(
      validator.removeSymbols(at: 7..<11, data: inputData).1?.cursorPosition.rawValue,
      3
    )
    XCTAssertEqual(
      validator.removeSymbols(at: 8..<10, data: inputData).1?.cursorPosition.rawValue,
      4
    )
    XCTAssertEqual(
      validator.removeSymbols(at: 0..<3, data: inputData).1?.cursorPosition.rawValue,
      0
    )
    XCTAssertEqual(
      validator.removeSymbols(at: 2..<7, data: inputData).1?.cursorPosition.rawValue,
      0
    )
    XCTAssertEqual(
      validator.removeSymbols(at: 15..<16, data: inputData).1?.cursorPosition.rawValue,
      8
    )
  }

  func test_addSymbols() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")
    // "0123456789012345678"
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
      "123abcdefghij"
    )
    XCTAssertEqual(
      validator.addSymbols(at: 3..<5, data: inputData, string: "12").0,
      "12cdefghij"
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

  func test_cursorAfterAddSymbols() {
    let validator = makeMaskValidator()
    let inputData = validator.formatted(rawText: "")

    // "0123456789012345678"
    // "   ab cde f g hi j "
    // "   01 234 5 6 78 9 "

    XCTAssertEqual(
      validator.addSymbols(at: 0..<1, data: inputData, string: "1").1?.cursorPosition.rawValue,
      1
    )
    XCTAssertEqual(
      validator.addSymbols(at: 5..<5, data: inputData, string: "1").1?.cursorPosition.rawValue,
      3
    )
    XCTAssertEqual(
      validator.addSymbols(at: 3..<5, data: inputData, string: "12").1?.cursorPosition.rawValue,
      2
    )
    XCTAssertEqual(
      validator.addSymbols(at: 3..<5, data: inputData, string: "123").1?.cursorPosition.rawValue,
      3
    )
    XCTAssertEqual(
      validator.addSymbols(at: 3..<5, data: inputData, string: "12345").1?.cursorPosition.rawValue,
      5
    )
  }
}

private func makeMaskValidator() -> MaskValidator {
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

  func formatted(rawText _: String, rawCursorPosition: CursorData?) -> InputData {
    var text = String(repeating: " ", count: Self.length)
    var rawData = [InputData.RawCharacter]()
    for data in Self.data {
      let indexInNewString = text.index(text.startIndex, offsetBy: data.indexInFormatted)
      text.replaceSubrange(indexInNewString...indexInNewString, with: [data.ch])
      rawData.append(.init(char: data.ch, index: indexInNewString))
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

extension MaskValidator {
  fileprivate func removeSymbols(at pos: Int, data: InputData) -> (String, CursorData?) {
    removeSymbols(at: String.Index(integerLiteral: pos), data: data)
  }

  fileprivate func removeSymbols(at range: Range<Int>, data: InputData) -> (String, CursorData?) {
    removeSymbols(at: range.indexRange, data: data)
  }

  fileprivate func addSymbols(
    at range: Range<Int>,
    data: InputData,
    string: String
  ) -> (String, CursorData?) {
    addSymbols(at: range.indexRange, data: data, string: string)
  }
}

extension String.Index: Swift.ExpressibleByIntegerLiteral {
  public init(integerLiteral value: IntegerLiteralType) {
    self.init(utf16Offset: value, in: String(repeating: " ", count: value))
  }
}

extension Range<Int> {
  fileprivate var indexRange: Range<String.Index> {
    String.Index(integerLiteral: lowerBound)..<String.Index(integerLiteral: upperBound)
  }
}
