@testable @_spi(Internal) import DivKit
@testable import LayoutKit
import Testing
import UIKit
import VGSL

let testCardId = DivCardID(rawValue: "test_card_id")

private let mode = ProcessInfo.processInfo.arguments.contains("UPDATE_SNAPSHOTS")
  ? TestMode.update
  : TestMode.verify

@MainActor
final class SnapshotTestRunner {
  private typealias CheckAction = (_ view: UIView?) async throws -> Void

  private let file: JsonFile

  init(file: JsonFile) {
    self.file = file
  }

  func run(
    caseName: String,
    blocksState: [IdAndCardId: ElementState] = [:],
    extensions: [DivExtensionHandler] = []
  ) async throws {
    let jsonDict = try #require(readJson(path: file.absolutePath))

    let divKitComponents = DivKitComponents(
      extensionHandlers: extensions,
      fontProvider: SnapshotFontProvider(),
      imageHolderFactory: TestImageHolderFactory(),
      layoutDirection: getLayoutDirection(jsonDict)
    )
    for (id, state) in blocksState {
      divKitComponents.blockStateStorage.setState(id: id.id, cardId: id.cardId, state: state)
    }

    let view = DivView(divKitComponents: divKitComponents)
    try await view.setSource(DivViewSource(
      kind: .json(jsonDict.getOptionalField("div_data") ?? jsonDict),
      cardId: testCardId
    ))

    if let steps = try loadSteps(dictionary: jsonDict) {
      for (index, step) in steps.enumerated() {
        step.actions?.forEach { action in
          divKitComponents.actionHandler.handle(
            action,
            path: testCardId.path,
            source: .tap,
            sender: nil
          )
        }
        divKitComponents.flushUpdateActions()
        view.forceLayout()

        let check: CheckAction = { additionalView in
          try await self.checkSnapshots(
            view: view,
            caseName: caseName,
            stepName: step.name ?? "step\(index)",
            additionalView: additionalView
          )
        }

        let tooltipManager = divKitComponents.tooltipManager as! DefaultTooltipManager
        try await tooltipsTestStepRun(
          manager: tooltipManager,
          check: check
        )
      }
    } else {
      try await checkSnapshots(view: view, caseName: caseName)
    }
  }

  private func tooltipsTestStepRun(
    manager: DefaultTooltipManager,
    check: CheckAction
  ) async throws {
    guard let tooltipWindow = manager.tooltipWindowManager?.modalWindow else {
      return try await check(nil)
    }

    while !tooltipWindow.isKeyWindow {
      await Task.yield()
    }

    guard let tooltipView = tooltipWindow.subviews.first else {
      return try await check(nil)
    }

    try await check(tooltipView)
  }

  private func getLayoutDirection(
    _ json: [String: any Sendable]
  ) -> UserInterfaceLayoutDirection {
    let configuration = try? json.getField("configuration") as [String: any Sendable]
    guard configuration?["layout_direction"] as? String == "rtl" else {
      return .leftToRight
    }
    return .rightToLeft
  }

  private func loadSteps(dictionary: [String: Any]) throws -> [TestStep]? {
    try? dictionary.getOptionalArray(
      "steps",
      transform: { try TestStep(dictionary: $0 as [String: Any]) }
    )
  }

  private func checkSnapshots(
    view: DivView,
    caseName: String,
    stepName: String? = nil,
    additionalView: UIView? = nil
  ) async throws {
    for _ in 0..<2 {
      view.layoutIfNeeded()
      await Task.yield()
    }

    let screen = Screen.makeForScale(UIScreen.main.scale)
    let cardSize = view.cardSize?.sizeFor(parentViewSize: screen.size) ?? .zero
    view.frame = CGRect(origin: .zero, size: cardSize)

    let testView = {
      if let additionalView {
        let testView = UIView()
        testView.addSubview(view)
        testView.addSubview(additionalView)

        testView.frame.origin = view.frame.origin
        testView.frame.size = view.frame.size.max(
          size: additionalView.frame.size
        )
        return testView
      } else {
        return view
      }
    }()

    let nonEmptyView: UIView
    if testView.bounds.isEmpty {
      let label = UILabel()
      label.text = "<empty view>"
      label.frame = CGRect(origin: .zero, size: label.intrinsicContentSize)
      nonEmptyView = label
    } else {
      nonEmptyView = testView
    }

    try SnapshotTestKit.compareSnapshot(
      #require(nonEmptyView.makeSnapshot()),
      referenceFileUrl: referenceFileUrl(screen: screen, caseName: caseName, stepName: stepName),
      mode: mode
    )
  }

  private func referenceFileUrl(
    screen: Screen,
    caseName: String,
    stepName: String?
  ) -> URL {
    var stepDescription = ""
    if let stepName {
      stepDescription = "_" + stepName
    }
    return URL(fileURLWithPath: ReferenceSet.referenceSnapshotsPath, isDirectory: true)
      .appendingPathComponent(file.subdirectory)
      .appendingPathComponent(
        "\(caseName)_\(Int(screen.size.width))@\(Int(screen.scale))x\(stepDescription).png",
        isDirectory: false
      )
  }
}

private func readJson(path: String) -> [String: any Sendable]? {
  guard let data = FileManager.default.contents(atPath: path) else {
    return nil
  }
  return (try? JSONSerialization.jsonObject(with: data)) as? [String: any Sendable]
}

private final class TestImageHolderFactory: @MainActor DivImageHolderFactory {
  private var reportedUrls = Set<String>()
  private let testBundle = Bundle(for: SnapshotTestRunner.self)

  @MainActor
  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    guard let url, url.absoluteString != "empty://" else {
      return placeholder?.toImageHolder() ?? NilImageHolder()
    }

    if let image = UIImage(named: url.lastPathComponent, in: testBundle, compatibleWith: nil) {
      return image
    }

    let urlString = url.absoluteString
    if !reportedUrls.contains(urlString) {
      Issue.record(
        "Loading images from network is prohibited in tests. You need to load image from \(urlString) and add it to Images.xcassets in testing bundle"
      )
      reportedUrls.insert(urlString)
    }

    return UIImage()
  }
}

private struct TestStep: Sendable {
  let name: String?
  let actions: [DivActionBase]?

  init(dictionary: [String: Any]) throws {
    let expectedScreenshot: String? = try dictionary.getOptionalField("expected_screenshot")
    name = expectedScreenshot?.replacingOccurrences(of: ".png", with: "")
    actions = try? dictionary.getOptionalArray(
      "div_actions",
      transform: { (actionDictionary: [String: Any]) -> DivActionBase in
        try DivTemplates.empty.parseValue(type: DivActionTemplate.self, from: actionDictionary)
          .unwrap()
      }
    ).unwrap()
  }
}

extension CGSize {
  fileprivate func max(size: CGSize) -> CGSize {
    CGSize(
      width: Swift.max(width, size.width),
      height: Swift.max(height, size.height)
    )
  }
}
