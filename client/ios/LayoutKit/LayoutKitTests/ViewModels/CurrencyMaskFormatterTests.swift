@testable import LayoutKit
import Testing
import VGSL
import XCTest

@Suite
struct CurrencyMaskFormatterTests {
  @Test(
    arguments: [
      ("", "", ""),
      ("1", "1", "1"),
      ("12", "12", "12"),
      ("1234", "1,234", "1234"),
      ("1234.0", "1,234.0", "1234.0"),
      ("1234.06", "1,234.06", "1234.06"),
      ("1234.", "1,234.", "1234."),
      ("0.33", "0.33", "0.33"),
    ]
  )
  func rawTextFormattingUsLocal(
    rawText: String, expectedText: String, expectedRawText: String
  ) {
    let formatter = makeFormatter()
    let result = formatter.formatted(rawText: rawText, rawCursorPosition: nil)

    #expect(result.text == expectedText)
    #expect(result.rawText == expectedRawText)
  }

  @Test(
    arguments: [
      "e4",
      "053",
      ".95",
      "345.0543",
      "345.05.43"
    ]
  )
  func rawTextWrongFormattingUsLocal(
    rawText: String
  ) {
    let formatter = makeFormatter()

    let result = formatter.formatted(rawText: rawText, rawCursorPosition: nil)

    #expect(result.rawText == "")
  }

  @Test(
    arguments: [
      ("", "", ""),
      ("1", "1", "1"),
      ("12", "12", "12"),
      ("1234", "1 234", "1234"),
      ("1234,0", "1 234,0", "1234,0"),
      ("1234,06", "1 234,06", "1234,06"),
      ("1234,", "1 234,", "1234,"),
      ("0,33", "0,33", "0,33")
    ]
  )
  func rawTextFormattingRusLocal(
    rawText: String, expectedText: String, expectedRawText: String
  ) {
    let formatter = makeFormatter(localIdentifier: "ru_RU")
    let result = formatter.formatted(rawText: rawText, rawCursorPosition: nil)

    #expect(result.text == expectedText)
    #expect(result.rawText == expectedRawText)
  }

  @Test
  func invalidInputReturnsLastCorrectValue() {
    let formatter = makeFormatter()

    let first = formatter.formatted(rawText: "1234", rawCursorPosition: nil)
    #expect(first.text == "1,234")

    let second = formatter.formatted(rawText: "12aa34", rawCursorPosition: nil)

    #expect(second.text == "1,234")
    #expect(second.rawText == "1234")
  }

  @Test
  func rawData() {
    let formatter = makeFormatter()
    let rawText = "1234.5"
    let text = "1 234.5"

    let data = formatter.formatted(rawText: rawText, rawCursorPosition: nil).rawData

    let expected: [InputData.RawCharacter] = [
      .init(char: "1", index: text.index(text.startIndex, offsetBy: 0)),
      .init(char: "2", index: text.index(text.startIndex, offsetBy: 2)),
      .init(char: "3", index: text.index(text.startIndex, offsetBy: 3)),
      .init(char: "4", index: text.index(text.startIndex, offsetBy: 4)),
      .init(char: ".", index: text.index(text.startIndex, offsetBy: 5)),
      .init(char: "5", index: text.index(text.startIndex, offsetBy: 6)),
    ]

    #expect(data == expected)
  }

  @Test(
    arguments: [
      (0, 0),
      (1, 2),
      (3, 4),
      (5, 7)
    ]
  )
  func cursorPositionWithoutFormattingSymbols(
    rawCursor: Int, expectedCursor: Int
  ) {
    let formatter = makeFormatter()
    let rawText = "1234567"

    let result = formatter.formatted(
      rawText: rawText,
      rawCursorPosition: .init(rawCursor, false)
    )

    let expected: CursorPosition = .init(integerLiteral: expectedCursor)

    #expect(result.cursorPosition == expected)
  }

  @Test
  func cursorPositionAtEnd() {
    let formatter = makeFormatter()
    let rawText = "1234.56"

    let result = formatter.formatted(
      rawText: rawText,
      rawCursorPosition: .init(rawText.count, false)
    )

    #expect(result.cursorPosition?.rawValue == result.text.endIndex)
  }

  @Test
  func cursorPositionAfterDecimalSeparator() {
    let formatter = makeFormatter()
    let rawText = "1234.5"

    let result = formatter.formatted(
      rawText: rawText,
      rawCursorPosition: .init(5, false)
    )

    #expect(result.cursorPosition == 6)
  }
}

private func makeFormatter(
  localIdentifier: String = "en_US"
) -> CurrencyMaskFormatter {
  CurrencyMaskFormatter(locale: localIdentifier)
}
