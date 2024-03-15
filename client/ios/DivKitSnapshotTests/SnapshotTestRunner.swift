@testable import DivKit
import UIKit

import Testing

import BaseUIPublic
import CommonCorePublic
import LayoutKit
import NetworkingPublic

let testCardId = "test_card_id"
let testDivCardId = DivCardID(rawValue: testCardId)

final class SnapshotTestRunner {
  #if UPDATE_SNAPSHOTS
  let mode = TestMode.update
  #else
  let mode = TestMode.verify
  #endif

  init(file: JsonFile) {
    self.file = file
  }

  private let file: JsonFile

  func run(
    caseName: String,
    blocksState: [IdAndCardId: ElementState] = [:],
    extensions: [DivExtensionHandler] = []
  ) throws {
    let jsonDict = try #require(makeJsonDict(from: file.absolutePath))

    let divKitComponents = DivKitComponents(
      extensionHandlers: extensions,
      fontProvider: YSFontProvider(),
      imageHolderFactory: TestImageHolderFactory(),
      layoutDirection: getLayoutDirection(from: jsonDict)
    )
    for (id, state) in blocksState {
      divKitComponents.blockStateStorage.setState(id: id.id, cardId: id.cardId, state: state)
    }

    let view = DivView(divKitComponents: divKitComponents)
    try view.setSource(DivViewSource(
      kind: .json(jsonDict.getOptionalField("div_data") ?? jsonDict),
      cardId: testDivCardId
    ))

    if let steps = try loadSteps(dictionary: jsonDict) {
      for (index, step) in steps.enumerated() {
        step.actions?.forEach { action in
          divKitComponents.actionHandler.handle(
            action,
            cardId: testDivCardId,
            source: .tap,
            sender: nil
          )
        }
        divKitComponents.flushUpdateActions()
        view.forceLayout()
        try checkSnapshots(
          view: view,
          caseName: caseName,
          stepName: step.name ?? "step\(index)"
        )
      }
    } else {
      try checkSnapshots(view: view, caseName: caseName)
    }
  }

  private func getLayoutDirection(from jsonDict: [String: Any]) -> UserInterfaceLayoutDirection {
    let configuration = try? jsonDict.getField("configuration") as [String: Any]
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
    stepName: String? = nil
  ) throws {
    let screen = try Screen.makeForScale(UIScreen.main.scale)
    let cardSize = view.cardSize?.sizeFor(parentViewSize: screen.size) ?? .zero
    view.frame = CGRect(origin: .zero, size: cardSize)

    let nonEmptyView: UIView
    if view.bounds.isEmpty {
      let label = UILabel()
      label.text = "<empty view>"
      label.frame = CGRect(origin: .zero, size: label.intrinsicContentSize)
      nonEmptyView = label
    } else {
      nonEmptyView = view
    }

    let image = try #require(nonEmptyView.makeSnapshot())

    try SnapshotTestKit.compareSnapshot(
      image,
      referenceURL: referenceFileURL(screen: screen, caseName: caseName, stepName: stepName),
      mode: mode,
      file: file
    )
  }

  private func referenceFileURL(
    screen: Screen,
    caseName: String,
    stepName: String?
  ) -> URL {
    var stepDescription = ""
    if let stepName {
      stepDescription = "_" + stepName
    }
    return URL(fileURLWithPath: ReferenceSet.path, isDirectory: true)
      .appendingPathComponent(file.subdirectory)
      .appendingPathComponent(
        "\(caseName)_\(Int(screen.size.width))@\(Int(screen.scale))x\(stepDescription).png",
        isDirectory: false
      )
  }
}

private func makeJsonDict(from path: String) -> [String: Any]? {
  guard let data = FileManager.default.contents(atPath: path) else {
    return nil
  }
  return (try? JSONSerialization.jsonObject(with: data)) as? [String: Any]
}

private final class TestImageHolderFactory: DivImageHolderFactory {
  private var reportedUrls = Set<String>()
  private let testBundle = Bundle(for: SnapshotTestRunner.self)

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
        "Loading images from network is prohibited in tests. You need to load image from "
          + urlString + " and add it to Images.xcassets in testing bundle"
      )
      reportedUrls.insert(urlString)
    }

    return UIImage()
  }
}

private struct TestStep {
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
