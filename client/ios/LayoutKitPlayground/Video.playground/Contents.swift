import LayoutKit
import LayoutKitPlayground
import PlaygroundSupport
import UIKit
import VGSL

private func createBlock() throws -> Block {
  let videoData = VideoData(
    videos: [
      Video(
        url: URL(string: "https://yastatic.net/s3/home/divkit/bears.mp4")!,
        mimeType: "video/mp4"
      ),
    ]
  )

  let playbackConfig = PlaybackConfig(
    autoPlay: true,
    repeatable: true,
    isMuted: true,
    startPosition: .zero,
    settingsPayload: [:]
  )

  let videoModel = VideoBlockViewModel(
    videoData: videoData,
    playbackConfig: playbackConfig,
    path: .init("video")
  )

  return try ContainerBlock(
    layoutDirection: .vertical,
    horizontalChildrenAlignment: .center,
    verticalChildrenAlignment: .center,
    children: [
      TextBlock(
        widthTrait: .intrinsic,
        text: "1:1".with(typo: Typo(size: 16.0, weight: .regular))
      ),
      VideoBlock(
        widthTrait: .fixed(300.0),
        heightTrait: .fixed(200.0),
        model: videoModel,
        state: VideoBlockViewState(state: .playing),
        playerFactory: DefaultPlayerFactory()
      ),
    ]
  )
}

PlaygroundPage.current.liveView = LayoutKitPlaygroundViewController(blockProvider: createBlock)
