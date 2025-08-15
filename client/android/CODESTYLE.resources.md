# Xml and resources

Based on https://github.com/ribot/android-guidelines/blob/master/project_and_code_guidelines.md

This document supplements and also redefines certain provisions of these standards. In case of conflict follow this document.

## Formatting

When an XML element has several attributes, you must write each on a separate line:

**Bad:**

```xml
<TextView android:id="@+id/text_view_profile"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content" />
```

**Good:**
```xml
<TextView
	android:id="@+id/text_view_profile"
	android:layout_width="wrap_content"
	android:layout_height="wrap_content" />
```


## Indents

Use 4 spaces for indentation.

**Good:**

```xml
<resources>
    <declare-styleable name="RoundedCornersWithStrokeLayout">
        <attr name="cornerRadius" format="dimension|reference"/>
        <attr name="strokeWidth" format="dimension|reference"/>
        <attr name="strokeColor" format="color|reference"/>
        <attr name="cornerColor" format="color|reference"/>
    </declare-styleable>
</resources>
```

## Naming

Don't use `ic_`-prefixes in naming any resources (not just xml) in modules. It is strongly recommended to use current module name prefixes instead (to avoid problems when merging modules due to the same resource names).

