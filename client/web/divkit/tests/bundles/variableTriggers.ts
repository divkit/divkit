// A variable trigger runs its actions without anyone waiting for them, so their failure
// can only show up as an unhandled rejection, which stops a Node.js server
export function catchRejections() {
    const reasons: unknown[] = [];
    const listener = (reason: unknown) => {
        reasons.push(reason);
    };
    process.on('unhandledRejection', listener);

    return {
        reasons,
        async settle() {
            // Node.js reports a rejection only after the microtasks of the render are done
            await new Promise(resolve => setTimeout(resolve, 0));
            process.off('unhandledRejection', listener);
        }
    };
}

export const TRIGGER_CONDITION = '@{padding > 8}';

export function cardWithTrigger(actions: object[]) {
    return {
        card: {
            log_id: 'test',
            states: [{
                state_id: 0,
                div: {
                    type: 'container',
                    id: 'header',
                    variables: [{
                        type: 'number',
                        name: 'padding',
                        value: 16
                    }],
                    animators: [{
                        type: 'number_animator',
                        id: 'padding_animator',
                        variable_name: 'padding',
                        duration: 200,
                        end_value: 8
                    }],
                    variable_triggers: [{
                        condition: TRIGGER_CONDITION,
                        actions: actions.map((action, index) => ({
                            log_id: `trigger_action_${index}`,
                            typed: action
                        }))
                    }],
                    items: [{
                        type: 'text',
                        text: 'Padding @{padding}'
                    }]
                }
            }]
        },
        templates: {}
    };
}

export const startAnimator = {
    type: 'animator_start',
    animator_id: 'padding_animator'
};

export const setStoredValue = {
    type: 'set_stored_value',
    name: 'padding',
    value: {
        type: 'integer',
        value: 8
    },
    lifetime: 60
};
