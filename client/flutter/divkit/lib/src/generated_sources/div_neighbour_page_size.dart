// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_fixed_size.dart';

class DivNeighbourPageSize with EquatableMixin {
  const DivNeighbourPageSize({
    required this.neighbourPageWidth,
  });

  static const type = "fixed";

  final DivFixedSize neighbourPageWidth;

  @override
  List<Object?> get props => [
        neighbourPageWidth,
      ];

  static DivNeighbourPageSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivNeighbourPageSize(
      neighbourPageWidth: safeParseObj(
        DivFixedSize.fromJson(json['neighbour_page_width']),
      )!,
    );
  }
}
