import SwiftUI

struct RegressionFolderView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  private let path: String
  private let divViewProvider: DivViewProvider

  private let folders: [String]
  private let files: [(URL, String)]

  init(
    path: String = Samples.regressionPath,
    divViewProvider: DivViewProvider
  ) {
    self.path = path
    self.divViewProvider = divViewProvider

    folders = Samples.getItems(path: path, extension: "")
      .filter(\.0.pathExtension.isEmpty)
      .map(\.1)
      .filter { $0 != Samples.patchesFolderName }

    files = Samples.getItems(path: path, extension: "json")
  }

  var body: some View {
    ViewWithHeader(
      String(path.split(separator: "/").last ?? ""),
      background: ThemeColor.regression,
      presentationMode: presentationMode
    ) {
      List {
        ForEach(folders, id: \.self) { folderName in
          NavigationLink(
            destination: RegressionFolderView(
              path: [path, folderName].joined(separator: "/"),
              divViewProvider: divViewProvider
            )
          ) {
            Text(folderName)
              .font(ThemeFont.text)
              .fontWeight(.semibold)
          }
        }
        ForEach(files.indices, id: \.self) {
          let (url, fileName) = files[$0]
          NavigationLink(
            destination: {
              RegressionFileView(title: fileName, url: url, divViewProvider: divViewProvider)
            },
            label: {
              Text(fileName)
                .font(ThemeFont.text)
            }
          )
        }
      }
    }
  }
}

private struct RegressionFileView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  let title: String
  let url: URL
  let divViewProvider: DivViewProvider

  var body: some View {
    ViewWithHeader(
      title,
      background: ThemeColor.regression,
      presentationMode: presentationMode
    ) {
      divViewProvider.makeDivView(url)
    }
  }
}
