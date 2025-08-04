import CoreGraphics
import Foundation
import VGSL

public final class SwitchBlock: Block {
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait
  public let on: Binding<Bool>
  public let enabled: Bool
  public let action: UserInterfaceAction?
  public let onTintColor: Color?
  public let accessibilityElement: AccessibilityElement?

  public let isVerticallyConstrained = false
  public let isHorizontallyConstrained = false

  public var isVerticallyResizable: Bool { heightTrait.isResizable }
  public var isHorizontallyResizable: Bool { widthTrait.isResizable }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      value
    case .intrinsic:
      uiSwitchSize.width
    case .weighted:
      0
    }
  }

  public var widthOfHorizontallyNonResizableBlock: CGFloat { uiSwitchSize.width }
  public var weightOfVerticallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public var weightOfHorizontallyResizableBlock: LayoutTrait.Weight {
    assertionFailure("try to get weight for non resizable block")
    return .default
  }

  public init(
    widthTrait: LayoutTrait = .intrinsic,
    heightTrait: LayoutTrait = .intrinsic,
    on: Bool,
    enabled: Bool,
    action: UserInterfaceAction?,
    onTintColor: Color? = nil,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.on = Binding(name: "", value: Property(booleanLiteral: on))
    self.enabled = enabled
    self.action = action
    self.onTintColor = onTintColor
    self.accessibilityElement = accessibilityElement
  }

  public init(
    widthTrait: LayoutTrait = .intrinsic,
    heightTrait: LayoutTrait = .intrinsic,
    on: Binding<Bool>,
    enabled: Bool,
    action: UserInterfaceAction?,
    onTintColor: Color? = nil,
    accessibilityElement: AccessibilityElement? = nil
  ) {
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.on = on
    self.enabled = enabled
    self.action = action
    self.onTintColor = onTintColor
    self.accessibilityElement = accessibilityElement
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      value
    case .intrinsic:
      uiSwitchSize.height
    case .weighted:
      0
    }
  }

  public func heightOfVerticallyNonResizableBlock(forWidth _: CGFloat) -> CGFloat {
    uiSwitchSize.height
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
  lhs.on.value == rhs.on.value &&
    lhs.enabled == rhs.enabled &&
    lhs.action == rhs.action &&
    lhs.onTintColor == rhs.onTintColor &&
    lhs.accessibilityElement == rhs.accessibilityElement
}

extension SwitchBlock: LayoutCachingDefaultImpl {}
extension SwitchBlock: ElementStateUpdatingDefaultImpl {}
