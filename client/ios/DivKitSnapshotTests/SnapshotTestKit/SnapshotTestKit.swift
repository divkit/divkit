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
  public static func compareSnapshot(
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
        throw "Snapshot saved. Don't forget to change mode back to `verify`!"
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
        throw "View snapshot is not equal to reference. Diff is attached in test result."
      }
    } catch {
      XCTFail("Error in \(mode) mode for \(referenceURL.path): \(error.localizedDescription)")
    }
  }
}

extension UIImage {
  fileprivate func makePNGData() throws -> Data {
    guard let data = pngData() else {
      throw "UIImage.pngData() returns nil"
    }
    return data
  }

  fileprivate static func makeWith(url: URL) throws -> UIImage {
    let data = try Data(contentsOf: url)
    guard let image = UIImage(data: data, scale: UIScreen.main.scale) else {
      throw "UIImage could not be created with data"
    }
    return image
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

extension String: LocalizedError {
  public var errorDescription: String? {
    self
  }
}
