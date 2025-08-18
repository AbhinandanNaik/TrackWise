import { download } from '../services/reportService'
export default function Reports(){
  return <div className="card">
    <h3>Reports</h3>
    <p className="small">Downloads from /api/reports/{`{usage|assignments|performance}`}</p>
    <button onClick={()=>download('usage')}>Download Usage</button>
    <button onClick={()=>download('assignments')}>Download Assignments</button>
    <button onClick={()=>download('performance')}>Download Performance</button>
  </div>
}
