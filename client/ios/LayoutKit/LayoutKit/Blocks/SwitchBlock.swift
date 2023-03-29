import CoreGraphics
import Foundation

import BasePublic
import BaseUIPublic
import CommonCorePublic
import LayoutKitInterface

public final class SwitchBlock: Block {
  public let on: Bool
  public let enabled: Bool
  public let action: UserInterfaceAction?
  public let onTintColor: Color?
  public let accessibilityElement: AccessibilityElement?

  public init(
    on: Bool,
    enabled: Bool,
    action: UserInterfaceAction?,
    onTintColor: Color? = nil,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.on = on
    self.enabled = enabled
    self.action = action
    self.onTintColor = onTintColor
    self.accessibilityElement = accessibilityElement
  }

  public let isVerticallyResizable = false
  public let isHorizontallyResizable = false

  public let isVerticallyConstrained = false
  public let isHorizontallyConstrained = false

  public var intrinsicContentWidth: CGFloat {
    uiSwitchSize.width
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    uiSwitchSize.height
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat { uiSwitchSize.width }
  public func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    uiSwitchSize.height
  }

  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }
  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? SwitchBlock else {
      return false
    }

    return self == other
  }

  public func getImageHolders() -> [ImageHolder] { [] }
}

public func ==(lhs: SwitchBlock, rhs: SwitchBlock) -> Bool {
  lhs.on == rhs.on &&
    lhs.enabled == rhs.enabled &&
    lhs.action == rhs.action &&
    lhs.onTintColor == rhs.onTintColor &&
    lhs.accessibilityElement == rhs.accessibilityElement
}

extension SwitchBlock: LayoutCachingDefaultImpl {}
extension SwitchBlock: ElementStateUpdatingDefaultImpl {}
