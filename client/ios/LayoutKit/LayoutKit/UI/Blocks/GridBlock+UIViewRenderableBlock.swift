import UIKit

import CommonCorePublic

extension GridBlock {
  public static func makeBlockView() -> BlockView {
    GridView()
  }

  public func configureBlockView(
    _ view: BlockView,
    with layout: GridBlock.Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let model = GridView.Model(
      layout: layout,
      contentAlignment: contentAlignment,
      items: items,
      grid: grid,
      source: Variable { [weak self] in self }
    )
    (view as! GridView)
      .setModel(
        model,
        withObserver: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is GridView
  }
}

private final class GridView: BlockView, VisibleBoundsTrackingContainer {
  struct Model: ReferenceEquatable {
    let layout: GridBlock.Layout?
    let contentAlignment: BlockAlignment2D
    let items: [GridBlock.Item]
    let grid: Grid
    let source: Variable<AnyObject?>
  }

  private var blockViews: [BlockView] = []
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockViews }
  var effectiveBackgroundColor: UIColor? { backgroundColor }

  private weak var observer: ElementStateObserver?
  private var modelAndLastLayoutSize: (model: Model?, lastLayoutSize: CGSize?)
  private var preventLayout = false

  func setModel(
    _ model: Model,
    withObserver observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard model != modelAndLastLayoutSize.model || observer !== self.observer else {
      return
    }

    modelAndLastLayoutSize = (model, nil)
    self.observer = observer

    // Configuring views may lead to unpredictable side effects,
    // including view hierarchy layout.
    preventLayout = true
    blockViews = blockViews.reused(
      with: model.items.map { $0.contents },
      attachTo: self,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    preventLayout = false

    setNeedsLayout()
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }

  override func layoutSubviews() {
    guard !preventLayout else { return }

    super.layoutSubviews()

    guard let model = modelAndLastLayoutSize.model,
          modelAndLastLayoutSize.lastLayoutSize != bounds.size else {
      return
    }
    modelAndLastLayoutSize.lastLayoutSize = bounds.size

    let layout = model.layout ?? GridBlock.Layout(
      size: bounds.size,
      items: model.items,
      grid: model.grid,
      contentAlignment: model.contentAlignment
    )

    zip(layout.itemFrames, blockViews).forEach {
      $1.frame = $0
    }
  }
}
