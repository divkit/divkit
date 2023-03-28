import UIKit

import BasePublic
import LayoutKitInterface

final class TabSelectionWireframe {
  private weak var tabListView: TabListView?
  private weak var tabContentsView: TabContentsView?
  private weak var tabListSelectionDataSource: TabListSelectionDataSource!

  init(
    tabListView: TabListView,
    tabListSelectionDataSource: TabListSelectionDataSource,
    tabContentsView: TabContentsView
  ) {
    self.tabListView = tabListView
    self.tabListSelectionDataSource = tabListSelectionDataSource
    self.tabContentsView = tabContentsView
    self.tabListView?.selectionDelegate = self
    self.tabContentsView?.delegate = self
  }
}

extension TabSelectionWireframe: TabContentsViewDelegate {
  func tabContentsViewDidChangeRelativeContentOffsetTo(_ offset: CGFloat) {
    guard let tabListView = tabListView else { return }
    let newModel = tabListSelectionDataSource.modelForItemSelection(offset)
    tabListView.model = newModel
  }

  func tabContentsViewDidEndAnimation() {
    tabListView?.endScrollingAnimation()
  }
}

extension TabSelectionWireframe: TabListViewDelegateTabSelection {
  func tabListViewDidSelectItemAt(_ index: Int, withUrl url: URL?, path: UIElementPath) {
    guard let contentsView = tabContentsView, let tabListView = tabListView else {
      return
    }
    let selection = CGFloat(index)
    let action: UserInterfaceAction
    if contentsView.selectedPageIndex.isApproximatelyEqualTo(selection) {
      if let url = url {
        action = UserInterfaceAction(url: url, path: path)
      } else {
        action = UserInterfaceAction(url: tabChangedUrl, path: path)
      }
    } else {
      action = UserInterfaceAction(url: tabChangedUrl, path: path)
      tabListView.prepareForScrollingAnimation(to: selection)
      contentsView.selectPageAtIndex(selection)
    }
    action.perform(sendingFrom: tabListView)
  }

  func tabListViewDidScrollTo(_ offset: CGFloat) {
    guard let tabListView = tabListView else {
      return
    }
    tabListView.model = tabListView.model.map { modified($0) { $0.offset = offset } }
  }
}

public let tabChangedUrl = URL(string: "tab-changed://")!
