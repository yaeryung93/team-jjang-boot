import { Link, useLoaderData } from 'react-router-dom';
export function ProjectsPage() {
    const projects = useLoaderData();
    return <>
    <section className="page-heading"><div><p className="eyebrow">OUR PROJECTS</p><h1>우리 팀의 프로젝트</h1><p>계획을 나누고, 진행 상황을 함께 확인하세요.</p></div><Link className="button" to="/projects/new">+ 새 프로젝트</Link></section>
    <div className="section-heading"><h2>전체 프로젝트 <span className="count">{projects.length}</span></h2><span>함께 만드는 한 걸음</span></div>
    {projects.length === 0 ? <section className="panel empty"><span className="empty-icon" aria-hidden="true">＋</span><h2>첫 프로젝트를 시작해 보세요</h2><p>프로젝트 이름과 마감일부터 정하면 돼요.</p><Link className="button" to="/projects/new">프로젝트 만들기</Link></section> :
            <div className="project-grid">{projects.map(project => <Link className="panel project-card" key={project.id} to={`/projects/${project.id}`}>
        <span className="project-mark" aria-hidden="true">{project.name.slice(0, 1)}</span><h2>{project.name}</h2><p>{project.description || '아직 프로젝트 설명이 없어요.'}</p><div className="card-footer"><span>마감 <time dateTime={project.deadline}>{project.deadline}</time></span><span aria-hidden="true">↗</span></div>
      </Link>)}</div>}
  </>;
}
