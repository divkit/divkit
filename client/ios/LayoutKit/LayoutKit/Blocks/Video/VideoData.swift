import Foundation
import UIKit

public struct VideoData: Equatable {
  public let videos: [Video]

  public init(videos: [Video] = []) {
    self.videos = videos
  }
}

public struct Video: Equatable {
  public let url: URL

  public let resolution: CGSize?
  public let bitrate: Double?

  public let mimeType: String

  public init(
    url: URL,
    resolution: CGSize? = nil,
    bitrate: Double? = nil,
    mimeType: String? = nil
  ) {
    self.url = url
    self.bitrate = bitrate
    self.resolution = resolution
    self.mimeType = mimeType ?? ""
  }
}
