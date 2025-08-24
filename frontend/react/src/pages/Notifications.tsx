import { useState } from 'react'
import * as svc from '../services/notificationService'
export default function Notifications(){
  const [recipientId, setRecipientId] = useState('')
  const [message, setMessage] = useState('')
  const [emailTo, setEmailTo] = useState('')
  const [emailSubject, setEmailSubject] = useState('')
  const [emailBody, setEmailBody] = useState('')
  return <div className="grid-2">
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!recipientId||!message) return; svc.createInApp({ recipientId, message }).then(()=>{ setRecipientId(''); setMessage('') }) }}>
      <h3>Create In-App Notification</h3>
      <div><label>Recipient ID</label><input value={recipientId} onChange={e=>setRecipientId(e.target.value)} required/></div>
      <div><label>Message</label><textarea value={message} onChange={e=>setMessage(e.target.value)} required/></div>
      <button>Send</button>
    </form>
    <form className="card" onSubmit={e=>{ e.preventDefault(); if(!emailTo||!emailSubject||!emailBody) return; svc.sendEmail(emailTo, emailSubject, emailBody).then(()=>{ setEmailTo(''); setEmailSubject(''); setEmailBody('') }) }}>
      <h3>Send Email</h3>
      <div><label>To</label><input type="email" value={emailTo} onChange={e=>setEmailTo(e.target.value)} required/></div>
      <div><label>Subject</label><input value={emailSubject} onChange={e=>setEmailSubject(e.target.value)} required/></div>
      <div><label>Body</label><textarea value={emailBody} onChange={e=>setEmailBody(e.target.value)} required/></div>
      <button>Send Email</button>
    </form>
  </div>
}
