import DivKit
import UIKit

@main
@MainActor
final class AppDelegate: NSObject, UIApplicationDelegate {
  func application(
    _: UIApplication,
    configurationForConnecting session: UISceneSession,
    options _: UIScene.ConnectionOptions
  ) -> UISceneConfiguration {
    let configuration = UISceneConfiguration(name: nil, sessionRole: session.role)
    configuration.delegateClass = SceneDelegate.self
    return configuration
  }
}

@MainActor
final class SceneDelegate: NSObject, UIWindowSceneDelegate {
  var window: UIWindow?

  private var client: UITestAppClient?

  func scene(
    _ scene: UIScene,
    willConnectTo _: UISceneSession,
    options _: UIScene.ConnectionOptions
  ) {
    guard let windowScene = scene as? UIWindowScene else { return }
    let configuration: UITestLaunchConfiguration
    do {
      configuration = try UITestLaunchConfiguration(arguments: ProcessInfo.processInfo.arguments)
    } catch {
      fatalError("Invalid UI test launch arguments: \(error.localizedDescription)")
    }
    let components = DivKitComponents(
      flagsInfo: DivFlagsInfo(
        initializeTriggerOnSet: false,
        useUntypedTemplateResolver: true
      ),
      fontProvider: SnapshotFontProvider()
    )
    let controller = UITestCardViewController(divKitComponents: components)
    let window = UIWindow(windowScene: windowScene)
    window.rootViewController = controller
    window.makeKeyAndVisible()
    self.window = window

    let cardId: DivCardID = "ui_test_card"
    let handler = UITestRequestHandler(components: components, cardId: cardId)
    client = UITestAppClient(
      port: configuration.connectionPort,
      handleRequest: handler.handle
    )

    Task {
      do {
        let card = try loadCard(path: configuration.scenarioPath, flagsInfo: components.flagsInfo)
        await controller.load(card, cardId: cardId)
      } catch {
        controller.showError(error.localizedDescription)
      }
    }
  }

  private func loadCard(path: String, flagsInfo: DivFlagsInfo) throws -> DivData {
    guard let url = Bundle.main.url(forResource: path, withExtension: nil) else {
      throw LaunchError.scenarioNotFound(path)
    }
    let data = try Data(contentsOf: url)
    guard let scenario = try JSONSerialization.jsonObject(with: data) as? [String: Any],
          let card = scenario["div_data"] as? [String: Any] else {
      throw LaunchError.invalidCard("Expected div_data to be a JSON object")
    }
    let rawCard = try RawDivData(dictionary: card)
    let parsed = DivData.resolve(
      card: rawCard.card,
      templates: rawCard.templates,
      flagsInfo: flagsInfo
    )
    if let divData = parsed.value {
      return divData
    }
    throw LaunchError.invalidCard(
      parsed.errorsOrWarnings?.map(\.description).joined(separator: "\n") ?? "DivData is missing"
    )
  }
}

private enum LaunchError: LocalizedError {
  case scenarioNotFound(String)
  case invalidCard(String)

  var errorDescription: String? {
    switch self {
    case let .scenarioNotFound(path):
      "UI test scenario is missing from the host bundle: \(path)"
    case let .invalidCard(details):
      "Failed to convert div_data: \(details)"
    }
  }
}
