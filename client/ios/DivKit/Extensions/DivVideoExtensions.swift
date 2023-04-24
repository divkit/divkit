import BaseTinyPublic
import Foundation
import LayoutKit
import CoreMedia

extension DivVideo: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let resumeActions = resumeActions?
      .compactMap { $0.makeUiAction(context: context.actionContext) } ?? []
    let bufferingActions = bufferingActions?.compactMap {
      $0.makeUiAction(context: context.actionContext)
    } ?? []
    let endActions = endActions?.compactMap {
      $0.makeUiAction(context: context.actionContext)
    } ?? []
    let resolver = context.expressionResolver
    let repeatable = resolveRepeatable(resolver)
    let muted = resolveMuted(resolver)
    let autostart = resolveAutostart(resolver)
    let elapsedTime = elapsedTimeVariable.flatMap {
      Binding<Int>(context: context, name: $0)
    }
    let preview: Image? = resolvePreview(resolver).flatMap(_makeImage(base64:))
    let videoData = videoData.makeVideoData(resolver: resolver)

    let playbackConfig = PlaybackConfig(
      autoPlay: autostart,
      repeatable: repeatable,
      isMuted: muted,
      startPosition: elapsedTime.flatMap { CMTime.init(value: $0.wrappedValue) } ?? .zero,
      settingsPayload: playerSettingsPayload ?? [:]
    )

    let model = VideoBlockViewModel(
      videoData: videoData,
      playbackConfig: playbackConfig,
      preview: preview,
      elapsedTime: elapsedTime,
      resumeActions: resumeActions,
      bufferingActions: bufferingActions,
      endActions: endActions,
      path: context.parentPath
    )

    let videoPath = context.parentPath + (id ?? DivVideo.type)
    let videoContext = modified(context) {
      $0.parentPath = videoPath
      $0.galleryResizableInsets = nil
    }

    let state: VideoBlockViewState = videoContext.blockStateStorage
      .getState(videoContext.parentPath) ?? .init(state: .playing)

    return VideoBlock(
      widthTrait: width.makeLayoutTrait(with: resolver),
      heightTrait: height.makeLayoutTrait(with: resolver),
      model: model,
      state: state,
      playerFactory: context.playerFactory
    )
  }
}

extension DivVideoData {
  func makeVideoData(resolver: ExpressionResolver) -> VideoData {
    switch self {
    case let .divVideoDataStream(stream):
      return .stream(stream.resolveUrl(resolver)!)
    case let .divVideoDataVideo(videos):
      return .video(videos.videoSources.map {
        let resolution: CGSize? = $0.resolution.flatMap { resolution in
          CGSize(
            width: resolution.resolveWidth(resolver) ?? 0,
            height: resolution.resolveHeight(resolver) ?? 0
          )
        }
        return LayoutKit.Video(
          url: $0.resolveUrl(resolver)!,
          resolution: resolution ?? .zero,
          codec: $0.resolveCodec(resolver),
          mimeType: $0.resolveMimeType(resolver)
        )
      })
    }
  }
}

private func _makeImage(base64: String) -> Image? {
  decode(base64: base64).flatMap(Image.init(data:))
}

fileprivate func decode(base64: String) -> Data? {
  if let data = Data(base64Encoded: base64) {
    return data
  }
  if let url = URL(string: base64),
     let dataHoldingURL = try? Data(contentsOf: url) {
    return dataHoldingURL
  }
  return nil
}
