import 'dart:math' as math;
import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:divkit/src/core/widgets/base/div_base_widget.dart';
import 'package:divkit/src/core/widgets/image/div_image_model.dart';
import 'package:divkit/src/generated_sources/div_image.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_svg/svg.dart';

class DivImageWidget extends StatefulWidget {
  final DivImage data;

  const DivImageWidget(
    this.data, {
    super.key,
  });

  @override
  State<DivImageWidget> createState() => _DivImageWidgetState();
}

class _DivImageWidgetState extends State<DivImageWidget> {
  // ToDo: Optimize repeated calculations on the same context
  Stream<DivImageModel>? stream;

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivImageModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivImageWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      stream = DivImageModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        action: widget.data.action,
        actions: widget.data.actions,
        child: StreamBuilder<DivImageModel>(
          stream: stream,
          builder: (context, snapshot) {
            if (snapshot.hasData) {
              final model = snapshot.requireData;

              final Widget image;
              if (model.src.startsWith('divkit-asset://')) {
                final src = model.src.replaceFirst('divkit-asset:/', 'assets');
                if (src.endsWith('.svg')) {
                  final blendMode = model.colorBlendMode;
                  final blendColor = model.color;
                  image = SvgPicture.asset(
                    src,
                    alignment: model.contentAlignment.resolve(
                      Directionality.maybeOf(context),
                    ),
                    fit: model.fit,
                    color: blendColor,
                    colorBlendMode: blendMode ?? BlendMode.srcIn,
                  );
                } else {
                  image = Image.asset(
                    src,
                    fit: model.fit,
                    color: model.color,
                    colorBlendMode: model.colorBlendMode,
                    alignment: model.contentAlignment.resolve(
                      Directionality.maybeOf(context),
                    ),
                  );
                }
              } else {
                if (model.src.endsWith('.svg')) {
                  final blendMode = model.colorBlendMode;
                  final blendColor = model.color;
                  image = SvgPicture.network(
                    model.src,
                    alignment: model.contentAlignment.resolve(
                      Directionality.maybeOf(context),
                    ),
                    fit: model.fit,
                    color: blendColor,
                    colorBlendMode: blendMode ?? BlendMode.srcIn,
                  );
                } else {
                  image = CachedNetworkImage(
                    imageUrl: model.src,
                    fit: model.fit,
                    color: model.color,
                    colorBlendMode: model.colorBlendMode,
                    alignment: model.contentAlignment.resolve(
                      Directionality.maybeOf(context),
                    ),
                    cacheManager: DivKitProvider.watch(context),
                  );
                }
              }

              final aspect = model.aspectRatio;

              final Widget blurredImage;
              final blurRadius = model.blurRadius;
              if (blurRadius != null) {
                blurredImage = ImageFiltered(
                  imageFilter: ImageFilter.blur(
                    sigmaX: blurRadius,
                    sigmaY: blurRadius,
                  ),
                  child: image,
                );
              } else {
                blurredImage = image;
              }

              final Widget mirroredImage;
              if (model.rtlMirror) {
                mirroredImage = Transform(
                  transform: Matrix4.rotationY(math.pi),
                  alignment: model.contentAlignment,
                  child: blurredImage,
                );
              } else {
                mirroredImage = blurredImage;
              }

              if (aspect != null) {
                return AspectRatio(
                  aspectRatio: aspect,
                  child: mirroredImage,
                );
              }

              return mirroredImage;
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    stream = null;
    super.dispose();
  }
}
