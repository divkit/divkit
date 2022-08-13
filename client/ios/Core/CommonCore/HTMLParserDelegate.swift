// Copyright 2017 Yandex LLC. All rights reserved.

import Foundation

final class HTMLParserDelegate: NSObject, XMLParserDelegate {
  private(set) var taggedStrings: [HTMLTaggedString] = []
  private var openedTags: [HTMLTag] = []
  private var textInProgress = ""

  func parser(
    _: XMLParser,
    didStartElement elementName: String,
    namespaceURI _: String?,
    qualifiedName _: String?,
    attributes attributeDict: [String: String] = [:]
  ) {
    if !textInProgress.isEmpty {
      taggedStrings.append(HTMLTaggedString(text: textInProgress, tags: openedTags))
      textInProgress = ""
    }
    if let tag = HTMLTag(tag: elementName, attributes: attributeDict) {
      openedTags.append(tag)
    }
  }

  func parser(
    _: XMLParser,
    didEndElement _: String,
    namespaceURI _: String?,
    qualifiedName _: String?
  ) {
    if !textInProgress.isEmpty {
      taggedStrings.append(HTMLTaggedString(text: textInProgress, tags: openedTags))
      textInProgress = ""
    }
    _ = openedTags.popLast()
  }

  func parser(_: XMLParser, foundCharacters string: String) {
    textInProgress += string
  }
}
