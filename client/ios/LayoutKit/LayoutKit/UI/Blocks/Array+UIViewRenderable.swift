import UIKit
import VGSL

extension [BlockView] {
  func reused(
    with blocks: [UIViewRenderable],
    attachTo parent: UIView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) -> [BlockView] {
    let reuseResult = calculateReusability(self, blocks: blocks)
    let newViews: [BlockView] = reuseResult.reusability.map { block, view in
      if let view {
        block.configureBlockViewWithReporting(
          view,
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
        parent.bringSubviewToFront(view)
        return view
      } else {
        let view = block.makeBlockView(
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
        parent.addSubview(view)
        return view
      }
    }
    reuseResult.orphanViews.forEach { $0.removeFromSuperview() }
    return newViews
  }
}

private struct ReuseResult {
  let reusability: [(UIViewRenderable, BlockView?)]
  let orphanViews: [BlockView]

  init(
    reusability: [(UIViewRenderable, BlockView?)],
    orphanViews: [BlockView]
  ) {
    self.reusability = reusability
    self.orphanViews = orphanViews
  }
}

private func calculateReusability(
  _ views: [BlockView],
  blocks: [UIViewRenderable]
) -> ReuseResult {
  var orphanViews = views
  var reusableViews = [Int: BlockView](minimumCapacity: blocks.count)
  for (blockIndex, block) in blocks.enumerated() {
    let reusableView = orphanViews
      .enumerated()
      .first { block.isBestViewForReuse($0.element) }
    if let (viewIndex, view) = reusableView {
      orphanViews.remove(at: viewIndex)
      reusableViews[blockIndex] = view
    }
  }

  let reusability: [(UIViewRenderable, BlockView?)] = blocks
    .enumerated()
    .map { blockIndex, block in
      if let view = reusableViews[blockIndex] {
        return (block, view)
      }
      let reusableView = orphanViews
        .enumerated()
        .first { block.canConfigureBlockView($0.element) }
      if let (index, view) = reusableView {
        orphanViews.remove(at: index)
        return (block, view)
      }
      return (block, nil)
    }

  return ReuseResult(
    reusability: reusability,
    orphanViews: orphanViews
  )
}
