import Foundation

enum ReferenceSet {
  static var referenceSnapshotsPath: String {
    let plistPath = Bundle.main.path(forResource: "Info", ofType: "plist")!
    let plistContents = try! PropertyListDecoder().decode(
      PlistContents.self,
      from: Data(contentsOf: URL(fileURLWithPath: plistPath))
    )
    return plistContents.referenceSnapshotsPath
  }

  static var resultSnapshotsPath: String {
    let plistPath = Bundle.main.path(forResource: "Info", ofType: "plist")!
    let plistContents = try! PropertyListDecoder().decode(
      PlistContents.self,
      from: Data(contentsOf: URL(fileURLWithPath: plistPath))
    )
    return plistContents.resultSnapshotsPath
  }
}

private struct PlistContents: Decodable {
  enum CodingKeys: String, CodingKey {
    case referenceSnapshotsPath = "REFERENCE_SNAPSHOTS_PATH"
    case resultSnapshotsPath = "RESULT_SNAPSHOTS_PATH"
  }

  let referenceSnapshotsPath: String
  let resultSnapshotsPath: String
}
