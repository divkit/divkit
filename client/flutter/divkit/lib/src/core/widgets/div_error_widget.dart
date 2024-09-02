import 'package:divkit/divkit.dart';

import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';

/// Configuration model.
class ShowUnsupportedDivs {
  final bool enabled;

  const ShowUnsupportedDivs(this.enabled);
}

class DivErrorWidget extends StatelessWidget {
  static const errorColor = Color(0xFFFF0000);

  final String? error;
  final DivBase data;

  const DivErrorWidget({
    required this.data,
    this.error,
    super.key,
  });

  @override
  Widget build(BuildContext context) =>
      watch<ShowUnsupportedDivs>(context)?.enabled ?? false
          ? DivBaseWidget(
              data: data,
              child: Placeholder(
                color: errorColor,
                child: Center(
                  child: Material(
                    type: MaterialType.transparency,
                    child: Text(
                      error ?? '',
                      style: const TextStyle(color: errorColor),
                    ),
                  ),
                ),
              ),
            )
          : const SizedBox.shrink();
}
