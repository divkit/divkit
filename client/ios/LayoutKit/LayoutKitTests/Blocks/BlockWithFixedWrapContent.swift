import Foundation

import LayoutKit
import VGSL

final class BlockWithFixedWrapContent: BlockWithTraits {
  var width: CGFloat
  var height: CGFloat

  init(
    width: CGFloat = 0,
    height: CGFloat = 0,
    constrainedHorizontally: Bool = false,
    constrainedVertically: Bool = false
  ) {
    self.width = width
    self.height = height
    self.widthTrait = .intrinsic(
      constrained: constrainedHorizontally,
      minSize: 0,
      maxSize: .infinity
    )
    self.heightTrait = .intrinsic(
      constrained: constrainedVertically,
      minSize: 0,
      maxSize: .infinity
    )
  }

  var widthTrait: LayoutTrait

  var heightTrait: LayoutTrait

  var intrinsicContentWidth: CGFloat {
    width
  }

  func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat {
    height
  }
}

extension BlockWithFixedWrapContent {
  func equals(_: LayoutKit.Block) -> Bool {
    false
  }

  func configureBlockView(
    _: LayoutKit.BlockView,
    observer _: LayoutKit.ElementStateObserver?,
    overscrollDelegate _: ScrollDelegate?,
    renderingDelegate _: LayoutKit.RenderingDelegate?
  ) {}

  var debugDescription: String { "" }

  static func makeBlockView() -> BlockView {
    #if os(iOS)
    NSObject() as! BlockView
    #else
    NSObject() as BlockView
    #endif
  }

  func canConfigureBlockView(_: BlockView) -> Bool {
    false
  }

  func getImageHolders() -> [ImageHolder] {
    []
  }
}

extension BlockWithFixedWrapContent: LayoutCachingDefaultImpl {}
extension BlockWithFixedWrapContent: ElementStateUpdatingDefaultImpl {}
