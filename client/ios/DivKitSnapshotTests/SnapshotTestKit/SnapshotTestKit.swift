import CoreGraphics
import Foundation
import Testing
import UIKit
import VGSL

enum TestMode {
  case update
  case verify
}

enum SnapshotTestKit {
  static func compareSnapshot(
    _ snapshot: UIImage,
    referenceFileUrl: URL,
    resultFolderUrl: URL,
    mode: TestMode
  ) throws {
    let deviceModel = ProcessInfo.processInfo.environment["SIMULATOR_MODEL_IDENTIFIER"]
      ?? UIDevice.current.systemModelName

    #expect(
      allowedDevices.contains(deviceModel),
      Comment(
        "Prohibited device: '\(deviceModel)'. Please, run snapshot tests on one of allowed device: '\(allowedDevices)'."
      )
    )

    let fileName = referenceFileUrl.lastPathComponent
    removeFile(path: resultFolderUrl, fileName: fileName)

    switch mode {
    case .update:
      if let reference = try? UIImage.makeWith(url: referenceFileUrl),
         snapshot.compare(with: reference) {
        return
      }

      try! saveFile(
        path: referenceFileUrl.deletingLastPathComponent(),
        fileName: fileName,
        image: snapshot
      )

      Issue.record(
        Comment("Snapshot saved. Don't forget to change mode back to `verify`.")
      )
    case .verify:
      let reference = try! UIImage.makeWith(url: referenceFileUrl)
      if snapshot.compare(with: reference) {
        return
      }

      let diff = snapshot.makeDiff(with: reference)
      try! saveFile(path: resultFolderUrl, fileName: "result.png", image: snapshot)
      try! saveFile(path: resultFolderUrl, fileName: "reference.png", image: reference)
      try! saveFile(path: resultFolderUrl, fileName: "diff.png", image: diff)

      Issue.record(
        Comment(
          "Actual snapshot is not equal to the reference. Diff is saved in Tests/results folder."
        )
      )
    }
  }
}

private func removeFile(path: URL, fileName: String) {
  let fileManager = FileManager.default
  if !fileManager.fileExists(atPath: path.path) {
    try? fileManager.removeItem(at: path.appendingPathComponent(fileName))
  }
}

private func saveFile(
  path: URL,
  fileName: String,
  image: UIImage
) throws {
  let fileManager = FileManager.default
  if !fileManager.fileExists(atPath: path.path) {
    try! fileManager.createDirectory(at: path, withIntermediateDirectories: true)
  }

  let pngData = try #require(image.pngData())
  try pngData.write(to: path.appendingPathComponent(fileName))
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
