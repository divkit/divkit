import UIKit

import CommonCore
import DivKit
import LayoutKit

final class DivHostView: UIScrollView {
  private struct State {
    var block: Block
    var view: BlockView
  }
  
  private let components: DivKitComponents
  
  private var oldBounds: CGRect = .zero

  private var state: State? {
    didSet {
      if let oldView = oldValue?.view, oldView !== state?.view {
        oldValue?.view.removeFromSuperview()
      }
      if let view = state?.view {
        addSubview(view)
        setNeedsLayout()
      }
    }
  }

  init(urlOpener: @escaping UrlOpener) {
    components = DivKitComponents(
      updateCardAction: nil,
      urlOpener: urlOpener
    )
    super.init(frame: .zero)
  }

  required init?(coder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  func setCard(_ card: DivData) throws {
    let context = components.makeContext(
      cardId: DivCardID(rawValue: card.logId),
      cachedImageHolders: []
    )
    let block = try card.makeBlock(context: context)
    let view = block.reuse(
      state?.view,
      observer: nil,
      overscrollDelegate: nil,
      renderingDelegate: nil,
      superview: self
    )
    state = State(block: block, view: view)
  }

  override func layoutSubviews() {
    super.layoutSubviews()

    if let state = state {
      let height = state.block.heightOfVerticallyNonResizableBlock(forWidth: bounds.width)
      state.view.frame = CGRect(x: 0, y: 0, width: bounds.width, height: height)
    }

    if oldBounds != bounds {
      passVisibleBoundsChanged(from: oldBounds, to: bounds)
      oldBounds = bounds
    }
  }
}

extension DivHostView: UIActionEventPerforming {
  func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    switch event.payload {
    case let .divAction(params):
      components.actionHandler.handle(
        params: params,
        urlOpener: components.urlOpener
      )
    case .empty,
         .url,
         .menu,
         .json,
         .composite:
      break
    }
  }
}

extension DivHostView: VisibleBoundsTrackingContainer {
  var visibleBoundsTrackingSubviews: [VisibleBoundsTrackingView] {
    (state?.view).asArray()
  }
}
