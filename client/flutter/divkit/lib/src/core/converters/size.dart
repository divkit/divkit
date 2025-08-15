import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/converters/converters.dart';

extension DivSizeConverter on DivSize {
  DivSizeValue resolve(
    DivVariableContext context, {
    required double viewScale,
  }) =>
      map(
        divFixedSize: (fixed) => DivFixed(
          fixed.resolve(context, viewScale: viewScale),
        ),
        divMatchParentSize: (flex) => DivMatchParent(
          flex.weight?.resolve(context).toInt(),
        ),
        divWrapContentSize: (wrapped) {
          final constrained = wrapped.constrained?.resolve(context) ?? false;

          if (!constrained) {
            return DivWrapContent(
              min: wrapped.minSize?.resolve(context, viewScale: viewScale),
              max: wrapped.maxSize?.resolve(context, viewScale: viewScale),
            );
          }

          return DivConstrained(
            min: wrapped.minSize?.resolve(context, viewScale: viewScale),
            max: wrapped.maxSize?.resolve(context, viewScale: viewScale),
          );
        },
      );
}
