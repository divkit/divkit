#!/bin/bash

if [[ $# -eq 0 ]] ; then
    echo "Supply path to browser repository"
    exit 1
fi

rm Base/*.*
cp $1/src/base/ios/yandex/algorithms/Combine.swift Base
cp $1/src/base/ios/yandex/algorithms/Functions.swift Base
cp $1/src/base/ios/yandex/algorithms/WeakCollection.swift Base
cp $1/src/base/ios/yandex/concurrency/GCD.swift Base
cp $1/src/base/ios/yandex/concurrency/operations/OperationQueue.swift Base
cp $1/src/base/ios/yandex/extensions/ArrayExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/ComparableExtension.swift Base
cp $1/src/base/ios/yandex/extensions/DictionaryExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/EncodableExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/HTTPURLResponse+Extensions.swift Base
cp $1/src/base/ios/yandex/extensions/IndexSetExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/NSTimerExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/NSURLExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/OptionalExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/Range+Extensions.swift Base
cp $1/src/base/ios/yandex/extensions/RangeReplaceableCollectionExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/SequenceExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/Signal+CombineLatest.swift Base
cp $1/src/base/ios/yandex/extensions/String+Extensions.swift Base
cp $1/src/base/ios/yandex/extensions/ThreadExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/TupleExtensions.swift Base
cp $1/src/base/ios/yandex/extensions/UnsignedIntegerExtenstions.swift Base
cp $1/src/base/ios/yandex/networking/URLResourceRequesting.swift Base
cp $1/src/base/ios/yandex/nonempty/NonEmpty+Array.swift Base
cp $1/src/base/ios/yandex/nonempty/NonEmpty+Set.swift Base
cp $1/src/base/ios/yandex/nonempty/NonEmpty+String.swift Base
cp $1/src/base/ios/yandex/nonempty/NonEmpty.swift Base
cp $1/src/base/ios/yandex/reactive/disposable/AutodisposePool.swift Base
cp $1/src/base/ios/yandex/reactive/disposable/Disposable.swift Base
cp $1/src/base/ios/yandex/reactive/observing/ObservableProperty.swift Base
cp $1/src/base/ios/yandex/reactive/observing/ObservableVariable.swift Base
cp $1/src/base/ios/yandex/reactive/observing/Observer.swift Base
cp $1/src/base/ios/yandex/reactive/promises/Either.swift Base
cp $1/src/base/ios/yandex/reactive/promises/Future.swift Base
cp $1/src/base/ios/yandex/reactive/promises/Promise.swift Base
cp $1/src/base/ios/yandex/reactive/Result.swift Base
cp $1/src/base/ios/yandex/reactive/Cancellable.swift Base
cp $1/src/base/ios/yandex/reactive/DeferredValue.swift Base
cp $1/src/base/ios/yandex/reactive/Lazy.swift Base
cp $1/src/base/ios/yandex/reactive/Property.swift Base
cp $1/src/base/ios/yandex/reactive/Signal.swift Base
cp $1/src/base/ios/yandex/reactive/SignalPipe.swift Base
cp $1/src/base/ios/yandex/reactive/Variable.swift Base
cp $1/src/base/ios/yandex/storage/KeyValueDirectStoringSupporting.swift Base
cp $1/src/base/ios/yandex/storage/KeyValueStorage.swift Base
cp $1/src/base/ios/yandex/storage/SettingProperty.swift Base
cp $1/src/base/ios/yandex/storage/SingleValueCoder.swift Base
cp $1/src/base/ios/yandex/thick_ui/graphics/InternalImageDescriptor.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/color/ColorExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/color/RGBAColorExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/gradient/Gradient.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/gradient/LinearGradientView+Layer.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/gradient/RadialGradientView.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/image/ColorHolder.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/image/ImageHolder.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/image/ImagePlaceholder.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/image/NilImageHolder.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/relative/RelativePoint.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/relative/RelativeRect.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/relative/RelativeSize.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/relative/RelativeValue.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/scroll/ScrollDelegate.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/scroll/ScrollEdge.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/scroll/ScrollViewTrackable.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/scroll/ScrollViewType.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/scroll/ScrollingToTop.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/style/KeyboardAppearance.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/style/StatusBarStyle.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/Alignment.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/AnchoredPoint.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/ImageContentMode.swift Base
cp $1/src/base/ios/yandex/thick_ui/types/PhysicalFeedbackGenerator.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CALayerExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CAMediaTimingFunction.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CATransaction+Extensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CGColorExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CGFloatExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CGPointExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CGRectExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/CGSizeExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/DimensionsFlipping.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/EdgeInsets+Extensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/EdgeInsetsExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/Image+Crop.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/ImageExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/UIScrollView+Extensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/uikit/UIViewExtensions.swift Base
cp $1/src/base/ios/yandex/thick_ui/iOS.swift Base
cp $1/src/base/ios/yandex/BaseExports.swift Base
cp $1/src/base/ios/yandex/InvalidArgumentError.swift Base
cp $1/src/base/ios/yandex/RWLock.swift Base
cp $1/src/base/ios/yandex/Time.swift Base
cp $1/src/base/ios/yandex/UrlOpener.swift Base
cp $1/src/base/ios/yandex/weakify.swift Base

rm BaseTiny/*.*
cp $1/src/base/ios/yandex/tiny/base/*.swift BaseTiny
cp $1/src/base/ios/yandex/tiny/base/types/*.swift BaseTiny
cp $1/src/base/ios/yandex/tiny/extensions/*.swift BaseTiny
cp $1/src/base/ios/yandex/tiny/storage/*.swift BaseTiny
cp $1/src/base/ios/yandex/tiny/*.swift BaseTiny
rm BaseTiny/*Tests.swift
rm BaseTiny/macOS.swift

rm BaseUI/*.*
cp $1/src/base/ios/yandex/ui/alert/*.swift BaseUI
cp $1/src/base/ios/yandex/ui/components/Label.swift BaseUI
cp $1/src/base/ios/yandex/ui/extensions/*.swift BaseUI
cp $1/src/base/ios/yandex/ui/types/*.swift BaseUI
cp $1/src/base/ios/yandex/ui/typo/*.swift BaseUI
cp $1/src/base/ios/yandex/ui/*.swift BaseUI
rm BaseUI/UIAlertControllerExtensions.swift
rm BaseUI/UIViewControllerExtensions.swift

rm CommonCore/*.*
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/Completions/CompletionAccumulating.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/Completions/CompletionAccumulator.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/ImageContaining.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/ImageLayerLayout.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/ObjectsReusability.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/RemoteImageView.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/Resetting.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/Theme.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/UIStyles.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Base/VisibleBoundsTracking.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/CommonUI/Navigation/ViewProtocol.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/CommonUI/CompoundScrollDelegate.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/CommonUI/ExclusiveTouchCollectionView.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/CommonUI/ScrollableContent.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Extensions/NSAttributedStringExtensions.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLEntities.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLEntitiesResolver.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLParser.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLParserDelegate.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLTag.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/NativeHTMLParser/HTMLTaggedString.swift CommonCore
cp $1/src/yandex/ios/search_app/CommonCore/CommonCore/Exported.swift CommonCore

rm Networking/*.*
cp $1/src/yandex/ios/search_app/CommonCore/Networking/ActiveRequestsTracker.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/AsyncResourceRequester.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/AuthChallengeHandler.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/ChallengeHandling.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/DataExtensions.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/ImageHolderFactory.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/iOS.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/HTTPCode.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/HTTPErrors.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/HTTPHeaders.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/LocalImageProviding.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/NetworkActivityIndicatorController.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/NetworkSessionMetrics.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/NetworkURLResourceRequester.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/NSErrorExtensions.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/RemoteImageHolder.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/Resource.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/TimeTracking.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/URLRequestPerformer.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/URLRequestPerforming.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/URLSessionDelegateImpl.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/URLSessionTaskMetricsExtensions.swift Networking
cp $1/src/yandex/ios/search_app/CommonCore/Networking/URLTransform.swift Networking
