import CommonCorePublic

public final class GradientBlock: BlockWithTraits {
  public let gradient: Gradient
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  public init(
    gradient: Gradient,
    widthTrait: LayoutTrait = .resizable,
    heightTrait: LayoutTrait = .resizable
  ) {
    self.gradient = gradient
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public var intrinsicContentWidth: CGFloat {
    switch widthTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, _):
      return minSize
    case .weighted:
      return 0
    }
  }

  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    switch heightTrait {
    case let .fixed(value):
      return value
    case let .intrinsic(_, minSize, _):
      return minSize
    case .weighted:
      return 0
    }
  }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? GradientBlock else { return false }
    return gradient == other.gradient
      && widthTrait == other.widthTrait
      && heightTrait == other.heightTrait
  }

  public var debugDescription: String {
    "\(widthTrait) x \(heightTrait) Gradient \(gradient)"
  }

  public func getImageHolders() -> [ImageHolder] { [] }
}

extension GradientBlock: LayoutCachingDefaultImpl {}
extension GradientBlock: ElementStateUpdatingDefaultImpl {}

#if os(iOS)
import UIKit

extension GradientBlock {
  public static func makeBlockView() -> BlockView {
    Background.makeBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    Background.gradient(gradient).canConfigureBlockView(view)
  }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    Background
      .gradient(gradient)
      .configureBlockView(
        view,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
  }
}
#endif
