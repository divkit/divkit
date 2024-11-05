import 'package:equatable/equatable.dart';

/// Options for text and UI scaling
class DivScale with EquatableMixin {
  final double view;
  final double text;

  const DivScale({
    this.view = 1.0,
    this.text = 1.0,
  });

  @override
  List<Object?> get props => [view, text];
}
