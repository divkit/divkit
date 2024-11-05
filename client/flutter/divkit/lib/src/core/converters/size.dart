import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/converters.dart';

extension DivSizeConverter on DivSize {
  DivSizeValue convert({
    required double viewScale,
  }) =>
      map(
        divFixedSize: (fixed) => DivFixed(
          fixed.convert(viewScale: viewScale),
        ),
        divMatchParentSize: (flex) => DivMatchParent(
          flex.weight?.value.toInt(),
        ),
        divWrapContentSize: (wrapped) {
          final constrained = wrapped.constrained?.value ?? false;

          if (!constrained) {
            return DivWrapContent(
              min: wrapped.minSize?.convert(viewScale: viewScale),
              max: wrapped.maxSize?.convert(viewScale: viewScale),
            );
          }

          return DivConstrained(
            min: wrapped.minSize?.convert(viewScale: viewScale),
            max: wrapped.maxSize?.convert(viewScale: viewScale),
          );
        },
      );
}
