import SwiftUI
import DivKit

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
        blockProvider: model.blockProvider,
        divKitComponents: model.divKitComponents,
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

private struct WebPreviewModel {
  private let socket = WebPreviewSocket()
  private(set) var divKitComponents: DivKitComponents!
  private(set) var blockProvider: DivBlockProvider!
  private(set) var payloadFactory: UIStatePayloadFactory!
  
  init() {
    divKitComponents = AppComponents.makeDivKitComponents(
      updateCardAction: { [weak blockProvider] _, patch in
        blockProvider?.update(patch: patch)
      }
    )
    blockProvider = DivBlockProvider(
      json: socket.response,
      divKitComponents: divKitComponents,
      shouldResetOnDataChange: false
    )
    payloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo(),
      errors: blockProvider.$errors
    )
  }
  
  func connect(httpUrl: URL) {
    socket.connect(httpUrl: httpUrl)
  }

  func endConnection() {
    socket.endConnection()
  }
  
  func sendScreenshot(_ screenshotInfo: ScreenshotInfo) {
    socket.send(
      state: payloadFactory.makePayload(screenshotInfo: screenshotInfo)
    )
  }
}
