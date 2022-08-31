import SwiftUI
import UIKit

import CommonCore
import DivKit

typealias ScreenshotCallback = (ScreenshotInfo) -> Void

struct WebPreviewViewRepresentable: UIViewControllerRepresentable {
  let blockProvider: DivBlockProvider
  let divKitComponents: DivKitComponents
  let onScreenshotTaken: ScreenshotCallback

  func makeUIViewController(context _: Context) -> UIViewController {
    WebPreviewViewController(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents,
      onScreenshotTaken: onScreenshotTaken
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

private final class WebPreviewViewController: DivViewController {
  private let onScreenshotTaken: ScreenshotCallback

  private var lastScreenshotDate: Date?
  private var screenshotCancellationToken: Cancellable?

  init(
    blockProvider: DivBlockProvider,
    divKitComponents: DivKitComponents,
    onScreenshotTaken: @escaping ScreenshotCallback
  ) {
    self.onScreenshotTaken = onScreenshotTaken

    super.init(
      blockProvider: blockProvider,
      divKitComponents: divKitComponents
    )
  }

  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    takeScreenshot(afterScreenUpdates: false)
  }
  
  override func onViewUpdated() {
    super.onViewUpdated()
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

private let throttlingTimeout: TimeInterval = 1
