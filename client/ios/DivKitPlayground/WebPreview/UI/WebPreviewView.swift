import Combine
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
        jsonPublisher: model.responsePublsiher,
        divKitComponents: model.divKitComponents,
        debugParams: model.debugParams,
        onScreenshotTaken: model.sendScreenshot(_:)
      )
      .onAppear { [model, url] in
        model.connect(httpUrl: url)
      }
      .onDisappear { [model] in
        model.disconnect()
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
  private var cancellables = Set<AnyCancellable>()

  var responsePublsiher: JsonPublisher {
    socket.responsePublisher
  }

  init() {
    let payloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo()
    )
    self.payloadFactory = payloadFactory

    divKitComponents = AppComponents.makeDivKitComponents(
      reporter: PlaygroundReporter {
        payloadFactory.addError($0)
      }
    )

    socket.responsePublisher
      .sink { _ in payloadFactory.resetErrors() }
      .store(in: &cancellables)

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

  func disconnect() {
    socket.disconnect()
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
