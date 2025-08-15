import CoreMedia
import Foundation
import LayoutKit
import VGSL

extension DivVideo: DivBlockModeling {
  public func makeBlock(context: DivBlockModelingContext) throws -> Block {
    let context = modifiedContextParentPath(context)
    return try applyBaseProperties(
      to: { try makeBaseBlock(context: context) },
      context: context,
      actionsHolder: nil
    )
  }

  private func makeBaseBlock(context: DivBlockModelingContext) throws -> Block {
    guard let playerFactory = context.playerFactory else {
      DivKitLogger.error("There is no player factory in the context")
      return EmptyBlock()
    }

    let resumeActions = resumeActions?.uiActions(context: context) ?? []
    let pauseActions = pauseActions?.uiActions(context: context) ?? []
    let bufferingActions = bufferingActions?.uiActions(context: context) ?? []
    let endActions = endActions?.uiActions(context: context) ?? []
    let fatalActions = fatalActions?.uiActions(context: context) ?? []
    let resolver = context.expressionResolver
    let aspectRatio = aspect.resolveAspectRatio(resolver)
    let repeatable = resolveRepeatable(resolver)
    let muted = resolveMuted(resolver)
    let autostart = resolveAutostart(resolver)
    let elapsedTime: Binding<Int>? = elapsedTimeVariable.flatMap {
      context.makeBinding(variableName: $0, defaultValue: 0)
    }
    let preview: Image? = resolvePreview(resolver).flatMap(_makeImage(base64:))
    let videoData = VideoData(videos: videoSources.compactMap { $0.makeVideo(resolver: resolver) })

    let playbackConfig = PlaybackConfig(
      autoPlay: autostart,
      repeatable: repeatable,
      isMuted: muted,
      startPosition: elapsedTime.flatMap { CMTime(value: $0.value) } ?? .zero,
      settingsPayload: playerSettingsPayload ?? [:]
    )

    let state: VideoBlockViewState = context.blockStateStorage
      .getState(context.path) ?? .init(state: autostart == true ? .playing : .paused)

    let model = VideoBlockViewModel(
      videoData: videoData,
      playbackConfig: playbackConfig,
      preview: preview,
      elapsedTime: elapsedTime,
      resumeActions: resumeActions,
      pauseActions: pauseActions,
      bufferingActions: bufferingActions,
      endActions: endActions,
      fatalActions: fatalActions,
      path: context.path,
      scale: resolveScale(resolver).scale
    )

    let videoBlock = VideoBlock(
      widthTrait: width.resolveLayoutTrait(resolver),
      heightTrait: height.resolveHeightLayoutTrait(resolver, aspectRatio: aspectRatio),
      model: model,
      state: state,
      playerFactory: playerFactory
    )

    if let aspectRatio {
      return AspectBlock(content: videoBlock, aspectRatio: aspectRatio)
    }

    return videoBlock
  }
}

extension DivVideoSource {
  public func makeVideo(resolver: ExpressionResolver) -> Video? {
    let resolution: CGSize? = resolution.flatMap { resolution in
      CGSize(
        width: resolution.resolveWidth(resolver) ?? 0,
        height: resolution.resolveHeight(resolver) ?? 0
      )
    }
    guard let url = resolveUrl(resolver) else {
      return nil
    }
    return Video(
      url: url,
      resolution: resolution,
      bitrate: resolveBitrate(resolver).flatMap { Double($0) },
      mimeType: resolveMimeType(resolver)
    )
  }
}

extension DivVideoScale {
  fileprivate var scale: VideoScale {
    switch self {
    case .fill:
      .fill
    case .noScale:
      .noScale
    case .fit:
      .fit
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
