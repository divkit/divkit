import BaseTinyPublic
import Foundation

public final class MaskValidator: Equatable {
  private let pattern: String
  private let alwaysVisible: Bool
  private let decoding: [Character: Pattern]
  private let inputLength: Int

  public init(pattern: String, alwaysVisible: Bool, patternElements: [PatternElement]) {
    self.pattern = pattern
    self.alwaysVisible = alwaysVisible
    let decoding = [Character: Pattern].init(
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
            if let rawCursorPosition, rawData.count == rawCursorPosition.cursorPosition.rawValue,
               rawCursorPosition.afterNonDecodingSymbols {
              newCursorPosition = .init(rawValue: text.count)
            }
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
          rawData.append(.init(char: rawText[stringIndex], index: String(text).endIndex))
          stringIndex = rawText.index(after: stringIndex)
        } else {
          text.append(placeholder)
        }

        if let rawCursorPosition, rawData.count <= rawCursorPosition.cursorPosition.rawValue {
          newCursorPosition = .init(rawValue: text.count)
        }
      } else {
        text.append(element)
        if let rawCursorPosition, rawData.count < rawCursorPosition.cursorPosition.rawValue {
          newCursorPosition = .init(rawValue: text.count)
        } else if let rawCursorPosition, rawData.count == rawCursorPosition.cursorPosition.rawValue,
                  rawCursorPosition.afterNonDecodingSymbols {
          newCursorPosition = .init(rawValue: text.count)
        }
      }
    }
    return InputData(text: String(text), cursorPosition: newCursorPosition, rawData: rawData)
  }

  public func removeSymbols(at pos: Int, data: InputData) -> (String, CursorData?) {
    var data = data
    let removeIndex = data.rawData.lastIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: pos),
      to: $0.index
    ) <= 0 }
    if let removeIndex = removeIndex {
      data.rawData.remove(at: removeIndex)
    }
    return (
      data.rawText,
      removeIndex
        .flatMap { CursorData(cursorPosition: .init(rawValue: $0), afterNonDecodingSymbols: false) }
    )
  }

  public func removeSymbols(at range: Range<Int>, data: InputData) -> (String, CursorData?) {
    let index = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
      to: $0.index
    ) > 0 }
    return (
      String(data.rawData.filter {
        data.text.distance(
          from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
          to: $0.index
        ) <= 0 ||
          data.text.distance(
            from: data.text.index(data.text.startIndex, offsetBy: range.upperBound),
            to: $0.index
          ) > 0
      }.map(\.char)),
      index
        .flatMap { CursorData(cursorPosition: .init(rawValue: $0), afterNonDecodingSymbols: false) }
    )
  }

  public func addSymbols(
    at pos: Int,
    data: InputData,
    string: String
  ) -> (String, CursorData?) {
    let addIndex = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: pos),
      to: $0.index
    ) > 0 } ?? data.rawData.count

    let prefix = String(data.rawData[0..<addIndex].map(\.char))
    let suffix = String(data.rawData[addIndex..<data.rawData.count].map(\.char))
    return (
      String((prefix + string + suffix).prefix(inputLength)),
      CursorData(
        cursorPosition: .init(rawValue: prefix.count + string.count),
        afterNonDecodingSymbols: true
      )
    )
  }

  public func addSymbols(
    at range: Range<Int>,
    data: InputData,
    string: String
  ) -> (String, CursorData?) {
    let leftIndex = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
      to: $0.index
    ) > 0 } ?? data.rawData.count

    let rightIndex = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.upperBound),
      to: $0.index
    ) > 0 } ?? data.rawData.count

    let prefix = String(data.rawData[0..<leftIndex].map(\.char))
    let suffix = String(data.rawData[rightIndex..<data.rawData.count].map(\.char))
    return (
      String((prefix + string + suffix).prefix(inputLength)),
      CursorData(
        cursorPosition: .init(rawValue: prefix.count + string.count),
        afterNonDecodingSymbols: true
      )
    )
  }

  public static func ==(lhs: MaskValidator, rhs: MaskValidator) -> Bool {
    lhs.decoding == rhs.decoding &&
      lhs.pattern == rhs.pattern &&
      lhs.alwaysVisible == rhs.alwaysVisible
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

public enum CursorPositionTag {}
public typealias CursorPosition = Tagged<CursorPositionTag, Int>

public struct CursorData {
  let cursorPosition: CursorPosition
  let afterNonDecodingSymbols: Bool
}

public struct InputData {
  public struct RawCharacter {
    let char: Character
    let index: String.Index
  }

  public let text: String
  public let cursorPosition: CursorPosition?
  public var rawData: [RawCharacter]
  public var rawText: String {
    String(rawData.map(\.char))
  }
}

public struct Pattern: Equatable {
  public let regexp: NSRegularExpression
  public var placeHolder: Character
}
