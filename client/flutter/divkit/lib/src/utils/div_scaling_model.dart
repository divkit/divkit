import 'package:equatable/equatable.dart';

/// Options for text and UI scaling
class DivScalingModel with EquatableMixin {
  final double viewScale;
  final double textScale;

  const DivScalingModel({
    this.viewScale = 1.0,
    this.textScale = 1.0,
  });

  @override
  List<Object?> get props => [
        viewScale,
        textScale,
      ];
}
