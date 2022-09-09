import SwiftUI

struct RegressionView: View {
  @Environment(\.presentationMode)
  var presentationMode: Binding<PresentationMode>

  @State
  private var query = ""

  private let divViewProvider: DivViewProvider

  init(
    divViewProvider: DivViewProvider
  ) {
    self.divViewProvider = divViewProvider
  }

  var body: some View {
    ViewWithHeader(
      "Testing",
      background: ThemeColor.regression,
      presentationMode: presentationMode
    ) {
      TextField("Input filter", text: $query)
        .textFieldStyle(.roundedBorder)
        .font(ThemeFont.text)
        .padding(EdgeInsets(top: 20, leading: 20, bottom: 10, trailing: 20))
      ScrollView {
        ForEach(tests, id: \.self) { testModel in
          NavigationButton(testModel.title) {
            RegressionTestView(
              model: testModel,
              divViewProvider: divViewProvider
            )
          }
          .padding(EdgeInsets(top: 0, leading: 0, bottom: 10, trailing: 0))
        }
        .padding(EdgeInsets(top: 10, leading: 20, bottom: 10, trailing: 20))
      }
    }
  }
  
  private var tests: [RegressionTestModel] {
    if query.isEmpty {
      return TestData.regressionTests
    }
    return TestData.regressionTests.filter {
      $0.title.range(of: query, options: .caseInsensitive) != nil
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
