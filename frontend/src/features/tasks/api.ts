import { request } from '../../lib/http'
import type { Task, CreateTask } from './types'

export const tasksApi = {
  list: (projectId: number, signal?: AbortSignal) => request<Task[]>(`/api/projects/${projectId}/tasks`, { signal }),
  create: (projectId: number, input: CreateTask) => request<Task>(`/api/projects/${projectId}/tasks`, { method: 'POST', body: JSON.stringify(input) }),
}
