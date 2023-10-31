import Foundation

enum ReferenceSet {
  static var path: String {
    let plistPath = Bundle.main.path(forResource: "Info", ofType: "plist")!
    let plistContents = try! PropertyListDecoder().decode(
      PlistContents.self,
      from: Data(contentsOf: URL(fileURLWithPath: plistPath))
    )
    return plistContents.referenceSnapshotsPath
  }
}

private struct PlistContents: Decodable {
  let referenceSnapshotsPath: String

  enum CodingKeys: String, CodingKey {
    case referenceSnapshotsPath = "REFERENCE_SNAPSHOTS_PATH"
  }
}
