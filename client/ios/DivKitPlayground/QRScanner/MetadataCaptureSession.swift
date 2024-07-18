import AVFoundation
import UIKit

import VGSL

typealias LogError = (String) -> Void

final class MetadataCaptureSession: NSObject, AVCaptureMetadataOutputObjectsDelegate {
  private let captureSession = AVCaptureSession()
  private let previewLayer: AVCaptureVideoPreviewLayer

  let result: ObservableProperty<String>
  private let logError: LogError

  var layer: CALayer { previewLayer }

  private var isInitialized = false

  init(
    result: ObservableProperty<String>,
    logError: @escaping LogError
  ) {
    self.result = result
    self.logError = logError

    previewLayer = AVCaptureVideoPreviewLayer(session: captureSession)
    previewLayer.videoGravity = .resizeAspectFill

    super.init()

    guard let videoCaptureDevice = AVCaptureDevice.default(for: .video) else {
      logError("Can't create video capture device")
      return
    }

    let videoInput: AVCaptureDeviceInput
    do {
      videoInput = try AVCaptureDeviceInput(device: videoCaptureDevice)
    } catch {
      logError("Can't create video input: \(error)")
      return
    }

    if captureSession.canAddInput(videoInput) {
      captureSession.addInput(videoInput)
    } else {
      logError("Capture session can't add video input")
      return
    }

    let metadataOutput = AVCaptureMetadataOutput()
    if captureSession.canAddOutput(metadataOutput) {
      captureSession.addOutput(metadataOutput)
      metadataOutput.setMetadataObjectsDelegate(self, queue: DispatchQueue.main)
      metadataOutput.metadataObjectTypes = [.qr]
    } else {
      logError("Capture session can't add metadata output")
      return
    }

    isInitialized = true
  }

  func pause() {
    if isInitialized, captureSession.isRunning == true {
      captureSession.stopRunning()
    }
  }

  func resume() {
    if isInitialized, captureSession.isRunning == false {
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
      logError("Can't retrieve string value from metadata")
      return
    }

    result.value = stringValue
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
