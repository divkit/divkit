import SwiftUI

struct MainView: View {
  @AppStorage(UserPreferences.isRTLEnabledKey)
  var isRTLEnabled: Bool = UserPreferences.isRTLEnabledDefault

  var colorScheme: ColorScheme {
    UITraitCollection.current.userInterfaceStyle == .dark ? .dark : .light
  }

  var body: some View {
    if #available(iOS 16.0, *) {
      NavigationStack {
        contentView
      }
    } else {
      NavigationView {
        contentView
      }
    }
  }

  private var contentView: some View {
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
            NavigationButton("testing", color: ThemeColor.regression, shape: .circle) {
              RegressionView(divViewProvider: makeDivViewProvider())
            }
            .frame(height: geometry.size.width)
          }
        }
      }
      NavigationButton(
        "settings",
        color: colorScheme.settingsColor,
        labelColor: colorScheme.settingsLabelColor
      ) {
        SettingsView()
      }
      .frame(height: 80)
    }
    .padding(EdgeInsets(top: 46, leading: 20, bottom: 20, trailing: 20))
    .navigationBarHidden(true)
  }

  private func makeDivViewProvider() -> DivViewProvider {
    DivViewProvider(
      layoutDirection: isRTLEnabled ? .rightToLeft : .leftToRight,
      colorScheme: colorScheme
    )
  }
}

private struct NavigationButton<Destination>: View where Destination: View {
  enum Shape {
    case circle
    case rounded
  }

  private let title: String
  private let color: Color
  private let labelColor: Color
  private let shape: Shape
  private let destination: () -> Destination

  init(
    _ title: String,
    color: Color,
    labelColor: Color = .white,
    shape: Shape = .rounded,
    destination: @escaping () -> Destination
  ) {
    self.title = title
    self.color = color
    self.labelColor = labelColor
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
      .foregroundColor(labelColor)
      .frame(maxWidth: .infinity, maxHeight: .infinity)
      .background(color)
  }
}

extension ColorScheme {
  fileprivate var settingsColor: Color {
    self == .dark ? ThemeColor.settingsDark : ThemeColor.settingsLight
  }

  fileprivate var settingsLabelColor: Color {
    self == .dark ? .black : .white
  }
}
