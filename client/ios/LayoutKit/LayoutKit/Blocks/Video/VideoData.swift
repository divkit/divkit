import Foundation

public enum VideoData: Equatable {
  case stream(URL)
  case video([Video])
}

public struct Video: Equatable {
  let url: URL
  let resolution: CGSize
  let codec: String?
  let mimeType: String?

  public init(
    url: URL,
    resolution: CGSize = .zero,
    codec: String? = nil,
    mimeType: String? = nil
  ) {
    self.url = url
    self.resolution = resolution
    self.codec = codec
    self.mimeType = mimeType
  }
}
