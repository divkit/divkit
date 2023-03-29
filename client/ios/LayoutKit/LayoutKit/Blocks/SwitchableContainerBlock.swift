import CoreGraphics
import Foundation

import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

public final class SwitchableContainerBlock: Block {
  public enum Selection: Int, Equatable {
    case left
    case right
  }

  public struct Title: Equatable {
    public let selected: NSAttributedString
    public let deselected: NSAttributedString

    public init(selected: NSAttributedString, deselected: NSAttributedString) {
      self.selected = selected
      self.deselected = deselected
    }

    public init(text: String, selectedTypo: Typo, deselectedTypo: Typo) {
      self.selected = text.with(typo: selectedTypo)
      self.deselected = text.with(typo: deselectedTypo)
    }
  }

  public struct Item {
    public let title: Title
    public let content: Block

    public init(title: Title, content: Block) {
      self.title = title
      self.content = content
    }
  }

  public typealias Pair<T> = (T, T)
  public typealias Titles = Pair<Title>
  public typealias Items = Pair<Item>

  public let selectedItem: Selection
  public let items: Items
  public let backgroundColor: Color
  public let selectedBackgroundColor: Color
  public let titleGaps: CGFloat
  public let titleContentGap: CGFloat
  public let selectorSideGaps: CGFloat
  public let switchAction: UserInterfaceAction?
  public let path: UIElementPath

  public init(
    selectedItem: Selection,
    items: Items,
    backgroundColor: Color,
    selectedBackgroundColor: Color,
    titleGaps: CGFloat,
    titleContentGap: CGFloat,
    selectorSideGaps: CGFloat,
    switchAction: UserInterfaceAction?,
    path: UIElementPath
  ) {
    self.selectedItem = selectedItem
    self.items = items
    self.backgroundColor = backgroundColor
    self.selectedBackgroundColor = selectedBackgroundColor
    self.titleGaps = titleGaps
    self.titleContentGap = titleContentGap
    self.selectorSideGaps = selectorSideGaps
    self.switchAction = switchAction
    self.path = path
  }

  public var isVerticallyResizable: Bool {
    items.0.content.isVerticallyResizable && items.1.content.isVerticallyResizable
  }

  public var isHorizontallyResizable: Bool {
    items.0.content.isHorizontallyResizable && items.1.content.isHorizontallyResizable
  }

  public let isVerticallyConstrained = false
  public let isHorizontallyConstrained = false

  public var intrinsicContentWidth: CGFloat {
    let layout = SwitchableContainerBlockLayout(
      width: .infinity,
      titles: (items.0.title, items.1.title),
      titleGaps: titleGaps,
      selectorSideGaps: selectorSideGaps
    )
    let contentMaxWidth = max(
      items.0.content.intrinsicContentWidth,
      items.1.content.intrinsicContentWidth
    )
    return max(layout.selectorIntrinsicWidth, contentMaxWidth)
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    var total: CGFloat = 0

    let layout = SwitchableContainerBlockLayout(
      width: width,
      titles: (items.0.title, items.1.title),
      titleGaps: titleGaps,
      selectorSideGaps: selectorSideGaps
    )
    total += layout.selectorIntrinsicHeight

    total += titleContentGap

    total += [items.0.content, items.1.content]
      .map { $0.intrinsicContentHeight(forWidth: width) }
      .max() ?? 0

    return total
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat {
    guard !isHorizontallyResizable else {
      assertionFailure("Should be at least one non resizable block")
      return 0
    }

    var width: CGFloat = 0
    if !items.0.content.isHorizontallyResizable {
      width = max(items.0.content.widthOfHorizontallyNonResizableBlock, width)
    }
    if !items.1.content.isHorizontallyResizable {
      width = max(items.1.content.widthOfHorizontallyNonResizableBlock, width)
    }
    return width
  }

  public func heightOfVerticallyNonResizableBlock(forWidth width: CGFloat) -> CGFloat {
    guard !isVerticallyResizable else {
      assertionFailure("Should be at leat one non resizable block")
      return 0
    }

    var height: CGFloat = 0

    let layout = SwitchableContainerBlockLayout(
      width: width,
      titles: (items.0.title, items.1.title),
      titleGaps: titleGaps,
      selectorSideGaps: selectorSideGaps
    )
    height += layout.selectorIntrinsicHeight
    height += titleContentGap

    var contentHeight: CGFloat = 0
    if !items.0.content.isVerticallyResizable {
      contentHeight = max(
        items.0.content.heightOfVerticallyNonResizableBlock(forWidth: width),
        contentHeight
      )
    }
    if !items.1.content.isVerticallyResizable {
      contentHeight = max(
        items.1.content.heightOfVerticallyNonResizableBlock(forWidth: width),
        contentHeight
      )
    }
    return height + contentHeight
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    precondition(isVerticallyResizable)
    return max(
      items.0.content.weightOfVerticallyResizableBlock,
      items.1.content.weightOfVerticallyResizableBlock
    )
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    precondition(isHorizontallyResizable)
    return max(
      items.0.content.weightOfHorizontallyResizableBlock,
      items.1.content.weightOfHorizontallyResizableBlock
    )
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? SwitchableContainerBlock else { return false }
    return self == other
  }
}

extension SwitchableContainerBlock.Item: Equatable {
  public static func ==(
    lhs: SwitchableContainerBlock.Item,
    rhs: SwitchableContainerBlock.Item
  ) -> Bool {
    lhs.title == rhs.title &&
      lhs.content == rhs.content
  }
}

extension SwitchableContainerBlock: Equatable {
  public static func ==(lhs: SwitchableContainerBlock, rhs: SwitchableContainerBlock) -> Bool {
    lhs.items.0 == rhs.items.0 &&
      lhs.items.1 == rhs.items.1 &&
      lhs.backgroundColor == rhs.backgroundColor &&
      lhs.selectedBackgroundColor == rhs.selectedBackgroundColor &&
      lhs.titleGaps == rhs.titleGaps &&
      lhs.titleContentGap == rhs.titleContentGap &&
      lhs.selectorSideGaps == rhs.selectorSideGaps &&
      lhs.switchAction == rhs.switchAction
  }
}

extension SwitchableContainerBlock: ImageContaining {
  public func getImageHolders() -> [ImageHolder] {
    items.0.content.getImageHolders() + items.1.content.getImageHolders()
  }
}

extension SwitchableContainerBlock: ElementStateUpdating {
  public func updated(withStates states: BlocksState) throws -> SwitchableContainerBlock {
    let newItems = try map(items) {
      SwitchableContainerBlock.Item(
        title: $0.title,
        content: try $0.content.updated(withStates: states)
      )
    }

    let newState = states
      .getState(at: path) ?? SwitchableContainerBlockState(selectedItem: selectedItem)

    guard newItems.0.content !== items.0.content ||
      newItems.1.content !== items.1.content ||
      newState.selectedItem != selectedItem else {
      return self
    }

    return SwitchableContainerBlock(
      selectedItem: newState.selectedItem,
      items: newItems,
      backgroundColor: backgroundColor,
      selectedBackgroundColor: selectedBackgroundColor,
      titleGaps: titleGaps,
      titleContentGap: titleContentGap,
      selectorSideGaps: selectorSideGaps,
      switchAction: switchAction,
      path: path
    )
  }
}

extension SwitchableContainerBlock: LayoutCaching {
  private func replacingContent(with items: Items) -> Block {
    SwitchableContainerBlock(
      selectedItem: selectedItem,
      items: items,
      backgroundColor: backgroundColor,
      selectedBackgroundColor: selectedBackgroundColor,
      titleGaps: titleGaps,
      titleContentGap: titleContentGap,
      selectorSideGaps: selectorSideGaps,
      switchAction: switchAction,
      path: path
    )
  }

  public func laidOut(for width: CGFloat) -> Block {
    replacingContent(with: (
      Item(title: items.0.title, content: items.0.content.laidOut(for: width)),
      Item(title: items.1.title, content: items.1.content.laidOut(for: width))
    ))
  }

  public func laidOut(for size: CGSize) -> Block {
    replacingContent(with: (
      Item(title: items.0.title, content: items.0.content.laidOut(for: size)),
      Item(title: items.1.title, content: items.1.content.laidOut(for: size))
    ))
  }
}

private func map<T, U>(_ tuple: (T, T), using block: (T) throws -> U) rethrows -> (U, U) {
  return (try block(tuple.0), try block(tuple.1))
}
