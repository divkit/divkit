## 0.4.0

* The mode configuration parameter has been removed! Instead, there are now customActions
* General stability improvements
* The work with the state has been redesigned, now you can open several editors in parallel
* Redesigned work with component properties: now all properties are treated the same, template fields are taken into account
* Redesigned processing of non-rendered components
* A noticeably larger number of properties now have the ability to set calculated expressions
* Calculated expressions should now be filled in more conveniently and applied by Enter
* Fixed the operation of the "read-only" mode in some situations
* Visual improvements and fixes
* Panels will now restore their dimensions by double-clicking on the divider
* Fixed reloading of used translations after changing json
* Fixed redrawing of previews after changing timers
* Fixed preview redrawing after removing colors in the palette
* Improved integration of several component property changes, should now work in a more expected way
* Fixed the display of platform warnings if the property was not supported in the flutter
* When you change the alignment of the selected component, the margins on the other axis are no longer reset
* Removed the possibility of alignment along the main axis of the container and gallery
* Improved the change of the number of dots in the gradient background
* Fixed highlighting of a component in the component tree when selecting if the component was not rendered
* Fixed the display of rotation greater than 180 degrees
* Fixed the ability to insert into a template that has child elements specified inside the template
* Fixed grid columns/rows display in some cases
* Fixed the scrolling in the component tree when creating a new component
* Removed the creation of an additional child element when creating an element with the "custom" type

## 0.3.9

* Added a new `timers` panel with timer settings
* Added support for new template properties (in particular, the fields for `url` in actions will now work)
* Fixed color editing in the background list
* Fixed editing of numeric fields
* Improved the display of the selection of rotated elements (including those with nested rotations)
* Improved editing of `grid` grids in the presence of rotations
* Added the ability to stretch grid elements lower than the border of the grid itself
* Fixed resizing of grid elements that is not a root element
* Fixed resizing of elements that are a child of the root element
* Now, when changing the column stretching of the grid element, `column_span` / `row_span` will be deleted from the data if the total value is 1
* Added highlighting of the guides in the grid when hovering

## 0.3.8

* Added highlighting of rows and columns in grids, dragging of child elements in grids, resize of child elements, as well as resize of columns and rows of the grids themselves
* Fixed an error when deleting the value of a numeric field
* Fixed crash with some text json changes
* Fixed a bug when using `actionLogUrlVariable`
* Fixed the ability to change pre-configured templates
* Minor stylistic edits

## 0.3.7

* Minor text edits
* Fixed the appearance of the "Add component" menu for predefined templates
* The fields with the color name in the palette have been converted to single-line ones due to problems with unnecessary line breaks in the name
* Removed an empty array with variables when using card colors in the palette
* Fixed reading colors for the palette
* Fixed duplicate colors in the palette when using conversion

## 0.3.5

* Fixed the display of the distance to the previously hidden selected element (and the distance went to the upper left corner)
* Fixed dragging elements inside some templates
* The logic of margin removal in some types of drags has been changed
* Updated visual highlighting of elements during dragging
* Fixed a bug with dragging a single element inside a container element, which led to the destruction of the parent element and further falling
* The "Add component" item in the component tree for some of the custom templates has been removed
* Added custom templates to the "Add Component" item in the component tree
* Fixed the operation of the minimum value "0" in numeric fields
* Added display of numeric field values calculated from expressions
* Added rounding of calculated width/height values in component properties
* Increased the size and scrolling of the search bar in the code panel
* Added methods for renaming templates in the card

## 0.3.4

* Fixed a problem with the loss of tanker keys when reopening in some cases
* Fixed the binding of errors shooting inside templates
* Added basic grid support, as well as column_span/row_span mapping for their child elements
* Fixed the logic of duplicating elements in some cases
* Improved parsing of template fields that use the properties of their scheme with "anyOf"
* Palette conversion functions now try to save the name of colors from the palette and make them more unique

## 0.3.3

* Added the display of custom templates in the "new component" panel
* Added support for template fields with the "floating point number" type
* Added support for restrictions in numeric template fields from the schema
* Fixed deletion of the "log_url" field when deleting a field from an action
* Fixed the value of "normal" text font weight (according to the scheme it is regular, but normal was specified)

## 0.3.2

* Removed browser selection of objects in previews (images, for example)
* Fixed simultaneous display of preset template properties (buttons) and expanded ones
* Object highlighting in json is back
* The palette is now updated when the json changes

## 0.3.1

* Fixed an issue when reopening the editor
* Fixed the "list item" template
* Fixed sticking styles in the text (including colors)
* Minor interface fixes
* Improved parsing of template array fields

# 0.3.0

* Dozens of small fixes and interface improvements
* The error panel is back
* The json panel now shows the full json with variables and templates
* Instead of showing only divjson created in the editor, the editor now knows (tries) to show arbitrary divjson and supports arbitrary templates

## 0.2.26

* Fixed a breakdown when variables were not passed to the editor and tanker keys were passed

## 0.2.25

* Fixed the initial display of tanker keys

## 0.2.24

* Fixed a serious problem with not updating the properties panel

## 0.2.23

* Fixed a problem with non-working property changes after moving and changing properties (and in some other cases)
* Fixed incorrect text color when editing "in place" in the FireFox in some cases
* Fixed an error when trying to delete the last element in the tree (besides the root one)

## 0.2.22

* Updated DivKit from fork 28.3.0 to 29.0.0 (stable, not fork). It is taken out separately, since a bunch of problems are possible so that you can easily roll back

## 0.2.21

* Added the ability to edit the root element (enabled by the rootConfigurable flag)
* Added scrollbar highlighting when hovering over the scrollbar area
* Slightly updated design of the preview
* Fixed mismatch between word wrapping in normal display and double-click editing
* Fixed a rare error when finishing editing by double-clicking
* Fixed crashes when trying to change the position and size of the root element (and without the rootConfigurable flag)
* Fixed crashes when trying to delete the root element in the component tree
* Fixed a drop when moving an element before or after the root element in the tree (and improved insertion)
* Added a Backspace/Delete shortcut to clear the "alignment" value from the keyboard
* Empty arrays are now deleted if the last element of them is deleted
* Fixed the signature in "complex properties" if the property is not supported by any platform
* An empty array of variables has been removed from the card if the variables are not needed
* Fixed a crash in the "complex properties" of the container (due to item_builder, it has been supported somehow so far, it is unclear whether it is still worth editing)

## 0.2.20

* Fixed another bug with an error when adding an item to the list (backgrounds, actions, and so on)
* Fixed the types of actions "next fullscreen" and "open fullscreen", finished editing
* The size distribution in "complex properties" has been changed
* The label has been removed from the "actions" property (the excess was highlighted when hovering)
* Added video properties
* Changed the cross pattern, added preload_required
* Fixed setting the properties of a component that did not display due to an error

## 0.2.19

* Added the ability to resize previews
* Fixed the display of the palette and color selection in the read-only mode
* Fixed the display of the alignment property in read-only mode
* Fixed a lost animation when hovering the "upload" image button in the preview
* The distance between the elements is now drawn above other symbols

## 0.2.18

* Fixed an issue where tanker keys restored without meta were not shown in the tanker panel

## 0.2.17

* Fixed an animation in the panel with variables that prevented switching panels (at the time of switching the panel was cracked)
* The editor has learned to live without meta data about translations (if you have not passed the meta, the editor will request translations via api on his own)

## 0.2.16

* Fixed a drop in text field editing if the variable is not a string
* Fixed "not applying" colors added to an empty palette
* Tanker support has been expanded, now it can go to "folders"
* Added a new panel with variables
* The Editor has learned not to lose the variables that are passed to it

## 0.2.15

* Fixed root log_id reset
* Fixed a crash in the absence of external sources
* Fixed adding an empty palette {} if there is no palette

## 0.2.14

* Fixed id generation for new colors in the palette
* Fixed changing the type of action in the dialog
* Fixed the leak of the __key service field in json
* Added properties for the gallery

## 0.2.13

* Fixed changing the text with the expression after exiting the text editor in place
* Fixed loss of focus in the expression editing dialog
* Added restrictions on preview size: warning (has a value of 10000 bytes by default) and error (it doesn't matter by default). Both can be set in the config
* Added a pop-up about the fact that the text from the tanker or with the expression cannot be edited

## 0.2.12

* Added a background in the preview for transparent elements
* Redone text field editing
* English localization edits
* Fixed the arrows in the gallery (again)

## 0.2.11

* Updated the DivKit again. In the previous one, content updates in containers, grids, galleries and pagers were broken due to support for div patches
* Added log_url in action settings
* Added the ability to upload previews for images
* Fixed the position of dialogs in the presence of horizontal scrolling
* Fixed the width of the field when entering the tanker key
* As a temporary solution, I began to show the unsolicited properties of the component if, as a result, the component could not be drawn
* Corrected the text input in the text fields (not very nice, but clearly better than it was)
* Fixed the loss of the last entered tanker key in some situations
* New typed actions in the scheme are supported

## 0.2.10

* Added support for external sources, a new panel
* So far only in the text field. Also, the field now outputs the calculated value
* Divkit has been updated for the first time in a long time

## 0.2.9

* Changed the color of the selected component in the component tree
* Improved translations, added a new panel, touched the selection of elements and text editing in place

## 0.2.8

* Added very early tanker support

## 0.2.7

* All palette operations now support undo/redo

## 0.2.6

* Added a function for the reverse conversion of the palette, and renamed the old one

## 0.2.5

* Added a feature to convert a palette from dict to palette

## 0.2.4

* Redone the capture of keyboard shortcuts + interaction with the rest of the page
* Added checking for duplicate names in the palette (and empty names)
* Added a new component rotation display
* Removed the ability to select DivKit components in the preview from the keyboard
* The "upload image" button in the preview no longer rotates along with the component itself

## 0.2.3

* Added a new preview for colors in all places where colors are displayed, now with transparency support, plus it should become more uniform
* Fixed errors when interacting with disabled context menu items
* Fixed a bug with not adding an item to the list in some situations (backgrounds, palette, and others)
* Fixed deletion of array elements in "complex properties"
* The color in the "complex properties" is now displayed in text (so that you can work with palettes and do many more things)
* Improved the display of "complex properties", now the values should take up more space
* Fixed a situation where the usual component properties are shown, which use a deleted color from the palette (and an error is shown)
* Fixed an error about the selection going out of bounds when the json editor is open + a few more conditions

## 0.2.2

* Added a new "pp" editor mode, which shows other actions (enabled separately)
* Added support for palettes (redesigned the old one) (included separately)
* Fixed the text editor getting stuck by double-clicking when resizing the window
* Fixed incorrect operation of some properties with undo/redo after editing the text in place
* Added display of the component size when resizing it with the mouse
* The reset of component alignment has been removed in some cases of their reuse
* Added the ability to drag and drop dialogs with the mouse (most of them)
* Fixed the "upload image" button in Russian on some widths of the selected component

## 0.2.1

* Added the display of match_parent dimensions in the properties panel
* Added the distance between the selected element and the hovered one
* Fixed the display of text properties with focus and mouse over them (the blue border disappeared)

# 0.2.0

* All actions in the editor have been redone: changing properties, adding, deleting, moving components, and so on
* Actions can now be combined - text changes will no longer be canceled by one letter
* When canceling/returning operations, the selection is saved
* The cancel/return buttons now say exactly what they cancel/return

Other edits:
* Duplicating a container in the tree now clones it next to it, rather than into itself
* Inserting a snapshot into the preview now inserts a component from the clipboard, rather than a duplicate of the current one
* Removed the arrows in the margins in the FireFox
* Fixed the size of the input field near the degrees of rotation in the FireFox

## 0.1.24

* Added counteraction to the browser menu when selecting text
* Fixed unsubscribing the value when deleting the editor

## 0.1.23

* Removed the fillets from scrollbars
* Changed the text "color" to "text color" in the properties
* Added keyboard support to the spectrum when choosing a color, now you can move the color with the arrows. And I also corrected the circumcision of the circle if they were close to the edge of the dialogue
* I've changed the logic of selecting an element, now components that are larger than the parent should stand out better
* Supported the dimensions of match_parent in the alignment property, now instead of 9 squares it can draw 3 rectangles or even 1. Also updated the design of the preset value on the right side
* Fixed horizontal alignment in the margins (some elements might not be centered)
* Added the "line height" property to the button
* Fixed fractional margins when dragging and dropping a new component in the preview

## 0.1.22

* Corrected the translation of "font weight" when editing the text
* Corrected the removal and insertion of components in read-only mode
* Changed the text color and background when creating the button

## 0.1.21

* Changed the action editor for the "List item"
* Cleaned outdated dialogs and properties (there is still something to clean)
* Fixed tooltips with the incorrect position
* Corrected the cropping of controls during selection (blue frame) if the component is located at the top of the preview

## 0.1.20

* Corrected the display of tooltips to align the text during editing under the text (by cutting out the custom tooltip)
* Renamed "font weight" in russian locale
* Enabled text centering in the button template
* Fine-grained improvement of the appearance of complex properties

## 0.1.19

* Made some kind of test edit on popups

## 0.1.18

* Made minification for files. The names of the dependencies have changed, but this should not affect anything.

## 0.1.17

* Returned the lost "list item" component to the panel with the addition of the component
* Fixed the adjacent text in long titles in the add component panel
* Tweaked the logic of selecting components with the mouse in the preview
* Corrected the error at the time of deleting the component and the hover of a piece of code in the json editor
* Fixed text sizes when editing with wrap_content and wrap_content+constrained sizes
* Redesigned the display of text editing + all property dialogs, now it should not be possible to interact with the page while they are open + fixed the order of closing dialogs if several are open at the same time
* Replaced the action editor in the button (I forgot to change it in the previous version)

## 0.1.16

* Updated the design of adding a new component, component tree, component tree menu, color properties, check box properties (constrained), json editor in a dark theme
* Removed the button with errors, as it is very bad
* Added new properties for text: alignment and underline/strikethrough
* Added alignment to the inline text editor, and also fixed the underline/strikethrough operation that affects the entire block of text
* Added a new property with actions

## 0.1.14

* The toolbar no longer rotates with the text itself when editing the text
* When dragging, the nested container is now given higher priority than the external one. And fixed interaction with horizontal containers
* Fixed an incorrect icon in the component tree that was "delayed" from changing properties (container and gallery)
* Now, when creating components, as well as when dragging them into the preview, all components try to place themselves at the end of the list, but before the cross, thus the cross should be on top on average. It won't work if the cross is no longer the last component. It won't work if you manually moved it to the last place in the tree
* Unified component creation in all three ways. For example, when creating through the menu in the tree, components are now created in the middle, and when dragging they get the same wrap_content+constrained size as with other creation methods
* New double-click text editing logic. Now it takes into account the set size (fixed/match_parent is one way, wrap_content is the second, wrap_content+constricted is the third), and tries to mimic the real component - it changes sizes, has a background. It should get better on average, but I admit that in some case I broke everything

## 0.1.13

* Added read-only mode. You do not need to change the layout - the editor removes the panel itself with the addition of the component. All dialogues were intentionally left out! So that you can view urls, gradient properties, links, and so on. Even double-click text editing was left
* Replaced the image selection dialog in the background dialog with a new one
* Set defaults for the background of the picture for transparency and image size
* Enabled the button to replace the picture/lottie in the preview always, and not only when the value is empty
* Enabled editing of actions for the cross

## 0.1.12

* Redesigned display of hover paddings and margins in the properties panel
* Fixed the addition of a component with negative (invalid) margins during drag and drop
* I hope I fixed the position of the tooltips
* Fixed the redrawing of the text (there was an incorrect text size in some cases)
* Turned off the logic of tuning margins to the old values when changing the size to match_parent, so that it was visually more obvious what was happening
* I hope I fixed the z order of the panels
* Removed the extra arrow in the properties panel in Safari

## 0.1.11

* Fixed editor error when adding container
* The arrows in the gallery are now clickable, and the gallery itself can be scrolled (for example, with a touchpad). And the components update their frames correctly when scrolling the gallery
* Drag and drop to the gallery is supported
* The container and gallery got a new layout when added (the size is larger, it is more convenient to interact)
* Fixed a number of problems with redrawing previews (by the type of incorrect margins)
* Updated component properties: image, gif, container, separator, lottie, button, close, list item (in most cases simply wrapped in a collapsible group)

## 0.1.10

* Fixed the text editing issues that I added in the previous version
* Fixed adding and removing nested array elements in complex properties (ranges->actions, for example)
* Added a naive implementation of additional properties. It occurs, for example, in the form of payload in action (in the json scheme it looks like additionalProperties: true)
* Fixed the "sticking" of the text editor in place in focus mode when it no longer became relevant, now it exits correctly (I hope)
* Fixed the sticking of the drag-and-drop state in the file upload dialog

## 0.1.9

* Updated image, gif, lottie parameters
* Added an image upload button directly to the preview
* Fixes for editing text in previews

## 0.1.8

* Fixed a case where images from lottie animations could remain when changing the order of elements
* Fixed a case where images could continue to remain hidden due to lottie animations when changing the order of elements
* Fixed a breakage of the editor when deleting the entire text
* Fixed a breakage of the editor when there are identical backgrounds
* Fixed the cancellation of the rotation (the very first one)
* When moving the component and releasing the mouse outside the preview, the selected element is no longer reset
* Added a minimum distance when moving, before which the element does not move (so that the selection does not blink when selecting an element and moving the mouse by 1 pixel)
* Completed the change of the order of the background layers: elements no longer blink when changing the order, and delete buttons are hidden during movement
* When switching the background type, the previous values are now reused

## 0.1.7

* Improvements to highlight text in the shadow dom
* Removed the theme change button in the preview
* Improvements to the text editor (link deletion button, return of focus after inserting an image, fixes for text loss)
* Added a button to delete an image
* Mouse elements are no longer highlighted when the text editor is open
* Fixed rounding of color transparency

## 0.1.6

* Added the "W" and "H" signatures to the component sizes
* Minor edits to the image upload dialog
* Updated text editing tool

## 0.1.5

* Added a new dialog for uploading images/lottie
* Fixed the text "content" on the arrow in the drop-down list

## 0.1.4

* DivKit no longer tries to load the url "empty://" for images
* Fixed simultaneous display of lottie animations and the image itself
* DivKit has learned how to correctly update lottie animations when they are changed in the editor (and not update when they are not changed)
* Added a parameter for passing as log_url to the new action
* The palette from the test page is hidden
* The variable with the palette is removed from json if no colors are specified in the palette
* The undo operation has been fixed, after which component properties could be incorrectly displayed
* Panel dividers are slightly reduced when hovering
* Fixed scrolling to elements in the component tree when selecting
* Enabled a new property with a background for the root element
* Updated the "lottie" template: the URL for the image is now "empty://", repeat_count = -1 and repeat_mode = restart have been added
* Rewrote background layers settings
* Completed the animation when releasing the background layer, now the layer smoothly reaches the desired location

## 0.1.3

* Scrollbars have been reduced
* Fixed the position of dialogs in case of window resize
* Added a new color selection dialog
* Fixed the logic of calculating the text after editing in place, after Enter, hyphenations, styles and images were lost
* The Lottie template has been changed, as it did not work in android/iOS

## 0.1.1

* Fixed problems of the previous version with adding/removing backgrounds
* Fixed closing the background dialog when "dragging" a color
* Improved the design of separators and headers of property groups
* Semi-new content alignment property in the container

# 0.1.0

* Updated styles: dividers, scrollbars, component icons and other small things
* Added groups to the property settings
* New "alignment" property
* New "background" property with new dialog
* New "margins" property

## 0.0.35

* Minor fixes for testing

## 0.0.34

* Moved the list item to the second list of components, and the separator to the first
* Fixed the clipping on top of the text editing controls
* New opacity and rotation controls
* Bug fixes

## 0.0.33

* Updated DivKit 24.2.0 -> 25.3.0
* Updated the palette panel, changed the palettes to dictionaries. A variable theme from the sdk is now expected
* Sawed the panel with properties into 2 and added new tooltips to them
* Corrected text editing on a dark theme
* Added an indentation to the preview and component tree from the bottom so that there is a little free space when scrolling
* Made the header in the preview stick so that all controls remain available when scrolling
* Redesigned calculation of the maximum width/height when resizing the component, as well as when resizing to the maximum, the size of match_parent is included
* Added the ability to edit text in buttons

## 0.0.32

* shadowRoot support

## 0.0.31

* Dialogs are now closed by ESC
* The text editor has been shaken up, it should become noticeably better in a number of things. If you notice any bugs, please bring them

## 0.0.30

* Ctrl+C / Ctrl+V to copy/paste components (in the tree and in the preview)
* Transfer, resize and rotation are now reset by ESC
* Unused templates are no longer included in json
* Updated external API
* File downloads are now differentiated by type (the image/gif/lottie file selection dialog now has the correct filter)
* Multiline text input field
* Properties for gif images and list items
* Text editor on double-click

## 0.0.28

* A number of fixes
* Added the selection of components when transferring the "autolayout"
view
* Edits on "sticking" during the resize and transfer, they should become fewer and should become better
* Added component transfers by arrows / arrows with shift (if the component is inside the container, it changes the order of the elements, otherwise it names the margins)
* Added component resize by arrows + Ctrl / arrows + Ctrl + Shift
* Added Shift. When transferring, it allows you to transfer only by x or by y. When resizing, it allows you to make square dimensions. When turning, it aligns turns by 45 degrees

## 0.0.21

* Brought most of the interface to a palette from design
* Added the constrained flag to the dimensions
* Translated all the names of components and templates
* Added the "list item" template
* Corrected the reset of the selection by clicking past the preview
* Corrected the reset of the selection in the code
* Corrected the selection of components in the preview (by hover on the button, the cursor no longer changes and the button itself does not change)
* In the panel for adding a component, I divided the properties into two lists, basic and non-basic
* New components now get the constrained=true flag for width
* Fixed text wrapping in rare cases when resizing, moving and rotating components (when the text was not hyphenated, but when moving the text was transferred)
* Template components can now contain child components (which are not highlighted or displayed in the component tree)
* Container and gallery are now created immediately with child text
* The container and gallery are now deleted if there are no child components left in them
* The "add component" menu item is now shown only in container components
* When setting the background and actions, if they were not there before, a default element is added (so that you need to click less)

## 0.0.19

* Fixed several bugs, improved the selection of elements with the mouse on click
* Redone (already once in the 5th) the rendering of the frame around the objects. Now supports scrolling and content that is larger than the screen
* Added duplication of components in the tree

## 0.0.18

* Added an error counter and an error window
* Added undo actions. Straight as basic as possible

## 0.0.17

* Put the cross in a separate component. You need to change the starting json
* Turned on the normal backgrounds for the button
* Added properties about content alignment to the container
* Changed the order of properties for all elements, now there will always be the same properties at the beginning
* Removed the "autolayout" checkbox
* Changed the name of the root element in the component tree
* Enabled at least basic properties for all components. For example, the seporator now has them
* Changed the horizontal/vertical alignment to select instead of radio buttons, added an empty value there
* Allowed to select, resize and rotate all components
* Dragging/resizing/and the like no longer moves the element through the tree
* When dragging, it now tries to use the old logic from "autolayout", but only if Ctrl is not pressed
* Fixed the position of the borders of the elements when selecting (it wasn't quite right before)
* Resizing and orientation via properties works more as expected

## 0.0.16

* Made the code selections blocky, not inline (looks neater)
* Added hovers in the code (when hovering the mouse, a block of code, a component in the tree and a preview are highlighted)
* Added component selection by click in the code
* When switching the code tab, the component is now immediately scrolled to
* I made two selections - the highlighted component and the hovered one, now it should work more as expected

## 0.0.15

* Improved the selection of backgrounds
* Lottie switched to lottie_light (since the previous one uses eval and we would have problems with CSP). Some animations may now look worse due to the fact that fewer features are now supported.
* Lottie is now loaded in a separate bundle, if necessary. In total, about half of the editor's code is loaded lazily
* Added a transition to the element code when selecting it
* Added highlighting of the element code when hovering in the element tree and in the preview
* Minor fixes

## 0.0.14

* Tweaked different dialogs, added translations, more contrast with the page, closing by clicking outside
* Fixed problems when moving with the action editor
* Added a separate set of properties for the root element and added a background selection there
* Started doing background editing
* Added a few animations to the properties, you can see on the width/height change

## 0.0.13

* Minor bugfixes

## 0.0.12

* Recolored a light theme, it should become a little more adequate
* Added the ability to add components with just a click without drag and drop
* Added the ability to add drag-and-drop elements to the preview with auto-layout turned off (the position is wrong a bit)
* Added "center alignment". With any action, it turns off, you can turn it on only by clicking in the properties
* Added the ability to delete components via "Delete" in the tree and preview
* Fixed the addition of images
* All sorts of different fixes

## 0.0.11

* Brought the design closer to the layouts, fixed various bugs

## 0.0.10

* Now supports 2 themes (the example is linked to the system theme, but you can set any one)
* Fixed the return of templates
* Now supports layout settings
* It has become closer to the layouts

## 0.0.9

* Added the destroy method

## 0.0.7

* Added a new padding field
* New color selection dialog

## 0.0.6

* Added the ability to rotate components
* Improved the work with template properties (in particular, the button now shows the width of content, not fill)

## 0.0.5

* Changed the templates to "non-existent components" and added a new Button component
* Added an action editor
* Removed the palette and templates from json so that they could not be changed there (based on the fact that we provide the templates ourselves from the outside)

## 0.0.4

* New panel with palettes
* The ability to select a color from the palette
* The ability to select the preview theme
* Added typings to the package
* Added a slider with alpha
