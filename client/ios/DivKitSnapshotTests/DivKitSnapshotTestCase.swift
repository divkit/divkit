import XCTest

import BaseUI
import CommonCore
import DivKit
import LayoutKit
import Networking

let testCardId = "test_card_id"

internal class DivKitSnapshotTestCase: XCTestCase {
  final var mode = TestMode.verify
  final var subdirectory = ""
  final var rootDirectory = "json"
  final var blocksState = BlocksState()

  override func setUp() {
    super.setUp()
    mode = .verify
  }

  final func testDivs(
    _ fileName: String,
    testName: String = #function,
    customCaseName: String? = nil,
    imageHolderFactory: ImageHolderFactory? = nil
  ) {
    let dataWithErrors = loadDivData(fileName: fileName)
    guard let data = dataWithErrors.0 else {
      XCTFail(
        "Data could not be created from json \(fileName), try to set breakpoint on the following errors: \(dataWithErrors.1.map { type(of: $0) })"
      )
      return
    }

    do {
      try testDivs(
        data: data,
        imageHolderFactory: imageHolderFactory ?? makeImageHolderFactory(),
        caseName: customCaseName ??
          (fileName.removingFileExtension + "_" + testName.extractingDescription)
      )
    } catch {
      XCTFail("Testing div failed with error: \(error.localizedDescription)")
    }
  }

  private func loadDivData(fileName: String) -> (DivData?, [Error]) {
    guard let data = jsonData(fileName: fileName, subdirectory: rootDirectory + "/" + subdirectory),
          let dict = jsonDict(data: data) else {
      return (nil, [DivTestingErrors.invalidData])
    }

    var errors = [Error]()
    do {
      let result = try RawDivData(dictionary: dict).resolve()
      if let data = result.value {
        return (data, errors)
      } else {
        errors += result.getErrorsOrWarnings()
      }
    } catch {
      errors.append(error)
    }
    return (nil, errors)
  }

  private func testDivs(
    data: DivData,
    imageHolderFactory: ImageHolderFactory,
    caseName: String
  ) throws {
    let currentScale = UIScreen.main.scale
    let devices = ScreenSize.portrait.filter { $0.scale == currentScale }
    let widths = devices.uniqueWidths
    let views = try makeDivViews(
      data: data,
      imageHolderFactory: imageHolderFactory,
      widths: widths
    )
    let snapshots = try views.map { view -> UIImage in
      guard let snapshot = view.makeSnapshot() else {
        throw DivTestingErrors.snapshotCouldNotBeCreated
      }
      return snapshot
    }
    let referencesURL = URL(
      fileURLWithPath: ReferenceSet.path,
      isDirectory: true
    ).appendingPathComponent(subdirectory)
    func referenceFileURL(forWidth width: CGFloat, scale: CGFloat) -> URL {
      let fileName = caseName + "_\(Int(width))" + scale.imageSuffix + ".png"
      return referencesURL.appendingPathComponent(fileName, isDirectory: false)
    }
    let referenceURLs = widths.map {
      referenceFileURL(forWidth: $0, scale: currentScale)
    }
    zip(snapshots, referenceURLs).forEach { snapshot, referenceURL in
      SnapshotTestKit.testSnapshot(
        snapshot,
        referenceURL: referenceURL,
        diffDirPath: referencesURL.path,
        mode: mode
      )
    }
    checkSnapshotsForAnotherScales(currentScale, nameForWidth: referenceFileURL(forWidth:scale:))
  }

  private func makeDivViews(
    data: DivData,
    imageHolderFactory: ImageHolderFactory,
    widths: [CGFloat]
  ) throws -> [UIView] {
    var views = [UIView]()
    try widths.forEach { width in
      guard let block = data.makeBlock(
        blocksState: blocksState,
        imageHolderFactory: imageHolderFactory
      ) else {
        throw DivTestingErrors.blockCouldNotBeCreatedFromData
      }
      let blockWidth = block.isHorizontallyResizable ? width : block
        .widthOfHorizontallyNonResizableBlock
      let height = block.heightOfVerticallyNonResizableBlock(forWidth: blockWidth)
      let divView = block.makeBlockView()
      divView.frame = CGRect(origin: .zero, size: CGSize(width: blockWidth, height: height))
      divView.layoutIfNeeded()
      views.append(divView)
    }
    return views
  }
}

private func jsonData(fileName: String, subdirectory: String) -> Data? {
  testBundle.url(
    forResource: fileName,
    withExtension: nil,
    subdirectory: subdirectory
  ).flatMap { try? Data(contentsOf: $0) }
}

private func jsonDict(data: Data) -> [String: Any]? {
  (try? JSONSerialization.jsonObject(with: data)) as? [String: Any]
}

private enum DivTestingErrors: LocalizedError {
  case invalidData
  case blockCouldNotBeCreatedFromData
  case snapshotCouldNotBeCreated

  var errorDescription: String? {
    switch self {
    case .invalidData:
      return "Invalid data"
    case .blockCouldNotBeCreatedFromData:
      return "Block could not be created from data"
    case .snapshotCouldNotBeCreated:
      return "Snapshot could not be created from view"
    }
  }
}

private func checkSnapshotsForAnotherScales(
  _ scale: CGFloat,
  nameForWidth: (CGFloat, CGFloat) -> URL
) {
  let scaleToCheck: CGFloat = scale == 2 ? 3 : 2
  let devices = ScreenSize.portrait.filter { $0.scale == scaleToCheck }
  let widthsToCheck = devices.uniqueWidths
  let fileManager = FileManager.default
  widthsToCheck.forEach { width in
    let path = nameForWidth(width, scaleToCheck).path
    XCTAssertTrue(
      fileManager.fileExists(atPath: path),
      "Don't forget to add file with scale \(scaleToCheck) and path \(path)"
    )
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
    blocksState: BlocksState,
    imageHolderFactory: ImageHolderFactory
  ) -> Block? {
    let context = DivBlockModelingContext(
      cardId: DivCardID(rawValue: testCardId),
      stateManager: DivStateManager(),
      blockStateStorage: DivBlockStateStorage(states: blocksState),
      imageHolderFactory: imageHolderFactory,
      fontSpecifiers: fontSpecifiers,
      variables: variables.flatMap { $0.extractDivVariableValues() } ?? [:]
    )

    do {
      return try makeBlock(context: context)
    } catch {
      XCTFail("Failure while making block: \(error)")
      return nil
    }
  }
}

extension ImageHolderFactory {
  static let placeholderOnly = ImageHolderFactory(
    make: { _, placeholder in
      switch placeholder {
      case let .image(image)?:
        return image
      case let .color(color)?:
        return ColorHolder(color: color)
      case .none:
        return NilImageHolder()
      }
    }
  )
}

private var reportedURLStrings = Set<String>()

private func makeImageHolderFactory() -> ImageHolderFactory {
  ImageHolderFactory(
    make: { url, _ in
      guard let url = url else {
        XCTFail("Predefined images not supported in tests")
        return UIImage()
      }

      if let image = UIImage(named: url.lastPathComponent, in: testBundle, compatibleWith: nil) {
        return image
      }

      let absoluteString = url.absoluteString
      if !reportedURLStrings.contains(absoluteString) {
        XCTFail(
          "Loading images from network is prohibited in tests. You need to load image from "
            + absoluteString + " and add it to Images.xcassets in testing bundle"
        )
        reportedURLStrings.insert(absoluteString)
      }

      return UIImage()
    }
  )
}
