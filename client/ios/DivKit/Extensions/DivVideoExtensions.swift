import BaseTinyPublic
import CoreMedia
import Foundation
import LayoutKit

extension DivVideo: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    guard let playerFactory = context.playerFactory else {
      DivKitLogger.warning("There is no player factory in the context")
      return EmptyBlock()
    }

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
    let videoData = VideoData(videos: videoSources.map { $0.makeVideo(resolver: resolver) })

    let playbackConfig = PlaybackConfig(
      autoPlay: autostart,
      repeatable: repeatable,
      isMuted: muted,
      startPosition: elapsedTime.flatMap { CMTime(value: $0.wrappedValue) } ?? .zero,
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
      .getState(videoContext.parentPath) ?? .init(state: autostart == true ? .playing : .paused)

    return VideoBlock(
      widthTrait: width.makeLayoutTrait(with: resolver),
      heightTrait: height.makeLayoutTrait(with: resolver),
      model: model,
      state: state,
      playerFactory: playerFactory
    )
  }
}

extension DivVideoSource {
  public func makeVideo(resolver: ExpressionResolver) -> Video {
    let resolution: CGSize? = resolution.flatMap { resolution in
      CGSize(
        width: resolution.resolveWidth(resolver) ?? 0,
        height: resolution.resolveHeight(resolver) ?? 0
      )
    }
    return Video(
      url: resolveUrl(resolver)!,
      resolution: resolution,
      bitrate: resolveBitrate(resolver).flatMap { Double($0) },
      mimeType: resolveMimeType(resolver)
    )
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
