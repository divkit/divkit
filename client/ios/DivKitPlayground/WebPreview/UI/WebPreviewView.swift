import BasePublic
import DivKit
import SwiftUI

struct WebPreviewView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  let url: URL

  private let model = WebPreviewModel()

  var body: some View {
    ViewWithHeader(
      "Web Preview",
      background: ThemeColor.divKit,
      presentationMode: presentationMode
    ) {
      WebPreviewViewRepresentable(
        jsonProvider: model.response,
        divKitComponents: model.divKitComponents,
        debugParams: model.debugParams,
        onScreenshotTaken: model.sendScreenshot(_:)
      )
      .onAppear { [model, url] in
        model.connect(httpUrl: url)
      }
      .onDisappear { [model] in
        model.endConnection()
      }
    }
  }
}

private final class WebPreviewModel {
  let divKitComponents: DivKitComponents
  private let socket = WebPreviewSocket()
  private(set) var debugParams: DebugParams!
  private let payloadFactory: UIStatePayloadFactory
  private var renderingTime: UIStatePayload.RenderingTime?
  private let disposePool = AutodisposePool()

  private let responsePipe: SignalPipe<[String: Any]>
  var response: Signal<[String: Any]> { responsePipe.signal }

  init() {
    let payloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo()
    )
    self.payloadFactory = payloadFactory

    let responsePipe = SignalPipe<[String: Any]>()
    self.responsePipe = responsePipe

    divKitComponents = AppComponents.makeDivKitComponents(
      reporter: DivReporterDelegate {
        payloadFactory.addError($0)
      }
    )

    socket.response
      .addObserver {
        payloadFactory.resetErrors()
        responsePipe.send($0)
      }
      .dispose(in: disposePool)

    debugParams = DebugParams(
      isDebugInfoEnabled: true,
      processMeasurements: { [weak self] _, measurement in
        self?.renderingTime = measurement.renderingTime
      }
    )
  }

  func connect(httpUrl: URL) {
    socket.connect(httpUrl: httpUrl)
  }

  func endConnection() {
    socket.endConnection()
  }

  func sendScreenshot(_ screenshotInfo: ScreenshotInfo) {
    guard let renderingTime else { return }
    socket.send(
      state: payloadFactory.makePayload(
        screenshotInfo: screenshotInfo,
        renderingTime: renderingTime
      )
    )
  }
}
