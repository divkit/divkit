import UIKit

import BasePublic
import CommonCorePublic
import LayoutKit

public final class DivView: VisibleBoundsTrackingView {
  private let divKitComponents: DivKitComponents
  private let blockProvider: DivBlockProvider
  private let disposePool = AutodisposePool()

  private var blockView: BlockView! {
    didSet {
      if blockView !== oldValue {
        oldValue?.removeFromSuperview()
        addSubview(blockView)
      }
    }
  }

  public init(divKitComponents: DivKitComponents) {
    self.divKitComponents = divKitComponents
    self.blockProvider = DivBlockProvider(divKitComponents: divKitComponents)

    super.init(frame: .zero)

    blockProvider.$block.currentAndNewValues.addObserver { [weak self] in
      self?.update(block: $0)
    }.dispose(in: disposePool)
  }

  public func setSource(
    _ source: DivBlockProvider.Source,
    debugParams: DebugParams = DebugParams(),
    shouldResetPreviousCardData: Bool = false
  ) {
    blockProvider.setSource(
      source,
      debugParams: debugParams,
      shouldResetPreviousCardData: shouldResetPreviousCardData
    )
  }

  public func setParentScrollView(_ parentScrollView: ScrollView) {
    blockProvider.parentScrollView = parentScrollView
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func layoutSubviews() {
    super.layoutSubviews()

    let blockSize = blockProvider.block.size(forResizableBlockSize: bounds.size)
    blockView.frame = CGRect(origin: .zero, size: blockSize)

    if bounds.size != blockSize {
      invalidateIntrinsicContentSize()
    }
  }

  public func intrinsicContentSize(for availableSize: CGSize) -> CGSize {
    blockProvider.block.size(forResizableBlockSize: availableSize)
  }

  public override var intrinsicContentSize: CGSize {
    blockProvider.block.size(forResizableBlockSize: bounds.size)
  }

  private func update(block: Block) {
    if let blockView = blockView, block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: self,
        overscrollDelegate: nil,
        renderingDelegate: divKitComponents.tooltipManager
      )
    } else {
      blockView = block.makeBlockView(
        observer: self,
        renderingDelegate: divKitComponents.tooltipManager
      )
    }
    setNeedsLayout()
  }

  public func onVisibleBoundsChanged(from: CGRect, to: CGRect) {
    blockView.onVisibleBoundsChanged(from: from, to: to)
  }
}

extension DivView: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    divKitComponents.blockStateStorage.elementStateChanged(state, forPath: path)
    blockProvider.update(withStates: [path: state])
  }
}

extension DivView: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    switch event.payload {
    case .composite, .empty, .json, .url:
      break
    case let .menu(menu):
      nearestViewController?.showMenu(menu, actionPerformer: self)
    case let .divAction(params):
      divKitComponents.actionHandler.handle(params: params, sender: sender)
    }
  }
}

extension UIResponder {
  fileprivate var nearestViewController: UIViewController? {
    next as? UIViewController ?? next?.nearestViewController
  }
}

extension UIViewController {
  fileprivate func showMenu(
    _ menu: Menu,
    actionPerformer: UIActionEventPerforming
  ) {
    let alert = UIAlertController(title: nil, message: nil, preferredStyle: .actionSheet)
    menu.items.forEach { item in
      let action = UIAlertAction(title: item.text, style: .default) { _ in
        let events = item.actions.map {
          UIActionEvent(uiAction: $0, originalSender: self)
        }
        actionPerformer.perform(uiActionEvents: events, from: self)
      }
      alert.addAction(action)
    }
    present(alert, animated: true)
  }
}
