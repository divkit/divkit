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

  /// Experimental tint/blur effect renderer
  public let imageBlurPreferMetal: Bool
  public let imageTintPreferMetal: Bool

  /// Indicates if legacy (pre 31.0) behavior should be applied to measure the tooltip width.
  ///
  /// `true` - `match_parent` width is interpreted as `wrap_content`. No way to achieve
  /// `match_parent` behavior.
  ///
  /// `false` - tooltips with `match_parent` width (default value for `DivBase.width`) will be
  /// streched to the full width of the window.
  public let useTooltipLegacyWidth: Bool

  /// Creates an instance of `DivFlagsInfo`.
  public init(
    imageLoadingOptimizationEnabled: Bool = true,
    imageBlurPreferMetal: Bool = true,
    imageTintPreferMetal: Bool = true,
    useTooltipLegacyWidth: Bool = true
  ) {
    self.imageLoadingOptimizationEnabled = imageLoadingOptimizationEnabled
    self.imageBlurPreferMetal = imageBlurPreferMetal
    self.imageTintPreferMetal = imageTintPreferMetal
    self.useTooltipLegacyWidth = useTooltipLegacyWidth
  }

  /// The default instance of `DivFlagsInfo`.
  public static let `default` = DivFlagsInfo()
}
