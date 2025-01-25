export function decline(number: number, word: string[]): string {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-condition
    if (!word || !word.length) {
        return '';
    }
    if (number === 0 && word[3]) {
        return word[3];
    }
    if (!word[1] && !word[2]) {
        return word[0];
    }
    if (!word[2]) {
        if (number >= 0 && number <= 1) {
            return word[0];
        }
        return word[1];
    }
    number %= 100;
    if (number >= 20) {
        number %= 10;
    }
    if (number === 1) {
        return word[0];
    }
    if (number > 1 && number < 5) {
        return word[1];
    }
    return word[2];
}
