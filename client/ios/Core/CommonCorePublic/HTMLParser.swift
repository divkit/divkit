// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

import BaseUIPublic

public final class HTMLParser {
  private let resolver = HTMLEntitiesResolver()
  private let tagsToReplace: [String: String]

  public init(tagsToReplace: [String: String]) {
    self.tagsToReplace = tagsToReplace
  }

  public convenience init() {
    self.init(tagsToReplace: defaultTagsToReplace)
  }

  public func parse(html: String, with baseTypo: Typo) -> NSAttributedString? {
    let stringWithReplacedSymbols = replaceSymbols(in: html)
    let stringWithResolvedHTMLEntities = resolver.resolveHTMLEntities(in: stringWithReplacedSymbols)
    let tagWrappedString = "<tag>\(stringWithResolvedHTMLEntities)</tag>"
    guard let data = tagWrappedString.data(using: .utf16) else {
      return nil
    }

    guard let taggedStrings = parse(htmlData: data) else {
      return nil
    }

    let resultString = NSMutableAttributedString()
    taggedStrings.forEach { substring in
      resultString.append(substring.attributedString(with: baseTypo))
    }

    return resultString
  }

  private func parse(htmlData: Data) -> [HTMLTaggedString]? {
    let parser = XMLParser(data: htmlData)
    parser.externalEntityResolvingPolicy = .never
    let delegate = HTMLParserDelegate()
    parser.delegate = delegate
    let success = parser.parse()
    let result: [HTMLTaggedString]? = success ? delegate.taggedStrings : nil
    parser.delegate = nil

    return result
  }

  private func replaceSymbols(in string: String) -> String {
    var resultString = string
    tagsToReplace.forEach { symbol in
      resultString = resultString.replacingOccurrences(
        of: symbol.key,
        with: symbol.value,
        options: [.literal, .caseInsensitive],
        range: nil
      )
    }

    return resultString
  }
}

private let defaultTagsToReplace: [String: String] = [
  "<br>": "\n",
  "<br/>": "\n",
]
