import Foundation
import UIKit

import CommonCorePublic

extension ShadedBlock {
  public static func makeBlockView() -> BlockView { ShadedBlockView() }

  public func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! ShadedBlockView
    let model = ShadedBlockView.Model(
      block: block,
      shadow: shadow,
      uniqueID: ObjectIdentifier(self)
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is ShadedBlockView
  }
}

private final class ShadedBlockView: ViewWithShadow, BlockViewProtocol,
  VisibleBoundsTrackingContainer {
  struct Model: Equatable {
    let block: Block
    let shadow: BlockShadow
    let uniqueID: ObjectIdentifier

    static func ==(lhs: Model, rhs: Model) -> Bool {
      lhs.uniqueID == rhs.uniqueID
    }
  }

  private var model: Model!
  private weak var observer: ElementStateObserver?

  private var blockView: BlockView! {
    didSet { contentView = blockView }
  }

  public var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { blockView.asArray() }

  var effectiveBackgroundColor: UIColor? { blockView.effectiveBackgroundColor }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    guard self.model != model || self.observer !== observer else { return }
    self.model = model
    self.observer = observer
    if let blockView = blockView, model.block.canConfigureBlockView(blockView) {
      model.block.configureBlockView(
        blockView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
    } else {
      blockView = model.block.makeBlockView(
        observer: observer,
        overscrollDelegate: overscrollDelegate
      )
    }
    shadow = model.shadow
  }
}
