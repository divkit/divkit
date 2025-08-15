import SwiftUI

struct RegressionTestView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  let model: RegressionTestModel
  let divViewProvider: DivViewProvider

  var body: some View {
    ViewWithHeader(
      model.title,
      background: ThemeColor.regression,
      presentationMode: presentationMode
    ) {
      divViewProvider.makeDivView(model.url)
      if !model.description.isEmpty {
        ScrollView {
          Text(model.description)
            .font(ThemeFont.makeRegular(size: 16))
            .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
            .frame(maxWidth: .infinity, alignment: .leading)
        }
        .frame(maxWidth: .infinity, maxHeight: 220)
        .background(Color(red: 0xF2 / 255, green: 0xF2 / 255, blue: 0xF2 / 255))
        .accessibilityHidden(true)
      }
    }
  }
}
