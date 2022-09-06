import SwiftUI

struct RegressionFolderView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  private let divViewProvider: DivViewProvider
  
  init(
    divViewProvider: DivViewProvider
  ) {
    self.divViewProvider = divViewProvider
  }

  var body: some View {
    ViewWithHeader(
      "Regression",
      background: ThemeColor.regression,
      presentationMode: presentationMode
    ) {
      ScrollView {
        ForEach(TestData.regressionTests, id: \.self) { testModel in
          NavigationButton(testModel.title) {
            RegressionTestView(
              model: testModel,
              divViewProvider: divViewProvider
            )
          }
          .padding(EdgeInsets(top: 0, leading: 0, bottom: 10, trailing: 0))
        }
        .padding(EdgeInsets(top: 20, leading: 20, bottom: 10, trailing: 20))
      }
    }
  }
}

private struct NavigationButton<Destination>: View where Destination: View {
  private let title: String
  private let destination: () -> Destination

  init(
    _ title: String,
    destination: @escaping () -> Destination
  ) {
    self.title = title
    self.destination = destination
  }
  
  var body: some View {
    NavigationLink(destination: destination) {
      label
    }
    .buttonStyle(ScaleAnimationButtonStyle())
  }
  
  private var label: some View {
    HStack {
      Text(title)
        .font(ThemeFont.text)
        .foregroundColor(.black)
        .padding(20)
      Spacer()
      Image(systemName: "chevron.forward")
        .resizable()
        .scaledToFit()
        .foregroundColor(.black)
        .frame(height: 16)
        .padding(20)
    }
    .frame(maxWidth: .infinity, minHeight: 68)
    .background(Color(red: 0xF2 / 255, green: 0xF2 / 255, blue: 0xF2 / 255))
    .cornerRadius(22)
  }
}
