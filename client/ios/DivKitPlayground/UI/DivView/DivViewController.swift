import SwiftUI
import UIKit

import CommonCorePublic
import DivKit
import DivKitExtensions
import LayoutKit

struct DivView: UIViewControllerRepresentable {
  let jsonProvider: Signal<[String: Any]>
  let divKitComponents: DivKitComponents

  func makeUIViewController(context _: Context) -> UIViewController {
    DivViewController(
      jsonProvider: jsonProvider,
      divKitComponents: divKitComponents,
      debugParams: AppComponents.debugParams
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

open class DivViewController: UIViewController {
  private let blockProvider: DivBlockProvider
  private let divKitComponents: DivKitComponents
  private let disposePool = AutodisposePool()
  private let scrollView = VisibilityTrackingScrollView()
  private var lastBounds = CGRect.zero

  init(
    jsonProvider: Signal<[String: Any]>,
    divKitComponents: DivKitComponents,
    debugParams: DebugParams
  ) {
    self.blockProvider = DivBlockProvider(divKitComponents: divKitComponents)
    self.divKitComponents = divKitComponents

    super.init(nibName: nil, bundle: nil)

    jsonProvider.addObserver { [weak self] in
      guard let self else { return }
      blockProvider.setSource(
        .init(
          kind: .json($0),
          cardId: "DivViewCard",
          parentScrollView: scrollView,
          debugParams: debugParams
        ),
        shouldResetPreviousCardData: true
      )
    }.dispose(in: disposePool)
  }

  @available(*, unavailable)
  public required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  public override func loadView() {
    scrollView.backgroundColor = .white
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
    scrollView.cardView?.frame = CGRect(origin: .zero, size: blockSize)
    scrollView.contentSize = blockSize
    scrollView.cardView?.layoutIfNeeded()
  }

  public override func viewDidLayoutSubviews() {
    onVisibleBoundsChanged(to: scrollView.bounds)
  }

  public override func viewDidDisappear(_: Bool) {
    disposePool.drain()

    onVisibleBoundsChanged(to: .zero)
  }

  open func onViewUpdated() {}

  private func updateBlockView(block: Block) {
    let elementStateObserver = self
    let renderingDelegate = divKitComponents.tooltipManager
    if let blockView = scrollView.cardView, block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: elementStateObserver,
        overscrollDelegate: nil,
        renderingDelegate: renderingDelegate
      )
    } else {
      scrollView.cardView = block.makeBlockView(
        observer: elementStateObserver,
        renderingDelegate: renderingDelegate
      )
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
    blockProvider.update(withStates: [path: state])
  }
}

extension DivViewController: UIActionEventPerforming {
  public func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    switch event.payload {
    case .composite, .empty, .json, .url:
      break
    case let .menu(menu):
      showMenu(menu, actionPerformer: self)
    case let .divAction(params):
      divKitComponents.actionHandler.handle(params: params, sender: sender)
    }
  }
}
