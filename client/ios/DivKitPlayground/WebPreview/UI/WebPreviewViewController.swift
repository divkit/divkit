import SwiftUI
import UIKit

import CommonCorePublic
import DivKit

typealias ScreenshotCallback = (ScreenshotInfo) -> Void

struct WebPreviewViewRepresentable: UIViewControllerRepresentable {
  let jsonProvider: Signal<[String: Any]>
  let divKitComponents: DivKitComponents
  let debugParams: DebugParams
  let onScreenshotTaken: ScreenshotCallback

  func makeUIViewController(context _: Context) -> UIViewController {
    WebPreviewViewController(
      jsonProvider: jsonProvider,
      divKitComponents: divKitComponents,
      debugParams: debugParams,
      onScreenshotTaken: onScreenshotTaken
    )
  }

  func updateUIViewController(_: UIViewController, context _: Context) {}
}

private final class WebPreviewViewController: DivViewController {
  private let onScreenshotTaken: ScreenshotCallback

  private var isAppeared = false
  private var lastScreenshotDate: Date?
  private var screenshotCancellationToken: Cancellable?

  init(
    jsonProvider: Signal<[String: Any]>,
    divKitComponents: DivKitComponents,
    debugParams: DebugParams,
    onScreenshotTaken: @escaping ScreenshotCallback
  ) {
    self.onScreenshotTaken = onScreenshotTaken

    super.init(
      jsonProvider: jsonProvider,
      divKitComponents: divKitComponents,
      debugParams: debugParams
    )
  }

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func viewDidLayoutSubviews() {
    super.viewDidLayoutSubviews()
    takeScreenshot(afterScreenUpdates: false)
  }

  public override func viewDidAppear(_: Bool) {
    isAppeared = true
  }

  public override func viewDidDisappear(_: Bool) {
    screenshotCancellationToken?.cancel()
    screenshotCancellationToken = nil

    isAppeared = false
  }

  override func onViewUpdated() {
    super.onViewUpdated()
    takeScreenshot(afterScreenUpdates: true)
  }

  private func takeScreenshot(afterScreenUpdates: Bool) {
    guard isAppeared else {
      return
    }

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
