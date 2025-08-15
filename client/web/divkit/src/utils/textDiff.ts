// Replicate from Android: client/android/div/src/main/java/com/yandex/div/core/util/mask/TextDiff.kt

export interface TextDiff {
    start: number;
    added: number;
    removed: number;
}

export function textDiff(left: string, right: string): TextDiff {
    if (left === right) {
        return {
            start: left.length,
            added: 0,
            removed: 0
        };
    }

    if (left.length > right.length) {
        const diff = textDiff(right, left);

        return {
            start: diff.start,
            added: diff.removed,
            removed: diff.added
        };
    }

    let leftIndex = 0;
    let rightIndex = right.length - 1;
    const lengthDiff = right.length - left.length;

    while (
        leftIndex < rightIndex && leftIndex < left.length &&
        left[leftIndex] === right[leftIndex]
    ) {
        ++leftIndex;
    }

    while (
        rightIndex - lengthDiff >= leftIndex &&
        left[rightIndex - lengthDiff] === right[rightIndex]
    ) {
        --rightIndex;
    }

    ++rightIndex;

    return {
        start: leftIndex,
        added: rightIndex - leftIndex,
        removed: rightIndex - leftIndex - lengthDiff
    };
}
