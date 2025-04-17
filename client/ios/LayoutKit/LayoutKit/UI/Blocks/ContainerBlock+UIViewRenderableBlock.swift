import CoreGraphics
import Foundation
import UIKit
import VGSL

extension ContainerBlock {
  public static func makeBlockView() -> BlockView { ContainerBlockView() }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is ContainerBlockView
  }

  func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! ContainerBlockView

    let model = ContainerBlockView.Model(
      children: children,
      separator: separator,
      lineSeparator: lineSeparator,
      gaps: gaps,
      blockLayoutDirection: blockLayoutDirection,
      layoutDirection: layoutDirection,
      layoutMode: layoutMode,
      axialAlignment: axialAlignment,
      crossAlignment: crossAlignment,
      contentAnimation: contentAnimation,
      anchorPoint: anchorPoint,
      layout: layout,
      source: Variable { [weak self] in self },
      childrenTransform: childrenTransform,
      clipContent: clipContent,
      accessibility: accessibilityElement
    )

    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class ContainerBlockView: UIView, BlockViewProtocol, VisibleBoundsTrackingContainer {
  struct Model: ReferenceEquatable {
    let children: [ContainerBlock.Child]
    let separator: ContainerBlock.Separator?
    let lineSeparator: ContainerBlock.Separator?
    let gaps: [CGFloat]
    let blockLayoutDirection: UserInterfaceLayoutDirection
    let layoutDirection: ContainerBlock.LayoutDirection
    let layoutMode: ContainerBlock.LayoutMode
    let axialAlignment: ContainerBlock.AxialAlignment
    let crossAlignment: ContainerBlock.CrossAlignment
    let contentAnimation: BlockAnimation?
    let anchorPoint: AnchorPoint
    let layout: ContainerBlockLayout?
    let source: Variable<AnyObject?>
    let childrenTransform: CGAffineTransform
    let clipContent: Bool
    let accessibility: AccessibilityElement?
  }

  private var blockViews: [BlockView] = []

  var layoutReporter: LayoutReporter?
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockViews }
  var effectiveBackgroundColor: UIColor? { blockViews.first?.effectiveBackgroundColor }

  // layoutSubivews is called multiple times for same view size and model, so we optimize out
  // redundant calls
  private var modelAndLastLayoutSize: (model: Model?, lastLayoutSize: CGSize?)
  private var preventLayoutAndConfig = false

  private var model: Model! {
    modelAndLastLayoutSize.model
  }

  private weak var observer: ElementStateObserver?
  private weak var overscrollDelegate: ScrollDelegate?
  private weak var renderingDelegate: RenderingDelegate?

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard model != modelAndLastLayoutSize.model ||
      observer !== self.observer,
      !preventLayoutAndConfig
    else {
      return
    }

    applyAccessibilityFromScratch(model.accessibility)
    modelAndLastLayoutSize = (model: model, lastLayoutSize: nil)
    self.observer = observer
    self.overscrollDelegate = overscrollDelegate
    self.renderingDelegate = renderingDelegate

    // Configuring views may lead to unpredictable side effects,
    // including view hierarchy layout.
    preventLayoutAndConfig = true
    blockViews = blockViews.reused(
      with: model.children.map(\.content),
      attachTo: self,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )

    preventLayoutAndConfig = false

    setNeedsLayout()

    clipsToBounds = model.clipContent

    let animationKey = "contentAnimation"
    layer.removeAnimation(forKey: animationKey)
    if let animation = model.contentAnimation?.keyFrameAnimation {
      layer.add(animation, forKey: animationKey)
    }
  }

  override func layoutSubviews() {
    guard !preventLayoutAndConfig else { return }

    super.layoutSubviews()

    guard !model.children.isEmpty else { return }

    if let lastLayoutSize = modelAndLastLayoutSize.lastLayoutSize, bounds.size == lastLayoutSize {
      return
    }
    layoutReporter?.willLayoutSubviews()

    let layout = model.layout ?? ContainerBlockLayout(
      children: model.children,
      separator: model.separator,
      lineSeparator: model.lineSeparator,
      gaps: model.gaps,
      blockLayoutDirection: model.blockLayoutDirection,
      layoutDirection: model.layoutDirection,
      layoutMode: model.layoutMode,
      axialAlignment: model.axialAlignment,
      crossAlignment: model.crossAlignment,
      size: bounds.size
    )

    if model.children != layout.childrenWithSeparators ||
      blockViews.count != layout.blockFrames.count {
      blockViews = blockViews.reused(
        with: layout.childrenWithSeparators.map(\.content),
        attachTo: self,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    }

    for (view, frame) in zip(blockViews, layout.blockFrames) {
      // if a view’s transform is not the identity transform, you should not set its frame
      // set bounds.size and center instead
      view.bounds.size = frame.size
      view.center = CGPoint(
        x: frame.origin.x + model.anchorPoint.x.value(for: frame.width) * frame.width,
        y: frame.origin.y + model.anchorPoint.y.value(for: frame.height) * frame.height
      )
      view.transform = model.childrenTransform
      view.layer.anchorPoint = model.anchorPoint.calculateCGPoint(for: view.bounds)
    }

    modelAndLastLayoutSize = (model: model, lastLayoutSize: bounds.size)
    layoutReporter?.didLayoutSubviews()
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result === self ? nil : result
  }
}
