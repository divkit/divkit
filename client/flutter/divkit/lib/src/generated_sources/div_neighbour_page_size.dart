// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_fixed_size.dart';

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

  DivNeighbourPageSize copyWith({
    DivFixedSize? neighbourPageWidth,
  }) =>
      DivNeighbourPageSize(
        neighbourPageWidth: neighbourPageWidth ?? this.neighbourPageWidth,
      );

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
