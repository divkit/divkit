@testable import DivKit

import XCTest

import BaseUIPublic
import CommonCorePublic
import LayoutKit
import NetworkingPublic

let testCardId = "test_card_id"
let testDivCardId = DivCardID(rawValue: testCardId)

open class DivKitSnapshotTestCase: XCTestCase {
  #if UPDATE_SNAPSHOTS
  final var mode = TestMode.update
  #else
  final var mode = TestMode.verify
  #endif

  final var subdirectory = ""
  final var rootDirectory = "json"

  static func isSupported(
    file: JsonFile,
    rootDirectory: String
  ) -> Bool {
    guard
      let jsonDict = jsonDict(
        fileName: file.name,
        subdirectory: rootDirectory + "/" + file.subdirectory
      )
    else {
      // json is invalid, test should be added into the test suite
      return true
    }

    guard let platforms = jsonDict.getArray("platforms").value else {
      return true
    }

    return platforms.contains { $0 as? String == "ios" }
  }

  final func run(
    _ fileName: String,
    testName: String = #function,
    customCaseName: String? = nil,
    blocksState: BlocksState = [:],
    extensions: [DivExtensionHandler] = []
  ) {
    guard
      let jsonDict = jsonDict(fileName: fileName, subdirectory: rootDirectory + "/" + subdirectory)
    else {
      XCTFail("Invalid json: \(fileName)")
      return
    }

    let divKitComponents = DivKitComponents(
      extensionHandlers: extensions,
      fontProvider: YSFontProvider(),
      imageHolderFactory: TestImageHolderFactory(),
      layoutDirection: getLayoutDirection(from: jsonDict)
    )
    for (path, state) in blocksState {
      divKitComponents.blockStateStorage.setState(path: path, state: state)
    }

    let caseName = customCaseName
      ?? (fileName.removingFileExtension + "_" + testName.extractingDescription)

    do {
      let view = DivView(divKitComponents: divKitComponents)
      try view.setSource(.init(
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
          try checkSnapshots(
            view: view,
            caseName: caseName,
            stepName: step.name ?? "step\(index)"
          )
        }
      } else {
        try checkSnapshots(view: view, caseName: caseName)
      }
    } catch {
      XCTFail("Testing div failed with error: \(error.localizedDescription)")
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
    try? dictionary
      .getOptionalArray(
        "steps",
        transform: { try TestStep(dictionary: $0 as [String: Any]) }
      ).unwrap()
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
    guard let image = nonEmptyView.makeSnapshot() else {
      if let stepName {
        throw "Failed to create spanshot. Step: \(stepName)"
      }
      throw "Failed to create spanshot."
    }

    SnapshotTestKit.compareSnapshot(
      image,
      referenceURL: referenceFileURL(screen: screen, caseName: caseName, stepName: stepName),
      mode: mode
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
      .appendingPathComponent(subdirectory)
      .appendingPathComponent(
        "\(caseName)_\(Int(screen.size.width))@\(Int(screen.scale))x\(stepDescription).png",
        isDirectory: false
      )
  }
}

private func jsonDict(fileName: String, subdirectory: String) -> [String: Any]? {
  guard let data = testBundle.url(
    forResource: fileName,
    withExtension: nil,
    subdirectory: subdirectory
  ).flatMap({ try? Data(contentsOf: $0) }) else {
    return nil
  }
  return (try? JSONSerialization.jsonObject(with: data)) as? [String: Any]
}

extension String {
  fileprivate var extractingDescription: String {
    let removedPrefix = "test_"
    let removedSuffix = "()"
    return String(self[removedPrefix.count..<(count - removedSuffix.count)])
  }

  var removingFileExtension: String {
    guard let dotIndex = firstIndex(of: ".") else {
      return self
    }
    return String(self[startIndex..<dotIndex])
  }
}

private let testBundle = Bundle(for: DivKitSnapshotTestCase.self)

private final class TestImageHolderFactory: DivImageHolderFactory {
  private var reportedUrls = Set<String>()

  func make(_ url: URL?, _ placeholder: ImagePlaceholder?) -> ImageHolder {
    guard let url, url.absoluteString != "empty://" else {
      return placeholder?.toImageHolder() ?? NilImageHolder()
    }

    if let image = UIImage(named: url.lastPathComponent, in: testBundle, compatibleWith: nil) {
      return image
    }

    let urlString = url.absoluteString
    if !reportedUrls.contains(urlString) {
      XCTFail(
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
