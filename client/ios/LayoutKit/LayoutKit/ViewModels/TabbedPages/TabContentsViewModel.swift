import CoreGraphics

import BasePublic
import LayoutKitInterface

public struct TabContentsViewModel: Equatable {
  public let path: UIElementPath!
  public var pages: [TabPageViewModel]
  public let pagesHeight: TabbedPagesHeightMode
  public let background: Background?
  public let footer: Block?
  public let scrollingEnabled: Bool
  public let contentInsets: EdgeInsets

  public init(
    pages: [TabPageViewModel],
    pagesHeight: TabbedPagesHeightMode,
    contentInsets: EdgeInsets = .zero,
    path: UIElementPath? = nil,
    background: Background? = nil,
    footer: Block? = nil,
    scrollingEnabled: Bool = true
  ) throws {
    self.path = path
    self.pages = pages
    self.pagesHeight = pagesHeight
    self.contentInsets = contentInsets
    self.background = background
    self.footer = footer
    self.scrollingEnabled = scrollingEnabled

    try checkConstraints()
  }

  private func checkConstraints() throws {
    if pages.isEmpty {
      throw TabError.missingChildren
    }

    if let footer = footer, footer.isVerticallyResizable {
      throw TabError.unsupportedFooter
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
    let pagesDescription = pages.map { $0.debugDescription }
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
