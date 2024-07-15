import CoreGraphics

import VGSL

#if canImport(UIKit)
import UIKit
#endif

public struct TabContentsViewModel: Equatable {
  public let path: UIElementPath!
  public var pages: [TabPageViewModel]
  public let pagesHeight: TabbedPagesHeightMode
  public let background: Background?
  public let footer: Block?
  public let scrollingEnabled: Bool
  public let contentInsets: EdgeInsets
  public let layoutDirection: UserInterfaceLayoutDirection

  public init(
    pages: [TabPageViewModel],
    pagesHeight: TabbedPagesHeightMode,
    contentInsets: EdgeInsets = .zero,
    path: UIElementPath? = nil,
    background: Background? = nil,
    footer: Block? = nil,
    scrollingEnabled: Bool = true,
    layoutDirection: UserInterfaceLayoutDirection = .leftToRight
  ) throws {
    self.path = path
    self.pages = pages
    self.pagesHeight = pagesHeight
    self.contentInsets = contentInsets
    self.background = background
    self.footer = footer
    self.scrollingEnabled = scrollingEnabled
    self.layoutDirection = layoutDirection

    try checkConstraints()
  }

  private func checkConstraints() throws {
    if pages.isEmpty {
      throw BlockError("Tab error: no children provided")
    }

    if let footer, footer.isVerticallyResizable {
      throw BlockError("Tab error: vertically resizable footer is unsupported")
    }
  }

  public static func ==(lhs: TabContentsViewModel, rhs: TabContentsViewModel) -> Bool {
    lhs.path == rhs.path
      && lhs.pages == rhs.pages
      && lhs.pagesHeight == rhs.pagesHeight
      && lhs.contentInsets == rhs.contentInsets
      && lhs.background == rhs.background
      && lhs.footer == rhs.footer
      && lhs.scrollingEnabled == rhs.scrollingEnabled
  }
}

extension TabContentsViewModel: CustomDebugStringConvertible {
  public var debugDescription: String {
    let pagesDescription = pages.map(\.debugDescription)
    return """
    { path: \(dbgStr(path)),
      pages: \(pagesDescription),
      pagesHeight: \(pagesHeight),
      contentInsets: \(contentInsets),
      background: \(dbgStr(background?.debugDescription)),
      footer: \(dbgStr(footer?.debugDescription)),
      scrollingEnabled: \(scrollingEnabled) }
    """
  }
}
