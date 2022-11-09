public struct DivFlagsInfo {
  public let isTextSelectingEnabled: Bool
  public let appendVariablesEnabled: Bool
  public let metalImageRenderingEnabled: Bool

  public init(
    isTextSelectingEnabled: Bool,
    appendVariablesEnabled: Bool,
    metalImageRenderingEnabled: Bool
  ) {
    self.isTextSelectingEnabled = isTextSelectingEnabled
    self.appendVariablesEnabled = appendVariablesEnabled
    self.metalImageRenderingEnabled = metalImageRenderingEnabled
  }

  public static let `default` = DivFlagsInfo(
    isTextSelectingEnabled: false,
    appendVariablesEnabled: true,
    metalImageRenderingEnabled: false
  )
}
