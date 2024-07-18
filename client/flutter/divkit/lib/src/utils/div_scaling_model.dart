import 'package:equatable/equatable.dart';

/// Options for text and UI scaling
class DivScalingModel with EquatableMixin {
  final double viewScale;
  final double textScale;

  const DivScalingModel({
    required this.viewScale,
    required this.textScale,
  });

  @override
  List<Object?> get props => [
        viewScale,
        textScale,
      ];
}
