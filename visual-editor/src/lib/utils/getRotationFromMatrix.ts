export function getRotationFromMatrix(value: string | undefined): number {
    if (!value || !value.startsWith('matrix(')) {
        return 0;
    }

    const parts = value.slice(7).split(',');
    let result = Math.acos(Number(parts[0]));
    if (Number(parts[1]) < 0) {
        result = Math.PI * 2 - result;
    }

    return result;
}
