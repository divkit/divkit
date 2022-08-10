import SwiftUI

struct WebPreviewView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  let url: URL

  var body: some View {
    let socket = WebPreviewSocket()
    let blockProvider = BlockProvider(
      json: socket.response,
      urlOpener: DemoUrlOpener().openUrl(_:)
    )
    let payloadFactory = UIStatePayloadFactory(
      deviceInfo: DeviceInfo(),
      errors: blockProvider.$errors
    )
    return ViewWithHeader(
      "Web Preview",
      background: ThemeColor.divKit,
      presentationMode: presentationMode
    ) {
      WebPreviewViewRepresentable(
        blockProvider: blockProvider,
        onScreenshotTaken: { [payloadFactory, socket] screenshotInfo in
          socket.send(
            state: payloadFactory.makePayload(screenshotInfo: screenshotInfo)
          )
        }
      )
      .onAppear { [socket, url] in
        socket.connect(httpUrl: url)
      }
      .onDisappear { [blockProvider, socket] in
        socket.endConnection()
        blockProvider.reset()
      }
    }
  }
}
