# DivKit in iOS. Quick start.

## Build an MVP.
Code-wise, connecting `DivKit` consists of three parts: initializing `DivKitComponents`, parsing the `JSON` file with the layout, and creating `DivHostingView` or `DivView` and adding it to your `SwiftUI` or `UIKit` hierarchy respectively.

#### Creating DivKitComponents.
You should create `DivKitComponents` object and provide the necessary dependencies and handlers.

#### Creating DivViewSource.
You should create DivViewSource where you need to provide `DivKit` data. This can be done in the following ways: 
- raw `JSON` in `Dictionary` format 
- `JSON` in `Swift.Data` format
- `DivData` object

#### Creating DivKit View.
To create `DivView` (UIKit) or `DivHostingView` (SwiftUI), you need to provide a previously created `DivKitComponents` instance to the constructor. Subsequently, you should populate the `DivView` with the data to be displayed at the appropriate time in `UIKit` via `setSource` or provide `DivViewSource` to `DivHostingView` constructor in `SwiftUI`.

## Initialization Example:

#### UIKit
```Swift
  let divView = DivView(divKitComponents: divKitComponents)
  divView.setSource(
      DivBlockProvider.Source(
          kind: .json(json),
          cardId: cardId
      ),
      debugParams: debugParams,
      shouldResetPreviousCardData: true
  )
```

#### SwiftUI
```Swift
    DivHostingView(
      divkitComponents: DivKitComponents(
        divCustomBlockFactory: SampleDivCustomBlockFactory(),
        urlHandler: SampleDivActionHandler(
          isPresented: $isPresented,
          text: $text
        )
      ),
      source: DivViewSource(kind: .data(sampleData), cardId: "Sample"),
      debugParams: DebugParams(isDebugInfoEnabled: true)
    )
```

