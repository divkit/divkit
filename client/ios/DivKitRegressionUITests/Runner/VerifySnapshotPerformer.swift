import UIKit
import VGSL
import XCTest

@MainActor
final class VerifySnapshotPerformer {
  private enum Mode {
    case verify
    case record
  }

  private let referenceSnapshotsDirectoryURL: URL
  private let scenarioPath: String
  private let mode: Mode = ProcessInfo.processInfo.arguments.contains("UPDATE_SNAPSHOTS")
    ? .record : .verify
  private var hasRecordedSnapshots = false

  init(referenceSnapshotsDirectoryURL: URL, scenarioPath: String) {
    self.referenceSnapshotsDirectoryURL = referenceSnapshotsDirectoryURL
    self.scenarioPath = scenarioPath
  }

  func perform(
    name: String,
    on element: any XCUIScreenshotProviding,
    croppingTo region: CGRect?
  ) throws {
    try XCTContext.runActivity(named: "verify_snapshot: \(name)") { activity in
      let screenshot = element.screenshot()
      let image = try region.map {
        try crop(screenshot.image, to: $0)
      } ?? screenshot.image
      do {
        let referenceURL = try snapshotReferenceURL(name: name, scale: image.scale)
        let reference: UIImage?
        do {
          let data = try Data(contentsOf: referenceURL)
          reference = UIImage(data: data, scale: image.scale)
        } catch {
          if mode == .verify {
            throw error
          }
          reference = nil
        }
        if let reference, image.compare(with: reference) {
          return
        }

        if mode == .record {
          try FileManager.default.createDirectory(
            at: referenceURL.deletingLastPathComponent(),
            withIntermediateDirectories: true
          )
          let data = image.pngData()
          guard let data else {
            throw SnapshotError.invalidImage
          }
          try data.write(to: referenceURL, options: .atomic)
          hasRecordedSnapshots = true
          return
        }

        guard let reference else {
          throw SnapshotError.invalidReference(referenceURL)
        }

        let expected = XCTAttachment(image: reference)
        expected.name = "\(name)_reference.png"
        expected.lifetime = .keepAlways
        activity.add(expected)
        let diff = XCTAttachment(image: image.makeDiff(with: reference))
        diff.name = "\(name)_diff.png"
        diff.lifetime = .keepAlways
        activity.add(diff)
        throw SnapshotError.mismatch(referenceURL)
      } catch {
        let actual = XCTAttachment(image: image)
        actual.name = "\(name)_actual.png"
        actual.lifetime = .keepAlways
        activity.add(actual)
        throw error
      }
    }
  }

  private func crop(_ image: UIImage, to region: CGRect) throws -> UIImage {
    let imageBounds = CGRect(origin: .zero, size: image.size)
    guard imageBounds.contains(region),
          let cropped = image.crop(
            rect: region.relative(in: imageBounds),
            preserveScale: true
          ) else {
      throw SnapshotError.invalidRegion(region)
    }
    return cropped
  }

  func validateExecutionResult() throws {
    if hasRecordedSnapshots {
      throw SnapshotError.snapshotsRecorded
    }
  }

  private func snapshotReferenceURL(name: String, scale: CGFloat) throws -> URL {
    guard !name.isEmpty, ![".", ".."].contains(name),
          (name as NSString).lastPathComponent == name else {
      throw SnapshotError.invalidName(name)
    }
    let caseURL = referenceSnapshotsDirectoryURL
      .appendingPathComponent(scenarioPath)
      .deletingPathExtension()
    let width = Int(AppMainWindow.shared.frame.width)
    let filename = "\(caseURL.lastPathComponent)_\(width)@\(Int(scale))x_\(name).png"
    return caseURL.deletingLastPathComponent().appendingPathComponent(filename)
  }
}

private enum SnapshotError: LocalizedError {
  case snapshotsRecorded
  case invalidImage
  case invalidRegion(CGRect)
  case invalidName(String)
  case invalidReference(URL)
  case mismatch(URL)

  var errorDescription: String? {
    switch self {
    case .snapshotsRecorded:
      "Snapshots recorded. Run without UPDATE_SNAPSHOTS to verify them."
    case .invalidImage:
      "Cannot encode snapshot image"
    case let .invalidRegion(region):
      "Invalid snapshot region: \(region)"
    case let .invalidName(name):
      "Invalid snapshot name: \(name)"
    case let .invalidReference(url):
      "Cannot decode snapshot reference: \(url.path)"
    case let .mismatch(url):
      "Actual snapshot is not equal to the reference: \(url.path)"
    }
  }
}
