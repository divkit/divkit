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
  @MainActor
  static func compareSnapshot(
    _ snapshot: UIImage,
    referenceFileUrl: URL,
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

    switch mode {
    case .update:
      if let reference = try? UIImage.makeWith(url: referenceFileUrl),
         snapshot.compare(with: reference) {
        return
      }

      try! saveFile(
        path: referenceFileUrl.deletingLastPathComponent(),
        fileName: referenceFileUrl.lastPathComponent,
        image: snapshot
      )

      Issue.record(Comment("Snapshot saved. Don't forget to change mode back to `verify`."))
    case .verify:
      let reference = try! UIImage.makeWith(url: referenceFileUrl)
      if snapshot.compare(with: reference) {
        return
      }

      try recordAttachment(fileName: "result.png", image: snapshot)
      try recordAttachment(fileName: "reference.png", image: reference)
      try recordAttachment(fileName: "diff.png", image: snapshot.makeDiff(with: reference))

      Issue.record(Comment("Actual snapshot is not equal to the reference."))
    }
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

private func recordAttachment(fileName: String, image: UIImage) throws {
  let pngData = try #require(image.pngData())
  let attachment = Attachment(pngData, named: fileName)
  Attachment.record(attachment)
}

extension UIImage {
  @MainActor
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
