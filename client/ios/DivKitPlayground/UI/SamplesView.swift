import DivKit
import SwiftUI

struct SamplesView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  private let model = SamplesModel()

  var body: some View {
    ViewWithHeader(
      "Samples",
      background: ThemeColor.samples,
      presentationMode: presentationMode
    ) {
      ScrollView {
        ForEach(model.items, id: \.cardId) {
          SampleView(model: $0)
        }
      }
    }
  }
}

struct SampleView: View {
  let model: SampleModel

  var body: some View {
    DivHostingView(
      divkitComponents: model.divKitComponents,
      source: DivViewSource(kind: .data(model.jsonData ?? Data()), cardId: model.cardId),
      shouldResetPreviousCardData: true
    )
  }
}

final class SamplesModel {
  private(set) var items: [SampleModel]!

  private let divKitComponents = AppComponents.makeDivKitComponents()

  init() {
    items = TestData.samples
      .map {
        SampleModel(url: $0, divKitComponents: divKitComponents)
      }
  }
}

final class SampleModel {
  let cardId: DivCardID
  let jsonData: Data?
  let divKitComponents: DivKitComponents

  init(
    url: URL,
    divKitComponents: DivKitComponents
  ) {
    cardId = DivCardID(rawValue: url.path)
    jsonData = try? Data(contentsOf: url)
    self.divKitComponents = divKitComponents
  }
}
