// Copyright 2022 Yandex LLC. All rights reserved.

import SwiftUI
import UIKit

import BaseUI
import CommonCore

@_implementationOnly import LayoutKit

typealias ScreenshotCallback = (ScreenshotInfo) -> Void

struct ScreenshotInfo {
  let data: Data
  let density: Double
  let height: Double
  let width: Double
}

@available(iOS 13, *)
struct LivePreviewView: UIViewControllerRepresentable {
  let blockProvider: BlockProvider
  let onScreenshotTaken: ScreenshotCallback

  func makeUIViewController(context _: Context) -> UIViewController {
    DivViewController(
      blockProvider: blockProvider,
      onSnapshotTaken: onScreenshotTaken
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

private final class DivViewController: UIViewController {
  private let blockProvider: BlockProvider
  private let onSnapshotTaken: ScreenshotCallback
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

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  init(
    blockProvider: BlockProvider,
    onSnapshotTaken: @escaping ScreenshotCallback
  ) {
    self.blockProvider = blockProvider
    self.onSnapshotTaken = onSnapshotTaken

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
    guard let screenshot = view.makeScreenshot(
      afterScreenUpdates: afterScreenUpdates
    ) else {
      return
    }

    let scale = PlatformDescription.screenScale()
    onSnapshotTaken(
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

extension DivViewController: UIActionEventPerforming {
  private func handle(_ payload: UserInterfaceAction.Payload) {
    switch payload {
    case .empty, .json, .url:
      break
    case let .menu(menu):
      showMenu(
        menu,
        actionPerformer: { [unowned self] in
          self.handle($0)
        }
      )
    case let .divAction(params):
      blockProvider.handleDivAction(params: params)
    case let .composite(lhs, rhs):
      handle(lhs)
      handle(rhs)
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

extension LayoutKit.Menu {
  fileprivate func makeActionSheet(actionPerformer: @escaping (UserInterfaceAction.Payload) -> Void)
    -> ActionSheetModel {
    let buttons = items.map { item in
      AlertButton(
        title: item.text,
        action: {
          item.actions.forEach { actionPerformer($0.payload) }
        }
      )
    } + [AlertButton(title: "Cancel", actionStyle: .cancel)]

    return ActionSheetModel(buttons: buttons)
  }
}

extension UIViewController {
  func showAlert(
    message: String? = nil,
    actions: [UIAlertAction] = []
  ) {
    let alert = UIAlertController(title: nil, message: message, preferredStyle: .actionSheet)
    actions.forEach {
      alert.addAction($0)
    }
    alert.addAction(UIAlertAction(title: "Cancel", style: .cancel))
    present(alert, animated: true)
  }

  func showMenu(
    _ menu: LayoutKit.Menu,
    actionPerformer: @escaping (UserInterfaceAction.Payload) -> Void
  ) {
    let actions = menu.items.map { item in
      UIAlertAction(title: item.text, style: .default) { _ in
        item.actions.forEach {
          actionPerformer($0.payload)
        }
      }
    }
    showAlert(actions: actions)
  }
}

private let throttlingTimeout: TimeInterval = 1
