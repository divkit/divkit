// eslint-disable-next-line @typescript-eslint/no-explicit-any
export function wrapJson(card: any, templates?: any) {
    if (card.card) {
        return card;
    } else if (templates) {
        return {
            card,
            templates
        };
    } else {
        return {
            card
        };
    }
}
