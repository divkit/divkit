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

  public func removeSymbols(at index: String.Index, data: InputData) -> (String, CursorData?) {
    var data = data
    let removePosition = data.rawData.lastIndex {
      data.text.distance(from: index, to: $0.index) <= 0
    }
    if let removePosition {
      data.rawData.remove(at: removePosition)
    }
    let rawText = data.rawText
    return (
      rawText,
      CursorData(
        cursorPosition: .init(
          rawValue: removePosition.flatMap { rawText.index(offsetBy: $0) } ?? rawText.startIndex
        ),
        afterNonDecodingSymbols: false
      )
    )
  }

  public func removeSymbols(at range: Range<String.Index>, data: InputData) -> (String, CursorData?) {
    let range = range.clamped(to: data.text.wholeStringRange)
    return (
      String(data.rawData.filter {
        data.text.distance(from: range.lowerBound, to: $0.index) < 0 ||
        data.text.distance(from: range.upperBound, to: $0.index) >= 0
      }.map(\.char)),
      data.rawData.firstIndex { data.text.distance(from: range.lowerBound, to: $0.index) >= 0 }
        .flatMap {
          CursorData(
            cursorPosition: .init(rawValue: data.rawText.index(offsetBy: $0)),
            afterNonDecodingSymbols: false
          )
        }
    )
  }

  public func addSymbols(
    at range: Range<String.Index>,
    data: InputData,
    string: String
  ) -> (String, CursorData?) {
    let range = range.clamped(to: data.text.wholeStringRange)
    let prefix = data.rawData.filter {
      data.text.distance(from: range.lowerBound, to: $0.index) < 0
    }.map(\.char)
    let suffix = data.rawData.filter {
      data.text.distance(from: range.upperBound, to: $0.index) >= 0
    }.map(\.char)
    let text = String(prefix + string + suffix)
    return (
      text,
      CursorData(
        cursorPosition: .init(rawValue: text.index(offsetBy: prefix.count + string.count)),
        afterNonDecodingSymbols: true
      )
    )
  }

  public static func ==(lhs: MaskValidator, rhs: MaskValidator) -> Bool {
    lhs.formatter.equals(rhs.formatter)
  }
}

public enum CursorPositionTag {}
public typealias CursorPosition = Tagged<CursorPositionTag, String.Index>

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

extension String {
  fileprivate func index(offsetBy: Int) -> Index {
    index(startIndex, offsetBy: offsetBy)
  }
}
