Set ws = CreateObject("Wscript.Shell")

Wscript.Sleep 100
ws.AppActivate 27948
ws.SendKeys "{F5}"

Wscript.Sleep 6000
ws.AppActivate 27948
ws.SendKeys "^{F11}"