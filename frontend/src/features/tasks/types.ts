export interface Task {
  id: number
  projectId: number
  assigneeId: number | null
  title: string
  optimisticHours: number
  likelyHours: number
  pessimisticHours: number
}
export type CreateTask = Omit<Task, 'id' | 'projectId'>
