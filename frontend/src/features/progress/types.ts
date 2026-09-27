export interface TaskProgress { id: number; percent: number; note: string; recordedAt: string }
export type CreateProgress = Pick<TaskProgress, 'percent' | 'note'>
