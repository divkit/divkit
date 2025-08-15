/// The `DivFlagsInfo` structure provides information about new features added under specific flags
/// within the `DivKit` framework.
///
/// These features may be experimental or in a testing phase and are not guaranteed to be free from
/// performance issues, crashes, or other problems. By default, only features that have testing are
/// included in the framework.
/// You can access the default `DivFlagsInfo` instance using the static property `default`.
public struct DivFlagsInfo {
  /// The default instance of `DivFlagsInfo`.
  public static let `default` = DivFlagsInfo()

  /// Defines the behavior of the visibility/disappear actions.
  ///
  /// `true` - visibility action URLs will be handled by `DivUrlHandler` the same way regular
  /// actions do.
  ///
  /// `false` - visibility action URLs will be ignored by `DivUrlHandler`.
  ///
  /// Default value is `false` for the backward compatibility reasons.
  public let useUrlHandlerForVisibilityActions: Bool

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

  /// Defines the default value for `DivText.autoEllipsize` property.
  /// Default value is `true`. It differs from other platforms where it is `false`.
  public let defaultTextAutoEllipsize: Bool

  /// Allows the usage of delayed trigger initialization in case the target div requires
  /// existence of nested objects (timers, tooltips, etc)
  ///
  /// `true` - `DivTriggerStorage.set` call initializes the triggers automatically,
  /// however this is **legacy** behaviour
  ///
  /// `false` - `DivTriggerStorage` requires you to call `set` and `initialize`
  /// methods before expecing it's correct functioning
  public let initializeTriggerOnSet: Bool

  /// Use cache for fonts adjusted by `font_feature_settings` property
  public let fontCacheEnabled: Bool

  /// Creates an instance of `DivFlagsInfo`.
  public init(
    useUrlHandlerForVisibilityActions: Bool = false,
    imageBlurPreferMetal: Bool = true,
    imageTintPreferMetal: Bool = true,
    useTooltipLegacyWidth: Bool = false,
    initializeTriggerOnSet: Bool = true,
    defaultTextAutoEllipsize: Bool = true,
    fontCacheEnabled: Bool = true
  ) {
    self.useUrlHandlerForVisibilityActions = useUrlHandlerForVisibilityActions
    self.imageBlurPreferMetal = imageBlurPreferMetal
    self.imageTintPreferMetal = imageTintPreferMetal
    self.useTooltipLegacyWidth = useTooltipLegacyWidth
    self.initializeTriggerOnSet = initializeTriggerOnSet
    self.defaultTextAutoEllipsize = defaultTextAutoEllipsize
    self.fontCacheEnabled = fontCacheEnabled
  }

}
