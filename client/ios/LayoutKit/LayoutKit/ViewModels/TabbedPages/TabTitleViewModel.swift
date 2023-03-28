import CoreGraphics
import Foundation

import CommonCorePublic
import LayoutKitInterface

final class TabTitleViewModel: Equatable {
  let text: NSAttributedString
  let color: Color
  let backgroundColor: Color
  let cornerRadius: CornerRadii?
  let path: UIElementPath
  let url: URL?
  let insets: EdgeInsets
  let cachedSize: CGSize

  var totalSize: CGSize { cachedSize + insets.size }

  init(
    text: NSAttributedString,
    color: Color,
    backgroundColor: Color,
    cornerRadius: CornerRadii? = nil,
    url: URL?,
    path: UIElementPath,
    insets: EdgeInsets,
    cachedSize: CGSize
  ) {
    self.text = text
    self.color = color
    self.backgroundColor = backgroundColor
    self.cornerRadius = cornerRadius
    self.path = path
    self.url = url
    self.insets = insets
    self.cachedSize = cachedSize
  }

  func with(color: Color) -> TabTitleViewModel {
    if color == self.color {
      return self
    }

    return TabTitleViewModel(
      text: text,
      color: color,
      backgroundColor: backgroundColor,
      cornerRadius: cornerRadius,
      url: url,
      path: path,
      insets: insets,
      cachedSize: cachedSize
    )
  }

  func with(backgroundColor: Color) -> TabTitleViewModel {
    if backgroundColor == self.backgroundColor {
      return self
    }

    return TabTitleViewModel(
      text: text,
      color: color,
      backgroundColor: backgroundColor,
      cornerRadius: cornerRadius,
      url: url,
      path: path,
      insets: insets,
      cachedSize: cachedSize
    )
  }

  func with(text: NSAttributedString) -> TabTitleViewModel {
    if text == self.text {
      return self
    }

    return TabTitleViewModel(
      text: text,
      color: color,
      backgroundColor: backgroundColor,
      cornerRadius: cornerRadius,
      url: url,
      path: path,
      insets: insets,
      cachedSize: cachedSize
    )
  }
}

func ==(lhs: TabTitleViewModel, rhs: TabTitleViewModel) -> Bool {
  if lhs === rhs {
    return true
  }

  return lhs.text == rhs.text &&
    lhs.color == rhs.color &&
    lhs.backgroundColor == rhs.backgroundColor &&
    lhs.cornerRadius == rhs.cornerRadius &&
    lhs.path == rhs.path &&
    lhs.url == rhs.url &&
    lhs.insets == rhs.insets &&
    lhs.cachedSize == rhs.cachedSize
}

extension EdgeInsets {
  fileprivate var size: CGSize {
    CGSize(width: left + right, height: top + bottom)
  }
}
