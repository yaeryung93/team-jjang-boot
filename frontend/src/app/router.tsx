import { createBrowserRouter, redirect } from 'react-router-dom'
import type { ActionFunctionArgs, LoaderFunctionArgs } from 'react-router-dom'
import { Layout } from '../components/Layout'
import { RouteError } from '../components/RouteError'
import { projectsApi } from '../features/projects/api'
import { membersApi } from '../features/members/api'
import { tasksApi } from '../features/tasks/api'
import { ApiError, errorMessage } from '../lib/http'
import { ProjectsPage } from '../pages/ProjectsPage'
import { NewProjectPage } from '../pages/NewProjectPage'
import { ProjectDetailPage } from '../pages/ProjectDetailPage'

export interface ProjectActionError { message: string; fields: Record<string, string> }

async function createProject({ request }: ActionFunctionArgs) {
  const form = await request.formData()
  try {
    const project = await projectsApi.create({ name: String(form.get('name') || '').trim(), description: String(form.get('description') || ''), deadline: String(form.get('deadline') || '') })
    return redirect(`/projects/${project.id}`)
  } catch (error) {
    return { message: errorMessage(error), fields: error instanceof ApiError ? error.fields : {} } satisfies ProjectActionError
  }
}

async function projectDetail({ params, request }: LoaderFunctionArgs) {
  if (!params.projectId || !/^[1-9]\d*$/.test(params.projectId)) throw new Response(null, { status: 404 })
  const id = Number(params.projectId)
  if (!Number.isSafeInteger(id)) throw new Response(null, { status: 404 })
  const [project, members, tasks] = await Promise.all([
    projectsApi.get(id, request.signal), membersApi.list(id, request.signal), tasksApi.list(id, request.signal),
  ])
  return { project, members, tasks }
}
export type ProjectDetail = Awaited<ReturnType<typeof projectDetail>>

export const router = createBrowserRouter([{
  element: <Layout />,
  children: [{
    errorElement: <RouteError />,
    children: [
      { index: true, loader: () => redirect('/projects') },
      { path: 'projects', loader: ({ request }) => projectsApi.list(request.signal), element: <ProjectsPage />, hydrateFallbackElement: <p role="status">불러오는 중…</p> },
      { path: 'projects/new', action: createProject, element: <NewProjectPage /> },
      { path: 'projects/:projectId', loader: projectDetail, element: <ProjectDetailPage />, hydrateFallbackElement: <p role="status">불러오는 중…</p> },
      { path: '*', loader: () => { throw new Response(null, { status: 404 }) } },
    ],
  }],
}])
