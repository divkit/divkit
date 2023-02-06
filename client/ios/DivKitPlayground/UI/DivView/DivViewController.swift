import SwiftUI
import UIKit

import CommonCore
import DivKit
import DivKitExtensions
import LayoutKit

struct DivView: UIViewControllerRepresentable {
  let blockProvider: DivBlockProvider
  let divKitComponents: DivKitComponents

  func makeUIViewController(context _: Context) -> UIViewController {
    DivViewController(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

open class DivViewController: UIViewController {
  private let blockProvider: DivBlockProvider
  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()

  private let cardView = VisibilityTrackingCardView()
  private let scrollView = VisibilityTrackingScrollView()

  private var lastBounds = CGRect.zero

  private var blockView: BlockView! {
    didSet {
      oldValue?.removeFromSuperview()
      scrollView.addSubview(blockView)
    }
  }

  init(
    blockProvider: DivBlockProvider,
    divKitComponents: DivKitComponents
  ) {
    self.blockProvider = blockProvider
    self.divKitComponents = divKitComponents

    super.init(nibName: nil, bundle: nil)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func loadView() {
    scrollView.backgroundColor = .white
    scrollView.cardView = cardView

    blockProvider.parentScrollView = scrollView

    view = scrollView
  }

  public override func viewDidLoad() {
    super.viewDidLoad()

    let pinchToZoomExtensionHandler = PinchToZoomExtensionHandler(overlayView: view)
    divKitComponents.extensionHandlers = divKitComponents.extensionHandlers.filter {
      $0.id != pinchToZoomExtensionHandler.id
    }
    divKitComponents.extensionHandlers.append(pinchToZoomExtensionHandler)
  }

  public override func viewWillAppear(_: Bool) {
    blockProvider.$block
      .currentAndNewValues
      .addObserver(updateBlockView)
      .dispose(in: disposePool)
  }

  public override func viewDidAppear(_: Bool) {
    onVisibleBoundsChanged(to: scrollView.bounds)
  }

  public override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()

    let blockSize = blockProvider.block.size(forResizableBlockSize: view.bounds.size)
    cardView.frame = CGRect(origin: .zero, size: blockSize)
    blockView.frame = cardView.bounds
    scrollView.contentSize = blockSize

    onVisibleBoundsChanged(to: scrollView.bounds)
  }

  public override func viewDidDisappear(_: Bool) {
    disposePool.drain()

    onVisibleBoundsChanged(to: .zero)
  }

  open func onViewUpdated() {}

  private func updateBlockView(block: Block) {
    let elementStateObserver = self
    if let blockView = blockView, block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: elementStateObserver,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    } else {
      blockView = block.makeBlockView(observer: elementStateObserver)
      cardView.blockView = blockView
    }

    view.setNeedsLayout()

    onViewUpdated()
  }

  private func onVisibleBoundsChanged(to: CGRect) {
    let from = lastBounds
    lastBounds = to
    scrollView.onVisibleBoundsChanged(from: from, to: to)
  }
}

extension DivViewController: ElementStateObserver {
  public func elementStateChanged(_ state: ElementState, forPath path: UIElementPath) {
    divKitComponents.blockStateStorage.elementStateChanged(state, forPath: path)
    blockProvider.update(reasons: [])
  }
}

extension DivViewController: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from _: AnyObject) {
    handle(event.payload)
    blockProvider.update(reasons: [])
  }

  private func handle(_ payload: UserInterfaceAction.Payload) {
    switch payload {
    case .composite, .empty, .json, .url:
      break
    case let .menu(menu):
      showMenu(menu, actionPerformer: self)
    case let .divAction(params):
      divKitComponents.handleActions(params: params)
    }
  }
}
