import XCTest

@testable import LayoutKit
import VGSL

final class PhoneMaskFormatterTests: XCTestCase {
  func test_rawTextFormatting() {
    let formatter = makeFormatter()

    formatter.formatted(rawText: "").checkInputData(text: "", rawText: "")
    formatter.formatted(rawText: "101").checkInputData(text: "+10 (1", rawText: "101")
    formatter.formatted(rawText: "1102").checkInputData(text: "+110 2", rawText: "1102")
    formatter.formatted(rawText: "1112").checkInputData(text: "+111 (2", rawText: "1112")
    formatter.formatted(rawText: "12").checkInputData(text: "+12 (", rawText: "12")
    formatter.formatted(rawText: "20").checkInputData(text: "+2 (0", rawText: "20")
    formatter.formatted(rawText: "1").checkInputData(text: "+1", rawText: "1")

    formatter.formatted(rawText: "123456789012")
      .checkInputData(text: "+12 (345) 678-90-12", rawText: "123456789012")
    formatter.formatted(rawText: "12345678901234")
      .checkInputData(text: "+12 (345) 678-90-1234", rawText: "12345678901234")

    formatter.formatted(rawText: "q1q2q3q4q5")
      .checkInputData(text: "+1 (234) 5", rawText: "12345")
  }

  func test_inputData() {
    let formatter = makeFormatter()
    let rawText = "12345"
    let text = "+12 (345)"
    let data = formatter.formatted(rawText: rawText).rawData
    // "+12 (345)"
    // "012345678"
    let rawData: [InputData.RawCharacter] = [
      .init(char: "1", index: text.index(text.startIndex, offsetBy: 1)),
      .init(char: "2", index: text.index(text.startIndex, offsetBy: 2)),
      .init(char: "3", index: text.index(text.startIndex, offsetBy: 5)),
      .init(char: "4", index: text.index(text.startIndex, offsetBy: 6)),
      .init(char: "5", index: text.index(text.startIndex, offsetBy: 7)),
    ]
    XCTAssertEqual(data, rawData)
  }

  func test_cursorPosition() {
    let formatter = makeFormatter()
    let rawText = "123456"
    // "01234567890"
    // "+12 (345) 6"
    // " 01  234  5"
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(0, false)).cursorPosition,
      1
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(1, false)).cursorPosition,
      2
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(2, false)).cursorPosition,
      3
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(4, false)).cursorPosition,
      7
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(5, false)).cursorPosition,
      8
    )

    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(2, true)).cursorPosition,
      5
    )
    XCTAssertEqual(
      formatter.formatted(rawText: rawText, rawCursorPosition: .init(6, true)).cursorPosition,
      11
    )
  }
}

private func makeFormatter() -> PhoneMaskFormatter {
  PhoneMaskFormatter(
    masksByCountryCode: masksByCountryCode,
    extraSymbols: "00"
  )
}

private let masksByCountryCode: JSONDictionary = [
  "1": .object([
    "0": .object(["value": .string("+00 (000) 0000000")]),
    "1": .object([
      "0": .object(["value": .string("+000 000 000-000")]),
      "*": .object(["value": .string("+000 (00) 000-00-00")]),
    ]),
    "*": .object(["value": .string("+00 (000) 000-00-00")]),
  ]),
  "*": .object(["value": .string("+0 (000) 000-00-00")]),
]

extension InputData {
  fileprivate func checkInputData(text: String, rawText: String) {
    XCTAssertEqual(self.text, text)
    XCTAssertEqual(self.rawText, rawText)
  }
}

extension CursorData {
  init(_ pos: Int, _ afterNonDecodingSymbols: Bool) {
    self.init(
      cursorPosition: .init(rawValue: String.Index(integerLiteral: pos)),
      afterNonDecodingSymbols: afterNonDecodingSymbols
    )
  }
}
