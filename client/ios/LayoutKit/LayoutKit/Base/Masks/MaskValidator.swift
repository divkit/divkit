import BaseTinyPublic
import Foundation

public protocol MaskFormatter {
  func formatted(rawText: String, rawCursorPosition: CursorData?) -> InputData

  func equals(_ other: MaskFormatter) -> Bool
}

public final class MaskValidator: Equatable {
  private let formatter: MaskFormatter

  public init(formatter: MaskFormatter) {
    self.formatter = formatter
  }

  public func formatted(rawText: String, rawCursorPosition: CursorData? = nil) -> InputData {
    formatter.formatted(rawText: rawText, rawCursorPosition: rawCursorPosition)
  }

  public func removeSymbols(at pos: Int, data: InputData) -> (String, CursorData?) {
    let pos = min(pos, data.text.count)
    var data = data
    let removeIndex = data.rawData.lastIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: pos),
      to: $0.index
    ) <= 0 }
    if let removeIndex {
      data.rawData.remove(at: removeIndex)
    }
    return (
      data.rawText,
      CursorData(cursorPosition: .init(rawValue: removeIndex ?? 0), afterNonDecodingSymbols: false)
    )
  }

  public func removeSymbols(at range: Range<Int>, data: InputData) -> (String, CursorData?) {
    let range = Range<Int>
      .init(uncheckedBounds: (
        min(range.lowerBound, data.text.count),
        min(range.upperBound, data.text.count)
      ))
    let index = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
      to: $0.index
    ) >= 0 }
    return (
      String(data.rawData.filter {
        data.text.distance(
          from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
          to: $0.index
        ) < 0 ||
          data.text.distance(
            from: data.text.index(data.text.startIndex, offsetBy: range.upperBound),
            to: $0.index
          ) >= 0
      }.map(\.char)),
      index
        .flatMap { CursorData(cursorPosition: .init(rawValue: $0), afterNonDecodingSymbols: false) }
    )
  }

  public func addSymbols(
    at range: Range<Int>,
    data: InputData,
    string: String
  ) -> (String, CursorData?) {
    let range = Range<Int>
      .init(uncheckedBounds: (
        min(range.lowerBound, data.text.count),
        min(range.upperBound, data.text.count)
      ))
    let leftIndex = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.lowerBound),
      to: $0.index
    ) >= 0 } ?? data.rawData.count

    let rightIndex = data.rawData.firstIndex { data.text.distance(
      from: data.text.index(data.text.startIndex, offsetBy: range.upperBound),
      to: $0.index
    ) >= 0 } ?? data.rawData.count

    let prefix = String(data.rawData[0..<leftIndex].map(\.char))
    let suffix = String(data.rawData[rightIndex..<data.rawData.count].map(\.char))
    return (
      String(prefix + string + suffix),
      CursorData(
        cursorPosition: .init(rawValue: prefix.count + string.count),
        afterNonDecodingSymbols: true
      )
    )
  }

  public static func ==(lhs: MaskValidator, rhs: MaskValidator) -> Bool {
    lhs.formatter.equals(rhs.formatter)
  }
}

public enum CursorPositionTag {}
public typealias CursorPosition = Tagged<CursorPositionTag, Int>

public struct CursorData: Equatable {
  let cursorPosition: CursorPosition
  let afterNonDecodingSymbols: Bool
}

public struct InputData {
  public struct RawCharacter: Equatable {
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
