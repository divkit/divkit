import CoreGraphics
import Foundation
import UIKit
import XCTest

import CommonCorePublic

public enum TestMode {
  case update
  case verify
}

public enum SnapshotTestKit {
  public static func testSnapshot(
    _ snapshot: UIImage,
    referenceURL: URL,
    mode: TestMode
  ) {
    let deviceModel = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"]
      ?? UIDevice.current.systemModelName
    guard allowedDevices.contains(deviceModel) else {
      XCTFail(
        "Prohibited device: '\(deviceModel)'. " +
          "Please, run snapshot tests on one of allowed device: '\(allowedDevices)'."
      )
      return
    }

    do {
      switch mode {
      case .update:
        if let reference = try? UIImage.makeWith(url: referenceURL),
           snapshot.compare(with: reference) {
          return
        }

        let snapshotsDir = referenceURL.deletingLastPathComponent()
        let fileManager = FileManager.default
        if !fileManager.fileExists(atPath: snapshotsDir.path) {
          try fileManager.createDirectory(at: snapshotsDir, withIntermediateDirectories: true)
        }

        try snapshot.makePNGData().write(to: referenceURL)
        throw SnapshotTestError.updateModeEnabled
      case .verify:
        let reference = try UIImage.makeWith(url: referenceURL)
        if snapshot.compare(with: reference) {
          return
        }

        let diff = snapshot.makeDiff(with: reference)
        XCTContext.runActivity(named: "Diff of \(referenceURL.lastPathComponent)") {
          $0.add(.init(image: snapshot, name: "result"))
          $0.add(.init(image: reference, name: "reference"))
          $0.add(.init(image: diff, name: "diff"))
        }
        throw SnapshotTestError.comparisonFailed
      }
    } catch {
      XCTFail("Error in \(mode) mode for \(referenceURL.path): \(error.localizedDescription)")
    }
  }
}

extension UIImage {
  fileprivate func makePNGData() throws -> Data {
    guard let data = pngData() else {
      throw SnapshotTestError.nilPNGData
    }
    return data
  }

  fileprivate static func makeWith(url: URL) throws -> UIImage {
    let data = try Data(contentsOf: url)
    guard let image = UIImage(data: data, scale: UIScreen.main.scale) else {
      throw SnapshotTestError.imageCouldNotBeCreated
    }
    return image
  }
}

private enum SnapshotTestError: LocalizedError {
  case updateModeEnabled
  case comparisonFailed
  case nilPNGData
  case imageCouldNotBeCreated

  var errorDescription: String? {
    switch self {
    case .updateModeEnabled:
      return "Snapshot saved. Don't forget to change mode back to `verify`!"
    case .comparisonFailed:
      return "View snapshot is not equal to reference. Diff is attached in test result."
    case .nilPNGData:
      return "UIImage.pngData() returns nil"
    case .imageCouldNotBeCreated:
      return "UIImage could not be created with data"
    }
  }
}

private let allowedDevices: Set<String> = [
  "iPhone12,8",
  "iPhone14,7",
]

extension XCTAttachment {
  fileprivate convenience init(image: UIImage, name: String) {
    self.init(image: image)
    self.name = name
  }
}
