import BaseTinyPublic

public struct VideoBlockViewModel: Equatable {
  public let videoData: VideoData
  public let playbackConfig: PlaybackConfig
  public var elapsedTime: Binding<Int>?
  public let preview: Image?
  public let resumeActions: [UserInterfaceAction]
  public let pauseActions: [UserInterfaceAction]
  public let bufferingActions: [UserInterfaceAction]
  public let endActions: [UserInterfaceAction]
  public let fatalActions: [UserInterfaceAction]
  public let path: UIElementPath
  public let scale: VideoScale

  public init(
    videoData: VideoData,
    playbackConfig: PlaybackConfig,
    preview: Image? = nil,
    elapsedTime: Binding<Int>? = nil,
    resumeActions: [UserInterfaceAction] = [],
    pauseActions: [UserInterfaceAction] = [],
    bufferingActions: [UserInterfaceAction] = [],
    endActions: [UserInterfaceAction] = [],
    fatalActions: [UserInterfaceAction] = [],
    path: UIElementPath,
    scale: VideoScale = .fit
  ) {
    self.videoData = videoData
    self.playbackConfig = playbackConfig
    self.preview = preview
    self.elapsedTime = elapsedTime
    self.resumeActions = resumeActions
    self.pauseActions = pauseActions
    self.bufferingActions = bufferingActions
    self.endActions = endActions
    self.fatalActions = fatalActions
    self.path = path
    self.scale = scale
  }

  public static func ==(lhs: VideoBlockViewModel, rhs: VideoBlockViewModel) -> Bool {
    lhs.videoData == rhs.videoData
  }
}
