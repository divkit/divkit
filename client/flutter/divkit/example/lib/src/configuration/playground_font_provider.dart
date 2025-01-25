import 'package:divkit/divkit.dart';

class PlaygroundDivFontProvider extends DivFontProvider {
  @override
  DivFontAsset resolve(String? fontFamily) {
    switch (fontFamily) {
      // The names don't have to match!
      case "custom-font-family":
        return const DivFontAsset(
          // package: There could be a package where the font is located.
          fontFamily: 'ProtestGuerrilla',
        );
    }

    // If we want other fonts to be able to resolve only by [fontFamily].
    return DivFontAsset(fontFamily: fontFamily);
  }
}
