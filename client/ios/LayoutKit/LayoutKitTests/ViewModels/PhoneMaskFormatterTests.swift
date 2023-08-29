import XCTest

import BaseTinyPublic
@testable import LayoutKit

final class PhoneMaskFormatterTests: XCTestCase {
  func test_rawTextFormatting() {
    let formatter = makeFormatter(alwaysVisible: false)

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
}

private func makeFormatter(alwaysVisible: Bool) -> PhoneMaskFormatter {
  PhoneMaskFormatter(
    masksByCountryCode: masksByCountryCode,
    extraSymbols: "00"
  )
}

private let masksByCountryCode: JSONDictionary = [
  "1": .object([
    "0": .object([ "value": .string("+00 (000) 0000000") ]),
    "1": .object([
      "0": .object([ "value": .string("+000 000 000-000") ]),
      "*": .object([ "value": .string("+000 (00) 000-00-00") ]),
    ]),
    "*": .object([ "value": .string("+00 (000) 000-00-00") ]),
  ]),
  "*": .object([ "value": .string("+0 (000) 000-00-00") ]),
]

extension InputData {
  fileprivate func checkInputData(text: String, rawText: String) {
    XCTAssertEqual(self.text, text)
    XCTAssertEqual(self.rawText, rawText)
  }
}
