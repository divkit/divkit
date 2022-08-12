import SwiftUI

import CommonCore
import LivePreview

struct MainView: View {
  @State
  private var alert: AlertInfo?

  var body: some View {
    NavigationView {
      List {
        MenuItem(title: FolderNames.samples, image: "square.grid.2x2") {
          DivSamplesView(urlOpener: openUrl(url:), parentFolderName: FolderNames.samples)
        }
        MenuItem(title: "Load", image: "rectangle.and.pencil.and.ellipsis") {
          DivOnlineView(urlOpener: openUrl(url:))
        }
        MenuItem(title: "Live Preview", image: "rectangle.and.text.magnifyingglass") {
          livePreviewView
        }
      }
      .navigationBarTitleDisplayMode(.inline)
      .navigationTitle("DivKit Demo")
    }
    .alert(item: $alert) {
      Alert(
        title: Text("Unhandled Action"),
        message: Text($0.text)
      )
    }
  }

  private var livePreviewView: some View {
    let graph = LivePreviewGraphImpl(
      logger: { message in
        print(message)
      },
      urlOpener: openUrl(url:)
    )
    return graph.view
  }

  private func openUrl(url: URL) {
    if !tryOpenURL(url) {
      alert = AlertInfo(id: url.hashValue, text: url.absoluteString)
    }
  }
}

private struct MenuItem<Destination>: View where Destination: View {
  let title: String
  let image: String
  let destination: () -> Destination

  var body: some View {
    NavigationLink(
      destination: {
        destination()
          .navigationBarTitleDisplayMode(.inline)
          .navigationTitle(title)
      }
    ) {
      Label(title, systemImage: image)
        .font(.system(size: 20))
    }
  }
}

private struct AlertInfo: Identifiable {
  let id: Int
  let text: String
}
