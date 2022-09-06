import SwiftUI

struct MainView: View {
  var body: some View {
    NavigationView {
      VStack(spacing: 10) {
        Image("Logo")
        Text("Welcome to DivKit – the modern layout technology by Yandex")
          .font(ThemeFont.text)
          .multilineTextAlignment(.center)
          .padding(EdgeInsets(top: 18, leading: 0, bottom: 18, trailing: 0))
        HStack(spacing: 8) {
          NavigationButton("samples", color: ThemeColor.samples) {
            SamplesView()
          }
          GeometryReader { geometry in
            VStack(spacing: 10) {
              NavigationButton("playground", color: ThemeColor.divKit) {
                UrlInputView(divViewProvider: makeDivViewProvider())
              }
              NavigationButton("regression", color: ThemeColor.regression, shape: .circle) {
                RegressionFolderView(divViewProvider: makeDivViewProvider())
              }
              .frame(height: geometry.size.width)
            }
          }
        }
        NavigationButton("settings", color: ThemeColor.settings) {
          SettingsView()
        }
        .frame(height: 80)
      }
      .padding(EdgeInsets(top: 46, leading: 20, bottom: 20, trailing: 20))
      .navigationBarHidden(true)
    }
  }
  
  private func makeDivViewProvider() -> DivViewProvider {
    DivViewProvider()
  }
}

private struct NavigationButton<Destination>: View where Destination: View {
  enum Shape {
    case circle
    case rounded
  }
  
  private let title: String
  private let color: Color
  private let shape: Shape
  private let destination: () -> Destination

  init(
    _ title: String,
    color: Color,
    shape: Shape = .rounded,
    destination: @escaping () -> Destination
  ) {
    self.title = title
    self.color = color
    self.shape = shape
    self.destination = destination
  }
  
  var body: some View {
    NavigationLink(destination: destination) {
      switch shape {
      case .circle:
        label.clipShape(Circle())
      case .rounded:
        label.cornerRadius(ThemeSize.cornerRadius)
      }
    }
    .buttonStyle(ScaleAnimationButtonStyle())
  }
  
  private var label: some View {
    Text(title)
      .font(ThemeFont.button)
      .foregroundColor(.white)
      .frame(maxWidth: .infinity, maxHeight: .infinity)
      .background(color)
  }
}
