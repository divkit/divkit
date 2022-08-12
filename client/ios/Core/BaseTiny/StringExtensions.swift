// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension String {
  /// This method is really slow and should be used only to format debugDescription.
  public func indented(level: Int = 1) -> String {
    // swiftlint:disable no_direct_use_of_repeating_count_initializer
    let indent = String(repeating: " ", count: max(level * 2, 0))
    // swiftlint:disable no_direct_use_of_repeating_count_initializer
    return components(separatedBy: "\n").map { indent + $0 }.joined(separator: "\n")
  }
}

extension String {
  public subscript(r: Range<Int>) -> String {
    let stringRange = rangeOfCharsIn(r)
    return String(self[stringRange])
  }

  public func rangeOfCharsIn(_ range: Range<Int>) -> Range<Index> {
    index(startIndex, offsetBy: range.lowerBound)..<index(startIndex, offsetBy: range.upperBound)
  }
}

extension String {
  public func stringWithFirstCharCapitalized() -> String {
    guard !self.isEmpty else { return self }
    let secondCharIndex = index(after: startIndex)
    return self[..<secondCharIndex].capitalized + self[secondCharIndex...]
  }
}

extension String {
  public var containsOnlyWhitespace: Bool {
    trimmingCharacters(in: .whitespaces).isEmpty
  }
}

extension String {
  public init(forQueryItemWithName name: String, value: String?) {
    let valueString = value.map { "=\($0.percentEncoded())" } ?? ""
    self = name.percentEncoded() + valueString
  }

  public func percentEncoded() -> String {
    addingPercentEncoding(withAllowedCharacters: URLUnreservedCharSet)!
  }

  public var percentEncodedURLString: String {
    let stringWithoutEncoding = removingPercentEncoding ?? self
    return stringWithoutEncoding.addingPercentEncoding(withAllowedCharacters: URLAllowedCharSet)!
  }

  public var percentEncodedExceptSpecialAndLatin: String {
    addingPercentEncoding(withAllowedCharacters: URLSpecialAndLatinCharSet)!
  }

  public func quoted() -> String {
    guard let jsString = try? JSONSerialization.data(
      withJSONObject: self,
      options: .fragmentsAllowed
    ),
      let result = String(data: jsString, encoding: .utf8) else {
      assertionFailure()
      return ""
    }
    return result
  }
}

public func dbgStr<T>(_ val: T?) -> String {
  val.map { "\($0)" } ?? "nil"
}

// RFC 3986 section 2.2
private let URLReservedChars = "!*'();:@&=+$,/?#[]"
private let URLUnreservedChars =
  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_.~"
private let URLSpecialAndLatinChars = URLReservedChars + URLUnreservedChars + "%"
private let URLUnreservedCharSet = CharacterSet(charactersIn: URLUnreservedChars)
private let URLAllowedCharSet = CharacterSet(charactersIn: URLReservedChars + URLUnreservedChars)
private let URLSpecialAndLatinCharSet = CharacterSet(charactersIn: URLSpecialAndLatinChars)
