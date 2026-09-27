export interface Project { id: number; name: string; description: string; deadline: string }
export type CreateProject = Omit<Project, 'id'>
