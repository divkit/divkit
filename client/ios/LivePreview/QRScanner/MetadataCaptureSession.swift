import AVFoundation
import UIKit

import CommonCore

final class MetadataCaptureSession: NSObject, AVCaptureMetadataOutputObjectsDelegate {
  private let captureSession = AVCaptureSession()
  private let previewLayer: AVCaptureVideoPreviewLayer
  private let logger: LivePreviewLogger

  private var previousCode = ""
  @Property
  var result: String

  var layer: CALayer { previewLayer }

  init(
    result: Property<String>,
    logger: @escaping LivePreviewLogger
  ) {
    _result = result
    self.logger = logger
    previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
    previewLayer.videoGravity = .resizeAspectFill

    super.init()

    guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else {
      logger("Can't create video capture device")
      return
    }

    let videoInput: AVCaptureDeviceInput
    do {
      videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
    } catch {
      logger("Can't create video input with \(error)")
      return
    }
    if captureSession.canAddInput(videoInput) {
      captureSession.addInput(videoInput)
    } else {
      logger("Capture session can't add video input")
      return
    }

    let metadataOutput = AVCaptureMetadataOutput()
    if captureSession.canAddOutput(metadataOutput) {
      captureSession.addOutput(metadataOutput)
      metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
      metadataOutput.metadataObjectTypes = [.qr]
    } else {
      logger("Capture session can't add metadata output")
      return
    }
  }

  func pause() {
    if captureSession.isRunning == true {
      captureSession.stopRunning()
    }
  }

  func resume() {
    if captureSession.isRunning == false {
      captureSession.startRunning()
    }
  }

  func didChangeOrientation(to orientation: UIDeviceOrientation) {
    previewLayer.connection?.videoOrientation = orientation.videoOrientation
  }

  func metadataOutput(
    _: AVCaptureMetadataOutput,
    didOutput metadataObjects: [AVMetadataObject],
    from _: AVCaptureConnection
  ) {
    guard let readableObject = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
          let stringValue = readableObject.stringValue else {
      logger("Can't retrieve string value from metadata")
      return
    }

    handle(code: stringValue)
  }

  private func handle(code: String) {
    guard result.isEmpty || previousCode != code else { return }
    previousCode = code
    result = code
  }
}

extension UIDeviceOrientation {
  fileprivate var videoOrientation: AVCaptureVideoOrientation {
    switch self {
    case .portraitUpsideDown:
      return .portraitUpsideDown
    case .landscapeRight:
      return .landscapeLeft
    case .landscapeLeft:
      return .landscapeRight
    case .portrait, .unknown, .faceUp, .faceDown:
      return .portrait
    @unknown default:
      return .portrait
    }
  }
}
