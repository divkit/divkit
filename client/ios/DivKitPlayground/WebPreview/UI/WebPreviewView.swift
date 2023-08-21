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
        jsonProvider: model.socket.response,
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
  let socket = WebPreviewSocket()
  let divKitComponents: DivKitComponents
  var debugParams: DebugParams!
  private let payloadFactory: UIStatePayloadFactory
  private var renderingTime: UIStatePayload.RenderingTime?
  private let disposePool = AutodisposePool()

  init() {
    divKitComponents = AppComponents.makeDivKitComponents()
    payloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo()
    )
    debugParams = DebugParams(
      isDebugInfoEnabled: true,
      processDivKitError: { _, errors in
        self.payloadFactory.updateErrors(errors: errors)
      },
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
