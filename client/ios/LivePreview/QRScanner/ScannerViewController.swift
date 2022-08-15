import UIKit

import CommonCore

final class ScannerViewController: UIViewController {
  private let captureSession: Lazy<MetadataCaptureSession>

  let result = ObservableProperty(initialValue: "")

  @available(*, unavailable)
  required init?(coder _: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  init(logger: @escaping LivePreviewLogger) {
    captureSession = Lazy(onMainThreadGetter: { [result] in
      MetadataCaptureSession(
        result: result.asProperty(),
        logger: logger
      )
    })
    super.init(nibName: nil, bundle: nil)
    NotificationCenter.default.addObserver(
      self, selector: #selector(orientationDidChange),
      name: UIDevice.orientationDidChangeNotification,
      object: nil
    )
  }

  override func viewDidLoad() {
    super.viewDidLoad()
    view.backgroundColor = UIColor.black
    view.layer.addSublayer(captureSession.value.layer)
  }

  override func viewWillAppear(_ animated: Bool) {
    super.viewWillAppear(animated)
    captureSession.value.resume()
  }

  override func viewWillDisappear(_ animated: Bool) {
    super.viewWillDisappear(animated)
    captureSession.value.pause()
  }

  override func viewWillLayoutSubviews() {
    super.viewWillLayoutSubviews()
    CATransaction.begin()
    CATransaction.setDisableActions(true)
    captureSession.value.layer.frame = view.bounds
    CATransaction.commit()
  }

  @objc private func orientationDidChange() {
    let orientation = UIDevice.current.orientation
    guard orientation != .portraitUpsideDown else { return }
    captureSession.value.didChangeOrientation(to: orientation)
  }
}
