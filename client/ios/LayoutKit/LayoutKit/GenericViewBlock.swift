import CoreGraphics
import VGSL

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

  public var intrinsicContentWidth: CGFloat {
    switch width {
    case .resizable: 0
    case let .fixed(width): width
    case let .intrinsic(calculator): calculator.calculateWidth()
    }
  }

  public var debugDescription: String {
    "Generic: \(dbgStr(content.currentValue)), W:\(dbgStr(width)), H:\(dbgStr(height))"
  }

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

  public func intrinsicContentHeight(forWidth width: CGFloat) -> CGFloat {
    switch height {
    case .resizable: 0
    case let .fixed(height): height
    case let .intrinsic(calculator): calculator.calculateHeight(width: width)
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

}

extension GenericViewBlock: LayoutCachingDefaultImpl {}
extension GenericViewBlock: ElementStateUpdatingDefaultImpl {}

#if os(iOS)
extension GenericViewBlock {
  public static func makeIntrinsicSized(for view: ViewType) -> GenericViewBlock {
    let calculator = ClosureIntrinsicCalculator(
      widthGetter: { view.sizeThatFits(.infinite).width },
      heightGetter: { width in
        view.sizeThatFits(CGSize(width: width, height: .infinity)).height
      }
    )
    let block = GenericViewBlock(
      content: .view(view),
      width: .intrinsic(calculator),
      height: .intrinsic(calculator)
    )
    return block
  }

  public static func makeIntrinsicSized(for view: Lazy<some ViewType>) -> GenericViewBlock {
    let calculator = ClosureIntrinsicCalculator(
      widthGetter: { view.value.sizeThatFits(.infinite).width },
      heightGetter: { width in
        view.value.sizeThatFits(CGSize(width: width, height: .infinity)).height
      }
    )
    let block = GenericViewBlock(
      lazyContent: view.map { .view($0) },
      width: .intrinsic(calculator),
      height: .intrinsic(calculator)
    )
    return block
  }
}
#endif

extension GenericViewBlock.Content {
  static func ===(lhs: GenericViewBlock.Content, rhs: GenericViewBlock.Content) -> Bool {
    switch (lhs, rhs) {
    case let (.view(lView), .view(rView)): lView === rView
    case let (.layer(lView), .layer(rView)): lView === rView
    case (.view, _), (.layer, _): false
    }
  }
}

extension GenericViewBlock.Trait {
  public static func ==(lhs: GenericViewBlock.Trait, rhs: GenericViewBlock.Trait) -> Bool {
    switch (lhs, rhs) {
    case (.resizable, .resizable): true
    case let (.fixed(lhs), .fixed(rhs)): lhs == rhs
    case let (.intrinsic(lhs), .intrinsic(rhs)): lhs === rhs
    case (.resizable, _), (.fixed, _), (.intrinsic, _): false
    }
  }
}

extension CGFloat? {
  fileprivate var trait: GenericViewBlock.Trait {
    map(GenericViewBlock.Trait.fixed) ?? GenericViewBlock.Trait.resizable
  }
}

extension GenericViewBlock.Trait {
  fileprivate var layoutTrait: LayoutTrait {
    switch self {
    case .resizable: .resizable
    case .intrinsic: .intrinsic
    case let .fixed(size): .fixed(size)
    }
  }
}

extension GenericViewBlock.Content? {
  static func !==(lhs: GenericViewBlock.Content?, rhs: GenericViewBlock.Content?) -> Bool {
    switch (lhs, rhs) {
    case let (lValue?, rValue?): !(lValue === rValue)
    case (nil, nil): false
    case (nil, _), (.some, _): true
    }
  }

  static func ===(lhs: GenericViewBlock.Content?, rhs: GenericViewBlock.Content?) -> Bool {
    !(lhs !== rhs)
  }
}
