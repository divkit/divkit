export function sizesToFall(arr: number[]): number[] {
    return arr.reduce((acc, item, index) => {
        acc.push((index > 0 ? acc[index - 1] : 0) + item);
        return acc;
    }, [] as number[]);
}
