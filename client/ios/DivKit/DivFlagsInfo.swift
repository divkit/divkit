/// The `DivFlagsInfo` structure provides information about new features added under specific flags
/// within the `DivKit` framework.
///
/// These features may be experimental or in a testing phase and are not guaranteed to be free from
/// performance issues, crashes, or other problems. By default, only features that have testing are
/// included in the framework.
/// You can access the default `DivFlagsInfo` instance using the static property `default`.
public struct DivFlagsInfo {
  /// Enable experimental image loading optimization
  public let imageLoadingOptimizationEnabled: Bool

  /// Creates an instance of `DivFlagsInfo`.
  public init(
    imageLoadingOptimizationEnabled: Bool = true
  ) {
    self.imageLoadingOptimizationEnabled = imageLoadingOptimizationEnabled
  }

  /// The default instance of `DivFlagsInfo`.
  public static let `default` = DivFlagsInfo()
}
