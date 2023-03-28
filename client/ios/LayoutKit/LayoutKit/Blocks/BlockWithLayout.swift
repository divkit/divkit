import CoreGraphics

import CommonCorePublic

#if os(iOS)
import UIKit

protocol BlockWithLayoutRenderingImpl {
  associatedtype Layout
  func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  )
}

#else
protocol BlockWithLayoutRenderingImpl {
  associatedtype Layout
}
#endif

protocol BlockWithLayout: Block, BlockWithLayoutRenderingImpl {
  func laidOutHierarchy(for size: CGSize) -> (Self, Layout)
}

extension BlockWithLayout {
  public func laidOut(for width: CGFloat) -> Block {
    LaidOutBlock(block: self, width: width)
  }

  public func laidOut(for size: CGSize) -> Block {
    LaidOutBlock(block: self, size: size)
  }
}
