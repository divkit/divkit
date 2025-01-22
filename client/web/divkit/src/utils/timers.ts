import type { DivTimer } from '../../typings/common';
import type { ExecAnyActionsFunc } from '../context/root';
import type { MaybeMissing } from '../expressions/json';
import type { VariableType } from '../expressions/variable';
import { type LogError, wrapError } from './wrapError';

interface TimerState {
    state: 'stopped' | 'running' | 'paused';
    definition: DivTimer;
    duration?: number;
    tick?: number;
    durationTimeout?: number;
    tickTimeout?: number;
    tickStarted?: number;
    tickPassed?: number;
    tickCount?: number;
    tickCountPredict?: number;
    durationStarted?: number;
    durationPassed?: number;
    hold?: boolean;
}

const AVAIL_ACTIONS_NAMES = ['start', 'stop', 'pause', 'resume', 'cancel', 'reset'] as const;

type AVAIL_ACTIONS_LIST = typeof AVAIL_ACTIONS_NAMES[number];

const AVAIL_ACTIONS = new Set<string>(AVAIL_ACTIONS_NAMES);

type ApplyVarsFunc = <T>(json: T) => MaybeMissing<T>;

type HasVariableWithTypeFunc = (name: string, type: VariableType) => boolean;

type SetVariableValueFunc = (name: string, value: unknown) => void;

export class TimersController {
    private readonly timers: Map<string, TimerState> = new Map();
    private readonly logError: LogError;
    private readonly applyVars: ApplyVarsFunc;
    private readonly hasVariableWithType: HasVariableWithTypeFunc;
    private readonly setVariableValue: SetVariableValueFunc;
    private readonly execAnyActions: ExecAnyActionsFunc;
    private readonly visibilityHandler: () => void;
    private awaitActions: {
        id: string;
        action: AVAIL_ACTIONS_LIST;
    }[] = [];

    constructor(opts: {
        logError: LogError;
        applyVars: ApplyVarsFunc;
        hasVariableWithType: HasVariableWithTypeFunc;
        setVariableValue: SetVariableValueFunc;
        execAnyActions: ExecAnyActionsFunc;
    }) {
        this.logError = opts.logError;
        this.applyVars = opts.applyVars;
        this.hasVariableWithType = opts.hasVariableWithType;
        this.setVariableValue = opts.setVariableValue;
        this.execAnyActions = opts.execAnyActions;

        this.visibilityHandler = () => {
            if (document.visibilityState === 'visible') {
                this.awaitActions.forEach(({ id, action }) => {
                    this.execTimerAction(id, action);
                });
                this.awaitActions = [];
                this.unholdAll();
            } else {
                this.holdAll();
            }
        };

        document.addEventListener('visibilitychange', this.visibilityHandler);
    }

    destroy(): void {
        document.removeEventListener('visibilitychange', this.visibilityHandler);

        for (const [_id, timer] of this.timers) {
            this.stopTimerTimeouts(timer);
        }
    }

    createTimer(timer: DivTimer) {
        if (!timer?.id) {
            this.logError(wrapError(new Error('Missing timer id')));
            return;
        }
        if (!(timer.duration || timer.tick_interval && (timer.value_variable || timer.tick_actions))) {
            this.logError(wrapError(new Error('Misconfigured timer'), {
                additional: {
                    id: timer.id
                }
            }));
            return;
        }

        this.timers.set(timer.id, {
            state: 'stopped',
            definition: timer
        });
    }

    execTimerAction(id: string | null | undefined, action: string | null | undefined) {
        if (!id || !action || !this.timers.has(id) || !AVAIL_ACTIONS.has(action)) {
            this.logError(wrapError(new Error('Incorrect timer action'), {
                additional: {
                    id,
                    action
                }
            }));
            return;
        }
        const actionTyped = action as AVAIL_ACTIONS_LIST;

        if (document.visibilityState !== 'visible') {
            // wait till the page is visible
            this.awaitActions.push({
                id,
                action: actionTyped
            });
            return;
        }

        // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
        const timer = this.timers.get(id)!;
        this[actionTyped](timer);
    }

    private stopTimerTimeouts(timer: TimerState): void {
        if (timer.durationTimeout) {
            clearTimeout(timer.durationTimeout);
            timer.durationTimeout = undefined;
        }
        if (timer.tickTimeout) {
            clearTimeout(timer.tickTimeout);
            timer.tickTimeout = undefined;
        }
    }

    private async tickOrUnholdAction(timer: TimerState): Promise<void> {
        const now = performance.now();
        const value = (timer.durationPassed || 0) + now - (timer.durationStarted || 0);
        if (timer.duration && value > timer.duration) {
            return;
        }
        this.updateVariable(timer, value);
        await this.callActions(timer, 'tick');

        if (timer.tickCount !== undefined) {
            ++timer.tickCount;
        }
    }

    private startOrResume(timer: TimerState): void {
        timer.state = 'running';
        timer.hold = false;

        timer.durationStarted = performance.now();

        const duration = timer.duration;
        if (duration) {
            timer.durationTimeout = window.setTimeout(async() => {
                this.updateVariable(timer, duration);
                if (
                    timer.tickCountPredict &&
                    timer.tickCount !== undefined &&
                    timer.tickCount < timer.tickCountPredict
                ) {
                    await this.callActions(timer, 'tick');
                }
                this.stop(timer);
            }, Math.max(0, duration - (timer.durationPassed || 0)));
        }
        const tick = timer.tick;
        if (tick) {
            const startTick = () => {
                const started = timer.tickStarted = performance.now();
                const duration = Math.max(0, tick - (timer.tickPassed || 0));
                timer.tickTimeout = window.setTimeout(async() => {
                    await this.tickOrUnholdAction(timer);
                    timer.tickPassed = ((performance.now() - started) - duration) % tick;
                    if (timer.state === 'running') {
                        startTick();
                    }
                }, duration);
            };

            startTick();
        }
    }

    private start(timer: TimerState): void {
        if (timer.state === 'running') {
            this.logError(wrapError(new Error('The timer is already running')));
            return;
        } else if (timer.state === 'paused') {
            this.logError(wrapError(new Error('The timer is paused')));
            return;
        }

        const variableName = timer.definition.value_variable;
        if (variableName && !this.hasVariableWithType(variableName, 'integer')) {
            this.logError(wrapError(new Error('Cannot find variable'), {
                additional: {
                    name: variableName
                }
            }));
            return;
        }

        if (variableName) {
            this.setVariableValue(variableName, 0);
        }

        if (timer.definition.duration) {
            timer.duration = this.applyVars(timer.definition.duration);
        }
        if (timer.definition.tick_interval) {
            timer.tick = this.applyVars(timer.definition.tick_interval);
        }
        // duration < 0 is incorrect
        // tick_interval <= is incorrect
        if (
            timer.duration !== undefined && timer.duration < 0 ||
            timer.tick !== undefined && timer.tick <= 0
        ) {
            this.logError(wrapError(new Error('Incorrect timer properties'), {
                additional: {
                    id: timer.definition.id
                }
            }));
            return;
        }

        if (timer.duration !== undefined && timer.tick !== undefined) {
            timer.tickCount = 0;
            timer.tickCountPredict = Math.floor(timer.duration / timer.tick);
        }

        this.startOrResume(timer);
    }

    private stop(timer: TimerState): void {
        if (timer.state === 'stopped') {
            this.logError(wrapError(new Error('The timer has already been stopped')));
            return;
        }

        timer.state = 'stopped';
        timer.durationPassed = 0;
        timer.tickPassed = 0;
        this.stopTimerTimeouts(timer);
        this.callActions(timer, 'end');
    }

    private pause(timer: TimerState): void {
        if (timer.state === 'stopped') {
            this.logError(wrapError(new Error('The timer has already been stopped')));
            return;
        } else if (timer.state === 'paused') {
            this.logError(wrapError(new Error('The timer has already been paused')));
            return;
        }

        timer.state = 'paused';

        this.stopTimerTimeouts(timer);

        const now = performance.now();

        if (timer.durationStarted) {
            timer.durationPassed = (timer.durationPassed || 0) + now - timer.durationStarted;
        }
        if (timer.tickStarted) {
            timer.tickPassed = (timer.tickPassed || 0) + now - timer.tickStarted;
        }

        const variableName = timer.definition.value_variable;
        if (variableName && timer.durationPassed) {
            this.setVariableValue(variableName, Math.round(timer.durationPassed));
        }
    }

    private resume(timer: TimerState): void {
        if (timer.state === 'stopped') {
            this.logError(wrapError(new Error('The timer has already been stopped')));
            return;
        } else if (timer.state === 'running') {
            this.logError(wrapError(new Error('The timer is already running')));
            return;
        }

        this.startOrResume(timer);
    }

    private cancel(timer: TimerState): void {
        if (timer.state === 'stopped') {
            return;
        }

        timer.state = 'stopped';
        timer.durationPassed = 0;
        timer.tickPassed = 0;
        this.stopTimerTimeouts(timer);
    }

    private reset(timer: TimerState): void {
        this.cancel(timer);
        this.start(timer);
    }

    private updateVariable(timer: TimerState, value: number): void {
        const variableName = timer.definition.value_variable;
        if (variableName) {
            this.setVariableValue(variableName, Math.round(value));
        }
    }

    private async callActions(timer: TimerState, type: 'tick' | 'end'): Promise<void> {
        const actions = timer.definition[type === 'end' ? 'end_actions' : 'tick_actions'];

        if (actions) {
            const actionsWithExpressions = this.applyVars(actions);
            return this.execAnyActions(actionsWithExpressions, {
                processUrls: false
            });
        }
    }

    private holdAll(): void {
        for (const [_id, timer] of this.timers) {
            if (timer.state === 'running') {
                timer.hold = true;
                this.stopTimerTimeouts(timer);
            }
        }
    }

    private async unholdAll(): Promise<void> {
        for (const [_id, timer] of this.timers) {
            if (timer.state === 'running' && timer.hold) {
                // All timeouts were canceled, but the time is not stopped
                const now = performance.now();

                if (timer.durationStarted) {
                    timer.durationPassed = (timer.durationPassed || 0) + now - timer.durationStarted;
                }
                if (timer.tickStarted) {
                    timer.tickPassed = (timer.tickPassed || 0) + now - timer.tickStarted;
                }

                if (timer.tick) {
                    // Run tick actions and update variable instantly after the page is shown, if:
                    // Timer has the tick_interval
                    // Timer is not done yet (check inside function)
                    await this.tickOrUnholdAction(timer);
                }

                this.startOrResume(timer);
            }
        }
    }
}
