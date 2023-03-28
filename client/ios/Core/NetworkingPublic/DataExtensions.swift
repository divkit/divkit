// Copyright 2019 Yandex LLC. All rights reserved.

import Foundation

private enum ImageHeaderData: CaseIterable {
  case png
  case jpeg
  case gif
  case tiff_01
  case tiff_02

  var rawValue: UInt8 {
    switch self {
    case .png:
      return 0x89
    case .jpeg:
      return 0xFF
    case .gif:
      return 0x47
    case .tiff_01:
      return 0x49
    case .tiff_02:
      return 0x4D
    }
  }
}

public enum ImageFormat {
  case unknown
  case png
  case jpeg
  case gif
  case tiff
}

extension Data {
  public var imageFormat: ImageFormat {
    guard !isEmpty else { return .unknown }
    let buffer: UInt8 = self[0]
    return ImageHeaderData.allCases.first { $0.rawValue == buffer }.format
  }
}

extension Optional where Wrapped == ImageHeaderData {
  fileprivate var format: ImageFormat {
    guard let format = self else { return .unknown }
    switch format {
    case .png:
      return .png
    case .jpeg:
      return .jpeg
    case .gif:
      return .gif
    case .tiff_01, .tiff_02:
      return .tiff
    }
  }
}
