import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/widgets/container/div_container_widget.dart';
import 'package:divkit/src/core/widgets/custom/div_custom_widget.dart';
import 'package:divkit/src/core/widgets/div_error_widget.dart';
import 'package:divkit/src/core/widgets/gallery/div_gallery_widget.dart';
import 'package:divkit/src/core/widgets/image/div_image_widget.dart';
import 'package:divkit/src/core/widgets/input/div_input_widget.dart';
import 'package:divkit/src/core/widgets/state/div_state_widget.dart';
import 'package:divkit/src/core/widgets/text/div_text_widget.dart';
import 'package:divkit/src/generated_sources/div.dart';
import 'package:flutter/widgets.dart';

class DivWidget extends StatelessWidget {
  final Div? data;

  const DivWidget(
    this.data, {
    super.key,
  });

  @override
  Widget build(BuildContext context) =>
      data?.maybeMap(
        divState: (data) => DivStateWidget(data),
        divContainer: (data) => DivContainerWidget(data),
        divGallery: (data) => DivGalleryWidget(data),
        divText: (data) => DivTextWidget(data),
        divImage: (data) => DivImageWidget(data),
        divInput: (data) => DivInputWidget(data),
        divCustom: (data) => DivCustomWidget(data),
        orElse: () {
          logger.warning("Attempt to render an unsupported div!");
          return DivErrorWidget(
            data: data!.value,
            error: "Div not supported",
          );
        },
      ) ?? // Valid empty view.
      const SizedBox.shrink();
}
