// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import BaseUIPublic

extension NSAttributedString {
  public var isEmpty: Bool {
    length == 0
  }

  public var prettyDebugDescription: String {
    var result = "\"" + string + "\""
    enumerateAttributes(in: NSRange(location: 0, length: length)) { attributes, range, _ in
      if range.location != 0 || range.length != length {
        result += "\nStyle for \(range.location)-\(range.upperBound):"
      } else {
        result += "\nStyle:"
      }
      result += "\n" + String(describing: Typo(attributes: attributes)).indented()
    }

    return result
  }
}

extension NSAttributedString {
  public func with(typo: Typo) -> NSAttributedString {
    let result = NSMutableAttributedString(attributedString: self)

    enumerateAttributes(
      in: NSRange(location: 0, length: length),
      options: []
    ) { attributes, range, _ in
      let sourceTypo = Typo(attributes: attributes)
      let resultTypo = sourceTypo + typo
      result.addAttributes(resultTypo.attributes, range: range)
    }

    return result
  }

  public func scaled(by scale: CGFloat) -> NSAttributedString {
    guard scale != 1 else { return self }

    let result = NSMutableAttributedString(attributedString: self)

    enumerateAttributes(
      in: NSRange(location: 0, length: length),
      options: []
    ) { attributes, range, _ in
      let sourceTypo = Typo(attributes: attributes)
      let resultTypo = sourceTypo.scaled(by: scale)
      result.setAttributes(resultTypo.attributes, range: range)
    }

    return result
  }
}

extension CGPath {
  fileprivate static func with(rect: CGRect) -> CGPath {
    let path = CGMutablePath()
    path.addRect(rect)
    return path
  }
}
