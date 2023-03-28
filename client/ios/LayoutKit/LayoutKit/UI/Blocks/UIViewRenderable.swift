import Foundation
import UIKit

import CommonCorePublic

public protocol UIViewRenderable {
  static func makeBlockView() -> BlockView
  func configureBlockView(
    _ view: BlockView,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?
  )
  func canConfigureBlockView(_ view: BlockView) -> Bool
}

extension UIViewRenderable {
  public func makeBlockView(
    observer: ElementStateObserver? = nil,
    overscrollDelegate: ScrollDelegate? = nil,
    renderingDelegate: RenderingDelegate? = nil
  ) -> BlockView {
    let result = type(of: self).makeBlockView()
    configureBlockView(
      result,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate
    )
    return result
  }

  public func configureBlockView(_ view: BlockView) {
    configureBlockView(
      view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil
    )
  }

  public func reuse(
    _ view: BlockView?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    superview: UIView?,
    subviewPosition: SubviewPosition = .trailing
  ) -> BlockView {
    reuseInternal(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: superview,
      subviewPosition: subviewPosition
    )
  }

  public func reusePreservingSuperview(
    _ view: BlockView,
    observer: ElementStateObserver? = nil,
    overscrollDelegate: ScrollDelegate? = nil,
    renderingDelegate: RenderingDelegate? = nil
  ) -> BlockView {
    reuseInternal(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: view.superview
    )
  }

  fileprivate func reuseInternal(
    _ view: BlockView?,
    observer: ElementStateObserver?,
    overscrollDelegate: ScrollDelegate?,
    renderingDelegate: RenderingDelegate?,
    superview: UIView?,
    subviewPosition: SubviewPosition = .trailing
  ) -> BlockView {
    if let view = view, canConfigureBlockView(view) {
      configureBlockView(
        view,
        observer: observer,
        overscrollDelegate: overscrollDelegate,
        renderingDelegate: renderingDelegate
      )
      if view.superview !== superview {
        view.removeFromSuperview()
        switch subviewPosition {
        case .trailing:
          superview?.addSubview(view)
        case let .index(position):
          superview?.insertSubview(view, at: position)
        }
      }
      return view
    } else {
      view?.removeFromSuperview()
      let newView =
        makeBlockView(
          observer: observer,
          overscrollDelegate: overscrollDelegate,
          renderingDelegate: renderingDelegate
        )
      switch subviewPosition {
      case .trailing:
        superview?.addSubview(newView)
      case let .index(position):
        superview?.insertSubview(newView, at: position)
      }
      return newView
    }
  }
}

extension Optional where Wrapped == UIViewRenderable {
  public func reuse(
    _ view: BlockView?,
    observer: ElementStateObserver? = nil,
    overscrollDelegate: ScrollDelegate? = nil,
    renderingDelegate: RenderingDelegate? = nil,
    superview: UIView
  ) -> BlockView? {
    guard case let .some(renderable) = self else {
      view?.removeFromSuperview()
      return nil
    }

    return renderable.reuseInternal(
      view,
      observer: observer,
      overscrollDelegate: overscrollDelegate,
      renderingDelegate: renderingDelegate,
      superview: superview
    )
  }
}

public enum SubviewPosition {
  case trailing
  case index(Int)
}
