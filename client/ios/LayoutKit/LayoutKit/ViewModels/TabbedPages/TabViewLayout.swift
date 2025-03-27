import CoreGraphics
import Foundation

public struct TabViewLayout: Equatable {
  public let size: CGSize
  public let listFrame: CGRect
  public let contentsFrame: CGRect
  public let separatorFrame: CGRect

  public init(
    model: TabViewModel,
    selectedPageIndex: CGFloat,
    width: CGFloat? = nil,
    height oldHeight: CGFloat? = nil
  ) {
    let listModel = model.listModel
    let contentsModel = model.contentsModel

    let titlesHeight = listModel.tabs.reduce(CGFloat(0)) { max($0, $1.totalSize.height) }
    let listHeight = titlesHeight + listModel.listPaddings.vertical.sum

    let separatorHeight: CGFloat
    let separatorInsets: EdgeInsets
    if let separatorStyle = model.separatorStyle {
      separatorHeight = defaultSeparatorHeight
      separatorInsets = separatorStyle.insets
    } else {
      separatorHeight = 0
      separatorInsets = .zero
    }

    let newWidth = width ?? max(contentsModel.intrinsicWidth, separatorInsets.horizontal.sum)

    let height: CGFloat? = switch contentsModel.pagesHeight {
    case .byHighestPage:
      oldHeight
    case .bySelectedPage:
      nil
    }
    let newHeight = height ??
      contentsModel.intrinsicHeight(
        forWidth: newWidth,
        selectedPageIndex: selectedPageIndex
      ) +
      listHeight +
      separatorHeight +
      separatorInsets.vertical.sum

    listFrame = CGRect(origin: .zero, size: CGSize(width: newWidth, height: listHeight))

    let separatorY = listFrame.maxY + separatorInsets.top
    let separatorWidth = newWidth - separatorInsets.horizontal.sum

    separatorFrame = CGRect(
      x: separatorInsets.left,
      y: separatorY,
      width: separatorWidth,
      height: separatorHeight
    )

    let contentsY = separatorFrame.maxY + separatorInsets.bottom + contentsModel.contentInsets.top

    contentsFrame = CGRect(
      x: 0,
      y: contentsY,
      width: newWidth,
      height: newHeight - contentsY - contentsModel.contentInsets.bottom
    )

    size = CGSize(width: newWidth, height: newHeight)
  }

  public init(
    model: TabViewModel,
    selectedPageIndex: CGFloat,
    size: CGSize
  ) {
    self.init(
      model: model,
      selectedPageIndex: selectedPageIndex,
      width: size.width,
      height: size.height
    )
  }
}

extension TabContentsViewModel {
  fileprivate var intrinsicWidth: CGFloat {
    TabContentsViewLayout.intrinsicWidth(
      for: pages.map(\.block),
      footer: footer,
      pagesInsets: contentInsets.horizontal
    )
  }

  fileprivate func intrinsicHeight(
    forWidth width: CGFloat,
    selectedPageIndex: CGFloat
  ) -> CGFloat {
    TabContentsViewLayout.intrinsicHeight(
      forWidth: width,
      pages: pages.map(\.block),
      pagesHeightMode: pagesHeight,
      selectedPageIndex: selectedPageIndex,
      footer: footer,
      pagesInsets: contentInsets
    )
  }
}

private let defaultSeparatorHeight: CGFloat = 1
