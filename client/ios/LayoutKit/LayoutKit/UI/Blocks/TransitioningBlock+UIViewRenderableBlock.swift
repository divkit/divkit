import CoreGraphics
import Foundation
import UIKit

import BasePublic
import CommonCorePublic

extension TransitioningBlock {
  public static func makeBlockView() -> BlockView {
    TransitioningBlockView()
  }

  public func canConfigureBlockView(_ view: BlockView) -> Bool {
    view is TransitioningBlockView
  }

  public func configureBlockView(
    _ view: BlockView,
    with layout: Layout?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    let view = view as! TransitioningBlockView
    let model = TransitioningBlockView.Model(
      block: self,
      layout: layout
    )
    view.configure(
      model: model,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
  }
}

private final class TransitioningBlockView: BlockView, VisibleBoundsTrackingContainer {
  typealias Layout = TransitioningBlock.Layout
  struct Model: Equatable {
    let block: TransitioningBlock
    let layout: Layout?
    static func ==(lhs: Model, rhs: Model) -> Bool {
      lhs.block.equals(rhs.block) && lhs.layout == rhs.layout
    }
  }

  private weak var observer: ElementStateObserver?

  private(set) var model: Model?

  private var fromView: BlockView? {
    didSet {
      if fromView !== oldValue {
        oldValue?.removeFromSuperview()
      }
    }
  }

  private var toView: BlockView? {
    didSet {
      if toView !== oldValue {
        oldValue?.removeFromSuperview()
      }
    }
  }

  private var toViewAnimationWorkItem: DispatchWorkItem?

  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] { toView.asArray() }
  var effectiveBackgroundColor: UIColor? { toView?.effectiveBackgroundColor }

  func configure(
    model: Model,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  ) {
    self.model = model
    self.observer = observer

    let animationOut = model.block.animationOut?.sortedChronologically() ?? []
    var animationIn = model.block.animationIn?.sortedChronologically() ?? []
    let animationInDelay = animationIn.first?.delay.value ?? 0
    animationIn = animationIn.withDelay(-animationInDelay)

    fromView = model.block.from?.reuse(
      fromView,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: self
    )

    addAnimationsToView(
      fromView,
      animations: animationOut,
      completion: { self.fromView = nil }
    )

    let item = DispatchWorkItem { [weak self] in
      guard let self = self else { return }
      self.toView = model.block.to.reuse(
        self.toView,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate,
        superview: self
      )

      self.addAnimationsToView(self.toView, animations: animationIn) {
        if UIAccessibility.isVoiceOverRunning {
          self.forRecursiveSubviews {
            if $0.accessibilityElementIsFocused() {
              UIAccessibility.post(notification: .layoutChanged, argument: $0)
            }
          }
        }
      }
    }

    toViewAnimationWorkItem?.cancel()
    toViewAnimationWorkItem = item

    if animationInDelay > 0 {
      DispatchQueue.main.asyncAfter(
        deadline: DispatchTime.now() + animationInDelay,
        execute: item
      )
    } else {
      item.perform()
    }
  }

  private func addAnimationsToView(
    _ view: BlockView?,
    animations: [TransitioningAnimation],
    completion: Action? = nil
  ) {
    guard let view = view else {
      completion?()
      return
    }

    forceLayout()

    view.setInitialParamsAndAnimate(animations: animations, completion: completion)
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    if let view = fromView {
      let currentTransform = view.transform
      view.transform = .identity
      view.frame = bounds
      view.transform = currentTransform
    }

    if let view = toView {
      let currentTransform = view.transform
      view.transform = .identity
      view.frame = bounds
      view.transform = currentTransform
    }
  }

  override func hitTest(_ point: CGPoint, with event: UIEvent?) -> UIView? {
    let result = super.hitTest(point, with: event)
    return result == self ? nil : result
  }
}
