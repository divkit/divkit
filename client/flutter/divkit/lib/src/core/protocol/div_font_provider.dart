/// A basic structure that helps the platform resolve
/// a short font family declaration into a specific resource.
class DivFontAsset {
  /// Prefix for resource in some package different from the target application.
  final String? package;

  /// The name of the font to use when painting the text (e.g., Roboto).
  ///
  /// If the font is defined in a package, this will be prefixed with
  /// 'packages/package_name/' (e.g. 'packages/cool_fonts/Roboto'). The
  /// prefixing is done by the constructor when the `package` argument is
  /// provided.
  ///
  /// The value provided in [fontFamily] will act as the preferred/first font
  /// family that glyphs are looked for in, followed in order by the font families
  /// in [fontFamilyFallback]. When [fontFamily] is null or not provided, the
  /// first value in [fontFamilyFallback] acts as the preferred/first font
  /// family. When neither is provided, then the default platform font will
  /// be used.
  final String? fontFamily;

  /// The ordered list of font families to fall back on when a glyph cannot be
  /// found in a higher priority font family.
  final List<String>? fontFamilyFallback;

  const DivFontAsset({
    this.package,
    this.fontFamily,
    this.fontFamilyFallback,
  });
}

/// Handles specific font family. Creates new base TextStyle.
abstract class DivFontProvider {
  const DivFontProvider();

  /// Returns the `DivFontAsset` corresponding to the text style.
  DivFontAsset resolve(String? fontFamily);

  /// Direct mapping fontFamily to DivFontAsset(fontFamily),
  /// suitable for cases where resources are in the main application.
  factory DivFontProvider.direct() => _DefaultDivFontProvider();
}

class _DefaultDivFontProvider implements DivFontProvider {
  @override
  DivFontAsset resolve(String? fontFamily) =>
      DivFontAsset(fontFamily: fontFamily);
}
