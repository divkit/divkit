import 'package:divkit/src/core/protocol/div_context.dart';
import 'package:divkit/src/core/protocol/div_custom.dart';
import 'package:divkit/src/generated_sources/div_custom.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:equatable/equatable.dart';
import 'package:flutter/material.dart';

class DivCustomModel with EquatableMixin {
  final Widget child;

  const DivCustomModel({
    required this.child,
  });

  static Stream<DivCustomModel> from(
    BuildContext context,
    DivCustom data,
  ) {
    final variables =
        DivKitProvider.watch<DivContext>(context)!.variableManager;
    final customResolver = DivKitProvider.watch<DivCustomHandler>(context)!;

    return variables.watch(
      (context) async {
        final type = data.customType;

        if (customResolver.isCustomTypeSupported(type)) {
          return DivCustomModel(child: customResolver.createCustom(data));
        }

        return const DivCustomModel(
          child: SizedBox(),
        );
      },
    );
  }

  @override
  List<Object?> get props => [child];
}
