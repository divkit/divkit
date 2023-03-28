import AVFoundation
import CoreGraphics
import Foundation

import BasePublic
import CommonCorePublic

public final class VideoBlock: BlockWithTraits {
  public struct VideoAssetHolder {
    let url: URL
    let playerItem: AVPlayerItem

    public init(
      url: URL,
      playerItem: AVPlayerItem
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

  public init(
    videoAssetHolder: VideoAssetHolder,
    widthTrait: LayoutTrait,
    heightTrait: LayoutTrait
  ) {
    self.videoAssetHolder = videoAssetHolder
    self.widthTrait = widthTrait
    self.heightTrait = heightTrait
  }

  public let intrinsicContentWidth: CGFloat = 0
  public func intrinsicContentHeight(forWidth _: CGFloat) -> CGFloat { 0 }

  public func getImageHolders() -> [ImageHolder] { [] }

  public func equals(_ other: Block) -> Bool {
    guard let other = other as? VideoBlock else {
      return false
    }

    return self == other
  }
}

extension VideoBlock: LayoutCachingDefaultImpl {}
extension VideoBlock: ElementStateUpdatingDefaultImpl {}

public func ==(rhs: VideoBlock, lhs: VideoBlock) -> Bool {
  rhs.videoAssetHolder.url == lhs.videoAssetHolder.url
}
