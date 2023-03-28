import CoreGraphics

import BaseUIPublic
import CommonCorePublic

public final class TabListViewModel: Equatable {
  public static let defaultListPaddings = EdgeInsets(top: 7, left: 12, bottom: 12, right: 12)

  public let tabTitles: [UILink]
  public let titleStyle: TabTitleStyle
  public let listPaddings: EdgeInsets

  lazy var tabs: [TabTitleViewModel] = tabTitles.map { item in
    let string = item.text.with(typo: titleStyle.typo)
    return .init(
      text: string,
      color: titleStyle.baseTextColor,
      backgroundColor: titleStyle.inactiveBackgroundColor,
      cornerRadius: titleStyle.cornerRadius,
      url: item.url,
      path: item.path,
      insets: titleStyle.paddings,
      cachedSize: string.sizeForWidth(.infinity)
    )
  }

  public init(
    tabTitles: [UILink],
    titleStyle: TabTitleStyle = TabTitleStyle(),
    listPaddings: EdgeInsets = defaultListPaddings
  ) {
    self.tabTitles = tabTitles
    self.titleStyle = titleStyle
    self.listPaddings = listPaddings
  }

  public convenience init(
    tabTitleLinks: [UILink],
    insets: EdgeInsets = defaultListPaddings,
    staticAttributes: Typo = TabTitleStyle.defaultTypo,
    baseColor: Color = TabTitleStyle.defaultBaseTextColor,
    selectedColor: Color = TabTitleStyle.defaultActiveTextColor,
    selectedBackgroundColor: Color = TabTitleStyle.defaultActiveBackgroundColor
  ) {
    let tabTitles = tabTitleLinks
    let titleStyle = TabTitleStyle(
      typo: staticAttributes,
      baseTextColor: baseColor,
      activeTextColor: selectedColor,
      activeBackgroundColor: selectedBackgroundColor
    )

    self.init(tabTitles: tabTitles, titleStyle: titleStyle, listPaddings: insets)
  }

  public static func ==(lhs: TabListViewModel, rhs: TabListViewModel) -> Bool {
    lhs === rhs || (
      lhs.tabTitles == rhs.tabTitles &&
        lhs.titleStyle == rhs.titleStyle &&
        lhs.listPaddings == rhs.listPaddings
    )
  }
}

extension TabListViewModel: CustomDebugStringConvertible {
  public var debugDescription: String {
    let titlesDescriptions = tabTitles.map { $0.debugDescription }
    return """
    { titles: \(titlesDescriptions),
      titleStyle: \(titleStyle),
      listPaddings: \(listPaddings)
    }
    """
  }
}
