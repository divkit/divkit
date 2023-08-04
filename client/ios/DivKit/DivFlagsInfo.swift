/// The `DivFlagsInfo` structure provides information about new features added under specific flags
/// within the `DivKit` framework.
///
/// These features may be experimental or in a testing phase and are not guaranteed to be free from
/// performance issues, crashes, or other problems. By default, only features that have testing are
/// included in the framework.
/// You can access the default `DivFlagsInfo` instance using the static property `default`.
public struct DivFlagsInfo {
  /// Creates an instance of `DivFlagsInfo`.
  public init() {}

  /// The default instance of `DivFlagsInfo`.
  public static let `default` = DivFlagsInfo()
}
