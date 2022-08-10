export function joinTemplateSizes(sizes: string[]): string {
    const result: string[] = [];
    let temp = sizes[0];
    let counter = 1;

    for (let i = 1; i <= sizes.length; i++) {
        if (sizes[i] !== temp) {
            result.push(counter > 1 ? `repeat(${counter}, ${temp})` : temp);
            temp = sizes[i];
            counter = 1;
        } else {
            counter++;
        }
    }

    return result.join(' ');
}
