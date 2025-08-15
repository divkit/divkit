import Foundation
import VGSL

public final class FixedLengthMaskFormatter: MaskFormatter {
  private let pattern: String
  private let alwaysVisible: Bool
  private let decoding: [Character: Pattern]
  private let inputLength: Int

  public init(pattern: String, alwaysVisible: Bool, patternElements: [PatternElement]) {
    self.pattern = pattern
    self.alwaysVisible = alwaysVisible
    let decoding = [Character: Pattern](
      patternElements.map(\.key),
      patternElements
        .map { Pattern(regexp: $0.regex, placeHolder: $0.placeholder) }
    )
    self.decoding = decoding
    self.inputLength = pattern.reduce(0) { $0 + (decoding.keys.contains($1) ? 1 : 0) }
  }

  public func formatted(rawText: String, rawCursorPosition: CursorData? = nil) -> InputData {
    var text = [Character]()
    var rawData = [InputData.RawCharacter]()
    var stringIndex = rawText.startIndex
    var newCursorPosition: CursorPosition?
    for (index, element) in pattern.enumerated() {
      guard stringIndex != rawText.endIndex else {
        for ch in pattern.suffix(pattern.count - index) {
          if let element = decoding[ch]?.placeHolder {
            guard alwaysVisible else { break }
            text.append(element)
          } else {
            text.append(ch)
          }
        }
        break
      }
      if let decodingElement = decoding[element] {
        let regexp = decodingElement.regexp
        let placeholder = decodingElement.placeHolder
        while stringIndex != rawText.endIndex,
              regexp.numberOfMatches(in: String(rawText[stringIndex]), range: NSRange(0..<1)) == 0 {
          stringIndex = rawText.index(after: stringIndex)
        }
        guard stringIndex != rawText.endIndex else { break }
        if regexp.numberOfMatches(in: String(rawText[stringIndex]), range: NSRange(0..<1)) != 0 {
          text.append(rawText[stringIndex])
          let textString = String(text)
          rawData
            .append(.init(
              char: rawText[stringIndex],
              index: textString.index(before: textString.endIndex)
            ))
          stringIndex = rawText.index(after: stringIndex)
        } else {
          text.append(placeholder)
        }
      } else {
        text.append(element)
      }
    }
    let textString = String(text)
    if let rawCursorPosition {
      let cursorIndex = rawCursorPosition.cursorPosition.rawValue
      if cursorIndex < rawText.endIndex {
        let pos = rawText.distance(from: rawText.startIndex, to: cursorIndex)
        if pos >= rawData.count {
          newCursorPosition = .init(rawValue: textString.endIndex)
        } else if rawCursorPosition.afterNonDecodingSymbols || pos == 0 {
          newCursorPosition = .init(rawValue: rawData[pos].index)
        } else {
          newCursorPosition = .init(rawValue: textString.index(after: rawData[pos - 1].index))
        }
      } else {
        newCursorPosition = .init(rawValue: textString.endIndex)
      }
    }
    return InputData(text: String(text), cursorPosition: newCursorPosition, rawData: rawData)
  }

  public func equals(_ other: MaskFormatter) -> Bool {
    guard let other = other as? FixedLengthMaskFormatter else {
      return false
    }

    return self.decoding == other.decoding &&
      self.pattern == other.pattern &&
      self.alwaysVisible == other.alwaysVisible
  }
}

public struct PatternElement {
  let key: Character
  let regex: NSRegularExpression
  let placeholder: Character

  public init(key: Character, regex: NSRegularExpression, placeholder: Character) {
    self.key = key
    self.regex = regex
    self.placeholder = placeholder
  }
}

public struct Pattern: Equatable {
  public let regexp: NSRegularExpression
  public var placeHolder: Character
}
