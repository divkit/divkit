import CoreGraphics
import Foundation

struct SwitchableContainerBlockLayout {
  private let width: CGFloat
  private let titles: SwitchableContainerBlock.Titles
  private let titleGaps: CGFloat
  private let selectorSideGaps: CGFloat

  init(
    width: CGFloat,
    titles: SwitchableContainerBlock.Titles,
    titleGaps: CGFloat,
    selectorSideGaps: CGFloat
  ) {
    self.width = width
    self.titles = titles
    self.titleGaps = titleGaps
    self.selectorSideGaps = selectorSideGaps
  }

  var selectorIntrinsicWidth: CGFloat {
    let leftTitleWidth = max(
      titles.0.selected.sizeForWidth(.infinity).width,
      titles.0.deselected.sizeForWidth(.infinity).width
    )
    let rightTitleWidth = max(
      titles.1.selected.sizeForWidth(.infinity).width,
      titles.1.deselected.sizeForWidth(.infinity).width
    )
    return 2 * selectorSideGaps + 3 * titleGaps + leftTitleWidth + rightTitleWidth
  }

  var titlesHeight: CGFloat {
    [titles.0, titles.1]
      .flatMap { [$0.selected, $0.deselected] }
      .map { $0.heightForWidth(width * 0.5, maxNumberOfLines: 1) }
      .max() ?? 0
  }

  var selectorIntrinsicHeight: CGFloat {
    titlesHeight + 2.0 * titleGaps
  }
}
