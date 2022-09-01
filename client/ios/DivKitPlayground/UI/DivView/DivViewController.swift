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

  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func loadView() {
    scrollView.backgroundColor = .white
    scrollView.cardView = cardView
    view = scrollView
  }

  public override func viewDidLoad() {
    super.viewDidLoad()

    divKitComponents.extensionHandlers = [
      PinchToZoomExtensionHandler(overlayView: view),
    ]

    blockProvider.parentScrollView = scrollView
    blockProvider.$block
      .currentAndNewValues
      .addObserver(updateBlockView)
      .dispose(in: disposePool)
  }

  public override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()

    let blockSize = blockProvider.block.size(forResizableBlockSize: view.bounds.size)
    cardView.frame = CGRect(origin: .zero, size: blockSize)
    blockView.frame = cardView.bounds
    scrollView.contentSize = blockSize

    onVisibleBoundsChanged(to: scrollView.bounds)
  }

  public override func viewDidAppear(_: Bool) {
    onVisibleBoundsChanged(to: scrollView.bounds)
  }

  public override func viewWillDisappear(_: Bool) {
    onVisibleBoundsChanged(to: .zero)
    divKitComponents.reset()
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
    blockProvider.update(patch: nil)
  }
}

extension DivViewController: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    perform(uiActionEvents: [event], from: sender)
  }

  public func perform(uiActionEvents events: [UIActionEvent], from _: AnyObject) {
    events.map { $0.payload }.forEach { handle($0) }
    blockProvider.update(patch: nil)
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
