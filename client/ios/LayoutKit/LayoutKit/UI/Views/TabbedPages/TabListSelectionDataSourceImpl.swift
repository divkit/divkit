import CoreGraphics
import Foundation

import CommonCorePublic

final class TabListSelectionDataSourceImpl: TabListSelectionDataSource {
  private let listModel: TabListViewModel

  init(listModel: TabListViewModel) {
    precondition(!listModel.tabTitles.isEmpty)
    self.listModel = listModel
  }

  var numberOfTabs: Int {
    listModel.tabTitles.count
  }

  func modelForItemSelection(_ selection: CGFloat) -> TabTitlesViewModel {
    let items = listModel.tabs.enumerated().map { offset, tab -> TabTitleViewModel in
      let distanceToSelected = abs(CGFloat(offset) - selection)
      let selectionFractionForItem = max(1 - distanceToSelected, 0)
      let textColor: Color
      let backgroundColor: Color
      let text: NSAttributedString
      if selectionFractionForItem == 0 {
        textColor = listModel.titleStyle.baseTextColor
        backgroundColor = listModel.titleStyle.inactiveBackgroundColor
        text = tab.text.string.with(typo: listModel.titleStyle.inactiveTypo)
      } else {
        let progress = easeInOutCubic(selectionFractionForItem)
        textColor = listModel.titleStyle.baseTextColor.interpolate(
          to: listModel.titleStyle.activeTextColor,
          progress: progress
        )
        backgroundColor = listModel.titleStyle.inactiveBackgroundColor.interpolate(
          to: .clear,
          progress: progress
        )
        text = tab.text.string.with(typo: listModel.titleStyle.typo)
      }
      return tab
        .with(backgroundColor: backgroundColor)
        .with(text: text)
        .with(color: textColor)
    }

    return TabTitlesViewModel(
      items: items,
      listPaddings: listModel.listPaddings,
      titlePaddings: listModel.titleStyle.paddings,
      selectedBackgroundColor: listModel.titleStyle.activeBackgroundColor,
      backgroundColor: listModel.titleStyle.inactiveBackgroundColor,
      cornerRadius: listModel.titleStyle.cornerRadius,
      itemSpacing: listModel.titleStyle.itemSpacing,
      selection: selection,
      offset: nil
    )
  }
}

private func easeInOutCubic(_ step: CGFloat) -> CGFloat {
  let t = clamp(step, min: 0, max: 1)
  if t < 0.5 {
    return 4 * pow(t, 3)
  } else {
    return 4 * pow(t - 1, 3) + 1
  }
}
