import SwiftUI
import UIKit

import BaseUI
import CommonCore
import LayoutKit

typealias ScreenshotCallback = (ScreenshotInfo) -> Void

struct ScreenshotInfo {
  let data: Data
  let density: Double
  let height: Double
  let width: Double
}

struct WebPreviewViewRepresentable: UIViewControllerRepresentable {
  let blockProvider: BlockProvider
  let onScreenshotTaken: ScreenshotCallback

  func makeUIViewController(context _: Context) -> UIViewController {
    WebPreviewViewController(
      blockProvider: blockProvider,
      onScreenshotTaken: onScreenshotTaken
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

private final class WebPreviewViewController: UIViewController {
  private let blockProvider: BlockProvider
  private let onScreenshotTaken: ScreenshotCallback
  private let disposePool = AutodisposePool()
  private lazy var scrollView = UIScrollView()

  private var blockView: BlockView? {
    didSet {
      oldValue?.removeFromSuperview()
      blockView.map { scrollView.addSubview($0) }
    }
  }

  private var lastScreenshotDate: Date?
  private var screenshotCancellationToken: Cancellable?

  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  init(
    blockProvider: BlockProvider,
    onScreenshotTaken: @escaping ScreenshotCallback
  ) {
    self.blockProvider = blockProvider
    self.onScreenshotTaken = onScreenshotTaken

    super.init(nibName: nil, bundle: nil)

    blockProvider.$block.currentAndNewValues
      .addObserver(updateBlockView)
      .dispose(in: disposePool)
  }

  override func loadView() {
    view = scrollView
    view.backgroundColor = .white
  }

  override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()

    let size = blockProvider.block?.size(forResizableBlockSize: view.bounds.size) ?? .zero
    blockView?.frame = CGRect(
      origin: .zero,
      size: size
    )
    scrollView.contentSize = size
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    takeScreenshot(afterScreenUpdates: false)
  }

  private func updateBlockView(with block: Block?) {
    let elementStateObserver = blockProvider.elementStateObserver
    if let block = block, let blockView = blockView,
       block.canConfigureBlockView(blockView) {
      block.configureBlockView(
        blockView,
        observer: elementStateObserver,
        overscrollDelegate: nil,
        renderingDelegate: nil
      )
    } else {
      blockView = block?.makeBlockView(observer: elementStateObserver)
    }

    view.setNeedsLayout()

    takeScreenshot(afterScreenUpdates: true)
  }

  private func takeScreenshot(afterScreenUpdates: Bool) {
    screenshotCancellationToken?.cancel()
    screenshotCancellationToken = nil

    let date = Date()
    guard date.timeIntervalSince(lastScreenshotDate ?? .distantPast) > throttlingTimeout else {
      screenshotCancellationToken = after(throttlingTimeout, onQueue: .main) { [weak self] in
        self?.takeScreenshot(afterScreenUpdates: afterScreenUpdates)
      }
      return
    }
    
    guard let screenshot = view.makeScreenshot(afterScreenUpdates: afterScreenUpdates) else {
      return
    }

    let scale = PlatformDescription.screenScale()
    onScreenshotTaken(
      ScreenshotInfo(
        data: screenshot.binaryRepresentation() ?? Data(),
        density: Double(scale),
        height: Double(view.bounds.height * scale),
        width: Double(view.bounds.width * scale)
      )
    )
    lastScreenshotDate = date
  }
}

extension WebPreviewViewController: UIActionEventPerforming {
  private func handle(_ payload: UserInterfaceAction.Payload) {
    switch payload {
    case .composite, .empty, .json, .url:
      break
    case let .menu(menu):
      showMenu(menu, actionPerformer: self)
    case let .divAction(params):
      blockProvider.handleDivAction(params: params)
    }
  }

  func perform(uiActionEvent event: UIActionEvent, from sender: AnyObject) {
    perform(uiActionEvents: [event], from: sender)
  }

  func perform(uiActionEvents events: [UIActionEvent], from _: AnyObject) {
    events.map { $0.payload }.forEach { handle($0) }
    blockProvider.update()
  }
}

private let throttlingTimeout: TimeInterval = 1
