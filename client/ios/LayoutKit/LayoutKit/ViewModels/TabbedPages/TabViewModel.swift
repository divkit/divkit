import Foundation

import VGSL

public final class TabViewModel: Equatable {
  public let listModel: TabListViewModel
  public let contentsModel: TabContentsViewModel
  public let separatorStyle: TabSeparatorStyle?

  public init(
    listModel: TabListViewModel,
    contentsModel: TabContentsViewModel,
    separatorStyle: TabSeparatorStyle? = nil
  ) throws {
    if contentsModel.pages.count != listModel.tabTitles.count {
      throw BlockError(
        "Tab error: pages count \(contentsModel.pages.count) " +
          "is not equal to tabs count \(listModel.tabTitles.count)"
      )
    }

    self.listModel = listModel
    self.contentsModel = contentsModel
    self.separatorStyle = separatorStyle
  }

  public static func ==(lhs: TabViewModel, rhs: TabViewModel) -> Bool {
    lhs === rhs || (
      lhs.listModel == rhs.listModel &&
        lhs.contentsModel == rhs.contentsModel &&
        lhs.separatorStyle == rhs.separatorStyle
    )
  }
}

extension TabViewModel: CustomDebugStringConvertible {
  public var debugDescription: String {
    """
    { listModel: \(listModel.debugDescription),
      contentsModel: \(contentsModel.debugDescription),
      separatorStyle: \(dbgStr(separatorStyle)) }
    """
  }
}

extension TabContentsViewModel {
  fileprivate func with(newPages: [TabPageViewModel]) throws -> TabContentsViewModel {
    try TabContentsViewModel(
      pages: newPages,
      pagesHeight: pagesHeight,
      contentInsets: contentInsets,
      path: path,
      background: background,
      footer: footer,
      scrollingEnabled: scrollingEnabled,
      layoutDirection: layoutDirection
    )
  }
}
