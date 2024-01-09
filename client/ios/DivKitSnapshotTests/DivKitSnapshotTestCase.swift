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

  func getLayoutDirection(from jsonDict: [String: Any]) -> UserInterfaceLayoutDirection {
    let configuration = try? jsonDict.getField("configuration") as [String: Any]
    guard configuration?["layout_direction"] as? String == "rtl" else {
      return .leftToRight
    }
    return .rightToLeft
  }

  final func testDivs(
    _ fileName: String,
    testName: String = #function,
    customCaseName: String? = nil,
    imageHolderFactory: DivImageHolderFactory? = nil,
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
      imageHolderFactory: imageHolderFactory ?? TestImageHolderFactory(),
      layoutDirection: getLayoutDirection(from: jsonDict)
    )
    for (path, state) in blocksState {
      divKitComponents.blockStateStorage.setState(path: path, state: state)
    }

    let dataWithErrors = loadDivData(dictionary: jsonDict, divKitComponents: divKitComponents)
    guard let data = dataWithErrors.0 else {
      XCTFail(
        "Data could not be created from json \(fileName), try to set breakpoint on the following errors: \(dataWithErrors.1.map { type(of: $0) })"
      )
      return
    }

    do {
      try testDivs(
        data: data,
        divKitComponents: divKitComponents,
        caseName: customCaseName ??
          (fileName.removingFileExtension + "_" + testName.extractingDescription),
        steps: loadSteps(dictionary: jsonDict)
      )
    } catch {
      XCTFail("Testing div failed with error: \(error.localizedDescription)")
    }
  }

  private func loadSteps(dictionary: [String: Any]) throws -> [TestStep]? {
    try? dictionary
      .getOptionalArray(
        "steps",
        transform: { try TestStep(dictionary: $0 as [String: Any]) }
      ).unwrap()
  }

  private func loadDivData(
    dictionary: [String: Any],
    divKitComponents: DivKitComponents
  ) -> (DivData?, [Error]) {
    var errors = [Error]()
    do {
      let divData = try dictionary.getOptionalField("div_data") ?? dictionary
      let result = try divKitComponents.parseDivDataWithTemplates(divData, cardId: testDivCardId)
      if let data = result.value {
        return (data, errors)
      } else {
        errors += result.errorsOrWarnings?.map { $0 as Error } ?? []
      }
    } catch {
      errors.append(error)
    }
    return (nil, errors)
  }

  private func testDivs(
    data: DivData,
    divKitComponents: DivKitComponents,
    caseName: String,
    steps: [TestStep]? = nil
  ) throws {
    if let steps {
      for (index, step) in steps.enumerated() {
        step.divActions?.forEach { divAction in
          divKitComponents.actionHandler.handle(
            divAction,
            cardId: testDivCardId,
            source: .tap,
            sender: nil
          )
        }
        try checkSnapshots(
          data: data,
          divKitComponents: divKitComponents,
          caseName: caseName,
          stepName: step.name ?? "step\(index)"
        )
      }
    } else {
      try checkSnapshots(
        data: data,
        divKitComponents: divKitComponents,
        caseName: caseName
      )
    }
  }

  private func checkSnapshots(
    data: DivData,
    divKitComponents: DivKitComponents,
    caseName: String,
    stepName: String? = nil
  ) throws {
    let currentScale = UIScreen.main.scale
    let devices = ScreenSize.portrait.filter { $0.scale == currentScale }
    try devices.forEach { device in
      var view = try makeDivView(
        data: data,
        divKitComponents: divKitComponents,
        size: device.size
      )
      if view.bounds.isEmpty {
        view = makeEmptyView()
      }
      guard let image = view.makeSnapshot() else {
        throw DivTestingErrors.snapshotCouldNotBeCreated
      }
      let referenceUrl = referenceFileURL(
        device: device,
        caseName: caseName,
        stepName: stepName
      )

      SnapshotTestKit.testSnapshot(
        image,
        referenceURL: referenceUrl,
        mode: mode
      )
      checkSnapshotsForAnotherScales(currentScale, caseName: caseName, stepName: stepName)
    }
  }

  private func checkSnapshotsForAnotherScales(
    _ scale: CGFloat,
    caseName: String,
    stepName: String?
  ) {
    let scaleToCheck: CGFloat = scale == 2 ? 3 : 2
    let devices = ScreenSize.portrait.filter { $0.scale == scaleToCheck }
    let fileManager = FileManager.default
    devices.forEach { device in
      let referenceUrl = referenceFileURL(
        device: device,
        caseName: caseName,
        stepName: stepName
      )
      let path = referenceUrl.path
      XCTAssertTrue(
        fileManager.fileExists(atPath: path),
        "Don't forget to add file with scale \(scaleToCheck) and path \(path)"
      )
    }
  }

  private func referenceFileURL(
    device: ScreenSize,
    caseName: String,
    stepName: String?
  ) -> URL {
    let referencesURL = URL(
      fileURLWithPath: ReferenceSet.path,
      isDirectory: true
    ).appendingPathComponent(subdirectory)
    var stepDescription = ""
    if let stepName {
      stepDescription = "_" + stepName
    }
    let fileName = caseName + "_\(Int(device.size.width))" + device.scale
      .imageSuffix + "\(stepDescription).png"
    return referencesURL.appendingPathComponent(fileName, isDirectory: false)
  }

  private func makeDivView(
    data: DivData,
    divKitComponents: DivKitComponents,
    size: CGSize
  ) throws -> UIView {
    guard let block = data.makeBlock(
      divKitComponents: divKitComponents
    ) else {
      throw DivTestingErrors.blockCouldNotBeCreatedFromData
    }
    let blockSize = block.size(forResizableBlockSize: size)
    let divView = block.makeBlockView()
    divView.frame = CGRect(origin: .zero, size: blockSize)
    divView.layoutIfNeeded()
    return divView
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

private func makeEmptyView() -> UIView {
  let label = UILabel()
  label.text = "<empty view>"
  label.frame = CGRect(origin: .zero, size: label.intrinsicContentSize)
  return label
}

private enum DivTestingErrors: LocalizedError {
  case blockCouldNotBeCreatedFromData
  case snapshotCouldNotBeCreated

  var errorDescription: String? {
    switch self {
    case .blockCouldNotBeCreatedFromData:
      return "Block could not be created from data"
    case .snapshotCouldNotBeCreated:
      return "Snapshot could not be created from view"
    }
  }
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

extension CGFloat {
  fileprivate var imageSuffix: String {
    self == 1 ? "" : "@\(Int(self))x"
  }
}

private let testBundle = Bundle(for: DivKitSnapshotTestCase.self)

extension DivData {
  fileprivate func makeBlock(
    divKitComponents: DivKitComponents
  ) -> Block? {
    let context = divKitComponents.makeContext(
      cardId: testDivCardId,
      cachedImageHolders: []
    )
    do {
      return try makeBlock(context: context)
    } catch {
      XCTFail("Failure while making block: \(error)")
      return nil
    }
  }
}

private final class TestImageHolderFactory: DivImageHolderFactory {
  private var reportedUrls = Set<String>()

  func make(_ url: URL?, _: ImagePlaceholder?) -> ImageHolder {
    guard let url else {
      XCTFail("Predefined images not supported in tests")
      return UIImage()
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
  let divActions: [DivActionBase]?

  public init(dictionary: [String: Any]) throws {
    let expectedScreenshot: String? = try dictionary.getOptionalField("expected_screenshot")
    name = expectedScreenshot?.replacingOccurrences(of: ".png", with: "")
    divActions = try? dictionary.getOptionalArray(
      "div_actions",
      transform: { (actionDictionary: [String: Any]) -> DivActionBase in
        try DivTemplates.empty.parseValue(type: DivActionTemplate.self, from: actionDictionary)
          .unwrap()
      }
    ).unwrap()
  }
}
