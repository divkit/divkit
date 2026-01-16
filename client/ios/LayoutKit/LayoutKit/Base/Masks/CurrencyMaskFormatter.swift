import Foundation

public final class CurrencyMaskFormatter: MaskFormatter {
  private let locale: Locale
  private var lastCorrectInput = InputData(
    text: "", cursorPosition: nil, rawData: []
  )

  private lazy var formatter: NumberFormatter = {
    let formatter = NumberFormatter()
    formatter.locale = locale
    formatter.numberStyle = .decimal
    formatter.maximumFractionDigits = 2
    return formatter
  }()

  public init(locale: String?) {
    self.locale = locale.map { Locale(identifier: $0) } ?? Locale.current
  }

  public func formatted(
    rawText: String,
    rawCursorPosition: CursorData?
  ) -> InputData {
    guard !rawText.isEmpty else {
      return InputData(text: "", cursorPosition: nil, rawData: [])
    }

    guard let formattedText: String = {
      let pattern = "^(?:0|[1-9]\\d*)(?:[.,]\\d{0,2})?$"

      if rawText.range(of: pattern, options: .regularExpression) != nil,
         let val = formattedValue(text: rawText) {
        return val
      }

      return nil
    }() else {
      return lastCorrectInput
    }

    var newCursorPosition: CursorPosition? = if rawText.endIndex == rawCursorPosition?
      .cursorPosition.rawValue {
      CursorPosition(rawValue: formattedText.endIndex)
    } else {
      nil
    }

    var rawData: [InputData.RawCharacter] = []
    var formattedTextPointer = 0

    for rawTextPointer in 0..<rawText.count {
      while formattedTextPointer < formattedText.count {
        guard let formattedTextChar = formattedText[formattedTextPointer],
              let rawTextChar = rawText[rawTextPointer] else {
          break
        }

        formattedTextPointer += 1
        if newCursorPosition == nil,
           formattedTextChar.char == rawTextChar.char,
           rawTextChar.index == rawCursorPosition?.cursorPosition.rawValue {
          newCursorPosition = CursorPosition(
            rawValue: formattedTextChar.index
          )
        }

        if formattedTextChar.char == rawTextChar.char {
          rawData.append(
            InputData.RawCharacter(
              char: rawTextChar.char,
              index: formattedTextChar.index
            )
          )
          break
        }
      }
    }

    let resultInputData = InputData(
      text: formattedText,
      cursorPosition: newCursorPosition,
      rawData: rawData
    )

    lastCorrectInput = resultInputData

    return resultInputData
  }

  public func equals(_ other: any MaskFormatter) -> Bool {
    guard let other = other as? CurrencyMaskFormatter else {
      return false
    }

    return self.locale == other.locale
  }

  private func formattedValue(text: String) -> String? {
    let decimalSeparator = locale.decimalSeparator ?? "."

    formatter.minimumFractionDigits = 0
    if let value = Int(text),
       let formatted = formatter.string(from: NSNumber(value: value)) {
      return formatted
    }

    if let value = Int(text.trimTrailingDecimalSeparator(decimalSeparator)),
       let formatted = formatter.string(from: NSNumber(value: value)) {
      return formatted + decimalSeparator
    }

    formatter.minimumFractionDigits = text.split(separator: decimalSeparator.first ?? ".").last?
      .count ?? 0

    if let value = Decimal(string: text, locale: locale) {
      return formatter.string(from: value as NSNumber)
    }

    return nil
  }
}

extension String {
  fileprivate subscript(_ i: Int) -> InputData.RawCharacter? {
    guard i >= 0, i < count else { return nil }
    let index = self.index(startIndex, offsetBy: i)
    return InputData.RawCharacter(
      char: self[index],
      index: index
    )
  }

  fileprivate func trimTrailingDecimalSeparator(_ separator: String) -> String {
    if hasSuffix(separator) {
      return String(dropLast())
    }

    return self
  }
}
