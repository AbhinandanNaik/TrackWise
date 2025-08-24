import { useState } from 'react'
import * as iot from '../services/iotService'

export default function IoT(){
  const [assetId, setAssetId] = useState('')
  const [temperature, setTemperature] = useState(25)
  const [batteryLevel, setBatteryLevel] = useState(90)
  const [inUse, setInUse] = useState(false)
  const [latitude, setLatitude] = useState(0)
  const [longitude, setLongitude] = useState(0)
  const [result, setResult] = useState<string>('')

  return <div className="grid-2">
    <form className="card" onSubmit={e=>{ e.preventDefault(); iot.ingest({ assetId, temperature, batteryLevel, inUse, latitude, longitude }).then(r=>setResult(JSON.stringify(r))).catch(e=>setResult(e.message)) }}>
      <h3>Ingest IoT Data</h3>
      <div><label>Asset ID</label><input value={assetId} onChange={e=>setAssetId(e.target.value)} required/></div>
      <div><label>Temperature</label><input type="number" value={temperature} onChange={e=>setTemperature(Number(e.target.value))} /></div>
      <div><label>Battery</label><input type="number" value={batteryLevel} onChange={e=>setBatteryLevel(Number(e.target.value))} /></div>
      <div><label>In Use</label><input type="checkbox" checked={inUse} onChange={e=>setInUse(e.target.checked)} /></div>
      <div><label>Latitude</label><input type="number" value={latitude} onChange={e=>setLatitude(Number(e.target.value))} /></div>
      <div><label>Longitude</label><input type="number" value={longitude} onChange={e=>setLongitude(Number(e.target.value))} /></div>
      <button>Submit</button>
    </form>
    <div className="card">
      <h3>Simulator</h3>
      <div style={{display:'flex', gap:8}}>
        <button onClick={()=>iot.startSimulator()}>Start Simulator</button>
        <button onClick={()=>iot.stopSimulator()}>Stop Simulator</button>
      </div>
      <h4 style={{marginTop:12}}>Process Sensor Data</h4>
      <form onSubmit={e=>{ e.preventDefault(); iot.processSensorData(assetId, { temperature, batteryLevel, inUse, latitude, longitude }).then(r=>setResult(JSON.stringify(r))).catch(e=>setResult(e.message)) }}>
        <button disabled={!assetId}>Process</button>
      </form>
    </div>
    {result && <div className="card"><pre>{result}</pre></div>}
  </div>
}


