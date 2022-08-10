@testable import LayoutKit

import XCTest

import BaseUI
import CommonCore

final class TabListSelectionDataSourceImplTests: XCTestCase {
  private var tabListSelectionDataSource: TabListSelectionDataSourceImpl!
  private var listModel: TabListViewModel!

  override func setUp() {
    listModel = TabListViewModel(
      tabTitleLinks: tabTitleLinks,
      staticAttributes: staticAttributes,
      baseColor: baseColor,
      selectedColor: selectedColor,
      selectedBackgroundColor: selectedBackgroundColor
    )
    tabListSelectionDataSource = TabListSelectionDataSourceImpl(listModel: listModel)
  }

  func test_NumberOfItems_IsEqualToNumberOfTabs() {
    let itemCount = tabListSelectionDataSource.numberOfTabs

    XCTAssertEqual(itemCount, tabTitleLinks.count)
  }

  func test_ModelForSelection_HasEqualSelection() {
    let selection: CGFloat = 0.8

    let model = tabListSelectionDataSource.modelForItemSelection(selection)

    XCTAssertEqual(model.selection, selection)
  }

  func test_ModelForSelection_HasItemsWithCorrespondingColors() {
    let selection: CGFloat = 0.3

    let model = tabListSelectionDataSource.modelForItemSelection(selection)

    let cubicSelection = 4 * pow(selection, 3)
    let colors = [
      baseColor.interpolate(to: selectedColor, progress: 1 - cubicSelection),
      baseColor.interpolate(to: selectedColor, progress: cubicSelection),
    ]
    let expectedItems = (0..<tabTitleLinks.count).map {
      TabTitleViewModel(
        text: tabTitleLinks[$0].text.with(typo: staticAttributes),
        color: colors[$0],
        backgroundColor: .clear,
        cornerRadius: nil,
        url: nil,
        path: UIElementPath("Deech"),
        insets: .zero,
        cachedSize: tabTitleLinks[$0].text.with(typo: staticAttributes).sizeForWidth(.infinity)
      )
    }
    XCTAssertTrue(model.items.isApproximatelyEqual(to: expectedItems))
  }

  func test_PassesSelectedBackgroundColorAndInsetsAndSpacingFromDataToModel() {
    let model = tabListSelectionDataSource.modelForItemSelection(0.5)

    XCTAssertEqual(model.selectedBackgroundColor, selectedBackgroundColor)
    XCTAssertEqual(model.listPaddings, listModel.listPaddings)
    XCTAssertEqual(model.titlePaddings, listModel.titleStyle.paddings)
  }
}

extension Array where Element == TabTitleViewModel {
  fileprivate func isApproximatelyEqual(to other: [TabTitleViewModel]) -> Bool {
    guard count == other.count else {
      return false
    }

    return zip(self, other).reduce(true) { result, tuple in
      let (lhs, rhs) = tuple
      return result && lhs.isApproximatelyEqual(to: rhs)
    }
  }
}

extension TabTitleViewModel {
  fileprivate func isApproximatelyEqual(to other: TabTitleViewModel) -> Bool {
    if self === other {
      return true
    }

    return text == other.text &&
      path == other.path &&
      url == other.url &&
      cachedSize == other.cachedSize &&
      color == other.color
  }
}

private let tabTitleLinks = ["Afisha", "Auto"]
  .map { UILink(text: $0, path: UIElementPath("Deech")) }
private let staticAttributes = Typo(baselineOffset: 1)
private let tabTitlesWidths: [CGFloat] = [34, 25]
private let baseColor = Color(red: 1, green: 0, blue: 0, alpha: 1)
private let selectedColor = Color(red: 0, green: 1, blue: 1, alpha: 0.5)
private let selectedBackgroundColor = Color.cyan
