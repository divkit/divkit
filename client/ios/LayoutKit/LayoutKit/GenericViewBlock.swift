import CoreGraphics

import CommonCorePublic

#if os(iOS)
import UIKit
#endif

// This block is intended for easy integration of complex custom views
// into block-based layout
// Using same instance of UIView in multiple GenericViewBlocks
// or configuring multiple UIViews with same GenericViewBlock instance
// leads to undefined behaviour

public protocol IntrinsicCalculator: AnyObject {
  func calculateWidth() -> CGFloat
  func calculateHeight(width: CGFloat) -> CGFloat
}

public final class GenericViewBlock: BlockWithTraits {
  public enum Trait: Equatable {
    case resizable
    case fixed(CGFloat)
    case intrinsic(IntrinsicCalculator)
  }

  #if os(iOS)
  public typealias LayerType = CALayer
  #else
  public typealias LayerType = AnyObject
  #endif

  public enum Content {
    case view(ViewType)
    case layer(LayerType)
  }

  public let content: Lazy<Content>
  public let width: Trait
  public let height: Trait

  public var widthTrait: LayoutTrait { width.layoutTrait }
  public var heightTrait: LayoutTrait { height.layoutTrait }

  public init(
    lazyContent: Lazy<Content>,
    width: Trait,
    height: Trait
  ) {
    self.content = lazyContent
    self.width = width
    self.height = height
  }

  public convenience init(
    content: Content,
    width: Trait,
    height: Trait
  ) {
    self.init(
      lazyContent: Lazy<Content>(
        onMainThreadGetter: { content }
      ),
      width: width,
      height: height
    )
  }

  public convenience init(
    view: ViewType,
    width: CGFloat? = nil,
    height: CGFloat? = nil
  ) {
    self.init(content: .view(view), width: width.trait, height: height.trait)
  }

  public convenience init(
    layer: LayerType,
    width: CGFloat? = nil,
    height: CGFloat? = nil
  ) {
    self.init(content: .layer(layer), width: width.trait, height: height.trait)
  }

  public var intrinsicContentWidth: CGFloat {
    switch width {
    case .resizable: return 0
    case let .fixed(width): return width
    case let .intrinsic(calculator): return calculator.calculateWidth()
    }
  }

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch height {
    case .resizable: return 0
    case let .fixed(height): return height
    case let .intrinsic(calculator): return calculator.calculateHeight(width: width)
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? GenericViewBlock else {
      return false
    }

    return content.currentValue === other.content.currentValue && width == other
      .width && height == other.height
  }

  public func getImageHolders() -> [ImageHolder] { [] }

  public var debugDescription: String {
    "Generic: \(dbgStr(content.currentValue)), W:\(dbgStr(width)), H:\(dbgStr(height))"
  }
}

extension GenericViewBlock: LayoutCachingDefaultImpl {}
extension GenericViewBlock: ElementStateUpdatingDefaultImpl {}

extension GenericViewBlock.Content {
  static func ===(lhs: GenericViewBlock.Content, rhs: GenericViewBlock.Content) -> Bool {
    switch (lhs, rhs) {
    case let (.view(lView), .view(rView)): return lView === rView
    case let (.layer(lView), .layer(rView)): return lView === rView
    case (.view, _), (.layer, _): return false
    }
  }
}

extension GenericViewBlock.Trait {
  public static func ==(lhs: GenericViewBlock.Trait, rhs: GenericViewBlock.Trait) -> Bool {
    switch (lhs, rhs) {
    case (.resizable, .resizable): return true
    case let (.fixed(lhs), .fixed(rhs)): return lhs == rhs
    case let (.intrinsic(lhs), .intrinsic(rhs)): return lhs === rhs
    case (.resizable, _), (.fixed, _), (.intrinsic, _): return false
    }
  }
}

extension Optional where Wrapped == CGFloat {
  fileprivate var trait: GenericViewBlock.Trait {
    map(GenericViewBlock.Trait.fixed) ?? GenericViewBlock.Trait.resizable
  }
}

extension GenericViewBlock.Trait {
  fileprivate var layoutTrait: LayoutTrait {
    switch self {
    case .resizable: return .resizable
    case .intrinsic: return .intrinsic
    case let .fixed(size): return .fixed(size)
    }
  }
}

extension Optional where Wrapped == GenericViewBlock.Content {
  static func !==(lhs: GenericViewBlock.Content?, rhs: GenericViewBlock.Content?) -> Bool {
    switch (lhs, rhs) {
    case let (lValue?, rValue?): return !(lValue === rValue)
    case (nil, nil): return false
    case (nil, _), (.some, _): return true
    }
  }

  static func ===(lhs: GenericViewBlock.Content?, rhs: GenericViewBlock.Content?) -> Bool {
    !(lhs !== rhs)
  }
}
