import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/widgets.dart';

class DivCustomWidget extends StatelessWidget {
  final DivCustom data;

  const DivCustomWidget(
    this.data, {
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    final divContext = watch<DivContext>(context)!;

    final type = data.customType;
    final customResolver = divContext.customHandler;

    if (customResolver.isCustomTypeSupported(type)) {
      return DivBaseWidget(
        data: data,
        child: provide(
          DivParentData.none,
          child: customResolver.createCustom(data, divContext),
        ),
      );
    }

    return DivErrorWidget(
      data: data,
      error: "Custom $type not supported",
    );
  }
}
