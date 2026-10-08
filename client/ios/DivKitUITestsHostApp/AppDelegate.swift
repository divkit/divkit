import DivKit
import DivKitExtensions
import DivKitMarkdownExtension
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
  private var controller: UITestCardViewController?

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
    let window = UIWindow(windowScene: windowScene)
    let controller = UITestCardViewController()
    self.controller = controller
    window.rootViewController = controller
    do {
      let scenario = try readScenario(path: configuration.scenarioPath)
      let logReporter = UITestLogReporter()
      let components = makeComponents(
        configuration: scenario.configuration,
        reporter: logReporter
      )
      let card = try parseCard(scenario: scenario, flagsInfo: components.flagsInfo)
      let cardId: DivCardID = "ui_test_card"
      let handler = UITestRequestHandler(
        components: components,
        cardId: cardId,
        rootView: controller.view,
        logReporter: logReporter
      )
      client = UITestAppClient(
        port: configuration.connectionPort,
        handleRequest: handler.handle
      )

      Task {
        await controller.load(
          card,
          cardId: cardId,
          divKitComponents: components,
          logReporter: logReporter
        )
      }
    } catch {
      controller.showError(error.localizedDescription)
    }
    window.makeKeyAndVisible()
    self.window = window
  }

  private func readScenario(path: String) throws -> UITestScenario {
    guard let url = Bundle.main.url(forResource: path, withExtension: nil) else {
      throw LaunchError.scenarioNotFound(path)
    }
    let data = try Data(contentsOf: url)
    return try UITestScenario(data: data)
  }

  private func makeComponents(
    configuration: UITestScenario.Configuration,
    reporter: DivReporter
  ) -> DivKitComponents {
    DivKitComponents(
      extensionHandlers: [GestureExtensionHandler(), MarkdownExtensionHandler()],
      flagsInfo: DivFlagsInfo(
        initializeTriggerOnSet: false,
        useUntypedTemplateResolver: true
      ),
      fontProvider: SnapshotFontProvider(),
      imageHolderFactory: TestImageHolderFactory { [weak self] message in
        self?.controller?.showError(message)
      },
      layoutDirection: configuration.layoutDirection,
      reporter: reporter
    )
  }

  private func parseCard(
    scenario: UITestScenario,
    flagsInfo: DivFlagsInfo
  ) throws -> DivData {
    let parsed = DivData.resolve(
      card: scenario.rawCard.card,
      templates: scenario.rawCard.templates,
      flagsInfo: flagsInfo
    )
    if scenario.configuration.failOnParsingError, let errors = parsed.errorsOrWarnings {
      throw LaunchError.invalidCard(errors.map(\.description).joined(separator: "\n"))
    }
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
