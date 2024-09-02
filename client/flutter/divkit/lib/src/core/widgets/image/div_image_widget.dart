// Since we still support a wide range of flutter_svg versions, we cannot migrate to the new API yet.
// ignore_for_file: deprecated_member_use

import 'dart:math' as math;
import 'dart:ui';

import 'package:cached_network_image/cached_network_image.dart';
import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/widgets/image/div_image_model.dart';
import 'package:divkit/src/utils/provider.dart';
import 'package:flutter/material.dart';
import 'package:flutter_cache_manager/flutter_cache_manager.dart';
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
  DivImageModel? value;
  Stream<DivImageModel>? stream;

  @override
  void initState() {
    super.initState();
    value = DivImageModel.value(context, widget.data);
  }

  @override
  void didChangeDependencies() {
    super.didChangeDependencies();

    stream ??= DivImageModel.from(context, widget.data);
  }

  @override
  void didUpdateWidget(covariant DivImageWidget oldWidget) {
    super.didUpdateWidget(oldWidget);

    if (widget.data != oldWidget.data) {
      value = DivImageModel.value(context, widget.data);
      stream = DivImageModel.from(context, widget.data);
    }
  }

  @override
  Widget build(BuildContext context) => DivBaseWidget(
        data: widget.data,
        aspect: widget.data.aspect?.ratio,
        tapActionData: DivTapActionData(
          action: widget.data.action,
          actions: widget.data.actions,
          longtapActions: widget.data.longtapActions,
          actionAnimation: widget.data.actionAnimation,
        ),
        child: StreamBuilder<DivImageModel>(
          initialData: value,
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
                    cacheManager: watch<BaseCacheManager>(context),
                  );
                }
              }

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

              return mirroredImage;
            }

            return const SizedBox.shrink();
          },
        ),
      );

  @override
  void dispose() {
    value = null;
    stream = null;
    super.dispose();
  }
}
