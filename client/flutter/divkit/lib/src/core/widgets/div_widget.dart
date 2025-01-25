import 'package:divkit/divkit.dart';
import 'package:divkit/src/utils/trace.dart';
import 'package:flutter/widgets.dart';

class DivWidget extends StatelessWidget {
  final Div? data;

  const DivWidget(
    this.data, {
    super.key,
  });

  @override
  Widget build(BuildContext context) => traceFunc(
        'Div.build',
        () =>
            data?.maybeMap(
              divState: (data) => DivStateWidget(data),
              divContainer: (data) => DivContainerWidget(data),
              divGallery: (data) => DivGalleryWidget(data),
              divText: (data) => DivTextWidget(data),
              divImage: (data) => DivImageWidget(data),
              divInput: (data) => DivInputWidget(data),
              divCustom: (data) => DivCustomWidget(data),
              divPager: (data) => DivPagerWidget(data),
              orElse: () {
                logger.warning("Attempt to render an unsupported div!");
                return DivErrorWidget(
                  data: data!.value,
                  error: "Div not supported",
                );
              },
            ) ?? // Valid empty view.
            const SizedBox.shrink(),
      );
}
