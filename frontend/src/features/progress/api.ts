import { request } from '../../lib/http'
import type { TaskProgress, CreateProgress } from './types'

export const progressApi = {
  list: (projectId: number, taskId: number, signal?: AbortSignal) => request<TaskProgress[]>(`/api/projects/${projectId}/tasks/${taskId}/progress`, { signal }),
  create: (projectId: number, taskId: number, input: CreateProgress) => request<TaskProgress>(`/api/projects/${projectId}/tasks/${taskId}/progress`, { method: 'POST', body: JSON.stringify(input) }),
}
