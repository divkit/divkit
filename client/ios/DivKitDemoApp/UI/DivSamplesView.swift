import SwiftUI

import Base
import CommonCore

struct DivSamplesView: View {
  private let divViewGraph: DivViewGraph
  private let urlsWithFileNames: [(URL, String)]
  private let folderNames: [String]
  private let urlOpener: UrlOpener
  private let parentFolderName: String

  init(urlOpener: @escaping UrlOpener, parentFolderName: String) {
    self.urlOpener = urlOpener
    self.parentFolderName = parentFolderName
    divViewGraph = DivViewGraph(urlOpener: urlOpener)
    urlsWithFileNames = makeURLWithFileNames(
      for: parentFolderName,
      withExtension: "json"
    )
    folderNames = makeURLWithFileNames(
      for: parentFolderName,
      withExtension: ""
    ).filter(\.0.pathExtension.isEmpty).map(\.1).filter { !FolderNames.excludedItems.contains($0) }
  }

  var body: some View {
    List {
      ForEach(folderNames, id: \.self) { folderName in
        NavigationLink(
          destination: DivSamplesView(
            urlOpener: urlOpener,
            parentFolderName: [parentFolderName, folderName].joined(separator: "/")
          )
          .navigationTitle(folderName)
        ) {
          Text(folderName).font(.system(size: 18)).bold()
        }
      }
      ForEach(urlsWithFileNames.indices, id: \.self) {
        let (url, fileName) = urlsWithFileNames[$0]
        NavigationLink(
          destination: makeDivView(url)
            .navigationTitle(fileName)
        ) {
          Text(fileName)
            .font(.system(size: 18))
        }
      }
    }
  }

  private func makeDivView(_ url: URL) -> some View {
    divViewGraph.makeDivView()
      .onAppear {
        divViewGraph.jsonDataProvider.set(url: url)
      }
  }
}
