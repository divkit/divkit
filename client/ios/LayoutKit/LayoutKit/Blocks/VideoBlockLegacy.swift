import AVFoundation
import CoreGraphics
import Foundation

import BasePublic
import CommonCorePublic

public final class VideoBlockLegacy: BlockWithTraits {
  public struct VideoAssetHolder {
    let url: URL
    let playerItem: Future<AVPlayerItem>

    public init(
      url: URL,
      playerItem: Future<AVPlayerItem>
    ) {
      self.url = url
      self.playerItem = playerItem
    }
  }

  public var debugDescription: String {
    "Video Block playing video with url \(videoAssetHolder.url)"
  }

  public let videoAssetHolder: VideoAssetHolder
  public let widthTrait: LayoutTrait
  public let heightTrait: LayoutTrait

  let autoplayAllowed: ObservableVariable<Bool>

  public init(
    videoAssetHolder: VideoAssetHolder,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait,
    autoplayAllowed: ObservableVariable<Bool>
  ) {
    self.videoAssetHolder = videoAssetHolder
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
    self.autoplayAllowed = autoplayAllowed
  }

  public let intrinsicContentWidth: CGFloat = 0
  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? VideoBlockLegacy else {
      return false
    }

    return self == other
  }
}

extension VideoBlockLegacy: LayoutCachingDefaultImpl {}
extension VideoBlockLegacy: ElementStateUpdatingDefaultImpl {}

public func ==(rhs: VideoBlockLegacy, lhs: VideoBlockLegacy) -> Bool {
  rhs.videoAssetHolder.url == lhs.videoAssetHolder.url
}
