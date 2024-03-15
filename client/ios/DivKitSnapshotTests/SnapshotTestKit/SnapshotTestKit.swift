import CoreGraphics
import Foundation
import Testing
import UIKit
import class XCTest.XCTAttachment
import class XCTest.XCTContext

import CommonCorePublic

enum TestMode {
  case update
  case verify
}

enum SnapshotTestKit {
  static func compareSnapshot(
    _ snapshot: UIImage,
    referenceURL: URL,
    mode: TestMode,
    file: JsonFile
  ) throws {
    let deviceModel = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"]
      ?? UIDevice.current.systemModelName

    #expect(
      allowedDevices.contains(deviceModel),

      Comment(
        rawValue: "Prohibited device: '\(deviceModel)'. " +
          "Please, run snapshot tests on one of allowed device: '\(allowedDevices)'."
      )
    )

    switch mode {
    case .update:
      if let reference = try? UIImage.makeWith(url: referenceURL),
         snapshot.compare(with: reference) {
        return
      }

      let snapshotsDir = referenceURL.deletingLastPathComponent()
      let fileManager = FileManager.default
      if !fileManager.fileExists(atPath: snapshotsDir.path) {
        try! fileManager.createDirectory(at: snapshotsDir, withIntermediateDirectories: true)
      }

      let pngData = try #require(snapshot.pngData())
      try pngData.write(to: referenceURL)
      Issue
        .record(
          Comment(
            "Snapshot saved. Don't forget to change mode back to `verify`!. Testing file path: \(file.absolutePath)"
          )
        )
    case .verify:
      let reference = try! UIImage.makeWith(url: referenceURL)
      if snapshot.compare(with: reference) {
        return
      }
      let diff = snapshot.makeDiff(with: reference)
      XCTContext.runActivity(named: "Diff of \(referenceURL.lastPathComponent)") {
        $0.add(.init(image: snapshot, name: "result"))
        $0.add(.init(image: reference, name: "reference"))
        $0.add(.init(image: diff, name: "diff"))
      }
      Issue
        .record(
          Comment(
            "View snapshot is not equal to reference. Diff is attached in test result. Testing file path: \(file.absolutePath)"
          )
        )
    }
  }
}

extension UIImage {
  fileprivate static func makeWith(url: URL) throws -> UIImage {
    let data = try Data(contentsOf: url)
    let image = try #require(UIImage(data: data, scale: UIScreen.main.scale))
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
