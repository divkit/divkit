import UIKit

import CommonCorePublic

extension Array where Element == BlockView {
  func reused(
    with blocks: [UIViewRenderable],
    attachTo parent: UIView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) -> [BlockView] {
    let reuseResult = calculateReusabilityFor(self, with: blocks) { $1.canConfigureBlockView($0) }
    let newViews: [BlockView] = reuseResult.modelsReusability.map {
      switch $0.1 {
      case let .hasReusableObject(view):
        $0.0.configureBlockView(
          view,
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
        parent.bringSubviewToFront(view)
        return view
      case .orphan:
        let view = $0.0.makeBlockView(
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
        parent.addSubview(view)
        return view
      }
    }
    reuseResult.orphanObjects.forEach { $0.removeFromSuperview() }
    return newViews
  }
}
