// Copyright 2018 Yandex LLC. All rights reserved.

import Foundation

import BaseUIPublic

extension String {
  public enum PaddingSide {
    case left
    case right
  }

  public func pad(_ side: PaddingSide, with character: Character, upTo upToCount: Int) -> String {
    let padCount = upToCount - count
    guard padCount > 0 else { return self }

    // swiftlint:disable:next no_direct_use_of_repeating_count_initializer
    let pad = String(repeating: character, count: padCount)
    switch side {
    case .left:
      return pad + self
    case .right:
      return self + pad
    }
  }

  public init(_ staticString: StaticString) {
    let result = staticString.withUTF8Buffer {
      String(decoding: $0, as: UTF8.self)
    }
    self.init(result)
  }
}

extension String {
  public func wordRangesForCaretPosition(_ caretPos: Index) ->
    (wordRange: Range<Index>, enclosingRange: Range<Index>)? {
    var wordRange: Range<Index>?
    var enclosingRange: Range<Index>?
    enumerateSubstrings(
      in: wholeStringRange,
      options: .byWords
    ) { _, _wordRange, _enclosingRange, stop -> Void in
      let caretAtTheBeginningOfWord = _wordRange.lowerBound == caretPos
      let caretInsideWord = _wordRange.contains(caretPos)
      let caretAtTheEndOfWord = _wordRange.upperBound == caretPos
      if !caretAtTheBeginningOfWord, caretInsideWord || caretAtTheEndOfWord {
        wordRange = _wordRange
        enclosingRange = _enclosingRange
        stop = true
      }
    }

    if let wordRange = wordRange, let enclosingRange = enclosingRange {
      return (wordRange, enclosingRange)
    } else {
      return nil
    }
  }

  public func allWordRanges() -> [Range<Index>] {
    var wordRanges: [Range<Index>] = []
    enumerateSubstrings(in: wholeStringRange, options: .byWords) { _, range, _, _ -> Void in
      wordRanges.append(range)
    }
    return wordRanges
  }

  public func allWords() -> [String] {
    allWordRanges().map { String(self[$0]) }
  }

  public var wholeStringRange: Range<Index> {
    startIndex..<endIndex
  }

  public var stringEndRange: Range<Index> {
    endIndex..<endIndex
  }

  public func rangeOfCommonWordPrefixWithString(_ str: String) -> CountableRange<Int> {
    var idx = 0
    let words = allWords()
    var strIdx = 0
    let strWords = str.allWords()

    while idx != words.count, strIdx != strWords.count, words[idx] == strWords[strIdx] {
      idx += 1
      strIdx += 1
    }

    return 0..<idx
  }

  public func rangeOfCommonWordSuffixWithString(_ str: String) -> CountableRange<Int>? {
    let words = allWords()
    let strWords = str.allWords()

    let pairsCount = min(words.count, strWords.count)
    let firstNonEqualWordIdx = zip(words.reversed(), strWords.reversed())
      .enumerated()
      .first(where: { $0.element.0 != $0.element.1 })?.offset ?? pairsCount

    let result = (words.count - firstNonEqualWordIdx)..<words.count
    return result.isEmpty ? nil : result
  }

  public func range(from nsRange: NSRange) -> Range<Index>? {
    let validPositions = (indices + [endIndex]).map { $0.utf16Offset(in: self) }

    guard let from = (
      validPositions.firstIndex(of: nsRange.lowerBound)
        ?? validPositions.firstIndex(where: { $0 > nsRange.lowerBound }).map { $0 - 1 }
    ),
      let to = (
        validPositions.firstIndex(of: nsRange.upperBound)
          ?? validPositions.reversed().firstIndex(where: { $0 < nsRange.upperBound })
          .map { validPositions.count - $0 }
      )
    else { return nil }

    guard let fromIndex = index(startIndex, offsetBy: from, limitedBy: endIndex),
          let toIndex = index(startIndex, offsetBy: to, limitedBy: endIndex) else { return nil }

    return fromIndex..<toIndex
  }
}

extension String {
  public func differsOnlyInWhitespaceFrom(_ other: String) -> Bool {
    let s1WithoutWhitespace = self.replacingOccurrences(of: " ", with: "")
    let s2WithoutWhitespace = other.replacingOccurrences(of: " ", with: "")
    return String(s1WithoutWhitespace) == String(s2WithoutWhitespace)
  }

  public func substringToString(_ string: String) -> String {
    components(separatedBy: string).first ?? ""
  }
}

extension String {
  public var trimmed: String {
    trimmingCharacters(in: .whitespaces)
  }

  /**
   Removes all whitespace at the beginning and at the end of the string, and ensures there is only one whitespace character between words.

   */
  public var normalizedForWhitespaces: String {
    let array = trimmed.components(separatedBy: .whitespacesAndNewlines).filter { !$0.isEmpty }
    return array.joined(separator: " ")
  }

  public var normalized: String {
    normalizedForWhitespaces.lowercased()
  }
}

private let regexForPattern = memoize { (pattern: String) throws -> NSRegularExpression in
  try NSRegularExpression(pattern: pattern, options: [])
}

extension String {
  public func matchesRegex(_ regex: NSRegularExpression) -> Bool {
    let range = stringRangeAsNSRange(wholeStringRange, inString: self)
    let result = regex.firstMatch(in: self, options: [], range: range)
    return result != nil
  }

  public func matchesPattern(_ pattern: String) throws -> Bool {
    let regex = try regexForPattern(pattern)
    return matchesRegex(regex)
  }
}

public func stringRangeAsNSRange(_ range: Range<String.Index>, inString string: String) -> NSRange {
  NSRange(range, in: string)
}

extension String {
  // MOBYANDEXIOS-772: some locales dont contain info about how words should be hyphenated,
  // so we can use other locales to reach this point; for example, Ukrainian may be
  // hyphenated using Russian rules: seems good
  private func hyphenationLocaleForLocale(_ locale: Locale) -> Locale? {
    let hyphenationLocaleMap = [
      "uk": "ru_RU",
      "be": "ru_RU",
    ]

    var fallbackLocale = locale

    for (localeID, hyphenationLocaleID) in hyphenationLocaleMap {
      if locale.identifier.hasPrefix("\(localeID)_") {
        fallbackLocale = Locale(identifier: hyphenationLocaleID)
        break
      }

      if locale.identifier == localeID {
        fallbackLocale = Locale(identifier: hyphenationLocaleID)
        break
      }
    }

    if CFStringIsHyphenationAvailableForLocale(fallbackLocale as CFLocale) {
      return fallbackLocale
    } else {
      return nil
    }
  }

  // MOBYANDEXIOS-772: as of UILabel does not correctly hyphenate words,
  // this method inserts special symbols into string: soft hyphens.
  // They work just like point of stops, without being actually rendered.
  public func hyphenatedStringForLocale(_ locale: Locale) -> String {
    guard let hyphenationLocale = hyphenationLocaleForLocale(locale) else {
      return self
    }

    let range = CFRangeMake(0, utf16.count)
    let hyphenIndices: [CFIndex] = utf16.indices.compactMap {
      let characterIndex = utf16.distance(from: utf16.startIndex, to: $0)
      let hyphenIndex = CFStringGetHyphenationLocationBeforeIndex(
        self as CFString,
        characterIndex,
        range,
        0,
        hyphenationLocale as CFLocale,
        nil
      )
      return (hyphenIndex == kCFNotFound ? nil : hyphenIndex)
    }

    let result = NSMutableString(string: self)
    Set(hyphenIndices).sorted(by: >).forEach {
      result.insert(softHyphenCharacter, at: $0)
    }

    return String(result)
  }

  public var hyphenatedStringForCyrillicAndLatin: String {
    hyphenatedStringForLocale(Locale(identifier: "ru_RU"))
      .hyphenatedStringForLocale(Locale(identifier: "en_US"))
  }
}

private let softHyphenCharacter = "\u{00AD}"

extension String {
  public func replacingCharactersInRange(
    _ range: Range<Index>,
    withString string: String,
    lengthLimit: Int = .max
  ) -> (result: String, insertionOffset: Index) {
    let indices = startIndex..<endIndex
    precondition(
      indices.contains(range.lowerBound) || startIndex == range
        .lowerBound || endIndex == range.lowerBound
    )
    precondition(indices.contains(range.upperBound) || endIndex == range.upperBound)
    let maxInsertionLength = max(
      0,
      lengthLimit - count + distance(from: range.lowerBound, to: range.upperBound)
    )
    let stringToInsert = String(string.prefix(maxInsertionLength))
    let result = replacingCharacters(in: range, with: stringToInsert)

    let insertionDistance = distance(from: startIndex, to: range.lowerBound) + stringToInsert.count
    let insertionOffset = result.index(
      result.startIndex,
      offsetBy: insertionDistance,
      limitedBy: result.endIndex
    ) ?? result.endIndex
    return (String(result.prefix(lengthLimit)), insertionOffset)
  }
}

extension String {
  public var utf8Data: Data? { data(using: String.Encoding.utf8) }
}

extension String {
  public var sanitizedPath: String {
    split(separator: "/").joined(separator: "/")
  }
}

extension String {
  public var base64Encoded: String {
    data(using: .utf8)?.base64EncodedString() ?? ""
  }
}

extension String {
  public func formatted(_ args: CVarArg...) -> Self {
    String(format: self, args)
  }
}
