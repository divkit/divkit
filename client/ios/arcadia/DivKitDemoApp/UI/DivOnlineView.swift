import SwiftUI

import CommonCore

struct DivOnlineView: View {
  @State
  private var urlString = ""

  private let divViewGraph: DivViewGraph

  init(urlOpener: @escaping UrlOpener) {
    divViewGraph = DivViewGraph(urlOpener: urlOpener)
  }

  var body: some View {
    VStack {
      HStack(spacing: 8) {
        TextField("URL", text: $urlString)
          .textFieldStyle(.roundedBorder)
        Button {
          divViewGraph.jsonDataProvider.set(url: urlString)
          UserDefaults.standard.set(urlString, forKey: urlKey)
        } label: {
          Image(systemName: "arrow.triangle.2.circlepath")
        }
      }
      .padding(8)
      divViewGraph.makeDivView()
    }.onAppear {
      urlString = UserDefaults.standard.string(forKey: urlKey) ?? ""
    }
  }
}

private let urlKey = "url_for_div_online"
