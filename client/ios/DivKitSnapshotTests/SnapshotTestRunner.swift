@testable @_spi(Internal) import DivKit
@testable import LayoutKit
import Testing
import UIKit
import VGSL

let testCardId = DivCardID(rawValue: "test_card_id")

private let mode: TestMode = EnvironmentVars.isSnapshotsUpdateMode ? .update : .verify

private var customActionHandler: DivCustomActionHandling {
  SnapshotCustomActionHandler()
}

private struct SnapshotCustomActionHandler: DivCustomActionHandling {
  func handle(payload: DivDictionary, context: DivActionHandlingContext, sender _: AnyObject?) {
    guard let value = payload["value"] as? String else { return }
    context.variablesStorage.update(
      path: context.path,
      name: DivVariableName(rawValue: "external_variable"),
      value: value
    )
  }
}

@MainActor
final class SnapshotTestRunner {
  private typealias CheckAction = (_ view: UIView?) async throws -> Void

  private let file: JsonFile

  init(file: JsonFile) {
    self.file = file
  }

  func run(
    caseName: String,
    statesByElementId: [String: ElementState] = [:],
    extensions: [DivExtensionHandler] = []
  ) async throws {
    let jsonDict = try #require(readJson(path: file.absolutePath))

    let divKitComponents = DivKitComponents(
      customActionHandler: customActionHandler,
      extensionHandlers: extensions,
      fontProvider: SnapshotFontProvider(),
      imageHolderFactory: TestImageHolderFactory(),
      layoutDirection: getLayoutDirection(jsonDict)
    )
    for (id, state) in statesByElementId {
      let paths = elementPaths(id: id, json: jsonDict)
      guard paths.count <= 1 else {
        Issue.record("Can not seed state: id '\(id)' is not unique in \(file.name)")
        continue
      }
      if let path = paths.first {
        divKitComponents.blockStateStorage.setState(path: path, state: state)
      }
    }

    divKitComponents.variablesStorage.append(
      variables: [DivVariableName(rawValue: "external_variable"): .string("initial")],
      triggerUpdate: false
    )

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
    let tooltip = await manager.currentTooltipView()
    return try await check(tooltip)
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

  private func loadSteps(
    dictionary: [String: any Sendable]
  ) throws -> [TestStep]? {
    guard dictionary["steps"] != nil else {
      return nil
    }

    let stepDictionaries: [[String: any Sendable]] = try dictionary.getField("steps")
    var actions: [DivActionBase] = []
    var steps: [TestStep] = []

    for stepDictionary in stepDictionaries {
      let type: String = try stepDictionary.getField("type")

      switch type {
      case "div_action":
        let actionDictionary: [String: any Sendable] = try stepDictionary.getField("action")
        let action = try DivTemplates.empty.parseValue(
          type: DivActionTemplate.self,
          from: actionDictionary
        ).unwrap()
        actions.append(action)
      case "wait":
        // Keep the existing iOS behavior: delays are ignored by the snapshot runner.
        break
      case "verify_snapshot":
        let name: String = try stepDictionary.getField("name")
        steps.append(TestStep(name: name, actions: actions))
        actions.removeAll()
      default:
        throw TestStepParsingError.unsupportedType(type)
      }
    }

    guard actions.isEmpty else {
      throw TestStepParsingError.actionsWithoutSnapshot
    }
    return steps
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

/// Repeats the path DivKit builds while modeling a card: every div contributes its id
/// (or its type when there is no id), and every item of a container contributes its index.
/// The state has to be stored under the exact path, otherwise the element does not see it.
/// Every match is returned, so that an id shared by several elements does not silently
/// seed an arbitrary one of them.
private func elementPaths(id: String, json: [String: Any]) -> [UIElementPath] {
  func find(div: [String: Any], path: UIElementPath, into paths: inout [UIElementPath]) {
    let divId = div["id"] as? String
    guard let segment = divId ?? div["type"] as? String else {
      return
    }
    let divPath = path + segment
    if divId == id {
      paths.append(divPath)
    }
    let items = div["items"] as? [[String: Any]] ?? []
    for (index, item) in items.enumerated() {
      find(div: item, path: divPath + String(index), into: &paths)
    }
  }

  let divData = json["div_data"] as? [String: Any] ?? json
  let card = divData["card"] as? [String: Any] ?? divData
  let states = card["states"] as? [[String: Any]] ?? []
  var paths = [UIElementPath]()
  for state in states {
    guard let div = state["div"] as? [String: Any],
          let stateId = state["state_id"] else {
      continue
    }
    find(div: div, path: testCardId.path + "\(stateId)", into: &paths)
  }
  return paths
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

  init(name: String, actions: [DivActionBase]) {
    self.name = name
    self.actions = actions
  }
}

private enum TestStepParsingError: Error {
  case actionsWithoutSnapshot
  case unsupportedType(String)
}

extension CGSize {
  fileprivate func max(size: CGSize) -> CGSize {
    CGSize(
      width: Swift.max(width, size.width),
      height: Swift.max(height, size.height)
    )
  }
}
