// Copyright 2020 Yandex LLC. All rights reserved.

import Foundation

public enum ReferenceSet {
  public static var path: String {
    // GN build support
    if let legacyPath = legacyPath {
      return legacyPath
    }

    let plistPath = Bundle.main.path(forResource: "Info", ofType: "plist")!
    let plistContents = try! PropertyListDecoder().decode(
      PlistContents.self,
      from: Data(contentsOf: URL(fileURLWithPath: plistPath))
    )
    return plistContents.referenceSnapshotsPath
  }
}

private var legacyPath: String? {
  guard let plistPath = Bundle.main.path(forResource: "SnapshotsInfo", ofType: "plist") else {
    return nil
  }
  let plistContents = try! PropertyListDecoder().decode(
    LegacyPlistContents.self,
    from: Data(contentsOf: URL(fileURLWithPath: plistPath))
  )
  return plistContents.projectPath + "/DivKit/reference_snapshots"
}

private struct LegacyPlistContents: Decodable {
  let projectPath: String

  enum CodingKeys: String, CodingKey {
    case projectPath = "PROJECT_DIR_PATH"
  }
}

private struct PlistContents: Decodable {
  let referenceSnapshotsPath: String

  enum CodingKeys: String, CodingKey {
    case referenceSnapshotsPath = "REFERENCE_SNAPSHOTS_PATH"
  }
}
